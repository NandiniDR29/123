package com.exigen.ren.modules.enrollment;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.excel.ExcelFile;
import com.exigen.ipb.eisa.utils.excel.io.entity.area.table.ExcelTable;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.EfolderConstants;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.module.efolder.defaulttabs.AddFileTab;
import com.exigen.ren.common.module.efolder.metadata.AddFileTabMetaData;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.pages.ProcessingResultsPage;
import com.exigen.ren.main.modules.caseprofile.tabs.CreateEnrolledCensusFileIntakeManagementTab;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.caseprofile.CaseProfileBaseTest;
import com.exigen.ren.utils.components.Components;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.module.efolder.EfolderContext.efolder;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.caseprofile.metadata.CreateEnrolledCensusFileIntakeManagementTabMetaData.GENERATE_AND_UPLOAD_CENSUS_FILE;
import static com.exigen.ren.main.modules.caseprofile.metadata.CreateEnrolledCensusFileIntakeManagementTabMetaData.INTAKE_PROFILE;
import static com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab.fileIntakeManagementTable;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGenerateAndUploadRatingCensusVerificationWhenMemberCompanyYesNo extends CaseProfileBaseTest implements LongTermDisabilityMasterPolicyContext,
        ShortTermDisabilityMasterPolicyContext, TermLifeInsuranceMasterPolicyContext, GroupDentalMasterPolicyContext, GroupVisionMasterPolicyContext {

    private static final File FILE_XLS = new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), "Ren_Enrollement_ALLProducts.xlsx");
    private static String SQL_GET_POLICY_INFO = "SELECT  policyNumber, memberCompanyName, LOB FROM PolicySummary WHERE policyNumber IN ('%s', '%s', '%s', '%s', '%s')";
    private static String SQL_GET_FILE_STAGE = "select id, fileName, status, subStatus  from ENROLLMENT_FILE_STAGE where id in " +
            "(select fileId from ENROLLMENT_GLOBAL_SEGMENT where nonIndCustomerId in ( '%s'));";

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-41175", component = Components.CASE_PROFILE)
    public void testGenerateAndUploadRatingCensusVerificationWhenMemberCompanyYes() {
        LOGGER.info("General preconditions");
        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(tdSpecific().getTestData("TestDataCustomer").resolveLinks()));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        String relationshipReference1 = RelationshipTab.getRelationShipCustomerReference(1);
        String relationshipReference2 = RelationshipTab.getRelationShipCustomerReference(2);

        createDefaultCaseProfile(
                shortTermDisabilityMasterPolicy.getType(),
                longTermDisabilityMasterPolicy.getType(),
                termLifeInsuranceMasterPolicy.getType(),
                groupDentalMasterPolicy.getType(),
                groupVisionMasterPolicy.getType());

        String relationship1 = tdSpecific().getValue("RelationshipTestData1", RelationshipTabMetaData.NAME_LEGAL.getLabel());
        String relationship2 = tdSpecific().getValue("RelationshipTestData2", RelationshipTabMetaData.NAME_LEGAL.getLabel());

        shortTermDisabilityMasterPolicy.createQuote(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(ShortTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(ShortTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationship1).resolveLinks());
        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.acceptContract().perform(getDefaultSTDMasterPolicyData());
        String quoteNumberSTD = PolicySummaryPage.labelPolicyNumber.getValue();

        longTermDisabilityMasterPolicy.createQuote(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(LongTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(LongTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationship2).resolveLinks());
        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.acceptContract().perform(getDefaultLTDMasterPolicyData());
        String quoteNumberLTD = PolicySummaryPage.labelPolicyNumber.getValue();

        termLifeInsuranceMasterPolicy.createQuote(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(TermLifeInsuranceMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(TermLifeInsuranceMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationship1).resolveLinks());
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.acceptContract().perform(getDefaultTLMasterPolicyData());
        String quoteNumberTL = PolicySummaryPage.labelPolicyNumber.getValue();

        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());
        String quoteNumberDN = PolicySummaryPage.labelPolicyNumber.getValue();

        groupVisionMasterPolicy.createQuote(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
        String quoteNumberVS = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step#1 verification");
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER);
        Efolder.expandFolder(EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName());
        efolder.addDocument(tdSpecific().getTestData("TestData_EnrollmentFileFail")
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.FILE_UPLOAD.getLabel()), String.format("$<file:%s>", "File_Test_Fail.xlsx"))
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.NAME.getLabel()), "File_Test_Fail.xlsx"), EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName());

        List<Map<String, String>> dbResponseListEnrollmentFileStageFailed = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_FILE_STAGE, customerId)),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        assertThat(dbResponseListEnrollmentFileStageFailed.stream().filter(x -> x.get("status").equals("FILE_STAGED")
                && x.get("subStatus").equals("VALIDATE_FAIL")).collect(Collectors.toList())).hasSize(1);

        LOGGER.info("Step#2 verification");
        List<Map<String, String>> dbResponseListPolicyInfo = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_POLICY_INFO, quoteNumberSTD, quoteNumberLTD, quoteNumberTL, quoteNumberDN, quoteNumberVS)),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(dbResponseListPolicyInfo.stream().filter(x -> x.get("policyNumber").equals(quoteNumberSTD)
                    && x.get("memberCompanyName").equals(relationship1)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListPolicyInfo.stream().filter(x -> x.get("policyNumber").equals(quoteNumberLTD)
                    && x.get("memberCompanyName").equals(relationship2)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListPolicyInfo.stream().filter(x -> x.get("policyNumber").equals(quoteNumberTL)
                    && x.get("memberCompanyName").equals(relationship1)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListPolicyInfo.stream().filter(x -> x.get("policyNumber").equals(quoteNumberDN))
                    .collect(Collectors.toList()).get(0).get("memberCompanyName")).isNull();
            softly.assertThat(dbResponseListPolicyInfo.stream().filter(x -> x.get("policyNumber").equals(quoteNumberVS))
                    .collect(Collectors.toList()).get(0).get("memberCompanyName")).isNull();
        });

        LOGGER.info("Steps#3, 4 verification");
        caseProfile.createEnrolledCensus().perform(caseProfile.getDefaultTestData("CreateEnrolledCensus", DEFAULT_TEST_DATA_KEY));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                .hasCellWithValue("Description", "Rating Census file from Policy Staging Table generation is failed with reason - No unprocessed data for Group Sponsor in Policy Staging Table");

        LOGGER.info("Step#5 verification");
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER);
        String filenameForUpdate = setValuesToXLSXFile(customerId, relationshipReference1, relationshipReference2);
        efolder.addDocument(efolder.getDefaultTestData(DATA_GATHER).getTestData("TestData_EnrollmentFile")
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.FILE_UPLOAD.getLabel()), String.format("$<file:%s>", filenameForUpdate))
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.NAME.getLabel()), filenameForUpdate), EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName());

        List<Map<String, String>> dbResponseListEnrollmentFileStage = RetryService.run(
                rows -> dbService.getRows(String.format(SQL_GET_FILE_STAGE, customerId)).size() > 1,
                () -> dbService.getRows(String.format(SQL_GET_FILE_STAGE, customerId)),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        assertThat(dbResponseListEnrollmentFileStage.get(1).get("status")).isEqualTo("RECORD_SYNC");
        assertThat(dbResponseListEnrollmentFileStage.get(1).get("subStatus")).isEqualTo("SYNC_SUCCESS");

        LOGGER.info("Steps#7, 8 verification");//TC6 was skipped due to minor sql verification
        caseProfile.createEnrolledCensus().start();
        caseProfile.createEnrolledCensus().getWorkspace().getTab(CreateEnrolledCensusFileIntakeManagementTab.class).fillTab(caseProfile.getDefaultTestData("CreateEnrolledCensus", DEFAULT_TEST_DATA_KEY));
        assertSoftly(softly -> {
            softly.assertThat(createEnrolledCensusTab.getAssetList().getAsset(INTAKE_PROFILE)).isDisabled();
            softly.assertThat(createEnrolledCensusTab.getAssetList().getAsset(GENERATE_AND_UPLOAD_CENSUS_FILE)).isDisabled();
        });
        caseProfile.createEnrolledCensus().close();

        LOGGER.info("Step#9 verification");
        String fileName1 = String.format("Enrolled Rating Census_%s", relationship1);
        String fileName2 = String.format("Enrolled Rating Census_%s", relationship2);
        String fileName3 = "UploadedFile";

        assertSoftly(softly -> {
            softly.assertThat(Efolder.isDocumentExistStartsContains(EfolderConstants.EFolderCase.NEW_BUSINESS_CENSUS_FILES.getName(), fileName1, ".xlsx"))
                    .withFailMessage("Generated document is absent in E-Folder").isTrue();
            softly.assertThat(Efolder.isDocumentExistStartsContains(EfolderConstants.EFolderCase.NEW_BUSINESS_CENSUS_FILES.getName(), fileName2, ".xlsx"))
                    .withFailMessage("Generated document is absent in E-Folder").isTrue();
            softly.assertThat(Efolder.isDocumentExistStartsContains(EfolderConstants.EFolderCase.NEW_BUSINESS_CENSUS_FILES.getName(), fileName3, ".xlsx"))
                    .withFailMessage("Generated document is absent in E-Folder").isTrue();
        });

        LOGGER.info("Step#10 verification");
        caseProfile.inquiry().start();
        NavigationPage.toMainTab(NavigationEnum.CaseProfileTab.FILE_INTAKE_MANAGEMENT.get());
        fileIntakeManagementTable.getColumn(TableConstants.FileIntakeManagement.INTAKE_PROFILE_NUMBER.getName()).getCell(1).controls.links.getFirst().click();
        NavigationPage.toMainTab(NavigationEnum.FileIntakeManagementTab.PROCESSING_RESULTS);
        assertThat(ProcessingResultsPage.tableProcessedFiles.getValue()).hasSize(4);

        LOGGER.info("Steps#11.1, 11.2 verification");
        Tab.buttonCancel.click();
        assertSoftly(softly -> {
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                    .hasCellWithValue("Description", String.format("Rating Census file from Policy Staging Table enrolled data file with the file name %s and 1 records in total has generated.", fileName1)); //failed by REN-47745
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                    .hasCellWithValue("Description", String.format("Rating Census file from Policy Staging Table enrolled data file with the file name %s and 1 records in total has generated.", fileName2));
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                    .hasCellWithValue("Description", String.format("Rating Census file from Policy Staging Table enrolled data file with the file name %s and 1 records in total has generated.", fileName3));
        });
    }

    private String setValuesToXLSXFile(String customerId, String relationshipReference1, String relationshipReference2) {
        ExcelTable excel = new ExcelFile(TestGenerateAndUploadRatingCensusVerificationWhenMemberCompanyYesNo.FILE_XLS).getSheet("Data").getTable(1);
        String ssn = RandomStringUtils.randomNumeric(9);
        String tempFileName = String.format("en_Enrollement_ALLProducts_%s.xlsx", ssn);

        excel.getCell(1, "GROUP SPONSOR ID").setValue(customerId);
        excel.getCell(7, "GROUP SPONSOR ID").setValue(customerId);
        excel.getCell(9, "GROUP SPONSOR ID").setValue(customerId);
        excel.getCell(11, "GROUP SPONSOR ID").setValue(customerId);
        excel.getCell(12, "GROUP SPONSOR ID").setValue(customerId);
        excel.getCell(15, "GROUP SPONSOR ID").setValue(customerId);
        excel.getRows().forEach(i -> i.getCell("ENROLLEE SSN").setValue(ssn));

        excel.getCell(2, "SUBSIDIARY ID").setValue(relationshipReference1);
        excel.getCell(3, "SUBSIDIARY ID").setValue(relationshipReference1);
        excel.getCell(4, "SUBSIDIARY ID").setValue(relationshipReference1);
        excel.getCell(5, "SUBSIDIARY ID").setValue(relationshipReference1);
        excel.getCell(6, "SUBSIDIARY ID").setValue(relationshipReference1);

        excel.getCell(9, "SUBSIDIARY ID").setValue(relationshipReference2);
        excel.getCell(10, "SUBSIDIARY ID").setValue(relationshipReference2);
        excel.getCell(11, "SUBSIDIARY ID").setValue(relationshipReference2);

        excel.saveAndClose(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), tempFileName));
        return tempFileName;
    }
}
