package com.pxr.guard.bean.xml;

import com.pxr.guard.bean.string.ResStringPoolRef;
import com.pxr.guard.utils.ByteUtils;

//RES_XML_START_NAMESPACE_TYPE = 0x0100;
//RES_XML_FIRST_CHUNK_TYPE = 0x0100;
//RES_XML_END_NAMESPACE_TYPE = 0x0101;
public class ResXmlTreeNamespace {
	public ResXmlTreeNode header;
	public ResXmlTreeNamespaceExt nsExt;

	public static ResXmlTreeNamespace parseNameSapce(byte[] src, int offset) {
		ResXmlTreeNamespace data = new ResXmlTreeNamespace();
		int i = offset;
		data.header = ResXmlTreeNode.parseNode(src, offset);
		offset += data.header.getByteSize();
		data.nsExt = ResXmlTreeNamespaceExt.parseResXmlTreeNamespaceExt(src, offset);
		data.nsBytes = ByteUtils.copyByte(src, i, data.getByteSize());
		return data;
	}

	public int getByteSize() {
		return header.header.size;
	}

	public static class ResXmlTreeNamespaceExt {
		public ResStringPoolRef prefix;
		public ResStringPoolRef uri;

		public static ResXmlTreeNamespaceExt parseResXmlTreeNamespaceExt(byte[] src, int offset) {
			ResXmlTreeNamespaceExt data = new ResXmlTreeNamespaceExt();
			data.prefix = ResStringPoolRef.parseResStringPoolRef(src, offset);
			offset += data.prefix.getByteSize();
			data.uri = ResStringPoolRef.parseResStringPoolRef(src, offset);
			return data;
		}

		public int getByteSize() {
			return prefix.getByteSize() + uri.getByteSize();
		}
	}

	private byte[] nsBytes;

	public byte[] toBytes() {
		return nsBytes;
	}
}
