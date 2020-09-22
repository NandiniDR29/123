/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory.pages;

import com.exigen.ipb.eisa.base.application.Application;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class CommonProductFactoryPage extends Page {
    public static PFButton buttonNavMenu = new PFButton(By.xpath("//button[contains(@class,'hamburger pf-button')]"));
    public static StaticElement formNavigation = new StaticElement(By.xpath("//form[@id='nav']"));


    public static void activateNavigation() {
        if (!formNavigation.isPresent()) {
            buttonNavMenu.waitForAccessible(60000);
            Application.wait(2000);
            buttonNavMenu.click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
            formNavigation.waitForAccessible(60000);
        }
    }
}
