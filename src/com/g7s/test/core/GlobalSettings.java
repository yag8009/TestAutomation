package com.g7s.test.core;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Global Settings
 */
public class GlobalSettings {

	public static Properties prop = getProperties();

	public static int browserCoreType = Integer.parseInt(prop.getProperty("BrowserCoreType", "2"));

	public static String chromeDriverPath = prop.getProperty("ChromeDriverPath", "resource/chromedriver_for_win.exe");
//	public static String chromeDriverPath = "/Users/chenDoInG/Downloads/chromedriver_for_mac_64";
	public static String ieDriverPath = prop.getProperty("IEDriverPath", "resource/iedriver_32.exe");

	public static String stepInterval = prop.getProperty("StepInterval", "500");

	public static String timeout = prop.getProperty("Timeout", "30000");
	
	public static String baseStorageUrl = prop.getProperty("baseStorageUrl", (new File("screenshot")).getParent());

	public static String getProperty(String property) {
		return prop.getProperty(property);
	}
	
	public static Properties getProperties() {
		Properties prop = new Properties();
		try {
			FileInputStream file = new FileInputStream("resource/global.properties");
			prop.load(file);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}
}