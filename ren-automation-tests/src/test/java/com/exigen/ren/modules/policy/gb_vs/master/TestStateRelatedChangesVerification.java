package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.PlanVision.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestStateRelatedChangesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-40104"}, component = POLICY_GROUPBENEFITS)
    public void testStateRelatedChangesVerification_VS() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        LOGGER.info("Scenario#1 verification");
        TestData tdPolicy1 = getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_NO)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "ME");
        quotePlansVerificationWithDifferentStates(tdPolicy1, ImmutableList.of(PlanB, PlanC, A_LA_CARTE));

        LOGGER.info("Scenario#2 verification");
        TestData tdPolicy2 = getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "ME");
        quotePlansVerificationWithDifferentStates(tdPolicy2, ImmutableList.of(ASOALC_VIS, ASO_PLANB));

        LOGGER.info("Scenario#3 Steps#1,2 verification");
        quoteInitiateAndFillUpToTab(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_NO)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NJ"), PlanDefinitionTab.class, false);

        LOGGER.info("Scenario#2 Step#3 verification");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonOpenPopup.click();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonSearch.click();
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).getAvailableItems()).doesNotContain(EXAM_PLUS);
    }

    private void quotePlansVerificationWithDifferentStates(TestData td, ImmutableList list) {
        LOGGER.info("Steps#1, 2 verification");
        quoteInitiateAndFillUpToTab(td, PlanDefinitionTab.class, false);

        LOGGER.info("Step#3 verification");
        availablePlansVerification(list);

        LOGGER.info("Step#7 verification");
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("CT");

        LOGGER.info("Step#8 verification");
        planDefinitionTab.navigateToTab();
        availablePlansVerification(list);
        PlanDefinitionTab.buttonSaveAndExit.click();
    }

    private void availablePlansVerification(ImmutableList list) {
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonOpenPopup.click();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonSearch.click();
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).getAvailableItems()).hasSameElementsAs(list);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonCancel.click();
    }
}
