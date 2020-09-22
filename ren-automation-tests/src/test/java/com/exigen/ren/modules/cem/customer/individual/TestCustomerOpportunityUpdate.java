/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.metadata.OpportunityActionTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.OpportunityActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerOpportunityUpdate extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24883", component = CRM_CUSTOMER)
    public void testCustomerOpportunityUpdate() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY);
        LOGGER.info("TEST: Add Opportunity for Customer # " + customerNumber);
        customerIndividual.addOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "TestData"));

        assertThat(OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.LIKELIHOOD))
                .hasValue(tdCustomerIndividual.getTestData("Opportunity").getTestData("TestData")
                        .getTestData(OpportunityActionTab.class.getSimpleName()).getValue(OpportunityActionTabMetaData.LIKELIHOOD.getLabel()));

        LOGGER.info("TEST: Update Opportunity for Customer # " + customerNumber);
        customerIndividual.updateOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "Adjustment_Likehool"));

        assertThat(OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.LIKELIHOOD))
                .hasValue(tdCustomerIndividual.getTestData("Opportunity").getTestData("Adjustment_Likehool")
                        .getTestData(OpportunityActionTab.class.getSimpleName()).getValue(OpportunityActionTabMetaData.LIKELIHOOD.getLabel()));

    }
}
