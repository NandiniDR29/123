package com.exigen.ren.modules.docgen.gb_vs;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage.PlanSummary;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;
import static com.exigen.ren.main.enums.PolicyConstants.PlanVision.A_LA_CARTE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.PROPOSED_TERM_RATE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.NUMBER_OF_PARTICIPANTS;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.RATE_TYPE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCaseProfileNumber;
import static com.exigen.ren.utils.DBHelper.EntityType.CASE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCombinedProposalIncludeAreaBasedForVision_ASOPlanNo extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23233"}, component = POLICY_GROUPBENEFITS)
    public void testCombinedProposalIncludeAreaBasedForVision_ASOPlanNo() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_PolicyCreationWithTwoPlans"), groupVisionMasterPolicy.getType());

        int numberOfParticipants = 5;
        TestData tdPolicy = getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_NO)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NV")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), RATE_GUARANTEE_MONTHS.getLabel()), "12")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", RATING.getLabel()), new SimpleDataProvider().adjust(RATE_TYPE.getLabel(), "Family Tier"))
                .adjust(TestData.makeKeyPath(classificationManagementMpTab.getMetaKey() + "[0]", PLAN_TIER_AND_RATING_INFO.getLabel(), COVERAGE_TIER.getLabel()), "Employee Only")
                .adjust(TestData.makeKeyPath(classificationManagementMpTab.getMetaKey() + "[0]", PLAN_TIER_AND_RATING_INFO.getLabel(), NUMBER_OF_PARTICIPANTS.getLabel()), String.valueOf(numberOfParticipants))
                .resolveLinks();
        groupVisionMasterPolicy.createQuote(tdPolicy);

        String caseProfileNumber = labelCaseProfileNumber.getValue();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        groupVisionMasterPolicy.propose().start().getWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), ProposalActionTab.class);
        Currency proposedTermRate = new Currency(groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).getAssetList().getAsset(PROPOSED_TERM_RATE).getValue());
        Currency premium = new Currency(proposedTermRate).multiply(numberOfParticipants);
        groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).submitTab();
        MainPage.QuickSearch.search(policyNumber);

        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(caseProfileNumber, CASE);

        LOGGER.info("REN-23233 Step 1");
        // VIS_WITH_AREAS
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkDocument(VIS_WITH_AREAS, "false");

        //VIS1_TITLE_FOR_RATES_LINE2
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkDocument(VIS1_TITLE_FOR_RATES_LINE1, "Monthly Vision Rates");

        //VIS1_TITLE_FOR_RATES_LINE2
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkDocument(VIS1_TITLE_FOR_RATES_LINE2, "(guaranteed for one year***)");

        //VIS3_TIER_NAME
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkDocument(VIS3_TIER_NAME, "Employee Only");

        //VIS4_COVERED_LIVES
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkDocument(VIS4_COVERED_LIVES, String.valueOf(numberOfParticipants));

        //VIS4A_TIER_RATE_AMOUNT_ITEMS
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkNodeNotPresent(VIS4A_TIER_RATE_AMOUNT_ITEMS);

        //VIS5_MONTHLY_RATE
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkDocument(VIS5_MONTHLY_RATE, proposedTermRate.toPlainString());

        //VIS6_MONTHLY_PREMIUM
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkDocument(VIS6_MONTHLY_PREMIUM, premium.toPlainString());

        //VIS2a planName is present
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE);

        //VIS2a policyNumber is present
        getQuoteInfoSection(xmlValidator, policyNumber);

        //VIS7_TOTAL_COVERED_LIVES
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkDocument(VIS7_TOTAL_COVERED_LIVES, String.valueOf(numberOfParticipants));

        //VIS7_TOTAL_MONTHLY_PREMIUM
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkDocument(VIS7_TOTAL_MONTHLY_PREMIUM, premium.toPlainString());

        //VIS7A_PROPOSED_ASO_FEE_VALUE
        getBenefitCostInfoSection(xmlValidator, policyNumber, A_LA_CARTE).checkNodeNotPresent(VIS7A_PROPOSED_ASO_FEE_VALUE);

        LOGGER.info("REN-23233 Step 2 UI verifications");
        //VIS6
        assertThat(QuoteSummaryPage.tablePlanSummary.getRow(1).getCell(PlanSummary.MODAL_PREMIUM.getName())).hasValue(premium.toString());

        //VIS1
        groupVisionMasterPolicy.quoteInquiry().start();
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Rate Guarantee (Months)"))).hasValue("12");

        //VIS3
        NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Coverage Tiers"))).hasValue("Employee Only");

        //VIS4 VIS7
        NavigationPage.toLeftMenuTab(CLASSIFICATION_MANAGEMENT);
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Number of Participants"))).hasValue(String.valueOf(numberOfParticipants));

        //VIS5 VIS7
        assertThat(new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Proposed Rate"))).hasValue(proposedTermRate.toString());
    }
}
