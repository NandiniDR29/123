/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.customer.metadata.OpportunityActionTabMetaData;
import org.openqa.selenium.By;

public class OpportunityActionTab extends ActionTab {
    public static Table tableOpportunity = new Table(By.id("opportunityForm:opportunityTable"));
    public static Button buttonCreateNewOpportunity = new Button(By.xpath("//a[.='Create New Opportunity']"));
    public static Button buttonAddOpportunity = new Button(By.id("editOpportunity:addProductButton"));
    public static Button buttonStartNewQuoteForOpportunity = new Button(By.id("editOpportunity:QUOTEstartBtn"));

    public OpportunityActionTab() {
        super(OpportunityActionTabMetaData.class);
    }
}
