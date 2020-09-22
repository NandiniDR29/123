package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.tabs.OtherIncomeBenefitActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.Tab.cancelClickAndCloseDialog;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.modules.claim.common.metadata.OtherIncomeBenefitActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationActionTabMetaData.PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationActionTabMetaData.ParticipantIndexedPreDisabilityEarningsMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationActionTabMetaData.REMOVE_PARTICIPANT_INDEXED;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.INFECTIOUS_DISEASE;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPaymentCalculationForInfectiousDiseaseBenefit extends ClaimGroupBenefitsLTDBaseTest {
    private static final String INFECTIOUS_AND_CONTAGIOUS_DISEASE_BENEFIT = "Infectious and Contagious Disease Benefit";
    private static final String WARNING_MESSAGE = "Current changes will impact existing Active&Completed payments. Completed Payments will be recalculated and generate " +
            "the Claim balance. Please review Active payments to check if they need to be updated or voided.";
    private PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
    private PolicyInformationParticipantParticipantInformationActionTab participantParticipantInformationActionTab = claim.claimUpdate().getWorkspace().getTab(PolicyInformationParticipantParticipantInformationActionTab.class);
    private Currency allocationAmount = new Currency(1800);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37037", component = CLAIMS_GROUPBENEFITS)
    public void testPaymentCalculationForInfectiousDiseaseBenefit() {
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), INFECTIOUS_DISEASE.getLabel()), "6 Months"));

        LOGGER.info("TEST REN-37037: Step 1");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));
        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY)
                .mask(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), BEGINNING_DATE.getLabel()))
                .mask(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), THROUGH_DATE.getLabel()))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "1500"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        LOGGER.info("TEST REN-37037: Step 2");
        verifyAllocationAmount(currentDate.plusMonths(6), currentDate.plusMonths(7).minusDays(1), allocationAmount, "1-1");
        cancelClickAndCloseDialog();

        LOGGER.info("TEST REN-37037: Step 3");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), BEGINNING_DATE.getLabel()), bpStartDate.plusDays(10).minusMonths(2).format(MM_DD_YYYY))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), THROUGH_DATE.getLabel()), bpStartDate.plusDays(10).format(MM_DD_YYYY))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "1500"), 1);

        verifyAllocationAmount(bpStartDate, bpStartDate.plusDays(30), new Currency(1662), "1-2");
        cancelClickAndCloseDialog();

        LOGGER.info("TEST REN-37037: Step 4");
        verifyAllocationAmount(bpStartDate.plusMonths(1), bpStartDate.plusMonths(2).minusDays(1), allocationAmount, "1-2");
        cancelClickAndCloseDialog();

        LOGGER.info("TEST REN-37037: Step 5");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        verifyAllocationAmount(currentDate.plusMonths(13), currentDate.plusMonths(14).minusDays(1), allocationAmount, "1-3");
        cancelClickAndCloseDialog();

        LOGGER.info("TEST REN-37037: Step 7");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), BEGINNING_DATE.getLabel()), bpStartDate.plusDays(10).minusMonths(2).format(MM_DD_YYYY))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), THROUGH_DATE.getLabel()), bpStartDate.plusDays(10).format(MM_DD_YYYY))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "300"), 1);

        verifyAllocationAmount(bpStartDate.plusMonths(1), bpStartDate.plusMonths(2).minusDays(1), allocationAmount, "1-4");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37106", component = CLAIMS_GROUPBENEFITS)
    public void testRecalculationOfNewSensitiveFieldsPayment1() {
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), INFECTIOUS_DISEASE.getLabel()), "6 Months"));

        LOGGER.info("TEST REN-37106: Step 1");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));
        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY)
                .mask(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), BEGINNING_DATE.getLabel()))
                .mask(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), THROUGH_DATE.getLabel()))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "1500"), 1);

        LOGGER.info("TEST REN-37106: Step 2");
        verifyAllocationAmount(currentDate.plusMonths(6), currentDate.plusMonths(7).minusDays(1), allocationAmount, "1-1");
        claim.addPayment().submit();

        checkScenarioForPayments(currentDate, allocationAmount, allocationAmount, allocationAmount);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-37106", "REN-37037"}, component = CLAIMS_GROUPBENEFITS)
    public void testRecalculationOfNewSensitiveFieldsPayment2() {
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), INFECTIOUS_DISEASE.getLabel()), "6 Months"));

        LOGGER.info("TEST REN-37106: Step 1");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));
        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        LOGGER.info("TEST REN-37106: Step 3");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), BEGINNING_DATE.getLabel()), bpStartDate.plusDays(10).minusMonths(2).format(MM_DD_YYYY))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), THROUGH_DATE.getLabel()), bpStartDate.plusDays(10).format(MM_DD_YYYY))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "1500"), 1);

        verifyAllocationAmount(bpStartDate, bpStartDate.plusDays(30), new Currency(1662), "1-2");
        claim.addPayment().submit();

        LOGGER.info("TEST REN-37106, TEST REN-37037: Step 3, Step 6");
        checkScenarioForPayments(currentDate, new Currency(1530), new Currency(1761), new Currency(1530));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37106", component = CLAIMS_GROUPBENEFITS)
    public void testRecalculationOfNewSensitiveFieldsPayment3() {
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), INFECTIOUS_DISEASE.getLabel()), "6 Months"));

        LOGGER.info("TEST REN-37106: Step 1");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));
        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        LOGGER.info("TEST REN-37106: Step 4");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), BEGINNING_DATE.getLabel()), bpStartDate.plusDays(10).minusMonths(2).format(MM_DD_YYYY))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), THROUGH_DATE.getLabel()), bpStartDate.plusDays(10).format(MM_DD_YYYY))
                .adjust(makeKeyPath(OtherIncomeBenefitActionTab.class.getSimpleName(), PAYMENT_AMOUNT.getLabel()), "1500"), 1);

        verifyAllocationAmount(bpStartDate.plusMonths(1), bpStartDate.plusMonths(2).minusDays(1), allocationAmount, "1-2");
        claim.addPayment().submit();

        checkScenarioForPayments(currentDate, allocationAmount, allocationAmount, allocationAmount);
    }

    private void checkScenarioForPayments(LocalDateTime currentDate, Currency allocationAmount1, Currency allocationAmount2, Currency allocationAmount3) {
        LOGGER.info("TEST REN-37106: Step 5");
        toSubTab(OVERVIEW);
        disabilityClaim.claimUpdate().start();
        participantParticipantInformationActionTab.navigate();
        participantParticipantInformationActionTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(currentDate.plusYears(2).format(MM_DD_YYYY));
        assertThat(participantParticipantInformationActionTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE)).hasWarningWithText(WARNING_MESSAGE);

        participantParticipantInformationActionTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.plusYears(1).format(MM_DD_YYYY));
        assertThat(participantParticipantInformationActionTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE)).hasWarningWithText(WARNING_MESSAGE);

        disabilityClaim.claimUpdate().submit();
        checkAllocationAmountAfterChanges(allocationAmount1);

        LOGGER.info("TEST REN-37106: Step 6");
        toSubTab(OVERVIEW);
        disabilityClaim.claimUpdate().start();
        participantParticipantInformationActionTab.navigate();
        participantParticipantInformationActionTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.format(MM_DD_YYYY));
        assertThat(participantParticipantInformationActionTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE)).hasWarningWithText(WARNING_MESSAGE);

        participantParticipantInformationActionTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(currentDate.plusYears(1).minusDays(1).format(MM_DD_YYYY));
        assertThat(participantParticipantInformationActionTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE)).hasWarningWithText(WARNING_MESSAGE);

        participantParticipantInformationActionTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS).setValue("10000");
        assertThat(participantParticipantInformationActionTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS)).hasWarningWithText(WARNING_MESSAGE);

        disabilityClaim.claimUpdate().submit();
        checkAllocationAmountAfterChanges(allocationAmount2);

        LOGGER.info("TEST REN-37106: Step 7");
        toSubTab(OVERVIEW);
        disabilityClaim.claimUpdate().start();
        participantParticipantInformationActionTab.navigate();
        participantParticipantInformationActionTab.getAssetList().getAsset(REMOVE_PARTICIPANT_INDEXED).click();
        Page.dialogConfirmation.confirm();

        disabilityClaim.claimUpdate().submit();
        checkAllocationAmountAfterChanges(allocationAmount3);
    }

    private void verifyAllocationAmount(LocalDateTime paymentFromDate, LocalDateTime paymentThroughDate, Currency allocationAmount, String singleBenefitNumber) {
        disabilityClaim.initiatePaymentAndFillToTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), IN_LIEU_BENEFIT.getLabel()), INFECTIOUS_AND_CONTAGIOUS_DISEASE_BENEFIT)
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), paymentFromDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), paymentThroughDate.format(MM_DD_YYYY)),
                PaymentPaymentPaymentAllocationTab.class, true);
        paymentPaymentPaymentAllocationTab.setSingleBenefitCalculationNumber(singleBenefitNumber);
        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(allocationAmount.toString());
    }

    private void checkAllocationAmountAfterChanges(Currency allocationAmount) {
        claim.paymentInquiry().start(1);
        paymentPaymentPaymentAllocationTab.navigateToTab();
        assertThat(new StaticElement(ALLOCATION_AMOUNT.getLocator())).hasValue(allocationAmount.toString());
        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
    }
}
