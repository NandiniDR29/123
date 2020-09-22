/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.common.metadata;

import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class CommissionSearchTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> PRODUCT_NAME = declare("Product Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> COMMISSION_STRATEGY_STATUS = declare("Commission Strategy Status", ComboBox.class, By.id("strategySearchForm:productSearchCriteria_strategyStatus"));
    public static final AssetDescriptor<TextBox> COMMISSION_BONUS_NAME = declare("Commission Bonus Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> COMMISSION_BONUS_STATUS = declare("Commission Bonus Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> REFERAL_STATUS = declare("Referral Status", ComboBox.class);
    public static final AssetDescriptor<TextBox> COMMISSION_GROUP_NAME = declare("Commission Group Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> COMMISSION_GROUP_STATUS = declare("Commission Group Status", ComboBox.class);
    public static final AssetDescriptor<ComboBox> BULK_ADJUSTMENT_STATUS = declare("Bulk Adjustment Status", ComboBox.class);
    public static final AssetDescriptor<TextBox> COMMISSION_TEMPLATE_NAME = declare("Template Name", TextBox.class);
    public static final AssetDescriptor<ComboBox> COMMISSION_TEMPLATE_TYPE = declare("Template Type", ComboBox.class);
}
