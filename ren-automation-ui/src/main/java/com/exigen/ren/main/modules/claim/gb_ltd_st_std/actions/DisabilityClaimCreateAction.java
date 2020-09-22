/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ltd_st_std.actions;

import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.common.actions.CreateAction;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.*;

public class DisabilityClaimCreateAction extends CreateAction {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(ReportingPartyAndClaimContactTab.class)
            .registerTab(PolicyInformationPolicyTab.class)
            .registerTab(PolicyInformationParticipantParticipantInformationTab.class)
            .registerTab(PolicyInformationParticipantParticipantCoverageTab.class)
            .registerTab(PolicyInformationDependentDependentInformationTab.class)
            .registerTab(PolicyInformationDependentDependentCoverageTab.class)
            .registerTab(PolicyInformationScheduledItemTab.class)
            .registerTab(PolicyInformationSponsorTab.class)
            .registerTab(EventInformationLossEventTab.class)
            .registerTab(EventInformationAuthorityReportTab.class)
            .registerTab(BenefitsBenefitSummaryTab.class)
            .registerTab(BenefitsLTDInjuryPartyInformationTab.class)
            .registerTab(BenefitsLTDIncidentTab.class)
            .registerTab(BenefitsSTDInjuryPartyInformationTab.class)
            .registerTab(BenefitsSTDIncidentTab.class)
            .registerTab(BenefitsStatutorySTDInjuryPartyInformationTab.class)
            .registerTab(BenefitsStatutorySTDIncidentTab.class)
            .registerTab(BenefitsPFLParticipantInformationTab.class)
            .registerTab(BenefitsPFLQualifyingEventTab.class)
            .registerTab(BenefitCoverageDeterminationTab.class)
            .registerTab(AdditionalPartiesWitnessTab.class)
            .registerTab(AdditionalPartiesAdditionalPartyTab.class)
            .registerTab(ClaimCoordinationAdditionalInsuranceTab.class)
            .registerTab(ClaimCoordinationRelatedClaimsTab.class)
            .registerTab(EAPServicesServiceRequestTab.class)
            .registerTab(ClaimHandlingClaimFileOwnerTab.class)
            .registerTab(ClaimHandlingSpecialHandlingTab.class)
            .registerTab(CompleteNotificationTab.class)
            .build();

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

}
