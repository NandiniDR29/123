/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.reports.templates;

import com.exigen.ipb.eisa.base.application.ApplicationFactory;
import com.exigen.ipb.eisa.base.application.impl.pages.LoginPage;
import com.exigen.ipb.eisa.base.config.CustomTestProperties;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.reports.templates.pages.TemplatesSummaryPage;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import org.openqa.selenium.By;

public class Template implements ITemplate {

    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/reports/templates");

    @Override
    public Workspace getDefaultWorkspace() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void create(TestData td) {
        throw new UnsupportedOperationException("Not supported");
    }

    public void navigate() {
        if (!NavigationPage.isMainTabSelected(NavigationEnum.AdminAppMainTabs.REPORTS)) {
            NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.REPORTS);
            NavigationPage.toSubTab(NavigationEnum.ReportsTab.OPERATIONAL_REPORTS.get());
            loginToReports();
        }
        NavigationPage.toSubTab(NavigationEnum.ReportsTab.TEMPLATES.get());
    }

    @Override
    public void validate() {
        navigate();
        new Button(By.id("actions:validateBtn")).click();
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

    public void importTemplate(TestData testData) {
        TemplatesSummaryPage.buttonImport.click();
        TemplatesSummaryPage.dialogImportTemplate.fill(testData);
        new Button(By.id("importTemplateForm:importTemplateOk")).click();
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
