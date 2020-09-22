package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipServiceRole.*;
import static com.exigen.ren.main.enums.CustomerConstants.INDIVIDUAL;
import static com.exigen.ren.main.enums.CustomerConstants.NON_INDIVIDUAL;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData.*;
import static com.exigen.ren.main.modules.customer.tabs.RelationshipTab.searchAndGetPartyRelationshipIndByName;
import static com.exigen.ren.main.modules.customer.tabs.RelationshipTab.searchAndGetPartyRelationshipNonIndByName;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestServiceRolesRulesVerification extends CustomerBaseTest {

    private RepeatAssetList assetListRelationshipTab = (RepeatAssetList) relationshipTab.getAssetList();

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35633", component = CRM_CUSTOMER)
    public void testServiceRolesRulesVerification() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        String customerName1 = customerCreationWithSpecificData("GeneralTabCustomer1");
        String customerName2 = customerCreationWithSpecificData("GeneralTabCustomer2");
        String customerName3 = customerCreationWithSpecificData("GeneralTabCustomer3");
        customerIndividual.createViaUI(getDefaultCustomerIndividualTestData().adjust(tdSpecific().getTestData("GeneralTabCustomer4").resolveLinks().resolveLinks()));
        String indCustomerFirstName = tdSpecific().getValue("GeneralTabCustomer4", GeneralTab.class.getSimpleName(), FIRST_NAME.getLabel());

        LOGGER.info("Step#1 Verification");
        customerNonIndividual.initiate();
        customerNonIndividual.getDefaultWorkspace().fillFromTo(getDefaultCustomerNonIndividualTestData(), GeneralTab.class, RelationshipTab.class);
        addNewRelationshipWithServiceRoles(customerName1, 0);

        LOGGER.info("Step#2 Verification");
        assetListRelationshipTab.getAsset(SERVICE_ROLE).setValue(ImmutableList.of(ADMINISTRATIVE.getUIName(), PORTAL_BENEFITS_ADMINISTRATOR.getUIName()));
        RelationshipTab.submitNewRelationship(1);

        LOGGER.info("Step#3 Verification");
        addNewRelationshipWithServiceRoles(customerName2, 1);

        assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE, 1).setValue("Email");
        assetListRelationshipTab.getAsset(SERVICE_ROLE, 1).setValue(ImmutableList.of(BILLING.getUIName(), PORTAL_BROKER_ADMINISTRATOR.getUIName()));
        RelationshipTab.submitNewRelationship(2);

        LOGGER.info("Step#4 Verification");
        addNewRelationshipWithServiceRoles(customerName3, 2);

        assetListRelationshipTab.getAsset(SERVICE_ROLE, 2).setValue(ImmutableList.of(CLAIMS.getUIName(), PORTAL_BROKER_ADMINISTRATOR.getUIName()));
        relationshipTab.submitTab();
        assertThat(CustomerSummaryPage.labelCustomerName).isPresent();

        LOGGER.info("Step#5 Verification");
        customerNonIndividual.update().start();
        relationshipTab.navigateToTab();

        RelationshipTab.buttonAddRelationship.click();
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE, 3).setValue(INDIVIDUAL);
        assetListRelationshipTab.getAsset(FIRST_NAME, 3).setValue(indCustomerFirstName);
        searchAndGetPartyRelationshipIndByName(4, indCustomerFirstName);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER, 3).setValue("Service Roles");
        serviceRolesSectionVerification(3);

        assetListRelationshipTab.getAsset(SERVICE_ROLE, 3).setValue(ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName(), PORTAL_BROKER_ADMINISTRATOR.getUIName()));
        RelationshipTab.submitNewRelationship(4);
        RelationshipTab.buttonTopSave.click();
        assertThat(CustomerSummaryPage.expandableSectionsRelationships.getCount()).isEqualTo(4);
    }

    private String customerCreationWithSpecificData(String testData) {
        customerNonIndividual.createViaUI(getDefaultCustomerNonIndividualTestData().adjust(tdSpecific().getTestData(testData).resolveLinks()).resolveLinks());
        return CustomerSummaryPage.labelCustomerName.getValue();
    }

    private void addNewRelationshipWithServiceRoles(String customerName, int index) {
        RelationshipTab.buttonAddRelationship.click();
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE, index).setValue(NON_INDIVIDUAL);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.NAME_LEGAL, index).setValue(customerName);
        searchAndGetPartyRelationshipNonIndByName(index+1, customerName);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER, index).setValue("Service Roles");
        serviceRolesSectionVerification(index);
    }

    private void serviceRolesSectionVerification(int index) {
        assetListRelationshipTab.getAssets(AUTHORIZATION_OPTION, PASSWORD, PASSWORD_REMINDER, CHALLENGE_QUESTION, ANSWER, COMMENT)
                .forEach(field -> assertThat(field).isAbsent());
        assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.ASSIGN_SERVICE_ROLE, index)).hasValue(VALUE_YES);
        assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE, index)).hasValue("Mail");
    }
}
