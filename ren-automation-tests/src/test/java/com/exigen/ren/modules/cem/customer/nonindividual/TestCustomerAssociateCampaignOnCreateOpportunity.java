/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.EntityLogger.EntityType;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.cem.campaigns.CampaignContext;
import com.exigen.ren.admin.modules.cem.campaigns.tabs.CampaignTab;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.enums.NavigationEnum.CustomerSummaryTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.tabs.OpportunityActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerAssociateCampaignOnCreateOpportunity extends CustomerBaseTest implements CampaignContext {

    private TestData tdCampaign = campaign.defaultTestData();

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24829", component = CRM_CUSTOMER)
    public void testCustomerAssociateCampaignOnCreateOpportunity() {
        adminApp().open();

        campaign.create(tdCampaign.getTestData("DataGather", "TestData_NonIndividuals"));

        String campaignId = CampaignTab.labelCampaignId.getValue();

        NavigationPage.toLeftMenuTab(NavigationEnum.CEMLeftMenu.CAMPAIGNS);
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.CAMPAIGN_ID.getLabel()), campaignId));
        campaign.startCampaign(1);

        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add Associate Campaign Opportunity tab  " + customerNumber);

        NavigationPage.toMainTab(CustomerSummaryTab.OPPORTUNITY);
        LOGGER.info("STEP: Add Associate Campaign to Opportunity tab ");
        customerNonIndividual.addAssociateCampaignOnOpportunity().perform(tdCustomerNonIndividual.getTestData("Opportunity", "TestData"), campaignId);

        assertThat(OpportunityActionTab.tableOpportunity).hasRows(1);
        assertThat(OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.STATUS)).hasValue("In Pipeline");

        LOGGER.info("STEP: Remove Associated Campaign to Opportunity tab " + campaignId);
        customerNonIndividual.removeAssociateCampaignOnOpportunity().perform(1);
        assertThat(OpportunityActionTab.tableOpportunity.getRow(1).getCell(CustomerConstants.CustomerOpportunityTable.STATUS)).hasValue("Draft");
    }
}
