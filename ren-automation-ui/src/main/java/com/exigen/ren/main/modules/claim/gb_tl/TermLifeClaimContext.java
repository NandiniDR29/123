/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_tl;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.common.tabs.*;
import com.exigen.ren.main.modules.claim.gb_tl.tabs.*;


public interface TermLifeClaimContext {
    TermLifeClaim termLifeClaim = AutomationContext.getService(TermLifeClaim.class);
    ReportingPartyAndClaimContactTab reportingPartyAndClaimContactTab = termLifeClaim.getDefaultWorkspace().getTab(ReportingPartyAndClaimContactTab.class);
    PolicyInformationPolicyTab policyInformationPolicyTab = termLifeClaim.getDefaultWorkspace().getTab(PolicyInformationPolicyTab.class);
    PolicyInformationParticipantParticipantInformationTab policyInformationParticipantParticipantInformationTab = termLifeClaim.getDefaultWorkspace().getTab(PolicyInformationParticipantParticipantInformationTab.class);
    PolicyInformationParticipantParticipantCoverageTab policyInformationParticipantParticipantCoverageTab = termLifeClaim.getDefaultWorkspace().getTab(PolicyInformationParticipantParticipantCoverageTab.class);
    PolicyInformationDependentDependentInformationTab policyInformationDependentDependentInformationTab = termLifeClaim.getDefaultWorkspace().getTab(PolicyInformationDependentDependentInformationTab.class);
    PolicyInformationDependentDependentCoverageTab policyInformationDependentDependentCoverageTab = termLifeClaim.getDefaultWorkspace().getTab(PolicyInformationDependentDependentCoverageTab.class);
    PolicyInformationSponsorTab policyInformationSponsorTab = termLifeClaim.getDefaultWorkspace().getTab(PolicyInformationSponsorTab.class);
    EventInformationLossEventTab eventInformationLossEventTab = termLifeClaim.getDefaultWorkspace().getTab(EventInformationLossEventTab.class);
    EventInformationAuthorityReportTab eventInformationAuthorityReportTab = termLifeClaim.getDefaultWorkspace().getTab(EventInformationAuthorityReportTab.class);
    BenefitsBenefitSummaryTab benefitsBenefitSummaryTab = termLifeClaim.getDefaultWorkspace().getTab(BenefitsBenefitSummaryTab.class);
    BenefitsDeathDecedentTab benefitsDeathDecedentTab = termLifeClaim.getDefaultWorkspace().getTab(BenefitsDeathDecedentTab.class);
    BenefitsDeathIncidentTab benefitsDeathIncidentTab = termLifeClaim.getDefaultWorkspace().getTab(BenefitsDeathIncidentTab.class);
    BenefitsDeathDeathCertificateTab benefitsDeathDeathCertificateTab = termLifeClaim.getDefaultWorkspace().getTab(BenefitsDeathDeathCertificateTab.class);
    BenefitsDeathBeneficiaryTab benefitsDeathBeneficiaryTab = termLifeClaim.getDefaultWorkspace().getTab(BenefitsDeathBeneficiaryTab.class);
    BenefitsAcceleratedDeathInjuryPartyInformationTab benefitsAcceleratedDeathInjuryPartyInformationTab = termLifeClaim.getDefaultWorkspace().getTab(BenefitsAcceleratedDeathInjuryPartyInformationTab.class);
    BenefitsAcceleratedDeathIncidentTab benefitsAcceleratedDeathIncidentTab = termLifeClaim.getDefaultWorkspace().getTab(BenefitsAcceleratedDeathIncidentTab.class);
    BenefitsPremiumWaiverInjuryPartyInformationTab benefitsPremiumWaiverInjuryPartyInformationTab = termLifeClaim.getDefaultWorkspace().getTab(BenefitsPremiumWaiverInjuryPartyInformationTab.class);
    BenefitsPremiumWaiverIncidentTab benefitsPremiumWaiverIncidentTab = termLifeClaim.getDefaultWorkspace().getTab(BenefitsPremiumWaiverIncidentTab.class);
    AdditionalPartiesWitnessTab additionalPartiesWitnessTab = termLifeClaim.getDefaultWorkspace().getTab(AdditionalPartiesWitnessTab.class);
    AdditionalPartiesAdditionalPartyTab additionalPartiesAdditionalPartyTab = termLifeClaim.getDefaultWorkspace().getTab(AdditionalPartiesAdditionalPartyTab.class);
    ClaimCoordinationAdditionalInsuranceTab claimCoordinationAdditionalInsuranceTab = termLifeClaim.getDefaultWorkspace().getTab(ClaimCoordinationAdditionalInsuranceTab.class);
    ClaimCoordinationRelatedClaimsTab claimCoordinationRelatedClaimsTab = termLifeClaim.getDefaultWorkspace().getTab(ClaimCoordinationRelatedClaimsTab.class);
    EAPServicesServiceRequestTab eAPServicesServiceRequestTab = termLifeClaim.getDefaultWorkspace().getTab(EAPServicesServiceRequestTab.class);
    ClaimHandlingClaimFileOwnerTab claimHandlingClaimFileOwnerTab = termLifeClaim.getDefaultWorkspace().getTab(ClaimHandlingClaimFileOwnerTab.class);
    ClaimHandlingSpecialHandlingTab claimHandlingSpecialHandlingTab = termLifeClaim.getDefaultWorkspace().getTab(ClaimHandlingSpecialHandlingTab.class);
    CompleteNotificationTab completeNotificationTab = termLifeClaim.getDefaultWorkspace().getTab(CompleteNotificationTab.class);

    default void createDefaultTermLifeInsuranceClaimForMasterPolicy() {
        termLifeClaim.create(termLifeClaim.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultTermLifeInsuranceClaimForCertificatePolicy() {
        termLifeClaim.create(termLifeClaim.getDefaultTestData("DataGatherCertificate", TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultTermLifeInsuranceClaimWithoutPolicy() {
        termLifeClaim.createWithoutPolicy(termLifeClaim.getDefaultTestData(TestDataKey.DATA_GATHER_WITHOUT_POLICY,
                TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void initiateTLClaimWithPolicyAndFillToTab(TestData testData, Class<? extends Tab> tabClass, boolean isFillTab) {
        termLifeClaim.initiate(testData);
        termLifeClaim.getDefaultWorkspace().fillUpTo(testData, tabClass, isFillTab);
    }
}
