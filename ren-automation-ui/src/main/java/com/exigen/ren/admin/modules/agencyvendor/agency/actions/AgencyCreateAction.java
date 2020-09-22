/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.agency.actions;

import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.*;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;

public class AgencyCreateAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(AgencyInfoTab.class)
            .registerTab(ContactInfoTab.class)
            .registerTab(BankingDetailsTab.class)
            .registerTab(SupportTeamTab.class)
            .registerTab(UsersTab.class)
            .registerTab(ChildrenTab.class)
            .registerTab(CommissionsInfoTab.class)
            .registerTab(LicenseInfoTab.class)
            .build();

    @Override
    public String getName() {
        return "Create Agency Action";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
