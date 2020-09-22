package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.ELIGIBILITY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_AN_ASSOCIATION;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddPlanDefinitionFieldsToIssueAction extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {
    private AssetList EligibilityRef = planDefinitionTab.getAssetList().getAsset(ELIGIBILITY);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21804", "REN-21805", "REN-21806", "REN-21807", "REN-21809"}, component = POLICY_GROUPBENEFITS)
    public void testAddPlanDefinitionFieldsToIssueAction() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), planDefinitionTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN-21804 Step 1, Step2");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_NO);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(PolicyConstants.PlanDental.ALACARTE));
            softly.assertThat(EligibilityRef).isPresent();
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.DOES_MIN_HOURLY_REQUIREMENT_APPLY)).isDisabled().isOptional().hasValue(VALUE_YES);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_YES);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(EligibilityRef).isPresent();
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.DOES_MIN_HOURLY_REQUIREMENT_APPLY)).isAbsent();
            LOGGER.info("REN-21804 Step 4, Step 5");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_NO);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.DOES_MIN_HOURLY_REQUIREMENT_APPLY)).isDisabled().hasValue(VALUE_YES);
            LOGGER.info("REN-21805 TC1 Step 1, Step 2 and REN-21805 TC2 Step1, Step 2 , Step 3");
            softly.assertThat(EligibilityRef).isPresent();
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.MINIMUM_HOURLY_REQUIREMENT)).isPresent();
            EligibilityRef.getAsset(EligibilityMetaData.MINIMUM_HOURLY_REQUIREMENT).setValue("23");
            LOGGER.info("REN-21806 Step 1");
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.ELIGIBILITY_WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES)).isPresent().isOptional();
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.ELIGIBILITY_WAITING_PERIOD_DEFINITION)).isPresent().isOptional();
            EligibilityRef.getAsset(EligibilityMetaData.ELIGIBILITY_WAITING_PERIOD_DEFINITION).setValue("Amount and Mode Only");
            LOGGER.info("REN-21807 Step 1, Step 5 , Step 6 , Step 8");
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.WAITING_PERIOD_AMOUNT)).isPresent().isOptional();
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.WAITING_PERIOD_MODE)).isPresent().isOptional();

            ImmutableList.of("0", "365", "22").forEach(Value1 -> {
                EligibilityRef.getAsset(PlanDefinitionTabMetaData.EligibilityMetaData.WAITING_PERIOD_AMOUNT).setValue(Value1);
                softly.assertThat(EligibilityRef.getAsset(PlanDefinitionTabMetaData.EligibilityMetaData.WAITING_PERIOD_AMOUNT)).hasNoWarning();
            });
            ImmutableList.of("-1", "366").forEach(Value2 -> {
                EligibilityRef.getAsset(EligibilityMetaData.WAITING_PERIOD_AMOUNT).setValue(Value2);
                softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.WAITING_PERIOD_AMOUNT)).hasWarningWithText("'Waiting Period Amount' should be within [0, 365], and the increment is 1.");
            });
            LOGGER.info("REN-21809 Step 1, Step 5 , Step 6 , Step 8");
            softly.assertThat(EligibilityRef).isPresent();
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.DEPENDENT_MAXIMUM_AGE)).isPresent().isOptional();
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.INCLUDE_DISABLED_DEPENDENTS)).isPresent().isOptional();
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.ALLOW_MEMBER_AND_SPOUSE_WHO_ARE_PART_OF_GROUP_ON_SEPARATE_CERTIFICATE)).isPresent().isOptional();
            softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.BENEFIT_END_ON)).isPresent().isOptional();

            ImmutableList.of("17", "27").forEach(Value3 -> {
                EligibilityRef.getAsset(EligibilityMetaData.DEPENDENT_MAXIMUM_AGE).setValue(Value3);
                softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.DEPENDENT_MAXIMUM_AGE)).hasWarningWithText("'Dependent Maximum Age' should be within [18, 26], and the increment is 1.");
            });
            ImmutableList.of("18", "26", "24").forEach(Value4 -> {
                EligibilityRef.getAsset(EligibilityMetaData.DEPENDENT_MAXIMUM_AGE).setValue(Value4);
                softly.assertThat(EligibilityRef.getAsset(EligibilityMetaData.DEPENDENT_MAXIMUM_AGE)).hasNoWarning();
            });

            LOGGER.info("REN-21804 Step 5 , REN-21805 TC1 Step 3 and TC2 Step 4, REN-21806 Step 4 , REN-21807 Step 10 , REN-21809 Step 10");
            groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }
}