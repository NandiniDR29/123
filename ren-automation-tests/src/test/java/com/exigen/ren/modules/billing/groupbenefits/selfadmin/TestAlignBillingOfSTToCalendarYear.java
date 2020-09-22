/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.BamConstants.*;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAlignBillingOfSTToCalendarYear extends GroupBenefitsBillingBaseTest implements BillingAccountContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-27927", component = BILLING_GROUPBENEFITS)
    public void testAlignBillingOfSTToCalendarYear() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        TestData tdMP2 = tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(tdSpecific().getTestData("TestData_Issue_Annual"));
        statutoryDisabilityInsuranceMasterPolicy.createPolicy(tdMP2);
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

        navigateToBillingAccount(policyNum);
        LOGGER.info("---=={Step 2}==---");
        String covEffDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        String policyEffDate = TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).format(DateTimeUtils.MM_DD_YYYY);
        String dueDate = TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).plusMonths(1).format(DateTimeUtils.MM_DD_YYYY);
        int monthCount = 13 - TimeSetterUtil.getInstance().getCurrentTime().getMonthValue();

        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tableBillableCoverages.getHeader().getValue()).contains(TableConstants.BillingBillableCoveragesGB.COVERAGE_EFF_DATE.getName());
            BillingSummaryPage.tableBillableCoverages.getRows().forEach(row -> {
                softly.assertThat(row.getCell(TableConstants.BillingBillableCoveragesGB.COVERAGE_EFF_DATE.getName())).hasValue(covEffDate);
                softly.assertThat(row.getCell(TableConstants.BillingBillableCoveragesGB.MASTER_POLICY_EFF_DATE.getName())).hasValue(policyEffDate);
            });
        });

        LOGGER.info("---=={Step 3}==---");
        billingAccount.generateFutureStatement().perform();
        String invoiceNum = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED).hasMatchingRows(1);

        String nextRenEffDate = TimeSetterUtil.getInstance().getCurrentTime().with(TemporalAdjusters.firstDayOfNextYear()).minusDays(1).format(DateTimeUtils.MM_DD_YYYY);
        String billingPeriod = String.format("%s - %s", policyEffDate, nextRenEffDate);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TableConstants.BillingBillsAndStatementsGB.BILLING_PERIOD.getName())).hasValue(billingPeriod);

        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNum);
        assertThat(BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getTotalRowsCount()).isEqualTo(monthCount*6);

        LOGGER.info("---=={Step 4}==---");
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(policyNum), String.format(INVOICE_GENERATE, invoiceNum, dueDate, billingPeriod), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(policyNum), String.format(INVOICE_SUCCESSFULLY_GENERATED, invoiceNum, dueDate, billingPeriod), FINISHED);
    }


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-27927", component = BILLING_GROUPBENEFITS)
    public void testAlignBillingOfSTToCalendarYearQuarterly() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        TestData tdMP2 = tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(tdSpecific().getTestData("TestData_Issue_Quarterly"));
        statutoryDisabilityInsuranceMasterPolicy.createPolicy(tdMP2);
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

        navigateToBillingAccount(policyNum);

        LOGGER.info("---=={Step 2}==---");
        String covEffDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        LocalDateTime policyEffDate = TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1);
        String policyEffDateString = policyEffDate.format(DateTimeUtils.MM_DD_YYYY);

        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tableBillableCoverages.getHeader().getValue()).contains(TableConstants.BillingBillableCoveragesGB.COVERAGE_EFF_DATE.getName());
            BillingSummaryPage.tableBillableCoverages.getRows().forEach(row -> {
                softly.assertThat(row.getCell(TableConstants.BillingBillableCoveragesGB.COVERAGE_EFF_DATE.getName())).hasValue(covEffDate);
                softly.assertThat(row.getCell(TableConstants.BillingBillableCoveragesGB.MASTER_POLICY_EFF_DATE.getName())).hasValue(policyEffDateString);
            });
        });

        LOGGER.info("---=={Step 3.1}==---");
        billingAccount.generateFutureStatement().perform();

        String invoiceNum = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        String dueDate = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(DUE_DATE).getValue();
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED).hasMatchingRows(1);

        LOGGER.info("---=={Step 3.2}==---");
        LocalDateTime firstDayOfQuarter = TimeSetterUtil.getInstance().getCurrentTime().with(TimeSetterUtil.getInstance().getCurrentTime().getMonth().firstMonthOfQuarter())
                .with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime lastDayOfFirstQuarter = firstDayOfQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        String lastDayOfQuarterString = lastDayOfFirstQuarter.format(DateTimeUtils.MM_DD_YYYY);
        String billingPeriod = String.format("%s - %s", policyEffDateString, lastDayOfQuarterString);
        assertThat(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TableConstants.BillingBillsAndStatementsGB.BILLING_PERIOD.getName())).hasValue(billingPeriod);

        LOGGER.info("---=={Step 3.3}==---");
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNum);
        int monthCount = Math.toIntExact(ChronoUnit.MONTHS.between(policyEffDate, lastDayOfFirstQuarter) + 1);
        assertThat(BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getTotalRowsCount()).isEqualTo(monthCount*6);

        LOGGER.info("---=={Step 7}==---");
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(policyNum), String.format(INVOICE_GENERATE, invoiceNum, dueDate, billingPeriod), FINISHED);
        BillingAccountsListPage.verifyBamActivities(getBillingAccountNumber(policyNum), String.format(INVOICE_SUCCESSFULLY_GENERATED, invoiceNum, dueDate, billingPeriod), FINISHED);

        LOGGER.info("---=={Step 4}==---");
        LocalDateTime secondQuarterLastDay = verifyQuarter(lastDayOfFirstQuarter.plusDays(1));

        LOGGER.info("---=={Step 5}==---");
        LocalDateTime thirdQuarterLastDay = verifyQuarter(secondQuarterLastDay.plusDays(1));

        LOGGER.info("---=={Step 6}==---");
        verifyQuarter(thirdQuarterLastDay.plusDays(1));
    }

    private LocalDateTime verifyQuarter(LocalDateTime firstDayOfQuarter) {

        billingAccount.generateFutureStatement().perform();
        String invoiceNum = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        String dueDate = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(DUE_DATE).getValue();
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNum)
                .with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED).hasMatchingRows(1);

        LocalDateTime lastDayOfQuarter = firstDayOfQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        String billingPeriod = String.format("%s - %s", firstDayOfQuarter.format(DateTimeUtils.MM_DD_YYYY), lastDayOfQuarter.format(DateTimeUtils.MM_DD_YYYY));
        assertThat(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TableConstants.BillingBillsAndStatementsGB.BILLING_PERIOD.getName()))
                .hasValue(billingPeriod);

        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNum);
        int monthCount = Math.toIntExact(ChronoUnit.MONTHS.between(firstDayOfQuarter, lastDayOfQuarter) + 1);
        assertThat(BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getTotalRowsCount()).isEqualTo(monthCount * 6);

        LOGGER.info("---=={Step 7}==---");
        BillingAccountsListPage.verifyBamActivities(BillingAccountsListPage.getAccountNumber(), String.format(INVOICE_GENERATE, invoiceNum, dueDate, billingPeriod), FINISHED);
        BillingAccountsListPage.verifyBamActivities(BillingAccountsListPage.getAccountNumber(), String.format(INVOICE_SUCCESSFULLY_GENERATED, invoiceNum, dueDate, billingPeriod), FINISHED);

        return lastDayOfQuarter;
    }
}