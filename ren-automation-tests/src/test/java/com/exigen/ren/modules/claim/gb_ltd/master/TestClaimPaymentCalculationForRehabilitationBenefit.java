package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesCalculatorActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesPaymentSeriesProfileActionTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitLTDInjuryPartyInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Condition;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimCalculateSingleBenefitTab.CALCULATOR;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.LONG_TERM_DISABILITY;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_PATTERN;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentSeriesTableExtended.SERIES_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended.PAYMENT_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.ASSOCIATE_POLICY_PARTY;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.TAXABILITY;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.REHABILITATION_INCENTIVE_BENEFIT;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPaymentCalculationForRehabilitationBenefit extends ClaimGroupBenefitsLTDBaseTest {

    private static final String REHABILITATION_BENEFIT = "Rehabilitation Benefit";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-25519", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalculationRIBNotIncludedInPolicy() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT.getLabel()), "None"));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits"));

        LOGGER.info("Step 1");
        claim.claimOpen().perform();

        claim.addBenefit().start(LONG_TERM_DISABILITY);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATE_POLICY_PARTY).setValueByIndex(1);

        List<String> associatedScheduledItemValues = benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(BenefitsLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM).getAllValues();

        assertThat(associatedScheduledItemValues).doNotHave(new Condition<>(str ->
                str.matches(REHABILITATION_BENEFIT + ".*"), REHABILITATION_BENEFIT + ".*"));

        disabilityClaim.addBenefit().getWorkspace().fill(tdClaim.getTestData("NewBenefit", "TestData_LTD_OtherValues")
                .mask(TestData.makeKeyPath(BenefitLTDInjuryPartyInformationTab.class.getSimpleName(), "Associate Policy Party")).resolveLinks());
        Tab.buttonSaveAndExit.click();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        LOGGER.info("Step 2");
        claim.addPayment().start();
        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), paymentCalculatorTab.getClass());

        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).doesNotContainOption(REHABILITATION_BENEFIT);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-25519", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalculationRIBIncludedInPolicy() {
        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
        PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab = claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);
        PaymentPaymentPaymentAllocationTab paymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), TAXABILITY.getLabel()), "Benefits Not Taxable")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", OPTIONS.getLabel(), REHABILITATION_INCENTIVE_BENEFIT.getLabel()), "10%"));

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits")
                .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000"));

        claim.claimOpen().perform();

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_LTD"));

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime BPStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1).getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        LOGGER.info("TEST: Step 3. Post Payment");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), BPStartDate.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), BPStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY)), paymentCalculatorTab.getClass());

        LOGGER.info("TEST: Step 4. Post Payment");
        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(REHABILITATION_BENEFIT);

        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).isPresent();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).isPresent();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).isPresent();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE)).isPresent();
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_COMMENTS)).isPresent();

            LOGGER.info("TEST: Step 5. Post Payment");
            paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue("abc");

            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).hasWarningWithText("Date format should be in MM/dd/yyyy.");

            LOGGER.info("TEST: Step 8. Post Payment");
            paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(ValueConstants.EMPTY);

            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).hasWarningWithText(String.format(ERROR_PATTERN, BEGINNING_DATE.getLabel()));

            LOGGER.info("TEST: Step 9. Post Payment");
            paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(BPStartDate.format(MM_DD_YYYY));

            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).hasNoWarning();

            LOGGER.info("TEST: Step 10. Post Payment");
            paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue("abc");

            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE)).hasWarningWithText("Date format should be in MM/dd/yyyy.");

            LOGGER.info("TEST: Step 13. Post Payment");
            paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(ValueConstants.EMPTY);

            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE)).hasWarningWithText(String.format(ERROR_PATTERN, THROUGH_DATE.getLabel()));
            LOGGER.info("TEST: Step 14. Post Payment");
            paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(BPStartDate.plusMonths(1).minusDays(1).format(MM_DD_YYYY));

            LOGGER.info("TEST: Step 21. Post Payment");
            assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).hasValue(new Currency(60).toString());

            LOGGER.info("TEST: Step 22. Post Payment");
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(new Currency(660).toString());

            LOGGER.info("TEST: Step 23. Post Payment");
            paymentPaymentAllocationTab.navigateToTab();
            claim.addPayment().submit();

            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
        });

        LOGGER.info("TEST: Step 24. Post Payment");
        claim.calculateSingleBenefitAmount().start(1);
        NavigationPage.toTreeTab(CALCULATOR.get());

        ImmutableList.of("Coverage Addition", "List of Coverage Addition", "Coverage Reduction", "List of Coverage Reduction").forEach(control ->
                assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(control))).isAbsent());

        Tab.buttonCancel.click();
        dialogConfirmation.confirm();

        LOGGER.info("TEST: Step 25");
        LOGGER.info("TEST: Step 3. Payment Series");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), BPStartDate.plusMonths(1).format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EXPIRATION_DATE.getLabel()), BPStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY)), PaymentSeriesCalculatorActionTab.class);

        LOGGER.info("TEST: Step 4. Payment Series");
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.ADDITION_TYPE).setValue(REHABILITATION_BENEFIT);

        assertSoftly(softly -> {
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.ADDITION_TYPE)).isPresent();
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE)).isPresent();
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.MONTHLY_BENEFIT_AMOUNT)).isPresent();
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE)).isPresent();
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.ADDITION_COMMENTS)).isPresent();

            LOGGER.info("TEST: Step 5. Payment Series");
            paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE).setValue("abc");

            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE)).hasWarningWithText("Date format should be in MM/dd/yyyy.");

            LOGGER.info("TEST: Step 8. Payment Series");
            paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE).setValue(ValueConstants.EMPTY);

            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE)).hasWarningWithText(String.format(ERROR_PATTERN, PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE.getLabel()));

            LOGGER.info("TEST: Step 9. Payment Series");
            paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE).setValue(BPStartDate.plusMonths(1).format(MM_DD_YYYY));

            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE)).hasNoWarning();

            LOGGER.info("TEST: Step 10. Payment Series");
            paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE).setValue("abc");

            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE)).hasWarningWithText("Date format should be in MM/dd/yyyy.");

            LOGGER.info("TEST: Step 13. Payment Series");
            paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE).setValue(ValueConstants.EMPTY);

            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE)).hasWarningWithText(String.format(ERROR_PATTERN, PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE.getLabel()));

            LOGGER.info("TEST: Step 14. Payment Series");
            paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE).setValue(BPStartDate.plusMonths(2).minusDays(1).format(MM_DD_YYYY));

            LOGGER.info("TEST: Step 21. Payment Series");
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.MONTHLY_BENEFIT_AMOUNT)).hasValue(new Currency(60).toString());

            LOGGER.info("TEST: Step 23. Payment Series");
            claim.createPaymentSeries().submit();

            softly.assertThat(tableSummaryOfClaimPaymentSeries).hasRows(1);

            LOGGER.info("TEST: Step 22. Payment Series");
            tableSummaryOfClaimPaymentSeries.getRow(1).getCell(SERIES_NUMBER.getName()).controls.links.getFirst().click();
            softly.assertThat(tableScheduledPaymentsOfSeries).with(PAYMENT_AMOUNT, new Currency("660").toString()).hasMatchingRows(1);
        });

        Tab.buttonCancel.click();
        dialogConfirmation.confirm();

        LOGGER.info("TEST: Step 24. Payment Series");
        claim.calculateSingleBenefitAmount().start(1);
        NavigationPage.toTreeTab(CALCULATOR.get());

        ImmutableList.of("Coverage Addition", "List of Coverage Addition", "Coverage Reduction", "List of Coverage Reduction").forEach(control ->
                assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(control))).isAbsent());
    }
}