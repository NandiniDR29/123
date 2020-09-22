/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs;

import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitStatutorySTDInjuryPartyInformationTabMetaData;
import org.openqa.selenium.By;

public class BenefitStatutorySTDInjuryPartyInformationTab extends DefaultTab {
    public static final Button addYTDTaxableWage = new Button(By.id("policyDataGatherForm:addStatPartyYTDTaxableWage"));

    public BenefitStatutorySTDInjuryPartyInformationTab() {
        super(BenefitStatutorySTDInjuryPartyInformationTabMetaData.class);
    }

    public static class YTDTaxableWageMultiAssetList extends MultiAssetList {

        public YTDTaxableWageMultiAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
            super(parent, locator, metaDataClass);
        }

        public YTDTaxableWageMultiAssetList(By locator, Class<? extends MetaData> metaDataClass) {
            super(locator, metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            addYTDTaxableWage.click();
        }

        @Override
        protected void selectSection(int i) {
        }
    }
}
