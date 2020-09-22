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
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.account.tabs.ManageInvoicingCalendarsActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.common.CancellationActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCoverageSummaryTable.MODAL_PREMIUM;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.INVOICING_CALENDAR;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData.INVOICE_DUE_DAY;
import static com.exigen.ren.main.modules.billing.account.metadata.ManageInvoicingCalendarsActionTabMetaData.LIST_BILL;
import static com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData.APPLICABLE_PAYMENT_MODES;
import static com.exigen.ren.main.modules.policy.common.metadata.common.CancellationActionTabMetaData.CANCEL_DATE;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_DATE;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CertificatePolicyTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HEALTH_SCREENING_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.HealthScreeningBenefitMetadata.APPLY_BENEFIT_HSB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.WAIVER_OF_PREMIUM_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.OptionalBenefitTabMetaData.WaiverOfPremiumBenefitMetadata.APPLY_BENEFIT_WPB;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.components.Components.CASE_PROFILE;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestPremiumEarnedButNotBilledSubledger extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    private String QUERY = "select txDate, DTYPE, entryAmt, " +
            "entryType, ledgerAccountNo, countyCd, countryCd, " +
            "underwriterCd, stateProvCd, stateProvinceCd, " +
            "producerCd, transactionType, periodStart, periodEnd, " +
            "productNumber from LedgerEntry where ledgerAccountNo in (2033, 2034) " +
            "and productNumber = '%s' order by txDate desc";

    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-40121", component = BILLING_GROUPBENEFITS)
    public void testPremiumEarnedButNotBilledSubledger() {
        LocalDateTime effectiveDate = DateTimeUtils.getCurrentDateTime().with(TemporalAdjusters.firstDayOfNextMonth());

        LocalDateTime periodStart1 = effectiveDate;
        LocalDateTime periodStart2 = effectiveDate.plusWeeks(1);
        LocalDateTime periodStart3 = effectiveDate.plusWeeks(2);
        LocalDateTime periodStart4 = effectiveDate.plusWeeks(3);

        LocalDateTime periodEnd1 = effectiveDate.plusWeeks(1).minusDays(1);
        LocalDateTime periodEnd2 = effectiveDate.plusWeeks(2).minusDays(1);
        LocalDateTime periodEnd3 = effectiveDate.plusWeeks(3).minusDays(1);
        LocalDateTime periodEnd4 = effectiveDate.plusWeeks(4).minusDays(1);

        mainApp().open();

        createDefaultNonIndividualCustomer();

        caseProfile.create(caseProfile.getDefaultTestData(CASE_PROFILE, DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getClass().getSimpleName(), APPLICABLE_PAYMENT_MODES.getLabel()), ImmutableList.of("12", "52")),
                groupAccidentMasterPolicy.getType());

        billingAccount.navigateToBillingAccount();
        billingAccount.addManageInvoicingCalendars().perform(billingAccount.getDefaultTestData("CreateCalendars", "TestDataSelfAdmin")
                .adjust(TestData.makeKeyPath(ManageInvoicingCalendarsActionTab.class.getSimpleName() + "[0]", INVOICE_DUE_DAY.getLabel()), "31")
                .adjust(TestData.makeKeyPath(ManageInvoicingCalendarsActionTab.class.getSimpleName() + "[0]", LIST_BILL.getLabel()), "true"));

        createPolicy(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()), "52")
                .adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", CREATE_NEW_BILLING_ACCOUNT.getLabel(), INVOICING_CALENDAR.getLabel()), "contains=CalendarIC1"));

        groupAccidentCertificatePolicy.createPolicy((groupAccidentCertificatePolicy.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY))
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks())));

        certificatePolicyNumber.set(PolicySummaryPage.labelPolicyNumber.getValue());

        Currency invoice1Amount = new Currency(PolicySummaryPage.tablePremiumSummary.getRow(1).getCell(MODAL_PREMIUM).getValue());

        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.addHold().perform(billingAccount.getDefaultTestData("AddHold", DEFAULT_TEST_DATA_KEY));

        billingAccount.generateFutureStatement().perform();

        LOGGER.info("Test: Step 1");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(periodStart2);
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB);

        assertThat(dbService.getRows(String.format(QUERY, masterPolicyNumber.get()))).isEmpty();

        LOGGER.info("Test: Step 2");
        mainApp().reopen();
        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.removeHold().perform(new SimpleDataProvider());

        mainApp().close();
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB, true);

        List<Map<String, String>> dbResponseList_step2 = dbService.getRows(String.format(QUERY, masterPolicyNumber.get()));

        assertThat(dbResponseList_step2).hasSize(4);
        //  2033
        verifySubledgerTransaction(dbResponseList_step2, "2033", "CREDIT", periodStart1, periodEnd1, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step2, "2033", "CREDIT", periodStart2, periodEnd2, invoice1Amount);
        //  2034
        verifySubledgerTransaction(dbResponseList_step2, "2034", "DEBIT", periodStart1, periodEnd1, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step2, "2034", "DEBIT", periodStart2, periodEnd2, invoice1Amount);

        LOGGER.info("Test: Step 3");
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB, true);
        assertThat(dbService.getRows(String.format(QUERY, masterPolicyNumber.get()))).hasSize(4);

        LOGGER.info("Test: Step 4");
        TimeSetterUtil.getInstance().nextPhase(periodEnd4);  // to last day of Invoice1 Item4.
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB);

        List<Map<String, String>> dbResponseList_step4 = dbService.getRows(String.format(QUERY, masterPolicyNumber.get()));

        assertThat(dbResponseList_step4).hasSize(8);

        verifySubledgerTransaction(dbResponseList_step4, "2033", "CREDIT", periodStart1, periodEnd1, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step4, "2033", "CREDIT", periodStart2, periodEnd2, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step4, "2033", "CREDIT", periodStart3, periodEnd3, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step4, "2033", "CREDIT", periodStart4, periodEnd4, invoice1Amount);

        verifySubledgerTransaction(dbResponseList_step4, "2034", "DEBIT", periodStart1, periodEnd1, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step4, "2034", "DEBIT", periodStart2, periodEnd2, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step4, "2034", "DEBIT", periodStart3, periodEnd3, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step4, "2034", "DEBIT", periodStart4, periodEnd4, invoice1Amount);

        LOGGER.info("Test: Step 5");
        mainApp().reopen();
        MainPage.QuickSearch.search(masterPolicyNumber.get());

        // perform Flat Endorsement for MP1, lowering the Modal Premium
        groupAccidentMasterPolicy.endorse().start();
        groupAccidentMasterPolicy.endorse().getWorkspace().fill(groupAccidentMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(StartEndorsementActionTab.class.getSimpleName(), ENDORSEMENT_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY)));

        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();

        optionalBenefitTab.navigateToTab();

        optionalBenefitTab.getAssetList().getAsset(WAIVER_OF_PREMIUM_BENEFIT).getAsset(APPLY_BENEFIT_WPB).setValue(false);

        GroupAccidentMasterPolicyContext.premiumSummaryTab.navigateToTab().submitTab();

        PolicySummaryPage.buttonPendedEndorsement.click();
        groupAccidentMasterPolicy.issue().perform(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT));

        MainPage.QuickSearch.search(certificatePolicyNumber.get());
        groupAccidentCertificatePolicy.endorse().perform(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupAccidentCertificatePolicy.issue().perform(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        mainApp().close();
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB, true);
        mainApp().reopen();
        MainPage.QuickSearch.search(masterPolicyNumber.get());

        Currency endorsePremium = new Currency(getModalPremiumAmountFromCoverageSummary(1));
        Currency entryAmt = endorsePremium.subtract(invoice1Amount);

        List<Map<String, String>> dbResponseList_step5 = dbService.getRows(String.format(QUERY, masterPolicyNumber.get()));

        assertThat(dbResponseList_step5).hasSize(16);

        verifySubledgerTransaction(dbResponseList_step5, "2033", "DEBIT", periodStart1, periodEnd1, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step5, "2033", "DEBIT", periodStart2, periodEnd2, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step5, "2033", "DEBIT", periodStart3, periodEnd3, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step5, "2033", "DEBIT", periodStart4, periodEnd4, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step5, "2034", "CREDIT", periodStart1, periodEnd1, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step5, "2034", "CREDIT", periodStart2, periodEnd2, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step5, "2034", "CREDIT", periodStart3, periodEnd3, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step5, "2034", "CREDIT", periodStart4, periodEnd4, entryAmt.abs());

        LOGGER.info("Test: Step 6.1");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(periodEnd4.with(TemporalAdjusters.firstDayOfNextMonth()));  // to first day of Invoice2 Billing Period
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB);

        List<Map<String, String>> dbResponseList_step6_1 = dbService.getRows(String.format(QUERY, masterPolicyNumber.get()));

        assertThat(dbResponseList_step6_1).hasSize(18);

        verifySubledgerTransaction(dbResponseList_step6_1, "2033", "CREDIT", periodStart1, periodEnd1, invoice1Amount);
        // 2034
        verifySubledgerTransaction(dbResponseList_step6_1, "2034", "DEBIT", periodStart1, periodEnd1, invoice1Amount);

        LOGGER.info("Test: Step 6.2");
        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNumber.get());
        groupAccidentCertificatePolicy.cancel().perform(groupAccidentCertificatePolicy.getDefaultTestData(CANCELLATION, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CancellationActionTab.class.getSimpleName(), CANCEL_DATE.getLabel()), periodEnd4.with(TemporalAdjusters.firstDayOfNextMonth()).format(MM_DD_YYYY)));

        List<Map<String, String>> dbResponseList_step6_2 = dbService.getRows(String.format(QUERY, masterPolicyNumber.get()));

        assertThat(dbResponseList_step6_2).hasSize(20);

        LOGGER.info("Test: Step 6.3");
        groupAccidentCertificatePolicy.reinstate().perform(groupAccidentCertificatePolicy.getDefaultTestData(REINSTATEMENT, DEFAULT_TEST_DATA_KEY));

        mainApp().close();
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB, true);

        List<Map<String, String>> dbResponseList_step6_3 = dbService.getRows(String.format(QUERY, masterPolicyNumber.get()));

        assertThat(dbResponseList_step6_3).hasSize(22);

        LOGGER.info("Test: Step 7");
        mainApp().reopen();
        MainPage.QuickSearch.search(masterPolicyNumber.get());
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.generateFutureStatement().perform();

        mainApp().close();
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB, true);
        List<Map<String, String>> dbResponseList_step7 = dbService.getRows(String.format(QUERY, masterPolicyNumber.get()));

        assertThat(dbResponseList_step7).hasSize(38);
        // 4 Pre-Endorsement 2033
        verifySubledgerTransaction(dbResponseList_step7, "2033", "DEBIT", periodStart1, periodEnd1, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step7, "2033", "DEBIT", periodStart2, periodEnd2, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step7, "2033", "DEBIT", periodStart3, periodEnd3, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step7, "2033", "DEBIT", periodStart4, periodEnd4, invoice1Amount);
