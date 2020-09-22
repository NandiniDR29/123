/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.gb_di_std.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.CertificatePolicyTab;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.InsuredTab;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs.PremiumSummaryTab;

public interface ShortTermDisabilityCertificatePolicyContext {
    ShortTermDisabilityCertificatePolicy shortTermDisabilityCertificatePolicy = AutomationContext.getService(ShortTermDisabilityCertificatePolicy.class);
    CertificatePolicyTab certificatePolicyTab = shortTermDisabilityCertificatePolicy.getDefaultWorkspace().getTab(CertificatePolicyTab.class);
    CoveragesTab coveragesTab = shortTermDisabilityCertificatePolicy.getDefaultWorkspace().getTab(CoveragesTab.class);
    InsuredTab insuredTab = shortTermDisabilityCertificatePolicy.getDefaultWorkspace().getTab(InsuredTab.class);
    PremiumSummaryTab premiumSummaryTab = shortTermDisabilityCertificatePolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class);

    default void createDefaultShortTermDisabilityCertificatePolicy() {
        shortTermDisabilityCertificatePolicy.createPolicyViaUI(getDefaultSTDCertificatePolicyDataGatherData()
                .adjust(shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE,TestDataKey.DEFAULT_TEST_DATA_KEY)));
    }

    default TestData getDefaultSTDCertificatePolicyDataGatherData() {
        return shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
    }
}
