package com.exigen.ren.modules.cem.customer.nonindividual;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.EfolderConstants;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.module.efolder.defaulttabs.AddFileTab;
import com.exigen.ren.common.module.efolder.metadata.AddFileTabMetaData;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.regex.Pattern;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.common.module.efolder.EfolderContext.efolder;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ENROLLMENT_FILE_DIALOG;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.EnrollmentFileMetaData.*;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.ISSUE_ENROLLMENT;
import static com.exigen.ren.main.pages.summary.SummaryPage.activitiesAndUserNotes;
import static com.exigen.ren.utils.components.Components.CRM_CUSTOMER;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestVerificationOfEnrollmentFilePopUp extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {CEM, CUSTOMER_NONIND, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-45933", "REN-45942"}, component = CRM_CUSTOMER)
    public void testVerificationOfEnrollmentFilePopUp() {
        LOGGER.info("General preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        createDefaultStatutoryDisabilityInsuranceMasterPolicy();
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER);
        assertSoftly(softly -> {

            LOGGER.info("REN-45942 TC#1 Step#1 verification");
            customerNonIndividual.inquiry().start();
            softly.assertThat(generalTab.getAssetList().getAsset(ISSUE_ENROLLMENT)).isPresent().isEnabled();

            LOGGER.info("REN-45942 TC#1 Step#2 verification");
            Tab.buttonCancel.click();
            softly.assertThat(generalTab.getAssetList().getAsset(ISSUE_ENROLLMENT)).isAbsent();

            LOGGER.info("REN-45942 TC#2 Step#1 verification");
            customerNonIndividual.update().start();
            softly.assertThat(generalTab.getAssetList().getAsset(ISSUE_ENROLLMENT)).isAbsent();

            LOGGER.info("REN-45942 TC#2 Step#1 verification");
            Tab.buttonCancel.click();
            Page.dialogConfirmation.confirm();
            softly.assertThat(CustomerSummaryPage.labelCustomerName).isPresent();
        });

        LOGGER.info("TC#1 Steps#1-5 verification");
        String enrollmentInitialUploaded = tdSpecific().getTestData("TestData_EnrollmentFile", AddFileTab.class.getSimpleName()).getValue(AddFileTabMetaData.NAME.getLabel());
        efolder.addDocument(tdSpecific().getTestData("TestData_EnrollmentFile"), EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName());

        String uploadedFileName = Efolder.getFileName(EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName(), enrollmentInitialUploaded);
        String enrollmentFileFullNameInitial = performVerificationForUploadedFile(enrollmentInitialUploaded);

        LOGGER.info("TC#2 Steps#1-5 verification");
        commonVerificationsWithFileRemoving(enrollmentFileFullNameInitial, uploadedFileName);

        LOGGER.info("TC#1 Steps#6, 7 verification");
        String enrollmentMidTermUploaded = tdSpecific().getTestData("TestData_MidTerm_Enrollment", AddFileTab.class.getSimpleName()).getValue(AddFileTabMetaData.NAME.getLabel());
        efolder.addDocument(tdSpecific().getTestData("TestData_MidTerm_Enrollment"), EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName());

        String uploadedFileNameMidterm = Efolder.getFileName(EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName(), enrollmentMidTermUploaded);
        String enrollmentFileFullNameMidTerm = performVerificationForUploadedFile(enrollmentMidTermUploaded);

        LOGGER.info("TC#2 Step#7 verification");
        commonVerificationsWithFileRemoving(enrollmentFileFullNameMidTerm, uploadedFileNameMidterm);

        LOGGER.info("TC#1 Steps#6, 7 verification");
        String enrollmentAnnualUploaded = tdSpecific().getTestData("TestData_Annual_Enrollment", AddFileTab.class.getSimpleName()).getValue(AddFileTabMetaData.NAME.getLabel());
        efolder.addDocument(tdSpecific().getTestData("TestData_Annual_Enrollment"), EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName());

        String uploadedFileNameAnnual = Efolder.getFileName(EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName(), enrollmentAnnualUploaded);
        String enrollmentFileFullNameAnnual = performVerificationForUploadedFile(enrollmentAnnualUploaded);

        LOGGER.info("TC#2 Step#7 verification");
        commonVerificationsWithFileRemoving(enrollmentFileFullNameAnnual, uploadedFileNameAnnual);
    }

    private String performVerificationForUploadedFile(String fileName) {
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();
        customerNonIndividual.inquiry().start();
        enrollmentFileFieldsVerification();

        LOGGER.info("Step#2 verification");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE_TYPE).setValue("Initial");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(RETRIEVE).click();
        String fileNameExpectedStarts = getEnrollmentFileExpectedValuesFromDb(customerName, fileName).split(" ")[0];

        assertSoftly(softly -> {
            softly.assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE)).hasValue(EMPTY);
            softly.assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE).getAllValues())
                    .anySatisfy(value -> assertThat(value).matches(String.format(fileNameExpectedStarts.concat("%s"), Pattern.compile(".*"))));

            LOGGER.info("Step#3 verification");
            generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE).setValueContains(fileNameExpectedStarts);
            softly.assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(SUBMIT_POPUP)).isEnabled();
            softly.assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(REMOVE_BTN_POPUP)).isEnabled();
        });
        String exactValue = generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE).getValue();

        LOGGER.info("Step#4 verification");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(CLOSE_POPUP).click();
        enrollmentFileFieldsVerification();

        LOGGER.info("Step#5 verification");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(CLOSE_POPUP).click();
        Tab.buttonCancel.click();
        return exactValue;
    }

    private void commonVerificationsWithFileRemoving(String enrollmentFileFullName, String uploadedFileName) {
        LOGGER.info("TC#2 Steps#1,2 verification");
        customerNonIndividual.inquiry().start();
        generalTab.getAssetList().getAsset(ISSUE_ENROLLMENT).click();
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE_TYPE).setValue("Initial");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(RETRIEVE).click();
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE).setValueContains(enrollmentFileFullName);
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(REMOVE_BTN_POPUP).click();
        Page.dialogConfirmation.confirm();
        assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE_TYPE)).isAbsent();

        LOGGER.info("TC#2 Step#3 verification");
        generalTab.getAssetList().getAsset(ISSUE_ENROLLMENT).click();
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE_TYPE).setValue("Initial");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(RETRIEVE).click();
        assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE).getAllValues()).containsSequence(EMPTY);

        LOGGER.info("TC#2 Step#4 verification");
        generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(CLOSE_POPUP).click();
        Tab.buttonCancel.click();
        assertThat(activitiesAndUserNotes).hasRowsThatContain(DESCRIPTION, String.format("Performer removed enrollment file %s", enrollmentFileFullName));

        LOGGER.info("TC#2 Step#5 verification");
        assertThat(Efolder.isDocumentExist(EfolderConstants.EFolderNonIndCustomer.ENROLLMENT_UPLOAD.getName(), uploadedFileName)).isTrue();
    }

    private void enrollmentFileFieldsVerification() {
        generalTab.getAssetList().getAsset(ISSUE_ENROLLMENT).click();
        assertSoftly(softly -> {
            softly.assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE_TYPE)).hasValue(EMPTY);
            softly.assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(ENROLLMENT_FILE)).hasValue(EMPTY);
            softly.assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(RETRIEVE)).isDisabled();
            softly.assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(SUBMIT_POPUP)).isDisabled();
            softly.assertThat(generalTab.getAssetList().getAsset(ENROLLMENT_FILE_DIALOG).getAsset(REMOVE_BTN_POPUP)).isDisabled();
        });
    }

    private String getEnrollmentFileExpectedValuesFromDb(String customerName, String filename) {
        Map<String, String> ss = DBService.get().getRow("SELECT fileName, id, updatedOn FROM ENROLLMENT_FILE_STAGE WHERE fileName LIKE ?",
                String.format("%s%%%s", customerName, filename));
        return String.format("%s_%s_%s", ss.get("fileName"), ss.get("id"), ss.get("updatedOn"));
    }
}
