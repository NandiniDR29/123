/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.customer.groupSponsors.model.GroupSponsorModel;
import com.exigen.ren.rest.customer.groupSponsors.model.ParticipationDetailsModel;
import com.exigen.ren.rest.customer.model.CustomerModel;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerDeleteGroupSponsorsEmploymentParticipant extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26426", component = CRM_CUSTOMER)
    public void testRestCustomerDeleteGroupSponsorsEmploymentParticipant() {
        mainApp().open();

        CustomerModel nonIndCustomer = customerRestClient.postCustomers(tdCustomerNonIndividual.getTestData("DataGather", TEST_DATA_KEY));
        CustomerModel indCustomer = customerRestClient.postCustomers(tdCustomerIndividual.getTestData("DataGather", TEST_DATA_KEY));

        LOGGER.info("STEP: Set Non-Individual With Student Participant (Individual Lead) Associated");
        assertThat(customerRestClient.postParticipants(nonIndCustomer.getCustomerNumber(), new GroupSponsorModel(indCustomer,
                RestUtil.convert(tdCustomerNonIndividual.getTestData("AddParticipant", "REST_Employment"), ParticipationDetailsModel.class))).getResponse()).hasStatus(200);

        LOGGER.info("TEST: DELETE/group-sponsors/{sponsorNumber}/employment-participants/{participantNumber}");
        assertThat(customerRestClient.deleteEmploymentParticipant(nonIndCustomer.getCustomerNumber(), indCustomer.getCustomerNumber())).hasStatus(204);

        MainPage.QuickSearch.search(indCustomer.getCustomerNumber());
        assertThat(CustomerSummaryPage.labelParticipant).hasValue(VALUE_NO);

        MainPage.QuickSearch.search(nonIndCustomer.getCustomerNumber());
        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CENSUS.get());
        assertThat(CustomerSummaryPage.tableEmploymentCensus).isAbsent();
    }
}
