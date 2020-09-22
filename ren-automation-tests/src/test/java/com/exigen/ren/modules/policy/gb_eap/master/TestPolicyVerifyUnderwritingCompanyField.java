package com.exigen.ren.modules.policy.gb_eap.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.CaseProfileConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.SearchConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.CaseProfileConstants.CaseProfileTable.CASE_PROFILE_NAME;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyVerifyUnderwritingCompanyField extends BaseTest implements CustomerContext, CaseProfileContext, EmployeeAssistanceProgramMasterPolicyContext {
    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-10433", component = POLICY_GROUPBENEFITS)
    public void testPolicyVerifyUnderwritingCompanyField() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(employeeAssistanceProgramMasterPolicy.getType());

        employeeAssistanceProgramMasterPolicy.createQuote(getDefaultEAPMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        employeeAssistanceProgramMasterPolicy.dataGather().start();
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.UNDEWRITING_COMPANY)).isPresent();

        Tab.buttonCancel.click();
        Page.dialogConfirmation.buttonYes.click();
        employeeAssistanceProgramMasterPolicy.propose().perform(getDefaultEAPMasterPolicyData());
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CASE.get());
        CaseProfileSummaryPage.tableSelectCaseProfile.getRow(1).getCell(CASE_PROFILE_NAME).controls.links.getFirst().click();
        NavigationPage.toSubTab(NavigationEnum.CaseProfileTab.PROPOSALS.get());
        assertThat(CaseProfileSummaryPage.tableProposal.getRow(1).getCell(CaseProfileConstants.CaseProfileProposalTable.PROPOSAL_STATUS)).hasValue(CaseProfileConstants.ProposalStatus.GENERATED);

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.QUOTE.get());
        assertThat(QuoteSummaryPage.tableSelectQuote.getRow(1).getCell(QuoteSummaryPage.SelectQuote.STATUS.getName())).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        QuoteSummaryPage.tableSelectQuote.getRow(1).getCell(SearchConstants.QUOTE).controls.links.getFirst().click();
        employeeAssistanceProgramMasterPolicy.acceptContract().start();
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.UNDEWRITING_COMPANY)).isDisabled();

        employeeAssistanceProgramMasterPolicy.acceptContract().getWorkspace().fill(employeeAssistanceProgramMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        PremiumSummaryBindActionTab.buttonNext.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        employeeAssistanceProgramMasterPolicy.issue().start();
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.UNDEWRITING_COMPANY)).isDisabled();
    }
}
