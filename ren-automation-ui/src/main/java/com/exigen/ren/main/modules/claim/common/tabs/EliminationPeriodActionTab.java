package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.common.metadata.EliminationPeriodActionTabMetaData;

public class EliminationPeriodActionTab extends ActionTab {
    public EliminationPeriodActionTab() {
        super(EliminationPeriodActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonSaveAndExit.click();
        return this;
    }
}