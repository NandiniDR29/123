package com.exigen.ren.modules.docgen.gb_di_ltd;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_SGR;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_100_MONTHLY_COVERED_PAYROLL;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.CLASSIFICATION_GROUP;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupPopup.CLASS_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.SPONSOR_PARTICIPANT_FUNDING_STRUCTURE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab.*;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.CustomerInformation.NAME_LEGAL;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.tableCustomerInformation;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyGenerationProposalDocumentOnePlanTwoClassesFL extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private String groupNameFirst;
    private String groupNameSecond;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-15581", component = POLICY_GROUPBENEFITS)
    public void testPolicyGenerationProposalDocumentOnePlanTwoClassesFL() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createCaseProfile();
        createQuote();

        longTermDisabilityMasterPolicy.propose().perform(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY));
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String caseProfileNumber = labelCaseProfileNumber.getValue();
        String groupName = tableCustomerInformation.getRow(1).getCell(NAME_LEGAL.getName()).getValue().toUpperCase();

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("TEST: Step #2");
        validateBenefitCostInfoItems(xmlValidator, policyNumber);

        LOGGER.info("TEST: Step #3-4");
        LOGGER.info("TEST: Validate common section");
        xmlValidator.checkDocument(LTD7, groupName);

        LOGGER.info("TEST: Step #3-4");
        LOGGER.info("TEST: Validate quoteLTDInfos");
        Map<DocGenEnum.AllSections, String> mapQuoteInfoSection = new HashMap<>();
        mapQuoteInfoSection.put(LTD8, String.format("%sT00:00:00Z",
                TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().withDayOfMonth(1).toLocalDate().toString()));
        mapQuoteInfoSection.put(LTD8a, policyNumber);

        getQuoteInfoSection(xmlValidator, policyNumber).checkDocument(mapQuoteInfoSection);

        LOGGER.info("TEST: Step #3-5");
        LOGGER.info("TEST: Validate benefitDetailsInfoItems for className=" + groupNameFirst);
        TestData tdDataSource = tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY);
        tdDataSource.adjust(LTD9_1.name(), "1");
        tdDataSource.adjust(LTD9_2.name(), groupNameFirst);
        tdDataSource.adjust(LTD9_3.name(), String.format("Class 1: %s", groupNameFirst));

        getBenefitDetailsInfoSection(xmlValidator, policyNumber, LTD_SGR, groupNameFirst).checkDocument(tdDataSource);
        getBenefitDetailsInfoSection(xmlValidator, policyNumber, LTD_SGR, groupNameFirst).checkNodeNotPresent(LTD56, LTD57, LTD58, LTD59, LTD60);

        LOGGER.info("TEST: Step #3-5");
        LOGGER.info("TEST: Validate benefitDetailsInfoItems for className=" + groupNameSecond);
        tdDataSource.adjust(LTD9_1.name(), "2");
        tdDataSource.adjust(LTD9_2.name(), groupNameSecond);
        tdDataSource.adjust(LTD9_3.name(), String.format("Class 2: %s", groupNameSecond));

        getBenefitDetailsInfoSection(xmlValidator, policyNumber, LTD_SGR, groupNameSecond).checkDocument(tdDataSource);
        getBenefitDetailsInfoSection(xmlValidator, policyNumber, LTD_SGR, groupNameSecond).checkNodeNotPresent(LTD56, LTD57, LTD58, LTD59, LTD60);
    }

    private void createCaseProfile() {
        List<TestData> classificationManagementTabTestData = caseProfile.getDefaultTestData("CaseProfile", "TestData_WithTwoPlans_NoSubGroups")
                .getTestDataList(classificationManagementTab.getMetaKey());
        groupNameFirst = classificationManagementTabTestData.get(0)
                .getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NAME.getLabel());
        groupNameSecond = classificationManagementTabTestData.get(1)
                .getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NAME.getLabel());

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithTwoPlans_NoSubGroups"),
                longTermDisabilityMasterPolicy.getType());
    }

    private void createQuote() {
        List<TestData> planDefinitionTabTestDataList = longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER,
                TestDataKey.DEFAULT_TEST_DATA_KEY).getTestDataList(planDefinitionTab.getMetaKey());

        planDefinitionTabTestDataList.get(0).adjust(PlanDefinitionTabMetaData.PLAN.getLabel(), LTD_SGR);
        planDefinitionTabTestDataList.get(1).adjust(tdSpecific().getTestData("Adjust_PlanDefinitionTab_SGR", planDefinitionTab.getMetaKey()))
                .mask(TestData.makeKeyPath(SPONSOR_PARTICIPANT_FUNDING_STRUCTURE.getLabel(), PlanDefinitionTabMetaData.SponsorParticipantFundingStructureMetaData.SPONSOR_PAYMENT_MODE.getLabel()));

        List<TestData> listClassificationManagementTabTestData = ImmutableList.of(
                longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "ClassificationManagementTab_Without_Change_Using_Sub-Groups"),
                longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "ClassificationManagementTab_Without_Change_Using_Sub-Groups"));

        listClassificationManagementTabTestData.get(0).adjust(CLASSIFICATION_GROUP_NAME.getLabel(), groupNameFirst);
        listClassificationManagementTabTestData.get(0).adjust("PlanKey", String.format("%1$s-%1$s", LTD_SGR));
        listClassificationManagementTabTestData.get(1).adjust(CLASSIFICATION_GROUP_NAME.getLabel(), groupNameSecond);
        listClassificationManagementTabTestData.get(1).adjust("PlanKey", String.format("%1$s-%1$s", LTD_SGR));

        longTermDisabilityMasterPolicy.createQuoteViaUI(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(policyInformationTab.getMetaKey(), tdSpecific().getTestData("Adjust_PolicyInformationTab", policyInformationTab.getMetaKey()))
                .adjust(planDefinitionTab.getMetaKey(), planDefinitionTabTestDataList)
                .adjust(classificationManagementMpTab.getMetaKey(), listClassificationManagementTabTestData));
    }

    private void validateBenefitCostInfoItems(XmlValidator xmlValidator, String policyNumber) {
        LOGGER.info("TEST: Prepare test date for validation classificationItems for className=" + groupNameFirst);
        Currency modalPremiumCommon =
                new Currency(PolicySummaryPage.tableCoverageSummary.getRow(1).getCell(PolicyConstants.PolicyCoverageSummaryTable.MODAL_PREMIUM).getValue());

        longTermDisabilityMasterPolicy.policyInquiry().start();
        classificationManagementMpTab.navigateToTab();

        tableCoverageRelationships.getRow(TableConstants.CoverageRelationships.CLASS_NAME.getName(), groupNameFirst)
                .getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        String classificationGroupNameFirst = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(CLASSIFICATION_GROUP_NAME.getLabel())).getValue();
        int numberOfParticipantsFirst = Integer.parseInt(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(NUMBER_OF_PARTICIPANTS.getLabel())).getValue());
        Currency totalVolumeFirst = new Currency(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(TOTAL_VOLUME.getLabel())).getValue());
        String proposedRateFirst = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Proposed Rate")).getValue();

        LOGGER.info("TEST: Prepare test date for validation classificationItems for className=" + groupNameSecond);
        tableCoverageRelationships.getRow(TableConstants.CoverageRelationships.CLASS_NAME.getName(), groupNameSecond)
                .getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        String classificationGroupNameSecond =
                new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(CLASSIFICATION_GROUP_NAME.getLabel())).getValue();
        int numberOfParticipantsSecond = Integer.parseInt(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(NUMBER_OF_PARTICIPANTS.getLabel())).getValue());
        Currency totalVolumeSecond = new Currency(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(TOTAL_VOLUME.getLabel())).getValue());
        String proposedRateSecond = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Proposed Rate")).getValue();

        LOGGER.info("TEST: Prepare test date for validation common section benefitCostInfoItems");
        int numberOfParticipantsCommon = numberOfParticipantsFirst + numberOfParticipantsSecond;
        Currency totalVolumeCommon = totalVolumeFirst.add(totalVolumeSecond);

        LOGGER.info("TEST: Validate classificationItems for className=" + groupNameFirst);
        Map<DocGenEnum.AllSections, String> mapClassificationItems = new HashMap<>();
        mapClassificationItems.put(LTD1b, classificationGroupNameFirst);
        mapClassificationItems.put(LTD2, Integer.toString(numberOfParticipantsFirst));
        mapClassificationItems.put(LTD3, formatTotalVolume(totalVolumeFirst));
        mapClassificationItems.put(LTD4_1, formatProposedRate(proposedRateFirst));
        mapClassificationItems.put(LTD4_2, PER_100_MONTHLY_COVERED_PAYROLL);
        mapClassificationItems.put(LTD4_3, "false");

        premiumSummaryTab.navigateToTab();
        premiumSummaryCoveragesTable.getRow(1).getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName()).controls.links.getFirst().click(doubleWaiter);
        premiumSummaryByPayorCoveragesTable.getRow(1).getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName()).controls.links.getFirst().click(doubleWaiter);
        String modalPremium1 = new Currency(premiumSummaryClassNameTable.getRow(1).getCell(PremiumSummaryClassNameTable.MODAL_PREMIUM.getName()).getValue()).toPlainString();
        mapClassificationItems.put(LTD5, modalPremium1);

        getClassificationSection(xmlValidator, policyNumber, LTD_SGR, groupNameFirst).checkDocument(mapClassificationItems);

        LOGGER.info("TEST: Validate classificationItems for className=" + groupNameSecond);
        mapClassificationItems.clear();
        mapClassificationItems.put(LTD1b, classificationGroupNameSecond);
        mapClassificationItems.put(LTD2, Integer.toString(numberOfParticipantsSecond));
        mapClassificationItems.put(LTD3, formatTotalVolume(totalVolumeSecond));
        mapClassificationItems.put(LTD4_1, formatProposedRate(proposedRateSecond));
        mapClassificationItems.put(LTD4_2, PER_100_MONTHLY_COVERED_PAYROLL);
        mapClassificationItems.put(LTD4_3, "false");

        String modalPremium2 = new Currency(premiumSummaryClassNameTable.getRow(2).getCell(PremiumSummaryClassNameTable.MODAL_PREMIUM.getName()).getValue()).toPlainString();
        mapClassificationItems.put(LTD5, modalPremium2);

        getClassificationSection(xmlValidator, policyNumber, LTD_SGR, groupNameSecond).checkDocument(mapClassificationItems);

        LOGGER.info("TEST: Validate common section benefitCostInfoItems");
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoItems = new HashMap<>();
        mapBenefitCostInfoItems.put(LTD1_1a, LTD_SGR);
        mapBenefitCostInfoItems.put(STD1_4, "(guaranteed for 15 months***)");
        mapBenefitCostInfoItems.put(STD1_5_TOTAL_COVERED_LIVES, String.valueOf(numberOfParticipantsCommon));
        mapBenefitCostInfoItems.put(STD1_6_TOTAL_INSURANCE_VOLUME, formatTotalVolume(totalVolumeCommon));
        mapBenefitCostInfoItems.put(STD1_7_TOTAL_MONTHLY_PREMIUM, modalPremiumCommon.toPlainString());

        getBenefitCostInfoSection(xmlValidator, policyNumber, LTD_SGR).checkDocument(mapBenefitCostInfoItems);
    }
}
