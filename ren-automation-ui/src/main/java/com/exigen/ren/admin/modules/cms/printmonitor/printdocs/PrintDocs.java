/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.printmonitor.printdocs;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.cms.ICMS;
import com.exigen.ren.admin.modules.cms.printmonitor.pages.PrintMonitorPage;
import com.exigen.ren.admin.modules.cms.printmonitor.printdocs.actions.PrintDocsRefreshAction;
import com.exigen.ren.admin.modules.cms.printmonitor.printdocs.tabs.PrintDocsTab;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class PrintDocs implements ICMS {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/cms/printmonitor/printdocs");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(PrintDocsRefreshAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        throw new UnsupportedOperationException("Not supported");
    }

    @Override
    public void initiate() {
        throw new UnsupportedOperationException("Initiate is not implemented in this entity");
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.CMS);
        NavigationPage.toLeftMenuTab(NavigationEnum.CMSLeftMenu.PRINT_MONITOR);
        NavigationPage.toSubTab(NavigationEnum.CMSPrintMonitorSubTab.PRINT_DOCS);
    }

    public void refresh(TestData testData) {
        navigate();
        getDefaultWorkspace().fill(testData);
        refreshResult(10);
    }

    public void cancel(Integer... rowNumber) {
        Arrays.asList(rowNumber).forEach(
                row -> PrintMonitorPage.tableRefreshResult.getRow(row).getCell(1).controls.checkBoxes.getFirst().setValue(true)
        );
        PrintDocsTab.buttonCancelSelected.click();
    }

    public void reschedule(TestData testData, Integer... rowNumber) {
        Arrays.asList(rowNumber).forEach(
                row -> PrintMonitorPage.tableRefreshResult.getRow(row).getCell(1).controls.checkBoxes.getFirst().setValue(true)
        );
        PrintDocsTab.buttonRescheduleSelected.click();
        PrintDocsTab.rescheduleTime.fill(testData);
        PrintDocsTab.buttonConfirmRescheduleDialog.click();
    }

    private void refreshResult(int attempt) {
        PrintDocsTab.buttonRefresh.click();
        RetryService.run(predicate -> PrintMonitorPage.tableRefreshResult.getRowsCount() != 0, () -> {
            PrintDocsTab.buttonRefresh.click();
            return null;
        }, StopStrategies.stopAfterAttempt(attempt), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
