/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory;

import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFLink;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.waiters.Waiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

/**
 * @author ikisly
 */
public abstract class DefaultTab {
    protected static String tabLocatorTemplate = "//a[.='%s' and contains(@id, 'nav:routes')]%s";
    protected PFLink linkTab = new PFLink(By.xpath(String.format(tabLocatorTemplate, tabName(), "")));
    protected PFButton buttonNavMenu = new PFButton(By.xpath("//button[contains(@class,'hamburger pf-button')]"));
    protected StaticElement formNavigation = new StaticElement(By.xpath("//form[@id='nav']"));
    protected static PFButton buttonOpenMenu = new PFButton(By.xpath("//div[@role='button' and @class='product-wrapper waves-effect']"));
    protected static PFButton buttonClose = new PFButton(By.className("close"));
    protected static PFLink linkReturnToSearch = new PFLink(By.id("nav:back"));
    protected StaticElement errorForm = new StaticElement(By.xpath("//div[@class='modal fade in']//div[@class='modal-content']"));

    public <T extends DefaultTab> T activateNavigation() {
        if (!formNavigation.isPresent()) {
            buttonNavMenu.waitForAccessible(60000);
            buttonNavMenu.click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
            formNavigation.waitForAccessible(60000);
        }
        return (T) this;
    }

    protected abstract String tabName();

    public <T extends DefaultTab> T open() {
        activateNavigation();
        linkTab.click(tabLoadingWaiter());
        return (T) this;
    }

    public void returnToSearch() {
        activateNavigation();
        buttonOpenMenu.click();
        linkReturnToSearch.click();
    }

    protected Waiter tabLoadingWaiter() {
        return Waiters.DEFAULT;
    }

    public boolean isErrorExist() {
        return new StaticElement(errorForm, By.xpath(".//h4[.='Error']")).isPresent();
    }

    public void closeError() {
        new PFButton(errorForm, By.xpath(".//button[.='Close']")).click();
    }
}
