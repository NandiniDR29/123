package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitLTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.LONG_TERM_DISABILITY;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.ADDITION_COMMENTS;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.BENEFIT;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageEvaluationTabMetaData.INSURED_PERSON_COVERAGE_EFFECTIVE_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FAMILY_CARE_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimBenefitOptionsMappingForFamilyCareBenefit extends ClaimGroupBenefitsLTDBaseTest {

    private static final String FAMILY_CARE_BENEFIT = "Family Care Benefit";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28168", component = CLAIMS_GROUPBENEFITS)
    public void testClaimBenefitOptionsMappingForFamilyCareBenefitIncluded() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks())
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), PlanDefinitionTabMetaData.OptionsMetaData.FAMILY_CARE_BENEFIT.getLabel()), "Not Included").resolveLinks()
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FAMILY_CARE_BENEFIT_AMOUNT.getLabel())).resolveLinks());
        createDefaultLongTermDisabilityCertificatePolicy();
        claim.create(tdClaim.getTestData("DataGatherCertificate", "TestData_Without_Benefits_Without_AdditionalParties"));

        LOGGER.info("Steps 1,2");
        claim.claimOpen().perform();
        claim.addBenefit().start(LONG_TERM_DISABILITY);
        claim.addBenefit().getWorkspace().getTab(BenefitLTDInjuryPartyInformationTab.class).getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).getAllValues().forEach(x -> assertThat(x).doesNotContain(FAMILY_CARE_BENEFIT));
        claim.addBenefit().getWorkspace().fillFromTo(tdClaim.getTestData("NewBenefit", "TestData_LTD_OtherValues")
                .mask(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), INSURED_PERSON_COVERAGE_EFFECTIVE_DATE.getLabel())), BenefitLTDInjuryPartyInformationTab.class, BenefitCoverageEvaluationTab.class, true);
        Tab.buttonSaveAndExit.click();

        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), PaymentPaymentPaymentAllocationTab.class, false);
        claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).getAssetList().getAsset(BENEFIT).getAllValues().forEach(x -> assertThat(x).doesNotContain(FAMILY_CARE_BENEFIT));

        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
        paymentCalculatorTab.navigate();
        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentCalculatorTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).getAllValues().forEach(x -> assertThat(x).doesNotContain(FAMILY_CARE_BENEFIT));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28168", component = CLAIMS_GROUPBENEFITS)
    public void testClaimBenefitOptionsMappingForFamilyCareBenefitNotIncluded() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData().adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));
        createDefaultLongTermDisabilityCertificatePolicy();
        claim.create(tdClaim.getTestData("DataGatherCertificate", "TestData_Without_Benefits_Without_AdditionalParties"));

        LOGGER.info("Step 3");
        claim.claimOpen().perform();
        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_LTD").mask(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), INSURED_PERSON_COVERAGE_EFFECTIVE_DATE.getLabel())));
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), PaymentCalculatorTab.class, false);

        LOGGER.info("Step 4");
        AbstractContainer<?, ?> paymentCalculatorTabAssetList = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class).getAssetList();
        paymentCalculatorTabAssetList.getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentCalculatorTabAssetList.getAsset(PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(FAMILY_CARE_BENEFIT);
        assertSoftly(softly -> {
            Stream.of(ADDITION_TYPE, BEGINNING_DATE, THROUGH_DATE, ADDITION_COMMENTS, NUMBER_OF_FAMILY_MEMBERS).forEach(x -> softly.assertThat(paymentCalculatorTabAssetList.getAsset(PAYMENT_ADDITION).getAsset(x)).isPresent());
            Stream.of(REMAINING_BENEFIT_MAXIMUM_FOR_CURRENT_CALENDAR_YEAR, BENEFIT_AMOUNT_PER_MEMBER_PER_MONTH, TOTAL_BENEFIT_AMOUNT).forEach(x -> softly.assertThat(paymentCalculatorTabAssetList.getAsset(x)).isPresent());

            LOGGER.info("Steps 5,14");
            Stream.of(REMAINING_BENEFIT_MAXIMUM_FOR_CURRENT_CALENDAR_YEAR, TOTAL_BENEFIT_AMOUNT).forEach(x -> softly.assertThat(paymentCalculatorTabAssetList.getAsset(x)).isDisabled().isRequired());

            LOGGER.info("Step 13");
            softly.assertThat(paymentCalculatorTabAssetList.getAsset(BENEFIT_AMOUNT_PER_MEMBER_PER_MONTH)).isDisabled().hasValue("$350.00");
        });
    }

}
