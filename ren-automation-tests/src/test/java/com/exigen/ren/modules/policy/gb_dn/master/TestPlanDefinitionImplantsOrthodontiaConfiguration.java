package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.ORTHODONTIA;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.OrthodontiaMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PPO_EPO_PLAN;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionImplantsOrthodontiaConfiguration extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static AssetList orthoDontiaAssetList = planDefinitionTab.getAssetList().getAsset(ORTHODONTIA);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-16853", "REN-16904", "REN-16910", "REN-16911", "REN-16913", "REN-16917", "REN-16918", "REN-16920", "REN-16925"}, component = POLICY_GROUPBENEFITS)
    public void testPlanDefinitionImplantsOrthodontiaConfiguration() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NV"), PlanDefinitionTab.class);
        LOGGER.info("Test REN-16853 Step 1");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).fill(getDefaultDNMasterPolicyData().getTestDataList(planDefinitionTab.getClass().getSimpleName()).get(0));
        LOGGER.info("Test REN-16853 Step 2");
        planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);
        assertSoftly(softly -> {

            ImmutableList.of("Implant Coverage", "Lifetime Maximum - In Network", "Lifetime Maximum - Out of Network", "Does Lifetime Max Apply toward Plan Year Max?").forEach(textValue ->
                    softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(textValue))).isAbsent());
            softly.assertThat(orthoDontiaAssetList).isPresent();
            orthoDontiaAssetList.getAsset(ORTHO_COVERAGE).setValue(VALUE_YES);
            ImmutableList.of(ORTHO_COVERAGE, ORTHO_AVAILABILITY, ORTHO_WAITING_PERIOD, IS_IT_GRADED_ORTHODONTIA, CO_INSURANCE_EPO, CO_INSURANCE_IN_NETWORK, CO_INSURANCE_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(orthoDontiaAssetList.getAsset(control)).isRequired().isPresent());
            LOGGER.info("Test REN-16925 Step 2");
            softly.assertThat(orthoDontiaAssetList.getAsset(YEARLY_DEDUCTIBLE_IN_NETWORK)).isAbsent();
            softly.assertThat(orthoDontiaAssetList.getAsset(YEARLY_DEDUCTIBLE_OUT_OF_NETWORK)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            orthoDontiaAssetList.getAsset(ORTHO_AVAILABILITY).setValue("Child Only");
            orthoDontiaAssetList.getAsset(IS_IT_GRADED_ORTHODONTIA).setValue(VALUE_YES);
            LOGGER.info("Test REN-16853 Step 3 REN-16910 Step 7");
            softly.assertThat(orthoDontiaAssetList.getAsset(NUMBER_OF_GRADED_YEARS)).isPresent().isRequired();
            softly.assertThat(orthoDontiaAssetList.getAsset(CO_INSURANCE_IN_NETWORK)).isAbsent();
            softly.assertThat(orthoDontiaAssetList.getAsset(CO_INSURANCE_OUT_OF_NETWORK)).isAbsent();

            orthoDontiaAssetList.getAsset(NUMBER_OF_GRADED_YEARS).setValue("2");
            LOGGER.info("Test REN-16918,REN-16920 Step 3");
            ImmutableList.of(CO_INSURANCE_THIRD_YEAR_IN_NETWORK, CO_INSURANCE_THIRD_YEAR_OUT_OF_NETWORK, CO_INSURANCE_FOURTH_YEAR_IN_NETWORK, CO_INSURANCE_FOURTH_YEAR_OUT_OF_NETWORK).forEach(
                    control ->
                            softly.assertThat(orthoDontiaAssetList.getAsset(control)).isAbsent()
            );
            LOGGER.info("Test REN-16853 Step 3 REN-16913,REN-16917 Step 3,REN-16918,REN-16920 Step 4");
            orthoDontiaAssetList.getAsset(NUMBER_OF_GRADED_YEARS).setValue("4");
            ImmutableList.of(ORTHO_AGE_LIMIT, CO_INSURANCE_FIRST_YEAR_IN_NETWORK, CO_INSURANCE_FIRST_YEAR_OUT_OF_NETWORK, CO_INSURANCE_SECOND_YEAR_IN_NETWORK, CO_INSURANCE_SECOND_YEAR_OUT_OF_NETWORK,
                    CO_INSURANCE_THIRD_YEAR_IN_NETWORK, CO_INSURANCE_THIRD_YEAR_OUT_OF_NETWORK, CO_INSURANCE_FOURTH_YEAR_IN_NETWORK, CO_INSURANCE_FOURTH_YEAR_OUT_OF_NETWORK, YEARLY_DEDUCTIBLE_IN_NETWORK, YEARLY_DEDUCTIBLE_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(orthoDontiaAssetList.getAsset(control)).isPresent().isRequired().isEnabled());
            LOGGER.info("Test REN-16904 Step 2");
            orthoDontiaAssetList.getAsset(ORTHO_COVERAGE).setValue(VALUE_NO);
            softly.assertThat(orthoDontiaAssetList.getAsset(ORTHO_AVAILABILITY)).isAbsent();
            softly.assertThat(orthoDontiaAssetList.getAsset(ORTHO_AGE_LIMIT)).isAbsent();
            LOGGER.info("Test REN-16910 Step 2");
            ImmutableList.of(IS_IT_GRADED_ORTHODONTIA, CO_INSURANCE_IN_NETWORK, CO_INSURANCE_OUT_OF_NETWORK, NUMBER_OF_GRADED_YEARS).forEach(control ->
                    softly.assertThat(orthoDontiaAssetList.getAsset(control)).isAbsent());
            LOGGER.info("Test REN-16925 Step 3");
            softly.assertThat(orthoDontiaAssetList.getAsset(YEARLY_DEDUCTIBLE_IN_NETWORK)).isAbsent();
            softly.assertThat(orthoDontiaAssetList.getAsset(YEARLY_DEDUCTIBLE_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-16911 Step 3");
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);
            softly.assertThat(orthoDontiaAssetList.getAsset(CO_INSURANCE_EPO)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            LOGGER.info("Test REN-16904 Step 4");
            orthoDontiaAssetList.getAsset(ORTHO_COVERAGE).setValue(VALUE_YES);
            softly.assertThat(orthoDontiaAssetList.getAsset(ORTHO_AVAILABILITY)).isEnabled();
            LOGGER.info("Test REN-16910 Step 4");
            softly.assertThat(orthoDontiaAssetList.getAsset(IS_IT_GRADED_ORTHODONTIA)).isPresent().isRequired().isEnabled();
            LOGGER.info("Test REN-16911 Step 2");
            softly.assertThat(orthoDontiaAssetList.getAsset(CO_INSURANCE_EPO)).isAbsent();
            LOGGER.info("Test REN-16904 Step 8");
            orthoDontiaAssetList.getAsset(ORTHO_AVAILABILITY).setValue("Adult and Child");
            softly.assertThat(orthoDontiaAssetList.getAsset(ORTHO_AGE_LIMIT)).isAbsent();
            LOGGER.info("Test REN-16925 Step 4");
            softly.assertThat(orthoDontiaAssetList.getAsset(YEARLY_DEDUCTIBLE_IN_NETWORK)).isPresent().isRequired().isEnabled();
            softly.assertThat(orthoDontiaAssetList.getAsset(YEARLY_DEDUCTIBLE_OUT_OF_NETWORK)).isPresent().isRequired().isEnabled();
            LOGGER.info("Test REN-16911 Step 5");
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);
            softly.assertThat(orthoDontiaAssetList.getAsset(CO_INSURANCE_EPO)).isPresent().isRequired();
        });
    }
}
