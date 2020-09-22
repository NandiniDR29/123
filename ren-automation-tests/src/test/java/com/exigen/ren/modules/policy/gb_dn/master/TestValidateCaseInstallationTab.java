/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.CaseInstallationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.CaseInstallationIssueActionTabMetaData.DEFINITION_OF_LEGAL_SPOUSE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.CaseInstallationIssueActionTabMetaData.DefinitionOfLegalSpouseMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.CaseInstallationIssueActionTabMetaData.INCLUDE_MEMBERS_ON_COBRA;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.ELIGIBILITY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.MINIMUM_HOURLY_REQUIREMENT;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestValidateCaseInstallationTab extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-24432", component = POLICY_GROUPBENEFITS)
    public void testValidateCaseInstallationTab() {

        preconditions();

        LOGGER.info("Step 2");
        assertThat(caseInstallationIssueActionTab.getAssetList().getAsset(INCLUDE_MEMBERS_ON_COBRA)).isEnabled().hasValue(VALUE_YES);

        LOGGER.info("Step 3");
        caseInstallationIssueActionTab.getAssetList().getAsset(INCLUDE_MEMBERS_ON_COBRA).setValue(VALUE_NO);

        LOGGER.info("Steps 4, 5, 6, 7");
        ImmutableList.of(SPOUSE_LEGALLY_RECOGNIZED_POLICY_ISSUE_STATE, CIVIL_UNION_LEGALLY_RECOGNIZED_POLICY_ISSUE_STATE, INCLUDES_DOMESTIC_PARTNER).forEach(control ->
                assertThat(caseInstallationIssueActionTab.getAssetList().getAsset(DEFINITION_OF_LEGAL_SPOUSE).getAsset(control)).isOptional().isEnabled().hasValue(true));

        LOGGER.info("Step 8");
        assertSoftly(softly ->
                ImmutableList.of(SPOUSE_LEGALLY_RECOGNIZED_POLICY_ISSUE_STATE, CIVIL_UNION_LEGALLY_RECOGNIZED_POLICY_ISSUE_STATE, INCLUDES_DOMESTIC_PARTNER).forEach(control -> {
                    caseInstallationIssueActionTab.getAssetList().getAsset(CaseInstallationIssueActionTabMetaData.DEFINITION_OF_LEGAL_SPOUSE).getAsset(control).setValue(false);
                    softly.assertThat(caseInstallationIssueActionTab.getAssetList().getAsset(CaseInstallationIssueActionTabMetaData.DEFINITION_OF_LEGAL_SPOUSE).getAsset(control)).hasValue(false);
                }));
    }

    private void preconditions() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData());

        String quote1 = PolicySummaryPage.labelPolicyNumber.getValue();
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());

        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), PolicyInformationTabMetaData.GROUP_IS_AN_ASSOCIATION.getLabel()), VALUE_YES)
                .mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", ELIGIBILITY.getLabel(), MINIMUM_HOURLY_REQUIREMENT.getLabel())));
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());

        MainPage.QuickSearch.search(quote1);

        groupDentalMasterPolicy.issue().start();
        caseInstallationIssueActionTab.navigateToTab();
    }
}