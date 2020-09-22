package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.ParticipantIndexedPreDisabilityEarningsMetaData.*;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentActionTab.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.PARTIAL_DISABILITY_BENEFIT;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUpdatePaymentCalcPartialDisabilityToApplyIPDE extends ClaimGroupBenefitsLTDBaseTest {

    private AbstractContainer<?, ?> paymentAllocationAssetList = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).getAssetList();
    private PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37116", component = CLAIMS_GROUPBENEFITS)
    public void testClaimChangeRulesToAllocationAmountTC01() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));

        claim.claimOpen().perform();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        LocalDateTime bpStartDate = disabilityClaim.getBenefitPeriodStartDate(1);

        LOGGER.info("TEST: Step 1");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPayment"), PaymentPaymentPaymentAllocationTab.class, true);

        assertThat(paymentAllocationAssetList.getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency("1500").toString());

        LOGGER.info("TEST: Step 2");
        claim.addPayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST: Step 3");
        verifyAllocationAmountAfterUpdatingPayment(1, bpStartDate.plusMonths(6), bpStartDate.plusMonths(7).minusDays(1), "1500");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37221", component = CLAIMS_GROUPBENEFITS)
    public void testClaimChangeRulesToAllocationAmountTC02() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));

        claim.claimOpen().perform();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        LocalDateTime bpStartDate = disabilityClaim.getBenefitPeriodStartDate(1);

        LOGGER.info("TEST: Step 1");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPayment")
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), CURRENT_EARNINGS.getLabel()), "1200"),
                PaymentPaymentPaymentAllocationTab.class, true);

        assertThat(paymentAllocationAssetList.getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency("1500").toString());

        LOGGER.info("TEST: Step 2");
        claim.addPayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST: Step 3");
        verifyAllocationAmountAfterUpdatingPayment(1, bpStartDate.plusMonths(6), bpStartDate.plusMonths(7).minusDays(1), "300");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37222", component = CLAIMS_GROUPBENEFITS)
    public void testClaimChangeRulesToAllocationAmountTC03() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim_REN-37222").resolveLinks()));

        claim.claimOpen().perform();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        LocalDateTime bpStartDate = disabilityClaim.getBenefitPeriodStartDate(1);

        LOGGER.info("TEST: Step 1");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPayment")
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), CURRENT_EARNINGS.getLabel()), "1200"),
                PaymentPaymentPaymentAllocationTab.class, true);

        assertThat(paymentAllocationAssetList.getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency("3000").toString());

        LOGGER.info("TEST: Step 2");
        claim.addPayment().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST: Step 3");
        verifyAllocationAmountAfterUpdatingPayment(1, bpStartDate.plusMonths(6), bpStartDate.plusMonths(7).minusDays(1), "4200");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37223", component = CLAIMS_GROUPBENEFITS)
    public void testClaimChangeRulesToAllocationAmountTC04() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));

        claim.claimOpen().perform();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        LocalDateTime bpStartDate = disabilityClaim.getBenefitPeriodStartDate(1);

        LOGGER.info("TEST: Step 1");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPayment")
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), CURRENT_EARNINGS.getLabel()), "1300")
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), IN_APPROVED_REHABILITATION_PROGRAM.getLabel()), "Yes"),
                PaymentPaymentPaymentAllocationTab.class, true);

        assertThat(paymentAllocationAssetList.getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency("1500").toString());

        LOGGER.info("TEST: Step 2");
        claim.addPayment().submit();

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST: Step 3");
        verifyAllocationAmountAfterUpdatingPayment(1, bpStartDate.plusMonths(6), bpStartDate.plusMonths(7).minusDays(1), "200");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37230", component = CLAIMS_GROUPBENEFITS)
    public void testClaimChangeRulesToAllocationAmountTC05() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim_REN-37230_REN-37253").resolveLinks()));

        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        LocalDateTime bpStartDate = disabilityClaim.getBenefitPeriodStartDate(1);

        claim.addPayment().perform(tdSpecific().getTestData("TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), CURRENT_EARNINGS.getLabel()), "1200")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), IN_APPROVED_REHABILITATION_PROGRAM.getLabel()), "Yes")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(12).minusDays(1).format(MM_DD_YYYY)));

        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);

        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);
        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);

        LOGGER.info("TEST: Step 1");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPayment")
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), CURRENT_EARNINGS.getLabel()), "1200")
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), IN_APPROVED_REHABILITATION_PROGRAM.getLabel()), "Yes")
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.plusMonths(12).format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(13).minusDays(1).format(MM_DD_YYYY)),
                PaymentPaymentPaymentAllocationTab.class, true);

        assertThat(paymentAllocationAssetList.getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency("900").toString());

        LOGGER.info("TEST: Step 2");
        claim.addPayment().submit();

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(2);

        LOGGER.info("TEST: Step 3");
        verifyAllocationAmountAfterUpdatingPayment(2, bpStartDate.plusMonths(18), bpStartDate.plusMonths(19).minusDays(1), "900");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37253", component = CLAIMS_GROUPBENEFITS)
    public void testClaimChangeRulesToAllocationAmountTC06() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy")
                        .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), PARTIAL_DISABILITY_BENEFIT.getLabel()), "Proportionate Loss")
                        .resolveLinks()));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim_REN-37230_REN-37253").resolveLinks()));

        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        LocalDateTime bpStartDate = disabilityClaim.getBenefitPeriodStartDate(1);

        claim.addPayment().perform(tdSpecific().getTestData("TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), CURRENT_EARNINGS.getLabel()), "1200")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), IN_APPROVED_REHABILITATION_PROGRAM.getLabel()), "Yes")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(12).minusDays(1).format(MM_DD_YYYY)));

        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);

        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);
        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);

        LOGGER.info("TEST: Step 1");
        disabilityClaim.initiatePaymentAndFillToTab(tdSpecific().getTestData("TestData_IndemnityPayment")
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), CURRENT_EARNINGS.getLabel()), "1200")
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), IN_APPROVED_REHABILITATION_PROGRAM.getLabel()), "Yes"),
                PaymentPaymentPaymentAllocationTab.class, true);

        assertThat(paymentAllocationAssetList.getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency("780").toString());

        LOGGER.info("TEST: Step 3");
        paymentAllocationAssetList.getAsset(PAYMENT_FROM_DATE).setValue(bpStartDate.plusMonths(18).format(DateTimeUtils.MM_DD_YYYY));
        paymentAllocationAssetList.getAsset(PAYMENT_THROUGH_DATE).setValue(bpStartDate.plusMonths(19).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));

        assertThat(paymentAllocationAssetList.getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency("780").toString());
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37254", component = CLAIMS_GROUPBENEFITS)
    public void testClaimChangeRulesToAllocationAmountTC07() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim_REN-37254").resolveLinks()));

        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        claim.calculateSingleBenefitAmount().perform(tdSpecific().getTestData("TestData_CalculateASingleBenefitAmount"), 1);
        LocalDateTime bpStartDate = disabilityClaim.getBenefitPeriodStartDate(1);

        claim.addPayment().perform(tdSpecific().getTestData("TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), CURRENT_EARNINGS.getLabel()), "1200")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)));

        claim.addPayment().perform(tdSpecific().getTestData("TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), CURRENT_EARNINGS.getLabel()), "1200")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY)));

        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);

        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);
        disabilityClaim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 2);
        disabilityClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);

        LOGGER.info("TEST: Step 1");
        claim.claimUpdate().start();
        policyInformationParticipantParticipantInformationTab.navigateToTab();
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS)
                .getAsset(YEAR_START_DATE).setValue(bpStartDate.minusDays(1).format(MM_DD_YYYY));
        Tab.buttonSaveAndExit.click();

        LOGGER.info("TEST: Step 2");
        verifyFirstPayment("0", 1);

        verifySecondPayment("1500", "1450");

        LOGGER.info("TEST: Step 3");
        toSubTab(OVERVIEW);
        claim.claimUpdate().start();
        policyInformationParticipantParticipantInformationTab.navigateToTab();
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS)
                .getAsset(YEAR_START_DATE).setValue(bpStartDate.plusDays(1).format(MM_DD_YYYY));
        Tab.buttonSaveAndExit.click();

        verifyFirstPayment("1200", 2);

        verifySecondPayment("1500", "1450");

        LOGGER.info("TEST: Step 4");
        toSubTab(OVERVIEW);
        claim.claimUpdate().start();
        policyInformationParticipantParticipantInformationTab.navigateToTab();
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS)
                .getAsset(YEAR_START_DATE).setValue(bpStartDate.plusMonths(6).format(MM_DD_YYYY));
        Tab.buttonSaveAndExit.click();

        verifyFirstPayment("1200", 3);

        verifySecondPayment("300", "250");

        LOGGER.info("TEST: Step 5");
        toSubTab(OVERVIEW);
        claim.claimUpdate().start();
        policyInformationParticipantParticipantInformationTab.navigateToTab();
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS)
                .getAsset(YEAR_START_DATE).setValue(bpStartDate.format(MM_DD_YYYY));
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS)
                .getAsset(YEAR_END_DATE).setValue(bpStartDate.format(MM_DD_YYYY));
        Tab.buttonSaveAndExit.click();

        verifyFirstPayment("0", 4);

        verifySecondPayment("300", "250");

        LOGGER.info("TEST: Step 6");
        toSubTab(OVERVIEW);
        claim.claimUpdate().start();
        policyInformationParticipantParticipantInformationTab.navigateToTab();
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS)
                .getAsset(YEAR_START_DATE).setValue(bpStartDate.format(MM_DD_YYYY));
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS)
                .getAsset(YEAR_END_DATE).setValue(bpStartDate.plusMonths(6).format(MM_DD_YYYY));
        Tab.buttonSaveAndExit.click();

        verifyFirstPayment("0", 5);

        verifySecondPayment("1500", "1450");

        LOGGER.info("TEST: Step 7");
        toSubTab(OVERVIEW);
        claim.claimUpdate().start();
        policyInformationParticipantParticipantInformationTab.navigateToTab();
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS)
                .getAsset(INDEXED_PRE_DISABILITY_EARNINGS).setValue("2500");
        Tab.buttonSaveAndExit.click();

        verifyFirstPayment("1200", 6);

        verifySecondPayment("300", "250");
    }

    private void verifyAllocationAmountAfterUpdatingPayment(int payment, LocalDateTime paymentFromDate, LocalDateTime paymentThroughDate, String allocationAmountExpected) {
        claim.updatePayment().start(payment);
        paymentPaymentPaymentAllocationTab.navigateToTab();
        paymentAllocationAssetList.getAsset(PAYMENT_FROM_DATE).setValue(paymentFromDate.format(DateTimeUtils.MM_DD_YYYY));
        paymentAllocationAssetList.getAsset(PAYMENT_THROUGH_DATE).setValue(paymentThroughDate.format(DateTimeUtils.MM_DD_YYYY));

        assertThat(paymentAllocationAssetList.getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency(allocationAmountExpected).toString());
    }

    private void verifyFirstPayment(String balanceAmount, int balanceRow) {
        claim.viewPayment().start(1);

        assertSoftly(softly -> {
            softly.assertThat(tableClaimPaymentAllocationView).hasMatchingRows(PaymentActionTab.ClaimPaymentAllocationView.ALLOCATION_AMOUNT.getName(), new Currency(1500).toString());
            softly.assertThat(tableClaimPaymentView).hasMatchingRows(PaymentActionTab.ClaimPaymentView.PAYMENT_AMOUNT.getName(), new Currency(1450).toString());
            softly.assertThat(tablePaymentBalanceHistory.getRow(balanceRow).getCell(PaymentBalanceHistory.BALANCE_AMOUNT.getName())).hasValue(new Currency(balanceAmount).toString());
        });

        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
    }

    private void verifySecondPayment(String allocationAmount, String paymentAmount) {
        claim.viewPayment().start(2);

        assertSoftly(softly -> {
            softly.assertThat(tableClaimPaymentAllocationView).hasMatchingRows(PaymentActionTab.ClaimPaymentAllocationView.ALLOCATION_AMOUNT.getName(), new Currency(allocationAmount).toString());
            softly.assertThat(tableClaimPaymentView).hasMatchingRows(PaymentActionTab.ClaimPaymentView.PAYMENT_AMOUNT.getName(), new Currency(paymentAmount).toString());
            softly.assertThat(tablePaymentBalanceHistory.getRow(1).getCell(1)).hasValue("No records found.");
        });

        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
    }
}