/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FICA_MATCH;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestIntegrationWithPolicyForTheFICAMatchFlag extends GroupBenefitsBillingBaseTest implements BillingAccountContext, LongTermDisabilityMasterPolicyContext, DisabilityClaimContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-46333", component = BILLING_GROUPBENEFITS)
    public void testAddPaymentSelfAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        TestData td = longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoPlans")
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Reimbursement")
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[2]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Embedded")
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "index=1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true");

        longTermDisabilityMasterPolicy.createQuote(td);
        longTermDisabilityMasterPolicy.propose().perform(td);
        longTermDisabilityMasterPolicy.acceptContract().perform(td);
        longTermDisabilityMasterPolicy.issue().start();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(td, BillingAccountTab.class);
        billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
        assertThat(billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT)).isDisabled().hasValue(true);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-46333", component = BILLING_GROUPBENEFITS)
    public void testAddPaymentSelfAdminTC2() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        TestData td = longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoPlans")
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Embedded")
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "index=1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true");

        longTermDisabilityMasterPolicy.createQuote(td);
        longTermDisabilityMasterPolicy.propose().perform(td);
        longTermDisabilityMasterPolicy.acceptContract().perform(td);
        longTermDisabilityMasterPolicy.issue().start();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(td, BillingAccountTab.class);
        billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
        assertThat(billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT)).isDisabled().hasValue(false);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-46333", component = BILLING_GROUPBENEFITS)
    public void testAddPaymentFullAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        TestData td = longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, "TestData_TwoPlans")
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Reimbursement")
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[2]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Embedded")
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "index=1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true");

        longTermDisabilityMasterPolicy.createQuote(td);
        longTermDisabilityMasterPolicy.propose().perform(td);
        longTermDisabilityMasterPolicy.acceptContract().perform(td);
        longTermDisabilityMasterPolicy.issue().start();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(td, BillingAccountTab.class);
        billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
        assertThat(billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT)).isDisabled().hasValue(true);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-46333", component = BILLING_GROUPBENEFITS)
    public void testAddPaymentFullAAdminTC2() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        TestData td = longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER_SELF_ADMIN, "TestData_TwoPlans")
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Embedded")
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", INVOICING_CALENDAR_VALUE.getLabel()), "index=1")
                .adjust(makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT.getLabel()), "true");

        longTermDisabilityMasterPolicy.createQuote(td);
        longTermDisabilityMasterPolicy.propose().perform(td);
        longTermDisabilityMasterPolicy.acceptContract().perform(td);
        longTermDisabilityMasterPolicy.issue().start();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(td, BillingAccountTab.class);
        billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
        assertThat(billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(CREATE_LINKED_NON_PREMIUM_TYPE_BILLING_ACCOUNT)).isDisabled().hasValue(false);
    }
}
