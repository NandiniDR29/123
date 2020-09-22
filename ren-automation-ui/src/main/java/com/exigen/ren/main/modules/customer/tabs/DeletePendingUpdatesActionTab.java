/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.tabs;

import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.customer.metadata.DeletePendingUpdatesActionTabMetaData;

public class DeletePendingUpdatesActionTab extends ActionTab {
    public DeletePendingUpdatesActionTab() {
        super(DeletePendingUpdatesActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        Page.dialogConfirmation.confirm();
        return this;
    }
}
