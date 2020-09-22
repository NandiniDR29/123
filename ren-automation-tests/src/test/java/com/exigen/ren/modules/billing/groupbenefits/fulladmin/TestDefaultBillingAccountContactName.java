package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.account.tabs.UpdateBillingAccountActionTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingAccountSetupTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingGroupsTab;
import com.exigen.ren.main.modules.customer.tabs.RelationshipTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.BillingGroupsActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.BillingAccountGeneralOptions.BILLING_CONTACT_NAME;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.SELECT_ACTION;
import static com.exigen.ren.main.modules.billing.setup_billing_groups.metadata.BillingGroupsTabMetaData.PAYOR;
import static com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData.*;
import static com.exigen.ren.main.modules.policy.common.metadata.master.BillingGroupsActionTabMetaData.ALLOW_MANUAL_SETUP_OF_BILLING_ACCOUNTS;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDefaultBillingAccountContactName extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-30309", component = BILLING_GROUPBENEFITS)
    public void testDefaultBillingAccountContactName() {

        BillingAccountTab billingAccountTab = groupAccidentMasterPolicy.issue().getWorkspace().getTab(BillingAccountTab.class);
        BillingGroupsTab billingGroupsTab = billingAccount.setupBillingGroups().getWorkspace().getTab(BillingGroupsTab.class);

        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customerName1 = CustomerSummaryPage.labelCustomerName.getValue();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        LOGGER.info("Test. Step: 1");
        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(BillingGroupsActionTab.class.getSimpleName(), ALLOW_MANUAL_SETUP_OF_BILLING_ACCOUNTS.getLabel()), VALUE_NO)
                .mask(TestData.makeKeyPath(billingAccountTab.getMetaKey())));

        billingAccount.navigateToBillingAccount();
        BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), customerName1))
                .getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).controls.links.getFirst().click();
        billingAccount.inquiry().start();

        assertSoftly(softly -> {

            softly.assertThat(billingAccount.updateBillingAccount().getWorkspace().getTab(UpdateBillingAccountActionTab.class).getAssetList().getAsset(BILLING_CONTACT_NAME)).hasValue(customerName1);

            LOGGER.info("Test. Step: 2");
            groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicyData());
            groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData());
            groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicyData());
            groupAccidentMasterPolicy.issue().start();
            groupAccidentMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultACMasterPolicyData(), BillingAccountTab.class);
            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");

            softly.assertThat(billingAccountTab.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(BILLING_CONTACT_NAME)).isEnabled().hasValue(customerName1);

            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).fillTab(getDefaultACMasterPolicyData()).submitTab();

            billingAccount.navigateToBillingAccount();
            BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), customerName1))
                    .getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).controls.links.getFirst().click();
            billingAccount.inquiry().start();

            softly.assertThat(billingAccount.updateBillingAccount().getWorkspace().getTab(UpdateBillingAccountActionTab.class).getAssetList().getAsset(BILLING_CONTACT_NAME)).hasValue(customerName1);

            LOGGER.info("Test. Step: 3");
            Tab.buttonBack.click();

            TestData updateBillingAccountTestData = DataProviderFactory.emptyData()
                    .adjust(BillingAccountTabMetaData.BillingAccountGeneralOptions.class.getSimpleName(), DataProviderFactory.emptyData().adjust(BILLING_CONTACT_NAME.getLabel(), "new Billing Contact Name"));

            billingAccount.updateBillingAccount().perform(updateBillingAccountTestData);
            billingAccount.inquiry().start();

            softly.assertThat(billingAccount.updateBillingAccount().getWorkspace().getTab(UpdateBillingAccountActionTab.class).getAssetList().getAsset(BILLING_CONTACT_NAME))
                    .hasValue(updateBillingAccountTestData.getValue(BillingAccountTabMetaData.BillingAccountGeneralOptions.class.getSimpleName(), BILLING_CONTACT_NAME.getLabel()));

            LOGGER.info("Test. Step: 4");
            customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                    .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)));
            String customerName2 = CustomerSummaryPage.labelCustomerName.getValue();
            String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();

            createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

            groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(DATA_GATHER_SELF_ADMIN, DEFAULT_TEST_DATA_KEY)
                    .adjust(groupAccidentMasterPolicy.getDefaultTestData(PROPOSE, DEFAULT_TEST_DATA_KEY))
                    .adjust(groupAccidentMasterPolicy.getDefaultTestData(ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                    .adjust(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY))
                    .adjust(TestData.makeKeyPath(BillingGroupsActionTab.class.getSimpleName(), ALLOW_MANUAL_SETUP_OF_BILLING_ACCOUNTS.getLabel()), VALUE_NO)
                    .mask(TestData.makeKeyPath(billingAccountTab.getMetaKey())));

            billingAccount.navigateToBillingAccount();

            billingAccount.inquiry().start();

            String salutation = tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).getValue(RelationshipTab.class.getSimpleName(), SALUTATION.getLabel());
            String firstName = tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).getValue(RelationshipTab.class.getSimpleName(), FIRST_NAME.getLabel());
            String lastName = tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).getValue(RelationshipTab.class.getSimpleName(), LAST_NAME.getLabel());

            String billingContactName = String.join(" ", salutation, firstName, lastName);

            softly.assertThat(billingAccount.updateBillingAccount().getWorkspace().getTab(UpdateBillingAccountActionTab.class).getAssetList().getAsset(BILLING_CONTACT_NAME)).hasValue(billingContactName);
            Tab.buttonBack.click();

            LOGGER.info("Test. Step: 5");
            MainPage.QuickSearch.search(customerNumber);

            customerNonIndividual.update().perform(tdSpecific().getTestData("TestData_Update"));

            groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicyData());
            String policyNumber1 = PolicySummaryPage.labelPolicyNumber.getValue();
            groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData());
            groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicyData());
            groupAccidentMasterPolicy.issue().start();
            groupAccidentMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultACMasterPolicyData(), BillingAccountTab.class);
            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");

            String relationship2Name = tdSpecific().getTestData("TestData_Update").getTestDataList(RelationshipTab.class.getSimpleName()).get(1).getValue(NAME_LEGAL.getLabel());

            softly.assertThat(billingAccountTab.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(BILLING_CONTACT_NAME)).isEnabled().hasValue(relationship2Name);

            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).fillTab(getDefaultACMasterPolicyData()).submitTab();

            billingAccount.navigateToBillingAccount();
            BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), customerName2))
                    .getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).controls.links.getFirst().click();
            billingAccount.inquiry().start();

            softly.assertThat(billingAccount.updateBillingAccount().getWorkspace().getTab(UpdateBillingAccountActionTab.class).getAssetList().getAsset(BILLING_CONTACT_NAME)).hasValue(relationship2Name);

            LOGGER.info("Test. Step: 6");
            MainPage.QuickSearch.search(policyNumber1);
            groupAccidentMasterPolicy.endorse().perform(groupAccidentMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
            PolicySummaryPage.buttonPendedEndorsement.click();
            groupAccidentMasterPolicy.issue().start();
            groupAccidentMasterPolicy.issue().getWorkspace().fillUpTo(groupAccidentMasterPolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY), BillingGroupsActionTab.class);

            billingGroupsTab.fillTab(billingAccount.getDefaultTestData("SetupBillingGroups", "TestData_Add_With_Remove")
                    .adjust(TestData.makeKeyPath(billingGroupsTab.getMetaKey() + "[0]", PAYOR.getLabel()), "Sponsor"));

            groupAccidentMasterPolicy.issue().getWorkspace().getTab(BillingGroupsActionTab.class).submitTab();
            groupAccidentMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class).submitTab();

            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
            softly.assertThat(billingAccountTab.getAssetList().getAsset(CREATE_NEW_BILLING_ACCOUNT).getAsset(BILLING_CONTACT_NAME)).isEnabled().hasValue(relationship2Name);
            BillingAccountSetupTab.saveTab();
            billingAccountTab.submitTab();

            billingAccount.navigateToBillingAccount();
            BillingAccountsListPage.tableBenefitAccounts.getRow(ImmutableMap.of(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT_NAME.getName(), customerName2))
                    .getCell(TableConstants.BillingBenefitsAccounts.BILLING_ACCOUNT.getName()).controls.links.getFirst().click();
            billingAccount.inquiry().start();

            softly.assertThat(billingAccount.updateBillingAccount().getWorkspace().getTab(UpdateBillingAccountActionTab.class).getAssetList().getAsset(BILLING_CONTACT_NAME)).hasValue(relationship2Name);
        });
    }
}