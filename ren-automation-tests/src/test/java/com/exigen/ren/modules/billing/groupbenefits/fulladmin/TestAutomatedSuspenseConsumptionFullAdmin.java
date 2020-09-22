package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.main.enums.BamConstants;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAutomatedSuspenseConsumptionFullAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext, GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-14176", component = BILLING_GROUPBENEFITS)
    public void testAutomatedSuspenseConsumptionFullAdmin() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(GroupBenefitsMasterPolicyType.GB_AC);

        LOGGER.info("TEST: Create AC full admin master policy");
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData()
                .adjust(ClassificationManagementTab.class.getSimpleName(),
                        ImmutableList.of(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "ClassificationManagementTab_BASEBU"))));

        createDefaultGroupAccidentCertificatePolicy();
        PolicySummaryPage.linkMasterPolicy.click();
        String fullAdminMasterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        Currency fullAdminCoverageModalPremium = new Currency(getModalPremiumAmountFromCoverageSummary(1));

        LOGGER.info("Add $200 to Suspense");
        navigateToBillingAccount(fullAdminMasterPolicyNumber);
        billingAccount.addSuspenseAmount("200");
        assertThat(BillingSummaryPage.tablePaymentsOtherTransactions)
                .hasMatchingRows(1, ImmutableMap.of(
                        TableConstants.BillingPaymentsAndTransactionsGB.TYPE.getName(), BillingConstants.PaymentsAndOtherTransactionTypeGB.ACCOUNT_SUSPENSE,
                        TableConstants.BillingPaymentsAndTransactionsGB.AMOUNT.getName(), new Currency("200").negate().toString()));

        LOGGER.info("FULL ADMIN: Step 1");
        navigateToBillingAccount(fullAdminMasterPolicyNumber);
        billingAccount.generateFutureStatement().perform();
        String fullAdminInvoice = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
        assertSoftly(softly -> {
            checkInvoiceStatus(softly, fullAdminInvoice, BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL);
            checkAcceptPaymentBamMessage(fullAdminCoverageModalPremium, fullAdminInvoice, softly);
        });

        LOGGER.info("FULL ADMIN: Step 2");
        billingAccount.discardBill().perform(new SimpleDataProvider());
        assertSoftly(softly -> {
            checkInvoiceStatus(softly, fullAdminInvoice, BillingConstants.BillsAndStatementsStatusGB.DISCARDED);
            billingAccount.regenerateBill().perform(new SimpleDataProvider());
            String fullAdminInvoiceAfterRegenerate = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
            checkInvoiceStatus(softly, fullAdminInvoiceAfterRegenerate, BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL);
            checkAcceptPaymentBamMessage(fullAdminCoverageModalPremium, fullAdminInvoiceAfterRegenerate, softly);
            checkRegenerateInvoiceBamMessage(fullAdminInvoice, softly);

            LOGGER.info("FULL ADMIN: Step 3");
            billingAccount.discardBill().perform(new SimpleDataProvider());
            checkInvoiceStatus(softly, fullAdminInvoiceAfterRegenerate, BillingConstants.BillsAndStatementsStatusGB.DISCARDED);
            mainApp().close();

            TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).plusMonths(1).with(DateTimeUtils.nextWorkingDay));
            LOGGER.info("Run job 'benefits.billingInvoiceJob'");
            JobRunner.executeJob(GeneralSchedulerPage.BENEFITS_BILLING_INVOICE_JOB);

            mainApp().open();
            navigateToBillingAccount(fullAdminMasterPolicyNumber);
            String fullAdminInvoiceAfterBillingInvoiceJob = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
            String fullAdminRegenerateInvoiceAfterBillingInvoiceJob = BillingSummaryPage.tableBillsAndStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
            checkRegenerateInvoiceBamMessage(fullAdminRegenerateInvoiceAfterBillingInvoiceJob, softly);
            Arrays.asList(
                    fullAdminInvoiceAfterBillingInvoiceJob,
                    fullAdminRegenerateInvoiceAfterBillingInvoiceJob).forEach(invoice -> {
                checkInvoiceStatus(softly, invoice, BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL);
                checkAcceptPaymentBamMessage(fullAdminCoverageModalPremium, invoice, softly);
            });
        });
    }

    private void checkRegenerateInvoiceBamMessage(String fullAdminInvoice, ETCSCoreSoftAssertions softly) {
        softly.assertThat(BillingAccountsListPage.activitiesAndUserNotes)
                .hasMatchingRows(1, ImmutableMap.of(
                        "Description", String.format(BamConstants.REGENERATE_INVOICE, fullAdminInvoice),
                        "Status", BamConstants.FINISHED));
        BillingSummaryPage.closeActivitiesAndUserNotes();
    }

    private void checkAcceptPaymentBamMessage(Currency fullAdminCoverageModalPremium, String fullAdminInvoice, ETCSCoreSoftAssertions softly) {
        BillingSummaryPage.openActivitiesAndUserNotes();
        softly.assertThat(BillingAccountsListPage.activitiesAndUserNotes)
                .hasMatchingRows(1, ImmutableMap.of(
                        "Description", String.format(BamConstants.ACCEPT_PAYMENT_FINISHED, fullAdminCoverageModalPremium.toString(), fullAdminInvoice),
                        "Status", BamConstants.FINISHED));
        BillingSummaryPage.closeActivitiesAndUserNotes();
    }

    private void checkInvoiceStatus(ETCSCoreSoftAssertions softly, String fullAdminInvoice, String status) {
        softly.assertThat(BillingSummaryPage.tableBillsAndStatements)
                .hasMatchingRows(1, ImmutableMap.of(
                        TableConstants.BillingBillsAndStatementsGB.INVOICE.getName(), fullAdminInvoice,
                        TableConstants.BillingBillsAndStatementsGB.STATUS.getName(), status));
    }
}
