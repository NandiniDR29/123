/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.printmonitor.printdocs.tabs;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.admin.modules.cms.printmonitor.printdocs.metadata.PrintDocsMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class PrintDocsTab extends DefaultTab {
    public static DialogAssetList rescheduleTime = new DialogAssetList(DialogAssetList.getDefaultPopupParentLocator(), PrintDocsMetaData.RescheduleTimeDialog.class);

    public static Button buttonRefresh = new Button(By.id("criteriaForm:findPrintDocuments"));
    public static Button buttonCancelSelected = new Button(By.id("selectionForm:cancelPrintDocuments"));
    public static Button buttonRescheduleSelected = new Button(By.id("selectionForm:reschedulePrintDocuments"));
    public static Button buttonConfirmRescheduleDialog = new Button(By.id("selectionForm:applyReschedulePrintDocuments"));

    public PrintDocsTab() {
        super(PrintDocsMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
