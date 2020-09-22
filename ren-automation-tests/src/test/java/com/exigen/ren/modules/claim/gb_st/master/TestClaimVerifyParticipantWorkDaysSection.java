package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsBenefitSummaryTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitStatutorySTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsPFLParticipantInformationTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.buttonCancel;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.BENEFITS;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.ADJUDICATION;
import static com.exigen.ren.common.pages.NavigationPage.toLeftMenuTab;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.*;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsStatutorySTDInjuryPartyInformationTabMetaData.PARTICIPANT_WORK_DAYS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsStatutorySTDInjuryPartyInformationTabMetaData.ParticipantWorkDaysMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimVerifyParticipantWorkDaysSection extends ClaimGroupBenefitsSTBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-15580", "REN-19974"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimVerifyParticipantWorkDaysSectionForStateNY() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        createDefaultStatutoryDisabilityInsuranceMasterPolicy();

        disabilityClaim.initiate(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Test: step #1-5");
        addBenefitOnFNOL(SHORT_TERM_DISABILITY);
        assertThat(benefitsStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS)).isAbsent();

        addBenefitOnFNOL(LONG_TERM_DISABILITY);
        assertThat(benefitsStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS)).isAbsent();

        addBenefitOnFNOL(STATUTORY_SHORT_TERM_DISABILITY);
        verifyParticipantWorkDaysSection(benefitsStatutorySTDInjuryPartyInformationTab);

        addBenefitOnFNOL(PAID_FAMILY_LEAVE);
        verifyParticipantWorkDaysSection(benefitsPflParticipantInformationTab);

        LOGGER.info("Test: step #11");
        toSubTab(ADJUDICATION);
        dialogConfirmation.confirm();

        BenefitStatutorySTDInjuryPartyInformationTab benefitStatutorySTDInjuryPartyInformationTab =
                disabilityClaim.addBenefit().getWorkspace().getTab(BenefitStatutorySTDInjuryPartyInformationTab.class);
        BenefitsPFLParticipantInformationTab benefitsFLParticipantInformationTab =
                disabilityClaim.addBenefit().getWorkspace().getTab(BenefitsPFLParticipantInformationTab.class);

        disabilityClaim.addBenefit().start(SHORT_TERM_DISABILITY);
        assertThat(benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS)).isAbsent();
        buttonCancel.click();
        dialogConfirmation.confirm();

        disabilityClaim.addBenefit().start(LONG_TERM_DISABILITY);
        assertThat(benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS)).isAbsent();
        buttonCancel.click();
        dialogConfirmation.confirm();

        disabilityClaim.addBenefit().start(STATUTORY_SHORT_TERM_DISABILITY);
        verifyParticipantWorkDaysSection(benefitStatutorySTDInjuryPartyInformationTab);
        buttonCancel.click();
        dialogConfirmation.confirm();

        disabilityClaim.addBenefit().start(PAID_FAMILY_LEAVE);
        verifyParticipantWorkDaysSection(benefitsFLParticipantInformationTab);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-15580", "REN-19974"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimVerifyParticipantWorkDaysSectionForStateNotNY() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.createPolicy(getDefaultSTMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA")
                .mask(makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())));

        disabilityClaim.initiate(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));

        LOGGER.info("Test: step #14");
        addBenefitOnFNOL(STATUTORY_SHORT_TERM_DISABILITY);
        assertThat(benefitsStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS)).isAbsent();

        addBenefitOnFNOL(PAID_FAMILY_LEAVE);
        assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS)).isAbsent();
    }

    private void addBenefitOnFNOL(String benefitType) {
        toLeftMenuTab(BENEFITS);
        BenefitsBenefitSummaryTab.comboboxDamage.setValue(benefitType);
        BenefitsBenefitSummaryTab.linkAddComponents.click();
    }

    private void verifyParticipantWorkDaysSection(Tab tab) {
        assertSoftly(softly -> {
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS).getAsset(SUNDAY)).isOptional().hasValue(false);
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS).getAsset(MONDAY)).isOptional().hasValue(true);
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS).getAsset(TUESDAY)).isOptional().hasValue(true);
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS).getAsset(WEDNESDAY)).isOptional().hasValue(true);
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS).getAsset(THURSDAY)).isOptional().hasValue(true);
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS).getAsset(FRIDAY)).isOptional().hasValue(true);
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS).getAsset(SATURDAY)).isOptional().hasValue(false);
        });
    }
}
