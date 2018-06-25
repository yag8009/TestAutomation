package com.g7s.test.tasks;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import com.g7s.test.core.BrowserEmulator;

public class PostmanTasks {
	private static Logger log = Logger.getLogger(LoginTasks.class);
	private String account = "";
	private String name = "";
	private String phone = "";
	private BrowserEmulator be = null;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public PostmanTasks(BrowserEmulator browser) {
		be = browser;
	}

	public void clickPostmanMenu(Map<String, String> data) {
		log.info("进入' 投递员列表'");
		be.click(data.get("menu_zpt"));
		be.click(data.get("menuitem_postman"));
	}

	public void createPostman(Map<String, String> data) {
		log.info("------新增 投递员:" + name + "------");
		// Click ”新增“
		be.enterFrame(data.get("frame"));
		be.click(data.get("btn_add").trim());
		// be.selectWindow(data.get("window"));
		be.leaveFrame();
		be.enterFrame(data.get("frame"));
		// 填写姓名、机构、手机号
		be.type(data.get("form_name").trim(), name);
		be.type(data.get("form_org").trim(), account);
		be.click(data.get("form_phone").trim());
		be.type(data.get("form_phone").trim(), phone);
		// 点击保存
		be.click(data.get("btn_save"));
		be.leaveFrame();
	}

	public void editPostman(Map<String, String> data) {
		log.info("---修改 投递员:" + name + "---");
		// select the row
		be.click("//table[@id='tblMain']/tbody/tr[1]/td[2]");
		// Click ”修改“
		be.click(data.get("btn_edit").trim());
		be.leaveFrame();
		be.enterFrame(data.get("frame"));
		// 填写姓名
		be.type(data.get("form_name").trim(), name);
		// 点击保存
		be.click(data.get("btn_save"));
		be.leaveFrame();
	}

	public void searchPostman(Map<String, String> data) {
		log.info("------查找 投递员:" + name + "------");
		be.enterFrame(data.get("frame"));
		be.type("//input[@name='postmanname']", name);
		be.click(data.get("btn_search"));
		be.pause(3000);

	}

	public void verifyPostmanNotInList() {
		be.expectElementExistOrNot(true,
				"//table[@id='tblMain']//td[@class='dataTables_empty']", 3000);
		log.info("测试通过——没有找到投递员:" + name);
	}

	public void verifyPostmanInList() {
		List<WebElement> results = be.getBrowserCore().findElements(
				By.xpath("//table[@id='tblMain']/tbody/tr[1]/td"));

		try {
			if (results != null && results.size() > 1) {
				Assert.assertEquals(results.get(2).getText(), name, "名字不匹配");
				Assert.assertEquals(results.get(3).getText(), account, "机构不匹配");
				Assert.assertEquals(results.get(4).getText(), phone, "机构名称不匹配");
				log.info("测试通过——找到投递员:" + name);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		be.handleFailure("查询失败，没有找到投递员:" + name);
	}

	public void deletePostman(Map<String, String> data) {
		log.info("---删除 投递员:" + name + "---");
		be.click("//table[@id='tblMain']/tbody/tr[1]/td[2]");
		be.click(data.get("btn_delete"));
		be.expectElementExistOrNot(true, "//div[@id='MsgBoxBack']", 3000);
		be.click("//button[@id='bot1-Msg1']");
		be.expectTextExistOrNot(true, "删除成功", 3000);
		be.leaveFrame();
	}
}
