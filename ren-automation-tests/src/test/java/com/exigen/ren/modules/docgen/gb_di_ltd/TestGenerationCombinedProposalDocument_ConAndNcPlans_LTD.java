package com.exigen.ren.modules.docgen.gb_di_ltd;

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
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
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
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_CON;
import static com.exigen.ren.main.enums.PolicyConstants.PlanLTD.LTD_NC;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_FALSE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupData.PAY_TYPE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupData.UNION_MEMBER;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.GROUP_DETAILS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.MEMBER_COMPANY_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.CLASSIFICATION_SUB_GROUP_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab.PremiumSummarySubGroupTable.SUB_GROUP_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab.premiumSummarySubGroupTable;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGenerationCombinedProposalDocument_ConAndNcPlans_LTD extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private static final String MONTHLY_PREMIUM = "monthlyPremium";
    private static final String _0_24 = "0-24";
    private static final String _25_29 = "25-29";
    private static final String _30_34 = "30-34";
    private static final String _35_39 = "35-39";
    private static final String _40_44 = "40-44";
    private static final String _45_49 = "45-49";
    private static final String _50_54 = "50-54";
    private static final String _55_59 = "55-59";
    private static final String _60_64 = "60-64";
    private static final String _65_69 = "65-69";
    private static final String _70Plus = "70+";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-40074"}, component = POLICY_GROUPBENEFITS)
    public void testGenerationCombinedProposalDocument_ConAndNcPlans_LTD() {
        mainApp().open();
        createDefaultNonIndividualCustomer();

        TestData tdCase = CaseProfileContext.getDefaultCaseProfileTestData(longTermDisabilityMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), PAY_TYPE.getLabel()), "Salary")
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), UNION_MEMBER.getLabel()), VALUE_NO).resolveLinks();
        caseProfile.create(tdCase);

        longTermDisabilityMasterPolicy.createQuote(tdSpecific().getTestData("TestData_LTD"));
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String customerName = PolicySummaryPage.labelCustomerName.getValue();
        String effectiveDate = LocalDate.parse(QuoteSummaryPage.tableMasterQuote.getRow(1).getCell(QuoteSummaryPage.MasterQuote.POLICY_EFFECTIVE_DATE.getName()).getValue(), DateTimeUtils.MM_DD_YYYY)
                .atStartOfDay().format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        String caseProfileNumber = labelCaseProfileNumber.getValue();

        longTermDisabilityMasterPolicy.propose().perform();

        MainPage.QuickSearch.search(quoteNumber);
        longTermDisabilityMasterPolicy.quoteInquiry().start();

        classificationManagementMpTab.navigate();
        ClassificationManagementTab.selectPlan("NC-NC");
        String totalCoveredLives = tableCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()).getValue();
        String totalInsuranceVolume = new Currency(tableCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()).getValue()).toPlainString();


        Map<String, String> map0_24 = getRateVolumeAndNumberOfParticipants(_0_24);
        Map<String, String> map25_29 = getRateVolumeAndNumberOfParticipants(_25_29);
        Map<String, String> map30_34 = getRateVolumeAndNumberOfParticipants(_30_34);
        Map<String, String> map35_39 = getRateVolumeAndNumberOfParticipants(_35_39);
        Map<String, String> map40_44 = getRateVolumeAndNumberOfParticipants(_40_44);
        Map<String, String> map45_49 = getRateVolumeAndNumberOfParticipants(_45_49);
        Map<String, String> map50_54 = getRateVolumeAndNumberOfParticipants(_50_54);
        Map<String, String> map55_59 = getRateVolumeAndNumberOfParticipants(_55_59);
        Map<String, String> map60_64 = getRateVolumeAndNumberOfParticipants(_60_64);
        Map<String, String> map65_69 = getRateVolumeAndNumberOfParticipants(_65_69);
        tableClassificationSubGroupsAndRatingInfo.pagination().goToNextPage();
        Map<String, String> map70Plus = getRateVolumeAndNumberOfParticipants(_70Plus);

        premiumSummaryTab.navigate();
        PremiumSummaryTab.openPremiumSummaryByPayorTable(LTD_NC);
        String totalMonthlyPremium = new Currency(PremiumSummaryTab.premiumSummaryByPayorCoveragesTable.getRow(1)
                .getCell(TableConstants.PremiumSummaryCoveragesTable.MODAL_PREMIUM.getName()).getValue()).multiply(2).toPlainString();
        PremiumSummaryTab.openPremiumSummaryClassNameTable("Sponsor");
        PremiumSummaryTab.openPremiumSummarySubGroupTable("1");
        map0_24.put(MONTHLY_PREMIUM, getModalPremium(_0_24));
        map25_29.put(MONTHLY_PREMIUM, getModalPremium(_25_29));
        map30_34.put(MONTHLY_PREMIUM, getModalPremium(_30_34));
        map35_39.put(MONTHLY_PREMIUM, getModalPremium(_35_39));
        map40_44.put(MONTHLY_PREMIUM, getModalPremium(_40_44));
        map45_49.put(MONTHLY_PREMIUM, getModalPremium(_45_49));
        map50_54.put(MONTHLY_PREMIUM, getModalPremium(_50_54));
        map55_59.put(MONTHLY_PREMIUM, getModalPremium(_55_59));
        map60_64.put(MONTHLY_PREMIUM, getModalPremium(_60_64));
        map65_69.put(MONTHLY_PREMIUM, getModalPremium(_65_69));
        map70Plus.put(MONTHLY_PREMIUM, getModalPremium(_70Plus));

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("Step 3");
        Map<DocGenEnum.AllSections, String> mapQuoteLTDInfosSection = new HashMap<>();
        mapQuoteLTDInfosSection.put(LTD1_2a, quoteNumber); //LTD8a
        mapQuoteLTDInfosSection.put(LTD7_POLICY_GROUP_NAME, customerName);
        mapQuoteLTDInfosSection.put(LTD8, effectiveDate);
        xmlValidator.checkDocument(mapQuoteLTDInfosSection);

        //Validation for benefitCostInfoItems section -> NC plan
        XmlValidator benefitCostInfoItems_NcPlanSection = getNeededSection(xmlValidator, BENEFIT_COST_INFO_SECTION_BY_PLAN_NAME, LTD_NC);  //1_1a
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoItems_NcPlanSection = new HashMap<>();
        mapBenefitCostInfoItems_NcPlanSection.put(CLASS_NAME, "1");
        mapBenefitCostInfoItems_NcPlanSection.put(LTD2, totalCoveredLives);
        mapBenefitCostInfoItems_NcPlanSection.put(LTD3, totalInsuranceVolume);
        mapBenefitCostInfoItems_NcPlanSection.put(LTD5, totalMonthlyPremium);
        benefitCostInfoItems_NcPlanSection.checkDocument(mapBenefitCostInfoItems_NcPlanSection);

        //Validation for benefitDetailsInfoItems section -> NC plan
        XmlValidator benefitDetailsInfoItems_NcPlanSection = getNeededSection(xmlValidator, BENEFIT_DETAILS_INFO_SECTION_BY_PLAN_NAME, LTD_NC);
        Map<DocGenEnum.AllSections, String> mapBenefitDetailsInfoItems_NcPlanSection = new HashMap<>();
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD9_1, "2");
        mapBenefitDetailsInfoItems_NcPlanSection.put(CLASS_NAME, "1");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD9_3, "Class 1: 1");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD10, "60% of Basic Monthly Earnings");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD11, "$6,000.00");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD12, "$6,000.00");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD13, "$100 or 10%");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD14, "180 days");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD16, "RBD w/ SSNRA");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD19, "60%/80%");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD21, "60%/80%");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD23, "Family");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD25, "50%");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD26, "Ten (10) Employees or 75%, whichever is greater.");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD27, "24 Months (Lifetime)");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD28, "24 Months (Lifetime)");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD32, "$100.00 maximum / month");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD33, "$300.00 / 36 months");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD42, "5 Years");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD43, "12 Months");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD44a, "$2 000");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD47, "12 Months");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD50a, "$2,000");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD53, "Included");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD55, "Embedded");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD17, "24 months");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD18, "Claimant is unable to perform the material and substantial duties of his/her regular occupation and has a 20% loss of indexed monthly earnings.");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD20, "During the first 12 Months of disability gross weekly benefit plus work earnings may not exceed 100% of pre-disability earnings");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD22, "Included");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD29, "3 Times Gross Monthly Benefit");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD30, "Terminal Illness Benefit");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD31, "2%");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD36, "An additional benefit of 5% of the gross weekly benefit to a maximum of $500 is payable if the claimant participates in an approved rehabilitation program.");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD38, "Not Available");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD41, "2%");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD44, "Not Available");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD48, "Not Available");
        mapBenefitDetailsInfoItems_NcPlanSection.put(LTD50, "$350/12 Months");

        //Validation for benefitDetailsInfoItems section -> NC plan -> ageBandedInfoItems (LTD56-LTD60)
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, "Under 25", map0_24);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, _25_29, map25_29);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, _30_34, map30_34);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, _35_39, map35_39);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, _40_44, map40_44);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, _45_49, map45_49);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, _50_54, map50_54);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, _55_59, map55_59);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, _60_64, map60_64);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, _65_69, map65_69);
        checkAgeBandedInfoItemsSection(benefitDetailsInfoItems_NcPlanSection, _70Plus, map70Plus);

        LOGGER.info("Step 4");
        //Validation for benefitDetailsInfoItems section -> CON plan
        XmlValidator benefitDetailsInfoItems_ConPlanSection = getNeededSection(xmlValidator, BENEFIT_DETAILS_INFO_SECTION_BY_PLAN_NAME, LTD_CON);
        Map<DocGenEnum.AllSections, String> mapBenefitDetailsInfoItems_ConPlanSection = new HashMap<>();
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD17, "Unlimited");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD18, "Claimant is unable to perform the material and substantial duties of his/her regular occupation and has a 20% loss of indexed monthly earnings.");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD20, "Not Included");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD22, "Not Included");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD29, "Not Included");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD30, "Not Included");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD31, "Not Included");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD41, "Not Included");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD50, "Not Included");
        benefitDetailsInfoItems_ConPlanSection.checkDocument(mapBenefitDetailsInfoItems_ConPlanSection);
    }

    private Map<String, String> getRateVolumeAndNumberOfParticipants(String subGroupName) {
        Map<String, String> map = new HashMap<>();
        map.put("rate", tableClassificationSubGroupsAndRatingInfo.getRow(CLASSIFICATION_SUB_GROUP_NAME.getName(), subGroupName)
                .getCell(ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.RATE.getName()).getValue().substring(0, 5));
        map.put("totalVolume", new Currency(tableClassificationSubGroupsAndRatingInfo.getRow(CLASSIFICATION_SUB_GROUP_NAME.getName(), subGroupName)
                .getCell(ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.TOTAL_VOLUME.getName()).getValue()).toPlainString());
        map.put("numberOfParticipants", tableClassificationSubGroupsAndRatingInfo.getRow(CLASSIFICATION_SUB_GROUP_NAME.getName(), subGroupName)
                .getCell(ClassificationManagementTab.ClassificationSubGroupsAndRatingColumns.NUMBER_OF_PARTICIPANTS.getName()).getValue());
        return map;
    }

    private String getModalPremium(String subGroupName) {
        return new Currency(premiumSummarySubGroupTable.getRow(SUB_GROUP_NAME.getName(), subGroupName)
                .getCell(PremiumSummaryTab.PremiumSummarySubGroupTable.MODAL_PREMIUM.getName()).getValue()).multiply(2).toPlainString();
    }

    private void checkAgeBandedInfoItemsSection(XmlValidator xmlValidator, String ageRange, Map<String, String> map) {
        XmlValidator quoteSTD_BenefitDetailsInfoItems_ConPlan_ageBandedInfoItemsSection65_69 = getNeededSection(xmlValidator, AGE_BANDED_INFO_SECTION_BY_AGE, ageRange); //LTD57
        quoteSTD_BenefitDetailsInfoItems_ConPlan_ageBandedInfoItemsSection65_69.checkDocument(LTD56, map.get("numberOfParticipants"));
        quoteSTD_BenefitDetailsInfoItems_ConPlan_ageBandedInfoItemsSection65_69.checkDocument(LTD58, map.get("rate"));
        quoteSTD_BenefitDetailsInfoItems_ConPlan_ageBandedInfoItemsSection65_69.checkDocument(LTD59, map.get("totalVolume"));
        quoteSTD_BenefitDetailsInfoItems_ConPlan_ageBandedInfoItemsSection65_69.checkDocument(LTD60, map.get(MONTHLY_PREMIUM));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-42263"}, component = POLICY_GROUPBENEFITS)
    public void testGenerationCombinedProposalDocument_NcPlan_LTD() {
        mainApp().open();
        customerNonIndividual.createViaUI(getDefaultCustomerNonIndividualTestData()
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.NON_INDIVIDUAL_TYPE.getLabel()), "PEO")
                .adjust(tdSpecific().getTestData("RelationshipWithNonIndividualType")).resolveLinks());

        TestData tdCase = CaseProfileContext.getDefaultCaseProfileTestData(longTermDisabilityMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), PAY_TYPE.getLabel()), "Salary")
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), UNION_MEMBER.getLabel()), VALUE_NO).resolveLinks();
        caseProfile.create(tdCase);

        longTermDisabilityMasterPolicy.createQuote(tdSpecific().getTestData("TestData_OnePlan"));
        String caseProfileNumber = labelCaseProfileNumber.getValue();

        longTermDisabilityMasterPolicy.propose().perform();

        longTermDisabilityMasterPolicy.quoteInquiry().start();
        String customerName = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format(MEMBER_COMPANY_NAME.getLabel())).getValue();

        classificationManagementMpTab.navigate();
        double rate = Double.parseDouble(new DecimalFormat("0.000").format(Double.parseDouble(tableCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.RATE.getName()).getValue())));

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("Step 1. Verification of quoteLTDInfos section");
        xmlValidator.checkDocument(LTD7_POLICY_GROUP_NAME, customerName);

        //Validation for benefitCostInfoItems section -> NC plan
        XmlValidator benefitCostInfoItems_ConPlanSection = getNeededSection(xmlValidator, BENEFIT_COST_INFO_SECTION_BY_PLAN_NAME, LTD_NC);
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoItems_ConPlanSection = new HashMap<>();
        mapBenefitCostInfoItems_ConPlanSection.put(LTD4_1, String.valueOf(rate));
        mapBenefitCostInfoItems_ConPlanSection.put(LTD4_2, "Per $100 Monthly Covered Payroll");
        mapBenefitCostInfoItems_ConPlanSection.put(LTD4_3, VALUE_FALSE);
        benefitCostInfoItems_ConPlanSection.checkDocument(mapBenefitCostInfoItems_ConPlanSection);

        //Validation for benefitDetailsInfoItems section -> NC plan
        XmlValidator benefitDetailsInfoItems_ConPlanSection = getNeededSection(xmlValidator, BENEFIT_DETAILS_INFO_SECTION_BY_PLAN_NAME, LTD_NC);
        Map<DocGenEnum.AllSections, String> mapBenefitDetailsInfoItems_ConPlanSection = new HashMap<>();
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD17, "24 months");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD38, "Not Included");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD44, "None");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD45, "None");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD49A_REVENUE_PROTECTION_BENEFIT_MAXIMUM, "$3,000");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD21, "3 Months/3 Months/12 Months");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD26, "Ten (10) Employees or 25%, whichever is greater.");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD29, "3 Times Net Monthly Benefit");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD33, "Not Available");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD43A_SELF_REPORTED_CONDITIONS_LIMITATION, "N/A");
        mapBenefitDetailsInfoItems_ConPlanSection.put(LTD48, "Not Available");
        benefitDetailsInfoItems_ConPlanSection.checkDocument(mapBenefitDetailsInfoItems_ConPlanSection);
    }
}
