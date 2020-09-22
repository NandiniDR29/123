/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingAccountSetupTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_SELF_ADMIN;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BILL_UNDER_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.SELECT_ACTION;
import static com.exigen.ren.main.modules.billing.setup_billing_groups.metadata.BillingAccountSetupTabMetaData.GENERATE_ADDITIONAL_SPREADSHEET_FORMAT_BILL;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddAbilityToGenerateBillsInTable extends GroupBenefitsBillingBaseTest implements BillingAccountContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-17064", component = BILLING_GROUPBENEFITS)
    public void testAddAbilityToGenerateBillsInTable() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        LOGGER.info("Scenario 1");
        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, "TestData_WithTwoCoverages")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));

        masterPolicyNumber.set(PolicySummaryPage.labelPolicyNumber.getValue());
        navigateToBillingAccount(masterPolicyNumber.get());

        assertSoftly(softly -> {
            billingAccount.setupBillingGroups().start();
            billingAccount.setupBillingGroups().getWorkspace().fillUpTo(billingAccount.getDefaultTestData("SetupBillingGroups", DEFAULT_TEST_DATA_KEY), BillingAccountSetupTab.class);
            Tab billingAccountSetupTab = billingAccount.setupBillingGroups().getWorkspace().getTab(BillingAccountSetupTab.class);
            softly.assertThat(billingAccountSetupTab.getAssetList().getAsset(GENERATE_ADDITIONAL_SPREADSHEET_FORMAT_BILL)).isPresent().isEnabled().hasValue(false);
            Tab.buttonFinish.click();
        });

        LOGGER.info("Scenario 3");
        TestData td = groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, "TestData_WithThreeCoverages")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));

        groupAccidentMasterPolicy.createQuote(td);
        groupAccidentMasterPolicy.propose().perform(td);
        groupAccidentMasterPolicy.acceptContract().perform(td);

        groupAccidentMasterPolicy.issue().start();
        groupAccidentMasterPolicy.issue().getWorkspace().fillUpTo(td, BillingAccountTab.class, true);

        BillingAccountSetupTab.tableBillingGroups.getRow(2).getCell(1).click();
        billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Bill Under Account");
        billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(BILL_UNDER_ACCOUNT).setValueByIndex(2);
        BillingAccountSetupTab.saveTab();

        assertSoftly(softly -> {
            softly.assertThat(new BillingAccountSetupTab().getAssetList().getAsset(GENERATE_ADDITIONAL_SPREADSHEET_FORMAT_BILL)).isPresent().isEnabled().hasValue(false);
            BillingAccountSetupTab.tableBillingGroups.getRow(1).getCell(1).click();
            softly.assertThat(new BillingAccountSetupTab().getAssetList().getAsset(GENERATE_ADDITIONAL_SPREADSHEET_FORMAT_BILL)).isPresent().isEnabled().hasValue(false);
            Tab.buttonFinish.click();

            LOGGER.info("Scenario 4: 'Billing Accounts Setup' - mark checkbox");
            billingAccount.setupBillingGroups().start();
            billingAccount.setupBillingGroups().getWorkspace().fillUpTo(billingAccount.getDefaultTestData("SetupBillingGroups", "TestData_Update"),
                    BillingAccountSetupTab.class);

            BillingAccountSetupTab.tableBillingGroups.getRow(3).getCell(1).click();
            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Bill Under Account");
            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(BILL_UNDER_ACCOUNT).setValueByIndex(2);
            BillingAccountSetupTab.saveTab();
            billingAccount.setupBillingGroups().getWorkspace().getTab(BillingAccountSetupTab.class).getAssetList().getAsset(GENERATE_ADDITIONAL_SPREADSHEET_FORMAT_BILL).setValue(true);

            IntStream.range(0, BillingAccountSetupTab.tableBillingGroups.getRowsCount()).forEach(rowNum ->{
                    BillingAccountSetupTab.tableBillingGroups.getRow(rowNum+1).getCell(1).click();
                    softly.assertThat(billingAccount.setupBillingGroups().getWorkspace().getTab(BillingAccountSetupTab.class)
                            .getAssetList().getAsset(GENERATE_ADDITIONAL_SPREADSHEET_FORMAT_BILL)).isPresent().isEnabled().hasValue(true);
            });
            Tab.buttonFinish.click();
        });
     }
}