/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules;

import com.exigen.ipb.eisa.base.application.ApplicationFactory;
import com.exigen.ipb.eisa.base.application.impl.app.AdminApplication;
import com.exigen.ipb.eisa.base.application.impl.app.MainApplication;
import com.exigen.ipb.eisa.base.application.impl.app.SisenseApplication;
import com.exigen.ipb.eisa.base.application.impl.app.WebStudioApplication;
import com.exigen.ipb.eisa.utils.MarkupParserController;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.TestDataException;
import com.exigen.ren.RenTestProperties;
import com.exigen.ren.TestDataManager;
import com.exigen.ren.Users;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.modules.customer.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;

public class BaseTest {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    protected TestDataManager testDataManager;

    static {
        Users.setGlobalUser();
    }

    public BaseTest() {
        testDataManager = new TestDataManager();
    }

    protected TestData tdSpecific() {
        try {
            return testDataManager.getDefault(this.getClass());
        } catch (TestDataException tde) {
            throw new TestDataException("Specified TestData for test is absent for test class " + this.getClass().getName(), tde);
        }
    }

    protected MainApplication mainApp() {
        return ApplicationFactory.getInstance().getMainApplication();
    }

    protected AdminApplication adminApp() {
        return ApplicationFactory.getInstance().getAdminApplication();
    }

    protected WebStudioApplication webStudioApp() {
        return ApplicationFactory.getInstance().getWebStudioApplication();
    }

    protected SisenseApplication sisenseApp() {
        return ApplicationFactory.getInstance().getSisenseApplication();
    }

    @AfterMethod(alwaysRun = true)
    public void afterTestMethod(ITestResult testResult) {
        Customer.CustomerData.setCustomerNumber(null);
        clearStoredValues();

        if (testResult.getStatus() == ITestResult.FAILURE && PropertyProvider.getProperty(RenTestProperties.LISTENER_ON_FAILURE_SAVE, false)) {
            tryToSaveIfTestFailed(testResult);
        }
        if (PropertyProvider.getProperty(RenTestProperties.LISTENER_ON_FAILURE_CLOSE, true)) {
            mainApp().close();
        }
    }

    protected void addStaticContent(String key, String value) {
        MarkupParserController.StaticContentHolder.getInstance().put(key, value);
    }

    protected String getStoredValue(String key) {
        return MarkupParserController.StaticContentHolder.getInstance().getValue(key);
    }

    protected void clearStoredValues() {
        MarkupParserController.StaticContentHolder.getInstance().clear();
    }

    protected void clearStoredValue(String key) {
        MarkupParserController.StaticContentHolder.getInstance().clear(key);
    }

    private void tryToSaveIfTestFailed(ITestResult testResult) {
        try {
            if (ErrorPage.buttonBack.isPresent()) {
                ErrorPage.buttonBack.click();
            }
            Tab.buttonSaveAndExit.click();
        } catch (Exception e) {
            LOGGER.debug("Unable to save test entities state for test [{}]", testResult.getMethod().getMethodName());
            LOGGER.debug(e.getLocalizedMessage());
        }
    }
}
