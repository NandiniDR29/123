/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.caseprofile;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum.AppMainTabs;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.actions.CaseCreateEnrolledCensus;
import com.exigen.ren.main.modules.caseprofile.actions.CaseProfileInquiryAction;
import com.exigen.ren.main.modules.caseprofile.actions.CaseProfileUpdateAction;
import com.exigen.ren.main.modules.caseprofile.actions.GenerateOnDemandDocumentAction;
import com.exigen.ren.main.modules.caseprofile.metadata.ProductAndPlanManagementTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.ProductAndPlanManagementTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import org.openqa.selenium.By;

public class CaseProfile implements ICaseProfile {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/caseprofile");
    public static Button buttonAddNewCaseProfile = new Button(By.id("casesList:addBtn"));
    public static StaticElement labelStatus = new StaticElement(By.id("caseBannerForm:caseList_statusText"));

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(CaseProfileInquiryAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
    }

    public void initiate() {
        NavigationPage.toMainTab(AppMainTabs.CASE);
        buttonAddNewCaseProfile.click();
    }

    public void create(TestData td, GroupBenefitsMasterPolicyType gbType) {
        initiate();
        getDefaultWorkspace().fill(td.adjust(TestData.makeKeyPath(ProductAndPlanManagementTab.class.getSimpleName(),
                ProductAndPlanManagementTabMetaData.PRODUCT.getLabel()), gbType.getName()));
    }

    @Override
    public Action update() {
        return AutomationContext.getAction(CaseProfileUpdateAction.class);
    }

    @Override
    public Action inquiry() {
        return AutomationContext.getAction(CaseProfileInquiryAction.class);
    }

    @Override
    public GenerateOnDemandDocumentAction generateDocument() {
        return AutomationContext.getAction(GenerateOnDemandDocumentAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }

    public CaseCreateEnrolledCensus createEnrolledCensus() {
        return AutomationContext.getAction(CaseCreateEnrolledCensus.class);
    }
}
