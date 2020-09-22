package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.SEPARATE_OR_COMBINED_EXPERIENCE_RATING;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.USE_EXPERIENCE_RATING;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PremiumSummaryTabMetaData.EXPERIENCE_RATING;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PremiumSummaryTabMetaData.ExperienceRatingMetaData.CREDIBILITY_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PremiumSummaryTabMetaData.ExperienceRatingMetaData.EXPERIENCE_CLAIM_AMOUNT;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestExperienceClaimAmountCredibilityFactorForRating extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-27669"}, component = POLICY_GROUPBENEFITS)
    public void testExperienceClaimAmountCredibilityFactorForRating() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        TestData tdPolicy = getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getClass().getSimpleName(), USE_EXPERIENCE_RATING.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(policyInformationTab.getClass().getSimpleName(), SEPARATE_OR_COMBINED_EXPERIENCE_RATING.getLabel()), "Separate")
                .adjust(planDefinitionTab.getClass().getSimpleName(), getDefaultTLMasterPolicyData().getTestDataList(planDefinitionTab.getMetaKey()).subList(0, 2))
                .resolveLinks();
        termLifeInsuranceMasterPolicy.initiate(tdPolicy);
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(tdPolicy, planDefinitionTab.getClass(), true);

        LOGGER.info("REN-27669 Step 2");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetaData.EXPERIENCE_CLAIM_AMOUNT).setValue("-1");
        Tab.buttonNext.click();
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetaData.EXPERIENCE_CLAIM_AMOUNT))
                .hasWarningWithText("Experience Claim Amount must be equal or greater than $0.00");

        LOGGER.info("REN-27669 Step 5");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetaData.CREDIBILITY_FACTOR).setValue("1");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetaData.EXPERIENCE_CLAIM_AMOUNT).setValue("0");
        Tab.buttonNext.click();
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetaData.CREDIBILITY_FACTOR))
                .hasWarningWithText("When Credibility Factor = 1, the Experience Claim Amount must be greater than $0.00");

        LOGGER.info("REN-REN-27669 Step 8");
        NavigationPage.toLeftMenuTab(POLICY_INFORMATION);
        policyInformationTab.getAssetList().getAsset(SEPARATE_OR_COMBINED_EXPERIENCE_RATING).setValue("Combined");
        NavigationPage.toLeftMenuTab(PREMIUM_SUMMARY);
        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("-1");
        Tab.buttonNext.click();
        assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT))
                .hasWarningWithText("Experience Claim Amount must be equal or greater than $0.00");

        LOGGER.info("REN-REN-27669 Step 9");
        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(CREDIBILITY_FACTOR).setValue("1");
        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("0");
        Tab.buttonNext.click();
        assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(CREDIBILITY_FACTOR))
                .hasWarningWithText("When Credibility Factor = 1, the Experience Claim Amount must be greater than $0.00");

        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(CREDIBILITY_FACTOR).setValue("1");
        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("-1");
        Tab.buttonNext.click();
        assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT))
                .hasWarningWithText("Experience Claim Amount must be equal or greater than $0.00");
        assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(CREDIBILITY_FACTOR)).hasNoWarning();
    }
}
