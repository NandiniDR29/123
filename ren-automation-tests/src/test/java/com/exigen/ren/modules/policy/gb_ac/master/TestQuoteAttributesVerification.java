package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
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
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_MESSAGE_TOTAL_MUST_BE_GREATER_THAN_0;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteAttributesVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private AssetList policyAssetList;
    private static final String ERROR_MESSAGE_TOTAL_NUMBER_IS_REQUIRED = "'Total Number of Eligible Lives' is required";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-14603", "REN-14607", "REN-24017"}, component = POLICY_GROUPBENEFITS)

    public void testQuoteAttributesVerification() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());

        LOGGER.info("Test REN-24017 Step 1 and Step 2");
        assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.RATE_BASIS)).isPresent().hasOptions("Per Month");

        AssetList internalAssetList = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.INTERNAL_TEAM);
        policyAssetList = (AssetList) policyInformationTab.getAssetList();
        assertSoftly(softly -> {
            softly.assertThat(policyAssetList.getAsset(UNDEWRITING_COMPANY)).isPresent();
            LOGGER.info("Step 2 for REN-14603, REN-14607");
            softly.assertThat(policyAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isPresent().isRequired().isEnabled().hasValue("");
            policyInformationTab.getAssetList().fill(getDefaultACMasterPolicyData());

            LOGGER.info("Steps 3,4 for REN-14603, REN-14607");
            checkErrorMessage(softly, "", ERROR_MESSAGE_TOTAL_NUMBER_IS_REQUIRED);

            LOGGER.info("Steps 5,6 for REN-14607");
            checkErrorMessage(softly, "0", ERROR_MESSAGE_TOTAL_MUST_BE_GREATER_THAN_0);
            checkErrorMessage(softly, "-1", ERROR_MESSAGE_TOTAL_MUST_BE_GREATER_THAN_0);

            softly.assertThat(policyAssetList.getAsset(MAXIMUM_NUMBER_OF_PARTICIPANTS)).isAbsent();


            LOGGER.info("Steps 3,4 for REN-14603");
            ImmutableList.of(SALES_REPRESENTATIVE, SALES_SUPPORT_ASSOCIATE, UNDERWRITER).forEach(asset ->
                    softly.assertThat(internalAssetList.getAsset(asset)).isPresent().isRequired().isEnabled());
            groupAccidentMasterPolicy.getDefaultWorkspace()
                    .fillFromTo(getDefaultACMasterPolicyData(), policyInformationTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            // Don't show in Consolidated View
            softly.assertThat(QuoteSummaryPage.tableMasterQuote.getHeader().getValue())
                    .doesNotContain(
                            SALES_SUPPORT_ASSOCIATE.getLabel(),
                            UNDERWRITER.getLabel(),
                            TOTAL_NUMBER_OF_ELIGIBLE_LIVES.getLabel(),
                            UNDEWRITING_COMPANY.getLabel());
            // Show in Consolidated View
            softly.assertThat(QuoteSummaryPage.tableMasterQuote.getHeader().getValue()).contains(QuoteSummaryPage.MasterQuote.SALES_REPRESENTATIVE.getName());
        });

        LOGGER.info("Steps 7,8 for REN-14607");
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("Steps 9 for REN-14607");
        groupAccidentMasterPolicy.dataGather().start();
        policyAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("2");
        Tab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);

        LOGGER.info("Steps 10 for REN-14607");
        groupAccidentMasterPolicy.calculatePremium();
        groupAccidentMasterPolicy.propose().perform(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        LOGGER.info("Steps 11 for REN-14607");
        groupAccidentMasterPolicy.acceptContract().start();
        assertThat(groupAccidentMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class)
                .getAssetList().getAsset(PolicyInformationBindActionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isDisabled();
        groupAccidentMasterPolicy.acceptContract().getWorkspace().fill(groupAccidentMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        groupAccidentMasterPolicy.acceptContract().submit();
        LOGGER.info("Steps 12 for REN-14607");
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        LOGGER.info("Steps 13 for REN-14607");
        groupAccidentMasterPolicy.issue().start();
        assertThat(groupAccidentMasterPolicy.issue().getWorkspace().getTab(PolicyInformationIssueActionTab.class)
                .getAssetList().getAsset(PolicyInformationIssueActionTabMetaData.TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isDisabled();
    }

    private void checkErrorMessage(ETCSCoreSoftAssertions softly, String value, String errorMessage) {
        policyAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue(value);
        Tab.buttonNext.click();
        softly.assertThat(policyAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasWarningWithText(errorMessage);
        premiumSummaryTab.navigate();
        premiumSummaryTab.rate();
        Row errorRow = ErrorPage.tableError.getRow(ErrorPage.TableError.MESSAGE.getName(), errorMessage);
        softly.assertThat(errorRow).exists();
        errorRow.getCell(ErrorPage.TableError.CODE.getName()).controls.links.getFirst().click();
    }
}
