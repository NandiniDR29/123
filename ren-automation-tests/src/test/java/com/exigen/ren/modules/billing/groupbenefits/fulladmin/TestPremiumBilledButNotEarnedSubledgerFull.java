/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.common.CancellationActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.ADD_INVOICING_CALENDAR;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.INVOICING_CALENDAR;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.InvoicingCalendarTab.*;
import static com.exigen.ren.main.modules.policy.common.metadata.common.CancellationActionTabMetaData.CANCEL_DATE;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CertificatePolicyTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.INCLUDE_OPTIONAL_BENEFITS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPremiumBilledButNotEarnedSubledgerFull extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-34570", component = BILLING_GROUPBENEFITS)
    public void testPremiumBilledButNotEarnedSubledgerFull() {
        LocalDateTime effectiveDate = DateTimeUtils.getCurrentDateTime().plusMonths(1);

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        TestData td = groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_TwoCoveragesNonContributory")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(optionalBenefitTab.getMetaKey()+"[0]", INCLUDE_OPTIONAL_BENEFITS.getLabel()), "No")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithCustomCalendar"));

        td.adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName()),
                ImmutableList.of(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "BillingAccountTabWithCustomCalendar")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), ADD_INVOICING_CALENDAR.getLabel(), CALENDAR_NAME.getLabel()), "INC1")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), ADD_INVOICING_CALENDAR.getLabel(), INVOICING_FREQUENCY.getLabel()), "Monthly")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), ADD_INVOICING_CALENDAR.getLabel(), INVOICING_RULE.getLabel()), "In Advance")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), ADD_INVOICING_CALENDAR.getLabel(), INVOICE_DUE_DAY.getLabel()), "31")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), ADD_INVOICING_CALENDAR.getLabel(), BILLING_PERIOD_OFFSET.getLabel()), "1")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), ADD_INVOICING_CALENDAR.getLabel(), CALENDAR_NAME.getLabel()), "INC1")
                        .adjust(TestData.makeKeyPath(CREATE_NEW_BILLING_ACCOUNT.getLabel(), INVOICING_CALENDAR.getLabel()), "INC1")));

        createPolicy(td);

        groupAccidentCertificatePolicy.createPolicy(groupAccidentCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY))
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));
        certificatePolicyNumber.set(PolicySummaryPage.labelPolicyNumber.getValue());
        PolicySummaryPage.linkMasterPolicy.click();

        groupAccidentCertificatePolicy.createPolicy(groupAccidentCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY))
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(DATA_GATHER, "CoveragesTab_PlanEnhanced").resolveLinks())
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));
        String policyNumberCP1 = PolicySummaryPage.labelPolicyNumber.getValue();
        PolicySummaryPage.linkMasterPolicy.click();

        String policyNumberMP1 = PolicySummaryPage.labelPolicyNumber.getValue();

        firstCoverageModalPremium.set(new Currency(getModalPremiumAmountFromCoverageSummary(1)));
        secondCoverageModalPremium.set(new Currency(getModalPremiumAmountFromCoverageSummary(2)));
        navigateToBillingAccount(policyNumberMP1);

        LOGGER.info("---=={Step 1}==---");
        billingAccount.generateFutureStatement().perform();

        navigateToBillingAccount(policyNumberMP1);
        String billingPeriod = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String startDate = LocalDate.parse(billingPeriod.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDate = LocalDate.parse(billingPeriod.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        LOGGER.info("---=={Step 1}==---");
        String sql = "select txDate, DTYPE, entryAmt, entryType, ledgerAccountNo, countyCd, countryCd, underwriterCd, stateProvCd, stateProvinceCd, transactionType, periodStart, periodEnd, productNumber from LedgerEntry where ledgerAccountNo = '2032' and productNumber = '%s' order by txDate desc;";
        List<Map<String, String>> dbResponseList = dbService.getRows(String.format(sql, policyNumberMP1));

        Map<String, String> cov1 = dbResponseList.stream().filter(map -> map.get("entryAmt").equals(firstCoverageModalPremium.get().toPlainString())).findFirst()
              .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", firstCoverageModalPremium.get().toPlainString())));

        String valueCreateDate = cov1.get("txDate");

        Map<String, String> cov2 = dbResponseList.stream().filter(map -> map.get("entryAmt").equals(secondCoverageModalPremium.get().toPlainString())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", secondCoverageModalPremium.get().toPlainString())));

        ImmutableList.of(cov1, cov2).forEach(map -> {
            assertSoftly(softly -> {
                softly.assertThat(map.get("entryType")).isEqualTo("CREDIT");
                softly.assertThat(map.get("underwriterCd")).isEqualTo("RLHICA");
                softly.assertThat(map.get("stateProvCd")).isEqualTo("GA");
                softly.assertThat(map.get("countryCd")).isEqualTo("US");
                softly.assertThat(map.get("periodStart")).contains(startDate);
                softly.assertThat(map.get("periodEnd")).contains(endDate);
            });
        });

        mainApp().close();

        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_UNEARNED_AMORTIZATION_JOB);
        mainApp().reopen();

        LOGGER.info("---=={Step 2}==---");
        navigateToBillingAccount(policyNumberMP1);
        billingAccount.discardBill().perform(new SimpleDataProvider());

        String sqlStep2 = "select txDate, DTYPE, entryAmt, entryType, ledgerAccountNo, countyCd, countryCd, underwriterCd, stateProvCd, stateProvinceCd, transactionType, periodStart, periodEnd, productNumber from LedgerEntry where entryType = 'DEBIT' and ledgerAccountNo = '2032' and productNumber = '%s' order by txDate desc;";
        List<Map<String, String>> dbResponseListStep2 = dbService.getRows(String.format(sqlStep2, policyNumberMP1));

        Map<String, String> cov1Step2 = dbResponseListStep2.stream().filter(map -> map.get("entryAmt").equals(firstCoverageModalPremium.get().toPlainString())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", firstCoverageModalPremium.get().toPlainString())));

        Map<String, String> cov2Step2 = dbResponseListStep2.stream().filter(map -> map.get("entryAmt").equals(secondCoverageModalPremium.get().toPlainString())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", secondCoverageModalPremium.get().toPlainString())));

        ImmutableList.of(cov1Step2, cov2Step2).forEach(map -> {
            assertSoftly(softly -> {
                softly.assertThat(map.get("underwriterCd")).isEqualTo("RLHICA");
                softly.assertThat(map.get("stateProvCd")).isEqualTo("GA");
                softly.assertThat(map.get("countryCd")).isEqualTo("US");
                softly.assertThat(map.get("periodStart")).contains(startDate);
                softly.assertThat(map.get("periodEnd")).contains(endDate);
            });
        });

        LOGGER.info("---=={Step 3}==---");
        billingAccount.regenerateBill().perform(new SimpleDataProvider());

        String billingPeriodStep3 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String startDateStep3 = LocalDate.parse(billingPeriodStep3.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDateStep3 = LocalDate.parse(billingPeriodStep3.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);
        navigateToBillingAccount(policyNumberMP1);

        String sqlStep3 = "select txDate, DTYPE, entryAmt, entryType, ledgerAccountNo, countyCd, countryCd, underwriterCd, stateProvCd, stateProvinceCd, transactionType, periodStart, periodEnd, productNumber from LedgerEntry " +
                "where entryType = 'CREDIT' and ledgerAccountNo = '2032'and productNumber = '%s' and txDate > '%s' order by txDate desc;";
        List<Map<String, String>> dbResponseListStep3 = dbService.getRows(String.format(sqlStep3, policyNumberMP1, valueCreateDate));

        Map<String, String> cov1Step3 = dbResponseListStep3.stream().filter(map -> map.get("entryAmt").equals(firstCoverageModalPremium.get().toPlainString())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", firstCoverageModalPremium.get().toPlainString())));

        String valueCreateDateStep3 = cov1Step3.get("txDate");
        Map<String, String> cov2Step3 = dbResponseListStep3.stream().filter(map -> map.get("entryAmt").equals(secondCoverageModalPremium.get().toPlainString())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", secondCoverageModalPremium.get().toPlainString())));

        ImmutableList.of(cov1Step3, cov2Step3).forEach(map -> {
            assertSoftly(softly -> {
                softly.assertThat(map.get("underwriterCd")).isEqualTo("RLHICA");
                softly.assertThat(map.get("stateProvCd")).isEqualTo("GA");
                softly.assertThat(map.get("countryCd")).isEqualTo("US");
                softly.assertThat(map.get("periodStart")).contains(startDateStep3);
                softly.assertThat(map.get("periodEnd")).contains(endDateStep3);
            });
        });

        LOGGER.info("---=={Step 4}==---");
        MainPage.QuickSearch.search(policyNumberMP1);
        groupAccidentMasterPolicy.createEndorsement(groupAccidentMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(StartEndorsementActionTab.class.getSimpleName(), StartEndorsementActionTabMetaData.ENDORSEMENT_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ENDORSEMENT, "TestData_EMPTY_withBasicBenefitsTab")
                        .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey(), PLAN.getLabel()), PolicyConstants.PlanAccident.BASE_BUY_UP).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, "TestDataWithExistingBA").resolveLinks()));

        Currency endorseFirstPremium = new Currency(getModalPremiumAmountFromCoverageSummary(1));
        MainPage.QuickSearch.search(policyNumberCP1);
        groupAccidentCertificatePolicy.cancel().perform(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CancellationActionTab.class.getSimpleName(), CANCEL_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY)));

        navigateToBillingAccount(policyNumberMP1);
        billingAccount.generateFutureStatement().perform();
        String billingPeriodStep4 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String startDateStep4 = LocalDate.parse(billingPeriodStep4.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDateStep4 = LocalDate.parse(billingPeriodStep4.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        String sqlStep4 = "select txDate, DTYPE, entryAmt, entryType, ledgerAccountNo, countyCd, countryCd, underwriterCd, stateProvCd, stateProvinceCd, transactionType, periodStart, periodEnd, productNumber from LedgerEntry " +
                "where entryType = 'CREDIT' and ledgerAccountNo = '2032'and productNumber = '%s' and txDate > '%s' order by txDate desc;";
        List<Map<String, String>> dbResponseListStep4 = dbService.getRows(String.format(sqlStep4, policyNumberMP1, valueCreateDateStep3));

        Map<String, String> cov1Step4 = dbResponseListStep4.stream().filter(map -> map.get("entryAmt").equals(endorseFirstPremium.toPlainString())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", endorseFirstPremium.toPlainString())));
        String valueCreateDateStep4 = cov1Step4.get("txDate");

        assertSoftly(softly -> {
            softly.assertThat(cov1Step4.get("entryType")).isEqualTo("CREDIT");
            softly.assertThat(cov1Step4.get("underwriterCd")).isEqualTo("RLHICA");
            softly.assertThat(cov1Step4.get("stateProvCd")).isEqualTo("GA");
            softly.assertThat(cov1Step4.get("countryCd")).isEqualTo("US");
            softly.assertThat(cov1Step4.get("periodStart")).contains(startDateStep4);
            softly.assertThat(cov1Step4.get("periodEnd")).contains(endDateStep4);
        });

        LOGGER.info("---=={Step 5}==---");
        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(effectiveDate);
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_UNEARNED_AMORTIZATION_JOB);
        mainApp().reopen();

        navigateToBillingAccount(policyNumberMP1);
        LOGGER.info("Verify values with Subledger Transactions = 2030");
        String sql2030 = "select txDate, DTYPE, entryAmt, entryType, ledgerAccountNo, countyCd, countryCd, underwriterCd, stateProvCd, stateProvinceCd, transactionType, periodStart, periodEnd, productNumber from LedgerEntry " +
                "where ledgerAccountNo = '2030' and productNumber = '%s' and txDate > '%s' order by txDate desc;";
        List<Map<String, String>> dbResponseList2030 = dbService.getRows(String.format(sql2030, policyNumberMP1, valueCreateDateStep4));
        assertThat(dbResponseList2030).hasSize(3);

        Map<String, String> cov1Credit = dbResponseList2030.stream()
                .filter(map -> map.get("entryAmt").equals(endorseFirstPremium.toPlainString()) && map.get("entryType").equals("CREDIT"))
                .findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", endorseFirstPremium.toPlainString())));

        verify(dbResponseList2030, cov1Credit, startDateStep3, endDateStep3);

        LOGGER.info("Verify values with Subledger Transactions = 2032");
        String sql2032 = "select txDate, DTYPE, entryAmt, entryType, ledgerAccountNo, countyCd, countryCd, underwriterCd, stateProvCd, stateProvinceCd, transactionType, periodStart, periodEnd, productNumber from LedgerEntry " +
                "where ledgerAccountNo = '2032' and productNumber = '%s' and txDate > '%s' order by txDate desc;";
        List<Map<String, String>> dbResponseList2032 = dbService.getRows(String.format(sql2032, policyNumberMP1, valueCreateDateStep4));
        assertThat(dbResponseList2032).hasSize(3);

        Map<String, String> cov1Debit = dbResponseList2032.stream()
                .filter(map -> map.get("entryAmt").equals(endorseFirstPremium.toPlainString()) && map.get("entryType").equals("DEBIT"))
                .findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", endorseFirstPremium.toPlainString())));

        verify(dbResponseList2032, cov1Debit, startDateStep3, endDateStep3);
        String valueCreateDateStep5 = cov1Debit.get("txDate");
        LOGGER.info("---=={Step 6}==---");
        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(effectiveDate.plusMonths(1));
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_UNEARNED_AMORTIZATION_JOB);
        mainApp().reopen();

        String sql2030Step6 = "select txDate, DTYPE, entryAmt, entryType, ledgerAccountNo, countyCd, countryCd, underwriterCd, stateProvCd, stateProvinceCd, transactionType, periodStart, periodEnd, productNumber from LedgerEntry " +
                "where ledgerAccountNo in (2030, 2032) and productNumber = '%s' and txDate > '%s' order by txDate desc;";
        List<Map<String, String>> dbResponseList2030Step6 = dbService.getRows(String.format(sql2030Step6, policyNumberMP1, valueCreateDateStep5));

        assertThat(dbResponseList2030Step6).hasSize(2);

        Map<String, String> cov1DebitStep6 = dbResponseList2030Step6.stream()
                .filter(map -> map.get("entryAmt").equals(endorseFirstPremium.toPlainString()) && map.get("entryType").equals("DEBIT"))
                .findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", secondCoverageModalPremium.get().toPlainString())));

        Map<String, String> cov1CreditStep6 = dbResponseList2030Step6.stream()
                .filter(map -> map.get("entryAmt").equals(endorseFirstPremium.toPlainString()) && map.get("entryType").equals("CREDIT"))
                .findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", secondCoverageModalPremium.get().toPlainString())));

        ImmutableList.of(cov1DebitStep6, cov1CreditStep6).forEach(map -> {
            assertSoftly(softly -> {
                softly.assertThat(map.get("underwriterCd")).isEqualTo("RLHICA");
                softly.assertThat(map.get("stateProvCd")).isEqualTo("GA");
                softly.assertThat(map.get("countryCd")).isEqualTo("US");
                softly.assertThat(map.get("periodStart")).contains(startDateStep4);
                softly.assertThat(map.get("periodEnd")).contains(endDateStep4);
            });
        });
    }

    private void verify(List<Map<String, String>> dbResponseList, Map<String, String>  cov1Credit, String startDate, String endDate) {

        Map<String, String> cov1Debit = dbResponseList.stream()
                .filter(map -> map.get("entryAmt").equals(secondCoverageModalPremium.get().toPlainString()) && map.get("entryType").equals("DEBIT"))
                .findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", secondCoverageModalPremium.get().toPlainString())));

        Map<String, String> cov2Credit = dbResponseList.stream()
                .filter(map -> map.get("entryAmt").equals(secondCoverageModalPremium.get().toPlainString()) && map.get("entryType").equals("DEBIT"))
                .findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with amount: %s not found", secondCoverageModalPremium.get().toPlainString())));

        ImmutableList.of(cov1Debit, cov2Credit, cov1Credit).forEach(map -> {
            assertSoftly(softly -> {
                softly.assertThat(map.get("underwriterCd")).isEqualTo("RLHICA");
                softly.assertThat(map.get("stateProvCd")).isEqualTo("GA");
                softly.assertThat(map.get("countryCd")).isEqualTo("US");
                softly.assertThat(map.get("periodStart")).contains(startDate);
                softly.assertThat(map.get("periodEnd")).contains(endDate);
            });
        });
    }
}