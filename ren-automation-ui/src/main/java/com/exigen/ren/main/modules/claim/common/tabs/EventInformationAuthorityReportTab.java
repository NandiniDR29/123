/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.common.metadata.EventInformationAuthorityReportTabMetaData;
import org.openqa.selenium.By;

public class EventInformationAuthorityReportTab extends ActionTab {
    public static final Button buttonAddClaimsReported = new Button(By.id("policyDataGatherForm:addClaimsReported"));
    public static final Button buttonAddAuthority = new Button(By.xpath("//*[@id='policyDataGatherForm:addAuthority' or @id='policyDataGatherForm:addPrecAuthority']"));

    public EventInformationAuthorityReportTab() {
        super(EventInformationAuthorityReportTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }

    @Override
    public Tab fillTab(TestData td) {
        if (td.containsKey(getMetaKey()) && !isEmpty(td)) {
            buttonAddClaimsReported.click();
            buttonAddAuthority.click();
            assetList.fill(td);
        }
        return this;
    }
}
