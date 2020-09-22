/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.cem.campaigns;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.cem.campaigns.pages.CampaignPage;
import com.exigen.ren.admin.modules.cem.campaigns.tabs.CampaignTab;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.modules.cem.cem.CemBaseTest;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.CustomerRestService;
import com.exigen.ren.rest.customer.campaigns.model.CampaignModel;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestPostCampaigns extends CemBaseTest {
    private CustomerRestService customerRestClient = RESTServiceType.CUSTOMERS.get();

    @Test(groups = {CEM, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26285", component = CRM_CUSTOMER)
    public void testRestPostCampaigns() {

        LOGGER.info("TEST: Create New Campaign # By REST");
        ResponseContainer<CampaignModel> campaignResponse = customerRestClient.postCampaigns(tdCampaign.getTestData("CampaignRestData","TestData_RestAddCampaign"));
        assertThat(campaignResponse.getResponse().getStatus()).isEqualTo(200);
        String campaignName=campaignResponse.getModel().getName();

        adminApp().open();
        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData")
                .adjust(TestData.makeKeyPath(CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.CAMPAIGN_NAME.getLabel()), campaignName));

        LOGGER.info("TEST: Check And Update Campaign");
        assertThat(CampaignPage.tableCampaigns.getRow(1).getCell("Status")).hasValue("Draft");
        campaign.updateCampaign().perform(tdCampaign.getTestData("DataGather", "UpdateCustomerType"), 1);
        assertThat(CampaignTab.labelMessages).isPresent();

        CampaignTab.linkCampaigns.click();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.CAMPAIGN_NAME.getLabel()), campaignName));
        assertThat(CampaignPage.tableCampaigns.getRow(1).getCell("Status")).hasValue("Active");
    }

}
