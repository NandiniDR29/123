package com.exigen.ren.modules.policy.gb_ac.certificate;

import com.exigen.ipb.eisa.controls.ParticipantMultiAssetList;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CoveragesConstants.CoverageTiers.EMPLOYEE_PLUS_CHILDREN;
import static com.exigen.ren.main.enums.CoveragesConstants.CoverageTiers.EMPLOYEE_PLUS_SPOUSE;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.CHILD_PARTICIPANT;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.SPOUSE_PARTICIPANT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.ENHANCED_10_UNITS;
import static com.exigen.ren.main.enums.PolicyConstants.RelationshipToInsured.DEPENDENT_CHILD;
import static com.exigen.ren.main.enums.PolicyConstants.RelationshipToInsured.SPOUSE_DOMESTIC_PARTNER;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.COVERAGE_TIER;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.PARTICIPANTS;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.ATTAINED_AGE;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.DATE_OF_BIRTH;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.FIRST_NAME;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.LAST_NAME;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.RELATIONSHIP_TO_INSURED;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.ParticipantsMetaData.PARTICIPANT_GENERAL_INFO;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.ParticipantsMetaData.PARTICIPANT_SELECTION;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CoveragesTabMetaData.ParticipantsMetaData.ROLE_NAME;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.GENERAL_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.AGE_TERMINATION;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.AgeTerminationSection.TERMINATION_AGE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.GB;
import static com.exigen.ren.utils.groups.Groups.GB_AC;
import static com.exigen.ren.utils.groups.Groups.GB_PRECONFIGURED;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestImprovingAgeCalculation extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentCertificatePolicyContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-34191"}, component = POLICY_GROUPBENEFITS)
    public void testImprovingAgeCalculation() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", AGE_TERMINATION.getLabel()), new SimpleDataProvider().adjust(TERMINATION_AGE.getLabel(), "70"))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.COVERAGE_TIERS.getLabel()))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
                .resolveLinks());

        LOGGER.info("TC1 Step 1");
        LocalDateTime todayDate = TimeSetterUtil.getInstance().getCurrentTime();
        TestData tdCertificateQuote = getDefaultACCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), todayDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), GENERAL_INFORMATION.getLabel(), InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH.getLabel()), todayDate.minusYears(26).format(DateTimeUtils.MM_DD_YYYY))
                .resolveLinks();
        groupAccidentCertificatePolicy.initiate(tdCertificateQuote);
        groupAccidentCertificatePolicy.getDefaultWorkspace().fillUpTo(tdCertificateQuote, CoveragesTab.class);

        LOGGER.info("TC1 Step 2");
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PLAN).setValue(ENHANCED_10_UNITS);
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.COVERAGE_TIER).setValue(EMPLOYEE_PLUS_SPOUSE);
        ParticipantMultiAssetList participantsAsset = coveragesTab.getAssetList().getAsset(PARTICIPANTS);
        coveragesTab.buttonAddParticipant.click();
        participantsAsset.getAsset(PARTICIPANT_SELECTION).setValue("New Person");
        participantsAsset.getAsset(ROLE_NAME).setValue(SPOUSE_PARTICIPANT);
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(RELATIONSHIP_TO_INSURED)).hasValue(SPOUSE_DOMESTIC_PARTNER);

        LOGGER.info("TC1 Step 3");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(70).format(DateTimeUtils.MM_DD_YYYY));
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(FIRST_NAME).setValue("First Name1");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(LAST_NAME).setValue("Last Name1zxvxc");
        coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.ENROLLMENT_DATE).setValue(todayDate.format(DateTimeUtils.MM_DD_YYYY));
        Tab.buttonNext.click();
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasWarningWithText("Spouse age exceeds termination age, unable to add.");

        LOGGER.info("TC1 Step 4");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(70).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        Tab.buttonNext.click();
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasWarningWithText("Spouse age exceeds termination age, unable to add.");

        LOGGER.info("TC1 Step 6");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(70).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        Tab.buttonNext.click();
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasNoWarning();

        LOGGER.info("TC1 Step 7");
        coveragesTab.navigateToTab().getAssetList().getAsset(COVERAGE_TIER).setValue(EMPLOYEE_PLUS_CHILDREN);
        coveragesTab.buttonAddParticipant.click();
        participantsAsset.getAsset(PARTICIPANT_SELECTION).setValueContains("John");
        coveragesTab.buttonAddParticipant.click();
        participantsAsset.getAsset(PARTICIPANT_SELECTION).setValue("New Person");
        participantsAsset.getAsset(ROLE_NAME).setValue(CHILD_PARTICIPANT);
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(RELATIONSHIP_TO_INSURED)).hasValue(DEPENDENT_CHILD);

        LOGGER.info("TC1 Step 8");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(26).format(DateTimeUtils.MM_DD_YYYY));
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(FIRST_NAME).setValue("First Name1");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(LAST_NAME).setValue("Last Name1hjf");
        Tab.buttonNext.click();
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasWarningWithText("Overage dependent, unable to add");

        LOGGER.info("TC1 Step 9");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(26).minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        Tab.buttonNext.click();
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(ATTAINED_AGE)).hasWarningWithText("Overage dependent, unable to add");

        LOGGER.info("TC1 Step 12");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(26).plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        Tab.buttonNext.click();
        GroupAccidentCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("TC3 Step 1");
        //ToDo rznamerovskyi: finish test when REN-394 will be ready
    }
}
