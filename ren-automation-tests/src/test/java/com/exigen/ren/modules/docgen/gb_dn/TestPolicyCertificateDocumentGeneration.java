package com.exigen.ren.modules.docgen.gb_dn;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonIndCustPolicyAndCert.CERTIFICATE;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonIndCustomer.POLICY_AND_CERT;
import static com.exigen.ren.main.enums.PolicyConstants.NameToDisplayOnMPDocumentsValues.MEMBER_COMPANY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ALACARTE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_FALSE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_TRUE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.CLASSIFICATION_GROUP;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupPopup.CLASS_NUMBER;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.ActiveAndRecentlyExpiredPolicies.MEMBER_COMPANY_NAME;
import static com.exigen.ren.utils.DBHelper.EntityType.CUSTOMER;
import static com.exigen.ren.utils.DBHelper.EventName.REN_POLICY_ISSUE_CLASS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyCertificateDocumentGeneration extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-36453"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyCertificateDocumentGeneration() {
        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey() + "[1]", RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER.getLabel()), MEMBER_COMPANY).resolveLinks());
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        TestData tdPolicy = getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Policy").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_Proposal").resolveLinks()).resolveLinks();
        groupDentalMasterPolicy.createPolicy(tdPolicy);

        LOGGER.info("Step 1");
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        String classNumber = CaseProfileContext.getDefaultCaseProfileTestData(groupDentalMasterPolicy.getType()).getTestDataList(classificationManagementTab.getMetaKey()).get(0).getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NUMBER.getLabel());
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String documentName = String.format("%s_DENTAL_%s-Certificate-%s_%s_%s_%s_%s", customerNumber, customerName.toUpperCase(), policyEffectiveDate, policyNumber, ALACARTE, classNumber, documentDate);
        assertThat(Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + CERTIFICATE.getName(), customerNumber, documentName))
                .withFailMessage("Generated document is absent in E-Folder. The name of document should be: " + documentName).isTrue();

        LOGGER.info("Step 2");
        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, CUSTOMER, REN_POLICY_ISSUE_CLASS);

        XmlValidator xmlValidatorCertificateInfoSection = getNeededSection(xmlValidator, CERTIFICATE_INFO_SECTION_BY_PRODUCT_TYPE, "dn");
        //5
        xmlValidatorCertificateInfoSection.checkDocument(FLOURIDE_TREATMENT_COVERED, VALUE_TRUE);
        //6
        xmlValidatorCertificateInfoSection.checkDocument(FLOURIDE_TREATMENT_LIMIT, "twice");
        xmlValidatorCertificateInfoSection.checkDocument(FLOURIDE_TREATMENT_FREQUENCY, "any Benefit Year");
        //6a
        xmlValidatorCertificateInfoSection.checkDocument(ORAL_EXAMINATION_TREATMENT_COVERED, VALUE_TRUE);
        xmlValidatorCertificateInfoSection.checkDocument(ORAL_EXAMINATION_TREATMENT_LIMIT, "twice");
        xmlValidatorCertificateInfoSection.checkDocument(ORAL_EXAMINATION_TREATMENT_FREQUENCY, "any Benefit Year");
        //6b, 6c
        xmlValidatorCertificateInfoSection.checkDocument(PROPHYLAXES_TREATMENT_COVERED, VALUE_TRUE);
        xmlValidatorCertificateInfoSection.checkDocument(PROPHYLAXES_TREATMENT_LIMIT, "once");
        xmlValidatorCertificateInfoSection.checkDocument(PROPHYLAXES_TREATMENT_FREQUENCY, "any Benefit Year");
        //7
        xmlValidatorCertificateInfoSection.checkDocument(FLOURIDE_TREATMENT_AGE_LIMIT, "20");
        //7a
        xmlValidatorCertificateInfoSection.checkDocument(SPACE_MAINTAINERS_COVERED, VALUE_TRUE);
        xmlValidatorCertificateInfoSection.checkDocument(SPACE_MAINTAINERS_AGE_LIMIT, "14");
        //7b, 11, 12, 13, 14, 15,
        xmlValidatorCertificateInfoSection.checkDocument(SEALANTS_COVERED, VALUE_TRUE);
        xmlValidatorCertificateInfoSection.checkDocument(SEALANTS_LIMIT, "once");
        xmlValidatorCertificateInfoSection.checkDocument(SEALANTS_FREQUENCY, "any Benefit Year");
        xmlValidatorCertificateInfoSection.checkDocument(CROWNS_LIMIT, "once");
        xmlValidatorCertificateInfoSection.checkDocument(CROWNS_FREQUENCY, "any Benefit Year");
        xmlValidatorCertificateInfoSection.checkDocument(SEALANTS_AGE_LIMIT, "16");
        //8
        xmlValidatorCertificateInfoSection.checkDocument(BRUSH_BIOPSY_COVERED, VALUE_TRUE);
        //10
        xmlValidatorCertificateInfoSection.checkDocument(FULL_MOUTH_RADIOGRAPHS_COVERED, VALUE_TRUE);
        xmlValidatorCertificateInfoSection.checkDocument(FULL_MOUTH_RADIOGRAPHS_LIMIT, "once");
        xmlValidatorCertificateInfoSection.checkDocument(FULL_MOUTH_RADIOGRAPHS_FREQUENCY, "any Benefit Year");
        //10a, 12b
        xmlValidatorCertificateInfoSection.checkDocument(OCCLUSAL_ADJUSTMENTS_COVERED, VALUE_TRUE);
        xmlValidatorCertificateInfoSection.checkDocument(OCCLUSAL_ADJUSTMENTS_LIMIT, "once");
        xmlValidatorCertificateInfoSection.checkDocument(OCCLUSAL_ADJUSTMENTS_FREQUENCY, "any Benefit Year");
        //12a
        xmlValidatorCertificateInfoSection.checkDocument(RELINES_COVERED, VALUE_TRUE);
        xmlValidatorCertificateInfoSection.checkDocument(RELINES_LIMIT, "once");
        xmlValidatorCertificateInfoSection.checkDocument(RELINES_FREQUENCY, "any Benefit Year");
        //13a
        xmlValidatorCertificateInfoSection.checkDocument(DENTURES_COVERED, VALUE_TRUE);
        xmlValidatorCertificateInfoSection.checkDocument(DENTURES_LIMIT, "once");
        xmlValidatorCertificateInfoSection.checkDocument(DENTURES_FREQUENCY, "any Benefit Year");
        //13b
        xmlValidatorCertificateInfoSection.checkDocument(BRIDGE_WORK_COVERED, VALUE_TRUE);
        xmlValidatorCertificateInfoSection.checkDocument(BRIDGE_WORK_LIMIT, "once");
        xmlValidatorCertificateInfoSection.checkDocument(BRIDGE_WORK_FREQUENCY, "any Benefit Year");
        //13c
        xmlValidatorCertificateInfoSection.checkDocument(IMPLANTS_COVERED, VALUE_TRUE);
        xmlValidatorCertificateInfoSection.checkDocument(IMPLANTS_LIMIT, "once");
        xmlValidatorCertificateInfoSection.checkDocument(IMPLANTS_FREQUENCY, "any Benefit Year");
        //16
        xmlValidatorCertificateInfoSection.checkDocument(ORTHO_AGE_LIMIT, "15");
        xmlValidatorCertificateInfoSection.checkDocument(ORTHO_AVAILABILITY, "Child Only");

        XmlValidator xmlValidatorCertSummaryInfoSection = getNeededSection(xmlValidator, CERT_SUMMARY_INFO_SECTION_BY_PRODUCT_TYPE, "dn");
        //1
        xmlValidator.checkDocument(CUSTOMER_NUMBER, customerNumber);
        xmlValidator.checkDocument(PACKAGE_NAME, ALACARTE);
        //2
        String memberCompanyName = CustomerSummaryPage.tableActiveAndRecentlyExpiredPolicies.getRow(1).getCell(MEMBER_COMPANY_NAME.getName()).getValue();
        xmlValidator.checkDocument(POLICY_HOLDER_NAME, memberCompanyName);
        //3
        xmlValidatorCertSummaryInfoSection.checkDocument(WHOLE_CALENDAR_YEAR, VALUE_FALSE);
        LocalDateTime currentStartDate = TimeSetterUtil.getInstance().getCurrentTime().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        xmlValidatorCertSummaryInfoSection.checkDocument(CURRENT_START_DT, currentStartDate.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z));
        //4
        xmlValidatorCertSummaryInfoSection.checkDocument(CURRENT_EN_DT, currentStartDate.plusYears(1).minusDays(1).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z));
        //5
        xmlValidatorCertSummaryInfoSection.checkDocument(PREVENTIVE_COVERED_PAYS_IN_NETWORK_PAY, "90%");
        //6
        xmlValidatorCertSummaryInfoSection.checkDocument(PREVENTIVE_COVERED_PAYS_IN_NETWORK_CERT_PAY, "10%");
        //7
        xmlValidatorCertSummaryInfoSection.checkDocument(PREVENTIVE_COVERED_PAYS_OUT_OF_NETWORK_PAY, "90%");
        //8
        xmlValidatorCertSummaryInfoSection.checkDocument(PREVENTIVE_COVERED_PAYS_OUT_OF_NETWORK_CERT_PAY, "10%");
        //9
        xmlValidatorCertSummaryInfoSection.checkDocument(WITH_BRUSH_BIOPSY, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(BRUSH_BIOPSY_SERVICE_DETAILS_NAME, "Brush Biopsy");
        xmlValidatorCertSummaryInfoSection.checkDocument(BRUSH_BIOPSY_SERVICE_DETAILS_DESCRIPTION, "to detect oral cancer");
        //10
        xmlValidatorCertSummaryInfoSection.checkDocument(BRUSH_BIOPSY_COVERED_PAYS_IN_NETWORK_PAY, "90%");
        //11
        xmlValidatorCertSummaryInfoSection.checkDocument(BRUSH_BIOPSY_COVERED_PAYS_IN_NETWORK_CERT_PAY, "10%");
        //12
        xmlValidatorCertSummaryInfoSection.checkDocument(BRUSH_BIOPSY_COVERED_PAYS_OUT_OF_NETWORK_PAY, "90%");
        //13
        xmlValidatorCertSummaryInfoSection.checkDocument(BRUSH_BIOPSY_COVERED_PAYS_OUT_OF_NETWORK_CERT_PAY, "10%");
        //15
        getNeededSection(xmlValidatorCertSummaryInfoSection, BASIC_SERVICE_DETAILS_BY_NAME, "Emergency Palliative Treatment")
                .checkDocument(ITEM_DESCRIPTION, "to temporarily relieve pain");

        getNeededSection(xmlValidatorCertSummaryInfoSection, BASIC_SERVICE_DETAILS_BY_NAME, "Other Basic Services")
                .checkDocument(ITEM_DESCRIPTION, "misc. services");

        getNeededSection(xmlValidatorCertSummaryInfoSection, BASIC_SERVICE_DETAILS_BY_NAME, "Periodontic Services")
                .checkDocument(ITEM_DESCRIPTION, "to treat gum disease");

        getNeededSection(xmlValidatorCertSummaryInfoSection, BASIC_SERVICE_DETAILS_BY_NAME, "Minor Restorative Services")
                .checkDocument(ITEM_DESCRIPTION, "fillings");

        getNeededSection(xmlValidatorCertSummaryInfoSection, BASIC_SERVICE_DETAILS_BY_NAME, "Endodontic Services")
                .checkDocument(ITEM_DESCRIPTION, "root canals");

        getNeededSection(xmlValidatorCertSummaryInfoSection, BASIC_SERVICE_DETAILS_BY_NAME, "Oral Surgery Services")
                .checkDocument(ITEM_DESCRIPTION, "extractions and dental surgery");
        //16
        xmlValidatorCertSummaryInfoSection.checkDocument(BASIC_COVERED_PAYS_IN_NETWORK_PAY, "80%");
        //17
        xmlValidatorCertSummaryInfoSection.checkDocument(BASIC_COVERED_PAYS_IN_NETWORK_CERT_PAY, "20%");
        //18
        xmlValidatorCertSummaryInfoSection.checkDocument(BASIC_COVERED_PAYS_OUT_OF_NETWORK_PAY, "80%");
        //19
        xmlValidatorCertSummaryInfoSection.checkDocument(BASIC_COVERED_PAYS_OUT_OF_NETWORK_CERT_PAY, "20%");
        //21
        getNeededSection(xmlValidatorCertSummaryInfoSection, MAJOR_SERVICE_DETAILS_BY_DESCRIPTION, "includes exams and space maintainers")
                .checkDocument(ITEM_NAME, "Diagnostic and Preventive Services");

        getNeededSection(xmlValidatorCertSummaryInfoSection, MAJOR_SERVICE_DETAILS_BY_DESCRIPTION, "crowns and veneers")
                .checkDocument(ITEM_NAME, "Major Restorative Services");

        getNeededSection(xmlValidatorCertSummaryInfoSection, MAJOR_SERVICE_DETAILS_BY_DESCRIPTION, "to bridges and dentures")
                .checkDocument(ITEM_NAME, "Relines and Repairs");

        getNeededSection(xmlValidatorCertSummaryInfoSection, MAJOR_SERVICE_DETAILS_BY_DESCRIPTION, "bridges, implants, and dentures")
                .checkDocument(ITEM_NAME, "Prosthodontic Services");
        //22
        xmlValidatorCertSummaryInfoSection.checkDocument(MAJOR_COVERED_PAYS_IN_NETWORK_PAY, "60%");
        //23
        xmlValidatorCertSummaryInfoSection.checkDocument(MAJOR_COVERED_PAYS_IN_NETWORK_CERT_PAY, "40%");
        //24
        xmlValidatorCertSummaryInfoSection.checkDocument(MAJOR_COVERED_PAYS_OUT_OF_NETWORK_PAY, "60%");
        //25
        xmlValidatorCertSummaryInfoSection.checkDocument(MAJOR_COVERED_PAYS_OUT_OF_NETWORK_CERT_PAY, "40%");
        //27
        getNeededSection(xmlValidatorCertSummaryInfoSection, ORTHODONTIA_SERVICE_DETAILS_BY_NAME, "Orthodontic Services")
                .checkDocument(ITEM_DESCRIPTION, "braces (up to age 15)");
        //27a
        xmlValidatorCertSummaryInfoSection.checkDocument(ORTHO_WITH_CHILD, VALUE_TRUE);
        //27b
        xmlValidatorCertSummaryInfoSection.checkDocument(ORTHO_CHILD_MAXAGE, "15");
        //27c
        xmlValidatorCertSummaryInfoSection.checkDocument(ORTHO_ADULT_AND_CHILD, VALUE_FALSE);
        //28
        xmlValidatorCertSummaryInfoSection.checkDocument(ORTHODONTIA_COVERED_PAYS_IN_NETWORK_PAY, "60%");
        //29
        xmlValidatorCertSummaryInfoSection.checkDocument(ORTHODONTIA_COVERED_PAYS_IN_NETWORK_CERT_PAY, "40%");
        //30
        xmlValidatorCertSummaryInfoSection.checkDocument(ORTHODONTIA_COVERED_PAYS_OUT_OF_NETWORK_PAY, "60%");
        //31
        xmlValidatorCertSummaryInfoSection.checkDocument(ORTHODONTIA_COVERED_PAYS_OUT_OF_NETWORK_CERT_PAY, "40%");
        //32
        xmlValidatorCertSummaryInfoSection.checkDocument(MAC_PLAN_IND, VALUE_TRUE);
        //33, 34
        xmlValidatorCertSummaryInfoSection.checkDocument(MAXIMUM_EXTENDER_IND, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(EXPENSE_PERIOD, "Calendar Year");
        //33a
        xmlValidatorCertSummaryInfoSection.checkDocument(PLAN_MAXIMUM_PAYS_IN_NETWORK_PAY, "$1,400");
        xmlValidatorCertSummaryInfoSection.checkDocument(PLAN_MAXIMUM_PAYS_OUT_OF_NETWORK_PAY, "$1,400");
        //35
        xmlValidatorCertSummaryInfoSection.checkDocument(ORTHO_APPLIED, VALUE_TRUE);
        //36
        xmlValidatorCertSummaryInfoSection.checkDocument(LIFETIME_MAXIMUM_PAYS_APPLIED, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(LIFETIME_MAXIMUM_PAYS_IN_NETWORK_PAY, "$1,400");
        xmlValidatorCertSummaryInfoSection.checkDocument(LIFETIME_MAXIMUM_PAYS_OUT_OF_NETWORK_PAY, "$1,400");
        //37
        xmlValidatorCertSummaryInfoSection.checkDocument(MAXIMUM_ROLL_OVER_IND, VALUE_TRUE);
        //38
        xmlValidatorCertSummaryInfoSection.checkDocument(ROLL_OVER_THRESHOLD, "750.00");
        //39, 40
        xmlValidatorCertSummaryInfoSection.checkDocument(ROLL_OVER_BENEFIT_LIMIT, "1500.00");
        //41
        xmlValidatorCertSummaryInfoSection.checkDocument(ANY_DED_APPLIED, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(ALL_SAME_ON_IN_AND_OUT, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(WITH_GRADED_YEARS_FOR_DED, VALUE_FALSE);
        xmlValidatorCertSummaryInfoSection.checkDocument(NUMBER_OF_GRADED_YEARS_FOR_DED, "0");
        //42
        xmlValidatorCertSummaryInfoSection.checkDocument(DEDUCTIBLE_PAYS_IN_NETWORK_PAY, "$50");
        xmlValidatorCertSummaryInfoSection.checkDocument(DEDUCTIBLE_PAYS_OUT_OF_NETWORK_PAY, "$50");
        xmlValidatorCertSummaryInfoSection.checkDocument(LIFETIME_DED_PAYS_APPLIED, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(LIFETIME_DED_PAYS_IN_NETWORK_PAY, "$100");
        xmlValidatorCertSummaryInfoSection.checkDocument(LIFETIME_DED_PAYS_OUT_OF_NETWORK_PAY, "$100");
        //43
        xmlValidatorCertSummaryInfoSection.checkDocument(FAMILY_DED_PAYS_AMOUNT_IN_NETWORK_PAY, "$150");
        xmlValidatorCertSummaryInfoSection.checkDocument(FAMILY_DED_PAYS_AMOUNT_OUT_OF_NETWORK_PAY, "$150");
        //44
        xmlValidatorCertSummaryInfoSection.checkDocument(APPLIED_ITEMS_DESC, "Basic or Major");
        //45
        xmlValidatorCertSummaryInfoSection.checkDocument(WITH_NOT_APPLIED_DED, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(NOT_APPLIED_ITEMS_DESC, "any Diagnostic & Preventive Services");
        //52
        xmlValidatorCertSummaryInfoSection.checkDocument(NEW_HIRE_WAITING_IND, VALUE_TRUE);
        //53
        xmlValidatorCertSummaryInfoSection.checkDocument(NEW_HIRE_WAITING_PERIOD, ValueConstants.NONE);
        //57, 59
        xmlValidatorCertSummaryInfoSection.checkDocument(COVERED_SERVICE_WAITING_IND, VALUE_TRUE);
        //58
        xmlValidatorCertSummaryInfoSection.checkDocument(SAME_WAITING_IND, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(BENEFIT_WAITING_PERIOD, "6 months");
        xmlValidatorCertSummaryInfoSection.checkDocument(PREVENTIVE_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(PREVENTIVE_WAITING_PERIOD_VALUE, "6 months");
        xmlValidatorCertSummaryInfoSection.checkDocument(RADIOGRAPHS_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(RADIOGRAPHS_WAITING_PERIOD_VALUE, "6 months");
        xmlValidatorCertSummaryInfoSection.checkDocument(BASIC_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(BASIC_WAITING_PERIOD_VALUE, "6 months");
        xmlValidatorCertSummaryInfoSection.checkDocument(MAJOR_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(MAJOR_WAITING_PERIOD_VALUE, "6 months");
        xmlValidatorCertSummaryInfoSection.checkDocument(ORTHO_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidatorCertSummaryInfoSection.checkDocument(ORTHO_WAITING_PERIOD_VALUE, "6 months");
        //61
        xmlValidatorCertSummaryInfoSection.checkDocument(MISSING_TOOTH_IND, VALUE_TRUE);
        //62
        xmlValidator.checkDocument(CLASS_NAME, "Employment");
        //63
        xmlValidatorCertSummaryInfoSection.checkDocument(MIN_HOURS_PER_WEEK, "10");
        //64
        xmlValidatorCertSummaryInfoSection.checkDocument(NOT_ONLY_EEO, VALUE_TRUE);
        //64a, 67
        xmlValidatorCertSummaryInfoSection.checkDocument(ALLOW_MEMBER_ON_CERT, VALUE_TRUE);
        //65
        xmlValidatorCertSummaryInfoSection.checkDocument(INCLUDES_DOMESTIC_PARTNER, VALUE_TRUE);
        //66
        xmlValidatorCertSummaryInfoSection.checkDocument(CERT_DEP_MAX_AGE, "18");
        //71
        xmlValidatorCertSummaryInfoSection.checkDocument(BENEFIT_END_ON_CD, "LASTDAYOFMONTH");
        //72
        xmlValidatorCertSummaryInfoSection.checkDocument(DEPENDENT_INCLUDE_DOMESTIC, VALUE_TRUE);
    }
}
