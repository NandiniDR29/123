package com.exigen.ren.modules.billing.api;

import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.model.BillingBenefitsAccountsModel;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBillingAccountTypeCd extends GroupBenefitsBillingBaseTest implements BillingAccountContext, RoleContext, ProfileContext, CustomerContext, CaseProfileContext {


    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40119", component = BILLING_REST)
    public void testBillingAccountTypeCd() {

        mainApp().open();

        createDefaultNonIndividualCustomer();
        String customer1 = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicySelfAdminData());
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicySelfAdminData());
        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicySelfAdminData());

        String masterPolicySelf = PolicySummaryPage.labelPolicyNumber.getValue();

        groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicyData());

        String masterPolicyFull = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("---=={Step 1}==---");
        ImmutableList.of("SELF_ADMINISTERED", "LIST_BILL", "NON_PREMIUM").forEach(value -> {
            assertSoftly(softly -> {
                ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                        billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1), null, null, null, null, null, value);
                softly.assertThat(response.getResponse()).hasStatus(200);
                softly.assertThat(response.getModel()).isEmpty();
            });
        });

        LOGGER.info("---=={Step 2}==---");
        MainPage.QuickSearch.search(masterPolicySelf);
        groupAccidentMasterPolicy.issue().perform(getDefaultACMasterPolicySelfAdminData());
        MainPage.QuickSearch.search(masterPolicyFull);
        groupAccidentMasterPolicy.issue().perform(getDefaultACMasterPolicyData());

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1), null, null, null, null, null, "SELF_ADMINISTERED");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isNotEmpty();
            softly.assertThat(response.getModel().get(0).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicySelf);
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1), null, null, null, null, null, "LIST_BILL");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isNotEmpty();
            softly.assertThat(response.getModel().get(0).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicyFull);
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1), null, null, null, null, null, "NON_PREMIUM");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isEmpty();
        });

        LOGGER.info("---=={Step 3}==---");
        createDefaultNonIndividualCustomer();
        String customer2 = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        createDefaultGroupAccidentMasterPolicy();
        String masterPolicyFullMP1 = PolicySummaryPage.labelPolicyNumber.getValue();
        createDefaultGroupAccidentMasterPolicySelfAdmin();
        String masterPolicySelfMP1 = PolicySummaryPage.labelPolicyNumber.getValue();

        groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicySelfAdminData());
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicySelfAdminData());
        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicySelfAdminData());

        String masterPolicySelf2 = PolicySummaryPage.labelPolicyNumber.getValue();

        groupAccidentMasterPolicy.createQuote(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.propose().perform(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.acceptContract().perform(getDefaultACMasterPolicyData());

        String masterPolicyFull2 = PolicySummaryPage.labelPolicyNumber.getValue();

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer2), null, null, null, null, null, "SELF_ADMINISTERED");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).hasSize(1);
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer2), null, null, null, null, null, "LIST_BILL");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).hasSize(1);
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer2), null, null, null, null, null, "NON_PREMIUM");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isEmpty();
        });

        LOGGER.info("---=={Step 4}==---");
        MainPage.QuickSearch.search(masterPolicySelf2);
        groupAccidentMasterPolicy.issue().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA"));
        MainPage.QuickSearch.search(masterPolicyFull2);
        groupAccidentMasterPolicy.issue().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA"));

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer2), null, null, null, null, null, "SELF_ADMINISTERED");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isNotEmpty();
            softly.assertThat(response.getModel().get(0).getMasterPolicies()).hasSize(2);
            response.getModel().get(0).getMasterPolicies().stream().filter(policy -> policy.getPolicyNumber().equals(masterPolicySelf2))
                    .findFirst().orElseThrow(() -> new IstfException(String.format("Policy num %s is not found", masterPolicySelf2)));
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer2), null, null, null, null, null, "LIST_BILL");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isNotEmpty();
            softly.assertThat(response.getModel().get(0).getMasterPolicies()).hasSize(2);
            response.getModel().get(0).getMasterPolicies().stream().filter(policy -> policy.getPolicyNumber().equals(masterPolicyFull2))
                    .findFirst().orElseThrow(() -> new IstfException(String.format("Policy num %s is not found", masterPolicyFull2)));
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer2), null, null, null, null, null, "NON_PREMIUM");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isEmpty();
        });

        LOGGER.info("---=={Step 5}==---");
        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1, customer2), null, null, null, null, null, "SELF_ADMINISTERED");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isNotEmpty();
            softly.assertThat(response.getModel()).hasSize(2);

            BillingBenefitsAccountsModel modelCustomer1 = response.getModel().stream()
                    .filter(billingBenefitsAccountsModel -> billingBenefitsAccountsModel.getCustomerNumber().equals(customer1))
                    .findFirst().orElseThrow(() -> new IstfException(String.format("Customer %s is not found", customer1)));
            BillingBenefitsAccountsModel modelCustomer2 = response.getModel().stream()
                    .filter(billingBenefitsAccountsModel -> billingBenefitsAccountsModel.getCustomerNumber().equals(customer2))
                    .findFirst().orElseThrow(() -> new IstfException(String.format("Customer %s is not found", customer2)));

            softly.assertThat(modelCustomer1.getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicySelf);
            softly.assertThat(modelCustomer2.getMasterPolicies()).hasSize(2);
            modelCustomer2.getMasterPolicies().stream().filter(policy -> policy.getPolicyNumber().equals(masterPolicySelf2))
                    .findFirst().orElseThrow(() -> new IstfException(String.format("Policy num %s is not found", masterPolicySelf2)));
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1, customer2), null, null, null, null, null, "LIST_BILL");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isNotEmpty();
            softly.assertThat(response.getModel()).hasSize(2);

            BillingBenefitsAccountsModel modelCustomer1 = response.getModel().stream()
                    .filter(billingBenefitsAccountsModel -> billingBenefitsAccountsModel.getCustomerNumber().equals(customer1))
                    .findFirst().orElseThrow(() -> new IstfException(String.format("Customer %s is not found", customer1)));
            BillingBenefitsAccountsModel modelCustomer2 = response.getModel().stream()
                    .filter(billingBenefitsAccountsModel -> billingBenefitsAccountsModel.getCustomerNumber().equals(customer2))
                    .findFirst().orElseThrow(() -> new IstfException(String.format("Customer %s is not found", customer2)));

            softly.assertThat(modelCustomer1.getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicyFull);
            softly.assertThat(modelCustomer2.getMasterPolicies()).hasSize(2);
            modelCustomer2.getMasterPolicies().stream().filter(policy -> policy.getPolicyNumber().equals(masterPolicyFull2))
                    .findFirst().orElseThrow(() -> new IstfException(String.format("Policy num %s is not found", masterPolicyFull2)));
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1, customer2), null, null, null, null, null, "NON_PREMIUM");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isEmpty();
        });

        LOGGER.info("---=={Step 6}==---");
        ResponseContainer<List<BillingBenefitsAccountsModel>> response2 =
                billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1, customer2), null, null, null, null, ImmutableList.of("-accountNumber"), "SELF_ADMINISTERED");
        assertThat(response2.getResponse()).hasStatus(200);
        assertSoftly(softly -> {

            softly.assertThat(response2.getModel()).isNotEmpty();
            softly.assertThat(response2.getModel()).hasSize(2);
            softly.assertThat(response2.getModel().get(1).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicySelf);
            softly.assertThat(response2.getModel().get(0).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicySelfMP1);
            softly.assertThat(response2.getModel().get(0).getMasterPolicies().get(1).getPolicyNumber()).isEqualTo(masterPolicySelf2);
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1, customer2), null, null, null, null, ImmutableList.of("-accountNumber"), "LIST_BILL");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isNotEmpty();
            softly.assertThat(response.getModel()).hasSize(2);
            softly.assertThat(response.getModel().get(1).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicyFull);
            softly.assertThat(response.getModel().get(0).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicyFullMP1);
            softly.assertThat(response.getModel().get(0).getMasterPolicies().get(1).getPolicyNumber()).isEqualTo(masterPolicyFull2);
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1, customer2), null, null, null, null, ImmutableList.of("-accountNumber"), "NON_PREMIUM");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isEmpty();
        });

        LOGGER.info("---=={Step 7}==---");
        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1, customer2), null, null, null, null, ImmutableList.of("accountNumber"), "SELF_ADMINISTERED");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isNotEmpty();
            softly.assertThat(response.getModel()).hasSize(2);
            softly.assertThat(response.getModel().get(0).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicySelf);
            softly.assertThat(response.getModel().get(1).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicySelfMP1);
            softly.assertThat(response.getModel().get(1).getMasterPolicies().get(1).getPolicyNumber()).isEqualTo(masterPolicySelf2);
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1, customer2), null, null, null, null, ImmutableList.of("accountNumber"), "LIST_BILL");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isNotEmpty();
            softly.assertThat(response.getModel()).hasSize(2);
            softly.assertThat(response.getModel().get(0).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicyFull);
            softly.assertThat(response.getModel().get(1).getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicyFullMP1);
            softly.assertThat(response.getModel().get(1).getMasterPolicies().get(1).getPolicyNumber()).isEqualTo(masterPolicyFull2);
        });

        assertSoftly(softly -> {
            ResponseContainer<List<BillingBenefitsAccountsModel>> response =
                    billingBenefitsRestService.getAccountsBillingAccount(ImmutableList.of(customer1, customer2), null, null, null, null, ImmutableList.of("accountNumber"), "NON_PREMIUM");
            softly.assertThat(response.getResponse()).hasStatus(200);
            softly.assertThat(response.getModel()).isEmpty();
        });
    }
}