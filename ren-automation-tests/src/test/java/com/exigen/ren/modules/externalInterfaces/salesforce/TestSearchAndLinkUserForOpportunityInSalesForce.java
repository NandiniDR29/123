package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.CaseProfileDetailsTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.tabs.DivisionsTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceOpportunityModel;
import com.exigen.ren.rest.salesforce.model.SalesforceUserModel;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CASE;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;
import static org.assertj.core.api.Assertions.assertThat;

public class TestSearchAndLinkUserForOpportunityInSalesForce extends SalesforceBaseTest implements CustomerContext, ProfileContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-20109"}, component = INTEGRATION)
    public void testSearchAndLinkUserForOpportunityInSalesForce() {
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
        String userId = responseOpportunity.getModel().getOpportunityOwnerID();

        LOGGER.info("Step#1 verification");
        //We will work with existing users: Karen Gorman and Roberta Gray. TC will updated in scope https://jira.exigeninsurance.com/browse/REN-45643
        ResponseContainer<SalesforceUserModel> responseUser = getResponseUser(userId);
        assertThat(responseUser.getModel().getUserName()).isEqualTo("kgorman@renaissancefamily.com.vpc");//email related to Karen Gorman
        assertThat(responseOpportunity.getModel().getSalesSupportContact()).isEqualTo(null);

        LOGGER.info("Step#2 verification");
        NavigationPage.toMainTab(CUSTOMER);
        customerNonIndividual.update().perform(tdSpecific().getTestData("TestDataCustomerUpdate").resolveLinks());
        DivisionsTab.buttonSaveAndExit.click();

        NavigationPage.toMainTab(CASE);
        CaseProfileSummaryPage.tableSelectCaseProfile.getRow(1).getCell(CaseProfileSummaryPage.CaseProfilesTable.CASE_PROFILE_NAME.getName()).controls.links.getFirst().click();
        caseProfile.update().start();
        CaseProfileDetailsTab.buttonSaveAndFinalize.click();
        Waiters.SLEEP(5000).go();

        ResponseContainer<SalesforceOpportunityModel> responseOpportunityUpdate = getResponseOpportunity(caseNumber);
        String userIdAfterUpdate = responseOpportunityUpdate.getModel().getOpportunityOwnerID();

        ResponseContainer<SalesforceUserModel> responseUserAfterUpdate = getResponseUser(userIdAfterUpdate);
        assertThat(responseUserAfterUpdate.getModel().getUserName()).isEqualTo("rgray@renaissancefamily.com.vpc");//email related to Roberta Gray
        assertThat(responseOpportunityUpdate.getModel().getSalesSupportContact()).isEqualTo(null);
    }
}
