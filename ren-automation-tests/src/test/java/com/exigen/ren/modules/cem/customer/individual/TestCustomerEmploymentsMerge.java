/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.Customer;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.MergeSearchTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerEmploymentsMerge extends CustomerBaseTest implements CustomerContext {
    private TestData tdEmployment = tdCustomerIndividual.getTestData("DataGather", "TestData_WithEmployment")
            .getTestData("EmploymentTab");

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(component = CRM_CUSTOMER, testCaseId = "IPBQA-11853")
    public void testCustomerEmploymentsMerge() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData_WithEmployment"));
        String customerNumberOne = Customer.CustomerData.getCustomerNumber();
        LOGGER.info("Created Customer #1 - " + customerNumberOne);

        customerIndividual.createWithExistingAccount(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        String customerNumberTwo = Customer.CustomerData.getCustomerNumber();
        LOGGER.info("Created Customer #2 - " + customerNumberTwo);

        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.CUSTOMER.get());

        customerIndividual.mergeCustomer().perform(tdCustomerIndividual.getTestData("MergeCustomers", "TestData")
                .adjust(TestData.makeKeyPath(searchMergeCustomerActionTab.getMetaKey(),
                        MergeSearchTabMetaData.MERGE_SEARCH_DIALOG.getLabel(),
                        MergeSearchTabMetaData.SearchMergeCustomerMetaData.CUSTOMER_NUMBER.getLabel()), customerNumberOne)
                .adjust(TestData.makeKeyPath("MergeCustomerActionTab", "Add"), "Employment"));

        assertThat(CustomerSummaryPage.tableEmploymentsInformation.getRow(1))
                .hasCellWithValue("Employer Name", tdEmployment.getValue("Employer Name"))
                .hasCellWithValue("Occupation", tdEmployment.getValue("Occupation"))
                .hasCellWithValue("Occupation Status", tdEmployment.getValue("Occupation Status"))
                .hasCellWithValue("Job Title", tdEmployment.getValue("Job Title"))
                .hasCellWithValue("As of Date", tdEmployment.getValue("As of Date"));

        MainPage.QuickSearch.search(customerNumberOne);

        assertThat(CustomerSummaryPage.tableCustomerGeneralInformation.getRow(1).getCell(3))
                .valueContains("Lead Status: Archived")
                .valueContains("Merged Into: " + customerNumberTwo);
    }
}
