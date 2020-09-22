package com.exigen.ren.modules.docgen.gb_di_std;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.NC;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.ClassificationManagementTabMetaData.CLASSIFICATION_GROUP_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.CRITERIA;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PremiumSummaryTab.*;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.CustomerInformation.NAME_LEGAL;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.tableCustomerInformation;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyGenerationProposalDocumentOnePlanOneClassWithSubGroup extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-15430"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyGenerationProposalDocumentOnePlanOneClassWithSubGroup() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_AutoSubGroup"), shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.createQuoteViaUI(shortTermDisabilityMasterPolicy.getDefaultTestData("DataGatherSelfAdmin", "TestData_UseCensusFile"));
        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String caseProfileNumber = labelCaseProfileNumber.getValue();
        String groupName = tableCustomerInformation.getRow(1).getCell(NAME_LEGAL.getName()).getValue().toUpperCase();

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("TEST: Step #2");
        LOGGER.info("TEST: Prepare test date for validation classificationItems");
        Currency modalPremium =
                new Currency(PolicySummaryPage.tableCoverageSummary.getRow(1).getCell(PolicyConstants.PolicyCoverageSummaryTable.MODAL_PREMIUM).getValue());

        shortTermDisabilityMasterPolicy.policyInquiry().start();
        classificationManagementMpTab.navigateToTab();

        String classificationGroupName =
                new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(CLASSIFICATION_GROUP_NAME.getLabel())).getValue();
        String numberOfParticipants =
                tableCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()).getValue();
        Currency totalVolume =
                new Currency(tableCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()).getValue());

        String ageDescSubGroup = getAgeDesc(tableClassificationSubGroupsAndRatingInfo.getRow(1).getCell(CRITERIA.getName()).getValue());
        String numberOfParticipantsSubGroup = tableClassificationSubGroupsAndRatingInfo.getRow(1)
                .getCell(ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.NUMBER_OF_PARTICIPANTS.getName()).getValue();
        Currency totalVolumeSubGroup = new Currency(tableClassificationSubGroupsAndRatingInfo.getRow(1)
                .getCell(ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.TOTAL_VOLUME.getName()).getValue());
        String proposedRateSubGroup = tableClassificationSubGroupsAndRatingInfo.getRow(1)
                .getCell(ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.RATE.getName()).getValue();

        premiumSummaryTab.navigateToTab();
        Currency modalPremiumSubGroup = getModalPremium(ageDescSubGroup);

        LOGGER.info("TEST: Validate benefitCostInfoItems and classificationItems");
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoItems = new HashMap<>();
        mapBenefitCostInfoItems.put(STD1_4, "(guaranteed for 2 years***)");
        mapBenefitCostInfoItems.put(STD1_5_TOTAL_COVERED_LIVES, numberOfParticipants);
        mapBenefitCostInfoItems.put(STD1_6_TOTAL_INSURANCE_VOLUME, formatTotalVolume(totalVolume));
        mapBenefitCostInfoItems.put(STD1_7_TOTAL_MONTHLY_PREMIUM, modalPremium.toPlainString());
        mapBenefitCostInfoItems.put(STD1b_1_PLAN_NAME, NC);

        mapBenefitCostInfoItems.put(STD2, classificationGroupName);
        mapBenefitCostInfoItems.put(STD3, numberOfParticipants);
        mapBenefitCostInfoItems.put(STD4, formatTotalVolume(totalVolume));
        mapBenefitCostInfoItems.put(STD5_2_RATE_BASIC, "Per $10 Total Weekly Benefit");
        mapBenefitCostInfoItems.put(STD5_3_USE_CLASS_SUB_GROUP, "true");
        mapBenefitCostInfoItems.put(STD6, modalPremium.toPlainString());

        getBenefitCostInfoSection(xmlValidator, policyNumber, NC).checkDocument(mapBenefitCostInfoItems);

        LOGGER.info("TEST: Validate common section");
        xmlValidator.checkDocument(STD8, groupName);

        LOGGER.info("TEST: Validate quoteSTDInfos");
        Map<DocGenEnum.AllSections, String> mapQuoteInfoSection = new HashMap<>();
        mapQuoteInfoSection.put(STD9, String.format("%sT00:00:00Z",
                TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().withDayOfMonth(1).toLocalDate().toString()));
        mapQuoteInfoSection.put(STD1b_2_POLICY_NUMBER, policyNumber);

        getQuoteInfoSection(xmlValidator, policyNumber).checkDocument(mapQuoteInfoSection);

        LOGGER.info("TEST: Validate benefitDetailsInfoItems");
        TestData tdDataSource = tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY);

        getBenefitDetailsInfoSection(xmlValidator, policyNumber, NC).checkDocument(tdDataSource);

        LOGGER.info("TEST: Validate ageBandedInfoItems");
        Map<DocGenEnum.AllSections, String> mapAgeBandedInfoItems = new HashMap<>();
        mapAgeBandedInfoItems.put(STD30, numberOfParticipantsSubGroup);
        mapAgeBandedInfoItems.put(STD31, "Under 25");
        mapAgeBandedInfoItems.put(STD32, formatProposedRate(proposedRateSubGroup));
        mapAgeBandedInfoItems.put(STD33, totalVolumeSubGroup.toPlainString().replace(",", ""));
        mapAgeBandedInfoItems.put(STD34, modalPremiumSubGroup.toPlainString());

        getAgeBandedInfoSection(xmlValidator, policyNumber, NC, "Under 25").checkDocument(mapAgeBandedInfoItems);
    }

    private Currency getModalPremium(String subGroupName) {
        premiumSummaryCoveragesTable.getRow(1).getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName()).controls.links.getFirst().click(Tab.doubleWaiter);
        premiumSummaryByPayorCoveragesTable.getRow(1).getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName()).controls.links.getFirst().click(Tab.doubleWaiter);
        premiumSummaryClassNameTable.getRow(1).getCell(PremiumSummaryClassNameTable.CLASS_NAME.getName()).controls.links.getFirst().click();

        return new Currency(premiumSummarySubGroupTable.getRow(PremiumSummarySubGroupTable.SUB_GROUP_NAME.getName(),
                subGroupName).getCell(PremiumSummarySubGroupTable.MODAL_PREMIUM.getName()).getValue());
    }
}
