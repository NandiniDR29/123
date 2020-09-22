package com.exigen.ren.modules.docgen.claim.gb_dn;

import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.common.enums.DocGenEnum;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_dn.DentalClaimContext;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.ProviderColumns;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.COVERED_CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SearchProviderMetaData.LICENSE_STATE_PROVINCE;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.InsuredPrincipalInformation.DATE_OF_BIRTH;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.InsuredPrincipalInformation.NAME;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;


public class TestClaimDentalInformationRequestLetter extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext, DentalClaimContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33850", component = CLAIMS_GROUPBENEFITS)
    public void testClaimDentalInformationRequestLetterWithCoveredCdtCode() {
        LOGGER.info("REN-33850 Precondition");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "NY")
                .adjust(makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.COUNTY_CODE.getLabel()), "index=1"));
        createDefaultGroupDentalCertificatePolicy();
        String insuredName = PolicySummaryPage.tableInsuredPrincipalInformation.getRow(1).getCell(NAME.getName()).getValue();
        String insuredDoB = PolicySummaryPage.tableInsuredPrincipalInformation.getRow(1).getCell(DATE_OF_BIRTH.getName()).getValue();

        dentalClaim.create(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        dentalClaim.claimSubmit().perform();
        dentalClaim.claimAdjust().perform();
        dentalClaim.lineOverride().perform(tdSpecific().getTestData("TestData_LineOverride"), 1);
        dentalClaim.claimSubmit().perform();
        dentalClaim.claimAdjust().perform();
        dentalClaim.generateDocument().perform(new SimpleDataProvider(), 1);

        LOGGER.info("REN-33850 Step#1");
        CustomAssertions.assertThat(Efolder.getFileName("Outbound Correspondence/Information Request", "InformationRequest"))
               .matches(String.format("InformationRequest-%1$s-%2$s-%3$s-\\d{2}-\\d{2}-\\d{2}-\\d{1,3}.pdf", claimNumber, insuredName.split(" ")[1],
                     DateTimeUtils.getCurrentDateTime().format(YYYY_MM_DD)));

        LOGGER.info("REN-33850 Step#2");
        NavigationPage.toMainTab(CUSTOMER.get());
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        MainPage.QuickSearch.search(claimNumber);
        XmlValidator xmlValidator = DBHelper.getDocument(claimNumber, DBHelper.EntityType.CLAIM);
        xmlValidator.checkDocument(PATIENT_NAME, insuredName);
        xmlValidator.checkDocument(PATIENT_DOB, String.format("%sT00:00:00Z", LocalDate.parse(insuredDoB, DateTimeFormatter.ofPattern("MM/dd/yyyy")).format(DateTimeUtilsHelper.YYYY_MM_DD)));
        xmlValidator.checkDocument(RELATIONSHIP_TO_INSURED, "Subscriber");
        xmlValidator.checkDocument(INSURED_FIRST_NAME, insuredName.split(" ")[0]);
        xmlValidator.checkDocument(INSURED_LAST_NAME, insuredName.split(" ")[1]);
        xmlValidator.checkDocument(DocGenEnum.AllSections.PROVIDER_NAME,
                ClaimSummaryPage.tableProvider.getRow(1).getCell(ProviderColumns.PROVIDER_NAME.getName()).getValue());
        xmlValidator.checkDocument(PROVIDER_LICENSE, ClaimSummaryPage.tableProvider.getRow(1).getCell(ProviderColumns.LICENSE.getName()).getValue());
        xmlValidator.checkDocument(PROVIDER_LICENSE_STATE, tdSpecific().getValue(intakeInformationTab.getMetaKey(), IntakeInformationTabMetaData.SEARCH_PROVIDER.getLabel(), LICENSE_STATE_PROVINCE.getLabel()));
        xmlValidator.checkDocument(PROVIDER_LICENSE_NPI, ClaimSummaryPage.tableProvider.getRow(1).getCell(ProviderColumns.NPI.getName()).getValue());
        CustomAssertions.assertThat(xmlValidator.getNodeValue(DOCUMENT_GENERATED_DATE.get())
                .matches(String.format("%sT\\d{2}:\\d{2}:\\d{2}.\\d{1,3}Z", DateTimeUtils.getCurrentDateTime().format(YYYY_MM_DD))));
        xmlValidator.checkDocument(DocGenEnum.AllSections.CUSTOMER_NUMBER, customerNumber);
        xmlValidator.checkDocument(DocGenEnum.AllSections.CLAIM_NUMBER, claimNumber);

        LOGGER.info("REN-33850 Step#3");
        ImmutableMap<String, String> requestInformationSectionMap1 = new ImmutableMap.Builder<String, String>()
                .put(LINE_NUMBER.get(), ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns.LINE_ID.getName()).getValue())
                .put(INDICATOR_SR.get(), "S")
                .put(TOOTH_AREA.get(), ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns.TOOTH.getName()).getValue())
                .put(SURFACE.get(), ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns.SURFACE.getName()).getValue())
                .put(CDT_DESC.get(), ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns.CDT_CODE.getName()).getValue())
                .put(DATE_OF_SERVICE.get(), String.format("%sT00:00:00Z", DateTimeUtils.getCurrentDateTime().format(YYYY_MM_DD)))
                .put(CHARGE_AMT.get(), ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns
                        .CHARGE.getName()).getValue().substring(1))
                .put(IR_NUMBERS.get(), "")
                .build();

        getRequestInformationSection(xmlValidator, REQUEST_INFORMATION_LIST, "1").checkDocument(requestInformationSectionMap1);

        LOGGER.info("REN-33850 Step#4");
        ImmutableMap<String, String> requestInformationSectionMap2 = new ImmutableMap.Builder<String, String>()
                .put(LINE_NUMBER.get(), ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns.LINE_ID.getName()).getValue())
                .put(INDICATOR_SR.get(), "R")
                .put(TOOTH_AREA.get(), ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns.TOOTH.getName()).getValue())
                .put(SURFACE.get(), ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns.SURFACE.getName()).getValue())
                .put(COVERED_CDT_DESC.get(), ClaimSummaryPage.tableResultsOfAdjudication.getRow(1).getCell(COVERED_CDT_CODE.getName()).getValue())
                .put(DATE_OF_SERVICE.get(), String.format("%sT00:00:00Z", DateTimeUtils.getCurrentDateTime().format(YYYY_MM_DD)))
                .put(CHARGE_AMT.get(), ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns
                        .CHARGE.getName()).getValue().substring(1))
                .put(IR_NUMBERS.get(), "IR99999")
                .build();
        getRequestInformationSection(xmlValidator, REQUEST_INFORMATION_LIST, "2").checkDocument(requestInformationSectionMap2);
    }

    private XmlValidator getRequestInformationSection(XmlValidator xmlValidator, DocGenEnum.AllSections section, String sectionNumber) {
        String xmlDocument =
                xmlValidator.convertNodeToString(xmlValidator.findNode(String.format(section.get(), sectionNumber)));

        CustomAssertions.assertThat(xmlDocument).as("Section 'requestInformationList' not found by item '" + sectionNumber + "'").isNotEmpty();
        return new XmlValidator(xmlDocument);
    }
}
