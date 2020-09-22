/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimFicaMatch extends ClaimGroupBenefitsSTBaseTest {

    private static final String FICA_VALUE = "Embedded";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-14292", component = CLAIMS_GROUPBENEFITS)
    public void testClaimFicaMatch() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(getDefaultSTMasterPolicyData()
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE,TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName(), PlanDefinitionTabMetaData.FICA_MATCH.getLabel()), FICA_VALUE));

        LOGGER.info("REN-14292, Steps 5-6");
        TestData defaultClaimTd = disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
        disabilityClaim.initiate(defaultClaimTd);
        disabilityClaim.getDefaultWorkspace().fillUpTo(defaultClaimTd, PolicyInformationParticipantParticipantCoverageTab.class, true);

        CustomAssertions.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.FICA_MATCH)).isDisabled().hasValue(FICA_VALUE);
    }
}
