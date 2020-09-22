/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.account.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.account.metadata.CommunicationActionTabMetaData;
import org.openqa.selenium.By;

public class CommunicationActionTab extends ActionTab {
    public static Table tableCommunications = new Table(By.id("communicationsSearchForm:searchTable")).applyConfiguration("Communication");
    public static Table tableCommunicationInfoDetails = new Table(By.id("communicationsSearchForm:searchTable:0:details:0:communicationInfoDetails"));
    public static Button buttonStartNewCommunication = new Button(By.xpath("//a[.='Start New Communication']"));

    public CommunicationActionTab() {
        super(CommunicationActionTabMetaData.class);
    }
}
