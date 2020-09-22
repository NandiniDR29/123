/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ac;

import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.main.modules.claim.common.tabs.*;


public interface AccidentHealthClaimContext {
    AccidentHealthClaim accHealthClaim = AutomationContext.getService(AccidentHealthClaim.class);

    ReportingPartyAndClaimContactTab reportingPartyAndClaimContactTab = accHealthClaim.getDefaultWorkspace().getTab(ReportingPartyAndClaimContactTab.class);
    PolicyInformationPolicyTab policyInformationPolicyTab = accHealthClaim.getDefaultWorkspace().getTab(PolicyInformationPolicyTab.class);
    PolicyInformationParticipantParticipantInformationTab policyInformationParticipantParticipantInformationTab = accHealthClaim.getDefaultWorkspace().getTab(PolicyInformationParticipantParticipantInformationTab.class);
    PolicyInformationParticipantParticipantCoverageTab policyInformationParticipantParticipantCoverageTab = accHealthClaim.getDefaultWorkspace().getTab(PolicyInformationParticipantParticipantCoverageTab.class);
    PolicyInformationDependentDependentInformationTab policyInformationDependentDependentInformationTab = accHealthClaim.getDefaultWorkspace().getTab(PolicyInformationDependentDependentInformationTab.class);
    PolicyInformationDependentDependentCoverageTab policyInformationDependentDependentCoverageTab = accHealthClaim.getDefaultWorkspace().getTab(PolicyInformationDependentDependentCoverageTab.class);
    PolicyInformationScheduledItemTab policyInformationScheduledItemTab = accHealthClaim.getDefaultWorkspace().getTab(PolicyInformationScheduledItemTab.class);
    PolicyInformationSponsorTab policyInformationSponsorTab = accHealthClaim.getDefaultWorkspace().getTab(PolicyInformationSponsorTab.class);
    EventInformationLossEventTab eventInformationLossEventTab = accHealthClaim.getDefaultWorkspace().getTab(EventInformationLossEventTab.class);
    EventInformationAuthorityReportTab eventInformationAuthorityReportTab = accHealthClaim.getDefaultWorkspace().getTab(EventInformationAuthorityReportTab.class);
    BenefitsBenefitSummaryTab benefitsBenefitSummaryTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsBenefitSummaryTab.class);
    BenefitsAccidentalDeathDecedentTab benefitsAccidentalDeathDecedentTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsAccidentalDeathDecedentTab.class);
    BenefitsAccidentalDeathIncidentTab benefitsAccidentalDeathIncidentTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsAccidentalDeathIncidentTab.class);
    BenefitsAccidentalDeathDeathCertificateTab benefitsAccidentalDeathDeathCertificateTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsAccidentalDeathDeathCertificateTab.class);
    BenefitsAccidentalDeathBeneficiaryTab benefitsAccidentalDeathBeneficiaryTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsAccidentalDeathBeneficiaryTab.class);
    BenefitsAccidentalDismembermentInjuryPartyInformationTab benefitsAccidentalDismembermentInjuryPartyInformationTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsAccidentalDismembermentInjuryPartyInformationTab.class);
    BenefitsAccidentalDismembermentIncidentTab benefitsAccidentalDismembermentIncidentTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsAccidentalDismembermentIncidentTab.class);
    BenefitsCriticalIllnessInjuryPartyInformationTab benefitsCriticalIllnessInjuryPartyInformationTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsCriticalIllnessInjuryPartyInformationTab.class);
    BenefitsCriticalIllnessIncidentTab benefitsCriticalIllnessIncidentTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsCriticalIllnessIncidentTab.class);
    BenefitsDiagnosisAndTreatmentInjuryPartyInformationTab benefitsDiagnosisAndTreatmentInjuryPartyInformationTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsDiagnosisAndTreatmentInjuryPartyInformationTab.class);
    BenefitsDiagnosisAndTreatmentIncidentTab benefitsDiagnosisAndTreatmentIncidentTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitsDiagnosisAndTreatmentIncidentTab.class);
    AdditionalPartiesWitnessTab additionalPartiesWitnessTab = accHealthClaim.getDefaultWorkspace().getTab(AdditionalPartiesWitnessTab.class);
    AdditionalPartiesAdditionalPartyTab additionalPartiesAdditionalPartyTab = accHealthClaim.getDefaultWorkspace().getTab(AdditionalPartiesAdditionalPartyTab.class);
    ClaimCoordinationAdditionalInsuranceTab claimCoordinationAdditionalInsuranceTab = accHealthClaim.getDefaultWorkspace().getTab(ClaimCoordinationAdditionalInsuranceTab.class);
    ClaimCoordinationRelatedClaimsTab claimCoordinationRelatedClaimsTab = accHealthClaim.getDefaultWorkspace().getTab(ClaimCoordinationRelatedClaimsTab.class);
    EAPServicesServiceRequestTab eAPServicesServiceRequestTab = accHealthClaim.getDefaultWorkspace().getTab(EAPServicesServiceRequestTab.class);
    ClaimHandlingClaimFileOwnerTab claimHandlingClaimFileOwnerTab = accHealthClaim.getDefaultWorkspace().getTab(ClaimHandlingClaimFileOwnerTab.class);
    ClaimHandlingSpecialHandlingTab claimHandlingSpecialHandlingTab = accHealthClaim.getDefaultWorkspace().getTab(ClaimHandlingSpecialHandlingTab.class);
    CompleteNotificationTab completeNotificationTab = accHealthClaim.getDefaultWorkspace().getTab(CompleteNotificationTab.class);

}
