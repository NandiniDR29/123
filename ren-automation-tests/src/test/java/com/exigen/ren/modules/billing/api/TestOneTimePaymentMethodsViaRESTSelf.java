package com.exigen.ren.modules.billing.api;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments.BillingAccountsBenefitsPayments;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL_ESTIMATED;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.components.Components.CASE_PROFILE;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestOneTimePaymentMethodsViaRESTSelf extends GroupBenefitsBillingBaseTest implements BillingAccountContext, RoleContext, ProfileContext, CustomerContext, CaseProfileContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-26600", component = BILLING_REST)
    public void testOneTimePaymentMethodsViaREST() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData(CASE_PROFILE, DEFAULT_TEST_DATA_KEY), groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();
        String invoiceNum1 = BillingSummaryPage.getInvoiceNumberByRowNum(1);
        billingAccount.generateFutureStatement().perform();
        String invoiceNum2 = BillingSummaryPage.getInvoiceNumberByRowNum(1);
        billingAccount.generateFutureStatement().perform();
        String invoiceNum3 = BillingSummaryPage.getInvoiceNumberByRowNum(1);

        LOGGER.info("---=={Step 1}==---");
        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED)
                    .containsMatchingRow(3);

            TestData tdCash = billingAccount.defaultAPITestData().getTestData("RestAccountsBenefitsPayments", "TestData_Cash")
                    .adjust(TestData.makeKeyPath("amount", "value"), modalPremiumAmount.get().add(new Currency("10")).toPlainString());

            ResponseContainer<BillingAccountsBenefitsPayments> response =  billingBenefitsRestService.postAccountsBenefitsPayments("REST1", invoiceNum3, tdCash);
            softly.assertThat(response.getResponse()).hasStatus(200);
            mainApp().reopen();
            navigateToBillingAccount(masterPolicyNumber.get());
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNum1)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED).hasMatchingRows(1);
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNum2)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED).hasMatchingRows(1);
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNum3)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED).hasMatchingRows(1);

            assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.TYPE, BillingConstants.PaymentsAndOtherTransactionTypeGB.ACCOUNT_SUSPENSE)
                    .with(TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT, String.format("(%s)", new Currency("10").toString())).hasMatchingRows(1);
        });

        LOGGER.info("---=={Step 2}==---");
        assertSoftly(softly -> {
            TestData tdCreditCard = billingAccount.defaultAPITestData().getTestData("RestAccountsBenefitsPayments", "TestData_CreditCard")
                    .adjust(TestData.makeKeyPath("amount", "value"), modalPremiumAmount.get().toPlainString());
            ResponseContainer<BillingAccountsBenefitsPayments> response = billingBenefitsRestService.postAccountsBenefitsPayments("REST2", invoiceNum2, tdCreditCard);
            softly.assertThat(response.getResponse()).hasStatus(200);
            mainApp().reopen();
            navigateToBillingAccount(masterPolicyNumber.get());
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNum2)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED).hasMatchingRows(1);
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNum1)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED).hasMatchingRows(1);
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNum3)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED).hasMatchingRows(1);
        });

        assertSoftly(softly -> {
            TestData tdCreditCard = billingAccount.defaultAPITestData().getTestData("RestAccountsBenefitsPayments", "TestData_EFT")
                    .adjust(TestData.makeKeyPath("amount", "value"), modalPremiumAmount.get().toPlainString());
            ResponseContainer<BillingAccountsBenefitsPayments> response = billingBenefitsRestService.postAccountsBenefitsPayments("REST3", invoiceNum3, tdCreditCard);
            softly.assertThat(response.getResponse()).hasStatus(200);
            mainApp().reopen();
            navigateToBillingAccount(masterPolicyNumber.get());
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNum3)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED).hasMatchingRows(1);
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNum1)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED).hasMatchingRows(1);
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                    .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNum2)
                    .with(TableConstants.BillingBillsAndStatementsGB.STATUS, PAID_IN_FULL_ESTIMATED).hasMatchingRows(1);
        });
    }
}