package com.exigen.ren.modules.docgen.gb_vs;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.EfolderConstants.DocumentTypes.CERTIFICATE;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderPolicy.POLICY_AND_CERT;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.PolicyConstants.PlanVision.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_FALSE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_TRUE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.CLASSIFICATION_GROUP;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupPopup.CLASS_NUMBER;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionIssueActionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.DBHelper.EventName.REN_POLICY_ISSUE_CLASS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static org.assertj.core.api.Assertions.assertThat;

public class TestPolicyCertificateDocumentForVision extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    private static final String CERTIFICATE_DATA_SOURCE_VISION = "ren-docgen-mp-certificate-doc-data-source/vision";
    private static final String CERTIFICATE_DATA_SOURCE = "ren-docgen-mp-certificate-doc-data-source";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-42561"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyCertificateDocumentForVisionUT() {
        mainApp().open();

        LOGGER.info("TEST REN-42561: Precondition");
        createDefaultNonIndividualCustomer();
        TestData tdCase = caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup");
        caseProfile.create(tdCase, groupVisionMasterPolicy.getType());
        TestData tdPolicy = tdSpecific().getTestData("TestData_Plan_Alacarte")
                .adjust(makeKeyPath(GroupVisionMasterPolicyContext.policyInformationTab.getClass().getSimpleName(), SITUS_STATE.getLabel()), "UT")
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(tdSpecific().getTestData("TestData_Issue"));
        groupVisionMasterPolicy.createPolicy(tdPolicy);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String groupName = PolicySummaryPage.labelCustomerName.getValue();

        LOGGER.info("TEST REN-42561: Step 1");
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay();
        String classNumber = tdCase.getTestDataList(classificationManagementTab.getMetaKey()).get(0).getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NUMBER.getLabel());
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        String documentName = String.format("%s_VISION_%s-Certificate-%s_%s_%s_%s_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, A_LA_CARTE, classNumber, documentDate);
        assertThat(Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + "Certificate", customerNumber + "_", documentName))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-42561: Step 2");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER, REN_POLICY_ISSUE_CLASS);

        //1
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE, SITUS_STATE_VALUE.get()), "UT");
        //6-8, 59
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, INCLUDES_DOMESTIC_PARTNER.get()), VALUE_TRUE);

        LOGGER.info("TEST REN-42561: Step 3");
        //1, 6
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DocGenEnum.AllSections.NETWORK.get()), "Choice");
        //2
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DocGenEnum.AllSections.PLAN_NAME.get()), A_LA_CARTE);
        //3
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE, POLICY_HOLDER_NAME.get()), customerName);
        //4
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), WHOLE_CALENDAR_YEAR.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), CURRENT_START_DT.get()), policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //5
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), CURRENT_EN_DT.get()), policyEffectiveDate.plusYears(1).minusDays(1).format(YYYY_MM_DD_HH_MM_SS_Z));
        //7
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, COMBINED_COPAY_COVERED.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE3.get()), "$5");
        //12
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_UP_TO.get()), "45.00");
        //13, 45
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE1.get()), "12");
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, FREQUENCY_IN_PDF.get()), "calendar months");
        //14, 26, 29, 30, 33, 47, 51, 52, 54, 55
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE2.get()), "24");
        //16
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_SINGLE_VISION_LENSES.get()), "30.00");
        //18
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LINED_BIFOCAL_LENSES.get()), "50.00");
        //20
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LINED_TRIFOCAL_LENSES.get()), "65.00");
        //22
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LENTICULAR_LENSES.get()), "100.00");
        //24
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, FRAME_UP_TO.get()), "70.00");
        //25, 49
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE3.get()), "24");
        //28
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE2.get()), "$210");
        //31, 50
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACT_LENSES_ALLOWANCE.get()), "$130");
        //32
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE1.get()), "$105");
        //40
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_SCRATCH_COATING_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_SAFETY_GLASSES_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_PHOTOCHROMIC_LENSES_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, PROGRESSIVE_LENSES_IN_FULL.get()), VALUE_FALSE);
        //42, REN-42561: Step 4.3
        //Value from PlanDefinitionTab -> Copay->Exam/Materials. If value like '$5 combined' then shuld be set to $5
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE1.get()), "$5");
        //43
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE2.get()), "$5");
        //53
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE2.get()), "$210");
        //56
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CLASS_NAME.get()), classNumber);
        //57
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, MIN_HOURS_PER_WEEK.get()),
                tdSpecific().getTestData("PlanDefinitionIssueActionTabALACARTE").getValue(MINIMUM_HOURLY_REQUIREMENT.getLabel()));
        //58, 61
        // Value from PlanDefinitionTab -> Eligibility -> Allow Member and Spouse (Who are part of Group) on Separate Certificate?
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, ALLOW_MEMBER_ON_CERT.get()), VALUE_FALSE);
        //59
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, INCLUDES_DOMESTIC_PARTNER.get()), VALUE_TRUE);
        //60
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DEPENDENT_MAX_AGE.get()),
                tdPolicy.getTestDataList(planDefinitionTab.getMetaKey()).get(1).getValue(ELIGIBILITY.getLabel(), DEPENDENT_MAXIMUM_AGE.getLabel()));
        //62
        // Value from PlanDefinitionTab -> Eligibility -> Benefits End On
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), BENEFIT_END_ON_CD.get()), "LASTDATEOFEMPLOY");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-46855"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyCertificateDocumentForVisionTX() {
        mainApp().open();

        LOGGER.info("TEST REN-46855: Precondition");
        createDefaultNonIndividualCustomer();
        TestData tdCase = caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_NoSubGroups");
        caseProfile.create(tdCase, groupVisionMasterPolicy.getType());
        TestData tdPolicy = tdSpecific().getTestData("TestData_Plan_Alacarte")
                .adjust(makeKeyPath(GroupVisionMasterPolicyContext.policyInformationTab.getClass().getSimpleName(), SITUS_STATE.getLabel()), "TX")
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(tdSpecific().getTestData("TestData_Issue"));
        groupVisionMasterPolicy.createPolicy(tdPolicy);

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String groupName = PolicySummaryPage.labelCustomerName.getValue();

        LOGGER.info("TEST REN-46855: Step 1");
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay();
        String classNumber = tdCase.getTestDataList(classificationManagementTab.getMetaKey()).get(0).getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NUMBER.getLabel());
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        String documentName = String.format("%s_VISION_%s-Certificate-%s_%s_%s_%s_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, A_LA_CARTE, "1", documentDate);
        RetryService.run(predicate -> Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + CERTIFICATE, customerNumber + "_", documentName),
                () -> {
                    BrowserController.get().driver().navigate().refresh();
                    return null;
                },
                StopStrategies.stopAfterAttempt(5),
                WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-46855: Step 2");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER, REN_POLICY_ISSUE_CLASS);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE, SITUS_STATE_VALUE.get()), "TX");

        LOGGER.info("TEST REN-46855: Step 3");
        //1, 6
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DocGenEnum.AllSections.NETWORK.get()), "Choice");
        //2
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DocGenEnum.AllSections.PLAN_NAME.get()), A_LA_CARTE);
        //3
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE, POLICY_HOLDER_NAME.get()), customerName);
        //4
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), WHOLE_CALENDAR_YEAR.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), CURRENT_START_DT.get()), policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //5
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), CURRENT_EN_DT.get()), policyEffectiveDate.plusYears(1).minusDays(1).format(YYYY_MM_DD_HH_MM_SS_Z));
        //7
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, COMBINED_COPAY_COVERED.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE3.get()), "$5");
        //12
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_UP_TO.get()), "45.00");
        //13, 45
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE1.get()), "12");
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, FREQUENCY_IN_PDF.get()), "calendar months");
        //14, 26, 29, 30, 33, 47, 51, 52, 54, 55
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE2.get()), "24");
        //16
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_SINGLE_VISION_LENSES.get()), "30.00");
        //18
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LINED_BIFOCAL_LENSES.get()), "50.00");
        //20
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LINED_TRIFOCAL_LENSES.get()), "65.00");
        //22
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LENTICULAR_LENSES.get()), "100.00");
        //24
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, FRAME_UP_TO.get()), "70.00");
        //25, 49
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE3.get()), "24");
        //28
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE2.get()), "$210");
        //31, 50
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACT_LENSES_ALLOWANCE.get()), "$130");
        //32
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE1.get()), "$105");
        //40
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_SCRATCH_COATING_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_SAFETY_GLASSES_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_PHOTOCHROMIC_LENSES_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, PROGRESSIVE_LENSES_IN_FULL.get()), VALUE_FALSE);
        //42, REN-46855: Step 4.3
        //Value from PlanDefinitionTab -> Copay->Exam/Materials. If value like '$5 combined' then shuld be set to $5
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE1.get()), "$5");
        //43
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE2.get()), "$5");
        //53
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE2.get()), "$210");
        //56
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CLASS_NAME.get()), classNumber);
        //57
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, MIN_HOURS_PER_WEEK.get()),
                tdSpecific().getTestData("PlanDefinitionIssueActionTabALACARTE").getValue(MINIMUM_HOURLY_REQUIREMENT.getLabel()));
        //58, 61
        // Value from PlanDefinitionTab -> Eligibility -> Allow Member and Spouse (Who are part of Group) on Separate Certificate?
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, ALLOW_MEMBER_ON_CERT.get()), VALUE_FALSE);
        //59, 63
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, INCLUDES_DOMESTIC_PARTNER.get()), VALUE_TRUE);
        //60
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DEPENDENT_MAX_AGE.get()),
                tdPolicy.getTestDataList(planDefinitionTab.getMetaKey()).get(1).getValue(ELIGIBILITY.getLabel(), DEPENDENT_MAXIMUM_AGE.getLabel()));
        //62
        // Value from PlanDefinitionTab -> Eligibility -> Benefits End On
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), BENEFIT_END_ON_CD.get()), "LASTDATEOFEMPLOY");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-45806"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyCertificateDocumentForVisionCO() {
        mainApp().open();

        LOGGER.info("TEST REN-45806: Precondition");
        createDefaultNonIndividualCustomer();
        TestData tdCase = caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup");
        caseProfile.create(tdCase, groupVisionMasterPolicy.getType());
        TestData tdPolicy = tdSpecific().getTestData("TestData_Plan_Alacarte")
                .adjust(makeKeyPath(GroupVisionMasterPolicyContext.policyInformationTab.getClass().getSimpleName(), SITUS_STATE.getLabel()), "CO")
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(tdSpecific().getTestData("TestData_Issue"));
        groupVisionMasterPolicy.createPolicy(tdPolicy);

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String groupName = PolicySummaryPage.labelCustomerName.getValue();

        LOGGER.info("TEST REN-45806: Step 1");
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay();
        String classNumber = tdCase.getTestDataList(classificationManagementTab.getMetaKey()).get(0).getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NUMBER.getLabel());
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        String documentName = String.format("%s_VISION_%s-Certificate-%s_%s_%s_%s_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, A_LA_CARTE, "1", documentDate);
        RetryService.run(predicate -> Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + CERTIFICATE, customerNumber + "_", documentName),
                () -> {
                    BrowserController.get().driver().navigate().refresh();
                    return null;
                },
                StopStrategies.stopAfterAttempt(5),
                WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-45806: Step 2");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER, REN_POLICY_ISSUE_CLASS);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE, SITUS_STATE_VALUE.get()), "CO");

        LOGGER.info("TEST REN-45806: Step 3");
        //1, 6
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DocGenEnum.AllSections.NETWORK.get()), "Choice");
        //2
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DocGenEnum.AllSections.PLAN_NAME.get()), A_LA_CARTE);
        //3
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE, POLICY_HOLDER_NAME.get()), customerName);
        //4
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), WHOLE_CALENDAR_YEAR.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), CURRENT_START_DT.get()), policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //5
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), CURRENT_EN_DT.get()), policyEffectiveDate.plusYears(1).minusDays(1).format(YYYY_MM_DD_HH_MM_SS_Z));
        //7
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, COMBINED_COPAY_COVERED.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE3.get()), "$5");
        //12
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_UP_TO.get()), "45.00");
        //13, 45
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE1.get()), "12");
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, FREQUENCY_IN_PDF.get()), "calendar months");
        //14, 26, 29, 30, 33, 47, 51, 52, 54, 55
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE2.get()), "24");
        //16
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_SINGLE_VISION_LENSES.get()), "30.00");
        //18
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LINED_BIFOCAL_LENSES.get()), "50.00");
        //20
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LINED_TRIFOCAL_LENSES.get()), "65.00");
        //22
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LENTICULAR_LENSES.get()), "100.00");
        //24
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, FRAME_UP_TO.get()), "70.00");
        //25, 49
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE3.get()), "24");
        //28
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE2.get()), "$210");
        //31, 50
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACT_LENSES_ALLOWANCE.get()), "$130");
        //32
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE1.get()), "$105");
        //40
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_SCRATCH_COATING_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_SAFETY_GLASSES_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_PHOTOCHROMIC_LENSES_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, PROGRESSIVE_LENSES_IN_FULL.get()), VALUE_FALSE);
        //42, REN-46855: Step 4.3
        //Value from PlanDefinitionTab -> Copay->Exam/Materials. If value like '$5 combined' then shuld be set to $5
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE1.get()), "$5");
        //43
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE2.get()), "$5");
        //53
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE2.get()), "$210");
        //56
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CLASS_NAME.get()), classNumber);
        //57
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, MIN_HOURS_PER_WEEK.get()),
                tdSpecific().getTestData("PlanDefinitionIssueActionTabALACARTE").getValue(MINIMUM_HOURLY_REQUIREMENT.getLabel()));
        //58, 61
        // Value from PlanDefinitionTab -> Eligibility -> Allow Member and Spouse (Who are part of Group) on Separate Certificate?
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, ALLOW_MEMBER_ON_CERT.get()), VALUE_FALSE);
        //59, 63
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, INCLUDES_DOMESTIC_PARTNER.get()), VALUE_TRUE);
        //60
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DEPENDENT_MAX_AGE.get()),
                tdPolicy.getTestDataList(planDefinitionTab.getMetaKey()).get(1).getValue(ELIGIBILITY.getLabel(), DEPENDENT_MAXIMUM_AGE.getLabel()));
        //62
        // Value from PlanDefinitionTab -> Eligibility -> Benefits End On
        xmlValidator.checkDocument(String.format("%s%s%s", CERTIFICATE_DATA_SOURCE_VISION, PLAN_COMMON_DATA.get(), BENEFIT_END_ON_CD.get()), "LASTDATEOFEMPLOY");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-45803"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyCertificateDocumentForVisionNY() {
        mainApp().open();

        LOGGER.info("TEST REN-45803: Precondition");
        createDefaultNonIndividualCustomer();
        TestData tdCase = caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup");
        caseProfile.create(tdCase, groupVisionMasterPolicy.getType());
        TestData tdPolicy = tdSpecific().getTestData("TestData_Plan_B")
                .adjust(makeKeyPath(GroupVisionMasterPolicyContext.policyInformationTab.getClass().getSimpleName(), SITUS_STATE.getLabel()), "NY")
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(tdSpecific().getTestData("TestData_Issue_PlanB"));
        groupVisionMasterPolicy.createPolicy(tdPolicy);

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String groupName = PolicySummaryPage.labelCustomerName.getValue();

        LOGGER.info("TEST REN-45803: Step 1");
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay();
        String classNumber = tdCase.getTestDataList(classificationManagementTab.getMetaKey()).get(0).getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NUMBER.getLabel());
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        String documentName = String.format("%s_VISION_%s-Certificate-%s_%s_%s_%s_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, "10 25 Plan B 130", classNumber, documentDate);
        RetryService.run(predicate -> Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + CERTIFICATE, customerNumber + "_", documentName),
                () -> {
                    BrowserController.get().driver().navigate().refresh();
                    return null;
                },
                StopStrategies.stopAfterAttempt(5),
                WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-45803: Step 2");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER, REN_POLICY_ISSUE_CLASS);
        //1
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE, SITUS_STATE_VALUE.get()), "NY");
        //3
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DocGenEnum.AllSections.NETWORK.get()), "Choice");
        //19, 47
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DEPENDENT_MAX_AGE.get()),
                tdPolicy.getTestDataList(planDefinitionTab.getMetaKey()).get(1).getValue(ELIGIBILITY.getLabel(), DEPENDENT_MAXIMUM_AGE.getLabel()));
        //22
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, INCLUDES_DOMESTIC_PARTNER.get()), VALUE_TRUE);
        //24
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE1.get()), "12");
        //25
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE2.get()), "12");
        //26
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE3.get()), "12");
        //31
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_SCRATCH_COATING_FACTOR.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_SAFETY_GLASSES_FACTOR.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EMPTY_PHOTOCHROMIC_LENSES_FACTOR.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, PROGRESSIVE_LENSES_IN_FULL.get()), VALUE_FALSE);
        //52
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE, NEXT_RENEWAL_EFF_DATE.get()), policyEffectiveDate.plusYears(1).format(YYYY_MM_DD_HH_MM_SS_Z));

        LOGGER.info("TEST REN-45803: Step 3");
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE1.get()), "$10");

        LOGGER.info("TEST REN-45803: Step 4");
        //2
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, DocGenEnum.AllSections.PLAN_NAME.get()), PlanB);
        //3
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE, POLICY_HOLDER_NAME.get()), customerName);
        //5
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, COMBINED_COPAY_COVERED.get()), VALUE_FALSE);
        //6
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, SEPARATE_COPAY_COVERED.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE1.get()), "$10");
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE2.get()), "$25");
        //7
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, SEPARATE_COPAY_NO_EXAM.get()), VALUE_FALSE);
        //9
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, COMBINED_COPAY_NOT_COVERED.get()), VALUE_FALSE);
        //10 N/A
        //11
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_MATERIALS_FIELD_VALUE2.get()), "$25");
        //12
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_UP_TO.get()), "45.00");
        //14 MP > Plan Definition > Frequency > Frequency Definition
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, FREQUENCY_IN_EIS.get()), "Calendar Year");
        //MP > Plan Definition > Frequency > Exam/Lenses/Frame
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, EXAM_LENSES_FRAME_FIELD_VALUE1.get()), "12");
        //15 N/A
        //17 MP > Plan Definitions > Out of Network Coverage > Single Vision Lenses - Up to
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_SINGLE_VISION_LENSES.get()), "30.00");
        //22 MP > Plan Definitions > Out of Network Coverage > Lined Bifocal Lenses - Up to
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LINED_BIFOCAL_LENSES.get()), "50.00");
        //27 MP > Plan Definitions > Out of Network Coverage > Lined Trifocal Lenses - Up to
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LINED_TRIFOCAL_LENSES.get()), "65.00");
        //32 MP > Plan Definitions > Out of Network Coverage > Lenticular Lenses - Up to
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, VIS14_LENTICULAR_LENSES.get()), "100.00");
        //37 MP > Plan Definitions > Out of Network Coverage > Frame - Up to
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, FRAME_UP_TO.get()), "70.00");
        //42 MP > Plan Definitions  > Out of Network Coverage > Contacts - Up to
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE2.get()), "$210");
        //50 MP > Plan Definitions  > Out of Network Coverage > Contacts - Up to
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACTS_UP_TO_FIELD_VALUE1.get()), "$105");
        //76 MP > Plan Definition > Plan benefits  > Contact Lenses - Allowance up to
        //   MP > Plan Definition > Plan benefits  > Frames - Allowance up to
        xmlValidator.checkDocument(String.format("%s%s", CERTIFICATE_DATA_SOURCE_VISION, CONTACT_LENSES_ALLOWANCE.get()), "$150");

        //Step 5 the same as Step 3
    }
}
