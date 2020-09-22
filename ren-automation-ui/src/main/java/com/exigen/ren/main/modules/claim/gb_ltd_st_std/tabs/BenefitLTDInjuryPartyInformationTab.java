/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs;

import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData;
import org.openqa.selenium.By;

public class BenefitLTDInjuryPartyInformationTab extends DefaultTab {
    public static StaticElement lableAddressLine1= new StaticElement(By.xpath("//span[@id='policyDataGatherForm:sedit_LongTermDisabilityPartyAddressContact_addressContact_address_addressLine1']"));
    public static final Button addYTDTaxableWage = new Button(By.id("policyDataGatherForm:addLTDPartyYTDTaxableWage"));

    public BenefitLTDInjuryPartyInformationTab() {
        super(BenefitLTDInjuryPartyInformationTabMetaData.class);
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
