/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.cem.campaigns;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.cem.campaigns.CampaignContext;
import com.exigen.ren.admin.modules.cem.campaigns.metadata.CampaignMetaData;
import com.exigen.ren.admin.modules.cem.campaigns.pages.CampaignPage;
import com.exigen.ren.admin.modules.cem.campaigns.tabs.CampaignTab;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.main.enums.CEMConstants;
import com.exigen.ren.modules.cem.cem.CemBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerCampaignAssociateParentOnCreate extends CemBaseTest implements CampaignContext {

    @Test(groups = {CEM, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24810", component = CRM_CUSTOMER)
    public void testCustomerCampaignAssociateParentOnCreate() {
        adminApp().open();

        LOGGER.info("STEP: Create New Campaign # ");
        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));

        String parentCampaignId = CampaignTab.labelCampaignId.getValue();

        campaign.navigate();

        LOGGER.info("Test: Associate Parent Campaign # " + parentCampaignId);
        campaign.create(tdSpecific().getTestData("TestData").adjust(TestData.makeKeyPath(CampaignTab.class.getSimpleName(),
                CampaignMetaData.ID.getLabel()), parentCampaignId).resolveLinks());

        String campaignId = CampaignTab.labelCampaignId.getValue();

        campaign.navigate();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.CAMPAIGN_ID.getLabel()), campaignId));

        CampaignPage.tableCampaigns.getRow(1).getCell(CEMConstants.CEMCampaignsTable.CAMPAIGN_ID).controls.links.getFirst().click();

        assertThat(CampaignTab.tableCampaignInfo.getRow(1))
                .hasCellWithValue(CEMConstants.CEMCampaignInfoTable.RELATIONSHIP, "Parent")
                .hasCellWithValue(CEMConstants.CEMCampaignsTable.CAMPAIGN_ID, parentCampaignId);
    }
}
