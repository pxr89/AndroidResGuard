package com.pxr.guard.bean.string;

import java.util.ArrayList;
import java.util.Arrays;

import com.pxr.guard.bean.ITableRes;
import com.pxr.guard.bean.ResType;
import com.pxr.guard.constant.Constants;
import com.pxr.guard.exception.StringPoolLengthExpressException;
import com.pxr.guard.utils.ByteUtils;

/**
 * 注意stringpool的总byte数必须是4的倍数， TODO style 部分暂时不解析
 * 
 * @author panxianrong
 *
 */
public class ResStringPool implements ITableRes {
	public ResStringPoolHeader strPoolHeader;
	public ArrayList<Integer> stringOffsets;
	public ArrayList<Integer> styleOffsets;
	public ArrayList<String> stringPool;
	
	public ArrayList<ResStringPoolSpan> stylePool;

	//
	public ResStringPool() {
		stringOffsets = new ArrayList<>();
		styleOffsets = new ArrayList<>();
		stringPool = new ArrayList<>();
		stylePool = new ArrayList<>();
	}

	@Override
	public int getByteSize() {
		return strPoolHeader.header.size;
	}

	// 记录字符串的byte字节
	public byte[] stringOffsetsBytes;
	public byte[] styleOffsetsBytes;
	public byte[] stringPoolBytes;
	public byte[] stylePoolBytes;

	public byte[] toBytes() {
		int temp = strPoolHeader.header.size;
		int emptyByte = stringPoolBytes.length % 4;
		if (emptyByte != 0) {
			stringPoolBytes = ByteUtils.mergeBytes(stringPoolBytes, new byte[4 - emptyByte]);
		}
		strPoolHeader.header.size = strPoolHeader.getByteSize() + stringOffsetsBytes.length + stringPoolBytes.length;
		System.out.println("--------字符串修正偏差值--------：" + (temp - strPoolHeader.header.size));
		if (strPoolHeader.styleCount != 0) {
			strPoolHeader.header.size += (styleOffsetsBytes.length + stylePoolBytes.length);
		}
		return ByteUtils.mergeBytes(strPoolHeader.toBytes(), stringOffsetsBytes, styleOffsetsBytes, stringPoolBytes,
				stylePoolBytes);
	}

	/**
	 * 替换字符串池中角标为i的字符串为strR
	 * 
	 * @param index
	 * @param strR
	 */
	public void replaceString(int index, String strR) {
		if (index >= strPoolHeader.stringCount || index < 0) {
			return;
		}
		// 获取对应字符串长度的byte
		int stringSize;
		int offset = stringOffsets.get(index);
		// 记录这个字符串之前的数据
		byte[] beforeString = Arrays.copyOfRange(stringPoolBytes, 0, offset);

		int u16len = stringPoolBytes[offset] & 0xFF;
		offset++;
		if ((u16len & 0x80) == 0) {
			int u8len = stringPoolBytes[offset] & 0xFF;
			offset++;
			if ((u8len & 0x80) == 0) {
				stringSize = u8len + 1;
			} else {
				// 让外界处理吧
				throw new StringPoolLengthExpressException();
			}
		} else {
			throw new StringPoolLengthExpressException();
		}

		// 记录这个字符串之后的数据
		offset += stringSize;
		byte[] afterString = ByteUtils.copyByte(stringPoolBytes, offset, stringPoolBytes.length - offset);

		// 修改字节以及长度
		// 修改stringOffsets对应角标的及其以后所有的的offset值，
		byte[] newBytes = ByteUtils.generateStrLengthBytes(strR);
		int lenDiff = newBytes.length - (stringSize + 2);
		stringPool.set(index, strR);
		modifyOffset(index, lenDiff);

		// 修改header中style池的开始位置
		// strPoolHeader.stylesStart += lenDiff;
		strPoolHeader.header.size += lenDiff;

		// 生成新得字符串池bytes

		stringPoolBytes = ByteUtils.mergeBytes(beforeString, newBytes, afterString);
	}

	public void modifyOffset(int index, int lenDiff) {
		int newOffset, oldOffset;
		for (int i = index + 1; i < strPoolHeader.stringCount; i++) {
			oldOffset = stringOffsets.get(i);
			newOffset = oldOffset + lenDiff;
			ByteUtils.replaceBytes(stringOffsetsBytes, i * 4, ByteUtils.int2Byte(newOffset));
			stringOffsets.set(i, newOffset);
		}
	}

