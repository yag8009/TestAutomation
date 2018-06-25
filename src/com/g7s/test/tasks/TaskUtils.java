package com.g7s.test.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.testng.Assert;

public class TaskUtils {
	/*
	 * Generates a string of (pseudo) random characters of specified length.
	 * 
	 * @param length
	 * 
	 * @return String of length
	 */
	public static String generateRandomString(int length) {
		if (length < 15) {
			length = 15;
		}

		String seed = "ABCDEFGHIJKLMNOPQRSTUVWXZabcdefghijklmnopqrstuvwxz1234567890";
		StringBuilder sb = new StringBuilder(128);
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append(seed.charAt(random.nextInt(seed.length())));
		}

		return sb.toString();
	}

	public static String generateRandomPhoneNumber() {
		StringBuilder sb = new StringBuilder(12);
		Random random = new Random();
		sb.append("13");
		sb.append(random.nextInt(1000000000));
		return sb.toString();
	}
	
	public static boolean generateImportFile(String filename, String[][] data) {
		WritableWorkbook tempwb = null;
		WritableSheet tempws = null;
		FileOutputStream out = null;
		// 获取要读取的EXCEL表格模板
		File template = new File("resource/data/" + filename+".xls");
		// 新建文件
		File temp = new File("resource/data/", filename+"_temp.xls");
		try {
			// 创建新文件
			temp.createNewFile();
			out = new FileOutputStream(temp);
			// 获取工作簿对象
			Workbook wb = Workbook.getWorkbook(template);
			WorkbookSettings settings = new WorkbookSettings ();  
			settings.setWriteAccess(null);  
		
			// 创建可写入的工作簿对象
			tempwb = Workbook.createWorkbook(out, wb,settings);
			// 根据工作表名获取WritableSheet对象
			tempws = tempwb.getSheet(0);

			Label label = null;
			int k = tempws.getRows();
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < tempws.getRow(0).length; j++) {
					// 创建label对象设置value值j相当于是X轴I是Y轴位置
					label = new Label(j, k, data[i][j]);
					// 添加到工作薄中
					tempws.addCell(label);				
				}
				k++;
			}
			// 将新建立的工作薄写入到磁盘
			tempwb.write();
		} catch (Exception e) {
			e.printStackTrace();
			 Assert.fail("unable to write into Excel");
			 return false;
		} finally {
			// 关闭流
			try {
				tempwb.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}