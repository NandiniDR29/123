package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationTab.ListOfParticipantPriorEarnings;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationTab.ParticipantPriorEarnings;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.OPEN;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.labelClaimStatus;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimCapture8WeeksPriorEarningsUIAndRulesForPFL extends ClaimGroupBenefitsSTBaseTest {
    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-18901", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCapture8WeeksPriorEarningsUIAndRulesForPFL() {
        TestData tdClaim = disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(PolicyInformationParticipantParticipantInformationTab.class.getSimpleName(), WORK_STATE.getLabel()), "NJ").resolveLinks();

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_NJ")
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));

        LOGGER.info("TEST: Step# 1");
        initiateClaimWithPolicyAndFillToTab(tdClaim, PolicyInformationParticipantParticipantInformationTab.class, false);

        LOGGER.info("TEST: Step# 2");
        assertSoftly(softly -> {
            ImmutableList.of(
                    ParticipantPriorEarningsMetaData.WEEK_NO,
                    ParticipantPriorEarningsMetaData.WEEK_ENDING_DATE,
                    ParticipantPriorEarningsMetaData.NUMBER_OF_DAYS_WORKED,
                    ParticipantPriorEarningsMetaData.GROSS_AMOUNT_PAID).forEach(control ->
                    softly.assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_PRIOR_EARNINGS).getAsset(control)).isPresent().isEnabled().hasValue(EMPTY)
            );
            softly.assertThat(PolicyInformationParticipantParticipantInformationTab.ParticipantPriorEarnings.addParticipantPriorEarning).isPresent();
        });
        policyInformationParticipantParticipantInformationTab.fillTab(tdClaim);

        LOGGER.info("TEST: Step# 3");
        ParticipantPriorEarnings participantPriorEarnings = policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_PRIOR_EARNINGS);

        participantPriorEarnings.fill(tdSpecific().getTestData("TestData_Earning_1"));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS)).hasValue(new Currency("1000").toString());
        ParticipantPriorEarnings.addParticipantPriorEarning.click();

        participantPriorEarnings.fill(tdSpecific().getTestData("TestData_Earning_2"));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS)).hasValue(new Currency("1100").toString());
        ParticipantPriorEarnings.addParticipantPriorEarning.click();

        participantPriorEarnings.fill(tdSpecific().getTestData("TestData_Earning_3"));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS)).hasValue(new Currency("1066.67").toString());
        ParticipantPriorEarnings.addParticipantPriorEarning.click();

        participantPriorEarnings.fill(tdSpecific().getTestData("TestData_Earning_4"));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS)).hasValue(new Currency("1100").toString());
        ParticipantPriorEarnings.addParticipantPriorEarning.click();

        participantPriorEarnings.fill(tdSpecific().getTestData("TestData_Earning_5"));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS)).hasValue(new Currency("1080").toString());
        ParticipantPriorEarnings.addParticipantPriorEarning.click();

        participantPriorEarnings.fill(tdSpecific().getTestData("TestData_Earning_6"));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS)).hasValue(new Currency("1100").toString());
        ParticipantPriorEarnings.addParticipantPriorEarning.click();

        participantPriorEarnings.fill(tdSpecific().getTestData("TestData_Earning_7"));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS)).hasValue(new Currency("1085.71").toString());
        ParticipantPriorEarnings.addParticipantPriorEarning.click();

        participantPriorEarnings.fill(tdSpecific().getTestData("TestData_Earning_8"));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS)).hasValue(new Currency("1100").toString());

        verifyParticipantPriorEarnings(8);
        assertThat(ParticipantPriorEarnings.addParticipantPriorEarning).isAbsent();

        LOGGER.info("TEST: Step# 6");
        IntStream.rangeClosed(1, 7).forEach(index -> {
            PolicyInformationParticipantParticipantInformationTab.listOfParticipantPriorEarnings.getRow(2).getCell(6).controls.links.get(ActionConstants.CHANGE).click();
            policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_PRIOR_EARNINGS).getAsset(ParticipantPriorEarningsMetaData.REMOVE_PARTICIPANT_PRIOR_EARNING).click();
            Page.dialogConfirmation.confirm();
        });
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_PRIOR_EARNINGS).getAsset(ParticipantPriorEarningsMetaData.REMOVE_PARTICIPANT_PRIOR_EARNING)).isAbsent();

        LOGGER.info("TEST: Step# 7");
        ParticipantPriorEarnings.addParticipantPriorEarning.click();
        participantPriorEarnings.fill(tdSpecific().getTestData("TestData_Earning_2"));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(COVERED_EARNINGS)).hasValue(new Currency("1100").toString());
        verifyParticipantPriorEarnings(2);

        LOGGER.info("TEST: Step# 8");
        policyInformationParticipantParticipantInformationTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(tdClaim, PolicyInformationParticipantParticipantCoverageTab.class);
        disabilityClaim.claimOpen().perform();
        assertThat(labelClaimStatus).hasValue(OPEN);
    }

    private void verifyParticipantPriorEarnings(int countRows) {
        IntStream.rangeClosed(1, countRows).forEach(index -> {
            TestData td = tdSpecific().getTestData("TestData_Earning_".concat(String.valueOf(index)));
            String weekNoValue = td.getValue(PARTICIPANT_PRIOR_EARNINGS.getLabel(), ParticipantPriorEarningsMetaData.WEEK_NO.getLabel());
            String weekEndingDate = td.getValue(PARTICIPANT_PRIOR_EARNINGS.getLabel(), ParticipantPriorEarningsMetaData.WEEK_ENDING_DATE.getLabel());
            String numberOfDaysWorked = td.getValue(PARTICIPANT_PRIOR_EARNINGS.getLabel(), ParticipantPriorEarningsMetaData.NUMBER_OF_DAYS_WORKED.getLabel());
            String grossAmountPaid = td.getValue(PARTICIPANT_PRIOR_EARNINGS.getLabel(), ParticipantPriorEarningsMetaData.GROSS_AMOUNT_PAID.getLabel());

            assertSoftly(softly -> {
                softly.assertThat(PolicyInformationParticipantParticipantInformationTab.listOfParticipantPriorEarnings.getRow(index).getCell(ListOfParticipantPriorEarnings.WEEK_NO.getName())).hasValue(weekNoValue);
                softly.assertThat(PolicyInformationParticipantParticipantInformationTab.listOfParticipantPriorEarnings.getRow(index).getCell(ListOfParticipantPriorEarnings.WEEK_ENDING_DATE.getName())).hasValue(weekEndingDate);
                softly.assertThat(PolicyInformationParticipantParticipantInformationTab.listOfParticipantPriorEarnings.getRow(index).getCell(ListOfParticipantPriorEarnings.NUMBER_OF_DAYS_WORKED.getName())).hasValue(numberOfDaysWorked);
                softly.assertThat(PolicyInformationParticipantParticipantInformationTab.listOfParticipantPriorEarnings.getRow(index).getCell(ListOfParticipantPriorEarnings.GROSS_AMOUNT_PAID.getName())).hasValue(new Currency(grossAmountPaid).toString());
            });
        });
    }
}