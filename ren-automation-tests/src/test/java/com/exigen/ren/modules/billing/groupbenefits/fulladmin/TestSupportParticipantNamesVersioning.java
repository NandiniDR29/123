/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.FIRST_NAME;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.LAST_NAME;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.RoleSummary.CUSTOMER_NAME;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSupportParticipantNamesVersioning extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-46352", component = BILLING_GROUPBENEFITS)
    public void testDeclinePaymentFullAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicyFullAdmin();

        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        navigateToBillingAccount(masterPolicyNumber.get());
        LOGGER.info("TEST: Generate Future Statement for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("---=={Step 1}==---");
        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        TestData tdEndorse = groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestDataEndorsement");
        String customerFirstName2 = tdEndorse.getValue(insuredTab.getMetaKey(), FIRST_NAME.getLabel());
        String customerLastName2 = tdEndorse.getValue(insuredTab.getMetaKey(), LAST_NAME.getLabel());


        groupAccidentCertificatePolicy.createEndorsement(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(tdEndorse.resolveLinks())
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));

        navigateToBillingAccount(masterPolicyNumber.get());

        billingAccount.generateFutureStatement().perform();

        String invoiceNumber2 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumber2);
        BillingSummaryPage.tableBillableCoveragesByBillingGroup.getRow(1).getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName())
                .controls.links.getFirst().click();

        assertThat(BillingSummaryPage.tableBillableCoveragesParticipantsByCoverageByLocation)
                .with(TableConstants.BillableCoveragesParticipantsByCoverageByLocation.PARTICIPANT, String.format("%s %s", customerFirstName2, customerLastName2))
                .hasMatchingRows(1);

        LOGGER.info("---=={Step 2}==---");
        MainPage.QuickSearch.search(certificatePolicyNumber.get());

        TestData tdEndorse2 = groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, "TestDataEndorsement");
        String customerFirstName3 = tdEndorse2.getValue(insuredTab.getMetaKey(), FIRST_NAME.getLabel());
        String customerLastName3 = tdEndorse2.getValue(insuredTab.getMetaKey(), LAST_NAME.getLabel());

        groupAccidentCertificatePolicy.createEndorsement(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, DEFAULT_TEST_DATA_KEY)
                .adjust(tdEndorse2.resolveLinks())
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY).resolveLinks()));

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.discardBill().perform(new SimpleDataProvider(), 1);

        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumber2);
        BillingSummaryPage.tableBillableCoveragesByBillingGroup.getRow(1).getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName())
                .controls.links.getFirst().click();

        assertThat(BillingSummaryPage.tableBillableCoveragesParticipantsByCoverageByLocation)
                .with(TableConstants.BillableCoveragesParticipantsByCoverageByLocation.PARTICIPANT, String.format("%s %s", customerFirstName2, customerLastName2))
                .hasMatchingRows(1);

        billingAccount.regenerateBill().perform(new SimpleDataProvider(), 1);
        String invoiceNumber3 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumber3);
        BillingSummaryPage.tableBillableCoveragesByBillingGroup.getRow(1).getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.COVERAGE.getName())
                .controls.links.getFirst().click();

        assertThat(BillingSummaryPage.tableBillableCoveragesParticipantsByCoverageByLocation)
                .with(TableConstants.BillableCoveragesParticipantsByCoverageByLocation.PARTICIPANT, String.format("%s %s", customerFirstName3, customerLastName3))
                .hasMatchingRows(1);
    }
}
