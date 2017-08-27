package com.pxr.guard.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.pxr.guard.constant.Constants;

public class ApkSign {

	public static String USER_DIR = System.getProperty("user.dir");
	
	public static String DESTPATH;//文件签名后的最终位置
	public static String STOREPASS;
	public static String KEYPASS;
	public static String KEY;
	public static String KEYSTOREFILEPATH;

	public static boolean canSign = true;

	/**
	 * 读取签名配置
	 */
	static {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(Constants.SIGN_CONFIG));
			STOREPASS = prop.getProperty("storepass");
			KEYPASS = prop.getProperty("keypass");
			KEY = prop.getProperty("key");
			KEYSTOREFILEPATH = prop.getProperty("keystorefilepath");
			DESTPATH = prop.getProperty("destpath");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			canSign = false;
			e.printStackTrace();
		}
	}

	/**
	 * 签名
	 */
	public static void signApk(String unsignApkPath, String signApkPath, String ziplignenApkPath) {
		if (!canSign) {
			System.out.println("签名配置不完善");
			return;
		}
		StringBuilder cmdSign = new StringBuilder().append("jarsigner")
				.append("  -digestalg SHA1 -sigalg MD5withRSA ")
				.append("-storepass").append(" ").append(STOREPASS).append(" ")
				.append("-keypass").append(" ").append(KEYPASS).append(" ")
				.append("-keystore").append(" ").append(USER_DIR).append(File.separator).append(KEYSTOREFILEPATH)
				.append(" ").append("-signedjar").append(" ").append(DESTPATH+ File.separator + signApkPath)
				.append(" ").append(USER_DIR + File.separator + unsignApkPath).append(" ").append(KEY);
		execCmd(cmdSign.toString());

		FileUtils.deleteFiles(USER_DIR + File.separator + unsignApkPath);
		
		StringBuilder cmdSignVerify = new StringBuilder("jarsigner -verify ").append(DESTPATH+ File.separator + signApkPath);
		execCmd(cmdSignVerify.toString());
		
		// ziplign优化
//		cmdSign = new StringBuilder().append(ZIPALIGN).append(" -f -v 4 ").append(DESKTOP + File.separator + signApkPath)
//				.append(" ").append(DESKTOP + File.separator + ziplignenApkPath).append(" ");
//		if (0 == execCmd(cmdSign.toString())) {
//			FileUtils.deleteFiles(DESKTOP + File.separator + signApkPath);
//		} else {
//			System.out.println("ddds");
//		}

	}

	private static int execCmd(String cmd) {
		ByteArrayOutputStream boStream = null;
		InputStream is = null;
		try {
			Process exec = Runtime.getRuntime().exec(cmd);
			int waitFor = exec.waitFor();
			is = exec.getInputStream();
			boStream = new ByteArrayOutputStream();
			int len = -1;
			byte[] bytes = new byte[1024];
			while ((len = is.read(bytes)) != -1) {
				boStream.write(bytes, 0, len);
			}
			boStream.flush();
			System.out.println("cmd finish");
			System.out.println(waitFor);
			System.out.println(boStream.toString());
			return waitFor;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeIOs(boStream,is);
		}
		return 1;
	}

}
