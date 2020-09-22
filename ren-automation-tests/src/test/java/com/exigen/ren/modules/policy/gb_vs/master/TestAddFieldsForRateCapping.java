package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.USER_10_LOGIN;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.buttonOverrideRules;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.tableErrorsList;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddFieldsForRateCapping extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20390"}, component = POLICY_GROUPBENEFITS)
    public void testAddFieldsForRateCapping() {
        mainApp().open(USER_10_LOGIN, "qa");
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN-20390 Step 1-3");
            planDefinitionTab.selectDefaultPlan();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP)).isPresent().hasValue("None").hasOptions("None", "2nd Year", "2nd Year & 3rd Year");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP).setValue("2nd Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP)).hasWarningWithText("Warning:Proposal will require Underwriter approval because Master Quote contains Rate Capping");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP).setValue("2nd Year & 3rd Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP)).hasWarningWithText("Warning:Proposal will require Underwriter approval because Master Quote contains Rate Capping");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP).setValue("None");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP)).hasNoWarning();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP).setValue("2nd Year");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.SECOND_YEAR_CAP_PCT).setValue("10");
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
            groupVisionMasterPolicy.propose().start();
            QuotesSelectionActionTab.selectQuote(ImmutableList.of(1));
            QuotesSelectionActionTab.textBoxProposalName.setValue("PreProposalName");
            Tab.buttonNext.click();
            buttonOverrideRules.click();
            tableErrorsList.getRows().forEach(row -> {
                softly.assertThat(tableErrorsList.getRow(row.getIndex()).getCell(TableConstants.OverrideErrorsTable.OVERRIDE.getName()).controls.checkBoxes.getFirst()).isDisabled();
            });
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20410", "REN-20411"}, component = POLICY_GROUPBENEFITS)
    public void testSecondThirdYearCap() {
        mainApp().reopen();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN-20410 Step 1 and Step 2");
            planDefinitionTab.selectDefaultPlan();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP).setValue("None");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.SECOND_YEAR_CAP_PCT)).isAbsent();
            LOGGER.info("REN-20410 Step 6 step 7 and Step 8");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP).setValue("2nd Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.SECOND_YEAR_CAP_PCT)).isPresent().isRequired();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP).setValue("2nd Year & 3rd Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.SECOND_YEAR_CAP_PCT)).isPresent().isRequired().hasValue("");

            LOGGER.info("REN-20411 Step 1, Step 2 and Step 5");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP).setValue("None");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.THIRD_YEAR_CAP_PCT)).isAbsent();
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP).setValue("2nd Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.THIRD_YEAR_CAP_PCT)).isAbsent();
            LOGGER.info("REN-20411 Step 9 and Step 10");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.RATE_CAP).setValue("2nd Year & 3rd Year");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.THIRD_YEAR_CAP_PCT)).isPresent().isRequired().hasValue("");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.SECOND_YEAR_CAP_PCT).setValue("20");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetadata.THIRD_YEAR_CAP_PCT).setValue("20");

            LOGGER.info("REN-20410 Step 20 and Step 21 and REN-20411 Step 22 and Step 2");
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        });
    }
}