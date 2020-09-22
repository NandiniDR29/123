package com.exigen.ren.modules.enrollment;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.CaseProfileConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.CreateEnrolledCensusFileIntakeManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.modules.caseprofile.CaseProfileBaseTest;
import com.exigen.ren.utils.components.Components;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CASE;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.caseprofile.metadata.CreateEnrolledCensusFileIntakeManagementTabMetaData.*;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.INTAKE_PROFILE_NAME;
import static com.exigen.ren.main.modules.caseprofile.tabs.CreateEnrolledCensusFileIntakeManagementTab.buttonExit;
import static com.exigen.ren.main.pages.summary.CaseProfileSummaryPage.tableFileIntakeManagement;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCreateEnrolledCensusFileIntakeManagementTabVerification extends CaseProfileBaseTest implements StatutoryDisabilityInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-41042", component = Components.CASE_PROFILE)
    public void testCreateEnrolledCensusFileIntakeManagementTabVerification_TC1() {
        LOGGER.info("General preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.CASE_PROFILE_NAME.getLabel()), "1"), statutoryDisabilityInsuranceMasterPolicy.getType());

        LOGGER.info("Steps#1, 2 execution");
        NavigationPage.toMainTab(CASE);
        CaseProfileSummaryPage.tableSelectCaseProfile.getRow(ImmutableMap.of(CaseProfileSummaryPage.CaseProfilesTable.CASE_PROFILE_NAME.getName(), "1"))
                .getCell(CaseProfileConstants.CaseProfileTable.CASE_PROFILE_NAME).controls.links.getFirst().click();

        LOGGER.info("Steps#3, 4 execution");
        caseProfile.createEnrolledCensus().start();

        LOGGER.info("Steps#5 - 10 verification");
        assertSoftly(softly -> {
            softly.assertThat(createEnrolledCensusTab.getAssetList().getAsset(INTAKE_PROFILE)).isPresent().isOptional().isEnabled().hasValue(EMPTY);
            softly.assertThat(createEnrolledCensusTab.getAssetList().getAsset(GENERATE_AND_UPLOAD_CENSUS_FILE)).isPresent().isDisabled();
            softly.assertThat(createEnrolledCensusTab.getAssetList().getAsset(NOTE)).isPresent().isOptional()
                    .hasValue("Please note that once you click \"Generate and Upload Census File\" " +
                            "the file will be queued for generation and will be available for viewing within the folder structure. " +
                            "File will be automatically uploaded into the Intake Profile selected above. This may take several minutes. " +
                            "If an error occurs during file generation or upload, then a BAM will generate to indicate this.");// failed by REN-45222
            softly.assertThat(buttonExit).isPresent().isEnabled();
        });

        LOGGER.info("Step#11 verification");
        CreateEnrolledCensusFileIntakeManagementTab.exitCreateEnrolledCensus();
        assertThat(CaseProfileSummaryPage.labelCaseProfileName).isPresent();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-41042", component = Components.CASE_PROFILE)
    public void testCreateEnrolledCensusFileIntakeManagementTabVerification_TC2() {
        LOGGER.info("General preconditions");
        String intakeProfileName = RandomStringUtils.randomNumeric(8);

        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_WithIntakeProfile_AutoSubGroup")
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.CASE_PROFILE_NAME.getLabel()), "1")
                .adjust(TestData.makeKeyPath(fileIntakeManagementTab.getMetaKey() + "[0]", INTAKE_PROFILE_NAME.getLabel()), intakeProfileName).resolveLinks(), statutoryDisabilityInsuranceMasterPolicy.getType());

        String intakeProfileNumber = tableFileIntakeManagement.getRowContains(TableConstants.FileIntakeManagement.INTAKE_PROFILE_NAME.getName(), intakeProfileName).getCell(TableConstants.FileIntakeManagement.INTAKE_PROFILE_NUMBER.getName()).getValue();
        String intakeProfileInitial = String.format("%s-%s", intakeProfileNumber, intakeProfileName);

        LOGGER.info("Step#1 execution");
        caseProfile.createEnrolledCensus().start();

        LOGGER.info("Step#2 verification");
        createEnrolledCensusTab.getAssetList().getAsset(INTAKE_PROFILE).setValue(intakeProfileInitial);
        assertThat(createEnrolledCensusTab.getAssetList().getAsset(INTAKE_PROFILE)).containsAllOptions(EMPTY, intakeProfileInitial);

        LOGGER.info("Step#3 verification");
        assertThat(createEnrolledCensusTab.getAssetList().getAsset(GENERATE_AND_UPLOAD_CENSUS_FILE)).isEnabled();

        LOGGER.info("Step#4 verification");
        CreateEnrolledCensusFileIntakeManagementTab.exitCreateEnrolledCensus();

        String intakeProfileNameUpdate1 = tdSpecific().getTestData("FileIntakeManagementTab1").getValue(INTAKE_PROFILE_NAME.getLabel());
        String intakeProfileNameUpdate2 = tdSpecific().getTestData("FileIntakeManagementTab2").getValue(INTAKE_PROFILE_NAME.getLabel());
        caseProfile.update().perform(tdSpecific().getTestData("UpdateCaseProfile1"));
        caseProfile.update().perform(tdSpecific().getTestData("UpdateCaseProfile2"));

        String intakeProfileNumberUp1 = tableFileIntakeManagement.getRowContains(TableConstants.FileIntakeManagement.INTAKE_PROFILE_NAME.getName(), intakeProfileNameUpdate1)
                .getCell(TableConstants.FileIntakeManagement.INTAKE_PROFILE_NUMBER.getName()).getValue();
        String intakeProfileNumberUp2 = tableFileIntakeManagement.getRowContains(TableConstants.FileIntakeManagement.INTAKE_PROFILE_NAME.getName(), intakeProfileNameUpdate2)
                .getCell(TableConstants.FileIntakeManagement.INTAKE_PROFILE_NUMBER.getName()).getValue();

        String intakeProfileUpdate1 = String.format("%s-%s", intakeProfileNumberUp1, intakeProfileNameUpdate1);
        String intakeProfileUpdate2 = String.format("%s-%s", intakeProfileNumberUp2, intakeProfileNameUpdate2);

        LOGGER.info("Step#5 verification");
        caseProfile.createEnrolledCensus().start();
        assertThat(createEnrolledCensusTab.getAssetList().getAsset(INTAKE_PROFILE)).containsAllOptions(EMPTY, intakeProfileInitial, intakeProfileUpdate1, intakeProfileUpdate2);
    }
}
