package com.pxr.guard.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FileUtils {

	public static String getFileName(File file) {
		String name = file.getName();
		if (name.contains(".")) {
			return name.substring(0, name.indexOf("."));
		}
		return name;
	}

	public static String getApkName(File file) {
		String name = file.getName();
		if (name.contains(".")) {
			return name.substring(0, name.lastIndexOf("."));
		}
		return name;
	}

	public static String getFileExtends(File file) {
		String name = file.getName();
		if (name.contains(".")) {
			return name.substring(name.indexOf("."));
		}
		return "";
	}

	/**
	 * 是否是资源路径
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isResPath(String path) {
		if (path != null && !path.isEmpty()) {
			if (path.startsWith("res/")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除文件及其子文件
	 * 
	 * @param path
	 */
	public static void deleteFiles(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else {
				String[] paths = file.list();
				if (paths != null) {
					for (int i = 0; i < paths.length; i++) {
						String tempPath = paths[i];
						deleteFiles(path + "/" + tempPath);
					}
				}
				file.delete();
			}
		}
	}

	public static File newFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {

		} else {
			File parent = new File(file.getParent());
			parent.mkdirs();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static void copyFile(File srcFile, File destFile) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(srcFile);
			fos = new FileOutputStream(destFile);
			byte[] bytes = new byte[1024];
			int len = -1;
			while ((len = fis.read(bytes)) != -1) {
				fos.write(bytes, 0, len);
			}
			fos.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			IOUtils.closeIO(fis);
			IOUtils.closeIO(fos);
		}
	}

	/**
	 * 从目录copy文件到目录
	 * 
	 * @param srcFileDir
	 * @param destFileDir
	 */
	public static void copyFiles(String srcFileDir, String destFileDir) {
		File srcDir = new File(srcFileDir);
		File destDir = new File(destFileDir, srcDir.getName());
		destDir.mkdirs();
		destFileDir = destDir.getAbsolutePath();
		if (srcDir.isDirectory()) {
			File[] listFiles = srcDir.listFiles();
			for (File file : listFiles) {
				if (file.isDirectory()) {
					copyFiles(file.getAbsolutePath(), destFileDir);
				} else {
					copyFile(file, new File(destFileDir, file.getName()));
				}
			}

		} else {
			copyFile(srcDir, new File(destFileDir, srcDir.getName()));
		}
	}

	/**
	 * 压缩文件夹
	 */
	public static void compressApk(String srcFilePath, String dest) {
		File src = new File(srcFilePath);
		if (!src.exists()) {
			throw new RuntimeException(srcFilePath + "不存在");
		}
		File destApk = FileUtils.newFile(dest);
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new FileOutputStream(destApk));
			File[] files = src.listFiles();
			for (File file : files) {
				compressbyType(file, zos, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeIO(zos);
		}

	}

	private static void compressbyType(File src, ZipOutputStream zos, String baseDir) {
		if (src.isFile()) {
			compressFile(src, zos, baseDir);
		} else if (src.isDirectory()) {
			compressDir(src, zos, baseDir);
		}
	}

	private static void compressFile(File file, ZipOutputStream zos, String baseDir) {
		if (!file.exists())
			return;
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			ZipEntry entry = new ZipEntry(baseDir + file.getName());
			zos.putNextEntry(entry);
			int count;
			byte[] buf = new byte[1024];
			while ((count = bis.read(buf)) != -1) {
				zos.write(buf, 0, count);
			}
			bis.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static void compressDir(File dir, ZipOutputStream zos, String baseDir) {
		if (!dir.exists())
			return;
		File[] files = dir.listFiles();
		if (files.length == 0) {
			try {
				zos.putNextEntry(new ZipEntry(baseDir + dir.getName() + File.separator));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (File file : files) {
			compressbyType(file, zos, baseDir + dir.getName() + File.separator);
		}

	}

	/**
	 * 获取apk中的resource.arsc流，并将其转换成byte数组
	 * 
	 * @param apkPath
	 * @return
	 */
	public static byte[] decodeApkRes(String apkPath) {
		return decodeApk(apkPath, "resources.arsc");
	}

	/**
	 * 获取apk中的资源文件流，并将其转换成byte数组
	 * 
	 * @param apkPath
	 * @return
	 */
	public static byte[] decodeApk(String apkPath, String fileName) {
		ZipFile zFile = null;
		InputStream is = null;
		ByteArrayOutputStream os = null;
		try {
			zFile = new ZipFile(apkPath);
			ZipEntry entry = zFile.getEntry(fileName);
			if (entry == null) {
				return null;
			}
			is = zFile.getInputStream(entry);
			os = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int len = -1;
			while ((len = is.read(bytes)) != -1) {
				os.write(bytes, 0, len);
			}
			os.flush();
			return os.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeIOs(os, is, zFile);
		}
		return null;
	}

	public static ArrayList<String> getApkDexNames(String apkPath) {
		ArrayList<String> dexNames = new ArrayList<>();
		ZipFile zFile = null;
		try {
			zFile = new ZipFile(apkPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (zFile == null) {
			return dexNames;
		}
		Enumeration<? extends ZipEntry> entries = zFile.entries();
		ZipEntry nextEntry = null;
		String entryName = null;
		while (entries.hasMoreElements()) {
			nextEntry = entries.nextElement();
			entryName = nextEntry.getName();
			if (nextEntry.isDirectory()) {
			} else {
				if (entryName.startsWith("classes") && entryName.endsWith(".dex")) {
					dexNames.add(entryName);
				}
			}
		}
		return dexNames;
	}

	/**
	 * 复制apk的文件，并重命名
	 */
	public static void copyApkFilesAndRename(String src, String dest, HashMap<String, String> fileMaps,
			List<String> exceptFiles) {
		ZipFile zFile = null;
		try {
			zFile = new ZipFile(src);
		} catch (IOException e) {
			e.printStackTrace();
			IOUtils.closeIO(zFile);
			return;
		}
		Enumeration<? extends ZipEntry> entries = zFile.entries();
		ZipEntry nextEntry = null;
		while (entries.hasMoreElements()) {
			nextEntry = entries.nextElement();
			String entryName = nextEntry.getName();
			if (nextEntry.isDirectory()) {
				new File(dest + File.separator + entryName).mkdirs();
			} else {
				if (entryName.startsWith("META-INF/") || (exceptFiles != null && exceptFiles.contains(entryName))) {
					continue;
				}
				String newName = fileMaps != null ? fileMaps.get(entryName) : null;
				if (newName != null) {
					entryName = newName;
				}
				// 复制文件到对应的地址
				// FileUtils.copyFile(entryName, );
				InputStream is = null;
				FileOutputStream os = null;
				try {
					is = zFile.getInputStream(nextEntry);
					os = new FileOutputStream(FileUtils.newFile(dest + File.separator + entryName));
					byte[] bytes = new byte[1024];
					int len = -1;
					while ((len = is.read(bytes)) != -1) {
						os.write(bytes, 0, len);
					}
					os.flush();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeIOs(os, is);
				}
			}
		}
		IOUtils.closeIO(zFile);
	}

	
	 public static void upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
	        File desDir = new File(folderPath);
	        if (!desDir.exists()) {
	            desDir.mkdirs();
	        }
	        ZipFile zf = new ZipFile(zipFile);
	        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
	            ZipEntry entry = ((ZipEntry) entries.nextElement());
	            InputStream in = zf.getInputStream(entry);
	            String str = folderPath + File.separator + entry.getName();
	            str = new String(str.getBytes("8859_1"), "GB2312");
	            File desFile = new File(str);
	            if (!desFile.exists()) {
	                File fileParentDir = desFile.getParentFile();
	                if (!fileParentDir.exists()) {
	                    fileParentDir.mkdirs();
	                }
	                desFile.createNewFile();
	            }
	            OutputStream out = new FileOutputStream(desFile);
	            byte buffer[] = new byte[1024 * 1024];
	            int realLength;
	            while ((realLength = in.read(buffer)) > 0) {
	                out.write(buffer, 0, realLength);
	            }
	            in.close();
	            out.close();
	        }
	        zf.close();
	    }
}
