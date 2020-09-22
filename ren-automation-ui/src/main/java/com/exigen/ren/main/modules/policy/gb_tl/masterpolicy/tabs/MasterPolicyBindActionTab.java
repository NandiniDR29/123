/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs;

import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.MasterPolicyBindActionTabMetaData;

public class MasterPolicyBindActionTab extends ActionTab {
    public MasterPolicyBindActionTab() {
        super(MasterPolicyBindActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        Waiters.AJAX.then(Waiters.SLEEP(500)).go();
        buttonNext.click(doubleWaiter);
        return this;
    }
}
