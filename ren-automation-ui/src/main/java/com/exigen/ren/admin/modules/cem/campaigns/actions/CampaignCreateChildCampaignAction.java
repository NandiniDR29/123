/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.campaigns.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.admin.modules.cem.campaigns.pages.CampaignPage;
import com.exigen.ren.admin.modules.cem.campaigns.tabs.CreateChildCampaignTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import org.openqa.selenium.By;

public class CampaignCreateChildCampaignAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(CreateChildCampaignTab.class)
            .build();

    @Override
    public String getName() {
        return "Copy";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    public Action start(int rowNumber) {
        CampaignPage.tableCampaigns.getRow(rowNumber).getCell(1).controls.checkBoxes.getFirst().setValue(true);
        Action.super.start();
        new Button(By.id("copyForm:copyAsChildButton")).click();
        return this;
    }

    public Action perform(TestData td, int rowNumber) {
        start(rowNumber);
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
    }

    @Override
    public Action submit() {
        CreateChildCampaignTab.buttonSave.click();
        return this;
    }
}
