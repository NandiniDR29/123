package com.exigen.ren.modules.docgen.general;

import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.AgencyInfoMetaData;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.ContactInfoMetaData;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.LicenseTabMetaData;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.LicenseTabMetaData.AddLicenseMetaData;
import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.AgencyInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.ContactInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.agency.tabs.LicenseInfoTab;
import com.exigen.ren.admin.modules.agencyvendor.common.metadata.AgencyVendorSearchMetaData;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import joptsimple.internal.Strings;
import org.testng.annotations.Test;

import java.util.*;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.agencyvendor.agency.AgencyContext.*;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAppointmentDocumentGenerationForAgency extends ValidationXMLBaseTest {

    private final String E_FOLDER_PATH = "Appointment Docs/Appointment Confirmation Letter";
    private final String PARTIAL_DOC_NAME = "AppointmentLtr";
    private final String FILE_NAME_TEMPLATE = "AppointmentLtr-%1$s-%2$s-%3$s-\\d{2}-\\d{2}-\\d{2}-\\d{2,3}.pdf";

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-30030"}, component = POLICY_GROUPBENEFITS)
    public void testAppointmentDocumentGenerationForNewAgency() {
        LOGGER.info("REN-30030 Precondition");
        adminApp().open();
        String agencyName = agency.createAgency(tdSpecific().getTestData("TestData_Static"));
        JobRunner.executeJob(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB);
        adminApp().reopen();
        searchAgencyAndOpenByName(agencyName);
        String creationDate = DateTimeUtils.getCurrentDateTime().format(DateTimeUtilsHelper.YYYY_MM_DD);
        String agencyCode = tdSpecific().getTestData("TestData_Static").getValue(AgencyInfoTab.class.getSimpleName(), AgencyInfoMetaData.AGENCY_CODE.getLabel());

        LOGGER.info("REN-30030 STEP#1");
        CustomAssertions.assertThat(Efolder.getFileName(E_FOLDER_PATH, PARTIAL_DOC_NAME)).matches(String.format(FILE_NAME_TEMPLATE, agencyName, agencyCode, creationDate));

        LOGGER.info("REN-30030 STEP#2");
        XmlValidator xmlValidator = DBHelper.getDocument(tdSpecific().getTestData("TestData_Static")
                .getValue(AgencyInfoTab.class.getSimpleName(), AgencyInfoMetaData.AGENCY_CODE.getLabel()), DBHelper.EntityType.AGENCY);

        CustomAssertions.assertThat(xmlValidator.getNodeValue(DocGenEnum.AllSections.GENERATION_DATE))
                .matches(String.format("%sT\\d{2}:\\d{2}:\\d{2}.\\d{2,3}Z", creationDate));

        Map<DocGenEnum.AllSections, String> appointmentLtrMap = new HashMap<>();
        appointmentLtrMap.put(FIRST_NAME, tdSpecific().getTestData("TestData_Static")
                .getValue(ContactInfoTab.class.getSimpleName(), ContactInfoMetaData.ADD_CONTACT.getLabel(), ContactInfoMetaData.AddContactMetaData.FIRST_NAME.getLabel()));
        appointmentLtrMap.put(LAST_NAME, tdSpecific().getTestData("TestData_Static")
                .getValue(ContactInfoTab.class.getSimpleName(), ContactInfoMetaData.ADD_CONTACT.getLabel(), ContactInfoMetaData.AddContactMetaData.LAST_NAME.getLabel()));
        appointmentLtrMap.put(LETTER_ADDRESS1, tdSpecific().getTestData("TestData_Static")
                .getValue(ContactInfoTab.class.getSimpleName(), ContactInfoMetaData.ADDRESS_LINE_1.getLabel()));
        appointmentLtrMap.put(LETTER_ADDRESS2, Strings.EMPTY);
        appointmentLtrMap.put(LETTER_ADDRESS3, Strings.EMPTY);
        appointmentLtrMap.put(LETTER_CITY, tdSpecific().getTestData("TestData_Static")
                .getValue(ContactInfoTab.class.getSimpleName(), ContactInfoMetaData.CITY.getLabel()));
        appointmentLtrMap.put(LETTER_STATE, tdSpecific().getTestData("TestData_Static")
                .getValue(ContactInfoTab.class.getSimpleName(), ContactInfoMetaData.STATE_PROVINCE.getLabel()));
        appointmentLtrMap.put(LETTER_ZIP_CODE, tdSpecific().getTestData("TestData_Static")
                .getValue(ContactInfoTab.class.getSimpleName(), ContactInfoMetaData.ZIP_POSTAL_CODE.getLabel()));
        appointmentLtrMap.put(STATE_IN_LICENSE, tdSpecific().getTestData("TestData_Static")
                .getTestDataList(LicenseInfoTab.class.getSimpleName()).get(0).getValue(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel()));
        appointmentLtrMap.put(APPOINTMENT_LETTER_STATE_LIST, tdSpecific().getTestData("TestData_Static")
                .getTestDataList(LicenseInfoTab.class.getSimpleName()).get(0).getValue(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel()));
        xmlValidator.checkDocument(appointmentLtrMap);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-30041"}, component = POLICY_GROUPBENEFITS)
    public void testAppointmentDocumentGenerationForMultiAgency() {
        LOGGER.info("REN-30041 Precondition");
        adminApp().open();
        List<TestData> tdAgencies = new ArrayList<>(Arrays.asList(
                tdSpecific().getTestData("License")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel()), "NY"),
                tdSpecific().getTestData("License")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel()), "NJ"),
                tdSpecific().getTestData("License")
                        .adjust(TestData.makeKeyPath(LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel()), "GA")));
        agency.createAgency(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(ContactInfoTab.class.getSimpleName(), ContactInfoMetaData.IS_MAILING_ADDRESS_THE_SAME_AS_PHYSICAL_ADDRESS.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(LicenseInfoTab.class.getSimpleName() + "[0]", LicenseTabMetaData.ADD_LICENSE.getLabel(), AddLicenseMetaData.STATE_PROVINCE.getLabel()), "LA"));
        String agencyName2 = agency.createAgency(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(ContactInfoTab.class.getSimpleName(), tdSpecific().getTestData("ContactInfoTab_WithMailingAddress"))
                .adjust(TestData.makeKeyPath(ContactInfoTab.class.getSimpleName(), ContactInfoMetaData.IS_MAILING_ADDRESS_THE_SAME_AS_PHYSICAL_ADDRESS.getLabel()), VALUE_NO)
                .adjust(LicenseInfoTab.class.getSimpleName(), tdAgencies));

        agency.createAgency(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .mask(LicenseInfoTab.class.getSimpleName()));

        JobRunner.executeJob(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB, true);
        adminApp().reopen();
        agency.navigate();
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName2));
        agency.update().perform(tdSpecific().getTestData("TestData_UpdateLicense"), 1);
        JobRunner.executeJob(GeneralSchedulerPage.DOC_GEN_APP_LETTER_JOB, true);
        adminApp().reopen();
        searchAgencyAndOpenByName(agencyName2);
        String agencyCode = agencyInfoTab.getAssetList().getAsset(AgencyInfoMetaData.AGENCY_CODE).getValue();
        String creationDate = DateTimeUtils.getCurrentDateTime().format(DateTimeUtilsHelper.YYYY_MM_DD);
        LOGGER.info("REN-30041 STEP#1");
        assertSoftly(softly -> {
            softly.assertThat(Efolder.getFileName(E_FOLDER_PATH, PARTIAL_DOC_NAME, 1)).matches(String.format(FILE_NAME_TEMPLATE, agencyName2, agencyCode, creationDate));
            Efolder.expandFolder("Appointment Confirmation Letter");
            Efolder.expandFolder("Appointment Docs");
            Efolder.linkCloseEFolder.click();
            softly.assertThat(Efolder.getFileName(E_FOLDER_PATH, PARTIAL_DOC_NAME, 2)).matches(String.format(FILE_NAME_TEMPLATE, agencyName2, agencyCode, creationDate));
        });
    }

    private void searchAgencyAndOpenByName(String agencyName) {
        agency.navigate();
        agency.search(agencyVendorSearchTab.getSearchTestData(AgencyVendorSearchMetaData.AGENCY_NAME.getLabel(), agencyName));
        agencyVendorSearchTab.openFirst();
    }
}
