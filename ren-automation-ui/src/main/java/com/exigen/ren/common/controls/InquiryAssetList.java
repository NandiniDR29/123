package com.exigen.ren.common.controls;

import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiter;
import org.openqa.selenium.By;

public class InquiryAssetList extends AssetList {
    public InquiryAssetList(By locator) {
        super(locator);
    }

    public InquiryAssetList(By locator, Class<? extends MetaData> metaDataClass) {
        super(locator, metaDataClass);
    }

    public InquiryAssetList(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
        super(parent, locator, metaDataClass);
    }

    protected void registerAsset(String assetName, Class<? extends BaseElement<?, ?>> controlClass, Waiter waiter, Class<? extends MetaData> metaClass, boolean hasParent, By assetLocator) {
        if (AbstractContainer.class.isAssignableFrom(controlClass)) {
            super.registerAsset(assetName, InquiryAssetList.class, waiter, metaClass, hasParent, assetLocator);
        } else {
            super.registerAsset(assetName, StaticElement.class, waiter, metaClass, hasParent, assetLocator);
        }
    }
}
