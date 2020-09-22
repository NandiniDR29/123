/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.MemberProfileModel;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpRestReviewCustomerProfileInformation extends RestBaseTest implements CustomerContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-14050", component = CUSTOMER_REST)
    public void testDxpRestReviewCustomerProfileInformation() {

        mainApp().open();
        customerIndividual.createViaUI(customerIndividual.getDefaultTestData(DATA_GATHER, "TestData_withPhoneEmail")
                .adjust(TestData.makeKeyPath(RelationshipTab.class.getSimpleName()),
                        customerIndividual.getDefaultTestData(DATA_GATHER, "RelationshipWithIndividualTypeRelative")));

        String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();

        CustomSoftAssertions.assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            ResponseContainer<MemberProfileModel> response = dxpRestService.getMemberProfile(customerNum);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);

            LOGGER.info("---=={Step 2}==---");
            MemberProfileModel profile = response.getModel();
            softly.assertThat(profile.getFirstName()).isNotNull().isNotEmpty();
            softly.assertThat(profile.getMiddleName()).isNotNull().isNotEmpty();
            softly.assertThat(profile.getLastName()).isNotNull().isNotEmpty();
            softly.assertThat(profile.getDateOfBirth()).isNotNull().isNotEmpty();
            softly.assertThat(profile.getPhones()).isNotNull().isNotEmpty();
            softly.assertThat(profile.getEmails()).isNotNull().isNotEmpty();
            softly.assertThat(profile.getAddresses()).isNotNull().isNotEmpty();
            softly.assertThat(profile.getRelationships()).isNotNull().isNotEmpty();

            LOGGER.info("---=={Step 3}==---");
            TestData tdUpdate = tdCustomerIndividual.getTestData(DATA_GATHER, "TestData_Update_Without_BusinessEntity");
            String FirstName = tdUpdate.getValue(GeneralTab.class.getSimpleName(), GeneralTabMetaData.FIRST_NAME.getLabel());
            String LastName = tdUpdate.getValue(GeneralTab.class.getSimpleName(), GeneralTabMetaData.LAST_NAME.getLabel());

            customerIndividual.update().perform(tdCustomerIndividual.getTestData(DATA_GATHER, "TestData_Update_Without_BusinessEntity"));
            LOGGER.info("---=={Step 4}==---");
            ResponseContainer<MemberProfileModel> responseUpdate = dxpRestService.getMemberProfile(customerNum);
            softly.assertThat(responseUpdate.getResponse().getStatus()).isEqualTo(200);
            MemberProfileModel profileUpdate = responseUpdate.getModel();
            softly.assertThat(profileUpdate.getFirstName()).isEqualTo(FirstName);
            softly.assertThat(profileUpdate.getLastName()).isEqualTo(LastName);

            LOGGER.info("---=={Step 5}==---");
            String IncorrectCustomerNum = "test";
            ResponseContainer<MemberProfileModel> responseIncorrect = dxpRestService.getMemberProfile(IncorrectCustomerNum);
            softly.assertThat(responseIncorrect.getResponse().getStatus()).isEqualTo(404);
            softly.assertThat(responseIncorrect.getModel().getErrorCode()).isEqualTo("CUSTOMER_NOT_FOUND");
            softly.assertThat(responseIncorrect.getModel().getMessage()).isEqualTo(String.format("Customer with number %s not found.", IncorrectCustomerNum));
        });
    }
}
