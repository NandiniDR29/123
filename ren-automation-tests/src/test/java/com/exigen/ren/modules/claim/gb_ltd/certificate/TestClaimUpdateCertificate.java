/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.ClaimConstants.ClaimLossEventTable;
import com.exigen.ren.main.enums.ClaimConstants.ClaimPartiesTable;
import com.exigen.ren.main.enums.ClaimConstants.ClaimStatus;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUpdateCertificate extends ClaimGroupBenefitsLTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "IPBQA-23948", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUpdateCertificate() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DI_LTD);

        createDefaultLongTermDisabilityClaimForCertificatePolicy();

        String claimNumber = ClaimSummaryPage.getClaimNumber();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.INITIAL);

        LOGGER.info("TEST: Submit Claim #" + claimNumber);
        claim.claimSubmit().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.NOTIFICATION);

        LOGGER.info("TEST: Open Claim #" + claimNumber);
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.OPEN);

        LOGGER.info("TEST: Update Claim #" + claimNumber);
        claim.claimUpdate().perform(tdClaim.getTestData("TestClaimUpdateCertificate", "TestData"));
        Map<String, String> mapValues = storeDataForVerification("TestClaimUpdateCertificate");

        Row rowLossEvent = ClaimSummaryPage.tableLossEvent.getRow(1);
        Row rowAddParty = ClaimSummaryPage.tableClaimParties.getRow(ClaimPartiesTable.PARTY_NAME, mapValues.get("Party Name"));

        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.OPEN);
            softly.assertThat(rowLossEvent.getCell(ClaimLossEventTable.DESCRIPTION_OF_LOSS)).hasValue(mapValues.get("Description of Loss"));
            softly.assertThat(rowAddParty.getCell(ClaimPartiesTable.PARTY_NAME)).hasValue(mapValues.get("Party Name"));
            softly.assertThat(rowAddParty.getCell(ClaimPartiesTable.ADDRESS)).valueContains(mapValues.get("Address"));
        });
    }
}
