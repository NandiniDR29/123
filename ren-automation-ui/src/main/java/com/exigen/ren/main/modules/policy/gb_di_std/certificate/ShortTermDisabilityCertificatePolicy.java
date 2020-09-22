/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_di_std.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.actions.ShortTermDisabilityCertificateInquiryAction;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.actions.ShortTermDisabilityCertificatePolicyDataGatherAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation for a specific entity type.
 * @category Generated
 */
public class ShortTermDisabilityCertificatePolicy implements GroupBenefitsCertificatePolicy {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/policy/gb_di_std/certificate");
    protected static final Logger LOGGER = LoggerFactory.getLogger(ShortTermDisabilityCertificatePolicy.class);

    @Override
    public GroupBenefitsCertificatePolicyType getType() {
        return GroupBenefitsCertificatePolicyType.GB_DI_STD;
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return dataGather().getWorkspace();
    }

    @Override
    public ShortTermDisabilityCertificatePolicyDataGatherAction dataGather() {
        return AutomationContext.getAction(ShortTermDisabilityCertificatePolicyDataGatherAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }

    @Override
    public ShortTermDisabilityCertificateInquiryAction policyInquiry() {
        return AutomationContext.getAction(ShortTermDisabilityCertificateInquiryAction.class);
    }

    @Override
    public ShortTermDisabilityCertificateInquiryAction quoteInquiry() {
        return policyInquiry();
    }
}
