/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.cem.account.nonindividual;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.cem.account.AccountBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CustomerConstants.EIN;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.GROUP_SPONSOR;
import static com.exigen.ren.utils.components.Components.CRM_ACCOUNT;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestEINFieldOptionalForGroupSponsor extends AccountBaseTest {

    @Test(groups = {ACCOUNT, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-19102", component = CRM_ACCOUNT)
    public void testEINFieldOptionalForGroupSponsor() {
        assertSoftly(softly -> {
            mainApp().open();
            createDefaultNonIndividualCustomer();
            softly.assertThat(CustomerSummaryPage.tableCustomers.getRow(1).getCell(CustomerSummaryPage.CustomerInformation.EIN.getName())).isNotEqualTo("");

            LOGGER.info("---=={Step 1}==---");
            customerNonIndividual.update().start();
            customerNonIndividual.getDefaultWorkspace().getTab(GeneralTab.class).getAssetList().getAsset(EIN);
            generalTab.getAssetList().getAsset(GROUP_SPONSOR).setValue(false);
            softly.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.EIN)).isPresent().isRequired();

            LOGGER.info("---=={Step 2}==---");
            generalTab.getAssetList().getAsset(GROUP_SPONSOR).setValue(true);
            softly.assertThat(generalTab.getAssetList().getAsset(GeneralTabMetaData.EIN)).isPresent().isOptional();

            LOGGER.info("---=={Step 3}==---");
            generalTab.getAssetList().getAsset(GeneralTabMetaData.EIN).setValue("");
            generalTab.submitTab();
            relationshipTab.submitTab();

            LOGGER.info("---=={Step 4}==---");
            softly.assertThat(CustomerSummaryPage.tableCustomers.getRow(1).getCell(CustomerSummaryPage.CustomerInformation.EIN.getName())).hasValue("");
        });

    }
}
