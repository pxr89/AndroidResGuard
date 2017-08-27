package com.pxr.guard.bean.string;

import com.pxr.guard.utils.ByteUtils;

/**
 * Reference to a string in a string pool.
 */
public class ResStringPoolRef {
	// Index into the string pool table (uint32_t-offset from the indices
	// immediately after ResStringPool_header) at which to find the location
	// of the string data in the pool.

	public int index;

	public int getByteSize() {
		return 4;
	}

	public ResStringPoolRef(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return " |index:" + ByteUtils.bytesToHexString(ByteUtils.int2Byte(index)) + "--" + index;
	}

	public static ResStringPoolRef parseResStringPoolRef(byte[] src, int offset) {
		return new ResStringPoolRef(ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4)));
	}

	public byte[] toBytes() {
		return ByteUtils.int2Byte(index);
	}
}
