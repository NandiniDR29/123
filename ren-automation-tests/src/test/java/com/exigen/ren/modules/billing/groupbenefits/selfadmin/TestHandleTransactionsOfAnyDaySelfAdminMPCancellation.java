/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;


import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.CancellationActionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.main.pages.summary.billing.ModalPremiumSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.CANCELLATION;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.BamConstants.FINISHED;
import static com.exigen.ren.main.enums.BamConstants.MODAL_PREMIUM_FOR_BILLING_GROUP;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.ENHANCED_10_UNITS;
import static com.exigen.ren.main.modules.policy.common.metadata.common.CancellationActionTabMetaData.CANCEL_DATE;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestHandleTransactionsOfAnyDaySelfAdminMPCancellation extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-47489", component = BILLING_GROUPBENEFITS)
    public void testHandleTransactionsOfAnyDaySelfAdminMPCancellation() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createPolicySelfAdminWithOneCoverage();
        String policyNumberMP1 = PolicySummaryPage.labelPolicyNumber.getValue();
        LocalDateTime policyEffectiveDateMP1 = PolicySummaryPage.getEffectiveDate();
        billingAccount.navigateToBillingAccountList();
        String ba1 = BillingAccountsListPage.tableBenefitAccounts.getRow(1).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();

        createPolicySelfAdminWithOneCoverage();
        String policyNumberMP2 = PolicySummaryPage.labelPolicyNumber.getValue();
        LocalDateTime policyEffectiveDateMP2 = PolicySummaryPage.getEffectiveDate();
        billingAccount.navigateToBillingAccountList();
        String ba2 = BillingAccountsListPage.tableBenefitAccounts.getRow(1).getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).getValue();

        LOGGER.info("---=={Step 1}==---");
        MainPage.QuickSearch.search(policyNumberMP1);
        groupAccidentMasterPolicy.cancel().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(ActionConstants.ProductAction.CANCELLATION);
        MainPage.QuickSearch.search(ba1);

        String billingGroup = BillingSummaryPage.tableBillableCoverages.getRow(1).getCell(TableConstants.BillingBillableCoveragesGB.BILLING_GROUP.getName()).getValue();
        String billingGroupValue = billingGroup.substring(billingGroup.indexOf('(') + 1, billingGroup.indexOf(')'));
        BillingAccountsListPage.verifyBamActivities(String.format(MODAL_PREMIUM_FOR_BILLING_GROUP, billingGroupValue, policyEffectiveDateMP1.format(MM_DD_YYYY)), FINISHED);
        billingAccount.viewModalPremium().start();
        ModalPremiumSummaryPage.tableModalPremium.getRow(ImmutableMap.of(ModalPremiumSummaryPage.ModalPremiums.POLICY_PLAN.getName(), ENHANCED_10_UNITS))
                .getCell(ModalPremiumSummaryPage.ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();

        assertThat(ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(0))
                .with(ModalPremiumSummaryPage.BillingModalPremiumTable.MODAL_PREMIUM_EFFECTIVE_DATE,  policyEffectiveDateMP1.format(MM_DD_YYYY))
                .with(ModalPremiumSummaryPage.BillingModalPremiumTable.AMOUNT, "Transaction Moved")
                .with(ModalPremiumSummaryPage.BillingModalPremiumTable.TRANSACTION_TYPE_SUBTYPE_REASON, "Cancellation (Flat / Non Payment of Premium)").hasMatchingRows(1);

        Tab.buttonBack.click();
        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED)
                .with(TableConstants.BillingBillsAndStatementsGB.CURRENT_DUE, new Currency().toString())
                .with(TableConstants.BillingBillsAndStatementsGB.PRIOR_DUE, new Currency().toString())
                .with(TableConstants.BillingBillsAndStatementsGB.TOTAL_DUE, new Currency().toString()) .hasMatchingRows(1);

        LOGGER.info("---=={Step 2}==---");
        MainPage.QuickSearch.search(policyNumberMP2);
        groupAccidentMasterPolicy.cancel().perform(groupAccidentMasterPolicy.getDefaultTestData(CANCELLATION, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CancellationActionTab.class.getSimpleName(), CANCEL_DATE.getLabel()), policyEffectiveDateMP2.plusDays(14).format(MM_DD_YYYY)));
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(ActionConstants.ProductAction.CANCELLATION);
        MainPage.QuickSearch.search(ba2);

        String billingGroup2 = BillingSummaryPage.tableBillableCoverages.getRow(1).getCell(TableConstants.BillingBillableCoveragesGB.BILLING_GROUP.getName()).getValue();
        String billingGroupValue2 = billingGroup2.substring(billingGroup2.indexOf('(') + 1, billingGroup2.indexOf(')'));
        BillingAccountsListPage.verifyBamActivities(String.format(MODAL_PREMIUM_FOR_BILLING_GROUP, billingGroupValue2, policyEffectiveDateMP1.format(MM_DD_YYYY)), FINISHED);
        billingAccount.viewModalPremium().start();
        ModalPremiumSummaryPage.tableModalPremium.getRow(ImmutableMap.of(ModalPremiumSummaryPage.ModalPremiums.POLICY_PLAN.getName(), ENHANCED_10_UNITS))
                .getCell(ModalPremiumSummaryPage.ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();

        assertThat(ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(0))
                .with(ModalPremiumSummaryPage.BillingModalPremiumTable.MODAL_PREMIUM_EFFECTIVE_DATE,  policyEffectiveDateMP1.plusMonths(1).format(MM_DD_YYYY))
                .with(ModalPremiumSummaryPage.BillingModalPremiumTable.AMOUNT, "Transaction Moved")
                .with(ModalPremiumSummaryPage.BillingModalPremiumTable.TRANSACTION_TYPE_SUBTYPE_REASON, "Cancellation ( - / Non Payment of Premium)").hasMatchingRows(1);

        Tab.buttonBack.click();
        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED)
                .with(TableConstants.BillingBillsAndStatementsGB.CURRENT_DUE, firstCoverageModalPremium.get().toString())
                .with(TableConstants.BillingBillsAndStatementsGB.PRIOR_DUE, new Currency().toString())
                .with(TableConstants.BillingBillsAndStatementsGB.TOTAL_DUE, firstCoverageModalPremium.get().toString()) .hasMatchingRows(1);

        billingAccount.generateFutureStatement().perform();
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED)
                .with(TableConstants.BillingBillsAndStatementsGB.CURRENT_DUE, new Currency().toString())
                .with(TableConstants.BillingBillsAndStatementsGB.PRIOR_DUE, firstCoverageModalPremium.get().toString())
                .with(TableConstants.BillingBillsAndStatementsGB.TOTAL_DUE, firstCoverageModalPremium.get().toString()) .hasMatchingRows(1);
    }


}
