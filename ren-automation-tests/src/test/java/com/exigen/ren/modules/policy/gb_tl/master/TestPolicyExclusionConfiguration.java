package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.ADD;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyExclusionConfiguration extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {


    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13338", component = POLICY_GROUPBENEFITS)
    public void testPolicyExclusionConfiguration() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        TestData tdDataGatherTestData = getDefaultTLMasterPolicyData();
        termLifeInsuranceMasterPolicy.initiate(tdDataGatherTestData);
        termLifeInsuranceMasterPolicy.getDefaultWorkspace().fillUpTo(tdDataGatherTestData, PlanDefinitionTab.class);
        planDefinitionTab.selectDefaultPlan();
        PlanDefinitionTab.changeCoverageTo(ADD);
        AssetList exclussionsAssetList = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.EXCLUSIONS);
        assertSoftly(softly -> {

            softly.assertThat(exclussionsAssetList).isPresent();

            LOGGER.info("Test REN-13338 Step 1");
            softly.assertThat(exclussionsAssetList.getAsset(PlanDefinitionTabMetaData.ExclusionMetaData.AIRCRAFT)).isPresent();
            softly.assertThat(exclussionsAssetList.getAsset(PlanDefinitionTabMetaData.ExclusionMetaData.BOARDING)).isAbsent();

            LOGGER.info("Test REN-13338 Step 2");
            softly.assertThat(exclussionsAssetList.getAsset(PlanDefinitionTabMetaData.ExclusionMetaData.DRIVING_UNDER_THE_INFLUENCE)).isPresent();
            softly.assertThat(exclussionsAssetList.getAsset(PlanDefinitionTabMetaData.ExclusionMetaData.ALCOHOL_PRESENCE)).isAbsent();

            LOGGER.info("Test REN-13338 Step 3");
            softly.assertThat(exclussionsAssetList.getAsset(PlanDefinitionTabMetaData.ExclusionMetaData.HAZARD_SPORTS)).isAbsent();

            LOGGER.info("Test REN-13338 Step 4");
            softly.assertThat(exclussionsAssetList.getAsset(PlanDefinitionTabMetaData.ExclusionMetaData.ACTIVE_DUTY)).isOptional().isDisabled().hasValue(true);

            LOGGER.info("Test REN-13338 Step 5");
            softly.assertThat(exclussionsAssetList.getAsset(PlanDefinitionTabMetaData.ExclusionMetaData.WORKER_COMPENSATION)).isOptional().isEnabled().hasValue(false);
        });
    }
}
