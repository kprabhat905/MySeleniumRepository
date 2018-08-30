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

// PROBLEM - when createLeadTest fails convert and delete lead test also fails
// Hence, we don't need the dependency among these test cases. 
// SOLUTION LeadTest1.java

public class LeadTest extends BaseTest{
	Xls_Reader xls;
	SoftAssert softAssert;
	
	@Test(priority=1, dataProvider="getData")
	public void createLeadTest(Hashtable<String, String> data){
		test = rep.startTest("CreateLeadTest");
		if(!DataUtil.isRunnable("CreateLeadTest", xls) || data.get("Runmode").equals("N")) {
			test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
			throw new SkipException("Skipping the test as runmode is N");
		}
//		init();
//
//		openBrowser("Chrome");
//		navigate("appurl");
//		
//		doLogin(prop.getProperty("username"), prop.getProperty("password"));
//
//		
//		click("crmlink_xpath");
//		click("leadstab_xpath");
//		click("newleadbutton_xpath");
//		type("leadcompany_xpath", "Samsung");
//		type("leadlastname_xpath", "Wilson");
//		
//		click("leadsavebutton_xpath");
//		
//		click("leadstab_xpath");
		
		// validate
			
	}
	
	@Test(priority=2, dependsOnMethods= {"createLeadTest"})
	public void convertLeadTest() {
		//Assert.fail();
	}
	
	@Test(priority=2, dependsOnMethods= {"createLeadTest","convertLeadTest"}) 
	public void deleteLeadTest() {
		
	}
	
	@DataProvider
	public Object[][] getData(){
		super.init();
		xls = new Xls_Reader(prop.getProperty("xlspath"));
		
		return DataUtil.getTestData(xls, "CreateLeadTest");
	}
	
	
}
