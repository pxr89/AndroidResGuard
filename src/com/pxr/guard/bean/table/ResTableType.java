package com.pxr.guard.bean.table;

import java.util.LinkedHashMap;

import com.pxr.guard.bean.ITableRes;
import com.pxr.guard.bean.ResType;
import com.pxr.guard.bean.ResValue;
import com.pxr.guard.constant.Constants;
import com.pxr.guard.utils.ByteUtils;

/**
 */
public class ResTableType implements ITableRes {
	public ResChunkHeader header;
	public byte id;
	public byte res0;
	public short res1;
	public int entryCount; // 不一定准确
	public int entriesStart;
	public ResTableConfig config;

	public LinkedHashMap<ResTableEntry, ResValue> entrys;

	public String typeName; // 类型

	public int getByteSize() {
		return header.size;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("\nResTableType:\n");
		s.append(Constants.TAB).append("|header:").append(header).append("\n").append("|id:").append((id & 0xff))
				.append("|res0:").append((res0 & 0xff)).append("|res1:").append(res1).append("|entryCount:")
				.append(entryCount).append("|entriesStart:").append(entriesStart).append("\n").append("typeName:")
				.append(typeName).append("\n");//.append("|config:").append(config).append("\n");
		for (ResTableEntry table : entrys.keySet()) {
			s.append(table).append("\n");
			if (entrys.get(table) == null) {

			} else {
				s.append(entrys.get(table)).append("\n");
			}
			s.append("=====================================\n");
		}
		return s.toString();
	}

	public static ResTableType parseResTableType(byte[] src, int offset) {
		ResTableType data = new ResTableType();
		int iOffset = offset;
		data.header = ResChunkHeader.parseHeader(src, offset);
		offset += data.header.getByteSize();
		data.id = src[offset++];

		data.typeName = ResTablePackage.typeStringLists.get((data.id & 0xff) - 1);

		data.res0 = src[offset++];
		data.res1 = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		data.entryCount = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;
		data.entriesStart = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
		offset += 4;

		data.config = ResTableConfig.parseResTableConfig(src, offset);

		// 解析复杂的entry实体内容
		offset = iOffset + data.entriesStart;
		data.entrys = new LinkedHashMap<>();
		short flags;
		ResTableEntry entry = null;
		ResValue value = null;
		for (int i = 0; i < data.entryCount && offset < (iOffset + data.header.size); i++) {
			// 1.获取entry的flags
			flags = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset + 2, 2));
			if (flags == ResTableEntry.FLAG_COMPLEX) {
				// 是map类型
				entry = ResTableMapEntry.parseResTableMapEntry(src, offset);
				offset += entry.getByteSize();
				data.entrys.put(entry, null);
			} else {
				entry = ResTableEntry.parseResTableEntry(src, offset);
				offset += entry.getByteSize();
				value = ResValue.parseResValue(src, offset);
				offset += value.getByteSize();
				data.entrys.put(entry, value);
			}
		}
		return data;
	}

	@Override
	public short getType() {
		return ResType.RES_TABLE_TYPE_TYPE;
	}
}
