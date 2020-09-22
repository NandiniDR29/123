package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CustomerConstants.NON_INDIVIDUAL;
import static com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData.TYPE;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddSpecialGroupTaggingAttributesVerification extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35552", component = CRM_CUSTOMER)
    public void testAddSpecialGroupTaggingAttributesVerification() {

        LOGGER.info("General Preconditions");
        List<String> nonIndividualType = ImmutableList.of("", "Association", "Corporation", "General Partnership", "Joint Venture",
                "Limited Liability Partnership", "Limited Partnership", "LLC", "Not For Profit Organization", "PEO", "Partnership",
                "Professional Corporation","Professional Limited Liability Company", "Sole Proprietorship", "Subchapter S Corporation", "Trust", "Undefined");

        List<String> relationshipToCustomer = ImmutableList.of("", "Agent", "Client (Eligibility Vendor)", "Division", "Franchisee", "Franchisor",
                "Legal Representation", "Member Company", "Parent Company", "Partner", "Service Roles", "Subsidiary", "Supplier", "TPA", "Other");

        mainApp().open();
        customerNonIndividual.initiate();

        LOGGER.info("Step#1 Verification");
        assertThat(customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(GeneralTabMetaData.NON_INDIVIDUAL_TYPE))
                .isPresent().hasOptions(nonIndividualType);

        LOGGER.info("Step#2 Verification");
        customerNonIndividual.getDefaultWorkspace().fillFromTo(getDefaultCustomerNonIndividualTestData(), GeneralTab.class, RelationshipTab.class);
        RelationshipTab.buttonAddRelationship.click();
        relationshipTab.getAssetList().getAsset(TYPE).setValue(NON_INDIVIDUAL);

        AbstractContainer<?, ?> relationshipTabAssetList = customerNonIndividual.getDefaultWorkspace().getTab(RelationshipTab.class).getAssetList();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(relationshipTabAssetList.getAsset(RelationshipTabMetaData.NON_INDIVIDUAL_TYPE)).isPresent().hasOptions(nonIndividualType);

            LOGGER.info("Step#3 Verification");
            softly.assertThat(relationshipTabAssetList.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER)).isPresent().hasOptions(relationshipToCustomer);

            LOGGER.info("Step#4 Verification");
            softly.assertThat(relationshipTabAssetList.getAsset(RelationshipTabMetaData.REVERSED_RELATIONSHIP_TO_CUSTOMER)).isPresent().hasOptions(relationshipToCustomer);
        });
    }
}
