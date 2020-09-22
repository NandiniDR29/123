/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.mywork.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.mywork.metadata.UpdateMyProfileActionTabMetaData;
import org.openqa.selenium.By;

public class UpdateMyProfileActionTab extends ActionTab {
    public static Button buttonSave = new Button(By.id("generalProfileInfoForm:saveProfileBtn"));
    public static Button buttonCancel = new Button(By.id("generalProfileInfoForm:cancelProfileBtn"));

    public UpdateMyProfileActionTab() {
        super(UpdateMyProfileActionTabMetaData.class);
    }
}
