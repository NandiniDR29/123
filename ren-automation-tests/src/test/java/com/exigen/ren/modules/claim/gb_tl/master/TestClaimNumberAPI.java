/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ClaimConstants.ClaimStatus;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.claim.ClaimRestContext;
import com.exigen.ren.rest.claim.model.common.fnol.FNOLResponseModel;
import org.testng.annotations.Test;

import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimNumberAPI extends ClaimGroupBenefitsTLBaseTest implements ClaimRestContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23320", component = CLAIMS_GROUPBENEFITS)
    public void testClaimNumberAPI() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerId = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        createDefaultTermLifeInsuranceMasterPolicy();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("---=={Step 1-2}==---");
        createDefaultTermLifeInsuranceClaimForMasterPolicy();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        String regExp = "C%s[3]5\\d{5}";
        assertThat(claimNumber.matches(String.format(regExp, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("YYMMdd"))))).isTrue();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.INITIAL);

        LOGGER.info("---=={Step 3}==---");
        LOGGER.info("TEST: Submit Claim #" + claimNumber);
        claim.claimSubmit().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.NOTIFICATION);

        LOGGER.info("TEST: Open Claim #" + claimNumber);
        claim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.OPEN);
        String claimNumber2 = ClaimSummaryPage.getClaimNumber();
        assertThat(claimNumber2).isEqualTo(claimNumber);

        LOGGER.info("---=={Step 6-7}==---");
        ResponseContainer<FNOLResponseModel> response = claimCoreRestService.postClaimFNOL(termLifeClaim.defaultTestData().getTestData("REST", "Test_Data_FNOL")
                .adjust(TestData.makeKeyPath("claim", "customerNumber"), customerId)
                .adjust(TestData.makeKeyPath("claim", "policyNumber"), policyNumber));

        assertThat(response.getResponse()).hasStatus(200);
        assertThat(response.getModel().getIdentifier().matches(String.format(regExp, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("YYMMdd"))))).isTrue();
    }
}