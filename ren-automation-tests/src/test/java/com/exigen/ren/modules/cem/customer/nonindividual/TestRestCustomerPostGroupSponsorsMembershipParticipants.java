/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.groupSponsors.model.GroupSponsorModel;
import com.exigen.ren.rest.customer.groupSponsors.model.ParticipationDetailsModel;
import com.exigen.ren.rest.customer.model.CustomerModel;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerPostGroupSponsorsMembershipParticipants extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26418", component = CRM_CUSTOMER)
    public void testRestCustomerPostGroupSponsorsMembershipParticipants() {
        mainApp().open();

        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        String customerNumberNonInd = CustomerSummaryPage.labelCustomerNumber.getValue();

        CustomerModel createdCustomer = customerRestClient.postCustomers(tdCustomerIndividual.getTestData("DataGather", "TestData"));

        LOGGER.info("STEP: Set Request Body  With Ind Customer Details and Membership Participation Details");
        ParticipationDetailsModel participationDetailsModel = RestUtil.convert(tdCustomerNonIndividual.getTestData("AddParticipant", "REST_Membership"), ParticipationDetailsModel.class);
        GroupSponsorModel groupSponsorModel = new GroupSponsorModel(createdCustomer, participationDetailsModel);

        LOGGER.info("TEST: POST/group-sponsors/<sponsorNumber>/participants");
        ResponseContainer<GroupSponsorModel> actualResponse = customerRestClient.postParticipants(customerNumberNonInd, groupSponsorModel);
        assertThat(actualResponse.getResponse()).hasStatus(200);
        assertThat(actualResponse.getModel().getParticipationDetails()).isEqualTo(participationDetailsModel);

        MainPage.QuickSearch.search(createdCustomer.getCustomerNumber());
        customerNonIndividual.inquiry().start();
        GeneralTab.linkMembershipRelationship.click();
        assertThat(CustomerSummaryPage.labelCustomerNumber).hasValue(customerNumberNonInd);
        assertThat(NavigationPage.isSubTabSelected(NavigationEnum.CustomerSummaryTab.PORTFOLIO)).isTrue();
    }
}
