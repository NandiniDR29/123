package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceAccountModel;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.NUMBER_OF_EMPLOYEES;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.PhoneDetailsMetaData.PHONE_NUMBER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUploadNewNonIndCustomerDataToSalesforceVerification extends SalesforceBaseTest implements CustomerContext {

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-15129", "REN-15781"}, component = INTEGRATION)
    public void testUploadNewNonIndCustomerDataToSalesforceVerification() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        customerNonIndividual.createViaUI(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY));

        LOGGER.info("REN-15129 Steps#1, 2 execution");
        String salesForceID = getSalesforceAccountId();

        LOGGER.info("REN-15129 Steps#3, 4 verification");
        ResponseContainer<SalesforceAccountModel> response = salesforceService.getAccount(salesForceID);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);

        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.getModel().getType()).isEqualTo("Prospect");
            softly.assertThat(response.getModel().getName()).isEqualTo(CustomerSummaryPage.getCustomerFirstName());
            softly.assertThat(response.getModel().getSic()).isEqualTo(tdSpecific().getValue(generalTab.getMetaKey(), GeneralTabMetaData.SIC_CODE.getLabel()));
            softly.assertThat(response.getModel().getSicDesc()).isEqualTo(tdSpecific().getValue(generalTab.getMetaKey(), GeneralTabMetaData.SIC_DESCRIPTION.getLabel()));
            softly.assertThat(response.getModel().getTaxId()).isEqualTo(tdSpecific().getValue(generalTab.getMetaKey(), GeneralTabMetaData.EIN.getLabel()));
            softly.assertThat(response.getModel().getEisId()).isEqualTo(CustomerSummaryPage.labelCustomerNumber.getValue());

            LOGGER.info("REN-15129 Step#5 verification");
            softly.assertThat(response.getModel().getBillingCity()).isEqualTo(tdSpecific().getValue("Address2", GeneralTabMetaData.AddressDetailsMetaData.CITY.getLabel()));
            softly.assertThat(response.getModel().getBillingCountry()).isEqualTo(tdSpecific().getValue("Address2", GeneralTabMetaData.AddressDetailsMetaData.COUNTRY.getLabel()));
            softly.assertThat(response.getModel().getBillingState()).isEqualTo(tdSpecific().getValue("Address2", GeneralTabMetaData.AddressDetailsMetaData.STATE_PROVINCE.getLabel()));
            softly.assertThat(response.getModel().getBillingStreet()).isEqualTo(tdSpecific().getValue("Address2", GeneralTabMetaData.AddressDetailsMetaData.ADDRESS_LINE_1.getLabel()));
            softly.assertThat(response.getModel().getBillingPostalCode()).isEqualTo(tdSpecific().getValue("Address2", GeneralTabMetaData.AddressDetailsMetaData.ZIP_POST_CODE.getLabel()));

            LOGGER.info("REN-15781 Step#4 verification");
            softly.assertThat(response.getModel().getNumberOfEmployees()).isEqualTo(tdSpecific().getValue(generalTab.getMetaKey(), NUMBER_OF_EMPLOYEES.getLabel()));
            softly.assertThat(response.getModel().getPhone()).isEqualTo(tdSpecific().getValue("ContactDetails_Phone_Work", PHONE_NUMBER.getLabel()));
        });
    }
}
