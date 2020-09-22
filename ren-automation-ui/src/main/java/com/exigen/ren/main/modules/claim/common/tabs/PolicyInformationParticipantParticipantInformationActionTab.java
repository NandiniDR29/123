/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationActionTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.PARTICIPANT_INFORMATION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.POLICY_INFORMATION;

public class PolicyInformationParticipantParticipantInformationActionTab extends ActionTab {
    public PolicyInformationParticipantParticipantInformationActionTab() {
        super(PolicyInformationParticipantParticipantInformationActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.expand(POLICY_INFORMATION.get(), this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return PARTICIPANT_INFORMATION.get();
    }

    public static class ParticipantIndexedPreDisabilityEarnings extends MultiAssetList {
        public static final Button addParticipantIndexedPreDisabilityEarnings = new Button(By.id("policyDataGatherForm:addRenClaimsIndexedPreDisabilityEarnings"));

        public ParticipantIndexedPreDisabilityEarnings(BaseElement<?, ?> parent, By locator, Class<? extends MetaData> metaDataClass) {
            super(parent, locator, metaDataClass);
        }

        public ParticipantIndexedPreDisabilityEarnings(By locator, Class<? extends MetaData> metaDataClass) {
            super(locator, metaDataClass);
        }

        @Override
        protected void addSection(int index, int size) {
            addParticipantIndexedPreDisabilityEarnings.click();
        }

        @Override
        protected void selectSection(int i) {
        }
    }
}
