package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.FinancialPaymentPaymentDetailsActionTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimBalancePage;
import com.exigen.ren.main.pages.summary.claim.ClaimFinancialsPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.buttonCancel;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FINANCIALS;
import static com.exigen.ren.common.pages.ErrorPage.tableError;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.PENDED;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYEE_RECOVERED_FROM;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.FinancialPaymentPaymentDetailsActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.FinancialPaymentPaymentDetailsActionTabMetaData.PaymentInterestMetaData.INTEREST_AMOUNT;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.OtherPayeeAddressSection.ADDRESS_TYPE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SearchProviderMetaData.TIN;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.TOOTH;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.RecoveryDetailsActionTabMetaData.RECOVERED_FROM;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithPrivilege;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.common.pages.ErrorPage.TableError.MESSAGE;

public class TestProcessClaimForOtherPayee extends ClaimGroupBenefitsDNBaseTest {
    private static final String GET_PATYOID = "select partyOid from ClaimsBalancingEntity where claimNumber='%s'";
    private RecoveryDetailsActionTab recoveryDetailsActionTab = dentalClaim.postRecovery().getWorkspace().getTab(RecoveryDetailsActionTab.class);
    private String company = "company";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22805", component = CLAIMS_GROUPBENEFITS)
    public void testProcessClaimForOtherPayee1() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("TEST REN-22805: Step 1");
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), REVIEW_REQUIRED_1)
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), TOOTH.getLabel()), "3"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.LOGGED_INTAKE);

        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(PENDED);

        LOGGER.info("TEST REN-22805: Step 2");
        dentalClaim.postRecovery().start();
        assertThat(recoveryDetailsActionTab.getAssetList().getAsset(RECOVERED_FROM)).hasOptions("", "Service Provider", "Primary Insured", "Other");

        LOGGER.info("TEST REN-22805: Step 3");
        recoveryDetailsActionTab.fillTab(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_PostRecovery")
                .adjust(makeKeyPath(recoveryDetailsActionTab.getMetaKey(), RECOVERED_FROM.getLabel()), "Other"));
        dentalClaim.postRecovery().submit();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), "Other payee is not added in the claim.");
        ErrorPage.buttonBack.click();

        LOGGER.info("TEST REN-22805: Step 4");
        recoveryDetailsActionTab.getAssetList().getAsset(RECOVERED_FROM).setValue("Service Provider");
        dentalClaim.postRecovery().submit();
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST REN-22805: Step 7");
        toSubTab(EDIT_CLAIM);
        assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).isDisabled();
        IntakeInformationTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-22805: Step 14");
        dentalClaim.create(tdSpecific().getTestData("TestData_WithOtherPayee"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.LOGGED_INTAKE);

        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(PENDED);

        LOGGER.info("TEST REN-22805: Step 15");
        dentalClaim.postRecovery().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_PostRecovery"));
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST REN-22805: Step 16");
        toSubTab(EDIT_CLAIM);
        String tin = intakeInformationTab.getAssetList().getAsset(SEARCH_PROVIDER).getAsset(TIN).getValue();
        assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).isDisabled();
        IntakeInformationTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-22805: Step 18, 20");
        dentalClaim.postRecovery().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_PostRecovery")
                .adjust(makeKeyPath(recoveryDetailsActionTab.getMetaKey(), RECOVERED_FROM.getLabel()), "Other"));
        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(2);
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(2)).hasCellWithValue(PAYEE_RECOVERED_FROM.getName(), company);
        });

        LOGGER.info("TEST REN-22805: Step 19");
        String partyOid = dbService.getRows(String.format(GET_PATYOID, claimNumber)).get(0).get("partyOid");
        assertThat(partyOid).isEqualTo(tin);

        LOGGER.info("TEST REN-22805: Step 21-22");
        ClaimFinancialsPage.buttonBalance.click();
        assertThat(ClaimBalancePage.tableClaimBalance.getRow(2)).hasCellWithValue(PAYEE_RECOVERED_FROM.getName(), company);
        buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("TEST REN-22805: Step 23");
        toSubTab(EDIT_CLAIM);
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).isDisabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(COMPANY_NAME)).isDisabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(TAX_IDENTIFICATION_NUMBER)).isDisabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS).getAsset(OtherPayeeAddressSection.STATE_PROVINCE)).isDisabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS).getAsset(OtherPayeeAddressSection.CITY)).isDisabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS).getAsset(OtherPayeeAddressSection.ZIP_POSTAL_CODE)).isDisabled();
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22834", component = CLAIMS_GROUPBENEFITS)
    public void testProcessClaimForOtherPayee2() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("TEST REN-22834: Step 1");
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), "D0160"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);

        LOGGER.info("TEST REN-22834: Step 2");
        toSubTab(FINANCIALS);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        LOGGER.info("TEST REN-22834: Step 5-6");
        dentalClaim.updatePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_UpdatePayment")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(),
                        REDUCTION_AMOUNT.getLabel()), "0.1")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(), PAYMENT_INTEREST.getLabel(),
                        INTEREST_AMOUNT.getLabel()), "0.1"), 1);

        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        dentalClaim.declinePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_DeclinePayment"), 1);
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.LOGGED_INTAKE);

        LOGGER.info("TEST REN-22834: Step 8");
        toSubTab(EDIT_CLAIM);
        assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).isDisabled();
        IntakeInformationTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-22834: Step 10");
        dentalClaim.create(tdSpecific().getTestData("TestData_WithOtherPayee")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), "D0160"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);

        LOGGER.info("TEST REN-22834: Step 11-12");
        toSubTab(FINANCIALS);
        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)).hasCellWithValue(PAYEE_RECOVERED_FROM.getName(), company);
        });

        LOGGER.info("TEST REN-22834: Step 15-16");
        dentalClaim.updatePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_UpdatePayment")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(),
                        REDUCTION_AMOUNT.getLabel()), "0.1")
                .adjust(TestData.makeKeyPath(FinancialPaymentPaymentDetailsActionTab.class.getSimpleName(),
                        INTEREST_AMOUNT.getLabel()), "0.1"), 1);
        dentalClaim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        dentalClaim.declinePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_DeclinePayment"), 1);
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.LOGGED_INTAKE);

        LOGGER.info("TEST REN-22834: Step 19");
        toSubTab(EDIT_CLAIM);
        assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).isDisabled();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22745", component = CLAIMS_GROUPBENEFITS)
    public void testProcessClaimForOtherPayee3() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("TEST REN-22745: Step 1");
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment"));

        LOGGER.info("TEST REN-22745: Step 2-3");
        toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(OTHER_COVERAGE).setValue(VALUE_YES);
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(COORDINATION_OF_BENEFITS)).isPresent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).hasOptions(ImmutableList.of("Service Provider", "Primary Insured", "Other"));
        });

        LOGGER.info("TEST REN-22745: Step 4-5");
        intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE).setValue("Other");
        intakeInformationTab.fillTab(tdSpecific().getTestData("TestData_Other"));
        IntakeInformationTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-22745: Step 7-9");
        toSubTab(EDIT_CLAIM);
        assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).hasValue("Other");

        intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE).setValueByIndex(1);
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(COMPANY_NAME)).isAbsent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS)).isAbsent();
        });

        intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE).setValue("Other");
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(COMPANY_NAME)).isPresent().isRequired();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(TAX_IDENTIFICATION_NUMBER)).isPresent().isRequired();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS).getAsset(ADDRESS_TYPE)).isPresent().isRequired();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS).getAsset(OtherPayeeAddressSection.CITY)).isPresent().isRequired();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS).getAsset(OtherPayeeAddressSection.ADDRESS_LINE_1)).isPresent().isRequired();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS).getAsset(OtherPayeeAddressSection.STATE_PROVINCE)).isPresent().isRequired();
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-22988", component = CLAIMS_GROUPBENEFITS)
    public void testProcessClaimForOtherPayeePrivileges() {
        mainApp().reopen();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("TEST REN-22988: Step 1-3");
        dentalClaim.create(tdSpecific().getTestData("TestData_WithOtherPayee"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST REN-22988: Step 4-5");
        adminApp().open();
        createUserWithPrivilege(ImmutableList.of("ALL", "EXCLUDE " + Privilege.CLAIM_ADD_OTHER_DENTAL_PAYEE.get()));

        search(claimNumber);
        toSubTab(EDIT_CLAIM);
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(COMPANY_NAME)).hasValue("company").isDisabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(TAX_IDENTIFICATION_NUMBER)).hasValue("36346").isDisabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS).getAsset(OtherPayeeAddressSection.STATE_PROVINCE)).hasValue("NY").isDisabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS).getAsset(OtherPayeeAddressSection.CITY)).hasValue("New York").isDisabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS).getAsset(OtherPayeeAddressSection.ZIP_POSTAL_CODE)).hasValue("10003").isDisabled();
        });

        intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE).setValueByIndex(1);
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(COMPANY_NAME)).isAbsent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(OTHER_PAYEE_ADDRESS)).isAbsent();
        });
    }
}
