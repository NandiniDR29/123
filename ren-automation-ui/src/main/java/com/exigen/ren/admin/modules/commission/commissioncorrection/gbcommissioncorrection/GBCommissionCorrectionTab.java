package com.exigen.ren.admin.modules.commission.commissioncorrection.gbcommissioncorrection;

import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;

public class GBCommissionCorrectionTab extends DefaultTab {

    public GBCommissionCorrectionTab(Class<? extends MetaData> mdClass) {
        super(mdClass);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
