/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.commission.common.tabs;

import com.exigen.ren.admin.modules.commission.common.metadata.CommissionSearchTabMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;

public class CommissionSearchTab extends DefaultTab {
    public CommissionSearchTab() {
        super(CommissionSearchTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
