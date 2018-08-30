package com.qtpselenium.zoho.project.testcases;

import java.util.Hashtable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.qtpselenium.zoho.project.base.BaseTest;
import com.qtpselenium.zoho.project.util.DataUtil;
import com.qtpselenium.zoho.project.util.Xls_Reader;
import com.relevantcodes.extentreports.LogStatus;

public class DealTest extends BaseTest{
    String testCaseName="CreateDealTest";
    Xls_Reader xls;
    SoftAssert softAssert ;
    
    @Test(priority=1,dataProvider="getData")
    public void createDealTest(Hashtable<String,String> data){
          test = rep.startTest(testCaseName);
          test.log(LogStatus.INFO, data.toString());
          if(!DataUtil.isRunnable(testCaseName, xls) || data.get("Runmode").equals("N")) {
                 test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
                 throw new SkipException("Skipping the test as runmode is N");
          }
          openBrowser(data.get("Browser"));
          navigate("appurl");
          doLogin(prop.getProperty("username"),prop.getProperty("password"));
          click("crmlink_xpath");
  			
          click("deallink_xpath");
          click("createdeal_xpath");
          type("dealname_xpath",data.get("DealName"));
          type("accountnamedeal_xpath",data.get("AccountName"));
          
          click("stagedeal_xpath");
          type1("stagedealinput_xpath",data.get("Stage"));
          
          selectDate(data.get("ClosingDate"));
         click("savedealbutton_xpath");
  		
         //validate - you
  			
  			if(isElementPresent("dealname_xpath"))
            {
               reportPass(data.get("DealName")+" has been added");
            }
            else
               reportFailure(data.get("DealName")+" has not been added");
            
  			waitForPageToLoad();
            takeScreenShot();
        

  		reportPass("Test Passed");
          
    }
    
    @Test(priority=2,dataProvider="getData",dependsOnMethods= {"createDealTest"})
    public void deleteDealAccountTest(Hashtable<String,String> data) {
    	test = rep.startTest("DeleteDealTest");
        test.log(LogStatus.INFO, data.toString());
        if(!DataUtil.isRunnable("DeleteDealTest", xls) || data.get("Runmode").equals("N")) {
               test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
               throw new SkipException("Skipping the test as runmode is N");
        }
        openBrowser(data.get("Browser"));
        navigate("appurl");
        doLogin(prop.getProperty("username"),prop.getProperty("password"));
        click("crmlink_xpath");
			
        click("deallink_xpath");
        
        Actions act = new Actions(driver);
        act.moveToElement(getElement("dealcheckbox_xpath")).build().perform();
        waitForVisibility("dealcheckbox_xpath");
        click("dealcheckbox_xpath");
        waitForVisibility("optionsdeletedeal_xpath");
        click("optionsdeletedeal_xpath");
        waitForVisibility("deletedeal_xpath");
        click("deletedeal_xpath");
        waitForVisibility("recyclebindeal_xpath");
        click("recyclebindeal_xpath");
        //VALIDATE
        if(!isElementPresent("dealname_xpath"))
        	reportPass(data.get("DealName")+" has been deleted");
        else
        	reportFailure(data.get("DealName")+" has not been added");
      
      takeScreenShot();

    }
    
    @DataProvider(parallel=true)
    public Object[][] getData(){
          super.init();
         xls = new Xls_Reader(prop.getProperty("xlspath"));
          return DataUtil.getTestData(xls,testCaseName);
    }
    
    @BeforeMethod
    public void init() {
          
          softAssert = new SoftAssert();
          
          
    }
    
    @AfterMethod
    public void quit() {
          try {
                 softAssert.assertAll();
          }catch(Error e) {
                 test.log(LogStatus.FAIL, e.getMessage());
          }
          if(rep!=null) {
          rep.endTest(test);
          rep.flush();
          }
          if(driver!=null) {
               driver.quit();
          }
    }
    
    
}


