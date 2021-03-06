/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common.tabs.master;

import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.policy.common.metadata.master.StartPremiumAdjustmentActionTabMetaData;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class StartPremiumAdjustmentActionTab extends ActionTab {
    public StartPremiumAdjustmentActionTab() {
        super(StartPremiumAdjustmentActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonOk.click();
        Page.dialogConfirmation.confirm();
        return this;
    }
}
