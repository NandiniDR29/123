package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.AbstractEditableStringElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.Users;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.PatientHistoryRecordTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.PatientHistoryRecordActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.exigen.ren.rest.claim.ClaimRestContext;
import com.exigen.ren.rest.claim.model.common.claimbody.claim.AddressModel;
import com.exigen.ren.rest.claim.model.common.claimbody.claim.PartyModel;
import com.exigen.ren.utils.AdminActionsHelper;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.exigen.ipb.eisa.verification.CustomAssertionsExtended.assertThat;
import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.ActionConstants.REMOVE;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.ADJUDICATED;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.LOGGED_INTAKE;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_PATTERN;
import static com.exigen.ren.main.enums.TableConstants.ClaimTablePatientHistory.SUBMITTED_CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.PatientMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.buttonSubmitClaim;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAbilityToEnterOtherPatient extends ClaimGroupBenefitsDNBaseTest implements ClaimRestContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-29823", "REN-29999"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimAbilityToEnterOtherPatientRESTAndHistory() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithPayment"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(LOGGED_INTAKE);

        LOGGER.info("REN-29823 Steps 1,3");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.fillTab(tdSpecific().getTestData("TestData_Individual1"));
        Tab.buttonTopSave.click();
        TestData testDataInd1 = tdSpecific().getTestData("TestData_Individual1").getTestData(intakeInformationTab.getMetaKey());
        assertRestImage(claimNumber, testDataInd1, 1);

        LOGGER.info("REN-29823 Step 4");
        TestData testDataInd2 = tdSpecific().getTestData("TestData_Individual2");
        intakeInformationTab.fillTab(testDataInd2);
        Tab.buttonTopSave.click();
        String insured1Name = String.format("%s %s", testDataInd1.getValue(PATIENT.getLabel(), FIRST_NAME.getLabel()), testDataInd1.getValue(PATIENT.getLabel(), LAST_NAME.getLabel()));
        intakeInformationTab.getAssetList().getAsset(PATIENT).getAsset(PatientMetaData.NAME).setValue(insured1Name);
        Tab.buttonTopSave.click();
        assertRestImage(claimNumber, testDataInd2.getTestData(intakeInformationTab.getMetaKey()), 1);
        assertRestImage(claimNumber, testDataInd1, 2);
        assertThat(claimCoreRestService.getClaimImage(claimNumber).getModel().getClaim().getParties().get(2).getRoles().get(0).getClaimsPartyRoleCd()).isEqualTo("ClaimsDentalPatient");

        LOGGER.info("REN-29999 Step 1");
        buttonSubmitClaim.click();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ADJUDICATED);

        LOGGER.info("REN-29999 Step 2");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.PATIENT_HISTORY.get());
        assertThat(tablePatientHistory).hasRows(1);

        LOGGER.info("REN-29999 Step 3");
        dentalClaim.addPatientHistoryRecord().start();
        AbstractContainer<?, ?> patientHistoryAssetList = dentalClaim.addPatientHistoryRecord().getWorkspace().getTab(PatientHistoryRecordActionTab.class).getAssetList();
        assertThat(patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.PATIENT_ID).getValue()).isNotEmpty();

        LOGGER.info("REN-29999 Step 4");
        patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.DOS).setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
        patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.SUBMITTED_CDT_CODE).setValue("D0120");
        dentalClaim.addPatientHistoryRecord().submit();
        assertThat(tablePatientHistory).hasRows(2);

        LOGGER.info("REN-29999 Step 5");
        TestData patientHistoryTestData = dentalClaim.getDefaultTestData("ClaimPatientHistory", DEFAULT_TEST_DATA_KEY);
        dentalClaim.updatePatientHistoryRecord().perform(tablePatientHistory.getRow(SUBMITTED_CDT_CODE.getName(), "D0120"), patientHistoryTestData);
        String submittedCdtCode = patientHistoryTestData.getValue(PatientHistoryRecordActionTab.class.getSimpleName(), SUBMITTED_CDT_CODE.getName());
        assertSoftly(softly -> {
            softly.assertThat(tablePatientHistory).hasRows(2);
            softly.assertThat(tablePatientHistory).with(SUBMITTED_CDT_CODE, submittedCdtCode).hasMatchingRows(1);
        });

        LOGGER.info("REN-29999 Step 6");
        tablePatientHistory.getRow(SUBMITTED_CDT_CODE.getName(), submittedCdtCode).getCell(23).controls.links.get(REMOVE).click();
        Page.dialogConfirmation.confirm();
        assertThat(tablePatientHistory).hasRows(1);
    }

    private void assertRestImage(String claimNumber, TestData td, int partyIndex) {
        String firstName = td.getValue(PATIENT.getLabel(), FIRST_NAME.getLabel());
        String lastName = td.getValue(PATIENT.getLabel(), LAST_NAME.getLabel());
        String relToPrimaryInsured = td.getValue(PATIENT.getLabel(), RELATIONSHIP_TO_PRIMARY_INSURED.getLabel());
        String dateOfBirth = LocalDate.parse(td.getValue(PATIENT.getLabel(), DATE_OF_BIRTH.getLabel()), MM_DD_YYYY).atStartOfDay().format(YYYY_MM_DD_HH_MM_SS_Z);
        String country = td.getValue(PATIENT_ADDRESS.getLabel(), PatientAddressMetaData.COUNTRY.getLabel());
        String zipPostalCode = td.getValue(PATIENT_ADDRESS.getLabel(), PatientAddressMetaData.ZIP_POSTAL_CODE.getLabel());
        String city = td.getValue(PATIENT_ADDRESS.getLabel(), PatientAddressMetaData.CITY.getLabel());
        String stateProvince = td.getValue(PATIENT_ADDRESS.getLabel(), PatientAddressMetaData.STATE_PROVINCE.getLabel());
        String addressLine1 = td.getValue(PATIENT_ADDRESS.getLabel(), PatientAddressMetaData.ADDRESS_LINE_1.getLabel());
        String addressLine2 = td.getValue(PATIENT_ADDRESS.getLabel(), PatientAddressMetaData.ADDRESS_LINE_2.getLabel());
        String addressLine3 = td.getValue(PATIENT_ADDRESS.getLabel(), PatientAddressMetaData.ADDRESS_LINE_3.getLabel());
        String addressType = td.getValue(PATIENT_ADDRESS.getLabel(), PatientAddressMetaData.ADDRESS_TYPE.getLabel());

        PartyModel partyModel = claimCoreRestService.getClaimImage(claimNumber).getModel().getClaim().getParties().get(partyIndex);
        assertSoftly(softly -> {
            softly.assertThat(partyModel.getFirstName()).isEqualTo(firstName);
            softly.assertThat(partyModel.getLastName()).isEqualTo(lastName);
            softly.assertThat(partyModel.getRelationShipToInsuredCd().getUIName()).isEqualTo(relToPrimaryInsured);
            softly.assertThat(partyModel.getBirthDt()).isEqualTo(dateOfBirth);

            AddressModel addressModel = partyModel.getAddresses().get(0);
            softly.assertThat(addressModel.getCountryCd().getUIName()).isEqualTo(country);
            softly.assertThat(addressModel.getPostalCode()).isEqualTo(zipPostalCode);
            softly.assertThat(addressModel.getCity()).isEqualTo(city);
            softly.assertThat(addressModel.getStateProvCd()).isEqualTo(stateProvince);
            softly.assertThat(addressModel.getAddressLine1()).isEqualTo(addressLine1);
            softly.assertThat(addressModel.getAddressLine2()).isEqualTo(addressLine2);
            softly.assertThat(addressModel.getAddressLine3()).isEqualTo(addressLine3);
            softly.assertThat(addressModel.getAddressTypeCd().getUIName()).isEqualTo(addressType);
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-29043", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAbilityToEnterOtherPatientRoles() {
        adminApp().open();
        String roleName = AdminActionsHelper.searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.CLAIM_ADD_OTHER_PATIENT.get())), roleCorporate);
        User userNoPrivilege = AdminActionsHelper.createUserWithSpecificRole(roleName, profileCorporate);
        mainApp().open(Users.DEFAULT.getLogin(), Users.DEFAULT.getPassword());
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithPayment"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(LOGGED_INTAKE);

        LOGGER.info("Step 1");
        NavigationPage.toSubTab(EDIT_CLAIM);
        AbstractContainer<?, ?> intakeInfoTabAssetList = intakeInformationTab.getAssetList();
        assertThat(intakeInfoTabAssetList.getAsset(PATIENT).getAsset(PatientMetaData.NAME)).valueContains("Customer");

        LOGGER.info("Step 2");
        loginAndNavigateToEditClaim(userNoPrivilege, claimNumber);
        assertThat(intakeInfoTabAssetList.getAsset(PATIENT).getAsset(PatientMetaData.NAME)).valueContains("Customer");

        LOGGER.info("Step 3");
        loginAndNavigateToEditClaim(Users.DEFAULT, claimNumber);
        intakeInfoTabAssetList.getAsset(PATIENT).getAsset(PatientMetaData.NAME).setValue("Other Individual");
        assertThat(intakeInfoTabAssetList.getAsset(PATIENT).getAsset(PatientMetaData.NAME)).hasNoWarning();

        LOGGER.info("Step 4");
        Tab.buttonTopSave.click();
        loginAndNavigateToEditClaim(userNoPrivilege, claimNumber);
        List<AssetDescriptor<? extends AbstractEditableStringElement>> fieldsPatient = Arrays.asList(FIRST_NAME, LAST_NAME, RELATIONSHIP_TO_PRIMARY_INSURED, DATE_OF_BIRTH);
        List<AssetDescriptor<? extends AbstractEditableStringElement>> fieldsPatientAddress = Arrays.asList(
                PatientAddressMetaData.ZIP_POSTAL_CODE, PatientAddressMetaData.ADDRESS_LINE_1, PatientAddressMetaData.CITY, PatientAddressMetaData.STATE_PROVINCE);
        assertSoftly(softly -> fieldsPatient.forEach(
                field -> softly.assertThat(intakeInfoTabAssetList.getAsset(PATIENT).getAsset(field)).isDisabled())
        );
        assertSoftly(softly -> fieldsPatientAddress.forEach(
                field -> softly.assertThat(intakeInfoTabAssetList.getAsset(PATIENT_ADDRESS).getAsset(field)).isDisabled())
        );

        LOGGER.info("Step 5");
        buttonSubmitClaim.click();
        List<AssetDescriptor<? extends AbstractEditableStringElement>> fieldsAll = new ArrayList<>(fieldsPatient);
        fieldsAll.addAll(fieldsPatientAddress);
        assertSoftly(softly -> fieldsAll.forEach(
                field -> softly.assertThat(ErrorPage.tableError).hasMatchingRows(ErrorPage.TableError.MESSAGE.getName(),
                        String.format(ERROR_PATTERN, field.getLabel())))
        );

        LOGGER.info("Step 6");
        Tab.buttonCancel.click();
        assertSoftly(softly -> fieldsPatient.forEach(
                field -> softly.assertThat(intakeInfoTabAssetList.getAsset(PATIENT).getAsset(field)).isDisabled())
        );
        assertSoftly(softly -> fieldsPatientAddress.forEach(
                field -> softly.assertThat(intakeInfoTabAssetList.getAsset(PATIENT_ADDRESS).getAsset(field)).isDisabled())
        );

        LOGGER.info("Step 7");
        loginAndNavigateToEditClaim(Users.DEFAULT, claimNumber);
        TestData testDataIndivid1 = tdSpecific().getTestData("TestData_Individual1").mask(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), PATIENT.getLabel(), PatientMetaData.NAME.getLabel()));
        intakeInformationTab.fillTab(testDataIndivid1);
        Tab.buttonTopSave.click();
        String individ1FirstName = testDataIndivid1.getValue(intakeInformationTab.getMetaKey(), PATIENT.getLabel(), FIRST_NAME.getLabel());
        assertThat(intakeInfoTabAssetList.getAsset(PATIENT).getAsset(FIRST_NAME)).hasValue(individ1FirstName);

        LOGGER.info("Step 8");
        intakeInfoTabAssetList.getAsset(PATIENT).getAsset(PatientMetaData.NAME).setValueContains("Customer");
        String individ1LastName = testDataIndivid1.getValue(intakeInformationTab.getMetaKey(), PATIENT.getLabel(), LAST_NAME.getLabel());
        intakeInfoTabAssetList.getAsset(PATIENT).getAsset(PatientMetaData.NAME).setValueContains(String.format("%s %s", individ1FirstName, individ1LastName));
        TestData testDataIndivid2 = tdSpecific().getTestData("TestData_Individual2").mask(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), PATIENT.getLabel(), PatientMetaData.NAME.getLabel()));
        intakeInformationTab.fillTab(testDataIndivid2);
        Tab.buttonTopSave.click();
        assertThat(intakeInfoTabAssetList.getAsset(PATIENT).getAsset(FIRST_NAME)).hasValue(testDataIndivid2.getValue(intakeInformationTab.getMetaKey(), PATIENT.getLabel(), FIRST_NAME.getLabel()));

        LOGGER.info("Step 9");
        loginAndNavigateToEditClaim(userNoPrivilege, claimNumber);
        assertThat(intakeInfoTabAssetList.getAsset(PATIENT).getAsset(PatientMetaData.NAME).getAllValues()).hasSize(2);

        LOGGER.info("Step 10");
        String individ2FirstName = testDataIndivid2.getValue(intakeInformationTab.getMetaKey(), PATIENT.getLabel(), FIRST_NAME.getLabel());
        String individ2LastName = testDataIndivid2.getValue(intakeInformationTab.getMetaKey(), PATIENT.getLabel(), LAST_NAME.getLabel());
        intakeInfoTabAssetList.getAsset(PATIENT).getAsset(PatientMetaData.NAME).setValueContains(String.format("%s %s", individ2FirstName, individ2LastName));
        assertSoftly(softly -> fieldsPatient.forEach(
                field -> softly.assertThat(intakeInfoTabAssetList.getAsset(PATIENT).getAsset(field)).isDisabled())
        );
        assertSoftly(softly -> fieldsPatientAddress.forEach(
                field -> softly.assertThat(intakeInfoTabAssetList.getAsset(PATIENT_ADDRESS).getAsset(field)).isDisabled())
        );

        LOGGER.info("Step 11");
        buttonSubmitClaim.click();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ADJUDICATED);
    }

    private void loginAndNavigateToEditClaim(User user, String claimNumber) {
        mainApp().reopen(user.getLogin(), user.getPassword());
        MainPage.QuickSearch.search(claimNumber);
        NavigationPage.toSubTab(EDIT_CLAIM);
    }

}
