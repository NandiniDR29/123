/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.dblookups.pages;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import com.exigen.ren.common.components.Dialog;
import org.openqa.selenium.By;

public class DBLookupsPage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.id("lookupsListForm"), DBLookupsSearchTab.class);

    public static Button buttonAddNewLookup = new Button(By.id("LookupValuesForm:addBtn"));
    public static Button buttonSearch = new Button(By.id("lookupsListForm:searchBtn"));
    public static Button buttonImportFromXls = new Button(By.id("lookupsListForm:importBtn"));
    public static Button buttonBrowse = new Button(By.id("file"));
    public static Button buttonImport = new Button(By.id("importLookupsForm:importLookupsOk"));
    public static TextBox textBoxLookupListName = new TextBox(By.id("lookupsListForm:searchCriteria"));
    public static Button buttonExport = new Button(By.id("lookupsListForm:exportBtn"));


    public static TableExtended<DBLookupsSearchResults> tableSearchResults = new TableExtended<>("DBLookups Search Results", By.xpath("//div[@id='lookupsListForm:lookupTableId']//table"));
    public static TableExtended<DBLookupsValues> tableLookupValues = new TableExtended<>("Lookup Values", By.xpath("//div[@id='LookupValuesForm:foundLookupsValues']//table"));

    public static Dialog uploadDialg = new Dialog("//div[@id='importLookupsPanel_content']");

    public static Link linkNext = new Link(By.xpath("//a[@id='LookupValuesForm:body_foundLookupsValues:dataScrollerfoundLookupsValues_ds_next']"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }

    public static void search(String lookupListName) {
        textBoxLookupListName.setValue(lookupListName);
        buttonSearch.click();
    }

    public static void selectCheckBox(String name) {
        new CheckBox(By.xpath(String.format("//td[a[.='Branch']]/preceding-sibling::td[1]/div[@class='ui-chkbox ui-widget']", name)))
                .setValue(true);
    }

    public static class DBLookupsSearchTab extends MetaData {
        public static final AssetDescriptor<TextBox> LOOKUPLIST_NAME = declare("LookupList Name: ", TextBox.class, By.id("lookupsListForm:searchCriteria"));

    }

    public enum DBLookupsSearchResults implements Named {
        NAME("Name"),
        DESCRIPTION("Description"),
        PRODUCT("Product")
        ;
        private String name;

        DBLookupsSearchResults(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum DBLookupsValues implements Named {
        CODE("code"),
        VALUE("value (default)"),
        ORDER_NO("orderNo"),
        VALUE_EN_US("value_EN_US_"),
        VALUE_PT_BR("value_PT_BR_"),
        ACTIONS("Actions"),
        LOB_CD("lobCd")
        ;
        private String name;

        DBLookupsValues(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
