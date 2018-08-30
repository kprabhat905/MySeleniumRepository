package com.qtpselenium.zoho.project.base;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.qtpselenium.zoho.project.util.ExtentManager;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class BaseTest {
	public WebDriver driver;
	public Properties prop;		
	public ExtentReports rep = ExtentManager.getInstance();
	public ExtentTest test;
	
	boolean gridRun = false;
	
	/***********************FUNCTIONS HELP TO INTERACT WITH UI***************************/
	
	public void init(){
		
		// READS from PROPERTIES FILE =>  prop.getProperty("iedriver_exe")
		
		
		//init the prop file
				if(prop==null){
					prop=new Properties();
					try {
						// fs will point to path of PROPERTIES file
						FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"//src//test//resources//projectconfig.properties");
						prop.load(fs);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	}
	
	public void openBrowser(String browserType){
		test.log(LogStatus.INFO, "Opening Browser "+ browserType);

		if(browserType.equals("Mozilla"))
			driver=new FirefoxDriver();
		else if(browserType.equals("Chrome")){
			//System.setProperty("webdriver.chrome.driver", prop.getProperty("chromedriver_exe"));
			driver=new ChromeDriver();
		}
		else if (browserType.equals("IE")){
			System.setProperty("webdriver.chrome.driver", prop.getProperty("iedriver_exe"));
			driver= new InternetExplorerDriver();
		}
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		
		test.log(LogStatus.INFO, "Browser opened successfully "+ browserType);
		
	}

	public void navigate(String urlKey){
		test.log(LogStatus.INFO, "Navigating to "+ prop.getProperty(urlKey));
		driver.get(prop.getProperty(urlKey));
	}
	
	public void click(String locatorKey){
		test.log(LogStatus.INFO, "Clicking on "+ locatorKey);
		
		getElement(locatorKey).click();
		test.log(LogStatus.INFO, "Clicked successfully on "+ locatorKey);

	}
	
	public void type(String locatorKey,String data){
		test.log(LogStatus.INFO, "Typing in "+ locatorKey+ ". Data - "+ data);
		getElement(locatorKey).sendKeys(data);
		test.log(LogStatus.INFO, "Typed successfully in "+ locatorKey);

	}
	public void type1(String locatorKey,String data){
		test.log(LogStatus.INFO, "Typing in "+ locatorKey+ ". Data - "+ data);
		waitForVisibility(locatorKey);
		getElement(locatorKey).sendKeys(data);
		
		driver.findElement(By.xpath("//li/span[text()='"+data+"']")).click();
		
		test.log(LogStatus.INFO, "Typed successfully in "+ locatorKey);

	}
	
	// finding element and returning it
	public WebElement getElement(String locatorKey){
		WebElement e=null;
		try{
		if(locatorKey.endsWith("_id"))
			e = driver.findElement(By.id(prop.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_name"))
			e = driver.findElement(By.name(prop.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_xpath"))
			e = driver.findElement(By.xpath(prop.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_cssSelector"))
			e = driver.findElement(By.cssSelector(prop.getProperty(locatorKey)));
		else{
			reportFailure("Locator not correct - " + locatorKey);
			Assert.fail("Locator not correct - " + locatorKey);
		}
		
		}catch(Exception ex){
			// fail the test and report the error
			reportFailure(ex.getMessage());
			ex.printStackTrace();
			Assert.fail("Failed the test - " + ex.getMessage());
		}
		return e;
	}
	/***********************Validation FUNCITONS***************************/
	public boolean verifyTitle(){
		return false;		
	}
	
	public boolean isElementPresent(String locatorKey){
		List<WebElement> elementList=null;
		if(locatorKey.endsWith("_id"))
			elementList = driver.findElements(By.id(prop.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_name"))
			elementList = driver.findElements(By.name(prop.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_xpath"))
			elementList = driver.findElements(By.xpath(prop.getProperty(locatorKey)));
		else{
			reportFailure("Locator not correct - " + locatorKey);
			Assert.fail("Locator not correct - " + locatorKey);
		}
		
		if(elementList.size()==0)
			return false;	
		else
			return true;
	}
	
	public boolean verifyText(String locatorKey,String expectedTextKey){
		
		String actualText = getElement(locatorKey).getText().trim();
		String expectedText = prop.getProperty(expectedTextKey);
		
		if(actualText.equals(expectedText))
			return true;
		else
			return false;
	}

	
	public void clickAndWait(String locator_clicked, String locator_press) {
		// TODO Auto-generated method stub
		
		test.log(LogStatus.INFO, "Clicking and waiting on - "+locator_clicked);
		
		int count = 5;
		for(int i=0; i<count; i++) {
			getElement(locator_clicked).click();
			wait(2);
			if(isElementPresent(locator_press))
				break;
		}
		
	}
	
	
	/*****************************Reporting FUNCTIONS*********************/
	
	public void reportPass(String msg){
		test.log(LogStatus.PASS, msg);
	}
	
	public void reportFailure(String msg){
		test.log(LogStatus.FAIL, msg);
		takeScreenShot();
		Assert.fail(msg);		// This will stop the test case (FOR CRITICAL FAILURES)
	}
	
	public void takeScreenShot(){
		
		// fileName of the screenshot
		Date d=new Date();
		String screenshotFile=d.toString().replace(":", "_").replace(" ", "_")+".png";
		// store screenshot in that file
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir")+"//screenshots//"+screenshotFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//put screenshot file in reports
		test.log(LogStatus.INFO,"Screenshot-> "+ test.addScreenCapture(System.getProperty("user.dir")+"//screenshots//"+screenshotFile));
		
	}

	public String getText(String locatorKey){
		test.log(LogStatus.INFO, "Getting text from "+locatorKey);
		return getElement(locatorKey).getText();

	}
	
	/************************App functions*****************************/

	public boolean doLogin(String username,String password) {
		test.log(LogStatus.INFO, "Trying to login with "+ username+","+password);
		click("loginLink_xpath");
		
		//System.out.println("========FRAME========"+driver.findElements(By.tagName("iframe")).size());
		//wait(1);
		
		// wait for page to load
		//waitForPageToLoad();
		
		// switch to frame
		//driver.switchTo().frame(0);
		type("loginid_xpath",username);
		type("password_xpath",password);
		click("signinButton_xpath");
		
		if(isElementPresent("crmlink_xpath")){
			test.log(LogStatus.INFO, "Login Success");
			return true;
		}
		else{
			test.log(LogStatus.INFO, "Login Failed");
			return false;
		}
		
	}
	
	public void waitForPageToLoad() {
		wait(1);
		JavascriptExecutor js=(JavascriptExecutor)driver;
		String state = (String)js.executeScript("return document.readyState");
		
		while(!state.equals("complete")){
			wait(2);
			state = (String)js.executeScript("return document.readyState");
		}
	}
	
	public void wait(int timeToWaitInSec){
		try {
			Thread.sleep(timeToWaitInSec * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void waitForVisibility(String element){
        WebDriverWait wait = new WebDriverWait(driver,5);
        wait.until(ExpectedConditions.visibilityOf(getElement(element)));
	}

	
	public void acceptAlert(){
		//EXPLICITLY WAITING FOR ALERT
		WebDriverWait wait = new WebDriverWait(driver,5);
		wait.until(ExpectedConditions.alertIsPresent());
		test.log(LogStatus.INFO,"Accepting alert");
		driver.switchTo().alert().accept();
		driver.switchTo().defaultContent();
	}
	
	public int getLeadRowNum(String leadName){
		test.log(LogStatus.INFO, "Finding the lead "+leadName);
		List<WebElement> leadNames=driver.findElements(By.xpath(prop.getProperty("leadnamescolumn_xpath")));
		for(int i=0;i<leadNames.size();i++){
			System.out.println(leadNames.get(i).getText());
			
			if(leadNames.get(i).getText().trim().equals(leadName)){
				test.log(LogStatus.INFO, "Lead found in row num "+(i+1));
				return (i+1);
			}
		}
		test.log(LogStatus.INFO, "Lead not found ");
		return -1;
	}
	
	public void clickOnLead(String leadName) {
		// TODO Auto-generated method stub
		test.log(LogStatus.INFO, "Clicking the lead "+leadName);
		int rNum = getLeadRowNum(leadName);
		driver.findElement(By.xpath(prop.getProperty("leadlinkpart1") + rNum + prop.getProperty("leadlinkpart2"))).click();;
		
	}

	public void selectDate(String d){
		test.log(LogStatus.INFO, "Selecting the date "+d);
		// convert the string date(input) in date object
		click("datetextfielddeal_xpath");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date dateTobeSelected = sdf.parse(d);
			Date currentDate = new Date();
			sdf = new SimpleDateFormat("MMMM");
			String monthToBeSelected=sdf.format(dateTobeSelected);
			sdf = new SimpleDateFormat("yyyy");
			String yearToBeSelected=sdf.format(dateTobeSelected);
			sdf = new SimpleDateFormat("d");
			String dayToBeSelected=sdf.format(dateTobeSelected);
			//June 2016
			String monthYearToBeSelected=monthToBeSelected+" "+yearToBeSelected;
			
			while(true){
				if(currentDate.compareTo(dateTobeSelected)==1){
					//back
					click("backdeal_xpath");
				}else if(currentDate.compareTo(dateTobeSelected)==-1){
					//front
					click("forwarddeal_xpath");
				}
				
				if(monthYearToBeSelected.equals(getText("monthyeardisplayed_xpath"))){
					break;
				}
				
				
			}
			driver.findElement(By.xpath("//td[text()='"+dayToBeSelected+"']")).click();
			test.log(LogStatus.INFO, "Date Selection Successful "+d);

			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
