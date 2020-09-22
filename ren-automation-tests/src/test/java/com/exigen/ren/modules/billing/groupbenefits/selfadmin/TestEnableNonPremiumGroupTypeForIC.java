/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar.metadata.DefaultInvoicingCalendarMetaData;
import com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar.tabs.DefaultInvoicingCalendarTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData;
import com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.account.tabs.ManageInvoicingCalendarsActionTab;
import com.exigen.ren.main.modules.billing.account.tabs.UpdateBillingAccountActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.ipb.eisa.verification.CustomAssertionsExtended.assertThat;
import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar.DefaultInvoicingCalendarContext.defaultInvoicingCalendar;
import static com.exigen.ren.admin.modules.billing.billingcycle.calendars.default_invoicing_calendar.metadata.DefaultInvoicingCalendarMetaData.*;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ActionConstants.DELETE;
import static com.exigen.ren.main.enums.ActionConstants.VIEW_EDIT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.ADD_INVOICING_CALENDAR;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.*;
import static com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab.saveTab;
import static com.exigen.ren.main.modules.billing.account.tabs.ManageInvoicingCalendarsActionTab.InvoicingCalendars.CALENDAR_NAME;
import static com.exigen.ren.main.modules.billing.account.tabs.ManageInvoicingCalendarsActionTab.tableInvoicingCalendars;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FICA_MATCH;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestEnableNonPremiumGroupTypeForIC extends GroupBenefitsBillingBaseTest implements BillingAccountContext, LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext {

    private String defaultNonPremiumCalendarName = "Monthly in Arrears";

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-47616", component = BILLING_GROUPBENEFITS)
    public void testAddPaymentSelfAdmin() {
        LOGGER.info("---=={Step 1}==---");
        adminApp().open();
        defaultInvoicingCalendar.navigate();
        ImmutableMap<String, String> calendarMap = ImmutableMap.of(DefaultInvoicingCalendarTab.DefaultInvoicingCalendar.NAME.getName(), defaultNonPremiumCalendarName);
        assertThat(DefaultInvoicingCalendarTab.tableCalendars).hasMatchingRows(calendarMap);

        DefaultInvoicingCalendarTab.tableCalendars.getRow(calendarMap).getCell(DefaultInvoicingCalendarTab.DefaultInvoicingCalendar.ACTIONS.getName())
                .controls.links.get(VIEW_EDIT).click();

        DefaultInvoicingCalendarTab defaultInvoicingCalendarTab = defaultInvoicingCalendar.getDefaultWorkspace().getTab(DefaultInvoicingCalendarTab.class);
        AbstractContainer<?, ?> assetList = defaultInvoicingCalendarTab.getAssetList();
        assertSoftly(softly -> {
            softly.assertThat(assetList.getAsset(BILLING_CALENDAR)).hasValue("Billing Calendar1");
            softly.assertThat(assetList.getAsset(DefaultInvoicingCalendarMetaData.CALENDAR_NAME)).hasValue(defaultNonPremiumCalendarName);
            softly.assertThat(assetList.getAsset(SELF_ADMINISTERED)).hasValue(false);
            softly.assertThat(assetList.getAsset(LIST_BILL)).hasValue(false);
            softly.assertThat(assetList.getAsset(NON_PREMIUM)).hasValue(true);
            softly.assertThat(assetList.getAsset(PRODUCTS)).hasValue(ImmutableList.of());
            assetList.getAsset(GEOGRAPHY).getAsset(ManageInvoicingCalendarsActionTabMetaData.GeographyMetaData.OPEN_POPUP).click();
            softly.assertThat(assetList.getAsset(GEOGRAPHY).getAsset(ManageInvoicingCalendarsActionTabMetaData.GeographyMetaData.COUNTRIES)).hasValue(ImmutableList.of());
            assetList.getAsset(GEOGRAPHY).getAsset(ManageInvoicingCalendarsActionTabMetaData.GeographyMetaData.CLOSE_POPUP).click();
            softly.assertThat(assetList.getAsset(INVOICING_FREQUENCY)).hasValue("Monthly");
            softly.assertThat(assetList.getAsset(INVOICING_RULE)).hasValue("In Arrears");
            softly.assertThat(assetList.getAsset(BILLING_PERIOD_OFFSET)).hasValue("1");
            softly.assertThat(assetList.getAsset(INVOICE_DUE_DAY)).hasValue("15");
            softly.assertThat(assetList.getAsset(GENERATION_DATE_RULE)).hasValue("14");
        });

        Tab.buttonCancel.click();

        DefaultInvoicingCalendarTab.buttonAdd.click();
        assertThat(assetList.getAsset(NON_PREMIUM)).hasValue(false).isEnabled();
        assetList.getAsset(BILLING_CALENDAR).setValue("index=1");
        assetList.getAsset(DefaultInvoicingCalendarMetaData.CALENDAR_NAME).setValue("Default Non-Premium IC");
        assetList.getAsset(LIST_BILL).setValue(true);
        assetList.getAsset(NON_PREMIUM).setValue(true);
        assetList.getAsset(INVOICING_FREQUENCY).setValue("Monthly");
        assetList.getAsset(INVOICING_RULE).setValue("In Arrears");
        assetList.getAsset(BILLING_PERIOD_OFFSET).setValue("1");
        assetList.getAsset(INVOICE_DUE_DAY).setValue("15");
        assetList.getAsset(GENERATION_DATE_RULE).setValue("14");

        Tab.buttonSave.click();
        assertThat(DefaultInvoicingCalendarTab.tableCalendars).with(DefaultInvoicingCalendarTab.DefaultInvoicingCalendar.NAME, "Default Non-Premium IC").hasMatchingRows(1);
        DefaultInvoicingCalendarTab.tableCalendars.getRow(ImmutableMap.of(DefaultInvoicingCalendarTab.DefaultInvoicingCalendar.NAME.getName(), "Default Non-Premium IC"))
                .getCell(DefaultInvoicingCalendarTab.DefaultInvoicingCalendar.ACTIONS.getName())
                .controls.links.get(DELETE).click();
        dialogConfirmation.confirm();
        assertThat(DefaultInvoicingCalendarTab.tableCalendars).with(DefaultInvoicingCalendarTab.DefaultInvoicingCalendar.NAME, "Default Non-Premium IC").hasMatchingRows(0);

        mainApp().reopen();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        TestData td = getDefaultLTDMasterPolicySelfAdminData()
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Reimbursement")
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "index=1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true");

        commonSteps(td);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-47616", component = BILLING_GROUPBENEFITS)
    public void testAddPaymentFullAdmin() {


        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        TestData td = getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Reimbursement")
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "index=1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true");

        commonSteps(td);
    }

    private void commonSteps(TestData td) {
        LOGGER.info("---=={Step 2}==---");
        longTermDisabilityMasterPolicy.createQuote(td);
        longTermDisabilityMasterPolicy.propose().perform(td);
        longTermDisabilityMasterPolicy.acceptContract().perform(td);
        longTermDisabilityMasterPolicy.issue().start();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(td, BillingAccountTab.class);
        Tab billingAccountTab = longTermDisabilityMasterPolicy.issue().getWorkspace().getTab(BillingAccountTab.class);
        billingAccountTab.getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
        assertThat(billingAccountTab.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT)).isDisabled().hasValue(true);

        billingAccountTab.getAssetList().getAsset(MANAGE_INVOICING_CALENDARS_NON_PREMIUM).click();
        tableInvoicingCalendars.getRow(ImmutableMap.of(CALENDAR_NAME.getName(), defaultNonPremiumCalendarName))
                .getCell(ManageInvoicingCalendarsActionTab.InvoicingCalendars.ACTIONS.getName()).controls.links.get(ActionConstants.VIEW_EDIT).click();

        AbstractContainer<?, ?> assetCalendar = billingAccountTab.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(ADD_INVOICING_CALENDAR);

        assertSoftly(softly -> {
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.BILLING_CALENDAR)).hasValue("Billing Calendar1");
            softly.assertThat(assetCalendar.getAsset(DefaultInvoicingCalendarMetaData.CALENDAR_NAME)).hasValue(defaultNonPremiumCalendarName);
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.SELF_ADMINISTERED)).hasValue(false);
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.LIST_BILL)).hasValue(false);
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.NON_PREMIUM)).hasValue(true);
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.PRODUCTS)).hasValue(ImmutableList.of());
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.GEOGRAPHY).getAsset(BillingAccountTabMetaData.GeographyMetaData.COUNTRIES)).hasValue(ImmutableList.of());
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.INVOICING_FREQUENCY)).hasValue("Monthly");
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.INVOICING_RULE)).hasValue("In Arrears");
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.BILLING_PERIOD_OFFSET)).hasValue("1");
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.INVOICE_DUE_DAY)).hasValue("15");
            softly.assertThat(assetCalendar.getAsset(InvoicingCalendarTab.GENERATION_DATE_RULE)).hasValue("14");
        });
        Tab.buttonCancel.click();
        Tab.buttonBack.click();
        LOGGER.info("---=={Step 3}==---");
        assertThat(billingAccountTab.getAssetList().getAsset(INVOICING_CALENDAR_VALUE)).containsAllOptions(ImmutableList.of(EMPTY, defaultNonPremiumCalendarName));
        billingAccountTab.getAssetList().getAsset(MANAGE_INVOICING_CALENDARS_NON_PREMIUM).click();
        assertThat(tableInvoicingCalendars).hasRows(2);
        Tab.buttonBack.click();
        saveTab();
        billingAccountTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("---=={Step 4}==---");
        navigateToBillingAccount(masterPolicyNumber);
        billingAccount.updateBillingAccount().start();
        Tab updateBillingAccountActionTab = billingAccount.updateBillingAccount().getWorkspace().getTab(UpdateBillingAccountActionTab.class);
        updateBillingAccountActionTab.getAssetList().getAsset(ADD_INVOICING_CALENDAR).getAsset(InvoicingCalendarTab.MANAGE_INVOICING_CALENDARS).click();

        assertThat(tableInvoicingCalendars).hasRows(2);
        tableInvoicingCalendars.getRow(CALENDAR_NAME.getName(), defaultNonPremiumCalendarName).getCell(ManageInvoicingCalendarsActionTab.InvoicingCalendars.ACTIONS.getName())
                .controls.links.get(VIEW_EDIT).click();

        AbstractContainer<?, ?> updateBillingAccountCalendarTab = updateBillingAccountActionTab.getAssetList().getAsset(ADD_INVOICING_CALENDAR);
        assertSoftly(softly -> {
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(BILLING_CALENDAR)).hasValue("Billing Calendar1");
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(DefaultInvoicingCalendarMetaData.CALENDAR_NAME)).hasValue(defaultNonPremiumCalendarName);
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(SELF_ADMINISTERED)).hasValue(false);
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(LIST_BILL)).hasValue(false);
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(NON_PREMIUM)).hasValue(true);
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(PRODUCTS)).hasValue(ImmutableList.of());
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(GEOGRAPHY).getAsset(ManageInvoicingCalendarsActionTabMetaData.GeographyMetaData.COUNTRIES)).hasValue(ImmutableList.of());
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(INVOICING_FREQUENCY)).hasValue("Monthly");
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(INVOICING_RULE)).hasValue("In Arrears");
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(BILLING_PERIOD_OFFSET)).hasValue("1");
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(INVOICE_DUE_DAY)).hasValue("15");
            softly.assertThat(updateBillingAccountCalendarTab.getAsset(GENERATION_DATE_RULE)).hasValue("14");
        });

    }
}
