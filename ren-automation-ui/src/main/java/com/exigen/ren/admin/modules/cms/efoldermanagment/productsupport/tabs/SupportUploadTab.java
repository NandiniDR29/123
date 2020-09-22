/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.efoldermanagment.productsupport.tabs;

import com.exigen.ipb.eisa.base.application.Application;
import com.exigen.ren.admin.modules.cms.efoldermanagment.productsupport.metadata.SupportUploadMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;

public class SupportUploadTab extends DefaultTab {
    public SupportUploadTab() {
        super(SupportUploadMetaData.class);
    }

    @Override
    public Tab submitTab() {
        Application.wait(1000);
        buttonOk.click();
        return this;
    }
}
