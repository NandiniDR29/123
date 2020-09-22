/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

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
import com.exigen.ren.main.modules.customer.tabs.ViewHistoryActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.tableNewProductDetails;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerScheduledUpdateProductsOwned extends CustomerBaseTest implements CustomerContext {
    private TestData tdCustomer = tdCustomerIndividual.getTestData("ScheduleUpdateAction", "TestData");
    private AssetList generalTabAssetList = (AssetList) generalTab.getAssetList();

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "IPBQA-26142", component = CRM_CUSTOMER)
    public void testCustomerScheduledUpdateProductsOwned() {
        mainApp().open();

        LOGGER.info("TEST: Customer Create");
        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData")
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()).resolveLinks());
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        customerIndividual.scheduledUpdate().start();
        scheduledUpdateActionTab.fillTab(tdCustomer);
        scheduledUpdateActionTab.submitTab();

        LOGGER.info("TEST: Products Owned Add");
        GeneralTab.ProductOwned.addNewProductDetails.click();
        generalTabAssetList.getAsset(GeneralTabMetaData.ADD_NEW_PRODUCT_DETAILS).fill(tdSpecific().getTestData("TestData_CondoProduct"));
        GeneralTab.ProductOwned.addAll.click();

        LOGGER.info("TEST: Products Owned Update");
        tableNewProductDetails.getRowContains("Policy Type",
                getPolicyType("TestData_AutoProduct")).getCell("Action").controls.buttons.get("Change").click();
        generalTabAssetList.getAsset(GeneralTabMetaData.ADD_NEW_PRODUCT_DETAILS).fill(tdSpecific().getTestData("TestData_HomeownerProduct"));

        LOGGER.info("TEST: Products Owned Delete");
        tableNewProductDetails.getRowContains("Policy Type",
                getPolicyType("TestData_BenefitsProduct")).getCell("Action").controls.buttons.get("Remove").click();
        Page.dialogConfirmation.confirm();
        Tab.buttonSaveAndExit.click();
        assertThat(CustomerSummaryPage.linkPendingUpdatesPanel).isPresent();


        CustomerSummaryPage.linkPendingUpdatesPanel.click();
        CustomerSummaryPage.linkPendingUpdatesExpand.click();
        CustomerSummaryPage.tablePendingUpdates.getRow(1).getCell(4).controls.links.getFirst().click();
        assertThat(ViewHistoryActionTab.tableIndvProductOwned.getRow(3).getCell(2))
                .hasValue(getPolicyType("TestData_AutoProduct"));


        CustomerSummaryPage.buttonCustomerOverview.click();
        CustomerSummaryPage.linkPendingUpdatesPanel.click();
        CustomerSummaryPage.linkPendingUpdatesCompareAll.click();
        assertThat(GeneralTab.tableTimeLine).hasRows(1);
        assertThat(GeneralTab.tableTimeLine.getRow(1).getCell(1)).hasValue("Pending");

        assertThat(ViewHistoryActionTab.tableIndvProductOwned).isPresent(false);
        viewHistoryActionTab.expandCollapseSection(getCarrierName("TestData_AutoProduct"),"");
        viewHistoryActionTab.expandCollapseSection(getCarrierName("TestData_BenefitsProduct"), "");
        viewHistoryActionTab.expandCollapseSection(
                tdSpecific().getValue("TestData_CondoProduct", GeneralTabMetaData.ADD_NEW_PRODUCT_DETAILS.getLabel(), GeneralTabMetaData.AddNewProductDetailsMetaData.CARRIER_NAME.getLabel()),
                "");
        assertThat(ViewHistoryActionTab.tableIndvProductOwned).isPresent(true);
        Tab.buttonCancel.click();
        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(LocalDate.parse(tdCustomer.getValue(scheduledUpdateActionTab.getMetaKey(),
                ScheduledUpdateActionTabMetaData.UPDATE_EFFECTIVE_DATE.getLabel()), DateTimeUtils.MM_DD_YYYY).atStartOfDay());

        adminApp().open();
        JobRunner.executeJob(GeneralSchedulerPage.PENDING_UPDATE_JOB);

        mainApp().open();
        MainPage.QuickSearch.search(customerNumber);
        assertThat(CustomerSummaryPage.linkPendingUpdatesPanel).isPresent(false);
        customerIndividual.update().start();
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
        customerIndividual.inquiry().start();
        assertThat(GeneralTab.ProductOwned.addNewProductDetails).isDisabled();
    }

    private String getPolicyType(String key) {
        return tdSpecific().getValue(key, GeneralTabMetaData.AddNewProductDetailsMetaData.POLICY_TYPE.getLabel());
    }

    private String getCarrierName(String key) {
        return tdSpecific().getValue(key, GeneralTabMetaData.AddNewProductDetailsMetaData.CARRIER_NAME.getLabel());
    }
}
