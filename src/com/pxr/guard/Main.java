package com.pxr.guard;

import com.pxr.guard.utils.GuardUitls;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException {
		guad();
	}

	private static void guad() {
		GuardUitls.startGuard();
	}

//	private static void makeDirs(String dirPath) {
//		File fileSign = new File(dirPath);
//		if (!fileSign.exists()) {
//			fileSign.mkdirs();
//		}
//	}
//
//	private static void guardActivity() {
//		makeDirs(Constants.GUARD_SIGN_APKS);
//		makeDirs(Constants.GUARD_UNSIGN_APKS);
//		Mapping.init();
//		String apkPath = "src_apks/app.apk";
//		String apkName = FileUtils.getFileName(new File(apkPath));
//
//		byte[] srcByte = FileUtils.decodeApk(apkPath, "classes.dex");
//		Dex dex = Dex.parseDex(srcByte, 0);
//
//		// TODO
//		HashSet<String> activity = Mapping.EXCEPT_MAPS.get("activity");
//		if (activity == null) {
//			System.out.println("activity null");
//			return;
//		}
//		for (String activityName : activity) {
//			System.out.println(activityName);
//			dex.strings.replaceActivityName(activityName, srcByte);
//		}
//		
//		DexUtils.fixFileSizeHeader(srcByte);
//		DexUtils.fixSh1Signature(srcByte);
//		DexUtils.fixCheckSum(srcByte);
////		
////		// TODO 修复header的
////
//		try {
//			FileUtils.upZipFile(new File(apkPath), apkName);
//		} catch (ZipException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		// 解析xml; 将xml中的注册activity换掉
//		// guard xml
//		byte[] xmlByte = FileUtils.decodeApk(apkPath, "AndroidManifest.xml");
//		XmlRes xmlRes = XmlRes.parseXmlRes(xmlByte);
//		for (String xmlName : dex.strings.replaceMap.keySet()) {
//			xmlRes.strPool.replaceString(xmlName, dex.strings.replaceMap.get(xmlName));
//		}
//			xmlRes.strPool.replaceString("com.tupo.test.TestSecondActivity", "com.tupo.test.ToooSoooodAooooooy");
//		deleteFile(apkName + File.separator + "AndroidManifest.xml");
//		deleteFile(apkName + File.separator + "classes.dex");
//		FileUtils.deleteFiles(apkName + File.separator+"META-INF");
//
//		IOUtils.writeByte2File(xmlRes.toBytes(), FileUtils.newFile(apkName + File.separator + "AndroidManifest.xml"));
//
//		IOUtils.writeByte2File(srcByte, FileUtils.newFile(apkName + File.separator + "classes.dex"));
//
//		// 依据mapping 修改文件路径和名字 ,复制文件
//
//		// 压缩文件
//		String unsignApkPath = Constants.GUARD_UNSIGN_APKS + File.separator + apkName + ".apk";
//		FileUtils.compressApk(apkName, unsignApkPath);
//		// 删除临时文件
//		FileUtils.deleteFiles(apkName);
//		
//		// 签名apk
//		ApkSign.signApk(unsignApkPath, apkName + "_sign_.apk",
//				// ApkSign.signApk(unsignApkPath, Constants.GUARD_SIGN_APKS +
//				// File.separator + apkName + "_sign_.apk",
//				Constants.GUARD_SIGN_APKS + File.separator + apkName + "_sign_guardres.apk");
//
//	}
//	
//	private static void deleteFile(String path){
//		File newFile = FileUtils.newFile(path);
//		if (newFile.exists()) {
//			newFile.delete();
//		}
//	}
//
//	private static void guardXml() {
//		String apkPath = "src_apks/stu_1.apk";
//		ArrayList<String> exceptCopyFiles = new ArrayList<>();
//		String apkName = FileUtils.getFileName(new File(apkPath));
//
//		// guard xml
//		byte[] srcByte = FileUtils.decodeApk(apkPath, "AndroidManifest.xml");
//		if (srcByte == null) {
//			System.out.println("resources decode fail");
//			return;
//		}
//		XmlRes xmlRes = XmlRes.parseXmlRes(srcByte);
//		System.out.println("parse xml finish");
//		xmlRes.addAttr("application", null, "tupo", "xuetuan");
//		String appName = xmlRes.replaceAttrValue("application", "name", "com.pxr.guard.ProxyApp");
//		System.out.println("source appname:" + appName);
//		// exceptCopyFiles.add("resources.arsc");
//		// 生成新的xml文件
//		IOUtils.writeByte2File(xmlRes.toBytes(), FileUtils.newFile(apkName + File.separator + "AndroidManifest.xml"));
//		exceptCopyFiles.add("AndroidManifest.xml");
//
//		// guard dex
//		// 复制jiagu.so
//		FileUtils.copyFiles("lib", apkName);
//
//		// 解壳dex + appname + 原来dex + (appname 和 原dex 的长度)
//		byte[] unShellDex = IOUtils.readFile2Byte("classes.dex");
//
//		byte[] appNameByte = appName.getBytes();
//		System.out.println("appNameByte:" + appNameByte.length);
//		appNameByte = ByteUtils.mergeBytes(ByteUtils.short2Byte((short) appNameByte.length), appNameByte);
//
//		appNameByte = ByteUtils.codeByte(appNameByte);
//		byte[] dexByte = FileUtils.decodeApk(apkPath, "classes.dex");
//		System.out.println("dexsize:" + dexByte.length);
//		// 压缩dexbyte
//		dexByte = IOUtils.gZip(dexByte);
//		System.out.println("dexsize zipsize:" + dexByte.length);
//		// 加密byte
//		dexByte = ByteUtils.codeByte(dexByte);
//
//		byte[] length = ByteUtils.int2Byte(dexByte.length + appNameByte.length); // 注意这个length
//		System.out.println("all_len:" + (dexByte.length + appNameByte.length));
//		length = ByteUtils.codeByte(length); // 是地位在前的顺序
//
//		unShellDex = ByteUtils.mergeBytes(unShellDex, appNameByte, dexByte, length);
//
//		// 注意顺序~~搞了我半天
//		DexUtils.fixFileSizeHeader(unShellDex);
//		DexUtils.fixSh1Signature(unShellDex);
//		DexUtils.fixCheckSum(unShellDex);
//
//		IOUtils.writeByte2File(unShellDex, FileUtils.newFile(apkName + File.separator + "classes.dex"));
//
//		exceptCopyFiles.add("classes.dex");
//
//		// 依据mapping 修改文件路径和名字 ,复制文件
//		FileUtils.copyApkFilesAndRename(apkPath, apkName, null, exceptCopyFiles);
//
//		// 压缩文件
//		String unsignApkPath = Constants.GUARD_UNSIGN_APKS + File.separator + apkName + ".apk";
//		FileUtils.compressApk(apkName, unsignApkPath);
//		// 删除临时文件
//		FileUtils.deleteFiles(apkName);
//		// 签名apk
//		ApkSign.signApk(unsignApkPath, apkName + "_sign_.apk",
//				// ApkSign.signApk(unsignApkPath, Constants.GUARD_SIGN_APKS +
//				// File.separator + apkName + "_sign_.apk",
//				Constants.GUARD_SIGN_APKS + File.separator + apkName + "_sign_GXML.apk");
//
//	}

}
