/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_std.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.POLICY_SUMMARY;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimCopyCertificate extends ClaimGroupBenefitsSTDBaseTest {
    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"IPBQA-23857", "REN-30022"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimCopyCertificate() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DI_STD);
        createDefaultShortTermDisabilityClaimForCertificatePolicy();
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

        List<Integer> columns = IntStream.rangeClosed(2, 9).boxed().collect(Collectors.toList());
        assertThat(ClaimSummaryPage.tableListOfNonDentalClaims.getRow(1).getPartialValueByIndex(columns))
                .isEqualTo(ClaimSummaryPage.tableListOfNonDentalClaims.getRow(TableConstants.LifeAndDisabilityClaims.CLAIM_NUM.getName(), claimNumber)
                        .getPartialValueByIndex(columns));
    }
}
