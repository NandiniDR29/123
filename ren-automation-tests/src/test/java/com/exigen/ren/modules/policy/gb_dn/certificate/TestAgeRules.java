package com.exigen.ren.modules.policy.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants.CertificateParticipants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.PlansTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.ISSUE;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.ActionConstants.REMOVE;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupDentalCoverages.EMPLOYEE_FAMILY;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupDentalCoverages.EMPLOYEE_ONLY;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.PRIMARY_PARTICIPANT;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.SPOUSE_PARTICIPANT;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_DATE;
import static com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementActionTabMetaData.ENDORSEMENT_REASON;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData.GENERAL_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData.COVERAGE_TIER;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData.PARTICIPANTS;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData.ParticipantsMetaData.PARTICIPANT_SELECTION;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData.ParticipantsMetaData.ROLE_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.ClassificationManagementTabMetaData.PLAN_TIER_AND_RATING_INFO;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS_CHANGE_CONFIRMATION;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.labelPolicyStatus;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAgeRules extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalCertificatePolicyContext, GroupDentalMasterPolicyContext {

    private static final TextBox dateOfBirthOnCoveragesTabAsset = coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(PlansTabMetaData.ParticipantsMetaData.GENERAL_INFORMATION).getAsset(PlansTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH);
    private static final String DATE_OF_BIRTH_IS_AFTER_EFFECTIVE_DATE = "Date of Birth should be on or before Transaction Effective Date";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-33512"}, component = POLICY_GROUPBENEFITS)
    public void testAgeRulesForPrimaryParticipant() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(groupDentalMasterPolicy.getType());

        LOGGER.info("REN-33512 Step 1");
        TestData tdCertificatePolicy = groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), COVERAGE_TIER.getLabel()), "Employee Only").resolveLinks();
        groupDentalCertificatePolicy.initiate(tdCertificatePolicy);

        LOGGER.info("REN-33512 Step 2");
        certificatePolicyTab.fillTab(tdCertificatePolicy);
        String effectiveDate = certificatePolicyTab.getAssetList().getAsset(CertificatePolicyTabMetaData.EFFECTIVE_DATE).getValue();

        LOGGER.info("REN-33512 Step 3");
        Tab.buttonNext.click();
        insuredTab.fillTab(tdCertificatePolicy);
        String dateOfBirth = "08/03/1983";
        insuredTab.getAssetList().getAsset(GENERAL_INFORMATION).getAsset(DATE_OF_BIRTH).setValue(dateOfBirth);

        LOGGER.info("REN-33512 Step 4, 5");
        Tab.buttonNext.click();
        coveragesTab.fillTab(tdCertificatePolicy);
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_INFORMATION).getAsset(PlansTabMetaData.RoleInformationMetaData.ROLE_NAME)).hasValue(PRIMARY_PARTICIPANT);

        LOGGER.info("REN-33512 Step 7");
        Tab.buttonNext.click();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.fillTab(tdCertificatePolicy).submitTab();
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
        groupDentalCertificatePolicy.dataGather().start();
        String dateOfBirth2 = "08/03/1993";
        insuredTab.navigateToTab().getAssetList().getAsset(GENERAL_INFORMATION).getAsset(DATE_OF_BIRTH).setValue(dateOfBirth2);
        Tab.buttonNext.click();
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_INFORMATION).getAsset(PlansTabMetaData.RoleInformationMetaData.ROLE_NAME)).hasValue(PRIMARY_PARTICIPANT);
        Tab.buttonNext.click();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("REN-33512 Step 11");
        String age2 = DBService.get().getRow(getAgeQuery, quoteNumber).get("age");
        int calculatedAge2 = Period.between(LocalDate.parse(dateOfBirth2, formatter), LocalDate.parse(effectiveDate, formatter)).getYears();
        assertThat(age2).isEqualTo(String.valueOf(calculatedAge2));

        LOGGER.info("REN-33512 Step 12");
        groupDentalCertificatePolicy.issue().perform(tdCertificatePolicy.adjust(groupDentalCertificatePolicy.getDefaultTestData(ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());
        assertThat(labelPolicyStatus).hasValue(POLICY_ACTIVE);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-33689"}, component = POLICY_GROUPBENEFITS)
    public void testAgeRulesForNonPrimaryParticipant() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        TestData tdPolicy = getDefaultDNMasterPolicyData()
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS.getLabel()))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
                .mask(TestData.makeKeyPath(classificationManagementMpTab.getMetaKey() + "[0]", PLAN_TIER_AND_RATING_INFO.getLabel(), PlanTierAndRatingInfoMetaData.COVERAGE_TIER.getLabel()))
                .resolveLinks();
        groupDentalMasterPolicy.createPolicy(tdPolicy);

        LOGGER.info("REN-33689 Step 1");
        TestData tdCertificatePolicy = groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PlansTabMetaData.COVERAGE_TIER.getLabel()), "Employee + Spouse").resolveLinks();
        groupDentalCertificatePolicy.initiate(tdCertificatePolicy);

        LOGGER.info("REN-33689 Step 2");
        certificatePolicyTab.fillTab(tdCertificatePolicy);
        String effectiveDate = certificatePolicyTab.getAssetList().getAsset(CertificatePolicyTabMetaData.EFFECTIVE_DATE).getValue();

        LOGGER.info("REN-33689 Step 3");
        Tab.buttonNext.click();
        insuredTab.fillTab(tdCertificatePolicy);
        insuredTab.getAssetList().getAsset(GENERAL_INFORMATION).getAsset(DATE_OF_BIRTH).setValue("08/03/1983");

        LOGGER.info("REN-33689 Step 4, 5");
        Tab.buttonNext.click();
        coveragesTab.fillTab(tdCertificatePolicy);
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_INFORMATION).getAsset(PlansTabMetaData.RoleInformationMetaData.ROLE_NAME)).hasValue(PRIMARY_PARTICIPANT);
        String dateOfBirth = "08/03/1985";
        coveragesTab.fillTab(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AddNewParticipant")
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PARTICIPANTS.getLabel(), PlansTabMetaData.ParticipantsMetaData.GENERAL_INFORMATION.getLabel(), PlansTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH.getLabel()), dateOfBirth));
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_INFORMATION).getAsset(PlansTabMetaData.RoleInformationMetaData.ROLE_NAME)).hasValue(SPOUSE_PARTICIPANT);


        LOGGER.info("REN-33689 Step 7");
        Tab.buttonNext.click();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.fillTab(tdCertificatePolicy);
        GroupDentalCertificatePolicyContext.premiumSummaryTab.submitTab();
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
        groupDentalCertificatePolicy.dataGather().start();
        String dateOfBirth2 = "08/03/1995";
        coveragesTab.navigateToTab();
        coveragesTab.tableParticipantsList.getRow(CertificateParticipants.ROLE_NAME.getName(), SPOUSE_PARTICIPANT).getCell(6).controls.links.get(CHANGE).click();
        coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(PlansTabMetaData.ParticipantsMetaData.GENERAL_INFORMATION).getAsset(PlansTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH).setValue(dateOfBirth2);
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_INFORMATION).getAsset(PlansTabMetaData.RoleInformationMetaData.ROLE_NAME)).hasValue(SPOUSE_PARTICIPANT);
        Tab.buttonNext.click();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("REN-33689 Step 11");
        String age2 = DBService.get().getRow(getAgeQuery, "1995-08-03 00:00:00.0", quoteNumber).get("age");
        int calculatedAge2 = Period.between(LocalDate.parse(dateOfBirth2, formatter), LocalDate.parse(effectiveDate, formatter)).getYears();
        assertThat(age2).isEqualTo(String.valueOf(calculatedAge2));

        LOGGER.info("REN-33689 Step 12");
        groupDentalCertificatePolicy.issue().perform(
                tdCertificatePolicy.adjust(groupDentalCertificatePolicy.getDefaultTestData(ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());
        assertThat(labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("REN-33689 Step 18, 19");
        groupDentalCertificatePolicy.endorse().start();
        String plusOneMonth = TimeSetterUtil.getInstance().getCurrentTime().plusMonths(1).format(DateTimeUtils.MM_DD_YYYY);
        groupDentalCertificatePolicy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList().getAsset(ENDORSEMENT_DATE).setValue(plusOneMonth);
        groupDentalCertificatePolicy.endorse().getWorkspace().getTab(StartEndorsementActionTab.class).getAssetList().getAsset(ENDORSEMENT_REASON).setValue("Marriage");
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        coveragesTab.navigateToTab();
        coveragesTab.tableParticipantsList.getRow(CertificateParticipants.ROLE_NAME.getName(), SPOUSE_PARTICIPANT).getCell(6).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        String dateOfBirth3 = "08/03/1990";
        coveragesTab.fillTab(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AddNewParticipant")
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PARTICIPANTS.getLabel(), PlansTabMetaData.ParticipantsMetaData.GENERAL_INFORMATION.getLabel(), PlansTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH.getLabel()), dateOfBirth3));
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANTS).getAsset(ROLE_INFORMATION).getAsset(PlansTabMetaData.RoleInformationMetaData.ROLE_NAME)).hasValue(SPOUSE_PARTICIPANT);

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

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-39180"}, component = POLICY_GROUPBENEFITS)
    public void testDateOfBirthLessOrEqualEffectivDate_PrimaryParticipant() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();

        TestData tdPolicy = getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentTime.format(DateTimeUtils.MM_DD_YYYY))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS.getLabel()))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
                .mask(TestData.makeKeyPath(classificationManagementMpTab.getMetaKey(), PLAN_TIER_AND_RATING_INFO.getLabel(), PlanTierAndRatingInfoMetaData.COVERAGE_TIER.getLabel()))
                .resolveLinks();
        groupDentalMasterPolicy.createPolicy(tdPolicy);

        TestData tdCertificatePolicy = getDefaultGroupDentalCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), PlansTabMetaData.EFFECTIVE_DATE.getLabel()), currentTime.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), GENERAL_INFORMATION.getLabel(), DATE_OF_BIRTH.getLabel()), currentTime.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), COVERAGE_TIER.getLabel()), EMPLOYEE_ONLY)
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PARTICIPANTS.getLabel(), PARTICIPANT_SELECTION.getLabel()), "index=0")
                .resolveLinks();
        groupDentalCertificatePolicy.initiate(tdCertificatePolicy);

        LOGGER.info("Step 1, 2");
        groupDentalCertificatePolicy.getDefaultWorkspace().fillUpTo(tdCertificatePolicy, PlansTab.class, true);
        assertThat(dateOfBirthOnCoveragesTabAsset).hasNoWarning();

        LOGGER.info("Step 3");
        dateOfBirthOnCoveragesTabAsset.setValue(currentTime.plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(dateOfBirthOnCoveragesTabAsset).hasWarningWithText(DATE_OF_BIRTH_IS_AFTER_EFFECTIVE_DATE);

        LOGGER.info("Step 4");
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigateToTab();
        Tab.buttonRate.click();
        assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), DATE_OF_BIRTH_IS_AFTER_EFFECTIVE_DATE)).isPresent();

        LOGGER.info("Step 5");
        Tab.buttonCancel.click();
        coveragesTab.navigateToTab();
        dateOfBirthOnCoveragesTabAsset.setValue(currentTime.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        Tab.buttonNext.click();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Step 6");
        groupDentalCertificatePolicy.issue(tdCertificatePolicy);
        assertThat(labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("Step 7");
        TestData tdCertificateEndorse = groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(StartEndorsementActionTab.class.getSimpleName(), ENDORSEMENT_DATE.getLabel()), currentTime.plusMonths(6).format(DateTimeUtils.MM_DD_YYYY)).resolveLinks();
        EndorseAction.startEndorsementForPolicy(groupDentalMasterPolicy.getType(), tdCertificateEndorse);
        coveragesTab.navigateToTab();
        dateOfBirthOnCoveragesTabAsset.setValue(currentTime.plusMonths(2).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(dateOfBirthOnCoveragesTabAsset).hasNoWarning();

        LOGGER.info("Step 8");
        dateOfBirthOnCoveragesTabAsset.setValue(currentTime.plusMonths(6).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(dateOfBirthOnCoveragesTabAsset).hasNoWarning();

        LOGGER.info("Step 9");
        dateOfBirthOnCoveragesTabAsset.setValue(currentTime.plusMonths(7).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(dateOfBirthOnCoveragesTabAsset).hasWarningWithText(DATE_OF_BIRTH_IS_AFTER_EFFECTIVE_DATE);

        LOGGER.info("Step 10");
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigateToTab();
        Tab.buttonRate.click();
        assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), DATE_OF_BIRTH_IS_AFTER_EFFECTIVE_DATE)).isPresent();

        LOGGER.info("Step 11");
        Tab.buttonCancel.click();
        coveragesTab.navigateToTab();
        dateOfBirthOnCoveragesTabAsset.setValue(currentTime.minusMonths(2).format(DateTimeUtils.MM_DD_YYYY));
        Tab.buttonNext.click();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(labelPolicyStatus).hasValue(POLICY_ACTIVE);
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalCertificatePolicy.issue().perform();
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();

        LOGGER.info("Step 13");
        //ToDo RZnamerovskyi: finish after REN-33820 will be done
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-39196"}, component = POLICY_GROUPBENEFITS)
    public void testDateOfBirthLessOrEqualEffectivDate_NonPrimaryParticipant() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        LocalDateTime currentTime = TimeSetterUtil.getInstance().getCurrentTime();

        TestData tdPolicy = getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentTime.format(DateTimeUtils.MM_DD_YYYY))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS.getLabel()))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
                .mask(TestData.makeKeyPath(classificationManagementMpTab.getMetaKey(), PLAN_TIER_AND_RATING_INFO.getLabel(), PlanTierAndRatingInfoMetaData.COVERAGE_TIER.getLabel()))
                .resolveLinks();
        groupDentalMasterPolicy.createPolicy(tdPolicy);

        TestData tdCertificatePolicy = getDefaultGroupDentalCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), PlansTabMetaData.EFFECTIVE_DATE.getLabel()), currentTime.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), GENERAL_INFORMATION.getLabel(), DATE_OF_BIRTH.getLabel()), currentTime.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), COVERAGE_TIER.getLabel()), EMPLOYEE_FAMILY)
                .resolveLinks();
        groupDentalCertificatePolicy.initiate(tdCertificatePolicy);

        LOGGER.info("Step 1, 2, 3");
        groupDentalCertificatePolicy.getDefaultWorkspace().fillUpTo(tdCertificatePolicy, PlansTab.class, true);
        coveragesTab.fillTab(groupDentalCertificatePolicy.getDefaultTestData(DATA_GATHER, "TestData_AddNewParticipant")
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PARTICIPANTS.getLabel(), PlansTabMetaData.ParticipantsMetaData.GENERAL_INFORMATION.getLabel(), PlansTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH.getLabel()), currentTime.format(DateTimeUtils.MM_DD_YYYY)));
        assertThat(dateOfBirthOnCoveragesTabAsset).hasNoWarning();

        LOGGER.info("Step 4");
        dateOfBirthOnCoveragesTabAsset.setValue(currentTime.plusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(dateOfBirthOnCoveragesTabAsset).hasWarningWithText(DATE_OF_BIRTH_IS_AFTER_EFFECTIVE_DATE);

        LOGGER.info("Step 5");
        GroupDentalCertificatePolicyContext.premiumSummaryTab.navigateToTab();
        Tab.buttonRate.click();
        assertThat(ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), DATE_OF_BIRTH_IS_AFTER_EFFECTIVE_DATE)).isPresent();

        LOGGER.info("Step 6");
        Tab.buttonCancel.click();
        coveragesTab.navigateToTab();
        dateOfBirthOnCoveragesTabAsset.setValue(currentTime.minusDays(1).format(DateTimeUtils.MM_DD_YYYY));
        Tab.buttonNext.click();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("Step 7");
        groupDentalCertificatePolicy.issue(tdCertificatePolicy);
        assertThat(labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("Step 8");
        TestData tdCertificateEndorse = groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(StartEndorsementActionTab.class.getSimpleName(), ENDORSEMENT_DATE.getLabel()), currentTime.plusMonths(6).format(DateTimeUtils.MM_DD_YYYY)).resolveLinks();
        EndorseAction.startEndorsementForPolicy(groupDentalMasterPolicy.getType(), tdCertificateEndorse);
        coveragesTab.navigateToTab();
        coveragesTab.updateExistingParticipantByRoleName(SPOUSE_PARTICIPANT);
        dateOfBirthOnCoveragesTabAsset.setValue(currentTime.plusMonths(2).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(dateOfBirthOnCoveragesTabAsset).hasNoWarning();

        LOGGER.info("Step 9");
        dateOfBirthOnCoveragesTabAsset.setValue(currentTime.plusMonths(6).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(dateOfBirthOnCoveragesTabAsset).hasNoWarning();

        LOGGER.info("Step 10");
        Tab.buttonNext.click();
        GroupDentalCertificatePolicyContext.premiumSummaryTab.submitTab();
        assertThat(labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("Step 11");
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalCertificatePolicy.issue().perform();
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();

        LOGGER.info("Step 13");
        //ToDo RZnamerovskyi: finish after REN-33820 will be done
    }
}