/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.vendor;

import com.exigen.ren.admin.modules.agencyvendor.vendor.tabs.ContactInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.vendor.tabs.VendorInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.vendor.tabs.VendorServiceTab;
import com.exigen.ren.common.AutomationContext;

public interface VendorContext {
    Vendor vendor = AutomationContext.getService(Vendor.class);
    ContactInfoTab contactInfoTab = vendor.getDefaultWorkspace().getTab(ContactInfoTab.class);
    VendorInfoTab vendorInfoTab = vendor.getDefaultWorkspace().getTab(VendorInfoTab.class);
    VendorServiceTab vendorServiceTab = vendor.getDefaultWorkspace().getTab(VendorServiceTab.class);

}
