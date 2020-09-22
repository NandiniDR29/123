package com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs;

import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.UpdateProcedureCodePlanDefinitionTabMetaData;

public class UpdateProcedureCodePlanDefinitionTab extends ActionTab {
    public UpdateProcedureCodePlanDefinitionTab() {
        super(UpdateProcedureCodePlanDefinitionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        Tab.buttonOk.click();
        return this;
    }
}
