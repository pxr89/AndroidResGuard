package com.pxr.guard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

public class DexUtils {
	/**
	 * 修复checksum
	 * @param dexBytes
	 */
	public static void fixCheckSum(byte[] dexBytes) {
		Adler32 adler = new Adler32();
		adler.update(dexBytes, 12, dexBytes.length - 12);// 从12到文件末尾计算校验码
		long value = adler.getValue();
		System.arraycopy(ByteUtils.int2Byte((int) value), 0, dexBytes, 8, 4);
		System.out.println(Long.toHexString(value));
	}

	/**
	 * 修复签名
	 * @param dexBytes
	 * @throws NoSuchAlgorithmException
	 */
	public static void fixSh1Signature(byte[] dexBytes) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		md.update(dexBytes, 32, dexBytes.length - 32);// 从32为到结束计算sha--1
		byte[] newdt = md.digest();
		System.arraycopy(newdt, 0, dexBytes, 12, 20);
		System.out.println(ByteUtils.bytesToHexString(newdt));
	}

	/**
	 * 修复filesize
	 * @param dexBytes
	 */
	public static void fixFileSizeHeader(byte[] dexBytes) {
		System.arraycopy(ByteUtils.int2Byte(dexBytes.length), 0, dexBytes, 32, 4);
	}
	
	

}
