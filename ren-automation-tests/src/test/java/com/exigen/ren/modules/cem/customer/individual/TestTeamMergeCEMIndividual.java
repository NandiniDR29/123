/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestTeamMergeCEMIndividual extends BaseTest implements CustomerContext {

    @Test(groups = {CEM, CUSTOMER_IND, TEAM_MERGE, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23715", component = CRM_CUSTOMER)
    public void testTeamMergeCEMIndividual() {

        TestData tdUpdated = customerIndividual.getDefaultTestData("DataGather", "TestData_Update")
                .getTestData(GeneralTab.class.getSimpleName());

        String firstNameUpdated = tdUpdated.getValue(GeneralTabMetaData.FIRST_NAME.getLabel());
        String lastNameUpdated = tdUpdated.getValue(GeneralTabMetaData.LAST_NAME.getLabel());

        mainApp().open();

        LOGGER.info("TEST: Customer Create");
        customerIndividual.createViaUI(customerIndividual.getDefaultTestData("DataGather", "TestData_TeamMerge"));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Customer Update");
        customerIndividual.update().perform(customerIndividual.getDefaultTestData("DataGather", "TestData_Update"));
        assertThat(CustomerSummaryPage.labelCustomerName).hasValue(firstNameUpdated + " " + lastNameUpdated);

        MainPage.QuickSearch.search(customerNumber);
        assertThat(CustomerSummaryPage.labelCustomerNumber).hasValue(customerNumber);

        LOGGER.info("TEST: Customer Timeline");
        customerIndividual.inquiry().start();
        GeneralTab.buttonTimeline.click();
        assertThat(GeneralTab.tableTimeLine).hasRows(2);
        assertThat(GeneralTab.tableVersionCompare.getRow(1, GeneralTabMetaData.FIRST_NAME.getLabel()).getCell(2)).hasValue(firstNameUpdated);
        assertThat(GeneralTab.tableVersionCompare.getRow(1, GeneralTabMetaData.LAST_NAME.getLabel()).getCell(2)).hasValue(lastNameUpdated);

        LOGGER.info("TEST: Customer Delete");
        MainPage.QuickSearch.search(customerNumber);
        customerIndividual.deleteCustomer().perform();
        MainPage.QuickSearch.search(customerNumber);
        assertThat(Page.dialogConfirmation.labelMessage).hasValue("Search item not found");
    }

    @Test(groups = {CEM, CUSTOMER_IND, TEAM_MERGE, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23715", component = CRM_CUSTOMER)
    public void testTeamMergeCEMIndividualRest() {
        mainApp().open();

        LOGGER.info("TEST: Create Individual Customer via REST");
        customerIndividual.createViaREST(customerIndividual.getDefaultTestData("DataGather", "TestData_TeamMerge"));
    }
}
