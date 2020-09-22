/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.customer.individual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.modules.cem.customer.CustomerBaseTest;
import org.testng.annotations.Test;

import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCustomerSalesInfoAttributes extends CustomerBaseTest {

    @Test(groups = {CUSTOMER_IND, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-13478", component = CRM_CUSTOMER)
    public void testCustomerSalesInfoAttributes() {
        mainApp().open();
        customerIndividual.initiate();

        LOGGER.info("REN-13478 Test case 2");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Sales Rep"))).isPresent();
            softly.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.SALES_REP_INTERNAL).getAsset(GeneralTabMetaData.SearchInternalSalesRepMetaData.OPEN_POPUP))
                    .isPresent().isEnabled().hasValue("Assign Sales Rep");

            softly.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.LEAD_SOURCE)).isPresent().isRequired();
            generalTab.getAssetList().getAsset(GeneralTabMetaData.LEAD_SOURCE).setValue(ValueConstants.VALUE_OTHER);
            softly.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.SOURCE_DESCRIPTION)).isPresent().isOptional();
            softly.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.RATING)).isPresent().isRequired();
        });
    }
}
