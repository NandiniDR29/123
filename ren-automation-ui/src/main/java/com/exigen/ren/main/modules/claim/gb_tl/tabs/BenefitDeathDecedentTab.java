/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_tl.tabs;

import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.main.modules.claim.gb_tl.metadata.BenefitDeathDecedentTabMetaData;
import org.openqa.selenium.By;

public class BenefitDeathDecedentTab extends DefaultTab {
    public static StaticElement lableAddressLine1 = new StaticElement(By.id("policyDataGatherForm:sedit_DeathPartyAddressContact_addressContact_address_addressLine1"));

    public BenefitDeathDecedentTab() {
        super(BenefitDeathDecedentTabMetaData.class);
    }
}
