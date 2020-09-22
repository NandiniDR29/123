/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_dn.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.TreatmentInformationServiceProviderTabMetaData;
import org.openqa.selenium.By;

public class TreatmentInformationServiceProviderTab extends DefaultTab {

    public static Button buttonAddClaimsServiceRequest = new Button(By.id("policyDataGatherForm:addClaimsServiceRequest"));
    public static Button buttonCreateVendor = new Button(By.xpath("//input[@value='Create Vendor']"));

    public TreatmentInformationServiceProviderTab() {
        super(TreatmentInformationServiceProviderTabMetaData.class);
    }

}
