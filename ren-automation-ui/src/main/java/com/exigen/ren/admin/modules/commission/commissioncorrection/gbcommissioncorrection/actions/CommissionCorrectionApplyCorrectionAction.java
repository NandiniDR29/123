package com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection.actions;

import com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection.metadata.CommissionCorrectionMetaData;
import com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection.tabs.ApplyManualCommissionCorrectionTab;
import com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection.tabs.CommissionCorrectionSearchTab;
import com.exigen.ren.admin.modules.commission.commissioncorrection.pages.CommissionCorrectionPage;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;

public class CommissionCorrectionApplyCorrectionAction implements Action {
    private Workspace workspace = new Workspace.Builder()
            .registerTab(CommissionCorrectionSearchTab.class)
            .registerTab(ApplyManualCommissionCorrectionTab.class)
            .build();

    public Action start(String policyNumber) {
        CommissionCorrectionPage.assetListSearchForm.getAsset(CommissionCorrectionMetaData.POLICY).setValue(policyNumber);
        Tab.buttonSearch.click();
        CommissionCorrectionPage.tableCommissionCorrection.getRow(CommissionCorrectionPage.CommissionCorrection.POLICY.getName(), policyNumber).getCell(1).controls.links.get("Apply Correction").click();
        return this;
    }

    public Action perform(String policyNumber) {
        start(policyNumber);
        CommissionCorrectionPage.tableCommissionCorrection.getRow(CommissionCorrectionPage.CommissionCorrection.POLICY.getName(), policyNumber).getCell(1).controls.links.get("Apply Correction").click();
        return submit();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        Tab.buttonSave.click();
        return this;
    }
}
