package com.exigen.ren.modules.policy.gb_ac.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PlansTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TestParticipantValidationRuleVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentCertificatePolicyContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-31958", "REN-32009"}, component = POLICY_GROUPBENEFITS)
    public void testParticipantValidationRuleVerification() {
        LOGGER.info("Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createPolicyViaUI(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.COVERAGE_TIERS.getLabel()), ImmutableList.of("Employee Only", "Employee + Family")).resolveLinks());

        groupAccidentCertificatePolicy.initiate(getDefaultACCertificatePolicyDataGatherData());
        groupAccidentCertificatePolicy.getDefaultWorkspace().fillUpTo(getDefaultACCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), CoveragesTabMetaData.COVERAGE_TIER.getLabel()), "Employee + Family"), CoveragesTab.class, true);

        LOGGER.info("REN-32009 ste#2 verification");
        coveragesTab.submitTab();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format("At least two Participants should be provided"))).isPresent();

        LOGGER.info("REN-31958 ste#1 verification");
        assertThat(coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PARTICIPANTS).getAsset(CoveragesTabMetaData.ParticipantsMetaData.PARTICIPANT_GENERAL_INFO)
                .getAsset(CoveragesTabMetaData.BeneficiaryGeneralInfoMetaData.GENDER)).hasOptions(ImmutableList.of(EMPTY, "Female", "Male", "Unspecified"));

        LOGGER.info("REN-32009 ste#3 verification");
        PlansTab.buttonAddParticipant.click();
        groupAccidentCertificatePolicy.getDefaultWorkspace().fill(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AddNewParticipant"));
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR
                .format("At least two Participants should be provided"))).isAbsent();
        GroupAccidentCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }
}