/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common.tabs.common;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.policy.common.metadata.common.IssueActionTabMetaData;
import org.openqa.selenium.By;

public class IssueActionTab extends ActionTab {
    public static Button buttonPurchase = new Button(By.xpath("//input[@value = 'Purchase' and not(@class = 'hidden') and not(contains(@style,'none'))]"));

    public IssueActionTab() {
        super(IssueActionTabMetaData.class);
    }
}
