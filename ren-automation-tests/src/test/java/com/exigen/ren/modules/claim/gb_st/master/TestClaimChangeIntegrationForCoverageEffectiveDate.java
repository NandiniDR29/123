package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.metadata.SearchMetaData;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationPolicyTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.LossContextTab;
import com.exigen.ren.main.modules.claim.common.tabs.LossEventTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantCoverageTab;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationPolicyTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ClaimConstants.Plan.STATUTORY_NEW_YORK;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.claim.common.metadata.LossContextTabMetaData.POLICY_LOB;
import static com.exigen.ren.main.modules.claim.common.metadata.LossContextTabMetaData.TYPE_OF_CLAIM;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.COVERAGE_STATUS;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PolicyInformationTabMetaData.RENEWAL_FREQUENCY;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimChangeIntegrationForCoverageEffectiveDate extends ClaimGroupBenefitsSTBaseTest {

    TestData tdClaimDefault = disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-30277", component = CLAIMS_GROUPBENEFITS)
    public void testClaimChangeIntegrationForCoverageEffectiveDateActive() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        String policyEffectiveDate = currentDate.minusDays(10).format(DateTimeUtils.MM_DD_YYYY);
        LossContextTab lossContextTab = disabilityClaim.getInitializationView().getTab(LossContextTab.class);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(getDefaultSTMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), policyEffectiveDate)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey(), COVERAGE_EFFECTIVE_DATE.getLabel()), currentDate.format(DateTimeUtils.MM_DD_YYYY)).resolveLinks());

        LOGGER.info("Test: Steps 1-2");
        disabilityClaim.initiateCreation();
        disabilityClaim.getInitializationView().fillUpTo(tdClaimDefault
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), LossEventTabMetaData.DATE_OF_LOSS.getLabel()), currentDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY)), LossContextTab.class, true);
        lossContextTab.submitTab();

        assertSoftly(softly -> {
            softly.assertThat(lossContextTab.getAssetList().getAsset(POLICY_LOB)).isRequired().hasValue(EMPTY).hasWarningWithText("'Policy LOB' is required");
            softly.assertThat(lossContextTab.getAssetList().getAsset(TYPE_OF_CLAIM)).isRequired().hasValue(EMPTY).hasWarningWithText("'Type of Claim' is required");
        });

        LOGGER.info("Test: Step 3");
        claim.navigateToClaim();
        Page.dialogConfirmation.confirm();

        disabilityClaim.initiateCreation();
        disabilityClaim.getInitializationView().fillUpTo(tdClaimDefault
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), LossEventTabMetaData.DATE_OF_LOSS.getLabel()), currentDate.format(DateTimeUtils.MM_DD_YYYY)), LossContextTab.class, false);

        assertSoftly(softly -> {
            softly.assertThat(lossContextTab.getAssetList().getAsset(POLICY_LOB)).hasNoWarning();
            softly.assertThat(lossContextTab.getAssetList().getAsset(TYPE_OF_CLAIM)).hasNoWarning();
        });

        LOGGER.info("Test: Step 5");
        lossContextTab.fillTab(tdClaimDefault).submitTab();

        LOGGER.info("Test: Step 6");
        disabilityClaim.getDefaultWorkspace().fillUpTo(tdClaimDefault, PolicyInformationPolicyTab.class);

        assertThat(policyInformationPolicyTab.getAssetList().getAsset(PolicyInformationPolicyTabMetaData.EFFECTIVE_DATE)).hasValue(policyEffectiveDate);

        LOGGER.info("Test: Step 7");
        disabilityClaim.getDefaultWorkspace().fillFromTo(tdClaimDefault, PolicyInformationPolicyTab.class, PolicyInformationParticipantParticipantCoverageTab.class);

        policyInformationParticipantParticipantCoverageTab.addCoverage(STATUTORY_NEW_YORK, "Stat NY - Statutory New York");

        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(COVERAGE_EFFECTIVE_DATE)).hasValue(currentDate.format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(policyInformationParticipantParticipantCoverageTab.getAssetList().getAsset(COVERAGE_STATUS)).hasValue("In force as of Date of Loss");
        });
    }


    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-30296", component = CLAIMS_GROUPBENEFITS)
    public void testClaimChangeIntegrationForCoverageEffectiveDatePending() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();

        LocalDateTime currentDatePlusTenDays = currentDate.plusDays(10);
        LossContextTab lossContextTab = disabilityClaim.getInitializationView().getTab(LossContextTab.class);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        statutoryDisabilityInsuranceMasterPolicy.createPolicy(getDefaultSTMasterPolicyData()
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), RENEWAL_FREQUENCY.getLabel()))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDatePlusTenDays.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE.getLabel()), currentDate.plusDays(10).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey(), PlanDefinitionTabMetaData.COVERAGE_EFFECTIVE_DATE.getLabel()), currentDate.plusDays(15).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey(), PlanDefinitionTabMetaData.MEMBER_PAYMENT_MODE.getLabel()), "1"));

        LOGGER.info("Test: Steps 1-2");
        disabilityClaim.initiateCreation();
        disabilityClaim.getInitializationView().fillUpTo(tdClaimDefault
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), LossEventTabMetaData.DATE_OF_LOSS.getLabel()), currentDate.minusDays(1).format(DateTimeUtils.MM_DD_YYYY)), LossContextTab.class, true);
        lossContextTab.submitTab();

        assertSoftly(softly -> {
            softly.assertThat(lossContextTab.getAssetList().getAsset(POLICY_LOB)).isRequired().hasValue(EMPTY).hasWarningWithText("'Policy LOB' is required");
            softly.assertThat(lossContextTab.getAssetList().getAsset(TYPE_OF_CLAIM)).isRequired().hasValue(EMPTY).hasWarningWithText("'Type of Claim' is required");
        });

        LOGGER.info("Test: Step 3");
        claim.navigateToClaim();
        Page.dialogConfirmation.confirm();

        disabilityClaim.initiateCreation();
        disabilityClaim.getInitializationView().fillUpTo(tdClaimDefault
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), LossEventTabMetaData.DATE_OF_LOSS.getLabel()), currentDate.plusDays(12).format(DateTimeUtils.MM_DD_YYYY)), LossContextTab.class, false);

        lossContextTab.fillTab(tdClaimDefault).submitTab();

        assertSoftly(softly -> {
            softly.assertThat(lossContextTab.getAssetList().getAsset(POLICY_LOB)).isRequired().hasValue(EMPTY).hasWarningWithText("'Policy LOB' is required");
            softly.assertThat(lossContextTab.getAssetList().getAsset(TYPE_OF_CLAIM)).isRequired().hasValue(EMPTY).hasWarningWithText("'Type of Claim' is required");
        });
    }
}
