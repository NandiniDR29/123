package com.exigen.ren.modules.docgen.gb_di_std;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonIndCustPolicyAndCert.CERTIFICATE;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonIndCustPolicyAndCert.POLICY;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonIndCustomer.POLICY_AND_CERT;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.common.module.efolder.Efolder.collapseFolder;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.TableConstants.ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME;
import static com.exigen.ren.main.enums.TableConstants.ClassificationSubGroupsAndRatingColumns.RATE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_FALSE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_TRUE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.CustomerInformation.NAME_LEGAL;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.tableCustomerInformation;
import static com.exigen.ren.utils.DBHelper.EventName.REN_POLICY_ISSUE;
import static com.exigen.ren.utils.DBHelper.EventName.REN_POLICY_ISSUE_CLASS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestCheckCPDocumentGeneration extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-32184", component = POLICY_GROUPBENEFITS)
    public void testCheckCPDocumentGeneration() {
        final String TWO = "two";
        final String INCLUDED = "Included";
        mainApp().open();

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(tdSpecific().getTestData("TestData_Customer").resolveLinks()));
        String phoneNumber = getStoredValue("MobilePhoneNumber2");
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_WithTwoPlans_NoAndYesSubGroups"), shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_Policy").resolveLinks()));
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String groupName = tableCustomerInformation.getRow(1).getCell(NAME_LEGAL.getName()).getValue();

        LOGGER.info("TEST REN-32184: Step 1");
        NavigationPage.toMainTab(CUSTOMER);
        LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().withDayOfMonth(1);
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        String documentName = String.format("%s_STD_%s-Certificate-%s_%s_NC_1_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, documentDate);
        assertThat(Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + CERTIFICATE.getName(), customerNumber + "_", documentName))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-32184: Step 2");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER, REN_POLICY_ISSUE_CLASS);

        //1
        xmlValidator.checkDocument(POLICY_HOLDER_NAME, groupName);
        //2
        xmlValidator.checkDocument(CUSTOMER_ADDRESS_ADDRESS_LINE1, "Address Line1");
        xmlValidator.checkDocument(CUSTOMER_ADDRESS_ADDRESS_LINE2, "Address Line2");
        xmlValidator.checkDocument(CUSTOMER_ADDRESS_ADDRESS_LINE3, "Address Line3");
        xmlValidator.checkDocument(CUSTOMER_ADDRESS_STATE_PROV, "CA");
        xmlValidator.checkDocument(CUSTOMER_ADDRESS_POSTAL_CODE, "45587");
        xmlValidator.checkDocument(CUSTOMER_ADDRESS_CITY, "Beverly Hills");
        //3
        xmlValidator.checkDocument(POLICY_HOLDER_PHONE, phoneNumber);
        //4
        xmlValidator.checkDocument(STD1b_2_POLICY_NUMBER, policyNumber);
        //5
        xmlValidator.checkDocument(POLICY_EFFECTIVE_DATE_VALUE, policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //6
        xmlValidator.checkDocument(STATE_DEPARTMENT_OF_INSURANCE_OF_SITUS_STATE, "New York State Department of Financial Services");
        //7
        xmlValidator.checkDocument(PHONE_OF_SITUS_STATE, "(800) 342-3736");
        //10
        xmlValidator.checkDocument(PRE_EXISTING_CONDITIONS, "Included");
        //11
        xmlValidator.checkDocument(STD2, "1");
        //12-13
        xmlValidator.checkDocument(MIN_HOURLY_REQ, "20.00");
        //14-15
        xmlValidator.checkDocument(BENEFIT_TYPE, "FIXED_SALARY_PCT");
        xmlValidator.checkDocument(BENEFIT_PERCENTAGE, "60%");
        //16
        xmlValidator.checkDocument(MAXIMUM_WEEKLY_BENEFIT, "1500.00");
        //19
        xmlValidator.checkNodeNotPresent(WEEKLY_BENEFIT_AMOUNT);

        LOGGER.info("TEST REN-32184: Step 3");
        //25
        xmlValidator.checkDocument(OFFSET_IS_FLAGGED, VALUE_TRUE);
        //27
        xmlValidator.checkDocument(MINIMUM_WEEKLY_BENEFIT, "$50");
        //29
        xmlValidator.checkDocument(ELIMINATION_PERIOD, "14");
        //30
        xmlValidator.checkDocument(IS_END_OF_SALARY_CONTINUATION, VALUE_TRUE);
        //31
        xmlValidator.checkDocument(IS_END_OF_ACCUMULATED_SICK_LEAVE, VALUE_TRUE);
        //32
        xmlValidator.checkDocument(ELIMINATION_PERIOD_SICKNESS, "14");
        //33
        xmlValidator.checkDocument(FIRST_DAY_HOSPITALIZATION, "Included");
        //20
        xmlValidator.checkDocument(MAX_BENEFIT_PERIOD, "26");
        //34
        xmlValidator.checkDocument(COVERAGE_BASIS, "Non Occupational Coverage");
        //35
        xmlValidator.checkDocument(PRE_EXISTING_CONDITIONS_35, "3 Months/12 Months");
        //39
        xmlValidator.checkNodeNotPresent(WAITING_PERIOD_INTEGER);
        //40
        xmlValidator.checkDocument(WAITING_PERIOD_DISPLAYED, VALUE_TRUE);
        //41
        xmlValidator.checkDocument(CONTRIBUTION_TYPE_DISPLAYED_41, VALUE_FALSE);
        //42
        xmlValidator.checkDocument(CONTRIBUTION_TYPE_DISPLAYED_42, VALUE_FALSE);
        //43
        xmlValidator.checkDocument(CONTRIBUTION_TYPE_DISPLAYED_43, VALUE_TRUE);
        //44
        xmlValidator.checkDocument(GROSS_UP, VALUE_TRUE);

        LOGGER.info("TEST REN-32184: Step 4");
        //45
        xmlValidator.checkDocument(PREMIUM_WAIVER_INCLUDED, VALUE_FALSE);
        //46-47
        xmlValidator.checkDocument(PREMIUM_WAIVER_INCLUDED, VALUE_FALSE);
        //48
        xmlValidator.checkDocument(EARNING_DEFINITION_48, TWO);
        //49
        xmlValidator.checkDocument(EARNING_DEFINITION_49, TWO);
        //50
        xmlValidator.checkDocument(EARNING_DEFINITION_50, TWO);
        //52
        xmlValidator.checkDocument(EARNING_DEFINITION_52, TWO);
        //53
        xmlValidator.checkDocument(EARNING_DEFINITION_53, VALUE_FALSE);
        //54
        xmlValidator.checkDocument(EARNING_DEFINITION_54, "52");
        //55
        xmlValidator.checkDocument(EARNING_DEFINITION_55, VALUE_FALSE);
        //56
        xmlValidator.checkDocument(EARNING_DEFINITION_56, VALUE_FALSE);
        //57
        xmlValidator.checkDocument(EARNING_DEFINITION_54, "52");
        //58
        xmlValidator.checkDocument(EARNING_DEFINITION_58, VALUE_FALSE);
        //68
        xmlValidator.checkDocument(CONTRIBUTION_TYPE_INDICATOR_68, VALUE_FALSE);
        //69
        xmlValidator.checkDocument(CONTRIBUTION_TYPE_INDICATOR_69, VALUE_FALSE);
        //70
        xmlValidator.checkDocument(CONTRIBUTION_TYPE_INDICATOR_70, VALUE_FALSE);
        //71
        xmlValidator.checkDocument(LAYOFF_DISPLAYED_71, VALUE_TRUE);
        //72, 74
        xmlValidator.checkDocument(SMALL_GROUP, VALUE_TRUE);

        LOGGER.info("TEST REN-32184: Step 5");
        //75
        xmlValidator.checkDocument(DISPLAYED_IND_75, VALUE_TRUE); //MP > Plan Definition > Schedule of Continuation Provision > Layoff
        //76
        xmlValidator.checkDocument(DISPLAYED_IND_76, VALUE_TRUE);
        //77
        xmlValidator.checkDocument(DISPLAYED_IND_77, VALUE_FALSE); //MP > Plan Definition > Schedule of Continuation Provision > Leave of Absence (Non-Medical)
        //79
        xmlValidator.checkDocument(MILITARY_LEAVE, "4 Weeks");
        //80
        xmlValidator.checkDocument(MILITARY_LEAVE_DURATION, "12Months");
        //82
        xmlValidator.checkDocument(PARTIAL_DISABILITY_IND_82, VALUE_TRUE);
        //82a
        xmlValidator.checkDocument(DISABILITY_DEFINITION, "Own Job");
        //83
        xmlValidator.checkDocument(PARTIAL_DISABILITY_IND_83, VALUE_FALSE);
        //84
        xmlValidator.checkDocument(PARTIAL_DISABILITY_IND_84, VALUE_FALSE);
        //85
        xmlValidator.checkDocument(ELIMINATION_PERIOD_SICKNESS_85, "7");
        //87-88
        xmlValidator.checkDocument(DISPLAYED_IND_87, VALUE_TRUE);
        //89
        xmlValidator.checkDocument(DISPLAYED_IND_89, VALUE_TRUE);
        //90
        xmlValidator.checkDocument(DISPLAYED_IND_90, VALUE_TRUE);
        //91
        xmlValidator.checkDocument(DISPLAYED_IND_91, VALUE_TRUE);
        //92
        xmlValidator.checkDocument(DISPLAYED_IND_92, VALUE_TRUE);
        //94
        xmlValidator.checkDocument(DISPLAYED_IND_94, VALUE_TRUE);
        //95
        xmlValidator.checkDocument(DISPLAYED_IND_95, VALUE_FALSE);
        //97
        xmlValidator.checkDocument(DISPLAYED_IND_97, VALUE_TRUE);
        //99
        xmlValidator.checkDocument(DISPLAYED_IND_99, VALUE_TRUE);

        LOGGER.info("TEST REN-32184: Step 6");
        //104
        xmlValidator.checkDocument(WORKER_COMPENSATION, INCLUDED);
        //105
        xmlValidator.checkDocument(STAT_OFFSET, INCLUDED);
        //106-107
        xmlValidator.checkDocument(INTEG_METHOD, "Family");
        //108
        xmlValidator.checkDocument(SICK_LEAVE, INCLUDED);
        //109
        xmlValidator.checkDocument(PTO, INCLUDED);
        //110
        xmlValidator.checkDocument(TERMINATION_OR_SEVERANCE, INCLUDED);
        //111
        xmlValidator.checkDocument(WORK_EARNINGS, INCLUDED);
        //112
        xmlValidator.checkDocument(RETIREMENT_PLAN, INCLUDED);
        //113
        xmlValidator.checkDocument(UNEMPLOYMENT, INCLUDED);
        //114, 117
        xmlValidator.checkDocument(INDIVIDUAL_DISABILITY_PLAN, INCLUDED);
        //115
        xmlValidator.checkDocument(DISPLAYED_IND_115, VALUE_TRUE);
        //118
        xmlValidator.checkDocument(PARTIAL_DISABILITY, "Work Incentive Benefit");
        //119
        xmlValidator.checkDocument(DISPLAYED_IND_119, VALUE_TRUE);
        //123
        xmlValidator.checkDocument(DISPLAYED_IND_123, VALUE_TRUE);
        //124
        xmlValidator.checkDocument(DISPLAYED_IND_124, VALUE_FALSE);
        //128
        xmlValidator.checkDocument(DISPLAYED_IND_128, VALUE_TRUE);
        //129
        xmlValidator.checkDocument(DISPLAYED_IND_129, VALUE_FALSE);
        //130
        xmlValidator.checkDocument(DISPLAYED_IND_130, VALUE_FALSE);
        //131
        xmlValidator.checkDocument(DISPLAYED_IND_131, VALUE_TRUE);

        LOGGER.info("TEST REN-32184: Step 7");
        //132
        xmlValidator.checkDocument(DISPLAYED_IND_132, VALUE_TRUE);
        //133
        xmlValidator.checkDocument(DISPLAYED_IND_133, VALUE_TRUE);
        //134
        xmlValidator.checkDocument(DISPLAYED_IND_134, VALUE_TRUE);
        //135
        xmlValidator.checkDocument(DISPLAYED_IND_135, VALUE_TRUE);
        //136
        xmlValidator.checkDocument(DISPLAYED_IND_136, VALUE_TRUE);
        //137
        xmlValidator.checkDocument(DISPLAYED_IND_137, VALUE_TRUE);
        //139
        xmlValidator.checkDocument(DISPLAYED_IND_139, VALUE_TRUE);
        //140
        xmlValidator.checkDocument(DISPLAYED_IND_140, VALUE_TRUE);
        //155
        xmlValidator.checkDocument(DISPLAYED_IND_155, VALUE_TRUE);
        //157
        xmlValidator.checkDocument(DISPLAYED_IND_157, VALUE_TRUE);
        //158
        xmlValidator.checkDocument(DISPLAYED_IND_158, VALUE_TRUE);
        //161
        xmlValidator.checkDocument(DISPLAYED_IND_161, VALUE_TRUE);
        //166
        xmlValidator.checkDocument(DISPLAYED_IND_166_ST_AND_ND, VALUE_FALSE);
        //168
        xmlValidator.checkDocument(DISPLAY_VALUE_168, String.format("1 %s of each year", policyEffectiveDate.getMonthValue()));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-31722", component = POLICY_GROUPBENEFITS)
    public void testPolicyMasterDocumentGeneration() {
        mainApp().open();

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(tdSpecific().getTestData("TestData_Customer").resolveLinks()));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        caseProfile.create(tdSpecific().getTestData("TestData_WithTwoPlans_NoAndYesSubGroups"), shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_Policy2").resolveLinks()));
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String groupName = tableCustomerInformation.getRow(1).getCell(NAME_LEGAL.getName()).getValue();

        NavigationPage.toMainTab(CUSTOMER);
        LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().withDayOfMonth(1);
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        String documentName1 = String.format("%s_STD_%s-Policy-%s_%s_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, documentDate);
        String documentName2 = String.format("%s_STD_%s-Certificate-%s_%s_NC_1_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, documentDate);

        LOGGER.info("TEST REN-31722: Step 1");
        assertThat(Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + POLICY.getName(), customerNumber + "_", documentName1))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();

        LOGGER.info("TEST REN-31722: Step 2");
        collapseFolder(POLICY_AND_CERT.getName());
        assertThat(Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + CERTIFICATE.getName(), customerNumber + "_", documentName2))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();

        search(policyNumber);
        shortTermDisabilityMasterPolicy.policyInquiry().start();
        premiumSummaryTab.navigate();
        PremiumSummaryBindActionTab.buttonViewRateHistory.click();
        String finalRate = RateDialogs.ViewRateHistoryDialog.tableRateHistory.getRow(1).getCell(TableConstants.RateHistoryTable.FINAL_RATE.getName()).getValue();
        RateDialogs.ViewRateHistoryDialog.close();
        classificationManagementMpTab.navigate();

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-31722: Step 3");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER, REN_POLICY_ISSUE);

        //1
        xmlValidator.checkDocument(POLICY_HOLDER_NAME, groupName);
        //2
        xmlValidator.checkDocument(STD1b_2_POLICY_NUMBER, policyNumber);
        //3, 5b
        xmlValidator.checkDocument(POLICY_EFFECTIVE_DT, policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //5
        xmlValidator.checkDocument(SITUS_STATE_VALUE, "NY");
        //6
        xmlValidator.checkDocument(BILLING_LOCATION_DETAILS_LOCATION_NAME, "Billing Location 1");
        //7
        xmlValidator.checkDocument(BILLING_LOCATION_DETAILS_LOCATION_CITY, "Indianapolis");
        xmlValidator.checkDocument(BILLING_LOCATION_DETAILS_LOCATION_STATE, "IN");
        //8, 12
        xmlValidator.checkDocument(RATE_BASIS, "Per $10 Gross Weekly Benefit");
        assertThat(xmlValidator.getNodeValue(RATE_BASIS_INFOS_FINAL_RATE)).startsWith(finalRate);
        //10
        xmlValidator.checkDocument(String.format("ren-docgen-policy-issue-data-source/productSTD/planDataItems/item[1]/rateBasisInfos/item[1]%s", STD5_3_USE_CLASS_SUB_GROUP.get()), VALUE_TRUE);
        //11
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_AGE_DESC.get(), 1), "Under 25");
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_RATE.get(), 1), getRateValue("0-24"));

        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_AGE_DESC.get(), 2), "25-29");
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_RATE.get(), 2), getRateValue("25-29"));

        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_AGE_DESC.get(), 3), "30-34");
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_RATE.get(), 3), getRateValue("30-34"));

        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_AGE_DESC.get(), 4), "35-39");
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_RATE.get(), 4), getRateValue("35-39"));

        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_AGE_DESC.get(), 5), "40-44");
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_RATE.get(), 5), getRateValue("40-44"));

        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_AGE_DESC.get(), 6), "45-49");
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_RATE.get(), 6), getRateValue("45-49"));

        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_AGE_DESC.get(), 7), "50-54");
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_RATE.get(), 7), getRateValue("50-54"));

        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_AGE_DESC.get(), 8), "55-59");
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_RATE.get(), 8), getRateValue("55-59"));

        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_AGE_DESC.get(), 9), "60-64");
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_RATE.get(), 9), getRateValue("60-64"));

        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_AGE_DESC.get(), 10), "65-69");
        xmlValidator.checkDocument(String.format(RATE_BASIS_INFOS_AGE_BANDED_INFO_LIST_RATE.get(), 10), getRateValue("65-69"));
        //14, 15
        xmlValidator.checkDocument(RATE_GUARANTEE, "24");
        //18
        xmlValidator.checkDocument(FREQUENCY_VALUE, "monthly");
        //19
        xmlValidator.checkDocument(WAIVER_OF_PREMIUM, VALUE_FALSE);
        //23
        xmlValidator.checkDocument(REQUIRED_PARTICIPATION_PCT_STR, "25%");
        //25
        xmlValidator.checkDocument(CONTRIBUTION_TYPE_VALUE, "VOLUNTARY");
        //26-28
        xmlValidator.checkDocument(IS_SMALL_GROUP, VALUE_TRUE);
    }

    private String getRateValue(String classificationName) {
        String rateValue = tableClassificationSubGroupsAndRatingInfo.getRow(CLASSIFICATION_SUB_GROUP_NAME.getName(), classificationName).getCell(RATE.getName()).getValue();
        return String.valueOf(Double.parseDouble(new DecimalFormat("##.##").format(Double.valueOf(rateValue))));
    }
}