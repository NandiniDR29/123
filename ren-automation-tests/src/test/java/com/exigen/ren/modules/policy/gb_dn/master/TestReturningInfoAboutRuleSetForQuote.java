package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.common.metadata.master.PremiumSummaryBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PremiumSummaryIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.TestDataKey.ENDORSEMENT;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PremiumSummaryTabMetaData.RATE_SECTION;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PremiumSummaryTabMetaData.RateMetaData.RATE_REQUEST_DATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PremiumSummaryTabMetaData.RateMetaData.RATING_FORMULA;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.TransactionHistory.selectTransactionsToCompare;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestReturningInfoAboutRuleSetForQuote extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-37120", component = POLICY_GROUPBENEFITS)
    public void testReturningInfoAboutRuleSetForQuote() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_Master").resolveLinks()),
                premiumSummaryTab.getClass(), false);

        LOGGER.info("Step 1");
        assertThat(premiumSummaryTab.getAssetList().getAsset(RATE_SECTION).getAsset(RATE_REQUEST_DATE)).isDisabled().hasValue(ValueConstants.EMPTY);

        LOGGER.info("Step 2");
        premiumSummaryTab.buttonRate.click();
        String ratingFormula = premiumSummaryTab.getAssetList().getAsset(RATE_SECTION).getAsset(RATING_FORMULA).getValue();
        assertThat(ratingFormula).isNotEmpty();
        premiumSummaryTab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("Step 5");
        groupDentalMasterPolicy.propose().perform();

        LOGGER.info("Step 7");
        groupDentalMasterPolicy.acceptContract().start();
        groupDentalMasterPolicy.acceptContract().getWorkspace().getTab(PremiumSummaryBindActionTab.class).navigateToTab();
        assertThat(groupDentalMasterPolicy.acceptContract().getWorkspace().getTab(PremiumSummaryBindActionTab.class)
                .getAssetList().getAsset(PremiumSummaryBindActionTabMetaData.RATING_FORMULA)).isDisabled().hasValue(ratingFormula);
        Tab.buttonNext.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(CUSTOMER_ACCEPTED);

        LOGGER.info("Step 9");
        groupDentalMasterPolicy.issue().start();
        groupDentalMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), PremiumSummaryIssueActionTab.class, false);
        PremiumSummaryIssueActionTab premiumSummaryIssueActionTab = groupDentalMasterPolicy.issue().getWorkspace().getTab(PremiumSummaryIssueActionTab.class);
        assertThat(premiumSummaryIssueActionTab.getAssetList().getAsset(PremiumSummaryIssueActionTabMetaData.RATING_FORMULA)).isDisabled().hasValue(ratingFormula);

        LOGGER.info("Step 10");
        //For the Dental product Rate button was disabled

        LOGGER.info("Step 11");
        groupDentalMasterPolicy.issue().getWorkspace().fillFrom(getDefaultDNMasterPolicyData(), PremiumSummaryIssueActionTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(POLICY_ACTIVE);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Rating Formula"))).isAbsent();

        LOGGER.info("Step 13");
        EndorseAction.startEndorsementForPolicy(groupDentalMasterPolicy.getType(), groupDentalMasterPolicy.getDefaultTestData(ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        premiumSummaryTab.navigateToTab();
        assertThat(premiumSummaryTab.getAssetList().getAsset(RATE_SECTION).getAsset(RATING_FORMULA)).isDisabled().hasValue(ratingFormula);

        LOGGER.info("Step 14");
        premiumSummaryTab.buttonRate.click();
        assertThat(premiumSummaryTab.getAssetList().getAsset(RATE_SECTION).getAsset(RATING_FORMULA)).hasValue(ratingFormula);
        premiumSummaryTab.buttonSaveAndExit.click();
        assertThat(PolicySummaryPage.buttonPendedEndorsement).isEnabled();

        LOGGER.info("Step 16");
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalMasterPolicy.issue().start();
        premiumSummaryIssueActionTab.navigateToTab();
        assertThat(premiumSummaryIssueActionTab.getAssetList().getAsset(PremiumSummaryIssueActionTabMetaData.RATING_FORMULA)).isDisabled().hasValue(ratingFormula);

        LOGGER.info("Step 17");
        premiumSummaryIssueActionTab.buttonRate.click();
        assertThat(premiumSummaryIssueActionTab.getAssetList().getAsset(PremiumSummaryIssueActionTabMetaData.RATING_FORMULA)).isDisabled().hasValue(ratingFormula);

        LOGGER.info("Step 18");
        groupDentalMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class).navigateToTab().submitTab();
        Tab.buttonFinish.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(POLICY_ACTIVE);
        selectTransactionsToCompare(ProductConstants.TransactionHistoryType.ENDORSEMENT, ProductConstants.TransactionHistoryType.ISSUE);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Rating Formula"))).isAbsent();

        LOGGER.info("Step 19 - 20");
        Tab.buttonCancel.click();
        Tab.buttonCancel.click();
        //ToDo rznamerovskyi: need to continue steps 19-20 after REN-394 will be finished
    }
}
