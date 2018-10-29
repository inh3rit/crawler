package com.zxsoft.crawler.p.occupancy;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUtil {

	public static String read(String filePath) {
		StringBuffer sb = new StringBuffer();
		try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
			byte[] bytes = new byte[256];
			int byteRead = 0;
			while ((byteRead = file.read(bytes)) != -1) {
				sb.append(new String(bytes, 0, byteRead));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void write(String filePath, String content) {
		try (RandomAccessFile file = new RandomAccessFile(filePath, "rw")) {
			file.seek(file.length());
			file.write(content.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
