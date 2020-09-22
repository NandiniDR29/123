package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteRulesVerificationForUnderwritingCompany extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-14611", component = POLICY_GROUPBENEFITS)
    public void testQuoteRulesVerificationForUnderwritingCompany() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());

        LOGGER.info("Steps 2, 3");
        assertSoftly(softly -> ImmutableMap.of(
                "AK", "Renaissance Life & Health Insurance Company of America",
                "NY", "Renaissance Life & Health Insurance Company of New York").forEach((key, value) -> {
                    policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue(key);
                    softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.UNDEWRITING_COMPANY)).hasValue(value);
        }));

        LOGGER.info("Steps 4");
        groupAccidentMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultACMasterPolicyData(), policyInformationTab.getClass(), premiumSummaryTab.getClass(), true);
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("Steps 5");
        groupAccidentMasterPolicy.propose().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        LOGGER.info("Steps 6");
        groupAccidentMasterPolicy.acceptContract().start();
        assertThat(groupAccidentMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class)
                .getAssetList().getAsset(PolicyInformationBindActionTabMetaData.UNDEWRITING_COMPANY)).isDisabled();

        LOGGER.info("Steps 7");
        groupAccidentMasterPolicy.acceptContract().getWorkspace().fill(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        groupAccidentMasterPolicy.acceptContract().submit();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        LOGGER.info("Steps 8");
        groupAccidentMasterPolicy.issue().start();
        assertThat(groupAccidentMasterPolicy.issue().getWorkspace().getTab(PolicyInformationIssueActionTab.class)
                .getAssetList().getAsset(PolicyInformationIssueActionTabMetaData.UNDEWRITING_COMPANY)).isDisabled();
    }
}
