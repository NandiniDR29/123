/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.cem.campaigns;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.cem.campaigns.pages.CampaignPage;
import com.exigen.ren.admin.modules.cem.campaigns.tabs.CampaignTab;
import com.exigen.ren.admin.modules.cem.common.pages.CemPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.CEMConstants;
import com.exigen.ren.modules.cem.cem.CemBaseTest;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.customer.CustomerRestService;
import com.exigen.ren.rest.customer.campaigns.model.CampaignModel;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.admin.modules.cem.campaigns.metadata.CampaignMetaData.*;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestPutCampaignsCampaignID extends CemBaseTest {
    private CustomerRestService customerRestClient = RESTServiceType.CUSTOMERS.get();
    private TestData tdUpdateCampaign = tdCampaign.getTestData("CampaignRestData", "TestData_RestUpdateCampaign");

    @Test(groups = {CEM, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26289", component = CRM_CUSTOMER)
    public void testRestPutCampaignsCampaignID() {
        adminApp().open();

        campaign.create(tdCampaign.getTestData("DataGather", "TestData_Individuals"));
        String campaignID = CampaignTab.labelCampaignId.getValue();

        LOGGER.info("TEST: Update Campaign # via REST");
        Response campaignResponse = customerRestClient.putCampaignByID(tdUpdateCampaign, campaignID);
        assertThat(campaignResponse.getStatus()).isEqualTo(204);

        CampaignTab.linkCampaigns.click();
        campaign.search(tdCampaign.getTestData("SearchData", "TestData").adjust(TestData.makeKeyPath(
                CemPage.SearchCem.class.getSimpleName(), CemPage.SearchCem.CAMPAIGN_ID.getLabel()), campaignID));
        CampaignPage.tableCampaigns.getRow(CEMConstants.CEMCampaignsTable.CAMPAIGN_ID, campaignID).getCell(2).controls.links.getFirst().click();

        LOGGER.info("TEST: Check Campaign Information");
        AssetList assetList = (AssetList) new CampaignTab().getAssetList();
        AssetList campaignAssetList = new AssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER));
        assetList.getAssetNames().forEach(a -> campaignAssetList.registerAsset(a, StaticElement.class, Waiters.NONE));
        CampaignModel campaignModelInfo = RestUtil.convert(tdUpdateCampaign, CampaignModel.class);

        assertSoftly(softly -> {
            softly.assertThat(campaignAssetList.getAssets(CAMPAIGN_NAME.getLabel(), PROMOTION_CODE.getLabel(), BUDGET_COST.getLabel(),
                    ACTUAL_COST.getLabel(), EXPECTED_REVENUE.getLabel(), START_DATE.getLabel(), END_DATE.getLabel()))
                    .extracting("value").isEqualTo(
                    Arrays.asList(campaignModelInfo.getName(), campaignModelInfo.getPromotionCd(),
                            new Currency(campaignModelInfo.getBudgetCost()).toString(),
                            new Currency(campaignModelInfo.getActualCost()).toString(),
                            new Currency(campaignModelInfo.getExpectedRevenue()).toString(),
                            campaignModelInfo.getStartDate().format(MM_DD_YYYY),
                            campaignModelInfo.getEndDate().format(MM_DD_YYYY)
                    ));
            softly.assertThat(CampaignTab.tableChannel.getRow(1))
                    .hasCellWithValue(1, campaignModelInfo.getChannels().get(0).getCampaignChannelCd().toLowerCase())
                    .hasCellWithValue(2, campaignModelInfo.getChannels().get(0).getCampaignSubChannelCd().toLowerCase());
            softly.assertThat(CampaignTab.tableProduct.getRow(1).getCell(1)).valueContains(campaignModelInfo.getProducts().get(0).getLobCd());
            softly.assertThat(CampaignTab.tableProduct.getRow(1).getCell(4)).valueContains(campaignModelInfo.getProducts().get(0).getRiskCd());
        });
    }
}
