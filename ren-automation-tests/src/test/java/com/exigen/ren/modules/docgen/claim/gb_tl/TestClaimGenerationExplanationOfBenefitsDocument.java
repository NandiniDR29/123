package com.exigen.ren.modules.docgen.claim.gb_tl;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDeathBeneficiaryTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.BenefitsAccidentalDeathDeathCertificateTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsAccidentalDeathBeneficiaryTab;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsAccidentalDeathDeathCertificateTab;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.LossEventTab;
import com.exigen.ren.main.modules.claim.gb_tl.TermLifeClaimContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import com.exigen.ren.utils.DBHelper;
import com.exigen.ren.utils.XmlValidator;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.DocGenEnum.AllSections.*;
import static com.exigen.ren.common.enums.EfolderConstants.EFolderNonDentalClaim.EOB;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.ADD_BL;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.BTL_BL;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.DBHelper.EntityType.CLAIM;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimGenerationExplanationOfBenefitsDocument extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext, TermLifeClaimContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-31844", component = CLAIMS_GROUPBENEFITS)
    public void testClaimGenerationExplanationOfBenefitsDocument() {
        mainApp().open();

        TestData tdCustomer = getDefaultCustomerNonIndividualTestData().adjust(tdSpecific().getTestData("TestData_Customer1"));
        String customerFirstName = tdCustomer.getValue(relationshipTab.getMetaKey(), RelationshipTabMetaData.FIRST_NAME.getLabel());
        String customerLastName = tdCustomer.getValue(relationshipTab.getMetaKey(), RelationshipTabMetaData.LAST_NAME.getLabel());
        tdCustomer.adjust(TestData.makeKeyPath(relationshipTab.getMetaKey(), RelationshipTabMetaData.FIRST_NAME.getLabel()), customerFirstName)
                .adjust(TestData.makeKeyPath(relationshipTab.getMetaKey(), RelationshipTabMetaData.LAST_NAME.getLabel()), customerLastName);
        customerNonIndividual.createViaUI(tdCustomer);
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();

        TestData tdCaseProfile = CaseProfileContext.getDefaultCaseProfileTestData(termLifeInsuranceMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.EFFECTIVE_DATE.getLabel()), currentDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(fileIntakeManagementTab.getMetaKey() + "[0]", FileIntakeManagementTabMetaData.EFFECTIVE_DATE.getLabel()), currentDate.format(DateTimeUtils.MM_DD_YYYY)).resolveLinks();
        caseProfile.create(tdCaseProfile);

        TestData tdPolicy = getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath("InitiniateDialog", "Coverage Effective Date"), currentDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), currentDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "GA")
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel())).resolveLinks();
        termLifeInsuranceMasterPolicy.createPolicy(tdPolicy);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(currentDate.plusDays(60));
        mainApp().open();
        MainPage.QuickSearch.search(policyNumber);

        TestData tdClaim1 = termLifeClaim.getDefaultTestData(DATA_GATHER, "TestData_BenefitAccidentalDeath")
                .adjust(tdSpecific().getTestData("TestData_BenefitAccidentalDeath").resolveLinks())
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), LossEventTabMetaData.DATE_OF_LOSS.getLabel()), currentDate.plusDays(30).format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(BenefitsAccidentalDeathBeneficiaryTab.class.getSimpleName() + "[0]", BenefitsAccidentalDeathBeneficiaryTabMetaData.PROOF_OF_LOSS_RECEIVED.getLabel()), currentDate.plusDays(59).format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(BenefitsAccidentalDeathDeathCertificateTab.class.getSimpleName(), BenefitsAccidentalDeathDeathCertificateTabMetaData.OFFICIAL_DATE_OF_DEATH.getLabel()), currentDate.plusDays(30).format(MM_DD_YYYY))
                .resolveLinks();
        termLifeClaim.create(tdClaim1);
        termLifeClaim.claimSubmit().perform();

        String insuredFullName = ClaimSummaryPage.tableClaimParticipant.getRow(1).getCell("Participant").getValue();
        String insuredFirstName = insuredFullName.substring(0, 12);
        String insuredLastName = insuredFullName.substring(26);

        termLifeClaim.calculateSingleBenefitAmount().perform(termLifeClaim.getDefaultTestData("CalculateASingleBenefitAmount", "TestDataWithDeductions")
                .adjust(TestData.makeKeyPath(CoveragesActionTab.class.getSimpleName(), CoveragesActionTabMetaData.COVERAGE.getLabel()), BTL_BL), 1);
        termLifeClaim.calculateSingleBenefitAmount().perform(termLifeClaim.getDefaultTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CoveragesActionTab.class.getSimpleName(), CoveragesActionTabMetaData.COVERAGE.getLabel()), ADD_BL), 1);

        termLifeClaim.addPayment().perform(tdSpecific().getTestData("TestData_FinalPayment"));

        String claimNumber = ClaimSummaryPage.getClaimNumber();
        String documentDate = currentDate.plusDays(60).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        LOGGER.info("Step 1");
        assertThat(Efolder.isDocumentExistStartsContains(EOB.getName(), EOB.getName() + "-", String.format("%s-%s-%s-%s", EOB, claimNumber, insuredLastName, documentDate)))
                .withFailMessage("Generated document is absent in E-Folder").isTrue();

        LOGGER.info("Step 2");
        LOGGER.info("TEST: Get document from database in XML format");
        XmlValidator xmlValidator = DBHelper.getDocument(claimNumber, CLAIM);

        //1
        xmlValidator.checkDocument(UNDERWRITING_COMPANY, "RLHICA");
        //2
        xmlValidator.checkDocument(MASTER_POLICY_NUM, policyNumber);
        //3
        xmlValidator.checkDocument(PARTICIPANT_FIRST_NAME, insuredFirstName);
        xmlValidator.checkDocument(PARTICIPANT_MIDDLE_NAME, "Middle Name1");
        xmlValidator.checkDocument(PARTICIPANT_LAST_NAME, insuredLastName);
        //4 19
        xmlValidator.checkDocument(PAYEE, customerName);
        //5
        xmlValidator.checkDocument(ADDRESS_LINE_1, "Address1 1");
        xmlValidator.checkDocument(ADDRESS_LINE_2, "Address1 2");
        xmlValidator.checkDocument(ADDRESS_LINE_3, "Address1 3");
        //6
        xmlValidator.checkDocument(CITY, "City1 1");
        //7
        xmlValidator.checkDocument(STATE, "CA");
        //8
        xmlValidator.checkDocument(ZIP, "11111");
        //9
        xmlValidator.checkDocument(CHECK_NUM, "0000001");
        //10
        xmlValidator.checkDocument(ISSUE_DATE, currentDate.plusDays(60).format(MM_DD_YYYY));
        //13
        xmlValidator.checkDocument(CLAIM_NUM, claimNumber);
        //14
        xmlValidator.checkDocument(CUSTOMER_FIRST_NAME, customerFirstName);
        xmlValidator.checkDocument(CUSTOMER_LAST_NAME, customerLastName);
        //15
        xmlValidator.checkDocument(CUSTOMER_ADDRESS_LINE_1, "Address1_Cust1_Rel1");
        xmlValidator.checkDocument(CUSTOMER_ADDRESS_LINE_2, "Address2_Cust1_Rel1");
        xmlValidator.checkDocument(CUSTOMER_ADDRESS_LINE_3, "Address3_Cust1_Rel1");
        //16
        xmlValidator.checkDocument(CUSTOMER_CITY, "City_Cust1_Rel1");
        //17
        xmlValidator.checkDocument(CUSTOMER_PROVINCE, "ON");
        //18
        xmlValidator.checkDocument(CUSTOMER_POSTAL_CODE, "M4B 1B3");
        //21
        xmlValidator.checkNodeNotPresent(PAYMENT_FROM_DATE);
        //22
        xmlValidator.checkNodeNotPresent(PAYMENT_THROUGH_DATE);
        //23
        xmlValidator.checkNodeNotPresent(ELIMINATION_PERIOD_START_DATE);
        //24
        xmlValidator.checkNodeNotPresent(ELIMINATION_PERIOD_END_DATE);
        //25
        XmlValidator coverageSection_BTL_BL = getNeededSection(xmlValidator, COVERAGE_NAME_BY_CCOVERAGE_NAME, BTL_BL);
        XmlValidator coverageSection_ADD_BL = getNeededSection(xmlValidator, COVERAGE_NAME_BY_CCOVERAGE_NAME, ADD_BL);
        xmlValidator.checkDocument(TAXES_NAME, "State Withholding Tax for Legal Payee State");
        xmlValidator.checkDocument(REDUCTIONS_NAME, "Deduction");
        //26
        coverageSection_BTL_BL.checkDocument(COVERAGES_AMOUNT_IN_PDF, "200.00"); //Claim > Payments > Payment > Inquiry Payment > Payment Allocation > Allocation Amount
        coverageSection_ADD_BL.checkDocument(COVERAGES_AMOUNT_IN_PDF, "200.00"); //Claim > Payments > Payment > Inquiry Payment > Payment Allocation > Allocation Amount
        xmlValidator.checkDocument(TAXES_ITEM_AMOUNT, "-105.00");
        xmlValidator.checkDocument(REDUCTIONS_ITEM_AMOUNT, "100.00");
        //27
        xmlValidator.checkDocument(NET_AMOUNT, "395.75");
        //28
        xmlValidator.checkNodeNotPresent(PAYMENT_MEMO);
    }
}
