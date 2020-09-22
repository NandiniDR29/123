/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.metadata.CompleteNotificationTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.COMPLETE_NOTIFICATION;

public class CompleteNotificationTab extends ActionTab {

    public static Button buttonSubmitClaim = new Button(By.id("policyDataGatherForm:claimSubmitBtn_ClaimsSubmitAction"));
    public static Button buttonOpenClaim = new Button(By.id("policyDataGatherForm:claimOpenBtn_ClaimsOpenAction"));

    public CompleteNotificationTab() {
        super(CompleteNotificationTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonSaveAndExit.click();
        return this;
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return COMPLETE_NOTIFICATION.get();
    }

}
