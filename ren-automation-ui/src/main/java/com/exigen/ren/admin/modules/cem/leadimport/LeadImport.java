/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.leadimport;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.admin.modules.cem.leadimport.pages.LeadImportPage;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import java.util.concurrent.TimeUnit;


public class LeadImport implements ILeadImport {

    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/cem/cem/leadimport");

    @Override
    public Workspace getDefaultWorkspace() {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void create(TestData td) {
        initiate();
        LeadImportPage.create(td);
        LeadImportPage.buttonImport.waitFor(BaseElement::isEnabled);
        LeadImportPage.buttonImport.click();
        checkLeadImportStatus();
    }

    @Override
    public void initiate() {
        navigate();
        LeadImportPage.buttonCreateNewImport.click();
    }

    public void search(TestData td) {
        CemPage.search(td);
    }

    public void navigate() {
        NavigationPage.toLeftMenuTab(NavigationEnum.CEMLeftMenu.LEAD_IMPORT);
        if (LeadImportPage.buttonConfirm.isPresent()) {
            LeadImportPage.buttonConfirm.click();
        }
    }

    public void checkLeadImportStatus() {
        RetryService.run(predicate -> LeadImportPage.tableLeadImports.getRow(1).getCell("Status").getValue().contains("Completed"),
                () -> {
                    NavigationPage.toLeftMenuTab(NavigationEnum.CEMLeftMenu.LEAD_IMPORT);
                    return null;
                }, StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
