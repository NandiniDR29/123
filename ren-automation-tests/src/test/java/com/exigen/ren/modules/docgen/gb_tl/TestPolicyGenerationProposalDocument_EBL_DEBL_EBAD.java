package com.exigen.ren.modules.docgen.gb_tl;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.main.enums.CoveragesConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.enums.TableConstants.ClassificationSubGroupsAndRatingColumns.RATE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.CLASSIFICATION_GROUP;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupPopup.CLASS_NAME;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab.coveragesTable;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.CustomerInformation.NAME_LEGAL;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.tableCustomerInformation;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyGenerationProposalDocument_EBL_DEBL_EBAD extends ValidationXMLBaseTestTL implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    private String policyNumber;
    private String groupNameFirst;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20134", "REN-20209"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyGenerationProposalDocument() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createCaseProfile();
        termLifeInsuranceMasterPolicy.createQuoteViaUI(tdSpecific().getTestData("TestData_CreateQuote"));
        termLifeInsuranceMasterPolicy.propose().perform(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY));
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String caseProfileNumber = labelCaseProfileNumber.getValue();

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("TEST: Step #1");
        String groupName = tableCustomerInformation.getRow(1).getCell(NAME_LEGAL.getName()).getValue().toUpperCase();

        LOGGER.info("TEST: Validate classificationItems_BL_Employee section");
        termLifeInsuranceMasterPolicy.policyInquiry().start();
        validateClassificationBL_Employee(xmlValidator);

        LOGGER.info("TEST: Validate classificationItems_BL_Dependent section");
        validateClassificationBL_Dependent(xmlValidator);

        LOGGER.info("TEST: Validate classificationItems_BADD_Employee section");
        validateClassificationBADD_Employee(xmlValidator);

        LOGGER.info("TEST: Validate that sections are absent");
        getBenefitCostInfoSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN).checkNodeNotPresent(CLASSIFICATION_BADD_DEPENDENT_SECTION,
                CLASSIFICATION_VL_EMPLOYEE_SECTION,
                CLASSIFICATION_VL_SPOUSE_SECTION,
                CLASSIFICATION_VL_CHILD_SECTION,
                CLASSIFICATION_VADD_EMPLOYEE_SECTION,
                CLASSIFICATION_VADD_DEPENDENT_SECTION);

        LOGGER.info("TEST: Step #2");
        LOGGER.info("TEST: Validate common section");
        xmlValidator.checkDocument(LIFE48, groupName);

        LOGGER.info("TEST: Validate quoteTLInfos");
        Map<DocGenEnum.AllSections, String> mapQuoteInfoSection = new HashMap<>();
        mapQuoteInfoSection.put(LIFE49, String.format("%sT00:00:00Z",
                TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().withDayOfMonth(1).toLocalDate().toString()));
        mapQuoteInfoSection.put(LIFE49a, policyNumber);

        getQuoteInfoSection(xmlValidator, policyNumber).checkDocument(mapQuoteInfoSection);

        LOGGER.info("TEST: Validate benefitCostInfoItems");
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoSection = new HashMap<>();
        mapBenefitCostInfoSection.put(LIFE5a, "(guaranteed for 2 years***)");
        mapBenefitCostInfoSection.put(LIFE49b, BASIC_LIFE_PLAN);

        getBenefitCostInfoSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN).checkDocument(mapBenefitCostInfoSection);

        LOGGER.info("TEST: Validate benefitDetailsInfos_COMBINED_BL");
        getBenefitDetailsInfoCombinedBLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.BTL).checkDocument(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY));

        LOGGER.info("TEST: Validate ageReductions LIFE56, LIFE73");
        assertThat(getAgeReductionsBenefitDetailsInfoBLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.BTL))
                .containsSequence("N/A");
        assertThat(getAgeReductionsBenefitDetailsInfoBADDSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.BTL))
                .containsSequence("Age 65 reduces to 65%", "Age 70 reduces to 40%", "Age 75 reduces to 25%");

        LOGGER.info("TEST: Step #3");
        LOGGER.info("TEST: Validate benefitDetailsInfos_COMBINED_BL");
        Map<DocGenEnum.AllSections, String> mapAgeBandedInfoSection = new HashMap<>();
        mapAgeBandedInfoSection.put(LIFE91_1, "true");
        mapAgeBandedInfoSection.put(LIFE91_2, "BASIC LIFE & AD&D MONTHLY PREMIUM RATES - Age Banded");
        mapAgeBandedInfoSection.put(LIFE94_1, "LIFE RATE Per $1,000");
        mapAgeBandedInfoSection.put(LIFE95_1, "AD&D RATE Per $1,000");
        mapAgeBandedInfoSection.put(LIFE95_3, "true");

        getBenefitDetailsInfoCombinedBLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.BTL).checkDocument(mapAgeBandedInfoSection);

        LOGGER.info("TEST: Validate ageBandedInfoItems");
        validateAgeBandedInfoItems(xmlValidator, policyNumber);
    }

    private void createCaseProfile() {
        List<TestData> classificationManagementTabTestData = caseProfile.getDefaultTestData("CaseProfile", "TestData_PolicyCreationWithTwoPlans")
                .getTestDataList(classificationManagementTab.getMetaKey());

        groupNameFirst = classificationManagementTabTestData.get(0)
                .getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NAME.getLabel());

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_PolicyCreationWithTwoPlans"),
                termLifeInsuranceMasterPolicy.getType());
    }

    private void validateClassificationBL_Employee(XmlValidator xmlValidator) {
        classificationManagementMpTab.navigateToTab();
        Map<String, String> mapValuesFromPlanDefinition = getValuesFromClassificationManagement(BTL, groupNameFirst);
        premiumSummaryTab.navigateToTab();
        Currency modalPremium = getCommonModalPremiumByPair(BTL);

        Map<DocGenEnum.AllSections, String> mapClassificationBL_EmployeeSection = new HashMap<>();
        mapClassificationBL_EmployeeSection.put(LIFE2, groupNameFirst);
        mapClassificationBL_EmployeeSection.put(LIFE3,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()));
        mapClassificationBL_EmployeeSection.put(LIFE4_2,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()));
        mapClassificationBL_EmployeeSection.put(LIFE5_USE_CLASS_SUB_GROUP, "true");
        mapClassificationBL_EmployeeSection.put(LIFE5_RATE_BASIC, "Per $1,000");
        mapClassificationBL_EmployeeSection.put(LIFE6, modalPremium.toPlainString());

        getClassificationBL_EmployeeSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, groupNameFirst).checkDocument(mapClassificationBL_EmployeeSection);
    }

    private void validateClassificationBL_Dependent(XmlValidator xmlValidator) {
        classificationManagementMpTab.navigateToTab();
        Map<String, String> mapValuesFromPlanDefinition = getValuesFromClassificationManagement(DEP_BTL, groupNameFirst);
        premiumSummaryTab.navigateToTab();
        Currency modalPremium = getCommonModalPremiumByPair(DEP_BTL);

        Map<DocGenEnum.AllSections, String> mapClassificationBL_DependentSection = new HashMap<>();
        mapClassificationBL_DependentSection.put(LIFE7, groupNameFirst);
        mapClassificationBL_DependentSection.put(LIFE8,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()));
        mapClassificationBL_DependentSection.put(LIFE9_2,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()));
        mapClassificationBL_DependentSection.put(LIFE10_1, "false");
        mapClassificationBL_DependentSection.put(LIFE10_3, "Per Unit");
        mapClassificationBL_DependentSection.put(LIFE11, modalPremium.toPlainString());

        getClassificationBL_DependentSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, groupNameFirst).checkDocument(mapClassificationBL_DependentSection);
    }

    private void validateClassificationBADD_Employee(XmlValidator xmlValidator) {
        Waiters.AJAX.go();
        classificationManagementMpTab.navigateToTab();
        Map<String, String> mapValuesFromPlanDefinition = getValuesFromClassificationManagement(ADD, groupNameFirst);
        premiumSummaryTab.navigateToTab();
        Currency modalPremium = getCommonModalPremiumByPair(ADD);

        Map<DocGenEnum.AllSections, String> mapClassificationBADD_EmployeeSection = new HashMap<>();
        mapClassificationBADD_EmployeeSection.put(LIFE12, groupNameFirst);
        mapClassificationBADD_EmployeeSection.put(LIFE13,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()));
        mapClassificationBADD_EmployeeSection.put(LIFE14,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()));
        mapClassificationBADD_EmployeeSection.put(LIFE15_1, "false");
        mapClassificationBADD_EmployeeSection.put(LIFE15_2,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.RATE.getName()));
        mapClassificationBADD_EmployeeSection.put(LIFE15_3, "Per $1,000");
        mapClassificationBADD_EmployeeSection.put(LIFE16, modalPremium.toPlainString());

        getClassificationBADD_EmployeeSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, groupNameFirst).checkDocument(mapClassificationBADD_EmployeeSection);
    }

    private void validateAgeBandedInfoItems(XmlValidator xmlValidator, String policyNumber) {
        Waiters.AJAX.go();
        classificationManagementMpTab.navigateToTab();
        coveragesTable.getRow(PolicyConstants.PolicyGroupCoveragesTable.COVERAGE_NAME, BTL).getCell(PolicyConstants.PolicyGroupCoveragesTable.COVERAGE_NAME).click();
        String proposedRateSubGroup = tableClassificationSubGroupsAndRatingInfo.getRow(1)
                .getCell(RATE.getName()).getValue();

        coveragesTable.getRow(PolicyConstants.PolicyGroupCoveragesTable.COVERAGE_NAME, ADD).getCell(PolicyConstants.PolicyGroupCoveragesTable.COVERAGE_NAME).click();
        String proposedRateADD = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Proposed Rate")).getValue();

        Map<DocGenEnum.AllSections, String> mapAgeBandedInfoSection = new HashMap<>();
        mapAgeBandedInfoSection.put(LIFE93, "Under 25");
        mapAgeBandedInfoSection.put(LIFE94_2, formatProposedRate(proposedRateSubGroup));
        mapAgeBandedInfoSection.put(LIFE95_2, formatProposedRate(proposedRateADD));

        getAgeBandedInfoBLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.BTL, "Under 25").checkDocument(mapAgeBandedInfoSection);
    }
}
