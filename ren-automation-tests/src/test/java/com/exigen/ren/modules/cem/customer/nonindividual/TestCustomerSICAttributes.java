package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.*;
import static com.exigen.ren.main.pages.summary.CustomerSummaryPage.CustomerInformation.CUSTOMER;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerSICAttributes extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-23533"}, component = CRM_CUSTOMER)
    public void testCustomerSICAttributes() {
        mainApp().open();
        customerNonIndividual.initiate();
        AssetList assetListGeneralTab = (AssetList) generalTab.getAssetList();

        LOGGER.info("Step 1");
        assetListGeneralTab.getAsset(DIVISION).setValue("I - Services");
        assetListGeneralTab.getAsset(INDUSTRY).setValue("88 - Private Households");
        assetListGeneralTab.getAsset(SIC_DESCRIPTION).setValue("Private Households");
        assertThat(assetListGeneralTab.getAsset(SIC_CODE)).hasValue("8811");
        customerNonIndividual.getDefaultWorkspace().fillFromTo(getDefaultCustomerNonIndividualTestData()
                .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), DIVISION.getLabel()))
                .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), INDUSTRY.getLabel()))
                .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), SIC_DESCRIPTION.getLabel())), GeneralTab.class, RelationshipTab.class, true);
        GeneralTab.buttonSaveAndExit.click();
        LOGGER.info("Created " + EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));

        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("Step 5");
        customerNonIndividual.update().start();
        assetListGeneralTab.getAsset(SIC_CODE).setValue("2033");
        assertSoftly(softly -> {

            softly.assertThat(assetListGeneralTab.getAsset(DIVISION)).hasValue( "D - Manufacturing");
            softly.assertThat(assetListGeneralTab.getAsset(INDUSTRY)).hasValue( "20 - Food and Kindred Products");
            softly.assertThat(assetListGeneralTab.getAsset(SIC_DESCRIPTION)).hasValue( "Canned Fruits, Vegetables, Preserves, Jams, and Jellies");

        });
        GeneralTab.buttonSaveAndExit.click();
        CustomerSummaryPage.tableCustomers.getRow(CUSTOMER.getName(), customerNumber).getCell(CUSTOMER.getName()).controls.links.getFirst().click();
        assertSoftly(softly -> {

            softly.assertThat(assetListGeneralTab.getAsset(DIVISION)).hasValue( "D - Manufacturing");
            softly.assertThat(assetListGeneralTab.getAsset(INDUSTRY)).hasValue( "20 - Food and Kindred Products");
            softly.assertThat(assetListGeneralTab.getAsset(SIC_DESCRIPTION)).hasValue( "Canned Fruits, Vegetables, Preserves, Jams, and Jellies");

        });
    }

}
