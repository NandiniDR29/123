/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_dn.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.BenefitPatientTabMetaData;
import org.openqa.selenium.By;

public class BenefitPatientTab extends DefaultTab {

    public static Button buttonAdd = new Button(By.xpath("//input[@value='Add']"));

    public BenefitPatientTab() {
        super(BenefitPatientTabMetaData.class);
    }

}
