/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.tabs.OpportunityActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class TestCustomerOpportunityBamMessages extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24882", component = CRM_CUSTOMER)
    public void testCustomerOpportunityBamMessages() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add Opportunity for Customer # " + customerNumber);
        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY.get());
        customerIndividual.addOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "TestData"));

        LOGGER.info("TEST: Update Opportunity for Customer # " + customerNumber);
        customerIndividual.updateOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "Adjustment_Likehool"));

        LOGGER.info("TEST: Close Opportunity for Customer # " + customerNumber);
        customerIndividual.removeOpportunity().perform(1);

        OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.ID).controls.links.getFirst().click();

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getColumn("Description").getValue())
                .contains("Update Opportunity. Status change to Closed", "Close Opportunity. Reason: No interest", "Preview Opportunity Details",
                        "Update Opportunity. No Status change", "Preview Opportunity Details", "Create Opportunity with Status: Draft");
    }
}
