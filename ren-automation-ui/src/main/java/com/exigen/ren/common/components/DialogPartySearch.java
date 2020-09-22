/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.components;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import org.openqa.selenium.By;

public class DialogPartySearch {
    public Button buttonCancel;
    public StaticElement labelMessage;
    public Table tableSearchResults;

    public DialogPartySearch(String dialogLocator) {
        buttonCancel = new Button(By.xpath(dialogLocator + "//input[@value = 'Cancel']"));
        labelMessage = new StaticElement(By.xpath(dialogLocator + "//div[contains(text(),'Party Search')]"));
        tableSearchResults = new Table(By.xpath(dialogLocator + "//table[@id = 'partySearchForm:body_partySearchResults']"));
    }

    public Link getFirstSearchResultLink() {
        return tableSearchResults.getRow(1).getCell(1).controls.links.get(1);
    }

    public StaticElement getFirstSearchResultAddressLine()
        {
            return tableSearchResults.getRow(1).getCell("Address");
    }
}
