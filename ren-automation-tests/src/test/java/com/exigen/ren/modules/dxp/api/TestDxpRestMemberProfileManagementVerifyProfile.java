/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.MemberProfileModel;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ADDRESS_DETAILS;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpRestMemberProfileManagementVerifyProfile extends RestBaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext,
        GroupAccidentCertificatePolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-14170", component = CUSTOMER_REST)
    public void testDxpRestReviewCustomerProfileInformation() {

        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentCertificatePolicy();
        navigateToCustomer();
        String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();

        customerIndividual.update().start();
        customerIndividual.update().getWorkspace().getTab(generalTab.getClass()).fillTab(customerIndividual.getDefaultTestData(DATA_GATHER, "TestData_withPhoneEmail")
                .mask(TestData.makeKeyPath(generalTab.getMetaKey(), ADDRESS_DETAILS.getLabel())));
        Tab.buttonSaveAndExit.click();

        CustomSoftAssertions.assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            ResponseContainer<MemberProfileModel> response = dxpRestService.getEmployerMemberProfile(customerNumberAuthorize, customerNum);
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
        });

        CustomSoftAssertions.assertSoftly(softly -> {
            LOGGER.info("---=={Step 3}==---");
            TestData tdUpdate = tdCustomerIndividual.getTestData(DATA_GATHER, "TestData_Update_Without_BusinessEntity");
            String FirstName = tdUpdate.getValue(GeneralTab.class.getSimpleName(), GeneralTabMetaData.FIRST_NAME.getLabel());
            String LastName = tdUpdate.getValue(GeneralTab.class.getSimpleName(), GeneralTabMetaData.LAST_NAME.getLabel());

            customerIndividual.update().perform(tdCustomerIndividual.getTestData(DATA_GATHER, "TestData_Update_Without_BusinessEntity"));
            LOGGER.info("---=={Step 4}==---");
            ResponseContainer<MemberProfileModel> responseUpdate = dxpRestService.getEmployerMemberProfile(customerNumberAuthorize, customerNum);
            softly.assertThat(responseUpdate.getResponse().getStatus()).isEqualTo(200);
            MemberProfileModel profileUpdate = responseUpdate.getModel();
            softly.assertThat(profileUpdate.getFirstName()).isEqualTo(FirstName);
            softly.assertThat(profileUpdate.getLastName()).isEqualTo(LastName);

            LOGGER.info("---=={Step 5}==---");
            String IncorrectCustomerNum = "test";
            ResponseContainer<MemberProfileModel> responseIncorrect = dxpRestService.getEmployerMemberProfile(IncorrectCustomerNum, IncorrectCustomerNum);
            softly.assertThat(responseIncorrect.getResponse().getStatus()).isEqualTo(403);
            softly.assertThat(responseIncorrect.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
            softly.assertThat(responseIncorrect.getModel().getMessage()).isEqualTo("User has no access to member");
        });
    }
}
