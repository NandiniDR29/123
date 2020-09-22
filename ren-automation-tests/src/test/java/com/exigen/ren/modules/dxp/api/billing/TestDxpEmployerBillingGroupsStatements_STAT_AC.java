package com.exigen.ren.modules.dxp.api.billing;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.billing.BillingBenefitsCustomerInvoiceModel;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.*;
import static com.exigen.ren.main.enums.TableConstants.BillingBillsAndStatementsGB.*;
import static com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest.getBillingAccountNumber;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpEmployerBillingGroupsStatements_STAT_AC extends RestBaseTest implements CaseProfileContext, BillingAccountContext,
        GroupAccidentMasterPolicyContext, GroupAccidentCertificatePolicyContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40581", component = BILLING_REST)
    public void testDxpEmployerBillingGroupsStatements_STAT_AC() {
        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        String customerNumNIC = CustomerSummaryPage.labelCustomerNumber.getValue();

        LOGGER.info("Create data for STAT product");
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType(), groupAccidentMasterPolicy.getType());
        createDefaultStatutoryDisabilityInsuranceMasterPolicy();
        String masterPolicyNumber_STAT = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccount_STAT = getBillingAccountNumber(masterPolicyNumber_STAT);
        MainPage.QuickSearch.search(billingAccount_STAT);
        billingAccount.generateFutureStatement().perform(new SimpleDataProvider());
        billingAccount.generateFutureStatement().perform(new SimpleDataProvider());
        billingAccount.discardBill().perform(new SimpleDataProvider());
        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements).with(STATUS, ISSUED_ESTIMATED).hasMatchingRows(1);
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements).with(STATUS, DISCARDED_ESTIMATED).hasMatchingRows(1);
        });
        String issuedInvoice_STAT = BillingSummaryPage.tableBillsAndStatements.getRow(STATUS.getName(), ISSUED_ESTIMATED).getCell(INVOICE.getName()).controls.links.getFirst().getValue();
        String discardedInvoice_STAT = BillingSummaryPage.tableBillsAndStatements.getRow(STATUS.getName(), DISCARDED_ESTIMATED).getCell(INVOICE.getName()).controls.links.getFirst().getValue();

        LOGGER.info("Create data for AC product");
        createDefaultGroupAccidentMasterPolicy();
        String masterPolicyNumber_AC = PolicySummaryPage.labelPolicyNumber.getValue();
        createDefaultGroupAccidentCertificatePolicy();
        String billingAccount_AC = getBillingAccountNumber(masterPolicyNumber_AC);
        MainPage.QuickSearch.search(billingAccount_AC);
        billingAccount.generateFutureStatement().perform(new SimpleDataProvider());
        billingAccount.generateFutureStatement().perform(new SimpleDataProvider());
        billingAccount.discardBill().perform(new SimpleDataProvider());
        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements).with(STATUS, DISCARDED).hasMatchingRows(1);
            softly.assertThat(BillingSummaryPage.tableBillsAndStatements).with(STATUS, ISSUED).hasMatchingRows(1);
        });

        String issuedInvoice_AC = BillingSummaryPage.tableBillsAndStatements.getRow(STATUS.getName(), ISSUED).getCell(INVOICE.getName()).controls.links.getFirst().getValue();
        LocalDate dueDateForIssuedInvoice_AC = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(STATUS.getName(), ISSUED).getCell(DUE_DATE.getName()).getValue(), DateTimeUtils.MM_DD_YYYY);
        Currency totalDueForIssuedInvoice_AC = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(STATUS.getName(), ISSUED).getCell(TOTAL_DUE.getName()).getValue());
        String discardedInvoice_AC = BillingSummaryPage.tableBillsAndStatements.getRow(STATUS.getName(), DISCARDED).getCell(INVOICE.getName()).controls.links.getFirst().getValue();

        LOGGER.info("Step 1");
        ResponseContainer<List<BillingBenefitsCustomerInvoiceModel>> response = dxpRestService.getEmployerBillingGroupsStatements(
                customerNumberAuthorize, customerNumNIC, null, null, null, null, null, null);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        assertThat(response.getModel()).hasSize(4);
        checkInvoice(billingAccount_AC, issuedInvoice_AC, response);
        checkInvoice(billingAccount_AC, discardedInvoice_AC, response);
        checkInvoice(billingAccount_STAT, issuedInvoice_STAT, response);
        checkInvoice(billingAccount_STAT, discardedInvoice_STAT, response);

        BillingBenefitsCustomerInvoiceModel secondInvoiceFromFirstResponse = response.getModel().get(1);

        LOGGER.info("Step 2");
        ResponseContainer<List<BillingBenefitsCustomerInvoiceModel>> response_2 = dxpRestService.getEmployerBillingGroupsStatements(
                customerNumberAuthorize, customerNumNIC, null, null, null, null, "1", "3");
        assertThat(response_2.getResponse().getStatus()).isEqualTo(200);
        assertThat(response_2.getModel()).hasSize(3);
        assertThat(response_2.getModel().get(0)).isEqualTo(secondInvoiceFromFirstResponse);

        LOGGER.info("Steps 3, 4");
        ResponseContainer<List<BillingBenefitsCustomerInvoiceModel>> response_3 = dxpRestService.getEmployerBillingGroupsStatements(
                customerNumberAuthorize, customerNumNIC, billingAccount_AC, issuedInvoice_AC,
                dueDateForIssuedInvoice_AC.format(DateTimeUtilsHelper.YYYY_MM_DD), dueDateForIssuedInvoice_AC.format(DateTimeUtilsHelper.YYYY_MM_DD),
                null, null);
        assertThat(response_3.getResponse().getStatus()).isEqualTo(200);
        assertThat(response_3.getModel()).hasSize(1);
        BillingBenefitsCustomerInvoiceModel invoiceModel = response_3.getModel().get(0);
        assertThat(invoiceModel.getTotalDue().getValue()).isEqualTo(totalDueForIssuedInvoice_AC.toPlainString());
        assertThat(invoiceModel.getInvoiceStatus()).isEqualTo("ISSUED");
    }

    public void checkInvoice(String billingAccountNumber, String invoiceId, ResponseContainer<List<BillingBenefitsCustomerInvoiceModel>> response) {
        response.getModel().stream().filter(invoice ->
                invoice.getAccountNumber().equals(billingAccountNumber) && invoice.getInvoiceNumber().equals(invoiceId))
                .findFirst().orElseThrow(() -> new IstfException(String.format("Invoice:%s for billing account:%s is not found", invoiceId, billingAccountNumber)));
    }
}
