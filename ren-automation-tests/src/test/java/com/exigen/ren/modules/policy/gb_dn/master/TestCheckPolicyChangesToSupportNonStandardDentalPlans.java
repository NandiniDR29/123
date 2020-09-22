package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.LimitationFrequencyMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ASOALC;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.BASEPOS;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSelectionValues.ASOALC_ASO_ALACARTE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSelectionValues.PLAN_BASEPOS;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.OVERRIDE_RULES_LIST_KEY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMajorMetaData.TISSUE_CONDITIONING;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalPreventAndDiagnosticMetaData.TWO_ADDITIONAL_PERIO_MAINTENANCE_VISITS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckPolicyChangesToSupportNonStandardDentalPlans extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private final String INFO_MESSAGE = "Cover 2 additional periodontal maintenance visits per year";
    private final String TISSUE_CONDITIONING_VALUE = "Once Every 2 Years";
    private final String TISSUE_CONDITIONING_DEFAULT_VALUE = "Twice Every 3 Years";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-37410"}, component = POLICY_GROUPBENEFITS)
    public void testCheckAttributesAndRulesOnGdAsoAlaCarte() {
        LOGGER.info("REN-37410 Preconditions");
        TestData testData = getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), ASO_PLAN.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[0]", PlanDefinitionTabMetaData.PLAN.getLabel()), ASOALC)
                .adjust(TestData.makeKeyPath(classificationManagementMpTab.getClass().getSimpleName(), PlanDefinitionTabMetaData.PLAN.getLabel()), ASOALC_ASO_ALACARTE)
                .adjust(TestData.makeKeyPath(proposalActionTab.getMetaKey(), ProposalTabMetaData.PROPOSED_ASO_FEE.getLabel()), "1")
                .adjust(TestData.makeKeyPath(proposalActionTab.getMetaKey(), ProposalTabMetaData.FEE_UPDATE_REASON.getLabel()), "index=1")
                .adjust(TestData.makeKeyPath(proposalActionTab.getMetaKey(), OVERRIDE_RULES_LIST_KEY), ImmutableList.of("Proposal for ASO Plan will require Underwriter approval"));
        createCustomerCaseProfileInitiateMqAndFillPlanTab(testData);

        LOGGER.info("REN-37410 Step#1 to 6 Check attributes");
        checkAttributes(true);

        LOGGER.info("REN-37410 Step#7 Rate Master Quote");
        Currency premiumCalculated = fillMandatoryFieldsRateMasterQuoteAndGetPremium(testData);

        LOGGER.info("REN-37410 Step#8 to 11 Go to Plan Definition Change Attribute And Check Premium Calculated ");
        goToPlanDefinitionChangeAttributeAndCheckPremium(premiumCalculated);

        LOGGER.info("REN-37410 Step#12 Propose >> Accept Contract and Issue MQ1 and check attributes");
        planDefinitionTab.submitTab();
        groupDentalMasterPolicy.propose().perform(testData);
        acceptIssueAndCheck(testData);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-37441"}, component = POLICY_GROUPBENEFITS)
    public void testCheckAttributesAndRulesOnGdAlaCarte() {
        LOGGER.info("REN-37441 Preconditions");
        createCustomerCaseProfileInitiateMqAndFillPlanTab(getDefaultDNMasterPolicyData());

        LOGGER.info("REN-37441 Step#1 to 6 Check attributes");
        checkAttributes(true);

        LOGGER.info("REN-37441 Step#7 Rate Master Quote");
        Currency premiumCalculated = fillMandatoryFieldsRateMasterQuoteAndGetPremium(getDefaultDNMasterPolicyData());

        LOGGER.info("REN-37441 Step#8 to 11 Go to Plan Definition Change Attribute And Check Premium Calculated ");
        goToPlanDefinitionChangeAttributeAndCheckPremium(premiumCalculated);

        LOGGER.info("REN-37441 Step#12 Propose >> Accept Contract and Issue MQ1 and check attributes");
        planDefinitionTab.submitTab();
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        acceptIssueAndCheck(getDefaultDNMasterPolicyData());
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-37443"}, component = POLICY_GROUPBENEFITS)
    public void testCheckAttributesAndRulesOnGdBasicEpos() {
        LOGGER.info("REN-37443 Preconditions");
        TestData testData = getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[0]", PlanDefinitionTabMetaData.PLAN.getLabel()), BASEPOS)
                .adjust(TestData.makeKeyPath(classificationManagementMpTab.getClass().getSimpleName(), PlanDefinitionTabMetaData.PLAN.getLabel()), PLAN_BASEPOS);
        createCustomerCaseProfileInitiateMqAndFillPlanTab(testData);

        LOGGER.info("REN-37443 Step#1 to 6 Check attributes");
        checkAttributes(false);

        LOGGER.info("REN-37443 Step#7 Rate Master Quote");
        Currency premiumCalculated = fillMandatoryFieldsRateMasterQuoteAndGetPremium(testData);

        LOGGER.info("REN-37443 Step#08 Rating info was NOT cleared or changed after select new value for Tissue Conditioning attribute");
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY).getAsset(LimitationFrequencyMetaData.DENTAL_MAJOR).getAsset(TISSUE_CONDITIONING).setValue(TISSUE_CONDITIONING_VALUE);
        verificationRatingInfoChangingAttribute(premiumCalculated);

        LOGGER.info("REN-37443 Step#9 Propose >> Accept Contract and Issue MQ1 and check attributes");
        planDefinitionTab.submitTab();
        groupDentalMasterPolicy.propose().perform();
        acceptIssueAndCheck(getDefaultDNMasterPolicyData());
    }

    private void verificationRatingInfoChangingAttribute(Currency premiumCalculated) {
        premiumSummaryTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
            softly.assertThat(premiumCalculated).isEqualTo(PremiumSummaryTab.getPremium());
        });
    }

    private void createCustomerCaseProfileInitiateMqAndFillPlanTab(TestData testData) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        initiateQuoteAndFillUpToTab(testData, PlanDefinitionTab.class, true);
    }

    private void checkAttributes(Boolean isEnabled) {
        assertSoftly(softly -> {
            softly.assertThat(PlanDefinitionTab.TWO_ADDITIONAL_PERIO_MAINTENANCE_VISITS_INFO.getAttribute("title")).contains(INFO_MESSAGE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY).getAsset(LimitationFrequencyMetaData.DENTAL_MAJOR).getAsset(TISSUE_CONDITIONING)).isEnabled().isRequired().hasValue(TISSUE_CONDITIONING_DEFAULT_VALUE).containsAllOptions(TISSUE_CONDITIONING_VALUE, TISSUE_CONDITIONING_DEFAULT_VALUE);
        });
        if (isEnabled) {
            assertSoftly(softly -> {
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY).getAsset(LimitationFrequencyMetaData.DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(TWO_ADDITIONAL_PERIO_MAINTENANCE_VISITS)).isEnabled().isRequired().hasValue(VALUE_NO);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.DENTAL_DEDUCTIBLE).getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.DEDUCTIBLE_CARRYOVER)).isEnabled();
                softly.assertThat(planDefinitionTab.getAssetList()
                        .getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY)
                        .getAsset(PlanDefinitionTabMetaData.LimitationFrequencyMetaData.EXCLUSIONS)
                        .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.COSMETIC_SERVICES)).isEnabled();
            });
        } else {
            assertSoftly(softly -> {
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY).getAsset(LimitationFrequencyMetaData.DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(TWO_ADDITIONAL_PERIO_MAINTENANCE_VISITS)).isDisabled().isRequired().hasValue(VALUE_NO);
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.DENTAL_DEDUCTIBLE).getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.DEDUCTIBLE_CARRYOVER)).isDisabled();
                softly.assertThat(planDefinitionTab.getAssetList()
                        .getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY)
                        .getAsset(PlanDefinitionTabMetaData.LimitationFrequencyMetaData.EXCLUSIONS)
                        .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.COSMETIC_SERVICES)).isDisabled();
            });
        }
    }

    private Currency fillMandatoryFieldsRateMasterQuoteAndGetPremium(TestData testData) {
        planDefinitionTab.submitTab();
        groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(testData, classificationManagementMpTab.getClass(), premiumSummaryTab.getClass(), false);
        premiumSummaryTab.rate();
        CustomAssertions.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
        return PremiumSummaryTab.getPremium();
    }

    private void acceptIssueAndCheck(TestData testData) {
        groupDentalMasterPolicy.acceptContract().perform(testData);
        groupDentalMasterPolicy.issue().perform(testData);
        assertSoftly(softly -> {
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY).getAsset(LimitationFrequencyMetaData.DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(TWO_ADDITIONAL_PERIO_MAINTENANCE_VISITS)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY).getAsset(LimitationFrequencyMetaData.DENTAL_MAJOR).getAsset(TISSUE_CONDITIONING)).isAbsent();
        });
    }

    private void goToPlanDefinitionChangeAttributeAndCheckPremium(Currency premiumCalculated) {
        LOGGER.info("Step#08 Rating info was NOT cleared or changed after select new value for Tissue Conditioning attribute");
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY).getAsset(LimitationFrequencyMetaData.DENTAL_MAJOR).getAsset(TISSUE_CONDITIONING).setValue(TISSUE_CONDITIONING_VALUE);
        verificationRatingInfoChangingAttribute(premiumCalculated);

        LOGGER.info("Step#09 Rating info was NOT cleared or changed after select new value for 2 Additional Perio Maintenance Visits attribute");
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY).getAsset(LimitationFrequencyMetaData.DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(TWO_ADDITIONAL_PERIO_MAINTENANCE_VISITS).setValue(VALUE_YES);
        verificationRatingInfoChangingAttribute(premiumCalculated);

        LOGGER.info("Step#10 Rating info was NOT cleared or changed after select new value for Deductible Carryover attribute");
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.DENTAL_DEDUCTIBLE).getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.DEDUCTIBLE_CARRYOVER).setValue(VALUE_YES);
        verificationRatingInfoChangingAttribute(premiumCalculated);

        LOGGER.info("Step#11 Rating info was NOT cleared or changed after select new value for Cosmetic Services attribute");
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList()
                .getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY)
                .getAsset(PlanDefinitionTabMetaData.LimitationFrequencyMetaData.EXCLUSIONS)
                .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.COSMETIC_SERVICES).setValue(false);
        verificationRatingInfoChangingAttribute(premiumCalculated);
    }
}
