/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.platform;

import com.exigen.ipb.eisa.base.application.impl.pages.LoginPage;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.Platform_Admin;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestLogin extends BaseTest {

    @Test(groups = {PLATFORM, PLATFORM_ADMIN, TEAM_MERGE, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23719", component = Platform_Admin)
    public void testLogin() {
        mainApp().openSession();
        mainApp().getLogin().login(false);
        assertThat(LoginPage.linkLogout).isPresent();
    }
}
