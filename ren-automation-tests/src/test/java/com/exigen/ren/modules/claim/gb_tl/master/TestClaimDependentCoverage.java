package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationDependentDependentInformationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN_PLUS_VOLUNTARY;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationDependentDependentInformationTabMetaData.RELATIONSHIP_TO_PARTICIPANT;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationDependentDependentCoverageTabMetaData.COVERAGE_TYPE_TEXT_BOX;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimDependentCoverage extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28114", component = CLAIMS_GROUPBENEFITS)
    public void testVerifyPolicyToClaimMappingForDependentCoverage() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData().adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()).resolveLinks());

        termLifeClaim.initiate(termLifeClaim.getDefaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY));

        LOGGER.info("Test: Step 1");
        termLifeClaim.getDefaultWorkspace().fillUpTo(termLifeClaim.getDefaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), PolicyInformationParticipantParticipantCoverageTab.class);

        policyInformationParticipantParticipantCoverageTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);
        policyInformationParticipantParticipantCoverageTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, VOL_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        LOGGER.info("Test: Steps 2-3");
        policyInformationDependentDependentInformationTab.navigate();
        policyInformationDependentDependentInformationTab.fillTab(termLifeClaim.getDefaultTestData().getTestData("DataGatherWithoutPolicy", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(PolicyInformationDependentDependentInformationTab.class.getSimpleName(), RELATIONSHIP_TO_PARTICIPANT.getLabel()), "Spouse/Domestic Partner"));

        policyInformationDependentDependentCoverageTab.navigate();
        policyInformationDependentDependentCoverageTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, DEP_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        assertSoftly(softly -> {
            softly.assertThat(policyInformationDependentDependentCoverageTab.getAssetList().
                    getAsset(COVERAGE_TYPE_TEXT_BOX)).isOptional().isDisabled().hasValue("Non-Occupational");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(BENEFIT_MAXIMUM_AMOUNT)).isRequired().isDisabled().hasValue(new Currency(15000).toString());
        });

        policyInformationDependentDependentInformationTab.navigate();
        policyInformationDependentDependentInformationTab.getAssetList().getAsset(RELATIONSHIP_TO_PARTICIPANT).setValue("Dependent Child");

        policyInformationDependentDependentCoverageTab.navigate();
        policyInformationDependentDependentCoverageTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, DEP_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        assertSoftly(softly -> {
            softly.assertThat(policyInformationDependentDependentCoverageTab.getAssetList().
                    getAsset(COVERAGE_TYPE_TEXT_BOX)).isOptional().isDisabled().hasValue("Non-Occupational");
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(BENEFIT_MAXIMUM_AMOUNT)).isRequired().isDisabled().hasValue(new Currency(20000).toString());
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(COVERAGE_UP_TO_MAX_OF_EMPLOYEE_COVERAGE)).isPresent().hasValue("50.00");
        });

        LOGGER.info("Test: Step 6");
        policyInformationDependentDependentInformationTab.navigate();
        policyInformationDependentDependentInformationTab.getAssetList().getAsset(RELATIONSHIP_TO_PARTICIPANT).setValue("Spouse/Domestic Partner");

        policyInformationDependentDependentCoverageTab.navigate();
        policyInformationDependentDependentCoverageTab.addCoverage(BASIC_LIFE_PLAN_PLUS_VOLUNTARY, DEP_VOL_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY);

        LOGGER.info("Test: Step 7");
        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_MAXIMUM_AMOUNT)).isAbsent();
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                    .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_MINIMUM_AMOUNT)).isAbsent();
        });
    }
}