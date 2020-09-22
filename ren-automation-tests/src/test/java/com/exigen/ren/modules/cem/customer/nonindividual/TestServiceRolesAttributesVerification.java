package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipServiceRole.*;
import static com.exigen.ren.main.enums.CustomerConstants.INDIVIDUAL;
import static com.exigen.ren.main.enums.CustomerConstants.NON_INDIVIDUAL;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestServiceRolesAttributesVerification extends CustomerBaseTest {

    private RepeatAssetList assetListRelationshipTab = (RepeatAssetList) relationshipTab.getAssetList();

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35627", component = CRM_CUSTOMER)
    public void testServiceRolesAttributesVerification() {
        LOGGER.info("General Preconditions");

        List<String> relationshipToCustomer = ImmutableList.of("", "Agent", "Client (Eligibility Vendor)", "Division", "Franchisee", "Franchisor", "Legal Representation",
                "Member Company", "Parent Company", "Partner", "Service Roles", "Subsidiary", "Supplier", "TPA", "Other");

        LOGGER.info("Step#1 Verification");
        mainApp().open();
        initiateCustomerToRelationshipTab();
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE).setValue(NON_INDIVIDUAL);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER)).hasOptions(relationshipToCustomer);

            LOGGER.info("Step#2 Verification");
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.REVERSED_RELATIONSHIP_TO_CUSTOMER)).hasOptions(relationshipToCustomer);

            LOGGER.info("Step#3 Verification");
            assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER).setValue("Service Roles");
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.SERVICE_ROLE))
                    .hasOptions(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName(), PORTAL_BENEFITS_ADMINISTRATOR.getUIName(), PORTAL_BROKER_ADMINISTRATOR.getUIName());

            LOGGER.info("Step#4 Verification");
            assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE).setValue(INDIVIDUAL);
            Page.dialogConfirmation.confirm();
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER)).hasOptions(ImmutableList.of("", "Service Roles", "Other"));
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.REVERSED_RELATIONSHIP_TO_CUSTOMER)).hasOptions(ImmutableList.of("", "Client (Agency)", "Client (attorney)", "Employer", "Other"));
        });

        LOGGER.info("Step#5 Verification");
        RelationshipTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        initiateCustomerToRelationshipTab();
        CustomSoftAssertions.assertSoftly(softly -> {
            assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE).setValue(INDIVIDUAL);
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.SERVICE_ROLE)).isAbsent();

            assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE).setValue(NON_INDIVIDUAL);
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.SERVICE_ROLE)).isAbsent();
        });
    }

    private void initiateCustomerToRelationshipTab() {
        customerNonIndividual.initiate();
        customerNonIndividual.getDefaultWorkspace().fillFromTo(getDefaultCustomerNonIndividualTestData(), GeneralTab.class, RelationshipTab.class);
        RelationshipTab.buttonAddRelationship.click();
    }
}
