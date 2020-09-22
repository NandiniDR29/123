/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationDependentDependentCoverageTabMetaData;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.DEPENDENT_COVERAGE;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.POLICY_INFORMATION;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationDependentDependentCoverageTabMetaData.COVERAGE_NAME;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationDependentDependentCoverageTabMetaData.PLAN;

public class PolicyInformationDependentDependentCoverageTab extends DefaultTab {

    protected static final Logger LOGGER = LoggerFactory.getLogger(PolicyInformationParticipantParticipantCoverageTab.class);
    public static Button buttonAddCoverage = new Button(By.xpath("//input[@value='Add Coverage']"));

    public PolicyInformationDependentDependentCoverageTab() {
        super(PolicyInformationDependentDependentCoverageTabMetaData.class);
    }

    @Override
    public Tab fillTab(TestData td) {
        if (!isEmpty(td)) {
            buttonAddCoverage.click();
            super.fillTab(td);
        }
        return this;
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.expand(POLICY_INFORMATION.get(), this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return DEPENDENT_COVERAGE.get();
    }

    public void addCoverage(String planName, String coverageName) {
        LOGGER.info(String.format("Add coverage: '%s' for plan '%s'", coverageName, planName));
        buttonAddCoverage.click();
        getAssetList().getAsset(PLAN).setValue(planName);
        getAssetList().getAsset(COVERAGE_NAME).setValue(coverageName);
    }
}
