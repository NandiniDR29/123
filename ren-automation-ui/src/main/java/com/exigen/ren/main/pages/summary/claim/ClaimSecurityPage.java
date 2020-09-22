/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.pages.summary.claim;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.main.pages.summary.SummaryPage;
import org.openqa.selenium.By;

public class ClaimSecurityPage extends SummaryPage {

    public static Table tableAccessControlList = new Table(By.xpath("//div[@id='policyDataGatherForm:aclEntriesTable']//table"));
    public static Link linkAddAdjuster = new Link(By.id("policyDataGatherForm:addAdjuster"));

    public static TextBox textBoxFirstName = new TextBox(By.id("adjusterSearchForm:criteriaTable_firstName"));
    public static Button buttonSearch = new Button(By.id("adjusterSearchForm:search"));
    public static Link linkResultName = new Link(By.id("adjusterSearchForm:results_internal:0:nameLink"));
    public static Link linkGrantedUsers = new Link(By.xpath("//div[@id='policyDataGatherForm:grantedUsersHeaderPanel:header']//div[.='Granted Users' and @class='rf-cp-lbl-colps']"));
    public static Table tableGrantedUsers = new Table(By.xpath("//div[@id='policyDataGatherForm:grantedUsersTable']//table"));
}
