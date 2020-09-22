package com.exigen.ren.modules.platform.admin;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.admin.modules.agencyvendor.agency.AgencyContext;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.LicenseTabMetaData;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.LicenseTabMetaData.AddLicenseMetaData;
import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.AgencyInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.LicenseInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.admin.modules.agencyvendor.common.tabs.AgencyVendorSearchTab;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.main.enums.BamConstants;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.agencyvendor.agency.metadata.LicenseTabMetaData.ADD_LICENSE;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS;
import static com.exigen.ren.utils.components.Components.Platform_Admin;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInitiateAppointmentDocumentGeneration extends BaseTest implements AgencyContext {

    private final String dbQueryWhere = "SELECT processedOnJobDate FROM License WHERE licensePermitNumber = ?";
    private final String timeStampTemplate = "%s \\d{2}:\\d{2}:\\d{2}.\\d{1,3}";
    private Job job = new Job(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB.getGroupName());

    @Test(groups = {PLATFORM, PLATFORM_ADMIN, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-39859", "REN-39871"}, component = Platform_Admin)
    public void testDocGenAppLetterJobProcessing() {
        String agencyLicenseNumber1 = String.format("1_%s", RandomStringUtils.random(6, false, true));
        String updateLicenseNumber1 = String.format("1_1_%s", RandomStringUtils.random(6, false, true));
        String agencyLicenseNumber4 = String.format("4_%s", RandomStringUtils.random(6, false, true));
        String agencyLicenseNumber5 = String.format("5_%s", RandomStringUtils.random(6, false, true));
        LocalDate localDate = DateTimeUtils.getCurrentDateTime().toLocalDate();

        LOGGER.info("REN-39859 Precondition & Step1");
        adminApp().open();
        String agencyCode1 = createAgencyAndGetAgencyCode(agencyLicenseNumber1, localDate, localDate.plusYears(1), "IL");

        LOGGER.info("REN-39859 Step 2");
        CustomAssertions.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber1)).isEmpty();

        LOGGER.info("REN-39859 Step 3");
        JobRunner.executeJob(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB);

        LOGGER.info("REN-39859 Step 4");
        Optional<String> timeStamp = DBService.get().getValue(dbQueryWhere, agencyLicenseNumber1);
        CustomAssertions.assertThat(timeStamp.get()).matches(String.format(timeStampTemplate, DateTimeUtils.getCurrentDateTime().format(YYYY_MM_DD)));

        LOGGER.info("REN-39859 Step 5-6");
        checkBamMessage(agencyCode1, 1);

        LOGGER.info("REN-39871 Precondition");
        createAgencyAndGetAgencyCode(agencyLicenseNumber4, localDate.minusMonths(2), localDate.minusDays(1), "AZ");
        createAgencyAndGetAgencyCode(agencyLicenseNumber5, localDate.minusMonths(2), localDate.minusDays(1), "CA");

        LOGGER.info("REN-39871 Step 1");
        agency.navigate();
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_CODE.getLabel(), agencyCode1));
        agency.update().perform(tdSpecific().getTestData("TestData_UpdateLicense")
                .adjust(makeKeyPath(LicenseInfoTab.class.getSimpleName(), ADD_LICENSE.getLabel(), AddLicenseMetaData.LICENSE_NUMBER.getLabel()), updateLicenseNumber1), 1);

        LOGGER.info("REN-39871 Step 2");
        assertSoftly(softly -> {
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber1).get()).isNotNull().isEqualTo(timeStamp.get());
            softly.assertThat(DBService.get().getValue(dbQueryWhere, updateLicenseNumber1)).isEmpty();
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber4)).isEmpty();
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber5)).isEmpty();
        });

        LOGGER.info("REN-39871 Step 3");
        JobRunner.executeJob(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB, true);

        LOGGER.info("REN-39871 Step 4");
        assertSoftly(softly -> {
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber1).get()).isNotNull().isEqualTo(timeStamp.get());
            softly.assertThat(DBService.get().getValue(dbQueryWhere, updateLicenseNumber1).get()).matches(String.format(timeStampTemplate, localDate.format(YYYY_MM_DD)));
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber4)).isEmpty();
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber5)).isEmpty();
        });

        LOGGER.info("REN-39871 Step 5-6");
        checkBamMessage(agencyCode1, 3);
    }

    private void checkBamMessage(String agencyCode1, int i) {
        adminApp().reopen();
        agency.navigate();
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_CODE.getLabel(), agencyCode1));
        agency.update().start(1);
        CustomAssertions.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(i))
                .hasCellWithValue("Description", String.format(BamConstants.APPOINTMENT_LETTER_GENERATION_INITIATED, tdSpecific().getTestData("TestData_UpdateLicense")
                        .getValue(LicenseInfoTab.class.getSimpleName(), ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel())));
        AgencyInfoTab.cancelClickAndCloseDialog();
    }

    @Test(groups = {PLATFORM, PLATFORM_ADMIN, WITH_TS})
    @TestInfo(testCaseId = {"REN-39873"}, component = Platform_Admin)
    public void testDocGenAppLetterJobProcessingWithRedoAgencyCdParameter() {
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
        String agencyLicenseNumber1 = String.format("1_%s", RandomStringUtils.random(6, false, true));
        String agencyLicenseNumber2 = String.format("2_%s", RandomStringUtils.random(6, false, true));
        String agencyLicenseNumber3 = String.format("3_%s", RandomStringUtils.random(6, false, true));
        String agencyLicenseNumber4 = String.format("4_%s", RandomStringUtils.random(6, false, true));

        LocalDate localDate = DateTimeUtils.getCurrentDateTime().toLocalDate();

        adminApp().open();
        String agencyCode1 = createAgencyAndGetAgencyCode(agencyLicenseNumber1, localDate, localDate.plusYears(1), "IL");


        job.setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", String.format("redoAgencyCd = %s", agencyCode1)));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB.getGroupName(), job));
        adminApp().reopen();
        String agencyCode2 = createAgencyAndGetAgencyCode(agencyLicenseNumber2, localDate, localDate.plusYears(1), "NY");
        createAgencyAndGetAgencyCode(agencyLicenseNumber3, localDate, localDate.plusYears(1), "NJ");
        String agencyCode4 = createAgencyAndGetAgencyCode(agencyLicenseNumber4, localDate.minusYears(1), localDate.minusDays(1), "AZ");

        LOGGER.info("REN-39873 Step 1");
        assertSoftly(softly -> {
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber1).get())
                    .matches(String.format(timeStampTemplate, DateTimeUtils.getCurrentDateTime().format(YYYY_MM_DD)));
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber2)).isEmpty();
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber3)).isEmpty();
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber4)).isEmpty();
        });

        LOGGER.info("REN-39873 Step 2");
        ImmutableList.of(agencyCode1, agencyCode2, agencyCode4).forEach(agencyCode -> {
            job.setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", String.format("redoAgencyCd=%s", agencyCode)));
            JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB.getGroupName(), job), true);
        });

        LOGGER.info("REN-39873 Step 3");
        assertSoftly(softly -> {
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber1).get())
                    .matches(String.format(timeStampTemplate, DateTimeUtils.getCurrentDateTime().format(YYYY_MM_DD)));
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber2).get())
                    .matches(String.format(timeStampTemplate, DateTimeUtils.getCurrentDateTime().format(YYYY_MM_DD)));
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber3)).isEmpty();
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber4)).isEmpty();
        });
    }


    @Test(groups = {PLATFORM, PLATFORM_ADMIN, WITH_TS})
    @TestInfo(testCaseId = {"REN-40041"}, component = Platform_Admin)
    public void testDocGenAppLetterJobProcessingWithProcessingJobDtTc1() {
        LOGGER.info("REN-40041 Precondition");
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(2));
        String agencyLicenseNumber5 = String.format("5_%s", RandomStringUtils.random(6, false, true));
        String agencyLicenseNumber6 = String.format("6_%s", RandomStringUtils.random(6, false, true));
        String agencyLicenseNumber7 = String.format("7_%s", RandomStringUtils.random(6, false, true));
        LocalDate localDate = DateTimeUtils.getCurrentDateTime().toLocalDate();
        adminApp().open();
        createAgencyAndGetAgencyCode(agencyLicenseNumber5, LocalDate.of(2020, 1, 1), localDate.plusDays(1), "IL");
        createAgencyAndGetAgencyCode(agencyLicenseNumber6, LocalDate.of(2020, 1, 1), localDate.minusDays(1), "NY");
        String agencyCode7 = createAgencyAndGetAgencyCode(agencyLicenseNumber7, LocalDate.of(2020, 1, 1), localDate.plusYears(1), "NJ");


        job.setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", String.format("redoAgencyCd=%s", agencyCode7)));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB.getGroupName(), job));

        LOGGER.info("REN-40041 TC#1 Step 2");
        job.setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", String.format("processingJobDt=%s", LocalDateTime.now().plusHours(1).format(YYYY_MM_DD_HH_MM_SS))));
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB.getGroupName(), job), true);

        LOGGER.info("REN-40041 TC#1 Step 3");
        assertSoftly(softly -> {
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber5).get())
                    .matches(String.format(timeStampTemplate, localDate.format(YYYY_MM_DD)));
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber6)).isEmpty();
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber7).get())
                    .matches(String.format(timeStampTemplate, localDate.format(YYYY_MM_DD)));
        });
    }


    @Test(groups = {PLATFORM, PLATFORM_ADMIN, WITH_TS})
    @TestInfo(testCaseId = {"REN-40041"}, component = Platform_Admin)
    public void testDocGenAppLetterJobProcessingWithProcessingJobDtTc2() {
        LOGGER.info("REN-40041 TC#2 Precondition ");
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(3));
        String agencyLicenseNumber8_1 = String.format("8_1_%s", RandomStringUtils.random(6, false, true));
        String agencyLicenseNumber8_2 = String.format("8_2_%s", RandomStringUtils.random(6, false, true));
        String agencyLicenseNumber8_3 = String.format("8_3_%s", RandomStringUtils.random(6, false, true));

        List<TestData> tdLicenses = new ArrayList<>(Arrays.asList(
                tdSpecific().getTestData("License")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.LICENSE_NUMBER.getLabel()), agencyLicenseNumber8_1)
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.EFFECTIVE_DATE.getLabel()), "$<today-1y>")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.EXPIRATION_DATE.getLabel()), "$<today-1d>")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel()), "AZ"),
                tdSpecific().getTestData("License")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.LICENSE_NUMBER.getLabel()), agencyLicenseNumber8_2)
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.EFFECTIVE_DATE.getLabel()), "$<today-1y>")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.EXPIRATION_DATE.getLabel()), "$<today>")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel()), "CA"),
                tdSpecific().getTestData("License")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.LICENSE_NUMBER.getLabel()), agencyLicenseNumber8_3)
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.EFFECTIVE_DATE.getLabel()), "$<today-1y>")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.EXPIRATION_DATE.getLabel()), "$<today+1d>")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel()), "ME")));
        adminApp().open();
        agency.createAgency(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).adjust(LicenseInfoTab.class.getSimpleName(), tdLicenses));

        LOGGER.info("REN-40041 TC#2 Step 1");
        JobRunner.executeJob(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB);

        LOGGER.info("REN-40041 TC#2 Step 2");
        assertSoftly(softly -> {
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber8_1)).isEmpty();
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber8_2)).isEmpty();
            softly.assertThat(DBService.get().getValue(dbQueryWhere, agencyLicenseNumber8_3).get())
                    .matches(String.format(timeStampTemplate, DateTimeUtils.getCurrentDateTime().format(YYYY_MM_DD)));
        });
    }

    private String createAgencyAndGetAgencyCode(String licenseNumber, LocalDate effectiveDate, LocalDate expDate, String state) {
        String agencyName = agency.createAgency(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(LicenseInfoTab.class.getSimpleName(), ADD_LICENSE.getLabel(), AddLicenseMetaData.LICENSE_NUMBER.getLabel()), licenseNumber)
                .adjust(makeKeyPath(LicenseInfoTab.class.getSimpleName(), ADD_LICENSE.getLabel(), AddLicenseMetaData.EFFECTIVE_DATE.getLabel()), effectiveDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(LicenseInfoTab.class.getSimpleName(), ADD_LICENSE.getLabel(), AddLicenseMetaData.EXPIRATION_DATE.getLabel()), expDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(LicenseInfoTab.class.getSimpleName(), ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel()), state));
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName));
        return AgencyVendorSearchTab.tableAgencies.getRowContains("Agency Name", agencyName).getCell("Agency Code").getValue();
    }
}
