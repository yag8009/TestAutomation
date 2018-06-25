
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
import com.g7s.test.core.GlobalSettings;
import com.g7s.test.core.TestDataHandler;
import com.g7s.test.tasks.LoginTasks;
import com.g7s.test.tasks.PostmanTasks;
import com.g7s.test.tasks.TaskUtils;

/**
 * @Description:移动任务->投递员列表  
 * 
 * @author Dai Qi
 *
 * @date 2014年12月10日
 * 
 */
public class PostmanTest extends BaseTestCase {
	private static BrowserEmulator be ;	
	private static LoginTasks loginTask;
	private static PostmanTasks postmanTask;
	private static String postman_name = "Postman_" + TaskUtils.generateRandomString(5);
	private static String postman_newname = "Postman_" + TaskUtils.generateRandomString(5);
	private static String postman_phone=TaskUtils.generateRandomPhoneNumber();
	
	@BeforeClass
	public void doBeforeClass() {
		be = new BrowserEmulator();	
		loginTask= new LoginTasks();
		postmanTask=new PostmanTasks(be);
	}
	
	@Test(dataProvider = "menu")
	public void enterPostmanMenu(Map<String, String> data) {		
		// login admin
		loginTask.loginHome(be);		
		//选择"投递员列表"菜单
		postmanTask.clickPostmanMenu(data);		
	}
	
	
	@Test(dependsOnMethods = "enterPostmanMenu", dataProvider = "postman")
	public void createPostman(Map<String, String> data) {
		//创建投递员
		postmanTask.setName(postman_name);
		postmanTask.setPhone(postman_phone);
		postmanTask.setAccount(GlobalSettings.getProperty("AccountName"));
		postmanTask.createPostman(data);
	}
	
	@Test(dependsOnMethods = "createPostman", dataProvider = "postman")
	public void searchPostman(Map<String, String> data) {
		//搜索投递员
		postmanTask.searchPostman(data);
		postmanTask.verifyPostmanInList();
	}

	@Test(dependsOnMethods = "searchPostman", dataProvider = "postman")
	public void editPostman(Map<String, String> data) {
		//搜索投递员
		postmanTask.setName(postman_newname);
		postmanTask.editPostman(data);
		postmanTask.searchPostman(data);
		postmanTask.verifyPostmanInList();
	}

	@Test(dependsOnMethods = "editPostman", dataProvider = "postman")
	public void deletePostman(Map<String, String> data) {
		//选择投递员,点击删除		
		postmanTask.deletePostman(data);
		//搜索已删除的投递员
		postmanTask.searchPostman(data);
		
		postmanTask.verifyPostmanNotInList();
		//退出系统
		loginTask.logout(be);	
	}
	
	
	@DataProvider(name = "postman")
	public Iterator<Object[]> dataforZPT_Postman(Method method)
			throws IOException {
		return new TestDataHandler("ZPT", "zpt_postman");
	}

	@DataProvider(name = "menu")
	public Iterator<Object[]> dataforZPT(Method method)
			throws IOException {
		return new TestDataHandler("ZPT", "zpt");
	}
	
	@AfterClass(alwaysRun = true)
	public void doAfterClass() {		
		be.quit();
	}
}
