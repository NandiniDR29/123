/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ac.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDeathDecedentTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitAccidentalDeathDecedentTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAddUpdateBenefitCertificate extends ClaimGroupBenefitsACBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-25733", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAddUpdateBenefit() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_AC);

        createDefaultGroupAccidentClaimForCertificatePolicy();

        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Test: Add New Benefit to Claim #" + claimNumber);

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_AccidentalDeath_OtherValues"));

        String firstName = tdClaim.getTestData("NewBenefit", "TestData_AccidentalDeath_OtherValues")
                .getValue(BenefitAccidentalDeathDecedentTab.class.getSimpleName(),
                        BenefitsAccidentalDeathDecedentTabMetaData.FIRST_NAME.getLabel());

        assertThat(ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_TYPE)).hasValue("Accidental Death");
        assertThat(ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.ASSOCIATED_PARTY)).valueContains(firstName);

        LOGGER.info("Test: Update Benefit to Claim #" + claimNumber);
        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData"), 2);

        String updatedFirstName = tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData")
                .getValue(BenefitAccidentalDeathDecedentTab.class.getSimpleName(),
                        BenefitsAccidentalDeathDecedentTabMetaData.FIRST_NAME.getLabel());

        assertThat(ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.ASSOCIATED_PARTY)).valueContains(updatedFirstName);
    }
}
