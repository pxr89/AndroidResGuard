package com.pxr.guard.bean.table;

import java.util.Arrays;

import com.pxr.guard.bean.string.ResStringPoolRef;
import com.pxr.guard.utils.ByteUtils;

public class ResTableMapEntry extends ResTableEntry {
	// Resource identifier of the parent mapping, or 0 if there is none.
	public ResTableRef parent;
	// Number of name/value pairs that follow for FLAG_COMPLEX.
	public int count;

	public ResTableMap[] maps;

	public int getByteSize() {
		int size = key.getByteSize() + 2 + 2 + parent.getByteSize() + 4; // 16
		for (int i = 0; i < count; i++) {
			// 12
			size += maps[i].getByteSize();
		}
		return size;
	}

	public static ResTableMapEntry parseResTableMapEntry(byte[] src, int offset) {
		ResTableMapEntry data = new ResTableMapEntry();
		data.size = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		data.flags = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		data.key = ResStringPoolRef.parseResStringPoolRef(src, offset);
		offset += data.key.getByteSize();

		data.parent = ResTableRef.parseResTableRef(src, offset);
		offset += data.parent.getByteSize();
		data.count = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		data.maps = new ResTableMap[data.count];
		for (int i = 0; i < data.count; i++) {
			data.maps[i] = ResTableMap.parseResTableMap(src, offset);
			offset += data.maps[i].getByteSize();
		}
		return data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("ResTableMapEntry:\n");
		builder.append(" size:").append(size).append(" flags:").append(flags).append("key:").append(key).append(" str:")
				.append(ResTablePackage.getKeyString(key.index)).append(" parent:").append(parent).append(" count:")
				.append(count).append("\n").append("maps:").append(Arrays.toString(maps));
		return builder.toString();
	}

}
