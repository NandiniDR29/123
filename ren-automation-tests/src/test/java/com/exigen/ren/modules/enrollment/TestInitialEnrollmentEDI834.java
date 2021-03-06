package com.exigen.ren.modules.enrollment;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.SSHController;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.ren.admin.modules.general.dblookups.DBLookupsContext;
import com.exigen.ren.admin.modules.general.dblookups.tabs.DBLookupsTab;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.metadata.SearchMetaData;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.helpers.initialenrollment.InitialEnrollmentHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicy;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.exigen.ren.utils.SFTPConnection;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.config.PropertyProvider.getProperty;
import static com.exigen.istf.config.TestProperties.UPLOAD_FILES_LOCATION;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.admin.modules.general.dblookups.metadata.DBLookupsMetaData.*;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MMDDYYYY;
import static com.exigen.ren.helpers.initialenrollment.InitialEnrollmentHelper.getParticipantsFromEDI834File;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCertificatePoliciesTable.POLICY_NUMBER;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ENROLLMENT_FILE_DIALOG;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.EnrollmentFileMetaData.*;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ISSUE_ENROLLMENT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.RoleSummary.CUSTOMER_NAME;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.tableCertificatePolicies;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.tableEnrollmentProcessingResults;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInitialEnrollmentEDI834 extends BaseTest implements CustomerContext, CaseProfileContext, DBLookupsContext,
        GroupVisionMasterPolicyContext, TermLifeInsuranceMasterPolicyContext, ShortTermDisabilityMasterPolicyContext,
        LongTermDisabilityMasterPolicyContext, GroupAccidentMasterPolicyContext, GroupDentalMasterPolicyContext {

    private static final File FILE = new File(getProperty(UPLOAD_FILES_LOCATION), "IE_834_file_template.dat");
    private static final String FILE_PATH_TO_UPLOAD = "/mnt/ren/shared/Enrollment/%s/834";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollment834_VS() throws JSchException, SftpException {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile"), groupVisionMasterPolicy.getType());

        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy_VS").resolveLinks()));

        String newEnrollmentFilename = commonSteps(groupVisionMasterPolicy, customerId);

        LOGGER.info("TEST: Step 8");
        assertThat(PolicySummaryPage.tableRoleSummary.getColumn(CUSTOMER_NAME).getValue())
                .containsAll(getParticipantsFromEDI834File(new File(getProperty(UPLOAD_FILES_LOCATION), newEnrollmentFilename)));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollment834_AC() throws JSchException, SftpException {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile"), groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy_AC").resolveLinks()));

        String newEnrollmentFilename = commonSteps(groupAccidentMasterPolicy, customerId);

        LOGGER.info("TEST: Step 8");
        assertThat(PolicySummaryPage.tableSTDCoreRoleSummary.getColumn(CUSTOMER_NAME).getValue())
                .containsAll(getParticipantsFromEDI834File(new File(getProperty(UPLOAD_FILES_LOCATION), newEnrollmentFilename)));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollment834_STD() throws JSchException, SftpException {
        String currentDateMinusFiveDays = TimeSetterUtil.getInstance().getCurrentTime().minusDays(5).format(MMDDYYYY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile_DI"), shortTermDisabilityMasterPolicy.getType());

        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData().adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_CON"))
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE.getLabel()), currentDateMinusFiveDays)
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), POLICY_EFFECTIVE_DATE.getLabel()), currentDateMinusFiveDays));

        String newEnrollmentFilename = commonSteps(shortTermDisabilityMasterPolicy, customerId);

        LOGGER.info("TEST: Step 8");
        assertThat(PolicySummaryPage.tableSTDCoreRoleSummary.getColumn(CUSTOMER_NAME).getValue())
                .containsOnly(getParticipantsFromEDI834File(new File(getProperty(UPLOAD_FILES_LOCATION), newEnrollmentFilename)).get(0));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollment834_LTD() throws JSchException, SftpException {
        String currentDateMinusFiveDays = TimeSetterUtil.getInstance().getCurrentTime().minusDays(5).format(MMDDYYYY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile_DI"), longTermDisabilityMasterPolicy.getType());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData().adjust(longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_CON"))
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE.getLabel()), currentDateMinusFiveDays)
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), POLICY_EFFECTIVE_DATE.getLabel()), currentDateMinusFiveDays));

        String newEnrollmentFilename = commonSteps(longTermDisabilityMasterPolicy, customerId);

        LOGGER.info("TEST: Step 8");
        assertThat(PolicySummaryPage.tableSTDCoreRoleSummary.getColumn(CUSTOMER_NAME).getValue())
                .containsOnly(getParticipantsFromEDI834File(new File(getProperty(UPLOAD_FILES_LOCATION), newEnrollmentFilename)).get(0));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollment834_TL() throws JSchException, SftpException {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile_DI"), termLifeInsuranceMasterPolicy.getType());

        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy_TL").resolveLinks()));

        String newEnrollmentFilename = commonSteps(termLifeInsuranceMasterPolicy, customerId);

        LOGGER.info("TEST: Step 8");
        List<String> participantList = getParticipantsFromEDI834File(new File(getProperty(UPLOAD_FILES_LOCATION), newEnrollmentFilename));

//        todo ddiachenko: add BTL for TL into 834 file after fixing https://jira.exigeninsurance.com/browse/REN-44669
        assertThat(PolicySummaryPage.tableEmployeeVoluntaryLifeInsuranceRoleSummary.getColumn(CUSTOMER_NAME).getValue()).containsOnly(participantList.get(0));
        assertThat(PolicySummaryPage.tableEmployeeVoluntaryADDRoleSummary.getColumn(CUSTOMER_NAME).getValue()).containsOnly(participantList.get(0));
        assertThat(PolicySummaryPage.tableSpouseVoluntaryLifeInsuranceRoleSummary.getColumn(CUSTOMER_NAME).getValue()).containsOnly(participantList.get(1));
        assertThat(PolicySummaryPage.tableChildVoluntaryLifeInsuranceRoleSummary.getColumn(CUSTOMER_NAME).getValue()).containsOnly(participantList.get(2));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollment834_DN() throws JSchException, SftpException {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile"), groupDentalMasterPolicy.getType());

        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy_DN").resolveLinks()));

        String newEnrollmentFilename = commonSteps(groupDentalMasterPolicy, customerId);

        LOGGER.info("TEST: Step 8");
        assertThat(PolicySummaryPage.tableRoleSummary.getColumn(CUSTOMER_NAME).getValue())
                .containsAll(getParticipantsFromEDI834File(new File(getProperty(UPLOAD_FILES_LOCATION), newEnrollmentFilename)));
    }

    private String commonSteps(GroupBenefitsMasterPolicy policyType, String customerId) throws SftpException, JSchException {
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Precondition 3");
        String sender = RandomStringUtils.randomNumeric(9);
        String filename = InitialEnrollmentHelper.prepareEDI834File(FILE, customerId, sender);

        adminApp().open();
        dbLookups.create(dbLookups.defaultTestData().getTestData("DataGather", "TestData_AddEnrollmentAllowedSenders")
                .adjust(TestData.makeKeyPath(DBLookupsTab.class.getSimpleName(), CODE.getLabel()), sender)
                .adjust(TestData.makeKeyPath(DBLookupsTab.class.getSimpleName(), VALUE_DEFAULT.getLabel()), sender)
                .adjust(TestData.makeKeyPath(DBLookupsTab.class.getSimpleName(), SENDER_ID.getLabel()), sender)
                .adjust(TestData.makeKeyPath(DBLookupsTab.class.getSimpleName(), CUSTOMER_ID.getLabel()), customerId));

        adminApp().close();
        JobRunner.executeJob(GeneralSchedulerPage.EDI_834_RETRIEVAL_JOB);

        LOGGER.info("TEST: Step 1");
        SSHController sftp = SFTPConnection.getClient();
        sftp.uploadFile(
                new File(getProperty(UPLOAD_FILES_LOCATION), filename),
                new File(String.format(FILE_PATH_TO_UPLOAD, sender)));
        mainApp().reopen();

        LOGGER.info("TEST: Step 4");
        MainPage.QuickSearch.search(customerId);
        customerNonIndividual.inquiry().start();
        generalTab.getAssetList().getAsset(ISSUE_ENROLLMENT).click();
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE_TYPE).setValue("Initial");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(RETRIEVE).click();

        try {
            RetryService.run(predicate -> generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE).getAllValues()
                            .stream().anyMatch(file -> file.contains("834_T_")),
                    () -> {
                        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(RETRIEVE).click();
                        return null;
                    }, StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            throw new IstfException("Enrollment File is absent", e);
        }
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE).setValueStarts("834_T_");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(SUBMIT_POPUP).click();
        Page.dialogConfirmation.confirm();
        Tab.buttonCancel.click();

        LOGGER.info("TEST: Step 5");
        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.expandEnrollmentProcessingResultsTable();

        LOGGER.info("TEST: Step 7");
        try {
            RetryService.run(predicate -> tableEnrollmentProcessingResults.getRow(1).getCell(8).getValue().equals("Completed"),
                    () -> {
                        BrowserController.get().driver().navigate().refresh();
                        PolicySummaryPage.expandEnrollmentProcessingResultsTable();
                        return null;
                    }, StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            throw new IstfException(String.format("'Enrollment Status': Expected: 'Completed', but Actual '%s'",
                    tableEnrollmentProcessingResults.getRow(1).getCell(8).getValue()), e);
        }

        try {
            RetryService.run(predicate -> tableCertificatePolicies.getRow(1).getCell(POLICY_NUMBER).isPresent(),
                    () -> {
                        BrowserController.get().driver().navigate().refresh();
                        return null;
                    }, StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            throw new IstfException("Certificate Policies table is empty", e);
        }

        PolicySummaryPage.tableCertificatePolicies.getRow(1).getCell(POLICY_NUMBER).controls.links.getFirst().click();

        return filename;
    }
}