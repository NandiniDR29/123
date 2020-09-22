package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceQuoteModel;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.CoveragesConstants.CoverageTiers.*;
import static com.exigen.ren.main.enums.PolicyConstants.PlanVision.A_LA_CARTE;
import static com.exigen.ren.main.enums.SalesforceConstants.SF_VS;
import static com.exigen.ren.main.enums.TableConstants.PlanTierAndRatingSelection.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.COVERAGE_TIERS_CHANGE_CONFIRMATION;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.ClassificationManagementTab.tableClassificationGroupPlanRelationships;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.ClassificationManagementTab.tablePlanTierAndRatingInfo;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestSalesforceUploadQuoteDataVerificationVSForMultipleClassGroup extends SalesforceBaseTest implements CustomerContext, ProfileContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-21601"}, component = INTEGRATION)
    public void testSalesforceUploadQuoteDataVerificationVSForMultipleClassGroup() {
        LOGGER.info("General Preconditions");
        mainApp().reopen();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_PolicyCreationWithTwoPlans")
                .adjust(TestData.makeKeyPath(ClassificationManagementTab.class.getSimpleName() + "[0]", "Classification Group", "Class Name"), "CLERICAL")
                .adjust(TestData.makeKeyPath(ClassificationManagementTab.class.getSimpleName() + "[1]", "Classification Group", "Class Name"), "MANAGER").resolveLinks(), groupVisionMasterPolicy.getType());

        groupVisionMasterPolicy.createQuote(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS.getLabel()), ImmutableList.of(EMPLOYEE_PLUS_FAMILY, EMPLOYEE_PLUS_SPOUSE, EMPLOYEE_PLUS_CHILD_REN, EMPLOYEE_ONLY))
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", COVERAGE_TIERS_CHANGE_CONFIRMATION.getLabel())).resolveLinks()
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()).resolveLinks());
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        groupVisionMasterPolicy.dataGather().start();
        classificationManagementMpTab.navigateToTab();

        tableClassificationGroupPlanRelationships.getRowContains("Class Name", "CLERICAL").getCell(6).controls.links.get(CHANGE).click();
        int planClericalParticipantsEOnly = Integer.parseInt(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_ONLY).getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue());
        String planClericalRateEOnly = new Currency(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_ONLY).getCell(RATE.getName()).getValue()).toPlainString();

        int planClericalParticipantsSpouse = Integer.parseInt(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_SPOUSE).getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue());
        String planClericalRateSpouse = new Currency(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_SPOUSE).getCell(RATE.getName()).getValue()).toPlainString();

        int planClericalParticipantsChild = Integer.parseInt(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_CHILDREN).getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue());
        String planClericalRateChild = new Currency(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_CHILDREN).getCell(RATE.getName()).getValue()).toPlainString();

        int planClericalParticipantsFam = Integer.parseInt(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_FAMILY).getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue());
        String planClericalRateFam = new Currency(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_FAMILY).getCell(RATE.getName()).getValue()).toPlainString();

        tableClassificationGroupPlanRelationships.getRowContains("Class Name", "MANAGER").getCell(6).controls.links.get(CHANGE).click();
        int planManagerParticipantsEOnly = Integer.parseInt(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_ONLY).getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue());
        int planManagerParticipantsSpouse = Integer.parseInt(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_SPOUSE).getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue());
        int planManagerParticipantsChild = Integer.parseInt(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_CHILDREN).getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue());
        int planManagerParticipantsFam = Integer.parseInt(tablePlanTierAndRatingInfo.getRowContains(COVERAGE_TIER.getName(), EMPLOYEE_PLUS_FAMILY).getCell(NUMBER_OF_PARTICIPANTS.getName()).getValue());
        Tab.buttonSaveAndExit.click();

        String participantsEOnly = String.valueOf(planClericalParticipantsEOnly + planManagerParticipantsEOnly).concat(".0");
        String participantsSpouse = String.valueOf(planClericalParticipantsSpouse + planManagerParticipantsSpouse).concat(".0");
        String participantsChild = String.valueOf(planClericalParticipantsChild + planManagerParticipantsChild).concat(".0");
        String participantsFam = String.valueOf(planClericalParticipantsFam + planManagerParticipantsFam).concat(".0");

        String quoteIdCoverageALaCarte = String.format("%s_%s_%s Insured", quoteNumber, A_LA_CARTE, SF_VS);
        ResponseContainer<SalesforceQuoteModel> responseQuoteVision = RetryService.run(
                predicate -> predicate.getResponse().getStatus() == 200 && predicate.getModel().getFirstPartyEnrollment() != null,
                () -> salesforceService.getQuoteFromSalesforce(quoteIdCoverageALaCarte),
                StopStrategies.stopAfterAttempt(10), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));

        assertSoftly(softly -> {
            softly.assertThat(responseQuoteVision.getModel().getFirstPartyEnrollment()).isEqualTo(participantsEOnly);
            softly.assertThat(responseQuoteVision.getModel().getSecondPartyEnrollment()).isEqualTo(participantsSpouse);
            softly.assertThat(responseQuoteVision.getModel().getThirdPartyEnrollment()).isEqualTo(participantsChild);
            softly.assertThat(responseQuoteVision.getModel().getFourthPartyEnrollment()).isEqualTo(participantsFam);

            softly.assertThat(responseQuoteVision.getModel().getFirstPartyFormulaRate()).isEqualTo(planClericalRateEOnly);
            softly.assertThat(responseQuoteVision.getModel().getSecondPartyFormulaRate()).isEqualTo(planClericalRateSpouse);
            softly.assertThat(responseQuoteVision.getModel().getThirdPartyFormulaRate()).isEqualTo(planClericalRateChild);
            softly.assertThat(responseQuoteVision.getModel().getFourthPartyFormulaRate()).isEqualTo(planClericalRateFam);

            softly.assertThat(responseQuoteVision.getModel().getFirstPartyQuotedRate()).isEqualTo(planClericalRateEOnly);
            softly.assertThat(responseQuoteVision.getModel().getSecondPartyQuotedRate()).isEqualTo(planClericalRateSpouse);
            softly.assertThat(responseQuoteVision.getModel().getThirdPartyQuotedRate()).isEqualTo(planClericalRateChild);
            softly.assertThat(responseQuoteVision.getModel().getFourthPartyQuotedRate()).isEqualTo(planClericalRateFam);
        });
    }
}
