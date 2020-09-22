/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin;


import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.GenerateDraftBillActionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.enums.BamConstants.*;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.components.Components.CASE_PROFILE;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBAMToDistinguishBillsGeneratedFullAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-15737", component = BILLING_GROUPBENEFITS)
    public void testUpdateBillingAccountFullAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        caseProfile.create(caseProfile.getDefaultTestData(CASE_PROFILE, DEFAULT_TEST_DATA_KEY), groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "TestData_OnTime").resolveLinks()));

        masterPolicyNumber.set(PolicySummaryPage.labelPolicyNumber.getValue());

        groupAccidentCertificatePolicy.createPolicy((groupAccidentCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks())));

        navigateToBillingAccount(masterPolicyNumber.get());

        ImmutableList<String> listDates = getListDateValues();

        LOGGER.info("---=={Step 1}==---");
        billingAccount.generateFutureStatement().start();
        Page.dialogConfirmation.buttonCancel.click();
        BillingAccountsListPage.verifyBamActivities(1, String.format(INVOICE_GENERATE_CANCEL, listDates.get(0), String.format("%s - %s", listDates.get(1), listDates.get(2))), CANCELLED);

        LOGGER.info("---=={Step 2}==---");
        billingAccount.generateFutureStatement().perform(new SimpleDataProvider());
        String invoiceNum1 = BillingSummaryPage.getInvoiceNumberByRowNum(1);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(INVOICE_SUCCESSFULLY_GENERATED, invoiceNum1, listDates.get(0), String.format("%s - %s", listDates.get(1), listDates.get(2))), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(INVOICE_GENERATE, invoiceNum1, listDates.get(0), String.format("%s - %s", listDates.get(1), listDates.get(2))), FINISHED);

        LOGGER.info("---=={Step 3}==---");
        billingAccount.generateFutureStatement().perform(new SimpleDataProvider());
        String invoiceNum2 = BillingSummaryPage.getInvoiceNumberByRowNum(1);
        billingAccount.discardBill().perform(new SimpleDataProvider());

        LOGGER.info("---=={Step 4}==---");
        billingAccount.regenerateBill().start();
        Page.dialogConfirmation.buttonCancel.click();
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), REGENERATE_INVOICE_WITHOUT, CANCELLED);

        LOGGER.info("---=={Step 5}==---");
        ImmutableList<String> listDates2 = getListDateValues();
        billingAccount.regenerateBill().perform(new SimpleDataProvider());
        String invoiceNum3 = BillingSummaryPage.getInvoiceNumberByRowNum(1);
        navigateToBillingAccount(masterPolicyNumber.get());
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(INVOICE_SUCCESSFULLY_GENERATED, invoiceNum3, listDates2.get(0), String.format("%s - %s", listDates2.get(1), listDates2.get(2))), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(masterPolicyNumber.get()), String.format(REGENERATE_INVOICE, invoiceNum2), FINISHED);

        LOGGER.info("---=={Step 6}==---");
        billingAccount.generateFutureStatement().perform(new SimpleDataProvider());
        billingAccount.discardBill().perform(new SimpleDataProvider());
        ImmutableList<String> listDates3 = getListDateValues();

        LOGGER.info("---=={Step 7}==---");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).plusMonths(3).with(DateTimeUtils.nextWorkingDay));

        LOGGER.info("Run job 'benefits.billingInvoiceJob'");
        JobRunner.executeJob(GeneralSchedulerPage.BENEFITS_BILLING_INVOICE_JOB);
        mainApp().reopen();
        navigateToBillingAccount(masterPolicyNumber.get());
        assertThat(BillingSummaryPage.tableBillsAndStatements).hasRows(6);
        String invoiceNum4 = BillingSummaryPage.getInvoiceNumberByRowNum(2);
        String invoiceNum5 = BillingSummaryPage.getInvoiceNumberByRowNum(1);

        assertSoftly(softly -> {
            String dueDate = LocalDate.parse(listDates3.get(0), DateTimeUtils.MM_DD_YYYY).atStartOfDay().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeUtils.MM_DD_YYYY);
            String periodStart = LocalDate.parse(listDates3.get(1), DateTimeUtils.MM_DD_YYYY).atStartOfDay().plusMonths(1).format(DateTimeUtils.MM_DD_YYYY);
            String periodEnd = LocalDate.parse(listDates3.get(2), DateTimeUtils.MM_DD_YYYY).atStartOfDay().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).format(DateTimeUtils.MM_DD_YYYY);

            BillingAccountsListPage.verifyBamActivities(String.format(INVOICE_SUCCESSFULLY_GENERATED, invoiceNum5, dueDate, String.format("%s - %s", periodStart, periodEnd)), FINISHED);
            BillingAccountsListPage.verifyBamActivities(String.format(INVOICE_SUCCESSFULLY_GENERATED, invoiceNum4, listDates3.get(0), String.format("%s - %s", listDates3.get(1), listDates3.get(2))), FINISHED);
            BillingAccountsListPage.verifyBamActivities(String.format(INVOICE_GENERATE, invoiceNum5, dueDate, String.format("%s - %s", periodStart, periodEnd)), FINISHED);
            BillingAccountsListPage.verifyBamActivities(String.format(REGENERATE_INVOICE, invoiceNum4), FINISHED);

        });
    }

    private ImmutableList<String> getListDateValues() {
        billingAccount.generateDraftBill().start();
        String[] parsedTextArray = GenerateDraftBillActionTab.draftBillPopupPanel.getValue().split(" ");
        String dueDate = parsedTextArray[3];
        String periodStart = parsedTextArray[6];
        String periodEnd = parsedTextArray[8];
        Page.dialogConfirmation.buttonCancel.click();
        return ImmutableList.of(dueDate, periodStart, periodEnd);
    }
}