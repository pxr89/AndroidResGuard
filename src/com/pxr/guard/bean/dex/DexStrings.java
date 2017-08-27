package com.pxr.guard.bean.dex;

import java.util.HashMap;
import java.util.HashSet;

import com.pxr.guard.bean.LEB128;
import com.pxr.guard.utils.ByteUtils;
import com.pxr.guard.utils.NameUtils;

/**
 * dex的字符串池
 * 
 * @author panxianrong
 *
 */
public class DexStrings {

	public int size; // 等于 DexHeader string_ids_size
	public int offset; // 等于 DexHeader string_ids_off

	public int[] offsets; // 所有的stringid的offset

	public HashSet<String> dexStr = new HashSet<>(); // 对应string的set
	public HashMap<String, Integer> strMap = new HashMap<>(); // 对应string的map

	public HashMap<String, String> replaceMap = new HashMap<>();

	public static DexStrings parseDexStrings(DexHeader header, byte[] src) {
		DexStrings data = new DexStrings();
		data.size = header.string_ids_size;
		data.offset = header.string_ids_off;
		data.offsets = new int[data.size];
		int offset = data.offset;
		for (int i = 0; i < data.size; i++) {
			int lebOffset = ByteUtils.byte2Int(ByteUtils.copyByte(src, offset, 4));
			data.offsets[i] = lebOffset;
			offset += 4;
			// 解析数据
			LEB128 leb128 = LEB128.readLEB128(src, lebOffset);
			int strOffset = lebOffset + leb128.len;
			String str = null;
			try {
				str = new String(ByteUtils.copyByte(src, strOffset, leb128.value), "utf-8").trim();
			} catch (Exception e) {
				System.out.println("string encode error:" + e.toString());
				continue;
			}
			System.out.println(str);
			data.dexStr.add(str);
			data.strMap.put(str, i);
		}

		return data;
	}

	/**
	 * 生成替换的字符串
	 * 
	 * @param activityName
	 *            格式应该为com.xxx.xxx 或者 Lcom.xxx.xxx; 这个类不能是内部类啊~~
	 */
	public void replaceActivityName(String activityName, byte[] data) {
		// 1.简单点直接替换成 aa.dd一个点的方式
		// 先获取文件名
		if (activityName == null) {
			System.out.println("activityName null");
			return;
		}
		if (!dexStr.contains(activityName)) {
			System.out.println("activityName:" + activityName + " not in set!");
			return;
		}

		String xmlActivityName = getDotActivityName(activityName);
		System.out.println(xmlActivityName);

		String classNameold = getActivityName(activityName);
		System.out.println("className:" + classNameold);
		String classNameNew = null;
		if (dexStr.contains(classNameold + ".java")) {
			// 说明是有这个类的，文件也在
//			classNameNew = NameUtils.getActivityName(dexStr);
			classNameNew = classNameold.replace("TestSecondActivity", "ToooSoooodAooooooy");

		} else {
			System.out.println("classname " + classNameold + " not found");
			return;
		}
		String classPath = null;
		int nameIndex = 0;
		String path = activityName.substring(1,activityName.lastIndexOf("/"));
//		do {
//			classPath = "L" + NameUtils.getIndexName(nameIndex) + "/" + classNameNew + ";";
//		} while (dexStr.contains(classPath));
		 classPath = "L" + path + "/" + classNameNew + ";";

		dexStr.add(classPath);

		replaceMap.put(xmlActivityName, getDotActivityName(classPath));
		// TODO 真正的将 str替换掉

		// str的长度不替换掉，但是 内容替换成我们对应的内容
		replaceString(activityName, classPath, data);

		// 修改类文件名
//		replaceString(classNameold + ".java", classNameNew + ".java", data);
	}

	private void replaceString(String oldStr, String newStr, byte[] data) {
		Integer arrIndex = strMap.get(oldStr);
		LEB128 readLEB128 = LEB128.readLEB128(data, offsets[arrIndex]);
		byte[] genereteLeb = LEB128.genereteLeb(newStr.length());
		byte[] mergeBytes = ByteUtils.mergeBytes(genereteLeb, newStr.getBytes());
		mergeBytes = ByteUtils.mergeBytes(mergeBytes, new byte[readLEB128.value - mergeBytes.length + 1]);
		// 将这个长度的data换成我们需要的data
		System.arraycopy(mergeBytes, 0, data, offsets[arrIndex], mergeBytes.length);		
		System.out.println("oldStr: " + oldStr + " been replaced by newStr: " + newStr);

	}

	private String getActivityName(String activityFullName) {
		return activityFullName.substring(activityFullName.lastIndexOf("/") + 1, activityFullName.lastIndexOf(";"));
	}

	private String getDotActivityName(String activityName) {
		String returnData = activityName.substring(1, activityName.length() - 1);
		return returnData.replaceAll("/", ".");
	}

}
