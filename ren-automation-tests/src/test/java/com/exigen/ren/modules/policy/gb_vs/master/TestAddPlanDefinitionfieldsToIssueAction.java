package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.USER_10_LOGIN;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.ELIGIBILITY;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_AN_ASSOCIATION;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddPlanDefinitionfieldsToIssueAction extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    private AssetList EligibilityRef = planDefinitionTab.getAssetList().getAsset(ELIGIBILITY);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21648","REN-21717"}, component = POLICY_GROUPBENEFITS)
    public void testAddPlanDefinitionfieldsToIssueAction() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN-21648 TC1 Step 1,2 and REN-21717 TC1 Step 1 and Step 3");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_NO);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.selectDefaultPlan();
            softly.assertThat(EligibilityRef).isPresent();
            softly.assertThat(EligibilityRef.getAsset(DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY)).isPresent().hasValue(VALUE_YES).isDisabled();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_NO);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(EligibilityRef.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).isPresent().isOptional();
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("REN-21648 TC2 Step 1-4");
            groupVisionMasterPolicy.dataGather().start();
            policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_YES);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.selectDefaultPlan();
            softly.assertThat(EligibilityRef).isPresent();
            softly.assertThat(EligibilityRef.getAsset(DOES_MINIMUM_HOURLY_REQUIREMENT_APPLY)).isAbsent();
            premiumSummaryTab.navigate();
            premiumSummaryTab.submitTab();
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }
    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21717"}, component = POLICY_GROUPBENEFITS)
    public void testUser1() {
        LOGGER.info("REN-21717 TC2 Step 1 and Step 4");
        mainApp().reopen(USER_10_LOGIN, "qa");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass());
        assertSoftly(softly -> {
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_NO);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.selectDefaultPlan();
            softly.assertThat(EligibilityRef).isPresent();
            softly.assertThat(EligibilityRef.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK)).isPresent().isOptional();
            EligibilityRef.getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue("25");
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }
}