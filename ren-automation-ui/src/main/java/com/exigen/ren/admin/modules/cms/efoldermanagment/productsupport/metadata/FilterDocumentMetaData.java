/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.efoldermanagment.productsupport.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class FilterDocumentMetaData extends MetaData {
    public static final AssetDescriptor<ComboBox> SUBSYSTEM = declare("Subsystem", ComboBox.class, By.id("fileListForm:subsystem"));
    public static final AssetDescriptor<ComboBox> PRODUCT = declare("Product", ComboBox.class, By.id("fileListForm:product"));
    public static final AssetDescriptor<ComboBox> DOCUMENT_TYPE = declare("Document Type", ComboBox.class, By.id("fileListForm:docType"));
    public static final AssetDescriptor<Button> BUTTON_REFRECH = declare("Refresh", Button.class, By.id("fileListForm:findFileList"));
}
