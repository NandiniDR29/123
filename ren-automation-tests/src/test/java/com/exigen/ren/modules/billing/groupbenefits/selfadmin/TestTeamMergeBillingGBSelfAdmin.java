/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.enums.SearchEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.SearchPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.billing.BillingRestService;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestTeamMergeBillingGBSelfAdmin extends BaseTest implements CustomerContext, CaseProfileContext, BillingAccountContext, GroupAccidentMasterPolicyContext {

    private BillingRestService billingRestService = RESTServiceType.BILLING.get();

    @Test(groups = {BILLING_GB, TEAM_MERGE, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23712", component = BILLING_GROUPBENEFITS)
    public void testTeamMergeBillingGBSelfAdmin() {
        mainApp().open();

        customerNonIndividual.create(customerNonIndividual.getDefaultTestData("DataGather", "TestData_TeamMerge"));

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicySelfAdminData());

        LOGGER.info("TEST: Generate Future Statement");
        billingAccount.navigateToBillingAccount();
        billingAccount.generateFutureStatement().perform(DataProviderFactory.emptyData());

        Currency paymentAmount = new Currency(billingAccount.getDefaultTestData().getValue(
                "AcceptPayment", "TestData_Ach_Rest", "amount", "value"));

        Currency invoiceAmount = new Currency(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(
                BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());

        assertThat(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(
                BillingConstants.BillingBillsAndStatmentsTable.STATUS)).hasValue(
                BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED);
        String billingAccountNumber = BillingAccountsListPage.getAccountNumber();

        LOGGER.info("TEST: Accept Payment via REST");
        assertThat(billingRestService.postAccountsBenefitsPayments(
                billingAccountNumber, billingAccount.getDefaultTestData("AcceptPayment", "TestData_Ach_Rest"))
                .getStatus()).isEqualTo(200);

        SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.BILLING_ACCOUNT, billingAccountNumber);

        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
                    .getCell(BillingPaymentsAndOtherTransactionsTable.TYPE)).hasValue(
                    BillingConstants.PaymentsAndOtherTransactionTypeGB.ACCOUNT_SUSPENSE);
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
                    .getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT)).hasValue(
                    new Currency(paymentAmount).negate().toString());
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2)
                    .getCell(BillingPaymentsAndOtherTransactionsTable.TYPE)).hasValue(
                    BillingConstants.PaymentsAndOtherTransactionTypeGB.INVOICE);
            softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(2)
                    .getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT)).hasValue(
                    invoiceAmount.toString());
        });
    }
}
