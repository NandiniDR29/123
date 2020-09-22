package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.USER_10_LOGIN;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.ORTHODONTIA;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.OrthodontiaMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PPO_EPO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureYearlyAndLifetimeMaximumDeductible extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final String CURRENCY_STRING = "$1,500";
    private static final AssetList orthodontiaAssetList = planDefinitionTab.getAssetList().getAsset(ORTHODONTIA);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-16998", "REN-17004", "REN-17096", "REN-17099", "REN-17100", "REN-17102", "REN-17103", "REN-17212", "REN-17213", "REN-17214", "REN-17215", "REN-17217"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureYearlyAndLifetimeMaximumDeductible() {
        mainApp().open();

        LOGGER.info("Test REN-16998 Preconditions");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NV"), PlanDefinitionTab.class);
        selectFirstPlanFromDNMasterPolicyDefaultTestData();
        planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);
        orthodontiaAssetList.getAsset(ORTHO_COVERAGE).setValue(VALUE_YES);
        assertSoftly(softly -> {

            LOGGER.info("Test REN-17217 Step 4");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_DEDUCTIBLE)).isPresent().isRequired();
            LOGGER.info("Test REN-16998 Step 3");
            orthodontiaAssetList.getAsset(YEARLY_MAXIMUM).setValue(VALUE_YES);
            ImmutableList.of(YEARLY_MAXIMUM_IN_NETWORK, YEARLY_MAXIMUM_EPO, YEARLY_MAXIMUM_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(orthodontiaAssetList.getAsset(control)).isPresent().isRequired()
            );
            orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM).setValue(VALUE_YES);
            ImmutableList.of(LIFETIME_MAXIMUM_IN_NETWORK, LIFETIME_MAXIMUM_EPO, LIFETIME_MAXIMUM_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(orthodontiaAssetList.getAsset(control)).isPresent().isRequired()
            );
            LOGGER.info("Test REN-17217 Step 10.2.1");
            orthodontiaAssetList.getAsset(LIFETIME_DEDUCTIBLE).setValue(VALUE_YES);
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_DEDUCTIBLE_IN_NETWORK)).isPresent().isRequired();
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_DEDUCTIBLE_OUT_OF_NETWORK)).isPresent().isRequired();
            LOGGER.info("Test REN-16998 Step 4");
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            orthodontiaAssetList.getAsset(IS_IT_GRADED_ORTHODONTIA).setValue(VALUE_YES);
            orthodontiaAssetList.getAsset(NUMBER_OF_GRADED_YEARS).setValue("4");
            ImmutableList.of(YEARLY_MAXIMUM_FIRST_YEAR_IN_NETWORK, YEARLY_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, YEARLY_MAXIMUM_SECOND_YEAR_IN_NETWORK, YEARLY_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK,
                    YEARLY_MAXIMUM_THIRD_YEAR_IN_NETWORK, YEARLY_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, YEARLY_MAXIMUM_FOURTH_YEAR_IN_NETWORK, YEARLY_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK,
                    LIFETIME_MAXIMUM_FIRST_YEAR_IN_NETWORK, LIFETIME_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK, LIFETIME_MAXIMUM_SECOND_YEAR_IN_NETWORK, LIFETIME_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK,
                    LIFETIME_MAXIMUM_THIRD_YEAR_IN_NETWORK, LIFETIME_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK, LIFETIME_MAXIMUM_FOURTH_YEAR_IN_NETWORK, LIFETIME_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(orthodontiaAssetList.getAsset(control)).isPresent().isRequired()
            );
            LOGGER.info("Test REN-17004 Step 2");
            orthodontiaAssetList.getAsset(ORTHO_COVERAGE).setValue(VALUE_NO);
            ImmutableList.of(YEARLY_MAXIMUM_IN_NETWORK, YEARLY_MAXIMUM_EPO, YEARLY_MAXIMUM_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(orthodontiaAssetList.getAsset(control)).isAbsent()
            );
            LOGGER.info("Test REN-17096 Step 2");
            orthodontiaAssetList.getAsset(ORTHO_COVERAGE).setValue(VALUE_YES);
            softly.assertThat(orthodontiaAssetList.getAsset(YEARLY_MAXIMUM_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(YEARLY_MAXIMUM_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17099 Step 2");
            orthodontiaAssetList.getAsset(YEARLY_MAXIMUM).setValue(VALUE_NO);
            softly.assertThat(orthodontiaAssetList.getAsset(YEARLY_MAXIMUM_FIRST_YEAR_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(YEARLY_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17100 Step 2");
            softly.assertThat(orthodontiaAssetList.getAsset(YEARLY_MAXIMUM_SECOND_YEAR_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(YEARLY_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17102 Step 2");
            softly.assertThat(orthodontiaAssetList.getAsset(YEARLY_MAXIMUM_THIRD_YEAR_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(YEARLY_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17103 Step 2");
            softly.assertThat(orthodontiaAssetList.getAsset(YEARLY_MAXIMUM_FOURTH_YEAR_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(YEARLY_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK)).isAbsent();

            LOGGER.info("Test REN-17212 Step 3.2.1");
            orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM).setValue(VALUE_NO);
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FIRST_YEAR_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17213 Step 3.2.1");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_SECOND_YEAR_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17214 Step 3.2.1");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_THIRD_YEAR_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17215 Step 3.2.1");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FOURTH_YEAR_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17212 Step 15.3.1");
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            orthodontiaAssetList.getAsset(IS_IT_GRADED_ORTHODONTIA).setValue(VALUE_YES);
            orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM).setValue(VALUE_YES);
            orthodontiaAssetList.getAsset(COINSURANCE_FIRST_YEAR_IN_NETWORK).setValue("50%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FIRST_YEAR_IN_NETWORK)).hasValue("$150").hasNoWarning();
            LOGGER.info("Test REN-17212 Step 15.3.2");
            orthodontiaAssetList.getAsset(COINSURANCE_FIRST_YEAR_OUT_OF_NETWORK).setValue("50%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK)).hasValue("$150").hasNoWarning();
            LOGGER.info("Test REN-17212 Step 16.3.1");
            orthodontiaAssetList.getAsset(COINSURANCE_FIRST_YEAR_IN_NETWORK).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FIRST_YEAR_IN_NETWORK)).hasValue(CURRENCY_STRING).hasNoWarning();
            orthodontiaAssetList.getAsset(COINSURANCE_FIRST_YEAR_OUT_OF_NETWORK).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FIRST_YEAR_OUT_OF_NETWORK)).hasValue(CURRENCY_STRING).hasNoWarning();
            LOGGER.info("Test REN-17213 Step 16.3.1");
            orthodontiaAssetList.getAsset(COINSURANCE_SECOND_YEAR_IN_NETWORK).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_SECOND_YEAR_IN_NETWORK)).hasValue(CURRENCY_STRING).hasNoWarning();
            orthodontiaAssetList.getAsset(COINSURANCE_SECOND_YEAR_OUT_OF_NETWORK).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_SECOND_YEAR_OUT_OF_NETWORK)).hasValue(CURRENCY_STRING).hasNoWarning();
            LOGGER.info("Test REN-17214 Step 3.2.2");
            orthodontiaAssetList.getAsset(NUMBER_OF_GRADED_YEARS).setValue("2");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_THIRD_YEAR_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17215 Step 3.2.2");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FOURTH_YEAR_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17214 Step 16.3.1");
            orthodontiaAssetList.getAsset(NUMBER_OF_GRADED_YEARS).setValue("4");
            orthodontiaAssetList.getAsset(COINSURANCE_THIRD_YEAR_IN_NETWORK).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_THIRD_YEAR_IN_NETWORK)).hasValue(CURRENCY_STRING).hasNoWarning();
            orthodontiaAssetList.getAsset(COINSURANCE_THIRD_YEAR_OUT_OF_NETWORK).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_THIRD_YEAR_OUT_OF_NETWORK)).hasValue(CURRENCY_STRING).hasNoWarning();
            LOGGER.info("Test REN-17215 Step 16.3.1");
            orthodontiaAssetList.getAsset(COINSURANCE_FOURTH_YEAR_IN_NETWORK).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FOURTH_YEAR_IN_NETWORK)).hasValue(CURRENCY_STRING).hasNoWarning();
            orthodontiaAssetList.getAsset(COINSURANCE_FOURTH_YEAR_OUT_OF_NETWORK).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_FOURTH_YEAR_OUT_OF_NETWORK)).hasValue(CURRENCY_STRING).hasNoWarning();
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-17106", "REN-17209"}, component = POLICY_GROUPBENEFITS)
    public void testUserAuthLevelZeroChecks() {
        assertSoftly(softly -> {

            LOGGER.info("Test REN-17106 Precondition");
            mainApp().open(USER_10_LOGIN, "qa");
            createDefaultNonIndividualCustomer();
            createDefaultCaseProfile(groupDentalMasterPolicy.getType());
            groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
            groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData().adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "NV"), PlanDefinitionTab.class);
            selectFirstPlanFromDNMasterPolicyDefaultTestData();
            LOGGER.info("Test REN-17106 Step 4");
            orthodontiaAssetList.getAsset(ORTHO_COVERAGE).setValue(VALUE_NO);
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            ImmutableList.of(LIFETIME_MAXIMUM_IN_NETWORK, LIFETIME_MAXIMUM_EPO, LIFETIME_MAXIMUM_OUT_OF_NETWORK).forEach(control ->
                    softly.assertThat(orthodontiaAssetList.getAsset(control)).isAbsent());
            LOGGER.info("Test REN-17106 Step 15.3.1");
            orthodontiaAssetList.getAsset(ORTHO_COVERAGE).setValue(VALUE_YES);
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);
            orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM).setValue(VALUE_YES);
            orthodontiaAssetList.getAsset(COINSURANCE_EPO).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_EPO)).hasValue(CURRENCY_STRING);
            LOGGER.info("Test REN-17209 Step 4.2.1");
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            orthodontiaAssetList.getAsset(IS_IT_GRADED_ORTHODONTIA).setValue(VALUE_YES);
            orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM).setValue(VALUE_NO);
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17209 Step 4.2.2");
            orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM).setValue(VALUE_YES);
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17209 Step 4.2.3");
            orthodontiaAssetList.getAsset(IS_IT_GRADED_ORTHODONTIA).setValue(VALUE_NO);
            orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM).setValue(VALUE_NO);
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_IN_NETWORK)).isAbsent();
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_OUT_OF_NETWORK)).isAbsent();
            LOGGER.info("Test REN-17209 Step 17.3.1.2");
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_YES);
            orthodontiaAssetList.getAsset(ORTHO_COVERAGE).setValue(VALUE_YES);
            orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM).setValue(VALUE_YES);
            orthodontiaAssetList.getAsset(COINSURANCE_IN_NETWORK).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_IN_NETWORK)).hasValue(CURRENCY_STRING);
            LOGGER.info("Test REN-17209 Step 17.3.2.2");
            orthodontiaAssetList.getAsset(COINSURANCE_OUT_OF_NETWORK).setValue("40%");
            softly.assertThat(orthodontiaAssetList.getAsset(LIFETIME_MAXIMUM_OUT_OF_NETWORK)).hasValue(CURRENCY_STRING);
        });
    }
}
