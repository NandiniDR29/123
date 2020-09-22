package com.exigen.ren.modules.docgen.gb_tl;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
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

import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.enums.TableConstants.ClassificationSubGroupsAndRatingColumns.RATE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.CLASSIFICATION_GROUP;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupPopup.CLASS_NAME;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab.*;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.CustomerInformation.NAME_LEGAL;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.tableCustomerInformation;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestPolicyGenerationProposalDocument_EBL_DEBL_EBAD_EVL_SVL extends ValidationXMLBaseTestTL implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    private String policyNumber;
    private String groupNameFirst;
    private String groupNameSecond;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-20276", component = POLICY_GROUPBENEFITS)
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

        LOGGER.info("TEST: Validate classificationItems_VL_Employee section");
        validateClassificationVL_Employee(xmlValidator);

        LOGGER.info("TEST: Validate classificationItems_VL_Spouse section");
        validateClassificationVL_Spouse(xmlValidator, groupNameFirst);
        validateClassificationVL_Spouse(xmlValidator, groupNameSecond);

        LOGGER.info("TEST: Validate that sections are absent");
        getBenefitCostInfoSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN).checkNodeNotPresent(CLASSIFICATION_BADD_DEPENDENT_SECTION,
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

        LOGGER.info("TEST: Step #3");
        LOGGER.info("TEST: Validate benefitDetailsInfos_COMBINED_BL");
        getBenefitDetailsInfoCombinedBLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.BTL).checkDocument(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY));

        LOGGER.info("TEST: Validate ageReductions LIFE56, LIFE73");
        assertThat(getAgeReductionsBenefitDetailsInfoBLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.BTL))
                .containsSequence("Age 65 reduces to 65%", "Age 70 reduces to 50%");
        assertThat(getAgeReductionsBenefitDetailsInfoBADDSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.BTL))
                .containsSequence("Age 65 reduces to 65%", "Age 70 reduces to 40%", "Age 75 reduces to 25%");

        LOGGER.info("TEST: Step #4");
        LOGGER.info("TEST: Validate benefitDetailsInfos_VL");
        getBenefitDetailsInfoVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.VOL_BTL).checkDocument(tdSpecific().getTestData("TestData_VL"));

        LOGGER.info("TEST: Validate guaranteedDescList LIFE102");
        assertThat(getGuaranteedDescListBenefitDetailsInfoVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.VOL_BTL))
                .containsSequence("$100,000");

        LOGGER.info("TEST: Validate ageReductions LIFE103");
        assertThat(getAgeReductionsBenefitDetailsInfoVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.VOL_BTL))
                .containsSequence("N/A");

        LOGGER.info("TEST: Step #5");
        LOGGER.info("TEST: Validate benefitDetailsInfos_DVL for className=" + groupNameFirst);
        getBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.SP_VOL_BTL, groupNameFirst)
                .checkDocument(tdSpecific().getTestData("TestData_DVL_1"));

        LOGGER.info("TEST: Validate guaranteedDescList LIFE117");
        //Plan Definition -> Guaranteed Issue -> GI Amount, GI Amount At Age, Age Limited GI Amount
        //'<GI Amount>' "under age <GI Amount At Age>
        //<Age Limited GI Amount>' "at age <GI Amount At Age> and above';
        assertThat(getGuaranteedDescListBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.SP_VOL_BTL, groupNameFirst))
                .containsSequence("$20,000 under age 65", "$1,000 at age 65 and above");

        LOGGER.info("TEST: Validate ageReductions LIFE119");
        assertThat(getAgeReductionsBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.SP_VOL_BTL, groupNameFirst))
                .containsSequence("Age 65 reduces to 65%", "Age 70 reduces to 50%");

        LOGGER.info("TEST: Validate benefitDetailsInfos_DVL for className=" + groupNameSecond);
        getBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.SP_VOL_BTL, groupNameSecond)
                .checkDocument(tdSpecific().getTestData("TestData_DVL_2"));

        LOGGER.info("TEST: Validate guaranteedDescList LIFE117");
        assertThat(getGuaranteedDescListBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.SP_VOL_BTL, groupNameSecond))
                .containsSequence("$20,000 under age 65", "$1,000 at age 65 and above");

        LOGGER.info("TEST: Validate ageReductions LIFE119");
        assertThat(getAgeReductionsBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.SP_VOL_BTL, groupNameSecond))
                .containsSequence("Age 65 reduces to 65%", "Age 70 reduces to 50%");

        LOGGER.info("TEST: Step #6-7");
        LOGGER.info("TEST: Validate that section benefitDetailsInfos_VADD is absent");
        getBenefitDetailsInfoSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN).checkNodeNotPresent(BENEFIT_DETAILS_INFO_VADD_SECTION);

        LOGGER.info("TEST: Step #7");
        LOGGER.info("TEST: Validate benefitDetailsInfos_COMBINED_BL(Life91-Life97)");
        Map<DocGenEnum.AllSections, String> mapAgeBandedInfoSection_BL_ADD = new HashMap<>();
        mapAgeBandedInfoSection_BL_ADD.put(LIFE91_1, "true");
        mapAgeBandedInfoSection_BL_ADD.put(LIFE91_2, "BASIC LIFE & AD&D MONTHLY PREMIUM RATES - Age Banded");
        mapAgeBandedInfoSection_BL_ADD.put(LIFE94_1, "LIFE RATE Per $1,000");
        mapAgeBandedInfoSection_BL_ADD.put(LIFE95_1, "AD&D RATE Per $1,000");
        mapAgeBandedInfoSection_BL_ADD.put(LIFE95_3, "true");

        getBenefitDetailsInfoCombinedBLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.BTL).checkDocument(mapAgeBandedInfoSection_BL_ADD);

        LOGGER.info("TEST: Validate ageBandedInfoItems");
        validateAgeBandedInfoItems_BL_ADD(xmlValidator, policyNumber);

        LOGGER.info("TEST: Validate benefitDetailsInfos_VL(Life150-Life153)");
        Map<DocGenEnum.AllSections, String> mapAgeBandedInfoSection_VL = new HashMap<>();
        mapAgeBandedInfoSection_VL.put(LIFE150_1_1, "true");
        mapAgeBandedInfoSection_VL.put(LIFE150_1_2, "VOLUNTARY EMPLOYEE LIFE MONTHLY PREMIUM RATES - Age Banded");
        mapAgeBandedInfoSection_VL.put(LIFE152_1_1, "LIFE RATE Per $1,000");
        mapAgeBandedInfoSection_VL.put(LIFE153_1_1, "false");

        getBenefitDetailsInfoVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.VOL_BTL).checkDocument(mapAgeBandedInfoSection_VL);

        LOGGER.info("TEST: Validate ageBandedInfoItems");
        validateAgeBandedInfoItems_VL(xmlValidator, policyNumber);

        LOGGER.info("TEST: Validate benefitDetailsInfos_DVL(Life150-Life153) for className=" + groupNameFirst);
        Map<DocGenEnum.AllSections, String> mapAgeBandedInfoSection_DVL = new HashMap<>();
        mapAgeBandedInfoSection_DVL.put(LIFE150_1_1, "true");
        mapAgeBandedInfoSection_DVL.put(LIFE150_1_2, "VOLUNTARY DEPENDENT LIFE MONTHLY PREMIUM RATES - Age Banded");
        mapAgeBandedInfoSection_DVL.put(LIFE152_1_1, "LIFE RATE Per $1,000");
        mapAgeBandedInfoSection_DVL.put(LIFE153_1_1, "false");

        getBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.SP_VOL_BTL, groupNameFirst).checkDocument(mapAgeBandedInfoSection_DVL);

        LOGGER.info("TEST: Validate ageBandedInfoItems");
        validateAgeBandedInfoItems_DVL(xmlValidator, policyNumber, groupNameFirst);

        LOGGER.info("TEST: Validate benefitDetailsInfos_DVL(Life150-Life153) for className=" + groupNameSecond);
        getBenefitDetailsInfoDVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN,
                CoveragesConstants.TermLifeCoveragesId.SP_VOL_BTL, groupNameSecond).checkDocument(mapAgeBandedInfoSection_DVL);

        LOGGER.info("TEST: Validate ageBandedInfoItems");
        validateAgeBandedInfoItems_DVL(xmlValidator, policyNumber, groupNameSecond);
    }

    private void createCaseProfile() {
        List<TestData> classificationManagementTabTestData = caseProfile.getDefaultTestData("CaseProfile", "TestData_WithTwoPlansAutoSubGroup")
                .getTestDataList(classificationManagementTab.getMetaKey());

        groupNameFirst = classificationManagementTabTestData.get(0)
                .getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NAME.getLabel());
        groupNameSecond = classificationManagementTabTestData.get(1)
                .getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NAME.getLabel());

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithTwoPlansAutoSubGroup"),
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
        Waiters.AJAX.go();
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

    private void validateClassificationVL_Employee(XmlValidator xmlValidator) {
        Waiters.AJAX.go();
        classificationManagementMpTab.navigateToTab();
        Map<String, String> mapValuesFromPlanDefinition = getValuesFromClassificationManagement(VOL_BTL, groupNameSecond);
        premiumSummaryTab.navigateToTab();
        Currency modalPremium = getCommonModalPremiumByPair(VOL_BTL);

        Map<DocGenEnum.AllSections, String> mapClassificationVL_EmployeeSection = new HashMap<>();
        mapClassificationVL_EmployeeSection.put(LIFE22, groupNameSecond);
        mapClassificationVL_EmployeeSection.put(LIFE23,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()));
        mapClassificationVL_EmployeeSection.put(LIFE24_2,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()));
        mapClassificationVL_EmployeeSection.put(LIFE25_1, "true");
        mapClassificationVL_EmployeeSection.put(LIFE25_3, "Per $1,000");
        mapClassificationVL_EmployeeSection.put(LIFE26, modalPremium.toPlainString());

        getClassificationVL_EmployeeSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, groupNameSecond).checkDocument(mapClassificationVL_EmployeeSection);
    }

    private void validateClassificationVL_Spouse(XmlValidator xmlValidator, String groupName) {
        doubleWaiter.go();
        classificationManagementMpTab.navigateToTab();
        Map<String, String> mapValuesFromPlanDefinition = getValuesFromClassificationManagement(SP_VOL_BTL, groupName);
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY.get(), Waiters.AJAX.then(Waiters.SLEEP(5000)).then(Waiters.AJAX)); //Additional time to wait for rating report
        Currency modalPremium = getModalPremiumByClassName(SP_VOL_BTL, groupName);

        Map<DocGenEnum.AllSections, String> mapClassificationVL_EmployeeSection = new HashMap<>();
        mapClassificationVL_EmployeeSection.put(LIFE27, groupName);
        mapClassificationVL_EmployeeSection.put(LIFE28,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()));
        mapClassificationVL_EmployeeSection.put(LIFE29_2,
                mapValuesFromPlanDefinition.get(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()));
        mapClassificationVL_EmployeeSection.put(LIFE30_USE_CLASS_SUB_GROUP, "true");
        mapClassificationVL_EmployeeSection.put(LIFE30_RATE_BASIC, "Per $1,000");
        mapClassificationVL_EmployeeSection.put(LIFE31, modalPremium.toPlainString());

        getClassificationVL_SpouseSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, groupName).checkDocument(mapClassificationVL_EmployeeSection);
    }

    private void validateAgeBandedInfoItems_BL_ADD(XmlValidator xmlValidator, String policyNumber) {
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

    private void validateAgeBandedInfoItems_VL(XmlValidator xmlValidator, String policyNumber) {
        coveragesTable.getRow(PolicyConstants.PolicyGroupCoveragesTable.COVERAGE_NAME, VOL_BTL).getCell(PolicyConstants.PolicyGroupCoveragesTable.COVERAGE_NAME).click();
        String proposedRateSubGroup = tableClassificationSubGroupsAndRatingInfo.getRow(1)
                .getCell(RATE.getName()).getValue();

        Map<DocGenEnum.AllSections, String> mapAgeBandedInfoSection = new HashMap<>();
        mapAgeBandedInfoSection.put(LIFE151_1, "Under 25");
        mapAgeBandedInfoSection.put(LIFE152_1_2, formatProposedRate(proposedRateSubGroup));
        mapAgeBandedInfoSection.put(LIFE153_1_3, "0.00");

        getAgeBandedInfoVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.VOL_BTL, "Under 25").checkDocument(mapAgeBandedInfoSection);
    }

    private void validateAgeBandedInfoItems_DVL(XmlValidator xmlValidator, String policyNumber, String groupName) {
        coveragesTable.getRow(PolicyConstants.PolicyGroupCoveragesTable.COVERAGE_NAME, SP_VOL_BTL).getCell(PolicyConstants.PolicyGroupCoveragesTable.COVERAGE_NAME).click();
        tableCoverageRelationships.getRow(TableConstants.CoverageRelationships.CLASS_NAME.getName(), groupName)
                .getCell(6).controls.links.get(ActionConstants.CHANGE).click();

        String proposedRateSubGroup = tableClassificationSubGroupsAndRatingInfo.getRow(1)
                .getCell(RATE.getName()).getValue();

        Map<DocGenEnum.AllSections, String> mapAgeBandedInfoSection = new HashMap<>();
        mapAgeBandedInfoSection.put(LIFE151_1, "Under 25");
        mapAgeBandedInfoSection.put(LIFE152_1_2, formatProposedRate(proposedRateSubGroup));
        mapAgeBandedInfoSection.put(LIFE153_1_3, "0.00");

        getAgeBandedInfoDVLSection(xmlValidator, policyNumber, BASIC_LIFE_PLAN, CoveragesConstants.TermLifeCoveragesId.SP_VOL_BTL, groupName, "Under 25")
                .checkDocument(mapAgeBandedInfoSection);
    }
}
