/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.ipb.eisa.controls.dialog.DialogSingleSelector;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class OpportunityActionTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> DESCRIPTION = declare("Description", TextBox.class);
    public static final AssetDescriptor<ComboBox> CHANNEL = declare("Channel", ComboBox.class);
    public static final AssetDescriptor<ComboBox> LIKELIHOOD = declare("Likelihood", ComboBox.class);
    public static final AssetDescriptor<TextBox> POTENTIAL = declare("Potential", TextBox.class);
    public static final AssetDescriptor<TextBox> EFFECTIVE_DATE = declare("Effective Date", TextBox.class);
    public static final AssetDescriptor<StaticElement> STATUS = declare("Status", StaticElement.class);
    public static final AssetDescriptor<Button> ASSIGN_REFERRAL = declare("Assign Referral", Button.class, By.id("editOpportunity:assignReferral"));
    public static final AssetDescriptor<DialogSingleSelector> REFERRED_BY = declare("Referred by", DialogSingleSelector.class, SearchReferralMetaData.class);
    public static final AssetDescriptor<Button> ASSIGN_OWNER = declare("Assign Owner", Button.class, By.id("editOpportunity:owner"));
    public static final AssetDescriptor<DialogAssetList> OPPORTUNITY_OWNER = declare("Opportunity Owner", DialogAssetList.class, SearchOwnerMetaData.class);

}
