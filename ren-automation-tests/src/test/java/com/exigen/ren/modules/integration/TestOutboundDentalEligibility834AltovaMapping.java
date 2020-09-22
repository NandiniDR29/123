package com.exigen.ren.modules.integration;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.Job;
import com.exigen.ipb.eisa.utils.batchjob.JobGroup;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.file.FileHelper;
import com.exigen.ren.helpers.file.X12FileHelper;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.TestDataKey.ISSUE;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYYMMDD;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestOutboundDentalEligibility834AltovaMapping extends ClaimGroupBenefitsDNBaseTest {

    private static final String OUTPUT_FOLDER = "/home/eis/ren/shared/dentaleligibilityextract/outbound/";
    private static final String FILE_NAME = "P834DentalEligibilityRENTesiaDenEligibility%s.txt";

    @Test(groups = {INTEGRATION, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36538", component = INTEGRATION)
    public void testOutboundDentalEligibility834AltovaMapping() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();

        mainApp().open();
        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        groupDentalCertificatePolicy.createPolicyViaUI(tdSpecific().getTestData("TestData_Certificate1")
                .adjust(groupDentalCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
        String certificateNumber1 = PolicySummaryPage.labelPolicyNumber.getValue();
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumberCert1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        MainPage.QuickSearch.search(policyNumber);

        groupDentalCertificatePolicy.createPolicyViaUI(tdSpecific().getTestData("TestData_Certificate2")
                .adjust(groupDentalCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
        String certificateNumber2 = PolicySummaryPage.labelPolicyNumber.getValue();
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumberCert2 = CustomerSummaryPage.labelCustomerNumber.getValue();

        mainApp().close();
        JobRunner.executeJob(new JobGroup(GeneralSchedulerPage.OUTBOUND_DENTAL_ELIGIBILITY_JOB.getGroupName(), new Job(GeneralSchedulerPage.OUTBOUND_DENTAL_ELIGIBILITY_JOB.getGroupName())
                .setJobParameters(ImmutableMap.of("JOB_UI_PARAMS", "Tesia"))));

        File file = new File(FileHelper.downloadFile(OUTPUT_FOLDER, String.format(FILE_NAME, currentDate.format(YYYYMMDD))));

        String[] fileContent = FileHelper.getFileContent(file).split("\n");

        List<String[]> certList1 = X12FileHelper.getListOfCertificatesLinesFrom834File(file, certificateNumber1);
        String[] employeeOnlyPrimaryInsured = certList1.get(0);

        List<String[]> certList2 = X12FileHelper.getListOfCertificatesLinesFrom834File(file, certificateNumber2);
        String[] employeeFamilyPrimaryInsured = certList2.get(0);
        String[] employeeFamilyChildren = certList2.get(1);
        String[] employeeFamilySpouse = certList2.get(2);

        assertSoftly(softly -> {
            String isa13Value = fileContent[0].substring(90, 99);

            // ISA segment
            softly.assertThat(fileContent[0]).matches(String.format("ISA\\*00\\*          \\*00\\*\\          \\*ZZ\\*470397286      \\*ZZ\\*381791480      \\*%s\\*\\d{4}\\*\\^\\*00501\\*\\d{9}\\*1\\*P\\*:~",
                    currentDate.format(DateTimeFormatter.ofPattern("yyMMdd"))));
            // GS segment
            softly.assertThat(fileContent[1]).matches(String.format("GS\\*BE\\*470397286\\*381791480\\*%s\\*\\d{4,8}\\*%s\\*X\\*005010X220A1~",
                    currentDate.format(YYYYMMDD), isa13Value.replaceFirst("^0+(?!$)", "")));
            // ST segment
            softly.assertThat(fileContent[2]).isEqualTo("ST*834*0001*005010X220A1~");
            // BGN segment
            softly.assertThat(fileContent[3]).matches(String.format("BGN\\*00\\*0001\\*%s\\*\\d{4,8}\\*\\*\\*\\*4~", currentDate.format(YYYYMMDD)));
            // DTP segment
            softly.assertThat(fileContent[4]).matches(String.format("DTP\\*007\\*D8\\*%s~", currentDate.format(YYYYMMDD)));
            // N1 Sponsor Name segment
            softly.assertThat(fileContent[5]).isEqualTo("N1*P5*Renaissance Life & Health Insurance Company*FI*470397286~");
            // N1 Payer segment
            softly.assertThat(fileContent[6]).isEqualTo("N1*IN*Renaissance Life & Health Insurance Company*FI*470397286~");

            //  Employee Only = Primary insured  (first certificate)
            softly.assertThat(employeeOnlyPrimaryInsured[0]).isEqualTo("INS*Y*18*030*XN*A***TE~");
            softly.assertThat(employeeOnlyPrimaryInsured[1]).isEqualTo(String.format("REF*0F*%s~", getStoredValue("SSN_REN-36538_Cert1")));
            softly.assertThat(employeeOnlyPrimaryInsured[2]).isEqualTo(String.format("REF*1L*%s~", certificateNumber1));
            softly.assertThat(employeeOnlyPrimaryInsured[3]).isEqualTo(String.format("REF*ZZ*%s~", customerNumberCert1));
            softly.assertThat(employeeOnlyPrimaryInsured[4]).isEqualTo(String.format("DTP*336*D8*%s~", currentDate.minusYears(2).format(YYYYMMDD)));  //todo currentDate.minusYears(2) ???
            softly.assertThat(employeeOnlyPrimaryInsured[5]).isEqualTo(String.format("NM1*IL*1*%s*%s****34*%s~", getStoredValue("LN_REN-36538_Cert1"), getStoredValue("FN_REN-36538_Cert1"), getStoredValue("SSN_REN-36538_Cert1")));
            softly.assertThat(employeeOnlyPrimaryInsured[6]).isEqualTo(String.format("N3*%s~", getStoredValue("Address_REN-36538_Cert1")));
            softly.assertThat(employeeOnlyPrimaryInsured[7]).isEqualTo("N4*Walnut Creek*CA*94596~");
            softly.assertThat(employeeOnlyPrimaryInsured[8]).isEqualTo("DMG*D8*19990609*F*I~");
            softly.assertThat(employeeOnlyPrimaryInsured[9]).isEqualTo("LUI*LD*ENG*English*7~");
            softly.assertThat(employeeOnlyPrimaryInsured[10]).isEqualTo("HD*030**DEN*GB_DN.DENTAL.ALACARTE.Employment*EMP~");
            softly.assertThat(employeeOnlyPrimaryInsured[11]).isEqualTo(String.format("DTP*348*D8*%s~", currentDate.with(TemporalAdjusters.firstDayOfMonth()).format(YYYYMMDD)));  //todo firstDayOfMonth ???
            softly.assertThat(employeeOnlyPrimaryInsured[12]).isEqualTo(String.format("REF*ZZ*%s~", certificateNumber1));

            //  Employee + Family Participant = Primary insured (second certificate)
            softly.assertThat(employeeFamilyPrimaryInsured[0]).isEqualTo("INS*Y*18*030*XN*A***TE~");
            softly.assertThat(employeeFamilyPrimaryInsured[1]).isEqualTo(String.format("REF*0F*%s~", getStoredValue("SSN_REN-36538_Cert2")));
            softly.assertThat(employeeFamilyPrimaryInsured[2]).isEqualTo(String.format("REF*1L*%s~", certificateNumber2));
            softly.assertThat(employeeFamilyPrimaryInsured[3]).isEqualTo(String.format("REF*ZZ*%s~", customerNumberCert2));
            softly.assertThat(employeeFamilyPrimaryInsured[4]).isEqualTo(String.format("DTP*336*D8*%s~", currentDate.minusYears(2).format(YYYYMMDD)));  //todo currentDate.minusYears(2) ???
            softly.assertThat(employeeFamilyPrimaryInsured[5]).isEqualTo(String.format("NM1*IL*1*%s*%s****34*%s~", getStoredValue("LN_REN-36538_Cert2"), getStoredValue("FN_REN-36538_Cert2"), getStoredValue("SSN_REN-36538_Cert2")));
            softly.assertThat(employeeFamilyPrimaryInsured[6]).isEqualTo(String.format("N3*%s~", getStoredValue("Address_REN-36538_Cert2")));
            softly.assertThat(employeeFamilyPrimaryInsured[7]).isEqualTo("N4*Walnut Creek*CA*94596~");
            softly.assertThat(employeeFamilyPrimaryInsured[8]).isEqualTo("DMG*D8*19990809*M*B~");
            softly.assertThat(employeeFamilyPrimaryInsured[9]).isEqualTo("LUI*LD*ENG*English*7~");
            softly.assertThat(employeeFamilyPrimaryInsured[10]).isEqualTo("HD*030**DEN*GB_DN.DENTAL.ALACARTE.Employment*FAM~");
            softly.assertThat(employeeFamilyPrimaryInsured[11]).isEqualTo(String.format("DTP*348*D8*%s~", currentDate.with(TemporalAdjusters.firstDayOfMonth()).format(YYYYMMDD)));  //todo firstDayOfMonth ???
            softly.assertThat(employeeFamilyPrimaryInsured[12]).isEqualTo(String.format("REF*ZZ*%s~", certificateNumber2));

            // Employee + Family Participant = Children (second certificate)
            softly.assertThat(employeeFamilyChildren[0]).isEqualTo("INS*N*19*030*XN*A***TE~");
            softly.assertThat(employeeFamilyChildren[1]).isEqualTo(String.format("REF*0F*%s~", getStoredValue("SSN_REN-36538_Cert2")));
            softly.assertThat(employeeFamilyChildren[2]).isEqualTo(String.format("REF*1L*%s~", certificateNumber2));
            softly.assertThat(employeeFamilyChildren[3]).isEqualTo(String.format("REF*ZZ*%s~", customerNumberCert2));
            softly.assertThat(employeeFamilyChildren[4]).isEqualTo(String.format("DTP*336*D8*%s~", currentDate.minusYears(2).format(YYYYMMDD)));  //todo currentDate.minusYears(2) ???
            softly.assertThat(employeeFamilyChildren[5]).isEqualTo(String.format("NM1*IL*1*%s*%s~", getStoredValue("LN_Child_REN-36538_Cert2"), getStoredValue("FN_Child_REN-36538_Cert2"), getStoredValue("SSN_REN-36538_Cert2")));
            softly.assertThat(employeeFamilyChildren[6]).isEqualTo(String.format("N3*%s~", getStoredValue("Address_REN-36538_Cert2")));
            softly.assertThat(employeeFamilyChildren[7]).isEqualTo("N4*Walnut Creek*CA*94596~");
            softly.assertThat(employeeFamilyChildren[8]).isEqualTo("DMG*D8*19990505*U~");
            softly.assertThat(employeeFamilyChildren[9]).isEqualTo("LUI*LD*ENG*English*7~");
            softly.assertThat(employeeFamilyChildren[10]).isEqualTo("HD*030**DEN*GB_DN.DENTAL.ALACARTE.Employment*FAM~");
            softly.assertThat(employeeFamilyChildren[11]).isEqualTo(String.format("DTP*348*D8*%s~", currentDate.with(TemporalAdjusters.firstDayOfMonth()).format(YYYYMMDD)));
            softly.assertThat(employeeFamilyChildren[12]).isEqualTo(String.format("REF*ZZ*%s~", certificateNumber2));

            // Employee + Family Participant = Spouse (second certificate)
            softly.assertThat(employeeFamilySpouse[0]).isEqualTo("INS*N*01*030*XN*A***TE~");
            softly.assertThat(employeeFamilySpouse[1]).isEqualTo(String.format("REF*0F*%s~", getStoredValue("SSN_REN-36538_Cert2")));
            softly.assertThat(employeeFamilySpouse[2]).isEqualTo(String.format("REF*1L*%s~", certificateNumber2));
            softly.assertThat(employeeFamilySpouse[3]).isEqualTo(String.format("REF*ZZ*%s~", customerNumberCert2));
            softly.assertThat(employeeFamilySpouse[4]).isEqualTo(String.format("DTP*336*D8*%s~", currentDate.minusYears(2).format(YYYYMMDD)));
            softly.assertThat(employeeFamilySpouse[5]).isEqualTo(String.format("NM1*IL*1*%s*%s~", getStoredValue("LN_Spouse_REN-36538_Cert2"), getStoredValue("FN_Spouse_REN-36538_Cert2"), getStoredValue("SSN_REN-36538_Cert2")));
            softly.assertThat(employeeFamilySpouse[6]).isEqualTo(String.format("N3*%s~", getStoredValue("Address_REN-36538_Cert2")));
            softly.assertThat(employeeFamilySpouse[7]).isEqualTo("N4*Walnut Creek*CA*94596~");
            softly.assertThat(employeeFamilySpouse[8]).isEqualTo("DMG*D8*19980505*U~");
            softly.assertThat(employeeFamilySpouse[9]).isEqualTo("LUI*LD*ENG*English*7~");
            softly.assertThat(employeeFamilySpouse[10]).isEqualTo("HD*030**DEN*GB_DN.DENTAL.ALACARTE.Employment*FAM~");
            softly.assertThat(employeeFamilySpouse[11]).isEqualTo(String.format("DTP*348*D8*%s~", currentDate.with(TemporalAdjusters.firstDayOfMonth()).format(YYYYMMDD)));
            softly.assertThat(employeeFamilySpouse[12]).isEqualTo(String.format("REF*ZZ*%s~", certificateNumber2));

            // SE segment
            softly.assertThat(fileContent[fileContent.length - 3]).matches(String.format("SE\\*%s\\*0001~", fileContent.length - 4));  // Count the number of rows, do not include the control 4 segments (ISA, GS, GE, IEA) == length-4
            // GE segment
            softly.assertThat(fileContent[fileContent.length - 2]).matches(String.format("GE\\*1\\*%s~", isa13Value.replaceFirst("^0+(?!$)", "")));  // Same as ISA13 but remove prevailing 0's. Ex: If ISA 13 is '000000055' Then the value for this field is '55'
            // IEA segment
            softly.assertThat(fileContent[fileContent.length - 1]).matches(String.format("IEA\\*1\\*%s~", isa13Value)); // Same as ISA13
        });
    }
}