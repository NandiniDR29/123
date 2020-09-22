/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ac.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDeathDecedentTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitAccidentalDeathDecedentTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUpdateAndInquiryBenefitCertificate extends ClaimGroupBenefitsACBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23974", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUpdateAndInquiryBenefitCertificate() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_AC);

        createDefaultGroupAccidentClaimForCertificatePolicy();

        claim.claimOpen().perform();

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_AccidentalDeath_OtherValues"));

        String  associatedParty=tdClaim.getTestData("NewBenefit", "TestData_AccidentalDeath_OtherValues").getValue(BenefitAccidentalDeathDecedentTab.class.getSimpleName(),
                BenefitsAccidentalDeathDecedentTabMetaData.FIRST_NAME.getLabel());

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();

        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();
        assertThat(BenefitAccidentalDeathDecedentTab.lableFirstName).hasValue(associatedParty);
        BenefitAccidentalDeathDecedentTab.buttonCancel.click();

        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());

        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimUpdateAndInquiryBenefit", "TestData"), 2);

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();
        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();

        verifyBenefitInformation(tdClaim.getTestData("TestClaimUpdateAndInquiryBenefit").getTestData("TestData", BenefitAccidentalDeathDecedentTab.class.getSimpleName()));
    }

    private void verifyBenefitInformation(TestData data){
        AssetList assetList = (AssetList) new BenefitAccidentalDeathDecedentTab().getAssetList();

        AssetList accidentalDeathParty = new AssetList(By.id("policyDataGatherForm:componentView_AccidentalDeathParty"));
        assetList.getAssetNames().forEach(a->accidentalDeathParty.registerAsset(a, StaticElement.class, Waiters.NONE));

        TestData actualData = accidentalDeathParty.getPartialValue(data);
        assertThat(actualData).isEqualTo(data);
    }
}
