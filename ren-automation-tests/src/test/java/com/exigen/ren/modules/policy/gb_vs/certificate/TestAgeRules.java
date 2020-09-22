package com.exigen.ren.modules.policy.gb_vs.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants.CertificateParticipants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.GeneralInfoMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.ClassificationManagementTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.ISSUE;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.ActionConstants.REMOVE;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.PRIMARY_PARTICIPANT;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.SPOUSE_PARTICIPANT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_DATE;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_REASON;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.COVERAGE_TIER;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.PARTICIPANTS;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.ParticipantsMetaData.PARTICIPANT_GENERAL_INFO;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.ParticipantsMetaData.ROLE_NAME;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PlansTab.buttonAddParticipant;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.ClassificationManagementTabMetaData.PLAN_TIER_AND_RATING_INFO;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS_CHANGE_CONFIRMATION;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.labelPolicyStatus;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAgeRules extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionCertificatePolicyContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-33512"}, component = POLICY_GROUPBENEFITS)
    public void testAgeRulesForPrimaryParticipant() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(groupVisionMasterPolicy.getType());

        LOGGER.info("REN-33512 Step 1");
        TestData tdCertificatePolicy = groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), COVERAGE_TIER.getLabel()), "Employee Only").resolveLinks();
        groupVisionCertificatePolicy.initiate(tdCertificatePolicy);

        LOGGER.info("REN-33512 Step 2");
        certificatePolicyTab.fillTab(tdCertificatePolicy);
        String effectiveDate = certificatePolicyTab.getAssetList().getAsset(CertificatePolicyTabMetaData.EFFECTIVE_DATE).getValue();

        LOGGER.info("REN-33512 Step 3");
        Tab.buttonNext.click();
        insuredTab.fillTab(tdCertificatePolicy);
        String dateOfBirth = "08/03/1983";
        insuredTab.getAssetList().getAsset(InsuredTabMetaData.GENERAL_INFORMATION).getAsset(InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH).setValue(dateOfBirth);

        LOGGER.info("REN-33512 Step 4, 5");
        Tab.buttonNext.click();
        coveragesTab.fillTab(tdCertificatePolicy);
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_NAME)).hasValue(PRIMARY_PARTICIPANT);

        LOGGER.info("REN-33512 Step 7");
        Tab.buttonNext.click();
        GroupVisionCertificatePolicyContext.premiumSummaryTab.fillTab(tdCertificatePolicy).submitTab();
        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("REN-33512 Step 8");
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String getAgeQuery = "select dateOfBirth,age,firstName from GeneralPartyInfoEntity\n" +
                "where id in ( select personInfo_id from Participant where id in" +
                "( select participant_id from ParticipantGroup_Participant where " +
                "participantGroup_id in (select id from RiskItem where POLICYDETAIL_ID in " +
                "(select policyDetail_id from PolicySummary ps where policyNumber=?)) ))";
        String age = DBService.get().getRow(getAgeQuery, quoteNumber).get("age");
        DateTimeFormatter formatter = DateTimeUtils.MM_DD_YYYY;
        int calculatedAge = Period.between(LocalDate.parse(dateOfBirth, formatter), LocalDate.parse(effectiveDate, formatter)).getYears();
        assertThat(age).isEqualTo(String.valueOf(calculatedAge));

        LOGGER.info("REN-33512 Step 10");
        groupVisionCertificatePolicy.dataGather().start();
        String dateOfBirth2 = "08/03/1993";
        insuredTab.navigateToTab().getAssetList().getAsset(InsuredTabMetaData.GENERAL_INFORMATION).getAsset(InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH).setValue(dateOfBirth2);
        Tab.buttonNext.click();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_NAME)).hasValue(PRIMARY_PARTICIPANT);
        Tab.buttonNext.click();
        GroupVisionCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("REN-33512 Step 11");
        String age2 = DBService.get().getRow(getAgeQuery, quoteNumber).get("age");
        int calculatedAge2 = Period.between(LocalDate.parse(dateOfBirth2, formatter), LocalDate.parse(effectiveDate, formatter)).getYears();
        assertThat(age2).isEqualTo(String.valueOf(calculatedAge2));

        LOGGER.info("REN-33512 Step 12");
        groupVisionCertificatePolicy.issue().perform(
                tdCertificatePolicy.adjust(groupVisionCertificatePolicy.getDefaultTestData(ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());
        assertThat(labelPolicyStatus).hasValue(POLICY_ACTIVE);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-33689"}, component = POLICY_GROUPBENEFITS)
    public void testAgeRulesForNonPrimaryParticipant() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        TestData tdPolicy = getDefaultVSMasterPolicyData()
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS.getLabel()))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
                .mask(TestData.makeKeyPath(GroupVisionMasterPolicyContext.classificationManagementMpTab.getMetaKey() + "[0]", PLAN_TIER_AND_RATING_INFO.getLabel(), PlanTierAndRatingInfoMetaData.COVERAGE_TIER.getLabel()))
                .resolveLinks();
        groupVisionMasterPolicy.createPolicy(tdPolicy);

        LOGGER.info("REN-33689 Step 1");
        TestData tdCertificatePolicy = groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), COVERAGE_TIER.getLabel()), "Employee + Spouse").resolveLinks();
        groupVisionCertificatePolicy.initiate(tdCertificatePolicy);

        LOGGER.info("REN-33689 Step 2");
        certificatePolicyTab.fillTab(tdCertificatePolicy);
        String effectiveDate = certificatePolicyTab.getAssetList().getAsset(CertificatePolicyTabMetaData.EFFECTIVE_DATE).getValue();

        LOGGER.info("REN-33689 Step 3");
        Tab.buttonNext.click();
        insuredTab.fillTab(tdCertificatePolicy);
        insuredTab.getAssetList().getAsset(InsuredTabMetaData.GENERAL_INFORMATION).getAsset(InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH).setValue("08/03/1983");

        LOGGER.info("REN-33689 Step 4, 5");
        Tab.buttonNext.click();
        coveragesTab.fillTab(tdCertificatePolicy);
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_NAME)).hasValue(PRIMARY_PARTICIPANT);
        buttonAddParticipant.click();
        String dateOfBirth = "08/03/1985";
        coveragesTab.fillTab(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AddNewParticipant")
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PARTICIPANTS.getLabel(), PARTICIPANT_GENERAL_INFO.getLabel(), GeneralInfoMetaData.DATE_OF_BIRTH.getLabel()), dateOfBirth));
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_NAME)).hasValue(SPOUSE_PARTICIPANT);

        LOGGER.info("REN-33689 Step 7");
        Tab.buttonNext.click();
        GroupVisionCertificatePolicyContext.premiumSummaryTab.fillTab(tdCertificatePolicy);
        GroupVisionCertificatePolicyContext.premiumSummaryTab.fillTab(tdCertificatePolicy).submitTab();
        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("REN-33689 Step 8");
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String getAgeQuery = "select dateOfBirth,age,firstName from GeneralPartyInfoEntity\n" +
                "where dateOfBirth = ? and id in ( select personInfo_id from Participant where id in" +
                "( select participant_id from ParticipantGroup_Participant where " +
                "participantGroup_id in (select id from RiskItem where POLICYDETAIL_ID in " +
                "(select policyDetail_id from PolicySummary ps where policyNumber=?)) ))";
        String age = DBService.get().getRow(getAgeQuery, "1985-08-03 00:00:00.0", quoteNumber).get("age");
        DateTimeFormatter formatter = DateTimeUtils.MM_DD_YYYY;
        int calculatedAge = Period.between(LocalDate.parse(dateOfBirth, formatter), LocalDate.parse(effectiveDate, formatter)).getYears();
        assertThat(age).isEqualTo(String.valueOf(calculatedAge));

        LOGGER.info("REN-33689 Step 10");
        groupVisionCertificatePolicy.dataGather().start();
        String dateOfBirth2 = "08/03/1995";
        coveragesTab.navigateToTab();
        coveragesTab.tableParticipantsList.getRow(CertificateParticipants.ROLE_NAME.getName(), SPOUSE_PARTICIPANT).getCell(6).controls.links.get(CHANGE).click();
        coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(PARTICIPANT_GENERAL_INFO).getAsset(GeneralInfoMetaData.DATE_OF_BIRTH).setValue(dateOfBirth2);
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_NAME)).hasValue(SPOUSE_PARTICIPANT);
        Tab.buttonNext.click();
        GroupVisionCertificatePolicyContext.premiumSummaryTab.fillTab(tdCertificatePolicy).submitTab();
        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("REN-33689 Step 11");
        String age2 = DBService.get().getRow(getAgeQuery, "1995-08-03 00:00:00.0", quoteNumber).get("age");
        int calculatedAge2 = Period.between(LocalDate.parse(dateOfBirth2, formatter), LocalDate.parse(effectiveDate, formatter)).getYears();
        assertThat(age2).isEqualTo(String.valueOf(calculatedAge2));

        LOGGER.info("REN-33689 Step 12");
        groupVisionCertificatePolicy.issue().perform(
                tdCertificatePolicy.adjust(groupVisionCertificatePolicy.getDefaultTestData(ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());
        assertThat(labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("REN-33689 Step 18, 19");
        groupVisionCertificatePolicy.endorse().start();
        String plusOneMonth = TimeSetterUtil.getInstance().getCurrentTime().plusMonths(1).format(DateTimeUtils.MM_DD_YYYY);
        groupVisionCertificatePolicy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList().getAsset(ENDORSEMENT_DATE).setValue(plusOneMonth);
        groupVisionCertificatePolicy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList().getAsset(ENDORSEMENT_REASON).setValue("Marriage");
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        coveragesTab.navigateToTab();
        coveragesTab.tableParticipantsList.getRow(CertificateParticipants.ROLE_NAME.getName(), SPOUSE_PARTICIPANT).getCell(6).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        buttonAddParticipant.click();
        String dateOfBirth3 = "08/03/1990";
        coveragesTab.fillTab(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AddNewParticipant")
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PARTICIPANTS.getLabel(), PARTICIPANT_GENERAL_INFO.getLabel(), GeneralInfoMetaData.DATE_OF_BIRTH.getLabel()), dateOfBirth3));
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_NAME)).hasValue(SPOUSE_PARTICIPANT);

        LOGGER.info("REN-33689 Step20");
        Tab.buttonNext.click();
        Tab.buttonRate.click();
        assertThat(QuoteSummaryPage.labelQuoteStatusCp).valueContains(PREMIUM_CALCULATED);
        Tab.buttonNext.click();

        LOGGER.info("REN-33689 Step21");
        String age3 = DBService.get().getRow(getAgeQuery, "1990-08-03 00:00:00.0", quoteNumber).get("age");
        int calculatedAge3 = Period.between(LocalDate.parse(dateOfBirth3, formatter), LocalDate.parse(effectiveDate, formatter)).getYears();
        assertThat(age3).isEqualTo(String.valueOf(calculatedAge3));
    }
}