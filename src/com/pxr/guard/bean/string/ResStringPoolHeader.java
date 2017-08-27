package com.pxr.guard.bean.string;

import com.pxr.guard.bean.table.ResChunkHeader;
import com.pxr.guard.constant.Constants;
import com.pxr.guard.utils.ByteUtils;

/**
 * @author panxianrong
 *
 */
public class ResStringPoolHeader {
	public ResChunkHeader header;
	public int stringCount;
	public int styleCount;// 通常为0
	public int flags;// 字符串的属性,可取值包括0x000(UTF-16),0x001(字符串经过排序)、0X100(UTF-8)和他们的组合值
	public int stringsStart; // 字符串内容块相对于其头部的距离
	public int stylesStart; // 字符串样式块相对于其头部的距离

	// TODO 注意 stringpool修改的时候需要修改stylesStart，如果没有style的话，不修改也没事~~
	public byte[] toBytes() {
		return ByteUtils.mergeBytes(header.toBytes(), ByteUtils.int2Byte(stringCount), ByteUtils.int2Byte(styleCount),
				ByteUtils.int2Byte(flags), ByteUtils.int2Byte(stringsStart),
//				ByteUtils.int2Byte(stylesStart == 0 ? -1 : stylesStart));
		ByteUtils.int2Byte(stylesStart));
	}

	public int getByteSize() {
		return header.headerSize; // == header.getByteSize() + 4 + 4 + 4 + 4 + 4
	}

	public static ResStringPoolHeader parseResStringPoolHeader(byte[] srcByte, int offset) {
		ResStringPoolHeader poolHeader = new ResStringPoolHeader();
		poolHeader.header = ResChunkHeader.parseHeader(srcByte, offset);
		offset += poolHeader.header.getByteSize();
		poolHeader.stringCount = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));
		offset += 4;
		poolHeader.styleCount = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));
		offset += 4;
		poolHeader.flags = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));
		offset += 4;
		poolHeader.stringsStart = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));
		offset += 4;
		poolHeader.stylesStart = ByteUtils.byte2Int(ByteUtils.copyByte(srcByte, offset, 4));
		return poolHeader;
	}

	@Override
	public String toString() {
		return "ResStringPoolHeader:\n" + Constants.TAB + "header:" + header + "\n" + Constants.TAB + "stringCount:"
				+ stringCount + "\n" + Constants.TAB + "styleCount:" + styleCount + "\n" + Constants.TAB + "flags:"
				+ flags + "\n" + Constants.TAB + "stringsStart:" + stringsStart + "\n" + Constants.TAB + "stylesStart:"
				+ stylesStart;
	}

}
