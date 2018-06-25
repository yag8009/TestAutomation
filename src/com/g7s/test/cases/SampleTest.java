package com.g7s.test.cases;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.g7s.test.core.BrowserEmulator;
import com.g7s.test.core.TestDataHandler;
import com.g7s.test.tasks.LoginTasks;
import com.g7s.test.tasks.TaskUtils;

public class SampleTest extends BaseTestCase {

	private static BrowserEmulator be;
	private static LoginTasks loginTask= new LoginTasks();;

	@BeforeClass
	public void doBeforeClass() {
		be = new BrowserEmulator();	
	}

	@Test(dataProvider = "menu")
	public void menuTest(Map<String, String> data) {		
		loginTask.loginHome(be);
		//选择投递员列表菜单
		be.click(data.get("menu_zpt"));
		be.click(data.get("menuitem_postman"));
		be.enterFrame(data.get("frame"));
	}

	@Test(dependsOnMethods = "menuTest", dataProvider = "edit")
	public void createPostman(Map<String, String> data) {
		//创建邮递员
		String postman_name = "Post" + TaskUtils.generateRandomString(5);
		//Click ”新增“
		be.click(data.get("btn_edit"));
		be.getBrowser().selectWindow(data.get("window"));
		be.enterFrame(data.get("frame"));
		//填写姓名
		be.type(data.get("form_name"), postman_name);
		//点击取消
		be.click(data.get("btn_cancel"));
		//退出系统
		loginTask.logout(be);
	}

	@DataProvider(name = "edit")
	public Iterator<Object[]> dataFortestMethod2(Method method)
			throws IOException {
		return new TestDataHandler("ZPT", "zpt_postman");
	}

	@DataProvider(name = "menu")
	public Iterator<Object[]> dataFortestMethod(Method method)
			throws IOException {
		return new TestDataHandler("ZPT", "zpt");
	}

	@AfterClass(alwaysRun = true)
	public void doAfterClass() {
		
		be.quit();
	}
}
