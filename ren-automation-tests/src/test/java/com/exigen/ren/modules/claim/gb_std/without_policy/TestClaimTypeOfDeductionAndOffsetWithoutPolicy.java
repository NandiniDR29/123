/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_std.without_policy;

import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.DeductionsActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.OtherIncomeBenefitActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.DeductionsActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.OtherIncomeBenefitActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimTypeOfDeductionAndOffsetWithoutPolicy extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-13228", "REN-12927"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimTypeOfDeductionAndOffset() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultShortTermDisabilityClaimWithoutPolicy();

        claim.claimSubmit().perform(new SimpleDataProvider());
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);

        claim.calculateSingleBenefitAmount().start(1);
        NavigationPage.toTreeTab(NavigationEnum.ClaimCalculateSingleBenefitTab.OTHER_INCOME_BENEFIT.get());
        OtherIncomeBenefitActionTab.buttonAdd.click();
        assertThat(new OtherIncomeBenefitActionTab().getAssetList().getAsset(OtherIncomeBenefitActionTabMetaData.TYPE_OF_OFFSET)).containsAllOptions("New York PFL", "New Jersey PFL");

        NavigationPage.toTreeTab(NavigationEnum.ClaimCalculateSingleBenefitTab.DEDUCTIONS.get());
        DeductionsActionTab.buttonAdd.click();
        assertThat(new DeductionsActionTab().getAssetList().getAsset(DeductionsActionTabMetaData.TYPE_OF_DEDUCTION)).hasOptions("", "Child Support", "Garnishment A");
    }
}
