/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.security.profile.tabs;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.admin.modules.security.SecurityDefaultTab;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.SecurityProfileSubTabMenu.AUTHORITY_LEVELS;

public class AuthorityLevelsTab extends SecurityDefaultTab {
    public static Button buttonSaveNewAuthority = new Button(By.id("userProfileForm:saveAuthority"));

    public static Table tableDefaultAuthorityLevels = new Table(By.id("userProfileForm:defaultAuthorityLevelsTable"));
    public static Table tableUserAuthorityLevels = new Table(By.id("userProfileForm:authorityLevelsTable"));

    public AuthorityLevelsTab() {
        super(AuthorityLevelsMetaData.class);
    }

    @Override
    public Tab submitTab() {
        if (buttonSaveNewAuthority.isPresent() && buttonSaveNewAuthority.isEnabled()) {
            buttonSaveNewAuthority.click();
        }
        return this;
    }

    @Override
    public Tab navigate() {
        NavigationPage.toSubTab(this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return AUTHORITY_LEVELS.get();
    }
}
