package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ALACARTE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.CommonMethods.getRandomElementFromList;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDentalDeductibleAdditionalStateRules extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private AssetList DEDUCTIBLE_REF = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.DENTAL_DEDUCTIBLE);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21576", "REN-21652", "REN-21654", "REN-21655", "REN-21656"}, component = POLICY_GROUPBENEFITS)
    public void testAddFieldsForRateCapping() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), planDefinitionTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN-21576 Step 1 to Step 2");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(getRandomElementFromList(ImmutableList.of("TX", "LA", "GA", "MS")));
            policyInformationTab.getAssetList().getAsset(ASO_PLAN).setValue(VALUE_NO);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(ALACARTE));
            softly.assertThat(DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.FAMILY_DEDUCTIBLE_IN_NETWORK)).isPresent().hasValue("3X");
            softly.assertThat(DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.FAMILY_DEDUCTIBLE_OUT_OF_NETWORK)).isPresent().hasValue("3X");

            LOGGER.info("REN-21652 Step 1 to Step 2");
            DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.IS_IT_GRADED_DENTAL_DEDUCTIBLE).setValue(VALUE_YES);
            softly.assertThat(DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.FAMILY_DEDUCTIBLE_FIRST_YEAR_IN_NETWORK)).isPresent().hasValue("3X");
            softly.assertThat(DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.FAMILY_DEDUCTIBLE_FIRST_YEAR_OUT_OF_NETWORK)).isPresent().hasValue("3X");

            LOGGER.info("REN-21654 Step 1 to Step 2");
            softly.assertThat(DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.FAMILY_DEDUCTIBLE_SECOND_YEAR_IN_NETWORK)).isPresent().hasValue("3X");
            softly.assertThat(DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.FAMILY_DEDUCTIBLE_SECOND_YEAR_OUT_OF_NETWORK)).isPresent().hasValue("3X");

            LOGGER.info("REN-21655 Step 1 to Step 2");
            softly.assertThat(DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.FAMILY_DEDUCTIBLE_THIRD_YEAR_IN_NETWORK)).isPresent().hasValue("3X");
            softly.assertThat(DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.FAMILY_DEDUCTIBLE_THIRD_YEAR_OUT_OF_NETWORK)).isPresent().hasValue("3X");

            LOGGER.info("REN-21656 Step 1 to Step 2");
            DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.NUMBER_OF_GRADED_YEARS).setValue("4");
            softly.assertThat(DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.FAMILY_DEDUCTIBLE_FOURTH_YEAR_IN_NETWORK)).isPresent().hasValue("3X");
            softly.assertThat(DEDUCTIBLE_REF.getAsset(PlanDefinitionTabMetaData.DentalDeductibleMetaData.FAMILY_DEDUCTIBLE_FOURTH_YEAR_OUT_OF_NETWORK)).isPresent().hasValue("3X");
        });
    }
}