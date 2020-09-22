package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceQuoteModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.CoveragesConstants.CoverageTiers.*;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupDentalCoverages.DENTAL;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ALACARTE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.ASOALC;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_DN;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_DN_ASO;
import static com.exigen.ren.main.enums.TableConstants.PlanTierAndRatingSelection.COVERAGE_TIER;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.*;
import static com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs.ViewRateHistoryDialog.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS_CHANGE_CONFIRMATION;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab.buttonViewRateHistory;
import static com.exigen.ren.main.pages.summary.CaseProfileSummaryPage.navigateToProposalsTab;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestSalesforceProposedQuoteMappingVerificationForDN extends SalesforceBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-43738"}, component = INTEGRATION)
    public void testSalesforceProposedQuoteMappingVerificationFor_DN() {
        LOGGER.info("General Preconditions");
        String quoteNumber = createQuote(DEFAULT_TEST_DATA_KEY, "TestDataALACARTE");
        String proposalNumber = preProposeAndGoToClassManagementTab(quoteNumber, false,
                ImmutableList.of("Proposal with an A La Carte Plan requires Underwriter approval",
                        "Proposal requires Underwriter approval because Major Waiting Period is less t..."));

        planTierAndRatingInfoTable.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_ONLY).getCell(5).controls.links.get(CHANGE).click();
        String rateEOnly = new Currency(rateValue.getValue()).toPlainString();
        String proposedRateEOnly = new Currency(proposedRateValue.getValue()).toPlainString();

        planTierAndRatingInfoTable.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_SPOUSE).getCell(5).controls.links.get(CHANGE).click();
        String rateSpouse = new Currency(rateValue.getValue()).toPlainString();
        String proposedRateSpouse = new Currency(proposedRateValue.getValue()).toPlainString().substring(0, 4);

        planTierAndRatingInfoTable.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_CHILD_REN).getCell(5).controls.links.get(CHANGE).click();
        String rateChild = new Currency(rateValue.getValue()).toPlainString();
        String proposedRateChild = new Currency(proposedRateValue.getValue()).toPlainString().substring(0, 5);

        planTierAndRatingInfoTable.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_FAMILY).getCell(5).controls.links.get(CHANGE).click();
        String rateFam = new Currency(rateValue.getValue()).toPlainString();
        String proposedRateFam = new Currency(proposedRateValue.getValue()).toPlainString();
        Tab.buttonCancel.click();

        String quoteIdCoverage = String.format("%s_%s_%s Insured", quoteNumber, ALACARTE, SF_DN);

        LOGGER.info("Step#1 execution");
        ResponseContainer<SalesforceQuoteModel> responseQuotePreProposed = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getFirstPartyFormulaRate() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuotePreProposed.getModel().getFirstPartyFormulaRate()).isEqualTo(rateEOnly);
            softly.assertThat(responseQuotePreProposed.getModel().getSecondPartyFormulaRate()).isEqualTo(rateSpouse);
            softly.assertThat(responseQuotePreProposed.getModel().getThirdPartyFormulaRate()).isEqualTo(rateChild);
            softly.assertThat(responseQuotePreProposed.getModel().getFourthPartyFormulaRate()).isEqualTo(rateFam);

            softly.assertThat(responseQuotePreProposed.getModel().getFirstPartyQuotedRate()).isEqualTo(proposedRateEOnly);
            softly.assertThat(responseQuotePreProposed.getModel().getSecondPartyQuotedRate()).isEqualTo(proposedRateSpouse);
            softly.assertThat(responseQuotePreProposed.getModel().getThirdPartyQuotedRate()).isEqualTo(proposedRateChild);
            softly.assertThat(responseQuotePreProposed.getModel().getFourthPartyQuotedRate()).isEqualTo(proposedRateFam);
        });

        LOGGER.info("Step#2 execution");
        completeProposalWithNewTermRateAndGoToRateHistory(proposalNumber, quoteNumber);

        tableRateHistory.getRowContains(TableConstants.RateHistoryTable.COVERAGE_NAME.getName(), DENTAL).getCell(1).controls.links.getFirst().click();
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

        tableDefaultTiers.getRowContains("Tier Name", EMPLOYEE_PLUS_CHILD_REN).getCell(1).controls.links.getFirst().click();
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

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-43739"}, component = INTEGRATION)
    public void testSalesforceProposedQuoteMappingVerificationFor_DN_ASO() {
        LOGGER.info("General Preconditions");
        String quoteNumber = createQuote("TestDataASO", "TestDataASOALC");
        String proposalNumber = preProposeAndGoToClassManagementTab(quoteNumber, true, ImmutableList.of(
                "Proposal for ASO Plan will require Underwriter approval",
                "Proposal requires Underwriter approval because Major Waiting Period is less t...",
                "Proposal requires Underwriter approval because Prosthodontics Waiting Period ..."));

        planTierAndRatingInfoTable.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_ONLY).getCell(5).controls.links.get(CHANGE).click();
        String rateEOnly = new Currency(rateValue.getValue()).toPlainString();
        String proposedRateEOnly = new Currency(proposedRateValue.getValue()).toPlainString();

        planTierAndRatingInfoTable.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_SPOUSE).getCell(5).controls.links.get(CHANGE).click();
        String rateSpouse = new Currency(rateValue.getValue()).toPlainString();
        String proposedRateSpouse = new Currency(proposedRateValue.getValue()).toPlainString();

        planTierAndRatingInfoTable.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_CHILD_REN).getCell(5).controls.links.get(CHANGE).click();
        String rateChild = new Currency(rateValue.getValue()).toPlainString();
        String proposedRateChild = new Currency(proposedRateValue.getValue()).toPlainString();

        planTierAndRatingInfoTable.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_FAMILY).getCell(5).controls.links.get(CHANGE).click();
        String rateFam = new Currency(rateValue.getValue()).toPlainString();
        String proposedRateFam = new Currency(proposedRateValue.getValue()).toPlainString();
        Tab.buttonCancel.click();

        String quoteIdCoverage = String.format("%s_%s_%s", quoteNumber, ASOALC, SF_DN_ASO);

        LOGGER.info("Step#1 execution");
        ResponseContainer<SalesforceQuoteModel> responseQuotePreProposed = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getFirstPartyFormulaRate() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuotePreProposed.getModel().getFirstPartyFormulaRate()).isEqualTo(rateEOnly);
            softly.assertThat(responseQuotePreProposed.getModel().getSecondPartyFormulaRate()).isEqualTo(rateSpouse);
            softly.assertThat(responseQuotePreProposed.getModel().getThirdPartyFormulaRate()).isEqualTo(rateChild);
            softly.assertThat(responseQuotePreProposed.getModel().getFourthPartyFormulaRate()).isEqualTo(rateFam);

            softly.assertThat(responseQuotePreProposed.getModel().getFirstPartyQuotedRate()).isEqualTo(proposedRateEOnly);
            softly.assertThat(responseQuotePreProposed.getModel().getSecondPartyQuotedRate()).isEqualTo(proposedRateSpouse);
            softly.assertThat(responseQuotePreProposed.getModel().getThirdPartyQuotedRate()).isEqualTo(proposedRateChild);
            softly.assertThat(responseQuotePreProposed.getModel().getFourthPartyQuotedRate()).isEqualTo(proposedRateFam);
        });

        LOGGER.info("Step#2 execution");
        completeProposalWithNewTermRateAndGoToRateHistory(proposalNumber, quoteNumber);

        tableRateHistory.getRowContains(TableConstants.RateHistoryTable.COVERAGE_NAME.getName(), "ASO Dental").getCell(1).controls.links.getFirst().click();
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

        tableDefaultTiers.getRowContains("Tier Name", EMPLOYEE_PLUS_CHILD_REN).getCell(1).controls.links.getFirst().click();
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

    private String createQuote(String testData, String tdClassMngmnt) {
        mainApp().reopen();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupDentalMasterPolicy.getType()));

        groupDentalMasterPolicy.createQuote(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, testData)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS.getLabel()),
                        ImmutableList.of(EMPLOYEE_ONLY, EMPLOYEE_PLUS_FAMILY, EMPLOYEE_PLUS_SPOUSE, EMPLOYEE_PLUS_CHILD_REN))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
                .adjust(tdSpecific().getTestData(tdClassMngmnt).resolveLinks()));
        return PolicySummaryPage.labelPolicyNumber.getValue();
    }

    private String preProposeAndGoToClassManagementTab(String quoteNumber, boolean isAso, ImmutableList<String> rulesForOverride) {
        groupDentalMasterPolicy.propose().start();
        groupDentalMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), ProposalActionTab.class, false);
        groupDentalMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).fillTab(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(ProposalActionTab.class.getSimpleName(), OVERRIDE_RULES_LIST_KEY), rulesForOverride));
        if (isAso) {
            AssetList groupVisionGetAssetListPath = (AssetList) groupDentalMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).getAssetList();
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.PROPOSED_ASO_FEE).setValue("2");
            groupVisionGetAssetListPath.getAsset(ProposalTabMetaData.FEE_UPDATE_REASON).setValue("Premium Fund");
        }
        IntStream.range(0, 4).forEach(value -> setProposedTermRateMoneyAndUpdateReason(0, value));

        String proposalNumber = ProposalActionTab.labelProposalNumber.getValue();
        performPreProposal();

        MainPage.QuickSearch.search(quoteNumber);
        groupDentalMasterPolicy.policyInquiry().start();
        classificationManagementMpTab.navigateToTab();
        return proposalNumber;
    }

    private void completeProposalWithNewTermRateAndGoToRateHistory(String proposalNumber, String quoteNumber) {
        navigateToProposalsTab();
        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        IntStream.range(0, 4).forEach(value -> setProposedTermRateMoneyAndUpdateReason(0, value));
        proposalActionTab.submitTab();

        MainPage.QuickSearch.search(quoteNumber);
        groupDentalMasterPolicy.quoteInquiry().start();
        premiumSummaryTab.navigateToTab();
        buttonViewRateHistory.click();
    }
}
