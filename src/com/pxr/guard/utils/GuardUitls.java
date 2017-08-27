package com.pxr.guard.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.pxr.guard.Mapping;
import com.pxr.guard.bean.ARSCRes;
import com.pxr.guard.bean.ResValue;
import com.pxr.guard.bean.XmlRes;
import com.pxr.guard.bean.table.ResTableEntry;
import com.pxr.guard.bean.table.ResTablePackage;
import com.pxr.guard.bean.table.ResTableType;
import com.pxr.guard.constant.Constants;

/**
 * 分析并修改arsc文件
 * @author panxianrong
 *
 */
public class GuardUitls {

	public static void startGuard() {
		makeDirs(Constants.GUARD_SIGN_APKS);
		makeDirs(Constants.GUARD_UNSIGN_APKS);
		Mapping.init();

		File srcApks = new File("src_apks");
		File[] files = srcApks.listFiles();
		for (File apk : files) {
			if (apk.exists() && apk.isFile() && apk.getName().endsWith(".apk")) {
				System.out.println(apk.getName());
				guard("src_apks/" + apk.getName());
			}
		}
	}

	private static void makeDirs(String dirPath) {
		File fileSign = new File(dirPath);
		if (!fileSign.exists()) {
			fileSign.mkdirs();
		}
	}

	private static void guard(String apkPath) {
		ArrayList<String> exceptCopyFiles = new ArrayList<>();

		byte[] srcByte = FileUtils.decodeApkRes(apkPath);
		if (srcByte == null) {
			System.out.println("resources decode fail");
			return;
		}
		// 解析 arsc 文件
		ARSCRes res = ARSCRes.parseRes(srcByte);
		// 初始化Mapping
		Mapping map = new Mapping(res.tablePackage.typeStringPool.stringPool);
		// guardres
		replaceResStrings(res, map);
		String apkName = FileUtils.getApkName(new File(apkPath));
		// 生成新的arsc文件
		IOUtils.writeByte2File(res.toBytes(), FileUtils.newFile(apkName + File.separator + "resources.arsc"));
		exceptCopyFiles.add("resources.arsc");

		// guardDex
//		guardDex(apkPath, exceptCopyFiles);

		// 依据mapping 修改文件路径和名字 ,复制文件
		FileUtils.copyApkFilesAndRename(apkPath, apkName, map.file_map, exceptCopyFiles);
		// 压缩文件
		String unsignApkPath = Constants.GUARD_UNSIGN_APKS + File.separator + apkName + ".apk";
		FileUtils.compressApk(apkName, unsignApkPath);
		// 删除临时文件
		FileUtils.deleteFiles(apkName);

		// 签名apk
		ApkSign.signApk(unsignApkPath, apkName + "_sign_.apk",
				// ApkSign.signApk(unsignApkPath, Constants.GUARD_SIGN_APKS +
				// File.separator + apkName + "_sign_.apk",
				Constants.GUARD_SIGN_APKS + File.separator + apkName + "_sign_guardres.apk");

	}

