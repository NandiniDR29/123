/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.helpers.claim.NonDentalClaimVerifier;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.POLICY_SUMMARY;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimCopy extends ClaimGroupBenefitsTLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"IPBQA-23857", "REN-30022"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimCopy() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        createDefaultSelfAdminTermLifeInsuranceMasterPolicy();

        createDefaultTermLifeInsuranceClaimForMasterPolicy();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST: REN-30022");
        toSubTab(POLICY_SUMMARY);
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Policy Participant"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Policy Dependent"))).isAbsent();
        });
        toSubTab(OVERVIEW);

        LOGGER.info("TEST: IPBQA-23857. Copy Claim #" + claimNumber);
        claim.claimCopy().perform(tdClaim.getTestData("ClaimCopy", "TestData"));

        claim.navigateToClaim();
        assertThat(ClaimSummaryPage.dialogConfirmation.isPresent()).isFalse();
        Row actualRow = ClaimSummaryPage.tableListOfNonDentalClaims.getRow(1);
        Row expectedRow = ClaimSummaryPage.tableListOfNonDentalClaims.getRow(TableConstants.LifeAndDisabilityClaims.CLAIM_NUM.getName(), claimNumber);
        new NonDentalClaimVerifier().rows.excludeColumn(ClaimConstants.ClaimListTable.CLAIM).verify(actualRow, expectedRow);
    }
}
