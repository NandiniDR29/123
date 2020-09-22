package com.exigen.ren.modules.billing.api;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.BillingBenefitsRestService;
import com.exigen.ren.rest.billing.model.billing_accounts_invoices.BillingAccountsInvoices;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBillingCapabilityToRetrieveInvoice extends GroupBenefitsBillingBaseTest implements BillingAccountContext, RoleContext, ProfileContext, CustomerContext, CaseProfileContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-16915", component = BILLING_REST)
    public void testBillingCapabilityToRetrieveInvoice() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createPolicySelfAdmin();
        LOGGER.info("---=={Step 1}==---");
        verifyApiInvoice(billingBenefitsRestService);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-16915", component = BILLING_REST)
    public void testBillingCapabilityToRetrieveInvoiceFull() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        LOGGER.info("---=={Step 2}==---");
        createPolicyFullAdmin();
        verifyApiInvoice(billingBenefitsRestService);
    }

    private void verifyApiInvoice(BillingBenefitsRestService billingRestService){

        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();

        String invoiceNum = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.INVOICE).getValue();
        billingAccountNumber.set(getBillingAccountNumber(masterPolicyNumber.get()));

        RetryService.run(predicate ->  billingRestService.getAccountsInvoices(billingAccountNumber.get(), invoiceNum).getModel().getDocument().getStatusCd().equals("SUCCESS"),
                 StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        ResponseContainer<BillingAccountsInvoices> response = billingRestService.getAccountsInvoices(billingAccountNumber.get(), invoiceNum);
        assertThat(response.getResponse().getStatus()).isEqualTo(200);
        BillingAccountsInvoices invoice = response.getModel();
        assertThat(invoice.getDocument()).isNotNull();

        assertSoftly(softly -> {
            softly.assertThat(invoice.getDocument().getDocgenTicket()).isNotNull();
            softly.assertThat(invoice.getDocument().getDocumentName()).isNotNull();
            softly.assertThat(invoice.getDocument().getRepositoryDocumentId()).isNotNull();
        });
    }
}