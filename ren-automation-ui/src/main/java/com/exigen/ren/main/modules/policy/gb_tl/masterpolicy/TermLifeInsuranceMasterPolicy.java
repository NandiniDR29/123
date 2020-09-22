/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_tl.masterpolicy;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.actions.*;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PremiumSummaryTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TermLifeInsuranceMasterPolicy implements GroupBenefitsMasterPolicy {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/policy/gb_tl/master");
    protected static final Logger LOGGER = LoggerFactory.getLogger(TermLifeInsuranceMasterPolicy.class);

    @Override
    public GroupBenefitsMasterPolicyType getType() {
        return GroupBenefitsMasterPolicyType.GB_TL;
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return dataGather().getWorkspace();
    }

    @Override
    public void calculatePremium() {
        dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get());
        ClassificationManagementTab.coveragesTable.getRows().forEach(row -> row.getCell(1).click());
        NavigationPage.PolicyNavigation.leftMenu(GroupBenefitsTab.PREMIUM_SUMMARY.get(), Tab.doubleWaiter);
        PremiumSummaryTab.buttonRate.click();
        PremiumSummaryTab.buttonSaveAndExit.click();
    }

    @Override
    public TermLifeInsuranceMasterPolicyDataGatherAction dataGather() {
        return AutomationContext.getAction(TermLifeInsuranceMasterPolicyDataGatherAction.class);
    }

    @Override
    public TermLifeInsuranceMasterPolicyAcceptContractAction acceptContract() {
        return AutomationContext.getAction(TermLifeInsuranceMasterPolicyAcceptContractAction.class);
    }

    @Override
    public TermLifeInsuranceMasterPolicyIssueAction issue() {
        return AutomationContext.getAction(TermLifeInsuranceMasterPolicyIssueAction.class);
    }

    @Override
    public TermLifeInsuranceMasterPolicyPremiumAdjustmentAction premiumAdjustment() {
        return AutomationContext.getAction(TermLifeInsuranceMasterPolicyPremiumAdjustmentAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }

    @Override
    public TermLifeInsuranceMasterInquiryAction policyInquiry() {
        return AutomationContext.getAction(TermLifeInsuranceMasterInquiryAction.class);
    }

    @Override
    public TermLifeInsuranceMasterInquiryAction quoteInquiry() {
        return policyInquiry();
    }
}
