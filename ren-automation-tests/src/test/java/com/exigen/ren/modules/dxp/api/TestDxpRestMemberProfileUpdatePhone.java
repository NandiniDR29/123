/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CustomerConstants;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.ProfilePhoneModel;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpRestMemberProfileUpdatePhone extends RestBaseTest implements CustomerContext {

    private static final String REST_ERROR_CODE = "422";
    private static final String INVALID_PHONE_ERROR_MESSAGE = "Phone number should contain only digits or have the following format: (xxx) xxx-xxxx";
    private static final String INVALID_CODE_ERROR_MESSAGE = "Wrong lookup code for Phone contactType";
    private static final String EMPTY_PHONE_ERROR_MESSAGE = "Phone Number is required";
    private static final String DUPLICATE_PHONE_ERROR_MESSAGE = "Duplicate contact error. Such PHONE contact details information already exists. Please check entered information.";
    private static final String OBJECT_NOT_FOUND_ERROR_CODE = "ERROR_SERVICE_OBJECT_NOT_FOUND";
    private static final String OBJECT_NOT_FOUND_ERROR_MESSAGE = "Object not found.";
    private static final String ENTITY_NOT_FOUND_ERROR_MESSAGE = "Entity with this id not found.";
    private static final String CONTACT_INFO_NOT_FOUND_ERROR_CODE = "CONTACT_INFO_NOT_FOUND";
    private static final String CONTACT_INFO_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Contact info %s not found";

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-13876", component = CUSTOMER_REST)
    public void testDxpRestMemberProfileUpdatePhone() {

        mainApp().open();
        customerIndividual.create(customerIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_withoutPhone"));
        String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("Test REN-13876 Steps 6");

        ProfilePhoneModel incorrectPhoneFormatModel = new ProfilePhoneModel();
        incorrectPhoneFormatModel.setPhone("11^11111111");
        incorrectPhoneFormatModel.setPhoneTypeCd("MOB");

        ProfilePhoneModel incorrectPhoneResponse = dxpRestService.postMemberProfilePhone(incorrectPhoneFormatModel, customerNum).getModel();
        checkResponse(incorrectPhoneResponse, INVALID_PHONE_ERROR_MESSAGE);

        LOGGER.info("Test REN-13876 Steps 7");
        ProfilePhoneModel incorrectPhoneCodeModel = new ProfilePhoneModel();
        incorrectPhoneCodeModel.setPhone("1111111111");
        incorrectPhoneCodeModel.setPhoneTypeCd("MdfdfB");

        ProfilePhoneModel incorrectPhoneCodeResponse = dxpRestService.postMemberProfilePhone(incorrectPhoneCodeModel, customerNum).getModel();
        checkResponse(incorrectPhoneCodeResponse, INVALID_CODE_ERROR_MESSAGE);

        LOGGER.info("Test REN-13876 Steps 8");
        ProfilePhoneModel emptyPhoneModel = new ProfilePhoneModel();
        emptyPhoneModel.setPhoneTypeCd("MOB");

        ProfilePhoneModel emptyPhoneModelResponse = dxpRestService.postMemberProfilePhone(emptyPhoneModel, customerNum).getModel();
        checkResponse(emptyPhoneModelResponse, EMPTY_PHONE_ERROR_MESSAGE);

        LOGGER.info("Test REN-13876 Steps 10");
        ProfilePhoneModel correctPhoneModel1 = new ProfilePhoneModel();
        correctPhoneModel1.setPhone("(485) 509-6995");
        correctPhoneModel1.setPhoneTypeCd("MOB");
        correctPhoneModel1.setPreferredInd(true);

        ProfilePhoneModel correctResponse1 = dxpRestService.postMemberProfilePhone(correctPhoneModel1, customerNum).getModel();
        assertThat(correctResponse1).isEqualToIgnoringGivenFields(correctPhoneModel1, "id");

        // Verify request result on UI in Main App
        MainPage.QuickSearch.search(customerNum);
        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        assertThat(CustomerSummaryPage.tableCustomerContacts).hasMatchingRows(1, ImmutableMap.of(
                CustomerConstants.CustomerContactsTable.CONTACT_DETAILS.getName(), correctPhoneModel1.getFormattedPhone(),
                CustomerConstants.CustomerContactsTable.CONTACT_METHOD.getName(), CustomerConstants.PHONE,
                CustomerConstants.CustomerContactsTable.CONTACT_TYPE.getName(), "Mobile",
                CustomerConstants.CustomerContactsTable.PREFERRED.getName(), VALUE_YES
        ));

        LOGGER.info("Test REN-13876 Steps 11");
        assertSoftly(softly -> {
            ProfilePhoneModel response = dxpRestService.postMemberProfilePhone(correctPhoneModel1, customerNum).getModel();

            softly.assertThat(response.getErrorCode()).isEqualTo(REST_ERROR_CODE);
            softly.assertThat(response.getMessage()).isEqualTo(DUPLICATE_PHONE_ERROR_MESSAGE);
        });

        LOGGER.info("Test REN-13876 Steps 12");
        ProfilePhoneModel correctPhoneModel2 = new ProfilePhoneModel();
        correctPhoneModel2.setPhone("(485) 509-6990");
        correctPhoneModel2.setPhoneTypeCd("MOB");
        correctPhoneModel2.setPreferredInd(true);

        ProfilePhoneModel correctResponse2 = dxpRestService.postMemberProfilePhone(correctPhoneModel2, customerNum).getModel();
        assertThat(correctResponse2).isEqualToIgnoringGivenFields(correctPhoneModel2, "id");

        LOGGER.info("Test REN-13876 Steps 13");
        ProfilePhoneModel correctPhoneModel3 = new ProfilePhoneModel();
        correctPhoneModel3.setPhone("(485) 509-6495");

        ProfilePhoneModel correctResponse3 = dxpRestService.postMemberProfilePhone(correctPhoneModel3, customerNum).getModel();
        assertThat(correctResponse3).isEqualToIgnoringGivenFields(correctPhoneModel3, "id");

        LOGGER.info("Test REN-13876 Steps 14");
        assertSoftly(softly -> {
            List<ProfilePhoneModel> phones = dxpRestService.getMemberProfilePhones(customerNum).getModel();

            correctPhoneModel1.setPreferredInd(false);
            correctPhoneModel2.setPreferredInd(true);

            softly.assertThat(phones).hasSize(3);
            softly.assertThat(phones.get(0)).isEqualToIgnoringGivenFields(correctPhoneModel1, "id");
            softly.assertThat(phones.get(1)).isEqualToIgnoringGivenFields(correctPhoneModel2, "id");
            softly.assertThat(phones.get(2)).isEqualToIgnoringGivenFields(correctPhoneModel3, "id");
        });

        // --------------- PUT ---------------
        LOGGER.info("Test REN-13876 Steps 16");
        String nonExistingPhoneId = RandomStringUtils.randomNumeric(7).replaceFirst("0", "1");

        assertSoftly(softly -> {
            ResponseContainer<ProfilePhoneModel> response = dxpRestService.putMemberProfilePhone(incorrectPhoneFormatModel, customerNum, nonExistingPhoneId);

            softly.assertThat(response.getResponse()).hasStatus(404);
            softly.assertThat(response.getModel().getErrorCode()).isEqualTo(OBJECT_NOT_FOUND_ERROR_CODE);
            softly.assertThat(response.getModel().getMessage()).isEqualTo(ENTITY_NOT_FOUND_ERROR_MESSAGE);
        });

        LOGGER.info("Test REN-13876 Steps 17.6");
        String correctPhoneId = correctResponse1.getId();
        ProfilePhoneModel incorrectPhoneFormatResponse = dxpRestService.putMemberProfilePhone(incorrectPhoneFormatModel, customerNum, correctPhoneId).getModel();
        checkResponse(incorrectPhoneFormatResponse, INVALID_PHONE_ERROR_MESSAGE);

        LOGGER.info("Test REN-13876 Steps 17.7");
        ProfilePhoneModel incorrectPhoneCodeResponse_2 = dxpRestService.putMemberProfilePhone(incorrectPhoneCodeModel, customerNum, correctPhoneId).getModel();
        checkResponse(incorrectPhoneCodeResponse_2, INVALID_CODE_ERROR_MESSAGE);

        LOGGER.info("Test REN-13876 Steps 17.8");
        ProfilePhoneModel emptyPhoneModelResponse_2 = dxpRestService.putMemberProfilePhone(emptyPhoneModel, customerNum, correctPhoneId).getModel();
        checkResponse(emptyPhoneModelResponse_2, EMPTY_PHONE_ERROR_MESSAGE);

        LOGGER.info("Test REN-13876 Steps 18");
        ProfilePhoneModel correctPhoneModelUpdate = new ProfilePhoneModel();
        correctPhoneModelUpdate.setPhone("2222222223");
        correctPhoneModelUpdate.setPhoneTypeCd("FAX");
        correctPhoneModelUpdate.setPreferredInd(true);

        ResponseContainer<ProfilePhoneModel> correctResponseUpdate = dxpRestService.putMemberProfilePhone(correctPhoneModelUpdate, customerNum, correctPhoneId);
        assertThat(correctResponseUpdate.getResponse()).hasStatus(200);

        assertSoftly(softly -> {
            List<ProfilePhoneModel> phones = dxpRestService.getMemberProfilePhones(customerNum).getModel();

            correctPhoneModel2.setPreferredInd(false);

            softly.assertThat(phones).hasSize(3);
            softly.assertThat(phones.get(0)).isEqualToIgnoringGivenFields(correctPhoneModelUpdate, "id");
            softly.assertThat(phones.get(1)).isEqualToIgnoringGivenFields(correctPhoneModel2, "id");
            softly.assertThat(phones.get(2)).isEqualToIgnoringGivenFields(correctPhoneModel3, "id");
        });

        // --------------- DELETE ---------------
        LOGGER.info("Test REN-13876 Steps 22");
        assertSoftly(softly -> {
            ResponseContainer<ProfilePhoneModel> response = dxpRestService.deleteMemberProfilePhone(customerNum, "abrvalg");

            softly.assertThat(response.getResponse()).hasStatus(404);
            softly.assertThat(response.getModel().getErrorCode()).isEqualTo(OBJECT_NOT_FOUND_ERROR_CODE);
            softly.assertThat(response.getModel().getMessage()).isEqualTo(OBJECT_NOT_FOUND_ERROR_MESSAGE);
        });

        LOGGER.info("Test REN-13876 Steps 23");
        assertSoftly(softly -> {
            ResponseContainer<ProfilePhoneModel> response = dxpRestService.deleteMemberProfilePhone(customerNum, nonExistingPhoneId);

            softly.assertThat(response.getResponse()).hasStatus(404);
            softly.assertThat(response.getModel().getErrorCode()).isEqualTo(CONTACT_INFO_NOT_FOUND_ERROR_CODE);
            softly.assertThat(response.getModel().getMessage()).isEqualTo(String.format(CONTACT_INFO_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, nonExistingPhoneId));
        });

        LOGGER.info("Test REN-13876 Steps 24");
        ResponseContainer<ProfilePhoneModel> responseDelete = dxpRestService.deleteMemberProfilePhone(customerNum, correctResponse3.getId());
        assertThat(responseDelete.getResponse()).hasStatus(200);

        // Verify request result on UI in Main App
        MainPage.QuickSearch.search(customerNum);
        NavigationPage.toSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());

        assertSoftly(softly -> {
            softly.assertThat(CustomerSummaryPage.tableCustomerContacts).hasMatchingRows(1, ImmutableMap.of(
                    CustomerConstants.CustomerContactsTable.CONTACT_DETAILS.getName(), correctPhoneModelUpdate.getFormattedPhone(),
                    CustomerConstants.CustomerContactsTable.CONTACT_METHOD.getName(), CustomerConstants.PHONE,
                    CustomerConstants.CustomerContactsTable.CONTACT_TYPE.getName(), "Fax",
                    CustomerConstants.CustomerContactsTable.PREFERRED.getName(), VALUE_YES
            ));

            softly.assertThat(CustomerSummaryPage.tableCustomerContacts).hasMatchingRows(1, ImmutableMap.of(
                    CustomerConstants.CustomerContactsTable.CONTACT_DETAILS.getName(), correctPhoneModel2.getFormattedPhone(),
                    CustomerConstants.CustomerContactsTable.CONTACT_METHOD.getName(), CustomerConstants.PHONE,
                    CustomerConstants.CustomerContactsTable.CONTACT_TYPE.getName(), "Mobile",
                    CustomerConstants.CustomerContactsTable.PREFERRED.getName(), VALUE_NO
            ));

            softly.assertThat(CustomerSummaryPage.tableCustomerContacts).hasMatchingRows(0, ImmutableMap.of(
                    CustomerConstants.CustomerContactsTable.CONTACT_DETAILS.getName(), correctPhoneModel3.getFormattedPhone(),
                    CustomerConstants.CustomerContactsTable.CONTACT_METHOD.getName(), CustomerConstants.PHONE
            ));
        });
    }

    public void checkResponse(ProfilePhoneModel response, String errorMessage) {
        assertThat(response.getErrorCode()).isEqualTo(REST_ERROR_CODE);
        assertThat(response.getMessage()).isEqualTo(errorMessage);
    }
}
