/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.metadata;

import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class ViewHistoryActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> VERSION_DATE = declare("Version Date", TextBox.class);
    public static final AssetDescriptor<StaticElement> PROVIDER_ID = declare("Provider ID", StaticElement.class);
    public static final AssetDescriptor<StaticElement> PROVIDER_NAME = declare("Provider Name", StaticElement.class);
    public static final AssetDescriptor<StaticElement> SPECIALTY = declare("Specialty", StaticElement.class);
    public static final AssetDescriptor<StaticElement> PRACTICE_ID = declare("Practice ID", StaticElement.class);
    public static final AssetDescriptor<StaticElement> PRACTICE_NAME = declare("Practice Name", StaticElement.class);
    public static final AssetDescriptor<StaticElement> ADDRESS = declare("Address", StaticElement.class);
    public static final AssetDescriptor<StaticElement> PHONE = declare("Phone", StaticElement.class);
    public static final AssetDescriptor<StaticElement> EFFECTIVE_DATE = declare("Effective Date", StaticElement.class);
    public static final AssetDescriptor<StaticElement> EXPIRATION_DATE = declare("Expiration Date", StaticElement.class);
    public static final AssetDescriptor<StaticElement> COMMENT = declare("Comment", StaticElement.class,
            By.xpath(" //table[contains(@class, 'comparisonTable') and .//*[(contains(@value,'Provider Information')" +
                    " or contains(text(),'Provider Information'))]] //div[contains(text(), 'Comment')]"));
}
