/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDeathBeneficiaryTabMetaData;
import org.openqa.selenium.By;

public class BenefitsAccidentalDeathBeneficiaryTab extends DefaultTab {

    public static Button buttonAdd = new Button(By.xpath("//input[@value='Add']"));
    public static Button buttonRemove = new Button(By.id("policyDataGatherForm:eliminateAccidentalDeathBeneficiary"));
    public static Button buttonAddBeneficiaryDesignation = new Button(By.id("policyDataGatherForm:addAccidentalDeathBeneficiaryCoverage"));

    public BenefitsAccidentalDeathBeneficiaryTab() {
        super(BenefitsAccidentalDeathBeneficiaryTabMetaData.class);

        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {

            @Override
            protected void addSection(int index, int size) {
                if(index > 0 && getAssetCollection().containsKey(BenefitsAccidentalDeathBeneficiaryTabMetaData.ASSOCIATED_INSURABLE_RISK.getLabel())) {
                    buttonAddBeneficiaryDesignation.click();
                }
            }

            @Override
            protected void selectSection(int index) {
            }

            @Override
            public void fill(TestData td) {
                if(buttonRemove.isPresent()){
                    super.fill(td);
                }
                else {
                    buttonAdd.click();
                    super.fill(td);
                }
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }
}
