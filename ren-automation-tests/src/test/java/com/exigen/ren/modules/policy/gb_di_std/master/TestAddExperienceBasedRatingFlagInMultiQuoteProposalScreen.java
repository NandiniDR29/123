package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.data.DataProviderFactory;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.caseprofile.tabs.QuotesSelectionActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.RATING;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.CREDIBILITY_FACTOR;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetaData.EXPERIENCE_CLAIM_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PremiumSummaryTabMetaData.EXPERIENCE_RATING;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddExperienceBasedRatingFlagInMultiQuoteProposalScreen extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-18929", component = POLICY_GROUPBENEFITS)
    public void testAddExperienceBasedRatingFlagSTD() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        LOGGER.info("Test REN-18929 Step 1 and Step 2");
        shortTermDisabilityMasterPolicy.createQuote(getDefaultSTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", RATING.getLabel()), DataProviderFactory.dataOf(CREDIBILITY_FACTOR.getLabel(), "0"))
                .adjust(TestData.makeKeyPath(premiumSummaryTab.getClass().getSimpleName(), EXPERIENCE_RATING.getLabel()), DataProviderFactory.dataOf(PremiumSummaryTabMetaData.ExperienceRatingMetaData.CREDIBILITY_FACTOR.getLabel(), "0")));
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("Test REN-18929 Step 3");
        shortTermDisabilityMasterPolicy.propose().start();
        QuotesSelectionActionTab quotesSelectionActionTab = new QuotesSelectionActionTab();
        quotesSelectionActionTab.fillTab(getDefaultSTDMasterPolicyData());
        quotesSelectionActionTab.submitTab();

        LOGGER.info("Test REN-18929 Step 4");
        assertThat(ProposalActionTab.getCoverageInfo(quoteNumber, "NC").getRow(1).getCell(4)).hasValue("Experience Based Rating - No");
        ProposalActionTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Test REN-18929 Step 8");
        MainPage.QuickSearch.search(quoteNumber);
        shortTermDisabilityMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR).setValue("1");
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(EXPERIENCE_CLAIM_AMOUNT).setValue("100");
        premiumSummaryTab.navigate();
        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(PremiumSummaryTabMetaData.ExperienceRatingMetaData.CREDIBILITY_FACTOR).setValue("0");
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

       LOGGER.info("Test REN-18929 Step 9");
        shortTermDisabilityMasterPolicy.propose().start();
        quotesSelectionActionTab.fillTab(getDefaultSTDMasterPolicyData());
        quotesSelectionActionTab.submitTab();

        LOGGER.info("Test REN-18929 Step 10");
        assertThat(ProposalActionTab.getCoverageInfo(quoteNumber, "NC").getRow(1).getCell(4)).hasValue("Experience Based Rating - Yes");
        ProposalActionTab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Test REN-18929 Step 15");
        MainPage.QuickSearch.search(quoteNumber);
        shortTermDisabilityMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        planDefinitionTab.getAssetList().getAsset(RATING).getAsset(CREDIBILITY_FACTOR).setValue("0");
        premiumSummaryTab.navigate();
        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(PremiumSummaryTabMetaData.ExperienceRatingMetaData.EXPERIENCE_CLAIM_AMOUNT).setValue("100");
        premiumSummaryTab.getAssetList().getAsset(EXPERIENCE_RATING).getAsset(PremiumSummaryTabMetaData.ExperienceRatingMetaData.CREDIBILITY_FACTOR).setValue("1");
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("Test REN-18929 Step 16");
        shortTermDisabilityMasterPolicy.propose().start();
        quotesSelectionActionTab.fillTab(getDefaultSTDMasterPolicyData());
        quotesSelectionActionTab.submitTab();
        assertThat(ProposalActionTab.getCoverageInfo(quoteNumber, "NC").getRow(1).getCell(4)).hasValue("Experience Based Rating - Yes");
        new ProposalActionTab().submitTab();  //additional verification that quote can be proposed with Experience Based Rating Flag = Yes
        assertThat(CaseProfileSummaryPage.tableProposal).as("Proposal action was not finished").isPresent();

        LOGGER.info("Test REN-18929 Step 17");
        shortTermDisabilityMasterPolicy.createQuote(shortTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_TwoPlans")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[1]", RATING.getLabel()), DataProviderFactory.dataOf(CREDIBILITY_FACTOR.getLabel(), "0"))
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName() + "[2]", RATING.getLabel()),
                        DataProviderFactory.dataOf(EXPERIENCE_CLAIM_AMOUNT.getLabel(), "50", CREDIBILITY_FACTOR.getLabel(), "1"))
                .adjust(TestData.makeKeyPath(premiumSummaryTab.getClass().getSimpleName(), EXPERIENCE_RATING.getLabel()),
                        DataProviderFactory.dataOf(PremiumSummaryTabMetaData.ExperienceRatingMetaData.EXPERIENCE_CLAIM_AMOUNT.getLabel(), "100", PremiumSummaryTabMetaData.ExperienceRatingMetaData.CREDIBILITY_FACTOR.getLabel(), "0")));
        String quoteNumber2 = PolicySummaryPage.labelPolicyNumber.getValue();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("Test REN-18929 Step 18");
        shortTermDisabilityMasterPolicy.propose().start();
        QuotesSelectionActionTab.textBoxProposalName.setValue("Proposal Name");
        QuotesSelectionActionTab.selectQuote(ImmutableList.of(1));
        QuotesSelectionActionTab.selectQuote(ImmutableList.of(2));
        quotesSelectionActionTab.submitTab();
        assertThat(ProposalActionTab.getCoverageInfo(quoteNumber, "NC").getRow(1).getCell(4)).hasValue("Experience Based Rating - Yes");
        assertThat(ProposalActionTab.getCoverageInfo(quoteNumber2, "NC").getRow(1).getCell(4)).hasValue("Experience Based Rating - No");
        assertThat(ProposalActionTab.getCoverageInfo(quoteNumber2, "CON").getRow(1).getCell(4)).hasValue("Experience Based Rating - Yes");
    }
}