	private static void guardDex(String apkPath, ArrayList<String> exceptCopyFiles) {
		long guardDexStart = System.currentTimeMillis();

		String apkName = FileUtils.getFileName(new File(apkPath));

		// guard xml
		byte[] srcByte = FileUtils.decodeApk(apkPath, "AndroidManifest.xml");
		if (srcByte == null) {
			System.out.println("resources decode fail");
			return;
		}
		XmlRes xmlRes = XmlRes.parseXmlRes(srcByte);
		System.out.println("parse xml finish");
		xmlRes.addAttr("application", null, "tupo", "xuetuan");
		String appName = xmlRes.replaceAttrValue("application", "name", "com.pxr.guard.ProxyApp");
		System.out.println("source appname:" + appName);
		// exceptCopyFiles.add("resources.arsc");
		// 生成新的xml文件
		IOUtils.writeByte2File(xmlRes.toBytes(), FileUtils.newFile(apkName + File.separator + "AndroidManifest.xml"));
		exceptCopyFiles.add("AndroidManifest.xml");

		// guard dex
		// 复制jiagu.so
		FileUtils.copyFiles("lib", apkName);

		// 解壳dex + appname + 原来dex + (appname 和 原dex 的长度)
		byte[] unShellDex = IOUtils.readFile2Byte("classes.dex");

		// appName
		byte[] appNameByte = appName.getBytes();
		System.out.println("appNameByte:" + appNameByte.length);
		appNameByte = ByteUtils.mergeBytes(ByteUtils.short2Byte((short) appNameByte.length), appNameByte);

		// 所有的dex
		ArrayList<String> dexNames = FileUtils.getApkDexNames(apkPath);
		System.out.println("dex count:" + dexNames.size());
		byte[] dexByte = null;
		byte[] dexCount = ByteUtils.int2Byte(dexNames.size());
		for (int i = 0; i < dexNames.size(); i++) {
			String name = dexNames.get(i);
			byte[] dexB = FileUtils.decodeApk(apkPath, name);
			dexCount = ByteUtils.mergeBytes(dexCount, ByteUtils.int2Byte(dexB.length));
			dexByte = ByteUtils.mergeBytes(dexByte, dexB);
			exceptCopyFiles.add(name);
			System.out.println("dex " + i + " length:" + dexB.length);
		}
		dexByte = ByteUtils.mergeBytes(appNameByte, dexCount, dexByte);
		// 压缩dexbyte
		dexByte = IOUtils.gZip(dexByte);
		// 加密byte
		dexByte = ByteUtils.codeByte(dexByte);
		byte[] totalLength = ByteUtils.int2Byte(dexByte.length);// 计算总长度
		
		System.out.println("total length:"+dexByte.length);

		// 为了整齐==!
		int eggsNum = 4 - dexByte.length % 4;
		byte[] eggs = null;
		if (eggsNum == 4) {
			eggsNum = 0;
		}
		eggs = new byte[eggsNum];
		System.out.println("eggs num:" + eggsNum);

		unShellDex = ByteUtils.mergeBytes(unShellDex,"tupo".getBytes() ,totalLength, dexByte, eggs);

		// 注意顺序~~
		DexUtils.fixFileSizeHeader(unShellDex);
		DexUtils.fixSh1Signature(unShellDex);
		DexUtils.fixCheckSum(unShellDex);

		IOUtils.writeByte2File(unShellDex, FileUtils.newFile(apkName + File.separator + "classes.dex"));
		
		System.out.println("guard " + apkName + " cost:" + (System.currentTimeMillis() - guardDexStart) + " millis");
	}

	private static void replaceResStrings(ARSCRes res, Mapping map) {
		// 迭代 res.tablePackage.resTableTypes的资源
		ResTableType resTableType;
		HashMap<String, String> maps = null;
		HashSet<String> exceptset = null;
		HashSet<String> keys = null;
		try {

			for (int i = 0, len = res.tablePackage.resTableTypes.size(); i < len; i++) {
				resTableType = res.tablePackage.resTableTypes.get(i);
				// 获取存储mapp关系的map
				maps = map.typeMaps.get(resTableType.typeName);
				keys = map.typeKeys.get(resTableType.typeName);
				exceptset = Mapping.EXCEPT_MAPS.get(resTableType.typeName.trim());

				String keyName; // 具体的type typeStringPool中
				for (ResTableEntry entry : resTableType.entrys.keySet()) {
					keyName = ResTablePackage.getKeyString(entry.key.index).trim();
					if (maps.containsKey(keyName)) {
						// 说明已经放进去了
					} else {
						// 是否是例外
						if (keyName.startsWith("umeng_social") || (exceptset != null && exceptset.contains(keyName))) {
							System.out.println("不混淆type：" + resTableType.typeName + " :name:" + keyName);
						} else {
							String modifyValue = NameUtils.getName(keys);
							maps.put(keyName, NameUtils.getName(keys));
							// 修改keyStringPool的值
							res.tablePackage.keyStringPool.replaceString(entry.key.index, modifyValue);
						}
					}
					// 判断是否是一个文件value
					ResValue value = resTableType.entrys.get(entry);
					if (value == null) {
						// 说明是个mapentry, 不用处理
					} else {
						if (value.dataType == ResValue.TYPE_STRING) {
							// 说明是一个文件value
							String oldfilePath = res.resStringPool.getString(value.data).trim();
							String newPath = map.generateNewFileRecord(oldfilePath);
							if (newPath != null) {
								res.resStringPool.replaceString(value.data, newPath);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
