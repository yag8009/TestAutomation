package com.g7s.test.core;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.WebDriverBackedSelenium;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.Reporter;

import com.thoughtworks.selenium.Wait;

/**
 * BrowserEmulator is based on Selenium2 and adds some enhancements
 * 浏览器模拟器是基于selenium2和增加了一些增强功能
 */
public class BrowserEmulator {

	RemoteWebDriver browserCore;
	WebDriverBackedSelenium browser;
	ChromeDriverService chromeServer;
	JavascriptExecutor javaScriptExecutor;
	
	int stepInterval = Integer.parseInt(GlobalSettings.stepInterval);
	int timeout = Integer.parseInt(GlobalSettings.timeout);
	
	private static Logger logger = Logger.getLogger(BrowserEmulator.class.getName());

	public BrowserEmulator() {
		setupBrowserCoreType(GlobalSettings.browserCoreType);
		browser = new WebDriverBackedSelenium(browserCore, "http://g7s.huoyunren.com/");
		javaScriptExecutor = (JavascriptExecutor) browserCore;
		logger.info("Started BrowserEmulator");
	}

	private void setupBrowserCoreType(int type) {
		if (type == 1) {
			browserCore = new FirefoxDriver();
			logger.info("Using Firefox");
			return;
		}
		if (type == 2) {
			chromeServer = new ChromeDriverService.Builder().usingDriverExecutable(new File(GlobalSettings.chromeDriverPath)).usingAnyFreePort().build();
			try {
				chromeServer.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized"));
			browserCore = new RemoteWebDriver(chromeServer.getUrl(), capabilities);
			logger.info("Using Chrome");
			return;
		}
		if (type == 3) {
			System.setProperty("webdriver.ie.driver", GlobalSettings.ieDriverPath);
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			browserCore = new InternetExplorerDriver(capabilities);
			logger.info("Using IE");
			return;
		}
		if (type == 4) {
			browserCore = new SafariDriver();
			logger.info("Using Safari");
			return;
		}

		Assert.fail("Incorrect browser type");
	}
	
	/**
	 * Get the WebDriver instance embedded in BrowserEmulator
	 * @return a WebDriver instance
	 *  WebDriver实例嵌入浏览器模拟器
	 *  @返回WebDriver的实例
	 */
	public RemoteWebDriver getBrowserCore() {
		return browserCore;
	}

	/**
	 * Get the WebDriverBackedSelenium instance embedded in BrowserEmulator
	 * @return a WebDriverBackedSelenium instance
	 * 把WebDriver支持Selenium嵌入浏览器模拟器实例
	 * @回复支持WebDriverSelenium实例
	 */
	public WebDriverBackedSelenium getBrowser() {
		return browser;
	}
	
	/**
	 * Get the JavascriptExecutor instance embedded in BrowserEmulator
	 * @return a JavascriptExecutor instance
	 */
	public JavascriptExecutor getJavaScriptExecutor() {
		return javaScriptExecutor;
	}

	/**
	 * Open the URL	打开URL
	 * @param url	@param URL
	 *            the target URL	目标URL
	 */
	public void open(String url) {
		pause(stepInterval);
		try {
			browser.open(url);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to open url " + url);
		}
		logger.info("Opened url " + url);
	}

	/**
	 * Quit the browser	退出浏览器
	 */
	public void quit() {
		pause(stepInterval);
		browserCore.quit();
		if (GlobalSettings.browserCoreType == 2) {
			chromeServer.stop();
		}
		logger.info("Quitted BrowserEmulator");
	}

	/**
	 * Click the page element 点击页面元素
	 * @param xpath	@param XPath
	 *            the element's xpath	元素的XPath
	 */
	public void click(String xpath) {
		pause(stepInterval);
		expectElementExistOrNot(true, xpath, timeout);
		try {
			clickTheClickable(xpath, System.currentTimeMillis(), 2500);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to click " + xpath);
		}
		logger.info("Clicked " + xpath);
	}

	/**
	 * Click an element until it's clickable or timeout 单击一个元素，直到它的点击或超时
	 * @param xpath
	 * @param startTime	@param开始时间
	 * @param timeout in millisecond	@param超时毫秒
	 * @throws Exception	@抛出异常
	 */
	private void clickTheClickable(String xpath, long startTime, int timeout) throws Exception {
		try {
			browserCore.findElementByXPath(xpath).click();
		} catch (Exception e) {
			if (System.currentTimeMillis() - startTime > timeout) {
				logger.info("Element " + xpath + " is unclickable");
				throw new Exception(e);
			} else {
				Thread.sleep(500);
				logger.info("Element " + xpath + " is unclickable, try again");
				clickTheClickable(xpath, startTime, timeout);
			}
		}
	}

	/**
	 * Type text at the page element<br>	在页面元素<br>类型文本
	 * Before typing, try to clear existed text	在打字，试图明确存在的文本
	 * @param xpath
	 *            the element's xpath	元素的XPath
	 * @param text
	 *            the input text	输入文本
	 */
	public void type(String xpath, String text) {
		pause(stepInterval);
		expectElementExistOrNot(true, xpath, timeout);

		WebElement we = browserCore.findElement(By.xpath(xpath));
		try {
			we.clear();
		} catch (Exception e) {
			logger.warn("Failed to clear text at " + xpath);
		}
		try {
			we.sendKeys(text);
		} catch (Exception e) {
			e.printStackTrace();
			handleFailure("Failed to type " + text + " at " + xpath);
		}

		logger.info("Type " + text + " at " + xpath);
	}

	/**
	 * Hover on the page element	悬停在页面元素
	 * 
	 * @param xpath
	 *            the element's xpath	元素的XPath
	 */
	public void mouseOver(String xpath) {
		pause(stepInterval);
		expectElementExistOrNot(true, xpath, timeout);
		// First make mouse out of browser 先出浏览器使鼠标
		Robot rb = null;
		try {
			rb = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		rb.mouseMove(0, 0);

		// Then hover 然后盘旋
		WebElement we = browserCore.findElement(By.xpath(xpath));

		if (GlobalSettings.browserCoreType == 2) {
			try {
				Actions builder = new Actions(browserCore);
				builder.moveToElement(we).build().perform();
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("Failed to mouseover " + xpath);
			}

			logger.info("Mouseover " + xpath);
			return;
		}

		// Firefox and IE require multiple cycles, more than twice, to cause a
		// hovering effect Firefox和IE需要多个时钟周期，两次以上，造成盘旋的效果
		if (GlobalSettings.browserCoreType == 1
				|| GlobalSettings.browserCoreType == 3) {
			for (int i = 0; i < 5; i++) {
				Actions builder = new Actions(browserCore);
				builder.moveToElement(we).build().perform();
			}
			logger.info("Mouseover " + xpath);
			return;
		}

		// Selenium doesn't support the Safari browser 不支持Safari浏览器
		if (GlobalSettings.browserCoreType == 4) {
			Assert.fail("Mouseover is not supported for Safari now");
		}
		Assert.fail("Incorrect browser type");
	}

	/**
	 * Switch window/tab	开关窗口/标签
	 * @param windowTitle	@param文档的浏览器窗口标题
	 *            the window/tab's title	窗口/标签的标题
	 */
	public void selectWindow(String windowTitle) {
		pause(stepInterval);
		browser.selectWindow(windowTitle);
		logger.info("Switched to window " + windowTitle);
	}

	/**
	 * Enter the iframe	进入iframe
	 * @param xpath
	 *            the iframe's xpath
	 */
	public void enterFrame(String xpath) {
		pause(stepInterval);
		browserCore.switchTo().frame(browserCore.findElementByXPath(xpath));
		logger.info("Entered iframe " + xpath);
	}

	/**
	 * Leave the iframe	离开iframe
	 */
	public void leaveFrame() {
		pause(stepInterval);
		browserCore.switchTo().defaultContent();
		logger.info("Left the iframe");
	}
	
	/**
	 * Refresh the browser 刷新浏览器
	 */
	public void refresh() {
		pause(stepInterval);
		browserCore.navigate().refresh();
		logger.info("Refreshed");
	}
	
	/**
	 * Mimic system-level keyboard event 系统级模拟键盘事件
	 * @param keyCode @参数
	 *            such as KeyEvent.VK_TAB, KeyEvent.VK_F11
	 */
	public void pressKeyboard(int keyCode) {
		pause(stepInterval);
		Robot rb = null;
		try {
			rb = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		rb.keyPress(keyCode);	// press key 按键
		rb.delay(100); 			// delay 100ms 延迟100ms
		rb.keyRelease(keyCode);	// release key 释放键
		logger.info("Pressed key with code " + keyCode);
	}

	/**
	 * Mimic system-level keyboard event with String 模拟系统级键盘事件与字符串
	 * 
	 * @param text
	 * 
	 */
	public void inputKeyboard(String text) {
		String cmd = System.getProperty("user.dir") + "\\res\\SeleniumCommand.exe" + " sendKeys " + text;

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			p.destroy();
		}
		logger.info("Pressed key with string " + text);
	}
	
	//TODO Mimic system-level mouse event

	/**
	 * Expect some text exist or not on the page<br> 期待一些文本存在或不在页<BR>
	 * Expect text exist, but not found after timeout => Assert fail<br> 期望的文本存在，但未发现超时后= >断言失败<BR>
	 * Expect text not exist, but found after timeout => Assert fail 期望文本不存在，但发现后超时= >断言失败
	 * @param expectExist
	 *            true or false
	 * @param text
	 *            the expected text 预期的文本
	 * @param timeout
	 *            timeout in millisecond 超时毫秒
	 */
	public void expectTextExistOrNot(boolean expectExist, final String text, int timeout) {
		if (expectExist) {
			try {
				new Wait() {
					public boolean until() {
						return isTextPresent(text, -1);
					}
				}.wait("Failed to find text " + text, timeout);
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("Failed to find text " + text);
			}
			logger.info("Found desired text " + text);
		} else {
			if (isTextPresent(text, timeout)) {
				handleFailure("Found undesired text " + text);
			} else {
				logger.info("Not found undesired text " + text);
			}
		}
	}

	/**
	 * Expect an element exist or not on the page<br>
	 * Expect element exist, but not found after timeout => Assert fail<br>
	 * Expect element not exist, but found after timeout => Assert fail<br>
	 * Here <b>exist</b> means <b>visible</b>
	 * @param expectExist
	 *            true or false
	 * @param xpath
	 *            the expected element's xpath 预期元素的XPath
	 * @param timeout
	 *            timeout in millisecond
	 */
	public void expectElementExistOrNot(boolean expectExist, final String xpath, int timeout) {
		if (expectExist) {
			try {
				new Wait() {
					public boolean until() {
						return isElementPresent(xpath, -1);
					}
				}.wait("Failed to find element " + xpath, timeout);
			} catch (Exception e) {
				e.printStackTrace();
				handleFailure("Failed to find element " + xpath);
			}
			logger.info("Found desired element " + xpath);
		} else {
			if (isElementPresent(xpath, timeout)) {
				handleFailure("Found undesired element " + xpath);
			} else {
				logger.info("Not found undesired element " + xpath);
			}
		}
	}

	/**
	 * Is the text present on the page 本页面上的文本
	 * @param text
	 *            the expected text 预期的文本
	 * @param time           
	 *            wait a moment (in millisecond) before search text on page;<br> 等等（毫秒）在网页搜索文本；<BR>
	 *            minus time means search text at once 减时间意味着一次搜索文本
	 * @return
	 */
	public boolean isTextPresent(String text, int time) {
		pause(time);
		boolean isPresent = browser.isTextPresent(text);
		if (isPresent) {
			logger.info("Found text " + text);
			return true;
		} else {
			logger.info("Not found text " + text);
			return false;
		}
	}

	/**
	 * Is the element present on the page<br> 出现在页<BR>元素
	 * Here <b>present</b> means <b>visible</b> 这里<b>目前</b>手段<b>可见</b>
	 * @param xpath
	 *            the expected element's xpath 预期元素的XPath
	 * @param time           
	 *            wait a moment (in millisecond) before search element on page;<br> 等等（毫秒）在网页搜索元素之前；<BR>
	 *            minus time means search element at once 减时间意味着搜索元一次
	 * @return
	 */
	public boolean isElementPresent(String xpath, int time) {
		pause(time);
		boolean isPresent = browser.isElementPresent(xpath) && browserCore.findElementByXPath(xpath).isDisplayed();
		if (isPresent) {
			logger.info("Found element " + xpath);
			return true;
		} else {
			logger.info("Not found element" + xpath);
			return false;
		}
	}
	
	/**
	 * Pause 暂停
	 * @param time in millisecond @param时间在毫秒
	 */
	public void pause(int time) {
		if (time <= 0) {
			return;
		}
		try {
			Thread.sleep(time);
			logger.info("Pause " + time + " ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void handleFailure(String notice) {
		String png = LogTools.screenShot(this);
		String log = notice + " >> capture screenshot at " + png;
		logger.error(log);
		if (GlobalSettings.baseStorageUrl.lastIndexOf("/") == GlobalSettings.baseStorageUrl.length()) {
			GlobalSettings.baseStorageUrl = GlobalSettings.baseStorageUrl.substring(0, GlobalSettings.baseStorageUrl.length() - 1);
		}
		Reporter.log(log + "<br/><img src=\"" + GlobalSettings.baseStorageUrl + "/" + png + "\" />");
		Assert.fail(log);
	}
	
	/**
	 * Return text from specified web element. 返回指定网页元素文本
	 * @param xpath
	 * @return
	 */
	public String getText(String xpath) {
		WebElement element = this.getBrowserCore().findElement(By.xpath(xpath)); 
		return element.getText();
	}
	
	/**
	 * Select an option by visible text from &lt;select&gt; web element. 选择<选择>网页元素的可见文本选项。
	 * @param xpath
	 * @param option
	 */
	public void select(String xpath, String option) {
		WebElement element = this.browserCore.findElement(By.xpath(xpath));
		Select select = new Select(element);
		select.selectByVisibleText(option);
	}
}
