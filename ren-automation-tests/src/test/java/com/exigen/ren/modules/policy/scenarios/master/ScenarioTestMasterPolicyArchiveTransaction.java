package com.exigen.ren.modules.policy.scenarios.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.enums.ProductConstants.TransactionHistoryType.ENDORSEMENT;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.ArchivedTransactions.*;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithSpecificRole;
import static com.exigen.ren.utils.AdminActionsHelper.searchOrCreateRole;

public class ScenarioTestMasterPolicyArchiveTransaction extends BaseTest implements CustomerContext, CaseProfileContext {

    public void testPolicyArchiveTransaction(GroupBenefitsMasterPolicyType policyType, TestData tdPolicy) {

        String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.POLICY_ARCHIVE_TRANSACTION.get())), roleCorporate);
        createUserWithSpecificRole(roleName, profileCorporate);
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(policyType);
        GroupBenefitsMasterPolicy policy = policyType.get();
        policy.createPolicy(tdPolicy);
        String MP_number = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Step 1");
        PolicySummaryPage.expandArchivedTransactionsTable();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions).hasRows(1);
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions.getRow(1).getCell(TRANSACTION_TYPE.getName())).hasValue("No records found.");
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions.getHeader()).hasValue(ImmutableList.of(
                    TRANSACTION_TYPE.getName(), PREMIUM.getName(), STATUS.getName(), EFFECTIVE_DATE.getName(),
                    LAST_CHANGE_DATE.getName(), PRIMARY_BROKER.getName(), LAST_PERFORMER.getName()));
        });

        LOGGER.info("Step 2");
        policy.endorse().perform(tdPolicy);
        PolicySummaryPage.expandArchivedTransactionsTable();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions).hasRows(1);
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions.getRow(1).getCell(TRANSACTION_TYPE.getName())).hasValue("No records found.");
        });


        LOGGER.info("Step 5");
        mainApp().reopen();
        search(MP_number);
        PolicySummaryPage.buttonPendedEndorsement.click();
        assertThat(PolicySummaryPage.tableEndorsements).isPresent();

        LOGGER.info("Step 6, 7, 10");
        policy.archiveTransaction().start();
        assertThat(new StaticElement(By.id("policyDataGatherForm:archiveTransactionText"))).hasValue("Do you want to archive transaction?");
        policy.archiveTransaction().submit();

        LOGGER.info("Step 12");
        PolicySummaryPage.expandArchivedTransactionsTable();
        assertSoftly(softly -> {
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions).hasRows(1);
            softly.assertThat(PolicySummaryPage.tableArchivedTransactions.getRow(1).getCell(TRANSACTION_TYPE.getName())).hasValue(ENDORSEMENT);
        });
    }
}
