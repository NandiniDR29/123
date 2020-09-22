package com.exigen.ren.modules.externalInterfaces.salesforce;

import  com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceQuoteModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.CoveragesConstants.CoverageTiers.*;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.ENHANCED_10_UNITS;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_GA;
import static com.exigen.ren.main.enums.TableConstants.PlanTierAndRatingSelection.COVERAGE_TIER;
import static com.exigen.ren.main.enums.TableConstants.PlansAndCoverages.COVERAGE_NAME;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.setProposedTermRateMoneyAndUpdateReason;
import static com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs.ViewRateHistoryDialog.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS_CHANGE_CONFIRMATION;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.ClassificationManagementTab.tablePlanTierAndRatingInfo;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.ClassificationManagementTab.tablePlansAndCoverages;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab.buttonViewRateHistory;
import static com.exigen.ren.main.pages.summary.CaseProfileSummaryPage.navigateToProposalsTab;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestSalesforceProposedQuoteMappingVerificationForGA extends SalesforceBaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-43737"}, component = INTEGRATION)
    public void testSalesforceProposedQuoteMappingVerificationFor_AC() {
        LOGGER.info("General Preconditions");
        mainApp().reopen();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupAccidentMasterPolicy.getType()));
        groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS.getLabel()),
                        ImmutableList.of(EMPLOYEE_ONLY, EMPLOYEE_PLUS_FAMILY, EMPLOYEE_PLUS_SPOUSE, EMPLOYEE_PLUS_CHILDREN))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
                .adjust(tdSpecific().getTestData("TestData").resolveLinks()).resolveLinks());
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        groupAccidentMasterPolicy.propose().start();
        groupAccidentMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultACMasterPolicyData(), ProposalActionTab.class, true);

        String proposalNumber = ProposalActionTab.labelProposalNumber.getValue();
        ProposalActionTab.buttonCalculatePremium.click();
        ProposalActionTab.buttonGeneratePreProposal.click();
        ProposalActionTab.dialogPreProposal.buttonYes.click();

        MainPage.QuickSearch.search(quoteNumber);
        groupAccidentMasterPolicy.policyInquiry().start();
        classificationManagementMPTab.navigateToTab();
        tablePlansAndCoverages.getRowContains(COVERAGE_NAME.getName(), ENHANCED_ACCIDENT).getCell(2).click();

        StaticElement rateValue = new StaticElement(By.id("policyDataGatherForm:sedit_GroupClassDefaultCoverRelationTierMoneyView_rate"));
        StaticElement proposedRateValue = new StaticElement(By.id("policyDataGatherForm:sedit_GroupClassDefaultCoverRelationTierMoneyView_proposedRate"));

        tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_ONLY).getCell(5).controls.links.get(CHANGE).click();
        String planEnhancedRateEOnly = new Currency(rateValue.getValue()).toPlainString();
        String planEnhancedProposedRateEOnly = new Currency(proposedRateValue.getValue()).toPlainString();

        tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_SPOUSE).getCell(5).controls.links.get(CHANGE).click();
        String planEnhancedRateSpouse = new Currency(rateValue.getValue()).toPlainString();
        String planEnhancedProposedRateSpouse = new Currency(proposedRateValue.getValue()).toPlainString();

        tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_CHILDREN).getCell(5).controls.links.get(CHANGE).click();
        String planEnhancedRateChild = new Currency(rateValue.getValue()).toPlainString();
        String planEnhancedProposedRateChild = new Currency(proposedRateValue.getValue()).toPlainString();

        tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_FAMILY).getCell(5).controls.links.get(CHANGE).click();
        String planEnhancedRateFam = new Currency(rateValue.getValue()).toPlainString();
        String planEnhancedProposedRateFam = new Currency(proposedRateValue.getValue()).toPlainString();
        Tab.buttonCancel.click();

        LOGGER.info("Step#1 verification");
        String quoteIdCoverage = String.format("%s_%s_%s", quoteNumber, ENHANCED_10_UNITS, SF_GA);
        ResponseContainer<SalesforceQuoteModel> responseQuote = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getFirstPartyFormulaRate() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuote.getModel().getFirstPartyFormulaRate()).isEqualTo(planEnhancedProposedRateEOnly);
            softly.assertThat(responseQuote.getModel().getSecondPartyFormulaRate()).isEqualTo(planEnhancedProposedRateSpouse);
            softly.assertThat(responseQuote.getModel().getThirdPartyFormulaRate()).isEqualTo(planEnhancedProposedRateChild);
            softly.assertThat(responseQuote.getModel().getFourthPartyFormulaRate()).isEqualTo(planEnhancedProposedRateFam);

            softly.assertThat(responseQuote.getModel().getFirstPartyQuotedRate()).isEqualTo(planEnhancedRateEOnly);
            softly.assertThat(responseQuote.getModel().getSecondPartyQuotedRate()).isEqualTo(planEnhancedRateSpouse);
            softly.assertThat(responseQuote.getModel().getThirdPartyQuotedRate()).isEqualTo(planEnhancedRateChild);
            softly.assertThat(responseQuote.getModel().getFourthPartyQuotedRate()).isEqualTo(planEnhancedRateFam);
        });

        LOGGER.info("Step#2 execution");
        navigateToProposalsTab();
        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        IntStream.range(0, 4).forEach(value -> setProposedTermRateMoneyAndUpdateReason(0, value));
        proposalActionTab.submitTab();

        MainPage.QuickSearch.search(quoteNumber);
        groupAccidentMasterPolicy.quoteInquiry().start();
        premiumSummaryTab.navigateToTab();
        buttonViewRateHistory.click();

        tableRateHistory.getRowContains(TableConstants.RateHistoryTable.COVERAGE_NAME.getName(), ENHANCED_ACCIDENT).getCell(1).controls.links.getFirst().click();
        tableClassificationGroups.getRow(1).getCell(1).controls.links.getFirst().click();
        tableClassificationSubGroups.getRow(1).getCell(1).controls.links.getFirst().click();

        tableDefaultTiers.getRowContains("Tier Name", EMPLOYEE_ONLY).getCell(1).controls.links.getFirst().click();
        String underwrittenRateEmployeeOnly = new Currency(tableTiersHareHistory.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue()).toPlainString();
        String proposedRateEmployeeOnly = new Currency(tableTiersHareHistory.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue()).toPlainString();

        tableDefaultTiers.getRowContains("Tier Name", EMPLOYEE_PLUS_SPOUSE).getCell(1).controls.links.getFirst().click();
        String underwrittenRateEmploySpouse = new Currency(tableTiersHareHistory.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue()).toPlainString();
        String proposedRateEmployeeSpouse = new Currency(tableTiersHareHistory.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue()).toPlainString();

        tableDefaultTiers.getRowContains("Tier Name", EMPLOYEE_PLUS_CHILDREN).getCell(1).controls.links.getFirst().click();
        String underwrittenRateEmployChildren = new Currency(tableTiersHareHistory.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue()).toPlainString();
        String proposedRateEmployeeChildren = new Currency(tableTiersHareHistory.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue()).toPlainString();

        tableDefaultTiers.getRowContains("Tier Name", EMPLOYEE_PLUS_FAMILY).getCell(1).controls.links.getFirst().click();
        String underwrittenRateEmployFamily = new Currency(tableTiersHareHistory.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue()).toPlainString();
        String proposedRateEmployeeFamily = new Currency(tableTiersHareHistory.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue()).toPlainString();

        LOGGER.info("Step#3 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteProposed = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getFirstPartyFormulaRate() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteProposed.getModel().getFirstPartyFormulaRate()).isEqualTo(underwrittenRateEmployeeOnly);
            softly.assertThat(responseQuoteProposed.getModel().getSecondPartyFormulaRate()).isEqualTo(underwrittenRateEmploySpouse);
            softly.assertThat(responseQuoteProposed.getModel().getThirdPartyFormulaRate()).isEqualTo(underwrittenRateEmployChildren);
            softly.assertThat(responseQuoteProposed.getModel().getFourthPartyFormulaRate()).isEqualTo(underwrittenRateEmployFamily);

            softly.assertThat(responseQuoteProposed.getModel().getFirstPartyQuotedRate()).isEqualTo(proposedRateEmployeeOnly);
            softly.assertThat(responseQuoteProposed.getModel().getSecondPartyQuotedRate()).isEqualTo(proposedRateEmployeeSpouse);
            softly.assertThat(responseQuoteProposed.getModel().getThirdPartyQuotedRate()).isEqualTo(proposedRateEmployeeChildren);
            softly.assertThat(responseQuoteProposed.getModel().getFourthPartyQuotedRate()).isEqualTo(proposedRateEmployeeFamily);
        });
    }
}
