package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.actions.AcceptPaymentAction;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ActionConstants.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData.BANK_ACCOUNT_TYPE;
import static com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData.MANAGE_PAYMENT_METHODS;
import static com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData.ManagePaymentMethods.*;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBillingBankAccountType extends GroupBenefitsBillingBaseTest implements BillingAccountContext, RoleContext, ProfileContext, CustomerContext, CaseProfileContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-43579", component = BILLING_GROUPBENEFITS)
    public void testBillingBankAccountType() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();
        billingAccount.navigateToBillingAccount();
        billingAccount.generateFutureStatement().perform();

        LOGGER.info("---=={Step 1}==---");
        billingAccount.acceptPayment().start();
        Tab acceptPaymentActionTab = billingAccount.acceptPayment().getWorkspace().getTab(AcceptPaymentActionTab.class);
        AssetList paymentMethodsAssetList = acceptPaymentActionTab.getAssetList().getAsset(MANAGE_PAYMENT_METHODS);
        paymentMethodsAssetList.getAsset(ADD_PAYMENT_METHOD).click();
        paymentMethodsAssetList.getAsset(PAYMENT_METHOD).setValue("EFT");
        assertThat(paymentMethodsAssetList.getAsset(BANK_ACCOUNT_TYPE)).isRequired().hasOptions(EMPTY, "Checking", "Savings", "Other");
        acceptPaymentActionTab.fillTab(billingAccount.getDefaultTestData("AcceptPayment", "Manage_Payment_Methods_EFT")
                .adjust(TestData.makeKeyPath(acceptPaymentActionTab.getMetaKey(), paymentMethodsAssetList.getName(), BANK_ACCOUNT_TYPE.getLabel()), "Other"));

        LOGGER.info("---=={Step 2}==---");
        openPaymentMethodsView("EFT");
        assertThat(paymentMethodsAssetList.getAsset(BANK_ACCOUNT_TYPE)).isPresent().isDisabled().hasValue("Other");

        openPaymentMethodsUpdate();
        assertThat(paymentMethodsAssetList.getAsset(BANK_ACCOUNT_TYPE)).isPresent().isEnabled().hasValue("Other").hasOptions(EMPTY, "Checking", "Savings", "Other");

        paymentMethodsAssetList.getAsset(BANK_ACCOUNT_TYPE).setValue("Savings");
        paymentMethodsAssetList.getAsset(ADD_UPDATE).click();

        openPaymentMethodsView("EFT");
        assertThat(paymentMethodsAssetList.getAsset(BANK_ACCOUNT_TYPE)).isPresent().isDisabled().hasValue("Savings");
        openPaymentMethodsUpdate();
        paymentMethodsAssetList.getAsset(BANK_ACCOUNT_TYPE).setValue("Checking");
        paymentMethodsAssetList.getAsset(ADD_UPDATE).click();
        openPaymentMethodsView("EFT");
        assertThat(paymentMethodsAssetList.getAsset(BANK_ACCOUNT_TYPE)).isPresent().isDisabled().hasValue("Checking");
        paymentMethodsAssetList.getAsset(BACK).click();

        LOGGER.info("---=={Step 3}==---");
        paymentMethodsAssetList.getAsset(ADD_PAYMENT_METHOD).click();
        paymentMethodsAssetList.getAsset(PAYMENT_METHOD).setValue("EFT");
        assertThat(paymentMethodsAssetList.getAsset(BANK_ACCOUNT_TYPE)).isRequired().hasOptions(EMPTY, "Checking", "Savings", "Other");
        acceptPaymentActionTab.fillTab(billingAccount.getDefaultTestData("AcceptPayment", "Manage_Payment_Methods_EFT")
                .adjust(TestData.makeKeyPath(acceptPaymentActionTab.getMetaKey(), paymentMethodsAssetList.getName(), BANK_ACCOUNT_TYPE.getLabel()), "Other"));
        assertThat( AcceptPaymentAction.paymentMethodsTable).hasRows(2);

        LOGGER.info("---=={Step 4}==---");
        paymentMethodsAssetList.getAsset(PAYMENT_METHOD).setValue("ACH");
        assertThat(paymentMethodsAssetList.getAsset(BANK_ACCOUNT_TYPE)).isRequired().hasOptions(EMPTY, "Checking", "Savings", "Other");
        acceptPaymentActionTab.fillTab(billingAccount.getDefaultTestData("AcceptPayment", "Manage_Payment_Methods")
                .adjust(TestData.makeKeyPath(acceptPaymentActionTab.getMetaKey(), paymentMethodsAssetList.getName(), BANK_ACCOUNT_TYPE.getLabel()), "Other"));

        openPaymentMethodsView("ACH");
        assertThat(paymentMethodsAssetList.getAsset(BANK_ACCOUNT_TYPE)).isPresent().isDisabled().hasValue("Other");

    }

    private void openPaymentMethodsView(String paymentMethodsType ) {
        AcceptPaymentAction.paymentMethodsTable.getRowContains(ImmutableMap.of(AcceptPaymentAction.PaymentMethods.PAYMENT_METHOD.getName(), paymentMethodsType))
                .getCell(AcceptPaymentAction.PaymentMethods.ACTION.getName()).controls.links.get(VIEW).click();
    }

    private void openPaymentMethodsUpdate() {
        AcceptPaymentAction.paymentMethodsTable.getRowContains(ImmutableMap.of(AcceptPaymentAction.PaymentMethods.PAYMENT_METHOD.getName(), "EFT"))
                .getCell(AcceptPaymentAction.PaymentMethods.ACTION.getName()).controls.links.get(EDIT).click();
    }

}