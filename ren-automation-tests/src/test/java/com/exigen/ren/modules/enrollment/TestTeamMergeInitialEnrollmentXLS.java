package com.exigen.ren.modules.enrollment;

import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.EfolderConstants.EFolderNonIndCustomer;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.module.efolder.defaulttabs.AddFileTab;
import com.exigen.ren.common.module.efolder.metadata.AddFileTabMetaData;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.helpers.initialenrollment.InitialEnrollmentHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.io.File;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.common.module.efolder.EfolderContext.efolder;
import static com.exigen.ren.main.enums.BamConstants.FINISHED;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCertificatePoliciesTable.POLICY_NUMBER;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.RoleSummary.CUSTOMER_NAME;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.tableCertificatePolicies;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestTeamMergeInitialEnrollmentXLS extends BaseTest implements CustomerContext, CaseProfileContext,
        GroupDentalMasterPolicyContext, GroupVisionMasterPolicyContext {

    private static final File FILE_XLS = new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), "Initial_Enrollment.xlsx");

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, GB_VS, WITHOUT_TS, TEAM_MERGE})
    @TestInfo(testCaseId = "REN-42270", component = POLICY_GROUPBENEFITS)
    public void testInitialEnrollmentExcel() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile"));

        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy_VS").resolveLinks()));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        String vsPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy_DN").resolveLinks()));

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);
        String dnPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Step 1");
        MainPage.QuickSearch.search(customerId);

        String filename = InitialEnrollmentHelper.prepareXLSXFile(FILE_XLS, customerId);

        Efolder.expandFolder(EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName());
        efolder.addDocument(efolder.getDefaultTestData(DATA_GATHER).getTestData("TestData_EnrollmentFile")
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.FILE_UPLOAD.getLabel()), String.format("$<file:%s>", filename))
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.NAME.getLabel()), filename), EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName());

        LOGGER.info("TEST: Step 2");
        CustomerSummaryPage.verifyBamActivitiesContains("4 records passed validation and 0 records failed", FINISHED);

        LOGGER.info("TEST: Step 4");
        customerNonIndividual.inquiry().start();
        generalTab.getAssetList().fill(customerNonIndividual.getDefaultTestData(DATA_GATHER, "TestData_Issue_Enrollment"));
        Tab.buttonCancel.click();

        LOGGER.info("TEST: Step 7");
        MainPage.QuickSearch.search(dnPolicyNumber);
        LOGGER.info("TEST: Step 8");
        PolicySummaryPage.waitEnrollmentStatusByRowNumber(1, "Completed");
        PolicySummaryPage.waitCertificatePolicyIssued(1);
        tableCertificatePolicies.getRow(1).getCell(POLICY_NUMBER).controls.links.getFirst().click();

        assertThat(PolicySummaryPage.tableRoleSummary.getColumn(CUSTOMER_NAME).getValue())
                .containsAll(InitialEnrollmentHelper.getParticipantsFromXLSXFile(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), filename)));

        MainPage.QuickSearch.search(vsPolicyNumber);
        LOGGER.info("TEST: Step 8");
        PolicySummaryPage.waitEnrollmentStatusByRowNumber(1, "Completed");
        PolicySummaryPage.waitCertificatePolicyIssued(1);
        tableCertificatePolicies.getRow(1).getCell(POLICY_NUMBER).controls.links.getFirst().click();

        assertThat(PolicySummaryPage.tableRoleSummary.getColumn(CUSTOMER_NAME).getValue())
                .containsAll(InitialEnrollmentHelper.getParticipantsFromXLSXFile(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), filename)));
    }
}