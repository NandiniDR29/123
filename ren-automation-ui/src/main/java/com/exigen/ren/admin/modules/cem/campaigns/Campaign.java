/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.campaigns;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.cem.campaigns.actions.*;
import com.exigen.ren.admin.modules.cem.campaigns.pages.CampaignPage;
import com.exigen.ren.admin.modules.cem.campaigns.tabs.CampaignTab;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.enums.NavigationEnum.AdminAppMainTabs;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;

public class Campaign implements ICampaign {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/cem/cem/campaigns");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(CampaignCreateAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
        CampaignTab.buttonSave.click();
    }

    @Override
    public void initiate() {
        navigate();
        CampaignPage.buttonCreateNewCampaign.click();
    }

    public void search(TestData td) {
        CemPage.search(td);
    }

    public void navigate() {
        NavigationPage.toMainTab(AdminAppMainTabs.CEM);
        NavigationPage.toLeftMenuTab(NavigationEnum.CEMLeftMenu.CAMPAIGNS);
    }

    public void startCampaign(int rowNumber) {
        CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
        NavigationPage.setActionAndGo("Start");
        Page.dialogConfirmation.confirm();
    }


    public CampaignUpdateAction updateCampaign() {
        return AutomationContext.getAction(CampaignUpdateAction.class);
    }

    public CampaignEndAction endCampaign() {
        return AutomationContext.getAction(CampaignEndAction.class);
    }

    public CampaignSuspendAction suspendCampaign() {
        return AutomationContext.getAction(CampaignSuspendAction.class);
    }

    public CampaignRestartAction restartCampaign() {
        return AutomationContext.getAction(CampaignRestartAction.class);
    }

    public CampaignCopyAction copyCampaign() {
        return AutomationContext.getAction(CampaignCopyAction.class);
    }

    public CampaignCreateChildCampaignAction createChildCampaign() {
        return AutomationContext.getAction(CampaignCreateChildCampaignAction.class);
    }

    public void archiveCampaign(int rowNumber) {
        CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
        NavigationPage.setActionAndGo("Archive");
        Page.dialogConfirmation.confirm();
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
