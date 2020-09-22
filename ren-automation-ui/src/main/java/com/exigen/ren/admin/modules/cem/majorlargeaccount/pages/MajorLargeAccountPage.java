/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cem.majorlargeaccount.pages;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.webstudio.pages.AdminPage;
import org.openqa.selenium.By;

public class MajorLargeAccountPage extends AdminPage {
    public static Button buttonCreateNewMajorLargeAccount = new Button(By.id("createNewAccount"));
    public static Table tableMajorLargeAccount = new Table(By.id("searchForm:searchTable"));


}
