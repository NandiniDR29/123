/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.reports.operationalreports;

import com.exigen.ipb.eisa.base.application.ApplicationFactory;
import com.exigen.ipb.eisa.base.application.impl.pages.LoginPage;
import com.exigen.ipb.eisa.base.config.CustomTestProperties;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.reports.operationalreports.action.OperationalReportCreateAction;
import com.exigen.ren.admin.modules.reports.operationalreports.tabs.OperationalReportsTab;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import org.openqa.selenium.By;

public class OperationalReport implements IOperationalReport {

    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/reports/operationalreports");

    public void navigate() {
        if (!NavigationPage.isMainTabSelected(NavigationEnum.AdminAppMainTabs.REPORTS)) {
            NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.REPORTS);
            loginToReports();
        }
        NavigationPage.toSubTab(NavigationEnum.ReportsTab.OPERATIONAL_REPORTS.get());
        loginToReports();
    }

    @Override
    public void initiate(TestData td) {
        navigate();
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(OperationalReportCreateAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        baseCreate(td);
        new Table(By.xpath("//table[@id='workbook']")).waitForAccessible(50000);
    }

    public void create(TestData td, boolean isReportExpected) {
        if (isReportExpected) {
            create(td);
        } else {
            baseCreate(td);
        }
    }

    @Override
    public void schedule(TestData td) {
        initiate(td);
        getDefaultWorkspace().fill(td);

        new Button(By.id("jobsForm:schedule")).click();
    }


    private void loginToReports() {
        if (!Tab.labelLoggedUser.isPresent()) {
            if (PropertyProvider.getProperty(CustomTestProperties.OR_URL_PATH).isEmpty()) {
                LoginPage.textBoxLogin.setValue(PropertyProvider.getProperty(TestProperties.APP_USER));
                LoginPage.textBoxPassword.setValue(PropertyProvider.getProperty(TestProperties.APP_PASSWORD));
                LoginPage.buttonLogin.click();
            } else {
                ApplicationFactory.getInstance().getOperationalReportsApplication().getLogin().login();
            }
        }
    }

    private void baseCreate(TestData testData) {
        initiate(testData);
        getDefaultWorkspace().fill(testData);
        OperationalReportsTab.buttonReport.click();
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
