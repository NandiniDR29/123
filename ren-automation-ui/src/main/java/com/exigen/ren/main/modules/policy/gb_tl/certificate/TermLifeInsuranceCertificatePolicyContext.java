/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.gb_tl.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CertificatePolicyTab;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.InsuredTab;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.tabs.PremiumSummaryTab;

public interface TermLifeInsuranceCertificatePolicyContext {
    TermLifeInsuranceCertificatePolicy termLifeInsuranceCertificatePolicy = AutomationContext.getService(TermLifeInsuranceCertificatePolicy.class);
    CertificatePolicyTab certificatePolicyTab = termLifeInsuranceCertificatePolicy.getDefaultWorkspace().getTab(CertificatePolicyTab.class);
    CoveragesTab coveragesTab = termLifeInsuranceCertificatePolicy.getDefaultWorkspace().getTab(CoveragesTab.class);
    InsuredTab insuredTab = termLifeInsuranceCertificatePolicy.getDefaultWorkspace().getTab(InsuredTab.class);
    PremiumSummaryTab premiumSummaryTab = termLifeInsuranceCertificatePolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class);

    default void createDefaultTermLifeInsuranceCertificatePolicy(){
        termLifeInsuranceCertificatePolicy.createPolicyViaUI(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER,TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE,TestDataKey.DEFAULT_TEST_DATA_KEY)));
    }

    default TestData getDefaultCertificatePolicyDataGatherData() {
        return termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
    }

}
