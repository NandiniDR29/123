package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.claim.common.tabs.LossEventTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FNOL;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.claim.common.metadata.ClaimHandlingSpecialHandlingTabMetaData.MENTAL_NERVOUS;
import static com.exigen.ren.main.modules.claim.common.metadata.ClaimHandlingSpecialHandlingTabMetaData.REINSURANCE;
import static com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDIncidentTabMetaData.DATE_OF_HIRE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RELATIONSHIP_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.RelationshipInformationMetaData.ANNUAL_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.PRESUMPTIVE_DISABILITY;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.PARTICIPANT_CONTRIBUTION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckCountyCodePaymentCalculationForAllBenefits extends ClaimGroupBenefitsLTDBaseTest {
    private PaymentPaymentPaymentAllocationTab paymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-28462", "REN-38139", "REN-44215", "REN-45704"}, component = CLAIMS_GROUPBENEFITS)
    public void testCheckCountyCodePaymentCalculationForAllBenefits1() {
        final String PRESUMPTIVE_DISABILITY_BENEFIT = "Presumptive Disability Benefit";
        LocalDate currentDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_CON"))
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDate.minusMonths(6).format(MM_DD_YYYY))
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), SPONSOR_PAYMENT_MODE.getLabel()))
                .mask(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), PARTICIPANT_CONTRIBUTION.getLabel()))
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), CONTRIBUTION_TYPE.getLabel()), "Voluntary")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), PRESUMPTIVE_DISABILITY.getLabel()), "90 days")
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()), "60%").resolveLinks());

        longTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultLTDCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), RELATIONSHIP_INFORMATION.getLabel(), ANNUAL_EARNINGS.getLabel()), "12000")
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        LOGGER.info("TEST REN-28462, REN-38139: Step 1-2");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), currentDate.minusMonths(6).format(MM_DD_YYYY))
                .adjust(makeKeyPath(benefitsLTDInjuryPartyInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(makeKeyPath(benefitsLTDIncidentTab.getMetaKey(), DATE_OF_HIRE.getLabel()), currentDate.minusMonths(7).format(MM_DD_YYYY)));

        LOGGER.info("REN-44215 Step 1");
        toSubTab(FNOL);
        claimHandlingSpecialHandlingTab.navigateToTab();
        assertThat(claimHandlingSpecialHandlingTab.getAssetList().getAsset(REINSURANCE)).isPresent().isEnabled().hasValue(ValueConstants.EMPTY);

        LOGGER.info("REN-45704 Step# 1");
        assertThat(claimHandlingSpecialHandlingTab.getAssetList().getAsset(MENTAL_NERVOUS)).isPresent().isEnabled().hasValue(ValueConstants.EMPTY);

        LOGGER.info("REN-45704 Step# 2");
        claimHandlingSpecialHandlingTab.getAssetList().getAsset(MENTAL_NERVOUS).setValue(VALUE_YES);
        Tab.buttonSaveAndExit.click();
        assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue("Mental Nervous");

        LOGGER.info("REN-44215 Step 2");
        toSubTab(FNOL);
        claimHandlingSpecialHandlingTab.navigateToTab();
        claimHandlingSpecialHandlingTab.getAssetList().getAsset(MENTAL_NERVOUS).setValue(VALUE_NO);
        claimHandlingSpecialHandlingTab.getAssetList().getAsset(REINSURANCE).setValue(VALUE_YES);
        Tab.buttonSaveAndExit.click();
        assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue("Reinsurance");

        claim.claimOpen().perform();

        LOGGER.info("TEST REN-28462, REN-38139: Step 6");
        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData"), 1);
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        LOGGER.info("TEST REN-28462, REN-38139: Step 7");
        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), currentDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), currentDate.plusDays(9).format(MM_DD_YYYY)), paymentAllocationTab.getClass(), true);
        assertThat(paymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT)).containsOption(PRESUMPTIVE_DISABILITY_BENEFIT);

        paymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT).setValue(PRESUMPTIVE_DISABILITY_BENEFIT);
        assertThat(paymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue("$200.00");

        LOGGER.info("TEST REN-28462, REN-38139: Step 9");
        claim.addPayment().submit();
        claim.paymentInquiry().start(1);
        paymentAllocationTab.navigate();
        assertThat(new StaticElement(IN_LIEU_BENEFIT.getLocator())).hasValue(PRESUMPTIVE_DISABILITY_BENEFIT);
    }
}