//        // 4 Post-Endorsement 2033
        verifySubledgerTransaction(dbResponseList_step7, "2033", "CREDIT", periodStart1, periodEnd1, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step7, "2033", "CREDIT", periodStart2, periodEnd2, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step7, "2033", "CREDIT", periodStart3, periodEnd3, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step7, "2033", "CREDIT", periodStart4, periodEnd4, entryAmt.abs());
//        // 4 Pre-Endorsement 2034
        verifySubledgerTransaction(dbResponseList_step7, "2034", "CREDIT", periodStart1, periodEnd1, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step7, "2034", "CREDIT", periodStart2, periodEnd2, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step7, "2034", "CREDIT", periodStart3, periodEnd3, invoice1Amount);
        verifySubledgerTransaction(dbResponseList_step7, "2034", "CREDIT", periodStart4, periodEnd4, invoice1Amount);
//        // 4 Post-Endorsement 2034
        verifySubledgerTransaction(dbResponseList_step7, "2034", "DEBIT", periodStart1, periodEnd1, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step7, "2034", "DEBIT", periodStart2, periodEnd2, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step7, "2034", "DEBIT", periodStart3, periodEnd3, entryAmt.abs());
        verifySubledgerTransaction(dbResponseList_step7, "2034", "DEBIT", periodStart4, periodEnd4, entryAmt.abs());

        LOGGER.info("Test: Step 8.1");
        TimeSetterUtil.getInstance().nextPhase(effectiveDate.plusWeeks(7));  // to the first day of Invoice2 Item4
        mainApp().reopen();
        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.generateFutureStatement().perform();

        List<Map<String, String>> dbResponseList_step8_2 = dbService.getRows(String.format(QUERY, masterPolicyNumber.get()));

        assertThat(dbResponseList_step8_2).hasSize(40);

        LOGGER.info("Test: Step 8.2");
        mainApp().close();
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB);

        assertThat(dbService.getRows(String.format(QUERY, masterPolicyNumber.get()))).hasSize(40);

        LOGGER.info("Test: Step 8.2.1");
        mainApp().reopen();
        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.discardBill().perform(new SimpleDataProvider());

        mainApp().close();
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB, true);

        assertThat(dbService.getRows(String.format(QUERY, masterPolicyNumber.get()))).hasSize(40);

        LOGGER.info("Test: Step 8.2.2");
        mainApp().reopen();
        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.regenerateBill().perform(new SimpleDataProvider());

        mainApp().close();
        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB, true);

        assertThat(dbService.getRows(String.format(QUERY, masterPolicyNumber.get()))).hasSize(40);

        LOGGER.info("Test: Step 9");
        mainApp().reopen();
        MainPage.QuickSearch.search(masterPolicyNumber.get());

        groupAccidentMasterPolicy.endorse().start();
        groupAccidentMasterPolicy.endorse().getWorkspace().fill(groupAccidentMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(StartEndorsementActionTab.class.getSimpleName(), ENDORSEMENT_DATE.getLabel()), effectiveDate.plusMonths(1).format(MM_DD_YYYY)));

        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();

        optionalBenefitTab.navigateToTab();

        optionalBenefitTab.getAssetList().getAsset(HEALTH_SCREENING_BENEFIT).getAsset(APPLY_BENEFIT_HSB).setValue(false);

        GroupAccidentMasterPolicyContext.premiumSummaryTab.navigateToTab().submitTab();

        PolicySummaryPage.buttonPendedEndorsement.click();
        groupAccidentMasterPolicy.issue().perform(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, EXISTENT_BILLING_ACCOUNT));

        MainPage.QuickSearch.search(certificatePolicyNumber.get());
        groupAccidentCertificatePolicy.endorse().perform(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupAccidentCertificatePolicy.issue().perform(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        Currency endorsePremium_2 = new Currency(PolicySummaryPage.tablePremiumSummary.getRow(1).getCell(MODAL_PREMIUM).getValue());

        LOGGER.info("Test: Step 9.1.1");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(effectiveDate.plusWeeks(9).plusDays(3));  // to middle of Invoice3 Item2

        JobRunner.executeJob(GeneralSchedulerPage.BILLING_BENEFITS_PREMIUM_EARNED_NOT_BILLED_JOB);

        LocalDateTime periodStartInvoice3_1 = effectiveDate.plusWeeks(8);
        LocalDateTime periodStartInvoice3_2 = effectiveDate.plusWeeks(9);

        LocalDateTime periodEndInvoice3_1 = effectiveDate.plusWeeks(9).minusDays(1);
        LocalDateTime periodEndInvoice3_2 = effectiveDate.plusWeeks(10).minusDays(1);

        List<Map<String, String>> dbResponseList_step9 = dbService.getRows(String.format(QUERY, masterPolicyNumber.get()));

        assertThat(dbResponseList_step9).hasSize(44);
        // 2 Invoice3 2033
        verifySubledgerTransaction(dbResponseList_step9, "2033", "CREDIT", periodStartInvoice3_1, periodEndInvoice3_1, endorsePremium_2);
        verifySubledgerTransaction(dbResponseList_step9, "2033", "CREDIT", periodStartInvoice3_2, periodEndInvoice3_2, endorsePremium_2);
        // 2 Invoice3 2034
        verifySubledgerTransaction(dbResponseList_step9, "2034", "DEBIT", periodStartInvoice3_1, periodEndInvoice3_1, endorsePremium_2);
        verifySubledgerTransaction(dbResponseList_step9, "2034", "DEBIT", periodStartInvoice3_2, periodEndInvoice3_2, endorsePremium_2);

        LOGGER.info("Test: Step 10");
        TimeSetterUtil.getInstance().nextPhase(effectiveDate.plusWeeks(12).minusDays(1).with(TemporalAdjusters.lastDayOfMonth()));  // to final day of Invoice3 Billing Period

        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNumber.get());
        groupAccidentCertificatePolicy.cancel().perform(groupAccidentCertificatePolicy.getDefaultTestData(CANCELLATION, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CancellationActionTab.class.getSimpleName(), CANCEL_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY)));

        List<Map<String, String>> dbResponseList_step10 = dbService.getRows(String.format(QUERY, masterPolicyNumber.get()));

        assertThat(dbResponseList_step10).hasSize(48);
        // 2 Invoice3 2033
        verifySubledgerTransaction(dbResponseList_step10, "2033", "DEBIT", periodStartInvoice3_1, periodEndInvoice3_1, endorsePremium_2);
        verifySubledgerTransaction(dbResponseList_step10, "2033", "DEBIT", periodStartInvoice3_2, periodEndInvoice3_2, endorsePremium_2);
        // 2 Invoice3 2034
        verifySubledgerTransaction(dbResponseList_step10, "2034", "CREDIT", periodStartInvoice3_1, periodEndInvoice3_1, endorsePremium_2);
        verifySubledgerTransaction(dbResponseList_step10, "2034", "CREDIT", periodStartInvoice3_2, periodEndInvoice3_2, endorsePremium_2);
    }

    private void verifySubledgerTransaction(List<Map<String, String>> dbResponseList, String ledgerAccountNo, String entryType, LocalDateTime periodStartDate, LocalDateTime periodEndDate, Currency entryAmt) {
        Map<String, String> transactionMap = dbResponseList.stream().filter(map ->
                map.get("ledgerAccountNo").equals(ledgerAccountNo) &&
                        map.get("periodStart").contains(periodStartDate.format(YYYY_MM_DD)) &&
                        map.get("periodEnd").contains(periodEndDate.format(YYYY_MM_DD)) &&
                        map.get("entryType").equals(entryType) &&
                        map.get("entryAmt").equals(entryAmt.toPlainString())).findFirst()
                .orElseThrow(() -> new IstfException(String.format("Row with ledgerAccountNo '%s', periodStart '%s', periodEnd '%s', entryAmt '%s' not found",
                        ledgerAccountNo, periodStartDate.format(YYYY_MM_DD), periodEndDate.format(YYYY_MM_DD), entryAmt)));

        assertSoftly(softly -> {
            softly.assertThat(transactionMap.get("productNumber")).isEqualTo(masterPolicyNumber.get());
            softly.assertThat(transactionMap.get("stateProvCd")).isEqualTo("GA");
            softly.assertThat(transactionMap.get("underwriterCd")).isEqualTo("RLHICA");
            softly.assertThat(transactionMap.get("countryCd")).isEqualTo("US");
        });
    }
}