/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.main.modules.policy.gb_eap.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.tabs.InsuredTab;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.tabs.CertificatePolicyTab;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.tabs.PremiumSummaryTab;

public interface EmployeeAssistanceProgramCertificatePolicyContext {
    EmployeeAssistanceProgramCertificatePolicy employeeAssistanceProgramCertificatePolicy = AutomationContext.getService(EmployeeAssistanceProgramCertificatePolicy.class);
    CertificatePolicyTab certificatePolicyTab = employeeAssistanceProgramCertificatePolicy.getDefaultWorkspace().getTab(CertificatePolicyTab.class);
    CoveragesTab coveragesTab = employeeAssistanceProgramCertificatePolicy.getDefaultWorkspace().getTab(CoveragesTab.class);
    InsuredTab insuredTab = employeeAssistanceProgramCertificatePolicy.getDefaultWorkspace().getTab(InsuredTab.class);
    PremiumSummaryTab premiumSummaryTab = employeeAssistanceProgramCertificatePolicy.getDefaultWorkspace().getTab(PremiumSummaryTab.class);

    default void createDefaultEmployeeAssistanceProgramCertificatePolicy() {
        employeeAssistanceProgramCertificatePolicy.createPolicyViaUI(getDefaultEAPCertificatePolicyDataGatherData()
                .adjust(employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE,TestDataKey.DEFAULT_TEST_DATA_KEY)));
    }

    default TestData getDefaultEAPCertificatePolicyDataGatherData() {
        return employeeAssistanceProgramCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
    }
}
