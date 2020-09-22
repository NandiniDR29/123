/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_ac.certificate.tabs;

import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CertificatePolicyTabMetaData;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.CERTIFICATE_POLICY;

public class CertificatePolicyTab extends DefaultTab {
    public CertificatePolicyTab() {
        super(CertificatePolicyTabMetaData.class);
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return CERTIFICATE_POLICY.get();
    }
}
