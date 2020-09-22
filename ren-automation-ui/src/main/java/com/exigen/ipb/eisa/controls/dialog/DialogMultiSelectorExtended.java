package com.exigen.ipb.eisa.controls.dialog;

import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import org.openqa.selenium.By;

public class DialogMultiSelectorExtended extends DialogMultiSelector {
    public DialogMultiSelectorExtended(By locator) {
        super(locator);
    }

    public DialogMultiSelectorExtended(By locator, Class<? extends MetaData> metaDataClass) {
        super(locator, metaDataClass);
    }

    public DialogMultiSelectorExtended(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
        super(parent, locator, metaDataClass);
    }

    @Override
    protected void select() {
        this.tableSearchResults.getRow(1).getCell(1).click();
        (new Button(popupParent, By.xpath(".//input[@value = 'Add' or @value = 'Move to Selected' or @value = 'Select'] | .//button[@type='submit']//span[text()='Select']/parent::*"))).click();
    }
}