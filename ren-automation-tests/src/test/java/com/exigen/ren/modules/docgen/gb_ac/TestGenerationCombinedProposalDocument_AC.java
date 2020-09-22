package com.exigen.ren.modules.docgen.gb_ac;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.enums.TableConstants.PlanTierAndRatingSelection;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.BASIC_ACCIDENT;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.BASE_BUY_UP;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_TRUE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupData.PAY_TYPE;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.ClassificationManagementTabGroupData.UNION_MEMBER;
import static com.exigen.ren.main.modules.caseprofile.metadata.ClassificationManagementTabMetaData.GROUP_DETAILS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.ClassificationManagementTab.tablePlanTierAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.ClassificationManagementTab.tablePlansAndCoverages;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab.*;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGenerationCombinedProposalDocument_AC extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private static final String EMPLOYEE_VALUES_BY_PREFIX_PATH = "%s/employeeValues/item[1]";
    private static final String SPOUSE_VALUES_BY_PREFIX_PATH = "%s/spouseValues/item[1]";
    private static final String CHILD_VALUES_BY_PREFIX_PATH = "%s/childValues/item[1]";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-40211"}, component = POLICY_GROUPBENEFITS)
    public void testGenerationCombinedProposalDocument_AC() {
        mainApp().open();
        createDefaultNonIndividualCustomer();

        TestData tdCase = CaseProfileContext.getDefaultCaseProfileTestData(groupAccidentMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), PAY_TYPE.getLabel()), "Salary")
                .adjust(TestData.makeKeyPath(classificationManagementTab.getMetaKey() + "[0]", GROUP_DETAILS.getLabel(), UNION_MEMBER.getLabel()), VALUE_NO).resolveLinks();
        caseProfile.create(tdCase);

        groupAccidentMasterPolicy.createQuote(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String customerName = PolicySummaryPage.labelCustomerName.getValue();
        String effectiveDate = LocalDate.parse(QuoteSummaryPage.tableMasterQuote.getRow(1).getCell(QuoteSummaryPage.MasterQuote.POLICY_EFFECTIVE_DATE.getName()).getValue(), DateTimeUtils.MM_DD_YYYY)
                .atStartOfDay().format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        String caseProfileNumber = labelCaseProfileNumber.getValue();

        groupAccidentMasterPolicy.propose().perform();

        MainPage.QuickSearch.search(quoteNumber);
        groupAccidentMasterPolicy.quoteInquiry().start();

        classificationManagementMPTab.navigate();
        List<String> coverageTierList = new ArrayList<>();
        tablePlanTierAndRatingInfo.getContinuousValue().forEach(row -> coverageTierList.add(row.getValue(PlanTierAndRatingSelection.COVERAGE_TIER.getName())));
        Map<String, String> tiersDataItemSectionUIValues = getVauesFromUIForTiersDataItemsSection();

        String $100_000 = "$100,000.00";
        String $50_000 = "$50,000.00";
        String $30_000 = "$30,000.00";
        String $15_000 = "$15,000.00";
        String $10_000 = "$10,000.00";
        String $5_000 = "$5,000.00";
        String $4_000 = "$4,000.00";
        String $3_200 = "$3,200.00";
        String $3_000 = "$3,000.00";
        String $2_000 = "$2,000.00";
        String $1_600 = "$1,600.00";
        String $1_500 = "$1,500.00";
        String $1_400 = "$1,400.00";
        String $1_200 = "$1,200.00";
        String $1_000 = "$1,000.00";
        String $800 = "$800.00";
        String $700 = "$700.00";
        String $600 = "$600.00";
        String $500 = "$500.00";
        String $400 = "$400.00";
        String $250 = "$250.00";
        String $200 = "$200.00";
        String $175 = "$175.00";
        String $150 = "$150.00";
        String $100 = "$100.00";
        String $50 = "$50.00";

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("Step 3. Verification of quoteACInfos section.");
        Map<DocGenEnum.AllSections, String> mapQuoteACInfosSection = new HashMap<>();
        mapQuoteACInfosSection.put(ACC4_POLICY_NUMBER, quoteNumber);
        mapQuoteACInfosSection.put(ACC11_POLICY_GROUP_NAME, customerName);
        mapQuoteACInfosSection.put(ACC12_EFFECTIVE_DT, effectiveDate);
        mapQuoteACInfosSection.put(ACC3_RATE_GUARANTEED, "24");
        xmlValidator.checkDocument(mapQuoteACInfosSection);

        //validation for tiersDataItems section
        checkTiersDataItemsSection(xmlValidator, coverageTierList, tiersDataItemSectionUIValues);

        //validation for benefitDetailsInfoItems section
        XmlValidator benefitDetailsInfoItemsSection = getNeededSection(xmlValidator, BENEFIT_DETAILS_INFO_SECTION);
        Map<DocGenEnum.AllSections, String> mapBenefitDetailsInfoItemsSection = new HashMap<>();
        mapBenefitDetailsInfoItemsSection.put(ACC10_LEFT_HEADER_COLUMN_LINE1, "BASIC BENEFITS - 4 UNITS");
        mapBenefitDetailsInfoItemsSection.put(ACC10_LEFT_HEADER_COLUMN_LINE2, "ENHANCED BENEFITS - 10 UNITS");
        mapBenefitDetailsInfoItemsSection.put(ACC13_CLASS_DISPLAY_IN_PDF, "Class Employment: Employment");
        mapBenefitDetailsInfoItemsSection.put(ACC14_WITH_BASIC, VALUE_TRUE);
        mapBenefitDetailsInfoItemsSection.put(ACC21_WITH_ENHANCED, VALUE_TRUE);
        mapBenefitDetailsInfoItemsSection.put(ACC40_SURGERY_BENEFIT_IND, VALUE_TRUE);
        mapBenefitDetailsInfoItemsSection.put(ACC55_LOSS_PART_OF_BODY_IND, VALUE_TRUE);
        mapBenefitDetailsInfoItemsSection.put(ACC89_WITH_OPTIONAL, VALUE_TRUE);
        benefitDetailsInfoItemsSection.checkDocument(mapBenefitDetailsInfoItemsSection);

        //validation for basicData section
        XmlValidator basicDataSection = getNeededSection(benefitDetailsInfoItemsSection, BASIC_DATA);
        checkEmployeeSpouseAndChildValues(basicDataSection, ACC15_AIR_AMBULANCE.get(), $2_000);
        checkEmployeeSpouseAndChildValues(basicDataSection, ACC16_GROUND_AMBULANCE.get(), $400);
        checkEmployeeSpouseAndChildValues(basicDataSection, ACC17_EMERGENCY_ROOM_TREATMENT.get(), $200);
        checkEmployeeSpouseAndChildValues(basicDataSection, ACC18_HOSPITAL_BENEFIT.get(), $2_000);
        checkEmployeeSpouseAndChildValues(basicDataSection, ACC19_MAJOR_DIAGNOSTIC_EXAM.get(), $400);
        checkEmployeeSpouseAndChildValues(basicDataSection, ACC20_PHYSICAL_THERAPY.get(), $100);

        //validation for enhancedData section
        XmlValidator enhancedDataSection = getNeededSection(benefitDetailsInfoItemsSection, ENHANCED_DATA);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC22_ABDOMINAL_OR_THORACIC.get(), $1_000);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC23_ACCIDENTAL_DEATH.get(), $50_000, $50_000, $15_000);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC24_ACCIDENTAL_DEATH_COMMON_CARRIER.get(), $100_000, $100_000, $30_000);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC25_ACCIDENT_FOLLOW_UP_TREATMENT.get(), $50);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC26_AIR_AMBULANCE.get(), $1_200);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC27_AMBULANCE.get(), $400);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC28_APPLIANCE.get(), $100);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC29_BLOOD_PLASMA_PLATELETS.get(), $500);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC30_BURN_BENEFIT.get(), $800);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC31_CATASTROPHIC_ACCIDENT.get(), "<70 years: $100,000.00", "<70 years: $100,000.00", "<70 years: $50,000.00");
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC32_COMA_INJURY.get(), "$12,500.00");
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC33_CONCUSSION_BENEFIT.get(), $100);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC34_EMERGENCY_DENTAL_WORK.get(), $250);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC35_EMERGENCY_ROOM_TREATMENT.get(), $50);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC36_EYE_INJURY.get(), $200);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC37_HERNIATED_DISC.get(), $400);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC38_HOSPITAL_BENEFIT.get(), $2_000);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC39_INTERNAL_ORGAN_LOSS.get(), "$2,500.00");
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC41_TORN_SIGLE_PART.get(), $600);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC42_TORN_MORE_PART.get(), $1_000);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC43_TORN_KNEE_CARTILAGE.get(), $800);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC44_TORN_WITHOUT_REPAIR.get(), $200);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC45_LACERATIONS_BENEFIT.get(), $50);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC46_LODGING_BENEFIT.get(), $100);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC47_MAJOR_DIAGNOSTIC_EXAM.get(), $600);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC48_PHYSICAL_THERAPY.get(), $50);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC49_PHYSICIAN_CARE.get(), $50);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC50_PROSTHETIC_LIMB.get(), $500);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC51_REHABILITATION.get(), $200);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC52_SKIN_GRAFT.get(), "25%");
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC53_SPORTS_INJURY.get(), "Lesser of 25% of All Benefits Paid or $1,000.00");
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC54_TRANSPORTATION.get(), "$300.00");
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC56_LOSS_MOREHANDS_FEET_EYES.get(), $30_000, $30_000, $10_000);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC57_LOSS_ONE_HAND_FOOT_EYE.get(), $15_000, $15_000, $5_000);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC58_LOSS_MORE_FINGERS_TOES.get(), $3_000, $3_000, $1_000);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC59_LOSS_ONE_FINGER_TOE.get(), $1_500, $1_500, $500);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC60_DISLOCATION.get(), "OPEN REDUCTION", "CLOSED REDUCTION AMOUNT", "WITHOUT ANESTHESIA");
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC61_DISLOCATION_HIP.get(), "$8,000.00", $4_000, $1_000);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC62_DISLOCATION_KNEE.get(), $4_000, $2_000, $500);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC63_DISLOCATION_ANKLE_OR_FOOT.get(), $3_200, $1_600, $400);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC64_DISLOCATION_COLLAR_BONE_STER.get(), $2_000, $1_000, $250);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC65_DISLOCATION_LOWER_JAW.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC66_DISLOCATION_SHOULDER.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC67_DISLOCATION_ELBOW.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC68_DISLOCATION_WRIST.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC69_DISLOCATION_HAND.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC70_DISLOCATION_COLLAR_BONE_ACRO.get(), $400, $200, $50);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC71_DISLOCATION_ONE_TOE_FIGURE.get(), $400, $200, $50);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC72_FRACTURE.get(), "OPEN REDUCTION", "CLOSED REDUCTION AMOUNT", "WITHOUT ANESTHESIA");
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC73_FRACTURE_SKULL.get(), $10_000, $5_000, "$1,250.00");
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC74_FRACTURE_HIP.get(), "$6,000.00", $3_000, "$750.00");
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC75_FRACTURE_VERTEBRA.get(), $3_200, $1_600, $400);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC76_FRACTURE_PELVIS.get(), $3_200, $1_600, $400);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC77_FRACTURE_LEG.get(), $3_200, $1_600, $400);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC78_FRACTURE_FACE_OR_NOSE.get(), $1_400, $700, $175);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC79_FRACTURE_UPPER_JAW.get(), $1_400, $700, $175);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC80_FRACTURE_UPPER_ARM.get(), $1_400, $700, $175);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC81_FRACTURE_LOWER_JAW.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC82_FRACTURE_SHOULDER_BLADE.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC83_FRACTURE_VERTEBRAL_PROCESS.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC84_FRACTURE_FOREARM_HAND_WRIST.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC85_FRACTURE_KNEECAP.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC86_FRACTURE_FOOT.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC87_FRACTURE_ANKLE.get(), $1_200, $600, $150);
        checkEmployeeSpouseAndChildValues(enhancedDataSection, ACC88_FRACTUR_RIB.get(), $1_000, $500, "$125.00");

        //validation for optionalData section
        XmlValidator optionalDataSection = getNeededSection(benefitDetailsInfoItemsSection, OPTIONAL_DATA);
        checkEmployeeSpouseAndChildValues(optionalDataSection, ACC90_ACCIDENTAL_DEATH.get(), $5_000, $5_000, $1_000);
        checkEmployeeSpouseAndChildValues(optionalDataSection, ACC91_ENHANCED_PHYSICIAN_CARE.get(), "$25.00");
        checkEmployeeSpouseAndChildValues(optionalDataSection, ACC92_HOSPITAL_CONFINEMENT.get(), "$40.00");
        checkEmployeeSpouseAndChildValues(optionalDataSection, ACC93_HOSPITAL_ICU_CONFINEMENT.get(), $100);
        checkEmployeeSpouseAndChildValues(optionalDataSection, ACC94_ENHANCED_EMERGENCY_ROOM.get(), $100);
        checkEmployeeSpouseAndChildValues(optionalDataSection, ACC95_DISABLED_FOR_FOR_PERIOD.get(), "Included");

        //validation for benefitCostInfoItems section
        XmlValidator benefitCostInfoItemsSection = getNeededSection(xmlValidator, BENEFIT_COST_INFO_SECTION_BY_PLAN_NAME, BASE_BUY_UP); //ACC4_PLAN_NAME
        Map<DocGenEnum.AllSections, String> mapBenefitCostInfoItemsSection = new HashMap<>();
        mapBenefitCostInfoItemsSection.put(ACC1_COLUMN_TITLE_FOR_RATES_EMPLOYEE, "Employee Monthly Rates (Per Employee; guaranteed for 2 years)");
        mapBenefitCostInfoItemsSection.put(ACC1_COLUMN_TITLE_FOR_RATES_EMPLOYER, "Employer Monthly Rates (Per Employee; guaranteed for 2 years)");
        benefitCostInfoItemsSection.checkDocument(mapBenefitCostInfoItemsSection);
    }

    private Map<String, String> getVauesFromUIForTiersDataItemsSection() {
        Map<String, String> map = new HashMap<>();

        classificationManagementMPTab.navigate();
        tablePlansAndCoverages.getContinuousValue().forEach(row -> {
            tablePlansAndCoverages.getRow(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName(), row.getValue(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName())).getCell(1).click();
            tablePlanTierAndRatingInfo.getContinuousValue().forEach(row2 -> {
                String coverageName = row.getValue(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName());
                String coverageTierName = row2.getValue(PlanTierAndRatingSelection.COVERAGE_TIER.getName());
                String rate = new Currency(row2.getValue(PlanTierAndRatingSelection.RATE.getName())).toPlainString();
                map.put(coverageName + coverageTierName + "rate", rate);
            });
        });

        premiumSummaryTab.navigate();
        premiumSummaryCoveragesTable.getContinuousValue().forEach(row -> {
            PremiumSummaryTab.openPremiumSummaryByPayorTable(row.getValue(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName()));
            premiumSummaryByPayorCoveragesTable.getRow(1).getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName())
                    .controls.links.getFirst().click(Tab.doubleWaiter);
            premiumSummaryClassNameTable.getRow(1).getCell(PremiumSummaryClassNameTable.CLASS_NAME.getName())
                    .controls.links.getFirst().click(Tab.doubleWaiter);
            premiumSummarySubGroupTiersTable.getContinuousValue().forEach(row2 -> {
                String coverageName = row.getValue(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName());
                String coverageTierName = row2.getValue(PremiumSummarySubGroupTiersTable.COVERAGE_TIER_NAME.getName());
                String modalPremium = new Currency(row2.getValue(PremiumSummarySubGroupTiersTable.MODAL_PREMIUM.getName())).toPlainString();
                map.put(coverageName + coverageTierName + "modalPremium", modalPremium);
            });
        });

        return map;
    }

    private void checkTiersDataItemsSection(XmlValidator xmlValidator, List<String> coverageTiersList, Map<String, String> valuesMap) {
        coverageTiersList.forEach(coverageTier -> {
            //check tiersDataItemsSection for Enhanced Accident coverage
            XmlValidator tiersDataItemsSectionEnhancedAccident = getNeededSection(xmlValidator, ACC5_TIER_NAME_BY_TIER_NAME_AND_COVERAGE_CD, coverageTier, "ENHANCED");
            Map<DocGenEnum.AllSections, String> mapTiersDataItemsSectionEnhancedAccident = new HashMap<>();
            String enhancedAccidentPlusCoverageTier = ENHANCED_ACCIDENT + coverageTier;
            mapTiersDataItemsSectionEnhancedAccident.put(ACC6_MONTHLY_RATE_FOR_EMPLOYEE, valuesMap.get(enhancedAccidentPlusCoverageTier + "rate"));
            mapTiersDataItemsSectionEnhancedAccident.put(ACC7_MONTHLY_RATE_FOR_EMPLOYER, "0.00");
            mapTiersDataItemsSectionEnhancedAccident.put(ACC8_MONTHLY_PREMIUM, valuesMap.get(enhancedAccidentPlusCoverageTier + "modalPremium"));
            tiersDataItemsSectionEnhancedAccident.checkDocument(mapTiersDataItemsSectionEnhancedAccident);

            //check tiersDataItemsSection for Basic Accident coverage
            XmlValidator tiersDataItemsSection = getNeededSection(xmlValidator, ACC5_TIER_NAME_BY_TIER_NAME_AND_COVERAGE_CD, coverageTier, "BASIC");
            Map<DocGenEnum.AllSections, String> mapTiersDataItemsSection = new HashMap<>();
            String basicAccidentPlusCoverageTier = BASIC_ACCIDENT + coverageTier;
            mapTiersDataItemsSection.put(ACC6_MONTHLY_RATE_FOR_EMPLOYEE, "0.00");
            mapTiersDataItemsSection.put(ACC7_MONTHLY_RATE_FOR_EMPLOYER, valuesMap.get(basicAccidentPlusCoverageTier + "rate"));
            mapTiersDataItemsSection.put(ACC8_MONTHLY_PREMIUM, valuesMap.get(basicAccidentPlusCoverageTier + "modalPremium"));
            tiersDataItemsSection.checkDocument(mapTiersDataItemsSection);
        });
    }

    private void checkEmployeeSpouseAndChildValues(XmlValidator xmlValidator, String sectionPrefix, String employee, String spouse, String child) {
        xmlValidator.checkDocument(String.format(EMPLOYEE_VALUES_BY_PREFIX_PATH, sectionPrefix), employee);
        xmlValidator.checkDocument(String.format(SPOUSE_VALUES_BY_PREFIX_PATH, sectionPrefix), spouse);
        xmlValidator.checkDocument(String.format(CHILD_VALUES_BY_PREFIX_PATH, sectionPrefix), child);
    }

    private void checkEmployeeSpouseAndChildValues(XmlValidator xmlValidator, String sectionPrefix, String expectedValue) {
        xmlValidator.checkDocument(String.format(EMPLOYEE_VALUES_BY_PREFIX_PATH, sectionPrefix), expectedValue);
        xmlValidator.checkDocument(String.format(SPOUSE_VALUES_BY_PREFIX_PATH, sectionPrefix), expectedValue);
        xmlValidator.checkDocument(String.format(CHILD_VALUES_BY_PREFIX_PATH, sectionPrefix), expectedValue);
    }
}
