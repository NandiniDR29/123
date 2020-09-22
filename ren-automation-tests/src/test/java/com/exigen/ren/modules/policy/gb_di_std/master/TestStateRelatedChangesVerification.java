package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestStateRelatedChangesVerification extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private static StaticElement planWarningMessage = new StaticElement(By.xpath("//form[@id='searchForm_plansSearch']//span[@class='error_message']"));

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-40202", component = POLICY_GROUPBENEFITS)
    public void testStateRelatedChangesVerification_STD() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        LOGGER.info("Scenario#1 Steps#1, 2 verification");
        initiateSTDQuoteAndFillToTab(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "MT")
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())), PlanDefinitionTab.class, false);

        LOGGER.info("Scenario#1 Step#2 verification");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonOpenPopup.click();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonSearch.click();
        assertThat(planWarningMessage).hasValue("   No Plans Found!");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonCancel.click();

        LOGGER.info("Scenario#2 Step#1, 2, 3 verification");
        checkPlanWithDifferentStates("MA");

        LOGGER.info("Scenario#2 Step#7 verification");
        checkPlanWithDifferentStates("WY");
    }

    private void checkPlanWithDifferentStates(String situsState) {
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.SITUS_STATE).setValue(situsState);
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonOpenPopup.click();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonSearch.click();
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).getAvailableItems()).containsExactlyInAnyOrder(CON, NC, SGR, VOL);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonCancel.click();
    }
}
