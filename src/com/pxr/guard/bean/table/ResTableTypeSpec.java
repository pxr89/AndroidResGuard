package com.pxr.guard.bean.table;

import java.util.Arrays;

import com.pxr.guard.bean.ITableRes;
import com.pxr.guard.bean.ResType;
import com.pxr.guard.constant.Constants;
import com.pxr.guard.utils.ByteUtils;

public class ResTableTypeSpec implements ITableRes {
	public static final int SPEC_PUBLIC = 0x40000000;

	public ResChunkHeader header;
	public byte id;// 标识资源的Type ID,Type
					// ID是指资源的类型ID。资源的类型有animator、anim、color、drawable、layout、menu、raw、string和xml等等若干种，每一种都会被赋予一个ID。
	public byte res0;
	public short res1;
	public int entryCount;

	public int[] entryCounts;
	
	public String typeName;

	// TODO header.size 大于 header.headerSize + 1+1+2+4 ，说明后面还有内容，但是格式是怎么的不知道了~~
	@Override
	public int getByteSize() {
		return header.size;
	}

	public static ResTableTypeSpec parseResTableTypeSpec(byte[] src, int offset) {
		ResTableTypeSpec ts = new ResTableTypeSpec();
		ts.header = ResChunkHeader.parseHeader(src, offset);
		offset += ts.header.getByteSize();
		ts.id = src[offset++];
		
		ts.typeName = ResTablePackage.typeStringLists.get((ts.id & 0xff) -1);
		
		ts.res0 = src[offset++];
		ts.res1 = ByteUtils.byte2Short(ByteUtils.copyByte(src, offset, 2));
		offset += 2;
		ts.entryCount = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));

		if (ts.entryCount > 0) {
			ts.entryCounts = new int[ts.entryCount];
			for (int i = 0; i < ts.entryCount; i++) {
				offset += 4;
				ts.entryCounts[i] = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
			}
		} else {
			ts.entryCounts = new int[] {};
		}

		return ts;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("\nResTableTypeSpec:\n");
		stringBuilder.append(Constants.TAB).append("|header:").append(header).append("\n")
		.append(" |id:").append((id & 0xFF))
		.append(" |res0:").append(res0)
		.append(" |res1:").append(res1)
		.append(" |entryCount:").append(entryCount).append("\n")
		.append("typeName:").append(typeName)
		.append("\n|entryCounts:").append(Arrays.toString(entryCounts));
		return stringBuilder.toString();
	}

	@Override
	public short getType() {
		return ResType.RES_TABLE_TYPE_SPEC_TYPE;
	}

}
