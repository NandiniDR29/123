/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor;

import com.exigen.istf.data.TestData;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.admin.modules.IAdmin;
import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.SupportTeamTab;
import com.exigen.ren.admin.modules.agencyvendor.common.actions.AgencyVendorSearchAction;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;

public interface IAgencyVendor extends IAdmin {
    default void search(TestData td) {
        search().perform(td);
    }

    @Override
    default void create(TestData td) {
        initiate();
        getDefaultWorkspace().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.AGENCY_VENDOR);
        SupportTeamTab.buttonDone.click();
        LOGGER.info("Created " + entity);
    }

    void initiate();

    Action update();

    default Action search() {
        return AutomationContext.getAction(AgencyVendorSearchAction.class);
    }
}
