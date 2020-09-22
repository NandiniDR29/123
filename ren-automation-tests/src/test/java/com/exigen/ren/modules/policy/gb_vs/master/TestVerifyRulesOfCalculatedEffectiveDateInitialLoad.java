package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.policyBenefits.enrollmentServices.models.EnrollmentCensusResponseModel;
import com.exigen.ren.rest.policyBenefits.enrollmentServices.models.enrollmentCensusRecordsModels.EnrollmentCensusRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCertificatePoliciesTable.STATUS;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_PENDING;
import static com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.ELIGIBILITY;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.WAITING_PERIOD_AMOUNT;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.tableCertificatePolicies;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static java.time.temporal.ChronoUnit.DAYS;

public class TestVerifyRulesOfCalculatedEffectiveDateInitialLoad extends RestBaseTest implements CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-36605", component = POLICY_GROUPBENEFITS)
    public void testVerifyRulesOfCalculatedEffectiveDateInitialLoad() {
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();
        LocalDateTime effectiveDate = currentDate.minusDays(2);

        mainApp().open();
        createDefaultNonIndividualCustomer();

        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupVisionMasterPolicy.getType())
                .adjust(makeKeyPath(caseProfileDetailsTab.getMetaKey(), EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY)));

        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy").resolveLinks())
                .adjust(makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ELIGIBILITY.getLabel(), WAITING_PERIOD_AMOUNT.getLabel()),
                        String.valueOf(DAYS.between(currentDate, currentDate.with(TemporalAdjusters.firstDayOfNextMonth())))));
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST TC1");
        commonSteps(
                policyNumber,
                effectiveDate.plusDays(1),
                effectiveDate.plusDays(1).with(TemporalAdjusters.firstDayOfNextMonth()),
                POLICY_PENDING,
                1);

        LOGGER.info("TEST TC2");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));
        mainApp().reopen();
        MainPage.QuickSearch.search(policyNumber);

        commonSteps(
                policyNumber,
                effectiveDate.minusDays(1),
                effectiveDate,
                POLICY_ACTIVE,
                2);
    }

    private void commonSteps(String policyNumber, LocalDateTime originalHireDt, LocalDateTime calculatedEffectiveDate, String enrollmentStatus, int rowNumber) {
        String personOID = String.format("P%s", RandomStringUtils.randomNumeric(5));
        String firstName = String.format("FirstName%s", RandomStringUtils.randomNumeric(4));
        String lastName = String.format("lastName%s", RandomStringUtils.randomNumeric(4));

        ResponseContainer<EnrollmentCensusResponseModel> responseTC1 = enrollmentRestService.postEnrollmentPolicyNumberDeploy(policyNumber,
                tdSpecific().getTestData("TestData_REST")
                        .adjust(makeKeyPath("fileMetaData", "masterPolicyNumber"), policyNumber)
                        .adjust(makeKeyPath("enrollmentCensusRecords", "certificate", "primaryParticipant", "relationship", "employment", "originalHireDt"), originalHireDt.format(YYYY_MM_DD_HH_MM_SS_Z))
                        .adjust(makeKeyPath("enrollmentCensusRecords", "certificate", "effectiveDt"), calculatedEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z))
                        .adjust(makeKeyPath("enrollmentCensusRecords", "certificate", "coverages", "participants", "personOID"), personOID)
                        .adjust(makeKeyPath("enrollmentCensusRecords", "certificate", "primaryParticipant", "personOID"), personOID)
                        .adjust(makeKeyPath("enrollmentCensusRecords", "allParticipants", "oid"), personOID)
                        .adjust(makeKeyPath("enrollmentCensusRecords", "allParticipants", "firstName"), firstName)
                        .adjust(makeKeyPath("enrollmentCensusRecords", "allParticipants", "lastName"), lastName).resolveLinks(),
                EnrollmentCensusRequest.class);

        assertThat(responseTC1.getResponse().getStatus()).isEqualTo(200);

        LOGGER.info("TEST: Step 1.2");
        PolicySummaryPage.waitEnrollmentStatusByName(firstName.concat(" ").concat(lastName), "Passed");

        LOGGER.info("TEST: Step 1.3");
        mainApp().close();
        JobRunner.executeJob(GeneralSchedulerPage.REN_GROUP_ENROLLMENT_PROCESSING_JOB);

        LOGGER.info("TEST: Step 1.4");
        mainApp().reopen();
        MainPage.QuickSearch.search(policyNumber);

        PolicySummaryPage.waitEnrollmentStatusByName(firstName.concat(" ").concat(lastName), "Completed");

        PolicySummaryPage.waitCertificatePolicyIssued(1);

        assertSoftly(softly -> {
            softly.assertThat(tableCertificatePolicies).hasRows(rowNumber);
            softly.assertThat(tableCertificatePolicies.getRow(1).getCell(STATUS)).hasValue(enrollmentStatus);
        });
    }
}