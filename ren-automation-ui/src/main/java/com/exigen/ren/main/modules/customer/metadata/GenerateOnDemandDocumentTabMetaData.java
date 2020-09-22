/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class GenerateOnDemandDocumentTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> TIPO_DE_APOLICE = declare("Tipo de Apólice", TextBox.class, By.id("manualDocGenForm:_field__mandatory1"));
    public static final AssetDescriptor<TextBox> ADJUSTER_TELEPHONE = declare("Adjuster Telephone", TextBox.class, By.id("manualDocGenForm:_field__AdjusterTelephone"));
    public static final AssetDescriptor<TextBox> ANOTHER_MANDATORY_FIELD = declare("Another Mandatory Field", TextBox.class, By.id("manualDocGenForm:_field__AnotherField"));
    public static final AssetDescriptor<Button> PRINT = declare("PRINT", Button.class, By.id("manualDocGenForm:_delivery__PRINT:0"));
    public static final AssetDescriptor<ComboBox> COUNTRY = declare("Sample Lookup (Country)", ComboBox.class, By.id("manualDocGenForm:_field__country"));
}
