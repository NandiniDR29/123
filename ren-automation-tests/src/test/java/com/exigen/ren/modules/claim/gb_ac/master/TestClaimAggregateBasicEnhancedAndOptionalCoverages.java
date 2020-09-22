package com.exigen.ren.modules.claim.gb_ac.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsAccidentalDismembermentIncidentTab;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsAccidentalDismembermentInjuryPartyInformationTab;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsAccidentalDismembermentInjuryPartyInformationTab.ListOfItemizedInjuryIllness;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDismembermentInjuryPartyInformationTabMetaData.ASSOCIATED_POLICY_PARTY;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDismembermentInjuryPartyInformationTabMetaData.ITEMIZED_INJURY_ILLNESS;
import static com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDismembermentInjuryPartyInformationTabMetaData.ItemizedInjuryIllnessMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.ASSOCIATED_INSURABLE_RISK;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.COVERAGE;
import static com.exigen.ren.main.modules.claim.common.tabs.BenefitsAccidentalDismembermentInjuryPartyInformationTab.removeBenefitFromListOfItemizedInjuryIllnessTable;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAggregateBasicEnhancedAndOptionalCoverages extends ClaimGroupBenefitsACBaseTest {

    private PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
    private AbstractContainer<?, ?> coveragesActionTabAssetList = accHealthClaim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class).getAssetList();
    private static final String AIR_AMBULANCE_BENEFIT = "Air Ambulance Benefit";
    private static final String GROUND_AMBULANCE_BENEFIT = "Ground Ambulance Benefit";
    private static final String BASIC_AIR_AMBULANCE_COVERAGE = "Basic Air Ambulance Benefit - Base Buy-Up";
    private static final String ENHANCED_AIR_AMBULANCE_COVERAGE = "Enhanced Air Ambulance Benefit - Base Buy-Up";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-32638", "REN-32652", "REN-32660"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimAggregateBasicEnhancedAndOptionalCoverages() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(tdSpecific().getTestData("TestData_Policy")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));

        initiateClaimWithPolicyAndFillUpToTab(accHealthClaim.getGbACTestData().getTestData(DATA_GATHER, "TestData_AccidentalDismembermentBenefit")
                        .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()),
                benefitsAccidentalDismembermentInjuryPartyInformationTab.getClass(), false);

        LOGGER.info("TEST REN-32638: Step 1");
        benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_POLICY_PARTY).setValueByIndex(1);
        benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ASSOCIATED_BENEFITS).setValue(AIR_AMBULANCE_BENEFIT);
        benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains(AIR_AMBULANCE_BENEFIT);

        assertThat(benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE)).hasValue(new Currency(3200).toString());

        LOGGER.info("TEST REN-32638: Step 2");
        BenefitsAccidentalDismembermentInjuryPartyInformationTab.buttonAddItem.click();
        benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ASSOCIATED_BENEFITS).setValue(GROUND_AMBULANCE_BENEFIT);
        benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains(GROUND_AMBULANCE_BENEFIT);

        assertSoftly(softly -> {
            softly.assertThat(benefitsAccidentalDismembermentInjuryPartyInformationTab.getAssetList().getAsset(ITEMIZED_INJURY_ILLNESS).getAsset(ESTIMATED_COST_VALUE))
                    .hasValue(new Currency(800).toString());
            softly.assertThat(BenefitsAccidentalDismembermentInjuryPartyInformationTab.tableListOfItemizedInjuryIllness).hasMatchingRows(1, ImmutableMap.of(
                    ListOfItemizedInjuryIllness.DESCRIPTION.getName(), AIR_AMBULANCE_BENEFIT,
                    ListOfItemizedInjuryIllness.ESTIMATED_COST_VALUE.getName(), new Currency(3200).toString()));
            softly.assertThat(BenefitsAccidentalDismembermentInjuryPartyInformationTab.tableListOfItemizedInjuryIllness).hasMatchingRows(1, ImmutableMap.of(
                    ListOfItemizedInjuryIllness.DESCRIPTION.getName(), GROUND_AMBULANCE_BENEFIT,
                    ListOfItemizedInjuryIllness.ESTIMATED_COST_VALUE.getName(), new Currency(800).toString()));
        });

        removeBenefitFromListOfItemizedInjuryIllnessTable(GROUND_AMBULANCE_BENEFIT);

        benefitsAccidentalDismembermentInjuryPartyInformationTab.fillTab(tdSpecific().getTestData("TestData_Claim")).submitTab();

        accHealthClaim.getDefaultWorkspace().fillFrom(accHealthClaim.getGbACTestData().getTestData(DATA_GATHER, "TestData_AccidentalDismembermentBenefit"),
                BenefitsAccidentalDismembermentIncidentTab.class);

        accHealthClaim.claimOpen().perform();
        String participantName = ClaimSummaryPage.tableClaimParticipant.getRow(1).getCell("Participant").getValue();

        LOGGER.info("TEST REN-32652: Step 1");
        accHealthClaim.calculateSingleBenefitAmount().start(1);
        coveragesActionTabAssetList.getAsset(ASSOCIATED_INSURABLE_RISK).setValueContains(AIR_AMBULANCE_BENEFIT);

        assertThat(coveragesActionTabAssetList.getAsset(COVERAGE)).containsAllOptions(EMPTY, BASIC_AIR_AMBULANCE_COVERAGE, ENHANCED_AIR_AMBULANCE_COVERAGE);

        coveragesActionTabAssetList.getAsset(COVERAGE).setValue(BASIC_AIR_AMBULANCE_COVERAGE);
        CoveragesActionTab.buttonAddCoverage.click();
        coveragesActionTabAssetList.getAsset(ASSOCIATED_INSURABLE_RISK).setValueContains(AIR_AMBULANCE_BENEFIT);
        coveragesActionTabAssetList.getAsset(COVERAGE).setValue(ENHANCED_AIR_AMBULANCE_COVERAGE);

        assertSoftly(softly -> {
            softly.assertThat(CoveragesActionTab.tableListOfCoverageDeterminationAndReserves)
                    .hasRowsThatContain(CoveragesActionTab.ListOfCoverageDeterminationAndReserves.COVERAGE.getName(), BASIC_AIR_AMBULANCE_COVERAGE);
            softly.assertThat(CoveragesActionTab.tableListOfCoverageDeterminationAndReserves)
                    .hasRowsThatContain(CoveragesActionTab.ListOfCoverageDeterminationAndReserves.COVERAGE.getName(), ENHANCED_AIR_AMBULANCE_COVERAGE);
        });

        accHealthClaim.calculateSingleBenefitAmount().submit();

        LOGGER.info("TEST REN-32660: Step 1");
        commonStepREN_32660(
                AIR_AMBULANCE_BENEFIT,
                ImmutableList.of(BASIC_AIR_AMBULANCE_COVERAGE, ENHANCED_AIR_AMBULANCE_COVERAGE),
                participantName);

        LOGGER.info("TEST REN-32660: Step 2");
        commonStepREN_32660(
                GROUND_AMBULANCE_BENEFIT,
                ImmutableList.of("Basic Ground Ambulance Benefit - Base Buy-Up", "Enhanced Ground Ambulance Benefit - Base Buy-Up"),
                participantName);

        LOGGER.info("TEST REN-32660: Step 3");
        commonStepREN_32660(
                "Emergency Room Treatment Benefit",
                ImmutableList.of("Basic Emergency Room Treatment Benefit - Base Buy-Up", "Enhanced Emergency Room Treatment Benefit - Base Buy-Up", "Optional Emergency Room Treatment Benefit - Base Buy-Up"),
                participantName);

        LOGGER.info("TEST REN-32660: Step 4");
        commonStepREN_32660(
                "Hospital Admission Benefit",
                ImmutableList.of("Basic Hospital Admission Benefit - Base Buy-Up", "Enhanced Hospital Admission Benefit - Base Buy-Up"),
                participantName);

        LOGGER.info("TEST REN-32660: Step 5");
        commonStepREN_32660(
                "Intensive Care Unit Admission Benefit",
                ImmutableList.of("Basic Intensive Care Unit Admission Benefit - Base Buy-Up", "Enhanced Intensive Care Unit Admission Benefit - Base Buy-Up"),
                participantName);

        LOGGER.info("TEST REN-32660: Step 6");
        commonStepREN_32660(
                "Major Diagnostic Benefit",
                ImmutableList.of("Basic Major Diagnostic Benefit - Base Buy-Up", "Enhanced Major Diagnostic Benefit - Base Buy-Up"),
                participantName);

        LOGGER.info("TEST REN-32660: Step 7");
        commonStepREN_32660(
                "Physical Therapy Services Benefit",
                ImmutableList.of("Basic Physical Therapy Services Benefit - Base Buy-Up", "Enhanced Physical Therapy Services Benefit - Base Buy-Up"),
                participantName);

        LOGGER.info("TEST REN-32660: Step 8");
        commonStepREN_32660(
                "Physician's Office/Urgent Care Treatment Benefit",
                ImmutableList.of("Optional Physician's Office/Urgent Care Treatment Benefit - Base Buy-Up", "Enhanced Physician's Office/Urgent Care Treatment Benefit - Base Buy-Up"),
                participantName);

        LOGGER.info("TEST REN-32660: Step 9");
        commonStepREN_32660(
                "Hospital ICU Confinement Benefit",
                ImmutableList.of("Enhanced Hospital ICU Confinement Benefit - Base Buy-Up", "Optional Hospital ICU Confinement Benefit - Base Buy-Up"),
                participantName);

        LOGGER.info("TEST REN-32660: Step 10");
        commonStepREN_32660(
                "Accidental Death Benefit - Employee",
                ImmutableList.of("Enhanced Accidental Death Benefit - Employee - Base Buy-Up", "Optional Accidental Death Benefit - Employee - Base Buy-Up"),
                participantName);

        LOGGER.info("TEST REN-32660: Step 11");
        commonStepREN_32660(
                "Accidental Death Benefit - Spouse",
                ImmutableList.of("Enhanced Accidental Death Benefit - Spouse - Base Buy-Up", "Optional Accidental Death Benefit - Spouse - Base Buy-Up"),
                participantName);

        LOGGER.info("TEST REN-32660: Step 12");
        commonStepREN_32660(
                "Accidental Death Benefit - Child",
                ImmutableList.of("Enhanced Accidental Death Benefit - Child - Base Buy-Up", "Optional Accidental Death Benefit - Child - Base Buy-Up"),
                participantName);
    }

    private void commonStepREN_32660(String associatedInsurableRisk, List<String> coverageList, String participantName) {
        accHealthClaim.calculateSingleBenefitAmount().start(1);

        coverageList.forEach(coverage -> {
            coveragesActionTabAssetList.getAsset(ASSOCIATED_INSURABLE_RISK).setValueContains(associatedInsurableRisk);
            coveragesActionTabAssetList.getAsset(COVERAGE).setValue(coverage);
            if (!coverage.equals(coverageList.get(coverageList.size() - 1))) {
                CoveragesActionTab.buttonAddCoverage.click();
            }
        });
        accHealthClaim.calculateSingleBenefitAmount().submit();

        accHealthClaim.initiatePaymentAndFillUpToTab(tdSpecific().getTestData("TestData_Payment"), PaymentPaymentPaymentAllocationTab.class, false);

        coverageList.forEach(coverage -> {
            accHealthClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).fillTab(tdSpecific().getTestData("TestData_Payment"));
            paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PaymentPaymentPaymentAllocationTabMetaData.COVERAGE).setValueContains(coverage);
            if (!coverage.equals(coverageList.get(coverageList.size() - 1))) {
                PaymentPaymentPaymentAllocationTab.buttonAddCoverage.click();
            }
        });

        coverageList.forEach(coverage -> {
            assertSoftly(softly -> {
                String paymentAllocation = String.format("%s (%s) - %s - Indemnity - $1,000.00", associatedInsurableRisk, participantName, coverage);
                softly.assertThat(PaymentPaymentPaymentAllocationTab.tableListOfPaymentAllocations.getRow(2, paymentAllocation)).exists();
            });
        });

        PaymentPaymentPaymentAllocationTab.buttonCancel.click();
        dialogConfirmation.confirm();
    }
}