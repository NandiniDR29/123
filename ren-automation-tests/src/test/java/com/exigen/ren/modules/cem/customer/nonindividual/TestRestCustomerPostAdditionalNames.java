/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.AdditionalNameModel;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerPostAdditionalNames extends RestBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26407", component = CRM_CUSTOMER)
    public void testRestCustomerPostAdditionalNames() {

        mainApp().open();

        customerNonIndividual.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("TEST: Check POST /customers/{customerNumber}/business-additional-names");
        AdditionalNameModel additionalNameModel = RestUtil.convert(tdCustomerNonIndividual.getTestData("AdditionalName", "REST_Add_AdditionalName"), AdditionalNameModel.class);
        ResponseContainer<AdditionalNameModel> response = customerRestClient.postCustomersBusinessAdditionalNames(customerNumber, additionalNameModel);
        customerNonIndividual.inquiry().start();
        assertThat(response.getResponse()).hasStatus(200);
        assertThat(GeneralTab.tableAdditionalNames.getRow(1)).hasCellWithValue("Name-DBA", additionalNameModel.getNameDba());
    }
}
