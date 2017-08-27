package com.pxr.guard.utils;

import java.util.HashSet;

/**
 * 计算name使用
 * 
 * @author panxianrong
 *
 */
public class NameUtils {
	public static final char[] CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public static final int LEN = CHARS.length;

	/**
	 * index must >= 0 依据index生成name
	 * 
	 * @param index
	 * @param keylists
	 * @return
	 */
	public static String getName(HashSet<String> keySet) {
		int index = keySet.size();
		StringBuilder stringBuilder = new StringBuilder();
		if (index == 0) {
			stringBuilder.append("a");
		} else {
			int i;
			while (index > 0) {
				i = index % CHARS.length;
				stringBuilder.append(CHARS[i]);
				index /= CHARS.length;
			}
		}
		String returnName = stringBuilder.reverse().toString();
		String tempName = returnName;
		if (keySet != null) {
			int i = 0;
			while (keySet.contains(tempName)) {
				tempName = returnName;
				tempName += i;
				i++;
			}
		}
		returnName = tempName;
		keySet.add(returnName);
		return returnName;
	}

	public static int index = 0;

	public static String getActivityName(HashSet<String> keySet) {
		String name = null;
		do {
			name = getIndexName();
		} while (keySet.contains(name + ".java"));
		keySet.add(name);
		return name;
	}

	/**
	 * 依据index 获取activityname
	 * 
	 * @return
	 */
	public static String getIndexName() {
		return getIndexName(index++);
	}

	public static String getIndexName(int index) {
		if (index == 0) {
			return "a";
		}
		int i = 0;
		StringBuilder builder = new StringBuilder();
		while (index > 0) {
			i = index % CHARS.length;
			builder.append(CHARS[i]);
			index /= CHARS.length;
		}
		return builder.reverse().toString();
	}

}
