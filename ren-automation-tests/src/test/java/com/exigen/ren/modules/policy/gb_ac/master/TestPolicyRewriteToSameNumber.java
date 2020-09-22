/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BamConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.RewriteActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.RewriteActionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyRewriteToSameNumber extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24468", component = POLICY_GROUPBENEFITS)
    public void testPolicyRewriteToSameNumber() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());

        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicySelfAdminData());

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Cancellation Policy #" + policyNumber);
        groupAccidentMasterPolicy.cancel().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        CustomAssertions.assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(ActionConstants.ProductAction.CANCELLATION);

        LOGGER.info("TEST: Rewrite Policy #" + policyNumber);
        groupAccidentMasterPolicy.createRewrite(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.REWRITE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.REWRITE, TestDataKey.DATA_GATHER).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, "TestDataWithExistingBA").resolveLinks()));

        assertThat(NavigationPage.comboBoxListAction).containsOption(ActionConstants.ProductAction.CANCELLATION);
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(ActionConstants.ProductAction.REWRITE);

        String rewriteEffectiveDate = groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.REWRITE, TestDataKey.DEFAULT_TEST_DATA_KEY).getValue(RewriteActionTab.class.getSimpleName(),
                RewriteActionTabMetaData.EFFECTIVE_DATE.getLabel());

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1)).hasCellWithValue("Description", String.format(BamConstants.POLICY_EFFECTIVE_FROM_QUOTE, policyNumber, rewriteEffectiveDate, policyNumber));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2).getCell("Description")).valueMatches("Issue Master Quote effective " + rewriteEffectiveDate + "; Transaction Premium: .+ for Policy " + policyNumber);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(4)).hasCellWithValue("Description", String.format(BamConstants.CONTRACT_ACCEPTANCE_QUOTE, policyNumber, rewriteEffectiveDate));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(5).getCell("Description")).valueMatches(String.format("Generate proposal .*? containing Quote %s effective %s", policyNumber, rewriteEffectiveDate));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(7)).hasCellWithValue("Description", String.format(BamConstants.UPDATE_QUOTE, policyNumber));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(8)).hasCellWithValue("Description", String.format(BamConstants.UPDATE_QUOTE, policyNumber));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(9)).hasCellWithValue("Description", String.format(BamConstants.REWRITTEN_QUOTE_FROM_CANCELLED, policyNumber, rewriteEffectiveDate, policyNumber));
    }
}
