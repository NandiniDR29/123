package com.exigen.ren.modules.billing.api;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.model.BillingPaymentMethodResponseModel;
import com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments.BillingAccountsBenefitsPayments;
import com.exigen.ren.rest.billing.model.billing_customers_payment_methods.BillingGETCustomersPaymentMethods;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.BamConstants.*;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.PAID_IN_FULL;
import static com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData.PAYMENT_METHOD;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSavedPaymentMethodsREST extends GroupBenefitsBillingBaseTest implements BillingAccountContext, RoleContext, ProfileContext, CustomerContext, CaseProfileContext {

    private String aCHMethodId;
    private String typeForCheck;
    private String paymentMethodEffectiveDate;
    private String paymentMethodExpirationDate;

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-27420", component = BILLING_REST)
    public void testSavedPaymentMethodsRESTFull() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createPolicyFullAdmin();

        commonSteps(customerNumber);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-27420", component = BILLING_REST)
    public void testSavedPaymentMethodsRESTSelf() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createPolicySelfAdmin();

        commonSteps(customerNumber);
    }

    private void commonSteps(String customerNumber){
        navigateToBillingAccount(masterPolicyNumber.get());
        billingAccount.generateFutureStatement().perform();
        String invoiceNum = BillingSummaryPage.getInvoiceNumberByRowNum(1);

        assertSoftly(softly -> {
            LOGGER.info("---=={Step 1}==---");
            TestData td = billingAccount.defaultAPITestData().getTestData("RestCustomerPaymentMethods", "TestData_Post_Request")
                    .adjust(TestData.makeKeyPath("id"), customerNumber);

            String type = td.getValue("ach", "accountNumber");
            typeForCheck = String.format("ACH Account %s%s",
                    type.substring(0, type.length() - 4).replaceAll("\\d", "*"),
                    type.substring(type.length() - 4));
            paymentMethodEffectiveDate = LocalDate.parse(td.getValue("ach", "paymentMethodEffectiveDate"), YYYY_MM_DD_HH_MM_SS_Z).format(MM_DD_YYYY);
            paymentMethodExpirationDate = LocalDate.parse(td.getValue("ach", "paymentMethodExpirationDate"), YYYY_MM_DD_HH_MM_SS_Z).format(MM_DD_YYYY);

            ResponseContainer<BillingPaymentMethodResponseModel> response = billingRestService.postCustomersEFTPaymentMethod(customerNumber, td);
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel().getId()).isNotEmpty();
            aCHMethodId = response.getModel().getId();

            billingAccount.navigateToBillingAccountList();
            BillingSummaryPage.verifyBamActivities(String.format(ACCEPT_PAYMENT_TYPE_ADDED_WITH_DATE, typeForCheck, paymentMethodEffectiveDate, paymentMethodExpirationDate), FINISHED);
        });

        LOGGER.info("---=={Step 2}==---");
        ResponseContainer<List<BillingPaymentMethodResponseModel>> getResponse = billingRestService.getAllPaymentsMethod(customerNumber);
        assertThat(getResponse.getResponse()).hasStatus(200);
        assertSoftly(softly -> {
            softly.assertThat(getResponse.getModel().get(0).getId()).isEqualTo(aCHMethodId);

            LOGGER.info("---=={Step 3}==---");
            TestData tdBenefitPayment = billingAccount.defaultAPITestData().getTestData("RestAccountsBenefitsPayments", "TestData_SavedPaymentMethod")
                    .adjust(TestData.makeKeyPath("amount", "value"), modalPremiumAmount.get().toPlainString())
                    .adjust(TestData.makeKeyPath("savedPaymentMethodId"), aCHMethodId);
            ResponseContainer<BillingAccountsBenefitsPayments> responseBenefit = billingBenefitsRestService.postAccountsBenefitsPayments("REST2", invoiceNum, tdBenefitPayment);
            softly.assertThat(responseBenefit.getResponse()).hasStatus(200);
            navigateToBillingAccount(masterPolicyNumber.get());
            BillingSummaryPage.verifyBamActivities(String.format(ACCEPT_PAYMENT_TYPE, "ACH", modalPremiumAmount.get().toString()), FINISHED);
            BillingSummaryPage.verifyBamActivities(String.format(INVOICE_STATUS_CHANGED_TO, invoiceNum, PAID_IN_FULL), FINISHED);
        });

        LOGGER.info("---=={Step 4}==---");
        ResponseContainer<BillingGETCustomersPaymentMethods> responsePaymentMethod = billingRestService.getPaymentMethod(customerNumber, aCHMethodId);
        assertThat(responsePaymentMethod.getResponse()).hasStatus(200);

        assertSoftly(softly -> {
            softly.assertThat(responsePaymentMethod.getModel().getType()).isEqualTo("ach");
            softly.assertThat(responsePaymentMethod.getModel().getSavedPaymentMethod().getId()).isEqualTo(aCHMethodId);

            LOGGER.info("---=={Step 5}==---");
            TestData tdForUpdate = billingAccount.defaultAPITestData().getTestData("RestCustomerPaymentMethods", "TestData_PUT_Request")
                    .adjust(TestData.makeKeyPath("id"), aCHMethodId);
            ResponseContainer<BillingPaymentMethodResponseModel> responseUpdatePaymentMethod = billingRestService.putPaymentMethod(tdForUpdate, customerNumber, aCHMethodId);
            softly.assertThat(responseUpdatePaymentMethod.getResponse()).hasStatus(200);
            navigateToBillingAccount(masterPolicyNumber.get());
            billingAccount.acceptPayment().start();
            Tab acceptPaymentActionTab = billingAccount.acceptPayment().getWorkspace().getTab(AcceptPaymentActionTab.class);
            softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(PAYMENT_METHOD)).containsOption(typeForCheck);
            navigateToBillingAccount(masterPolicyNumber.get());
            billingAccount.navigateToBillingAccount();
            BillingSummaryPage.verifyBamActivities(String.format(ACCEPT_PAYMENT_TYPE_EDITED_WITH_DATE, typeForCheck, paymentMethodEffectiveDate, paymentMethodExpirationDate), FINISHED);

            LOGGER.info("---=={Step 6}==---");
            ResponseContainer<BillingGETCustomersPaymentMethods> responsePaymentMethod2 = billingRestService.getPaymentMethod(customerNumber, aCHMethodId);
            softly.assertThat(responsePaymentMethod2.getResponse()).hasStatus(200);
            BillingGETCustomersPaymentMethods model = responsePaymentMethod2.getModel();

            assertThat(model.getAch()).isNotNull();
            assertThat(model.getAch().getAccountHolderInfo()).isNotNull();
            assertThat(model.getAch().getPayorDetails()).isNotNull();

            ImmutableList.of(
                    model.getAch().getBankName(),
                    model.getAch().getFirstName(),
                    model.getAch().getLastName(),
                    model.getAch().getAccountHolderInfo().getFirstName(),
                    model.getAch().getAccountHolderInfo().getLastName(),
                    model.getAch().getAccountHolderInfo().getMiddleName(),
                    model.getAch().getAccountHolderInfo().getOtherName(),
                    model.getAch().getPayorDetails().getPayorNameInfo().getFirstName(),
                    model.getAch().getPayorDetails().getPayorNameInfo().getMiddleName(),
                    model.getAch().getPayorDetails().getPayorNameInfo().getLastName(),
                    model.getAch().getPayorDetails().getPayorAddress().getAddressLine1(),
                    model.getAch().getPayorDetails().getPayorAddress().getAddressLine2(),
                    model.getAch().getPayorDetails().getPayorAddress().getAddressLine3()).forEach(field ->
                    softly.assertThat(field).isNotNull().contains("changes"));
            softly.assertThat(model.getAch().getPaymentMethodEffectiveDate()).isEqualTo(paymentMethodEffectiveDate);
            softly.assertThat(model.getAch().getPaymentMethodExpirationDate()).isEqualTo(paymentMethodExpirationDate);

            LOGGER.info("---=={Step 7}==---");
            Response responsePaymentMethodDelete = billingRestService.deletePaymentMethod(customerNumber, aCHMethodId);
            softly.assertThat(responsePaymentMethodDelete).hasStatus(200);
            BillingSummaryPage.verifyBamActivities(String.format(ACCEPT_PAYMENT_TYPE_DELETED_WITH_DATE, typeForCheck, paymentMethodEffectiveDate, paymentMethodExpirationDate), FINISHED);
        });
    }
}