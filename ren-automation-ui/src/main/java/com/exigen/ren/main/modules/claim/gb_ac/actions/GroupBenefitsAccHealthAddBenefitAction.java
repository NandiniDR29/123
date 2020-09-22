/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ac.actions;

import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.common.actions.AddBenefitAction;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.claim.gb_ac.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.modules.claim.gb_ac.tabs.BenefitPremiumWaiverIncidentTab;
import com.exigen.ren.main.modules.claim.gb_ac.tabs.BenefitPremiumWaiverInjuryPartyInformationTab;

public class GroupBenefitsAccHealthAddBenefitAction extends AddBenefitAction {

    private Workspace workspace = new Workspace.Builder()
            .registerTab(BenefitAccidentalDeathDecedentTab.class)
            .registerTab(BenefitAccidentalDeathIncidentTab.class)
            .registerTab(BenefitAccidentalDeathDeathCertificateTab.class)
            .registerTab(BenefitAccidentalDeathBeneficiaryTab.class)
            .registerTab(BenefitAccidentalDismembermentInjuryPartyInformationTab.class)
            .registerTab(BenefitAccidentalDismembermentIncidentTab.class)
            .registerTab(BenefitCriticalIllnessInjuryPartyInformationTab.class)
            .registerTab(BenefitCriticalIllnessIncidentTab.class)
            .registerTab(BenefitPremiumWaiverInjuryPartyInformationTab.class)
            .registerTab(BenefitPremiumWaiverIncidentTab.class)
            .registerTab(BenefitDiagnosisAndTreatmentInjuryPartyInformationTab.class)
            .registerTab(BenefitDiagnosisAndTreatmentIncidentTab.class)
            .registerTab(BenefitCoverageEvaluationTab.class)
            .registerTab(EAPServicesServiceRequestTab.class)
            .build();

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }
}
