package com.g7s.test.tasks;

import org.apache.log4j.Logger;

import com.g7s.test.core.BrowserEmulator;
import com.g7s.test.core.GlobalSettings;

public class LoginTasks {
	private static Logger log = Logger.getLogger(LoginTasks.class);
	private static String url = GlobalSettings.getProperty("LoginURL");
	private static String name = GlobalSettings.getProperty("LoginName");
	private static String passwd = GlobalSettings.getProperty("LoginPWD");
	//private static String account = GlobalSettings.getProperty("AccountName");

	public LoginTasks() {

	}

	public void loginHome(BrowserEmulator browser) {
		log.info ("登陆G7s: '" + name + "' password: '"
				+ passwd + "'");

		browser.open(url);
		browser.type("//input[@id='username']", name);
		browser.type("//input[@id='passwd']", passwd);
		browser.click("//button[@id= 'form_button']");		
		browser.getBrowserCore().manage().window().maximize();
		browser.expectElementExistOrNot(true, "//a[@title='退出系统']", 5000);	
		log.info("登陆成功！");
	}

	public void logout(BrowserEmulator browser) {		
		log.info("退出系统: '" + name + "'");
		browser.leaveFrame();
		browser.click("//a[@title= '退出系统']");
		browser.expectElementExistOrNot(true, "//div[@id='MsgBoxBack']", 5000);
		browser.click("//button[@id='bot2-Msg1']");
	}
}
