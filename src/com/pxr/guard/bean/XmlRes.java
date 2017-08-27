package com.pxr.guard.bean;

import java.util.ArrayList;

import javax.xml.stream.events.StartElement;

import com.pxr.guard.bean.string.ResStringPoolRef;
import com.pxr.guard.bean.table.ResChunkHeader;
import com.pxr.guard.bean.xml.ResXMLTreeResourceMap;
import com.pxr.guard.bean.xml.ResXmlElement;
import com.pxr.guard.bean.xml.ResXmlStringPool;
import com.pxr.guard.bean.xml.ResXmlTreeAttrExt;
import com.pxr.guard.bean.xml.ResXmlTreeAttribute;
import com.pxr.guard.bean.xml.ResXmlTreeNamespace;
import com.pxr.guard.bean.xml.ResXmlTreeNode;
import com.pxr.guard.bean.xml.ResXmlTreeStartElement;
import com.pxr.guard.bean.xml.ResXmlTreeeEndElement;
import com.pxr.guard.bean.xml.ResXmlTreeeEndElement.ResXmlTreeeEndElementExt;
import com.pxr.guard.utils.ByteUtils;

public class XmlRes {

	public ResChunkHeader header;
	public ResXmlStringPool strPool;
	public ResXMLTreeResourceMap resMap;
	public ResXmlTreeNamespace nsStart;
	public ArrayList<ResXmlElement> elements;
	public ResXmlTreeNamespace nsEnd;

	public static XmlRes parseXmlRes(byte[] src) {
		XmlRes res = new XmlRes();
		int offset = 0;
		res.header = ResChunkHeader.parseHeader(src, offset);
		offset += res.header.getByteSize();
		res.strPool = ResXmlStringPool.parseResStringPool(src, offset);
		offset += res.strPool.getByteSize();
		res.resMap = ResXMLTreeResourceMap.parseXMLResMap(src, offset);
		offset += res.resMap.getByteSize();
		res.elements = new ArrayList<>();
		short type = 0;
		while (offset < src.length) {
			type = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
			switch (type) {
			case ResType.RES_XML_START_ELEMENT_TYPE:
				ResXmlTreeStartElement startElement = ResXmlTreeStartElement.parseStartElementType(src, offset);
				res.elements.add(startElement);
				offset += startElement.getByteSize();
				break;
			case ResType.RES_XML_END_ELEMENT_TYPE:
				ResXmlTreeeEndElement endElement = ResXmlTreeeEndElement.parseResXmlTreeeEndElement(src, offset);
				res.elements.add(endElement);
				offset += endElement.getByteSize();
				break;
			case ResType.RES_XML_START_NAMESPACE_TYPE:
				res.nsStart = ResXmlTreeNamespace.parseNameSapce(src, offset);
				offset += res.nsStart.getByteSize();
				break;
			case ResType.RES_XML_END_NAMESPACE_TYPE:
				res.nsEnd = ResXmlTreeNamespace.parseNameSapce(src, offset);
				offset += res.nsEnd.getByteSize();
				break;
			default:
				break;
			}
		}
		return res;
	}

	public byte[] toBytes() {
		byte[] elementBytes = null;
		for (int i = 0, len = elements.size(); i < len; i++) {
			elementBytes = ByteUtils.mergeBytes(elementBytes, elements.get(i).toBytes());
		}
		byte[] afterHeader = ByteUtils.mergeBytes(strPool.toBytes(), resMap.toBytes(), nsStart.toBytes(), elementBytes,
				nsEnd.toBytes());

		header.size = header.headerSize + strPool.getByteSize() + resMap.getByteSize() + nsStart.getByteSize()
				+ elementBytes.length + nsEnd.getByteSize();

		return ByteUtils.mergeBytes(header.toBytes(), afterHeader);
	}

	/**
	 * 添加一个字符串
	 * 
	 * @param str
	 */
	public void addStr(String str) {
		strPool.addString(str);
	}

	/**
	 * 添加一个属性
	 */

	public void addAttr(String startElementName, String nameSpace, String attrName, String attrValue) {
		// 找到element
		ResXmlTreeStartElement startElement = getStartElement(startElementName);
		if (startElement == null) {
			System.out.println("真的没有这个element：" + startElementName);
			return;
		}
		// 插入value值
		int attrNameIndex = findStrIndex(attrName, true);
		int valueIndex = findStrIndex(attrValue, true);
		int nsIndex = findStrIndex(nameSpace, false);

		// 生成属性bean
		ResXmlTreeAttribute attr = new ResXmlTreeAttribute();
		if (nsIndex == -1) {
			attr.ns = nsStart.nsExt.uri;
		} else {
			attr.ns = new ResStringPoolRef(nsIndex);
		}
		attr.name = new ResStringPoolRef(attrNameIndex);
		attr.rawValue = new ResStringPoolRef(valueIndex);
		attr.typedValue = new ResValue(ResValue.TYPE_STRING, valueIndex);
		// 插入bean
		startElement.addAttr(attr);
	}

