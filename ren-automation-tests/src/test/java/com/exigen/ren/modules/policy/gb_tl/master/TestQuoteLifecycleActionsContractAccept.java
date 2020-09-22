/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.main.enums.MyWorkConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.main.modules.mywork.actions.CompleteTaskAction;
import com.exigen.ren.main.modules.mywork.tabs.CompleteTaskActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.exigen.ren.utils.AdminActionsHelper;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.main.modules.mywork.metadata.CompleteTaskActionTabMetaData.CHOICE;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.tableTasks;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteLifecycleActionsContractAccept extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext, MyWorkContext {

    private TestData tdCorporateRole = roleCorporate.defaultTestData();
    private TestData tdSecurityProfile = profileCorporate.defaultTestData();
    private String roleForUpdate;
    private static final String TASK_NAME_AGENT_APPOINTMENT_VERIFICATION = "Agent Appointment Verification";
    private static final String TASK_NAME_ENROLLMENT_PACKET = "Enrollment Packet";
    private static final String TASK_NAME_ENROLLMENT_RECEIPT_PENDING = "Enrollment Receipt Pending";
    private static final String TASK_NAME_INTAKE_VERIFICATION = "Intake Verification";
    private static final String TASK_NAME_UNDERWRITING_REVIEW = "Underwriting Review";
    private static final String TASK_NAME_CASE_INSTALLATION = "Case Installation";
    private static final String STATUS_ACTIVE = "Active";
    private static final String STATUS_COMPLETED = "Completed";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-15932", component = POLICY_GROUPBENEFITS)
    public void testQuoteLifecycleActionsContractAccept() {

        adminApp().open();

        User user = createUserWithPrivilege(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_AGENT_CARE_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_AGENT_CARE_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_SALES_SUPPORT_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_SALES_SUPPORT_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_CASE_INSTALLATION_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_CASE_INSTALLATION_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_UW_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_UW_QUEUE.get()));

        mainApp().reopen(user.getLogin(), user.getPassword());

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        termLifeInsuranceMasterPolicy.createQuote(getDefaultTLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("TEST: Issue Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());
        termLifeInsuranceMasterPolicy.propose().perform(getDefaultTLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);
        termLifeInsuranceMasterPolicy.acceptContract().perform(getDefaultTLMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("---=={Step 5}==---");
        LOGGER.info("Set role for Agent Care");
        updateRole(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_SALES_SUPPORT_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_SALES_SUPPORT_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_CASE_INSTALLATION_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_CASE_INSTALLATION_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_UW_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_UW_QUEUE.get()
        ));
        mainApp().reopen(user.getLogin(), user.getPassword());
        myWork.filterTask().perform(masterPolicyNumber, STATUS_ACTIVE, TASK_NAME_AGENT_APPOINTMENT_VERIFICATION);
        myWork.completeTask().perform(1, myWork.getDefaultTestData().getTestData(CompleteTaskAction.class.getSimpleName(), DEFAULT_TEST_DATA_KEY));
        myWork.filterTask().perform(masterPolicyNumber, STATUS_COMPLETED, TASK_NAME_AGENT_APPOINTMENT_VERIFICATION);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME_AGENT_APPOINTMENT_VERIFICATION)).isPresent();

        myWork.filterTask().perform(masterPolicyNumber, STATUS_ACTIVE, TASK_NAME_ENROLLMENT_PACKET);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME_ENROLLMENT_PACKET)).isAbsent();

        LOGGER.info("---=={Step 8}==---");
        LOGGER.info("Update role for Sales Support");
        updateRole(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_AGENT_CARE_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_AGENT_CARE_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_CASE_INSTALLATION_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_CASE_INSTALLATION_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_UW_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_UW_QUEUE.get()
        ));
        mainApp().reopen(user.getLogin(), user.getPassword());
        myWork.filterTask().perform(masterPolicyNumber, STATUS_ACTIVE, TASK_NAME_ENROLLMENT_PACKET);
        myWork.completeTask().perform(1, myWork.getDefaultTestData().getTestData(CompleteTaskAction.class.getSimpleName(), DEFAULT_TEST_DATA_KEY));
        myWork.filterTask().perform(masterPolicyNumber, STATUS_COMPLETED, TASK_NAME_ENROLLMENT_PACKET);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME_ENROLLMENT_PACKET)).isPresent();

        myWork.filterTask().perform(masterPolicyNumber, STATUS_ACTIVE, TASK_NAME_ENROLLMENT_RECEIPT_PENDING);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME_ENROLLMENT_RECEIPT_PENDING)).isAbsent();

        LOGGER.info("---=={Step 11}==---");
        LOGGER.info("Update role for Case Installation");
        updateRole(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_AGENT_CARE_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_AGENT_CARE_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_SALES_SUPPORT_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_SALES_SUPPORT_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_UW_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_UW_QUEUE.get()
        ));
        mainApp().reopen(user.getLogin(), user.getPassword());
        myWork.filterTask().perform(masterPolicyNumber, STATUS_ACTIVE, TASK_NAME_ENROLLMENT_RECEIPT_PENDING);
        myWork.completeTask().perform(1, myWork.getDefaultTestData().getTestData(CompleteTaskAction.class.getSimpleName(), DEFAULT_TEST_DATA_KEY));
        myWork.filterTask().perform(masterPolicyNumber, STATUS_COMPLETED, TASK_NAME_ENROLLMENT_RECEIPT_PENDING);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME_ENROLLMENT_RECEIPT_PENDING)).isPresent();

        myWork.filterTask().perform(masterPolicyNumber, STATUS_ACTIVE, TASK_NAME_INTAKE_VERIFICATION);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME_INTAKE_VERIFICATION)).isPresent();

        LOGGER.info("---=={Step 14}==---");
        LOGGER.info("Update role for Case Installation");
        mainApp().reopen(user.getLogin(), user.getPassword());

        myWork.filterTask().perform(masterPolicyNumber, STATUS_ACTIVE, TASK_NAME_INTAKE_VERIFICATION);
        myWork.completeTask().perform(1, myWork.getDefaultTestData().getTestData(CompleteTaskAction.class.getSimpleName(), DEFAULT_TEST_DATA_KEY));
        myWork.filterTask().perform(masterPolicyNumber, STATUS_COMPLETED, TASK_NAME_INTAKE_VERIFICATION);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME_INTAKE_VERIFICATION)).isPresent();

        myWork.filterTask().perform(masterPolicyNumber, STATUS_ACTIVE, TASK_NAME_CASE_INSTALLATION);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME_CASE_INSTALLATION)).isAbsent();

        LOGGER.info("---=={Step 17}==---");
        LOGGER.info("Update role for UW");
        updateRole(ImmutableList.of("ALL",
                "EXCLUDE " + Privilege.ACCESS_AGENT_CARE_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_AGENT_CARE_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_SALES_SUPPORT_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_SALES_SUPPORT_QUEUE.get(),
                "EXCLUDE " + Privilege.ACCESS_CASE_INSTALLATION_QUEUE.get(),
                "EXCLUDE " + Privilege.CONTROL_CASE_INSTALLATION_QUEUE.get()
        ));
        mainApp().reopen(user.getLogin(), user.getPassword());
        myWork.filterTask().perform(masterPolicyNumber, STATUS_ACTIVE, TASK_NAME_UNDERWRITING_REVIEW);
        myWork.completeTask().perform(1, myWork.getDefaultTestData().getTestData(CompleteTaskAction.class.getSimpleName(), DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CompleteTaskActionTab.class.getSimpleName(), CHOICE.getLabel()), "Sold"));
        myWork.filterTask().perform(masterPolicyNumber, STATUS_COMPLETED, TASK_NAME_UNDERWRITING_REVIEW);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME_UNDERWRITING_REVIEW)).isPresent();

        myWork.filterTask().perform(masterPolicyNumber, STATUS_ACTIVE, TASK_NAME_CASE_INSTALLATION);
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), TASK_NAME_CASE_INSTALLATION)).isAbsent();

    }

    private User createUserWithPrivilege(ImmutableList privilegeList) {

        roleForUpdate = AdminActionsHelper.searchOrCreateRole(tdCorporateRole.getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), privilegeList), roleCorporate);

        return AdminActionsHelper.createUserWithSpecificRole(roleForUpdate, profileCorporate);
    }

    private void updateRole(ImmutableList privilegeConfigList) {
        mainApp().close();
        adminApp().open();

        TestData td = tdCorporateRole.getTestData(DATA_GATHER, "TestData_NonSecureUser").adjust(TestData.makeKeyPath(
                GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()), privilegeConfigList);
        AdminActionsHelper.updateRole(td, roleForUpdate, roleCorporate);

        adminApp().close();
    }
}
