package com.pxr.guard;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;

import com.pxr.guard.constant.Constants;
import com.pxr.guard.utils.FileUtils;
import com.pxr.guard.utils.NameUtils;

/**
 * 记录对应的修改关系，以便修改文件名
 * 
 * @author panxianrong
 *
 */
public class Mapping {

	/**
	 * 文件替换相关
	 */
	public static final String RES = "R"; // res文件夹修改的名字
	public static final String[] PATH_ARR = { "a", "b", "c", "d" }; // TODO 可优化
	public static final int PATH_ARR_LEN = PATH_ARR.length;
	public static final HashMap<String, HashSet<String>> EXCEPT_MAPS = new HashMap<>(); // 记录不混淆的配置
	
	String aa="aa";
	/**
	 * 记录不混淆的
	 * 
	 * @param typePool
	 */
	public static void init(){
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(Constants.EXCEPT_CONFIG));
			String tempK;
			String value;
			for (Object key : prop.keySet()) {
				tempK = (String) key;
				value = (String) prop.get(tempK);
				HashSet<String> set = EXCEPT_MAPS.get(value);
				if (set == null) {
					set = new HashSet<>();
					EXCEPT_MAPS.put(value, set);
				}
				set.add(tempK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Mapping(ArrayList<String> typePool) {
		for (int i = 0; i < PATH_ARR.length; i++) {
			fileNameMaps.put(PATH_ARR[i], new HashSet<>());
		}
		String typeName = null;
		for (int i = 0; i < typePool.size(); i++) {
			typeName = typePool.get(i);
			typeMaps.put(typeName, new HashMap<>());
			typeKeys.put(typeName, new HashSet<>());
		}
	}

	public HashMap<String, String> file_map = new HashMap<>(); // 记录文件替换的map
	public HashMap<String, HashSet<String>> fileNameMaps = new HashMap<>(); // 每个set记录当前文件目录下的文件名(不包括后缀)

	/**
	 * key值替换相关
	 */
	public HashMap<String, HashMap<String, String>> typeMaps = new HashMap<>();// 记录type类型的混淆
	public HashMap<String, HashSet<String>> typeKeys = new HashMap<>();

	public HashSet<String> getNameSet(String str) {
		return fileNameMaps.get(str);
	}

	public String generateNewFileRecord(String oldfilePath) {
		if (oldfilePath != null && !oldfilePath.isEmpty()) {
			if (FileUtils.isResPath(oldfilePath)) {
				if (file_map.containsKey(oldfilePath)) {
					
				} else {
					String parentName = Mapping.PATH_ARR[new Random().nextInt(Mapping.PATH_ARR_LEN)];
					File oldFile = new File(oldfilePath);
					String fileExtends = FileUtils.getFileExtends(oldFile);
					HashSet<String> nameSet = fileNameMaps.get(parentName);
					// 生成替换的地址
					String newPath = new StringBuilder(RES).append("/").append(parentName).append("/")
							.append(NameUtils.getName(nameSet)).append(fileExtends).toString();
					file_map.put(oldfilePath, newPath);
					return newPath;
				}
			} 
		}
		return null;
	}

}
