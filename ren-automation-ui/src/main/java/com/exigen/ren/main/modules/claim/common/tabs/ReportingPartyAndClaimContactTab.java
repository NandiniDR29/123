/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.main.modules.claim.common.metadata.ReportingPartyAndClaimContactTabMetaData;
import org.openqa.selenium.By;

public class ReportingPartyAndClaimContactTab extends DefaultTab {

    public static StaticElement labelClaimContactSection = new StaticElement(By.xpath("//label[@id='policyDataGatherForm:componentViewPanelHeaderLabel_MainContactParty' or @id='policyDataGatherForm:componentViewPanelHeaderLabel_PrecMainContactParty']"));
    public static StaticElement labelClaimContactAddressSection = new StaticElement(By.xpath("//label[@id='policyDataGatherForm:componentViewPanelHeaderLabel_MainContactPartyAddressContact' or @id='policyDataGatherForm:componentViewPanelHeaderLabel_PrecMainContactPartyAddressContact']"));

    public static Button buttonAddReportingPartyAddress = new Button(By.xpath("//input[@id='policyDataGatherForm:addPrecReportingPartyAddressContact' or @id='policyDataGatherForm:addReportingPartyAddressContact']"));

    public static Table tableReportingPartyAddress = new Table(By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListReportingPartyAddressContact']//table"));
    public static Button buttonRemoveReportingPartyAddress = new Button(By.xpath("//input[@id='policyDataGatherForm:eliminatePrecReportingPartyAddressContact' or @id='policyDataGatherForm:eliminateReportingPartyAddressContact']"));

    public ReportingPartyAndClaimContactTab() {
        super(ReportingPartyAndClaimContactTabMetaData.class);
    }
    
}
