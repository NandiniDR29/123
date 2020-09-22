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
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitReservesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.modules.claim.gb_ac.AccidentHealthClaimACContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYYMMDD;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.modules.integration.ledgerTransferJob.LedgerTransferJobBaseTest.PeopleSoftProductCode.EIACC;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUploadingMappedGLExtractToSFTPForAC_GA extends LedgerTransferJobBaseTest implements GroupAccidentMasterPolicyContext, GroupAccidentCertificatePolicyContext, AccidentHealthClaimACContext {

    private TestData paymentData = tdClaim.getTestData("ClaimPayment", "TestData_PartialPayment");
    private String reverseType = paymentData.getValue(
            PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.RESERVE_TYPE.getLabel());
    private Currency reverseAmount = new Currency(tdClaim.getTestData("BenefitReserves", TestDataKey.DEFAULT_TEST_DATA_KEY).getValue(
            BenefitReservesActionTab.class.getSimpleName(), reverseType + " Reserve"));

    @Test(groups = {INTEGRATION, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = {"REN-12930", "REN-12931", "REN-13288", "REN-13767"}, component = INTEGRATION)
    public void testUploadingMappedGLExtractToSFTPForAC_GA() {
        LocalDateTime policyCreationDate = TimeSetterUtil.getInstance().getCurrentTime();
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), STATE_GA));

        createDefaultGroupAccidentClaimForMasterPolicy();
        claim.claimOpen().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_AccidentalDeath"), 1);
        claim.addBenefitReserves().perform(tdClaim.getTestData("BenefitReserves", "TestData"), 1);

        LOGGER.info("TEST: Post Partial Payment for Claim #" + claimNumber);
        Currency paymentAmount = reverseAmount.subtract(new Currency(getRandomPaymentValue()));

        LOGGER.info("TEST: Payment include in ledger file");
        LOGGER.info("TEST: accountingDateFrom < accountingDateFrom + 2d(Payment date) < accountingDateFrom + 3d(accountingDateTo)");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(2));

        mainApp().open();
        MainPage.QuickSearch.search(claimNumber);
        claim.addPayment().perform(paymentData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PaymentPaymentPaymentDetailsTabMetaData.GROSS_AMOUNT.getLabel()),
                        paymentAmount.toPlainString())
                .resolveLinks()
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT.getLabel()),
                        paymentAmount.toPlainString())
                .resolveLinks());

        LOGGER.info("TEST: Payment doesn't include in ledger file");
        LOGGER.info("TEST: accountingDateFrom < accountingDateFrom + 3d(Payment date) < accountingDateFrom + 3d(accountingDateTo)");
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));

        mainApp().open();
        MainPage.QuickSearch.search(claimNumber);
        claim.addPayment().perform(paymentData
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PaymentPaymentPaymentDetailsTabMetaData.GROSS_AMOUNT.getLabel()),
                        paymentAmount.toPlainString())
                .resolveLinks()
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentAllocationTab.class.getSimpleName(), PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT.getLabel()),
                        paymentAmount.toPlainString())
                .resolveLinks());

        runLedgerTransferJob(policyCreationDate);

        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        String destinationDir = FileHelper.downloadFile(DIR_PATH, getFullFileName(String.format(FILE_NAME_PS, currentDate.format(YYYYMMDD))));
        List<String> list = readFile(destinationDir);

        checkCountOfLines(list, STATE_GA, EIACC, paymentAmount.toPlainString(), 2);
        checkJournalHeaderSection(list, STATE_GA, policyCreationDate.plusDays(1));
        checkJournalLineSection(list, STATE_GA, EIACC, paymentAmount);
    }

    @Override
    protected GroupBenefitsClaimType getClaimType() {
        return GroupBenefitsClaimType.CLAIM_ACC_HEALTH_AC;
    }
}
