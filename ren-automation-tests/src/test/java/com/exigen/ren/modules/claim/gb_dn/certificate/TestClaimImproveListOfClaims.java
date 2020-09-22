package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_ac.AccidentHealthClaimACContext;
import com.exigen.ren.main.modules.claim.gb_dn.DentalClaimContext;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimLTDContext;
import com.exigen.ren.main.modules.claim.gb_tl.TermLifeClaimContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;
import com.exigen.ren.modules.BaseTest;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.PENDED;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.LAST_NAME;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS_CHANGE_CONFIRMATION;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableListOfDentalClaims;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableListOfNonDentalClaims;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimImproveListOfClaims extends BaseTest implements CustomerContext, CaseProfileContext,
        GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext, DentalClaimContext,
        TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext, TermLifeClaimContext,
        GroupAccidentMasterPolicyContext, GroupAccidentCertificatePolicyContext, AccidentHealthClaimACContext,
        LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext, DisabilityClaimLTDContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-35424", component = CLAIMS_GROUPBENEFITS)
    public void testClaimImproveListOfClaims() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String nonIndCustomerNum = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType(), termLifeInsuranceMasterPolicy.getType(), groupAccidentMasterPolicy.getType(), longTermDisabilityMasterPolicy.getType());

        createDefaultTermLifeInsuranceMasterPolicy();
        createDefaultTermLifeInsuranceCertificatePolicy();
        String certificatePolicyNumberTL = PolicySummaryPage.labelPolicyNumber.getValue();
        NavigationPage.toMainTab(CUSTOMER);
        String indCustomerNum = CustomerSummaryPage.labelCustomerNumber.getValue();
        String indCustomerName = CustomerSummaryPage.labelCustomerName.getValue();
        String indCustomerFirstName = indCustomerName.split(" ")[0];
        String indCustomerLastName = indCustomerName.split(" ")[1];

        search(nonIndCustomerNum);
        createDefaultGroupAccidentMasterPolicy();
        groupAccidentCertificatePolicy.createPolicyViaUI(groupAccidentCertificatePolicy.getDefaultTestData(DATA_GATHER, "TestData_WithoutNewCustomer")
                .adjust(TestData.makeKeyPath(GroupAccidentCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER.getLabel(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.FIRST_NAME.getLabel()), indCustomerFirstName)
                .adjust(TestData.makeKeyPath(GroupAccidentCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER.getLabel(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.LAST_NAME.getLabel()), indCustomerLastName)
                .mask(TestData.makeKeyPath(GroupAccidentCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.FIRST_NAME.getLabel()))
                .mask(TestData.makeKeyPath(GroupAccidentCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.LAST_NAME.getLabel()))
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
        String certificatePolicyNumberGA = PolicySummaryPage.labelPolicyNumber.getValue();

        search(nonIndCustomerNum);
        createDefaultLongTermDisabilityMasterPolicy();
        longTermDisabilityCertificatePolicy.createPolicyViaUI(longTermDisabilityCertificatePolicy.getDefaultTestData(DATA_GATHER, "TestData_WithoutNewCustomer")
                .adjust(TestData.makeKeyPath(LongTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(), SEARCH_CUSTOMER.getLabel(), InsuredTabMetaData.SearchCustomerSingleSelector.FIRST_NAME.getLabel()), indCustomerFirstName)
                .adjust(TestData.makeKeyPath(LongTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(), SEARCH_CUSTOMER.getLabel(), LAST_NAME.getLabel()), indCustomerLastName)
                .mask(TestData.makeKeyPath(LongTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.FIRST_NAME.getLabel()))
                .mask(TestData.makeKeyPath(LongTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.LAST_NAME.getLabel()))
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY)));
        String certificatePolicyNumberLTD = PolicySummaryPage.labelPolicyNumber.getValue();

        search(nonIndCustomerNum);
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()), "index=1")
                // to avoid "Proposal requires Underwriter approval because Master Quote contains Ortho Coverage and Total Number of Eligible Lives is less than 10" error during proposal generating
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.policyInformationTab.getMetaKey(), TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), "40")
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS.getLabel()), ImmutableList.of("Employee + Child(ren)", "Employee + Family", "Employee + Spouse", "Employee Only"))
                .mask(TestData.makeKeyPath(GroupDentalMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel()))
        );
        groupDentalCertificatePolicy.createPolicyViaUI(groupDentalCertificatePolicy.defaultTestData().getTestData(DATA_GATHER, "TestDataWithoutNewCustomer")
                .adjust(tdSpecific().getTestData("TestData_CertificatePolicy_TwoInsured"))
                .adjust(groupDentalCertificatePolicy.getDefaultTestData(ISSUE, DEFAULT_TEST_DATA_KEY))
                .adjust(TestData.makeKeyPath(GroupDentalCertificatePolicyContext.insuredTab.getMetaKey(), com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER.getLabel(),
                        com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.FIRST_NAME.getLabel()), indCustomerFirstName)
                .adjust(TestData.makeKeyPath(GroupDentalCertificatePolicyContext.insuredTab.getMetaKey(), com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER.getLabel(),
                        com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.LAST_NAME.getLabel()), indCustomerLastName));
        String certificatePolicyNumberDN = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step 1");
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CLAIM);
        assertSoftly(softly -> {
            softly.assertThat(tableListOfDentalClaims.getRow(1).getCell(1)).hasValue("No records found.");
            softly.assertThat(tableListOfNonDentalClaims.getRow(1).getCell(1)).hasValue("No records found.");
        });

        LOGGER.info("Step 2");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithoutPayment")
                .adjust(makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), REVIEW_REQUIRED_1)
                .adjust(makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel(), TOOTH.getLabel()), "3"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(PENDED);
        String claimNumberDN = ClaimSummaryPage.getClaimNumber();
        String tin = ClaimSummaryPage.tableProvider.getRow(1).getCell(IntakeInformationTab.ProviderColumns.TIN.getName()).getValue();
        String npi = ClaimSummaryPage.tableProvider.getRow(1).getCell(IntakeInformationTab.ProviderColumns.NPI.getName()).getValue();
        String dos = ClaimSummaryPage.tableSubmittedServices.getRow(1).getCell(IntakeInformationTab.SubmittedServicesColumns.DOS.getName()).getValue();
        String benefitAmount = ClaimSummaryPage.tableResultsOfAdjudication.getRow(1).getCell(TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.BENEFIT_AMOUNT.getName()).getValue();

        MainPage.QuickSearch.search(certificatePolicyNumberTL);
        createDefaultTermLifeInsuranceClaimForCertificatePolicy();
        String claimNumberTL = ClaimSummaryPage.getClaimNumber();

        MainPage.QuickSearch.search(certificatePolicyNumberGA);
        createDefaultGroupAccidentClaimForCertificatePolicy();
        String claimNumberGA = ClaimSummaryPage.getClaimNumber();

        MainPage.QuickSearch.search(certificatePolicyNumberLTD);
        createDefaultLongTermDisabilityClaimForCertificatePolicy();
        String claimNumberLTD = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Step 3");
        mainApp().reopen();
        search(indCustomerNum);
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CLAIM);
        assertSoftly(softly -> {
            softly.assertThat(tableListOfNonDentalClaims).hasRows(3);
            softly.assertThat(tableListOfNonDentalClaims).with(TableConstants.LifeAndDisabilityClaims.CLAIM_NUM, claimNumberTL).isPresent();
            softly.assertThat(tableListOfNonDentalClaims).with(TableConstants.LifeAndDisabilityClaims.CLAIM_NUM, claimNumberGA).isPresent();
            softly.assertThat(tableListOfNonDentalClaims).with(TableConstants.LifeAndDisabilityClaims.CLAIM_NUM, claimNumberLTD).isPresent();

        LOGGER.info("Step 4");
            softly.assertThat(tableListOfDentalClaims).hasRows(1);
            softly.assertThat(tableListOfDentalClaims).with(TableConstants.DentalClaims.CLAIM_NUM, claimNumberDN).isPresent();
            softly.assertThat(tableListOfDentalClaims.getRow(1).getCell(TableConstants.DentalClaims.STATUS.getName()).getValue()).startsWith(PENDED);
            softly.assertThat(tableListOfDentalClaims).with(TableConstants.DentalClaims.POLICY_NUM, certificatePolicyNumberDN).isPresent();
            softly.assertThat(tableListOfDentalClaims).with(TableConstants.DentalClaims.INSURED, indCustomerName).isPresent();
            softly.assertThat(tableListOfDentalClaims).with(TableConstants.DentalClaims.PATIENT, indCustomerName).isPresent();
            softly.assertThat(tableListOfDentalClaims).with(TableConstants.DentalClaims.DOS, dos).isPresent();
            softly.assertThat(tableListOfDentalClaims).with(TableConstants.DentalClaims.TIN, tin).isPresent();
            softly.assertThat(tableListOfDentalClaims).with(TableConstants.DentalClaims.LICENSE_NPI, npi).isPresent();
            softly.assertThat(tableListOfDentalClaims).with(TableConstants.DentalClaims.BENEFIT_AMOUNT, benefitAmount).isPresent();
        });
    }

}
