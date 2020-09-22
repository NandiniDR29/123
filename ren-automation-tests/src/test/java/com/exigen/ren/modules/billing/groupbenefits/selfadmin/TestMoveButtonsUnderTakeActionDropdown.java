package com.exigen.ren.modules.billing.groupbenefits.selfadmin;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.billing.account.tabs.ManageInvoicingCalendarsActionTab;
import com.exigen.ren.main.modules.billing.account.tabs.PaymentMethodsActionTab;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.main.pages.summary.billing.PaymentsAndBillingMaintenancePage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestMoveButtonsUnderTakeActionDropdown extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34564", component = BILLING_GROUPBENEFITS)
    public void testMoveButtonsUnderTakeActionDropdown() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicySelfAdminData());
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicySelfAdminData());

        billingAccount.navigateToBillingAccountList();
        BillingAccountsListPage.activitiesAndUserNotes.expand();

        LOGGER.info("Test. Step: 1");
        assertSoftly(softly -> {
            softly.assertThat(BillingAccountsListPage.comboBoxFilterByPolicy).isPresent();
            softly.assertThat(BillingAccountsListPage.buttonNotesAlerts).isPresent();
            softly.assertThat(BillingAccountsListPage.activitiesAndUserNotes).isPresent();
        });

        LOGGER.info("Test. Step: 2");
        assertThat(NavigationPage.comboBoxListAction)
                .containsAllOptions(ImmutableList.of(ActionConstants.BillingAction.PAYMENTS_AND_BILLING_MAINTENANCE, ActionConstants.BillingAction.MANAGE_INVOICING_CALENDARS, ActionConstants.BillingAction.PAYMENT_METHODS, ActionConstants.BillingAction.ADD_PAYMENT_METHOD));

        LOGGER.info("Test. Step: 3");
        NavigationPage.comboBoxListAction.setValue(ActionConstants.BillingAction.PAYMENTS_AND_BILLING_MAINTENANCE);
        assertThat(PaymentsAndBillingMaintenancePage.buttonAddPaymentBatch).isPresent();

        LOGGER.info("Test. Step: 4");
        billingAccount.navigateToBillingAccount();
        billingAccount.addManageInvoicingCalendars().start();
        assertThat(ManageInvoicingCalendarsActionTab.tableInvoicingCalendars).isPresent();

        LOGGER.info("Test. Step: 5");
        Tab.buttonBack.click();
        billingAccount.managePaymentMethods().start();
        assertThat(PaymentMethodsActionTab.tableManagePaymentMethods).isPresent();

        LOGGER.info("Test. Step: 6");
        Tab.buttonBack.click();
        NavigationPage.comboBoxListAction.setValue(ActionConstants.BillingAction.ADD_PAYMENT_METHOD);
        assertThat(AcceptPaymentActionTab.tablePaymentMethods).isPresent();
    }
}