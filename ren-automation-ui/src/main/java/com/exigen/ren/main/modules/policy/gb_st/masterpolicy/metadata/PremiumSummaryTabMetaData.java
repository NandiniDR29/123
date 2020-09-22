/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class PremiumSummaryTabMetaData extends MetaData {
    public static final AssetDescriptor<TextBox> CREDIBILITY_FACTOR = declare("Credibility Factor", TextBox.class);
    public static final AssetDescriptor<ComboBox> SELECT_RATING_CENSUS = declare("Select Rating Census", ComboBox.class);
    public static final AssetDescriptor<Button> APPLY = declare("Apply", Button.class, By.id("policyDataGatherForm:applyRatingCensusBtn"));
    public static final AssetDescriptor<Button> REMOVE = declare("Remove", Button.class, By.id("policyDataGatherForm:removeRatingCensusBtn"));
    public static final StaticElement APPLIED_RATING_CENSUS = new StaticElement(By.id("policyDataGatherForm:appliedCensusFileIdshow"));
    public static final AssetDescriptor<AssetList> RATE_SECTION = declare("Rate", AssetList.class, PremiumSummaryTabMetaData.RateMetaData.class);

    public static final class RateMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> RATE_REQUEST_DATE = declare("Rate Request Date", TextBox.class);
    }

}
