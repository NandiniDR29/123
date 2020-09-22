package com.exigen.ren.modules.docgen.gb_dn;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonIndCustPolicyAndCert.CERTIFICATE;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderPolicy.POLICY_AND_CERT;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.PolicyConstants.NameToDisplayOnMPDocumentsValues.MEMBER_COMPANY;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_FALSE;
import static com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER;
import static com.exigen.ren.utils.DBHelper.EventName.REN_POLICY_ISSUE_CLASS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestPolicyMasterDocumentGenerationForGA extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37316", component = POLICY_GROUPBENEFITS)
    public void testPolicyMasterDocumentGenerationForGA() {
        final String SIX_MONTH = "6 months";
        final String TWICE = "twice";
        final String ONCE = "once";
        final String ANY_BENEFIT_YEAR = "any Benefit Year";
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(makeKeyPath(relationshipTab.getMetaKey() + "[1]", RELATIONSHIP_TO_CUSTOMER.getLabel()), MEMBER_COMPANY).resolveLinks());
        caseProfile.create(tdSpecific().getTestData("TestData_WithTwoPlans_NoAndYesSubGroups"), groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_Policy").resolveLinks())
                .adjust(tdSpecific().getTestData("TestData_Proposal").resolveLinks()));
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String groupName = PolicySummaryPage.labelCustomerName.getValue();

        LOGGER.info("TEST REN-37316: Step 1");
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().withDayOfMonth(1);
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        String documentName = String.format("%s_DENTAL_%s-Certificate-%s_%s_A La Carte_1_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, documentDate);
        assertThat(Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + CERTIFICATE.getName(), customerNumber + "_", documentName))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-37316: Step 2");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER, REN_POLICY_ISSUE_CLASS);

        //2-4
        xmlValidator.checkDocument(INCLUDES_DOMESTIC_PARTNER, VALUE_TRUE);
        //5
        xmlValidator.checkDocument(FLOURIDE_TREATMENT_COVERED, VALUE_TRUE);
        //6
        xmlValidator.checkDocument(FLOURIDE_TREATMENT_LIMIT, TWICE);
        xmlValidator.checkDocument(FLOURIDE_TREATMENT_FREQUENCY, ANY_BENEFIT_YEAR);
        //6a
        xmlValidator.checkDocument(ORAL_EXAMINATION_TREATMENT_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(ORAL_EXAMINATION_TREATMENT_LIMIT, TWICE);
        xmlValidator.checkDocument(ORAL_EXAMINATION_TREATMENT_FREQUENCY, ANY_BENEFIT_YEAR);
        //6b-6c
        xmlValidator.checkDocument(PROPHYLAXES_TREATMENT_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(PROPHYLAXES_TREATMENT_LIMIT, TWICE);
        xmlValidator.checkDocument(PROPHYLAXES_TREATMENT_FREQUENCY, ANY_BENEFIT_YEAR);
        //7
        xmlValidator.checkDocument(FLOURIDE_TREATMENT_AGE_LIMIT, "19");
        //7a
        xmlValidator.checkDocument(SPACE_MAINTAINERS_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(SPACE_MAINTAINERS_AGE_LIMIT, "14");
        //7b, 11, 14, 15
        xmlValidator.checkDocument(SEALANTS_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(SEALANTS_AGE_LIMIT, "16");
        //8
        xmlValidator.checkDocument(BRUSH_BIOPSY_COVERED, VALUE_TRUE);
        //10
        xmlValidator.checkDocument(FULL_MOUTH_RADIOGRAPHS_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(FULL_MOUTH_RADIOGRAPHS_LIMIT, ONCE);
        xmlValidator.checkDocument(FULL_MOUTH_RADIOGRAPHS_FREQUENCY, ANY_BENEFIT_YEAR);
        //10a, 12b
        xmlValidator.checkDocument(OCCLUSAL_ADJUSTMENTS_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(OCCLUSAL_ADJUSTMENTS_LIMIT, ONCE);
        xmlValidator.checkDocument(OCCLUSAL_ADJUSTMENTS_FREQUENCY, ANY_BENEFIT_YEAR);
        //12
        xmlValidator.checkDocument(SEALANTS_LIMIT, ONCE);
        xmlValidator.checkDocument(SEALANTS_FREQUENCY, ANY_BENEFIT_YEAR);
        //12a
        xmlValidator.checkDocument(RELINES_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(RELINES_LIMIT, ONCE);
        xmlValidator.checkDocument(RELINES_FREQUENCY, ANY_BENEFIT_YEAR);
        //13
        xmlValidator.checkDocument(CROWNS_LIMIT, ONCE);
        xmlValidator.checkDocument(CROWNS_FREQUENCY, ANY_BENEFIT_YEAR);
        //13a
        xmlValidator.checkDocument(DENTURES_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(DENTURES_LIMIT, ONCE);
        xmlValidator.checkDocument(DENTURES_FREQUENCY, ANY_BENEFIT_YEAR);
        //13b
        xmlValidator.checkDocument(BRIDGE_WORK_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(BRIDGE_WORK_LIMIT, ONCE);
        xmlValidator.checkDocument(BRIDGE_WORK_FREQUENCY, ANY_BENEFIT_YEAR);
        //13c
        xmlValidator.checkDocument(IMPLANTS_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(IMPLANTS_LIMIT, TWICE);
        xmlValidator.checkDocument(IMPLANTS_FREQUENCY, ANY_BENEFIT_YEAR);
        //16
        xmlValidator.checkDocument(ORTHO_AGE_LIMIT, "99");
        xmlValidator.checkDocument(ORTHO_AVAILABILITY, "Adult and Child");
        //30
        xmlValidator.checkDocument(POLICY_HOLDER_NAME, groupName);

        LOGGER.info("TEST REN-37316: Step 3");
        //1
        xmlValidator.checkDocument(CUSTOMER_NUMBER, customerNumber);
        //2
        xmlValidator.checkDocument(POLICY_HOLDER_NAME, groupName);
        //3
        xmlValidator.checkDocument(CURRENT_START_DT, policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //4
        xmlValidator.checkDocument(CURRENT_EN_DT, policyEffectiveDate.plusYears(1).minusDays(1).format(YYYY_MM_DD_HH_MM_SS_Z));
        //19a
        xmlValidator.checkDocument(ORTHO_WITH_CHILD, VALUE_FALSE);
        //19c
        xmlValidator.checkDocument(ORTHO_ADULT_AND_CHILD, VALUE_TRUE);
        //20
        xmlValidator.checkDocument(MAC_PLAN_IND, VALUE_TRUE);
        //21, 23
        xmlValidator.checkDocument(MAXIMUM_EXTENDER_IND, VALUE_FALSE);
        //22
        xmlValidator.checkDocument(PLAN_MAXIMUM_PAYS_IN_NETWORK_PAY, "$1,400");
        xmlValidator.checkDocument(PLAN_MAXIMUM_PAYS_OUT_OF_NETWORK_PAY, "$1,400");
        //24
        xmlValidator.checkDocument(ORTHO_APPLIED, VALUE_TRUE);
        //25
        xmlValidator.checkDocument(LIFETIME_MAXIMUM_PAYS_APPLIED, VALUE_FALSE);
        xmlValidator.checkDocument(LIFETIME_MAXIMUM_PAYS_IN_NETWORK_PAY, EMPTY);
        xmlValidator.checkDocument(LIFETIME_MAXIMUM_PAYS_OUT_OF_NETWORK_PAY, EMPTY);
        //26
        xmlValidator.checkDocument(MAXIMUM_ROLL_OVER_IND, VALUE_FALSE);
        //27
        xmlValidator.checkNodeNotPresent(ROLL_OVER_THRESHOLD);
        //28, 29
        xmlValidator.checkNodeNotPresent(ROLL_OVER_BENEFIT_LIMIT);
        //30
        xmlValidator.checkDocument(ANY_DED_APPLIED, VALUE_TRUE);
        xmlValidator.checkDocument(ALL_SAME_ON_IN_AND_OUT, VALUE_TRUE);
        xmlValidator.checkDocument(WITH_GRADED_YEARS_FOR_DED, VALUE_FALSE);
        xmlValidator.checkDocument(NUMBER_OF_GRADED_YEARS_FOR_DED, "0");
        //31
        xmlValidator.checkDocument(DEDUCTIBLE_PAYS_IN_NETWORK_PAY, "$50");
        xmlValidator.checkDocument(DEDUCTIBLE_PAYS_OUT_OF_NETWORK_PAY, "$50");
        //32
        xmlValidator.checkDocument(FAMILY_DED_PAYS_AMOUNT_IN_NETWORK_PAY, "$150");
        xmlValidator.checkDocument(FAMILY_DED_PAYS_AMOUNT_OUT_OF_NETWORK_PAY, "$150");
        //33
        xmlValidator.checkDocument(APPLIED_ITEMS_DESC, "Basic or Major");
        //34
        xmlValidator.checkDocument(WITH_NOT_APPLIED_DED, VALUE_TRUE);
        xmlValidator.checkDocument(NOT_APPLIED_ITEMS_DESC, "any Diagnostic & Preventive Services");
        //39
        xmlValidator.checkDocument(ANY_DED_APPLIED, VALUE_TRUE);
        //40
        xmlValidator.checkDocument(NEW_HIRE_WAITING_IND, VALUE_TRUE);
        //41-44
        xmlValidator.checkDocument(NEW_HIRE_WAITING_PERIOD, "FirstOfMonthCoincidentAmountAndMode");
        xmlValidator.checkDocument(ELIGIBILITY_WAITING_AMT_MODE, "12 days");
        //45
        xmlValidator.checkDocument(COVERED_SERVICE_WAITING_IND, VALUE_TRUE);
        //46-47
        xmlValidator.checkDocument(BENEFIT_WAITING_PERIOD, SIX_MONTH);
        xmlValidator.checkDocument(PREVENTIVE_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidator.checkDocument(PREVENTIVE_WAITING_PERIOD_VALUE, SIX_MONTH);
        xmlValidator.checkDocument(RADIOGRAPHS_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidator.checkDocument(RADIOGRAPHS_WAITING_PERIOD_VALUE, SIX_MONTH);
        xmlValidator.checkDocument(BASIC_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidator.checkDocument(BASIC_WAITING_PERIOD_VALUE, SIX_MONTH);
        xmlValidator.checkDocument(MAJOR_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidator.checkDocument(MAJOR_WAITING_PERIOD_VALUE, SIX_MONTH);
        xmlValidator.checkDocument(ORTHO_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidator.checkDocument(ORTHO_WAITING_PERIOD_VALUE, SIX_MONTH);
        //48
        xmlValidator.checkDocument(MISSING_TOOTH_IND, VALUE_FALSE);
        //49
        xmlValidator.checkDocument(CLASS_NAME, "1");
        //50
        xmlValidator.checkDocument(MIN_HOURS_PER_WEEK, "15");
        //51a, 54
        xmlValidator.checkDocument(DN_CERT_SUMMARY_INFO_ALLOW_MEMBER_ON_CERT, VALUE_FALSE);
        //52
        xmlValidator.checkDocument(INCLUDES_DOMESTIC_PARTNER, VALUE_TRUE);
        //53
        xmlValidator.checkDocument(CERT_DEP_MAX_AGE, "18");
        //58
        xmlValidator.checkDocument(BENEFIT_END_ON_CD, "LASTDATEOFEMPLOY");
        //59
        xmlValidator.checkDocument(DEPENDENT_INCLUDE_DOMESTIC, VALUE_TRUE);
    }
}

