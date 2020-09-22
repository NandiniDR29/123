package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSelectionValues.PLAN_ALACARTE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.CENSUS_TYPE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PremiumSummaryTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PremiumSummaryTabMetaData.ExperienceRatingMetaData.CREDIBILITY_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PremiumSummaryTabMetaData.ExperienceRatingMetaData.EXPERIENCE_CLAIM_AMOUNT;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigurePremiumSummaryUI extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21002", "REN-21005"}, component = POLICY_GROUPBENEFITS)
    public void testConfigurePremiumSummaryUI() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), premiumSummaryTab.getClass());
        PremiumSummaryTab.buttonRate.click();

        assertSoftly(softly -> {
            LOGGER.info("REN 21002 TC1 TC2 Step 1");
            softly.assertThat(PremiumSummaryTab.premiumSummaryCoveragesTable.getRow(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), PLAN_ALACARTE)).isPresent();

            LOGGER.info("REN 21002 TC1 Step 3,6");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS)).isPresent().isEnabled().hasValue(StringUtils.EMPTY);
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(APPLY)).isPresent().isDisabled();

            LOGGER.info("REN 21002 TC1 Step 4");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Volume"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Request New Rates"))).isAbsent();

            LOGGER.info("REN 21002 TC2 Step 2,3");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(CENSUS_TYPE).setValue("None");
            premiumSummaryTab.navigate();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(SELECT_RATING_CENSUS)).isAbsent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(APPLY)).isAbsent();

            LOGGER.info("REN 21005 TC1 Step 1");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING)).isPresent();

            LOGGER.info("REN 21005 TC1 Step 2,4");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT))
                    .isPresent().isRequired().isEnabled().hasValue(new Currency(0).toString());

            LOGGER.info("REN 21005 TC1 Step 3,5");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(CREDIBILITY_FACTOR))
                    .isPresent().isRequired().isEnabled().hasValue("0.0000000");

            LOGGER.info("REN 21005 TC1 Step 12");
            premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("10");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT))
                    .hasValue(new Currency(10).toString());

            LOGGER.info("REN 21005 TC1 Step 15");
            premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("54321.99");
            premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(CREDIBILITY_FACTOR).setValue("0.8000001");

            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("REN 21005 TC1 Step 16");
            groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

            LOGGER.info("REN 21005 TC1 Step 18");
            groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

            LOGGER.info("REN 21005 TC1 Step 20,21");
            groupVisionMasterPolicy.issue().perform(getDefaultVSMasterPolicyData());
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        });
    }
}
