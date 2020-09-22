package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.CustomerModel;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.pages.ErrorPage.TableError.DESCRIPTION;
import static com.exigen.ren.common.pages.ErrorPage.tableError;
import static com.exigen.ren.main.enums.ActionConstants.REMOVE;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerContactsTable.ACTION;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerContactsTable.CONTACT_DETAILS;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestAddCemRestApiToChangeContactPreferenceNonInd extends RestBaseTest {

    private final static String EMAIL_3 = "email_3@email.com";
    private final static String EMAIL_4 = "email_4@email.com";
    private final static String EMAIL = "EMAIL";
    private final static String EMAIL_SMALL = "Email";
    private final static String MAIL = "Mail";

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-43861", component = CRM_CUSTOMER)
    public void testNonIndividualEmailTypeVerification() {
        mainApp().open();
        LOGGER.info("REN-43861 Step 1");
        initiateCreateIndividualAndFillToTab(tdSpecific().getTestData("TestData_REN_43861"), GeneralTab.class, true);
        commonStepsFromFirstToFifth();

        LOGGER.info("REN-43861 Step 6");
        customerNonIndividual.update().start();
        generalTab.getAssetList().getAsset(GeneralTabMetaData.GROUP_SPONSOR).setValue(false);
        GeneralTab.tableContactDetails.getRow(CONTACT_DETAILS.getName(), EMAIL_3).getCell(ACTION.getName()).controls.buttons.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        GeneralTab.tableContactDetails.getRow(CONTACT_DETAILS.getName(), EMAIL_4).getCell(ACTION.getName()).controls.buttons.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        commonStepsFromFirstToFifth();
    }


    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-43927", component = CRM_CUSTOMER)
    public void testApiRenCrmServicesPutCustomerPaperless() {
        LOGGER.info("REN-43927 Precondition");
        mainApp().open();
        customerIndividual.createViaUI(tdSpecific().getTestData("TestData_REN_43927_1"));
        String customerId1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        customerIndividual.createViaUI(tdSpecific().getTestData("TestData_REN_43927_2"));
        String customerId3 = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("REN-43927 Step1");
        ResponseContainer<CustomerModel> responseContainer_1 = renCustomerRestService.updatesPaperlessForCustomer(customerId1, createCustomerModelWithParameters("NNN"));
        assertSoftly(softly -> {
            softly.assertThat(responseContainer_1.getResponse().getStatus()).isEqualTo(400);
            softly.assertThat(responseContainer_1.getError().getMessage()).isEqualTo("Parameter value type is incorrect");
            softly.assertThat(responseContainer_1.getError().getField()).isEqualTo("preferredContactMethod");

        });

        LOGGER.info("REN-43927 Step2");
        ResponseContainer<CustomerModel> responseContainer_2 = renCustomerRestService.updatesPaperlessForCustomer(customerId1, createCustomerModelWithParameters(null));
        assertSoftly(softly -> {
            softly.assertThat(responseContainer_2.getResponse().getStatus()).isEqualTo(204);
            softly.assertThat(responseContainer_2.getError()).isNull();
        });

        LOGGER.info("REN-43927 Step3");
        customerNonIndividual.inquiry().start();
        CustomAssertions.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.PRIMARY_CONTACT_PREFERENCE)).hasValue(MAIL);
        Tab.buttonCancel.click();

        LOGGER.info("REN-43927 Step4");
        ResponseContainer<CustomerModel> responseContainer_3 = renCustomerRestService.updatesPaperlessForCustomer(customerId1, createCustomerModelWithParameters(EMAIL));
        assertSoftly(softly -> {
            softly.assertThat(responseContainer_3.getResponse().getStatus()).isEqualTo(204);
            softly.assertThat(responseContainer_3.getError()).isNull();
        });
        MainPage.QuickSearch.search(customerId1);
        customerNonIndividual.inquiry().start();
        CustomAssertions.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.PRIMARY_CONTACT_PREFERENCE)).hasValue(EMAIL_SMALL);
        Tab.buttonCancel.click();

        LOGGER.info("REN-43927 Step9");
        ResponseContainer<CustomerModel> responseContainer_4 = renCustomerRestService.updatesPaperlessForCustomer(customerId3, createCustomerModelWithParameters(EMAIL));
        assertSoftly(softly -> {
            softly.assertThat(responseContainer_4.getResponse().getStatus()).isEqualTo(422);
            softly.assertThat(responseContainer_4.getError().getErrors().get(0).getMessage())
                    .isEqualTo("One Legal address is required. One Common email address is required if Paperless is checked");
        });
    }

    private CustomerModel createCustomerModelWithParameters(String preferredContactMethod) {
        CustomerModel customerModel = new CustomerModel();
        customerModel.setCustomerStatus(null);
        customerModel.setSourceCd(null);
        customerModel.setRatingCd(null);
        customerModel.setPreferredSpokenLanguageCd(null);
        customerModel.setPreferredWrittenLanguageCd(null);
        customerModel.setPaperless(null);
        customerModel.setPaperless(true);
        customerModel.setPreferredContactMethod(preferredContactMethod);
        return customerModel;
    }

    private void commonStepsFromFirstToFifth(){
        LOGGER.info("REN-43861 Step 2");
        generalTab.getAssetList().getAsset(GeneralTabMetaData.ADDITIONAL_INFORMATION).getAsset(GeneralTabMetaData.AdditionalInformationMetaData.PAPERLESS).setValue(true);
        generalTab.buttonTopSave.click();
        checkErrorMsg();
        ErrorPage.buttonBack.click();

        LOGGER.info("REN-43861 Step 3");
        addEmailAndClickSave(EMAIL_3);
        CustomAssertions.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.NON_INDIVIDUAL_TYPE)).isPresent();

        LOGGER.info("REN-43861 Step 4");
        addEmailAndClickSave(EMAIL_4);
        checkErrorMsg();
        ErrorPage.buttonBack.click();

        LOGGER.info("REN-43861 Step 5");
        generalTab.getAssetList().getAsset(GeneralTabMetaData.ADDITIONAL_INFORMATION).getAsset(GeneralTabMetaData.AdditionalInformationMetaData.PAPERLESS).setValue(false);
        generalTab.buttonSaveAndExit.click();
        CustomAssertions.assertThat(CustomerSummaryPage.labelCustomerNumber).isPresent();
    }

    private void addEmailAndClickSave(String email) {
        GeneralTab.comboBoxSelectContactMethod.setValue("Email");
        customerNonIndividual.addNewContactsDetails().perform(tdCustomerNonIndividual.getTestData("ContactsDetails", "Adjustment_NewEmail")
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), GeneralTabMetaData.EMAIl_DETAILS.getLabel(), GeneralTabMetaData.EmailDetailsMetaData.EMAIL_ADDRESS.getLabel()), email));
        NavigationPage.toSubTab(NavigationEnum.CustomerTab.GENERAL);
        generalTab.buttonTopSave.click();
    }

    private void checkErrorMsg() {
        CustomAssertions.assertThat(tableError)
                .hasMatchingRows(DESCRIPTION.getName(), "One Legal address is required. One Common email address is required if Paperless is checked");
    }
}
