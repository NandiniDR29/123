package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceQuoteModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.CON;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.VOL;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_STD;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_STD_VOLUNTARY;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.setProposedTermRateAndUpdateReason;
import static com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs.ViewRateHistoryDialog.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PremiumSummaryTab.buttonViewRateHistory;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestSalesforceProposedQuoteMappingVerificationForSTD extends SalesforceBaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-43730"}, component = INTEGRATION)
    public void testSalesforceProposedQuoteMappingVerificationFor_STD() {
        LOGGER.info("General Preconditions");
        mainApp().reopen();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(shortTermDisabilityMasterPolicy.getType()));
        shortTermDisabilityMasterPolicy.createQuote(getDefaultSTDMasterPolicyData()
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()));
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        shortTermDisabilityMasterPolicy.propose().start();
        shortTermDisabilityMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), ProposalActionTab.class, false);

        IntStream.range(0, 11).forEach(value -> setProposedTermRateAndUpdateReason(0, value));
        IntStream.range(0, 11).forEach(value -> setProposedTermRateAndUpdateReason(1, value));
        proposalActionTab.submitTab();

        MainPage.QuickSearch.search(quoteNumber);
        shortTermDisabilityMasterPolicy.quoteInquiry().start();
        premiumSummaryTab.navigateToTab();
        buttonViewRateHistory.click();

        LOGGER.info("Step#1 execution");
        tableRateHistory.getRowContains(TableConstants.RateHistoryTable.PLAN_NAME.getName(), CON).getCell(1).controls.links.getFirst().click();
        tableClassificationGroups.getRow(1).getCell(1).controls.links.getFirst().click();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "0-24").getCell(1).controls.links.getFirst().click();
        String underwrittenRate24CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate24CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "25-29").getCell(1).controls.links.getFirst().click();
        String underwrittenRate29CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate29CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "30-34").getCell(1).controls.links.getFirst().click();
        String underwrittenRate34CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate34CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "35-39").getCell(1).controls.links.getFirst().click();
        String underwrittenRate39CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate39CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "40-44").getCell(1).controls.links.getFirst().click();
        String underwrittenRate44CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate44CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "45-49").getCell(1).controls.links.getFirst().click();
        String underwrittenRate49CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate49CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "50-54").getCell(1).controls.links.getFirst().click();
        String underwrittenRate54CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate54CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "55-59").getCell(1).controls.links.getFirst().click();
        String underwrittenRate59CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate59CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "60-64").getCell(1).controls.links.getFirst().click();
        String underwrittenRate64CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate64CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "65-69").getCell(1).controls.links.getFirst().click();
        String underwrittenRate69CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate69CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "70+").getCell(1).controls.links.getFirst().click();
        String underwrittenRate70CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate70CON = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue().concat(".0");

        LOGGER.info("Step#2 execution");
        tableRateHistory.getRowContains(TableConstants.RateHistoryTable.PLAN_NAME.getName(), VOL).getCell(1).controls.links.getFirst().click();
        tableClassificationGroups.getRow(1).getCell(1).controls.links.getFirst().click();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "0-24").getCell(1).controls.links.getFirst().click();
        String underwrittenRate24VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate24VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "25-29").getCell(1).controls.links.getFirst().click();
        String underwrittenRate29VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate29VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "30-34").getCell(1).controls.links.getFirst().click();
        String underwrittenRate34VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate34VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "35-39").getCell(1).controls.links.getFirst().click();
        String underwrittenRate39VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate39VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "40-44").getCell(1).controls.links.getFirst().click();
        String underwrittenRate44VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate44VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "45-49").getCell(1).controls.links.getFirst().click();
        String underwrittenRate49VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate49VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "50-54").getCell(1).controls.links.getFirst().click();
        String underwrittenRate54VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate54VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "55-59").getCell(1).controls.links.getFirst().click();
        String underwrittenRate59VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate59VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "60-64").getCell(1).controls.links.getFirst().click();
        String underwrittenRate64VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate64VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "65-69").getCell(1).controls.links.getFirst().click();
        String underwrittenRate69VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate69VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();

        tableClassificationSubGroups.getRowContains(TableConstants.ClassificationSubGroupsTable.SUB_GROUP_NAME.getName(), "70+").getCell(1).controls.links.getFirst().click();
        String underwrittenRate70VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Underwritten Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue();
        String proposedRate70VOL = tableRateHistoryForSubGroup.getRowContains(TableConstants.RateHistoryForSubGroupTable.RATE_TYPE.getName(), "Proposed Rate")
                .getCell(TableConstants.RateHistoryForSubGroupTable.RATE.getName()).getValue().concat(".0");

        String quoteIdCoverage1 = String.format("%s_%s_%s", quoteNumber, CON, SF_STD);
        String quoteIdCoverage2 = String.format("%s_%s_%s", quoteNumber, VOL, SF_STD_VOLUNTARY);

        LOGGER.info("Step#1 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCON = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getRateUnder20() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage1),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        LOGGER.info("Step#2 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteVOL = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getRateUnder20() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage2),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertSoftly(softly -> {

            LOGGER.info("Step# 1 verification");
            softly.assertThat(responseQuoteCON.getModel().getRateUnder20()).isEqualTo(underwrittenRate24CON);
            softly.assertThat(responseQuoteCON.getModel().getRate20_24()).isEqualTo(underwrittenRate24CON);
            softly.assertThat(responseQuoteCON.getModel().getRate25_29()).isEqualTo(underwrittenRate29CON);
            softly.assertThat(responseQuoteCON.getModel().getRate30_34()).isEqualTo(underwrittenRate34CON);
            softly.assertThat(responseQuoteCON.getModel().getRate35_39()).isEqualTo(underwrittenRate39CON);
            softly.assertThat(responseQuoteCON.getModel().getRate40_44()).isEqualTo(underwrittenRate44CON);
            softly.assertThat(responseQuoteCON.getModel().getRate45_49()).isEqualTo(underwrittenRate49CON);
            softly.assertThat(responseQuoteCON.getModel().getRate50_54()).isEqualTo(underwrittenRate54CON);
            softly.assertThat(responseQuoteCON.getModel().getRate55_59()).isEqualTo(underwrittenRate59CON);
            softly.assertThat(responseQuoteCON.getModel().getRate60_64()).isEqualTo(underwrittenRate64CON);
            softly.assertThat(responseQuoteCON.getModel().getRate65_69()).isEqualTo(underwrittenRate69CON);
            softly.assertThat(responseQuoteCON.getModel().getRate70_74()).isEqualTo(underwrittenRate70CON);

            softly.assertThat(responseQuoteCON.getModel().getProposedRateUnder20()).isEqualTo(proposedRate24CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate20_24()).isEqualTo(proposedRate24CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate25_29()).isEqualTo(proposedRate29CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate30_34()).isEqualTo(proposedRate34CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate35_39()).isEqualTo(proposedRate39CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate40_44()).isEqualTo(proposedRate44CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate45_49()).isEqualTo(proposedRate49CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate50_54()).isEqualTo(proposedRate54CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate55_59()).isEqualTo(proposedRate59CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate60_64()).isEqualTo(proposedRate64CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate65_69()).isEqualTo(proposedRate69CON);
            softly.assertThat(responseQuoteCON.getModel().getProposedRate70_74()).isEqualTo(proposedRate70CON);

            LOGGER.info("Step# 2 verification");
            softly.assertThat(responseQuoteVOL.getModel().getRateUnder20()).isEqualTo(underwrittenRate24VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate20_24()).isEqualTo(underwrittenRate24VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate25_29()).isEqualTo(underwrittenRate29VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate30_34()).isEqualTo(underwrittenRate34VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate35_39()).isEqualTo(underwrittenRate39VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate40_44()).isEqualTo(underwrittenRate44VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate45_49()).isEqualTo(underwrittenRate49VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate50_54()).isEqualTo(underwrittenRate54VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate55_59()).isEqualTo(underwrittenRate59VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate60_64()).isEqualTo(underwrittenRate64VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate65_69()).isEqualTo(underwrittenRate69VOL);
            softly.assertThat(responseQuoteVOL.getModel().getRate70_74()).isEqualTo(underwrittenRate70VOL);

            softly.assertThat(responseQuoteVOL.getModel().getProposedRateUnder20()).isEqualTo(proposedRate24VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate20_24()).isEqualTo(proposedRate24VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate25_29()).isEqualTo(proposedRate29VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate30_34()).isEqualTo(proposedRate34VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate35_39()).isEqualTo(proposedRate39VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate40_44()).isEqualTo(proposedRate44VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate45_49()).isEqualTo(proposedRate49VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate50_54()).isEqualTo(proposedRate54VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate55_59()).isEqualTo(proposedRate59VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate60_64()).isEqualTo(proposedRate64VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate65_69()).isEqualTo(proposedRate69VOL);
            softly.assertThat(responseQuoteVOL.getModel().getProposedRate70_74()).isEqualTo(proposedRate70VOL);
        });
    }
}
