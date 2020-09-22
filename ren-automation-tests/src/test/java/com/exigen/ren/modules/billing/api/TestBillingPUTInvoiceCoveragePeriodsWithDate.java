package com.exigen.ren.modules.billing.api;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.main.pages.summary.billing.ModalPremiumSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.model.BillingInvoiceCoveragePeriodDateModel;
import com.exigen.ren.rest.billing.model.BillingInvoiceCoveragePeriodDatePUTModel;
import com.exigen.ren.rest.model.RestError;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Collections;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.BamConstants.FINISHED;
import static com.exigen.ren.main.enums.BamConstants.MODAL_PREMIUM_FOR_BILLING_GROUP;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.DISCARDED_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.BASIC_ACCIDENT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.BASE_BUY_UP;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBillingPUTInvoiceCoveragePeriodsWithDate extends GroupBenefitsBillingBaseTest implements BillingAccountContext, RoleContext, ProfileContext, CustomerContext, CaseProfileContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36856", component = BILLING_REST)
    public void testBillingPUTInvoiceCoveragePeriodsWithDateTC1() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createPolicySelfAdmin();
        String ba1 = billingAccountNumber.get();

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        String billingPeriod = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();

        String startDate = LocalDate.parse(billingPeriod.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDate = LocalDate.parse(billingPeriod.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        createPolicyFullAdmin();
        String ba2 = billingAccountNumber.get();

        LOGGER.info("---=={Step 1}==---");
        BillingInvoiceCoveragePeriodDatePUTModel model = new BillingInvoiceCoveragePeriodDatePUTModel();
        model.setTransactionReason("test");
        BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems premiumItemsModel = new BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems();
        premiumItemsModel.setItemId("3");
        premiumItemsModel.setPeriodAmount("2");
        premiumItemsModel.setNumberOfInsureds("0");
        premiumItemsModel.setBenefitsAmountOrVolume("0");
        model.setPremiumItems(Collections.singletonList(premiumItemsModel));

        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response = billingBenefitsRestService.putAccountsInvoiceCoveragePeriodsWithDate(model, "ba2test", startDate, endDate);
        assertThat(response.getResponse().getStatus()).isEqualTo(404);
        assertThat(response.getError().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_FOUND");
        assertThat(response.getError().getMessage()).isEqualTo(String.format("Billing account #%s is not found", "ba2test"));

        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response2 = billingBenefitsRestService.putAccountsInvoiceCoveragePeriodsWithDate(model, ba2, startDate, endDate);
        assertThat(response2.getResponse().getStatus()).isEqualTo(422);
        assertThat(response2.getError().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_SELF_ADMIN");
        assertThat(response2.getError().getMessage()).isEqualTo(String.format("Billing account #%s is not self-administered", ba2));

        LOGGER.info("---=={Step 2}==---");
        BillingInvoiceCoveragePeriodDatePUTModel model2 = new BillingInvoiceCoveragePeriodDatePUTModel();
        model2.setTransactionReason("");
        BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems premiumItemsModel2 = new BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems();
        premiumItemsModel2.setItemId("");
        premiumItemsModel2.setPeriodAmount("");
        premiumItemsModel2.setNumberOfInsureds(null);
        premiumItemsModel2.setBenefitsAmountOrVolume("");
        model2.setPremiumItems(Collections.singletonList(premiumItemsModel2));
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response3 = billingBenefitsRestService.putAccountsInvoiceCoveragePeriodsWithDate(model2, ba1, startDate, endDate);

        assertThat(response3.getResponse().getStatus()).isEqualTo(422);
        assertThat(response3.getError().getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response3.getError().getMessage()).isEqualTo("The request was well-formed but was unable to be followed due to semantic errors (invalid field value, validation rules and etc)");
        assertThat(response3.getError().getErrors().stream().map(RestError::getField)).containsExactlyInAnyOrder("premiumItems[0].itemId", "transactionReason", "premiumItems[0].benefitsAmountOrVolume", "premiumItems[0].numberOfInsureds", "premiumItems[0].periodAmount");

    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36856", component = BILLING_REST)
    public void testBillingPUTInvoiceCoveragePeriodsWithDateTC2() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createPolicySelfAdmin();
        String ba1 = billingAccountNumber.get();

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();
        String billingPeriod = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();

        String startDate = LocalDate.parse(billingPeriod.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDate = LocalDate.parse(billingPeriod.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        LOGGER.info("---=={Step 1}==---");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(ba1, startDate, endDate);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);

        BillingInvoiceCoveragePeriodDateModel.PremiumItems model = response.getModel().getPremiumItems().stream()
                .filter(premiumItems -> premiumItems.getCoverage().getPolicyPackageName().equals(BASE_BUY_UP))
                .findFirst().orElseThrow(() -> new IstfException(String.format("Policy plan %s not found", BASE_BUY_UP)));
        String itemId = model.getItemId();

        BillingInvoiceCoveragePeriodDatePUTModel putModel = new BillingInvoiceCoveragePeriodDatePUTModel();
        String transactionReason = "test1";
        putModel.setTransactionReason(transactionReason);
        BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems premiumItemsModel = new BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems();
        premiumItemsModel.setItemId(itemId);
        premiumItemsModel.setPeriodAmount(modalPremiumAmount.get().toPlainString());
        premiumItemsModel.setNumberOfInsureds("11");
        premiumItemsModel.setBenefitsAmountOrVolume("22");
        putModel.setPremiumItems(Collections.singletonList(premiumItemsModel));
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response2 = billingBenefitsRestService.putAccountsInvoiceCoveragePeriodsWithDate(putModel, ba1, startDate, endDate);
        assertThat(response2.getResponse().getStatus()).isEqualTo(204);

        navigateToBillingAccount(masterPolicyNumber.get());
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED).hasMatchingRows(1);
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, DISCARDED_ESTIMATED).hasMatchingRows(1);
        String invoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(TableConstants.BillingBillsAndStatementsGB.STATUS.getName(), ISSUED).getCell(INVOICE).getValue();

        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumber);

        assertThat(BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice)
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.POLICY_PLAN, BASE_BUY_UP)
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.PARTICIPANTS, "11")
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.VOLUME, new Currency("22").toString())
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM, modalPremiumAmount.get().toString()).hasMatchingRows(1);

        billingAccount.viewModalPremium().start();
        assertThat(ModalPremiumSummaryPage.tableModalPremium)
                .with(ModalPremiumSummaryPage.ModalPremiums.POLICY_PLAN, BASE_BUY_UP)
                .with(ModalPremiumSummaryPage.ModalPremiums.AMOUNT, modalPremiumAmount.get().toString())
                .with(ModalPremiumSummaryPage.ModalPremiums.TRANSACTION_TYPE_SUBTYPE_REASON, String.format("Invoice Premium Update ( - / %s)", transactionReason)).hasMatchingRows(1);

        navigateToBillingAccount(masterPolicyNumber.get());
        String billingGroup = BillingSummaryPage.tableBillableCoverages.getRow(1).getCell(TableConstants.BillingBillableCoveragesGB.BILLING_GROUP.getName()).getValue();
        String billingGroupValue = billingGroup.substring(billingGroup.indexOf('(') + 1, billingGroup.indexOf(')'));
        BillingAccountsListPage.verifyBamActivities(String.format(MODAL_PREMIUM_FOR_BILLING_GROUP, billingGroupValue, billingPeriod.split(" - ")[0]), FINISHED);

        LOGGER.info("---=={Step 2.1}==---");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response3 = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(ba1, startDate, endDate);
        assertThat(response3.getResponse().getStatus()).isEqualTo(200);

        BillingInvoiceCoveragePeriodDateModel.PremiumItems model3 = response3.getModel().getPremiumItems().stream()
                .filter(premiumItems -> premiumItems.getCoverage().getPolicyPackageName().equals(BASE_BUY_UP))
                .findFirst().orElseThrow(() -> new IstfException(String.format("Policy plan %s not found", BASE_BUY_UP)));
        String itemId2 = model3.getItemId();

        billingAccount.generateFutureStatement().perform();
        String invoiceNumber2 = BillingSummaryPage.tableBillsAndStatements.getRow(TableConstants.BillingBillsAndStatementsGB.STATUS.getName(), ISSUED).getCell(INVOICE).getValue();

        billingAccount.discardBill().perform(new SimpleDataProvider());
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumber2);

        assertThat(BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice)
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.POLICY_PLAN, BASE_BUY_UP)
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.PARTICIPANTS, "11")
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.VOLUME, new Currency("22").toString())
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM, modalPremiumAmount.get().toString()).hasMatchingRows(1);

        LOGGER.info("---=={Step 2.2}==---");
        BillingInvoiceCoveragePeriodDatePUTModel putModel2 = new BillingInvoiceCoveragePeriodDatePUTModel();
        putModel2.setTransactionReason("test2");
        BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems premiumItemsModel2 = new BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems();
        premiumItemsModel2.setItemId(itemId2);
        Currency amount = modalPremiumAmount.get().add(new Currency(1));
        premiumItemsModel2.setPeriodAmount(amount.toPlainString());
        premiumItemsModel2.setNumberOfInsureds("33");
        premiumItemsModel2.setBenefitsAmountOrVolume("44");
        putModel2.setPremiumItems(Collections.singletonList(premiumItemsModel2));
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response4 = billingBenefitsRestService.putAccountsInvoiceCoveragePeriodsWithDate(putModel2, ba1, startDate, endDate);
        assertThat(response4.getResponse().getStatus()).isEqualTo(204);

        navigateToBillingAccount(masterPolicyNumber.get());
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNumber)
                .with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED).hasMatchingRows(1);

        BillingAccountsListPage.verifyBamActivities(String.format(MODAL_PREMIUM_FOR_BILLING_GROUP, billingGroupValue, billingPeriod.split(" - ")[0]), FINISHED);

        LOGGER.info("---=={Step 2.3}==---");
        billingAccount.regenerateBill().perform(new SimpleDataProvider());
        String invoiceNumberRegenerate = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumberRegenerate);

        assertThat(BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice)
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.POLICY_PLAN, BASE_BUY_UP)
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.PARTICIPANTS, "33")
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.VOLUME, new Currency("44").toString())
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM, amount.toString()).hasMatchingRows(1);

        LOGGER.info("---=={Step 2.4}==---");
        billingAccount.generateFutureStatement().perform();
        String invoiceNumber3 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumber3);

        assertThat(BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice)
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.POLICY_PLAN, BASE_BUY_UP)
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.PARTICIPANTS, "33")
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.VOLUME, new Currency("44").toString())
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM, amount.toString()).hasMatchingRows(1);

        LOGGER.info("---=={Step 3.1}==---");
        String billingPeriod2 = BillingSummaryPage.tableBillsAndStatements.getRow(TableConstants.BillingBillsAndStatementsGB.INVOICE.getName(), invoiceNumberRegenerate).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();

        String startDate2 = LocalDate.parse(billingPeriod2.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDate2 = LocalDate.parse(billingPeriod2.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response5 = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(ba1, startDate2, endDate2);
        assertThat(response5.getResponse().getStatus()).isEqualTo(200);

        BillingInvoiceCoveragePeriodDateModel.PremiumItems model4 = response5.getModel().getPremiumItems().stream()
                .filter(premiumItems -> premiumItems.getCoverage().getPolicyPackageName().equals(BASE_BUY_UP))
                .findFirst().orElseThrow(() -> new IstfException(String.format("Policy plan %s not found", BASE_BUY_UP)));
        String itemId4 = model4.getItemId();

        LOGGER.info("---=={Step 3.2}==---");
        BillingInvoiceCoveragePeriodDatePUTModel putModel5 = new BillingInvoiceCoveragePeriodDatePUTModel();
        String transactionReason2 = "test3";
        putModel5.setTransactionReason(transactionReason2);
        BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems premiumItemsModel3 = new BillingInvoiceCoveragePeriodDatePUTModel.PremiumItems();
        premiumItemsModel3.setItemId(itemId4);
        Currency amount2 = modalPremiumAmount.get().add(new Currency(2));
        premiumItemsModel3.setPeriodAmount(amount2.toPlainString());
        premiumItemsModel3.setNumberOfInsureds("33");
        premiumItemsModel3.setBenefitsAmountOrVolume("44");
        putModel5.setPremiumItems(Collections.singletonList(premiumItemsModel3));
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response6 = billingBenefitsRestService.putAccountsInvoiceCoveragePeriodsWithDate(putModel5, ba1, startDate2, endDate2);
        assertThat(response6.getResponse().getStatus()).isEqualTo(204);
        navigateToBillingAccount(masterPolicyNumber.get());
        BillingAccountsListPage.verifyBamActivities(String.format(MODAL_PREMIUM_FOR_BILLING_GROUP, billingGroupValue, billingPeriod2.split(" - ")[0]), FINISHED);

        assertThat(BillingSummaryPage.tableBillsAndStatements).hasRows(6);
        assertThat(BillingSummaryPage.tableBillsAndStatements)
                .with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNumberRegenerate)
                .with(TableConstants.BillingBillsAndStatementsGB.STATUS, DISCARDED_ESTIMATED).hasMatchingRows(1);

        String invoiceNumber2AfterPUT = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.INVOICE, invoiceNumber2AfterPUT)
                .with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED).hasMatchingRows(1);

        LOGGER.info("---=={Step 3.3}==---");
        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumber2AfterPUT);

        assertThat(BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice)
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.POLICY_PLAN, BASE_BUY_UP)
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.PARTICIPANTS, "33")
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.VOLUME, new Currency("44").toString())
                .with(TableConstants.BillableCoveragesBillingGroupsByInvoice.PREMIUM, amount2.toString()).hasMatchingRows(1);

        billingAccount.viewModalPremium().start();
        ModalPremiumSummaryPage.tableModalPremium.getRow(ImmutableMap.of(ModalPremiumSummaryPage.ModalPremiums.COVERAGE.getName(), BASIC_ACCIDENT))
                .getCell(ModalPremiumSummaryPage.ModalPremiums.COVERAGE.getName()).controls.links.getFirst().click();

        assertThat(ModalPremiumSummaryPage.getModalPremiumsTableByBillableCoverage(1))
                .with(ModalPremiumSummaryPage.BillingModalPremiumTable.MODAL_PREMIUM_EFFECTIVE_DATE, LocalDate.parse(billingPeriod2.split(" - ")[0], MM_DD_YYYY))
                .with(ModalPremiumSummaryPage.BillingModalPremiumTable.AMOUNT, amount2.toString())
                .with(ModalPremiumSummaryPage.BillingModalPremiumTable.TRANSACTION_TYPE_SUBTYPE_REASON, String.format("Invoice Premium Update ( - / %s)", transactionReason2)).hasMatchingRows(1);


    }
}