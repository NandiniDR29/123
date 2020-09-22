/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.customer.metadata.EmployeeInfoTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.EmployeeInfoTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.groupSponsors.model.GroupSponsorModel;
import com.exigen.ren.rest.customer.groupSponsors.model.ParticipationDetailsModel;
import com.exigen.ren.rest.customer.model.CustomerModel;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerPutGroupSponsorsEmploymentParticipant extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26414", component = CRM_CUSTOMER)
    public void testRestCustomerPutGroupSponsorsEmploymentParticipant() {
        TestData tdAddParticipant = tdCustomerNonIndividual.getTestData("AddParticipant", "REST_Employment");
        TestData tdUpdateParticipant = tdCustomerNonIndividual.getTestData("AddParticipant", "REST_UpdateEmployment");

        mainApp().open();

        LOGGER.info("STEP: Create Ind And NonInd Customer");
        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", TEST_DATA_KEY));
        String customerNumberNonInd = CustomerSummaryPage.labelCustomerNumber.getValue();
        CustomerModel indCustomer = customerRestClient.postCustomers(tdCustomerIndividual.getTestData("DataGather", TEST_DATA_KEY));

        LOGGER.info("STEP: Set Non-Individual With Student Participant (Individual Lead) Associated");
        assertThat(customerRestClient.postParticipants(customerNumberNonInd, new GroupSponsorModel(indCustomer,
                RestUtil.convert(tdAddParticipant, ParticipationDetailsModel.class))).getResponse()).hasStatus(200);

        LOGGER.info("TEST: Check PUT/group-sponsors/<sponsorNumber>/participants/<participantNumber>");
        GroupSponsorModel groupSponsorModel = RestUtil.convert(tdUpdateParticipant, GroupSponsorModel.class);
        ResponseContainer<GroupSponsorModel> actualResponse = customerRestClient.putParticipants(customerNumberNonInd, indCustomer.getCustomerNumber(), groupSponsorModel);
        assertThat(actualResponse.getResponse()).hasStatus(200);

        MainPage.QuickSearch.search(indCustomer.getCustomerNumber());
        customerNonIndividual.update().start();
        NavigationPage.toSubTab(NavigationEnum.CustomerTab.EMPLOYEE_INFO.get());
        AbstractContainer<?, ?> assertList = new EmployeeInfoTab().getAssetList();
        assertSoftly(softly -> {
            ParticipationDetailsModel participant = groupSponsorModel.getParticipationDetails();
            softly.assertThat(assertList.getAsset(EmployeeInfoTabMetaData.DEPARTMENT_ID)).hasValue(participant.getDepartmentId());
            softly.assertThat(assertList.getAsset(EmployeeInfoTabMetaData.DIVISION_ID)).hasValue(participant.getDivisionId());
            softly.assertThat(assertList.getAsset(EmployeeInfoTabMetaData.LOCATION_ID)).hasValue(participant.getLocationId());
        });
    }
}
