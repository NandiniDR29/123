package com.exigen.ren.modules.enrollment;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
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
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.pages.ProcessingResultsPage;
import com.exigen.ren.main.modules.caseprofile.tabs.CreateEnrolledCensusFileIntakeManagementTab;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.caseprofile.CaseProfileBaseTest;
import com.exigen.ren.utils.components.Components;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.module.efolder.EfolderContext.efolder;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.caseprofile.metadata.CreateEnrolledCensusFileIntakeManagementTabMetaData.*;
import static com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab.fileIntakeManagementTable;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestMappingOfCensusFromPolicyStagingTableVerification extends CaseProfileBaseTest implements LongTermDisabilityMasterPolicyContext,
        ShortTermDisabilityMasterPolicyContext, TermLifeInsuranceMasterPolicyContext, GroupDentalMasterPolicyContext, GroupVisionMasterPolicyContext {

    private static final File FILE_XLS = new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), "Ren_Enrollement_ALLProducts_3v.xlsx");

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-41516", component = Components.CASE_PROFILE)
    public void testMappingOfCensusFromPolicyStagingTableVerification() {
        LOGGER.info("General preconditions");
        String relationshipNameLegal = String.format("NameLegal_%s", RandomStringUtils.randomNumeric(9));

        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithOneNonIndRelationshipType")
                .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey(), RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER.getLabel()), "Member Company")
                .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey(), RelationshipTabMetaData.NAME_LEGAL.getLabel()), relationshipNameLegal));
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
        String relationshipReference1 = RelationshipTab.getRelationShipCustomerReference(1);

        createDefaultCaseProfile(
                shortTermDisabilityMasterPolicy.getType(),
                longTermDisabilityMasterPolicy.getType(),
                termLifeInsuranceMasterPolicy.getType(),
                groupDentalMasterPolicy.getType(),
                groupVisionMasterPolicy.getType());

        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationshipNameLegal).resolveLinks());
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());

        groupVisionMasterPolicy.createQuote(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestDataASO")
                .mask(TestData.makeKeyPath(GroupVisionMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.ELIGIBILITY.getLabel()))
                .adjust(TestData.makeKeyPath(GroupVisionMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(GroupVisionMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationshipNameLegal).resolveLinks());
        String quoteNumberVS = PolicySummaryPage.labelPolicyNumber.getValue();

        TestData tdPropose = groupVisionMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY);
        ProposalActionTab.performProposalForPolicyWithASO(GroupBenefitsMasterPolicyType.GB_VS, tdPropose, com.google.common.collect.ImmutableList.of(
                "Proposal for ASO Plan requires Underwriter approval"));
        MainPage.QuickSearch.search(quoteNumberVS);
        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());

        shortTermDisabilityMasterPolicy.createQuote(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(ShortTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(ShortTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationshipNameLegal).resolveLinks());
        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.acceptContract().perform(getDefaultSTDMasterPolicyData());

        longTermDisabilityMasterPolicy.createQuote(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(LongTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(LongTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationshipNameLegal).resolveLinks());
        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.acceptContract().perform(getDefaultLTDMasterPolicyData());

        termLifeInsuranceMasterPolicy.createQuote(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(TermLifeInsuranceMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(TermLifeInsuranceMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationshipNameLegal).resolveLinks());
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.acceptContract().perform(getDefaultTLMasterPolicyData());

        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());

        groupVisionMasterPolicy.createQuote(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());

        LOGGER.info("Steps#1, 2 verification");
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER);
        String filename = setValuesToXLSXFile(customerId, relationshipReference1);
        efolder.addDocument(efolder.getDefaultTestData(DATA_GATHER).getTestData("TestData_EnrollmentFile")
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.FILE_UPLOAD.getLabel()), String.format("$<file:%s>", filename))
                .adjust(TestData.makeKeyPath(AddFileTab.class.getSimpleName(), AddFileTabMetaData.NAME.getLabel()), filename), EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName());

        String fileNameFull = Efolder.getFileName("Enrollment Upload", customerName);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                .hasCellWithValue("Description", String.format("17 records have been processed in enrollment load sheet %s. 17 records passed validation and 0 records failed.", fileNameFull));

        LOGGER.info("TC#1 Steps#3, 4 verification");
        caseProfile.createEnrolledCensus().start();
        caseProfile.createEnrolledCensus().getWorkspace().getTab(CreateEnrolledCensusFileIntakeManagementTab.class).fillTab(caseProfile.getDefaultTestData("CreateEnrolledCensus", DEFAULT_TEST_DATA_KEY));
        assertSoftly(softly -> {
            softly.assertThat(createEnrolledCensusTab.getAssetList().getAsset(ERROR_MESSAGE)).isAbsent();
            softly.assertThat(createEnrolledCensusTab.getAssetList().getAsset(INTAKE_PROFILE)).isDisabled();
            softly.assertThat(createEnrolledCensusTab.getAssetList().getAsset(GENERATE_AND_UPLOAD_CENSUS_FILE)).isDisabled();
        });
        caseProfile.createEnrolledCensus().close();

        LOGGER.info("TC#1 Steps#5.1, 5,2 verification");
        String fileName1 = String.format("Enrolled Rating Census_%s", relationshipNameLegal);
        String fileName2 = "Enrolled Rating Census_NULL";

        assertSoftly(softly -> {
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                    .hasCellWithValue("Description", String.format("Rating Census file from Policy Staging Table with the name %s and 1 records in total is uploaded.", fileName1));
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2))
                    .hasCellWithValue("Description", String.format("Rating Census file from Policy Staging Table with the name %s and 1 records in total is uploaded.", fileName2));
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(3))
                    .hasCellWithValue("Description", String.format("Rating Census file from Policy Staging Table enrolled data file with the file name %s and 1 records in total has generated.", fileName1));
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(4))
                    .hasCellWithValue("Description", String.format("Rating Census file from Policy Staging Table enrolled data file with the file name %s and 1 records in total has generated.", fileName2));
        });

        LOGGER.info("TC#1 Step#6 verification");
        caseProfile.inquiry().start();
        NavigationPage.toMainTab(NavigationEnum.CaseProfileTab.FILE_INTAKE_MANAGEMENT.get());
        fileIntakeManagementTable.getColumn(TableConstants.FileIntakeManagement.INTAKE_PROFILE_NUMBER.getName()).getCell(1).controls.links.getFirst().click();
        NavigationPage.toMainTab(NavigationEnum.FileIntakeManagementTab.PROCESSING_RESULTS);
        assertThat(ProcessingResultsPage.tableProcessedFiles.getValue()).hasSize(3);
        Tab.buttonCancel.click();

        LOGGER.info("TC#1 Step#7 verification");
        assertThat(Efolder.isDocumentExistStartsContains(EfolderConstants.EFolderCase.NEW_BUSINESS_CENSUS_FILES.getName(), fileName1, ".xlsx"))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();
        assertThat(Efolder.isDocumentExistStartsContains(EfolderConstants.EFolderCase.NEW_BUSINESS_CENSUS_FILES.getName(), fileName2, ".xlsx"))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();

        LOGGER.info("TC#1 Step#8 verification");
        Efolder.downLoadFileByDescription(EfolderConstants.EFolderCase.NEW_BUSINESS_CENSUS_FILES.getName(), fileName1);
        Efolder.downLoadFileByDescription(EfolderConstants.EFolderCase.NEW_BUSINESS_CENSUS_FILES.getName(), fileName2);

        LOGGER.info("TC#2 Step#1 verification");
        String fileNameFullCensus = Efolder.getFileName(EfolderConstants.EFolderCase.NEW_BUSINESS_CENSUS_FILES.getName(), relationshipNameLegal);
        ExcelTable excelDownloaded = new ExcelFile((new File(PropertyProvider.getProperty(TestProperties.BROWSER_DOWNLOAD_FILES_LOCATION), fileNameFullCensus))).getSheet("Sheet0").getTable(1);

        assertSoftly(softly -> {
            softly.assertThat(excelDownloaded.getHeader().getColumnsNames())
                    .isEqualTo(ImmutableList.of(
                            "Census \"As of\" Date",
                            "Employee ID Number",
                            "Employee Gender",
                            "Employee Date of Birth",
                            "Employee Age",
                            "Smoker indicator",
                            "Occupation",
                            "Date of Hire",
                            "Employment Status",
                            "Hourly/Salaried",
                            "Active Status",
                            "Union/Non-Union",
                            "Full Time / Part Time",
                            "Retired",
                            "Retired Date",
                            "Earnings Mode",
                            "Life Earnings",
                            "Date of Disability",
                            "Disability Earnings",
                            "Residential Zip Code",
                            "Employee State of Residence",
                            "Subsidiary Code",
                            "Location Code",
                            "Employee Work Location Street Address",
                            "Employee Work Location City",
                            "Employee Work Location State",
                            "Employee Work Location Country",
                            "Employee Work Location Zip code",
                            "Spouse Date of Birth",
                            "Spouse Gender",
                            "Statutory Disability?",
                            "Statutory State",
                            "Basic Life Eligible?",
                            "Basic Life Elected?",
                            "Basic Life Plan",
                            "Basic Life Class",
                            "Basic Life Volume",
                            "Optional Life Eligible?",
                            "Optional Life Elected?",
                            "Optional Life Plan",
                            "Optional Life Class",
                            "Optional Life Volume",
                            "Voluntary Life Eligible?",
                            "Voluntary Life Elected?",
                            "Voluntary Life Plan",
                            "Voluntary Life Class",
                            "Voluntary Life Volume",
                            "Basic AD&D Eligible?",
                            "Basic AD&D Elected?",
                            "Basic AD&D Plan",
                            "Basic AD&D Class",
                            "Basic AD&D Volume",
                            "Optional AD&D Eligible?",
                            "Optional AD&D Elected?",
                            "Optional AD&D Plan",
                            "Optional AD&D Class",
                            "Optional AD&D Volume",
                            "Voluntary AD&D Eligible?",
                            "Voluntary AD&D Elected?",
                            "Voluntary AD&D Plan",
                            "Voluntary AD&D Class",
                            "Voluntary AD&D Volume",
                            "Dependent Basic Life Eligible?",
                            "Dependent Basic Life Elected?",
                            "Dependent Basic Life Plan",
                            "Dependent Basic Life Class",
                            "Dependent Basic Life Type",
                            "Dependent Basic Life Volume - Spouse",
                            "Dependent Basic Life Volume - Child",
                            "Dependent Basic AD&D Eligible?",
                            "Dependent Basic AD&D Elected?",
                            "Dependent Basic AD&D Plan",
                            "Dependent Basic AD&D Class",
                            "Dependent Basic AD&D Type",
                            "Dependent Basic AD&D Volume - Spouse",
                            "Dependent Basic AD&D Volume - Child",
                            "Spouse Optional Life Eligible?",
                            "Spouse Optional Life Elected?",
                            "Spouse Optional Life Plan",
                            "Spouse Optional Life Class",
                            "Spouse Optional Life Volume",
                            "Child Optional Life Eligible?",
                            "Child Optional Life Elected?",
                            "Child Optional Life Plan",
                            "Child Optional Life Class",
                            "Child Optional Life Volume",
                            "Spouse Voluntary Life Eligible?",
                            "Spouse Voluntary Life Elected?",
                            "Spouse Voluntary Life Plan",
                            "Spouse Voluntary Life Class",
                            "Spouse Voluntary Life Volume",
                            "Child Voluntary Life Eligible?",
                            "Child Voluntary Life Elected?",
                            "Child Voluntary Life Plan",
                            "Child Voluntary Life Class",
                            "Child Voluntary Life Volume",
                            "Dependent Optional AD&D Eligible?",
                            "Dependent Optional AD&D Elected?",
                            "Dependent Optional AD&D Plan",
                            "Dependent Optional AD&D Class",
                            "Dependent Optional AD&D Type",
                            "Dependent Optional AD&D Volume - Spouse",
                            "Dependent Optional AD&D Volume - Child",
                            "Dependent Voluntary AD&D Eligible?",
                            "Dependent Voluntary AD&D Elected?",
                            "Dependent Voluntary AD&D Plan",
                            "Dependent Voluntary AD&D Class",
                            "Dependent Voluntary AD&D Type",
                            "Dependent Voluntary AD&D Volume - Spouse",
                            "Dependent Voluntary AD&D Volume - Child",
                            "Family AD&D Eligible?",
                            "Family AD&D Elected?",
                            "Family AD&D Plan",
                            "Family AD&D Class",
                            "Family AD&D Type",
                            "Family AD&D Volume - Spouse",
                            "Family AD&D Volume - Child",
                            "Family AD&D Volume - Employee",
                            "LTD Eligible?",
                            "LTD Elected?",
                            "LTD Plan",
                            "LTD Class",
                            "LTD Volume",
                            "LTD Buy-up Eligible?",
                            "LTD Buy-up Elected?",
                            "LTD Buy-up Plan",
                            "LTD Buy-up Class",
                            "LTD Buy-up Volume",
                            "STD Eligible?",
                            "STD Elected?",
                            "STD Plan",
                            "STD Class",
                            "STD Volume",
                            "STD Buy-up Eligible?",
                            "STD Buy-up Elected?",
                            "STD Buy-up Plan",
                            "STD Buy-up Class",
                            "STD Buy-up Volume",
                            "Stat NY Eligible?",
                            "Stat NY Elected?",
                            "Stat NY Plan",
                            "Stat NY Class",
                            "Stat NY Volume",
                            "Stat NJ Eligible?",
                            "Stat NJ Elected?",
                            "Stat NJ Plan",
                            "Stat NJ Class",
                            "Stat NJ Volume",
                            "Stat HI Eligible?",
                            "Stat HI Elected?",
                            "Stat HI Plan",
                            "Stat HI Class",
                            "Stat HI Volume",
                            "Enhanced NY Eligible?",
                            "Enhanced NY Elected?",
                            "Enhanced NY Plan",
                            "Enhanced NY Class",
                            "Enhanced NY Volume",
                            "Vision Eligible?",
                            "Vision Elected?",
                            "Vision Plan",
                            "Vision Class",
                            "Vision Tier",
                            "Dental Eligible?",
                            "Dental Elected?",
                            "Dental Plan",
                            "Dental Class",
                            "Dental Tier"));

            LOGGER.info("TC#2 Step#2 verification");
            softly.assertThat(excelDownloaded.getCell(1, "Census \"As of\" Date").getColumn().getValues().get(0).toString())
                    .isEqualTo(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            LOGGER.info("TC#2 Step#3 verification");
            softly.assertThat(excelDownloaded.getCell(1, "Subsidiary Code").getColumn().getValues().get(0).toString())
                    .isEqualTo(relationshipReference1);
        });
    }

    private String setValuesToXLSXFile(String customerId, String relationshipReference) {
        ExcelTable excel = new ExcelFile(TestMappingOfCensusFromPolicyStagingTableVerification.FILE_XLS).getSheet("Data").getTable(1);
        String ssn = RandomStringUtils.randomNumeric(9);
        String tempFileName = String.format("Ren_Enrollement_ALLProducts_3v%s.xlsx", ssn);

        excel.getCell(1, "GROUP SPONSOR ID").setValue(customerId);
        excel.getCell(7, "GROUP SPONSOR ID").setValue(customerId);
        excel.getCell(9, "GROUP SPONSOR ID").setValue(customerId);
        excel.getCell(11, "GROUP SPONSOR ID").setValue(customerId);
        excel.getCell(12, "GROUP SPONSOR ID").setValue(customerId);
        excel.getCell(15, "GROUP SPONSOR ID").setValue(customerId);
        excel.getRows().forEach(i -> i.getCell("ENROLLEE SSN").setValue(ssn));
        excel.getRows().forEach(i -> i.getCell("SUBSIDIARY ID").setValue(relationshipReference));

        excel.getCell(7, "SUBSIDIARY ID").setValue(EMPTY);
        excel.getCell(8, "SUBSIDIARY ID").setValue(EMPTY);

        excel.saveAndClose(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION), tempFileName));
        return tempFileName;
    }
}
