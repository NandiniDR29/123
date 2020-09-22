package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PremiumSummaryTabMetaData.CREDIBILITY_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PremiumSummaryTabMetaData.EXPERIENCE_CLAIM_AMOUNT;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestExperienceClaimAmountCredibilityFactorForRating extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18352", "REN-18281"}, component = POLICY_GROUPBENEFITS)
    public void testExperienceClaimAmountCredibilityFactorForRating() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), premiumSummaryTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("{REN-18281,REN-18352} Step 1,5");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_CLAIM_AMOUNT)).isPresent().isRequired().isEnabled().hasValue(new Currency('0').toString());
            premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("6");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(CREDIBILITY_FACTOR)).isPresent().isRequired().isEnabled().hasValue("0.0000000");
            premiumSummaryTab.getAssetList().getAsset(CREDIBILITY_FACTOR).setValue("1.0000000");
            premiumSummaryTab.fillTab(getDefaultLTDMasterPolicyData()).submitTab();
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-26699", component = POLICY_GROUPBENEFITS)
    public void testExperienceClaimAmountCredibilityFactorForRating_NegativeScenario() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), planDefinitionTab.getClass(), true);

        LOGGER.info("REN-26699 Step 2");
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RatingMetaData.EXPERIENCE_CLAIM_AMOUNT).setValue("-1");
        Tab.buttonNext.click();
        assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RatingMetaData.EXPERIENCE_CLAIM_AMOUNT))
                .hasWarningWithText("Experience Claim Amount must be equal or greater than $0.00");

        LOGGER.info("REN-26699 Step 5");
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RatingMetaData.CREDIBILITY_FACTOR).setValue("1");
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RatingMetaData.EXPERIENCE_CLAIM_AMOUNT).setValue("0");
        Tab.buttonNext.click();
        assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(RatingMetaData.CREDIBILITY_FACTOR))
                .hasWarningWithText("When Credibility Factor = 1, the Experience Claim Amount must be greater than $0.00");

        LOGGER.info("REN-26699 Step 8");
        NavigationPage.toLeftMenuTab(PREMIUM_SUMMARY);
        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("-1");
        Tab.buttonNext.click();
        assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_CLAIM_AMOUNT))
                .hasWarningWithText("Experience Claim Amount must be equal or greater than $0.00");

        LOGGER.info("REN-26699 Step 9");
        premiumSummaryTab.getAssetList().getAsset(CREDIBILITY_FACTOR).setValue("1");
        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("0");
        Tab.buttonNext.click();
        assertThat(premiumSummaryTab.getAssetList().getAsset(CREDIBILITY_FACTOR))
                .hasWarningWithText("When Credibility Factor = 1, the Experience Claim Amount must be greater than $0.00");

        premiumSummaryTab.getAssetList().getAsset(CREDIBILITY_FACTOR).setValue("1");
        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("-1");
        Tab.buttonNext.click();
        assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_CLAIM_AMOUNT))
                .hasWarningWithText("Experience Claim Amount must be equal or greater than $0.00");
        assertThat(premiumSummaryTab.getAssetList().getAsset(CREDIBILITY_FACTOR)).hasNoWarning();
    }
}