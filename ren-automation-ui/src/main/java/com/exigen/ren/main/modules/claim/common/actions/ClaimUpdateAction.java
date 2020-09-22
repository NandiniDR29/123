/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.actions;

import com.exigen.ren.common.Action;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.common.tabs.*;

public class ClaimUpdateAction implements Action {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(ReportingPartyAndClaimContactActionTab.class)
            .registerTab(PolicyInformationPolicyActionTab.class)
            .registerTab(PolicyInformationInsurableRiskRiskItemActionTab.class)
            .registerTab(PolicyInformationInsurableRiskScheduledItemActionTab.class)
            .registerTab(PolicyInformationPartiesInsuranceAgentActionTab.class)
            .registerTab(PolicyInformationPartiesInsuredActionTab.class)
            .registerTab(PolicyInformationPartiesDriverActionTab.class)
            .registerTab(EventInformationLossEventActionTab.class)
            .registerTab(EventInformationAuthorityReportActionTab.class)
            .registerTab(EventInformationTheftActionTab.class)
            .registerTab(ClaimDeductibleCalculatorActionTab.class)
            .registerTab(AdditionalPartiesWitnessActionTab.class)
            .registerTab(AdditionalPartiesAdditionalPartyActionTab.class)
            .registerTab(ClaimCoordinationRelatedClaimsActionTab.class)
            .registerTab(ServicesServiceRequestActionTab.class)
            .registerTab(ClaimHandlingClaimFileOwnerActionTab.class)
            .registerTab(ClaimHandlingSpecialHandlingActionTab.class)
            .registerTab(CompleteNotificationActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Claim Update";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        Tab.buttonSaveAndExit.click();
        return this;
    }

}
