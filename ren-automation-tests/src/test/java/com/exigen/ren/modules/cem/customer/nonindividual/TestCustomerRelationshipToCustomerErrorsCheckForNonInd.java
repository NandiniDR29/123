package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.RepeatAssetList;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerPrimaryContactPreference.EMAIL;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerPrimaryContactPreference.MAIL;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipServiceRole.*;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipToCustomer.TPA;
import static com.exigen.ren.main.enums.CustomerConstants.NON_INDIVIDUAL;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.SERVICE_ROLE_VALIDATION_MESSAGE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerRelationshipToCustomerErrorsCheckForNonInd extends CustomerBaseTest {
    private RepeatAssetList assetListRelationshipTab = (RepeatAssetList) relationshipTab.getAssetList();

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15712", component = CRM_CUSTOMER)
    public void testCustomerRelationshipToCustomerErrorsCheckForNonInd() {
        LOGGER.info("General preconditions");
        mainApp().open();
        initiateCreateNonIndividualAndFillToTab(getDefaultCustomerNonIndividualTestData(), RelationshipTab.class, false);

        LOGGER.info("Step#1 verification");
        addRelationshipAndCheckServiceRole(0);

        LOGGER.info("Step#3 verification");
        assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE, 0)).hasValue(MAIL);

        LOGGER.info("Step#6 execution");
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.SERVICE_ROLE, 0).setValue(ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName()));
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE, 0).setValue(EMAIL);
        relationshipTab.getAssetList().fill(tdSpecific().getTestData("TestDataRelationshipAddress"));
        assertThat(CustomerSummaryPage.tableRelationshipResult).hasRows(1);

        LOGGER.info("Step#10 verification");
        addRelationshipAndCheckServiceRole(1);

        LOGGER.info("Step#12 execution");
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE, 1).setValue(EMAIL);

        LOGGER.info("Step#26 execution"); //skipped steps 11, 18, 19, 22, 23 due to non-automation verification
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.SERVICE_ROLE, 1).setValue(ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName()));

        LOGGER.info("Steps#15, 27 verification");
        relationshipTab.getAssetList().fill(tdSpecific().getTestData("TestDataRelationshipEmail"));
        assertThat(CustomerSummaryPage.tableRelationshipResult).hasRows(1);

        LOGGER.info("Step#28 execution");
        relationshipTab.submitTab();
        assertThat(ErrorPage.tableError).isPresent().hasMatchingRows(ErrorPage.TableError.DESCRIPTION.getName(), SERVICE_ROLE_VALIDATION_MESSAGE);
        ErrorPage.buttonBack.click();

        LOGGER.info("Step#30 execution");
        relationshipTab.navigateToTab();
        RelationshipTab.editRelationship(1);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.ASSIGN_SERVICE_ROLE, 0).setValue(VALUE_NO);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.ASSIGN_SERVICE_ROLE, 0).setValue(VALUE_YES);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.SERVICE_ROLE, 0).setValue(ImmutableList.of(ADMINISTRATIVE.getUIName()));
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.PRIMARY_CONTACT_PREFERENCE, 0).setValue(MAIL);

        LOGGER.info("Step#31 execution");
        relationshipTab.submitTab();

        LOGGER.info("Step#32 execution");
        assertThat(CustomerSummaryPage.labelCustomerNumber).isPresent();
    }

    private void addRelationshipAndCheckServiceRole(int customerIndex) {
        RelationshipTab.buttonAddRelationship.click();
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.TYPE, customerIndex).setValue(NON_INDIVIDUAL);
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.NAME_LEGAL, customerIndex).setValue(String.valueOf(tdSpecific().getTestData("CustomerRandomName", GeneralTab.class.getSimpleName())
                .getValue(GeneralTabMetaData.NAME_LEGAL.getLabel())));
        assetListRelationshipTab.getAsset(RelationshipTabMetaData.RELATIONSHIP_TO_CUSTOMER, customerIndex).setValue(TPA);
        assertThat(assetListRelationshipTab.getAsset(RelationshipTabMetaData.ASSIGN_SERVICE_ROLE, customerIndex)).hasValue(VALUE_YES);
    }
}