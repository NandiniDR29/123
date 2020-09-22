package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.modules.claim.common.metadata.DeductionsActionTabMetaData.BEGINNING_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab.ClaimUnprocessedBalanceTableExtended.BALANCE_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab.ClaimUnprocessedBalanceTableExtended.PAYMENT_NUMBER;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.REHABILITATION_BENEFIT_MAXIMUM_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.REHABILITATION_INCENTIVE_BENEFIT;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckSpecificOptionalBenefitsWithAdditionPart extends ClaimGroupBenefitsSTDBaseTest {
    private PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38307", component = CLAIMS_GROUPBENEFITS)
    public void testCheckSpecificOptionalBenefitRehabilitationBenefit() {
        Currency allocationAmount = new Currency(660);
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT.getLabel()), "10%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), REHABILITATION_BENEFIT_MAXIMUM_AMOUNT.getLabel()), "$400"));

        disabilityClaim.create(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusYears(12).format(MM_DD_YYYY)));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_STD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPaymentWithAdditionParty")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(6).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(6).format(MM_DD_YYYY)));

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPaymentWithAdditionParty")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.plusDays(7).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(13).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.plusDays(7).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(13).format(MM_DD_YYYY)));

        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 2);

        LOGGER.info("TEST REN-38307: Step 1");
        toSubTab(OVERVIEW);
        disabilityClaim.claimUpdate().perform(tdClaim.getTestData("TestClaimUpdate", "TestData_Update")
                .adjust(makeKeyPath(PolicyInformationParticipantParticipantInformationActionTab.class.getSimpleName(), COVERED_EARNINGS.getLabel()), "2000"));

        checkPaymentAmount(1, allocationAmount, new Currency(60));
        checkPaymentAmount(2, new Currency(1100), new Currency(100));
        String paymentNumber1 = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).getValue();
        claim.checkBalance().start();
        assertSoftly(softly -> {
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries.getRow(PAYMENT_NUMBER.getName(), paymentNumber1))
                    .hasCellWithValue(BALANCE_AMOUNT.getName(), new Currency(400).negate().toString());
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        });
        BalanceActionTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("TEST REN-38307: Step 2");
        toSubTab(OVERVIEW);
        disabilityClaim.claimUpdate().perform(tdClaim.getTestData("TestClaimUpdate", "TestData_Update")
                .adjust(makeKeyPath(PolicyInformationParticipantParticipantCoverageActionTab.class.getSimpleName(), BENEFIT_PERCENTAGE.getLabel()), "30"));

        checkPaymentAmount(1, allocationAmount, new Currency(60));
        checkPaymentAmount(2, allocationAmount, new Currency(60));
    }

    private void checkPaymentAmount(int rowPayment, Currency allocationAmount, Currency deductionAmount) {
        claim.paymentInquiry().start(rowPayment);
        paymentCalculatorTab.navigateToTab();
        assertThat(new StaticElement(PaymentAdditionMetaData.WEEKLY_BENEFIT_AMOUNT.getLocator())).hasValue(deductionAmount.toString());
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();

        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(rowPayment).getCell(ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
    }
}
