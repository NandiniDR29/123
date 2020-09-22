/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationPolicyTabMetaData;
import org.openqa.selenium.By;

public class PolicyInformationPolicyTab extends DefaultTab {

    public static TextBox textBoxclaimWithoutPolicyNumber = new TextBox(By.id("policyDataGatherForm:sedit_ClaimsPolicy_policyNumber"));

    public PolicyInformationPolicyTab() {
        super(PolicyInformationPolicyTabMetaData.class);
    }

    @Override
    public Tab navigateToTab() {
        NavigationPage.toTreeTab(getTabName());
        return this;
    }

    @Override
    public String getTabName(){
        return "Policy Information";
    }
}
