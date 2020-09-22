/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.role;

import com.exigen.istf.data.TestData;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.admin.modules.security.role.pages.RolePage;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;

public class RoleCorporate extends Role {
    private static final RoleType ROLE_TYPE = RoleType.CORPORATE;

    @Override
    public TestData defaultTestData() {
        return TestDataProvider.getDefaultTestDataProvider().get("modules/platform/admin/security/role/corporate");
    }

    public void initiate() {
        navigate();
        RolePage.assetListSearchForm.getAsset(RolePage.RoleSearch.BUSINESS_DOMAIN).setValue(ROLE_TYPE.get());
        GeneralRoleTab.buttonAddNewRole.click();
    }

    @Override
    public void create(TestData td) {
        initiate(ROLE_TYPE);
        getDefaultWorkspace().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.ROLE);
        GeneralRoleTab.buttonSave.click();
        LOGGER.info("Created Corporate Role " + entity);
    }
}
