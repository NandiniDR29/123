package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
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
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ASO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CO_INSURANCE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CoInsuranceMetaData.NETWORK_ARRANGEMENT;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CoInsuranceMetaData.UC_PERCENTILE_LEVEL;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PPO_EPO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddNetworkArrangement extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {
    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21560", "REN-22484", "REN-22486"}, component = POLICY_GROUPBENEFITS)
    public void testAddNetworkArrangement() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        LOGGER.info("REN-21560 step 01 setting Situs State");
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getClass().getSimpleName(), SITUS_STATE.getLabel()), "NV"), planDefinitionTab.getClass());

        assertSoftly(softly -> {
            LOGGER.info("REN-21560 step 2 and step 3");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(ALACARTE));

            LOGGER.info("REN-21560 step 4");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(NETWORK_ARRANGEMENT)).isPresent().isDisabled();

            LOGGER.info("REN-21560 step 6 and REN-22486 step 1 and step 2");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN)).isPresent().isEnabled().hasValue(VALUE_NO);
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PPO_EPO_PLAN).setValue(VALUE_YES);
            planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(UC_PERCENTILE_LEVEL).setValue("PPO Schedule");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(NETWORK_ARRANGEMENT)).hasValue("Renaissance NV Elite MAC Ren OON");

            LOGGER.info("REN-21560 step 7");
            NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("WV");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(ALACARTE));
            planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN).setValue(VALUE_NO);
            planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(UC_PERCENTILE_LEVEL).setValue("REN 80th");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(NETWORK_ARRANGEMENT)).hasValue("Renaissance PPO Plus WV (2015)");

            LOGGER.info("REN-22484 step 1");
            NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("AK");
            policyInformationTab.getAssetList().getAsset(ASO_PLAN).setValue(VALUE_YES);

            LOGGER.info("REN-22484 step 2");
            NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(ASO));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PPO_EPO_PLAN)).isDisabled().hasValue(VALUE_NO);
        });
    }
}
