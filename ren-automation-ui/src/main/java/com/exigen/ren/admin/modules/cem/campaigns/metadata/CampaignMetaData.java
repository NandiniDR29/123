/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.campaigns.metadata;

import com.exigen.ipb.eisa.controls.AssociateTextBox;
import com.exigen.ipb.eisa.controls.cem.composite.capmaigns.CampaignProductsAssetList;
import com.exigen.ipb.eisa.controls.cem.composite.capmaigns.MarketChannelsAssetList;
import com.exigen.istf.webdriver.controls.CheckBox;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class CampaignMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> CAMPAIGN_NAME = declare("Campaign Name", TextBox.class);
    public static final AssetDescriptor<TextBox> PROMOTION_CODE = declare("Promotion Code", TextBox.class);
    public static final AssetDescriptor<TextBox> BUDGET_COST = declare("Budget Cost", TextBox.class);
    public static final AssetDescriptor<TextBox> ACTUAL_COST = declare("Actual Cost", TextBox.class);
    public static final AssetDescriptor<TextBox> EXPECTED_REVENUE = declare("Expected Revenue", TextBox.class);
    public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
    public static final AssetDescriptor<ComboBox> RELATED_WITH = declare("Related with", ComboBox.class);
    public static final AssetDescriptor<AssociateTextBox> ID = declare("ID", AssociateTextBox.class, By.id("parentId"));
    public static final AssetDescriptor<AssociateTextBox> NAME = declare("Name", AssociateTextBox.class, By.id("parentName"));
    public static final AssetDescriptor<ComboBox> CUSTOMER_TYPE = declare("Customer Type", ComboBox.class);
    public static final AssetDescriptor<TextBox> START_DATE = declare("Start date", TextBox.class, By.id("startDateInputDate"));
    public static final AssetDescriptor<TextBox> END_DATE = declare("End date", TextBox.class, By.id("endDateInputDate"));
    public static final AssetDescriptor<TextBox> DURATION = declare("Duration (days)", TextBox.class, By.id("duration"));
    public static final AssetDescriptor<CheckBox> START_CAMPAIGN_AUTOMATICALY = declare("Start campaign automatically", CheckBox.class, By.id("autoStart"));
    public static final AssetDescriptor<CheckBox> DIVERT_CAMPAIGN_NOTIFICATION = declare("Divert Campaign Notification?", CheckBox.class);
    public static final AssetDescriptor<MarketChannelsAssetList> MARKETING_CHANELS = declare("Marketing Channels", MarketChannelsAssetList.class, MarketingChannelsSection.class);
    public static final AssetDescriptor<CampaignProductsAssetList> CAMPAIGN_PRODUCTS = declare("Campaign Products", CampaignProductsAssetList.class, CampaignProductsSection.class);

    public static final class MarketingChannelsSection extends MetaData {
        public static final AssetDescriptor<ComboBox> CAMPAIGN_CHANEL = declare("Campaign Channel", ComboBox.class, By.id("selectChannel"));
        public static final AssetDescriptor<ComboBox> SUB_CHANEL = declare("Sub Channel", ComboBox.class, By.id("selectSubChannel"));
        public static final AssetDescriptor<ComboBox> MATERIAL = declare("Material", ComboBox.class, By.id("selectMaterial"));
    }

    public static final class CampaignProductsSection extends MetaData {
        public static final AssetDescriptor<ComboBox> LINE_OF_BUSINESS = declare("Line of Business", ComboBox.class, By.id("selectLOB"));
        public static final AssetDescriptor<ComboBox> PRODUCT_NAME = declare("Product Name", ComboBox.class, By.id("selectProduct"));
        public static final AssetDescriptor<ComboBox> RISK_ITEM = declare("Risk Item", ComboBox.class, By.id("productRiskCd"));
    }
}
