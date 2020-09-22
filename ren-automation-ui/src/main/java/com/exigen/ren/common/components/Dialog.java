/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.components;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.collection.Controls;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.metadata.SearchMetaData;
import org.openqa.selenium.By;

public class Dialog {

    private String locator;

    public Button buttonYes;
    public Button buttonNo;
    public Button buttonOk;
    public Button buttonCancel;
    public Button buttonClose;
    public Button buttonNext;
    public Button buttonProceed;
    public Button buttonRequestApproval;
    public Button buttonAdd;

    public StaticElement labelHeader;
    public StaticElement labelMessage;


    public Controls controls;

    private AssetList assetList;

    public Dialog(String dialogLocator) {
        locator = dialogLocator;
        controls = new Controls(By.xpath(dialogLocator));

        buttonYes = new Button(By.xpath(dialogLocator + "//*[@value='Yes' or @value='YES' or text()='Yes' or text()='YES']"));
        buttonNo = new Button(By.xpath(dialogLocator + "//*[@value='No' or @value='NO' or text()='No' or text()='NO']"));
        buttonOk = new Button(By.xpath(dialogLocator + "//*[@value='Ok' or @value='OK' or text()='Ok' or text()='OK']"));
        buttonCancel = new Button(By.xpath(dialogLocator + "//*[@value='Cancel' or @value='CANCEL' or text()='Cancel' or text()='CANCEL']"));
        buttonClose = new Button(By.xpath(dialogLocator + "//*[contains(@class,'ui-dialog-titlebar-close')]"));
        buttonNext = new Button(By.xpath(dialogLocator + "//*[@value='Next' or text()='Next']"));
        buttonProceed = new Button(By.xpath(dialogLocator + "//*[@value='Proceed' or text()='Proceed']"));
        buttonRequestApproval = new Button(By.xpath(dialogLocator + "//*[@value='Request Approval' or text()='Request Approval']"));
        buttonAdd = new Button(By.xpath(dialogLocator + "//*[@value='Add' or @value='ADD' or text()='Add' or text()='ADD']"));

        labelHeader = new StaticElement(By.xpath(dialogLocator + "//div[contains(@id, '_header_content')]"));
        labelMessage = new StaticElement(By.xpath(dialogLocator + "//div[contains(@id, '_content_scroller') or contains(@class,'content')]"
                + "//*[contains(@id, 'Message') or contains(@class, 'textBold') or contains(@class, 'message')]"));
        assetList = new AssetList(By.xpath(locator), SearchMetaData.DialogSearch.class);
    }

    public void confirm() {
        Button buttonOk =  new Button(By.xpath(locator + "//*[text()='Yes' or text()='YES' or text()='Ok' or text()='OK'  or text()='Confirm' or text()='Proceed' or text()='PROCEED' "
                + "or @value='Yes' or @value='YES' or @value='Ok' or @value='OK' or @value='Proceed' or @value='PROCEED']"));
        buttonOk.waitForAccessible(8000);
        buttonOk.click();
    }

    public void reject() {
        new Button(By.xpath(locator + "//*[@value='Cancel' or @value='CANCEL' or @value='No' or @value='NO' or text()='Cancel' or text()='CANCEL' or text()='No' or text()='NO']")).click();
    }

    public void fillAssetList(TestData testData, String nameOfAssetTypeRange) {
        getAssetList().setValue(testData.getTestData(nameOfAssetTypeRange));
    }

    public boolean isPresent() {
        return new StaticElement(By.xpath(locator)).isPresent();
    }

    public boolean isVisible() {
        return new StaticElement(By.xpath(locator)).isVisible();
    }

    public String getLocator() {
        return locator;
    }

    public AssetList getAssetList() {
        return assetList;
    }
}
