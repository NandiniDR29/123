package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.PatientHistoryRecordTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.PatientHistoryRecordActionTab;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.LineOverrideTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.exigen.ren.rest.claim.ClaimRestContext;
import com.exigen.ren.rest.claim.model.common.claimbody.claim.PatientHistoryModel;
import com.exigen.ren.rest.claim.model.common.claimbody.damages.LossExtensionModel;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.main.enums.ActionConstants.LINE_VIEW;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.PatientHistoryRecordTabMetaData.SUBMITTED_CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.buttonSubmitClaim;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimUpdateListCDTRemarkCodes extends ClaimGroupBenefitsDNBaseTest implements ClaimRestContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-25423", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUpdateListCDTRemarkCodes() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();

        LOGGER.info("Steps 1,2");
        dentalClaim.initiate(dentalClaim.getDefaultTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY));
        intakeInformationTab.fillTab(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithPayment")
                .mask(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel())));
        String todayDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(todayDate);
        new TextBox(By.id("policyDataGatherForm:sedit_ClaimsProcedure_lossInfo_lossDescription_input")).setValue("D0440", Waiters.AJAX.then(Waiters.SLEEP(1000)));
        assertThat(new StaticElement(By.id("policyDataGatherForm:procedureType_error"))).hasValue("No records found.");

        LOGGER.info("Step 3");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).clear();
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D0412");
        assertThat(intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE)).hasNoWarning();

        LOGGER.info("Step 5");
        IntakeInformationTab.buttonAddService.click();
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(todayDate);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D0233");
        assertThat(intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE)).hasNoWarning();

        LOGGER.info("Step 7");
        IntakeInformationTab.buttonAddService.click();
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(todayDate);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue(REVIEW_REQUIRED_1);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(TOOTH).setValue("3");

        LOGGER.info("Step 9");
        buttonSubmitClaim.click();
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED);
            softly.assertThat(ClaimSummaryPage.tableResultsOfAdjudication).with(DECISION, "Disallowed").with(REMARK_CODE, "MI07006").hasMatchingRows(1);
        });

        LOGGER.info("Step 11");
        ClaimSummaryPage.tableResultsOfAdjudication.getRow(REMARK_CODE.getName(), "MI07006").getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE).click();
        AbstractContainer<?, ?> lineOverrideTabAssetList = new LineOverrideTab().getAssetList();
        new TextBox(By.id("policyDataGatherForm:sedit_ClaimsDentalValuesOverride_procedureCd_input")).setValue("D0440", Waiters.AJAX.then(Waiters.SLEEP(1000)));
        assertThat(new StaticElement(By.id("policyDataGatherForm:procedureCd_error"))).hasValue("No records found.");
        new TextBox(By.id("policyDataGatherForm:sedit_ClaimsDentalConsultantReview_alternateCDTCd_input")).setValue("D0440", Waiters.AJAX.then(Waiters.SLEEP(1000)));
        assertThat(new StaticElement(By.id("policyDataGatherForm:alternateCDTCd_error"))).hasValue("No records found.");

        LOGGER.info("Step 12");
        lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(OverrideLineValuesSection.COVERED_CDT_CODE).setValue("D0412");
        assertThat(lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(OverrideLineValuesSection.COVERED_CDT_CODE)).hasNoWarning();
        lineOverrideTabAssetList.getAsset(CONSULTANT_REVIEW).getAsset(ConsultantReviewSection.ALTERNATE_CDT_CODE).setValue("D0412");
        assertThat(lineOverrideTabAssetList.getAsset(CONSULTANT_REVIEW).getAsset(ConsultantReviewSection.ALTERNATE_CDT_CODE)).hasNoWarning();

        LOGGER.info("Step 13");
        lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(OverrideLineValuesSection.COVERED_CDT_CODE).setValue("D0233");
        assertThat(lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(OverrideLineValuesSection.COVERED_CDT_CODE)).hasNoWarning();
        lineOverrideTabAssetList.getAsset(CONSULTANT_REVIEW).getAsset(ConsultantReviewSection.ALTERNATE_CDT_CODE).setValue("D0233");
        assertThat(lineOverrideTabAssetList.getAsset(CONSULTANT_REVIEW).getAsset(ConsultantReviewSection.ALTERNATE_CDT_CODE)).hasNoWarning();

        LOGGER.info("Step 15");
        lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_RULES).getAsset(OverrideLineRulesSection.OVERRIDE_REMARK_CODE).setValue(true);
        new TextBox(By.id("policyDataGatherForm:sedit_ClaimsDentalRulesOverride_newRemarkCode_input")).setValue("RR001", Waiters.AJAX.then(Waiters.SLEEP(1000)));
        assertThat(new StaticElement(By.id("policyDataGatherForm:newRemarkCode_error"))).hasValue("No records found.");

        LOGGER.info("Step 16");
        lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_RULES).getAsset(OverrideLineRulesSection.NEW_REMARK_CODE).setValue("EL00154");
        assertThat(lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_RULES).getAsset(OverrideLineRulesSection.NEW_REMARK_CODE)).hasNoWarning();

        LOGGER.info("Step 17");
        lineOverrideTabAssetList.getAsset(REASON).setValue("Reason");
        Tab.buttonSaveAndExit.click();
        assertThat(ClaimSummaryPage.tableResultsOfAdjudication).with(DECISION, "Disallowed").with(REMARK_CODE, "MI07006").hasMatchingRows(1);

        LOGGER.info("Step 19");
        claim.addPatientHistoryRecord().start();
        AbstractContainer<?, ?> addPatientHistoryAssetList = claim.addPatientHistoryRecord().getWorkspace().getTab(PatientHistoryRecordActionTab.class).getAssetList();
        new TextBox(By.id("policyDataGatherForm:sedit_ClaimsDentalAddPatientHistoryAction_patientHistoryEntity_procedureCd_input")).setValue("D0440", Waiters.AJAX.then(Waiters.SLEEP(1000)));
        assertThat(new StaticElement(By.id("policyDataGatherForm:procedureType_error"))).hasValue("No records found.");
        new TextBox(By.id("policyDataGatherForm:sedit_ClaimsDentalAddPatientHistoryAction_patientHistoryEntity_consideredProcedureCd_input")).setValue("D0440", Waiters.AJAX.then(Waiters.SLEEP(1000)));
        assertThat(new StaticElement(By.id("policyDataGatherForm:consideredProcedureType_error"))).hasValue("No records found.");

        LOGGER.info("Step 20");
        addPatientHistoryAssetList.getAsset(SUBMITTED_CDT_CODE).setValue("D0412");
        assertThat(addPatientHistoryAssetList.getAsset(SUBMITTED_CDT_CODE)).hasNoWarning();
        addPatientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.COVERED_CDT_CODE).setValue("D0412");
        assertThat(addPatientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.COVERED_CDT_CODE)).hasNoWarning();

        LOGGER.info("Step 21");
        addPatientHistoryAssetList.getAsset(SUBMITTED_CDT_CODE).setValue("D0233");
        assertThat(addPatientHistoryAssetList.getAsset(SUBMITTED_CDT_CODE)).hasNoWarning();
        addPatientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.COVERED_CDT_CODE).setValue("D0233");
        assertThat(addPatientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.COVERED_CDT_CODE)).hasNoWarning();

        LOGGER.info("Step 23");
        addPatientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.DOS).setValue(todayDate);
        claim.addPatientHistoryRecord().submit();
        assertThat(tablePatientHistory).hasRows(1);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-25559", component = CLAIMS_GROUPBENEFITS)
    public void testClaimUpdateListCDTRemarkCodesUI() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("Step 1");
        dentalClaim.initiate(dentalClaim.getDefaultTestData("DataGatherCertificate", DEFAULT_TEST_DATA_KEY));
        AbstractContainer<?, ?> intakeInformationAssetList = intakeInformationTab.getAssetList();
        assertSoftly(softly -> {
            softly.assertThat(intakeInformationAssetList.getAsset(SUBMITTED_SERVICES).getAsset(TOOTH)).isPresent().isOptional().hasValue("");
            softly.assertThat(intakeInformationAssetList.getAsset(SUBMITTED_SERVICES).getAsset(ORAL_CAVITY)).isPresent().isOptional().hasValue("");
        });

        LOGGER.info("Steps 3,4");
        intakeInformationAssetList.getAsset(SUBMITTED_SERVICES).getAsset(TOOTH).setValue("tooth");
        intakeInformationAssetList.getAsset(SUBMITTED_SERVICES).getAsset(ORAL_CAVITY).setValue("or");

        LOGGER.info("Step 5");
        intakeInformationTab.submitTab();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        assertRestResponseDamages(claimNumber, "TOOTH", "or");
        assertSoftly(softly -> {
            softly.assertThat(tableSubmittedServices.getColumn(IntakeInformationTab.SubmittedServicesColumns.TOOTH)).isPresent().hasCellWithValue(1, "TOOTH");
            softly.assertThat(tableSubmittedServices.getColumn(IntakeInformationTab.SubmittedServicesColumns.ORAL_CAVITY)).isPresent().hasCellWithValue(1, "or");
        });

        LOGGER.info("Step 7");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.fillTab(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithPayment")
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), REVIEW_REQUIRED_1)
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel(), TOOTH.getLabel()), "3")
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel(), ORAL_CAVITY.getLabel()), ""));
        buttonSubmitClaim.click();
        assertRestResponseDamages(claimNumber, "3", "");
        assertSoftly(softly -> {
            softly.assertThat(tableSubmittedServices.getRow(IntakeInformationTab.SubmittedServicesColumns.TOOTH.getName(), "3")).isPresent();
            softly.assertThat(tableSubmittedServices.getRow(IntakeInformationTab.SubmittedServicesColumns.ORAL_CAVITY.getName(), "")).isPresent();
        });

        LOGGER.info("Step 9");
        tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(LINE_VIEW).click();
        AbstractContainer<?, ?> lineOverrideTabAssetList = new LineOverrideTab().getAssetList();
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Consultant Remark Code"))).isAbsent();
            softly.assertThat(lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(LineOverrideTabMetaData.OverrideLineValuesSection.CATEGORY)).isAbsent();
        });

        LOGGER.info("Step 10");
        Tab.buttonCancel.click();
        ClaimSummaryPage.tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE).click();
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Consultant Remark Code"))).isAbsent();
            softly.assertThat(lineOverrideTabAssetList.getAsset(OVERRIDE_LINE_VALUES).getAsset(LineOverrideTabMetaData.OverrideLineValuesSection.CATEGORY)).isAbsent();
        });

        LOGGER.info("Step 11");
        lineOverrideTabAssetList.getAsset(REASON).setValue("Reason");
        Tab.buttonSaveAndExit.click();
        assertThat(tableResultsOfAdjudication).isPresent();

        LOGGER.info("Step 12");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.PATIENT_HISTORY.get());
        assertSoftly(softly -> {
            softly.assertThat(tablePatientHistory.getColumn(TableConstants.ClaimTablePatientHistory.TOOTH)).isEmpty();
            softly.assertThat(tablePatientHistory.getColumn(TableConstants.ClaimTablePatientHistory.ORAL_CAVITY)).isEmpty();
        });

        LOGGER.info("Step 13");
        dentalClaim.addPatientHistoryRecord().start();
        AbstractContainer<?, ?> patientHistoryAssetList = claim.addPatientHistoryRecord().getWorkspace().getTab(PatientHistoryRecordActionTab.class).getAssetList();
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Category"))).isAbsent();
            softly.assertThat(patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.TOOTH)).isPresent().isOptional().hasValue("");
            softly.assertThat(patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.ORAL_CAVITY)).isPresent().isOptional().hasValue("");
        });

        LOGGER.info("Steps 14-16");
        patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.DOS).setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
        patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.SUBMITTED_CDT_CODE).setValue("D0233");
        patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.TOOTH).setValue("Tooth");
        patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.ORAL_CAVITY).setValue("OC");
        dentalClaim.addPatientHistoryRecord().submit();
        assertRestResponseDamages(claimNumber, "3", "");
        assertRestResponsePatientHistory(claimNumber, "TOOTH", "OC");
        assertThat(tablePatientHistory).hasMatchingRows(1, ImmutableMap.of(TableConstants.ClaimTablePatientHistory.TOOTH.getName(), "TOOTH",
                                                                           TableConstants.ClaimTablePatientHistory.ORAL_CAVITY.getName(), "OC"));

        LOGGER.info("Step 17");
        dentalClaim.updatePatientHistoryRecord().start(tablePatientHistory.getRow(1));
        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Category"))).isAbsent();
            softly.assertThat(patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.TOOTH)).hasValue("TOOTH");
            softly.assertThat(patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.ORAL_CAVITY)).hasValue("OC");
        });

        LOGGER.info("Step 18");
        patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.TOOTH).setValue("");
        patientHistoryAssetList.getAsset(PatientHistoryRecordTabMetaData.ORAL_CAVITY).setValue("");
        dentalClaim.updatePatientHistoryRecord().submit();
        assertRestResponseDamages(claimNumber, "3", "");
        assertRestResponsePatientHistory(claimNumber, "", "");
        assertThat(tablePatientHistory).hasMatchingRows(1, ImmutableMap.of(TableConstants.ClaimTablePatientHistory.TOOTH.getName(), "",
                                                                           TableConstants.ClaimTablePatientHistory.ORAL_CAVITY.getName(), ""));

        LOGGER.info("Step 20");
        NavigationPage.toSubTab(EDIT_CLAIM);

        LOGGER.info("Step 22");
        intakeInformationAssetList.getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D0210");
        intakeInformationAssetList.getAsset(SUBMITTED_SERVICES).getAsset(TOOTH).setValue("tooth2");
        intakeInformationAssetList.getAsset(SUBMITTED_SERVICES).getAsset(ORAL_CAVITY).setValue("up");
        intakeInformationTab.submitTab();
        assertRestResponseDamages(claimNumber, "TOOTH2", "up");
        assertRestResponsePatientHistory(claimNumber, "TOOTH2", "up");
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
            softly.assertThat(tableSubmittedServices).hasMatchingRows(1, ImmutableMap.of(IntakeInformationTab.SubmittedServicesColumns.TOOTH.getName(), "TOOTH2",
                                                                                         IntakeInformationTab.SubmittedServicesColumns.ORAL_CAVITY.getName(), "up"));
        });

        LOGGER.info("Step 23");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.PATIENT_HISTORY.get());
        assertThat(tablePatientHistory).hasRows(2);
        assertThat(tablePatientHistory).hasMatchingRows(1, ImmutableMap.of(TableConstants.ClaimTablePatientHistory.TOOTH.getName(), "TOOTH2",
                                                                           TableConstants.ClaimTablePatientHistory.ORAL_CAVITY.getName(), "up"));
    }

    private void assertRestResponseDamages(String claimNumber, String toothArea, String oralCavity) {
        LossExtensionModel lossExtensionModel = claimCoreRestService.getClaimImage(claimNumber).getModel().getDamages().get(0).getLoss().getExtension();
        assertSoftly(softly -> {
            softly.assertThat(lossExtensionModel.getToothLetter()).isEqualTo(toothArea);
            softly.assertThat(lossExtensionModel.getOralCavityArea()).isEqualTo(oralCavity);
        });
    }

    private void assertRestResponsePatientHistory(String claimNumber, String toothArea, String oralCavity) {
        PatientHistoryModel patientHistoryModel = claimCoreRestService.getClaimImage(claimNumber).getModel().getClaim().getParties().get(0).getExtension().getPatientHistory().get(0);
        assertSoftly(softly -> {
            softly.assertThat(patientHistoryModel.getToothArea()).isEqualTo(toothArea);
            softly.assertThat(patientHistoryModel.getOralCavityArea()).isEqualTo(oralCavity);
        });
    }

}
