package com.exigen.ren.modules.policy.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.metadata.SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAnniversaryJobDaysInAdvanceParameterVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalCertificatePolicyContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITH_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-33857", component = POLICY_GROUPBENEFITS)
    public void testAnniversaryJobDaysInAdvanceParameterVerification_DN() {
        LOGGER.info("General preconditions");
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        String policyEffectiveDate = groupDentalMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "InitiniateDialog").getValue(COVERAGE_EFFECTIVE_DATE.getLabel());

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData());
        groupDentalCertificatePolicy.createPolicy(getDefaultGroupDentalCertificatePolicyData()
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), InsuredTabMetaData.GENERAL_INFORMATION.getLabel(), InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH.getLabel()),
                        currentDate.minusYears(40).plusMonths(1).minusDays(1).format(MM_DD_YYYY))
                .adjust(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));
        String certificatePolicyNumberDN = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step#2 execution");
        TimeSetterUtil.getInstance().nextPhase(currentDate.plusYears(1).withDayOfYear(1).minusDays(20));
        Job job = new Job(GeneralSchedulerPage.ANNIVERSARY_AGING_PROCESSING_JOB.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", "daysInAdvance=20"));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.ANNIVERSARY_AGING_PROCESSING_JOB.getGroupName(), job));

        LOGGER.info("Steps#4 execution");
        mainApp().reopen();
        MainPage.QuickSearch.search(certificatePolicyNumberDN);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                .hasCellWithValue("Description", String.format("Update Anniversary Date for Policy %s effective %s", certificatePolicyNumberDN, policyEffectiveDate));
    }
}
