/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitSTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitSTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAddUpdateBenefit extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-25733", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAddUpdateBenefit() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_DI_STD);

        createDefaultShortTermDisabilityClaimForMasterPolicy();

        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Test: Add New Benefit to Claim #" + claimNumber);

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_STD_OtherValues"));

        String address1 = tdClaim.getTestData("NewBenefit", "TestData_STD_OtherValues")
                .getValue(BenefitSTDInjuryPartyInformationTab.class.getSimpleName(),
                        BenefitSTDInjuryPartyInformationTabMetaData.ADDRESS_LINE_1.getLabel());

        assertThat(ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_TYPE)).hasValue("Short Term Disability");

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();
        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();
        assertThat(BenefitSTDInjuryPartyInformationTab.lableAddressLine1).hasValue(address1);
        BenefitSTDInjuryPartyInformationTab.buttonCancel.click();

        LOGGER.info("Test: Update Benefit to Claim #" + claimNumber);
        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData"), 2);

        String updatedAddress1 = tdClaim.getTestData("TestClaimAddUpdateBenefit", "TestData")
                .getValue(BenefitSTDInjuryPartyInformationTab.class.getSimpleName(),
                        BenefitLTDInjuryPartyInformationTabMetaData.ADDRESS_LINE_1.getLabel());

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();
        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();
        assertThat(BenefitSTDInjuryPartyInformationTab.lableAddressLine1).hasValue(updatedAddress1);
    }
}
