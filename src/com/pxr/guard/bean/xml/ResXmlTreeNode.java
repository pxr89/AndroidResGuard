package com.pxr.guard.bean.xml;

import com.pxr.guard.bean.string.ResStringPoolRef;
import com.pxr.guard.bean.table.ResChunkHeader;
import com.pxr.guard.utils.ByteUtils;

public class ResXmlTreeNode {
	public ResChunkHeader header;
	// Line number in original source file at which this element appeared.
	public int lineNumber;
	// Optional XML comment that was associated with this element; -1 if none.
	public ResStringPoolRef comment;

	public static ResXmlTreeNode parseNode(byte[] src, int offset) {
		ResXmlTreeNode node = new ResXmlTreeNode();
		node.header = ResChunkHeader.parseHeader(src, offset);
		offset += node.header.getByteSize();
		node.lineNumber = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		node.comment = ResStringPoolRef.parseResStringPoolRef(src, offset);
		return node;
	}

	public int getByteSize() {
		return header.getByteSize() + 4 + comment.getByteSize();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("ResXmlTreeNode-:");
		stringBuilder.append(header).append("  lineNumber:").append(lineNumber).append(" comment").append(comment);
		return stringBuilder.toString();
	}

	public byte[] toBytes() {
		return ByteUtils.mergeBytes(header.toBytes(), ByteUtils.int2Byte(lineNumber), comment.toBytes());
	}

	public ResXmlTreeNode() {
	}
	
	public ResXmlTreeNode(ResChunkHeader header, int lineNumber, ResStringPoolRef comment) {
		super();
		this.header = header;
		this.lineNumber = lineNumber;
		this.comment = comment;
	}
}
