/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.profile.tabs;

import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.admin.modules.security.SecurityDefaultTab;
import com.exigen.ren.admin.modules.security.profile.metadata.BankingDetailsMetaData;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class BankingDetailsTab extends SecurityDefaultTab {

    public static StaticElement errorExpirationDate = new StaticElement(By.id("userProfileForm:paymentEFT_effectiveTerm_expiration_error"));

    public BankingDetailsTab() {
        super(BankingDetailsMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
