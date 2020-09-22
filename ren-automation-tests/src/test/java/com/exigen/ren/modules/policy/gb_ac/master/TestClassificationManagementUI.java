package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ActionConstants.REMOVE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.BASE_BUY_UP;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.ClassificationManagementTab.tablePlanTierAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClassificationManagementUI extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {
    private static final ImmutableList<String> COVERAGE_TIERS_VAR = ImmutableList.of("Employee Only", "Employee + Spouse", "Employee + Children", "Employee + Family");
    private static final ImmutableList<String> COVERAGE_TIERS_VAR1 = ImmutableList.of("", "Employee Only", "Employee + Spouse", "Employee + Children", "Employee + Family");
    private static final String COVERAGE_TIER_EMPLOYEE_ONLY = "Employee Only";
    private static final MultiAssetList classificationManagementTabList = (MultiAssetList) GroupAccidentMasterPolicyContext.classificationManagementMPTab.getAssetList();

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21506", "REN-21511", "REN-21703"}, component = POLICY_GROUPBENEFITS)
    public void testClassificationManagementUi() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData(), ClassificationManagementTab.class);
        classificationManagementTabList.getAsset(ADD_CLASSIFICATION_GROUP_RELATIONSHIP).click();
        assertSoftly(softly -> {
            LOGGER.info("REN-21506 step 1");
            softly.assertThat(classificationManagementTabList.getAsset(PLAN_TIER_AND_RATING_INFO)).isPresent();
            softly.assertThat(classificationManagementTabList.getAsset(NUMBER_OF_PARTICIPANTS)).isPresent().isOptional().isEnabled().hasValue("0");
            softly.assertThat(classificationManagementTabList.getAsset(RATE_BASIS)).isPresent().isDisabled().isRequired();
            softly.assertThat(classificationManagementTabList.getAsset(RATE)).isPresent().isOptional().isDisabled().hasValue(new Currency(1).toString());
            softly.assertThat(tablePlanTierAndRatingInfo.getHeader().getValue().subList(1, 4)).hasSameElementsAs(ImmutableList.of("Coverage Tier", "Number of Participants", "Rate"));

            LOGGER.info("REN-21511 step 1 step 2 and REN-21703 step 1");
            planDefinitionTab.navigateToTab();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(BASE_BUY_UP));
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_TIERS).setValue(COVERAGE_TIERS_VAR);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get());
            classificationManagementTabList.getAsset(ADD_CLASSIFICATION_GROUP_RELATIONSHIP).click();
            classificationManagementTabList.getAsset(CLASSIFICATION_GROUP_NAME).setValue("Employment");
            ImmutableList.of("Employee + Spouse", "Employee + Children", "Employee + Family").forEach(coverageName -> {
                tablePlanTierAndRatingInfo.getRow(ImmutableMap.of(TableConstants.PlanTierAndRatingSelection.COVERAGE_TIER.getName(), coverageName)).getCell(5).controls.links.get(REMOVE).click();
                Page.dialogConfirmation.confirm();});
            classificationManagementTabList.getAsset(ADD_COVERAGE_TIER).click();
            tablePlanTierAndRatingInfo.getRow(ImmutableMap.of(TableConstants.PlanTierAndRatingSelection.COVERAGE_TIER.getName(), COVERAGE_TIER_EMPLOYEE_ONLY)).getCell(5).controls.links.get(REMOVE).click();
            Page.dialogConfirmation.confirm();

            softly.assertThat(classificationManagementTabList.getAsset(USE_CLASSIFICATION_SUB_GROUPS)).isPresent().isRequired().isDisabled().hasValue(VALUE_NO);
            softly.assertThat(classificationManagementTabList.getAsset(COVERAGE_TIER)).isPresent().isEnabled().isRequired().hasValue(StringUtils.EMPTY).hasOptions(COVERAGE_TIERS_VAR1);
            softly.assertThat(classificationManagementTabList.getAsset(NUMBER_OF_PARTICIPANTS)).isPresent().isOptional().isEnabled().hasValue("0");
            softly.assertThat(classificationManagementTabList.getAsset(RATE_BASIS)).isPresent().isRequired().isDisabled().hasValue("Per Month");
            softly.assertThat(classificationManagementTabList.getAsset(RATE)).isPresent().isOptional().isDisabled().hasValue(new Currency(1).toString());
            softly.assertThat(tablePlanTierAndRatingInfo.getRowsCount()).isEqualTo(1);
            softly.assertThat(tablePlanTierAndRatingInfo.getRow(TableConstants.PlanTierAndRatingSelection.COVERAGE_TIER.getName(), StringUtils.EMPTY)).isPresent();
            softly.assertThat(tablePlanTierAndRatingInfo.getRow(TableConstants.PlanTierAndRatingSelection.NUMBER_OF_PARTICIPANTS.getName(), "0")).isPresent();
            softly.assertThat(tablePlanTierAndRatingInfo.getRow(TableConstants.PlanTierAndRatingSelection.RATE.getName(), new Currency(1).toString())).isPresent();

            LOGGER.info("REN-21703 step 2");
            classificationManagementTabList.getAsset(COVERAGE_TIER).setValue(COVERAGE_TIER_EMPLOYEE_ONLY);
            classificationManagementTabList.getAsset(NUMBER_OF_PARTICIPANTS).setValue("1");
            classificationManagementTabList.getAsset(ADD_COVERAGE_TIER).click();
            softly.assertThat(tablePlanTierAndRatingInfo.getRow(TableConstants.PlanTierAndRatingSelection.COVERAGE_TIER.getName(), COVERAGE_TIER_EMPLOYEE_ONLY)).isPresent();
            softly.assertThat(classificationManagementTabList.getAsset(COVERAGE_TIER)).doesNotContainOption(COVERAGE_TIER_EMPLOYEE_ONLY);

            LOGGER.info("REN-21703 step 3");
            ImmutableList.of("Employee + Spouse", "Employee + Children").forEach(subgroup -> {
                classificationManagementTabList.getAsset(COVERAGE_TIER).setValue(subgroup);
                classificationManagementTabList.getAsset(ADD_COVERAGE_TIER).click();
            });
            classificationManagementTabList.getAsset(COVERAGE_TIER).setValue("Employee + Family");
            softly.assertThat(classificationManagementTabList.getAsset(ADD_COVERAGE_TIER)).isAbsent();

            planDefinitionTab.navigateToTab();
            groupAccidentMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultACMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            groupAccidentMasterPolicy.dataGather().start();

            LOGGER.info("REN-21511 step 6 and step 7");
            policyInformationTab.getAssetList().getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("2");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get());
            softly.assertThat(classificationManagementTabList.getAsset(RATE)).isPresent();
        });
    }
}
