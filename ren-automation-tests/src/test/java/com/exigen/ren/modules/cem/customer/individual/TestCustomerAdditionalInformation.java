/* Copyright © 2018 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ADDITIONAL_INFORMATION;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.AdditionalInformationMetaData.*;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerAdditionalInformation extends CustomerBaseTest {
    private AssetList generalTabAssetList = (AssetList) generalTab.getAssetList();
    private TestData tdGeneralTab = tdSpecific().getTestData("TestData", generalTab.getMetaKey(), ADDITIONAL_INFORMATION.getLabel());

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-24824", component = CRM_CUSTOMER)
    public void testCustomerAdditionalInformation() {
        mainApp().open();

        customerIndividual.create(tdCustomerIndividual.getTestData("DataGather", "TestData"));

        customerIndividual.update().perform(tdSpecific().getTestData("TestData"));
        NavigationPage.setActionAndGo("Update");

        assertSoftly( softly -> {
            softly.assertThat(generalTabAssetList.getAsset(ADDITIONAL_INFORMATION).getAssets(
                    PREFERRED_LANGUAGE_SPOKEN, PREFREED_LANGUAGE_WRITTEN, CITIZENSHIP,
                    OCCUPATION, TOBACCO, PAPERLESS, REGISTERED_ONLINE))
                    .extracting("value").isEqualTo(Arrays.asList(
                    tdGeneralTab.getValue(PREFERRED_LANGUAGE_SPOKEN.getLabel()),
                    tdGeneralTab.getValue(PREFREED_LANGUAGE_WRITTEN.getLabel()),
                    tdGeneralTab.getValue(CITIZENSHIP.getLabel()),
                    tdGeneralTab.getValue(OCCUPATION.getLabel()),
                    tdGeneralTab.getValue(TOBACCO.getLabel()),
                    Boolean.parseBoolean(tdGeneralTab.getValue(PAPERLESS.getLabel())),
                    Boolean.parseBoolean(tdGeneralTab.getValue(REGISTERED_ONLINE.getLabel())))
            );
            softly.assertThat(generalTabAssetList.getAsset(ADDITIONAL_INFORMATION).getAsset(SEGMENTS)).as("SEGMENTS")
                    .containsAllOptions(tdSpecific().getList("ComboListItems", SEGMENTS.getLabel()));
            softly.assertThat(generalTabAssetList.getAsset(ADDITIONAL_INFORMATION).getAsset(CUSTOMER_INTEREST)).as("CUSTOMER_INTEREST")
                    .containsAllOptions(tdSpecific().getList("ComboListItems", CUSTOMER_INTEREST.getLabel()));
            softly.assertThat(generalTabAssetList.getAsset(ADDITIONAL_INFORMATION).getAsset(IMPAIRMENTS_DISABILITIES)).as("IMPAIRMENTS_DISABILITIES")
                    .containsAllOptions(tdSpecific().getList("ComboListItems", IMPAIRMENTS_DISABILITIES.getLabel()));
        });
    }
}
