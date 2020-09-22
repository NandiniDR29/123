package com.exigen.ren.modules.billing.api;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.profile.tabs.GeneralProfileTab;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.pages.summary.billing.BillingAccountsListPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.BillingBenefitsRestService;
import com.exigen.ren.rest.billing.model.BillingAccountsDocumentGenerationStatus;
import com.exigen.ren.rest.billing.model.BillingGenerateDraftBillModel;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.BamConstants.*;
import static com.exigen.ren.rest.model.RestError.ErrorCode.ERROR_403;
import static com.exigen.ren.rest.model.RestError.ErrorCode.ERROR_404;
import static com.exigen.ren.utils.components.Components.BILLING_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDraftBillGeneration extends GroupBenefitsBillingBaseTest implements BillingAccountContext, RoleContext, ProfileContext, CustomerContext, CaseProfileContext {

    private String user2Login;
    private String user2Password;

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15742", component = BILLING_REST)
    public void testDraftBillGenerationSelfAdmin() {

        preconditions();
        createPolicySelfAdmin();
        testSteps();
    }

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15742", component = BILLING_REST)
    public void testDraftBillGenerationFullAdmin() {

        preconditions();
        createPolicyFullAdmin();
        billingAccountNumber.set(getBillingAccountNumber(masterPolicyNumber.get()));
        testSteps();
    }

    private void preconditions() {
        TestData tdCorporateRole = roleCorporate.defaultTestData();
        TestData tdSecurityProfile = profileCorporate.defaultTestData();
        adminApp().open();
        String roleName = tdCorporateRole.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.ROLE_NAME.getLabel());

        roleCorporate.create(tdCorporateRole.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), ImmutableList.of("ALL", "EXCLUDE " + Privilege.BILLING_ACCOUNT_GENERATE_DRAFT_BILL.get())));

        user2Login = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.USER_LOGIN.getLabel());
        user2Password = tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.PASSWORD.getLabel());

        profileCorporate.create(tdSecurityProfile.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralProfileTab.class.getSimpleName(), GeneralProfileMetaData.ROLES.getLabel()), roleName));

        mainApp().reopen();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
    }

    private void testSteps() {
        BillingBenefitsRestService billingRestServiceUser2 = new BillingBenefitsRestService(new User(user2Login, user2Password));


        LOGGER.info("---=={Step 1}==---");
        ResponseContainer<BillingGenerateDraftBillModel> responseUser2 = billingRestServiceUser2.postGenerateDraftBill(billingAccountNumber.get());

        LOGGER.info("---=={Step 2}==---");
        assertThat(responseUser2.getResponse()).hasStatus(Integer.parseInt(ERROR_403.getCode()));
        assertSoftly(softly -> {
            softly.assertThat(responseUser2.getModel().getErrorCode()).isEqualTo(ERROR_403.getCode());
            softly.assertThat(responseUser2.getModel().getMessage()).isEqualTo("Authentication succeeded but authenticated user doesn't have access to the resource.");
        });
        LOGGER.info("---=={Step 3}==---");
        String invalidNumber = "invalid";
        ResponseContainer<BillingGenerateDraftBillModel> responseUser1Invalid = billingBenefitsRestService.postGenerateDraftBill(invalidNumber);
        assertSoftly(softly -> {
            softly.assertThat(responseUser1Invalid.getResponse()).hasStatus(Integer.parseInt(ERROR_404.getCode()));
            softly.assertThat(responseUser1Invalid.getModel().getErrorCode()).isEqualTo("BILLING_ACCOUNT_NOT_FOUND");
            softly.assertThat(responseUser1Invalid.getModel().getMessage()).isEqualTo(String.format("Billing account #%s is not found", invalidNumber));
        });
        LOGGER.info("---=={Step 4}==---");
        ResponseContainer<BillingGenerateDraftBillModel> responseUser1 = billingBenefitsRestService.postGenerateDraftBill(billingAccountNumber.get());
        assertThat(responseUser1.getResponse()).hasStatus(200);
        String docGenTicket = responseUser1.getModel().getDocgenTicket();
        String periodStart = LocalDate.parse(responseUser1.getModel().getPeriodStart()).format(DateTimeUtils.MM_DD_YYYY);
        String periodEnd = LocalDate.parse(responseUser1.getModel().getPeriodEnd()).format(DateTimeUtils.MM_DD_YYYY);
        String dueDate = LocalDate.parse(responseUser1.getModel().getDueDate()).format(DateTimeUtils.MM_DD_YYYY);
        assertThat(docGenTicket).isNotNull();

        MainPage.QuickSearch.search(billingAccountNumber.get());
        BillingAccountsListPage.verifyBamActivities(String.format(BILLING_DRAFT_BILL_GENERATE, dueDate, periodStart, periodEnd), FINISHED);
        BillingAccountsListPage.verifyBamActivities(String.format(BILLING_DRAFT_BILL_INITIATE, dueDate, periodStart, periodEnd), FINISHED);

        LOGGER.info("---=={Step 5}==---");
        ResponseContainer<BillingGenerateDraftBillModel> secondResponseUser1 = billingBenefitsRestService.postGenerateDraftBill(billingAccountNumber.get());
        assertThat(secondResponseUser1.getResponse()).hasStatus(200);
        assertThat(docGenTicket).isEqualTo(secondResponseUser1.getModel().getDocgenTicket());

        LOGGER.info("---=={Step 6}==---");
        ResponseContainer<BillingAccountsDocumentGenerationStatus> responseDocStatus = billingBenefitsRestService.getAccountsDocumentGenerationStatus(docGenTicket);
        assertSoftly(softly -> {
            softly.assertThat(responseDocStatus.getModel().getStatusCd()).isEqualTo("SUCCESS");
            softly.assertThat(responseDocStatus.getModel().getDocumentName()).isNotNull();
            softly.assertThat(responseDocStatus.getModel().getRepositoryDocumentId()).isNotNull();
        });
        LOGGER.info("---=={Step 7}==---");
        ResponseContainer<BillingAccountsDocumentGenerationStatus> responseDocStatusInvalid = billingBenefitsRestService.getAccountsDocumentGenerationStatus(invalidNumber);
        assertSoftly(softly -> {
            softly.assertThat(responseDocStatusInvalid.getResponse()).hasStatus(Integer.parseInt(ERROR_404.getCode()));
            softly.assertThat(responseDocStatusInvalid.getModel().getErrorCode()).isEqualTo("DRAFT_BILL_NOT_FOUND");
            softly.assertThat(responseDocStatusInvalid.getModel().getMessage()).isEqualTo(String.format("Document #%s is not found", invalidNumber));
        });
    }
}