package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestStateRelatedChangesVerification extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    private static StaticElement planWarningMessage = new StaticElement(By.xpath("//form[@id='searchForm_plansSearch']//span[@class='error_message']"));

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-40194", component = POLICY_GROUPBENEFITS)
    public void testStateRelatedChangesVerification_TL() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        LOGGER.info("Steps#1, 2 verification");
        initiateTLQuoteAndFillToTab(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "CA")
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())), PlanDefinitionTab.class, false);

        LOGGER.info("Steps#3 verification");
        planVerification();

        LOGGER.info("Step#4 verification");
        checkPlanWithDifferentStates("ND");
        checkPlanWithDifferentStates("SD");
    }

    private void planVerification() {
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonOpenPopup.click();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonSearch.click();
        assertThat(planWarningMessage).hasValue("   No Plans Found!");
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).buttonCancel.click();
    }

    private void checkPlanWithDifferentStates(String situsState) {
        policyInformationTab.navigateToTab();
        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(situsState);
        planDefinitionTab.navigateToTab();
        planVerification();
    }
}
