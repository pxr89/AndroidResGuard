package com.pxr.guard.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class IOUtils {
	public static void closeIO(Closeable stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeIOs(Closeable... streams) {
		for (int i = 0; i < streams.length; i++) {
			closeIO(streams[i]);
		}
	}

	/**
	 * 读取文件并转换成byte
	 */
	public static byte[] readFile2Byte(String path) {
		ByteArrayOutputStream os = null;
		InputStream is = null;
		byte[] returnB = null;
		try {
			is = new FileInputStream(path);
			os = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int len = -1;
			while ((len = is.read(bytes)) != -1) {
				os.write(bytes, 0, len);
			}
			os.flush();
			returnB = os.toByteArray();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			closeIO(is);
			closeIO(os);
		}
		return returnB;
	}

	/**
	 * 将byte[] 写入文件
	 * 
	 * @param path
	 * @return
	 */
	public static void writeByte2File(byte[] bytes, File file) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeIO(fos);
		}
	}

	/**
	 * 将一段字节用gzip压缩
	 * @param data
	 * @return
	 */
	public static byte[] gZip(byte[] data) {
		byte[] b = null;
		ByteArrayOutputStream bos = null;
		GZIPOutputStream gzip = null;
		try {
			bos = new ByteArrayOutputStream();
			gzip = new GZIPOutputStream(bos);
			gzip.write(data);
			gzip.finish();
			b = bos.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeIOs(gzip, bos);
		}
		return b;
	}

	/**
	 * 解gzip压缩
	 * @param data
	 * @return
	 */
	public static byte[] unGZip(byte[] data) {
		byte[] b = null;
		ByteArrayInputStream bis = null;
		GZIPInputStream gzip = null;
		ByteArrayOutputStream baos = null;
		try {
			bis = new ByteArrayInputStream(data);
			gzip = new GZIPInputStream(bis);
			byte[] buf = new byte[1024];
			int num = -1;
			baos = new ByteArrayOutputStream();
			while ((num = gzip.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			baos.flush();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeIOs(baos, gzip, bis);
		}
		return b;
	}
}
