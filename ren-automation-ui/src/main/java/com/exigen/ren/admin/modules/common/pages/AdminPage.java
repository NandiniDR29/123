/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.common.pages;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class AdminPage extends Page {
    public static Button buttonSearch = new Button(By.xpath("//input[@value='Search' and not(ancestor::div[contains(@style,'visibility: hidden')]) and not(contains(@style, 'none'))]"));
    public static Button buttonClear = new Button(By.xpath("//input[@value='Clear' and not(ancestor::div[contains(@style,'visibility: hidden')]) and not(contains(@style, 'none'))]"));
}
