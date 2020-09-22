package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceQuoteModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NJ_STAT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanStat.NY_STAT;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_ST_DISABILITY_BENEFIT_LAW;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_ST_TEMPORARY_DISABILITY_BENEFITS;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.ASSUMPTIONS;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.*;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;
import static org.assertj.core.api.Assertions.assertThat;

public class TestSalesforceMappingProposalAssumptionFieldCheckForST extends SalesforceBaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    private static final String ASSUMPTION_VALUE = RandomStringUtils.random(15, "1234567890abcdefghijklmnopqrstuvwxyz");
    private static final String ASSUMPTION_NEW_VALUE = RandomStringUtils.random(15, "1234567890abcdefghijklmnopqrstuvwxyz");

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-45938"}, component = INTEGRATION)
    public void testSalesforceMappingProposalAssumptionFieldCheck_ST_NJ() {
        LOGGER.info("General Preconditions");
        String quoteNumber = createQuote("TestData_NJ");
        String quoteIdCoverage = String.format("%s_%s_%s", quoteNumber, NJ_STAT, SF_ST_TEMPORARY_DISABILITY_BENEFITS);
        String proposalNumber = initiateAndPerformPreProposal();

        LOGGER.info("Step#1 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCov1 = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getUnderwritingNotes() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(responseQuoteCov1.getModel().getUnderwritingNotes()).isEqualTo(ASSUMPTION_VALUE);

        LOGGER.info("Step#4 execution");
        updateAssumptionAndGenerateProposal(proposalNumber);

        LOGGER.info("Step#5 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCov1Updated = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && !predicate.getModel().getUnderwritingNotes().equals(ASSUMPTION_VALUE),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(responseQuoteCov1Updated.getModel().getUnderwritingNotes()).isEqualTo(ASSUMPTION_NEW_VALUE);
    }

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-45941"}, component = INTEGRATION)
    public void testSalesforceMappingProposalAssumptionFieldCheck_ST_NY() {
        LOGGER.info("General Preconditions");
        String quoteNumber = createQuote(DEFAULT_TEST_DATA_KEY);
        String quoteIdCoverage = String.format("%s_%s_%s", quoteNumber, NY_STAT, SF_ST_DISABILITY_BENEFIT_LAW);
        String proposalNumber = initiateAndPerformPreProposal();

        LOGGER.info("Step#1 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCov1 = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getUnderwritingNotes() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(responseQuoteCov1.getModel().getUnderwritingNotes()).isEqualTo(ASSUMPTION_VALUE);

        LOGGER.info("Step#4 execution");
        updateAssumptionAndGenerateProposal(proposalNumber);

        LOGGER.info("Step#5 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCov1Updated = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && !predicate.getModel().getUnderwritingNotes().equals(ASSUMPTION_VALUE),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage),
                StopStrategies.stopAfterAttempt(20), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(responseQuoteCov1Updated.getModel().getUnderwritingNotes()).isEqualTo(ASSUMPTION_NEW_VALUE);
    }

    private String createQuote(String testData) {
        mainApp().reopen();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(statutoryDisabilityInsuranceMasterPolicy.getType()));
        statutoryDisabilityInsuranceMasterPolicy.createQuote(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, testData));
        return PolicySummaryPage.labelPolicyNumber.getValue();
    }

    private String initiateAndPerformPreProposal() {
        statutoryDisabilityInsuranceMasterPolicy.propose().start();
        statutoryDisabilityInsuranceMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultSTMasterPolicyData(), ProposalActionTab.class, false);

        proposalActionTab.getAssetList().getAsset(ASSUMPTIONS).setValue(ASSUMPTION_VALUE);
        String proposalNumber = ProposalActionTab.labelProposalNumber.getValue();
        performPreProposal();
        return proposalNumber;
    }

    private void updateAssumptionAndGenerateProposal(String proposalNumber) {
        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        proposalActionTab.getAssetList().getAsset(ASSUMPTIONS).setValue(ASSUMPTION_NEW_VALUE);
        ProposalActionTab.buttonSaveAndExit.click();

        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        buttonGenerateProposal.click();
        dialogConfirmation.buttonYes.click();
    }
}
