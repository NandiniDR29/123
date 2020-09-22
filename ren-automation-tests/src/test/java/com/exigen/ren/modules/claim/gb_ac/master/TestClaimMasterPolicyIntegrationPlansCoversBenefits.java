package com.exigen.ren.modules.claim.gb_ac.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsAccidentalDismembermentIncidentTab;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsAccidentalDismembermentInjuryPartyInformationTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDismembermentInjuryPartyInformationTabMetaData.ASSOCIATED_POLICY_PARTY;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDismembermentInjuryPartyInformationTabMetaData.ITEMIZED_INJURY_ILLNESS;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDismembermentInjuryPartyInformationTabMetaData.ItemizedInjuryIllnessMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimMasterPolicyIntegrationPlansCoversBenefits extends ClaimGroupBenefitsACBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-29762", component = CLAIMS_GROUPBENEFITS)
    public void testClaimMasterPolicyIntegrationPlansCoversBenefits() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createQuote(tdSpecific().getTestData("TestData_AllPlans_EmployeeFamily")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsAtoCTab[0]", "PlanKey"), "BASEBU-Base Buy-Up")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsAtoCTab[1]", "PlanKey"), "VOL10-Voluntary 10 Units")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsAtoCTab[2]", "PlanKey"), "ENHANCED10-Enhanced 10 Units")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsDtoFTab[0]", "PlanKey"), "BASEBU-Base Buy-Up")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsDtoFTab[1]", "PlanKey"), "VOL10-Voluntary 10 Units")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsDtoFTab[2]", "PlanKey"), "ENHANCED10-Enhanced 10 Units")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsHtoLTab[0]", "PlanKey"), "BASEBU-Base Buy-Up")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsHtoLTab[1]", "PlanKey"), "VOL10-Voluntary 10 Units")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsHtoLTab[2]", "PlanKey"), "ENHANCED10-Enhanced 10 Units")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsMtoTTab[0]", "PlanKey"), "BASEBU-Base Buy-Up")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsMtoTTab[1]", "PlanKey"), "VOL10-Voluntary 10 Units")
                .adjust(TestData.makeKeyPath("EnhancedBenefitsMtoTTab[2]", "PlanKey"), "ENHANCED10-Enhanced 10 Units")
                .adjust(TestData.makeKeyPath("OptionalBenefitTab[1]", "PlanKey"), "VOL10-Voluntary 10 Units")
                .adjust(TestData.makeKeyPath("OptionalBenefitTab[2]", "PlanKey"), "ENHANCED10-Enhanced 10 Units"));
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.issue().perform(getDefaultACMasterPolicyData());

        LOGGER.info("Steps 1,2");
        TestData tdClaim = accHealthClaim.getGbACTestData().getTestData(DATA_GATHER, "TestData_AccidentalDismembermentBenefit");
        accHealthClaim.initiate(tdClaim);
        accHealthClaim.getDefaultWorkspace().fillUpTo(tdClaim, BenefitsAccidentalDismembermentInjuryPartyInformationTab.class, true);

        LOGGER.info("Step 3");
        String name = String.format(" (%s)", benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_POLICY_PARTY).getValue());
        checkBenefitsItemsValues(name);

        LOGGER.info("Step 5");
        benefitsAccidentalDismembermentInjuryPartyInformationTab.submitTab();
        accHealthClaim.getDefaultWorkspace().fillFrom(tdClaim, BenefitsAccidentalDismembermentIncidentTab.class);
        claim.claimOpen().perform();

        LOGGER.info("Step 6");
        claim.updateBenefit().start(1);
        checkBenefitsItemsValues(name);
    }

    private void checkBenefitsItemsValues(String name) {
        TestData td = tdSpecific().getTestData("TestData_Check");
        assertSoftly(softly -> {
            td.getKeys().forEach(parentKey -> {
                td.getTestData(parentKey).getKeys().forEach(childKey -> {
                    benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ASSOCIATED_BENEFITS).setValue(parentKey);
                    benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ASSOCIATED_SCHEDULED_ITEM).setValue(childKey + name);
                    String expectedEstimatedCostValue = new Currency(td.getValue(parentKey, childKey)).toString();
                    String actualEstimatedCostValue = benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE).getValue();
                    softly.assertThat(actualEstimatedCostValue).withFailMessage("Wrong value by path: %s - %s. Expected: %s, Actual: %s.", parentKey, childKey, expectedEstimatedCostValue, actualEstimatedCostValue)
                            .isEqualTo(expectedEstimatedCostValue);
                });
            });
        });
    }

}
