package com.exigen.ren.modules.policy.gb_eap.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.master.PolicyInformationBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PolicyInformationBindActionTabMetaData.AMOUNT_RECEIVED_WITH_APPLICATION;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestVerifyAmountReceivedWithApplicationAttributeIsOptional extends BaseTest implements CustomerContext, CaseProfileContext, EmployeeAssistanceProgramMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_EAP, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42356", component = POLICY_GROUPBENEFITS)
    public void testVerifyAmountReceivedWithApplicationAttributeIsOptional() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(employeeAssistanceProgramMasterPolicy.getType());

        LOGGER.info("TEST: Step 1");
        employeeAssistanceProgramMasterPolicy.createQuote(getDefaultEAPMasterPolicyData());

        LOGGER.info("TEST: Step 2");
        employeeAssistanceProgramMasterPolicy.propose().perform(getDefaultEAPMasterPolicyData());

        LOGGER.info("TEST: Step 3");
        employeeAssistanceProgramMasterPolicy.acceptContract().start();

        LOGGER.info("TEST: Step 4");
        PolicyInformationBindActionTab policyInformationBindActionTab = employeeAssistanceProgramMasterPolicy.acceptContract().getWorkspace().getTab(PolicyInformationBindActionTab.class);

        assertThat(policyInformationBindActionTab.getAssetList().getAsset(AMOUNT_RECEIVED_WITH_APPLICATION)).isPresent().hasValue(EMPTY).isOptional();

        policyInformationBindActionTab.submitTab();

        assertThat(employeeAssistanceProgramMasterPolicy.acceptContract().getWorkspace().getTab(PremiumSummaryBindActionTab.class).isTabSelected()).isTrue();

        LOGGER.info("TEST: Step 5");
        employeeAssistanceProgramMasterPolicy.acceptContract().submit();
        employeeAssistanceProgramMasterPolicy.issue().perform(getDefaultEAPMasterPolicyData());

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}