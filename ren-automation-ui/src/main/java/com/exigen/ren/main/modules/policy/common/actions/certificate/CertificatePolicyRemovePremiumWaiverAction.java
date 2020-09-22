/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common.actions.certificate;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.policy.common.tabs.certificate.RemovePremiumWaiverActionTab;

public class CertificatePolicyRemovePremiumWaiverAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(RemovePremiumWaiverActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Remove Premium Waiver";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
