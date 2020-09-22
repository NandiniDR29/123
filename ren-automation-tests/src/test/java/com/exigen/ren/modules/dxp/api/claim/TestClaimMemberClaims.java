package com.exigen.ren.modules.dxp.api.claim;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.ClaimConstants.ClaimStatus;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_ac.AccidentHealthClaimACContext;
import com.exigen.ren.main.modules.claim.gb_dn.DentalClaimContext;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimLTDContext;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimSTDContext;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.modules.claim.gb_tl.TermLifeClaimContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.ShortTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.claim.ClaimEmployerModel;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageEvaluationTabMetaData.INSURED_PERSON_COVERAGE_EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.FIRST_NAME;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.LAST_NAME;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.COUNTY_CODE;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.labelCustomerName;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimMemberClaims extends RestBaseTest implements CaseProfileContext,
        TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext, TermLifeClaimContext,
        LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext, DisabilityClaimLTDContext,
        ShortTermDisabilityMasterPolicyContext, ShortTermDisabilityCertificatePolicyContext, DisabilityClaimSTDContext,
        GroupAccidentMasterPolicyContext, GroupAccidentCertificatePolicyContext, AccidentHealthClaimACContext,
        GroupDentalMasterPolicyContext, GroupDentalCertificatePolicyContext, DentalClaimContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38341", component = CUSTOMER_REST)
    public void testDxpMembersClaims() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumNIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(
                termLifeInsuranceMasterPolicy.getType(),
                longTermDisabilityMasterPolicy.getType(),
                shortTermDisabilityMasterPolicy.getType(),
                groupAccidentMasterPolicy.getType(),
                groupDentalMasterPolicy.getType());

        // TL claim section
        createDefaultTermLifeInsuranceMasterPolicy();
        createDefaultTermLifeInsuranceCertificatePolicy();
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();
        String customerNameForCP_TL = CustomerSummaryPage.labelCustomerName.getValue();
        String firstNameIC1 = customerNameForCP_TL.split(" ")[0];
        String lastNameIC1 = customerNameForCP_TL.split(" ")[1];

        termLifeClaim.create(termLifeClaim.getDefaultTestData("DataGatherCertificate", "TestData_Without_Benefits"));
        String claimNumber_TL = ClaimSummaryPage.getClaimNumber();
        termLifeClaim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.OPEN);

        termLifeClaim.addBenefit().perform(termLifeClaim.getDefaultTestData("NewBenefit", "TestData_PremiumWaiver"));
        termLifeClaim.calculateSingleBenefitAmount().perform(termLifeClaim.getDefaultTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY), 1);
        assertThat(ClaimAdjudicationBenefitPage.tableAllSingleBenefitCalculations).hasRows(1);

        termLifeClaim.addPayment().perform(termLifeClaim.getDefaultTestData("ClaimPayment", "TestData_FinalPayment"));
        termLifeClaim.issuePayment().perform(termLifeClaim.getDefaultTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS)).hasValue("Issued");

        // LTD claim section
        search(customerNumNIC1);
        createDefaultLongTermDisabilityMasterPolicy();
        longTermDisabilityCertificatePolicy.createPolicyViaUI(longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithoutNewCustomer")
                .adjust(TestData.makeKeyPath(LongTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(), SEARCH_CUSTOMER.getLabel(), FIRST_NAME.getLabel()), firstNameIC1)
                .adjust(TestData.makeKeyPath(LongTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(), SEARCH_CUSTOMER.getLabel(), LAST_NAME.getLabel()), lastNameIC1)
                .adjust(longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        String customerNameForCP_LTD = labelCustomerName.getValue();
        String firstCustomerNameForCP_LTD = customerNameForCP_LTD.split(" ")[0];
        String lastCustomerNameForCP_LTD = customerNameForCP_LTD.split(" ")[1];

        createDefaultLTDClaimForCertificatePolicyWithoutBenefits();
        String claimNumber_LTD = ClaimSummaryPage.getClaimNumber();
        termLifeClaim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.OPEN);
        LocalDateTime dateAssigned = LocalDateTime.parse(ClaimSummaryPage.tableLossEvent.getRow(1).getCell("Date Assigned").getValue(), DateTimeUtilsHelper.MM_DD_YYYY_H_MM_A);

        disabilityClaim.flagFraudPotential().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.SIU_POTENTIAL);

        disabilityClaim.addBenefit().perform(disabilityClaim.getLTDTestData().getTestData("NewBenefit", "TestData_LTD")
                // https://jira.exigeninsurance.com/browse/REN-33082: CWCP is out of scope
                .mask(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), INSURED_PERSON_COVERAGE_EFFECTIVE_DATE.getLabel())));
        disabilityClaim.calculateSingleBenefitAmount().perform(disabilityClaim.getLTDTestData().getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        disabilityClaim.addBenefitReserves().perform(disabilityClaim.getLTDTestData().getTestData("BenefitReserves", "TestData"), 1);
        disabilityClaim.postRecovery().perform(disabilityClaim.getLTDTestData().getTestData("ClaimPayment", "TestData_PostRecovery"));

        // STD claim section
        search(customerNumNIC1);
        createDefaultShortTermDisabilityMasterPolicy();
        shortTermDisabilityCertificatePolicy.createPolicyViaUI(shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithoutNewCustomer")
                .adjust(TestData.makeKeyPath(ShortTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER.getLabel(),
                        com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.FIRST_NAME.getLabel()), firstCustomerNameForCP_LTD)
                .adjust(TestData.makeKeyPath(ShortTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER.getLabel(),
                        com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.LAST_NAME.getLabel()), lastCustomerNameForCP_LTD)
                .mask(TestData.makeKeyPath(ShortTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.InsuredTabMetaData.FIRST_NAME.getLabel()))
                .adjust(shortTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        String customerNameForCP_STD = labelCustomerName.getValue();
        String lastCustomerNameForCP_STD = customerNameForCP_STD.split(" ")[1];

        disabilityClaim.create(disabilityClaim.getSTDTestData().getTestData("DataGatherCertificate", "TestData_Without_AdditionalParties"));
        String claimNumber_STD = ClaimSummaryPage.getClaimNumber();

        disabilityClaim.flagFraudPotential().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.SIU_POTENTIAL);

        disabilityClaim.reviewFraud().perform(new SimpleDataProvider());
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.SIU_REVIEW);
        disabilityClaim.calculateSingleBenefitAmount().perform(disabilityClaim.getSTDTestData().getTestData("CalculateASingleBenefitAmount", "TestData_STD"), 1);

        disabilityClaim.addPayment().perform(disabilityClaim.getSTDTestData().getTestData("ClaimPayment", "TestData_PartialPayment"));
        disabilityClaim.issuePayment().perform(disabilityClaim.getSTDTestData().getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS)).hasValue("Issued");

        // AC claim section
        search(customerNumNIC1);
        createDefaultGroupAccidentMasterPolicy();
        groupAccidentCertificatePolicy.createPolicyViaUI(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithoutNewCustomer")
                .adjust(TestData.makeKeyPath(ShortTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER.getLabel(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.FIRST_NAME.getLabel()), firstCustomerNameForCP_LTD)
                .adjust(TestData.makeKeyPath(ShortTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER.getLabel(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.SearchCustomerSingleSelector.LAST_NAME.getLabel()), lastCustomerNameForCP_STD)
                .mask(TestData.makeKeyPath(ShortTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.InsuredTabMetaData.FIRST_NAME.getLabel()))
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        createDefaultGroupAccidentClaimForCertificatePolicy();
        String claimNumber_AC = ClaimSummaryPage.getClaimNumber();

        termLifeClaim.claimOpen().perform();
        accHealthClaim.calculateSingleBenefitAmount().perform(accHealthClaim.getGbACTestData().getTestData("CalculateASingleBenefitAmount", "TestData_AccidentalDeath"), 1);
        accHealthClaim.addPayment().perform(accHealthClaim.getGbACTestData().getTestData("ClaimPayment", "TestData_PartialPayment"));
        accHealthClaim.issuePayment().perform(accHealthClaim.getGbACTestData().getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        assertThat(ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                .getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS)).hasValue("Issued");

        toSubTab(OVERVIEW);
        accHealthClaim.claimClose().perform(accHealthClaim.getGbACTestData().getTestData("ClaimClose", "TestData"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.CLOSED);

        // DN claim section
        search(customerNumNIC1);
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(GroupDentalMasterPolicyContext.policyInformationTab.getMetaKey(), COUNTY_CODE.getLabel()), "index=1"));
        groupDentalCertificatePolicy.createPolicyViaUI(groupDentalCertificatePolicy.defaultTestData().getTestData(TestDataKey.DATA_GATHER, "TestDataWithoutNewCustomer")
                .adjust(groupDentalCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(TestData.makeKeyPath(GroupDentalCertificatePolicyContext.insuredTab.getMetaKey(), InsuredTabMetaData.SEARCH_CUSTOMER.getLabel(),
                        InsuredTabMetaData.SearchCustomerSingleSelector.FIRST_NAME.getLabel()), firstCustomerNameForCP_LTD)
                .adjust(TestData.makeKeyPath(ShortTermDisabilityCertificatePolicyContext.insuredTab.getMetaKey(), InsuredTabMetaData.LAST_NAME.getLabel()), "Smith" + RandomStringUtils.random(8, false, true)));
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimStatus.LOGGED_INTAKE);
        String claimNumber_DN = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Step 1");
        assertSoftly(softly -> {
            ResponseContainer<List<ClaimEmployerModel>> response = dxpRestService.getMemberClaims(customerNumIC1, "1", "1", null, null, null, null, null, null);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(response.getModel().size()).isEqualTo(1);
            softly.assertThat(response.getModel().get(0).getClaimNumber()).isEqualTo(claimNumber_LTD);
        });

        LOGGER.info("Step 2");
        assertSoftly(softly -> {
            ResponseContainer<List<ClaimEmployerModel>> response = dxpRestService.getMemberClaims(customerNumIC1, null, null, firstCustomerNameForCP_LTD, "-insuredLastName", null, null, null, null);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(response.getModel().size()).isEqualTo(4);
            List<String> claimLastNamesFromResponse = response.getModel().stream().map(model -> model.getInsured().getLastName()).collect(Collectors.toList());
            softly.assertThat(claimLastNamesFromResponse).isEqualTo(Ordering.natural().reverse().sortedCopy(claimLastNamesFromResponse));
        });

        LOGGER.info("Step 3");
        assertSoftly(softly -> {
            ResponseContainer<List<ClaimEmployerModel>> response = dxpRestService.getMemberClaims(
                    customerNumIC1, null, null, null, null,
                    String.format("%sT00:00:00Z", TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().toString()), "FRAUD_POTENTIAL", null, null);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(response.getModel().size()).isEqualTo(1);
            softly.assertThat(response.getModel().get(0).getClaimNumber()).isEqualTo(claimNumber_LTD);
        });

        LOGGER.info("Step 4");
        assertSoftly(softly -> {
            ResponseContainer<List<ClaimEmployerModel>> response = dxpRestService.getMemberClaims(customerNumIC1, null, null, null, null, null, null, dateAssigned.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z), lastNameIC1);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(response.getModel().size()).isEqualTo(1);
            softly.assertThat(response.getModel().get(0).getClaimNumber()).isEqualTo(claimNumber_TL);
        });

        LOGGER.info("Step 5");
        assertSoftly(softly -> {
            ResponseContainer<List<ClaimEmployerModel>> response = dxpRestService.getMemberClaims(customerNumIC1, null, null, null, null, null, null, null, null);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(response.getModel().size()).isEqualTo(5);
            List<String> claimNumbersFromResponse = response.getModel().stream().map(ClaimEmployerModel::getClaimNumber).collect(Collectors.toList());
            softly.assertThat(claimNumbersFromResponse).containsExactlyInAnyOrder(claimNumber_TL, claimNumber_LTD, claimNumber_STD, claimNumber_AC, claimNumber_DN);
        });
    }
}