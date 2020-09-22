/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.account.tabs.ManageInvoicingCalendarsActionTab;
import com.exigen.ren.main.modules.billing.account.tabs.UpdateBillingAccountActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER_SELF_ADMIN;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.INVOICING_CALENDAR;
import static com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData.GEOGRAPHY;
import static com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData.GeographyMetaData.*;
import static com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData.PRODUCTS;
import static com.exigen.ren.main.modules.billing.account.tabs.ManageInvoicingCalendarsActionTab.InvoicingCalendars.CALENDAR_NAME;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInvoicingCalendarRuntime extends GroupBenefitsBillingBaseTest implements BillingAccountContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15722", component = BILLING_GROUPBENEFITS)
    public void testInvoicingCalendarRuntime() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType(), termLifeInsuranceMasterPolicy.getType());

        billingAccount.navigateToBillingAccount();
        billingAccount.addManageInvoicingCalendars().perform(billingAccount.getDefaultTestData("CreateCalendars", "TestDataSelfAdmin"));

        LOGGER.info("---=={Step 1}==---");
        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GroupAccidentMasterPolicyContext.policyInformationTab.getClass().getSimpleName(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "GA")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestData_WithoutBillingAccount")));

        LOGGER.info("---=={Step 2-4}==---");
        TestData td = termLifeInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, DEFAULT_TEST_DATA_KEY)
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA"));
        termLifeInsuranceMasterPolicy.createPolicy(td);

        LOGGER.info("---=={Step 6}==---");
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        navigateToBillingAccount(masterPolicyNumber);

        LOGGER.info("---=={Step 7}==---");
        List<TestData> tdList = billingAccount.getDefaultTestData("CreateCalendars", "TestDataSelfAdmin").getTestDataList(
                ManageInvoicingCalendarsActionTab.class.getSimpleName());
        String calendarNameIC1 = tdList.get(0).getValue(ManageInvoicingCalendarsActionTabMetaData.CALENDAR_NAME.getLabel());
        String calendarNameIC2 = tdList.get(1).getValue(ManageInvoicingCalendarsActionTabMetaData.CALENDAR_NAME.getLabel());
        billingAccount.updateBillingAccount().start();
        Tab updateBillingAccountActionTab = billingAccount.updateBillingAccount().getWorkspace().getTab(UpdateBillingAccountActionTab.class);
        assertThat(updateBillingAccountActionTab.getAssetList().getAsset(INVOICING_CALENDAR)).containsAllOptions(ImmutableList.of("", "Default Invoicing Calendar1", calendarNameIC1, calendarNameIC2));

        LOGGER.info("---=={Step 8}==---");
        BillingAccountTab.linkManageInvocingCalendars.click();
        ManageInvoicingCalendarsActionTab.tableInvoicingCalendars.getRow(ImmutableMap.of(CALENDAR_NAME.getName(), calendarNameIC1))
                .getCell(ManageInvoicingCalendarsActionTab.InvoicingCalendars.ACTIONS.getName()).controls.links.get(ActionConstants.VIEW_EDIT).click();

        LOGGER.info("---=={Step 9}==---");
        DialogAssetList geography = billingAccount.addManageInvoicingCalendars().getWorkspace().getTab(ManageInvoicingCalendarsActionTab.class).getAssetList().getAsset(GEOGRAPHY);
        geography.getAsset(OPEN_POPUP).click();
        geography.getAsset(STATES).unsetAllValues();
        geography.getAsset(STATES).setValue("GA");
        geography.getAsset(SUBMIT_POPUP).click();
        Tab.buttonSave.click();
        Tab.buttonBack.click();
        assertThat(updateBillingAccountActionTab.getAssetList().getAsset(INVOICING_CALENDAR)).containsAllOptions(ImmutableList.of("", "Default Invoicing Calendar1", calendarNameIC2));

        LOGGER.info("---=={Step 10}==---");
        BillingAccountTab.linkManageInvocingCalendars.click();
        ManageInvoicingCalendarsActionTab.tableInvoicingCalendars.getRow(ImmutableMap.of(CALENDAR_NAME.getName(), calendarNameIC1))
                .getCell(ManageInvoicingCalendarsActionTab.InvoicingCalendars.ACTIONS.getName()).controls.links.get(ActionConstants.VIEW_EDIT).click();
        geography.getAsset(OPEN_POPUP).click();
        geography.getAsset(STATES).unsetAllValues();
        geography.getAsset(STATES).setValue(ImmutableList.of("GA", "NY"));
        geography.getAsset(SUBMIT_POPUP).click();
        Tab.buttonSave.click();
        Tab.buttonBack.click();
        assertThat(updateBillingAccountActionTab.getAssetList().getAsset(INVOICING_CALENDAR)).containsAllOptions(ImmutableList.of("", "Default Invoicing Calendar1", calendarNameIC1, calendarNameIC2));

        LOGGER.info("---=={Step 11}==---");
        updateBillingAccountActionTab.getAssetList().getAsset(INVOICING_CALENDAR).setValue(calendarNameIC1);
        Tab.buttonSave.click();
        billingAccount.updateBillingAccount().start();
        assertThat(updateBillingAccountActionTab.getAssetList().getAsset(INVOICING_CALENDAR)).hasValue(calendarNameIC1);

        LOGGER.info("---=={Step 12-13}==---");
        BillingAccountTab.linkManageInvocingCalendars.click();
        ManageInvoicingCalendarsActionTab.tableInvoicingCalendars.getRow(ImmutableMap.of(CALENDAR_NAME.getName(), calendarNameIC2))
                .getCell(ManageInvoicingCalendarsActionTab.InvoicingCalendars.ACTIONS.getName()).controls.links.get(ActionConstants.VIEW_EDIT).click();

        LOGGER.info("---=={Step 14}==---");
        billingAccount.addManageInvoicingCalendars().getWorkspace().getTab(ManageInvoicingCalendarsActionTab.class).getAssetList().getAsset(PRODUCTS).setValue(ImmutableList.of(GroupBenefitsMasterPolicyType.GB_AC.getName()));
        Tab.buttonSave.click();
        Tab.buttonBack.click();
        assertThat(updateBillingAccountActionTab.getAssetList().getAsset(INVOICING_CALENDAR)).containsAllOptions(ImmutableList.of("", "Default Invoicing Calendar1", calendarNameIC1));

        LOGGER.info("---=={Step 15}==---");
        BillingAccountTab.linkManageInvocingCalendars.click();
        ManageInvoicingCalendarsActionTab.tableInvoicingCalendars.getRow(ImmutableMap.of(CALENDAR_NAME.getName(), calendarNameIC2))
                .getCell(ManageInvoicingCalendarsActionTab.InvoicingCalendars.ACTIONS.getName()).controls.links.get(ActionConstants.VIEW_EDIT).click();
        billingAccount.addManageInvoicingCalendars().getWorkspace().getTab(ManageInvoicingCalendarsActionTab.class).getAssetList().getAsset(PRODUCTS).setValue(ImmutableList.of(GroupBenefitsMasterPolicyType.GB_AC.getName(), GroupBenefitsMasterPolicyType.GB_TL.getName()));
        Tab.buttonSave.click();
        Tab.buttonBack.click();
        assertThat(updateBillingAccountActionTab.getAssetList().getAsset(INVOICING_CALENDAR)).containsAllOptions(ImmutableList.of("", "Default Invoicing Calendar1", calendarNameIC1, calendarNameIC2));

        updateBillingAccountActionTab.getAssetList().getAsset(INVOICING_CALENDAR).setValue(calendarNameIC2);
        Tab.buttonSave.click();
        billingAccount.updateBillingAccount().start();
        assertThat(updateBillingAccountActionTab.getAssetList().getAsset(INVOICING_CALENDAR)).hasValue(calendarNameIC2);
    }
}