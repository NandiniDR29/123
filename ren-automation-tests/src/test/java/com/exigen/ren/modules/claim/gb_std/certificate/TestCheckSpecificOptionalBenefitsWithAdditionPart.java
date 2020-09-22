package com.exigen.ren.modules.claim.gb_std.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitSTDInjuryPartyInformationTab;
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
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.modules.claim.common.metadata.DeductionsActionTabMetaData.BEGINNING_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.DeductionsActionTabMetaData.THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PAYMENT_ADDITION;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab.ClaimUnprocessedBalanceTableExtended.BALANCE_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab.ClaimUnprocessedBalanceTableExtended.PAYMENT_NUMBER;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RELATIONSHIP_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.CONTRIBUTION_TYPE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
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

        shortTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultSTDCertificatePolicyDataGatherData()
                .adjust(makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "52000")
                .adjust(shortTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        disabilityClaim.create(disabilityClaim.getSTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitsSTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusYears(12).format(MM_DD_YYYY)));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_STD")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), CoveragesActionTabMetaData.BENEFIT_PERCENTAGE.getLabel()), "60"), 1);
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
        disabilityClaim.updateBenefit().perform(tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData_Update")
                .adjust(makeKeyPath(BenefitSTDInjuryPartyInformationTab.class.getSimpleName(), COVERED_EARNINGS.getLabel()), "2000"), 1);

        checkPaymentAmount(1, allocationAmount, new Currency(60));
        checkPaymentAmount(2, new Currency(1100), new Currency(100));
        String paymentNumber1 = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).getValue();
        claim.checkBalance().start();
        assertSoftly(softly -> {
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries.getRow(PAYMENT_NUMBER.getName(), paymentNumber1))
                    .hasCellWithValue(BALANCE_AMOUNT.getName(), new Currency(400).negate().toString());
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        });
        BalanceActionTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("TEST REN-38307: Step 2");
        disabilityClaim.recalculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ReCalculate")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), CoveragesActionTabMetaData.BENEFIT_PERCENTAGE.getLabel()), "30"), 1);

        checkPaymentAmount(1, allocationAmount, new Currency(60));
        checkPaymentAmount(2, allocationAmount, new Currency(60));
    }

    private void checkPaymentAmount(int rowPayment, Currency allocationAmount, Currency deductionAmount) {
        claim.paymentInquiry().start(rowPayment);
        paymentCalculatorTab.navigateToTab();
        assertThat(new StaticElement(PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.WEEKLY_BENEFIT_AMOUNT.getLocator())).hasValue(deductionAmount.toString());
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();

        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(rowPayment).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
    }
}
