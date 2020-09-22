/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.partner;

import com.exigen.ren.admin.modules.agencyvendor.partner.tabs.PartnerTab;
import com.exigen.ren.common.AutomationContext;

public interface PartnerContext {
    Partner partner = AutomationContext.getService(Partner.class);
    PartnerTab partnerTab = partner.getDefaultWorkspace().getTab(PartnerTab.class);
}
