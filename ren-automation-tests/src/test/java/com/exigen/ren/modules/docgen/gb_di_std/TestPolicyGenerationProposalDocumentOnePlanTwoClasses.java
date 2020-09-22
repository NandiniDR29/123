package com.exigen.ren.modules.docgen.gb_di_std;

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
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
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
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.NC;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupPopup.CLASS_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.CLASSIFICATION_GROUP;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.CustomerInformation.NAME_LEGAL;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.tableCustomerInformation;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyGenerationProposalDocumentOnePlanTwoClasses extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private String groupNameFirst;
    private String groupNameSecond;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-15426"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyGenerationProposalDocumentOnePlanTwoClasses() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createCaseProfile();
        createQuote();

        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String caseProfileNumber = labelCaseProfileNumber.getValue();
        String groupName = tableCustomerInformation.getRow(1).getCell(NAME_LEGAL.getName()).getValue().toUpperCase();

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("TEST: Validate benefitCostInfoItems and classificationItems");
        validateBenefitCostInfoItems(xmlValidator, policyNumber);

        LOGGER.info("TEST: Validate common section");
        xmlValidator.checkDocument(STD8, groupName);

        LOGGER.info("TEST: Validate quoteSTDInfos");
        Map<DocGenEnum.AllSections, String> mapQuoteInfoSection = new HashMap<>();
        mapQuoteInfoSection.put(STD9, String.format("%sT00:00:00Z",
                TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().withDayOfMonth(1).toLocalDate().toString()));
        mapQuoteInfoSection.put(STD1b_2_POLICY_NUMBER, policyNumber);

        getQuoteInfoSection(xmlValidator, policyNumber).checkDocument(mapQuoteInfoSection);

        LOGGER.info("TEST: Validate benefitDetailsInfoItems for className=" + groupNameFirst);
        TestData tdDataSource = tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY);
        tdDataSource.adjust(STD10_1_CLASSSEQ_NUM.name(), "1");
        tdDataSource.adjust(STD10_2.name(), groupNameFirst);
        tdDataSource.adjust(STD10_3_CLASS_DISPLAY_IN_PDF.name(), String.format("Class 1: %s", groupNameFirst));

        getBenefitDetailsInfoSection(xmlValidator, policyNumber, NC, groupNameFirst).checkDocument(tdDataSource);
        getBenefitDetailsInfoSection(xmlValidator, policyNumber, NC, groupNameFirst).checkNodeNotPresent(STD30, STD31, STD32, STD33, STD34);

        LOGGER.info("TEST: Step #3-5");
        LOGGER.info("TEST: Validate benefitDetailsInfoItems for className=" + groupNameSecond);
        tdDataSource.adjust(STD10_1_CLASSSEQ_NUM.name(), "2");
        tdDataSource.adjust(STD10_2.name(), groupNameSecond);
        tdDataSource.adjust(STD10_3_CLASS_DISPLAY_IN_PDF.name(), String.format("Class 2: %s", groupNameSecond));

        getBenefitDetailsInfoSection(xmlValidator, policyNumber, NC, groupNameSecond).checkDocument(tdDataSource);
        getBenefitDetailsInfoSection(xmlValidator, policyNumber, NC, groupNameSecond).checkNodeNotPresent(STD30, STD31, STD32, STD33, STD34);

    }

    private void createCaseProfile() {
        List<TestData> classificationManagementTabTestData = caseProfile
                .getDefaultTestData("CaseProfile", "TestData_WithTwoPlans_NoSubGroups")
                .getTestDataList(classificationManagementTab.getMetaKey());

        groupNameFirst = classificationManagementTabTestData.get(0)
                .getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NAME.getLabel());
        groupNameSecond = classificationManagementTabTestData.get(1)
                .getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NAME.getLabel());


        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithTwoPlans_NoSubGroups"),
                shortTermDisabilityMasterPolicy.getType());
    }

    private void createQuote() {
        List<TestData> planDefinitionTabTestDataList = shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER,
                TestDataKey.DEFAULT_TEST_DATA_KEY).getTestDataList(planDefinitionTab.getMetaKey());

        planDefinitionTabTestDataList.get(1).adjust(tdSpecific().getTestData("Adjust_PlanDefinitionTab_NC", planDefinitionTab.getMetaKey()));

        List<TestData> listClassificationManagementTabTestData = ImmutableList.of(
                shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "ClassificationManagementTab_Without_Change_Using_Sub-Groups"),
                shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "ClassificationManagementTab_Without_Change_Using_Sub-Groups"));

        listClassificationManagementTabTestData.get(0).adjust(CLASSIFICATION_GROUP_NAME.getLabel(), groupNameFirst);
        listClassificationManagementTabTestData.get(0).adjust("PlanKey", String.format("%1$s-%1$s", NC));
        listClassificationManagementTabTestData.get(1).adjust(CLASSIFICATION_GROUP_NAME.getLabel(), groupNameSecond);
        listClassificationManagementTabTestData.get(1).adjust("PlanKey", String.format("%1$s-%1$s", NC));

        shortTermDisabilityMasterPolicy.createQuoteViaUI(getDefaultSTDMasterPolicyData()
                .adjust(policyInformationTab.getMetaKey(), tdSpecific().getTestData("Adjust_PolicyInformationTab", policyInformationTab.getMetaKey()))
                .adjust(planDefinitionTab.getMetaKey(), planDefinitionTabTestDataList)
                .adjust(classificationManagementMpTab.getMetaKey(), listClassificationManagementTabTestData));
    }

    private void validateBenefitCostInfoItems(XmlValidator xmlValidator, String policyNumber) {
        LOGGER.info("TEST: Prepare test date for validation classificationItems for className=" + groupNameFirst);
        Currency modalPremiumFirst =
                new Currency(PolicySummaryPage.tableCoverageSummary.getRow(1).getCell(PolicyConstants.PolicyCoverageSummaryTable.MODAL_PREMIUM).getValue());
        Currency modalPremiumSecond =
                new Currency(PolicySummaryPage.tableCoverageSummary.getRow(2).getCell(PolicyConstants.PolicyCoverageSummaryTable.MODAL_PREMIUM).getValue());

        shortTermDisabilityMasterPolicy.policyInquiry().start();
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

        String classificationGroupNameSecond = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(CLASSIFICATION_GROUP_NAME.getLabel())).getValue();
        int numberOfParticipantsSecond = Integer.parseInt(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(NUMBER_OF_PARTICIPANTS.getLabel())).getValue());
        Currency totalVolumeSecond = new Currency(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(TOTAL_VOLUME.getLabel())).getValue());
        String proposedRateSecond = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Proposed Rate")).getValue();

        LOGGER.info("TEST: Prepare test date for validation common section benefitCostInfoItems");
        int numberOfParticipantsCommon = numberOfParticipantsFirst + numberOfParticipantsSecond;
        Currency totalVolumeCommon = totalVolumeFirst.add(totalVolumeSecond);
        Currency modalPremiumCommon = modalPremiumFirst.add(modalPremiumSecond);

        LOGGER.info("TEST: Validate classificationItems for className=" + groupNameFirst);
        Map<DocGenEnum.AllSections, String> mapClassificationItems = new HashMap<>();
        mapClassificationItems.put(STD2, classificationGroupNameFirst);
        mapClassificationItems.put(STD3, Integer.toString(numberOfParticipantsFirst));
        mapClassificationItems.put(STD4, formatTotalVolume(totalVolumeFirst));
        mapClassificationItems.put(STD5_1_MONTHLY_RATE, formatProposedRate(proposedRateFirst));
        mapClassificationItems.put(STD5_2_RATE_BASIC, "Per $10 Total Weekly Benefit");
        mapClassificationItems.put(STD5_3_USE_CLASS_SUB_GROUP, "false");
        mapClassificationItems.put(STD6, modalPremiumCommon.multiply(numberOfParticipantsFirst)
                .divide(numberOfParticipantsCommon).toString().substring(1));

        getClassificationSection(xmlValidator, policyNumber, NC, groupNameFirst).checkDocument(mapClassificationItems);

        LOGGER.info("TEST: Validate classificationItems for className=" + groupNameSecond);
        mapClassificationItems.clear();
        mapClassificationItems.put(STD2, classificationGroupNameSecond);
        mapClassificationItems.put(STD3, Integer.toString(numberOfParticipantsSecond));
        mapClassificationItems.put(STD4, formatTotalVolume(totalVolumeSecond));
        mapClassificationItems.put(STD5_1_MONTHLY_RATE, formatProposedRate(proposedRateSecond));
        mapClassificationItems.put(STD5_2_RATE_BASIC, "Per $10 Total Weekly Benefit");
        mapClassificationItems.put(STD5_3_USE_CLASS_SUB_GROUP, "false");
        mapClassificationItems.put(STD6, modalPremiumCommon.multiply(numberOfParticipantsSecond).
                divide(numberOfParticipantsCommon).toString().substring(1));

        getClassificationSection(xmlValidator, policyNumber, NC, groupNameSecond).checkDocument(mapClassificationItems);

        LOGGER.info("TEST: Validate common section benefitCostInfoItems");
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoItems = new HashMap<>();
        mapBenefitCostInfoItems.put(STD1b_1_PLAN_NAME, NC);
        mapBenefitCostInfoItems.put(STD1_4, "(guaranteed for 15 months***)");
        mapBenefitCostInfoItems.put(STD1_5_TOTAL_COVERED_LIVES, String.valueOf(numberOfParticipantsCommon));
        mapBenefitCostInfoItems.put(STD1_6_TOTAL_INSURANCE_VOLUME, formatTotalVolume(totalVolumeCommon));
        mapBenefitCostInfoItems.put(STD1_7_TOTAL_MONTHLY_PREMIUM, modalPremiumCommon.toPlainString());

        getBenefitCostInfoSection(xmlValidator, policyNumber, NC).checkDocument(mapBenefitCostInfoItems);
    }
}
