package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.Tab.buttonCancel;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.common.pages.ErrorPage.TableError.MESSAGE;
import static com.exigen.ren.common.pages.ErrorPage.tableError;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimPartiesTable.ADDRESS;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.*;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.STATUS;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.InternationalProviderAddressSection.ZIP_POSTAL_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.InternationalProviderAddressSection.COUNTRY;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.InternationalProviderAddressSection.ADDRESS_TYPE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.InternationalProviderAddressSection.ADDRESS_LINE_2;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.InternationalProviderAddressSection.ADDRESS_LINE_3;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.InternationalProviderAddressSection.STATE_PROVINCE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SearchProviderMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.InternationalProviderSection.FIRST_NAME;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.InternationalProviderSection.INTERNATIONAL_PROVIDER_VALUE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.removeProvider;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithPrivilege;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestProcessClaimForInternationalProvider extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-37749", component = CLAIMS_GROUPBENEFITS)
    public void testProcessClaimForInternationalProvider1() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("TEST REN-37749: Step 2");
        dentalClaim.initiate(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE)).hasValue(VALUE_NO).isOptional().isEnabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(FIRST_NAME)).isAbsent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(LAST_NAME)).isAbsent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(PRACTICE_NAME)).isAbsent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(SEARCH_PROVIDER)).isPresent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).hasValue(EMPTY).isEnabled();
        });

        LOGGER.info("TEST REN-37749: Step 4");
        intakeInformationTab.fillTab(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE)).hasValue(VALUE_NO).isDisabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(FIRST_NAME)).isAbsent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(LAST_NAME)).isAbsent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(PRACTICE_NAME)).isAbsent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).hasValue(EMPTY).isEnabled();
        });

        LOGGER.info("TEST REN-37749: Step 6.1");
        removeProvider();
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE)).hasValue(VALUE_NO).isEnabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).hasValue(EMPTY).isEnabled();
        });

        LOGGER.info("TEST REN-37749: Step 6.2");
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE).setValue(VALUE_YES);
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE)).isPresent().isOptional();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(SEARCH_PROVIDER)).isAbsent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).hasValue("Primary Insured").isDisabled();

            ImmutableList.of(FIRST_NAME, LAST_NAME, PRACTICE_NAME).forEach(field ->
                    softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(field)).isPresent().isRequired());

            ImmutableList.of(COUNTRY, ZIP_POSTAL_CODE, ADDRESS_TYPE, ADDRESS_LINE_1, CITY).forEach(field ->
                    softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(field)).isPresent().isEnabled().isRequired());

            ImmutableList.of(ADDRESS_LINE_2, ADDRESS_LINE_3, STATE_PROVINCE).forEach(field ->
                    softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(field)).isPresent().isEnabled().isOptional());
        });

        LOGGER.info("TEST REN-37749: Step 7");
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(FIRST_NAME).setValue("Name");
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(ZIP_POSTAL_CODE).setValue("24332");
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE).setValue(VALUE_NO);

        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(SEARCH_PROVIDER)).isPresent();
            ImmutableList.of(TIN, LICENSE_NUMBER, LICENSE_NPI, LICENSE_STATE_PROVINCE, SPECIALTY, ADDRESS_LINE_1).forEach(field ->
                    softly.assertThat(intakeInformationTab.getAssetList().getAsset(SEARCH_PROVIDER).getAsset(field)).isPresent().hasValue(EMPTY));

            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).hasValue(EMPTY).isEnabled();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS)).isAbsent();
            ImmutableList.of(FIRST_NAME, LAST_NAME, PRACTICE_NAME).forEach(field ->
                    softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(field)).isAbsent());
        });

        LOGGER.info("TEST REN-37749: Step 9");
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE).setValue(VALUE_YES);
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE)).isPresent().isOptional();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(SEARCH_PROVIDER)).isAbsent();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).hasValue("Primary Insured").isDisabled();

            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(ADDRESS_TYPE)).hasValue("Street Address").isEnabled().isRequired();
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(COUNTRY)).hasValue("United States").isEnabled().isRequired();
            ImmutableList.of(FIRST_NAME, LAST_NAME, PRACTICE_NAME).forEach(field ->
                    softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(field)).isPresent().isRequired());

            ImmutableList.of(ZIP_POSTAL_CODE, ADDRESS_LINE_1, CITY).forEach(field ->
                    softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(field)).isPresent().isEnabled().isRequired());

            ImmutableList.of(ADDRESS_LINE_2, ADDRESS_LINE_3, STATE_PROVINCE).forEach(field ->
                    softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(field)).hasValue(EMPTY).isPresent().isEnabled().isOptional());
        });

        LOGGER.info("TEST REN-37749: Step 10");
        IntakeInformationTab.buttonSaveAndExit.click();
        assertSoftly(softly -> {
            softly.assertThat(tableInternationalProvider.getRow(1)).hasCellWithValue(PROVIDER_NAME.getLabel(), EMPTY);
            softly.assertThat(tableInternationalProvider.getRow(1)).hasCellWithValue(PRACTICE_NAME.getLabel(), EMPTY);
            softly.assertThat(tableInternationalProvider.getRow(1)).hasCellWithValue(ADDRESS, "US");
        });

        LOGGER.info("TEST REN-37749: Step 12");
        toSubTab(EDIT_CLAIM);
        intakeInformationTab.fillTab(tdSpecific().getTestData("TestData_WithoutProvider"));
        IntakeInformationTab.buttonSubmitClaim.click();
        assertSoftly(softly -> ImmutableList.of(FIRST_NAME, LAST_NAME, PRACTICE_NAME, ADDRESS_LINE_1, CITY, ZIP_POSTAL_CODE).forEach(field ->
                softly.assertThat(tableError).hasMatchingRows(MESSAGE.getName(), String.format("'%s' is required", field.getLabel()))));
        buttonCancel.click();

        LOGGER.info("TEST REN-37749: Step 16");
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(ZIP_POSTAL_CODE).setValue("22322");
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(CITY).setValue("Providence");

        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(ZIP_POSTAL_CODE).setValue("2232233");
        assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(CITY)).hasValue("Providence");

        LOGGER.info("TEST REN-37749: Step 19");
        ImmutableList.of("Canada", "Ireland", "Colombia", "United States").forEach(country -> {
            intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(COUNTRY).setValue(country);
            assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(STATE_PROVINCE)).isPresent();
        });

        LOGGER.info("TEST REN-37749: Step 21");
        intakeInformationTab.fillTab(tdSpecific().getTestData("Data1"));
        IntakeInformationTab.buttonSaveAndExit.click();
        assertSoftly(softly -> {
            softly.assertThat(tableProvider).isAbsent();
            softly.assertThat(tableInternationalProvider.getRow(1)).hasCellWithValue(PROVIDER_NAME.getLabel(), "FirstName LastName");
            softly.assertThat(tableInternationalProvider.getRow(1)).hasCellWithValue(PRACTICE_NAME.getLabel(), "PracticeName");
            softly.assertThat(tableInternationalProvider.getRow(1)).hasCellWithValue(ADDRESS, "Address Line 1, Providence, AK, 22122, US");
        });

        LOGGER.info("TEST REN-37749: Step 22");
        toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(FIRST_NAME).setValue("NewName");
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(ZIP_POSTAL_CODE).setValue("44444");
        IntakeInformationTab.buttonSaveAndExit.click();
        assertSoftly(softly -> {
            softly.assertThat(tableInternationalProvider.getRow(1)).hasCellWithValue(PROVIDER_NAME.getLabel(), "NewName LastName");
            softly.assertThat(tableInternationalProvider.getRow(1)).hasCellWithValue(PRACTICE_NAME.getLabel(), "PracticeName");
            softly.assertThat(tableInternationalProvider.getRow(1)).hasCellWithValue(ADDRESS, "Address Line 1, Providence, AK, 44444, US");
        });

        LOGGER.info("TEST REN-37749: Step 23");
        toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE).setValue(VALUE_NO);
        intakeInformationTab.fillTab(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), PAYEE_TYPE.getLabel()), "index=1"));
        IntakeInformationTab.buttonSubmitClaim.click();
        assertThat(labelClaimStatus.getValue()).startsWith(PENDED);

        toSubTab(EDIT_CLAIM);
        removeProvider();
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE).setValue(VALUE_YES);
        IntakeInformationTab.buttonSaveAndExit.click();
        assertSoftly(softly -> ImmutableList.of(FIRST_NAME, LAST_NAME, PRACTICE_NAME, ADDRESS_LINE_1, CITY, ZIP_POSTAL_CODE).forEach(field ->
                softly.assertThat(tableError).hasMatchingRows(MESSAGE.getName(), String.format("'%s' is required", field.getLabel()))));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38283", component = CLAIMS_GROUPBENEFITS)
    public void testProcessClaimForInternationalProvider2() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("TEST REN-38283: Step 1");
        dentalClaim.initiate(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));

        LOGGER.info("TEST REN-38283: Step 4");
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE).setValue(VALUE_YES);
        intakeInformationTab.fillTab(tdSpecific().getTestData("TestData_WithoutProvider"));
        IntakeInformationTab.buttonSubmitClaim.click();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), "Claim temporary cannot be processed because International Provider is added.");

        LOGGER.info("TEST REN-38283: Step 5");
        buttonCancel.click();
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE).setValue(VALUE_NO);
        IntakeInformationTab.buttonSubmitClaim.click();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), "There is no provider");

        LOGGER.info("TEST REN-38283: Step 6");
        buttonCancel.click();
        intakeInformationTab.fillTab(tdSpecific().getTestData("TestData_WithProvider"));
        IntakeInformationTab.buttonSubmitClaim.click();
        assertThat(labelClaimStatus.getValue()).startsWith(PENDED);

        LOGGER.info("TEST REN-38283: Step 7");
        toSubTab(EDIT_CLAIM);
        removeProvider();

        IntakeInformationTab.buttonSaveAndExit.click();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), "There is no provider");

        LOGGER.info("TEST REN-38283: Step 8");
        buttonCancel.click();
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE).setValue(VALUE_YES);
        intakeInformationTab.fillTab(tdSpecific().getTestData("Data1"));
        IntakeInformationTab.buttonSaveAndExit.click();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), "Claim temporary cannot be processed because International Provider is added.");
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38288", component = CLAIMS_GROUPBENEFITS)
    public void testProcessClaimForInternationalProvider3() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        adminApp().open();
        User user = createUserWithPrivilege(ImmutableList.of("ALL", "EXCLUDE " + Privilege.CLAIM_INTERNATIONAL_PROVIDER.get()));
        search(policyNumber);

        LOGGER.info("TEST REN-38288: Step 1");
        dentalClaim.initiate(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));
        assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE)).hasValue(VALUE_NO).isDisabled();

        intakeInformationTab.fillTab(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        IntakeInformationTab.buttonSaveAndExit.click();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("TEST REN-38288: Step 3");
        mainApp().reopen();
        search(claimNumber);

        toSubTab(EDIT_CLAIM);
        assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE)).hasValue(VALUE_NO).isDisabled();

        LOGGER.info("TEST REN-38288: Step 5");
        removeProvider();
        intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE).setValue(VALUE_YES);

        LOGGER.info("TEST REN-38288: Step 6");
        intakeInformationTab.fillTab(tdSpecific().getTestData("Data1"));
        IntakeInformationTab.buttonSaveAndExit.click();
        assertThat(labelClaimStatus).hasValue(LOGGED_INTAKE);

        LOGGER.info("TEST REN-38288: Step 7");
        mainApp().reopen(user.getLogin(), user.getPassword());
        search(claimNumber);

        toSubTab(EDIT_CLAIM);
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE)).hasValue(VALUE_YES).isDisabled();

            ImmutableList.of(FIRST_NAME, LAST_NAME, PRACTICE_NAME).forEach(field ->
                    softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(field)).isPresent().isDisabled());

            ImmutableList.of(ADDRESS_TYPE, COUNTRY, ZIP_POSTAL_CODE, ADDRESS_LINE_1, CITY, ADDRESS_LINE_2, ADDRESS_LINE_3, STATE_PROVINCE).forEach(field ->
                    softly.assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER_ADDRESS).getAsset(field)).isPresent().isDisabled());
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39242", component = CLAIMS_GROUPBENEFITS)
    public void testProcessClaimForInternationalProvider4() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("TEST REN-39242: Step 1");
        dentalClaim.initiate(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));

        LOGGER.info("TEST REN-39242: Step 2");
        intakeInformationTab.fillTab(tdSpecific().getTestData("TestData_WithProvider"));
        IntakeInformationTab.buttonSubmitClaim.click();
        assertThat(labelClaimStatus.getValue()).startsWith(PENDED);

        LOGGER.info("TEST REN-39242: Step 3");
        dentalClaim.postRecovery().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_PostRecovery"));
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)).hasCellWithValue(STATUS.getName(), "Issued");

        LOGGER.info("TEST REN-39242: Step 4");
        toSubTab(EDIT_CLAIM);
        removeProvider();
        assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE)).hasValue(VALUE_NO).isDisabled();

        LOGGER.info("TEST REN-39242: Step 5");
        buttonCancel.click();
        Page.dialogConfirmation.confirm();

        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithoutPayment")
                .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), "D0160"));
        dentalClaim.claimSubmit().perform();
        assertThat(labelClaimStatus).hasValue(ADJUDICATED);

        LOGGER.info("TEST REN-39242: Step 6");
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", DEFAULT_TEST_DATA_KEY));
        toSubTab(EDIT_CLAIM);
        assertThat(intakeInformationTab.getAssetList().getAsset(INTERNATIONAL_PROVIDER).getAsset(INTERNATIONAL_PROVIDER_VALUE)).hasValue(VALUE_NO).isDisabled();
    }
}
