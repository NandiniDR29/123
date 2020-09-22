package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationSponsorTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDIncidentTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.ASSOCIATED_INSURABLE_RISK;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.ASSOCIATE_POLICY_PARTY;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.REPORTED_EXPENSE_AMOUNT;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPolicyBenefitOptionsMappingToClaim extends ClaimGroupBenefitsLTDBaseTest {

    private static final List<String> listBenefitsDisplayed = ImmutableList.of("Survivor - Family Income Benefit", "Workplace Modification Benefit", "Terminal Illness Benefit", "Presumptive Benefit Remaining Balance");
    private static final List<String> listBenefitsNotDisplayed = ImmutableList.of("Child Education Benefit", "COBRA Premium Reimbursement Benefit", "Student Loan Repayment Benefit");

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-21643, REN-21675, REN-21676, REN-21677, REN-21678, REN-21679, REN-21680, REN-21681, REN-21682, REN-21683"}, component = CLAIMS_GROUPBENEFITS)
    public void testSurvivorFamilyIncomeBenefitMappingToclaim() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy_CON").resolveLinks()));

        LOGGER.info("Step 1");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                        .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                        .adjust(makeKeyPath(policyInformationParticipantParticipantCoverageTab.getMetaKey(), BENEFIT_PERCENTAGE.getLabel()), "60"),
                PolicyInformationParticipantParticipantCoverageTab.class, true);

        assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList()
                .getAsset(PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_AMOUNT)).hasValue(new Currency(600).toString());

        policyInformationParticipantParticipantInformationTab.submitTab();

        LOGGER.info("Step 3");
        disabilityClaim.getDefaultWorkspace().fillFromTo(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY),
                PolicyInformationSponsorTab.class, BenefitsLTDInjuryPartyInformationTab.class, false);

        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATE_POLICY_PARTY).setValueByIndex(1);

        String insuredPartyName = benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATE_POLICY_PARTY).getValue();

        assertSoftly(softly -> listBenefitsDisplayed.forEach(benefit ->
                softly.assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM)).containsOption(String.format("%s (%s)", benefit, insuredPartyName))));

        assertSoftly(softly -> listBenefitsNotDisplayed.forEach(benefit ->
                softly.assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM)).doesNotContainOption(String.format("%s (%s)", benefit, insuredPartyName))));

        LOGGER.info("TEST: Step 4");
        listBenefitsDisplayed.forEach(benefit -> {
            benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).setValue(String.format("%s (%s)", benefit, insuredPartyName));
            if (benefit.equals("Workplace Modification Benefit")) {
                benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(REPORTED_EXPENSE_AMOUNT).setValue("3000");
            }
            BenefitsLTDInjuryPartyInformationTab.buttonAddAssociatedScheduledItem.click();
        });

        LOGGER.info("TEST: Step 6");
        benefitsLTDInjuryPartyInformationTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), BenefitsLTDIncidentTab.class);
        disabilityClaim.claimOpen().perform();

        disabilityClaim.calculateSingleBenefitAmount().start(1);

        assertSoftly(softly -> listBenefitsDisplayed.forEach(benefit ->
                softly.assertThat(claim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class).getAssetList().getAsset(ASSOCIATED_INSURABLE_RISK)).containsOption(String.format("%s (%s)", benefit, insuredPartyName))));

        assertSoftly(softly -> listBenefitsNotDisplayed.forEach(benefit ->
                softly.assertThat(claim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class).getAssetList().getAsset(ASSOCIATED_INSURABLE_RISK)).doesNotContainOption(String.format("%s (%s)", benefit, insuredPartyName))));
    }
}