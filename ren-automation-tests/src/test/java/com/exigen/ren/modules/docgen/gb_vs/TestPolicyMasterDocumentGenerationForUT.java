package com.exigen.ren.modules.docgen.gb_vs;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
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
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderPolicy.POLICY_AND_CERT;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_FALSE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_TRUE;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.ActiveAndRecentlyExpiredPolicies.POLICY;
import static com.exigen.ren.utils.DBHelper.EventName.REN_POLICY_ISSUE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyMasterDocumentGenerationForUT extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-42374"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyMasterDocumentGenerationForUT() {
        final String RATE_BASIS = "Monthly Tiered Price Per Participant";
        mainApp().open();

        createDefaultNonIndividualCustomer();
        caseProfile.create(tdSpecific().getTestData("TestData_WithTwoPlans_NoAndYesSubGroups"), groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.createPolicy(getDefaultVSMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_Policy").resolveLinks())
                .adjust(tdSpecific().getTestData("TestDataIssue").resolveLinks()));
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String groupName = PolicySummaryPage.labelCustomerName.getValue();

        LOGGER.info("TEST REN-42374: Step 1");
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        LocalDateTime policyEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().withDayOfMonth(1);
        String documentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        String documentName = String.format("%s_VISION_%s-Policy-%s_%s_%s", customerNumber, groupName.toUpperCase(), policyEffectiveDate.format(YYYY_MM_DD), policyNumber, documentDate);
        assertThat(Efolder.isDocumentExistStartsContains(POLICY_AND_CERT.getName() + "/" + POLICY.getName(), customerNumber + "_", documentName))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-40972: Step 2");
        XmlValidator xmlValidator = DBHelper.getDocument(customerNumber, DBHelper.EntityType.CUSTOMER, REN_POLICY_ISSUE);

        //1
        xmlValidator.checkDocument(SITUS_STATE_VALUE, "UT");
        //4
        xmlValidator.checkDocument(DEPENDENT_MAX_AGE, "18");
        //4a
        xmlValidator.checkDocument(INCLUDE_DISABLED_DEP, VALUE_TRUE);
        //6-8
        xmlValidator.checkDocument(INCLUDES_DOMESTIC_PARTNER, VALUE_TRUE);

        LOGGER.info("TEST REN-40972: Step 3");
        //A
        xmlValidator.checkDocument(NETWORK, "Choice");
        //1, 3
        xmlValidator.checkDocument(POLICY_EFFECTIVE_DT, policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z));
        //2
        xmlValidator.checkDocument(POLICY_HOLDER_NAME, groupName);
        //4
        xmlValidator.checkDocument(NEXT_RENEWAL_EFF_DATE, policyEffectiveDate.plusYears(1).format(YYYY_MM_DD_HH_MM_SS_Z));
        //5
        xmlValidator.checkDocument(CUSTOMER_ID, customerNumber);

        //5-70 for the first plan
        ImmutableMap<String, String> requestInformationSection1 = new ImmutableMap.Builder<String, String>()
                .put(PLAN_NAME.get(), "A La Carte")
                .put(CLASS_NAME.get(), "1")
                .put(MIN_HOURS_PER_WEEK.get(), "35")
                .put(ALLOW_MEMBER_ON_CERT.get(), VALUE_TRUE)
                .put(INCLUDES_DOMESTIC_PARTNER.get(), VALUE_TRUE)
                .put(NETWORK.get(), "Choice")
                .put(COMBINED_COPAY_COVERED.get(), VALUE_FALSE)
                .put(SEPARATE_COPAY_COVERED.get(), VALUE_TRUE)
                .put(EXAM_MATERIALS_FIELD_VALUE1.get(), "$10")
                .put(EXAM_MATERIALS_FIELD_VALUE2.get(), "$25")
                .put(SEPARATE_COPAY_NO_EXAM.get(), VALUE_FALSE)
                .put(COMBINED_COPAY_NOT_COVERED.get(), VALUE_FALSE)
                .put(EXAM_UP_TO.get(), "45.00")
                .put(EXAM_LENSES_FRAME_FIELD_VALUE1.get(), "12")
                .put(FREQUENCY_IN_PDF.get(), "calendar months")
                .put(EXAM_LENSES_FRAME_FIELD_VALUE2.get(), "12")
                .put(VIS14_SINGLE_VISION_LENSES.get(), "30.00")
                .put(VIS14_LINED_BIFOCAL_LENSES.get(), "50.00")
                .put(VIS14_LINED_TRIFOCAL_LENSES.get(), "65.00")
                .put(VIS14_LENTICULAR_LENSES.get(), "100.00")
                .put(FRAME_UP_TO.get(), "70.00")
                .put(EXAM_LENSES_FRAME_FIELD_VALUE3.get(), "12")
                .put(CONTACTS_UP_TO_FIELD_VALUE2.get(), "$210")
                .put(CONTACT_LENSES_ALLOWANCE.get(), "$130")
                .put(CONTACTS_UP_TO_FIELD_VALUE1.get(), "$105")
                .put(EMPTY_SCRATCH_COATING_FACTOR.get(), VALUE_TRUE)
                .put(EMPTY_SAFETY_GLASSES_FACTOR.get(), VALUE_TRUE)
                .put(EMPTY_PHOTOCHROMIC_LENSES_FACTOR.get(), VALUE_TRUE)
                .put(PROGRESSIVE_LENSES_IN_FULL.get(), VALUE_FALSE)
                .put(WITH_TIER_DATA.get(), VALUE_FALSE)
                .put(FULL_PRCP_CTB.get(), VALUE_FALSE)
                .put(CTB_WITH_FULL_EMP_AND_NO_DEP_PTCP.get(), VALUE_FALSE)
                .put(BENEFIT_END_ON_CD.get(), "LASTDATEOFEMPLOY")
                .put(WHOLE_CALENDAR_YEAR.get(), VALUE_FALSE)
                .put(CURRENT_START_DT.get(), policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z))
                .put(CURRENT_EN_DT.get(), policyEffectiveDate.plusYears(1).minusDays(1).format(YYYY_MM_DD_HH_MM_SS_Z))
                .build();

        getRequestInformationSection(xmlValidator, "1").checkDocument(requestInformationSection1);

        //5-70 for the second plan
        ImmutableMap<String, String> requestInformationSection2 = new ImmutableMap.Builder<String, String>()
                .put(PLAN_NAME.get(), "10/25 Plan B 130")
                .put(CLASS_NAME.get(), "1")
                .put(MIN_HOURS_PER_WEEK.get(), "35")
                .put(ALLOW_MEMBER_ON_CERT.get(), VALUE_TRUE)
                .put(INCLUDES_DOMESTIC_PARTNER.get(), VALUE_TRUE)
                .put(NETWORK.get(), "Choice")
                .put(COMBINED_COPAY_COVERED.get(), VALUE_FALSE)
                .put(SEPARATE_COPAY_COVERED.get(), VALUE_TRUE)
                .put(EXAM_MATERIALS_FIELD_VALUE1.get(), "$10")
                .put(EXAM_MATERIALS_FIELD_VALUE2.get(), "$25")
                .put(SEPARATE_COPAY_NO_EXAM.get(), VALUE_FALSE)
                .put(COMBINED_COPAY_NOT_COVERED.get(), VALUE_FALSE)
                .put(EXAM_UP_TO.get(), "45.00")
                .put(EXAM_LENSES_FRAME_FIELD_VALUE1.get(), "12")
                .put(FREQUENCY_IN_PDF.get(), "calendar months")
                .put(EXAM_LENSES_FRAME_FIELD_VALUE2.get(), "12")
                .put(VIS14_SINGLE_VISION_LENSES.get(), "30.00")
                .put(VIS14_LINED_BIFOCAL_LENSES.get(), "50.00")
                .put(VIS14_LINED_TRIFOCAL_LENSES.get(), "65.00")
                .put(VIS14_LENTICULAR_LENSES.get(), "100.00")
                .put(FRAME_UP_TO.get(), "70.00")
                .put(EXAM_LENSES_FRAME_FIELD_VALUE3.get(), "12")
                .put(CONTACTS_UP_TO_FIELD_VALUE2.get(), "$210")
                .put(CONTACT_LENSES_ALLOWANCE.get(), "$130")
                .put(CONTACTS_UP_TO_FIELD_VALUE1.get(), "$105")
                .put(EMPTY_SCRATCH_COATING_FACTOR.get(), VALUE_TRUE)
                .put(EMPTY_SAFETY_GLASSES_FACTOR.get(), VALUE_TRUE)
                .put(EMPTY_PHOTOCHROMIC_LENSES_FACTOR.get(), VALUE_TRUE)
                .put(PROGRESSIVE_LENSES_IN_FULL.get(), VALUE_FALSE)
                .put(WITH_TIER_DATA.get(), VALUE_TRUE)
                .put(String.format(RATE_INFOS_ITEM_TIER_NAME.get(), "1"), "Employee + 1")
                .put(String.format(RATE_INFOS_ITEM_TIER_NAME.get(), "2"), "Employee + Family")
                .put(String.format(RATE_INFOS_ITEM_TIER_NAME.get(), "3"), "Employee Only")
                .put(String.format(RATE_INFOS_ITEM_RATE.get(), "1"), "14.00")
                .put(String.format(RATE_INFOS_ITEM_RATE.get(), "2"), "22.55")
                .put(String.format(RATE_INFOS_ITEM_RATE.get(), "3"), "7.00")
                .put(String.format(RATE_INFOS_ITEM_RATE_BASIS.get(), "1"), RATE_BASIS)
                .put(String.format(RATE_INFOS_ITEM_RATE_BASIS.get(), "2"), RATE_BASIS)
                .put(String.format(RATE_INFOS_ITEM_RATE_BASIS.get(), "3"), RATE_BASIS)
                .put(FULL_PRCP_CTB.get(), VALUE_TRUE)
                .put(CTB_WITH_FULL_EMP_AND_NO_DEP_PTCP.get(), VALUE_FALSE)
                .put(BENEFIT_END_ON_CD.get(), "LASTDATEOFEMPLOY")
                .put(WHOLE_CALENDAR_YEAR.get(), VALUE_FALSE)
                .put(CURRENT_START_DT.get(), policyEffectiveDate.format(YYYY_MM_DD_HH_MM_SS_Z))
                .put(CURRENT_EN_DT.get(), policyEffectiveDate.plusYears(1).minusDays(1).format(YYYY_MM_DD_HH_MM_SS_Z))
                .build();

        getRequestInformationSection(xmlValidator, "2").checkDocument(requestInformationSection2);

        LOGGER.info("TEST REN-40972: Step 4");
        //3
        xmlValidator.checkDocument(EXAM_MATERIALS_FIELD_VALUE1, "$10");
    }

    private XmlValidator getRequestInformationSection(XmlValidator xmlValidator, String sectionNumber) {
        String xmlDocument = xmlValidator.convertNodeToString(xmlValidator.findNode(String.format(PLAN_DATA_ITEMS.get(), sectionNumber)));

        CustomAssertions.assertThat(xmlDocument).as("Section 'planDataItems' not found by item '" + sectionNumber + "'").isNotEmpty();
        return new XmlValidator(xmlDocument);
    }
}
