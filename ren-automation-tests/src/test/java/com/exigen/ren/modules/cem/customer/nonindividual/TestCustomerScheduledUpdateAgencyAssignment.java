/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.metadata.ScheduledUpdateActionTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.ScheduledUpdateActionTab;
import com.exigen.ren.main.modules.customer.tabs.ViewHistoryActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerScheduledUpdateAgencyAssignment extends CustomerBaseTest implements CustomerContext {
    private TestData tdCustomer = tdCustomerNonIndividual.getTestData("ScheduleUpdateAction", "TestData");
    private AssetList generalTabAssetList = (AssetList) generalTab.getAssetList();

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "IPBQA-26147", component = CRM_CUSTOMER)
    public void testCustomerScheduledUpdateAgencyAssignment() {
        mainApp().open();

        LOGGER.info("TEST: Customer Create");
        customerNonIndividual.createViaUI(tdCustomerNonIndividual.getTestData("DataGather", "TestData")
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()).resolveLinks());
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        scheduledUpdateAndUpdateAgencyAssignment();

        LOGGER.info("TEST: Compare");
        CustomerSummaryPage.linkPendingUpdatesPanel.click();
        CustomerSummaryPage.linkPendingUpdatesExpand.click();
        CustomerSummaryPage.tablePendingUpdates.getRow(1).getCell(4).controls.links.getFirst().click();
        assertThat(ViewHistoryActionTab.tableAssignmentInfoCompare.getRow(4).getCell(2))
                .valueContains(tdSpecific().getValue("QAGAgency", GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER.getLabel(),
                        GeneralTabMetaData.AddAgencyMetaData.AGENCY_NAME.getLabel()));
        CustomerSummaryPage.buttonCustomerOverview.click();

        LOGGER.info("TEST: Compare All");
        CustomerSummaryPage.linkPendingUpdatesPanel.click();
        CustomerSummaryPage.linkPendingUpdatesCompareAll.click();
        assertThat(GeneralTab.tableTimeLine).hasRows(1);
        assertThat(GeneralTab.tableTimeLine.getRow(1, "Pending")).describedAs("Version # is Pending").isPresent();

        assertThat(ViewHistoryActionTab.tableAssignmentInfoCompare).isAbsent();
        viewHistoryActionTab.expandCollapseSection("Agency", "Assignment");
        assertThat(ViewHistoryActionTab.tableAssignmentInfoCompare).isPresent();
        Tab.buttonCancel.click();

        LOGGER.info("TEST: Remove all 'Pending Update'");
        CustomerSummaryPage.linkPendingUpdatesPanel.click();
        CustomerSummaryPage.linkPendingUpdatesRemoveAll.click();
        Page.dialogConfirmation.confirm();
        assertThat(CustomerSummaryPage.linkPendingUpdatesPanel).isAbsent();

        scheduledUpdateAndUpdateAgencyAssignment();

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(LocalDate.parse(tdCustomer.getValue(ScheduledUpdateActionTab.class.getSimpleName(),
                ScheduledUpdateActionTabMetaData.UPDATE_EFFECTIVE_DATE.getLabel()), DateTimeUtils.MM_DD_YYYY).atStartOfDay());

        adminApp().open();
        JobRunner.executeJob(GeneralSchedulerPage.PENDING_UPDATE_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(customerNumber);
        LOGGER.info("TEST: Open Update Mode");
        customerNonIndividual.update().start();
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("TEST: Open Inquiry Mode");
        customerNonIndividual.inquiry().start();
        assertThat(GeneralTab.buttonAddAssignment).isDisabled();
    }

    private void scheduledUpdateAndUpdateAgencyAssignment() {

        customerNonIndividual.scheduledUpdate().start();
        scheduledUpdateActionTab.fillTab(tdCustomer);
        scheduledUpdateActionTab.submitTab();

        LOGGER.info("TEST: Agency Assignment Add");
        GeneralTab.buttonAddAssignment.click();
        generalTabAssetList.getAsset(GeneralTabMetaData.AGENCY_ASSIGNMENT).getAsset(GeneralTabMetaData.AddAgencyMetaData.AGENCY_PRODUCER).fill(tdSpecific().getTestData("TestData_TestAgency"));

        LOGGER.info("TEST: Agency Assignment Update");
        generalTabAssetList.getAsset(GeneralTabMetaData.AGENCY_ASSIGNMENT).fill(tdSpecific().getTestData("TestData_TaskRestAgency"));

        LOGGER.info("TEST: Agency Assignment Delete");
        GeneralTab.AgencyAssignment.removeAgencyAssignment(1);
        Tab.buttonSaveAndExit.click();
        assertThat(CustomerSummaryPage.linkPendingUpdatesPanel).isPresent();
    }

}
