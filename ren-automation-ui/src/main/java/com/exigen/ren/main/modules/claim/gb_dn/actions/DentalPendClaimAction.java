package com.exigen.ren.main.modules.claim.gb_dn.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.PendClaimActionTabMetData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.PendClaimActionTab;

import static com.exigen.ren.main.modules.claim.gb_dn.tabs.PendClaimActionTab.buttonPendClaim;

public class DentalPendClaimAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(PendClaimActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Pend Claim";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        buttonPendClaim.click();
        return this;
    }

    public Action perform(String reason) {
        start();
        getWorkspace().getTab(PendClaimActionTab.class).getAssetList().getAsset(PendClaimActionTabMetData.REASON).setValue(reason);
        submit();
        return this;
    }
}
