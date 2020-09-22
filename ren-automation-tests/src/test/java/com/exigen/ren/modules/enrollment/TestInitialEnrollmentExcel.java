package com.exigen.ren.modules.enrollment;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.metadata.SearchMetaData;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.module.efolder.defaulttabs.AddFileTab;
import com.exigen.ren.common.module.efolder.metadata.AddFileTabMetaData;
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
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD;
import static com.exigen.ren.common.module.efolder.EfolderContext.efolder;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MMDDYYYY;
import static com.exigen.ren.main.enums.BamConstants.FINISHED;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCertificatePoliciesTable.POLICY_NUMBER;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ENROLLMENT_FILE_DIALOG;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.EnrollmentFileMetaData.*;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.EnrollmentFileMetaData.SUBMIT_POPUP;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ISSUE_ENROLLMENT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.RoleSummary.CUSTOMER_NAME;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.tableCertificatePolicies;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.tableEnrollmentProcessingResults;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInitialEnrollmentExcel extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext, TermLifeInsuranceMasterPolicyContext, ShortTermDisabilityMasterPolicyContext, LongTermDisabilityMasterPolicyContext, GroupAccidentMasterPolicyContext, GroupDentalMasterPolicyContext {

    private static final File FILE = new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), "Initial_Enrollment.xlsx");

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollmentExcel_VS() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile"), groupVisionMasterPolicy.getType());

        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy_VS").resolveLinks()));

        String newEnrollmentFilename = commonSteps(groupVisionMasterPolicy, customerId);

        LOGGER.info("TEST: Step 8");
        assertThat(PolicySummaryPage.tableRoleSummary.getColumn(CUSTOMER_NAME).getValue())
                .containsAll(InitialEnrollmentHelper.getParticipantsFromXLSXFile(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), newEnrollmentFilename)));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollmentExcel_AC() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile"), groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy_AC").resolveLinks()));

        String newEnrollmentFilename = commonSteps(groupAccidentMasterPolicy, customerId);

        LOGGER.info("TEST: Step 8");
        assertThat(PolicySummaryPage.tableSTDCoreRoleSummary.getColumn(CUSTOMER_NAME).getValue())
                .containsAll(InitialEnrollmentHelper.getParticipantsFromXLSXFile(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), newEnrollmentFilename)));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollmentExcel_STD() {
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
        assertThat(PolicySummaryPage.tableSTDCoreRoleSummary.getColumn(CUSTOMER_NAME).getValue()).
                containsOnly(InitialEnrollmentHelper.getParticipantsFromXLSXFile(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), newEnrollmentFilename)).get(0));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollmentExcel_LTD() {
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
        assertThat(PolicySummaryPage.tableSTDCoreRoleSummary.getColumn(CUSTOMER_NAME).getValue()).
                containsOnly(InitialEnrollmentHelper.getParticipantsFromXLSXFile(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), newEnrollmentFilename)).get(0));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollmentExcel_TL() {
        String currentDateMinusFiveDays = TimeSetterUtil.getInstance().getCurrentTime().minusDays(5).format(MMDDYYYY);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile_DI"), termLifeInsuranceMasterPolicy.getType());

        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE.getLabel()), currentDateMinusFiveDays)
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), POLICY_EFFECTIVE_DATE.getLabel()), currentDateMinusFiveDays));

        String newEnrollmentFilename = commonSteps(termLifeInsuranceMasterPolicy, customerId);

        LOGGER.info("TEST: Step 8");
        List<String> participantList = InitialEnrollmentHelper.getParticipantsFromXLSXFile(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), newEnrollmentFilename));

        assertThat(PolicySummaryPage.tableEmployeeBasicLifeInsuranceRoleSummary.getColumn(CUSTOMER_NAME).getValue()).containsOnly(participantList.get(0));
        assertThat(PolicySummaryPage.tableEmployeeBADIRoleSummary.getColumn(CUSTOMER_NAME).getValue()).containsOnly(participantList.get(0));
        assertThat(PolicySummaryPage.tableDependentBasicLifeInsuranceRoleSummary.getColumn(CUSTOMER_NAME).getValue()).containsAll(participantList.subList(1, 4));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollmentExcel_DN() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile"), groupDentalMasterPolicy.getType());

        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy_DN").resolveLinks()));

        String newEnrollmentFilename = commonSteps(groupDentalMasterPolicy, customerId);

        LOGGER.info("TEST: Step 8");
        assertThat(PolicySummaryPage.tableRoleSummary.getColumn(CUSTOMER_NAME).getValue()).
                containsAll(InitialEnrollmentHelper.getParticipantsFromXLSXFile(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), newEnrollmentFilename)));
    }

    private String commonSteps(GroupBenefitsMasterPolicy policyType, String customerId) {
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Step 1");
        MainPage.QuickSearch.search(customerId);

        String filename = InitialEnrollmentHelper.prepareXLSXFile(FILE, customerId);

        Efolder.expandFolder(ENROLLMENT_UPLOAD.getName());
        efolder.addDocument(efolder.getDefaultTestData(DATA_GATHER).getTestData("TestData_EnrollmentFile")
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.FILE_UPLOAD.getLabel()), String.format("$<file:%s>", filename))
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.NAME.getLabel()), filename), ENROLLMENT_UPLOAD.getName());

        LOGGER.info("TEST: Step 2");
        CustomerSummaryPage.verifyBamActivitiesContains("4 records passed validation and 0 records failed", FINISHED);

        LOGGER.info("TEST: Step 4");
        customerNonIndividual.inquiry().start();
        generalTab.getAssetList().getAsset(ISSUE_ENROLLMENT).click();
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE_TYPE).setValue("Initial");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(RETRIEVE).click();

        try {
            RetryService.run(predicate -> generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE).getAllValues()
                            .stream().anyMatch(file -> file.contains("Initial_Enrollment")),
                    () -> {
                        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(RETRIEVE).click();
                        return null;
                    }, StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            throw new IstfException("Enrollment File is absent", e);
        }
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE).setValueContains("Initial_Enrollment");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(SUBMIT_POPUP).click();
        Page.dialogConfirmation.confirm();
        Tab.buttonCancel.click();

        LOGGER.info("TEST: Step 7");
        MainPage.QuickSearch.search(policyNumber);
        PolicySummaryPage.expandEnrollmentProcessingResultsTable();

        try {
            RetryService.run(predicate -> tableEnrollmentProcessingResults.getRow(1).getCell(8).getValue().equals("Completed"),
                    () -> {
                        BrowserController.get().driver().navigate().refresh();
                        PolicySummaryPage.expandEnrollmentProcessingResultsTable();
                        return null;
                    }, StopStrategies.stopAfterAttempt(30), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            throw new IstfException(String.format("'Enrollment Status': Expected: 'Completed', but Actual '%s'",
                    tableEnrollmentProcessingResults.getRow(1).getCell(8).getValue()), e);
        }

        try {
            RetryService.run(predicate -> tableCertificatePolicies.getRow(1).getCell(POLICY_NUMBER).isPresent(),
                    () -> {
                        BrowserController.get().driver().navigate().refresh();
                        return null;
                    }, StopStrategies.stopAfterAttempt(30), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            throw new IstfException("Certificate Policies table is empty", e);
        }

        PolicySummaryPage.tableCertificatePolicies.getRow(1).getCell(POLICY_NUMBER).controls.links.getFirst().click();

        return filename;
    }
}