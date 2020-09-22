package com.exigen.ren.modules.docgen.gb_dn;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab;
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
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.MAIL_CARDS_TO;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonIndCustomer.POLICY_AND_CERT;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupDentalCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.NameToDisplayOnMPDocumentsValues.MEMBER_COMPANY;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_FALSE;
import static com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.planTierAndRatingInfoTable;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.ActiveAndRecentlyExpiredPolicies.POLICY;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.MasterQuote.MEMBER_COMPANY_NAME;
import static com.exigen.ren.utils.DBHelper.EventName.REN_POLICY_ISSUE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestPolicyMasterDocumentGenerationForIA extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-41248", component = POLICY_GROUPBENEFITS)
    public void testPolicyMasterDocumentGenerationForIA() {
        final String TWELVE_MONTH = "12 months";
        final String ANY_2_YEAR_PERIOD = "any 2 year period";
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

        groupDentalMasterPolicy.policyInquiry().start();
        classificationManagementMpTab.navigate();
        String employeePlusOneRate = new Currency(planTierAndRatingInfoTable.getRow(ClassificationManagementTab.PlanTierAndRatingSelection.COVERAGE_TIER.getName(), EMPLOYEE_ONE)
                .getCell(ClassificationManagementTab.PlanTierAndRatingSelection.RATE.getName()).getValue()).toPlainString();
        String employeePlusFamilyRate = new Currency(planTierAndRatingInfoTable.getRow(ClassificationManagementTab.PlanTierAndRatingSelection.COVERAGE_TIER.getName(), EMPLOYEE_FAMILY)
                .getCell(ClassificationManagementTab.PlanTierAndRatingSelection.RATE.getName()).getValue()).toPlainString();
        String employeeOnlyRate = new Currency(planTierAndRatingInfoTable.getRow(ClassificationManagementTab.PlanTierAndRatingSelection.COVERAGE_TIER.getName(), EMPLOYEE_ONLY)
                .getCell(ClassificationManagementTab.PlanTierAndRatingSelection.RATE.getName()).getValue()).toPlainString();

        LOGGER.info("TEST REN-41248: Step 1");
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String memberCompanyName = CustomerSummaryPage.tableActiveAndRecentlyExpiredPolicies.getRow(1).getCell(MEMBER_COMPANY_NAME.getName()).getValue();
        LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().withDayOfMonth(1);
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        String documentName = String.format("%s_DENTAL_%s-Policy-%s_%s_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, documentDate);
        assertThat(Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + POLICY.getName(), customerNumber + "_", documentName))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-41248: Step 2");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER, REN_POLICY_ISSUE);

        //4a
        xmlValidator.checkDocument(DEPENDENT_MAX_AGE, "18");
        //4a
        xmlValidator.checkDocument(INCLUDE_DISABLED_DEP, VALUE_TRUE);
        //5
        xmlValidator.checkDocument(INCLUDES_DOMESTIC_PARTNER, VALUE_TRUE);
        //6
        xmlValidator.checkDocument(FLOURIDE_TREATMENT_COVERED, VALUE_TRUE);
        //7
        xmlValidator.checkDocument(FLOURIDE_TREATMENT_LIMIT, TWICE);
        xmlValidator.checkDocument(FLOURIDE_TREATMENT_FREQUENCY, ANY_BENEFIT_YEAR);
        //8
        xmlValidator.checkDocument(FLOURIDE_TREATMENT_AGE_LIMIT, "19");
        //9
        xmlValidator.checkDocument(ORAL_EXAMINATION_TREATMENT_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(ORAL_EXAMINATION_TREATMENT_LIMIT, TWICE);
        xmlValidator.checkDocument(ORAL_EXAMINATION_TREATMENT_FREQUENCY, ANY_BENEFIT_YEAR);
        //10
        xmlValidator.checkDocument(PROPHYLAXES_TREATMENT_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(PROPHYLAXES_TREATMENT_LIMIT, TWICE);
        xmlValidator.checkDocument(PROPHYLAXES_TREATMENT_FREQUENCY, ANY_BENEFIT_YEAR);
        //10a
        xmlValidator.checkDocument(BITEWINGS_TREATMENT_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(BITEWINGS_TREATMENT_LIMIT, ONCE);
        xmlValidator.checkDocument(BITEWINGS_TREATMENT_FREQUENCY, ANY_BENEFIT_YEAR);
        //11
        xmlValidator.checkDocument(SPACE_MAINTAINERS_AGE_LIMIT, "14");
        //12
        xmlValidator.checkDocument(WITH_BRUSH_BIOPSY, VALUE_TRUE);
        //13
        xmlValidator.checkDocument(FULL_MOUTH_RADIOGRAPHS_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(FULL_MOUTH_RADIOGRAPHS_LIMIT, ONCE);
        xmlValidator.checkDocument(FULL_MOUTH_RADIOGRAPHS_FREQUENCY, ANY_BENEFIT_YEAR);
        //15-15a
        xmlValidator.checkDocument(SEALANTS_AGE_LIMIT, "16");
        //16
        xmlValidator.checkDocument(SEALANTS_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(SEALANTS_LIMIT, TWICE);
        xmlValidator.checkDocument(SEALANTS_FREQUENCY, ANY_BENEFIT_YEAR);
        //17
        xmlValidator.checkDocument(PERIODONTAL_SURGERY_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(PERIODONTAL_SURGERY_LIMIT, ONCE);
        xmlValidator.checkDocument(PERIODONTAL_SURGERY_FREQUENCY, ANY_BENEFIT_YEAR);
        //18
        xmlValidator.checkDocument(FULL_MOUTH_DEBRIDEMENT_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(FULL_MOUTH_DEBRIDEMENT_LIMIT, ONCE);
        xmlValidator.checkDocument(FULL_MOUTH_DEBRIDEMENT_FREQUENCY, ANY_BENEFIT_YEAR);
        //19
        xmlValidator.checkDocument(CROWNS_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(CROWNS_LIMIT, ONCE);
        xmlValidator.checkDocument(CROWNS_FREQUENCY, "any 5 year period");
        //20a
        xmlValidator.checkDocument(VENEERS_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(VENEERS_LIMIT, ONCE);
        xmlValidator.checkDocument(VENEERS_FREQUENCY, "any 5 year period");
        //21
        xmlValidator.checkDocument(DENTURES_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(DENTURES_LIMIT, ONCE);
        xmlValidator.checkDocument(DENTURES_FREQUENCY, ANY_2_YEAR_PERIOD);
        //22,25
        xmlValidator.checkDocument(BRIDGE_WORK_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(BRIDGE_WORK_LIMIT, ONCE);
        xmlValidator.checkDocument(BRIDGE_WORK_FREQUENCY, ANY_2_YEAR_PERIOD);
        //26
        xmlValidator.checkDocument(RELINES_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(RELINES_LIMIT, ONCE);
        xmlValidator.checkDocument(RELINES_FREQUENCY, ANY_2_YEAR_PERIOD);
        //27-28
        xmlValidator.checkDocument(OCCLUSAL_ADJUSTMENTS_COVERED, VALUE_TRUE);
        xmlValidator.checkDocument(OCCLUSAL_ADJUSTMENTS_LIMIT, ONCE);
        xmlValidator.checkDocument(OCCLUSAL_ADJUSTMENTS_FREQUENCY, ANY_BENEFIT_YEAR);
        //29
        xmlValidator.checkDocument(ORTHO_AGE_LIMIT, "19");
        xmlValidator.checkDocument(ORTHO_AVAILABILITY, "Child Only");

        LOGGER.info("TEST REN-41248: Step 3");
        //1,3
        xmlValidator.checkDocument(POLICY_EFFECTIVE_DT, policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //2
        xmlValidator.checkDocument(POLICY_HOLDER_NAME, memberCompanyName);
        //4
        xmlValidator.checkDocument(FIRST_RENEWAL_DATE, policyEffectiveDate.plusYears(1).format(YYYY_MM_DD_HH_MM_SS_Z));
        //5
        xmlValidator.checkDocument(CUSTOMER_ID, customerNumber);
        xmlValidator.checkDocument(PLAN_NAME, "A La Carte");
        //6a
        xmlValidator.checkDocument(CLASS_NAME, "1");
        //7
        xmlValidator.checkDocument(MIN_HOURS_PER_WEEK, "10");
        //8
        xmlValidator.checkDocument(DECLARATIONS_INFO_ALLOW_MEMBER_ON_CERT, VALUE_TRUE);
        //9
        xmlValidator.checkDocument(DEPENDENT_EXCLUDE_DOMESTIC, VALUE_FALSE);
        //10
        xmlValidator.checkDocument(DEPENDENT_INCLUDE_DOMESTIC, VALUE_TRUE);
        //12
        xmlValidator.checkDocument(ANY_DED_APPLIED, VALUE_TRUE);
        xmlValidator.checkDocument(ALL_SAME_ON_IN_AND_OUT, VALUE_TRUE);
        xmlValidator.checkDocument(WITH_GRADED_YEARS_FOR_DED, VALUE_FALSE);
        xmlValidator.checkDocument(NUMBER_OF_GRADED_YEARS_FOR_DED, "0");
        //13
        xmlValidator.checkDocument(DEDUCTIBLE_PAYS_IN_NETWORK_PAY, "$50");
        xmlValidator.checkDocument(DEDUCTIBLE_PAYS_OUT_OF_NETWORK_PAY, "$50");
        xmlValidator.checkDocument(LIFETIME_DED_PAYS_APPLIED, VALUE_TRUE);
        xmlValidator.checkDocument(LIFETIME_DED_PAYS_IN_NETWORK_PAY, "$100");
        xmlValidator.checkDocument(LIFETIME_DED_PAYS_OUT_OF_NETWORK_PAY, "$100");
        //14
        xmlValidator.checkDocument(FAMILY_DED_PAYS_AMOUNT_IN_NETWORK_PAY, "$150");
        xmlValidator.checkDocument(FAMILY_DED_PAYS_AMOUNT_OUT_OF_NETWORK_PAY, "$150");
        //15
        xmlValidator.checkDocument(APPLIED_ITEMS_DESC, "Basic or Major");
        //16
        xmlValidator.checkDocument(WITH_NOT_APPLIED_DED, VALUE_TRUE);
        xmlValidator.checkDocument(NOT_APPLIED_ITEMS_DESC, "any Diagnostic & Preventive Services");
        //22
        xmlValidator.checkDocument(ANY_DED_APPLIED, VALUE_TRUE);
        //23
        xmlValidator.checkDocument(PREVENTIVE_COVERED_PAYS_IN_NETWORK_PAY, "90%");
        //24
        xmlValidator.checkDocument(PREVENTIVE_COVERED_PAYS_IN_NETWORK_CERT_PAY, "10%");
        //25
        xmlValidator.checkDocument(PREVENTIVE_COVERED_PAYS_OUT_OF_NETWORK_PAY, "90%");
        //26
        xmlValidator.checkDocument(PREVENTIVE_COVERED_PAYS_OUT_OF_NETWORK_CERT_PAY, "10%");
        //27
        xmlValidator.checkDocument(WITH_BRUSH_BIOPSY, VALUE_TRUE);
        //28
        xmlValidator.checkDocument(BRUSH_BIOPSY_COVERED_PAYS_IN_NETWORK_PAY, "90%");
        //29
        xmlValidator.checkDocument(BRUSH_BIOPSY_COVERED_PAYS_IN_NETWORK_CERT_PAY, "10%");
        //30
        xmlValidator.checkDocument(BRUSH_BIOPSY_COVERED_PAYS_OUT_OF_NETWORK_PAY, "90%");
        //31
        xmlValidator.checkDocument(BRUSH_BIOPSY_COVERED_PAYS_OUT_OF_NETWORK_CERT_PAY, "10%");
        //33
        xmlValidator.checkDocument(String.format(BASIC_COVERED_SERVICES_ITEM.get(), 1), "Emergency Palliative Treatment");
        xmlValidator.checkDocument(String.format(BASIC_COVERED_SERVICES_ITEM.get(), 2), "Radiographs");
        xmlValidator.checkDocument(String.format(BASIC_COVERED_SERVICES_ITEM.get(), 3), "Other Basic Services");
        xmlValidator.checkDocument(String.format(BASIC_COVERED_SERVICES_ITEM.get(), 4), "Periodontic Services");
        xmlValidator.checkDocument(String.format(BASIC_COVERED_SERVICES_ITEM.get(), 5), "Minor Restorative Services");
        xmlValidator.checkDocument(String.format(BASIC_COVERED_SERVICES_ITEM.get(), 6), "Endodontic Services");
        xmlValidator.checkDocument(String.format(BASIC_COVERED_SERVICES_ITEM.get(), 7), "Surgical Extractions");
        //34
        xmlValidator.checkDocument(BASIC_COVERED_PAYS_IN_NETWORK_PAY, "80%");
        //35
        xmlValidator.checkDocument(BASIC_COVERED_PAYS_IN_NETWORK_CERT_PAY, "20%");
        //36
        xmlValidator.checkDocument(BASIC_COVERED_PAYS_OUT_OF_NETWORK_PAY, "80%");
        //37
        xmlValidator.checkDocument(BASIC_COVERED_PAYS_OUT_OF_NETWORK_CERT_PAY, "20%");
        //39
        xmlValidator.checkDocument(String.format(MAJOR_COVERED_SERVICES_ITEM.get(), 1), "Diagnostic and Preventive Services");
        xmlValidator.checkDocument(String.format(MAJOR_COVERED_SERVICES_ITEM.get(), 2), "Simple Extractions");
        xmlValidator.checkDocument(String.format(MAJOR_COVERED_SERVICES_ITEM.get(), 3), "Other Oral Surgery Services");
        xmlValidator.checkDocument(String.format(MAJOR_COVERED_SERVICES_ITEM.get(), 4), "Major Restorative Services");
        xmlValidator.checkDocument(String.format(MAJOR_COVERED_SERVICES_ITEM.get(), 5), "Relines and Repairs");
        xmlValidator.checkDocument(String.format(MAJOR_COVERED_SERVICES_ITEM.get(), 6), "Prosthodontic Services");
        xmlValidator.checkDocument(String.format(MAJOR_COVERED_SERVICES_ITEM.get(), 7), "TMD");
        //40
        xmlValidator.checkDocument(MAJOR_COVERED_PAYS_IN_NETWORK_PAY, "60%");
        //41
        xmlValidator.checkDocument(MAJOR_COVERED_PAYS_IN_NETWORK_CERT_PAY, "40%");
        //42
        xmlValidator.checkDocument(MAJOR_COVERED_PAYS_OUT_OF_NETWORK_PAY, "60%");
        //43
        xmlValidator.checkDocument(MAJOR_COVERED_PAYS_OUT_OF_NETWORK_CERT_PAY, "40%");
        //44a
        xmlValidator.checkDocument(String.format(ORTHODONTIA_COVERED_SERVICES_ITEM.get(), 1), "Orthodontic Services");
        //45
        xmlValidator.checkDocument(ORTHODONTIA_COVERED_PAYS_IN_NETWORK_PAY, "60%");
        //46
        xmlValidator.checkDocument(ORTHODONTIA_COVERED_PAYS_IN_NETWORK_CERT_PAY, "40%");
        //47
        xmlValidator.checkDocument(ORTHODONTIA_COVERED_PAYS_OUT_OF_NETWORK_PAY, "60%");
        //48
        xmlValidator.checkDocument(ORTHODONTIA_COVERED_PAYS_OUT_OF_NETWORK_CERT_PAY, "40%");
        //49
        xmlValidator.checkDocument(ORTHO_WITH_CHILD, VALUE_TRUE);
        //50
        xmlValidator.checkDocument(ORTHO_CHILD_MAXAGE, "19");
        //51
        xmlValidator.checkDocument(ORTHO_ADULT_AND_CHILD, VALUE_FALSE);
        //52
        xmlValidator.checkDocument(ORTHO_APPLIED, VALUE_TRUE);
        //53
        xmlValidator.checkDocument(NEW_HIRE_WAITING_IND, VALUE_TRUE);
        //54
        xmlValidator.checkDocument(NEW_HIRE_WAITING_PERIOD, "AmountAndModeOnly");
        //55-57a
        xmlValidator.checkDocument(ELIGIBILITY_WAITING_AMT_MODE, "12 days");
        xmlValidator.checkDocument(ELIGIBILITY_WAITING_AMOUNT, "12");
        //58
        xmlValidator.checkDocument(COVERED_SERVICE_WAITING_IND, VALUE_TRUE);
        //59-60
        xmlValidator.checkDocument(BENEFIT_WAITING_PERIOD, TWELVE_MONTH);
        xmlValidator.checkDocument(PREVENTIVE_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidator.checkDocument(PREVENTIVE_WAITING_PERIOD_VALUE, TWELVE_MONTH);
        xmlValidator.checkDocument(RADIOGRAPHS_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidator.checkDocument(RADIOGRAPHS_WAITING_PERIOD_VALUE, TWELVE_MONTH);
        xmlValidator.checkDocument(BASIC_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidator.checkDocument(BASIC_WAITING_PERIOD_VALUE, TWELVE_MONTH);
        xmlValidator.checkDocument(MAJOR_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidator.checkDocument(MAJOR_WAITING_PERIOD_VALUE, TWELVE_MONTH);
        xmlValidator.checkDocument(ORTHO_WAITING_PERIOD_VALID, VALUE_TRUE);
        xmlValidator.checkDocument(ORTHO_WAITING_PERIOD_VALUE, TWELVE_MONTH);
        //61
        xmlValidator.checkDocument(MISSING_TOOTH_IND, VALUE_TRUE);
        //62
        xmlValidator.checkDocument(MAC_PLAN_IND, VALUE_TRUE);
        //64
        xmlValidator.checkDocument(PLAN_MAXIMUM_PAYS_IN_NETWORK_PAY, "$1,400");
        xmlValidator.checkDocument(PLAN_MAXIMUM_PAYS_OUT_OF_NETWORK_PAY, "$1,400");
        //65
        xmlValidator.checkDocument(MAXIMUM_EXTENDER_IND, VALUE_TRUE);
        //66
        xmlValidator.checkDocument(ORTHO_APPLIED, VALUE_TRUE);
        //67
        xmlValidator.checkDocument(LIFETIME_MAXIMUM_PAYS_APPLIED, VALUE_TRUE);
        xmlValidator.checkDocument(LIFETIME_MAXIMUM_PAYS_IN_NETWORK_PAY, "$1,000");
        xmlValidator.checkDocument(LIFETIME_MAXIMUM_PAYS_OUT_OF_NETWORK_PAY, "$1,000");
        //68
        xmlValidator.checkDocument(MAXIMUM_ROLL_OVER_IND, VALUE_TRUE);
        //69
        xmlValidator.checkDocument(ROLL_OVER_THRESHOLD, "750.00");
        //70
        xmlValidator.checkDocument(ROLL_OVER_BENEFIT, "375.00");
        //71
        xmlValidator.checkDocument(ROLL_OVER_BENEFIT_LIMIT, "1500.00");
        //72
        xmlValidator.checkDocument(WITH_TIER_DATA, VALUE_TRUE);
        xmlValidator.checkDocument(String.format(ALL_COVERAGE_TIERS_DESC_ITEM.get(), 1), "Employee + 1");
        xmlValidator.checkDocument(String.format(ALL_COVERAGE_TIERS_DESC_ITEM.get(), 2), "Employee + Family");
        xmlValidator.checkDocument(String.format(ALL_COVERAGE_TIERS_DESC_ITEM.get(), 3), "Employee Only");
        //73
        xmlValidator.checkDocument(String.format(RATE_INFOS_ITEM_TIER_NAME.get(), 1), "Employee + 1");
        xmlValidator.checkDocument(String.format(RATE_INFOS_ITEM_TIER_NAME.get(), 2), "Employee + Family");
        xmlValidator.checkDocument(String.format(RATE_INFOS_ITEM_TIER_NAME.get(), 3), "Employee Only");
        xmlValidator.checkDocument(String.format(RATE_INFOS_ITEM_RATE.get(), 1), employeePlusOneRate);
        xmlValidator.checkDocument(String.format(RATE_INFOS_ITEM_RATE.get(), 2), employeePlusFamilyRate);
        xmlValidator.checkDocument(String.format(RATE_INFOS_ITEM_RATE.get(), 3), employeeOnlyRate);
        //73a
        xmlValidator.checkDocument(String.format(RATE_INFOS_ITEM_RATE_BASIS.get(), 1), "Monthly Tiered Price Per Participant");
        xmlValidator.checkDocument(String.format(RATE_INFOS_ITEM_RATE_BASIS.get(), 2), "Monthly Tiered Price Per Participant");
        xmlValidator.checkDocument(String.format(RATE_INFOS_ITEM_RATE_BASIS.get(), 3), "Monthly Tiered Price Per Participant");
        //74
        xmlValidator.checkDocument(NON_CTB_WITH_NO_PTCP_CTB, VALUE_TRUE);
        //75
        xmlValidator.checkDocument(FULL_PRCP_CTB, VALUE_FALSE);
        //76
        xmlValidator.checkDocument(REQUIRED_PARTICIPATION, "100");
        //77
        xmlValidator.checkDocument(CTB_WITH_FULL_EMP_AND_NO_DEP_PTCP, VALUE_FALSE);
        //78
        xmlValidator.checkDocument(CTB_WITH_OTHER_THAN_FULL_EMP_OR_NO_DEP_PTCP, VALUE_FALSE);
        //82
        xmlValidator.checkDocument(BENEFIT_END_ON_CD, "LASTDAYOFMONTH");
        //83
        xmlValidator.checkDocument(WHOLE_CALENDAR_YEAR, VALUE_FALSE);
        //85
        xmlValidator.checkDocument(CURRENT_START_DT, policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //86
        xmlValidator.checkDocument(CURRENT_EN_DT, policyEffectiveDate.plusYears(1).minusDays(1).format(YYYY_MM_DD_HH_MM_SS_Z));
        //87
        xmlValidator.checkDocument(MAIL_CARDS_TO, "GROUP");
    }
}