	/**
	 * 修改一个属性的值，并将原来的值返回
	 */

	public String replaceAttrValue(String startElementName, String attrName, String newValue) {
		// 找到element
		ResXmlTreeStartElement startElement = getStartElement(startElementName);
		if (startElement == null) {
			System.out.println("真的没有这个element：" + startElementName);
			return "";
		}
		// 插入value值
		int attrNameIndex = findStrIndex(attrName, true);
		int newValueIndex = findStrIndex(newValue, true);
		// 找到这个属性
		int oldIndex = -1;
		for (int i = 0, len = startElement.attrib.length; i < len; i++) {
			if (startElement.attrib[i].name.index == attrNameIndex) {
				oldIndex = startElement.attrib[i].typedValue.data;
				startElement.attrib[i].typedValue.data = newValueIndex;
				startElement.attrib[i].rawValue.index = newValueIndex;
				startElement.setNeedResetAttribBytes(true); // toByte的时候重新计算byte
				break;
			}
		}
		if (oldIndex == -1) {
			addAttr(startElementName, null, attrName, newValue);
			return newValue;
		}
		return strPool.stringPoolStrings.get(oldIndex);

	}

	/**
	 * 从strPool查找string的index， 如果没有则插入
	 */
	public int findStrIndex(String str, boolean isInsert) {
		if (str == null) {
			return -1;
		}
		int index = strPool.stringPoolStrings.indexOf(str);
		if (!isInsert || index != -1) {
			return index;
		}
		strPool.addString(str);
		return strPool.stringPoolStrings.size() - 1;
	}

	/**
	 * 添加一个metaData属性
	 */
	public void addElement(ResXmlTreeStartElement parentElement, String elementName, String key, String keyvalue) {

		int lineNumer = parentElement == null ? 0 : parentElement.header.lineNumber + 1;

		int position = elements.indexOf(parentElement) + 1;
		if (position < elements.size()) {
			ResXmlElement resXmlElement = elements.get(position);
			lineNumer = resXmlElement.header.lineNumber -1;
		}

		int keyIndex = findStrIndex(key, true);
		int keyvalueIndex = findStrIndex(keyvalue, true);
		int nameIndex = findStrIndex("name", true);
		int valueIndex = findStrIndex("value", true);
		int elementNameIndex = findStrIndex(elementName, true);

		ResXmlTreeStartElement startElement = new ResXmlTreeStartElement();
		ResXmlTreeeEndElement endElement = new ResXmlTreeeEndElement();

		// 创建startElement
		startElement.attrExt = new ResXmlTreeAttrExt(new ResStringPoolRef(-1), new ResStringPoolRef(elementNameIndex),
				(short) 20, (short) 20, (short) 0);
		// header 部分
		startElement.header = new ResXmlTreeNode(new ResChunkHeader(ResType.RES_XML_START_ELEMENT_TYPE, (short) 16),
				lineNumer, new ResStringPoolRef(-1));
		// 属性数组部分
		ResXmlTreeAttribute keyAttr = new ResXmlTreeAttribute(nsStart.nsExt.uri, new ResStringPoolRef(nameIndex),
				new ResStringPoolRef(keyIndex), new ResValue(ResValue.TYPE_STRING, keyIndex));
		ResXmlTreeAttribute valueAttr = new ResXmlTreeAttribute(nsStart.nsExt.uri, new ResStringPoolRef(valueIndex),
				new ResStringPoolRef(keyvalueIndex), new ResValue(ResValue.TYPE_STRING, keyvalueIndex));
		startElement.addAttr(keyAttr, valueAttr); // 这里已经对header的size计算了

		// 创建endElement
		endElement.header = new ResXmlTreeNode(new ResChunkHeader(ResType.RES_XML_END_ELEMENT_TYPE, (short) 16, 24),
				lineNumer, new ResStringPoolRef(-1));
		endElement.endEleExt = new ResXmlTreeeEndElementExt(new ResStringPoolRef(-1),
				new ResStringPoolRef(elementNameIndex));

		System.out.println(endElement.header.header.headerSize);

		elements.add(position, endElement);
		elements.add(position, startElement);

	}

	public ResXmlTreeStartElement getStartElement(String elementName) {
		return getStartElement(findStrIndex(elementName, false));
	}

	public ResXmlTreeStartElement getStartElement(int indexOfElementName) {
		ResXmlTreeStartElement startElement = null;
		for (int i = 0; i < elements.size(); i++) {
			ResXmlElement element = elements.get(i);
			if (element.getType() == ResXmlElement.TYPE_START) {
				startElement = (ResXmlTreeStartElement) element;
				if (startElement.attrExt.name.index == indexOfElementName) {
					// 找到了
					break;
				}
			}
		}
		return startElement;
	}

}
