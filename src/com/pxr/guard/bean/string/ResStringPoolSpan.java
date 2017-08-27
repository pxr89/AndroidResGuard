package com.pxr.guard.bean.string;

import com.pxr.guard.utils.ByteUtils;

/**
 * This structure defines a span of style information associated with a string
 * in the pool.
 */
public class ResStringPoolSpan {
	public static final int END =  0xFFFFFFFF;

    // This is the name of the span -- that is, the name of the XML
    // tag that defined it.  The special value END (0xFFFFFFFF) indicates
    // the end of an array of spans.
	public ResStringPoolRef name;// FF FF FF FF 则表示无样式

	public int firstChar, lastChar;

	public int offset;
	public int getByteSize() {
		return offset;
	}

	@Override
	public String toString() {
		return " |name:" + name + " |firstChar:" + ByteUtils.bytesToHexString(ByteUtils.int2Byte(firstChar)) + " |lastChar:"
				+ ByteUtils.bytesToHexString(ByteUtils.int2Byte(lastChar));
	}

	public static ResStringPoolSpan parseResStringPoolSpan(byte[] src, int offset) {
		ResStringPoolSpan ref = new ResStringPoolSpan();
		int first = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		if(first == END){
			ref.offset = 4;
		} else {
			offset += 4;
			ref.firstChar = first;
			ref.lastChar = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
			ref.offset = 12;
		}
		ref.name = new  ResStringPoolRef(END); 
		return ref;
	}

}
