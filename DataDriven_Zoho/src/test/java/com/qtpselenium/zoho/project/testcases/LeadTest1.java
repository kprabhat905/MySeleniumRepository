package com.qtpselenium.zoho.project.testcases;

import java.util.Hashtable;

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

public class LeadTest1 extends BaseTest{
	Xls_Reader xls;
	SoftAssert softAssert;

	@Test(priority=1,dataProvider="getData")
	public void createLeadTest(Hashtable<String,String> data){
			
		test = rep.startTest("CreateLeadTest");
		if(!DataUtil.isRunnable("CreateLeadTest", xls) || data.get("Runmode").equals("N")) {
			test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
			throw new SkipException("Skipping the test as runmode is N");
		}
		
		openBrowser(data.get("Browser"));
		navigate("appurl");
		doLogin(prop.getProperty("username"), prop.getProperty("password"));
		click("crmlink_xpath");
		click("leadstab_xpath");
		click("newleadbutton_xpath");
		type("leadcompany_xpath", data.get("LeadCompany"));
		type("leadlastname_xpath", data.get("LeadLastName"));
		click("leadsavebutton_xpath");
		
		// HERE IS NO EFFECT on clicking below tab. We need to apply wait(5)
		//click("leadstab_xpath");
		
		// NOT GOOD OPTION
		//SOLUTION: 	//DELIBERATELY SEARCH ELEMENT i.e. clicked
		clickAndWait("leadstab_xpath", "newleadbutton_xpath");
		
		
		// VALIDATE whether Lead created is present or not
		int rNum = getLeadRowNum(data.get("LeadLastName"));

		if(rNum	== -1)
			reportFailure("Lead not found in lead table "+data.get("LeadLastName"));
		
		reportPass("Lead found in lead table "+data.get("LeadLastName"));
		takeScreenShot();
	}
	
	

	@Test(priority=2, dataProvider="getData")
	public void convertLeadTest(Hashtable<String,String> data){
	
		test = rep.startTest("ConvertLeadTest");
		if(!DataUtil.isRunnable("ConvertLeadTest", xls) || data.get("Runmode").equals("N")) {
			test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
			throw new SkipException("Skipping the test as runmode is N");
		}
		
		openBrowser(data.get("Browser"));
		navigate("appurl");
		doLogin(prop.getProperty("username"), prop.getProperty("password"));
		click("crmlink_xpath");
		click("leadstab_xpath");
		
		// get row number to check the lead
		int rNum = getLeadRowNum(data.get("LeadLastName"));
		clickOnLead(data.get("LeadLastName"));
		
		click("convertlead_xpath");
		click("convertleadandsave_xpath");
		
		click("gobacktolead_xpath");
		
		// VALIDATE : i.e. lead i.e. converted should not be present on Lead page
		rNum = getLeadRowNum(data.get("LeadLastName"));
		if(rNum==-1)
			reportPass(data.get("LeadLastName")+" has been converted");
		else
			reportFailure(data.get("LeadLastName")+" has not been converted");
		
		takeScreenShot(); 
		
	}
	
	

	@Test(priority=3,dataProvider="getDataDeleteLead")
    public void deleteLeadAccountTest(Hashtable<String,String> data) {
          
          test = rep.startTest("DeleteLeadAccountTest");
          test.log(LogStatus.INFO, data.toString());
          if(!DataUtil.isRunnable("DeleteLeadAccountTest", xls) ||  data.get("Runmode").equals("N")){
                 test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
                 throw new SkipException("Skipping the test as runmode is N");
          }
          openBrowser(data.get("Browser"));
          navigate("appurl");
  		
          doLogin(prop.getProperty("username"), prop.getProperty("password"));
  		
          click("crmlink_xpath");
  		
          click("leadstab_xpath");
          
          clickOnLead(data.get("LeadLastName"));
          //waitForPageToLoad();
          click("optionslead_xpath");
          waitForVisibility("deletelead_xpath");
          click("deletelead_xpath");
          waitForVisibility("recyclebinlead_xpath");
          click("recyclebinlead_xpath");
          waitForPageToLoad();
          clickAndWait("leadstab_xpath", "newleadbutton_xpath");
          
          
          int rNum =getLeadRowNum(data.get("LeadLastName"));
          if(rNum==-1)
        	  reportPass(data.get("LeadLastName")+" has been deleted");
          
          else      
        	  reportFailure(data.get("LeadLastName")+" has not been deleted");
          
          	takeScreenShot();
          
    }

	
	@DataProvider(parallel=true)
	public Object[][] getData(){
		super.init();
		xls = new Xls_Reader(prop.getProperty("xlspath"));
		
		return DataUtil.getTestData(xls, "CreateLeadTest");
	}
	
	@DataProvider(parallel=true)
	public Object[][] getDataDeleteLead(){
		super.init();
		xls = new Xls_Reader(prop.getProperty("xlspath"));
		Object[][] data= DataUtil.getTestData(xls, "DeleteLeadAccountTest");
		return data;
		
		
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
		
		if(rep!=null) {				// SOMETIMES, report object don't get initialise and it directly throws NULL POINTER EXCEPTION
			rep.endTest(test);
			rep.flush();
		}
		
		
		// GOOD PRACTICE TO QUIT Browser after testcase is over
		if(driver !=null)
			driver.quit();
	}
}
