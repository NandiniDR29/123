/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.actions;

import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.policy.common.actions.master.MasterPolicyDataGatherAction;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.*;

public class TermLifeInsuranceMasterPolicyDataGatherAction extends MasterPolicyDataGatherAction {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(PolicyInformationTab.class)
            .registerTab(EnrollmentTab.class)
            .registerTab(PlanDefinitionTab.class)
            .registerTab(ClassificationManagementTab.class)
            .registerTab(PremiumSummaryTab.class)
            .build();

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
