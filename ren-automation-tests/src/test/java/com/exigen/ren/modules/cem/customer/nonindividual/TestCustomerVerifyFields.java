package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerVerifyFields extends CustomerBaseTest {

    private final String labelContactsPattern = "//div[@id='crmForm:custEditContactsGroup']//label[text()='%s']";
    private final StaticElement buttonAddNAICSClassification = new StaticElement(ByT.xpath("//div[@id='crmForm:customerBusinessInfoPanel']//*[normalize-space(text())='%s']").format("Add NAICS Classification"));

    @Test(groups = {CUSTOMER_NONIND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-6743", "REN-6553", "REN-6689", "REN-13179"}, component = CRM_CUSTOMER)
    public void testCustomerVerifyFields() {
        mainApp().open();
        customerNonIndividual.initiate();

        AssetList assetListGeneralTab = (AssetList) customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList();
        assertSoftly(softly -> {
            // Assert for REN-13179
            softly.assertThat(buttonAddNAICSClassification).isAbsent();

            // Assert for REN-6743
            softly.assertThat(assetListGeneralTab.getAsset(GeneralTabMetaData.NON_INDIVIDUAL_TYPE)).isPresent().isRequired().hasValue("Undefined");

            // Assert for REN-6553
            softly.assertThat(assetListGeneralTab.getAsset(GeneralTabMetaData.DATE_BUSINESS_STARTED)).isOptional();
        });

        // Assert for REN-6743
        assetListGeneralTab.getAsset(GeneralTabMetaData.NON_INDIVIDUAL_TYPE).setValue("");
        assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.NON_INDIVIDUAL_TYPE)).hasWarning();

        // Asserts for REN-6689
        assertSoftly(softly -> {
            // Check for each fieldName that field is absent on the page.
            ImmutableList.of("Subdivision, military, organization, other", "Latitude", "Longitude", "Accuracy", "Reference ID", "Temporary").forEach(fieldName -> {
                softly.assertThat(new StaticElement(ByT.xpath(labelContactsPattern).format(fieldName)))
                        .as(String.format("Field '%s' isn't hidden", fieldName))
                        .isAbsent();

            });
        });
        GeneralTab.buttonNext.click();
        assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.DATE_BUSINESS_STARTED)).hasNoWarning();
    }
}