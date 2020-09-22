/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer.actions;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.enums.DocGenConstants;
import com.exigen.ren.main.modules.customer.tabs.GenerateOnDemandDocumentTab;

public class GenerateOnDemandDocumentAction implements Action {
    private static String folderName;
    private Workspace workspace = new Workspace.Builder()
            .registerTab(GenerateOnDemandDocumentTab.class)
            .build();

    @Override
    public String getName() {
        return "Generate On Demand Document";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    public Action perform(TestData testData, int row) {
        start(row);
        getWorkspace().fill(testData);
        return this;
    }

    public Action start(int row) {
        Action.super.start();
        folderName = GenerateOnDemandDocumentTab.tableDocumentTemplates.getRow(row).getCell(6).getValue();
        GenerateOnDemandDocumentTab.tableDocumentTemplates.getRow(row).getCell(DocGenConstants.DocGendDcumentTemplatesTable.TEMPLATE_ID).controls.links.getFirst().click();
        return this;
    }

    public String getFolderName() {
        return folderName;
    }
}
