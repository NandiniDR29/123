/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_ac.masterpolicy;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.actions.GroupAccidentMasterInquiryAction;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.actions.GroupAccidentMasterPolicyDataGatherAction;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PremiumSummaryTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation for a specific entity type.
 * @category Generated
 */
public class GroupAccidentMasterPolicy implements GroupBenefitsMasterPolicy {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/policy/gb_ac/master");
    protected static final Logger LOGGER = LoggerFactory.getLogger(GroupAccidentMasterPolicy.class);

    @Override
    public GroupBenefitsMasterPolicyType getType() {
        return GroupBenefitsMasterPolicyType.GB_AC;
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
    public GroupAccidentMasterPolicyDataGatherAction dataGather() {
        return AutomationContext.getAction(GroupAccidentMasterPolicyDataGatherAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }

    @Override
    public GroupAccidentMasterInquiryAction policyInquiry() {
        return AutomationContext.getAction(GroupAccidentMasterInquiryAction.class);
    }

    @Override
    public GroupAccidentMasterInquiryAction quoteInquiry() {
        return policyInquiry();
    }
}
