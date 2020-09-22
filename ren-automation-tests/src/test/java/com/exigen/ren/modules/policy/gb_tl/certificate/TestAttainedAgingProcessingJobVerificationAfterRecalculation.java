package com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MM_DD_YYYY;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.BTL;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.DEP_BTL;
import static com.exigen.ren.main.enums.PolicyConstants.Participants.*;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyTransactionHistoryTable.TYPE;
import static com.exigen.ren.main.enums.PolicyConstants.RelationshipToInsured.*;
import static com.exigen.ren.main.enums.ProductConstants.TransactionHistoryType.*;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CertificatePolicyTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.InsuredTabMetaData.DATE_OF_BIRTH;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CoveragesTab.*;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.tableTransactionHistory;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAttainedAgingProcessingJobVerificationAfterRecalculation extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    private static final StaticElement attainedAge = new StaticElement(By.id("policyDataGatherForm:sedit_GroupCertificateParticipantPersonInfoProxy_person_generalPartyInfoExt_attainedAge"));

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-39327", component = POLICY_GROUPBENEFITS)
    public void testAttainedAgingProcessingJobVerificationAfterRecalculation_TL() {
        LOGGER.info("General preconditions");
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.createPolicyViaUI(getDefaultTLMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestDataMaster").resolveLinks()));
        termLifeInsuranceCertificatePolicy.createPolicy(getDefaultCertificatePolicyDataGatherData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), EFFECTIVE_DATE.getLabel()), currentDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), DATE_OF_BIRTH.getLabel()), currentDate.minusYears(69).plusDays(20).format(DateTimeUtils.MM_DD_YYYY))
                .adjust(tdSpecific().getTestData("TestDataCertificate").resolveLinks())
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)).resolveLinks());
        String certPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        Job job = new Job(GeneralSchedulerPage.ATTAINED_AGING_PROCESSING_JOB.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", "productCodes=GB_TL;daysInAdvance=20;crashedJobTaskAge=43200"));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.ATTAINED_AGING_PROCESSING_JOB.getGroupName(), job));

        LOGGER.info("Scenario#1 step#1 verification");
        mainApp().reopen();
        MainPage.QuickSearch.search(certPolicyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Scenario#1 step#2 verification");
        assertSoftly(softly -> {
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                    .hasCellWithValue("Description", String.format("Update Participant Attained Age for participants who reached attained age on Policy %s", certPolicyNumber));
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getColumn("Description").getValue()).doesNotContain
                    (String.format("Issue Endorsement from Attained Aging Processing effective %s for Policy %s", currentDate.format(DateTimeUtils.MM_DD_YYYY), certPolicyNumber));
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getColumn("Description").getValue()).doesNotContain
                    (String.format("Policy Cancellation from Attained Aging Processing effective %s for Policy %s", currentDate.format(DateTimeUtils.MM_DD_YYYY), certPolicyNumber));
        });

        LOGGER.info("Scenario#1 step#3 verification");
        PolicySummaryPage.buttonTransactionHistory.click();
        assertThat(tableTransactionHistory.getColumn(TYPE).getValue()).doesNotContain(ENDORSEMENT, CANCELLATION).containsOnly(ISSUE);
        Tab.buttonCancel.click();

        LOGGER.info("Scenario#1 step#4 verification");
        termLifeInsuranceCertificatePolicy.quoteInquiry().start();
        coveragesTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(tableCoveragesList.getColumn(TableConstants.CertificateCoverages.COVERAGE_NAME).getValue()).contains(BTL, DEP_BTL);

            LOGGER.info("Scenario#1 step#5 verification");
            openAddedPlan(BTL);
            softly.assertThat(tableParticipantsList.getColumn(TableConstants.CertificateParticipants.ROLE_NAME).getValue()).containsOnly(PRIMARY_PARTICIPANT);

            LOGGER.info("Scenario#1 step#6 verification");
            softly.assertThat(attainedAge).hasValue("69");

            LOGGER.info("Scenario#1 step#7 verification");
            openAddedPlan(DEP_BTL);
            softly.assertThat(tableParticipantsList.getColumn(TableConstants.CertificateParticipants.ROLE_NAME).getValue()).containsOnly(SPOUSE_PARTICIPANT, CHILD_PARTICIPANT, CHILD_PARTICIPANT);

            LOGGER.info("Scenario#1 step#8 verification");
            openExistingParticipant(SPOUSE_DOMESTIC_PARTNER);
            softly.assertThat(attainedAge).hasValue("69");

            LOGGER.info("Scenario#1 step#9 verification");
            openExistingParticipant(DEPENDENT_CHILD);
            softly.assertThat(attainedAge).hasValue("25");

            openExistingParticipant(DISABLED_DEPENDENT);
            softly.assertThat(attainedAge).hasValue("25");
        });

        LOGGER.info("Scenario#2 step#1 verification");
        String jobName = String.format("%s%s", GeneralSchedulerPage.ATTAINED_AGING_PROCESSING_JOB.getGroupName(), RandomStringUtils.randomNumeric(5));
        JobRunner.executeJob(new JobGroup(jobName, new Job(GeneralSchedulerPage.ATTAINED_AGING_PROCESSING_JOB.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", "productCodes=GB_TL;daysInAdvance=384"))));

        LOGGER.info("Scenario#2 step#1 verification");
        mainApp().reopen();
        MainPage.QuickSearch.search(certPolicyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Scenario#2 step#2 verification");
        RetryService.run(
                predicate -> NotesAndAlertsSummaryPage.activitiesAndUserNotes.getValue().size() > 6,
                () -> {
                    MainPage.QuickSearch.search(certPolicyNumber);
                    return null;
                },
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertSoftly(softly -> {
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                    .hasCellWithValue("Description", String.format("Issue Endorsement from Attained Aging Processing effective %s for Policy %s", currentDate.plusYears(1).plusDays(10).format(DateTimeUtils.MM_DD_YYYY), certPolicyNumber));
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2))
                    .hasCellWithValue("Description", String.format("Issue Endorsement from Attained Aging Processing effective %s for Policy %s", currentDate.plusYears(1).plusDays(5).format(DateTimeUtils.MM_DD_YYYY), certPolicyNumber));
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getColumn("Description").getValue()).doesNotContain
                    (String.format("Policy Cancellation from Attained Aging Processing effective %s for Policy %s", currentDate.format(DateTimeUtils.MM_DD_YYYY), certPolicyNumber));
        });

        LOGGER.info("Scenario#2 step#3 verification");
        PolicySummaryPage.buttonTransactionHistory.click();
        assertThat(tableTransactionHistory.getColumn(TYPE).getValue()).containsOnly(ENDORSEMENT, ENDORSEMENT, ISSUE);

        LOGGER.info("Scenario#2 step#4 verification");
        assertSoftly(softly -> {
            softly.assertThat(tableTransactionHistory.getRow(1).getCell(TYPE)).hasValue(ENDORSEMENT);
            softly.assertThat(tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.TRANSACTION_DATE)).hasValue(currentDate.format(MM_DD_YYYY));
            softly.assertThat(tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.EFFECTIVE_DATE)).hasValue(currentDate.plusYears(1).plusDays(10).format(MM_DD_YYYY));
            softly.assertThat(tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.REASON)).hasValue("Attained Aging Processing");
            softly.assertThat(tableTransactionHistory.getRow(1).getCell(PolicyConstants.PolicyTransactionHistoryTable.PERFORMER)).hasValue("IPB System user IPB System user");

            softly.assertThat(tableTransactionHistory.getRow(2).getCell(TYPE)).hasValue(ENDORSEMENT);
            softly.assertThat(tableTransactionHistory.getRow(2).getCell(PolicyConstants.PolicyTransactionHistoryTable.TRANSACTION_DATE)).hasValue(currentDate.format(MM_DD_YYYY));
            softly.assertThat(tableTransactionHistory.getRow(2).getCell(PolicyConstants.PolicyTransactionHistoryTable.EFFECTIVE_DATE)).hasValue(currentDate.plusYears(1).plusDays(5).format(MM_DD_YYYY));
            softly.assertThat(tableTransactionHistory.getRow(2).getCell(PolicyConstants.PolicyTransactionHistoryTable.REASON)).hasValue("Attained Aging Processing");
            softly.assertThat(tableTransactionHistory.getRow(2).getCell(PolicyConstants.PolicyTransactionHistoryTable.PERFORMER)).hasValue("IPB System user IPB System user");
        });
        Tab.buttonCancel.click();

        LOGGER.info("Scenario#2 step#12 verification");
        termLifeInsuranceCertificatePolicy.quoteInquiry().start();
        coveragesTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(tableCoveragesList.getColumn(TableConstants.CertificateCoverages.COVERAGE_NAME).getValue()).contains(BTL, DEP_BTL);

            LOGGER.info("Scenario#2 step#13 verification");
            openAddedPlan(BTL);
            softly.assertThat(tableParticipantsList.getColumn(TableConstants.CertificateParticipants.ROLE_NAME).getValue()).containsOnly(PRIMARY_PARTICIPANT);

            LOGGER.info("Scenario#2 step#14 verification");
            softly.assertThat(attainedAge).hasValue("69");

            LOGGER.info("Scenario#2 step#15 verification");
            openAddedPlan(DEP_BTL);
            softly.assertThat(tableParticipantsList.getColumn(TableConstants.CertificateParticipants.ROLE_NAME).getValue()).containsOnly(CHILD_PARTICIPANT);

            LOGGER.info("Scenario#2 step#16 verification");
            openExistingParticipant(DISABLED_DEPENDENT);
            softly.assertThat(attainedAge).hasValue("26");
        });

        LOGGER.info("Scenario#3 step#1 verification");
        TimeSetterUtil.getInstance().nextPhase(currentDate.plusYears(1).plusDays(10));

        String jobName2 = String.format("%s%s", GeneralSchedulerPage.ATTAINED_AGING_PROCESSING_JOB.getGroupName(), RandomStringUtils.randomNumeric(5));
        JobRunner.executeJob(new JobGroup(jobName2, new Job(GeneralSchedulerPage.ATTAINED_AGING_PROCESSING_JOB.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", "productCodes=GB_TL;daysInAdvance=10"))));

        LOGGER.info("Scenario#3 step#1 verification");
        mainApp().reopen();
        MainPage.QuickSearch.search(certPolicyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

        LOGGER.info("Scenario#3 step#2 verification");
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                .hasCellWithValue("Description", String.format("Policy Cancellation from Attained Aging Processing effective %s for Policy %s", currentDate.plusYears(1).plusDays(20).format(MM_DD_YYYY), certPolicyNumber));

        LOGGER.info("Scenario#3 step#3 verification");
        PolicySummaryPage.buttonTransactionHistory.click();
        LocalDateTime currentDateNew = TimeSetterUtil.getInstance().getCurrentTime();

        assertSoftly(softly -> {
            softly.assertThat(tableTransactionHistory.getRowContains(TYPE, CANCELLATION).getCell(PolicyConstants.PolicyTransactionHistoryTable.TRANSACTION_DATE)).hasValue(currentDateNew.format(MM_DD_YYYY));
            softly.assertThat(tableTransactionHistory.getRowContains(TYPE, CANCELLATION).getCell(PolicyConstants.PolicyTransactionHistoryTable.EFFECTIVE_DATE)).hasValue(currentDate.plusYears(1).plusDays(20).format(MM_DD_YYYY));
            softly.assertThat(tableTransactionHistory.getRowContains(TYPE, CANCELLATION).getCell(PolicyConstants.PolicyTransactionHistoryTable.REASON)).hasValue("Attained Aging Processing");
            softly.assertThat(tableTransactionHistory.getRowContains(TYPE, CANCELLATION).getCell(PolicyConstants.PolicyTransactionHistoryTable.PERFORMER)).hasValue("IPB System user IPB System user");
        });
        Tab.buttonCancel.click();

        LOGGER.info("Scenario#3 step#5 verification");
        termLifeInsuranceCertificatePolicy.quoteInquiry().start();
        coveragesTab.navigateToTab();
        assertThat(tableCoveragesList.getColumn(TableConstants.CertificateCoverages.COVERAGE_NAME).getValue()).contains(BTL, DEP_BTL);

        LOGGER.info("Scenario#3 step#6 verification");
        openAddedPlan(BTL);
        assertThat(tableParticipantsList.getColumn(TableConstants.CertificateParticipants.ROLE_NAME).getValue()).containsOnly(PRIMARY_PARTICIPANT);

        LOGGER.info("Scenario#3 step#7 verification");
        assertThat(attainedAge).hasValue("70");
    }
}
