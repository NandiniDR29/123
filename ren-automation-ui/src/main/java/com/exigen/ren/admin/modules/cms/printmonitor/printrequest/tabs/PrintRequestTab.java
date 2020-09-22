/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.cms.printmonitor.printrequest.tabs;

import com.exigen.ipb.eisa.controls.dialog.DialogAssetList;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.admin.modules.cms.printmonitor.printrequest.metadata.PrintRequestMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class PrintRequestTab extends DefaultTab {
    public static DialogAssetList rescheduleTime = new DialogAssetList(DialogAssetList.getDefaultPopupParentLocator(), PrintRequestMetaData.RescheduleTimeDialog.class);

    public static Button buttonRefresh = new Button(By.id("criteriaForm:findPrintRequests"));
    public static Button buttonCancelSelected = new Button(By.id("selectionForm:cancelPrintRequests"));
    public static Button buttonPauseSelected = new Button(By.id("selectionForm:pausePrintRequests"));
    public static Button buttonRescheduleSelected = new Button(By.id("selectionForm:reschedulePrintRequests"));
    public static Button buttonConfirmRescheduleDialog = new Button(By.id("selectionForm:applyReschedulePrintRequests"));

    public PrintRequestTab() {
        super(PrintRequestMetaData.class);
    }

    @Override
    public Tab submitTab() {
        return this;
    }
}
