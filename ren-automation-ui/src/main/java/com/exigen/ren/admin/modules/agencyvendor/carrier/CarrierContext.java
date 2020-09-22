/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.carrier;

import com.exigen.ren.admin.modules.agencyvendor.carrier.tabs.BankingDetailsTab;
import com.exigen.ren.admin.modules.agencyvendor.carrier.tabs.CarrierInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.carrier.tabs.CompaniesTab;
import com.exigen.ren.common.AutomationContext;

public interface CarrierContext {
    Carrier carrier = AutomationContext.getService(Carrier.class);
    BankingDetailsTab bankingDetailsTab = carrier.getDefaultWorkspace().getTab(BankingDetailsTab.class);
    CompaniesTab companiesTab = carrier.getDefaultWorkspace().getTab(CompaniesTab.class);
    CarrierInfoTab carrierInfoTab = carrier.getDefaultWorkspace().getTab(CarrierInfoTab.class);
}
