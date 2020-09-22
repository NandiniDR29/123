/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.agency.actions;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.SupportTeamTab;
import com.exigen.ren.admin.modules.agencyvendor.pages.AgencyVendorSummaryPage;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.AdminConstants;
import org.openqa.selenium.By;

public class AgencyUpdateAction implements Action {
    @Override
    public String getName() {
        return "Update Agency Action";
    }

    @Override
    public Workspace getWorkspace() {
        return AutomationContext.getAction(AgencyCreateAction.class).getWorkspace();
    }

    public Action start(int rowNumber) {
        AgencyVendorSummaryPage.tableAgencies.getRow(rowNumber).getCell(AdminConstants.AdminAgenciesTable.ACTION).controls.links.getFirst().click();
        return this;
    }

    @Override
    public Action start() {
        throw new UnsupportedOperationException("start() method without parameters is not supported for this action. Use start(int rowNumber) instead.");
    }

    @Override
    public Action submit() {
        new Button(By.xpath("//input[contains(@id,'saveButton_footer')]")).click();
        return this;
    }

    public Action perform(TestData td, int rowNumber) {
        start(rowNumber);
        getWorkspace().fill(td);
        return submit();
    }

    @Override
    public Action perform(TestData td) {
        AgencyVendorSummaryPage.tableAgencies.getRow(1).getCell("Action").controls.links.getFirst().click();
        getWorkspace().fill(td);
        SupportTeamTab.buttonDone.click();
        return this;
    }
}
