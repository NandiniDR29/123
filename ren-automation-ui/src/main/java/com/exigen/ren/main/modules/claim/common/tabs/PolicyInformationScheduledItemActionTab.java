/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationScheduledItemActionTabMetaData;
import org.openqa.selenium.By;

public class PolicyInformationScheduledItemActionTab extends ActionTab {
    public static Button buttonRemove= new Button(By.xpath("//input[@value='Remove']"));

    public PolicyInformationScheduledItemActionTab() {
        super(PolicyInformationScheduledItemActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        if(buttonRemove.isPresent()){
            buttonRemove.click();
            Page.dialogConfirmation.confirm();
            buttonNext.click();
        }
        return this;
    }
}
