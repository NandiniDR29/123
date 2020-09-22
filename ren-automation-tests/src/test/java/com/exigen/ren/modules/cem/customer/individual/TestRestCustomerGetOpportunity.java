/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.metadata.OpportunityActionTabMetaData;
import com.exigen.ren.main.modules.customer.metadata.SearchOwnerMetaData;
import com.exigen.ren.main.modules.customer.metadata.SearchReferralMetaData;
import com.exigen.ren.main.modules.customer.tabs.OpportunityActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.opportunity.model.OpportunitiesModel;
import com.exigen.ren.rest.customer.opportunity.model.OpportunityModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerGetOpportunity extends RestBaseTest {
    private TestData opportunityTab = tdCustomerIndividual.getTestData("Opportunity").getTestData("Opportunity_Rest", OpportunityActionTab.class.getSimpleName());
    private String customerNumber;

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26160", component = CRM_CUSTOMER)
    public void testRestCustomerGetOpportunity() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.OPPORTUNITY.get());
        LOGGER.info("TEST: Add Opportunity for Customer # " + customerNumber);
        customerIndividual.addOpportunity().perform(tdCustomerIndividual.getTestData("Opportunity", "Opportunity_Rest"));

        String opportunityId = OpportunityActionTab.tableOpportunity.getRow(1).getCell("ID").getValue();

        LOGGER.info("TEST: Check Opportunity Response Body  for Customer # " + customerNumber);
        ResponseContainer<List<OpportunitiesModel>> opportunitiesResponse = customerRestClient.getOpportunity(customerNumber);
        checkResponse(new OpportunityModel(opportunitiesResponse.getModel().get(0)), opportunitiesResponse.getResponse().getStatus(), false);

        ResponseContainer<OpportunityModel> opportunityResponse = customerRestClient.getOpportunityByID(customerNumber, opportunityId);
        checkResponse(opportunityResponse.getModel(), opportunityResponse.getResponse().getStatus(), true);

    }


    private void checkResponse(OpportunityModel opportunityModel, int responseCode, Boolean byId) {
        assertSoftly(softly -> {
            softly.assertThat(responseCode).isEqualTo(200);
            softly.assertThat(opportunityModel.getDescription()).isEqualTo(opportunityTab.getValue(OpportunityActionTabMetaData.DESCRIPTION.getLabel()));
            softly.assertThat(opportunityModel.getOpportunityChannelCd()).isEqualTo(opportunityTab.getValue(OpportunityActionTabMetaData.CHANNEL.getLabel()).toUpperCase());
            softly.assertThat(opportunityModel.getOpportunityLikelihoodCd()).isEqualTo(opportunityTab.getValue(OpportunityActionTabMetaData.LIKELIHOOD.getLabel()).toUpperCase());
            softly.assertThat(opportunityModel.getPotential().getAmount()).isEqualTo(Double.valueOf(opportunityTab.getValue(OpportunityActionTabMetaData.POTENTIAL.getLabel())));
            softly.assertThat(opportunityModel.getPotential().getCurrencyCd()).isEqualTo("USD");
            softly.assertThat(opportunityModel.getStatus()).isEqualTo("DRAFT");
            softly.assertThat(opportunityModel.getCustomerNumber()).isEqualTo(customerNumber);
            if (byId) {
                softly.assertThat(opportunityModel.getReferral().getDisplayValue())
                        .contains(opportunityTab.getTestData(OpportunityActionTabMetaData.REFERRED_BY.getLabel()).getValue(SearchReferralMetaData.FIRST_NAME.getLabel()));
                softly.assertThat(opportunityModel.getOwner().getQueueName())
                        .contains(opportunityTab.getTestData(OpportunityActionTabMetaData.OPPORTUNITY_OWNER.getLabel()).getValue(SearchOwnerMetaData.WORK_QUEUE.getLabel()));
            }
        });
    }
}


