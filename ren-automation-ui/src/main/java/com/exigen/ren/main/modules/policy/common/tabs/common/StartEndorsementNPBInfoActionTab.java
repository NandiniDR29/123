package com.exigen.ren.main.modules.policy.common.tabs.common;

import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementNPBInfoActionTabMetaData;

public class StartEndorsementNPBInfoActionTab extends ActionTab {
    public StartEndorsementNPBInfoActionTab() {
        super(StartEndorsementNPBInfoActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        return this;
    }
}
