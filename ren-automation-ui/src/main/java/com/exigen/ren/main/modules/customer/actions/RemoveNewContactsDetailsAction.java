/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.AbstractAction;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.tabs.BusinessEntityTab;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;

public class RemoveNewContactsDetailsAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(GeneralTab.class)
            .registerTab(BusinessEntityTab.class)
            .registerTab(RelationshipTab.class)
            .build();

    @Override
    public String getName() {
        return "Remove New Contacts Details";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    public Action start(int rowNumber) {
        CustomerSummaryPage.buttonAddNewContactsDetails.click();
        CustomerSummaryPage.tableCustomerContacts.getRow(rowNumber).getCell(CustomerConstants.CustomerContactsTable.ACTION.getName()).controls.buttons.get("Remove").click();
        Page.dialogConfirmation.confirm();
        GeneralTab.buttonNext.click();
        BusinessEntityTab.buttonNext.click();
        return this;
    }

    public Action perform(int rowNumber) {
        start(rowNumber);
        return submit();
    }

    @Override
    public AbstractAction start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    @Override
    public AbstractAction perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(int rowNumber) instead.");
    }

    @Override
    public Action submit() {
        RelationshipTab.buttonDone.click();
        return this;
    }

}
