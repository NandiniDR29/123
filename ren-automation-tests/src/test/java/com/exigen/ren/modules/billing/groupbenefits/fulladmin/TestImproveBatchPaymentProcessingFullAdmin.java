/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.metadata.PaymentsMaintenanceMetaData.SelectGroupInsuranceCustomer;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.PaymentsMaintenanceContext;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata.AddPaymentBatchActionTabMetaData;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.pages.ViewSuspensePage;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs.AddPaymentBatchActionTab;
import com.exigen.ren.main.modules.billing.paymentsmaintenance.tabs.AllocationsActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.main.pages.summary.billing.PaymentsAndBillingMaintenancePage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.BamConstants.ADD_SUSPENSE;
import static com.exigen.ren.main.enums.BamConstants.FINISHED;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.billing.paymentsmaintenance.metadata.AddPaymentBatchActionTabMetaData.BATCH_REFERENCE;
import static com.exigen.ren.main.pages.summary.billing.PaymentsAndBillingMaintenancePage.BatchPaymentList.REFERENCE;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestImproveBatchPaymentProcessingFullAdmin extends GroupBenefitsBillingBaseTest implements BillingAccountContext, PaymentsMaintenanceContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-27214", component = BILLING_GROUPBENEFITS)
    public void testImproveBatchPaymentProcessingFullAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicyFullAdmin();

        commonSteps(customerName);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-27214", component = BILLING_GROUPBENEFITS)
    public void testImproveBatchPaymentProcessingSelfAdmin() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();

        commonSteps(customerName);
    }

    private void commonSteps(String customerName) {
        navigateToBillingAccount(masterPolicyNumber.get());
        String billingAccountNumber = getBillingAccountNumber(masterPolicyNumber.get());
        LOGGER.info("TEST: Generate Future Statement for Policy # " + masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("---=={Step 1-3}==---");
        Currency secondAmount = new Currency(2);
        String modalPremiumAmountSum = modalPremiumAmount.get().add(secondAmount).toString();
        TestData td =  tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(AddPaymentBatchActionTab.class.getSimpleName(), AddPaymentBatchActionTabMetaData.TOTAL_AMOUNT.getLabel()), modalPremiumAmountSum);
        List<TestData> list = td.getTestDataList(AddPaymentBatchActionTab.class.getSimpleName(), AddPaymentBatchActionTabMetaData.PAYMENT_LIST.getLabel());
        list.get(0).adjust(TestData.makeKeyPath(AddPaymentBatchActionTabMetaData.PaymentListData.SELECT_GROUP_INSURANCE_CUSTOMER.getLabel(), SelectGroupInsuranceCustomer.BILLING_ACCOUNT.getLabel()), billingAccountNumber)
                .adjust(TestData.makeKeyPath(AddPaymentBatchActionTabMetaData.PaymentListData.AMOUNT.getLabel()), modalPremiumAmount.get().toString())
                .adjust(TestData.makeKeyPath(AddPaymentBatchActionTabMetaData.PaymentListData.ALLOCATION_SEARCH.getLabel(), AddPaymentBatchActionTabMetaData.AllocationSearchMetaData.BILLING_ACCOUNT.getLabel()), billingAccountNumber);

        list.get(1).adjust(TestData.makeKeyPath(AddPaymentBatchActionTabMetaData.PaymentListData.AMOUNT.getLabel()), secondAmount.toString());
        td.adjust(TestData.makeKeyPath(AddPaymentBatchActionTab.class.getSimpleName(), AddPaymentBatchActionTabMetaData.PAYMENT_LIST.getLabel()), list);
        paymentsMaintenance.addPaymentBatch().perform(td);

        assertThat(AddPaymentBatchActionTab.tablePaymentList).hasRows(2);

        LOGGER.info("---=={Step 4}==---");
        AddPaymentBatchActionTab.tablePaymentList.getRow(1).getCell(AddPaymentBatchActionTab.PaymentListColumns.ACTION.getName()).controls.links.get(ActionConstants.ALLOCATE).click();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Invoice #"))).isAbsent();
        Tab.navButtonCancel.click();

        LOGGER.info("---=={Step 5}==---");
        AddPaymentBatchActionTab.tablePaymentList.getRow(2).getCell(AddPaymentBatchActionTab.PaymentListColumns.ACTION.getName()).controls.links.get(ActionConstants.ALLOCATE).click();
        assertThat(new AllocationsActionTab().getAssetList().getAsset(AddPaymentBatchActionTabMetaData.AllocationSearchMetaData.SELECT_ACTION)).hasValue("Suspend Payment").isDisabled();
        Tab.navButtonCancel.click();
        String ref = paymentsMaintenance.addPaymentBatch().getWorkspace().getTab(AddPaymentBatchActionTab.class).getAssetList().getAsset(BATCH_REFERENCE).getValue();

        LOGGER.info("---=={Step 6}==---");
        paymentsMaintenance.addPaymentBatch().submit();
        mainApp().close();
        mainApp().reopen();
        navigateToBillingAccount(masterPolicyNumber.get());
        BillingSummaryPage.buttonPaymentsBillingMaintenance.click();

        PaymentsAndBillingMaintenancePage.tablePaymentBatches.getRow(1).getCell(PaymentsAndBillingMaintenancePage.SuspenseTable.COMPLETE.getName()).controls.links.getFirst().click();

        LOGGER.info(String.format("Verify Reference #%s, is present in the Payment Batches List", ref));
        RetryService.run(predicate ->  PaymentsAndBillingMaintenancePage.tablePaymentBatchesList.getRow(ImmutableMap.of(REFERENCE.getName(), ref)).isPresent(),
                ()-> { BrowserController.get().driver().navigate().refresh();
                    return null;},
                StopStrategies.stopAfterAttempt(30),
                WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertThat(PaymentsAndBillingMaintenancePage.tablePaymentBatchesList)
                .with(REFERENCE, ref)
                .with(PaymentsAndBillingMaintenancePage.BatchPaymentList.PAYMENTS, 2)
                .with(PaymentsAndBillingMaintenancePage.BatchPaymentList.TOTAL_AMOUNT, modalPremiumAmountSum)
                .with(PaymentsAndBillingMaintenancePage.BatchPaymentList.STATUS, "Complete")
                .hasMatchingRows(1);

        PaymentsAndBillingMaintenancePage.tablePaymentBatchesList.getRow(ImmutableMap.of(REFERENCE.getName(), ref))
                .getCell(REFERENCE.getName()).controls.links.getFirst().click();

        assertThat(AddPaymentBatchActionTab.tablePaymentList)
                .with(AddPaymentBatchActionTab.PaymentListColumns.AMOUNT, modalPremiumAmount.get())
                .with(AddPaymentBatchActionTab.PaymentListColumns.PAYOR, customerName)
                .with(AddPaymentBatchActionTab.PaymentListColumns.CHECK_NUMBER, 1)
                .hasMatchingRows(1);

        assertThat(AddPaymentBatchActionTab.tablePaymentList)
                .with(AddPaymentBatchActionTab.PaymentListColumns.AMOUNT, secondAmount)
                .with(AddPaymentBatchActionTab.PaymentListColumns.PAYOR, EMPTY)
                .with(AddPaymentBatchActionTab.PaymentListColumns.CHECK_NUMBER, 2)
                .hasMatchingRows(1);

        Tab.navButtonCancel.click();
        Tab.buttonBack.click();

        LOGGER.info("---=={Step 7}==---");
        paymentsMaintenance.viewSuspense().perform(paymentsMaintenance.getDefaultTestData("ViewSuspense", "TestDataAmount"), secondAmount, secondAmount);
        String refNum = ViewSuspensePage.tableSuspense.getRow(ImmutableMap.of(ViewSuspensePage.SuspenseListTable.BILLING_ACCOUNTS.getName(), EMPTY))
                .getCell(ViewSuspensePage.SuspenseListTable.REFERENCE.getName()).getValue();
        Tab.buttonBack.click();

        BillingAccountsListPage.verifyBamActivities(String.format(ADD_SUSPENSE, refNum, secondAmount), FINISHED);
    }
}
