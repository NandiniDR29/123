package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.ipb.eisa.controls.AdvancedSelector;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.enums.ErrorConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.PlanVision.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestLimitPlanBySitusState extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21662"}, component = POLICY_GROUPBENEFITS)
    public void testLimitPlanBySitusState() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), policyInformationTab.getClass());

        LOGGER.info("Step 1");
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("MD");
        assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE)).hasWarningWithText(ErrorConstants.ErrorMessages.SITUS_STATE_ERROR_MESSAGE);
        planDefinitionTab.navigateToTab();
        AdvancedSelector plan = planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN);
        plan.buttonOpenPopup.click();
        plan.buttonSearch.click();
        StaticElement planWarningMessage = new StaticElement(By.xpath("//form[@id='searchForm_plansSearch']//span[@class='error_message']"));
        assertThat(planWarningMessage).hasValue("   No Plans Found!");
        plan.buttonCancel.click();

        LOGGER.info("Step 2,-4, 7");
        ImmutableList.of("CT", "ME", "AK", "WY").forEach(situsState -> {
            policyInformationTab.navigateToTab();
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(situsState);
            assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE)).hasWarningWithText(ErrorConstants.ErrorMessages.SITUS_STATE_ERROR_MESSAGE);
            planDefinitionTab.navigateToTab();
            plan.buttonOpenPopup.click();
            plan.buttonSearch.click();
            assertThat(plan.getAvailableItems()).hasSameElementsAs(ImmutableList.of(PlanB, PlanC, A_LA_CARTE));
            plan.buttonCancel.click();
        });
    }
}