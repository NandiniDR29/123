/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.templatemanagment.doctemplates.pages;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import org.openqa.selenium.By;

public class DocTemplatesPage extends AdminPage {
    public static DialogAssetList dialogAddTemplate = new DialogAssetList(DialogAssetList.getDefaultPopupParentLocator(), DocTemplate.class);
    public static Table tableTemplateSearch = new Table(By.id("selectionForm:documentTemplates"));
    public static Button buttonRefresh = new Button(By.id("criteriaForm:findDocumentTemplates"));


    public static final class DocTemplate extends MetaData {
        public static final AssetDescriptor<TextBox> TEMPLATE_ID = declare("Template ID(Name)", TextBox.class, Waiters.DEFAULT, false);

        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false,
                By.id("selectionForm:addTemplate"));
        public static final AssetDescriptor<Button> DEFAULT_POPUP_SUBMITTER_NAME = declare(AbstractDialog.getDefaultPopupSubmitterName(), Button.class, Waiters.DEFAULT, false,
                By.id("addTemplatePanelForm:createNewTemplate"));
    }
}
