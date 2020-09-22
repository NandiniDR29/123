/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData;
import org.openqa.selenium.By;

public class LossEventTab extends DefaultTab {
    public static StaticElement labelPartyName = new StaticElement(By.id("policyDataGatherForm:sedit_ClaimsInitParty-override_party_oid_label"));
    public static StaticElement textBoxPartyName = new StaticElement(By.id("policyDataGatherForm:partyDisplay_ClaimsInitParty"));
    public static StaticElement claimsHeaderPanel = new StaticElement(By.id("claimsDatagatherForm:header_panel"));
    public static Button buttonCancel = new Button(By.id("topCancelLink"));

    public LossEventTab() {
        super(LossEventTabMetaData.class);
    }
}
