package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.USER_10_LOGIN;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.ELIGIBILITY;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.RATE_TYPE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_AN_ASSOCIATION;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.RENEWAL_NOTIFICATION_DAYS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureRatingAndEligibility extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {
    private AssetList eligibilityAsset = planDefinitionTab.getAssetList().getAsset(ELIGIBILITY);
    private AssetList ratingAsset = planDefinitionTab.getAssetList().getAsset(RATING);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-15811", "REN-15837", "REN-15835"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureRatingAndEligibility() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), policyInformationTab.getClass());

        RadioGroup groupIsAnAssociation = policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION);

        LOGGER.info("REN-15811 Step 1,2");
        assertSoftly(softly -> {
            softly.assertThat(groupIsAnAssociation).isPresent().isEnabled().isRequired().hasValue(VALUE_NO);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RENEWAL_NOTIFICATION_DAYS)).isPresent().isEnabled().isRequired().hasValue("60");
            policyInformationTab.getAssetList().getAsset(RENEWAL_NOTIFICATION_DAYS).setValue("80");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(RENEWAL_NOTIFICATION_DAYS)).hasValue("80");
        });

        LOGGER.info("REN-15837 Step 1.1");
        groupIsAnAssociation.setValue(VALUE_NO);
        planDefinitionTab.navigateToTab();
        planDefinitionTab.selectDefaultPlan();
        assertThat(eligibilityAsset.getAsset(DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY)).isPresent().isDisabled().hasValue("Yes");

        LOGGER.info("REN-15837 Step 1.2");
        policyInformationTab.navigateToTab();
        groupIsAnAssociation.setValue(VALUE_YES);
        planDefinitionTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(eligibilityAsset.getAsset(DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY)).isAbsent();
            softly.assertThat(eligibilityAsset.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).isAbsent();
        });

        LOGGER.info("REN-15837 Step 2");
        policyInformationTab.navigateToTab();
        groupIsAnAssociation.setValue(VALUE_NO);
        planDefinitionTab.navigateToTab();
        assertThat(eligibilityAsset.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).isPresent().isOptional().isEnabled();

        LOGGER.info("REN-15837 Step 3");
        assertThat(planDefinitionTab.getAssetList().getAsset(ELIGIBILITY).getAsset(ELIGIBILITY_WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEE))
                .isPresent().isEnabled().isOptional().hasValue(ValueConstants.EMPTY);

        LOGGER.info("REN-15837 Step 5.1");
        assertSoftly(softly -> {
            ImmutableList.of(
                    "First of the month coincident with or next following date of hire",
                    "First of the month following date of hire",
                    "None").forEach(value -> {
                eligibilityAsset.getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue(value);
                softly.assertThat(eligibilityAsset.getAsset(WAITING_PERIOD_AMOUNT)).isAbsent();
                softly.assertThat(eligibilityAsset.getAsset(WAITING_PERIOD_MODE)).isAbsent();
            });
        });

        LOGGER.info("REN-15837 Step 5.2");
        ImmutableList.of(
                "Amount and Mode Only",
                "First of the month coincident with or next following (amount and mode)",
                "First of the month following (amount and mode)").forEach(value -> {
            eligibilityAsset.getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue(value);
            assertSoftly(softly -> {
                softly.assertThat(eligibilityAsset.getAsset(WAITING_PERIOD_AMOUNT)).isPresent().isEnabled().isOptional();
                softly.assertThat(eligibilityAsset.getAsset(WAITING_PERIOD_MODE)).isPresent().isEnabled().isOptional();
            });
        });

        LOGGER.info("REN-15837 Step 7");
        assertThat(eligibilityAsset.getAsset(ALLOW_MEMBER_AND_SPOUSE_ON_SEPARATE_CERTIFICATE)).isPresent().isEnabled().isOptional().hasValue(ValueConstants.EMPTY);

        LOGGER.info("REN-15835 Step 1.1-2");
        assertSoftly(softly -> {
            softly.assertThat(ratingAsset.getAsset(RATE_TYPE)).isPresent().isEnabled().isRequired().hasOptions(ImmutableList.of("Family Tier", "Area + Tier"));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.PLAN_TYPE)).isPresent().isEnabled().isRequired().hasOptions(ImmutableList.of("Full Feature"));
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-15835"}, component = POLICY_GROUPBENEFITS)
    public void testRatingSectionWithUserLevelBelowOne() {
        mainApp().reopen(USER_10_LOGIN, "qa");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.selectDefaultPlan();
        LOGGER.info("REN-15835 Step 1.2");
        assertThat(ratingAsset.getAsset(RATE_TYPE)).isPresent().isDisabled();
    }
}