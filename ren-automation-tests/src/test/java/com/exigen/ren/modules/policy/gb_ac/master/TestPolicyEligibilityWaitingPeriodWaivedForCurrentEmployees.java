package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyEligibilityWaitingPeriodWaivedForCurrentEmployees extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {


    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13328", component = POLICY_GROUPBENEFITS)
    public void testPolicyEligibilityWaitingPeriodWaivedForCurrentEmployees() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());

        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData(), PlanDefinitionTab.class);
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).fill(getDefaultACMasterPolicyData().getTestDataList(planDefinitionTab.getClass().getSimpleName()).get(0));

        LOGGER.info("Test REN-13328, Step 2.1 to 2.3");
        assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY)
                .getAsset(PlanDefinitionTabMetaData.EligibilityMetadata.WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES)).isOptional().hasValue(StringUtils.EMPTY);

        groupAccidentMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultACMasterPolicyData(), PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.submitTab();

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        groupAccidentMasterPolicy.dataGather().start();

        NavigationPage.PolicyNavigation.expand(NavigationEnum.GroupBenefitsTab.PLAN_GENERIC_INFO.get(),NavigationEnum.PlanGenericInfoTab.PLAN_DEFINITION.get());

        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY)
                .getAsset(PlanDefinitionTabMetaData.EligibilityMetadata.WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES).setValue(VALUE_NO);
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Test REN-13328, Step 6.1");
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);


    }
}
