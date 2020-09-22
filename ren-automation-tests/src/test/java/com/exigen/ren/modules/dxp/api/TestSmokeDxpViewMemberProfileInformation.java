/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.*;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest.getBillingAccountNumber;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSmokeDxpViewMemberProfileInformation extends RestBaseTest implements CaseProfileContext, GroupAccidentMasterPolicyContext, GroupAccidentCertificatePolicyContext {

    @Test(groups = {DXP, TEAM_MERGE, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22408", component = CUSTOMER_REST)
    public void testSmokeDxpViewMemberProfileInformation() {

        mainApp().open();
        String indCustomerIC1 = createDefaultNICWithIndRelationshipDefaultRoles();

        String customerNumNIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerNameNIC1 = CustomerSummaryPage.labelCustomerName.getValue();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccountNumber = getBillingAccountNumber(masterPolicyNumber);

        createDefaultGroupAccidentCertificatePolicy();
        navigateToCustomer();

        customerIndividual.update().start();
        String indCustomerNC1FirstName = generalTab.getAssetList().getAsset(GeneralTabMetaData.FIRST_NAME).getValue();
        String indCustomerNC1MiddleName = generalTab.getAssetList().getAsset(GeneralTabMetaData.MIDDLE_NAME).getValue();
        String indCustomerNC1LastName = generalTab.getAssetList().getAsset(GeneralTabMetaData.LAST_NAME).getValue();
        String indCustomerNC1DateOfBirth = generalTab.getAssetList().getAsset(GeneralTabMetaData.DATE_OF_BIRTH).getValue();
        Tab.buttonSaveAndExit.click();
        String customerCertIndNum = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("---=={Step 1}==---");
        ResponseContainer<EmployerProfileModel> response = dxpRestService.getEmployerProfile(customerCertIndNum);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);

        assertSoftly(softly -> {
            EmployerProfileModel model = response.getModel();
            softly.assertThat(model.getCustomerNumber()).isEqualTo(customerCertIndNum);
            softly.assertThat(model.getFirstName()).isEqualTo(indCustomerNC1FirstName);
            softly.assertThat(model.getLastName()).isEqualTo(indCustomerNC1LastName);
        });

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 2}==---");
            ResponseContainer<List<EmployerGroupsModel>> responseGroups = dxpRestService.getEmployerGroups(indCustomerIC1, customerNumNIC1, null, null, null);
            softly.assertThat(responseGroups.getResponse().getStatus()).isEqualTo(200);
            List<EmployerGroupsModel> profile = responseGroups.getModel();
            softly.assertThat(profile.get(0).getGroupCustomerNumber()).isEqualTo(customerNumNIC1);
            softly.assertThat(profile.get(0).getLegalName()).isEqualTo(customerNameNIC1);
            softly.assertThat(profile.get(0).getBillingAccountName()).isEqualTo(customerNameNIC1);
            softly.assertThat(profile.get(0).getBillingAccountNumber()).isEqualTo(billingAccountNumber);
            softly.assertThat(profile.get(0).getGroupType()).isEqualTo("LIST_BILL");
            softly.assertThat(profile.get(0).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicyNumber);
        });

        LOGGER.info("---=={Step 3}==---");
        ProfileEmailModel emailCodeModel = new ProfileEmailModel();
        emailCodeModel.setEmail("email@email.com");
        emailCodeModel.setEmailTypeCd("WORK");
        emailCodeModel.setPreferredInd(true);

        assertSoftly(softly -> {
            ResponseContainer<ProfileEmailModel> responseEmail = dxpRestService.postMemberProfileEmail(emailCodeModel, customerCertIndNum);
            softly.assertThat(responseEmail.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(responseEmail.getModel().getEmail()).isEqualTo(emailCodeModel.getEmail());
            softly.assertThat(responseEmail.getModel().getId()).isNotNull().isNotEmpty();
            softly.assertThat(responseEmail.getModel().getEmailTypeCd()).isEqualTo(emailCodeModel.getEmailTypeCd());
            softly.assertThat(responseEmail.getModel().getPreferredInd()).isEqualTo(emailCodeModel.getPreferredInd());
        });
        LOGGER.info("---=={Step 4}==---");
        ProfilePhoneModel phoneModel = new ProfilePhoneModel();
        phoneModel.setPhone("123456789");
        phoneModel.setId(null);
        phoneModel.setPhoneTypeCd("WORK");
        phoneModel.setPreferredInd(true);

        assertSoftly(softly -> {
            ResponseContainer<List<ProfilePhoneModel>> responsePhones = dxpRestService.postMemberProfilePhones(phoneModel, indCustomerIC1, customerCertIndNum);
            softly.assertThat(responsePhones.getResponse().getStatus()).isEqualTo(200);
            ProfilePhoneModel phoneModelResponse = responsePhones.getModel().get(0);
            softly.assertThat(phoneModelResponse.getPhone()).isEqualTo(phoneModel.getPhone());
            softly.assertThat(phoneModelResponse.getId()).isNotNull().isNotEmpty();
            softly.assertThat(phoneModelResponse.getPhoneTypeCd()).isEqualTo(phoneModel.getPhoneTypeCd());
            softly.assertThat(phoneModelResponse.getPreferredInd()).isEqualTo(phoneModel.getPreferredInd());
        });

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 5}==---");
            ResponseContainer<MemberProfileModel> responseProfile = dxpRestService.getEmployerMemberProfile(indCustomerIC1, customerCertIndNum);
            softly.assertThat(responseProfile.getResponse().getStatus()).isEqualTo(200);
            MemberProfileModel profileModel = responseProfile.getModel();
            softly.assertThat(profileModel.getFirstName()).isEqualTo(indCustomerNC1FirstName);
            softly.assertThat(profileModel.getMiddleName()).isEqualTo(indCustomerNC1MiddleName);
            softly.assertThat(profileModel.getLastName()).isEqualTo(indCustomerNC1LastName);
            softly.assertThat(LocalDate.parse(profileModel.getDateOfBirth(), YYYY_MM_DD).format(DateTimeUtils.MM_DD_YYYY))
                    .isEqualTo(indCustomerNC1DateOfBirth);

            softly.assertThat(profileModel.getPhones().get(0).getPhone()).isEqualTo(phoneModel.getPhone());
            softly.assertThat(profileModel.getPhones().get(0).getId()).isNotNull().isNotEmpty();
            softly.assertThat(profileModel.getPhones().get(0).getPhoneTypeCd()).isEqualTo(phoneModel.getPhoneTypeCd());
            softly.assertThat(profileModel.getPhones().get(0).getPreferredInd()).isEqualTo(phoneModel.getPreferredInd());

            softly.assertThat(profileModel.getEmails().get(0).getEmail()).isEqualTo(emailCodeModel.getEmail());
            softly.assertThat(profileModel.getEmails().get(0).getId()).isNotNull().isNotEmpty();
            softly.assertThat(profileModel.getEmails().get(0).getEmailTypeCd()).isEqualTo(emailCodeModel.getEmailTypeCd());
            softly.assertThat(profileModel.getEmails().get(0).getPreferredInd()).isEqualTo(emailCodeModel.getPreferredInd());

            softly.assertThat(profileModel.getAddresses().get(0)).isNotNull();

        });

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 6}==---");
            ResponseContainer<MemberProfileModel> responseUpdate = dxpRestService.getMemberProfile(customerCertIndNum);
            softly.assertThat(responseUpdate.getResponse().getStatus()).isEqualTo(200);
            MemberProfileModel profileUpdate = responseUpdate.getModel();

            softly.assertThat(profileUpdate.getFirstName()).isEqualTo(indCustomerNC1FirstName);
            softly.assertThat(profileUpdate.getMiddleName()).isEqualTo(indCustomerNC1MiddleName);
            softly.assertThat(profileUpdate.getLastName()).isEqualTo(indCustomerNC1LastName);
            softly.assertThat(LocalDate.parse(profileUpdate.getDateOfBirth(), YYYY_MM_DD).format(DateTimeUtils.MM_DD_YYYY))
                    .isEqualTo(indCustomerNC1DateOfBirth);

            softly.assertThat(profileUpdate.getPhones().get(0).getPhone()).isEqualTo(phoneModel.getPhone());
            softly.assertThat(profileUpdate.getPhones().get(0).getId()).isNotNull().isNotEmpty();
            softly.assertThat(profileUpdate.getPhones().get(0).getPhoneTypeCd()).isEqualTo(phoneModel.getPhoneTypeCd());
            softly.assertThat(profileUpdate.getPhones().get(0).getPreferredInd()).isEqualTo(phoneModel.getPreferredInd());

            softly.assertThat(profileUpdate.getEmails().get(0).getEmail()).isEqualTo(emailCodeModel.getEmail());
            softly.assertThat(profileUpdate.getEmails().get(0).getId()).isNotNull().isNotEmpty();
            softly.assertThat(profileUpdate.getEmails().get(0).getEmailTypeCd()).isEqualTo(emailCodeModel.getEmailTypeCd());
            softly.assertThat(profileUpdate.getEmails().get(0).getPreferredInd()).isEqualTo(emailCodeModel.getPreferredInd());

            softly.assertThat(profileUpdate.getAddresses().get(0)).isNotNull();
        });

        ResponseContainer<List<EmployerGoupsMasterPoliciesModel>> responsePolicy = dxpRestService.getEmployerGroupMasterPolicies(indCustomerIC1, customerNumNIC1);
        assertThat(responsePolicy.getResponse().getStatus()).isEqualTo(200);
        assertThat(responsePolicy.getModel().get(0).getPolicyNumber()).isEqualTo(masterPolicyNumber);

    }
}
