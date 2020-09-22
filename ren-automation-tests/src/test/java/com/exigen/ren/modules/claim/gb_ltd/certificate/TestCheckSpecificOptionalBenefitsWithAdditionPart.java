package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitLTDInjuryPartyInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.modules.claim.common.metadata.ClaimChangeDateOfLossActionTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.common.metadata.DeductionsActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.DeductionsActionTabMetaData.THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.BUTTON_ADD_PAYMENT_ADDITION;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PAYMENT_ADDITION;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.ADDITION_TYPE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab.ClaimUnprocessedBalanceTableExtended.BALANCE_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab.ClaimUnprocessedBalanceTableExtended.PAYMENT_NUMBER;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RELATIONSHIP_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAX_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.EMPLOYER_BENEFITS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestCheckSpecificOptionalBenefitsWithAdditionPart extends ClaimGroupBenefitsLTDBaseTest {
    private PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
    private DeductionsActionTab deductionsActionTab = claim.calculateSingleBenefitAmount().getWorkspace().getTab(DeductionsActionTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38302", component = CLAIMS_GROUPBENEFITS)
    public void testCheckSpecificOptionalBenefitRehabilitationBenefit() {
        Currency allocationAmount = new Currency(630);
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT.getLabel()), "5%"));

        longTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "12000")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusYears(12).format(MM_DD_YYYY)));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPaymentWithAdditionParty")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)));

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPaymentWithAdditionParty")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY)));

        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 2);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        LOGGER.info("TEST REN-38302: Step 1");
        disabilityClaim.updateBenefit().perform(tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData_Update")
                .adjust(makeKeyPath(BenefitLTDInjuryPartyInformationTab.class.getSimpleName(), COVERED_EARNINGS.getLabel()), "2000"), 1);

        checkPaymentAmount(1, allocationAmount, new Currency(30));
        checkPaymentAmount(2, new Currency(1260), new Currency(60));
        String paymentNumber1 = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).getValue();
        claim.checkBalance().start();
        assertSoftly(softly -> {
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries.getRow(PAYMENT_NUMBER.getName(), paymentNumber1))
                    .hasCellWithValue(BalanceActionTab.ClaimUnprocessedBalanceTableExtended.NET_AMOUNT.getName(), allocationAmount.toString());
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        });
        BalanceActionTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("TEST REN-38302: Step 2");
        disabilityClaim.recalculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ReCalculate")
                .adjust(makeKeyPath(CoveragesActionTab.class.getSimpleName(), CoveragesActionTabMetaData.BENEFIT_PERCENTAGE.getLabel()), "30"), 1);

        checkPaymentAmount(1, allocationAmount, new Currency(30));
        checkPaymentAmount(2, allocationAmount, new Currency(30));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38436", component = CLAIMS_GROUPBENEFITS)
    public void testCheckSpecificOptionalBenefitCatastrophicDisability() {
        Currency allocationAmount = new Currency(700);
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CATASTROPHIC_DISABILITY_BENEFIT.getLabel()), "10%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), MAXIMUM_DOLLAR_AMOUNT.getLabel()), "$2 000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), MAX_MONTHLY_BENEFIT_AMOUNT.getLabel()), "6000"));

        longTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "12000")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusYears(12).format(MM_DD_YYYY)));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPaymentWithAdditionParty")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", ADDITION_TYPE.getLabel()), "Catastrophic Disability Benefit")
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)));

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPaymentWithAdditionParty")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", ADDITION_TYPE.getLabel()), "Catastrophic Disability Benefit")
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.plusMonths(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY)));

        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 2);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        LOGGER.info("TEST REN-38436: Step 1");
        disabilityClaim.updateBenefit().perform(tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData_Update")
                .adjust(makeKeyPath(BenefitLTDInjuryPartyInformationTab.class.getSimpleName(), COVERED_EARNINGS.getLabel()), "2000"), 1);

        checkPaymentAmount(1, allocationAmount, new Currency(100));
        checkPaymentAmount(2, new Currency(1400), new Currency(200));
        String paymentNumber1 = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).getValue();
        claim.checkBalance().start();
        assertSoftly(softly -> {
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries.getRow(PAYMENT_NUMBER.getName(), paymentNumber1))
                    .hasCellWithValue(BalanceActionTab.ClaimUnprocessedBalanceTableExtended.NET_AMOUNT.getName(), allocationAmount.toString());
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38882", component = CLAIMS_GROUPBENEFITS)
    public void testCheckSpecificOptionalBenefitDOL1() {
        createPolicyWithClaim();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.addPayment().perform(tdSpecific().getTestData("TestData_IndemnityPaymentWithAdditionParty")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY)));

        claim.addPayment().perform(tdSpecific().getTestData("TestData_IndemnityPaymentWithAdditionParty")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.plusDays(30).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(59).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.plusDays(30).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(59).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", BEGINNING_DATE.getLabel()), bpStartDate.plusDays(30).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", THROUGH_DATE.getLabel()), bpStartDate.plusDays(59).format(MM_DD_YYYY)));

        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 2);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        LOGGER.info("TEST REN-38882: Step 1");
        toSubTab(OVERVIEW);
        disabilityClaim.changeDateOfLossAction().perform(disabilityClaim.getLTDTestData().getTestData("ClaimChangeDateOfLoss", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(ClaimChangeDateOfLossActionTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusDays(59).format(MM_DD_YYYY)));

        claim.paymentInquiry().start(1);
        paymentCalculatorTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(1).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(100).toString());
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(2).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(30).toString());
        });
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        String paymentNumber1 = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).getValue();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT)).hasValue(new Currency(730).toString());

        claim.paymentInquiry().start(2);
        paymentCalculatorTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(1).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(3.33).toString());
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(2).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(1).toString());
        });
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT)).hasValue(new Currency(24.33).toString());

        claim.checkBalance().start();
        assertSoftly(softly -> {
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries.getRow(PAYMENT_NUMBER.getName(), paymentNumber1))
                    .hasCellWithValue(BALANCE_AMOUNT.getName(), new Currency(730).toString());
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38882", component = CLAIMS_GROUPBENEFITS)
    public void testCheckSpecificOptionalBenefitDOL2() {
        createPolicyWithClaim();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().start(1);
        claim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), OtherIncomeBenefitActionTab.class);
        OtherIncomeBenefitActionTab.buttonSaveAndExit.click();
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.recalculateSingleBenefitAmount().start(1);
        deductionsActionTab.navigate();
        deductionsActionTab.fillTab(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestDataWithDeductions")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), TYPE_OF_DEDUCTION.getLabel()), "contains=401K")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT.getLabel()), "60")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), THROUGH_DATE.getLabel()), bpStartDate.plusYears(10).format(MM_DD_YYYY))
                .mask(makeKeyPath(deductionsActionTab.getMetaKey(), DEDUCTION_AMOUNT.getLabel())));
        deductionsActionTab.fillTab(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestDataWithDeductions")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), TYPE_OF_DEDUCTION.getLabel()), "contains=Revenue")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), THROUGH_DATE.getLabel()), bpStartDate.plusYears(10).format(MM_DD_YYYY))
                .mask(makeKeyPath(deductionsActionTab.getMetaKey(), DEDUCTION_AMOUNT.getLabel())));
        DeductionsActionTab.buttonSaveAndExit.click();

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(29).format(MM_DD_YYYY)));
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.plusDays(30).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(59).format(MM_DD_YYYY)));

        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 3);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 6);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 3);

        LOGGER.info("TEST REN-38882: Step 2");
        toSubTab(OVERVIEW);
        disabilityClaim.changeDateOfLossAction().perform(disabilityClaim.getLTDTestData().getTestData("ClaimChangeDateOfLoss", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(ClaimChangeDateOfLossActionTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().plusDays(59).format(MM_DD_YYYY)));

        claim.paymentInquiry().start(3);
        paymentCalculatorTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(1).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(60).toString());
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(2).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(100).toString());
        });
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        String paymentNumber1 = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(3).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).getValue();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(3).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT)).hasValue(new Currency(600).toString());

        claim.paymentInquiry().start(6);
        paymentCalculatorTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(1).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(2).toString());
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(2).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(3.33).toString());
        });
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(6).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT)).hasValue(new Currency(20).toString());

        claim.checkBalance().start();
        assertSoftly(softly -> {
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries.getRow(PAYMENT_NUMBER.getName(), paymentNumber1))
                    .hasCellWithValue(BALANCE_AMOUNT.getName(), new Currency(600).toString());
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39284", component = CLAIMS_GROUPBENEFITS)
    public void testCheckSpecificOptionalBenefitRecalculatePaidPayment1() {
        createPolicyWithClaim();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.addPayment().perform(tdSpecific().getTestData("TestData_IndemnityPaymentWithAdditionParty")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[0]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentCalculatorTab.getMetaKey(), PAYMENT_ADDITION.getLabel() + "[1]", THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)));

        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 1);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        LOGGER.info("TEST REN-39284: Step 1");
        claim.recalculatePaidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_RecalculatePaidPayment")
                .adjust(makeKeyPath(RecalculatePaidPaymentActionTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(9).format(MM_DD_YYYY)), 1);

        claim.paymentInquiry().start(1);
        paymentCalculatorTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(1).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(100).toString());
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(2).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(30).toString());
        });
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        String paymentNumber1 = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).getValue();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT)).hasValue(new Currency(730).toString());

        claim.checkBalance().start();
        assertSoftly(softly -> {
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries.getRow(PAYMENT_NUMBER.getName(), paymentNumber1))
                    .hasCellWithValue(BALANCE_AMOUNT.getName(), new Currency(486.67).toString());
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39284", component = CLAIMS_GROUPBENEFITS)
    public void testCheckSpecificOptionalBenefitRecalculatePaidPayment2() {
        createPolicyWithClaim();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().start(1);
        claim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), OtherIncomeBenefitActionTab.class);
        OtherIncomeBenefitActionTab.buttonSaveAndExit.click();
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        claim.recalculateSingleBenefitAmount().start(1);
        deductionsActionTab.navigate();
        deductionsActionTab.fillTab(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestDataWithDeductions")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), TYPE_OF_DEDUCTION.getLabel()), "contains=401K")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), REQUIRED_MONTHLY_401K_CONTRIBUTION_AMOUNT.getLabel()), "60")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), THROUGH_DATE.getLabel()), bpStartDate.plusYears(10).format(MM_DD_YYYY))
                .mask(makeKeyPath(deductionsActionTab.getMetaKey(), DEDUCTION_AMOUNT.getLabel())));
        deductionsActionTab.fillTab(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestDataWithDeductions")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), TYPE_OF_DEDUCTION.getLabel()), "contains=Revenue")
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), BEGINNING_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(deductionsActionTab.getMetaKey(), THROUGH_DATE.getLabel()), bpStartDate.plusYears(10).format(MM_DD_YYYY))
                .mask(makeKeyPath(deductionsActionTab.getMetaKey(), DEDUCTION_AMOUNT.getLabel())));
        DeductionsActionTab.buttonSaveAndExit.click();

        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bpStartDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)));

        mainApp().reopen(approvalUsername, approvalPassword);
        search(claimNumber);
        claim.approvePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_ApprovePayment"), 3);
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 3);

        LOGGER.info("TEST REN-38882: Step 2");
        claim.recalculatePaidPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_RecalculatePaidPayment")
                .adjust(makeKeyPath(RecalculatePaidPaymentActionTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bpStartDate.plusDays(9).format(MM_DD_YYYY)), 3);

        claim.paymentInquiry().start(3);
        paymentCalculatorTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(1).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(60).toString());
            softly.assertThat(PaymentCalculatorTab.tableListOfPaymentAddition.getRow(2).getCell(PaymentCalculatorTab.PaymentCalculatorPaymentAddition.AMOUNT.getName()))
                    .hasValue(new Currency(100).toString());
        });
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        String paymentNumber1 = ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(3).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).getValue();
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(3).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT)).hasValue(new Currency(600).toString());

        claim.checkBalance().start();
        assertSoftly(softly -> {
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries.getRow(PAYMENT_NUMBER.getName(), paymentNumber1))
                    .hasCellWithValue(BALANCE_AMOUNT.getName(), new Currency(400).toString());
            softly.assertThat(BalanceActionTab.tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        });
    }

    private void createPolicyWithClaim() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "AL")
                .mask(makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()))
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT.getLabel()), "5%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT_MAX_AMOUNT.getLabel()), "$500")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CATASTROPHIC_DISABILITY_BENEFIT.getLabel()), "10%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), MAXIMUM_DOLLAR_AMOUNT.getLabel()), "$2 000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), MAX_MONTHLY_BENEFIT_AMOUNT.getLabel()), "6000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_DURING_DISABILITY.getLabel()), "10%")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FOUR_HUNDRED_ONE_K_CONTRIBUTION_MONTHLY_MAXIMUM_AMOUNT.getLabel()), "1000")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", EMPLOYER_BENEFITS.getLabel(), EmployerBenefitsMetaData.NONE.getLabel()), "false")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", EMPLOYER_BENEFITS.getLabel(), EmployerBenefitsMetaData.REVENUE_PROTECTION_BENEFIT_PERCENT.getLabel()), "10%"));

        longTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "12000")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), DateTimeUtils.getCurrentDateTime().plusYears(12).format(MM_DD_YYYY)));
        claim.claimOpen().perform();
    }

    private void checkPaymentAmount(int rowPayment, Currency allocationAmount, Currency deductionAmount) {
        claim.paymentInquiry().start(rowPayment);
        paymentCalculatorTab.navigateToTab();
        assertThat(new StaticElement(PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.MONTHLY_BENEFIT_AMOUNT.getLocator())).hasValue(deductionAmount.toString());
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();

        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(rowPayment).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TOTAL_PAYMENT_AMOUNT)).hasValue(allocationAmount.toString());
    }
}
