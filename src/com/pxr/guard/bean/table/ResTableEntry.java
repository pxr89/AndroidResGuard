package com.pxr.guard.bean.table;

import com.pxr.guard.bean.string.ResStringPoolRef;
import com.pxr.guard.utils.ByteUtils;

public class ResTableEntry {
	public final static int FLAG_COMPLEX = 0x0001; //
	public final static int FLAG_PUBLIC = 0x0002;

	public short size;
	public short flags; // 如果是 FLAG_COMPLEX，那么就是ResTableMapEntry类型

	// Reference into ResTable_package::keyStrings identifying this entry.
	public ResStringPoolRef key;

	public int getByteSize() {
		return key.getByteSize() + 2 + 2;
	}

	public static ResTableEntry parseResTableEntry(byte[] src, int offset) {
		ResTableEntry data = new ResTableEntry();
		data.size = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		data.flags = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		data.key = ResStringPoolRef.parseResStringPoolRef(src, offset);
		return data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("\nResTableEntry:\n");
		builder.append(" size:").append(size).append(" flags:").append(flags).append(" key:").append(key)
				.append(" str:").append(ResTablePackage.getKeyString(key.index));
		return builder.toString();
	}

}
