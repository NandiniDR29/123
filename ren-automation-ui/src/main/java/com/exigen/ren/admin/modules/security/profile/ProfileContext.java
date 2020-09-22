/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.profile;

import com.exigen.ren.admin.modules.security.profile.tabs.AuthorityLevelsTab;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.admin.modules.security.profile.tabs.SearchProfileTab;
import com.exigen.ren.common.AutomationContext;

public interface ProfileContext {
    ProfileAgency profileAgency = AutomationContext.getService(ProfileAgency.class);
    ProfileCorporate profileCorporate = AutomationContext.getService(ProfileCorporate.class);
    ProfileVendor profileVendor = AutomationContext.getService(ProfileVendor.class);

    AuthorityLevelsTab authorityLevelsTab = profileCorporate.getDefaultWorkspace().getTab(AuthorityLevelsTab.class);
    GeneralProfileTab generalProfileTab = profileCorporate.getDefaultWorkspace().getTab(GeneralProfileTab.class);
    SearchProfileTab searchProfileTab = profileCorporate.search().getWorkspace().getTab(SearchProfileTab.class);

}
