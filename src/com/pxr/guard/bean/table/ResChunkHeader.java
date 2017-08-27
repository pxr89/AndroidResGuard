package com.pxr.guard.bean.table;

import com.pxr.guard.utils.ByteUtils;

/**
 * Header that appears at the front of every data chunk in a resource.
 * 
 * struct ResChunk_header { // Type identifier for this chunk. The meaning of
 * this value depends // on the containing chunk. uint16_t type; // Size of the
 * chunk header (in bytes). Adding this value to // the address of the chunk
 * allows you to find its associated data // (if any). uint16_t headerSize; //
 * Total size of this chunk (in bytes). This is the chunkSize plus // the size
 * of any data associated with the chunk. Adding this value // to the chunk
 * allows you to completely skip its contents (including // any child chunks).
 * If this value is the same as chunkSize, there is // no data associated with
 * the chunk. uint32_t size; };
 */
public class ResChunkHeader {
	public short type; // Constants.ResType
	public short headerSize;
	public int size;

	public int getByteSize() {
		return 2 + 2 + 4;
	}

	@Override
	public String toString() {
		return "ResChunkHeader:\n" + "type:" + ByteUtils.bytesToHexString(ByteUtils.short2Byte(type)) + "  headerSize:"
				+ headerSize + "  size:" + size;
	}

	public static ResChunkHeader parseHeader(byte[] src, int offset) {
		ResChunkHeader header = new ResChunkHeader();
		header.type = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		header.headerSize = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset + 2, 2));
		header.size = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset + 4, 4));
		return header;
	}

	public byte[] toBytes() {
		return ByteUtils.mergeBytes(ByteUtils.mergeBytes(ByteUtils.short2Byte(type), ByteUtils.short2Byte(headerSize)),
				ByteUtils.int2Byte(size));
	}
	public ResChunkHeader() {
		// TODO Auto-generated constructor stub
	}

	public ResChunkHeader(short type, short headerSize, int size) {
		super();
		this.type = type;
		this.headerSize = headerSize;
		this.size = size;
	}
	public ResChunkHeader(short type, short headerSize) {
		this.type = type;
		this.headerSize = headerSize;
	}
	public ResChunkHeader(short type) {
		this.type = type;
	}
}
