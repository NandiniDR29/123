package com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.StartRateGuaranteeRenewalTab;

public class GroupDentalMasterPolicyRateGuaranteeRenewAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(StartRateGuaranteeRenewalTab.class)
            .build();

    @Override
    public String getName() {
        return "Rate Guarantee Renew";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
