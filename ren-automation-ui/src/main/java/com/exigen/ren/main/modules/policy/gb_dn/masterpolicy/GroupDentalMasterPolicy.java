/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.policy.gb_dn.masterpolicy;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.actions.*;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupDentalMasterPolicy implements GroupBenefitsMasterPolicy {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/policy/gb_dn/master");
    protected static final Logger LOGGER = LoggerFactory.getLogger(GroupDentalMasterPolicy.class);

    @Override
    public GroupBenefitsMasterPolicyType getType() {
        return GroupBenefitsMasterPolicyType.GB_DN;
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return dataGather().getWorkspace();
    }

    @Override
    public void calculatePremium() {
        dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(GroupBenefitsTab.PREMIUM_SUMMARY.get(), Tab.doubleWaiter);
        PremiumSummaryTab.buttonRate.click();
        PremiumSummaryTab.buttonSaveAndExit.click();
    }

    @Override
    public GroupDentalMasterPolicyDataGatherAction dataGather() {
        return AutomationContext.getAction(GroupDentalMasterPolicyDataGatherAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }

    @Override
    public GroupDentalMasterPolicyInquiryAction policyInquiry() {
        return AutomationContext.getAction(GroupDentalMasterPolicyInquiryAction.class);
    }

    @Override
    public GroupDentalMasterPolicyInquiryAction quoteInquiry() {
        return policyInquiry();
    }

    @Override
    public GroupDentalMasterPolicyIssueAction issue() {
        return AutomationContext.getAction(GroupDentalMasterPolicyIssueAction.class);
    }

    @Override
    public GroupDentalMasterPolicyUpdateProcedureCodeAction updateProcedureCode() {
        return AutomationContext.getAction(GroupDentalMasterPolicyUpdateProcedureCodeAction.class);
    }

    public GroupDentalMasterPolicyRateGuaranteeRenewAction rateGuaranteeRenew() {
        return AutomationContext.getAction(GroupDentalMasterPolicyRateGuaranteeRenewAction.class);
    }
}
