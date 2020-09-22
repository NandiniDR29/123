package com.exigen.ren.modules.policy.scenarios.master;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicy;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;

import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.pages.summary.QuoteSummaryPage.getQuoteNumber;
import static org.assertj.core.api.Assertions.assertThat;

public class ScenarioTestUsePolicyTermFieldAbsence extends BaseTest implements CustomerContext, CaseProfileContext {

    public void testUsePolicyTermFieldAbsence(GroupBenefitsMasterPolicyType policyType, TestData tdPolicy, DefaultTab policyInformationtab) {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(policyType);
        GroupBenefitsMasterPolicy policy = policyType.get();

        LOGGER.info("Step 1");
        policy.initiate(tdPolicy);
        assertThatUsePolicyTermIsAbsent();

        LOGGER.info("Step 1.1");
        Tab.buttonTopSave.click();
        String policyID = getQuoteNumber();
        verifyUsePolicyTemValueInDatabase(policyID);

        LOGGER.info("Step 2");
        policy.getDefaultWorkspace().fillFrom(tdPolicy, policyInformationtab.getClass());

        LOGGER.info("Step 3");
        policy.quoteInquiry().start();
        assertThatUsePolicyTermIsAbsent();
        Tab.buttonCancel.click();

        LOGGER.info("Step 3.1");
        verifyUsePolicyTemValueInDatabase(policyID);

        LOGGER.info("Step 4");
        policy.propose().perform(tdPolicy);
        policy.acceptContract().start();
        assertThatUsePolicyTermIsAbsent();

        LOGGER.info("Step 4.1");
        verifyUsePolicyTemValueInDatabase(policyID);

        LOGGER.info("Step 5");
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
        policy.acceptContract().perform(tdPolicy);
        policy.issue().start();
        assertThatUsePolicyTermIsAbsent();

        LOGGER.info("Step 5.1");
        Tab.buttonTopSave.click();
        verifyUsePolicyTemValueInDatabase(policyID);

        LOGGER.info("Step 6");
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();
        policy.issue().perform(tdPolicy);

        LOGGER.info("Step 7");
        policy.policyInquiry().start();
        assertThatUsePolicyTermIsAbsent();

        LOGGER.info("Step 7.1");
        verifyUsePolicyTemValueInDatabase(policyID);
    }

    private void verifyUsePolicyTemValueInDatabase(String policyID) {
        String usePolicyTerm = DBService.get().getRow("select usePolicyTerm from PolicySummary where policyNumber = ?", policyID).get("usePolicyTerm");
        assertThat(usePolicyTerm).isEqualTo("0");
    }

    private void assertThatUsePolicyTermIsAbsent() {
        CustomAssertions.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Use Policy Term?"))).isAbsent();
    }
}
