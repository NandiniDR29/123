/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.tabs.OpportunityActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerOpportunityBamMessages extends CustomerBaseTest {
    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24882", component = CRM_CUSTOMER)
    public void testCustomerOpportunityBamMessages() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add Opportunity for Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY);
        customerNonIndividual.addOpportunity().perform(tdCustomerNonIndividual.getTestData("Opportunity", "TestData"));

        LOGGER.info("TEST: Update Opportunity for Customer # " + customerNumber);
        customerNonIndividual.updateOpportunity().perform(tdCustomerNonIndividual.getTestData("Opportunity", "Adjustment_Likehool"));

        LOGGER.info("TEST: Close Opportunity for Customer # " + customerNumber);
        customerNonIndividual.removeOpportunity().perform(1);

        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.getFirst().click();

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getColumn("Description").getValue())
                .contains("Update Opportunity. Status change to Closed", "Close Opportunity. Reason: No interest", "Preview Opportunity Details",
                        "Update Opportunity. No Status change", "Preview Opportunity Details", "Create Opportunity with Status: Draft");
    }
}
