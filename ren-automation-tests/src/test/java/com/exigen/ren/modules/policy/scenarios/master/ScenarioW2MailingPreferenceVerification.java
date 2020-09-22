package com.exigen.ren.modules.policy.scenarios.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.main.enums.ActionConstants.ProductAction.ISSUE;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData.MAIL_W_2_TO;

public class ScenarioW2MailingPreferenceVerification extends BaseTest implements CustomerContext, CaseProfileContext {

    protected void scenarioW2MailingPreferenceVerification(GroupBenefitsMasterPolicyType policyType, TestData tdPolicy) {
        LOGGER.info("Preconditions");
        String w2ErrorMessage = "Mail W-2 To is mandatory for the Group Sponsor customer record. Please complete Mail W-2 To before continuing with Master Quote issue";

        mainApp().open();
        customerNonIndividual.createViaUI(customerNonIndividual.getDefaultTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(generalTab.getMetaKey(), MAIL_W_2_TO.getLabel()), EMPTY));
        createDefaultCaseProfile(policyType);
        GroupBenefitsMasterPolicy policy = policyType.get();

        //For ST product 1st value for step#, for other products 2nd value for step#
        LOGGER.info("Step#5/7 verification");
        policy.createQuote(tdPolicy);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("Step#6/8 verification");
        policy.propose().perform(policy.getDefaultTestData(TestDataKey.PROPOSE, DEFAULT_TEST_DATA_KEY));
        policy.acceptContract().perform(policy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        LOGGER.info("Step#7/9 verification");
        NavigationPage.comboBoxListAction.setValue(ISSUE);
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(w2ErrorMessage))).isPresent();

        LOGGER.info("Steps#8,9/10,11 execution");
        Page.dialogConfirmation.buttonCancel.click();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        NavigationPage.toMainTab(CUSTOMER);
        customerIndividual.update().start();
        generalTab.getAssetList().getAsset(MAIL_W_2_TO).setValue("Group");
        GeneralTab.buttonSaveAndExit.click();

        LOGGER.info("Step#10/12 verification");
        MainPage.QuickSearch.search(policyNumber);
        policy.issue().start();
        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(w2ErrorMessage))).isAbsent();
    }
}
