/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_tl.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_tl.metadata.BenefitDeathDecedentTabMetaData;
import com.exigen.ren.main.modules.claim.gb_tl.tabs.BenefitDeathDecedentTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAddUpdateBenefitCertificate extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-25733", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAddUpdateBenefit() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_TL);

        createDefaultTermLifeInsuranceClaimForCertificatePolicy();

        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Test: Add New Benefit to Claim #" + claimNumber);

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_Death_OtherValues"));

        String address1 = tdClaim.getTestData("NewBenefit", "TestData_Death_OtherValues")
                .getValue(BenefitDeathDecedentTab.class.getSimpleName(),
                        BenefitDeathDecedentTabMetaData.ADDRESS_LINE_1.getLabel());

        assertThat(ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_TYPE)).hasValue("Death");

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();
        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();
        assertThat(BenefitDeathDecedentTab.lableAddressLine1).hasValue(address1);
        BenefitDeathDecedentTab.buttonCancel.click();

        LOGGER.info("Test: Update Benefit to Claim #" + claimNumber);
        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData"), 2);

        String updatedAddress1 = tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData")
                .getValue(BenefitDeathDecedentTab.class.getSimpleName(),
                        BenefitDeathDecedentTabMetaData.ADDRESS_LINE_1.getLabel());

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();
        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();
        assertThat(BenefitDeathDecedentTab.lableAddressLine1).hasValue(updatedAddress1);
    }
}
