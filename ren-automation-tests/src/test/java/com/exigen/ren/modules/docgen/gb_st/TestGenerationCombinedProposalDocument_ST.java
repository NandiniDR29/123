package com.exigen.ren.modules.docgen.gb_st;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.ENHANCED_NY;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.PFL_NY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupData.PAY_TYPE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupData.UNION_MEMBER;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.GROUP_DETAILS;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab.tableClassificationGroupCoverageRelationships;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGenerationCombinedProposalDocument_ST extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-40211"}, component = POLICY_GROUPBENEFITS)
    public void testGenerationCombinedProposalDocument_ST() {
        mainApp().open();
        createDefaultNonIndividualCustomer();

        TestData tdCase = CaseProfileContext.getDefaultCaseProfileTestData(statutoryDisabilityInsuranceMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), PAY_TYPE.getLabel()), "Salary")
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), UNION_MEMBER.getLabel()), VALUE_NO).resolveLinks();
        caseProfile.create(tdCase);

        statutoryDisabilityInsuranceMasterPolicy.createQuote(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String customerName = PolicySummaryPage.labelCustomerName.getValue();
        String effectiveDate = LocalDate.parse(QuoteSummaryPage.tableMasterQuote.getRow(1).getCell(QuoteSummaryPage.MasterQuote.POLICY_EFFECTIVE_DATE.getName()).getValue(), DateTimeUtils.MM_DD_YYYY)
                .atStartOfDay().format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        String caseProfileNumber = labelCaseProfileNumber.getValue();

        statutoryDisabilityInsuranceMasterPolicy.propose().perform();

        MainPage.QuickSearch.search(quoteNumber);
        statutoryDisabilityInsuranceMasterPolicy.quoteInquiry().start();

        classificationManagementMpTab.navigate();
        ClassificationManagementTab.selectPlan(ENHANCED_NY);
        String numberOfparticipantsEnhanced = tableClassificationGroupCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()).getValue();
        String totalVolumeEnhanced = new Currency(tableClassificationGroupCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()).getValue()).toPlainString();
        String rateEnhanced = String.valueOf(Double.parseDouble(new DecimalFormat("##.##")
                .format(Double.valueOf(tableClassificationGroupCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.RATE.getName()).getValue()))));

        ClassificationManagementTab.selectPlan(PFL_NY);
        String numberOfparticipantsPFL = tableClassificationGroupCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.NUMBER_OF_PARTICIPANTS.getName()).getValue();
        String totalVolumePFL = new Currency(tableClassificationGroupCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.TOTAL_VOLUME.getName()).getValue()).toPlainString();
        BigDecimal ratePFL = BigDecimal.valueOf(Double.valueOf(tableClassificationGroupCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.RATE.getName()).getValue())).multiply(BigDecimal.valueOf(100)).stripTrailingZeros();

        premiumSummaryTab.navigate();
        PremiumSummaryTab.openPremiumSummaryByPayorTable(ENHANCED_NY);
        String modalPremiumEnhanced = new Currency(PremiumSummaryTab.premiumSummaryByPayorCoveragesTable.getRow(1).getCell(TableConstants.PremiumSummaryCoveragesTable.MODAL_PREMIUM.getName()).getValue()).toPlainString();
        String annualPremiumEnhanced = new Currency(PremiumSummaryTab.premiumSummaryCoveragesTable.getRow(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName(), ENHANCED_NY)
                .getCell(TableConstants.PremiumSummaryCoveragesTable.ANNUAL_PREMIUM.getName()).getValue()).toPlainString();
        PremiumSummaryTab.openPremiumSummaryByPayorTable(PFL_NY);
        String modalPremiumPFL = new Currency(PremiumSummaryTab.premiumSummaryByPayorCoveragesTable.getRow(1).getCell(TableConstants.PremiumSummaryCoveragesTable.MODAL_PREMIUM.getName()).getValue()).toPlainString();
        String annualPremiumPFL = new Currency(PremiumSummaryTab.premiumSummaryCoveragesTable.getRow(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName(), ENHANCED_NY)
                .getCell(TableConstants.PremiumSummaryCoveragesTable.ANNUAL_PREMIUM.getName()).getValue()).toPlainString();


        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        xmlValidator.checkDocument(DBL34_SIC_CODE, "5411");
        xmlValidator.checkDocument(DBL34_SIC_DESCRIPTION, "Grocery Stores");
        xmlValidator.checkDocument(DBL35_SITUS_STATE, "NY");

        LOGGER.info("Step 4. Verification of quoteStatNYInfos section.");
        Map<DocGenEnum.AllSections, String> mapQuoteStatNYInfosSection = new HashMap<>();
        mapQuoteStatNYInfosSection.put(DBL2_POLICY_NUMBER, quoteNumber); //DBL11_POLICY_NUMBER
        mapQuoteStatNYInfosSection.put(DBL9_POLICY_GROUP_NAME, customerName);
        mapQuoteStatNYInfosSection.put(DBL10_EFFECTIVE_DT, effectiveDate);
        xmlValidator.checkDocument(mapQuoteStatNYInfosSection);

        //validation for classificationItems_DBL_enhanced_NY_Employee section
        XmlValidator classificationItemsDBLEnhancedSection = getNeededSection(xmlValidator, CLASSIFICATION_ITEMS_DBL_ENHANCED_NY_EMPLOYEE);
        classificationItemsDBLEnhancedSection.checkDocument(DBL4_COVERED_LIVES, numberOfparticipantsEnhanced);
        classificationItemsDBLEnhancedSection.checkDocument(DBL5_INSURANCE_VOLUME, totalVolumeEnhanced.substring(0, (totalVolumeEnhanced.length() - 3)));
        classificationItemsDBLEnhancedSection.checkDocument(DBL6_MONTHLY_RATE, rateEnhanced);
        classificationItemsDBLEnhancedSection.checkDocument(DBL7_MONTHLY_PREMIUM, modalPremiumEnhanced);

        //validation for CLASSIFICATION_ITEMS_PFL__NY_EMPLOYEE section
        XmlValidator classificationItemsPFLSection = getNeededSection(xmlValidator, CLASSIFICATION_ITEMS_PFL_NY_EMPLOYEE);
        classificationItemsPFLSection.checkDocument(PFL2_COVERED_LIVES, numberOfparticipantsPFL);
        classificationItemsPFLSection.checkDocument(PFL3_INSURANCE_VOLUME, totalVolumePFL.substring(0, (totalVolumePFL.length() - 3)));
        classificationItemsPFLSection.checkDocument(PFL4_MONTHLY_RATE, ratePFL.toPlainString());
        classificationItemsPFLSection.checkDocument(PFL5_MONTHLY_PREMIUM, modalPremiumPFL);

        //validation for benefitDetailsInfos_Stat_DBL_enhancedNY section
        XmlValidator benefitDetailsInfosStatDBLEnhancedNYSection = getNeededSection(xmlValidator, BENEFIT_DETAILS_INFOS_STAT_DBL_ENHANCED_NY);
        Map<DocGenEnum.AllSections, String> mapBenefitDetailsInfosStatDBLEnhancedNYSection = new HashMap<>();
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL13_BENEFIT_PERCENTAGE, "60% of Average Weekly Wages");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL14_MAXIMUM_WEEKLY_BENEFIT, "$340.00 per week");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL16_MAXIMUM_BENEFIT_DURATION, "26 weeks within a 52 week period");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL18_ACCIDENT_ELIMINATION_PERIOD, "7 days");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL19_SICKNESS_ELIMINATION_PERIOD, "7 days");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL21_DESCRIPTION_OF_ELIGIBILITY, "1");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL22_PARTICIPATION_REQUIREMENT, "100%");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL23_COVERAGE_TYPE, "Non occupational coverage");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL29_ESTIMATED_QUARTERLY_PREMIUM, annualPremiumEnhanced);
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL27_NUMBER_OF_COVERED_EMPLOYEES, "1 Males/1 Females. Total of 2 Lives");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL28_PREMIUM_RATE, "$" + rateEnhanced + " Per Employee Per Month");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL30_RATE_GAURANTEE, "2 years");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(DBL20_FIRST_DAY_HOSPITAL, "Included");
        benefitDetailsInfosStatDBLEnhancedNYSection.checkDocument(mapBenefitDetailsInfosStatDBLEnhancedNYSection);

        //validation for benefitDetailsInfos_Stat_DBL_PFL_NY section
        XmlValidator benefitDetailsInfosStatDblPflNYSection = getNeededSection(xmlValidator, BENEFIT_DETAILS_INFOS_STAT_DBL_ENHANCED_NY);
        Map<DocGenEnum.AllSections, String> mapbenefitDetailsInfosStatDblPflNYSection = new HashMap<>();
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL11_BENEFIT_PERCENTAGE, "60% of Average Weekly Wages");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL12_MAXIMUM_WEEKLY_BENEFIT, "$840.70 per week");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL13_MAXIMUM_BENEFIT_DURATION, "10 weeks within a 52 week period");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL14_ACCIDENT_ELIMINATION_PERIOD, "0 days");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL15_DESCRIPTION_OF_ELIGIBILITY, "1");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL16_PARTICIPATION_REQUIREMENT, "100%");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL21_ESTIMATED_ANNUAL_PREMIUM, annualPremiumPFL);
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL18_NUMBER_OF_COVERED_EMPLOYEES, numberOfparticipantsPFL + " Lives");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL19_PREMIUM_RATE, new DecimalFormat("0.000").format(ratePFL) + "% of covered payroll");
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL20_ESTIMATED_COVERED_PAYROLL, totalVolumePFL.substring(0, (totalVolumePFL.length() - 3)));
        mapBenefitDetailsInfosStatDBLEnhancedNYSection.put(PFL22_RATE_GAURANTEE, "2 years");
        benefitDetailsInfosStatDblPflNYSection.checkDocument(mapbenefitDetailsInfosStatDblPflNYSection);
    }
}