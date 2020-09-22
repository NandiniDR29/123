package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
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
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.CON;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.VOL;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_LTD;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_LTD_VOLUNTARY;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.ASSUMPTIONS;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;
import static org.assertj.core.api.Assertions.assertThat;

public class TestSalesforceMappingProposalAssumptionFieldCheckForLTD extends SalesforceBaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-45925"}, component = INTEGRATION)
    public void testSalesforceMappingProposalAssumptionFieldToUnderwritingNotes_LTD() {
        LOGGER.info("General Preconditions");
        mainApp().reopen();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(longTermDisabilityMasterPolicy.getType()));
        longTermDisabilityMasterPolicy.createQuote(getDefaultLTDMasterPolicyData()
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()));
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        String assumptionValue = RandomStringUtils.random(15, "1234567890abcdefghijklmnopqrstuvwxyz");
        String assumptionNewValue = RandomStringUtils.random(15, "1234567890abcdefghijklmnopqrstuvwxyz");
        String quoteIdCoverage1 = String.format("%s_%s_%s", quoteNumber, CON, SF_LTD);
        String quoteIdCoverage2 = String.format("%s_%s_%s", quoteNumber, VOL, SF_LTD_VOLUNTARY);

        longTermDisabilityMasterPolicy.propose().start();
        longTermDisabilityMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), ProposalActionTab.class, false);

        proposalActionTab.getAssetList().getAsset(ASSUMPTIONS).setValue(assumptionValue);
        String proposalNumber = ProposalActionTab.labelProposalNumber.getValue();
        ProposalActionTab.buttonCalculatePremium.click();
        ProposalActionTab.buttonGeneratePreProposal.click();
        ProposalActionTab.dialogPreProposal.buttonYes.click();

        LOGGER.info("Step#1 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCov1 = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getUnderwritingNotes() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage1),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));;
        assertThat(responseQuoteCov1.getModel().getUnderwritingNotes()).isEqualTo(assumptionValue);

        LOGGER.info("Step#2 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCov2 = getResponseQuote(quoteIdCoverage2);
        assertThat(responseQuoteCov2.getModel().getUnderwritingNotes()).isEqualTo(assumptionValue);

        LOGGER.info("Step#6 execution");
        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        proposalActionTab.getAssetList().getAsset(ASSUMPTIONS).setValue(assumptionNewValue);
        ProposalActionTab.buttonSaveAndExit.click();

        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        ProposalActionTab.buttonGenerateProposal.click();
        Page.dialogConfirmation.buttonYes.click();

        LOGGER.info("Step#7 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCov1Updated = RetryService.run(
                predicate -> (predicate.getResponse().getStatus() == 200) && !predicate.getModel().getUnderwritingNotes().equals(assumptionValue),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage1),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(responseQuoteCov1Updated.getModel().getUnderwritingNotes()).isEqualTo(assumptionNewValue);

        LOGGER.info("Step#8 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCov2Updated = getResponseQuote(quoteIdCoverage2);
        assertThat(responseQuoteCov2Updated.getModel().getUnderwritingNotes()).isEqualTo(assumptionNewValue);
    }
}
