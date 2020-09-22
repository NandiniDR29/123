package com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection.tabs.ApplyManualCommissionCorrectionTab;
import com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection.tabs.CommissionCorrectionSearchTab;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicy;

public interface GBCommissionCorrectionContext {

    GBCommissionCorrection gbCommissionCorrection = AutomationContext.getService(GBCommissionCorrection.class);
    CommissionCorrectionSearchTab gbCommissionCorrectionSearchTab = gbCommissionCorrection.getDefaultWorkspace().getTab(CommissionCorrectionSearchTab.class);
    ApplyManualCommissionCorrectionTab gbCommissionCorrectionTab = gbCommissionCorrection.getDefaultWorkspace().getTab(ApplyManualCommissionCorrectionTab.class);

    default GroupBenefitsMasterPolicy getPolicy() {
        throw new UnsupportedOperationException("Not implemented");
    }

    default TestData getPolicyTestData() {
        throw new UnsupportedOperationException("Not implemented");
    }

    default TestData getCommissionTestData() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
