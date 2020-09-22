/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.numberrange.pages;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class NumberRangePage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.id("numberRangeForm"), NumberRangeSearchTab.class);

    public static Button buttonAddRange = new Button(By.id("numberRangeForm:addButton"));
    public static Button buttonSubmitRange = new Button(By.id("addNumberRangeForm:saveButton_footer"));

    public static TableExtended<RangeSearchResults> tableSearchResults = new TableExtended<>("SearchResults", By.xpath("//div[@id='numberRangeForm:dt']//table"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }

    public static class NumberRangeSearchTab extends MetaData {
        public static final AssetDescriptor<ComboBox> RANGE_TYPE = declare("Range Type", ComboBox.class);
        public static final AssetDescriptor<ComboBox> RANGE_STATUS = declare("Range Status", ComboBox.class);
    }

    public enum RangeSearchResults implements Named {
        ID("ID"),
        CREATE_DATE("Create Date"),
        START("Start #"),
        END("End #"),
        CURRENT_ID("Current ID"),
        RANGE_TYPE("Range Type"),
        RANGE_STATUS("Range Status")
        ;
        private String name;

        RangeSearchResults(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
