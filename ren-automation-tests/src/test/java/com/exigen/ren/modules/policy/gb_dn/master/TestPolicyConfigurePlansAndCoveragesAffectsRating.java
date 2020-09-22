package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CoveragePackageMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DENTAL_PLAN_DESIGN_CODE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyConfigurePlansAndCoveragesAffectsRating extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13756", component = POLICY_GROUPBENEFITS)
    public void testPolicyConfigurePlansAndCoveragesAffectsRating() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData());

        LOGGER.info("Test REN-13756,Step 1.4.1");
        groupDentalMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        String dentalPlanDesignCode = planDefinitionTab.getAssetList().getAsset(DENTAL_PLAN_DESIGN_CODE).getValue();
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE).getAsset(STD).setValue(true);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE).getAsset(LTD).setValue(true);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE).getAsset(LIFE).setValue(true);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_INCLUDED_IN_PACKAGE).getAsset(VISION).setValue(true);
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("Test REN-13756,Step 1.5.1");
        groupDentalMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        assertThat(planDefinitionTab.getAssetList().getAsset(DENTAL_PLAN_DESIGN_CODE)).hasValue(dentalPlanDesignCode);
    }
}
