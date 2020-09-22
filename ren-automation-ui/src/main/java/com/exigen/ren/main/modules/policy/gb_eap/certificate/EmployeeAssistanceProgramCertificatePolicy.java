/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_eap.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.actions.EmployeeAssistanceProgramCertificatePolicyDataGatherAction;
import com.exigen.ren.main.modules.policy.gb_eap.certificate.actions.EmployeeAssistanceProgramCertificateInquiryAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation for a specific entity type.
 * @category Generated
 */
public class EmployeeAssistanceProgramCertificatePolicy implements GroupBenefitsCertificatePolicy {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/policy/gb_eap/certificate");
    protected static final Logger LOGGER = LoggerFactory.getLogger(EmployeeAssistanceProgramCertificatePolicy.class);

    @Override
    public GroupBenefitsCertificatePolicyType getType() {
        return GroupBenefitsCertificatePolicyType.GB_EAP;
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return dataGather().getWorkspace();
    }

    @Override
    public EmployeeAssistanceProgramCertificatePolicyDataGatherAction dataGather() {
        return AutomationContext.getAction(EmployeeAssistanceProgramCertificatePolicyDataGatherAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }

    @Override
    public EmployeeAssistanceProgramCertificateInquiryAction policyInquiry() {
        return AutomationContext.getAction(EmployeeAssistanceProgramCertificateInquiryAction.class);
    }

    @Override
    public EmployeeAssistanceProgramCertificateInquiryAction quoteInquiry() {
        return policyInquiry();
    }
}
