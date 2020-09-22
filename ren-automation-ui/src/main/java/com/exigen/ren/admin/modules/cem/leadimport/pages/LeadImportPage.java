/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.leadimport.pages;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.FileUpload;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class LeadImportPage extends AdminPage {

    public static Button buttonImport = new Button(By.id("editImportForm:saveButton_footer"));
    public static Button buttonCreateNewImport = new Button(By.id("createNewImport"));
    public static Button buttonSave = new Button(By.id("configForm:saveButton_footer"));
    public static Button buttonConfirm = new Button(By.id("cancelConfirmDialogDialog_form:buttonYes"));

    public static AssetList importLeadFile = new AssetList(By.xpath("//div[contains(@id,'editImportForm') and contains(@id,'content')]"), LeadImportFile.class);

    public static Table tableLeadImports = new Table(By.id("searchForm:searchTable"));

    public static void create(TestData td) {
        importLeadFile.fill(td);
    }

    public static class LeadImportFile extends MetaData {
        public static final AssetDescriptor<TextBox> IMPORT_NAME = declare("Import Name", TextBox.class, By.id("editImportForm:importInfo_name"));
        public static final AssetDescriptor<FileUpload> CHOOSE_FILE = declare("Choose file", FileUpload.class,
                Waiters.AJAX(15000), By.id("file"));
    }
}
