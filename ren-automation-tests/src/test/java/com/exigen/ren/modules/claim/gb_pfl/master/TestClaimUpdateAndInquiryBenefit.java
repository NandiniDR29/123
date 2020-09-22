/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsPFLParticipantInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsPFLBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUpdateAndInquiryBenefit extends ClaimGroupBenefitsPFLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23974", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUpdateAndInquiryBenefit() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_PFL);

        createDefaultPaidFamilyLeaveClaimForMasterPolicy();

        claim.claimOpen().perform();

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_PFL"));

        String addressLine1 = tdClaim.getTestData("NewBenefit", "TestData_PFL").getValue(BenefitsPFLParticipantInformationTab.class.getSimpleName(),
                BenefitsPFLParticipantInformationTabMetaData.ADDRESS_LINE_1.getLabel());

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();

        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();
        assertThat(BenefitsPFLParticipantInformationTab.lableAddressLine1).hasValue(addressLine1);
        BenefitsPFLParticipantInformationTab.buttonCancel.click();

        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());

        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimUpdateAndInquiryBenefit", "TestData"), 2);

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();
        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();

        verifyBenefitInformation(tdClaim.getTestData("TestClaimUpdateAndInquiryBenefit").getTestData("TestData", BenefitsPFLParticipantInformationTab.class.getSimpleName()));
    }

    private void verifyBenefitInformation(TestData data) {
        AssetList assetList = (AssetList) new BenefitsPFLParticipantInformationTab().getAssetList();

        AssetList partyAddressContact = new AssetList(By.id("policyDataGatherForm:componentView_PaidFamilyLeavePartyAddressContact"));
        assetList.getAssetNames().forEach(a -> partyAddressContact.registerAsset(a, StaticElement.class, Waiters.NONE));

        TestData actualData = partyAddressContact.getPartialValue(data);
        assertThat(actualData).isEqualTo(data);
    }
}
