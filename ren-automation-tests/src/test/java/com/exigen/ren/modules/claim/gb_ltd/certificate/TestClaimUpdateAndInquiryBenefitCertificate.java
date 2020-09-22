/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitLTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitSTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageEvaluationTabMetaData.INSURED_PERSON_COVERAGE_EFFECTIVE_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUpdateAndInquiryBenefitCertificate extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23974", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUpdateAndInquiryBenefitCertificate() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DI_LTD);

        createDefaultLongTermDisabilityClaimForCertificatePolicy();

        claim.claimOpen().perform();

        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_LTD")
                // https://jira.exigeninsurance.com/browse/REN-33082: CWCP is out of scope
                .mask(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), INSURED_PERSON_COVERAGE_EFFECTIVE_DATE.getLabel())));

        String addressLine1 = tdClaim.getTestData("NewBenefit", "TestData_LTD").getValue(BenefitLTDInjuryPartyInformationTab.class.getSimpleName(),
                BenefitLTDInjuryPartyInformationTabMetaData.ADDRESS_LINE_1.getLabel());

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();

        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();
        assertThat(BenefitLTDInjuryPartyInformationTab.lableAddressLine1).hasValue(addressLine1);
        BenefitLTDInjuryPartyInformationTab.buttonCancel.click();

        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());

        claim.updateBenefit().perform(tdClaim.getTestData("TestClaimUpdateAndInquiryBenefit", "TestData"), 2);

        ClaimAdjudicationBenefitPage.tableAllClaimBenefits.getRow(2).getCell(ClaimConstants.ClaimAllBenefitsTable.BENEFIT_NUMBER).controls.links.getFirst().click();
        ClaimAdjudicationBenefitPage.linkBenefitInquiry.click();

        verifyBenefitInformation(tdClaim.getTestData("TestClaimUpdateAndInquiryBenefit").getTestData("TestData", BenefitSTDInjuryPartyInformationTab.class.getSimpleName()));
    }

    private void verifyBenefitInformation(TestData data) {
        AssetList assetList = (AssetList) new BenefitLTDInjuryPartyInformationTab().getAssetList();

        AssetList partyAddressContact = new AssetList(By.id("policyDataGatherForm:componentView_LongTermDisabilityPartyAddressContact"));
        assetList.getAssetNames().forEach(a -> partyAddressContact.registerAsset(a, StaticElement.class, Waiters.NONE));

        TestData actualData = partyAddressContact.getPartialValue(data);
        assertThat(actualData).isEqualTo(data);
    }
}
