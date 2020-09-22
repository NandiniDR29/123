package com.exigen.ren.modules.dxp.api;

import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.CustomerGroupContactModel;
import com.exigen.ren.rest.dxp.model.CustomerGroupContactRelationshipModel;
import com.exigen.ren.rest.model.RestError;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipServiceRole.*;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipToCustomer.SERVICE_ROLES;
import static com.exigen.ren.main.enums.CustomerConstants.INDIVIDUAL;
import static com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData.*;
import static com.exigen.ren.main.modules.customer.tabs.RelationshipTab.searchAndGetPartyRelationshipIndByName;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestDxpEmployerEditGroupDetails extends RestBaseTest implements CustomerContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40705", component = CUSTOMER_REST)
    public void testDxpEmployerEditGroupDetails() {
        LOGGER.info("Scenarios 1, 3, 4");
        mainApp().open();

        createDefaultIndividualCustomer();
        String customerNumberIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerIC1FirstName = CustomerSummaryPage.getCustomerFirstName();
        String customerIC1LastName = CustomerSummaryPage.getCustomerLastName();

        createDefaultIndividualCustomer();
        String customerNumberIC2 = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerIC2FirstName = CustomerSummaryPage.getCustomerFirstName();
        String customerIC2LastName = CustomerSummaryPage.getCustomerLastName();


        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        String customerNumberNIC = CustomerSummaryPage.labelCustomerNumber.getValue();
        customerNonIndividual.update().start();
        customerNonIndividual.update().getWorkspace().getTab(relationshipTab.getClass()).navigateToTab();
        RelationshipTab.buttonAddRelationship.click();
        RepeatAssetList assetListRelationshipTab = (RepeatAssetList) relationshipTab.getAssetList();
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE).setValue(INDIVIDUAL);
        assetListRelationshipTab.getAsset(FIRST_NAME, 0).setValue(customerIC1FirstName);
        assetListRelationshipTab.getAsset(LAST_NAME, 0).setValue(customerIC1LastName);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER, 0).setValue(SERVICE_ROLES);
        searchAndGetPartyRelationshipIndByName(1, customerIC1FirstName);
        assetListRelationshipTab.getAsset(SERVICE_ROLE).setValue(ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName(), PORTAL_BENEFITS_ADMINISTRATOR.getUIName()));

        RelationshipTab.buttonAddRelationship.click();
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE, 1).setValue(INDIVIDUAL);
        assetListRelationshipTab.getAsset(FIRST_NAME, 1).setValue(customerIC2FirstName);
        assetListRelationshipTab.getAsset(LAST_NAME, 1).setValue(customerIC2LastName);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER, 1).setValue(SERVICE_ROLES);
        searchAndGetPartyRelationshipIndByName(2, customerIC2FirstName);
        assetListRelationshipTab.getAsset(SERVICE_ROLE, 1).setValue(ImmutableList.of(CLAIMS.getUIName()));
        relationshipTab.submitTab();

        LOGGER.info("Scenario 1, 4: Step 1");
        ResponseContainer<List<CustomerGroupContactModel>> groupNumberResponse = dxpRestService.getEmployerGroupsGroupNumberContacts(customerNumberIC1, customerNumberNIC);
        assertThat(groupNumberResponse.getResponse().getStatus()).isEqualTo(200);
        assertThat(groupNumberResponse.getModel()).hasSize(2);
        CustomerGroupContactModel contactIC1 = groupNumberResponse.getModel().stream().filter(contact -> contact.getCustomerNumber().equals(customerNumberIC1)).findFirst().orElseThrow(() -> new IstfException(String.format("Contact with number='%s' not found", customerNumberIC1)));
        CustomerGroupContactModel contactIC2 = groupNumberResponse.getModel().stream().filter(contact -> contact.getCustomerNumber().equals(customerNumberIC2)).findFirst().orElseThrow(() -> new IstfException(String.format("Contact with number='%s' not found", customerNumberIC2)));
        assertSoftly(softly -> {
            softly.assertThat(contactIC1.getFirstName()).isEqualTo(customerIC1FirstName);
            softly.assertThat(contactIC1.getLastName()).isEqualTo(customerIC1LastName);

            softly.assertThat(contactIC2.getFirstName()).isEqualTo(customerIC2FirstName);
            softly.assertThat(contactIC2.getLastName()).isEqualTo(customerIC2LastName);
            softly.assertThat(contactIC2.getServiceRoleCds()).isEqualTo(ImmutableList.of("CLAIMS"));
        });

        LOGGER.info("Scenario 3: Step 2");
        CustomerGroupContactRelationshipModel customerGroupContactRelationshipModel = new CustomerGroupContactRelationshipModel();
        customerGroupContactRelationshipModel.setCustomerNumber(customerNumberIC2);
        customerGroupContactRelationshipModel.setServiceRoleCds(ImmutableList.of("ADMINISTRATIVE", "BILLING"));
        ResponseContainer<CustomerGroupContactRelationshipModel> responsePutContactRelationship = dxpRestService.putEmployerContactRelationship(customerNumberIC1, customerNumberNIC, contactIC2.getContactRelationshipId(), customerGroupContactRelationshipModel);
        assertThat(responsePutContactRelationship.getResponse().getStatus()).isEqualTo(200);

        LOGGER.info("Scenario 3: Step 3");
        ResponseContainer<List<CustomerGroupContactModel>> groupNumberResponse_2 = dxpRestService.getEmployerGroupsGroupNumberContacts(customerNumberIC1, customerNumberNIC);
        assertThat(groupNumberResponse_2.getResponse().getStatus()).isEqualTo(200);
        assertThat(groupNumberResponse_2.getModel()).hasSize(2);
        CustomerGroupContactModel contactIC2_2 = groupNumberResponse_2.getModel().stream().filter(contact -> contact.getCustomerNumber().equals(customerNumberIC2)).findFirst().orElseThrow(() -> new IstfException(String.format("Contact with number='%s' not found", customerNumberIC2)));
        assertSoftly(softly -> {
            softly.assertThat(contactIC2_2.getFirstName()).isEqualTo(customerIC2FirstName);
            softly.assertThat(contactIC2_2.getLastName()).isEqualTo(customerIC2LastName);
            softly.assertThat(contactIC2_2.getServiceRoleCds()).isEqualTo(ImmutableList.of("ADMINISTRATIVE", "BILLING"));
        });

        LOGGER.info("Scenario 4: Step 2");
        ResponseContainer<RestError> responseDeleteContactRelationship = dxpRestService.deleteEmployerContactRelationship(customerNumberIC1, customerNumberNIC, contactIC2_2.getContactRelationshipId());
        assertThat(responseDeleteContactRelationship.getResponse().getStatus()).isEqualTo(204);

        LOGGER.info("Scenario 4: Step 3");
        ResponseContainer<List<CustomerGroupContactModel>> groupNumberResponse_3 = dxpRestService.getEmployerGroupsGroupNumberContacts(customerNumberIC1, customerNumberNIC);
        assertThat(groupNumberResponse_3.getResponse().getStatus()).isEqualTo(200);
        assertThat(groupNumberResponse_3.getModel()).hasSize(1);
        assertThat(groupNumberResponse_3.getModel().stream().filter(contact -> contact.getCustomerNumber().equals(customerNumberIC2)).findAny().orElse(null))
                .as("Contact relationship with id='%s' wasn't deleted in previous step", contactIC2_2.getContactRelationshipId()).isNull();
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40705", component = CUSTOMER_REST)
    public void testDxpEmployerEditGroupDetailsPostContactRelationships() {
        LOGGER.info("Scenario 2");
        mainApp().open();

        createDefaultIndividualCustomer();
        String customerNumberIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerIC1FirstName = CustomerSummaryPage.getCustomerFirstName();
        String customerIC1LastName = CustomerSummaryPage.getCustomerLastName();

        createDefaultIndividualCustomer();
        String customerNumberIC2 = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerIC2FirstName = CustomerSummaryPage.getCustomerFirstName();
        String customerIC2LastName = CustomerSummaryPage.getCustomerLastName();

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        String customerNumberNIC = CustomerSummaryPage.labelCustomerNumber.getValue();
        customerNonIndividual.update().start();
        customerNonIndividual.update().getWorkspace().getTab(relationshipTab.getClass()).navigateToTab();
        RelationshipTab.buttonAddRelationship.click();
        RepeatAssetList assetListRelationshipTab = (RepeatAssetList) relationshipTab.getAssetList();
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE).setValue(INDIVIDUAL);
        assetListRelationshipTab.getAsset(FIRST_NAME, 0).setValue(customerIC1FirstName);
        assetListRelationshipTab.getAsset(LAST_NAME, 0).setValue(customerIC1LastName);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER, 0).setValue(SERVICE_ROLES);
        searchAndGetPartyRelationshipIndByName(1, customerIC1FirstName);
        assetListRelationshipTab.getAsset(SERVICE_ROLE).setValue(ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName(), PORTAL_BENEFITS_ADMINISTRATOR.getUIName()));
        relationshipTab.submitTab();

        LOGGER.info("Scenario 2: Step 1");
        CustomerGroupContactRelationshipModel customerGroupContactRelationshipModel = new CustomerGroupContactRelationshipModel();
        customerGroupContactRelationshipModel.setCustomerNumber(customerNumberIC2);
        customerGroupContactRelationshipModel.setServiceRoleCds(ImmutableList.of("CLAIMS"));
        ResponseContainer<CustomerGroupContactRelationshipModel> respPostContactRelationship = dxpRestService.postEmployerGroupsGroupNumberContactRelationships(customerNumberIC1, customerNumberNIC, customerGroupContactRelationshipModel);
        assertThat(respPostContactRelationship.getResponse().getStatus()).isEqualTo(200);

        ResponseContainer<List<CustomerGroupContactModel>> groupNumberResponse = dxpRestService.getEmployerGroupsGroupNumberContacts(customerNumberIC1, customerNumberNIC);
        assertThat(groupNumberResponse.getResponse().getStatus()).isEqualTo(200);
        assertThat(groupNumberResponse.getModel()).hasSize(2);
        assertSoftly(softly -> {
            CustomerGroupContactModel contactIC1 = groupNumberResponse.getModel().stream().filter(contact -> contact.getCustomerNumber().equals(customerNumberIC1)).findFirst().orElseThrow(() -> new IstfException(String.format("Contact with number='%s' not found", customerNumberIC1)));
            softly.assertThat(contactIC1.getFirstName()).isEqualTo(customerIC1FirstName);
            softly.assertThat(contactIC1.getLastName()).isEqualTo(customerIC1LastName);

            CustomerGroupContactModel contactIC2 = groupNumberResponse.getModel().stream().filter(contact -> contact.getCustomerNumber().equals(customerNumberIC2)).findFirst().orElseThrow(() -> new IstfException(String.format("Contact with number='%s' not found", customerNumberIC2)));
            softly.assertThat(contactIC2.getFirstName()).isEqualTo(customerIC2FirstName);
            softly.assertThat(contactIC2.getLastName()).isEqualTo(customerIC2LastName);
        });
    }
}