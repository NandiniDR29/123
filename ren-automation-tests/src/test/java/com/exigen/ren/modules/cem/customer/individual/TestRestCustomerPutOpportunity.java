/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.metadata.OpportunityActionTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.OpportunityActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.opportunity.model.OpportunityModel;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerPutOpportunity extends RestBaseTest {
    private TestData oppTestData = tdCustomerIndividual.getTestData("Opportunity", "Update_Opportunity_REST");

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26161", component = CRM_CUSTOMER)
    public void testRestCustomerPutOpportunity() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.OPPORTUNITY.get());
        LOGGER.info("TEST: Add Opportunity for Customer # " + customerNumber);
        customerIndividual.addOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "Opportunity_Rest"));

        String opportunityId = OpportunityActionTab.tableOpportunity.getRow(1).getCell("ID").getValue();

        LOGGER.info("TEST: Update Opportunity for Customer # " + customerNumber);
        ResponseContainer<OpportunityModel> opportunityResponse =
                customerRestClient.putOpportunity(oppTestData, customerNumber, opportunityId);
        assertThat(opportunityResponse.getResponse().getStatus()).isEqualTo(200);

        OpportunityActionTab.tableOpportunity.getRow("ID", opportunityId).getCell("ID").controls.links.getFirst().click();

        LOGGER.info("TEST: Check Opportunity Update ");
        OpportunityModel opportunityModel = RestUtil.convert(oppTestData, OpportunityModel.class);
        AssetList assetList = (AssetList) new OpportunityActionTab().getAssetList();
        AssetList generalInformation = new AssetList(By.id("generalInfoPanel:content"));
        assetList.getAssetNames().stream().forEach(a -> generalInformation.registerAsset(a, StaticElement.class, Waiters.NONE));

        assertSoftly(softly -> {
            softly.assertThat(generalInformation.getAsset(OpportunityActionTabMetaData.DESCRIPTION.getLabel(), StaticElement.class)).hasValue(opportunityModel.getDescription());
            softly.assertThat(generalInformation.getAsset(OpportunityActionTabMetaData.CHANNEL.getLabel(), StaticElement.class).getValue().toUpperCase())
                    .isEqualTo(opportunityModel.getOpportunityChannelCd());
            softly.assertThat(generalInformation.getAsset(OpportunityActionTabMetaData.LIKELIHOOD.getLabel(), StaticElement.class).getValue().toUpperCase())
                    .isEqualTo(opportunityModel.getOpportunityLikelihoodCd());
            softly.assertThat(generalInformation.getAsset(OpportunityActionTabMetaData.POTENTIAL.getLabel(), StaticElement.class))
                    .hasValue(new Currency(opportunityModel.getPotential().getAmount()).toString());
        });
    }
}


