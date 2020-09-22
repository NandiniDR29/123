package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.tabs.ConsolidatedStatementDetailsActionTab;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.BillingConstants.BillingConsolidatedStatement.STATUS_N;
import static com.exigen.ren.main.enums.BillingConstants.BillingConsolidatedStatement.STATUS_Y;
import static com.exigen.ren.main.enums.TableConstants.BillingBenefitsAccounts.CONSOLIDATED_STATEMENT;
import static com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData.ADD_BILLING_ACCOUNTS;
import static com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData.AddBillingAccountsMetaData.BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData.CONSOLIDATED_STATEMENT_ENABLED_FROM;
import static com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage.tableBenefitAccounts;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;


public class TestConsolidatedStatement extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34563", component = BILLING_GROUPBENEFITS)
    public void testConsolidatedStatementSelf() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicySelfAdmin();
        String ba1 = billingAccountNumber.get();

        createPolicySelfAdmin();
        String ba2 = billingAccountNumber.get();

        commonSteps(ba1, ba2);
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-34563", component = BILLING_GROUPBENEFITS)
    public void testConsolidatedStatementFull() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createPolicyFullAdmin();
        String ba1 = getBillingAccountNumber(masterPolicyNumber.get());

        createPolicyFullAdmin();
        String ba2 = getBillingAccountNumber(masterPolicyNumber.get());

        commonSteps(ba1, ba2);
    }

    private void commonSteps(String ba1, String ba2) {

        LOGGER.info("Test. Steps: 1, 2");
billingAccount.navigateToBillingAccountList();
        assertSoftly(softly -> {
            softly.assertThat(tableBenefitAccounts.getHeader().getValue()).contains(CONSOLIDATED_STATEMENT.getName());
            softly.assertThat(tableBenefitAccounts.getRow(BillingConstants.BillingBenefitAccountsTable.BILLING_ACCOUNT, ba1).getCell(CONSOLIDATED_STATEMENT.getName())).hasValue(STATUS_N);
            softly.assertThat(tableBenefitAccounts.getRow(BillingConstants.BillingBenefitAccountsTable.BILLING_ACCOUNT, ba2).getCell(CONSOLIDATED_STATEMENT.getName())).hasValue(STATUS_N);
        });

        LOGGER.info("Test. Step: 3");
        TestData td = billingAccount.getDefaultTestData("SetUpConsolidatedStatement", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(ConsolidatedStatementDetailsActionTab.class.getSimpleName(), CONSOLIDATED_STATEMENT_ENABLED_FROM.getLabel()),  String.format("contains=%s",ba1))
                .adjust(TestData.makeKeyPath(ConsolidatedStatementDetailsActionTab.class.getSimpleName(), ADD_BILLING_ACCOUNTS.getLabel() + "[0]", BILLING_ACCOUNT.getLabel()), ba2)
                .resolveLinks();

        billingAccount.setUpConsolidatedStatement().perform(td);
        LOGGER.info("Test. Step: 4");
        assertSoftly(softly -> {
            softly.assertThat(tableBenefitAccounts.getRow(BillingConstants.BillingBenefitAccountsTable.BILLING_ACCOUNT, ba1).getCell(CONSOLIDATED_STATEMENT.getName())).hasValue(STATUS_Y);
            softly.assertThat(tableBenefitAccounts.getRow(BillingConstants.BillingBenefitAccountsTable.BILLING_ACCOUNT, ba2).getCell(CONSOLIDATED_STATEMENT.getName())).hasValue(STATUS_N);
        });
    }
}