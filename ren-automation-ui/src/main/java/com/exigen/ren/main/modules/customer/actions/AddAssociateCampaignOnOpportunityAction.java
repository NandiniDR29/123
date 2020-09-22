/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.customer.tabs.OpportunityActionTab;
import org.openqa.selenium.By;

public class AddAssociateCampaignOnOpportunityAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(OpportunityActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Add Associate Campaign On Opportunity";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        OpportunityActionTab.buttonCreateNewOpportunity.click();
        OpportunityActionTab.buttonAddOpportunity.click();
        return this;
    }

    public Action perform(TestData td, String id) {
        start();
        getWorkspace().fill(td);
        new TextBox(By.id("editOpportunity:campaignAssignId")).setValue(id);
        new Button(By.id("editOpportunity:associateById")).click();
        return submit();
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, String id) instead.");
    }

    @Override
    public Action submit() {
        OpportunityActionTab.buttonDone.click();
        OpportunityActionTab.buttonDone.click();
        return this;
    }
}
