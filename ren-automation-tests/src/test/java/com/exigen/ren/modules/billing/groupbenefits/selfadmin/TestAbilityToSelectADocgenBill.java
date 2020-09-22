/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.account.tabs.UpdateBillingAccountActionTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingAccountSetupTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.BillingConstants.InvoiceDocumentTemplate.*;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.*;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.INVOICE_DOCUMENT_TEMPLATE;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.INVOICING_CALENDAR;
import static com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab.buttonSave;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAbilityToSelectADocgenBill extends GroupBenefitsBillingBaseTest implements BillingAccountContext, StatutoryDisabilityInsuranceMasterPolicyContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-30400", component = BILLING_GROUPBENEFITS)
    public void testAbilityToSelectADocgenBill() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType(), groupAccidentMasterPolicy.getType(), paidFamilyLeaveMasterPolicy.getType());

        TestData tdMP1 = tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(tdSpecific().getTestData("TestData_Issue_Annual")).resolveLinks();
        statutoryDisabilityInsuranceMasterPolicy.createQuote(tdMP1);
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(tdMP1);
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(tdMP1);
        statutoryDisabilityInsuranceMasterPolicy.issue().start();
        statutoryDisabilityInsuranceMasterPolicy.issue().getWorkspace().fillUpTo(tdMP1, BillingAccountTab.class, true);
        BillingAccountTab.expandBillingAccountGeneralOptions();

        BillingAccountTab tabBA = statutoryDisabilityInsuranceMasterPolicy.issue().getWorkspace().getTab(BillingAccountTab.class);

        LOGGER.info("---=={TC-1 Step 1}==---");
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_1);
        tabBA.submitTab();

        LOGGER.info("---=={TC-1 Step 2 }==---");
        billingAccount.navigateToBillingAccount();
        billingAccount.setupBillingGroups().start();
        billingAccount.setupBillingGroups().getWorkspace().fillUpTo(tdSpecific().getTestData("TestDataSplitCoverages"), BillingAccountSetupTab.class);

        BillingAccountTab.expandBillingAccountGeneralOptions();
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_1);

        billingAccount.setupBillingGroups().getWorkspace().getTab(BillingAccountTab.class).fillTab(
                new SimpleDataProvider().adjust(BillingAccountTab.class.getSimpleName(), tdSpecific().getTestData("BillingAccountTabWithQuarterlyCalendar"))
                        .adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName(), "Billing Group Name Key"), "BG002"));

        BillingAccountTab.expandBillingAccountGeneralOptions();
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_2)
                .hasOptions(STATUTORY_INVOICE_TYPE_2, STATUTORY_INVOICE_TYPE_3);

        tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE).setValue(STATUTORY_INVOICE_TYPE_3);

        String ba1 = BillingAccountTab.tableBillingGroups.getRow(BillingAccountSetupTab.BillingGroups.BILLING_GROUP_NAME.getName(), "BG001")
                .getCell(BillingAccountSetupTab.BillingGroups.BILLING_ACCOUNT.getName()).getValue();
        String ba2 = BillingAccountTab.tableBillingGroups.getRow(BillingAccountSetupTab.BillingGroups.BILLING_GROUP_NAME.getName(), "BG002")
                .getCell(BillingAccountSetupTab.BillingGroups.BILLING_ACCOUNT.getName()).getValue();
        buttonSave.click();
        tabBA.submitTab();

        LOGGER.info("---=={TC2}==---");
        TestData tdMP2 = tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(tdSpecific().getTestData("TestData_Issue_Quarterly_Exist")).resolveLinks();
        statutoryDisabilityInsuranceMasterPolicy.createQuote(tdMP2);
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(tdMP2);
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(tdMP2);

        statutoryDisabilityInsuranceMasterPolicy.issue().start();
        statutoryDisabilityInsuranceMasterPolicy.issue().getWorkspace().fillUpTo(tdMP2, BillingAccountTab.class, true);
        BillingAccountTab.expandBillingAccountGeneralOptions();

        LOGGER.info("---=={TC2 Step 1}==---");
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_2)
                .hasOptions(STATUTORY_INVOICE_TYPE_2, STATUTORY_INVOICE_TYPE_3);

        String ba3 = BillingAccountTab.tableBillingGroups.getRow(BillingAccountSetupTab.BillingGroups.BILLING_GROUP_NAME.getName(), "BG001")
                .getCell(BillingAccountSetupTab.BillingGroups.BILLING_ACCOUNT.getName()).getValue();
        tabBA.submitTab();

        LOGGER.info("---=={TC2 Step 2}==---");
        billingAccount.navigateToBillingAccount();
        billingAccount.setupBillingGroups().start();
        billingAccount.setupBillingGroups().getWorkspace().fillUpTo(tdSpecific().getTestData("TestDataSplitCoverages"), BillingAccountSetupTab.class);

        BillingAccountTab.expandBillingAccountGeneralOptions();
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_2)
                .hasOptions(STATUTORY_INVOICE_TYPE_2, STATUTORY_INVOICE_TYPE_3);

        BillingAccountTab.tableBillingGroups.getRow(BillingAccountSetupTab.BillingGroups.BILLING_GROUP_NAME.getName(), "BG002").getCell(1).click();

        tabBA.getAssetList().getAsset(SELECT_ACTION).setValue("Bill Under Account");
        tabBA.getAssetList().getAsset(BILL_UNDER_ACCOUNT).setValueContains(ba1);
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_1);

        tabBA.getAssetList().getAsset(BILL_UNDER_ACCOUNT).setValueContains(ba2);
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_3)
                .hasOptions(STATUTORY_INVOICE_TYPE_3, STATUTORY_INVOICE_TYPE_2);

        buttonSave.click();
        tabBA.submitTab();

        LOGGER.info("---=={Step 3}==---");
        MainPage.QuickSearch.search(ba2);
        billingAccount.updateBillingAccount().start();
        ComboBox invoiceDocTemplate = billingAccount.updateBillingAccount().getWorkspace().getTab(UpdateBillingAccountActionTab.class).getAssetList().getAsset(INVOICE_DOCUMENT_TEMPLATE);
        assertThat(invoiceDocTemplate).hasValue(STATUTORY_INVOICE_TYPE_3);
        invoiceDocTemplate.setValue(STATUTORY_INVOICE_TYPE_2);
        assertThat(invoiceDocTemplate).hasValue(STATUTORY_INVOICE_TYPE_2);
        billingAccount.updateBillingAccount().submit();

        LOGGER.info("---=={TC3}==---");
        TestData tdMP3 = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ")
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY));

        statutoryDisabilityInsuranceMasterPolicy.createQuote(tdMP3);
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(tdMP3);
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(tdMP3);

        LOGGER.info("---=={TC3 Step 1}==---");
        statutoryDisabilityInsuranceMasterPolicy.issue().start();
        statutoryDisabilityInsuranceMasterPolicy.issue().getWorkspace().fillUpTo(tdMP2, BillingAccountTab.class, false);
        tabBA.getAssetList().getAsset(SELECT_ACTION).setValue("Bill Under Account");
        tabBA.getAssetList().getAsset(BILL_UNDER_ACCOUNT).setValueContains(ba3);
        BillingAccountTab.expandBillingAccountGeneralOptions();

        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICING_CALENDAR)).valueContains("QuarterlyCalendar");
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_2);
        buttonSave.click();
        tabBA.getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
        tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICING_CALENDAR).setValueContains("QuarterlyCalendar");
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_5).hasOptions(STATUTORY_INVOICE_TYPE_5, STATUTORY_INVOICE_TYPE_6, STATUTORY_INVOICE_TYPE_7);
        buttonSave.click();
        String ba4 = BillingAccountTab.tableBillingGroups.getRow(BillingAccountSetupTab.BillingGroups.BILLING_GROUP_NAME.getName(), "BG001")
                .getCell(BillingAccountSetupTab.BillingGroups.BILLING_ACCOUNT.getName()).getValue();
        tabBA.submitTab();

        LOGGER.info("---=={TC3 Step 2}==---");
        paidFamilyLeaveMasterPolicy.createQuote(getDefaultPFLMasterPolicyData()
                .adjust(TestData.makeKeyPath(PaidFamilyLeaveMasterPolicyContext.policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NJ"));
        paidFamilyLeaveMasterPolicy.propose().perform(getDefaultPFLMasterPolicyData());
        paidFamilyLeaveMasterPolicy.acceptContract().perform(getDefaultPFLMasterPolicyData());
        paidFamilyLeaveMasterPolicy.issue().start();
        paidFamilyLeaveMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultPFLMasterPolicyData(), BillingAccountTab.class, false);

        tabBA.getAssetList().getAsset(SELECT_ACTION).setValue("Bill Under Account");
        tabBA.getAssetList().getAsset(BILL_UNDER_ACCOUNT).setValueContains(ba4);
        BillingAccountTab.expandBillingAccountGeneralOptions();
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE))
                .hasWarningWithText("Selected document template does not match coverage, please select one of the provided templates.")
                .hasValue(STATUTORY_INVOICE_TYPE_5).hasOptions(STATUTORY_INVOICE_TYPE_5, STATUTORY_INVOICE_TYPE_6, STATUTORY_INVOICE_TYPE_7);

        tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE).setValue(STATUTORY_INVOICE_TYPE_6);
        buttonSave.click();
        tabBA.getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
        BillingAccountTab.expandBillingAccountGeneralOptions();
        tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICING_CALENDAR).setValueContains("AnnualCalendar");
        buttonSave.click();
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasWarningWithText("Invoice document template is required.");

        tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICING_CALENDAR).setValueContains("QuarterlyCalendar");
        BillingAccountTab.expandBillingAccountGeneralOptions();
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_6)
                .hasOptions(STATUTORY_INVOICE_TYPE_6, STATUTORY_INVOICE_TYPE_7);
        String ba5 = BillingAccountTab.tableBillingGroups.getRow(BillingAccountSetupTab.BillingGroups.BILLING_GROUP_NAME.getName(), "BG001")
                .getCell(BillingAccountSetupTab.BillingGroups.BILLING_ACCOUNT.getName()).getValue();
        buttonSave.click();
        tabBA.submitTab();

        LOGGER.info("---=={TC3 Step 3}==---");
        MainPage.QuickSearch.search(ba4);
        billingAccount.setupBillingGroups().start();
        billingAccount.setupBillingGroups().getWorkspace().fillUpTo(tdSpecific().getTestData("TestDataSplitCoveragesTC3"), BillingAccountSetupTab.class, false);
        tabBA.getAssetList().getAsset(SELECT_ACTION).setValue("Bill Under Account");
        tabBA.getAssetList().getAsset(BILL_UNDER_ACCOUNT).setValueContains(ba5);
        BillingAccountTab.expandBillingAccountGeneralOptions();
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).hasValue(STATUTORY_INVOICE_TYPE_6)
                .hasOptions(STATUTORY_INVOICE_TYPE_6, STATUTORY_INVOICE_TYPE_7);
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICING_CALENDAR)).valueContains("QuarterlyCalendar");
        buttonSave.click();
        tabBA.submitTab();

        LOGGER.info("---=={TC4}==---");
        groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicySelfAdminData());
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicySelfAdminData());
        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicySelfAdminData());
        groupAccidentMasterPolicy.issue().start();
        groupAccidentMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultACMasterPolicySelfAdminData(), BillingAccountTab.class, false);

        tabBA.getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICING_CALENDAR)).valueContains("Default");
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).isAbsent();

        tabBA.getAssetList().getAsset(SELECT_ACTION).setValue("Bill Under Account");
        tabBA.getAssetList().getAsset(BILL_UNDER_ACCOUNT).setValueContains(ba1);
        BillingAccountTab.expandBillingAccountGeneralOptions();

        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICING_CALENDAR)).valueContains("AnnualCalendar");
        assertThat(tabBA.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICE_DOCUMENT_TEMPLATE)).isAbsent();
        buttonSave.click();
        tabBA.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
    }

}