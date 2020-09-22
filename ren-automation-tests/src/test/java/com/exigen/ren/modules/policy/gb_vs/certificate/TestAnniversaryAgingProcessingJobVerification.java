package com.exigen.ren.modules.policy.gb_vs.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage.ANNIVERSARY_AGING_PROCESSING_JOB;
import static com.exigen.ren.common.metadata.SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupDentalCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.RelationshipToInsured.SELF;
import static com.exigen.ren.main.enums.PolicyConstants.RelationshipToInsured.SPOUSE_DOMESTIC_PARTNER;
import static com.exigen.ren.main.enums.TableConstants.CertificateParticipants.RELATIONSHIP_TO_INSURED;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PlansTabMetaData.PARTICIPANTS;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PlansTab.tableParticipantsList;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs.PremiumSummaryTab.tablePremiumSummary;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.DEPENDENT_MAXIMUM_AGE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAnniversaryAgingProcessingJobVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionCertificatePolicyContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-36327", component = POLICY_GROUPBENEFITS)
    public void testAnniversaryAgingProcessingJobVerificationScenario3() {
        LOGGER.info("General preconditions");
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        String policyEffectiveDate = groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "InitiniateDialog").getValue(COVERAGE_EFFECTIVE_DATE.getLabel());

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData()
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS.getLabel()), ImmutableList.of(EMPLOYEE_ONLY, EMPLOYEE_SPOUSE, EMPLOYEE_CHILD_REN, EMPLOYEE_FAMILY)).resolveLinks()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ELIGIBILITY.getLabel(), DEPENDENT_MAXIMUM_AGE.getLabel()), "25"));

        groupVisionCertificatePolicy.createPolicy(getDefaultVSCertificatePolicyData()
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), InsuredTabMetaData.GENERAL_INFORMATION.getLabel(), InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH.getLabel()), currentDate.minusYears(35).withDayOfYear(1).minusDays(3).format(MM_DD_YYYY))
                .adjust(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PARTICIPANTS.getLabel() + "[1]",
                                PlansTabMetaData.ParticipantsMetaData.PARTICIPANT_GENERAL_INFO.getLabel(), PlansTabMetaData.GeneralInfoMetaData.DATE_OF_BIRTH.getLabel()), currentDate.minusYears(24).withDayOfYear(1).minusDays(3).format(MM_DD_YYYY)).resolveLinks()).resolveLinks());
        String initialPremium = PolicySummaryPage.tablePremiumSummary.getColumn(PolicySummaryPage.PremiumSummary.ANNUAL_PREMIUM.getName()).getCell(1).getValue();
        String certificatePolicyNumberTL = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step#1 execution");
        TimeSetterUtil.getInstance().nextPhase(currentDate.plusYears(1).withDayOfYear(1));
        JobRunner.executeJob(ANNIVERSARY_AGING_PROCESSING_JOB);

        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNumberTL);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                .hasCellWithValue("Description", String.format("Update Anniversary Date for Policy %s effective %s", certificatePolicyNumberTL, policyEffectiveDate));

        LOGGER.info("Step#2, 3 verification");
        TimeSetterUtil.getInstance().nextPhase(currentDate.plusYears(2).withDayOfYear(1));
        JobRunner.executeJob(ANNIVERSARY_AGING_PROCESSING_JOB);

        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNumberTL);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                .hasCellWithValue("Description", String.format("Update Anniversary Date for Policy %s effective %s", certificatePolicyNumberTL, policyEffectiveDate));

        LOGGER.info("Step#4 verification");
        groupVisionCertificatePolicy.quoteInquiry().start();
        coveragesTab.navigateToTab();
        assertThat(tableParticipantsList.getColumn(RELATIONSHIP_TO_INSURED.getName()).getValue()).containsOnly(SELF, SPOUSE_DOMESTIC_PARTNER);

        LOGGER.info("Step#6 verification");
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(EMPLOYEE_SPOUSE))).isPresent();

        LOGGER.info("Step#7 verification");
        GroupVisionCertificatePolicyContext.premiumSummaryTab.navigateToTab();
        String premiumAfterJobExecution = tablePremiumSummary.getColumn(PremiumSummaryTab.PremiumSummary.ANNUAL_PREMIUM.getName()).getCell(1).getValue();
        assertThat(premiumAfterJobExecution).isNotEqualTo(initialPremium);
    }
}
