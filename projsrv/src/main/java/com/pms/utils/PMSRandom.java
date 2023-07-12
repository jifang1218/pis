package com.pms.utils;

import java.util.Random;

public class PMSRandom {
	public static String randomPassword(int length) {
		String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer strBuf = new StringBuffer();
		int size = charset.length();
		for (int i=0; i<length; ++i) {
			int index = random.nextInt(size);
			strBuf.append(charset.charAt(index));
		}
		
		return strBuf.toString();
	}
}
