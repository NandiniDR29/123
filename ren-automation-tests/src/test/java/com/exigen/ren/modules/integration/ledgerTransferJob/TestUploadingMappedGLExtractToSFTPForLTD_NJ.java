/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.integration.ledgerTransferJob;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.helpers.file.FileHelper;
import com.exigen.ren.main.modules.claim.GroupBenefitsClaimType;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitReservesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimLTDContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.exigen.ren.TestDataKey.ACCEPT_CONTRACT;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYYMMDD;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.modules.integration.ledgerTransferJob.LedgerTransferJobBaseTest.PeopleSoftProductCode.EILTD;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUploadingMappedGLExtractToSFTPForLTD_NJ extends LedgerTransferJobBaseTest implements LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext, DisabilityClaimLTDContext {

    private TestData paymentData = tdClaim.getTestData("ClaimPayment", "TestData_PartialPayment");
    private String reverseType = paymentData.getValue(
            PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.RESERVE_TYPE.getLabel());
    private Currency reverseAmount = new Currency(tdClaim.getTestData("BenefitReserves", TestDataKey.DEFAULT_TEST_DATA_KEY).getValue(
            BenefitReservesActionTab.class.getSimpleName(), reverseType + " Reserve"));

    @Test(groups = {INTEGRATION, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = {"REN-12930", "REN-12931", "REN-13288", "REN-13767"}, component = INTEGRATION)
    public void testUploadingMappedGLExtractToSFTPForLTD_NJ() {
        LocalDateTime policyCreationDate = TimeSetterUtil.getInstance().getCurrentTime();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), STATE_NJ)
                .mask(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), COUNTY_CODE.getLabel()))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));

        createDefaultLongTermDisabilityClaimForMasterPolicy();
        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", TestDataKey.DEFAULT_TEST_DATA_KEY), 1);

        LOGGER.info("TEST: Post Partial Payment for Claim #" + claimNumber);
        Currency paymentAmount = reverseAmount.subtract(new Currency(getRandomPaymentValue()));

        LOGGER.info("TEST: Payment include in ledger file");
        LOGGER.info("TEST: accountingDateFrom < accountingDateFrom + 1d(Payment date) < accountingDateFrom + 3d(accountingDateTo)");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));

        mainApp().open();
        MainPage.QuickSearch.search(claimNumber);
        claim.addPayment().perform(paymentData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT.getLabel()),
                        paymentAmount.toPlainString())
                .resolveLinks());

        LOGGER.info("TEST: Synchronized time with other Ledger tests");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(2));
        runLedgerTransferJob(policyCreationDate);

        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        String destinationDir = FileHelper.downloadFile(DIR_PATH, getFullFileName(String.format(FILE_NAME_PS, currentDate.format(YYYYMMDD))));
        List<String> list = readFile(destinationDir);

        checkCountOfLines(list, STATE_NJ, EILTD, paymentAmount.toPlainString(), 2);
        checkJournalHeaderSection(list, STATE_NJ, policyCreationDate.plusDays(1));
        checkJournalLineSection(list, STATE_NJ, EILTD, paymentAmount);
    }


    @Override
    protected GroupBenefitsClaimType getClaimType() {
        return GroupBenefitsClaimType.CLAIM_DI_LTD;
    }
}
