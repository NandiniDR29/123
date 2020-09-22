package com.exigen.ren.modules.docgen.gb_di_std;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupSTDCoverages.STD_CORE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.CON;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.NC;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupData.PAY_TYPE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupData.UNION_MEMBER;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.GROUP_DETAILS;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MINIMUM_WEEKLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.MEMBER_COMPANY_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGenerationCombinedProposalDocument_ConAndNcPlans_STD extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-40074"}, component = POLICY_GROUPBENEFITS)
    public void testGenerationCombinedProposalDocument_ConAndNcPlans_STD() {
        mainApp().open();
        createDefaultNonIndividualCustomer();

        TestData tdCase = CaseProfileContext.getDefaultCaseProfileTestData(shortTermDisabilityMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), PAY_TYPE.getLabel()), "Salary")
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), UNION_MEMBER.getLabel()), VALUE_NO).resolveLinks();
        caseProfile.create(tdCase);

        shortTermDisabilityMasterPolicy.createQuote(tdSpecific().getTestData("TestData_STD_CON"));
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String customerName = PolicySummaryPage.labelCustomerName.getValue();
        String effectiveDate = LocalDate.parse(QuoteSummaryPage.tableMasterQuote.getRow(1).getCell(QuoteSummaryPage.MasterQuote.POLICY_EFFECTIVE_DATE.getName()).getValue(), DateTimeUtils.MM_DD_YYYY)
                .atStartOfDay().format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        String caseProfileNumber = labelCaseProfileNumber.getValue();

        shortTermDisabilityMasterPolicy.propose().perform();

        MainPage.QuickSearch.search(quoteNumber);
        shortTermDisabilityMasterPolicy.quoteInquiry().start();

        planDefinitionTab.navigate();
        String minWeeklyBenefit = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(MINIMUM_WEEKLY_BENEFIT_AMOUNT.getLabel())).getValue();

        classificationManagementMpTab.navigate();
        ClassificationManagementTab.selectPlan(STD_CORE);
        String totalCoveredLives = tableCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()).getValue();
        String totalInsuranceVolume = new Currency(tableCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()).getValue()).toPlainString();

        String rate0_24 = getRateValue("0-24");
        String rate25_29 = getRateValue("25-29");
        String rate30_34 = getRateValue("30-34");
        String rate35_39 = getRateValue("35-39");
        String rate40_44 = getRateValue("40-44");
        String rate45_49 = getRateValue("45-49");
        String rate50_54 = getRateValue("50-54");
        String rate55_59 = getRateValue("55-59");
        String rate60_64 = getRateValue("60-64");
        String rate65_69 = getRateValue("65-69");
        tableClassificationSubGroupsAndRatingInfo.pagination().goToNextPage();
        String rate70Plus = getRateValue("70+");

        premiumSummaryTab.navigate();
        PremiumSummaryTab.openPremiumSummaryByPayorTable(CON);
        String totalMonthlyPremium = new Currency(PremiumSummaryTab.premiumSummaryByPayorCoveragesTable.getRow(1)
                .getCell(TableConstants.PremiumSummaryCoveragesTable.MODAL_PREMIUM.getName()).getValue()).toPlainString();

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("Step 1. Verification of quoteSTDInfos section");
        Map<DocGenEnum.AllSections, String> mapQuoteSTDInfosSection = new HashMap<>();
        mapQuoteSTDInfosSection.put(STD1b_2_POLICY_NUMBER, quoteNumber);
        mapQuoteSTDInfosSection.put(STD8_POLICY_GROUP_NAME, customerName);
        mapQuoteSTDInfosSection.put(STD9, effectiveDate);
        xmlValidator.checkDocument(mapQuoteSTDInfosSection);

        //Validation for benefitCostInfoItems section -> CON plan
        XmlValidator benefitCostInfoItems_ConPlanSection = getNeededSection(xmlValidator, BENEFIT_COST_INFO_SECTION_BY_PLAN_NAME, CON);
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoItems_ConPlanSection = new HashMap<>();
        mapBenefitCostInfoItems_ConPlanSection.put(STD1_5_TOTAL_COVERED_LIVES, totalCoveredLives);
        mapBenefitCostInfoItems_ConPlanSection.put(STD1_6_TOTAL_INSURANCE_VOLUME, totalInsuranceVolume);
        mapBenefitCostInfoItems_ConPlanSection.put(STD1_7_TOTAL_MONTHLY_PREMIUM, totalMonthlyPremium);
        mapBenefitCostInfoItems_ConPlanSection.put(CLASS_NAME, "1");
        mapBenefitCostInfoItems_ConPlanSection.put(STD3, totalCoveredLives);
        mapBenefitCostInfoItems_ConPlanSection.put(STD4, totalInsuranceVolume);
        mapBenefitCostInfoItems_ConPlanSection.put(STD5_3_USE_CLASS_SUB_GROUP, VALUE_TRUE);
        mapBenefitCostInfoItems_ConPlanSection.put(STD6, totalMonthlyPremium);
        benefitCostInfoItems_ConPlanSection.checkDocument(mapBenefitCostInfoItems_ConPlanSection);

        //Validation for benefitDetailsInfoItems section -> CON plan
        XmlValidator benefitDetailsInfoItems_ConPlanSection = getNeededSection(xmlValidator, BENEFIT_DETAILS_INFO_SECTION_BY_PLAN_NAME, CON);
        Map<DocGenEnum.AllSections, String> mapBenefitDetailsInfoItems_ConPlanSection = new HashMap<>();
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD7_2_RATE_BASIC_OF_CLASS, "STD RATE Per $10 Total Weekly Benefit");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD7_3_USE_CLASS_SUB_GROUP, VALUE_TRUE);
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD10_1_CLASSSEQ_NUM, "1");
        mapBenefitDetailsInfoItems_ConPlanSection.put(CLASS_NAME, "1"); //STD10_2
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD10_3_CLASS_DISPLAY_IN_PDF, "Class 1: 1");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD11_1_BENEFIT_ROW_NAME, "Benefit Percentage");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD11_2_BENEFIT_ROW_DESC, "60% of Basic Weekly Earnings rounded to the next higher $1");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD13, minWeeklyBenefit);
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD14, "7 days");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD15, "7 days");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD16, "Not Included");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD17, "26 weeks");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD18, "Residual with loss of duties and loss of earnings: Claimant is unable to perform the " +
                "material and substantial duties of his/her regular occupation and has a 20% or more loss of weekly earnings. Total disability is not required during the elimination period.");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD19, "Gross weekly benefit plus work earnings may not exceed 100% of pre-disability earnings.");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD20, "3 Months/3 Months/12 Months");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD22, "0% (non-contributory)");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD23, "100%");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD24, "Non-Occupational");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD26, "An additional benefit of 5% of the gross weekly benefit is payable if the claimant participates in an approved rehabilitation program.");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD27, "30 days");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD28, "Not Included");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD29, "Primary & Family");
        benefitDetailsInfoItems_ConPlanSection.checkDocument(mapBenefitDetailsInfoItems_ConPlanSection);

        //Validation for benefitDetailsInfoItems section -> CON plan -> ageBandedInfoItems (STD30-STD32)
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "Under 25", "2", rate0_24);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "25-29", "0", rate25_29);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "30-34", "0", rate30_34);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "35-39", "2", rate35_39);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "40-44", "0", rate40_44);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "45-49", "2", rate45_49);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "50-54", "0", rate50_54);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "55-59", "0", rate55_59);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "60-64", "0", rate60_64);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "65-69", "0", rate65_69);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_ConPlanSection, "70+", "0", rate70Plus);

        LOGGER.info("Step 2");
        //Validation for benefitDetailsInfoItems section -> NC plan
        XmlValidator benefitDetailsInfoItems_NCPlanSection = getNeededSection(xmlValidator, BENEFIT_DETAILS_INFO_SECTION_BY_PLAN_NAME, NC);
        Map<DocGenEnum.AllSections, String> mapBenefitDetailsInfoItems_NCPlanSection = new HashMap<>();
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD11_1_BENEFIT_ROW_NAME, "Benefit");
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD18, "Total: Claimant is unable to perform the material and substantial duties of his/her own job and is not working in any occupation.");
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD19, "Not Included");
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD20, "None");
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD22, "50%");
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD23, "Ten (10) Employees or 75%, whichever is greater.");
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD24, "24 Hour");
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD26, "An additional benefit of 5% of the gross weekly benefit is payable if the claimant participates in an approved rehabilitation program.");
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD28, "Included");
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD29, "Family");
        mapBenefitDetailsInfoItems_NCPlanSection.put(STD11_3_BENEFIT_PERCENTAGE_AMOUNT, "500.00");
        benefitDetailsInfoItems_NCPlanSection.checkDocument(mapBenefitDetailsInfoItems_NCPlanSection);

        benefitDetailsInfoItems_NCPlanSection.checkNodeNotPresent(STD12);
    }

    private String getRateValue(String subGroupName) {
        return tableClassificationSubGroupsAndRatingInfo.getRow(CLASSIFICATION_SUB_GROUP_NAME.getName(), subGroupName)
                .getCell(ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.RATE.getName()).getValue().substring(0, 5);
    }

    private void checkAgeBandedInfoItemsSection(XmlValidator xmlValidator, String ageRange, String numberOfLives, String rate) {
        XmlValidator quoteSTD_BenefitDetailsInfoItems_ConPlan_ageBandedInfoItemsSection65_69 = getNeededSection(xmlValidator, AGE_BANDED_INFO_SECTION_BY_AGE, ageRange); //STD31
        quoteSTD_BenefitDetailsInfoItems_ConPlan_ageBandedInfoItemsSection65_69.checkDocument(STD30, numberOfLives);
        quoteSTD_BenefitDetailsInfoItems_ConPlan_ageBandedInfoItemsSection65_69.checkDocument(STD32, rate);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-42263"}, component = POLICY_GROUPBENEFITS)
    public void testGenerationCombinedProposalDocument_ConPlan_STD() {
        mainApp().open();
        customerNonIndividual.createViaUI(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.NON_INDIVIDUAL_TYPE.getLabel()), "PEO")
                .adjust(tdSpecific().getTestData("RelationshipWithNonIndividualType")).resolveLinks());

        TestData tdCase = CaseProfileContext.getDefaultCaseProfileTestData(shortTermDisabilityMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), PAY_TYPE.getLabel()), "Salary")
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), UNION_MEMBER.getLabel()), VALUE_NO).resolveLinks();
        caseProfile.create(tdCase);

        shortTermDisabilityMasterPolicy.createQuote(tdSpecific().getTestData("TestData_OnePlan"));
        String caseProfileNumber = labelCaseProfileNumber.getValue();

        shortTermDisabilityMasterPolicy.propose().perform();

        shortTermDisabilityMasterPolicy.quoteInquiry().start();
        String customerName = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(MEMBER_COMPANY_NAME.getLabel())).getValue();

        classificationManagementMpTab.navigate();
        double rate = Double.parseDouble(new DecimalFormat("0.000").format(Double.parseDouble(tableCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.RATE.getName()).getValue())));

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("Step 1. Verification of quoteSTDInfos section");
        xmlValidator.checkDocument(STD8_POLICY_GROUP_NAME, customerName);

        //Validation for benefitCostInfoItems section -> CON plan
        XmlValidator benefitCostInfoItems_ConPlanSection = getNeededSection(xmlValidator, BENEFIT_COST_INFO_SECTION_BY_PLAN_NAME, CON);
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoItems_ConPlanSection = new HashMap<>();
        mapBenefitCostInfoItems_ConPlanSection.put(STD5_1_MONTHLY_RATE, String.valueOf(rate));
        mapBenefitCostInfoItems_ConPlanSection.put(STD5_2_RATE_BASIC, "Per $10 Total Weekly Benefit");
        mapBenefitCostInfoItems_ConPlanSection.put(STD5_3_USE_CLASS_SUB_GROUP, VALUE_FALSE);
        benefitCostInfoItems_ConPlanSection.checkDocument(mapBenefitCostInfoItems_ConPlanSection);

        //Validation for benefitDetailsInfoItems section -> CON plan
        XmlValidator benefitDetailsInfoItems_ConPlanSection = getNeededSection(xmlValidator, BENEFIT_DETAILS_INFO_SECTION_BY_PLAN_NAME, CON);
        Map<DocGenEnum.AllSections, String> mapBenefitDetailsInfoItems_ConPlanSection = new HashMap<>();
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD18, "Partial with loss of duties and loss of earnings: Claimant is unable to perform the material and substantial duties of his/her regular occupation and has a 20% or more loss of weekly earnings. Total disability is required during the elimination period.");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD20, "3 Months/12 Months");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD23, "100% for groups of 2-5 and 75% for groups of 6-9.");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD27, "14 days");
        mapBenefitDetailsInfoItems_ConPlanSection.put(STD28, "Included");
        benefitDetailsInfoItems_ConPlanSection.checkDocument(mapBenefitDetailsInfoItems_ConPlanSection);

        xmlValidator.checkNodeNotPresent(STD12);
    }
}
