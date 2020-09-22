package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceQuoteModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.BASIC_LIFE_PLAN;
import static com.exigen.ren.main.enums.PolicyConstants.PlanTermLifeInsurance.VOLUNTARY_LIFE_PLAN;
import static com.exigen.ren.main.enums.SalesforceConstants.*;
import static com.exigen.ren.main.modules.caseprofile.metadata.ProposalTabMetaData.ASSUMPTIONS;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestSalesforceMappingProposalAssumptionFieldCheckForTL extends SalesforceBaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-45915"}, component = INTEGRATION)
    public void testSalesforceMappingProposalAssumptionFieldVerification_TL() {
        LOGGER.info("General Preconditions");
        mainApp().reopen();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(termLifeInsuranceMasterPolicy.getType()));
        termLifeInsuranceMasterPolicy.createQuote(getDefaultTLMasterPolicyData()
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()));
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        String assumptionValue = RandomStringUtils.random(15, "1234567890abcdefghijklmnopqrstuvwxyz");
        String assumptionNewValue = RandomStringUtils.random(15, "1234567890abcdefghijklmnopqrstuvwxyz");

        String quoteIdCoverage1 = String.format("%s_%s_%s", quoteNumber, BASIC_LIFE_PLAN, SF_TL_BASIC_LIFE_AND_ADD);
        String quoteIdCoverage2 = String.format("%s_%s_%s", quoteNumber, BASIC_LIFE_PLAN, SF_TL_DEPENDENT_LIFE);
        String quoteIdCoverage3 = String.format("%s_%s_%s", quoteNumber, VOLUNTARY_LIFE_PLAN, SF_TL_VOLUNTARY_DEPENDENT_LIFE);
        String quoteIdCoverage4 = String.format("%s_%s_%s", quoteNumber, VOLUNTARY_LIFE_PLAN, SF_TL_VOLUNTARY_LIFE_AND_ADD);

        termLifeInsuranceMasterPolicy.propose().start();
        termLifeInsuranceMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultTLMasterPolicyData(), ProposalActionTab.class, false);

        proposalActionTab.getAssetList().getAsset(ASSUMPTIONS).setValue(assumptionValue);
        String proposalNumber = ProposalActionTab.labelProposalNumber.getValue();
        ProposalActionTab.buttonCalculatePremium.click();
        ProposalActionTab.buttonGeneratePreProposal.click();
        ProposalActionTab.dialogPreProposal.buttonYes.click();

        LOGGER.info("Step#1 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCov1 = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getUnderwritingNotes() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage1),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        ResponseContainer<SalesforceQuoteModel> responseQuoteCov2 = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getUnderwritingNotes() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage2),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        ResponseContainer<SalesforceQuoteModel> responseQuoteCov3 = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getUnderwritingNotes() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage3),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        ResponseContainer<SalesforceQuoteModel> responseQuoteCov4 = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getUnderwritingNotes() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage4),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteCov1.getModel().getUnderwritingNotes()).isEqualTo(assumptionValue);

            LOGGER.info("Step#2 verification");
            softly.assertThat(responseQuoteCov2.getModel().getUnderwritingNotes()).isEqualTo(assumptionValue);

            LOGGER.info("Step#3 verification");
            softly.assertThat(responseQuoteCov3.getModel().getUnderwritingNotes()).isEqualTo(assumptionValue);

            LOGGER.info("Step#4 verification");
            softly.assertThat(responseQuoteCov4.getModel().getUnderwritingNotes()).isEqualTo(assumptionValue);
        });

        LOGGER.info("Step#10 execution");
        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        proposalActionTab.getAssetList().getAsset(ASSUMPTIONS).setValue(assumptionNewValue);
        ProposalActionTab.buttonSaveAndExit.click();

        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        ProposalActionTab.buttonGenerateProposal.click();
        Page.dialogConfirmation.buttonYes.click();

        LOGGER.info("Step#11 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuoteCov1Updated = RetryService.run(
                predicate -> (predicate.getResponse().getStatus() == 200) && !predicate.getModel().getUnderwritingNotes().equals(assumptionValue),
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverage1),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteCov1Updated.getModel().getUnderwritingNotes()).isEqualTo(assumptionNewValue);

            LOGGER.info("Step#12 verification");
            ResponseContainer<SalesforceQuoteModel> responseQuoteCov2Updated = getResponseQuote(quoteIdCoverage2);
            softly.assertThat(responseQuoteCov2Updated.getModel().getUnderwritingNotes()).isEqualTo(assumptionNewValue);

            LOGGER.info("Step#13 verification");
            ResponseContainer<SalesforceQuoteModel> responseQuoteCov3Updated = getResponseQuote(quoteIdCoverage3);
            softly.assertThat(responseQuoteCov3Updated.getModel().getUnderwritingNotes()).isEqualTo(assumptionNewValue);

            LOGGER.info("Step#14 verification");
            ResponseContainer<SalesforceQuoteModel> responseQuoteCov4Updated = getResponseQuote(quoteIdCoverage4);
            softly.assertThat(responseQuoteCov4Updated.getModel().getUnderwritingNotes()).isEqualTo(assumptionNewValue);
        });
    }
}
