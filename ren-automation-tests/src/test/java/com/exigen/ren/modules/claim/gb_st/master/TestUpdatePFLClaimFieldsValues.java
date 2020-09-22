package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.claim.common.tabs.CompleteNotificationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsPFLParticipantInformationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsPFLQualifyingEventTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_NAME;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLQualifyingEventTabMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUpdatePFLClaimFieldsValues extends ClaimGroupBenefitsSTBaseTest {
    private static final String NEWBORN_BONDING = "Newborn Bonding";
    private static final String ADOPTION_BONDING = "Adoption Bonding";
    private static final String FOSTER_CHILD_BONDING = "Foster Child Bonding";
    private static final String FAMILY_LEAVE = "Family Leave";
    private static final String MILITARY = "Military";
    private static final String CHILD = "Child";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40493", component = CLAIMS_GROUPBENEFITS)
    public void testUpdatePFLClaimFieldsValues() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.createPolicy(getDefaultSTMasterPolicyData().adjust(tdSpecific().getTestData("TestData").resolveLinks()));

        LOGGER.info("TEST REN-40493: Step 1");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getSTTestData().getTestData(DATA_GATHER, "TestData_With_PFL_Benefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantCoverageTab.getMetaKey(), COVERAGE_NAME.getLabel()), "contains=PFL"), BenefitsPFLQualifyingEventTab.class, true);
        assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON))
                .hasOptions(ImmutableList.of(NEWBORN_BONDING, ADOPTION_BONDING, FOSTER_CHILD_BONDING, FAMILY_LEAVE, MILITARY));

        checkPFLClaimFields(false);

        LOGGER.info("TEST REN-40493: Step 7");
        benefitsPflQualifyingEventTab.fillTab(disabilityClaim.getSTTestData().getTestData(DATA_GATHER, "TestData_With_PFL_Benefit")
                .mask(makeKeyPath(benefitsPflQualifyingEventTab.getMetaKey(), THE_FAMILY_MEMBER_IS_EMPLOYEES.getLabel()))
                .adjust(makeKeyPath(benefitsPflQualifyingEventTab.getMetaKey(), FMLA_CONCURRENTLY_WITH_PFL.getLabel()), VALUE_YES));
        completeNotificationTab.navigate();
        CompleteNotificationTab.buttonOpenClaim.click();

        claim.updateBenefit().start(1);
        BenefitsPFLParticipantInformationTab.buttonNext.click();
        checkPFLClaimFields(true);
    }

    private void checkPFLClaimFields(boolean isEnabled) {
        LOGGER.info("TEST REN-40493: Step 2");
        benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON).setValue(NEWBORN_BONDING);
        assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(THE_FAMILY_MEMBER_IS_EMPLOYEES)).hasValue(CHILD).isEnabled(isEnabled);

        LOGGER.info("TEST REN-40493: Step 3");
        benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON).setValue(ADOPTION_BONDING);
        assertSoftly(softly -> {
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(THE_FAMILY_MEMBER_IS_EMPLOYEES)).hasValue(CHILD).isEnabled(isEnabled);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(FOSTER_CARE_OR_ADOPTION_PLACEMENT)).isAbsent();
        });

        LOGGER.info("TEST REN-40493: Step 4");
        benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON).setValue(FOSTER_CHILD_BONDING);
        assertSoftly(softly -> {
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(THE_FAMILY_MEMBER_IS_EMPLOYEES)).hasValue(CHILD).isEnabled(isEnabled);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(FOSTER_CARE_OR_ADOPTION_PLACEMENT)).isAbsent();
        });

        LOGGER.info("TEST REN-40493: Step 5");
        benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON).setValue(MILITARY);
        assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(THE_FAMILY_MEMBER_IS_EMPLOYEES))
                .hasOptions(ImmutableList.of("Spouse", "Domestic Partner", CHILD, "Parent"));

        LOGGER.info("TEST REN-40493: Step 6");
        benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON).setValue(FAMILY_LEAVE);
        assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(THE_FAMILY_MEMBER_IS_EMPLOYEES))
                .hasOptions(ImmutableList.of(CHILD, "Spouse", "Domestic Partner", "Parent", "Parent-In-Law", "Grandparent", "Grandchild"));
    }
}
