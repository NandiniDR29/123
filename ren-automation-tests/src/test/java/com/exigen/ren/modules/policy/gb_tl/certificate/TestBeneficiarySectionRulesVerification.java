package com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.table.Column;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CoveragesConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.COVERAGES;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.BTL;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.DEP_BTL;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CoveragesTabMetaData.BENEFICIARIES;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CoveragesTab.*;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelInsuredNameCp;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class TestBeneficiarySectionRulesVerification extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-28727", component = POLICY_GROUPBENEFITS)
    public void testBeneficiarySectionRulesVerification() {
        LOGGER.info("REN-28727 preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicyViaUI(getDefaultTLMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataCoverages").resolveLinks()));

        termLifeInsuranceCertificatePolicy.initiate(getDefaultCertificatePolicyDataGatherData());
        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultCertificatePolicyDataGatherData(), CoveragesTab.class, false);

        LOGGER.info("Step#1 verification");
        addPlanAndBeneficiarySelectionCheck("TestDataAddPlanWithBasicLifeCoverage", BTL);
        addPlanAndBeneficiarySelectionCheck("TestDataAddEmployeeBasicAccidentalDeathCoverage", CoveragesConstants.TermLifeCoverages.ADD);

        LOGGER.info("Step#2 verification");
        String insuredName = String.format("Self, %s", labelInsuredNameCp.getValue());

        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fill(tdSpecific().getTestData("TestDataAddDependentBasicLifeInsuranceCoverage"));
        NavigationPage.toLeftMenuTab(COVERAGES.get());
        openAddedPlan(DEP_BTL);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ADD_BENEFICIARY).click();
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.BENEFICIARY_SELECTION)).hasOptions(insuredName, EMPTY, "New Person", "New Non-Person");

        LOGGER.info("Step#7-8 verification");
        Column customerName = tableBeneficiariesList.getColumn(TableConstants.CertificateBeneficiaries.CUSTOMER_NAME.getName());
        Column relationshipToInsured = tableBeneficiariesList.getColumn(TableConstants.CertificateBeneficiaries.RELATIONSHIP_TO_INSURED.getName());
        Column customerNamePart = tableParticipantsList.getColumn(TableConstants.CertificateParticipants.CUSTOMER_NAME.getName());
        Column relationshipToInsuredPart = tableParticipantsList.getColumn(TableConstants.CertificateParticipants.RELATIONSHIP_TO_INSURED.getName());
        ComboBox beneficiarySelection = coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.BENEFICIARY_SELECTION);

        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fill(tdSpecific().getTestData("TestDataBeneficiaryCoverageBTL"));
        NavigationPage.toLeftMenuTab(COVERAGES.get());
        buttonTopSave.click();

        String participantCoverage1 = String.format("%s, %s", relationshipToInsuredPart.getCell(1).getValue(), customerNamePart.getCell(1).getValue());
        String beneficiary1Coverage1 = String.format("%s, %s", relationshipToInsured.getCell(1).getValue(), customerName.getCell(1).getValue());
        String beneficiary2Coverage1 = String.format("%s, %s", relationshipToInsured.getCell(2).getValue(), customerName.getCell(2).getValue());
        String beneficiary3Coverage1 = String.format("%s, %s", relationshipToInsured.getCell(3).getValue(), customerName.getCell(3).getValue());
        String beneficiary4Coverage1 = String.format("%s, %s", relationshipToInsured.getCell(4).getValue(), customerName.getCell(4).getValue());

        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fill(tdSpecific().getTestData("TestDataBeneficiaryCoverageDEP_BTL"));
        NavigationPage.toLeftMenuTab(COVERAGES.get());
        buttonTopSave.click();
        openAddedPlan(DEP_BTL);

        String participantCoverage2 = String.format("%s, %s", relationshipToInsuredPart.getCell(1).getValue(), customerNamePart.getCell(1).getValue());
        String beneficiary1Coverage2 = String.format("%s, %s", relationshipToInsured.getCell(1).getValue(), customerName.getCell(1).getValue());
        String beneficiary2Coverage2 = String.format("%s, %s", relationshipToInsured.getCell(2).getValue(), customerName.getCell(2).getValue());
        String beneficiary3Coverage2 = String.format("%s, %s", relationshipToInsured.getCell(3).getValue(), customerName.getCell(3).getValue());
        String beneficiary4Coverage2 = String.format("%s, %s", relationshipToInsured.getCell(4).getValue(), customerName.getCell(4).getValue());
        String beneficiary5Coverage2 = String.format("%s, %s", relationshipToInsured.getCell(5).getValue(), customerName.getCell(5).getValue());

        openAddedPlan(BTL);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ADD_BENEFICIARY).click();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(beneficiarySelection).doesNotContainOption(participantCoverage1);
            softly.assertThat(beneficiarySelection).doesNotContainOption(beneficiary1Coverage1);
            softly.assertThat(beneficiarySelection).doesNotContainOption(beneficiary2Coverage1);
            softly.assertThat(beneficiarySelection).doesNotContainOption(beneficiary3Coverage1);
            softly.assertThat(beneficiarySelection).doesNotContainOption(beneficiary4Coverage1);
            softly.assertThat(beneficiarySelection).containsOption(participantCoverage2);
            softly.assertThat(beneficiarySelection).containsAllOptions(ImmutableList.of(beneficiary2Coverage2, beneficiary3Coverage2, beneficiary4Coverage2, beneficiary5Coverage2));
            softly.assertThat(beneficiarySelection).doesNotContainOption(beneficiary1Coverage2);

            LOGGER.info("Step#9-10 verification");
            openAddedPlan(DEP_BTL);
            coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ADD_BENEFICIARY).click();
            softly.assertThat(beneficiarySelection).containsAllOptions(ImmutableList.of(beneficiary1Coverage1, beneficiary2Coverage1, beneficiary3Coverage1, beneficiary4Coverage1));
            softly.assertThat(beneficiarySelection).doesNotContainOption(participantCoverage2);
            softly.assertThat(beneficiarySelection).doesNotContainOption(beneficiary1Coverage2);
            softly.assertThat(beneficiarySelection).doesNotContainOption(beneficiary2Coverage2);
            softly.assertThat(beneficiarySelection).doesNotContainOption(beneficiary3Coverage2);
            softly.assertThat(beneficiarySelection).doesNotContainOption(beneficiary4Coverage2);
            softly.assertThat(beneficiarySelection).doesNotContainOption(beneficiary5Coverage2);
        });
    }

    private void addPlanAndBeneficiarySelectionCheck(String testData, String coverageName) {
        termLifeInsuranceCertificatePolicy.getDefaultWorkspace().fill(tdSpecific().getTestData(testData));
        NavigationPage.toLeftMenuTab(COVERAGES.get());
        openAddedPlan(coverageName);
        coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.ADD_BENEFICIARY).click();
        assertThat(coveragesTab.getAssetList().getAsset(BENEFICIARIES).getAsset(CoveragesTabMetaData.BeneficiariesMetaData.BENEFICIARY_SELECTION)).hasOptions(EMPTY, "New Person", "New Non-Person");
    }
}