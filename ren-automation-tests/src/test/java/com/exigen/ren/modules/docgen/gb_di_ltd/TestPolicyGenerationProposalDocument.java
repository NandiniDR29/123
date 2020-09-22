package com.exigen.ren.modules.docgen.gb_di_ltd;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_CON;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_NC;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.CLASSIFICATION_GROUP;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupPopup.CLASS_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.ClassificationManagementTabMetaData.CLASSIFICATION_GROUP_NAME;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyGenerationProposalDocument extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private String groupName;

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-18895", component = POLICY_GROUPBENEFITS)
    public void testPolicyGenerationCombinedProposalDocument() {
        mainApp().open();
        createDefaultNonIndividualCustomer();

        createCaseProfile();
        createQuote();

        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String caseProfileNumber = labelCaseProfileNumber.getValue();

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("TEST: Validate benefitCostInfoItems for plan CON");
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoItemsPlanCon = new HashMap<>();
        mapBenefitCostInfoItemsPlanCon.put(LTD1_1a, LTD_CON);
        mapBenefitCostInfoItemsPlanCon.put(LTD1b, groupName);

        getBenefitCostInfoSection(xmlValidator, policyNumber, LTD_CON).checkDocument(mapBenefitCostInfoItemsPlanCon);

        LOGGER.info("TEST: Validate benefitDetailsInfoItems for plan CON");
        Map<DocGenEnum.AllSections, String> mapBenefitDetailsInfoItemsPlanCon = new HashMap<>();
        mapBenefitDetailsInfoItemsPlanCon.put(LTD9_1, "1");
        mapBenefitDetailsInfoItemsPlanCon.put(LTD9_2, groupName);
        mapBenefitDetailsInfoItemsPlanCon.put(LTD9_3, String.format("Class 1: %s", groupName));

        getBenefitDetailsInfoSection(xmlValidator, policyNumber, LTD_CON).checkDocument(mapBenefitDetailsInfoItemsPlanCon);

        LOGGER.info("TEST: Validate benefitCostInfoItems for plan NC");
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoItemsPlanNC = new HashMap<>();
        mapBenefitCostInfoItemsPlanNC.put(LTD1_1a, LTD_NC);
        mapBenefitCostInfoItemsPlanNC.put(LTD1b, groupName);

        getBenefitCostInfoSection(xmlValidator, policyNumber, LTD_NC).checkDocument(mapBenefitCostInfoItemsPlanNC);

        LOGGER.info("TEST: Validate benefitDetailsInfoItems for plan NC");
        Map<DocGenEnum.AllSections, String> mapBenefitDetailsInfoItemsPlanNC = new HashMap<>();
        mapBenefitDetailsInfoItemsPlanNC.put(LTD9_1, "2");
        mapBenefitDetailsInfoItemsPlanNC.put(LTD9_2, groupName);
        mapBenefitDetailsInfoItemsPlanNC.put(LTD9_3, String.format("Class 1: %s", groupName));

        getBenefitDetailsInfoSection(xmlValidator, policyNumber, LTD_NC).checkDocument(mapBenefitDetailsInfoItemsPlanNC);
    }

    private void createCaseProfile() {
        List<TestData> classificationManagementTabTestData = caseProfile
                .getDefaultTestData("CaseProfile", "TestData_PolicyCreationWithTwoPlansWithoutSubGroup")
                .getTestDataList(classificationManagementTab.getMetaKey());

        groupName = classificationManagementTabTestData.get(0)
                .getValue(CLASSIFICATION_GROUP.getLabel(), CLASS_NAME.getLabel());

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_PolicyCreationWithTwoPlansWithoutSubGroup"),
                longTermDisabilityMasterPolicy.getType());
    }

    private void createQuote() {
        TestData planDefinitionTabTestData = tdSpecific()
                .getTestData("Adjust_PlanDefinitionTab_NC", planDefinitionTab.getMetaKey());

        List<TestData> listPlanDefinitionTabTestData = longTermDisabilityMasterPolicy
                .getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoPlans").getTestDataList(planDefinitionTab.getMetaKey());
        List<TestData> listClassificationManagementTabTestData = longTermDisabilityMasterPolicy
                .getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoPlans").getTestDataList(classificationManagementTab.getMetaKey());

        listPlanDefinitionTabTestData.get(2).adjust(planDefinitionTabTestData);
        listClassificationManagementTabTestData.get(0).adjust(CLASSIFICATION_GROUP_NAME.getLabel(), groupName);
        listClassificationManagementTabTestData.get(1).adjust(CLASSIFICATION_GROUP_NAME.getLabel(), groupName);

        longTermDisabilityMasterPolicy.createQuoteViaUI(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoPlans")
                .adjust(planDefinitionTab.getMetaKey(), listPlanDefinitionTabTestData)
                .adjust(classificationManagementTab.getMetaKey(), listClassificationManagementTabTestData));
    }
}
