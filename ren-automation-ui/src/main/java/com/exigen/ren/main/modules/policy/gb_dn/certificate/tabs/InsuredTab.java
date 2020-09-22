/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs;

import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificateTab;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.INSURED;

public class InsuredTab extends GroupBenefitsCertificateTab {
    public InsuredTab() {
        super(InsuredTabMetaData.class);
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return INSURED.get();
    }
}
