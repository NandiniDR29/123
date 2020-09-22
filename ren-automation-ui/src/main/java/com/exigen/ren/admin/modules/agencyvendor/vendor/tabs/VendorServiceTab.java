/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.vendor.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.admin.modules.agencyvendor.AgencyVendorDefaultTab;
import com.exigen.ren.admin.modules.agencyvendor.vendor.metadata.VendorServiceMetaData;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class VendorServiceTab extends AgencyVendorDefaultTab {
    public static Button buttonSubmit = new Button(By.id("vendorServicesForm:okBtnService"));

    public VendorServiceTab() {
        super(VendorServiceMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonSubmit.click();
        return this;
    }
}
