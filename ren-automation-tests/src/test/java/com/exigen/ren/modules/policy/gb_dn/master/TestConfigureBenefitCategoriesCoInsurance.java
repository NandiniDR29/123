package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.controls.AutoCompleteBox;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.RadioGroup;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.*;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.*;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.ActionConstants.REMOVE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSelectionValues.*;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionIssueActionTabMetaData.INCLUDE_RETIREES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionIssueActionTabMetaData.PROCEDURE_CODE_CO_INSURANCE_OVERRIDE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES;
import static com.exigen.ren.main.pages.summary.SummaryPage.activitiesAndUserNotes;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithSpecificRole;
import static com.exigen.ren.utils.AdminActionsHelper.searchOrCreateRole;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;

public class TestConfigureBenefitCategoriesCoInsurance extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final AutoCompleteBox cbxProcedureCode = planDefinitionIssueActionTab.getAssetList().getAsset(PROCEDURE_CODE_CO_INSURANCE_OVERRIDE)
            .getAsset(PlanDefinitionIssueActionTabMetaData.ProcedureCodeCoInsuranceOverride.PROCEDURE_CODE);
    private static final ComboBox cbxCoInsuranceOutNetwork = planDefinitionIssueActionTab.getAssetList().getAsset(PROCEDURE_CODE_CO_INSURANCE_OVERRIDE)
            .getAsset(PlanDefinitionIssueActionTabMetaData.ProcedureCodeCoInsuranceOverride.CO_INSURANCE_OUT_NETWORK);
    private static final ComboBox cbxCoInsuranceInNetwork = planDefinitionIssueActionTab.getAssetList().getAsset(PROCEDURE_CODE_CO_INSURANCE_OVERRIDE)
            .getAsset(PlanDefinitionIssueActionTabMetaData.ProcedureCodeCoInsuranceOverride.CO_INSURANCE_IN_NETWORK);
    private static final ComboBox cbxCoInsuranceEpo = planDefinitionIssueActionTab.getAssetList().getAsset(PROCEDURE_CODE_CO_INSURANCE_OVERRIDE)
            .getAsset(PlanDefinitionIssueActionTabMetaData.ProcedureCodeCoInsuranceOverride.CO_INSURANCE_EPO);
    private static final RadioGroup rgOverrideProcedureCodeCoInsurance = planDefinitionIssueActionTab.getAssetList()
            .getAsset(PlanDefinitionIssueActionTabMetaData.OVERRIDE_PROCEDURE_CODE_CO_INSURANCE);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-29119", "REN-27223"}, component = POLICY_GROUPBENEFITS)
    public void testAttributesVerification() {
        TestData td = getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "NV")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", PlanDefinitionTabMetaData.PPO_EPO_PLAN.getLabel()), VALUE_YES)
                .adjust(PlanDefinitionIssueActionTab.class.getSimpleName(), tdSpecific().getTestData("PlanDefinitionIssueActionTab_TC1"));
        createQuoteAndProceedToPlanDefinitionIssueTab(td, true);
        assertSoftly(softly -> {
            LOGGER.info("REN-29119 Step#11 verification");
            softly.assertThat(rgOverrideProcedureCodeCoInsurance).isPresent().isEnabled().isRequired().hasValue(VALUE_NO);
            softly.assertThat(planDefinitionIssueActionTab.getAssetList().getAsset(PROCEDURE_CODE_CO_INSURANCE_OVERRIDE)).isAbsent();

            rgOverrideProcedureCodeCoInsurance.setValue(VALUE_YES);

            softly.assertThat(cbxProcedureCode).isPresent().isEnabled().isRequired().hasValue(EMPTY);
            softly.assertThat(cbxCoInsuranceOutNetwork).isPresent().isEnabled().isOptional().hasValue(EMPTY);
            softly.assertThat(cbxCoInsuranceInNetwork).isPresent().isEnabled().isOptional().hasValue(EMPTY);
            softly.assertThat(cbxCoInsuranceEpo).isPresent().isEnabled().isOptional().hasValue(EMPTY);

            LOGGER.info("REN-27223 Step#8 verification");
            cbxCoInsuranceInNetwork.setValue("50%");
            cbxCoInsuranceOutNetwork.setValue("85%");
            cbxProcedureCode.setValue("D1110");
            planDefinitionIssueActionTab.getAssetList().getAsset(INCLUDE_RETIREES).setValue(VALUE_NO);
            planDefinitionIssueActionTab.submitTab();
            softly.assertThat(cbxCoInsuranceOutNetwork).hasWarningWithText("State Requirement: Co-Insurance - Out of Network must be within 30% of Co-Insurance - In Network.");

            groupDentalMasterPolicy.issue().getWorkspace().fillFromTo(td, PlanDefinitionIssueActionTab.class, BillingAccountTab.class, true);
            groupDentalMasterPolicy.issue().getWorkspace().getTab(BillingAccountTab.class).submitTab();

            String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

            LOGGER.info("REN-29534 Step 15");
            groupDentalMasterPolicy.updateProcedureCode().start();
            LOGGER.info("REN-29119 Step#14 verification");
            softly.assertThat(groupDentalMasterPolicy.updateProcedureCode().getWorkspace().getTab(StartUpdateProcedureCodeTab.class).getAssetList().getAsset(StartUpdateProcedureCodeTabMetaData.EFFECTIVE_DATE))
                    .isPresent().isEnabled().isRequired().hasValue(EMPTY);
            softly.assertThat(Tab.buttonOk).isPresent().isEnabled();
            softly.assertThat(Tab.buttonCancel).isPresent().isEnabled();
            Tab.buttonCancel.click();

            LOGGER.info("REN-29534 Step 24");
            groupDentalMasterPolicy.rateGuaranteeRenew().start();
            groupDentalMasterPolicy.rateGuaranteeRenew().getWorkspace().getTab(StartRateGuaranteeRenewalTab.class).getAssetList()
                    .getAsset(StartRateGuaranteeRenewalTabMetaData.RENEWAL_EFFECTIVE_DATE).setValue(getDefaultDNMasterPolicyData().getValue(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()));
            groupDentalMasterPolicy.rateGuaranteeRenew().submit();
            Tab.buttonSaveAndExit.click();

            LOGGER.info("REN-29534 Step 25");
            groupDentalMasterPolicy.updateProcedureCode().start();
            softly.assertThat(dialogConfirmation.labelMessage).isPresent().hasValue("Pended Rate Guarantee Renewal exists. The pended transaction must be completed, deleted, or archived before you can proceed with the Update Procedure Code.");
            dialogConfirmation.buttonCancel.click();

            PolicySummaryPage.buttonPendedRateGuaranteeRenewal.click();
            groupDentalMasterPolicy.deletePendedTransaction().perform(new SimpleDataProvider());
            softly.assertThat(PolicySummaryPage.buttonPendedRateGuaranteeRenewal).isDisabled();

            groupDentalMasterPolicy.updateProcedureCode().start();
            TextBox txtUpdateProcedureCodeEffectiveDate = groupDentalMasterPolicy.updateProcedureCode().getWorkspace().getTab(StartUpdateProcedureCodeTab.class).getAssetList()
                    .getAsset(StartUpdateProcedureCodeTabMetaData.EFFECTIVE_DATE);

            LOGGER.info("REN-29534 Step 32");
            txtUpdateProcedureCodeEffectiveDate.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusMonths(1).format(MM_DD_YYYY));
            softly.assertThat(txtUpdateProcedureCodeEffectiveDate.getWarning())
                    .hasValue("Procedure Code Update should become effective on the start of a Policy Year defined by the Current Policy Year Start Date; Update Procedure Code Effective Date date should be greater than or equal to Policy Effective Date");

            LOGGER.info("REN-29534 Step 34");
            txtUpdateProcedureCodeEffectiveDate.setValue(TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).plusDays(1).format(MM_DD_YYYY));
            softly.assertThat(txtUpdateProcedureCodeEffectiveDate.getWarning())
                    .hasValue("Procedure Code Update should become effective on the start of a Policy Year defined by the Current Policy Year Start Date");

            LOGGER.info("REN-29534 Step 38");
            groupDentalMasterPolicy.updateProcedureCode().getWorkspace().fillUpTo(tdSpecific()
                    .getTestData("UpdateProcedureCode"), UpdateProcedureCodePlanDefinitionTab.class, true);
            groupDentalMasterPolicy.updateProcedureCode().getWorkspace().getTab(UpdateProcedureCodePlanDefinitionTab.class).submitTab();
            activitiesAndUserNotes.expand();
            softly.assertThat(activitiesAndUserNotes.getRow(DESCRIPTION, String.format("Process Procedure Code update for Policy %s", policyNumber))).isPresent();

        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-29129"}, component = POLICY_GROUPBENEFITS)
    public void testRuleVerificationIfOverrideProcedureCodeCoInsuranceNo() {
        TestData td = tdSpecific().getTestData("TestData_TC2")
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "FL")
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
        createQuoteAndProceedToPlanDefinitionIssueTab(td, false);
        assertSoftly(softly -> {
            LOGGER.info("REN-29129 Step#7 verification");
            ImmutableList.of(PLAN_ALACARTE, PLAN_GRAD_GRADUATED).forEach(
                    plan -> {
                        PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), plan).getCell(6).controls.links.get(CHANGE).click();
                        softly.assertThat(rgOverrideProcedureCodeCoInsurance).isDisabled().hasValue(VALUE_NO);
                    });
            PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), PLAN_DHMO).getCell(6).controls.links.get(CHANGE).click();
            softly.assertThat(rgOverrideProcedureCodeCoInsurance).isAbsent();
            softly.assertThat(planDefinitionIssueActionTab.getAssetList().getAsset(PROCEDURE_CODE_CO_INSURANCE_OVERRIDE)).isAbsent();
            String quoteNumber = QuoteSummaryPage.getQuoteNumber();

            LOGGER.info("REN-29129 Step#8 verification");
            Tab.buttonSaveAndExit.click();
            mainApp().reopen();
            MainPage.QuickSearch.search(quoteNumber);
            groupDentalMasterPolicy.issue().start();
            groupDentalMasterPolicy.issue().getWorkspace().fillUpTo(td, PlanDefinitionIssueActionTab.class, false);
            PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), PLAN_ALACARTE).getCell(6).controls.links.get(CHANGE).click();
            softly.assertThat(rgOverrideProcedureCodeCoInsurance).isEnabled().hasValue(VALUE_NO);
            PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), PLAN_GRAD_GRADUATED).getCell(6).controls.links.get(CHANGE).click();
            softly.assertThat(rgOverrideProcedureCodeCoInsurance).isDisabled().hasValue(VALUE_NO);
            PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), PLAN_DHMO).getCell(6).controls.links.get(CHANGE).click();
            softly.assertThat(rgOverrideProcedureCodeCoInsurance).isAbsent();
            softly.assertThat(planDefinitionIssueActionTab.getAssetList().getAsset(PROCEDURE_CODE_CO_INSURANCE_OVERRIDE)).isAbsent();
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-29262"}, component = POLICY_GROUPBENEFITS)
    public void testRuleVerification_TC3() {
        TestData td = tdSpecific().getTestData("TestData_ALACARTE_TripleAdvantage")
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
        createQuoteAndProceedToPlanDefinitionIssueTab(td, true);
        assertSoftly(softly -> {
            LOGGER.info("REN-29262 Step#8 verification");
            ImmutableList.of(PLAN_ALACARTE, PLAN_TRIPLE_ADVANTAGE).forEach(
                    plan -> {
                        PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), plan).getCell(6).controls.links.get(CHANGE).click();
                        rgOverrideProcedureCodeCoInsurance.setValue(VALUE_YES);
                        softly.assertThat(cbxProcedureCode).isPresent().isEnabled().isRequired();
                        softly.assertThat(cbxCoInsuranceOutNetwork).isPresent().isEnabled().isOptional();
                        softly.assertThat(cbxCoInsuranceInNetwork)
                                .isPresent().isEnabled().isOptional();
                        softly.assertThat(planDefinitionIssueActionTab.getAssetList().getAsset(PROCEDURE_CODE_CO_INSURANCE_OVERRIDE).getAsset(PlanDefinitionIssueActionTabMetaData.ProcedureCodeCoInsuranceOverride.ADD_CODE))
                                .isPresent().isEnabled();
                        planDefinitionIssueActionTab.getAssetList().getAsset(PROCEDURE_CODE_CO_INSURANCE_OVERRIDE).getAsset(PlanDefinitionIssueActionTabMetaData.ProcedureCodeCoInsuranceOverride.ADD_CODE).click();
                        softly.assertThat(PlanDefinitionIssueActionTab.tableProcedureCodeCoInsuranceOverride.getRowsCount()).isEqualTo(2);
                        PlanDefinitionIssueActionTab.tableProcedureCodeCoInsuranceOverride.getRows().forEach(
                                row -> {
                                    softly.assertThat(row.getCell(6).controls.links.get(CHANGE)).isPresent().isEnabled();
                                    softly.assertThat(row.getCell(6).controls.links.get(REMOVE)).isPresent().isEnabled();
                                }
                        );
                    });
            softly.assertThat(cbxCoInsuranceEpo).isPresent().isEnabled().isOptional();
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-29512"}, component = POLICY_GROUPBENEFITS)
    public void testListValuesVerificationCoInsuranceEPO() {
        TestData td = tdSpecific().getTestData("TestData_ALACARTE_TripleAdvantage")
                .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[1]", PlanDefinitionTabMetaData.PPO_EPO_PLAN.getLabel()), VALUE_YES)
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
        createQuoteAndProceedToPlanDefinitionIssueTab(td, true);
        assertSoftly(softly -> {
            LOGGER.info("REN-29512 Steps#9-10 verification");
            ImmutableList.of(PLAN_ALACARTE, PLAN_TRIPLE_ADVANTAGE).forEach(
                    plan -> {
                        PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), plan).getCell(6).controls.links.get(CHANGE).click();
                        rgOverrideProcedureCodeCoInsurance.setValue(VALUE_YES);
                        softly.assertThat(cbxCoInsuranceEpo).hasOptions("", "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%",
                                "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%");
                    });
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-29595"}, component = POLICY_GROUPBENEFITS)
    public void testListValuesVerification_AK() {
        TestData td = tdSpecific().getTestData("TestData_AK")
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "AK")
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
        LOGGER.info("REN-29595 Steps#1-3");
        createQuoteAndProceedToPlanDefinitionIssueTab(td, true);
        assertSoftly(softly -> {
            LOGGER.info("REN-29595 Steps#8-9 verification");
            ImmutableList.of(PLAN_BASEPOS, PLAN_FLEX_PLUS, PLAN_MAJEPOS).forEach(
                    plan -> {
                        PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), plan).getCell(6).controls.links.get(CHANGE).click();
                        rgOverrideProcedureCodeCoInsurance.setValue(VALUE_YES);
                        softly.assertThat(cbxCoInsuranceInNetwork).hasOptions("", "25%", "30%", "35%", "40%", "45%", "50%",
                                "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%");
                        softly.assertThat(cbxCoInsuranceOutNetwork).hasOptions("", "25%", "30%", "35%", "40%", "45%", "50%",
                                "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%");
                    });
            PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), PLAN_ALACARTE).getCell(6).controls.links.get(CHANGE).click();
            rgOverrideProcedureCodeCoInsurance.setValue(VALUE_YES);
            softly.assertThat(cbxCoInsuranceInNetwork).hasOptions("", "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%",
                    "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%");
            softly.assertThat(cbxCoInsuranceOutNetwork).hasOptions("", "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%",
                    "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%");
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-29595"}, component = POLICY_GROUPBENEFITS)
    public void testListValuesVerification_WA() {
        TestData td = tdSpecific().getTestData("TestData_WA")
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "WA")
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel()), "50")
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
        LOGGER.info("REN-29595 Steps#16-18");
        createQuoteAndProceedToPlanDefinitionIssueTab(td, true);
        assertSoftly(softly -> {
            LOGGER.info("REN-29595 Step#19 verification");
            ImmutableList.of(PLAN_WA_ONE, PLAN_WA_TWO, PLAN_WA_THREE, PLAN_WA_EIGHT, PLAN_WA_NINE).forEach(
                    plan -> {
                        PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), plan).getCell(6).controls.links.get(CHANGE).click();
                        rgOverrideProcedureCodeCoInsurance.setValue(VALUE_YES);
                        softly.assertThat(cbxCoInsuranceInNetwork).hasOptions("", "80%");
                        softly.assertThat(cbxCoInsuranceOutNetwork).hasOptions("", "80%");
                    });
            ImmutableList.of(PLAN_WA_FOUR, PLAN_WA_FIVE, PLAN_WA_SIX, PLAN_WA_SEVEN).forEach(
                    plan -> {
                        PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), plan).getCell(6).controls.links.get(CHANGE).click();
                        rgOverrideProcedureCodeCoInsurance.setValue(VALUE_YES);
                        softly.assertThat(cbxCoInsuranceInNetwork).hasOptions("", "90%");
                        softly.assertThat(cbxCoInsuranceOutNetwork).hasOptions("", "80%");
                    });
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-29595"}, component = POLICY_GROUPBENEFITS)
    public void testListValuesVerification_ASO() {
        TestData td = tdSpecific().getTestData("TestData_ASO")
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), ASO_PLAN.getLabel()), "Yes")
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY));
        createQuoteAndProceedToPlanDefinitionIssueTab(td, true);
        assertSoftly(softly -> {
            LOGGER.info("REN-29595 Step#40 verification");
            ImmutableList.of(PLAN_ASO, ASOALC_ASO_ALACARTE).forEach(
                    plan -> {
                        PlanDefinitionIssueActionTab.tablePlanSelection.getRow(TableConstants.Plans.PLAN.getName(), plan).getCell(6).controls.links.get(CHANGE).click();
                        rgOverrideProcedureCodeCoInsurance.setValue(VALUE_YES);
                        softly.assertThat(cbxCoInsuranceInNetwork).hasOptions("", "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%",
                                "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%");
                        softly.assertThat(cbxCoInsuranceOutNetwork).hasOptions("", "0%", "5%", "10%", "15%", "20%", "25%", "30%", "35%", "40%", "45%", "50%",
                                "55%", "60%", "65%", "70%", "75%", "80%", "85%", "90%", "95%", "100%");
                    });
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-27223"}, component = POLICY_GROUPBENEFITS)
    public void testRuleVerification_TC5_LA() {
        TestData td = getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "LA")
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY))
                .adjust(PlanDefinitionIssueActionTab.class.getSimpleName(), tdSpecific().getTestData("PlanDefinitionIssueActionTab_TC5_LA"));
        createQuoteAndProceedToPlanDefinitionIssueTab(td, true);
        LOGGER.info("REN-27223 Step#16 verification");
        planDefinitionIssueActionTab.fillTab(td);
        planDefinitionIssueActionTab.submitTab();
        assertThat(cbxCoInsuranceOutNetwork).hasWarningWithText("State Requirement: Co-Insurance - Out of Network must be equal to Co-Insurance - In Network.");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-27223"}, component = POLICY_GROUPBENEFITS)
    public void testRuleVerification_TC5_MD() {
        TestData td = getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "MD")
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY))
                .adjust(PlanDefinitionIssueActionTab.class.getSimpleName(), tdSpecific().getTestData("PlanDefinitionIssueActionTab_TC5_MD"));
        createQuoteAndProceedToPlanDefinitionIssueTab(td, true);
        LOGGER.info("REN-27223 Step#24 verification");
        planDefinitionIssueActionTab.fillTab(td);
        planDefinitionIssueActionTab.submitTab();
        assertThat(cbxCoInsuranceOutNetwork).hasWarningWithText("State Requirement: Co-Insurance - Out of Network must be within 20% of Co-Insurance - In Network.");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-27223"}, component = POLICY_GROUPBENEFITS)
    public void testRuleVerification_TC5_KY() {
        TestData td = getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), SITUS_STATE.getLabel()), "KY")
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY))
                .adjust(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, DEFAULT_TEST_DATA_KEY))
                .adjust(PlanDefinitionIssueActionTab.class.getSimpleName(), tdSpecific().getTestData("PlanDefinitionIssueActionTab_TC5_KY"));
        createQuoteAndProceedToPlanDefinitionIssueTab(td, true);
        LOGGER.info("REN-27223 Step#32 verification");
        planDefinitionIssueActionTab.fillTab(td);
        planDefinitionIssueActionTab.submitTab();
        assertThat(cbxCoInsuranceOutNetwork).hasWarningWithText("State Requirement: Co-Insurance - Out of Network must be within 25% of Co-Insurance - In Network.");
    }

    private void createQuoteAndProceedToPlanDefinitionIssueTab(TestData td, boolean isUserWithUpdateProcedureCodePrivilege) {
        if (isUserWithUpdateProcedureCodePrivilege) {
            mainApp().open();
        }
        else {
            LOGGER.info("User creation without 'Update Procedure Code' privilege");
            String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                    .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                            ImmutableList.of("ALL", "EXCLUDE " + Privilege.UPDATE_PROCEDURE_CODE.get())), roleCorporate);
            createUserWithSpecificRole(roleName, profileCorporate);
        }
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.createQuote(td);
        groupDentalMasterPolicy.propose().perform(td);
        groupDentalMasterPolicy.acceptContract().perform(td);
        groupDentalMasterPolicy.issue().start();
        groupDentalMasterPolicy.issue().getWorkspace().fillUpTo(td, PlanDefinitionIssueActionTab.class, false);
    }

}
