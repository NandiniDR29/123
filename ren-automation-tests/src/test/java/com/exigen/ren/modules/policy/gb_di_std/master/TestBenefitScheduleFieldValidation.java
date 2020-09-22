package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.NC;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBenefitScheduleFieldValidation extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {
    private MultiAssetList planDefinitionTabAssetList;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18344", "REN-18498", "REN-18500", "REN-18508"}, component = POLICY_GROUPBENEFITS)
    public void testBenefitScheduleFieldValidation() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), planDefinitionTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN 18344, Step 3");
            planDefinitionTabAssetList = (MultiAssetList) planDefinitionTab.getAssetList();
            planDefinitionTabAssetList.getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(NC));
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PARTIAL_DISABILITY).setValue("Work Incentive Benefit");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_INJURY).setValue("30");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_SICKNESS).setValue("30");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_PERCENTAGE)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PARTIAL_DISABILITY)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.DEFINITION_OF_DISABILITY)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.EARNING_DEFINITION)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.EARNING_TEST)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.RTW_DURING_EP)).isPresent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.MINIMUM_WEEKLY_BENEFIT_AMOUNT)).isPresent();

            LOGGER.info("REN 18498, Step 3");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAXIMUM_WEEKLY_BENEFIT_AMOUNT)).isPresent().hasValue(new Currency("1000").toString());
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Minimum Weekly Benefit Percentage"))).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.TEST_DEFINITION)).isPresent().hasValue("Loss of Duties and Earnings");

            LOGGER.info("REN-18500, Step 3,Step 6");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PARTIAL_DISABILITY).setValue("None");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.EARNING_DEFINITION)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.EARNING_TEST)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.PARTIAL_DISABILITY).setValue("Work Incentive Benefit");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.EARNING_DEFINITION)).isPresent().isRequired().hasValue("Salary");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.EARNING_TEST)).isPresent().isRequired().hasValue("80%");

            LOGGER.info("REN-18508, Step 3,step 5");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_INJURY).setValue("14");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_SICKNESS).setValue("14");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.RTW_DURING_EP)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_INJURY).setValue("30");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_SICKNESS).setValue("14");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.RTW_DURING_EP)).isPresent().isRequired().hasValue("7");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_INJURY).setValue("14");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_SICKNESS).setValue("30");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.RTW_DURING_EP)).isPresent().isRequired().hasValue("7");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_INJURY).setValue("30");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.ELIMINATION_PERIOD_SICKNESS).setValue("30");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.RTW_DURING_EP)).isPresent().isRequired().hasValue("7");
        });
    }
}