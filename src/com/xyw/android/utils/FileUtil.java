package com.xyw.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

/**
 * @version 0.1
 * @author xyw 
 * 
 * File operation Don't solve the limitation that the source
 * location is the same to destination location.
 */
public class FileUtil {

	public static boolean copyFile(File src, File des) {
		boolean resultCode = false;
		des = new File(des.getAbsoluteFile() + "/" + src.getName());
		try {
			if (!des.exists()) {
				des.createNewFile();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(src);
			fos = new FileOutputStream(des);
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = fis.read(buffer)) > 0) {
				fos.write(buffer, 0, length);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			resultCode = false;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			resultCode = false;
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return resultCode;
	}

	public static boolean copyFolder(File src, File des) {
		boolean resultCode = false;
		File tmp = null;
		File[] files = src.listFiles();
		tmp = new File(des.getAbsoluteFile() + "/" + src.getName());
		if (!tmp.exists()) {
			resultCode = tmp.mkdir();
		}
		if (files.length != 0) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					resultCode = copyFolder(files[i], new File(des.getAbsoluteFile() + "/" + src.getName()));
				} else {
					resultCode = copyFile(files[i], new File(des.getAbsoluteFile() + "/" + src.getName()));
				}
			}
		}
		return resultCode;
	}

	public static boolean deleteFile(File src) {
		return src.delete();
	}

	public static boolean deleteFolder(File src) {
		boolean resultCode = false;
		File[] files = src.listFiles();
		if (files.length == 0) {
			resultCode = src.delete();
		} else {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					resultCode = deleteFolder(files[i]);
				} else {
					resultCode = deleteFile(files[i]);
				}
			}
		}
		return resultCode;
	}

}