	// TODO 如果strPoolHeader的styleCount不为0，那么需要做style的解析
	public static ResStringPool parseResStringPool(byte[] src, int offset) {
		final int initalOffset = offset;
		ResStringPool pool = new ResStringPool();
		pool.strPoolHeader = ResStringPoolHeader.parseResStringPoolHeader(src, offset);

		offset += pool.strPoolHeader.getByteSize();

		pool.stringOffsetsBytes = ByteUtils.copyByte(src, offset, pool.strPoolHeader.stringCount * 4);
		for (int i = 0, len = pool.strPoolHeader.stringCount; i < len; i++) {
			pool.stringOffsets.add(ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4)));
			offset += 4;
		}

		pool.styleOffsetsBytes = ByteUtils.copyByte(src, offset, pool.strPoolHeader.styleCount * 4);
		for (int i = 0, len = pool.strPoolHeader.styleCount; i < len; i++) {
			pool.styleOffsets.add(ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4)));
			offset += 4;
		}
		String val;
		int stringSize = 0;
		// TODO 这里解析是有一定的限制的，只限定utf-8
		// stringpool里的String格式为 前两个字节表示长度，一般取一个字节就好了， + 字符串 + 00结束符
		// (如果是utf-16就是 0000结束符)
		final int stringStart = initalOffset + pool.strPoolHeader.stringsStart + pool.strPoolHeader.styleCount * 4;
		for (int i = 0; i < pool.strPoolHeader.stringCount; i++) {
			val = "";
			offset = stringStart + pool.stringOffsets.get(i);

			// 计算size 这里只提供utf-8编码方式的长度计算方式
			int u16len = src[offset] & 0xFF;
			offset++;
			if ((u16len & 0x80) == 0) {
				int u8len = src[offset] & 0xFF;
				offset++;
				if ((u8len & 0x80) == 0) {
					stringSize = u8len + 1;
				} else {
					int u8len_fix = src[offset] & 0xFF;
					offset++;
					stringSize = ((u8len & 0x7F) << 8) | u8len_fix + 1;
				}
			} else {
				int u16len_fix = src[offset] & 0xFF;
				offset++;
				int u8len = src[offset] & 0xFF;
				offset++;
				if ((u8len & 0x80) == 0) {
					stringSize = u8len + 1;
				} else {
					int u8len_fix = src[offset] & 0xFF;
					offset++;
					stringSize = (((u8len & 0x7F) << 8) | u8len_fix) + 1;
				}
			}
			// 获取字符串内容
			if (stringSize > 0) {
				try {
					val = new String(ByteUtils.copyByte(src, offset, stringSize), "utf-8");
				} catch (Exception e) {
					System.out.println("string encode error:" + e.toString());
				}
				pool.stringPool.add(val);
			} else {
				System.out.println("stringSize 为负数; i=" + i);
				pool.stringPool.add(val);
			}
			if (i == pool.strPoolHeader.stringCount - 1) {
				offset += stringSize;
			}
		}

		pool.stringPoolBytes = ByteUtils.copyByte(src, stringStart, offset - stringStart);
		// offset - initalOffset
		// 就是StringPool的内容的byte数，但是比strPoolHeader.header.size小两个字节，
		// 猜测是结束字符为 00 00，暂时这样子吧,但是有的结束字符是00 ,很奇怪
		return pool;
	}

	@Override
	public short getType() {
		return ResType.RES_STRING_POOL_TYPE;
	}

	public String getString(int index) {
		if (index < 0 || index >= stringOffsets.size()) {
			return "";
		}
		return stringPool.get(index);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("\n");
		s.append("ResStringPool:\n").append(Constants.TAB).append(strPoolHeader.toString()).append("\n");
		s.append("stringOffsets:\n");
		s.append("[");
		for (int i = 0, len = strPoolHeader.stringCount; i < len; i++) {
			s.append(ByteUtils.bytesToHexString(ByteUtils.int2Byte(stringOffsets.get(i)))).append(",");
		}
		s.append("]\n");
		s.append("stringPool start:\n");
		for (int i = 0, len = strPoolHeader.stringCount; i < len; i++) {
			s.append("i=" + i + ":" + stringPool.get(i)).append("\n");
		}
		s.append("stringPool end:\n");
		return s.toString();
	}

}
