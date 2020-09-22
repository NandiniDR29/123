/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.cem.campaigns;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.cem.campaigns.pages.CampaignPage;
import com.exigen.ren.admin.modules.cem.campaigns.tabs.CampaignTab;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.modules.cem.cem.CemBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerCampaignCreate extends CemBaseTest {

    @Test(groups = {CEM, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24814", component = CRM_CUSTOMER)
    public void testCustomerCampaignCreate() {
        adminApp().open();

        LOGGER.info("TEST: Create New Campaign # ");
        campaign.create(tdCampaign.getTestData("DataGatherFull", "TestData_Individuals"));

        String campaignId = CampaignTab.labelCampaignId.getValue();

        campaign.navigate();

        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.CAMPAIGN_ID.getLabel()), campaignId));

        assertThat(CampaignPage.tableCampaigns).hasRows(1);
    }
}
