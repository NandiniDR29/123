/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs;

import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificateTab;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.InsuredTabMetaData;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.INSURED;
import static com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext.insuredTab;

public class InsuredTab extends GroupBenefitsCertificateTab {
    public InsuredTab() {
        super(InsuredTabMetaData.class);
    }

    @Override
    public String getTabName() {
        return INSURED.get();
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName());
        return this;
    }

    public static AssetList getGeneralInfoAsset() {
        return insuredTab.getAssetList().getAsset(InsuredTabMetaData.GENERAL_INFORMATION);
    }

    public static AssetList getAddressInfoAsset() {
        return insuredTab.getAssetList().getAsset(InsuredTabMetaData.ADDRESS_INFORMATION);
    }

    public static AssetList getRelationshipInfoAsset() {
        return insuredTab.getAssetList().getAsset(InsuredTabMetaData.RELATIONSHIP_INFORMATION);
    }
}
