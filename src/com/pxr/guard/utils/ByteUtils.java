package com.pxr.guard.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.pxr.guard.exception.StringTooLongException;

public class ByteUtils {

	/**
	 * 高位在后
	 * 
	 * @param b
	 * @return
	 */
	public static int byte2Int(byte[] res) {
		if (res == null) {
			return 0;
		}
		int targets = ((res[0] & 0xff)) | ((res[1] & 0xff) << 8) | ((res[2] & 0xff) << 16) | ((res[3] & 0xff) << 24);
		return targets;
	}

	/**
	 * 高位在后
	 * 
	 * @param integer
	 * @return
	 */
	public static byte[] int2Byte(final int integer) {
		byte[] byteArray = new byte[4];

		for (int n = 0; n < 4; n++)
			byteArray[n] = (byte) (integer >>> (n * 8));
		return byteArray;
	}

	/**
	 * 高位在后
	 * 
	 * @param b
	 * @return
	 */
	public static byte[] short2Byte(short b) {
		byte[] byteArray = new byte[2];
		byteArray[1] = (byte) (b >>> 8);
		byteArray[0] = (byte) b;
		return byteArray;
	}

	/**
	 * 高位在后
	 * 
	 * @param b
	 * @return
	 */
	public static short byte2Short(byte[] b) {
		if (b == null) {
			return 0;
		}
		short s = 0;
		short s0 = (short) (b[0] & 0xff);
		short s1 = (short) (b[1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;
	}

	/**
	 * 高位在前的正常顺序输出
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		// byte[] src = reverseBytes(src1);
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = src.length - 1; i >= 0; i--) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv + " ");
		}
		if (stringBuilder.length() > 1) {
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		return stringBuilder.toString();
	}

	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);
		return cb.array();
	}

	public static byte[] copyByte(byte[] src, int start, int len) {
		if (src == null) {
			return null;
		}
		if (start > src.length) {
			return null;
		}
		if ((start + len) > src.length) {
			return null;
		}
		if (start < 0) {
			return null;
		}
		if (len <= 0) {
			return null;
		}
		byte[] resultByte = new byte[len];
		for (int i = 0; i < len; i++) {
			resultByte[i] = src[i + start];
		}
		return resultByte;
	}

	public static byte[] copyByteBetween(byte[] src, int start, int end) {
		return copyByte(src, start, end - start);
	}

	public static byte[] reverseBytes(byte[] bytess) {
		byte[] bytes = new byte[bytess.length];
		for (int i = 0; i < bytess.length; i++) {
			bytes[i] = bytess[i];
		}
		if (bytes == null || (bytes.length % 2) != 0) {
			return bytes;
		}
		int i = 0, len = bytes.length;
		while (i < (len / 2)) {
			byte tmp = bytes[i];
			bytes[i] = bytes[len - i - 1];
			bytes[len - i - 1] = tmp;
			i++;
		}
		return bytes;
	}

	public static String filterStringNull(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		byte[] strByte = str.getBytes();
		ArrayList<Byte> newByte = new ArrayList<Byte>();
		for (int i = 0; i < strByte.length; i++) {
			if (strByte[i] != 0) {
				newByte.add(strByte[i]);
			}
		}
		byte[] newByteAry = new byte[newByte.size()];
		for (int i = 0; i < newByteAry.length; i++) {
			newByteAry[i] = newByte.get(i);
		}
		return new String(newByteAry);
	}

	public static String getStringFromByteAry(byte[] srcByte, int start) {
		if (srcByte == null) {
			return "";
		}
		if (start < 0) {
			return "";
		}
		if (start >= srcByte.length) {
			return "";
		}
		byte val = srcByte[start];
		int i = 1;
		ArrayList<Byte> byteList = new ArrayList<Byte>();
		while (val != 0) {
			byteList.add(srcByte[start + i]);
			val = srcByte[start + i];
			i++;
		}
		byte[] valAry = new byte[byteList.size()];
		for (int j = 0; j < byteList.size(); j++) {
			valAry[j] = byteList.get(j);
		}
		try {
			return new String(valAry, "UTF-8");
		} catch (Exception e) {
			System.out.println("encode error:" + e.toString());
			return "";
		}
	}

	/**
	 * 两个byte数组合并
	 * 
	 * @param res
	 * @return
	 */
	public static byte[] mergeBytes(byte[]... bytes) {
		int tatol = 0;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == null) {
				continue;
			}
			tatol += bytes[i].length;
		}
		byte[] returnB = new byte[tatol];
		int temp = 0;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == null) {
				continue;
			}
			for (int j = 0; j < bytes[i].length; j++) {
				returnB[temp + j] = bytes[i][j];
			}
			temp += bytes[i].length;
		}
		return returnB;
	}

	/**
	 * 将srcReplace从start处全部替换为 r
	 * 
	 * @param srcReplace
	 * @param start
	 * @param r
	 */
	public static void replaceBytes(byte[] srcReplace, int start, byte[] r) {
		for (int i = 0; i < r.length; i++) {
			srcReplace[start + i] = r[i];
		}
	}

	/**
	 * str长度不要超过127 生成一个str的byte，两个字节
	 * 
	 * @return
	 */
	public static byte[] generateStrLengthBytes(String str) {
		byte[] strByte = str.getBytes();
		if (strByte.length > Byte.MAX_VALUE) {
			throw new StringTooLongException();
		}
		byte length = (byte) (strByte.length);
		return ByteUtils.mergeBytes(new byte[] { length, length }, strByte, new byte[] { (byte) 0 });
	}

	/**
	 * str长度不要超过127 生成一个str的byte，两个字节一个字符 for xml
	 * 
	 * @return
	 */
	public static byte[] generateXmlStrBytes(String str) {
		final byte[] bytes = str.getBytes();
		if (bytes.length > Byte.MAX_VALUE) {
			throw new StringTooLongException();
		}
		byte length = (byte) (bytes.length);
		byte[] strByte = new byte[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			strByte[i * 2] = bytes[i];
		}
		return ByteUtils.mergeBytes(new byte[] { length, 0 }, strByte, new byte[] { (byte) 0, (byte) 0 });
	}

	public static byte[] generateLongStrBytes(String str) {
		final byte[] bytes = str.getBytes();
		byte[] strByte = new byte[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			strByte[i * 2] = bytes[i];
		}
		return ByteUtils.mergeBytes(strByte, new byte[] { (byte) 0, (byte) 0 });
	}

	/**
	 * 加密byte
	 * @param src
	 * @return
	 */
	public static byte[] codeByte(byte[] src) {
		for (int i = 0; i < src.length; i++) {
			src[i] ^= 0xff;
		}
		return src;
	}
}
