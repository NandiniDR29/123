package com.exigen.ren.modules.policy.gb_vs.certificate;

import com.exigen.ipb.eisa.controls.ParticipantMultiAssetList;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PlansTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.TestDataKey.ISSUE;
import static com.exigen.ren.main.enums.CoveragesConstants.CoverageTiers.EMPLOYEE_PLUS_CHILD_REN;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.CHILD_PARTICIPANT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ALACARTE;
import static com.exigen.ren.main.enums.PolicyConstants.RelationshipToInsured.DEPENDENT_CHILD;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_DATE;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_REASON;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.GeneralInfoMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.PARTICIPANTS;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.ParticipantsMetaData.PARTICIPANT_GENERAL_INFO;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.ParticipantsMetaData.PARTICIPANT_SELECTION;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS_CHANGE_CONFIRMATION;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestImprovingAgeCalculation extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionCertificatePolicyContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-34002"}, component = POLICY_GROUPBENEFITS)
    public void testImprovingAgeCalculation() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.ELIGIBILITY.getLabel(), PlanDefinitionTabMetaData.EligibilityMetadata.DEPENDENT_MAXIMUM_AGE.getLabel()), "25")
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.COVERAGE_TIERS.getLabel()))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
                .resolveLinks());

        LOGGER.info("TC1 Step 1, 2");
        LocalDateTime todayDate = TimeSetterUtil.getInstance().getCurrentTime();
        TestData tdCertificateQuote = getDefaultVSCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), todayDate.format(DateTimeUtils.MM_DD_YYYY))
                .resolveLinks();
        groupVisionCertificatePolicy.initiate(tdCertificateQuote);
        groupVisionCertificatePolicy.getDefaultWorkspace().fillUpTo(tdCertificateQuote, PlansTab.class);
        coveragesTab.getAssetList().getAsset(PlansTabMetaData.PLAN_NAME).setValue(ALACARTE);
        assertThat(coveragesTab.getAssetList().getAsset(PlansTabMetaData.DEPENDENT_MAXIMUM_AGE)).hasValue("25");

        LOGGER.info("TC1 Step 3");
        coveragesTab.getAssetList().getAsset(PlansTabMetaData.ENROLLMENT_DATE).setValue(todayDate.format(DateTimeUtils.MM_DD_YYYY));
        coveragesTab.getAssetList().getAsset(PlansTabMetaData.COVERAGE_TIER).setValue(EMPLOYEE_PLUS_CHILD_REN);

        coveragesTab.buttonAddParticipant.click();
        ParticipantMultiAssetList participantsAsset = coveragesTab.getAssetList().getAsset(PARTICIPANTS);
        participantsAsset.getAsset(PARTICIPANT_SELECTION).setValueContains("John");

        coveragesTab.buttonAddParticipant.click();
        participantsAsset.getAsset(PARTICIPANT_SELECTION).setValue("New Person");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(RELATIONSHIP_TO_INSURED).setValue(DEPENDENT_CHILD);
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(FIRST_NAME).setValue("First Name1");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(LAST_NAME).setValue("Last Name1akdf");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(25).format(DateTimeUtils.MM_DD_YYYY));

        Tab.buttonNext.click();
        GroupVisionCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("TC1 Step 4");
        tdCertificateQuote.adjust(groupVisionCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)).resolveLinks();
        groupVisionCertificatePolicy.issue().perform(tdCertificateQuote);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("TC2 Step 1");
        groupVisionCertificatePolicy.endorse().start();
        groupVisionCertificatePolicy.endorse().getWorkspace()
                .getTab(StartEndorsementActionTab.class).getAssetList().getAsset(ENDORSEMENT_DATE).setValue(todayDate.plusMonths(1).format(DateTimeUtils.MM_DD_YYYY));
        groupVisionCertificatePolicy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList().getAsset(ENDORSEMENT_REASON).setValue("Marriage");
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        coveragesTab.navigateToTab();
        coveragesTab.tableParticipantsList.getRow(TableConstants.CertificateParticipants.ROLE_NAME.getName(), CHILD_PARTICIPANT).getCell(6).controls.links.getFirst().click();
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(RELATIONSHIP_TO_INSURED)).hasValue(DEPENDENT_CHILD);
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasValue(todayDate.minusYears(25).format(DateTimeUtils.MM_DD_YYYY));

        LOGGER.info("TC2 Step 4");
        participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(todayDate.minusYears(20).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(participantsAsset.getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasNoWarning();

        LOGGER.info("TC2 Step 5");
        GroupVisionCertificatePolicyContext.premiumSummaryTab.navigateToTab().submitTab();

        LOGGER.info("TC2 Step 10");
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupVisionCertificatePolicy.issue().perform(groupVisionCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("TC4 Step 1");
        //ToDo rznamerovskyi: finish test when REN-394 will be ready
    }
}
