package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerPrimaryContactPreference.MAIL;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipServiceRole.*;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipToCustomer.*;
import static com.exigen.ren.main.enums.CustomerConstants.INDIVIDUAL;
import static com.exigen.ren.main.enums.CustomerConstants.NON_INDIVIDUAL;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang.StringUtils.EMPTY;

public class TestCustomerRelationshipToCustomerDropDownVerification extends CustomerBaseTest {
    private RepeatAssetList assetListRelationshipTab = (RepeatAssetList) relationshipTab.getAssetList();

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15673", component = CRM_CUSTOMER)
    public void testCustomerRelationshipToCustomerDropDownVerification() {
        mainApp().open();
        initiateCreateNonIndividualAndFillToTab(getDefaultCustomerNonIndividualTestData(), RelationshipTab.class, false);
        RelationshipTab.buttonAddRelationship.click();

        LOGGER.info("Verification for Non-Individual relationship type");
        relationshipFieldsVerification(NON_INDIVIDUAL, TPA, AGENT, false);

        LOGGER.info("Verification for Individual relationship type");
        relationshipFieldsVerification(INDIVIDUAL, SERVICE_ROLES, OTHER, true);
    }

    private void relationshipFieldsVerification(String relationshipType, String relationshipToCustomerValue1, String relationshipToCustomerValue2, boolean changeCustomedType) {
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE).setValue(relationshipType);
        if (changeCustomedType) {
            Page.dialogConfirmation.buttonYes.click();
        }
        assertSoftly(softly -> {
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER)).hasValue(EMPTY).isRequired();
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.ASSIGN_SERVICE_ROLE))
                    .hasValue(VALUE_NO);
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE)).hasValue(EMPTY);

            assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER).setValue(relationshipToCustomerValue1);
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.ASSIGN_SERVICE_ROLE))
                    .hasValue(VALUE_YES);
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE)).hasValue(MAIL);
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.AUTHORIZATION_OPTION)).isAbsent();
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.PASSWORD)).isAbsent();
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.PASSWORD_REMINDER)).isAbsent();
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.CHALLENGE_QUESTION)).isAbsent();
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.ANSWER)).isAbsent();
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.COMMENT)).isAbsent();
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.SERVICE_ROLE))
                    .hasOptions(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName(), PORTAL_BENEFITS_ADMINISTRATOR.getUIName(), PORTAL_BROKER_ADMINISTRATOR.getUIName());

            assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER).setValue(relationshipToCustomerValue2);
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.ASSIGN_SERVICE_ROLE))
                    .hasValue(VALUE_NO);
            softly.assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE)).hasValue(EMPTY);
        });
    }
}
