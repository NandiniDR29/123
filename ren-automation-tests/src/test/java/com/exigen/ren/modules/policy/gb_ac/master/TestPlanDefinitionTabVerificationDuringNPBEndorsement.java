package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.common.StartEndorsementNPBInfoActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.common.StartEndorsementNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionEndorseNPBInfoActionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import jersey.repackaged.com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.enums.NavigationEnum.PlanGenericInfoTab.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccidentSelectionValues.*;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyTransactionHistoryTable.*;
import static com.exigen.ren.main.enums.ProductConstants.TransactionHistoryType.NPB_ENDORSEMENT;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionEndorseNPBInfoActionTabMetaData.EligibilityMetadata.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPlanDefinitionTabVerificationDuringNPBEndorsement extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-35968"}, component = POLICY_GROUPBENEFITS)
    public void testPlanDefinitionTabVerificationDuringNPBEndorsement() {
        LOGGER.info("General Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.createPolicy(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_AllPlans")
                .adjust(tdSpecific().getTestData("TestData").resolveLinks().resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Steps#1, 2, 3 execution");
        groupAccidentMasterPolicy.endorseNPBInfo().start();
        groupAccidentMasterPolicy.endorseNPBInfo().getWorkspace()
                .fillUpTo(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT_NPB_INFO, TestDataKey.DEFAULT_TEST_DATA_KEY)
                        .adjust(TestData.makeKeyPath(StartEndorsementNPBInfoActionTab.class.getSimpleName(), StartEndorsementNPBInfoActionTabMetaData.ENDORSEMENT_DATE.getLabel()),
                                TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY)), PlanDefinitionEndorseNPBInfoActionTab.class, false);
        CustomSoftAssertions.assertSoftly(softly -> {

            LOGGER.info("Step#4 verification");
            ImmutableList.of(BASIC_BENEFITS.get(), ENHANCED_BENEFITS_A_TO_C.get(),
                    ENHANCED_BENEFITS_D_TO_F.get(), ENHANCED_BENEFITS_M_TO_T.get(),
                    ENHANCED_BENEFITS_H_TO_L.get(), "Sponsor/Participant Funding Structure", "Age Termination")
                    .forEach(tabName -> softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(tabName))).isAbsent());

            LOGGER.info("Step#5 verification");
            softly.assertThat(PlanDefinitionEndorseNPBInfoActionTab.planTable.getRowContains(TableConstants.CoverageDefinition.PLAN.getName(), PLAN_BASE_BUY_UP)).isPresent();
            softly.assertThat(PlanDefinitionEndorseNPBInfoActionTab.planTable.getRowContains(TableConstants.CoverageDefinition.PLAN.getName(), PLAN_ENHANCED_10_UNITS)).isPresent();
            softly.assertThat(PlanDefinitionEndorseNPBInfoActionTab.planTable.getRowContains(TableConstants.CoverageDefinition.PLAN.getName(), PLAN_VOLUNTARY_10_UNITS)).isPresent();

            LOGGER.info("Step#6 verification");
            AbstractContainer<?, ?> assetListPlanDefinition = groupAccidentMasterPolicy.endorseNPBInfo().getWorkspace().getTab(PlanDefinitionEndorseNPBInfoActionTab.class).getAssetList();
            assetListPlanDefinition.getAssets(COVERAGE_NAME, PLAN, SIC_CODE, SIC_DESCRIPTION, CENSUS_TYPE, COVERAGE_TIERS, NUMBER_OF_UNITS, COVERAGE_BASIS)
                    .forEach(control -> softly.assertThat(control).isPresent().isDisabled());

            LOGGER.info("Step#7 verification");
            softly.assertThat(assetListPlanDefinition.getAsset(PLAN_NAME)).isPresent().isEnabled();

            LOGGER.info("Step#10 verification");
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT)).isPresent().isDisabled();

            LOGGER.info("Step#12 verification");
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(ELIGIBILITY_WAITING_PERIOD_DEFINITION)).isPresent().isEnabled();
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD)).isPresent().isEnabled();
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_MODE)).isPresent().isEnabled();
            softly.assertThat(assetListPlanDefinition.getAsset(ELIGIBILITY).getAsset(WAITING_PERIOD_WAIVED_FOR_CURRENT_EMPLOYEES)).isPresent().isEnabled();

            LOGGER.info("Step#21 execution");
            PlanDefinitionEndorseNPBInfoActionTab.buttonIssue.click();
            softly.assertThat(Page.dialogConfirmation.labelHeader).hasValue("Please confirm NPBE action");
            softly.assertThat(Page.dialogConfirmation.labelMessage).hasValue("Are you sure you want to issue Non-Premium Bearing Endorsement?");

            LOGGER.info("Step#22 verification");
            Page.dialogConfirmation.confirm();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        });

        //Actions added for correct Endorse NPB Info transaction verification
        PolicySummaryPage.buttonTransactionHistory.click();
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(TYPE)).hasValue(NPB_ENDORSEMENT);
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(TYPE, NPB_ENDORSEMENT).getCell(TRAN_PREMIUM)).hasValue(String.valueOf(new Currency()));
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(TYPE, NPB_ENDORSEMENT).getCell(TRANSACTION_DATE)).hasValue(DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRowContains(TYPE, NPB_ENDORSEMENT).getCell(EFFECTIVE_DATE)).hasValue(DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY));
        });
    }
}
