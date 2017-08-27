package com.pxr.guard.bean.xml;

import com.pxr.guard.bean.string.ResStringPoolRef;
import com.pxr.guard.utils.ByteUtils;

//RES_XML_END_ELEMENT_TYPE = 0x0103;
public class ResXmlTreeeEndElement extends ResXmlElement {
	public ResXmlTreeeEndElement(){
		
	}
	public ResXmlTreeeEndElement(ResXmlTreeNode header, ResXmlTreeeEndElementExt endEleExt) {
		super();
		this.header = header;
		this.endEleExt = endEleExt;
	}

	public ResXmlTreeeEndElementExt endEleExt;

	public int getByteSize() {
		return header.header.size;
	}

	public static ResXmlTreeeEndElement parseResXmlTreeeEndElement(byte[] src, int offset) {
		ResXmlTreeeEndElement data = new ResXmlTreeeEndElement();
		data.header = ResXmlTreeNode.parseNode(src, offset);
		offset += data.header.getByteSize();
		data.endEleExt = ResXmlTreeeEndElementExt.parseResXmlTreeeEndElementExt(src, offset);
		offset += data.endEleExt.getByteSize();
		return data;
	}

	public static class ResXmlTreeeEndElementExt {
		public ResStringPoolRef ns;
		public ResStringPoolRef name;

		public int getByteSize() {
			return ns.getByteSize() + name.getByteSize();
		}

		public static ResXmlTreeeEndElementExt parseResXmlTreeeEndElementExt(byte[] src, int offset) {
			ResXmlTreeeEndElementExt data = new ResXmlTreeeEndElementExt();
			data.ns = ResStringPoolRef.parseResStringPoolRef(src, offset);
			offset += data.ns.getByteSize();
			data.name = ResStringPoolRef.parseResStringPoolRef(src, offset);
			return data;
		}

		public ResXmlTreeeEndElementExt() {
		}

		public ResXmlTreeeEndElementExt(ResStringPoolRef ns, ResStringPoolRef name) {
			this.ns = ns;
			this.name = name;
		}

		public byte[] toBytes() {
			return ByteUtils.mergeBytes(ns.toBytes(), name.toBytes());
		}
	}

	@Override
	public int getType() {
		return TYPE_END;
	}

	@Override
	public byte[] toBytes() {
		return ByteUtils.mergeBytes(header.toBytes(),endEleExt.toBytes());
	}
}
