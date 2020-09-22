/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_dn.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.BenefitBenefitDetailsTabMetaData;
import org.openqa.selenium.By;

public class BenefitBenefitDetailsTab extends DefaultTab {

    public static ComboBox comboboxDamage = new ComboBox(By.id("policyDataGatherForm:damageTypes_ClaimsDamageManager"));
    public static Link linkAddComponent = new Link(By.id("policyDataGatherForm:addComponentButton_ClaimsDamageManager"));
    public static Button buttonAddParty = new Button(By.xpath("//input[@id='policyDataGatherForm:addPropertyDamageParty' or @id='policyDataGatherForm:addAutoOccupant']"));

    public BenefitBenefitDetailsTab() {
        super(BenefitBenefitDetailsTabMetaData.class);
    }

}
