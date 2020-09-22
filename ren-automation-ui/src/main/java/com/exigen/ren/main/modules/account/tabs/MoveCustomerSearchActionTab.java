/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.account.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.common.pages.SearchPage;
import com.exigen.ren.main.modules.account.metadata.MoveCustomerSearchActionTabMetaData;
import org.openqa.selenium.By;

public class MoveCustomerSearchActionTab extends ActionTab {

    public static Button buttonCreateNewAccount = new Button(By.xpath("//a[.='Create New Account']"));
    public static Table tableSearchResults = new Table(By.id("searchTable1Form:body_searchTable1"));
    public static Button buttonLastCustomerConfirmation = new Button(By.id("lastCustomerConfirmDialog_form:buttonYes"));

    public MoveCustomerSearchActionTab() {
        super(MoveCustomerSearchActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        SearchPage.buttonSearch.click();
        if (tableSearchResults.isPresent()) {
            tableSearchResults.getRow(1).getCell(1).controls.links.getFirst().click();
        } else if (buttonCreateNewAccount.isPresent()) {
            buttonCreateNewAccount.click();
            Page.dialogConfirmation.confirm();
            buttonLastCustomerConfirmation.click();
        }
        return this;
    }
}
