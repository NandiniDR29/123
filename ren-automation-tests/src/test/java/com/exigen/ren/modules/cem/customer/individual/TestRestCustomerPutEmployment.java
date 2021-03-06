/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.customer.model.CustomerModel;
import com.exigen.ren.rest.customer.model.EmploymentModel;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CustomerConstants.EmploymentsTable.*;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.tableEmploymentsInformation;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerPutEmployment extends RestBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26419", component = CRM_CUSTOMER)
    public void testRestCustomerPutEmployment() {

        mainApp().open();

        LOGGER.info("STEP: Create Customer With Employment");
        CustomerModel createdCustomer = customerRestClient.postCustomers(tdCustomerIndividual.getTestData("DataGather", "TestData_WithEmployment"));

        LOGGER.info("TEST: PUT /customers/{customerNumber}/employments/{employmentId}");
        EmploymentModel employmentModel = RestUtil.convert(tdCustomerIndividual.getTestData("Employment", "REST_UpdateEmployment"), EmploymentModel.class);
        assertThat(customerRestClient.putEmployment(createdCustomer.getCustomerNumber(), String.valueOf(createdCustomer.getCustomerEmployments().get(0).getId()), employmentModel).getResponse()).hasStatus(200);

        LOGGER.info("TEST: Check Employment Updated");
        MainPage.QuickSearch.search(createdCustomer.getCustomerNumber());
        assertSoftly(softly -> {
            softly.assertThat(tableEmploymentsInformation.getRow(1))
                    .hasCellWithValue(EMPLOYER_NAME.getName(), employmentModel.getEmployerName())
                    .hasCellWithValue(OCCUPATION.getName(), employmentModel.getOccupationCd())
                    .hasCellWithValue(OCCUPATION_DESCRIPTION.getName(), employmentModel.getOccupationDescription())
                    .hasCellWithValue(JOB_TITLE_DESCRIPTION.getName(), employmentModel.getJobTitleDescription())
                    .hasCellWithValue(AS_OF_DATE.getName(), employmentModel.getAsOfDate().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(tableEmploymentsInformation.getRow(1).getCell(OCCUPATION_STATUS.getName()).getValue().toUpperCase())
                    .isEqualTo(employmentModel.getOccupationStatusCd());
            softly.assertThat(tableEmploymentsInformation.getRow(1).getCell(JOB_TITLE.getName()).getValue().toUpperCase())
                    .isEqualTo(employmentModel.getJobTitleCd());
        });
    }
}
