/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.billing.groupbenefits.fulladmin;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.BillingAccountContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.billing.groupbenefits.GroupBenefitsBillingBaseTest;
import com.google.common.collect.ImmutableMap;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.main.enums.BillingConstants.BillsAndStatementsStatusGB.ISSUED;
import static com.exigen.ren.main.enums.CaseProfileConstants.ErrorMessages.NO_RECORD_FOUND;
import static com.exigen.ren.main.modules.policy.gb_ac.certificate.metadata.CertificatePolicyTabMetaData.BILLING_LOCATION;
import static com.exigen.ren.utils.components.Components.BILLING_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSupportBillingByLocation extends GroupBenefitsBillingBaseTest implements BillingAccountContext {

    private final ImmutableList listLocations = ImmutableList.of("LOC1", "LOC2","LOC3");
    private final String cov1Name = "Enhanced Accident (Employee Only)";

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-32938", component = BILLING_GROUPBENEFITS)
    public void testSupportBillingByLocation() {

        mainApp().open();

        createDefaultNonIndividualCustomer();

        caseProfile.create(tdSpecific().getTestData("TestData_Case_Profile"), groupAccidentMasterPolicy.getType());

        //MP1 (Product1, coverages - COV1, COV2, Contribution Type for every coverage is Voluntary, Payment Mode is 12).
        groupAccidentMasterPolicy.createPolicy(tdSpecific().getTestData("TestData_TwoCoverages")
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        String policyMP1 = PolicySummaryPage.labelPolicyNumber.getValue();

        groupAccidentCertificatePolicy.createPolicyViaUI(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getClass().getSimpleName(), BILLING_LOCATION.getLabel()), "LOC1")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        String policyMP1CP1 = PolicySummaryPage.labelPolicyNumber.getValue();

        PolicySummaryPage.linkMasterPolicy.click();

        groupAccidentCertificatePolicy.createPolicy(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "CoveragesTab_PlanEnhanced").resolveLinks())
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getClass().getSimpleName(), BILLING_LOCATION.getLabel()), "LOC2")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));
        String policyMP1CP2 = PolicySummaryPage.labelPolicyNumber.getValue();
        PolicySummaryPage.linkMasterPolicy.click();

        //MP2 (Product2, coverage - COV3, Contribution Type for coverage is Non-Contributory, Payment Mode is 12).
        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA")));
        String policyMP2 = PolicySummaryPage.labelPolicyNumber.getValue();

        groupAccidentCertificatePolicy.createPolicy(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "CoveragesTab_PlanEnhanced").resolveLinks())
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getClass().getSimpleName(), BILLING_LOCATION.getLabel()), "LOC3")
                .adjust(groupAccidentCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks()));
        String policyMP2CP1 = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("---=={Step 1}==---");
        navigateToBillingAccount(policyMP1);
        billingAccount.generateFutureStatement().perform();

        String billingGroup1 = BillingSummaryPage.tableBillableCoverages.getRow(ImmutableMap.of(TableConstants.BillingBillableCoveragesGB.POLICY.getName(), policyMP1))
                .getCell(TableConstants.BillingBillableCoveragesGB.BILLING_GROUP.getName()).getValue();
        String billingGroup2 = BillingSummaryPage.tableBillableCoverages.getRow(ImmutableMap.of(TableConstants.BillingBillableCoveragesGB.POLICY.getName(), policyMP2))
                .getCell(TableConstants.BillingBillableCoveragesGB.BILLING_GROUP.getName()).getValue();

        assertThat(BillingSummaryPage.tableBillsAndStatements).with(TableConstants.BillingBillsAndStatementsGB.STATUS, ISSUED).containsMatchingRow(1);

        LOGGER.info("---=={Step 2}==---");
        BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).controls.links.getFirst().click();
        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.radioButtonBillableCoveragesByBillingGroup).hasValue(true);
            softly.assertThat(BillingSummaryPage.radioButtonBillableCoveragesByLocation).hasValue(false);
        });

        LOGGER.info("---=={Step 4}==---");
        BillingSummaryPage.openBillsStatementsLocationView();
        assertSoftly(softly -> {
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByLocation).hasRows(3);
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByLocation.getColumn(1).getValue()).isEqualTo(listLocations);
        });

        LOGGER.info("---=={Step 5-7}==---");
        assertSoftly(softly -> {
            //LOC1
            BillingSummaryPage.searchByLocationFilter("LOC1");
            BillingSummaryPage.tableBillableCoveragesByLocation.getRow(1, "LOC1").getCell(1).controls.links.getFirst().click();
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesBillingGroupsByLocation.getHeader()).hasValue(ImmutableList.of(
                    TableConstants.BillableCoveragesBillingGroupsByLocation.BILLING_GROUP.getName(),
                    TableConstants.BillableCoveragesBillingGroupsByLocation.PAYOR.getName(),
                    TableConstants.BillableCoveragesBillingGroupsByLocation.PREMIUM.getName()));

            softly.assertThat(BillingSummaryPage.tableBillableCoveragesBillingGroupsByLocation).hasRows(1);
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesBillingGroupsByLocation).with(TableConstants.BillableCoveragesBillingGroupsByLocation.BILLING_GROUP, billingGroup1).containsMatchingRow(1);

            BillingSummaryPage.tableBillableCoveragesBillingGroupsByLocation.getRow(TableConstants.BillableCoveragesBillingGroupsByLocation.BILLING_GROUP.getName(), billingGroup1).getCell(TableConstants.BillableCoveragesBillingGroupsByLocation.BILLING_GROUP.getName()).controls.links.getFirst().click();
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByGroupByLocation.getHeader()).hasValue(ImmutableList.of(
                    TableConstants.BillableCoveragesByGroupByLocation.COVERAGE.getName(),
                    TableConstants.BillableCoveragesByGroupByLocation.POLICY_PLAN.getName(),
                    TableConstants.BillableCoveragesByGroupByLocation.COVERAGE_SEGMENT_CLASSIFIER.getName(),
                    TableConstants.BillableCoveragesByGroupByLocation.PREMIUM.getName()));

            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByGroupByLocation).hasRows(1);
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByGroupByLocation).with(TableConstants.BillableCoveragesByGroupByLocation.COVERAGE,  cov1Name)
                    .with(TableConstants.BillableCoveragesByGroupByLocation.POLICY_PLAN, PolicyConstants.PlanAccident.BASE_BUY_UP).containsMatchingRow(1);

            BillingSummaryPage.tableBillableCoveragesByGroupByLocation.getRow(TableConstants.BillableCoveragesByGroupByLocation.COVERAGE.getName(), cov1Name).getCell(TableConstants.BillableCoveragesByGroupByLocation.COVERAGE.getName()).controls.links.getFirst().click();
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesParticipantsByCoverageByLocation.getHeader()).hasValue(ImmutableList.of(
                    TableConstants.BillableCoveragesParticipantsByCoverageByLocation.PARTICIPANT.getName(),
                    TableConstants.BillableCoveragesParticipantsByCoverageByLocation.CERTIFICATES.getName(),
                    TableConstants.BillableCoveragesParticipantsByCoverageByLocation.VOLUME.getName(),
                    TableConstants.BillableCoveragesParticipantsByCoverageByLocation.RATE.getName(),
                    TableConstants.BillableCoveragesParticipantsByCoverageByLocation.RATE_BASIS.getName(),
                    TableConstants.BillableCoveragesParticipantsByCoverageByLocation.PREMIUM.getName()));
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesParticipantsByCoverageByLocation).hasRows(1);
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesParticipantsByCoverageByLocation).with(TableConstants.BillableCoveragesParticipantsByCoverageByLocation.CERTIFICATES, policyMP1CP1).containsMatchingRow(1);

            //LOC2
            BillingSummaryPage.searchByLocationFilter("LOC2");
            BillingSummaryPage.tableBillableCoveragesByLocation.getRow(1, "LOC2").getCell(1).controls.links.getFirst().click();
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesBillingGroupsByLocation).hasRows(1);
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesBillingGroupsByLocation).with(TableConstants.BillableCoveragesBillingGroupsByLocation.BILLING_GROUP, billingGroup1).containsMatchingRow(1);

            BillingSummaryPage.tableBillableCoveragesBillingGroupsByLocation.getRow(TableConstants.BillableCoveragesBillingGroupsByLocation.BILLING_GROUP.getName(), billingGroup1).getCell(TableConstants.BillableCoveragesBillingGroupsByLocation.BILLING_GROUP.getName()).controls.links.getFirst().click();
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByGroupByLocation).hasRows(1);
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByGroupByLocation).with(TableConstants.BillableCoveragesByGroupByLocation.COVERAGE,  cov1Name)
                    .with(TableConstants.BillableCoveragesByGroupByLocation.POLICY_PLAN, PolicyConstants.PlanAccident.ENHANCED_10_UNITS).containsMatchingRow(1);

            BillingSummaryPage.tableBillableCoveragesByGroupByLocation.getRow(TableConstants.BillableCoveragesByGroupByLocation.COVERAGE.getName(), cov1Name).getCell(TableConstants.BillableCoveragesByGroupByLocation.COVERAGE.getName()).controls.links.getFirst().click();
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesParticipantsByCoverageByLocation).hasRows(1);
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesParticipantsByCoverageByLocation).with(TableConstants.BillableCoveragesParticipantsByCoverageByLocation.CERTIFICATES, policyMP1CP2).containsMatchingRow(1);

            //LOC3
            BillingSummaryPage.searchByLocationFilter("LOC3");
            BillingSummaryPage.tableBillableCoveragesByLocation.getRow(1, "LOC3").getCell(1).controls.links.getFirst().click();
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesBillingGroupsByLocation).hasRows(1);
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesBillingGroupsByLocation).with(TableConstants.BillableCoveragesBillingGroupsByLocation.BILLING_GROUP, billingGroup2).containsMatchingRow(1);

            BillingSummaryPage.tableBillableCoveragesBillingGroupsByLocation.getRow(TableConstants.BillableCoveragesBillingGroupsByLocation.BILLING_GROUP.getName(), billingGroup2).getCell(TableConstants.BillableCoveragesBillingGroupsByLocation.BILLING_GROUP.getName()).controls.links.getFirst().click();
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByGroupByLocation).hasRows(1);
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByGroupByLocation).with(TableConstants.BillableCoveragesByGroupByLocation.COVERAGE,  cov1Name)
                    .with(TableConstants.BillableCoveragesByGroupByLocation.POLICY_PLAN, PolicyConstants.PlanAccident.ENHANCED_10_UNITS).containsMatchingRow(1);

            BillingSummaryPage.tableBillableCoveragesByGroupByLocation.getRow(TableConstants.BillableCoveragesByGroupByLocation.COVERAGE.getName(), cov1Name).getCell(TableConstants.BillableCoveragesByGroupByLocation.COVERAGE.getName()).controls.links.getFirst().click();
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesParticipantsByCoverageByLocation).hasRows(1);
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesParticipantsByCoverageByLocation).with(TableConstants.BillableCoveragesParticipantsByCoverageByLocation.CERTIFICATES, policyMP2CP1).containsMatchingRow(1);
        });

        LOGGER.info("---=={Step 8}==---");
        assertSoftly(softly -> {
            BillingSummaryPage.searchByLocationFilter("LO");
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByLocation).hasRows(3);

            BillingSummaryPage.searchByLocationFilter("LOC3");
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByLocation).hasRows(1);

            BillingSummaryPage.searchByLocationFilter("LOC4");
            softly.assertThat(BillingSummaryPage.tableBillableCoveragesByLocation.getRow(1).getValue()).contains(NO_RECORD_FOUND);
        });

    }
}
