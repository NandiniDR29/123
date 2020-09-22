package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.common.metadata.LossContextTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.LossContextTab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimPolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import io.netty.util.internal.StringUtil;
import org.testng.annotations.Test;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.ADJUDICATION;
import static com.exigen.ren.common.pages.NavigationPage.isSubTabSelected;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.SubmittedServicesColumns.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimIntegrationWithPolicyAllowToCreateClaim extends ClaimGroupBenefitsDNBaseTest implements CustomerContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23447", component = CLAIMS_GROUPBENEFITS)
    public void testClaimIntegrationWithPolicy() {
        LOGGER.info("REN-23447 Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();
        String insured = PolicySummaryPage.tableInsuredPrincipalInformation.getRow(1).getCell(PolicySummaryPage.InsuredPrincipalInformation.NAME.getName()).getValue();
        String dateOfBirth = PolicySummaryPage.tableInsuredPrincipalInformation.getRow(1).getCell(PolicySummaryPage.InsuredPrincipalInformation.DATE_OF_BIRTH.getName()).getValue();
        String address = PolicySummaryPage.tableInsuredPrincipalInformation.getRow(1).getCell(PolicySummaryPage.InsuredPrincipalInformation.ADDRESS.getName()).getValue();

        LOGGER.info("REN-23447 STEP#13");
        dentalClaim.initiateCreation();
        dentalClaim.getInitializationView().fillUpTo(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY), LossContextTab.class);
        CustomAssertions.assertThat(dentalClaim.getInitializationView().getTab(LossContextTab.class).getAssetList().getAsset(LossContextTabMetaData.TYPE_OF_CLAIM))
                .hasValue("Dental Claim");

        LOGGER.info("REN-23447 STEP#14");
        dentalClaim.getInitializationView().fillFrom(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY), LossContextTab.class);

        assertSoftly(softly -> {

            LOGGER.info("REN-23447 STEP#15");
            softly.assertThat(ClaimPolicySummaryPage.labelClaimTypeOfClaim).isPresent();
            softly.assertThat(ClaimPolicySummaryPage.labelClaimStatus).isPresent();
            softly.assertThat(ClaimPolicySummaryPage.labelClaimPatient).isPresent();
            softly.assertThat(ClaimPolicySummaryPage.labelClaimPolicyProduct).isPresent();
            softly.assertThat(ClaimPolicySummaryPage.labelClaimSpecialHandling).hasValue(StringUtil.EMPTY_STRING);
            softly.assertThat(ClaimPolicySummaryPage.labelClaimTotalIncurred).isPresent();

            softly.assertThat(ClaimPolicySummaryPage.labelClaimInsured).valueContains(insured.split(" ")[0]);
            softly.assertThat(ClaimPolicySummaryPage.labelClaimPolicyNumber).valueContains("CP");
            softly.assertThat(ClaimPolicySummaryPage.labelClaimPolicyStatus).hasValue("Active");
            softly.assertThat(ClaimPolicySummaryPage.labelClaimPolicyPlan).hasValue("ALACARTE");

            LOGGER.info("REN-23447 STEP#16");
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PATIENT).getAsset(PatientMetaData.NAME)).valueContains(insured);
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PATIENT).getAsset(PatientMetaData.RELATIONSHIP_TO_PRIMARY_INSURED)).hasValue("Self");
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PATIENT).getAsset(PatientMetaData.DATE_OF_BIRTH)).hasValue(dateOfBirth);
            softly.assertThat(intakeInformationTab.getAssetList().getAsset(PATIENT).getAsset(PatientMetaData.ADDRESS)).valueContains(address);

            ImmutableList.of(
                    SearchProviderMetaData.TIN,
                    SearchProviderMetaData.LICENSE_NUMBER,
                    SearchProviderMetaData.LICENSE_NPI,
                    SearchProviderMetaData.LICENSE_STATE_PROVINCE,
                    SearchProviderMetaData.SPECIALTY,
                    SearchProviderMetaData.ZIP_POSTAL_CODE,
                    SearchProviderMetaData.ADDRESS_LINE_1,
                    SearchProviderMetaData.ADDRESS_LINE_2,
                    SearchProviderMetaData.CITY,
                    SearchProviderMetaData.STATE_PROVINCE
                    )
                    .forEach(control -> softly.assertThat(intakeInformationTab.getAssetList().getAsset(IntakeInformationTabMetaData.SEARCH_PROVIDER).getAsset(control)).isPresent());

            ImmutableList.of(
                    IntakeInformationTabMetaData.TYPE_OF_TRANSACTION,
                    IntakeInformationTabMetaData.RECEIVED_DATE,
                    IntakeInformationTabMetaData.SOURCE,
                    IntakeInformationTabMetaData.PREDETERMINATION_NUM,
                    IntakeInformationTabMetaData.PAYEE_TYPE,
                    IntakeInformationTabMetaData.ORTHO_MONTHS,
                    IntakeInformationTabMetaData.OTHER_COVERAGE)
                    .forEach(control -> softly.assertThat(intakeInformationTab.getAssetList().getAsset(control)).isPresent());

            intakeInformationTab.getAssetList().getAsset(OTHER_COVERAGE).setValue("Yes");

            softly.assertThat(intakeInformationTab.getAssetList().getAsset(IntakeInformationTabMetaData.COORDINATION_OF_BENEFITS).getAssetNames())
                    .allMatch(control -> intakeInformationTab.getAssetList().getAsset(IntakeInformationTabMetaData.COORDINATION_OF_BENEFITS).getAsset(control).isPresent());

            ImmutableList.of(
                    SubmittedServicesSection.DOS,
                    SubmittedServicesSection.CDT_CODE,
                    SubmittedServicesSection.TOOTH,
                    SubmittedServicesSection.ORAL_CAVITY,
                    SubmittedServicesSection.CHARGE,
                    SubmittedServicesSection.SURFACE,
                    SubmittedServicesSection.QUANTITY
            ).forEach(control -> softly.assertThat(intakeInformationTab.getAssetList().getAsset(IntakeInformationTabMetaData.SUBMITTED_SERVICES).getAsset(control)).isPresent());

            softly.assertThat(IntakeInformationTab.tableSubmittedServices.getHeader())
                    .hasValue(ImmutableList.of("", DOS.getName(), CDT_CODE.getName(), TOOTH.getName(), ORAL_CAVITY.getName(), SURFACE.getName(),
                            QUANTITY.getName(), CHARGE.getName(), ""));
            softly.assertThat(IntakeInformationTab.buttonSubmitClaim).isPresent().isEnabled();

            LOGGER.info("REN-23447 STEP#17-18");
            dentalClaim.getDefaultWorkspace().fillFrom(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY), intakeInformationTab.getClass());
            softly.assertThat(isSubTabSelected(ADJUDICATION)).withFailMessage("%s Tab did't Open", ADJUDICATION).isTrue();

            softly.assertThat(ClaimSummaryPage.tableClaimData.getHeader()).hasValue(ImmutableList.of(
                    TableConstants.ClaimSummaryClaimDataTableExtended.TYPE_OF_TRANSACTION.getName(),
                    TableConstants.ClaimSummaryClaimDataTableExtended.RECEIVED_DATE.getName(),
                    TableConstants.ClaimSummaryClaimDataTableExtended.CLEAN_CLAIM_DATE.getName(),
                    TableConstants.ClaimSummaryClaimDataTableExtended.SOURCE.getName(),
                    TableConstants.ClaimSummaryClaimDataTableExtended.PREDETERMINATION_NUM.getName(),
                    TableConstants.ClaimSummaryClaimDataTableExtended.PAYEE_TYPE.getName(),
                    TableConstants.ClaimSummaryClaimDataTableExtended.ORTHO_MONTHS.getName(),
                    TableConstants.ClaimSummaryClaimDataTableExtended.OTHER_COVERAGE.getName()
            ));

            softly.assertThat(ClaimSummaryPage.tableClaimData.getRow(1))
                    .hasCellWithValue(TableConstants.ClaimSummaryClaimDataTableExtended.TYPE_OF_TRANSACTION.getName(),
                            dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY).getValue(intakeInformationTab.getMetaKey(), TYPE_OF_TRANSACTION.getLabel()))
                    .hasCellWithValue(TableConstants.ClaimSummaryClaimDataTableExtended.RECEIVED_DATE.getName(), DateTimeUtils.getCurrentDateTime().format(MM_DD_YYYY))
                    .hasCellWithValue(TableConstants.ClaimSummaryClaimDataTableExtended.SOURCE.getName(), "Manual Entry")
                    .hasCellWithValue(TableConstants.ClaimSummaryClaimDataTableExtended.PAYEE_TYPE.getName(), "Service Provider")
                    .hasCellWithValue(TableConstants.ClaimSummaryClaimDataTableExtended.OTHER_COVERAGE.getName(), "Yes");

            softly.assertThat(ClaimSummaryPage.tableSubmittedServices.getHeader()).hasValue(ImmutableList.of(
                    LINE.getName(), LINE_ID.getName(), DOS.getName(), CDT_CODE.getName(), TOOTH.getName(),
                    ORAL_CAVITY.getName(), SURFACE.getName(), QUANTITY.getName(), CHARGE.getName()));
            softly.assertThat(ClaimSummaryPage.tableSubmittedServices.getRow(1))
                    .hasCellWithValue(CDT_CODE.getName(), dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                            .getValue(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.SUBMITTED_SERVICES.getLabel(), CDT_CODE.getName()))
                    .hasCellWithValue(CHARGE.getName(), "$".concat(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                            .getValue(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.SUBMITTED_SERVICES.getLabel(), CHARGE.getName())));
        });
    }
}
