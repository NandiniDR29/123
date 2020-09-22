package com.exigen.ren.modules.claim.gb_ac.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsDiagnosisAndTreatmentInjuryPartyInformationTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FNOL;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitsDiagnosisAndTreatmentInjuryPartyInformationTabMetaData.ITEMIZED_INJURY_ILLNESS;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitsDiagnosisAndTreatmentInjuryPartyInformationTabMetaData.ItemizedInjuryIllness.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckACMasterPolicyIntegrationWithClaims extends ClaimGroupBenefitsACBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-30354", component = CLAIMS_GROUPBENEFITS)
    public void testVerifyCalculateSingleBenefitCalculationAndPayment() {
        final String ANKLE_BONE_FOOT_CLOSED_REDUCTION = "Ankle / Bone or Bones of the Foot-Closed Reduction";
        Currency estimatedCostFractureBenefit = new Currency(600);
        Currency estimatedCostDislocationBenefit = new Currency(1600);
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData")));
        createDefaultGroupAccidentCertificatePolicy();

        LOGGER.info("TEST REN-30354: Step 1");
        accHealthClaim.initiate(tdSpecific().getTestData("TestData_Claim"));
        accHealthClaim.getDefaultWorkspace().fillUpTo(tdSpecific().getTestData("TestData_Claim"), BenefitsDiagnosisAndTreatmentInjuryPartyInformationTab.class, true);
        assertThat(benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE)).hasValue(estimatedCostFractureBenefit.toString());

        LOGGER.info("TEST REN-30354: Step 2");
        benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(CHIP_FRACTURE_BENEFIT_AMOUNT).setValue(true);
        assertThat(benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE)).hasValue(new Currency(150).toString());

        LOGGER.info("TEST REN-30354: Step 3");
        benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(CHIP_FRACTURE_BENEFIT_AMOUNT).setValue(false);
        assertThat(benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE)).hasValue(estimatedCostFractureBenefit.toString());

        LOGGER.info("TEST REN-30354: Step 4");
        BenefitsDiagnosisAndTreatmentInjuryPartyInformationTab.buttonSaveAndExit.click();
        claim.updateBenefit().start(1);
        benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains("Ankle-Open Reduction");
        assertThat(benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE)).hasValue(new Currency(1200).toString());
        BenefitsDiagnosisAndTreatmentInjuryPartyInformationTab.buttonCancel.click();
        Page.dialogConfirmation.buttonYes.click();

        LOGGER.info("TEST REN-30354: Step 6");
        toSubTab(FNOL);
        benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.navigate();
        BenefitsDiagnosisAndTreatmentInjuryPartyInformationTab.buttonAddItem.click();
        benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ASSOCIATED_BENEFITS).setValueContains("Dislocation Benefit");
        benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains(ANKLE_BONE_FOOT_CLOSED_REDUCTION);
        assertThat(benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE)).hasValue(estimatedCostDislocationBenefit.toString());

        LOGGER.info("TEST REN-30354: Step 7");
        benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(PARTIAL_DISLOCATION_BENEFIT_AMOUNT).setValue(true);
        assertThat(benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE)).hasValue(new Currency(400).toString());

        LOGGER.info("TEST REN-30354: Step 8");
        benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(PARTIAL_DISLOCATION_BENEFIT_AMOUNT).setValue(false);
        assertThat(benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE)).hasValue(estimatedCostDislocationBenefit.toString());

        LOGGER.info("TEST REN-30354: Step 9");
        BenefitsDiagnosisAndTreatmentInjuryPartyInformationTab.buttonSaveAndExit.click();
        claim.updateBenefit().start(1);
        BenefitsDiagnosisAndTreatmentInjuryPartyInformationTab.listOfItemizedInjuryIllness.getRow(BenefitsDiagnosisAndTreatmentInjuryPartyInformationTab.ListOfItemizedInjuryIllness.DESCRIPTION.getName(), ANKLE_BONE_FOOT_CLOSED_REDUCTION)
                .getCell(5).controls.links.get(CHANGE).click();
        benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains("Ankle / Bone or Bones of the Foot-Open");
        assertThat(benefitsDiagnosisAndTreatmentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE)).hasValue(new Currency(3200).toString());
    }
}
