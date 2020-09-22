package com.exigen.ren.modules.enrollment;

import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.customer.metadata.RelationshipTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.caseprofile.CaseProfileBaseTest;
import com.exigen.ren.utils.components.Components;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.modules.billing.BillingStrategyConfigurator.dbService;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGenerateAndUploadRatingCensusVerificationWhenMemberCompanyYes extends CaseProfileBaseTest implements LongTermDisabilityMasterPolicyContext,
        ShortTermDisabilityMasterPolicyContext, TermLifeInsuranceMasterPolicyContext, GroupDentalMasterPolicyContext, GroupVisionMasterPolicyContext {

    private static String SQL_GET_POLICY_INFO = "SELECT  policyNumber, memberCompanyName, LOB FROM PolicySummary WHERE policyNumber IN ('%s', '%s', '%s', '%s', '%s')";

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-41175", component = Components.CASE_PROFILE)
    public void testGenerateAndUploadRatingCensusVerificationWhenMemberCompanyYes() {
        LOGGER.info("General preconditions");
        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_WithRelationshipTypes")
                .adjust(tdSpecific().getTestData("TestDataCustomer").resolveLinks()));
        createDefaultCaseProfile(
                shortTermDisabilityMasterPolicy.getType(),
                longTermDisabilityMasterPolicy.getType(),
                termLifeInsuranceMasterPolicy.getType(),
                groupDentalMasterPolicy.getType(),
                groupVisionMasterPolicy.getType());

        String relationship1 = tdSpecific().getValue("RelationshipTestData1", RelationshipTabMetaData.NAME_LEGAL.getLabel());
        String relationship2 = tdSpecific().getValue("RelationshipTestData2", RelationshipTabMetaData.NAME_LEGAL.getLabel());

        shortTermDisabilityMasterPolicy.createQuote(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(ShortTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(ShortTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationship1).resolveLinks());
        shortTermDisabilityMasterPolicy.propose().perform(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.acceptContract().perform(getDefaultSTDMasterPolicyData());
        String quoteNumberSTD = PolicySummaryPage.labelPolicyNumber.getValue();

        longTermDisabilityMasterPolicy.createQuote(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(LongTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(LongTermDisabilityMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationship2).resolveLinks());
        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.acceptContract().perform(getDefaultLTDMasterPolicyData());
        String quoteNumberLTD = PolicySummaryPage.labelPolicyNumber.getValue();

        termLifeInsuranceMasterPolicy.createQuote(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(TermLifeInsuranceMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_MEMBER_COMPANY.getLabel()), VALUE_YES)
                .adjust(TestData.makeKeyPath(TermLifeInsuranceMasterPolicyContext.policyInformationTab.getMetaKey(),
                        com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.MEMBER_COMPANY_NAME.getLabel()), relationship1).resolveLinks());
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
        termLifeInsuranceMasterPolicy.acceptContract().perform(getDefaultTLMasterPolicyData());
        String quoteNumberTL = PolicySummaryPage.labelPolicyNumber.getValue();

        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());
        String quoteNumberDN = PolicySummaryPage.labelPolicyNumber.getValue();

        groupVisionMasterPolicy.createQuote(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());
        String quoteNumberVS = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step#1 execution");
        caseProfile.createEnrolledCensus().perform(caseProfile.getDefaultTestData("CreateEnrolledCensus", DEFAULT_TEST_DATA_KEY));

        LOGGER.info("Step#2 verification");
        List<Map<String, String>> dbResponseListPolicyInfo = RetryService.run(
                rows -> !rows.isEmpty(),
                () -> dbService.getRows(String.format(SQL_GET_POLICY_INFO, quoteNumberSTD, quoteNumberLTD, quoteNumberTL, quoteNumberDN, quoteNumberVS)),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(5, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(dbResponseListPolicyInfo.stream().filter(x -> x.get("policyNumber").equals(quoteNumberSTD)
                    && x.get("memberCompanyName").equals(relationship1)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListPolicyInfo.stream().filter(x -> x.get("policyNumber").equals(quoteNumberLTD)
                    && x.get("memberCompanyName").equals(relationship2)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListPolicyInfo.stream().filter(x -> x.get("policyNumber").equals(quoteNumberTL)
                    && x.get("memberCompanyName").equals(relationship1)).collect(Collectors.toList())).hasSize(1);
            softly.assertThat(dbResponseListPolicyInfo.stream().filter(x -> x.get("policyNumber").equals(quoteNumberDN))
                    .collect(Collectors.toList()).get(0).get("memberCompanyName")).isNull();
            softly.assertThat(dbResponseListPolicyInfo.stream().filter(x -> x.get("policyNumber").equals(quoteNumberVS))
                    .collect(Collectors.toList()).get(0).get("memberCompanyName")).isNull();

            LOGGER.info("Step#3 verification");
            softly.assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1))
                    .hasCellWithValue("Description", "Generation of Rating Census file from Policy Staging Table enrolled data file is failed - No enrolled data for Group Sponsor in Policy Staging Table.");
        });
    }
}
