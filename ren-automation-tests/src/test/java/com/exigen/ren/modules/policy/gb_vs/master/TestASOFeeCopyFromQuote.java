package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.helpers.logging.RatingLogHolder;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.CopyPolicyActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.CopyQuoteActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.RateBasisValues.PER_EMPLOYEE_PER_MONTH;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryASOFeeTable.ASO_FEE_BASIS;
import static com.exigen.ren.main.enums.TableConstants.ProposalASOFeeTable.PROPOSED_ANNUAL_ASO_FEE;
import static com.exigen.ren.main.enums.TableConstants.ProposalASOFeeTable.PROPOSED_ASO_FEE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.FEE_UPDATE_REASON;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.proposalASOFeeTable;
import static com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab.SELECT_QUOTE_BY_ROW_NUMBER_KEY;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.ClassificationManagementTabMetaData.PLAN_TIER_AND_RATING_INFO;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestASOFeeCopyFromQuote extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-27767", "REN-23491"}, component = POLICY_GROUPBENEFITS)
    public void testASOFeeCopyFromQuote() {

        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());

        LOGGER.info("REN-27767 Steps#1, 2, 3 verification");
        quoteInitiateAndFillUpToTab(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.ASO_PLAN.getLabel()), VALUE_YES)
                .adjust(tdSpecific().getTestData("TestDataForASOPlan").resolveLinks()).resolveLinks(), ClassificationManagementTab.class, true);
        assertThat(ClassificationManagementTab.tablePlanTierAndRatingInfo).isPresent();
        classificationManagementMpTab.submitTab();

        LOGGER.info("REN-23491 Step#1 verification");
        assertThat(PremiumSummaryTab.premiumSummaryASOFeeTable).isPresent();
        assertThat(premiumSummaryTab.getAssetList().getAsset(PremiumSummaryTabMetaData.ASO_FEE_BASIS.getLabel())).isPresent();

        LOGGER.info("REN-27767 Step#4, REN-23491 Step#2 verification");
        assertThat(PremiumSummaryTab.premiumSummaryASOFeeTable.getRowsCount()).isEqualTo(0);

        LOGGER.info("REN-27767 Steps#5, REN-23491 Step#4 verification");
        premiumSummaryTab.rate();
        String quoteNumber = QuoteSummaryPage.getQuoteNumber();

        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(quoteNumber);
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();

        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));
        Currency underwrittenASOFee = new Currency((responseFromLog.get("adminCost.ASOFee")), RoundingMode.HALF_DOWN);
        int participants = Integer.parseInt(tdSpecific().getValue("ClassificationManagementTab_ASO_ALACARTE", PLAN_TIER_AND_RATING_INFO.getLabel(), PlanTierAndRatingInfoMetaData.NUMBER_OF_PARTICIPANTS.getLabel()));
        asoFeeTableValuesVerification(underwrittenASOFee, participants);

        LOGGER.info("REN-27767 Step#6 verification");
        premiumSummaryTab.submitTab();
        String policyEffDate = PolicySummaryPage.labelPolicyEffectiveDate.getValue();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("REN-27767 Step#7 verification");
        groupVisionMasterPolicy.copyQuote().perform(groupVisionMasterPolicy.getDefaultTestData(TestDataKey.COPY_FROM_QUOTE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath("CopyQuoteActionTab", CopyQuoteActionTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()), policyEffDate)
                .adjust(TestData.makeKeyPath("CopyQuoteActionTab", CopyQuoteActionTabMetaData.COPY_UNDERWRITTEN_RATES.getLabel()), VALUE_NO));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);

        LOGGER.info("REN-27767 Step#9 verification, REN-23491 Step#2");
        groupVisionMasterPolicy.dataGather().start();
        premiumSummaryTab.navigateToTab();
        assertThat(PremiumSummaryTab.premiumSummaryASOFeeTable.getRowsCount()).isEqualTo(0);
        assertThat(PremiumSummaryTab.premiumSummaryASOFeeTable.getHeader()).doesNotHaveValue(ImmutableList.of(PROPOSED_ASO_FEE.getName(), PROPOSED_ANNUAL_ASO_FEE.getName()));

        LOGGER.info("REN-27767 Steps#10 verification");
        premiumSummaryTab.rate();
        asoFeeTableValuesVerification(underwrittenASOFee, participants);

        LOGGER.info("REN-27767 Steps#11 verification");
        premiumSummaryTab.submitTab();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        initiateProposeAdSetASOFeeValues(underwrittenASOFee, participants);

        LOGGER.info("REN-23491 Steps#13 verification");
        ProposalActionTab.buttonSaveAndExit.click();
        MainPage.QuickSearch.search(policyNumber);

        groupVisionMasterPolicy.dataGather().start();
        policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.POLICY_TOTAL_NUMBER_ELIGIBLE_LIVES).setValue("55");
        premiumSummaryTab.navigateToTab();
        assertThat(PremiumSummaryTab.premiumSummaryASOFeeTable.getRowsCount()).isEqualTo(0);
        premiumSummaryTab.submitTab();

        LOGGER.info("REN-27767 Steps#12 verification");
        initiateProposeAdSetASOFeeValues(underwrittenASOFee, participants);
        groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).overrideRules(Collections.singletonList("Proposal for ASO Plan requires Underwriter approval"), "Life");
        groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).submitTab();
        MainPage.QuickSearch.search(policyNumber);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        LOGGER.info("REN-27767 Steps#15 verification");
        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.issue().perform(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(PlanDefinitionIssueActionTab.class.getSimpleName(), "PlanKey"), "ASO A La Carte-ASO A La Carte").resolveLinks());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("REN-27767 Steps#16 verification");
        groupVisionMasterPolicy.policyCopy().perform(groupVisionMasterPolicy.getDefaultTestData("CopyFromPolicy", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath("CopyPolicyActionTab", CopyPolicyActionTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()), policyEffDate));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
    }

    private void asoFeeTableValuesVerification(Currency underwrittenASOFee, int participants) {
        Row premiumSummaryASOTableRow = PremiumSummaryTab.premiumSummaryASOFeeTable.getRow(ImmutableMap.of(ASO_FEE_BASIS.getName(), PER_EMPLOYEE_PER_MONTH));
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(premiumSummaryASOTableRow.getCell(TableConstants.PremiumSummaryASOFeeTable.PARTICIPANTS.getName()))
                    .hasValue(String.valueOf(participants));
            softly.assertThat(premiumSummaryASOTableRow.getCell(TableConstants.PremiumSummaryASOFeeTable.UNDERWRITTEN_ASO_FEE.getName()))
                    .hasValue(String.valueOf(underwrittenASOFee));
            softly.assertThat(premiumSummaryASOTableRow.getCell(TableConstants.PremiumSummaryASOFeeTable.UNDERWRITTEN_ANNUAL_ASO_FEE.getName()))
                    .hasValue(String.valueOf(underwrittenASOFee.multiply(12).multiply(participants)));
        });
    }

    private void initiateProposeAdSetASOFeeValues(Currency underwrittenASOFee, int participants) {
        groupVisionMasterPolicy.propose().start();
        groupVisionMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(QuotesSelectionActionTab.class.getSimpleName(), SELECT_QUOTE_BY_ROW_NUMBER_KEY), "2"), ProposalActionTab.class);
        groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).getAssetList().getAsset(ProposalTabMetaData.PROPOSED_ASO_FEE)
                .setValue(String.valueOf(underwrittenASOFee.subtract(1)));
        groupVisionMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).getAssetList().getAsset(FEE_UPDATE_REASON).setValueByIndex(1);
        ProposalActionTab.buttonCalculatePremium.click();
        assertThat(proposalASOFeeTable.getColumn(PROPOSED_ANNUAL_ASO_FEE.getName()).getCell(1))
                .hasValue(String.valueOf((underwrittenASOFee.subtract(1)).multiply(12).multiply(participants)));
    }
}
