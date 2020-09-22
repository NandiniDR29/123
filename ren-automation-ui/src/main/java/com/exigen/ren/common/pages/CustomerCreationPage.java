/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common.pages;

import com.exigen.istf.webdriver.controls.composite.table.Table;
import org.openqa.selenium.By;

public class CustomerCreationPage extends Page {

    public static Table tableAdressDetails = new Table(By.xpath("//*[@id='crmForm:editAddressSection_0']//table[@class='pfForm pfMultiColumn']"));
}
