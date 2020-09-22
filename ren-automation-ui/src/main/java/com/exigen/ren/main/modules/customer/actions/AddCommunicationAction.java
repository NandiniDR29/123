/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.actions;


import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.customer.tabs.CommunicationActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;

public class AddCommunicationAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(CommunicationActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Add Communication";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action start() {
        CustomerSummaryPage.buttonStartNewCommunication.click();
        return this;
    }

    @Override
    public Action submit() {
        CommunicationActionTab.buttonSave.click();
        return this;
    }

    public Action start(int index) {
        CustomerSummaryPage.buttonStartNewCommunication.click();
        CustomerSummaryPage.tableCustomerListAccount.getColumn("Customer Name").getCell(index).click();
        return this;
    }
}