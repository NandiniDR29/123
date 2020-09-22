package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.NC;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyChangesNeededToSupportDocGen extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private List<String> states = new ArrayList<>(Arrays.asList("NY", "CT", "AK"));

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-37170, REN-37174"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyAttributesNeededToSupportDocGen() {
        LOGGER.info("Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        LOGGER.info("REN-37170, REN-37174  STEP#1 Verify changes for attributes");
        states.forEach(state -> {
            longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData()
                    .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()))
                    .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), state), PlanDefinitionTab.class);
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(Collections.singletonList(NC));

            LOGGER.info(String.format("REN-37170 STEP#1 Verify changes for attribute 'Annual Enrollment Underwriting Offer', state - %s", state));
            CustomAssertions.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(PlanDefinitionTabMetaData.BenefitScheduleMetaData.EARNING_DEFINITION)).containsAllOptions("Net Profit (12 months)", "Net Profit (24 months)", "Net Profit (36 months)");

            LOGGER.info(String.format("REN-37174 STEP#1 Verify changes for attribute 'Military Leave Duration', state - %s", state));
            CustomAssertions.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.SCHEDULE_OF_CONTINUATION_PROVISION).getAsset(PlanDefinitionTabMetaData.ScheduleOfContinuationProvisionMetaData.MILITARY_LEAVE_DURATION)).hasOptions("12 Months", "24 Months");

            policyInformationTab.navigateToTab();
        });

        LOGGER.info("REN-37170 STEP#3 Verify changes for attribute 'Annual Enrollment Underwriting Offer'");
        planDefinitionTab.navigateToTab();
        CustomAssertions.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ANNUAL_ENROLLMENT_UNDERWRITING_OFFER)).hasOptions("Enrollment period - No EOI required", "Enrollment period - EOI required", "Enrollment period No EOI/Changing with EOI Only", "No Enrollment Period");
    }
}
