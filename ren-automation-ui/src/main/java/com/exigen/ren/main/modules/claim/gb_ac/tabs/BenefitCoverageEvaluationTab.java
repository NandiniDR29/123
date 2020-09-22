package com.exigen.ren.main.modules.claim.gb_ac.tabs;

import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitCoverageEvaluationTabMetaData;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimBenefitLeftMenu.*;

public class BenefitCoverageEvaluationTab extends DefaultTab {
    public BenefitCoverageEvaluationTab() {
        super(BenefitCoverageEvaluationTabMetaData.class);
    }


    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.expand(PREMIUM_WAIVER.get(), this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return COVERAGE_EVALUATION.get();
    }
}