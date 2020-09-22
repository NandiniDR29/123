package com.exigen.ren.modules.billing.api;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.model.BillingInvoiceCoveragePeriodDateModel;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.stream.IntStream;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.DISCARDED_ESTIMATED;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED_ESTIMATED;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBillingGETInvoiceCoveragePeriodsWithDate extends GroupBenefitsBillingBaseTest implements BillingAccountContext, RoleContext, ProfileContext, CustomerContext, CaseProfileContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36855", component = BILLING_REST)
    public void testBillingGETInvoiceCoveragePeriods() {

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

        billingAccount.discardBill().perform(new SimpleDataProvider());
        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, DISCARDED_ESTIMATED).hasMatchingRows(1);
        createPolicyFullAdmin();
        String ba2 = billingAccountNumber.get();

        LOGGER.info("---=={Step 1}==---");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate("ba2test", startDate, endDate);
        assertThat(response.getResponse().getStatus()).isEqualTo(404);
        assertThat(response.getError().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_FOUND");
        assertThat(response.getError().getMessage()).isEqualTo(String.format("Billing account #%s is not found", "ba2test"));

        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response2 = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(ba2, startDate, endDate);
        assertThat(response2.getResponse().getStatus()).isEqualTo(422);
        assertThat(response2.getError().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_SELF_ADMIN");
        assertThat(response2.getError().getMessage()).isEqualTo(String.format("Billing account #%s is not self-administered", ba2));

        LOGGER.info("---=={Step 2}==---");
        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response3 = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(ba1, startDate, endDate);
        assertThat(response3.getResponse().getStatus()).isEqualTo(422);
        assertThat(response3.getError().getErrorCode()).isEqualTo("ONLY_DISCARDED_INVOICES_FOUND");
        assertThat(response3.getError().getMessage()).isEqualTo("Specified Billing Account has only invoices with status ’Discarded’");

        MainPage.QuickSearch.search(ba1);
        billingAccount.regenerateBill().perform(new SimpleDataProvider(), 1);
        String invoiceNumber = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumber);
        String policyPlan =  BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getRow(1)
                .getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.POLICY_PLAN.getName()).getValue();
        String rate = BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getRow(1)
                .getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.RATE.getName()).getValue();
        String numberOfInsureds = BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getRow(1)
                .getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.PARTICIPANTS.getName()).getValue();
        String benefitsAmountOrVolume = BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getRow(1)
                .getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.VOLUME.getName()).getValue();

        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response4 = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(ba1, startDate, endDate);
        assertThat(response4.getResponse().getStatus()).isEqualTo(200);

        assertSoftly(softly -> {
            softly.assertThat(response4.getModel().getInvoiceNumber()).isEqualTo(invoiceNumber);
            BillingInvoiceCoveragePeriodDateModel.PremiumItems model = response4.getModel().getPremiumItems().stream()
                    .filter(premiumItems -> premiumItems.getCoverage().getPolicyPackageName().equals(policyPlan))
                    .findFirst().orElseThrow(() -> new IstfException(String.format("Policy plan %s not found", policyPlan)));

            softly.assertThat(model.getItemPeriod().getStartDate()).isEqualTo(startDate);
            softly.assertThat(model.getItemPeriod().getEndDate()).isEqualTo(endDate);
            softly.assertThat(model.getPeriodRate().getRate()).isEqualTo(rate);
            softly.assertThat(model.getPeriodRate().getNumberOfInsureds()).isEqualTo(numberOfInsureds);
            softly.assertThat(new Currency(model.getPeriodRate().getBenefitsAmountOrVolume()).toString()).isEqualTo(benefitsAmountOrVolume);
            softly.assertThat(model.getPremiumRate().getRate()).isEqualTo(rate);
            softly.assertThat(model.getPremiumRate().getNumberOfInsureds()).isEqualTo(numberOfInsureds);
            softly.assertThat(new Currency(model.getPremiumRate().getBenefitsAmountOrVolume()).toString()).isEqualTo(benefitsAmountOrVolume);
        });

        LOGGER.info("---=={Step 3}==---");
        billingAccount.generateFutureStatement().perform();
        billingAccount.generateFutureStatement().perform();
        String invoiceNumber3 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();
        String billingPeriod3 = BillingSummaryPage.tableBillsAndStatements.getRow(ImmutableMap.of(TableConstants.BillingBillsAndStatementsGB.INVOICE.getName(), invoiceNumber3))
                .getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();

        String startDate3 = LocalDate.parse(billingPeriod3.split(" - ")[0], MM_DD_YYYY).format(YYYY_MM_DD);
        String endDate3 = LocalDate.parse(billingPeriod3.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        BillingSummaryPage.expandBillsStatementsInvoiceViewByInvoice(invoiceNumber3);
        String policyPlan3 =  BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getRow(1)
                .getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.POLICY_PLAN.getName()).getValue();
        String rate3 = BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getRow(1)
                .getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.RATE.getName()).getValue();
        String numberOfInsureds3 = BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getRow(1)
                .getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.PARTICIPANTS.getName()).getValue();
        String benefitsAmountOrVolume3 = BillingSummaryPage.tableCurrentPeriodForBillCovBillGroupsByInvoice.getRow(1)
                .getCell(TableConstants.BillableCoveragesBillingGroupsByInvoice.VOLUME.getName()).getValue();

        IntStream.range(0, 3).forEach($ -> billingAccount.generateFutureStatement().perform(new SimpleDataProvider()));

        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED_ESTIMATED).hasMatchingRows(6);

        ResponseContainer<BillingInvoiceCoveragePeriodDateModel> response5 = billingBenefitsRestService.getAccountsInvoiceCoveragePeriodsWithDate(ba1, startDate3, endDate3);
        assertThat(response5.getResponse().getStatus()).isEqualTo(200);

        assertSoftly(softly -> {
            softly.assertThat(response5.getModel().getInvoiceNumber()).isEqualTo(invoiceNumber3);
            BillingInvoiceCoveragePeriodDateModel.PremiumItems model = response5.getModel().getPremiumItems().stream()
                    .filter(premiumItems -> premiumItems.getCoverage().getPolicyPackageName().equals(policyPlan3))
                    .findFirst().orElseThrow(() -> new IstfException(String.format("Policy plan %s not found", policyPlan3)));

            softly.assertThat(model.getItemPeriod().getStartDate()).isEqualTo(startDate3);
            softly.assertThat(model.getItemPeriod().getEndDate()).isEqualTo(endDate3);
            softly.assertThat(model.getPeriodRate().getRate()).isEqualTo(rate3);
            softly.assertThat(model.getPeriodRate().getNumberOfInsureds()).isEqualTo(numberOfInsureds3);
            softly.assertThat(new Currency(model.getPeriodRate().getBenefitsAmountOrVolume()).toString()).isEqualTo(benefitsAmountOrVolume3);
            softly.assertThat(model.getPremiumRate().getRate()).isEqualTo(rate3);
            softly.assertThat(model.getPremiumRate().getNumberOfInsureds()).isEqualTo(numberOfInsureds3);
            softly.assertThat(new Currency(model.getPremiumRate().getBenefitsAmountOrVolume()).toString()).isEqualTo(benefitsAmountOrVolume3);
        });
    }
}