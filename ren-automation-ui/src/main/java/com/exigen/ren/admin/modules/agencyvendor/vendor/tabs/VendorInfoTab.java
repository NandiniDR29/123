/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.vendor.tabs;

import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.agencyvendor.AgencyVendorDefaultTab;
import com.exigen.ren.admin.modules.agencyvendor.vendor.metadata.VendorInfoMetaData;
import org.openqa.selenium.By;

public class VendorInfoTab extends AgencyVendorDefaultTab {
    public static Table tableChildVendor = new Table(By.id("vendorProfileForm:childVendorTableId"));

    public VendorInfoTab() {
        super(VendorInfoMetaData.class);
    }
}
