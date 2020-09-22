package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.INCLUDED;
import static com.exigen.ren.main.enums.ValueConstants.NOT_INCLUDED;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OffsetsMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionOffsets extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private static final ImmutableList<String> OFFSET_LOOKUP_VALUES = ImmutableList.of(INCLUDED, NOT_INCLUDED);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-13514", component = POLICY_GROUPBENEFITS)
    public void testPlanDefinitionOffsets() {

        preconditions();

        assertSoftly(softly -> {

            LOGGER.info("Test REN-13514 Step 1");
            ImmutableList.of(
                    SICK_LEAVE, PTO,
                    COMPULSORY_STATE_PLANS,
                    TERMINATION_OR_SEVERANCE,
                    WORK_EARNINGS,
                    RETIREMENT_PLAN,
                    AUTO_LIABILITY,
                    THIRD_PARTY_SETTLEMENT).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OFFSETS).getAsset(control)).isPresent().hasValue(INCLUDED).hasOptions(OFFSET_LOOKUP_VALUES));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.OFFSETS).getAsset(INDIVIDUAL_DISABILITY_PLAN)).isPresent().hasValue(NOT_INCLUDED).hasOptions(OFFSET_LOOKUP_VALUES);
        });
    }

    private void preconditions() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        initiateQuoteAndSelectDefaultPlan(getDefaultLTDMasterPolicyData());
    }
}


