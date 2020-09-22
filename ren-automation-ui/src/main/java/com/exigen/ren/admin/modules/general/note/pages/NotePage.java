/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.note.pages;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class NotePage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.id("quickNotesForm"), SearchNoteTab.class);

    public static Button buttonAddNoteCategory = new Button(By.id("quickNotesForm:addNote"));
    public static Button buttonDisableCategory = new Button(By.id("quickNotesForm:disableCategory"));

    public static Table tableSearchResults = new Table(By.xpath("//div[@id='quickNotesForm:quickNoteInfoTable']//table"));

    public static StaticElement labelTitle = new StaticElement(By.id("previewDialogForm:preview_title"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }

    public static class SearchNoteTab extends MetaData {
        public static final AssetDescriptor<ComboBox> TYPE = declare("Type", ComboBox.class);
        public static final AssetDescriptor<TextBox> CATEGORY = declare("Category", TextBox.class);
        public static final AssetDescriptor<TextBox> TITLE = declare("Title", TextBox.class);
    }
}
