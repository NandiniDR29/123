package com.exigen.ren.modules.externalInterfaces.salesforce;

import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.salesforce.model.SalesforceQuoteModel;
import org.testng.annotations.Test;

import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupVisionCoverages.VISION;
import static com.exigen.ren.main.enums.PolicyConstants.PlanVision.A_LA_CARTE;

import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.FREQUENCY;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.FrequencyMetadata.EXAM_LENSES_FRAME;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.SALES_SUPPORT_ASSOCIATE;
import static com.exigen.ren.utils.components.Components.INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;
import static org.assertj.core.api.Assertions.assertThat;

public class TestMappingVerificationForQuoteVisionPlan extends SalesforceBaseTest implements CustomerContext, ProfileContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, INTEGRATION})
    @TestInfo(testCaseId = {"REN-43671"}, component = INTEGRATION)
    public void testMappingVerificationForQuoteVisionPlan() {
        LOGGER.info("General Preconditions");
        mainApp().reopen();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData()
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()));
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupVisionMasterPolicy.getType()));

        groupVisionMasterPolicy.createQuote(getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.INTERNAL_TEAM.getLabel(), SALES_SUPPORT_ASSOCIATE.getLabel()), "Karen Gorman")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", FREQUENCY.getLabel()), new SimpleDataProvider().adjust(EXAM_LENSES_FRAME.getLabel(), "Plan A (12/24/24)").resolveLinks()));
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String quoteId = String.format("%s_%s_%s Insured", quoteNumber, A_LA_CARTE, VISION);

        LOGGER.info("Step#1 verification");
        ResponseContainer<SalesforceQuoteModel> responseQuote = getResponseQuote(quoteId);
        assertThat(responseQuote.getModel().getPlan()).isEqualTo("Plan A (12-24-24)");
    }
}
