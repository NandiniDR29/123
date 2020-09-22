/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.metadata.SearchMetaData;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.account.tabs.ManageInvoicingCalendarsActionTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingAccountSetupTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.CaseProfileDetailsTab;
import com.exigen.ren.main.modules.caseprofile.tabs.ProductAndPlanManagementTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.TestDataKey.ISSUE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.*;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.INVOICING_CALENDAR;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.InvoicingCalendarTab.CALENDAR_NAME;
import static com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData.GEOGRAPHY;
import static com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData.PRODUCTS;
import static com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab.addToAll;
import static com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData.CASE_PROFILE_NAME;
import static com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData.GROUP_DOMICILE_STATE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProductAndPlanManagementTabMetaData.PRODUCT;
import static com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType.GB_ST;
import static com.exigen.ren.main.modules.policy.common.actions.common.ProposeAction.SELECT_CASE_BY_NAME;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestMatchingTheInvoicingCalendarSettings extends GroupBenefitsBillingBaseTest implements BillingAccountContext, StatutoryDisabilityInsuranceMasterPolicyContext, TermLifeInsuranceMasterPolicyContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15729", component = BILLING_GROUPBENEFITS)
    public void testMatchingTheInvoicingCalendarSettings() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        TestData tdCASE1 = caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_NoSubGroups")
                .adjust(TestData.makeKeyPath(ProductAndPlanManagementTab.class.getSimpleName(), PRODUCT.getLabel()), GB_ST.getName())
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), GROUP_DOMICILE_STATE.getLabel()),"AL")
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), CASE_PROFILE_NAME.getLabel()),"CASE1");

        TestData tdCASE2 = CaseProfileContext.getDefaultCaseProfileTestData(GroupBenefitsMasterPolicyType.GB_TL)
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), GROUP_DOMICILE_STATE.getLabel()),"NY")
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), CASE_PROFILE_NAME.getLabel()),"CASE2");

        TestData tdCASE3 = caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_NoSubGroups")
                .adjust(TestData.makeKeyPath(ProductAndPlanManagementTab.class.getSimpleName(), PRODUCT.getLabel()), GB_ST.getName())
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), GROUP_DOMICILE_STATE.getLabel()),"CA")
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), CASE_PROFILE_NAME.getLabel()),"CASE3");

        caseProfile.create(tdCASE1);
        caseProfile.create(tdCASE2);
        caseProfile.create(tdCASE3);

        billingAccount.navigateToBillingAccount();

        TestData tdInc1 = billingAccount.getDefaultTestData("CreateCalendars", "ManageInvoicingCalendarsActionSelf")
                .adjust(TestData.makeKeyPath(CALENDAR_NAME.getLabel()),"INC1");
        TestData tdInc2 = billingAccount.getDefaultTestData("CreateCalendars", "ManageInvoicingCalendarsActionSelf")
                .adjust(TestData.makeKeyPath(PRODUCTS.getLabel()), GroupBenefitsMasterPolicyType.GB_TL.getName())
                .adjust(TestData.makeKeyPath(GEOGRAPHY.getLabel(), ManageInvoicingCalendarsActionTabMetaData.GeographyMetaData.STATES.getLabel()),"NY")
                .adjust(TestData.makeKeyPath(CALENDAR_NAME.getLabel()),"INC2");
        TestData tdInc3 = billingAccount.getDefaultTestData("CreateCalendars", "ManageInvoicingCalendarsActionSelf")
                .adjust(TestData.makeKeyPath(GEOGRAPHY.getLabel(), ManageInvoicingCalendarsActionTabMetaData.GeographyMetaData.STATES.getLabel()),"CA")
                .adjust(TestData.makeKeyPath(CALENDAR_NAME.getLabel()),"INC3");
        TestData tdInc4 = billingAccount.getDefaultTestData("CreateCalendars", "ManageInvoicingCalendarsActionFull")
                .adjust(TestData.makeKeyPath(GEOGRAPHY.getLabel(), ManageInvoicingCalendarsActionTabMetaData.GeographyMetaData.STATES.getLabel()),"NY")
                .adjust(TestData.makeKeyPath(CALENDAR_NAME.getLabel()),"INC4");

        billingAccount.addManageInvoicingCalendars().perform(billingAccount.getDefaultTestData("CreateCalendars", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(ManageInvoicingCalendarsActionTab.class.getSimpleName()), ImmutableList.of(tdInc1, tdInc2, tdInc3, tdInc4)));

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            createPolicyMP1();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);

            LOGGER.info("---=={Step 2}==---");
            TestData tdMP2 = termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, TestDataKey.DEFAULT_TEST_DATA_KEY)
                    .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.CASE_PROFILE.getLabel()), "CASE2")
                    .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                            .adjust(TestData.makeKeyPath(QuotesSelectionActionTab.class.getSimpleName(), SELECT_CASE_BY_NAME), "CASE2"))
                    .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY));

            termLifeInsuranceMasterPolicy.createQuote(tdMP2);
            termLifeInsuranceMasterPolicy.propose().perform(tdMP2);
            termLifeInsuranceMasterPolicy.acceptContract().perform(tdMP2);
            termLifeInsuranceMasterPolicy.issue().start();
            termLifeInsuranceMasterPolicy.issue().getWorkspace()
                    .fillUpTo(termLifeInsuranceMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithExistingBA"), BillingAccountTab.class, true);

            Tab billingAccountTab = billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class);
            softly.assertThat(billingAccountTab.getAssetList().getAsset(BILLING_ACCOUNT_SETUP_ERROR_MESSAGE)).hasValue("Policy information does not match Invoicing Calendar Configuration. Please update Policy information or choose another Billing Account");

            setCalendar("INC2");
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);

            LOGGER.info("---=={Step 3}==---");
            createPolicyMP3();
            softly.assertThat(billingAccountTab.getAssetList().getAsset(BILLING_ACCOUNT_SETUP_ERROR_MESSAGE)).hasValue("Policy information does not match Invoicing Calendar Configuration. Please update Policy information or choose another Billing Account");

            setCalendar("INC3");
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);

            LOGGER.info("---=={Step 4}==---");
            createPolicyMP4();
            setCalendar("INC4");
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        });
    }

    private void createPolicyMP1(){
        TestData tdIssue = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY);
        tdIssue.getTestDataList(BillingAccountTab.class.getSimpleName()).get(0)
                .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), INVOICING_CALENDAR.getLabel()), "INC1");

        TestData tdMP1 = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ")
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.CASE_PROFILE.getLabel()), "CASE1")
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "AL")
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(tdIssue);

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(tdMP1);
    }

    private void createPolicyMP3() {

        TestData tdMP3 = statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ")
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.CASE_PROFILE.getLabel()), "CASE3")
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "CA")
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(QuotesSelectionActionTab.class.getSimpleName(), SELECT_CASE_BY_NAME), "CASE3"))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA"));

        statutoryDisabilityInsuranceMasterPolicy.initiate(tdMP3);
        statutoryDisabilityInsuranceMasterPolicy.getDefaultWorkspace().fill(tdMP3);
        statutoryDisabilityInsuranceMasterPolicy.propose().perform(tdMP3);
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(tdMP3);
        statutoryDisabilityInsuranceMasterPolicy.issue().start();
        statutoryDisabilityInsuranceMasterPolicy.issue().getWorkspace().fillUpTo(tdMP3, BillingAccountTab.class, false);

        billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Bill Under Account");
        billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(BILL_UNDER_ACCOUNT).setValueByIndex(2);
        BillingAccountSetupTab.saveTab();
    }

    private void createPolicyMP4() {

        TestData tdMP4 = termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.CASE_PROFILE.getLabel()), "CASE2")
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(QuotesSelectionActionTab.class.getSimpleName(), SELECT_CASE_BY_NAME), "CASE2"))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA"));

        termLifeInsuranceMasterPolicy.createQuote(tdMP4);
        termLifeInsuranceMasterPolicy.propose().perform(tdMP4);
        termLifeInsuranceMasterPolicy.acceptContract().perform(tdMP4);
        termLifeInsuranceMasterPolicy.issue().start();
        termLifeInsuranceMasterPolicy.issue().getWorkspace().fillUpTo(tdMP4, IssueActionTab.class, true);
        termLifeInsuranceMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class).submitTab();

    }

    private void setCalendar(String calendarName){
        Tab billingAccountTab = billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class);
        billingAccountTab.getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
        BillingAccountTab.expandBillingAccountGeneralOptions();
        billingAccountTab.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(INVOICING_CALENDAR).setValue(calendarName);
        BillingAccountSetupTab.saveTab();
        addToAll.click();
        Tab.buttonFinish.click();
    }
}