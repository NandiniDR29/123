/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.vendor.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.agencyvendor.vendor.pages.VendorPage;
import com.exigen.ren.admin.modules.agencyvendor.vendor.tabs.ContactInfoTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.AdminConstants;

public class VendorUpdateAction implements Action {
    @Override
    public String getName() {
        return "Update Vendor Action";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(VendorCreateAction.class).getWorkspace();
    }

    public Action start(int rowNumber) {
        VendorPage.tableVendorProfile.getRow(rowNumber).getCell(AdminConstants.AdminVendorProfileTable.ACTION).controls.links.getFirst().click();
        return this;
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    @Override
    public Action submit() {
        ContactInfoTab.buttonOk.click();
        return this;
    }

    public Action perform(TestData td, int rowNumber) {
        start(rowNumber);
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action perform(TestData td) {
        throw new UnsupportedOperationException("perform(TestData td) method with testData is not supported for this action. Use perform(TestData td, int rowNumber) instead.");
    }
}
