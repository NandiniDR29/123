/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.ren.admin.modules.billing.rules.write_off.benefits.WriteOffBenefitsContext;
import com.exigen.ren.admin.modules.billing.rules.write_off.benefits.pages.WriteOffBenefitsPage;
import com.exigen.ren.admin.modules.billing.rules.write_off.benefits.tabs.WriteOffBenefitsTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.admin.modules.billing.rules.write_off.benefits.metadata.WriteOffBenefitsMetaData.*;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.PaymentsAndAdjustmentsStatusGB.APPROVED;
import static com.exigen.ren.main.enums.BillingConstants.PaymentsAndAdjustmentsStatusGB.PENDING;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigurationScriptForWriteOffType extends GroupBenefitsBillingBaseTest implements BillingAccountContext, WriteOffBenefitsContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23642", component = BILLING_GROUPBENEFITS)
    public void TestConfigurationScriptForWriteOffTypeTC1() {
        assertSoftly(softly -> {
            adminApp().open();
            writeOffBenefit.navigate();
            writeOffBenefit.search(defaultWriteOffBenefitTestData());
            softly.assertThat(WriteOffBenefitsPage.tableWriteOffReasonsPage).hasMatchingRows(1, ImmutableMap.of(
                    WriteOffBenefitsPage.WriteOffTypes.DESCRIPTION.getName(), "ManualWriteOff",
                    WriteOffBenefitsPage.WriteOffTypes.WRITE_OFF_TYPE.getName(), "Premium: manual write off for all invoices",
                    WriteOffBenefitsPage.WriteOffTypes.EFFECTIVE.getName(), "01/01/2019"));

            WriteOffBenefitsPage.tableWriteOffReasonsPage.getRow(1).getCell(WriteOffBenefitsPage.WriteOffTypes.ACTIONS.getName())
                    .controls.links.get(ActionConstants.EDIT).click();
            Tab writeOffBenefitsTab =  writeOffBenefit.getDefaultWorkspace().getTab(WriteOffBenefitsTab.class);
            softly.assertThat(writeOffBenefitsTab.getAssetList().getAsset(WRITE_OFF_DESCRIPTION)).hasValue("en_US=ManualWriteOff");
            softly.assertThat(writeOffBenefitsTab.getAssetList().getAsset(WRITE_OFF_TYPE)).hasValue("Premium: manual write off for all invoices");
            softly.assertThat(writeOffBenefitsTab.getAssetList().getAsset(EFFECTIVE_DATE)).hasValue("01/01/2019");
        });
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-23642", component = BILLING_GROUPBENEFITS)
    public void TestConfigurationScriptForWriteOffTypeTC2Self() {
        assertSoftly(softly -> {
            mainApp().open();
            createDefaultNonIndividualCustomer();
            createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

            createPolicySelfAdmin();
            navigateToBillingAccount(masterPolicyNumber.get());

            LOGGER.info("TEST: Generate Future Statement for Policy # " + masterPolicyNumber.get());
            billingAccount.generateFutureStatement().perform();
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED).hasMatchingRows();
            commonSteps(softly);

        });
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-23642", component = BILLING_GROUPBENEFITS)
    public void TestConfigurationScriptForWriteOffTypeTC2Full() {
        assertSoftly(softly -> {
            mainApp().open();
            createDefaultNonIndividualCustomer();
            createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

            createPolicyFullAdmin();
            navigateToBillingAccount(masterPolicyNumber.get());

            LOGGER.info("TEST: Generate Future Statement for Policy # " + masterPolicyNumber.get());
            billingAccount.generateFutureStatement().perform();
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED).hasMatchingRows();
            commonSteps(softly);

        });
    }

    private void commonSteps(ETCSCoreSoftAssertions softly) {

        mainApp().close();

        LOGGER.info("---=={Step 1}==---");
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(1));
        mainApp().reopen();
        navigateToBillingAccount(masterPolicyNumber.get());

        LOGGER.info("---=={Step 2}==---");
        billingAccount.otherTransactions().perform(billingAccount.getDefaultTestData("OtherTransactions", "TestData_Adjustment"));
        softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.SUBTYPE, "ManualWriteOff")
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.REASON, "Termination")
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, PENDING).hasMatchingRows(1);

        LOGGER.info("---=={Step 3}==---");
        billingAccount.paymentsAdjustmentsAction(ActionConstants.BillingPendingTransactionAction.APPROVE);
        softly.assertThat(BillingSummaryPage.tablePaymentsAndAdjustmentsGB)
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.SUBTYPE, "ManualWriteOff")
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.REASON, "Termination")
                .with(TableConstants.BillingPaymentsAndAdjustmentsGB.STATUS, APPROVED).hasMatchingRows(1);
    }
}