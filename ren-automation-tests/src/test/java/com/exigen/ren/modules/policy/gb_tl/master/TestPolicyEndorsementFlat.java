/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package  com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyEndorsementFlat extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "IPBQA-24523", component = POLICY_GROUPBENEFITS)
    public void testPolicyEndorsementFlat() {
        mainApp().open();

        EntitiesHolder.openCopiedMasterPolicy(termLifeInsuranceMasterPolicy.getType());

        LOGGER.info("TEST: Flat Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        termLifeInsuranceMasterPolicy.createEndorsement(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.DATA_GATHER, "TestData_Endorsement").resolveLinks())
                .adjust(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.EXISTENT_BILLING_ACCOUNT).resolveLinks()));

        assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        PolicySummaryPage.buttonTransactionHistory.click();
        assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(
                "Type")).hasValue(ProductConstants.TransactionHistoryType.ENDORSEMENT);
    }
}
