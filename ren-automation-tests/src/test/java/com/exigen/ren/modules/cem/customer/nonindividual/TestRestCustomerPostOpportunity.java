/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.tabs.OpportunityActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.opportunity.model.OpportunityModel;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.customer.metadata.OpportunityActionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerPostOpportunity extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26162", component = CRM_CUSTOMER)
    public void testRestCustomerPostOpportunity() {
        mainApp().open();

        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Add Opportunity for Customer # " + customerNumber);
        ResponseContainer<OpportunityModel> opportunityResponse = customerRestClient.postOpportunity(customerNumber, DataProviderFactory.emptyData()
                .adjust("customerNumber", customerNumber));
        assertThat(opportunityResponse.getResponse().getStatus()).isEqualTo(200);

        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.OPPORTUNITY.get());
        OpportunityActionTab.tableOpportunity.getRow(1).getCell("ID").controls.links.getFirst().click();

        LOGGER.info("TEST: Check Opportunity for Customer # " + customerNumber);
        checkOpportunity(opportunityResponse.getModel());
    }

    private void checkOpportunity(OpportunityModel opportunityModel) {
        AssetList assetList = (AssetList) new OpportunityActionTab().getAssetList();

        AssetList generalInformation = new AssetList(By.id("generalInfoPanel:content"));
        assetList.getAssetNames().stream().forEach(a -> generalInformation.registerAsset(a, StaticElement.class, Waiters.NONE));

        assertSoftly(softly -> {
            softly.assertThat(generalInformation.getAsset(DESCRIPTION.getLabel(), StaticElement.class)).hasValue(opportunityModel.getDescription());
            softly.assertThat(generalInformation.getAsset(CHANNEL.getLabel(), StaticElement.class).getValue().toUpperCase())
                    .isEqualTo(opportunityModel.getOpportunityChannelCd());
            softly.assertThat(generalInformation.getAsset(LIKELIHOOD.getLabel(), StaticElement.class).getValue().toUpperCase())
                    .isEqualTo(opportunityModel.getOpportunityLikelihoodCd());
            softly.assertThat(generalInformation.getAsset(POTENTIAL.getLabel(), StaticElement.class))
                    .hasValue(new Currency(opportunityModel.getPotential().getAmount()).toString());
            softly.assertThat(generalInformation.getAsset(STATUS).getValue().toUpperCase()).isEqualTo(opportunityModel.getStatus());
            softly.assertThat(generalInformation.getAsset(REFERRED_BY.getLabel(), StaticElement.class))
                    .hasValue(opportunityModel.getReferral().getDisplayValue());
            softly.assertThat(generalInformation.getAsset(OPPORTUNITY_OWNER.getLabel(), StaticElement.class))
                    .hasValue(opportunityModel.getOwner().getDisplayValue());
        });
    }
}


