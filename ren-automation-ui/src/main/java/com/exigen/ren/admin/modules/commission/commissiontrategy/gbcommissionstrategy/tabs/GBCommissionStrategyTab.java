/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.GBCommissionTab;
import com.exigen.ren.admin.modules.commission.commissiontrategy.gbcommissionstrategy.metadata.GBCommissionStrategyMetaData;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class GBCommissionStrategyTab extends GBCommissionTab {

    public static final TableExtended<CommissionOverrideOptions> tableCommissionOverrideOptions = new TableExtended<>("Commission Override Options", By.xpath("//div[@id='strategyTopForm:overrideOptionRulesTable']//table"));
    public static final TableExtended<GBCommissionRuleTab.CommissionRules> tableCommissionRules = new TableExtended<>("Commission Rules",
            By.xpath("//div[@id='strategyTopForm:rulesTable']//table"), By.xpath("//*[@id='strategyTopForm:rulesTable_paginator_bottom']"));

    public GBCommissionStrategyTab() {
        super(GBCommissionStrategyMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonSave.click();
        return this;
    }

    public enum CommissionOverrideOptions implements Named {
        NAME("Name"),
        COMMISSION_TYPE("Commission Type"),
        ACTIONS("Actions");
        private String name;

        CommissionOverrideOptions(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CommissionRules implements Named {
        SALES_CHANNEL("Sales Channel"),
        LOCATION("Location"),
        COMMISSION_TYPE("Commission Type"),
        COVERAGES("Coverages"),
        ACTIONS("Actions");
        private String name;

        CommissionRules(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class CommissionOverrideAssetList extends MultiAssetList {
        public CommissionOverrideAssetList(By locator, Class<? extends MetaData> metaDataClass) {
            super(locator, metaDataClass);
        }

        public CommissionOverrideAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
            super(parent, locator, metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {

        }

        @Override
        protected void selectSection(int index) {

        }
    }

}
