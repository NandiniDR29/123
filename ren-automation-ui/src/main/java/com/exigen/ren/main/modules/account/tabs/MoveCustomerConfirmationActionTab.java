/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.account.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.account.metadata.MoveCustomerConfirmationActionTabMetaData;
import org.openqa.selenium.By;

public class MoveCustomerConfirmationActionTab extends ActionTab {

    public static Button buttonMove = new Button(By.id("searchForm:moveBtn1"));

    public MoveCustomerConfirmationActionTab() {
        super(MoveCustomerConfirmationActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonMove.click();
        Page.dialogConfirmation.confirm();
        return this;
    }
}
