/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.general.catastrophe.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.general.catastrophe.metadata.CatastropheMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;

public class CatastropheTab extends DefaultTab {

    public CatastropheTab() {
        super(CatastropheMetaData.class);
    }


    @Override
    public Tab fillTab(TestData td) {
        super.fillTab(td);
        return this;
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
