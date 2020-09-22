/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.claim.gb_ac.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.mywork.tabs.MyWorkTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData.PARTY_NAME;
import static com.exigen.ren.main.modules.mywork.tabs.MyWorkTab.MyWorkTasks.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInformClaimsUserAboutIndividualCustomerDetailsChanged extends ClaimGroupBenefitsACBaseTest {


    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-43868", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUpdateRecovery() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentCertificatePolicy();
        String customerFullName = PolicySummaryPage.labelCustomerName.getValue();
        String customerFirstName = customerFullName.split(" ")[0];

        LOGGER.info("---=={Step 1}==---");
        accHealthClaim.create(accHealthClaim.getGbACTestData().getTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(additionalPartiesAdditionalPartyTab.getMetaKey(), PARTY_NAME.getLabel()), String.format("contains=%s", customerFirstName)));
        claim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        navigateToCustomer();

        LOGGER.info("---=={Step 2}==---");
        customerNonIndividual.update().start();
        generalTab.getAssetList().getAsset(GeneralTabMetaData.FIRST_NAME).setValue("TesterFirst");
        generalTab.getAssetList().getAsset(GeneralTabMetaData.MIDDLE_NAME).setValue("ASD");
        generalTab.getAssetList().getAsset(GeneralTabMetaData.LAST_NAME).setValue("TesterLast");
        generalTab.getAssetList().getAsset(GeneralTabMetaData.SSN_TAX_IDENTIFICATION).setValue("111-10-0011");
        generalTab.getAssetList().getAsset(GeneralTabMetaData.DATE_OF_BIRTH).setValue("01/01/1991");
        generalTab.getAssetList().getAsset(GeneralTabMetaData.GENDER).setValue("Female");
        Tab.buttonSaveAndExit.click();

        CustomerSummaryPage.buttonTasks.click();
        assertThat(MyWorkTab.tableMyWorkTasks).with(TASK_NAME, "Customer Change Notification").with(REFERENCE_ID, claimNumber)
                .with(QUEUE, "Life & DI Claim Management").hasMatchingRows(1);

        LOGGER.info("---=={Step 3}==---");
        accHealthClaim.create(accHealthClaim.getGbACTestData().getTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(additionalPartiesAdditionalPartyTab.getMetaKey(), PARTY_NAME.getLabel()), String.format("contains=%s", customerFirstName)));
        claim.claimClose().perform(tdClaim.getTestData("ClaimClose", TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.CLOSED);
        String claimNumber2 = ClaimSummaryPage.getClaimNumber();

        navigateToCustomer();
        customerNonIndividual.update().start();
        generalTab.getAssetList().getAsset(GeneralTabMetaData.FIRST_NAME).setValue(customerFirstName);
        Tab.buttonSaveAndExit.click();

        CustomerSummaryPage.buttonTasks.click();
        assertThat(MyWorkTab.tableMyWorkTasks).with(TASK_NAME, "Customer Change Notification").with(REFERENCE_ID, claimNumber2)
                .with(QUEUE, "Life & DI Claim Management").hasMatchingRows(0);
    }
}
