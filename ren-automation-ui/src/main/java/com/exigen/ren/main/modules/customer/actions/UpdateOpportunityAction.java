/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.tabs.OpportunityActionTab;

public class UpdateOpportunityAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(OpportunityActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Update Opportunity";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.getFirst().click();
        NavigationPage.setActionAndGo("Update");
        return this;
    }

    @Override
    public Action submit() {
        OpportunityActionTab.buttonDone.click();
        OpportunityActionTab.buttonDone.click();
        return this;
    }
}
