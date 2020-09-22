/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.EmploymentModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerGetEmployments extends RestBaseTest {

    @Test(groups = {CUSTOMER_IND, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-26404", component = CRM_CUSTOMER)
    public void testRestCustomerGetEmployments() {
        mainApp().open();

        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData_WithEmployment")
                .adjust(tdSpecific().getTestData("EmploymentInfo")));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        LOGGER.info("Created Customer #1 - " + customerNumber);

        ResponseContainer<List<EmploymentModel>> response = customerRestClient.getEmployments(customerNumber);
        EmploymentModel employmentModel = response.getModel().get(0);
        TestData td = tdSpecific().getTestData("EmploymentResponse");
        assertSoftly(softly -> {
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(employmentModel.getEmployerName()).isEqualTo(td.getValue("employerName"));
            softly.assertThat(employmentModel.getOccupationCd()).isEqualTo(td.getValue("occupationCd"));
            softly.assertThat(employmentModel.getOccupationDescription()).isEqualTo(td.getValue("occupationDescription"));
            softly.assertThat(employmentModel.getOccupationStatusCd()).isEqualTo(td.getValue("occupationStatusCd"));
            softly.assertThat(employmentModel.getJobTitleCd()).isEqualTo(td.getValue("jobTitleCd"));
            softly.assertThat(employmentModel.getJobTitleDescription()).isEqualTo(td.getValue("jobTitleDescription"));
            softly.assertThat(employmentModel.getAsOfDate()).isEqualTo(td.getValue("asOfDate"));
        });
    }
}
