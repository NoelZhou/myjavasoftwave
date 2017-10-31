package com.zt.common;

import java.io.File;
import java.io.IOException;

public class FileDiretory {
	public static String getCurrentDir(){
		File directory = new File("");//参数为空 
		String courseFile="";
		try {
			courseFile = directory.getCanonicalPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return courseFile;
	}
}
