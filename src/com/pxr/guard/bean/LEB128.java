package com.pxr.guard.bean;

import java.util.Arrays;

/**
 * 这里使用的是无符号的LEB1128
 * 
 * @author panxianrong
 *
 */
public class LEB128 {
	public byte len;// 长度 1-5的值
	public int value;
	public byte[] bytes;

	/**
	 * 读取一个
	 * 
	 * @return
	 */
	public static LEB128 readLEB128(byte[] bytes, int offset) {
		LEB128 leb128 = new LEB128();
		int result = bytes[offset++] & 0xFF;
		leb128.len++;
		if (result > 0x7f) {
			leb128.len++;
			int cur = bytes[offset++] & 0xFF;
			result = (result & 0x7f) | ((cur & 0x7f) << 7);
			if (cur > 0x7f) {
				leb128.len++;
				cur = bytes[offset++] & 0xFF;
				result |= (cur & 0x7f) << 14;
				if (cur > 0x7f) {
					leb128.len++;
					cur = bytes[offset++] & 0xFF;
					result |= (cur & 0x7f) << 21;
					if (cur > 0x7f) {
						leb128.len++;
						cur = bytes[offset++] & 0xFF;
						result |= cur << 28;
					}
				}
			}
		}
		leb128.value = result;
		leb128.bytes = Arrays.copyOfRange(bytes, offset - leb128.len, offset);
		return leb128;
	}

	/**
	 * 还原一个LEB128的BYTE值
	 * 
	 * static inline uint8_t* EncodeUnsignedLeb128(uint8_t* dest, uint32_t
	 * value) { uint8_t out = value & 0x7f; value >>= 7; while (value != 0) {
	 * dest++ = out | 0x80; out = value & 0x7f; value >>= 7; } dest++ = out;
	 * return dest; }
	 * 
	 */
	public static byte[] genereteLeb(int value) {
		byte[] bytes = new byte[5];
		byte out = (byte) (value & 0x7f);
		value >>= 7;
		int i = 0;
		while (value != 0) {
			bytes[i] = (byte) (out | 0x80);
			out = (byte) (value & 0x7f);
			value >>= 7;
			i++;
		}
		bytes[i] = out;
		return Arrays.copyOfRange(bytes, 0, i+1);
	}
}
