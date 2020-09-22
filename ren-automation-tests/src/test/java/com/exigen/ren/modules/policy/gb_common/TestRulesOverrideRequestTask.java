package com.exigen.ren.modules.policy.gb_common;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.profile.metadata.AuthorityLevelsMetaData;
import com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.metadata.CreateTaskActionTabMetaData;
import com.exigen.ren.main.modules.mywork.tabs.CreateTaskActionTab;
import com.exigen.ren.main.modules.mywork.tabs.MyWorkTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AGENCY_LOCATIONS;
import static com.exigen.ren.admin.modules.security.profile.metadata.GeneralProfileMetaData.AddAgencyMetaData.AGENCY_NAME;
import static com.exigen.ren.main.enums.PolicyConstants.PlanSTD.CON;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.buttonOverrideRules;
import static com.exigen.ren.main.modules.mywork.metadata.MyWorkTabMetaData.ALL_QUEUES;
import static com.exigen.ren.main.modules.mywork.metadata.MyWorkTabMetaData.MY_WORK_TASKS;
import static com.exigen.ren.main.modules.mywork.metadata.MyWorkTabMetaData.MyWorkTasksMetaData.QUEUE;
import static com.exigen.ren.main.modules.mywork.tabs.MyWorkTab.MyWorkTasks.REFERENCE_ID;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_SCHEDULE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAX_MONTHLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetadata.MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.GROUP_IS_AN_ASSOCIATION;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRulesOverrideRequestTask extends BaseTest implements RoleContext, ProfileContext, CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext, GroupVisionMasterPolicyContext, MyWorkContext {

    private static final String LOGIN_KEY = "Login";
    private static final String PASSWORD_KEY = "Password";

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-24011", component = POLICY_GROUPBENEFITS)
    public void testRulesOverrideRequestTaskQuoteRate() {
        Map<String, String> credentials = preconditionsAdmin();
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), PlanDefinitionTab.class, false);
        LongTermDisabilityMasterPolicyContext.planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(CON));

        LOGGER.info("REN-24011 TC1 Step 1");
        LongTermDisabilityMasterPolicyContext.planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(MAX_MONTHLY_BENEFIT_AMOUNT).setValue("$7,000");
        assertThat(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getAssetList().getAsset(BENEFIT_SCHEDULE).getAsset(MAX_MONTHLY_BENEFIT_AMOUNT)).hasWarningWithText("Benefit Amount over $6,000 requires Underwriter approval");

        LOGGER.info("REN-24011 TC1 Step 2");
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultLTDMasterPolicyData().mask(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName() + "[0]", "Plan"))
                        .adjust(TestData.makeKeyPath(PlanDefinitionTab.class.getSimpleName()), longTermDisabilityMasterPolicy.getDefaultTestData(DATA_GATHER, "PlanDefinitionTab_CON")),
                PlanDefinitionTab.class, PremiumSummaryTab.class, true);
        Tab.buttonTopSave.click();
        LongTermDisabilityMasterPolicyContext.premiumSummaryTab.rate();
        Row errorRow = ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), "Benefit Amount over $6,000 requires Underwriter approval");
        assertThat(errorRow).exists();

        LOGGER.info("REN-24011 TC1 Step 3");
        errorRow.getCell(ErrorPage.TableError.APPROVAL.getName()).controls.checkBoxes.getFirst().setValue(true);
        errorRow.getCell(ErrorPage.TableError.DURATION.getName()).controls.radioGroups.getFirst().setValueContains("Term");
        errorRow.getCell(ErrorPage.TableError.REASON_FOR_OVERRIDE.getName()).controls.comboBoxes.getFirst().setValueByIndex(1);
        ErrorPage.buttonReferForApproval.click();
        assertThat(PremiumSummaryTab.premiumSummaryCoveragesTable.getRow(1).getCell(1)).hasValue("No premium information is available.");

        LOGGER.info("REN-24011 TC1 Step 4");
        Tab.buttonSaveAndExit.click();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
        mainApp().reopen(credentials.get(LOGIN_KEY), credentials.get(PASSWORD_KEY));

        LOGGER.info("REN-24011 TC1 Step 5");
        assertApprovalRequestDisplayedInQueue(policyNum);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-24011", component = POLICY_GROUPBENEFITS)
    public void testRulesOverrideRequestTaskPropose() {
        Map<String, String> credentials = preconditionsAdmin();
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData().adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), GROUP_IS_AN_ASSOCIATION.getLabel()), VALUE_NO),
                GroupVisionMasterPolicyContext.planDefinitionTab.getClass(), true);

        LOGGER.info("REN-24011 TC2 Step 1");
        GroupVisionMasterPolicyContext.planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK).setValue("12");
        assertThat(GroupVisionMasterPolicyContext.planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.ELIGIBILITY).getAsset(MINIMUM_HOURLY_REQUIREMENT_HOURS_PER_WEEK))
                .hasWarningWithText("Proposal will require Underwriter approval because Minimum Hourly Requirement (hours per week) is 24 hours or fewer.");

        LOGGER.info("REN-24011 TC2 Step 2");
        GroupVisionMasterPolicyContext.planDefinitionTab.submitTab();
        groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), ClassificationManagementTab.class, GroupVisionMasterPolicyContext.premiumSummaryTab.getClass());
        GroupVisionMasterPolicyContext.premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("REN-24011 TC2 Step 3");
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();
        groupVisionMasterPolicy.propose().start();
        groupVisionMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), ProposalActionTab.class, false);
        buttonOverrideRules.click();
        Row errorRow = ProposalActionTab.tableErrorsList.getRow(TableConstants.OverrideErrorsTable.MESSAGE.getName(), "\"Proposal requires Underwriter approval because Minimum Hourly Requirement (hours per week) is 24 hours or fewer.\"");
        errorRow.getCell(TableConstants.OverrideErrorsTable.APPROVAL.getName()).controls.checkBoxes.getFirst().setValue(true);
        errorRow.getCell(TableConstants.OverrideErrorsTable.DURATION.getName()).controls.radioGroups.getFirst().setValue("Term");
        errorRow.getCell(TableConstants.OverrideErrorsTable.REASON_FOR_OVERRIDE.getName()).controls.comboBoxes.getFirst().setValueByIndex(1);
        ProposalActionTab.buttonReferForApproval.click();

        MainPage.QuickSearch.search(policyNum);
        Page.dialogConfirmation.confirm();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(PREMIUM_CALCULATED);

        LOGGER.info("REN-24011 TC2 Step 4");
        mainApp().reopen(credentials.get(LOGIN_KEY), credentials.get(PASSWORD_KEY));

        LOGGER.info("REN-24011 TC2 Step 5");
        assertApprovalRequestDisplayedInQueue(policyNum);
    }

    private Map<String, String> preconditionsAdmin() {
        TestData tdCorporateRole = roleCorporate.defaultTestData();
        TestData tdSecurityProfile = profileCorporate.defaultTestData();
        adminApp().open();
        String roleName = tdCorporateRole.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, generalRoleTab.getMetaKey(), GeneralRoleMetaData.ROLE_NAME.getLabel());

        roleCorporate.create(tdCorporateRole.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                generalRoleTab.getMetaKey(), GeneralRoleMetaData.PRIVILEGES.getLabel()), Arrays.asList("Access UW Approvals Queue", "Control UW Approvals Queue")));

        Map<String,String> credentials = new HashMap<>();
        credentials.put(LOGIN_KEY, tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, generalProfileTab.getMetaKey(), GeneralProfileMetaData.USER_LOGIN.getLabel()));
        credentials.put(PASSWORD_KEY, tdSecurityProfile.getValue(DATA_GATHER, DEFAULT_TEST_DATA_KEY, generalProfileTab.getMetaKey(), GeneralProfileMetaData.PASSWORD.getLabel()));

        profileCorporate.create(tdSecurityProfile.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AGENCY_LOCATIONS.getLabel(), AGENCY_NAME.getLabel()), "QA Agency")
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), GeneralProfileMetaData.ROLES.getLabel()), Arrays.asList(roleName, "QA All"))
                .adjust(TestData.makeKeyPath(generalProfileTab.getMetaKey(), AuthorityLevelsMetaData.LEVEL.getLabel()), "Level 6"));

        return credentials;
    }

    @Test(groups = {GB, GB_PRECONFIGURED, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-24203", component = POLICY_GROUPBENEFITS)
    public void testRulesOverrideRequestTaskMyWorkTab() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        createDefaultLongTermDisabilityMasterPolicy();
        String policyNum = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("REN-24203 Step 1");
       navigateToCustomer();
        myWork.createTask().perform(myWork.defaultTestData().getTestData("CreateTaskAction", DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CreateTaskActionTab.class.getSimpleName(), CreateTaskActionTabMetaData.TASK_NAME.getLabel()), "Rules Override Request")
                .adjust(TestData.makeKeyPath(CreateTaskActionTab.class.getSimpleName(), CreateTaskActionTabMetaData.REFERENCE_ID.getLabel()), policyNum));
        assertThat(CustomerSummaryPage.labelCustomerName).isPresent();

        LOGGER.info("REN-24203 Step 2");
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.MY_WORK);
        assertApprovalRequestDisplayedInQueue(policyNum);
    }

    private void assertApprovalRequestDisplayedInQueue(String policyNum) {
        myWorkTab.getAssetList().getAsset(MY_WORK_TASKS).getAsset(QUEUE).setValue("UW Approvals");
        myWorkTab.getAssetList().getAsset(ALL_QUEUES).click();
        assertThat(MyWorkTab.tableMyWorkTasks.findRow(REFERENCE_ID.getName(), policyNum)).exists();
    }

}
