/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.cem.campaigns;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.cem.campaigns.tabs.CampaignTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.cem.CemBaseTest;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.CustomerRestService;
import com.exigen.ren.rest.customer.campaigns.model.CampaignModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.admin.modules.cem.campaigns.metadata.CampaignMetaData.*;
import static com.exigen.ren.admin.modules.cem.campaigns.metadata.CampaignMetaData.CampaignProductsSection.LINE_OF_BUSINESS;
import static com.exigen.ren.admin.modules.cem.campaigns.metadata.CampaignMetaData.CampaignProductsSection.PRODUCT_NAME;
import static com.exigen.ren.admin.modules.cem.campaigns.metadata.CampaignMetaData.MarketingChannelsSection.CAMPAIGN_CHANEL;
import static com.exigen.ren.admin.modules.cem.campaigns.metadata.CreateChildCampaignMetaData.MarketingChannelsSection.SUB_CHANEL;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestGetCampaigns extends CemBaseTest implements CustomerContext {
    private String campaignsName, promotionCode;
    private TestData queryData;
    private CustomerRestService customerRestClient = RESTServiceType.CUSTOMERS.get();
    private TestData td = tdCampaign.getTestData("CampaignRestData");
    private TestData campaignInd = tdCampaign.getTestData("DataGatherFull").getTestData("TestData_Individuals", CampaignTab.class.getSimpleName());
    private TestData campaignNonInd = tdCampaign.getTestData("DataGatherFull", "TestData_NonIndividuals").getTestData(CampaignTab.class.getSimpleName());

    @Test(groups = {CEM, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26189", component = CRM_CUSTOMER)
    public void testRestGetCampaigns() {
        LOGGER.info("TEST: Create New Campaign # For Individual");
        adminApp().open();
        campaign.create(tdCampaign.getTestData("DataGatherFull", "TestData_Individuals"));
        campaignsName = CampaignTab.labelCampaignName.getValue();
        promotionCode = CampaignTab.labelPromotionCode.getValue();

        mainApp().open();
        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.MARITAL_STATUS.getLabel()), "Married"));

        LOGGER.info("TEST: Check GET /campaigns");
        queryData = td.getTestData("CampaignsQueryRequest")
                .adjust(TestData.makeKeyPath("query", "name"), campaignsName)
                .adjust(TestData.makeKeyPath("query", "customerNumber"), CustomerSummaryPage.labelCustomerNumber.getValue());
        checkGetCampaigns(campaignInd, queryData);

        LOGGER.info("TEST: Create New Campaign # For Non-Individual");
        adminApp().open();
        campaign.create(tdCampaign.getTestData("DataGatherFull", "TestData_NonIndividuals"));
        campaignsName = CampaignTab.labelCampaignName.getValue();
        promotionCode = CampaignTab.labelPromotionCode.getValue();
        String campaignId = CampaignTab.labelCampaignId.getValue();

        mainApp().open();
        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));

        LOGGER.info("TEST: Check GET /campaigns");
        queryData = td.getTestData("CampaignsQueryRequest")
                .adjust(TestData.makeKeyPath("query", "campaignID"), campaignId)
                .adjust(TestData.makeKeyPath("query", "customerNumber"), CustomerSummaryPage.labelCustomerNumber.getValue());
        checkGetCampaigns(campaignNonInd, queryData);
    }

    private void checkGetCampaigns(TestData td, TestData queryData) {
        ResponseContainer<List<CampaignModel>> campaignResponse = RetryService.run(predicate -> (predicate.getResponse().getStatus() == 200 && !predicate.getModel().isEmpty()),
                () -> customerRestClient.getCampaigns(queryData),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        CampaignModel campaignModel = campaignResponse.getModel().get(0);
        assertSoftly(softly -> {
            softly.assertThat(campaignModel.getName()).isEqualTo(campaignsName);
            softly.assertThat(campaignModel.getPromotionCd()).isEqualTo(promotionCode);
            softly.assertThat(campaignModel.getBudgetCost()).isEqualTo(Double.valueOf(td.getValue(BUDGET_COST.getLabel())));
            softly.assertThat(campaignModel.getActualCost()).isEqualTo(Double.valueOf(td.getValue(ACTUAL_COST.getLabel())));
            softly.assertThat(campaignModel.getExpectedRevenue()).isEqualTo(Double.valueOf(td.getValue(EXPECTED_REVENUE.getLabel())));
            softly.assertThat(campaignModel.getDescription()).isEqualTo(td.getValue(DESCRIPTION.getLabel()));
            softly.assertThat(campaignModel.getStartDate().format(MM_DD_YYYY)).isEqualTo(td.getValue(START_DATE.getLabel()));
            softly.assertThat(campaignModel.getEndDate().format(MM_DD_YYYY)).isEqualTo(td.getValue(END_DATE.getLabel()));

            softly.assertThat(campaignModel.getProducts().get(0).getLobCd())
                    .isEqualTo(td.getTestData(CAMPAIGN_PRODUCTS.getLabel()).getValue(LINE_OF_BUSINESS.getLabel()).toLowerCase());
            softly.assertThat(campaignModel.getProducts().get(0).getProductCd())
                    .isEqualTo(td.getTestData(CAMPAIGN_PRODUCTS.getLabel()).getValue(PRODUCT_NAME.getLabel()).toLowerCase());

            softly.assertThat(campaignModel.getChannels().get(0).getCampaignChannelCd())
                    .isEqualTo(td.getTestData(MARKETING_CHANELS.getLabel()).getValue(CAMPAIGN_CHANEL.getLabel()).toLowerCase());
            softly.assertThat(campaignModel.getChannels().get(0).getCampaignSubChannelCd())
                    .isEqualTo(td.getTestData(MARKETING_CHANELS.getLabel()).getValue(SUB_CHANEL.getLabel()).toLowerCase());
        });
    }
}
