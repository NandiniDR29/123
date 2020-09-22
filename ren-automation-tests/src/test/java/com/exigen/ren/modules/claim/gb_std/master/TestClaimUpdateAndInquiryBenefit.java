/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_std.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitStatutorySTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitSTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUpdateAndInquiryBenefit extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23974", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUpdateAndInquiryBenefit() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_DI_STD);

        createDefaultShortTermDisabilityClaimForMasterPolicy();

        claim.claimOpen().perform();

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_STD_OtherValues"));

        String addressLine1 = tdClaim.getTestData("NewBenefit", "TestData_STD_OtherValues").getValue(BenefitSTDInjuryPartyInformationTab.class.getSimpleName(),
                BenefitStatutorySTDInjuryPartyInformationTabMetaData.ADDRESS_LINE_1.getLabel());

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();

        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();
        assertThat(BenefitSTDInjuryPartyInformationTab.lableAddressLine1).hasValue(addressLine1);
        BenefitSTDInjuryPartyInformationTab.buttonCancel.click();

        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());

        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimUpdateAndInquiryBenefit", "TestData"), 2);

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();
        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();

        verifyBenefitInformation(tdClaim.getTestData("TestClaimUpdateAndInquiryBenefit").getTestData("TestData", BenefitSTDInjuryPartyInformationTab.class.getSimpleName()));
    }

    private void verifyBenefitInformation(TestData data) {
        AssetList assetList = (AssetList) new BenefitSTDInjuryPartyInformationTab().getAssetList();

        AssetList partyAddressContact = new AssetList(By.id("policyDataGatherForm:componentView_ShortTermDisabilityPartyAddressContact"));
        assetList.getAssetNames().stream().forEach(a->partyAddressContact.registerAsset(a, StaticElement.class, Waiters.NONE));

        TestData actualData = partyAddressContact.getPartialValue(data);
        assertThat(actualData).isEqualTo(data);
    }
}
