/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.UnsecureClaimActionTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUnsecure extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23946", component = CLAIMS_GROUPBENEFITS)
    public void testUnsecureClaim() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultSelfAdminTermLifeInsuranceMasterPolicy();
        createDefaultTermLifeInsuranceClaimForMasterPolicy();
        claim.claimOpen().perform();
        claim.secureClaim().perform(tdClaim.getTestData("SecureClaim", "TestData"));
        assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue(ClaimConstants.SpecialHandlingStatus.SECURE_CLAIM);

        claim.unsecureClaim().start();
        claim.unsecureClaim().getWorkspace().fill(tdClaim.getTestData("UnsecureClaim", "TestData"));
        UnsecureClaimActionTab.buttonCancel.click();
        assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue(ClaimConstants.SpecialHandlingStatus.SECURE_CLAIM);

        claim.unsecureClaim().perform(tdClaim.getTestData("UnsecureClaim", "TestData"));
        assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue("");
        assertThat(NavigationPage.isSubTabPresent(NavigationEnum.ClaimTab.SECURITY.get())).isFalse();
    }
}
