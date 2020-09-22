/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common;

import com.exigen.istf.data.TestData;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.billing.setup_billing_groups.actions.SetupBillingGroupsAction;
import com.exigen.ren.main.modules.policy.common.actions.common.ArchiveTransactionAction;
import com.exigen.ren.main.modules.policy.common.actions.common.InquiryAction;
import com.exigen.ren.main.modules.policy.common.actions.master.*;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;

public interface GroupBenefitsMasterPolicy extends IPolicyRest {

    @Override
    default void initiate(TestData td) {
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE);
        QuoteSummaryPage.buttonAddNewQuote.click();
        Page.dialogConfirmation.fillAssetList(td, "InitiniateDialog");
        Page.dialogConfirmation.buttonNext.click();
    }

    @Override
    default void createQuoteViaUI(TestData td) {
        initiate(td);
        getDefaultWorkspace().fill(td);
        LOGGER.info("Created Quote " + EntityLogger.getEntityHeader(EntityLogger.EntityType.QUOTE));
    }

    @Override
    default void createPolicyViaUI(TestData td) {
        initiate(td);
        getDefaultWorkspace().fill(td);
        propose().perform(td);
        acceptContract().perform(td);
        issue().perform(td);
        LOGGER.info("Created Policy " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
    }

    @Override
    default void createRenewal(TestData td) {
        renew().perform(td);
        PolicySummaryPage.buttonRenewals.click();
        calculatePremium();
        PolicySummaryPage.buttonRenewals.click();
        //workaround to not change tests, probably should be fixed later to propose().perform(td);
        propose().perform();
        PolicySummaryPage.buttonRenewals.click();
        acceptContract().perform(td);
        PolicySummaryPage.buttonRenewals.click();
        issue().perform(td);
    }

    @Override
    default void createEndorsement(TestData td) {
        endorse().perform(td);
        PolicySummaryPage.buttonPendedEndorsement.click();
        dataGather().perform(td);
        PolicySummaryPage.buttonPendedEndorsement.click();
        issue().perform(td);
    }

    default void createRewrite(TestData td) {
        rewrite().perform(td);
        dataGather().perform(td);
        //workaround to not change tests, probably should be fixed later to propose().perform(td);
        propose().perform();
        acceptContract().perform(td);
        issue().perform(td);
    }

    @Override
    default void issue(TestData td) {
        issue().perform(td);
        LOGGER.info("Issue Quote " + EntityLogger.getEntityHeader(EntityLogger.EntityType.POLICY));
    }

    default Action acceptContract() {
        return AutomationContext.getAction(MasterPolicyAcceptContractAction.class);
    }

    default Action issue() {
        return AutomationContext.getAction(MasterPolicyIssueAction.class);
    }

    @Override
    default Action dataGather() {
        return AutomationContext.getAction(MasterPolicyDataGatherAction.class);
    }

    default Action quoteInquiry() {
        return AutomationContext.getAction(InquiryAction.class);
    }

    default Action cancelNotice() {
        return AutomationContext.getAction(MasterPolicyCancelNoticeAction.class);
    }

    default Action declineByCompanyQuote() {
        return AutomationContext.getAction(MasterPolicyDeclineByCompanyQuoteAction.class);
    }

    default Action declineByCustomerQuote() {
        return AutomationContext.getAction(MasterPolicyDeclineByCustomerQuoteAction.class);
    }

    default Action removeCancelNotice() {
        return AutomationContext.getAction(MasterPolicyRemoveCancelNoticeAction.class);
    }

    default Action deletePendedTransaction() {
        return AutomationContext.getAction(MasterPolicyDeletePendedTransactionAction.class);
    }

    default Action rewrite() {
        return AutomationContext.getAction(MasterPolicyRewriteAction.class);
    }

    default Action copyQuote() {
        return AutomationContext.getAction(MasterPolicyCopyQuoteAction.class);
    }

    default Action premiumAdjustment() {
        return AutomationContext.getAction(MasterPolicyPremiumAdjustmentAction.class);
    }

    default Action policyCopy() {
        return AutomationContext.getAction(MasterPolicyCopyAction.class);
    }

    default MasterPolicyRollOnAction rollOn() {
        return AutomationContext.getAction(MasterPolicyRollOnAction.class);
    }

    default Action setupBillingGroups() {
        return AutomationContext.getAction(SetupBillingGroupsAction.class);
    }

    default Action updateReinsuranceRate() {
        return AutomationContext.getAction(MasterPolicyUpdateReinsuranceRateAction.class);
    }

    default Action updateProcedureCode() {
        return AutomationContext.getAction(MasterPolicyUpdateProcedureCode.class);
    }

    default ArchiveTransactionAction archiveTransaction() {
        return AutomationContext.getAction(ArchiveTransactionAction.class);
    }

    default void initiateAndFillUpToTab(TestData testData, Class<? extends Tab> tabClass, boolean isFillTab) {
        initiate(testData);
        getDefaultWorkspace().fillUpTo(testData, tabClass, isFillTab);
    }
}
