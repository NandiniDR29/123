/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.role;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.AutomationContext;

public interface RoleContext {
    RoleCorporate roleCorporate = AutomationContext.getService(RoleCorporate.class);
    RoleAgency roleAgency = AutomationContext.getService(RoleAgency.class);
    RoleVendor roleVendor = AutomationContext.getService(RoleVendor.class);

    GeneralRoleTab generalRoleTab = roleCorporate.getDefaultWorkspace().getTab(GeneralRoleTab.class);

    default TestData getCorporateTestData() {
        return TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/security/role/corporate");
    }
}
