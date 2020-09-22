package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceOpportunityModel;
import com.exigen.ren.rest.salesforce.model.SalesforceUserModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.SALES_SUPPORT_ASSOCIATE;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestSalesSupportContactMappingVerification extends SalesforceBaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-43707"}, component = INTEGRATION)
    public void testSalesSupportContactMappingVerification() {
        LOGGER.info("Admin Preconditions for sync with SF");
        adminApp().reopen();
        profileCorporate.update("kgorman", tdSpecific().getTestData("TestDataKaren"));
        profileCorporate.update("rgray", tdSpecific().getTestData("TestDataRoberta"));

        LOGGER.info("General Preconditions");
        mainApp().reopen();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()));
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupAccidentMasterPolicy.getType()));
        String caseNumber = CaseProfileSummaryPage.labelCaseProfileNumber.getValue();

        ResponseContainer<SalesforceOpportunityModel> responseOpportunity = getResponseOpportunity(caseNumber);
        String userIdRoberta = responseOpportunity.getModel().getOpportunityOwnerID();

        groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.INTERNAL_TEAM.getLabel(), SALES_SUPPORT_ASSOCIATE.getLabel()), "Roberta Gray"));

        LOGGER.info("Step#1 verification");
        ResponseContainer<SalesforceOpportunityModel> responseOpportunityWithQuote =
                RetryService.run(predicate -> (predicate.getModel().getSalesSupportContact() != null),
                () -> salesforceService.getOpportunity(caseNumber),
                StopStrategies.stopAfterAttempt(15), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        assertThat(responseOpportunityWithQuote.getModel().getSalesSupportContact()).isEqualTo(userIdRoberta);

        LOGGER.info("Step#2 execution");
        groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.INTERNAL_TEAM.getLabel(), SALES_SUPPORT_ASSOCIATE.getLabel()), "Karen Gorman"));

        LOGGER.info("Step#3 verification");
        ResponseContainer<SalesforceOpportunityModel> responseOpportunityNew =
                RetryService.run(predicate -> (!predicate.getModel().getSalesSupportContact().equals(userIdRoberta)),
                        () -> getResponseOpportunity(caseNumber),
                        StopStrategies.stopAfterAttempt(15), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        ResponseContainer<SalesforceUserModel> responseUserAfterUpdate = getResponseUser(responseOpportunityNew.getModel().getSalesSupportContact());
        assertThat(responseUserAfterUpdate.getModel().getUserName()).isEqualTo("kgorman@renaissancefamily.com.vpc");//email related to Karen Gorman
    }
}
