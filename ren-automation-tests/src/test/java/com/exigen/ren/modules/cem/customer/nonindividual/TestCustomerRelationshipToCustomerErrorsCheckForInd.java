package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerPrimaryContactPreference.EMAIL;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerPrimaryContactPreference.MAIL;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipServiceRole.*;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipToCustomer.SERVICE_ROLES;
import static com.exigen.ren.main.enums.CustomerConstants.INDIVIDUAL;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerRelationshipToCustomerErrorsCheckForInd extends CustomerBaseTest {
    private RepeatAssetList assetListRelationshipTab = (RepeatAssetList) relationshipTab.getAssetList();

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15719", component = CRM_CUSTOMER)
    public void testCustomerRelationshipToCustomerErrorsCheckForInd() {
        LOGGER.info("General preconditions");
        mainApp().open();
        initiateCreateNonIndividualAndFillToTab(getDefaultCustomerNonIndividualTestData(), RelationshipTab.class, false);

        LOGGER.info("Step#1 verification");
        addRelationshipAndCheckServiceRole(0);

        LOGGER.info("Step#2 verification");
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.SERVICE_ROLE, 0).setValue(ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName()));

        LOGGER.info("Step#3 verification");
        assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE, 0)).hasValue(MAIL);

        LOGGER.info("Step#6 execution");
        relationshipTab.getAssetList().fill(tdSpecific().getTestData("TestDataRelationshipAddress"));

        LOGGER.info("Step#10 verification");
        addRelationshipAndCheckServiceRole(1);

        LOGGER.info("Step#11 verification");
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.SERVICE_ROLE, 1).setValue(CLAIMS.getUIName());

        LOGGER.info("Step#12 verification");
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE, 1).setValue(EMAIL);

        LOGGER.info("Steps#15 execution");
        relationshipTab.getAssetList().fill(tdSpecific().getTestData("TestDataRelationshipEmail"));

        LOGGER.info("Steps#16 execution");
        relationshipTab.submitTab();
        assertThat(CustomerSummaryPage.labelCustomerNumber).isPresent();
    }

    private void addRelationshipAndCheckServiceRole(int customerIndex) {
        RelationshipTab.buttonAddRelationship.click();
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE, customerIndex).setValue(INDIVIDUAL);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.FIRST_NAME, customerIndex).setValue(String.valueOf(tdSpecific().getTestData("CustomerRandomName", GeneralTab.class.getSimpleName())
                .getValue(GeneralTabMetaData.FIRST_NAME.getLabel())));
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.LAST_NAME, customerIndex).setValue(String.valueOf(tdSpecific().getTestData("CustomerRandomName", GeneralTab.class.getSimpleName())
                .getValue(GeneralTabMetaData.LAST_NAME.getLabel())));
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER, customerIndex).setValue(SERVICE_ROLES);
        assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.ASSIGN_SERVICE_ROLE, customerIndex))
                .hasValue(VALUE_YES);
    }
}