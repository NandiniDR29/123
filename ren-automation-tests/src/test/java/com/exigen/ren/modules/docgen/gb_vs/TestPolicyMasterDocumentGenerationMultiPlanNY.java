package com.exigen.ren.modules.docgen.gb_vs;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderPolicy.POLICY_AND_CERT;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.CoveragesConstants.CoverageTiers.EMPLOYEE_ONLY;
import static com.exigen.ren.main.enums.CoveragesConstants.CoverageTiers.EMPLOYEE_PLUS_FAMILY;
import static com.exigen.ren.main.enums.CoveragesConstants.CoverageTiers.EMPLOYEE_PLUS_ONE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_FALSE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_TRUE;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.ActiveAndRecentlyExpiredPolicies.POLICY;
import static com.exigen.ren.utils.DBHelper.EventName.REN_POLICY_ISSUE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static org.assertj.core.api.Assertions.assertThat;

public class TestPolicyMasterDocumentGenerationMultiPlanNY extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-44581"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyMasterDocumentGenerationMultiPlanNY() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithYesAndNoSubGroups"), groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_Policy").resolveLinks())
                .adjust(tdSpecific().getTestData("TestDataProposal"))
                .adjust(tdSpecific().getTestData("TestDataIssue").resolveLinks()));

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String groupName = PolicySummaryPage.labelCustomerName.getValue();

        LOGGER.info("TEST REN-44581: Step 1");
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay();
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        String documentName = String.format("%s_VISION_%s-Policy-%s_%s_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, documentDate);
        assertThat(Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + POLICY.getName(), customerNumber + "_", documentName))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();

        LOGGER.info("TEST REN-44581: Step 2");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER, REN_POLICY_ISSUE);

        xmlValidator.checkDocument(String.format("%s%s", ISSUE_DATA_SOURCE_PRODUCT_VS.get(), SITUS_STATE_VALUE.get()), "NY");
        //3
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), NETWORK.get()), "Choice");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), NETWORK.get()), "Choice");
        //19, 47
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), DEPENDENT_MAX_AGE.get()), "21");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), DEPENDENT_MAX_AGE.get()), "20");
        //22
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), INCLUDES_DOMESTIC_PARTNER.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), INCLUDES_DOMESTIC_PARTNER.get()), VALUE_TRUE);
        //24
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), EXAM_LENSES_FRAME_FIELD_VALUE1.get()), "12");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), FREQUENCY_IN_PDF.get()), "months");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), EXAM_LENSES_FRAME_FIELD_VALUE1.get()), "12");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), FREQUENCY_IN_PDF.get()), "calendar months");
        //25
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), EXAM_LENSES_FRAME_FIELD_VALUE2.get()), "12");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), EXAM_LENSES_FRAME_FIELD_VALUE2.get()), "12");
        //26
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), EXAM_LENSES_FRAME_FIELD_VALUE3.get()), "24");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), EXAM_LENSES_FRAME_FIELD_VALUE3.get()), "24");
        //27
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), EXAM_LENSES_FRAME_FIELD_VALUE4.get()), "PLANB");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), EXAM_LENSES_FRAME_FIELD_VALUE4.get()), "PLANB");
        //31
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), EMPTY_SCRATCH_COATING_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), EMPTY_SAFETY_GLASSES_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), EMPTY_PHOTOCHROMIC_LENSES_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), PROGRESSIVE_LENSES_IN_FULL.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), EMPTY_SCRATCH_COATING_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), EMPTY_SAFETY_GLASSES_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), EMPTY_PHOTOCHROMIC_LENSES_FACTOR.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), PROGRESSIVE_LENSES_IN_FULL.get()), VALUE_FALSE);

        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), SCRATCH_COATING_FACTOR.get()), "0.0000");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), SAFETY_GLASSES_FACTOR.get()), "0.0000");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), PHOTOCHROMIC_LENSES_FACTOR.get()), "0.0000");

        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), SCRATCH_COATING_FACTOR.get()), "0.0000");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), SAFETY_GLASSES_FACTOR.get()), "0.0000");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), PHOTOCHROMIC_LENSES_FACTOR.get()), "0.0000");

        //52, //2 from Step 3
        xmlValidator.checkDocument(String.format("%s%s", ISSUE_DATA_SOURCE_PRODUCT_VS.get(), NEXT_RENEWAL_EFF_DATE.get()), policyEffectiveDate.plusYears(1).format(YYYY_MM_DD_HH_MM_SS_Z));

        LOGGER.info("TEST REN-44581: Step 3");
        //1
        xmlValidator.checkDocument(String.format("%s%s", ISSUE_DATA_SOURCE_PRODUCT_VS.get(), POLICY_EFFECTIVE_DT.get()), policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //3, 4a
        xmlValidator.checkDocument(String.format("%s%s", ISSUE_DATA_SOURCE_PRODUCT_VS.get(), CUSTOMER_ID.get()), customerNumber);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), PLAN_NAME.get()), PolicyConstants.PlanVision.A_LA_CARTE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), PLAN_NAME.get()), PolicyConstants.PlanVision.PlanB);
        //5
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), MIN_HOURS_PER_WEEK.get()), "35");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), MIN_HOURS_PER_WEEK.get()), "35");
        //6 Plan Definition > Eligibility > Allow Member and Spouse (Who are part of Group) on Separate Certificate?
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), ALLOW_MEMBER_ON_CERT.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), ALLOW_MEMBER_ON_CERT.get()), VALUE_TRUE);
        //7, 8 MP Issue work space: Case Installation Tab > Definition of Legal Spouse > Includes Domestic Partner
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), INCLUDES_DOMESTIC_PARTNER.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), INCLUDES_DOMESTIC_PARTNER.get()), VALUE_TRUE);
        //9, 10, 11
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 1), WITH_TIER_DATA.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 1), WITH_AREA_TIER_DATA.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 1), String.format(ALL_COVERAGE_TIERS_DESC_ITEM.get(), 1)), EMPLOYEE_PLUS_ONE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 1), String.format(ALL_COVERAGE_TIERS_DESC_ITEM.get(), 2)), EMPLOYEE_PLUS_FAMILY);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 1), String.format(ALL_COVERAGE_TIERS_DESC_ITEM.get(), 3)), EMPLOYEE_ONLY);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 1), String.format(RATE_INFOS_ITEM_TIER_NAME.get(), 1)), EMPLOYEE_PLUS_ONE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 1), String.format(RATE_INFOS_ITEM_RATE.get(), 1)), "14.38");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 1), String.format(RATE_INFOS_ITEM_RATE_BASIS.get(), 1)), "Monthly Tiered Price Per Participant");

        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 2), WITH_TIER_DATA.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 2), WITH_AREA_TIER_DATA.get()), VALUE_TRUE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 2), String.format(ALL_COVERAGE_TIERS_DESC_ITEM.get(), 1)), EMPLOYEE_PLUS_ONE);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 2), String.format(ALL_COVERAGE_TIERS_DESC_ITEM.get(), 2)), EMPLOYEE_PLUS_FAMILY);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 2), String.format(ALL_COVERAGE_TIERS_DESC_ITEM.get(), 3)), EMPLOYEE_ONLY);
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 2), String.format(RATE_INFOS_ITEM_ITEM_NAME.get(), 1)), "Area 1");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM_RATE_DETAILS.get(), 2), String.format(RATE_INFOS_ITEM_RATE_AMOUNT_ITEMS_ITEM.get(), 1, 1)), "10.55");
        //12
        xmlValidator.checkDocument(String.format("%s%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), PLAN_COMMON_DATA.get(), FULL_PRCP_CTB.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), PLAN_COMMON_DATA.get(), FULL_PRCP_CTB.get()), VALUE_TRUE);
        //18 Plan Definition > Eligibility > Benefits End On
        xmlValidator.checkDocument(String.format("%s%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), PLAN_COMMON_DATA.get(), BENEFIT_END_ON_CD.get()), "LASTDAYOFMONTH");
        xmlValidator.checkDocument(String.format("%s%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), PLAN_COMMON_DATA.get(), BENEFIT_END_ON_CD.get()), "LASTDATEOFEMPLOY");
        //19-20
        xmlValidator.checkDocument(String.format("%s%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), PLAN_COMMON_DATA.get(), WHOLE_CALENDAR_YEAR.get()), VALUE_FALSE);
        xmlValidator.checkDocument(String.format("%s%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), PLAN_COMMON_DATA.get(), WHOLE_CALENDAR_YEAR.get()), VALUE_FALSE);
        //21
        xmlValidator.checkDocument(String.format("%s%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), PLAN_COMMON_DATA.get(), CURRENT_START_DT.get()), policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        xmlValidator.checkDocument(String.format("%s%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), PLAN_COMMON_DATA.get(), CURRENT_START_DT.get()), policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //22
        xmlValidator.checkDocument(String.format("%s%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), PLAN_COMMON_DATA.get(), CURRENT_EN_DT.get()), policyEffectiveDate.plusYears(1).minusDays(1).format(YYYY_MM_DD_HH_MM_SS_Z));
        xmlValidator.checkDocument(String.format("%s%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), PLAN_COMMON_DATA.get(), CURRENT_EN_DT.get()), policyEffectiveDate.plusYears(1).minusDays(1).format(YYYY_MM_DD_HH_MM_SS_Z));

        LOGGER.info("TEST REN-44581: Steps 4-5");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 1), EXAM_MATERIALS_FIELD_VALUE1.get()), "$0");
        xmlValidator.checkDocument(String.format("%s%s", String.format(ISSUE_DATA_SOURCE_PRODUCT_VS_PLAN_DATA_ITEM.get(), 2), EXAM_MATERIALS_FIELD_VALUE1.get()), "$10");

    }
}
