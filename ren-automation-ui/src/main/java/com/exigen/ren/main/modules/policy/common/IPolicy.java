/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common;

import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.EntityService;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.policy.common.actions.common.*;
import com.exigen.ren.main.modules.policy.common.actions.master.ChangeAgencyAction;

/**
 * Interface for a specific entity type.
 * Modify this class if action set for a specific entity type has to be altered.
 */
public interface IPolicy extends EntityService {

    Workspace getDefaultWorkspace();

    /**
     * Initiate product/entity creation
     *
     * @param td TestData to fill the form upon product intiation, needed only for GB and CL. It is OK to pass null for personal lines.
     */
    void initiate(TestData td);

    /**
     * Initiate and create quote
     *
     * @param td TestData object
     */
    void createQuote(TestData td);

    /**
     * Initiate and create policy
     *
     * @param td TestData object
     */
    void createPolicy(TestData td);

    /**
     * Issue renewal
     **/
    void createRenewal(TestData td);

    /**
     * Issue endorsement
     **/
    void createEndorsement(TestData td);

    /**
     * Calculate premium
     */
    void calculatePremium();

    void issue(TestData td);

    /**
     * Remove Coverage on endorsement
     **/
    default void removeCoverage(TestData td, String coverageName) {
        throw new IstfException(String.format("Remove coverage was not implemented for current policy type[%1$s].", this.getClass().getSimpleName()));
    }

    default Action endorse() {
        return AutomationContext.getAction(EndorseAction.class);
    }

    default Action renew() {
        return AutomationContext.getAction(RenewAction.class);
    }

    default Action cancel() {
        return AutomationContext.getAction(CancelAction.class);
    }

    default Action dataGather() {
        throw new IstfException(String.format("Data Gather action was not implemented for current policy type[%1$s].", this.getClass().getSimpleName()));
    }

    default Action issue() {
        throw new IstfException(String.format("Issue action was not implemented for current policy type[%1$s].", this.getClass().getSimpleName()));
    }

    default Action policyInquiry() {
        throw new IstfException(String.format("Policy Inquiry action was not implemented for current policy type[%1$s].", this.getClass().getSimpleName()));
    }

    default Action propose() {
        return AutomationContext.getAction(ProposeAction.class);
    }

    default Action preProposal() {
        return AutomationContext.getAction(PreProposalAction.class);
    }

    default Action quoteInquiry() {
        throw new IstfException(String.format("Quote Inquiry action was not implemented for current policy type[%1$s].", this.getClass().getSimpleName()));
    }

    default Action reinstate() {
        return AutomationContext.getAction(ReinstateAction.class);
    }

    default Action rescindCancellation() {
        return AutomationContext.getAction(RescindCancellationAction.class);
    }

    @Override
    default TestData defaultTestData() {
        throw new IstfException(String.format("Should be implemented in service class[%1$s] implementation with appropriate test data", this.getClass().getSimpleName()));
    }

    default Action endorseNPBInfo() {
        return AutomationContext.getAction(EndorseNPBInfoAction.class);
    }

    default Action changeAgency() {
        return AutomationContext.getAction(ChangeAgencyAction.class);
    }
}