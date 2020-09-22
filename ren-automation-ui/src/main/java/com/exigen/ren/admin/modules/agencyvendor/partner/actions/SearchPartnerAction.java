/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.agencyvendor.partner.actions;

import com.exigen.ren.admin.modules.agencyvendor.partner.pages.PartnerPage;
import com.exigen.ren.admin.modules.agencyvendor.partner.tabs.SearchPartnerTab;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;

public class SearchPartnerAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(SearchPartnerTab.class)
            .build();

    @Override
    public String getName() {
        return "Search Partner Action";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        PartnerPage.buttonSearch.click();
        return this;
    }
}
