/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.BamConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.RewriteActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.RewriteActionTab;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyRewriteToNewNumber extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24466", component = POLICY_GROUPBENEFITS)
    public void testPolicyRewriteToNewNumber() {
        mainApp().open();

        createDefaultNonIndividualCustomer();

        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicySelfAdminData());

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TEST: Cancellation Policy #" + policyNumber);
        shortTermDisabilityMasterPolicy.cancel().perform(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(ActionConstants.ProductAction.CANCELLATION);

        LOGGER.info("TEST: Rewrite Policy #" + policyNumber);
        shortTermDisabilityMasterPolicy.createRewrite(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.REWRITE, "TestDataNewNumber")
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.REWRITE, TestDataKey.DATA_GATHER).resolveLinks())
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .adjust(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        String policyNumberRewritten = PolicySummaryPage.labelPolicyNumber.getValue();
        String policyEffectiveDateRewritten = shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.REWRITE, "TestDataNewNumber").getValue(RewriteActionTab.class.getSimpleName(),
                RewriteActionTabMetaData.EFFECTIVE_DATE.getLabel());

        assertThat(NavigationPage.comboBoxListAction).containsOption(ActionConstants.ProductAction.CANCELLATION);
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(ActionConstants.ProductAction.REWRITE);

        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(1)).hasCellWithValue("Description", String.format(BamConstants.POLICY_EFFECTIVE_FROM_QUOTE, policyNumberRewritten, policyEffectiveDateRewritten, policyNumberRewritten));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(2).getCell("Description")).valueMatches("Issue Master Quote effective " + policyEffectiveDateRewritten + "; Transaction Premium: .+ for Policy " + policyNumberRewritten);
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(4)).hasCellWithValue("Description", String.format(BamConstants.CONTRACT_ACCEPTANCE_QUOTE, policyNumberRewritten, policyEffectiveDateRewritten));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(5).getCell("Description")).valueMatches(String.format("Generate proposal .*? containing Quote %s effective %s", policyNumberRewritten, policyEffectiveDateRewritten));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(7)).hasCellWithValue("Description", String.format(BamConstants.UPDATE_QUOTE, policyNumberRewritten));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(8)).hasCellWithValue("Description", String.format(BamConstants.UPDATE_QUOTE, policyNumberRewritten));
        assertThat(NotesAndAlertsSummaryPage.activitiesAndUserNotes.getRow(9)).hasCellWithValue("Description", String.format(BamConstants.REWRITTEN_QUOTE_FROM_CANCELLED, policyNumberRewritten, policyEffectiveDateRewritten, policyNumber));
    }
}
