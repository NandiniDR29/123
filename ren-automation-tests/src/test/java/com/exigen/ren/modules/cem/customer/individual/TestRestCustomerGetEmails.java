/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.ContactMethodEmailModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRestCustomerGetEmails extends RestBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-26397", component = CRM_CUSTOMER)
    public void testRestCustomerGetEmails() {
        mainApp().open();

        LOGGER.info("STEP:Create Customer With Email");
        customerIndividual.createViaUI(tdCustomerIndividual.getTestData("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.EMAIl_DETAILS.getLabel()),
                        tdSpecific().getTestData("Contact_Emails")));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("STEP:Check GET/customers/{customerNumber}/emails");
        ResponseContainer<List<ContactMethodEmailModel>> response = customerRestClient.getCustomersEmails(customerNumber);
        TestData td = tdSpecific().getTestData("TestData_Check");
        assertSoftly(softly -> {
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            ContactMethodEmailModel email = response.getModel().get(0);
            softly.assertThat(email.getContactMethod()).isEqualTo(td.getValue("contactMethod"));
            softly.assertThat(email.getContactType()).isEqualTo(td.getValue("contactType"));
            softly.assertThat(email.getPreferredInd()).isEqualTo(td.getValue("preferredInd"));
            softly.assertThat(email.getDoNotSolicitInd().toString()).isEqualTo(td.getValue("doNotSolicitInd"));
            softly.assertThat(email.getComment()).isEqualTo(td.getValue("comment"));
            softly.assertThat(email.getEmailId()).isEqualTo(td.getValue("emailId"));
            softly.assertThat(email.getConsentStatus()).isEqualTo(td.getValue("consentStatus"));
            softly.assertThat(email.getConsentDate()).isEqualTo(td.getValue("consentDate"));
        });
    }
}
