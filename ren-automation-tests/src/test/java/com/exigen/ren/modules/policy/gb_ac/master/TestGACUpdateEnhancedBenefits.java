package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.EnhancedBenefitsHtoLTab;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.INTERNAL_ORGAN_LOSS_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.InternalOrganLossBenefitMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGACUpdateEnhancedBenefits extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {
    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-14774"}, component = POLICY_GROUPBENEFITS)
    public void testGACUpdateEnhancedBenefits()
    {
        mainApp().open();
        LOGGER.info("Test REN-14774 Preconditions");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData(), EnhancedBenefitsHtoLTab.class);
        assertSoftly(softly -> {
            LOGGER.info("Test REN-14774 Step 2");
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(INTERNAL_ORGAN_LOSS_BENEFIT)).isPresent();
            LOGGER.info("Test REN-14774 Step 3");
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(INTERNAL_ORGAN_LOSS_BENEFIT).getAsset(APPLY_BENEFIT_INTERNAL_ORGAN)).isPresent().isEnabled().hasValue(true);
            LOGGER.info("Test REN-14774 Step 4");
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(INTERNAL_ORGAN_LOSS_BENEFIT).getAsset(TREATMENT_PERIOD_DAYS_INTERNAL_ORGAN)).isRequired().isPresent().isEnabled().hasValue("365");
            LOGGER.info("Test REN-14774 Step 5");
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(INTERNAL_ORGAN_LOSS_BENEFIT).getAsset(INTERNAL_ORGAN_LOSS_BENEFIT_AMOUNT)).isRequired().isPresent().isEnabled();
        });
    }
}
