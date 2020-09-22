package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.claim.gb_ltd.scenarios.ScenarioTestClaimFamilyCareBenefitSinglePayment;
import org.testng.annotations.Test;

import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimFamilyCareBenefitSinglePayment extends ScenarioTestClaimFamilyCareBenefitSinglePayment {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-29589", component = CLAIMS_GROUPBENEFITS)
    public void testClaimFamilyCareBenefitSinglePayment() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", PlanDefinitionTabMetaData.OPTIONS.getLabel(), FAMILY_CARE_BENEFIT.getLabel()), "Included")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", PlanDefinitionTabMetaData.OPTIONS.getLabel(), FAMILY_CARE_BENEFIT_AMOUNT.getLabel()), "$350")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", PlanDefinitionTabMetaData.OPTIONS.getLabel(), FAMILY_CARE_BENEFIT_AMOUNT_DURATION.getLabel()), "12 Months")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", PlanDefinitionTabMetaData.OPTIONS.getLabel(), FAMILY_CARE_BENEFIT_MAXIMUM_ALL_FAMILY_MEMBERS.getLabel()), "$2,000")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", PlanDefinitionTabMetaData.BENEFIT_SCHEDULE.getLabel(), BENEFIT_PERCENTAGE.getLabel()), "60%")
                .resolveLinks());

        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(TestData.makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "$300,000.00")
                .resolveLinks());

        super.testClaimFamilyCareBenefitSinglePayment();
    }
}