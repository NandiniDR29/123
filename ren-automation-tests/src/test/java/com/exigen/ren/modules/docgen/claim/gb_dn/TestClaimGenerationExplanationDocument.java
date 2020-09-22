package com.exigen.ren.modules.docgen.claim.gb_dn;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_dn.DentalClaimContext;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.LineOverrideTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.LineOverrideTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderDentalClaim.OUTBOUND_CORRESPONDENCE;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderDentalClaimOutCorresp.PREDETERMINATIONS;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderIndCustDentalClaims.EOBS;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonDentalClaim.EOB;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.common.module.efolder.Efolder.expandFolder;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.*;
import static com.exigen.ren.main.enums.TableConstants.DentalClaims.BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.PatientMetaData.DATE_OF_BIRTH;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.DOS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableProvider;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableResultsOfAdjudication;
import static com.exigen.ren.utils.DBHelper.EntityType.CLAIM;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimGenerationExplanationDocument extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext,
        GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext, DentalClaimContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32086", component = CLAIMS_GROUPBENEFITS)
    public void testClaimGenerationExplanationDocument() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        final String SERVICE_DESC = "Inlay-Metallic - one surface";
        mainApp().open();

        customerNonIndividual.createViaUI(getDefaultCustomerNonIndividualTestData()
                .adjust(tdSpecific().getTestData("TestData_Customer").resolveLinks()));

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY")
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()), "index=1"));
        createDefaultGroupDentalCertificatePolicy();
        String customerFullName = PolicySummaryPage.labelCustomerName.getValue();
        String firstName = customerFullName.substring(0, 12);
        String lastName = customerFullName.substring(13);

        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()));
        toSubTab(EDIT_CLAIM);
        String dateOfBirth = LocalDate.parse(intakeInformationTab.getAssetList().getAsset(PATIENT).getAsset(DATE_OF_BIRTH).getValue(), MM_DD_YYYY).format(YYYY_MM_DD);
        IntakeInformationTab.buttonSubmitClaim.click();

        String claimNumber = ClaimSummaryPage.getClaimNumber();
        String providerName = tableProvider.getRow(1).getCell(IntakeInformationTab.ProviderColumns.PROVIDER_NAME.getName()).getValue();
        String practiceName = tableProvider.getRow(1).getCell(IntakeInformationTab.ProviderColumns.PRACTICE_NAME.getName()).getValue();
        String license = tableProvider.getRow(1).getCell(IntakeInformationTab.ProviderColumns.LICENSE.getName()).getValue();
        String charge = new Currency(ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns.CHARGE.getName()).getValue()).toPlainString();
        String benefitAmount = new Currency(tableResultsOfAdjudication.getRow(1).getCell(BENEFIT_AMOUNT.getName()).getValue()).toPlainString();
        String patientResponsibility = new Currency(tableResultsOfAdjudication.getRow(1).getCell(PATIENT_RESPONSIBILITY.getName()).getValue()).toPlainString();

        tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_VIEW).click();
        String coinsurance = new StaticElement(LineOverrideTabMetaData.ResultsOfAdjudicationSection.COINSURANCE.getLocator()).getValue();
        LineOverrideTab.buttonCancel.click();

        dentalClaim.issuePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        String documentDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        LOGGER.info("TEST REN-32086: Step 1");
        expandFolder(OUTBOUND_CORRESPONDENCE.getName());
        assertSoftly(softly -> {
            softly.assertThat(Efolder.isDocumentExistStartsContains(EOBS.getName(), EOB.getName() + "-", String.format("%s-Patient-Billing Statement-%s-%s-%s", EOB, claimNumber, lastName, documentDate)))
                    .withFailMessage("Generated document is absent in E-Folder").isTrue();
            softly.assertThat(Efolder.isDocumentExistStartsContains(EOBS.getName(), EOB.getName() + "-", String.format("%s-Provider-Billing Statement-%s-%s-%s", EOB, claimNumber, lastName, documentDate)))
                    .withFailMessage("Generated document is absent in E-Folder").isTrue();
        });

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-32086: Step 2");
        XmlValidator xmlValidator1 = DBHelper.getDocumentByNumber(claimNumber, CLAIM, 1);

        //1
        xmlValidator1.checkDocument(BUSINESS_UNIT_NAME, "New York");
        //2
        xmlValidator1.checkDocument(PATIENT_FIRST_NAME, firstName);
        xmlValidator1.checkDocument(PATIENT_LAST_NAME, lastName);
        //3
        assertThat(xmlValidator1.getNodeValue(PATIENT_DOB)).startsWith(dateOfBirth);
        //4
        xmlValidator1.checkDocument(RELATIONSHIP, "Subscriber");
        //5
        xmlValidator1.checkDocument(SUBSCRIBER_FIRST_NAME, firstName);
        xmlValidator1.checkDocument(SUBSCRIBER_LAST_NAME, lastName);
        //6
        xmlValidator1.checkDocument(PRACTICE_NAME, practiceName);
        xmlValidator1.checkDocument(PROVIDER_NAME, providerName);
        //7
        xmlValidator1.checkDocument(PROVIDER_LICENSE_NUMBER, license);
        xmlValidator1.checkDocument(PROVIDER_STATE_CD, "NY");
        //8
        xmlValidator1.checkDocument(PAYMENT_ISSUE_DATE, currentDate.format(MM_DD_YYYY));
        //9
        xmlValidator1.checkDocument(RECEIPT_DATE, currentDate.format(MM_DD_YYYY));
        //10
        xmlValidator1.checkDocument(CLAIM_NUMBER, claimNumber);
        //11
        xmlValidator1.checkDocument(TREATMENT_RECORDS_TOOTH_AREA, "S");
        //12
        assertThat(xmlValidator1.getNodeValue(TREATMENT_RECORDS_DATE_OF_SERVICE)).startsWith(currentDate.format(YYYY_MM_DD));
        //13
        xmlValidator1.checkDocument(TREATMENT_RECORDS_SUBMITTED_SERVICE_DESC, SERVICE_DESC);
        //13a
        xmlValidator1.checkDocument(TREATMENT_RECORDS_COVERED_SERVICE_DESC, SERVICE_DESC);
        //14
        xmlValidator1.checkDocument(TREATMENT_RECORDS_CHARGE, charge);
        //19
        xmlValidator1.checkDocument(TREATMENT_RECORDS_COINSURANCE_PERCENT_STR, coinsurance);
        //20
        xmlValidator1.checkDocument(TREATMENT_RECORDS_PAYMENT_AMT, benefitAmount);
        //21
        xmlValidator1.checkDocument(TREATMENT_RECORDS_PATIENT_PAYMENT_AMT, patientResponsibility);
        //22
        xmlValidator1.checkDocument(TREATMENT_RECORDS_PAY_TO, "SERVICEPROVIDER");
        //23
        xmlValidator1.checkDocument(SUBMITTED_AMT_TOTAL, charge);
        //29
        xmlValidator1.checkDocument(PAYMENT_AMT_TOTAL, benefitAmount);
        //30
        xmlValidator1.checkDocument(PATIENT_PAYMENT_AMT_TOTAL, patientResponsibility);
        //33
        xmlValidator1.checkDocument(PATIENT_COPY_IND, "true");
        //34
        xmlValidator1.checkNodeNotPresent(FORM_NUMBER);
        //35
        assertThat(xmlValidator1.getNodeValue(GENERATION_DATE)).startsWith(currentDate.format(YYYY_MM_DD));
        //39
        xmlValidator1.checkNodeIsPresent(CHECK_NUMBER);

        LOGGER.info("TEST REN-32086: Step 3");
        XmlValidator xmlValidator2 = DBHelper.getDocumentByNumber(claimNumber, CLAIM, 0);

        //1
        xmlValidator2.checkDocument(BUSINESS_UNIT_NAME, "New York");
        //2
        xmlValidator2.checkDocument(PATIENT_FIRST_NAME, firstName);
        xmlValidator2.checkDocument(PATIENT_LAST_NAME, lastName);
        //3
        assertThat(xmlValidator2.getNodeValue(PATIENT_DOB)).startsWith(dateOfBirth);
        //4
        xmlValidator2.checkDocument(RELATIONSHIP, "Subscriber");
        //5
        xmlValidator2.checkDocument(SUBSCRIBER_FIRST_NAME, firstName);
        xmlValidator2.checkDocument(SUBSCRIBER_LAST_NAME, lastName);
        //6
        xmlValidator2.checkDocument(PRACTICE_NAME, practiceName);
        xmlValidator2.checkDocument(PROVIDER_NAME, providerName);
        //7
        xmlValidator2.checkDocument(PROVIDER_LICENSE_NUMBER, license);
        xmlValidator2.checkDocument(PROVIDER_STATE_CD, "NY");
        //8
        xmlValidator2.checkDocument(PAYMENT_ISSUE_DATE, currentDate.format(MM_DD_YYYY));
        //9
        xmlValidator2.checkDocument(RECEIPT_DATE, currentDate.format(MM_DD_YYYY));
        //10
        xmlValidator2.checkDocument(CLAIM_NUMBER, claimNumber);
        //11
        xmlValidator2.checkDocument(TREATMENT_RECORDS_TOOTH_AREA, "S");
        //12
        assertThat(xmlValidator2.getNodeValue(TREATMENT_RECORDS_DATE_OF_SERVICE)).startsWith(currentDate.format(YYYY_MM_DD));
        //13
        xmlValidator2.checkDocument(TREATMENT_RECORDS_SUBMITTED_CTD, "D2510");
        //13a
        xmlValidator2.checkDocument(TREATMENT_RECORDS_COVERED_CTD, "D2510");
        //14
        xmlValidator2.checkDocument(TREATMENT_RECORDS_CHARGE, charge);
        //19
        xmlValidator2.checkDocument(TREATMENT_RECORDS_COINSURANCE_PERCENT_STR, coinsurance);
        //20
        xmlValidator2.checkDocument(TREATMENT_RECORDS_PAYMENT_AMT, benefitAmount);
        //21
        xmlValidator2.checkDocument(TREATMENT_RECORDS_PATIENT_PAYMENT_AMT, patientResponsibility);
        //22
        xmlValidator2.checkDocument(TREATMENT_RECORDS_PAY_TO, "SERVICEPROVIDER");
        //23
        xmlValidator2.checkDocument(SUBMITTED_AMT_TOTAL, charge);
        //29
        xmlValidator2.checkDocument(PAYMENT_AMT_TOTAL, benefitAmount);
        //30
        xmlValidator2.checkDocument(PATIENT_PAYMENT_AMT_TOTAL, patientResponsibility);
        //33
        xmlValidator2.checkDocument(PATIENT_COPY_IND, "false");
        //34
        xmlValidator2.checkNodeNotPresent(FORM_NUMBER);
        //35
        assertThat(xmlValidator2.getNodeValue(GENERATION_DATE)).startsWith(currentDate.format(YYYY_MM_DD));
        //39
        xmlValidator2.checkNodeIsPresent(CHECK_NUMBER);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32307", component = CLAIMS_GROUPBENEFITS)
    public void testClaimGenerationExplanationEOBDocument() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        final String SERVICE_DESC = "Inlay-Metallic - one surface";
        mainApp().open();

        customerNonIndividual.createViaUI(getDefaultCustomerNonIndividualTestData()
                .adjust(tdSpecific().getTestData("TestData_Customer").resolveLinks()));

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY")
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()), "index=1"));
        createDefaultGroupDentalCertificatePolicy();
        String customerFullName = PolicySummaryPage.labelCustomerName.getValue();
        String firstName = customerFullName.substring(0, 12);
        String lastName = customerFullName.substring(13);

        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim2").resolveLinks()));

        toSubTab(EDIT_CLAIM);
        String dateOfBirth = LocalDate.parse(intakeInformationTab.getAssetList().getAsset(PATIENT).getAsset(DATE_OF_BIRTH).getValue(), MM_DD_YYYY).format(YYYY_MM_DD);
        IntakeInformationTab.buttonSubmitClaim.click();

        String claimNumber = ClaimSummaryPage.getClaimNumber();
        String providerName = tableProvider.getRow(1).getCell(IntakeInformationTab.ProviderColumns.PROVIDER_NAME.getName()).getValue();
        String practiceName = tableProvider.getRow(1).getCell(IntakeInformationTab.ProviderColumns.PRACTICE_NAME.getName()).getValue();
        String license = tableProvider.getRow(1).getCell(IntakeInformationTab.ProviderColumns.LICENSE.getName()).getValue();
        String charge = new Currency(ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns.CHARGE.getName()).getValue()).toPlainString();
        String benefitAmount = new Currency(tableResultsOfAdjudication.getRow(1).getCell(BENEFIT_AMOUNT.getName()).getValue()).toPlainString();
        String patientResponsibility = new Currency(tableResultsOfAdjudication.getRow(1).getCell(PATIENT_RESPONSIBILITY.getName()).getValue()).toPlainString();
        String documentDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        LOGGER.info("TEST REN-32307: Step 1");
        RetryService.run(predicate -> Efolder.isDocumentExistStartsContains(PREDETERMINATIONS.getName(), "PTE" + "-", String.format("PTE-Patient-Billing Statement-%s-%s-%s", claimNumber, lastName, documentDate))
                        &&
                        Efolder.isDocumentExistStartsContains(PREDETERMINATIONS.getName(), "PTE" + "-", String.format("PTE-Provider-Billing Statement-%s-%s-%s", claimNumber, lastName, documentDate)),
                () -> {
                    expandFolder(OUTBOUND_CORRESPONDENCE.getName());
                    return null;
                },
                StopStrategies.stopAfterAttempt(15),
                WaitStrategies.fixedWait(15, TimeUnit.SECONDS));

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-32307: Step 2");
        XmlValidator xmlValidator1 = DBHelper.getDocumentByNumber(claimNumber, CLAIM, 0);

        //1
        xmlValidator1.checkDocument(UNDERWRITING_COMPANY_CD, "RLHINY");
        //2
        xmlValidator1.checkDocument(PATIENT_FIRST_NAME, firstName);
        xmlValidator1.checkDocument(PATIENT_LAST_NAME, lastName);
        //3
        assertThat(xmlValidator1.getNodeValue(PATIENT_DOB)).startsWith(dateOfBirth);
        //4
        xmlValidator1.checkDocument(RELATIONSHIP, "Subscriber");
        //5
        xmlValidator1.checkDocument(SUBSCRIBER_FIRST_NAME, firstName);
        xmlValidator1.checkDocument(SUBSCRIBER_LAST_NAME, lastName);
        //6
        xmlValidator1.checkDocument(PRACTICE_NAME, practiceName);
        xmlValidator1.checkDocument(PROVIDER_NAME, providerName);
        //7
        xmlValidator1.checkDocument(PROVIDER_LICENSE_NUMBER, license);
        xmlValidator1.checkDocument(PROVIDER_STATE_CD, "NY");
        //8
        xmlValidator1.checkDocument(PAYMENT_ISSUE_DATE, currentDate.format(MM_DD_YYYY));
        //9
        xmlValidator1.checkDocument(RECEIPT_DATE, currentDate.format(MM_DD_YYYY));
        //10
        xmlValidator1.checkDocument(CLAIM_NUMBER, claimNumber);
        //11
        xmlValidator1.checkDocument(TREATMENT_RECORDS_TOOTH_AREA, "S");
        //12
        xmlValidator1.checkNodeNotPresent(TREATMENT_RECORDS_DATE_OF_SERVICE);
        //13
        xmlValidator1.checkDocument(TREATMENT_RECORDS_SUBMITTED_CTD, "D2510");
        //13a
        xmlValidator1.checkDocument(TREATMENT_RECORDS_COVERED_CTD, "D2510");
        //14
        xmlValidator1.checkDocument(TREATMENT_RECORDS_CHARGE, charge);
        //20
        xmlValidator1.checkDocument(TREATMENT_RECORDS_PAYMENT_AMT, benefitAmount);
        //21
        xmlValidator1.checkDocument(TREATMENT_RECORDS_PATIENT_PAYMENT_AMT, patientResponsibility);
        //22
        xmlValidator1.checkDocument(TREATMENT_RECORDS_PAY_TO, "SERVICEPROVIDER");
        //23
        xmlValidator1.checkDocument(SUBMITTED_AMT_TOTAL, charge);
        //29
        xmlValidator1.checkDocument(PAYMENT_AMT_TOTAL, benefitAmount);
        //30
        xmlValidator1.checkDocument(PATIENT_PAYMENT_AMT_TOTAL, patientResponsibility);
        //33
        xmlValidator1.checkDocument(PATIENT_COPY_IND, "true");
        //34
        xmlValidator1.checkNodeNotPresent(FORM_NUMBER);
        //35
        assertThat(xmlValidator1.getNodeValue(GENERATION_DATE)).startsWith(currentDate.format(YYYY_MM_DD));

        LOGGER.info("TEST REN-32086: Step 3");
        XmlValidator xmlValidator2 = DBHelper.getDocumentByNumber(claimNumber, CLAIM, 1);

        //1
        xmlValidator2.checkDocument(UNDERWRITING_COMPANY_CD, "RLHINY");
        //2
        xmlValidator2.checkDocument(PATIENT_FIRST_NAME, firstName);
        xmlValidator2.checkDocument(PATIENT_LAST_NAME, lastName);
        //3
        assertThat(xmlValidator2.getNodeValue(PATIENT_DOB)).startsWith(dateOfBirth);
        //4
        xmlValidator2.checkDocument(RELATIONSHIP, "Subscriber");
        //5
        xmlValidator2.checkDocument(SUBSCRIBER_FIRST_NAME, firstName);
        xmlValidator2.checkDocument(SUBSCRIBER_LAST_NAME, lastName);
        //6
        xmlValidator2.checkDocument(PRACTICE_NAME, practiceName);
        xmlValidator2.checkDocument(PROVIDER_NAME, providerName);
        //7
        xmlValidator2.checkDocument(PROVIDER_LICENSE_NUMBER, license);
        xmlValidator2.checkDocument(PROVIDER_STATE_CD, "NY");
        //8
        xmlValidator2.checkDocument(PAYMENT_ISSUE_DATE, currentDate.format(MM_DD_YYYY));
        //9
        xmlValidator2.checkDocument(RECEIPT_DATE, currentDate.format(MM_DD_YYYY));
        //10
        xmlValidator2.checkDocument(CLAIM_NUMBER, claimNumber);
        //11
        xmlValidator2.checkDocument(TREATMENT_RECORDS_TOOTH_AREA, "S");
        //12
        xmlValidator2.checkNodeNotPresent(TREATMENT_RECORDS_DATE_OF_SERVICE);
        //13
        xmlValidator2.checkDocument(TREATMENT_RECORDS_SUBMITTED_SERVICE_DESC, SERVICE_DESC);
        //13a
        xmlValidator2.checkDocument(TREATMENT_RECORDS_COVERED_SERVICE_DESC, SERVICE_DESC);
        //14
        xmlValidator2.checkDocument(TREATMENT_RECORDS_CHARGE, charge);
        //20
        xmlValidator2.checkDocument(TREATMENT_RECORDS_PAYMENT_AMT, benefitAmount);
        //21
        xmlValidator2.checkDocument(TREATMENT_RECORDS_PATIENT_PAYMENT_AMT, patientResponsibility);
        //22
        xmlValidator2.checkDocument(TREATMENT_RECORDS_PAY_TO, "SERVICEPROVIDER");
        //23
        xmlValidator2.checkDocument(SUBMITTED_AMT_TOTAL, charge);
        //29
        xmlValidator2.checkDocument(PAYMENT_AMT_TOTAL, benefitAmount);
        //30
        xmlValidator2.checkDocument(PATIENT_PAYMENT_AMT_TOTAL, patientResponsibility);
        //33
        xmlValidator2.checkDocument(PATIENT_COPY_IND, "false");
        //34
        xmlValidator2.checkNodeNotPresent(FORM_NUMBER);
        //35
        assertThat(xmlValidator2.getNodeValue(GENERATION_DATE)).startsWith(currentDate.format(YYYY_MM_DD));
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35028", component = CLAIMS_GROUPBENEFITS)
    public void testClaimGenerationPreTreatmentEstimateDocument() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        mainApp().open();

        customerNonIndividual.createViaUI(getDefaultCustomerNonIndividualTestData()
                .adjust(tdSpecific().getTestData("TestData_Customer").resolveLinks()));

        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY")
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()), "index=1"));
        createDefaultGroupDentalCertificatePolicy();
        String customerFullName = PolicySummaryPage.labelCustomerName.getValue();
        String lastName = customerFullName.substring(13);

        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY)
                .adjust(tdSpecific().getTestData("TestData_Claim")
                        .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), TYPE_OF_TRANSACTION.getLabel()), "Predetermination")
                        .adjust(makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), DOS.getLabel()), "").resolveLinks()));
        dentalClaim.claimSubmit().perform();

        String claimNumber = ClaimSummaryPage.getClaimNumber();
        String documentDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        DBService dbService = DBService.get();
        dbService.executeUpdate("UPDATE ClaimsFeature SET maximumApprovedFee = 10.00, dentistAdjustment = 20.00, allowedAmount = 30.00, payableDeductible = 40.00, " +
                "patientResponsibility = 50.00, copay = 60.00 WHERE claimNumber = ?", claimNumber);

        LOGGER.info("TEST REN-35028: Step 1");
        expandFolder(OUTBOUND_CORRESPONDENCE.getName());
        RetryService.run(predicate -> Efolder.isDocumentExistStartsContains(PREDETERMINATIONS.getName(), "PTE" + "-", String.format("PTE-Patient-Billing Statement-%s-%s-%s", claimNumber, lastName, documentDate))
                        &&
                        Efolder.isDocumentExistStartsContains(PREDETERMINATIONS.getName(), "PTE" + "-", String.format("PTE-Provider-Billing Statement-%s-%s-%s", claimNumber, lastName, documentDate)),
                () -> {
                    BrowserController.get().driver().navigate().refresh();
                    return null;
                },
                StopStrategies.stopAfterAttempt(5),
                WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        LOGGER.info("TEST: Get document from database in XML format");
        LOGGER.info("TEST REN-35028: Step 2");
        XmlValidator xmlValidator1 = DBHelper.getDocumentByNumber(claimNumber, CLAIM, 0);

        //8
        xmlValidator1.checkDocument(PAYMENT_ISSUE_DATE, currentDate.format(MM_DD_YYYY));
        //9
        xmlValidator1.checkDocument(RECEIPT_DATE, currentDate.format(MM_DD_YYYY));
        //15
        xmlValidator1.checkDocument(RECEIPT_DATE, currentDate.format(MM_DD_YYYY));

        LOGGER.info("TEST REN-35028: Step 3");
        XmlValidator xmlValidator2 = DBHelper.getDocumentByNumber(claimNumber, CLAIM, 1);

        //8
        xmlValidator2.checkDocument(PAYMENT_ISSUE_DATE, currentDate.format(MM_DD_YYYY));
        //9
        xmlValidator2.checkDocument(RECEIPT_DATE, currentDate.format(MM_DD_YYYY));
    }
}
