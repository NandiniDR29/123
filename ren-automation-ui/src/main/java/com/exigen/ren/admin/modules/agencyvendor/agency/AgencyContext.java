/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.agency;

import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.*;
import com.exigen.ren.admin.modules.agencyvendor.common.tabs.AgencyVendorSearchTab;
import com.exigen.ren.common.AutomationContext;

public interface AgencyContext {
    Agency agency = AutomationContext.getService(Agency.class);
    AgencyInfoTab agencyInfoTab = agency.getDefaultWorkspace().getTab(AgencyInfoTab.class);
    BankingDetailsTab bankingDetailsTab = agency.getDefaultWorkspace().getTab(BankingDetailsTab.class);
    ChildrenTab childrenTab = agency.getDefaultWorkspace().getTab(ChildrenTab.class);
    ContactInfoTab contactInfoTab = agency.getDefaultWorkspace().getTab(ContactInfoTab.class);
    SupportTeamTab supportTeamTab = agency.getDefaultWorkspace().getTab(SupportTeamTab.class);
    UsersTab usersTab = agency.getDefaultWorkspace().getTab(UsersTab.class);
    AgencyVendorSearchTab agencyVendorSearchTab = agency.search().getWorkspace().getTab(AgencyVendorSearchTab.class);
    LicenseInfoTab licenseInfoTab = agency.getDefaultWorkspace().getTab(LicenseInfoTab.class);
    CommissionsInfoTab commissionTab = agency.getDefaultWorkspace().getTab(CommissionsInfoTab.class);
}
