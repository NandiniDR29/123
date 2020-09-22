/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.commissionbonus.tabs;

import com.exigen.ren.admin.modules.commission.commissionbonus.CommissionBonusDefaultTab;
import com.exigen.ren.admin.modules.commission.commissionbonus.metadata.CommissionBonusMetaData;
import com.exigen.ren.common.Tab;

public class CommissionBonusTab extends CommissionBonusDefaultTab {

    public CommissionBonusTab() {
        super(CommissionBonusMetaData.class);
    }

    @Override
    public Tab submitTab() {
        if (buttonSave.isPresent()) {
            buttonSave.click();
        } else {
            buttonNext.click();
        }
        return this;
    }
}
