/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.admin.modules.cem.campaigns.CampaignContext;
import com.exigen.ren.admin.modules.cem.campaigns.tabs.CampaignTab;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.CustomerModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestGetCustomerWithActiveCampaign extends RestBaseTest implements CampaignContext {

    private TestData tdCampaign = campaign.defaultTestData();

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26361", component = CRM_CUSTOMER)
    public void testRestGetCustomerWithActiveCampaign() {
        adminApp().open();
        campaign.create(tdCampaign.getTestData("DataGatherFull", "TestData_Individuals"));

        String campaignId = CampaignTab.labelCampaignId.getValue();

        NavigationPage.toLeftMenuTab(NavigationEnum.CEMLeftMenu.CAMPAIGNS);
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.CAMPAIGN_ID.getLabel()), campaignId));
        campaign.startCampaign(1);
        LOGGER.info("Created Active Campaign # {} for Individual ", campaignId);

        mainApp().open();
        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created Individual {} ", EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        LOGGER.info("TEST: Check GET/customers/with-active-campaigns Response ");
        ResponseContainer<List<CustomerModel>> customerDetailsResponse = customerRestClient.getCustomerWithActiveCampaign();
        assertThat(customerDetailsResponse.getResponse()).hasStatus(200);

        customerDetailsResponse.getModel().stream()
                .flatMap(customerList -> customerList.getActiveCampaigns().stream())
                .forEach(camp -> assertThat(camp.getStatus()).isEqualTo("ACTIVE"));
    }
}