package com.exigen.ren.modules.claim.gb_ltd.scenarios;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.assertj.core.api.Condition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.ASSOCIATED_INSURABLE_RISK;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_FROM_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.PAYMENT_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesPaymentSeriesProfileActionTabMetaData.EXPIRATION_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageDeterminationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;
import static org.assertj.core.api.Assertions.assertThat;

public class ScenarioTestClaimPaymentCalcForCatastrophicDisability extends ClaimGroupBenefitsLTDBaseTest {

    private final static String CATASTROPHIC_DISABILITY_BENEFIT = "Catastrophic Disability Benefit";

    public void testClaimPaymentCalcForCatastrophicDisabilityBenefit(boolean cwcp, String claimTestDataKey) {
        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
        PaymentPaymentPaymentAllocationTab paymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
        PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab = claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData().adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));

        LOGGER.info("Test: Steps 1, 2");

        if (cwcp) {
            createDefaultLongTermDisabilityCertificatePolicy();
            initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                    .adjust(makeKeyPath(BenefitsLTDInjuryPartyInformationTab.class.getSimpleName(), BenefitsLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS.getLabel()), "2000"), BenefitsLTDInjuryPartyInformationTab.class, true);
        } else {
            initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                    .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "2000"), BenefitsLTDInjuryPartyInformationTab.class, true);
        }

        List<String> associatedScheduledItemValues = benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).getAllValues();

        assertThat(associatedScheduledItemValues).doNotHave(new Condition<>(str ->
                str.matches(CATASTROPHIC_DISABILITY_BENEFIT + ".*"), CATASTROPHIC_DISABILITY_BENEFIT + ".*"));

        benefitsLTDInjuryPartyInformationTab.submitTab();

        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getLTDTestData().getTestData(claimTestDataKey, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(benefitCoverageDeterminationTab.getMetaKey(), APPROVED_THROUGH_DATE.getLabel()), "12/31/2099"), benefitsLTDIncidentTab.getClass());

        disabilityClaim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Test: Step 4");
        disabilityClaim.calculateSingleBenefitAmount().start(1);
        disabilityClaim.calculateSingleBenefitAmount().getWorkspace().fillUpTo(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), CoveragesActionTab.class);

        List<String> associatedInsurableRiskValues = disabilityClaim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class).getAssetList()
                .getAsset(ASSOCIATED_INSURABLE_RISK).getAllValues();

        assertThat(associatedInsurableRiskValues).doNotHave(new Condition<>(str ->
                str.matches(CATASTROPHIC_DISABILITY_BENEFIT + ".*"), CATASTROPHIC_DISABILITY_BENEFIT + ".*"));

        disabilityClaim.calculateSingleBenefitAmount().getWorkspace().fillFrom(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), CoveragesActionTab.class);
        disabilityClaim.calculateSingleBenefitAmount().submit();

        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bPStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1).getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        LOGGER.info("Test: Step 5");
        disabilityClaim.addPayment().start();
        disabilityClaim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                        .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_FROM_DATE.getLabel()), bPStartDate.format(MM_DD_YYYY))
                        .adjust(makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PAYMENT_THROUGH_DATE.getLabel()), bPStartDate.format(MM_DD_YYYY)),
                PaymentPaymentPaymentAllocationTab.class, true);

        assertThat(disabilityClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).getAssetList().getAsset(PaymentPaymentPaymentAllocationTabMetaData.ASSOCIATED_INSURABLE_RISK_LABEL).getValue())
                .doesNotContainPattern(CATASTROPHIC_DISABILITY_BENEFIT);

        paymentCalculatorTab.navigateToTab();

        Currency net1Value = new Currency(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT).getValue());

        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(CATASTROPHIC_DISABILITY_BENEFIT);

        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).isRequired().isDisabled().hasValue(EMPTY);
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE)).isRequired().hasValue(EMPTY);
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE)).isRequired().hasValue(EMPTY);
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_COMMENTS)).isOptional().hasValue(EMPTY);
        });

        LOGGER.info("Test: Step 6");
        paymentPaymentAllocationTab.navigateToTab();

        disabilityClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).fillTab(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"));
        paymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(bPStartDate.format(MM_DD_YYYY));
        paymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(bPStartDate.format(MM_DD_YYYY));

        paymentCalculatorTab.navigateToTab();
        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(CATASTROPHIC_DISABILITY_BENEFIT);

        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(bPStartDate.format(MM_DD_YYYY));
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(bPStartDate.format(MM_DD_YYYY));

        Currency net2Value = new Currency(200).divide(30).add(net1Value);

        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).isRequired().isDisabled().hasValue(new Currency(200).toString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(net2Value.toString());
        });

        disabilityClaim.addPayment().submit();

        LOGGER.info("Test: Step 9");
        toSubTab(OVERVIEW);

        if (cwcp) {
            claim.updateBenefit().start(1);
            benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(BenefitsLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS).setValue("30000");
        } else {
            claim.claimUpdate().start();
            policyInformationParticipantParticipantInformationTab.navigateToTab();
            policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS).setValue("30000");
        }

        Tab.buttonSaveAndExit.click();

        LOGGER.info("Test: Step 10");
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries"), PaymentSeriesPaymentAllocationActionTab.class, true);

        Currency net1ValuePaymentSeries = new Currency(claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesPaymentAllocationActionTab.class).getAssetList().getAsset(ALLOCATION_AMOUNT).getValue());

        paymentPaymentAllocationTab.submitTab();
        claim.createPaymentSeries().getWorkspace().fillFromTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries")
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EFFECTIVE_DATE.getLabel()), bPStartDate.plusYears(1).format(MM_DD_YYYY))
                .adjust(makeKeyPath(PaymentSeriesPaymentSeriesProfileActionTab.class.getSimpleName(), EXPIRATION_DATE.getLabel()), bPStartDate.plusYears(2).minusDays(1).format(MM_DD_YYYY)), PaymentSeriesAdditionalPayeeTab.class, PaymentSeriesCalculatorActionTab.class, true);

        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(CATASTROPHIC_DISABILITY_BENEFIT);
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(BEGINNING_DATE).setValue(bPStartDate.plusYears(1).format(MM_DD_YYYY));
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(THROUGH_DATE).setValue(bPStartDate.plusYears(1).plusDays(25).format(MM_DD_YYYY));

        claim.createPaymentSeries().submit();

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(bPStartDate.plusYears(2));
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);

        claim.updatePaymentSeries().start(1);   // workaround for avoiding Payment Series status Pending
        claim.updatePaymentSeries().submit();

        RetryService.run(predicate -> ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).isPresent(),
                () -> {
                    toSubTab(PAYMENTS);
                    return null;
                },
                StopStrategies.stopAfterAttempt(5),
                WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        claim.updatePayment().start(2);

        paymentCalculatorTab.navigateToTab();

        Currency net3Value = net1ValuePaymentSeries.add(1733.33);   // NET2 = NET1+2000/30*26

        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).isRequired().isDisabled().hasValue(new Currency(2000).toString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(net3Value.toString());
        });

        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        claim.updatePayment().start(3);
        paymentCalculatorTab.navigateToTab();

        assertSoftly(softly -> {
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(MONTHLY_BENEFIT_AMOUNT)).isRequired().isDisabled().hasValue(new Currency(2000).toString());
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(ALLOCATION_NET_AMOUNT)).hasValue(net1ValuePaymentSeries.toString()); // NET2 = NET1+0
        });
    }
}