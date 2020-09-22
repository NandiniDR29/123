package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsBenefitSummaryTab;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimChangeDateOfLossActionTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageDeterminationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsPFLParticipantInformationTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FNOL;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.modules.claim.common.metadata.ClaimChangeDateOfLossActionTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.CURRENT_AGE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.DATE_OF_BIRTH;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData.ASSOCIATE_POLICY_PARTY;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimCurrentAgeForPFLBenefit extends ClaimGroupBenefitsSTBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33798", component = CLAIMS_GROUPBENEFITS)
    public void testClaimCurrentAgeForPFLBenefit() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(getDefaultSTMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy").resolveLinks()));

        createDefaultStatutoryDisabilityInsuranceClaimWithoutBenefits();

        LOGGER.info("TEST: Steps 1-3");
        toSubTab(FNOL);
        policyInformationParticipantParticipantInformationTab.navigateToTab();

        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(DATE_OF_BIRTH).setValue(currentDate.minusYears(5).format(MM_DD_YYYY));

        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(CURRENT_AGE)).hasValue("5");

        LOGGER.info("TEST: Step 4");
        benefitsBenefitSummaryTab.navigateToTab();
        disabilityClaim.getDefaultWorkspace().fillFromTo(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit"), BenefitsBenefitSummaryTab.class, BenefitsPFLParticipantInformationTab.class);

        benefitsPflParticipantInformationTab.getAssetList().getAsset(ASSOCIATE_POLICY_PARTY).setValueByIndex(1);

        assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(BenefitsPFLParticipantInformationTabMetaData.CURRENT_AGE)).hasValue("5");

        LOGGER.info("TEST: Step 5");
        disabilityClaim.getDefaultWorkspace().fillFromTo(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit")
                .adjust(tdSpecific().getTestData("TestData_Benefit")), BenefitsPFLParticipantInformationTab.class, BenefitCoverageDeterminationTab.class);

        Tab.buttonSaveAndExit.click();

        disabilityClaim.claimOpen().perform();

        disabilityClaim.changeDateOfLossAction().perform(disabilityClaim.getLTDTestData().getTestData("ClaimChangeDateOfLoss", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(ClaimChangeDateOfLossActionTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), currentDate.plusYears(1).format(MM_DD_YYYY)));

        disabilityClaim.updateBenefit().start(1);
        assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(BenefitsPFLParticipantInformationTabMetaData.CURRENT_AGE)).hasValue("6");
    }
}