package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.master.ScenarioAlignAffectsRatingFieldsVerification;
import org.testng.annotations.Test;

import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SpecialPlanFeaturesMetadata.PHOTOCHROMIC_LENSES_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SpecialPlanFeaturesMetadata.SAFETY_GLASSES_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SpecialPlanFeaturesMetadata.SCRATCH_COATING_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.SponsorParticipantFundingStructure.ASSUMED_PARTICIPATION_PERCENTAGE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAlignAffectsRatingFieldsVerification extends ScenarioAlignAffectsRatingFieldsVerification implements GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41034"}, component = POLICY_GROUPBENEFITS)
    public void testAlignAffectsRatingFieldsVerification() {
        initiateAndRateQuote(GroupBenefitsMasterPolicyType.GB_VS, tdSpecific().getTestData("TestData"),
                PremiumSummaryTab.class);
        //verification steps
        verificationField(planDefinitionTab.getAssetList().getAsset(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE)
                .getAsset(ASSUMED_PARTICIPATION_PERCENTAGE), planDefinitionTab, "70", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(SPECIAL_PLAN_FEATURES).getAsset(PHOTOCHROMIC_LENSES_FACTOR), planDefinitionTab, "5", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(SPECIAL_PLAN_FEATURES).getAsset(SCRATCH_COATING_FACTOR), planDefinitionTab, "6", true);
        verificationField(planDefinitionTab.getAssetList().getAsset(SPECIAL_PLAN_FEATURES).getAsset(SAFETY_GLASSES_FACTOR), planDefinitionTab, "7", true);
    }
}
