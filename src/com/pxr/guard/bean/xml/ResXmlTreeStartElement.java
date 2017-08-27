package com.pxr.guard.bean.xml;

import java.util.Arrays;

import com.pxr.guard.utils.ByteUtils;

//RES_XML_START_ELEMENT_TYPE = 0x0102;
public class ResXmlTreeStartElement extends ResXmlElement {
	public ResXmlTreeAttrExt attrExt;
	public ResXmlTreeAttribute[] attrib;

	public int getByteSize() {
		return header.header.size;
	}

	public static ResXmlTreeStartElement parseStartElementType(byte[] src, int offset) {
		ResXmlTreeStartElement data = new ResXmlTreeStartElement();
		data.header = ResXmlTreeNode.parseNode(src, offset);
		offset += data.header.getByteSize();
		data.attrExt = ResXmlTreeAttrExt.parseResXmlTreeAttrExt(src, offset);
		offset += data.attrExt.getByteSize();
		data.attrib = new ResXmlTreeAttribute[data.attrExt.attributeCount];
		int iOffset = offset;
		for (int i = 0; i < data.attrExt.attributeCount; i++) {
			data.attrib[i] = ResXmlTreeAttribute.parseAttribute(src, offset);
			offset += data.attrib[i].getByteSize();
		}
		data.attribBytes = ByteUtils.copyByteBetween(src, iOffset, offset);
		return data;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("ResXmlTreeStartElement:===\n");
		stringBuilder.append("|header:").append(header).append("|attrExt:").append(attrExt);
		for (int i = 0; i < attrib.length; i++) {
			stringBuilder.append("|attrib:").append("-" + i + "-").append(attrib[i]);
		}
		return stringBuilder.toString();
	}

	@Override
	public int getType() {
		return TYPE_START;
	}

	private byte[] attribBytes;

	/**
	 * add attr to start element
	 * 
	 * @param attr
	 */
	public void addAttr(ResXmlTreeAttribute... attr) {
		attrExt.addAttrCount(attr.length);
		if (attrib != null) {
			int iLen = attrib.length;
			attrib = Arrays.copyOf(attrib, attrExt.attributeCount);
			for (int i = 0; i < attr.length; i++, iLen++) {
				attrib[iLen] = attr[i];
				attribBytes = ByteUtils.mergeBytes(attribBytes, attr[i].toBytes());
			}
		} else {
			attrib = attr;
			for (int i = 0; i < attr.length; i++) {
				attribBytes = ByteUtils.mergeBytes(attribBytes, attr[i].toBytes());
			}
		}
		header.header.size = header.header.headerSize + attrExt.getByteSize()
				+ attrExt.attributeCount * attrib[0].getByteSize();
	}

	/**
	 * getBytes
	 * 
	 * @return
	 */
	public byte[] toBytes() {
		byte[] temp = null;
		if (needResetAttribBytes) {
			for (int i = 0; i < attrib.length; i++) {
				temp = ByteUtils.mergeBytes(temp, attrib[i].toBytes());
			}
			attribBytes = temp;
		}
		return ByteUtils.mergeBytes(header.toBytes(), attrExt.toBytes(), attribBytes);
	}

	private boolean needResetAttribBytes = false;

	public void setNeedResetAttribBytes(boolean need) {
		needResetAttribBytes = need;
	}

}
