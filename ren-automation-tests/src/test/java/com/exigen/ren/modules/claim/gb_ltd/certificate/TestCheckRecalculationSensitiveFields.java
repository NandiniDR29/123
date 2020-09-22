package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.TestDataKey.ISSUE;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.ClaimChangeDateOfLossActionTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RELATIONSHIP_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.BENEFIT_AMOUNT_PER_CHILD;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckRecalculationSensitiveFields extends ClaimGroupBenefitsLTDBaseTest {
    private PaymentPaymentPaymentAllocationTab paymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
    private PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35679", component = CLAIMS_GROUPBENEFITS)
    public void testRecalculatedChildEducationBenefit() {
        TestData tdMasterPolicy = getDefaultLTDMasterPolicyData()
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CHILD_EDUCATION_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), BENEFIT_AMOUNT_PER_CHILD.getLabel()), "100")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), MAXIMUM_MONTHLY_PAYMENTS.getLabel()), "36")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), BENEFIT_AMOUNT_FOR_ALL_CHILDREN.getLabel()), "300")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()), "60%");

        TestData tdClaimCreate = disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000");
        preconditionCreatePolicyAndClaim(tdMasterPolicy, tdClaimCreate);

        LocalDateTime bpStartDate = getBpStartDate();

        LOGGER.info("TEST REN-35679: Step 1");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY)), paymentCalculatorTab.getClass());

        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue("Child Education Benefit");
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN).setValue("2");
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(bpStartDate.format(DateTimeUtils.MM_DD_YYYY));
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(bpStartDate.plusMonths(2).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));

        Currency paymentAmount = new Currency("800");
        submitPayment(paymentAmount);

        checkRecalculationSensitiveFields(bpStartDate, new Currency("426.67"), paymentAmount, new Currency("600.00"));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35684", component = CLAIMS_GROUPBENEFITS)
    public void testRecalculatedCOBRAPremiumReimbursementBenefit() {
        TestData tdMasterPolicy = getDefaultLTDMasterPolicyData()
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), COBRA_PREMIUM_REIMB_AMOUNT.getLabel()), "1000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()), "60%");

        TestData tdClaimCreate = disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusYears(50).format(MM_DD_YYYY));
        preconditionCreatePolicyAndClaim(tdMasterPolicy, tdClaimCreate);

        LocalDateTime bpStartDate = getBpStartDate();

        LOGGER.info("TEST REN-35684: Step 1");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)), paymentCalculatorTab.getClass());

        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue("COBRA Premium Reimbursement Benefit");
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(REQUIRED_MONTHLY_COBRA_PREMIUM).setValue("100");
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(bpStartDate.format(DateTimeUtils.MM_DD_YYYY));
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(bpStartDate.plusMonths(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));

        Currency paymentAmount = new Currency("700");
        submitPayment(paymentAmount);
        checkRecalculationSensitiveFields(bpStartDate, new Currency("373.33"), paymentAmount, new Currency("600.00"));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35686", component = CLAIMS_GROUPBENEFITS)
    public void testRecalculatedFamilyCareBenefit() {
        TestData tdMasterPolicy = getDefaultLTDMasterPolicyData()
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FAMILY_CARE_BENEFIT.getLabel()), "Included")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FAMILY_CARE_BENEFIT_AMOUNT.getLabel()), "$350")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FAMILY_CARE_BENEFIT_AMOUNT_DURATION.getLabel()), "12 Months")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FAMILY_CARE_BENEFIT_MAXIMUM_ALL_FAMILY_MEMBERS.getLabel()), "$2,000");

        TestData tdClaimCreate = disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000000")
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusYears(50).format(MM_DD_YYYY));
        preconditionCreatePolicyAndClaim(tdMasterPolicy, tdClaimCreate);

        LocalDateTime bpStartDate = getBpStartDate();
        bpStartDate = bpStartDate.plusMonths(3).getYear() > bpStartDate.getYear() ? bpStartDate.plusYears(1).withDayOfYear(1) : bpStartDate;

        LOGGER.info("TEST REN-35686: Step 1");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY)), paymentCalculatorTab.getClass());

        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue("Family Care Benefit");
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(NUMBER_OF_FAMILY_MEMBERS).setValue("1");
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(bpStartDate.format(DateTimeUtils.MM_DD_YYYY));
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(bpStartDate.plusMonths(1).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));

        Currency paymentAmount = new Currency("6350");
        submitPayment(paymentAmount);
        checkRecalculationSensitiveFields(bpStartDate, new Currency("3386.67"), paymentAmount, new Currency("6000.00"));
    }

    private void checkRecalculationSensitiveFields(LocalDateTime bpStartDate, Currency amountBalance, Currency paymentAmount, Currency totalPaymentAmount) {
        LOGGER.info("TEST REN-35679, REN-35684, REN-35686: Step 2-3");
        changeDateOfLoss(bpStartDate.plusMonths(2));

        toSubTab(PAYMENTS);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)).hasCellWithValue(TOTAL_PAYMENT_AMOUNT, totalPaymentAmount.toString());

        LOGGER.info("TEST REN-35679, REN-35684, REN-35686: Step 5-7");
        changeDateOfLoss(TimeSetterUtil.getInstance().getCurrentTime());

        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        claim.recalculatePaidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_RecalculatePaidPayment")
                .adjust(makeKeyPath(RecalculatePaidPaymentActionTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(13).format(MM_DD_YYYY)), 1);

        LOGGER.info("TEST REN-35679, REN-35684, REN-35686: Step 9-11");
        claim.checkBalance().start();
        assertThat(claim.checkBalance().getWorkspace().getTab(BalanceActionTab.class).getAssetList().getAsset(AMOUNT)).hasValue(amountBalance.toString());
        BalanceActionTab.buttonCancel.click();
        Page.dialogConfirmation.buttonYes.click();

        changeDateOfLoss(bpStartDate.plusMonths(2));
        claim.checkBalance().start();
        assertThat(claim.checkBalance().getWorkspace().getTab(BalanceActionTab.class).getAssetList().getAsset(AMOUNT)).hasValue(paymentAmount.toString());
    }

    private void preconditionCreatePolicyAndClaim(TestData tdMasterPolicy, TestData tdClaim) {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(tdMasterPolicy);
        longTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "12000")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        disabilityClaim.create(tdClaim);
        claim.claimOpen().perform();
    }

    private LocalDateTime getBpStartDate() {
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        return LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();
    }

    private void submitPayment(Currency paymentAmount) {
        paymentAllocationTab.navigateToTab();
        claim.addPayment().submit();
        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)).hasCellWithValue(TOTAL_PAYMENT_AMOUNT, paymentAmount.toString());
        });
    }

    private void changeDateOfLoss(LocalDateTime newDateOfLoss) {
        toSubTab(OVERVIEW);
        disabilityClaim.changeDateOfLossAction().perform(disabilityClaim.getLTDTestData().getTestData("ClaimChangeDateOfLoss", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(ClaimChangeDateOfLossActionTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), newDateOfLoss.format(MM_DD_YYYY)));
    }
}
