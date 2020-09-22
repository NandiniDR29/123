/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package  com.exigen.ren.modules.policy.gb_tl.certificate;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.common.ReinstateActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.common.ReinstateActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.PolicyConstants.ReinstateReason.*;
import static com.exigen.ren.main.enums.PolicyConstants.ReinstateReason.OTHER;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyReinstatement extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceCertificatePolicyContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"IPBQA-24480", "REN-25172"}, component = POLICY_GROUPBENEFITS)
    public void testPolicyReinstatement() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(termLifeInsuranceMasterPolicy.getType());

        createDefaultTermLifeInsuranceCertificatePolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("Cancelling Policy #" + policyNumber);
        termLifeInsuranceCertificatePolicy.cancel().perform(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        LOGGER.info("REN-25172 Step 12");
        termLifeInsuranceCertificatePolicy.reinstate().start();
        assertThat(termLifeInsuranceCertificatePolicy.reinstate().getWorkspace().getTab(ReinstateActionTab.class).getAssetList().getAsset(ReinstateActionTabMetaData.REASON))
                .hasOptions("", GROUP_SPONSOR_ERROR, HOME_OFFICE_ERROR, REHIRE, REGAINED_ACTIVE_STATUS, CONTINUATION, OTHER);

        LOGGER.info("TEST: Reinstate Policy #" + policyNumber);
        termLifeInsuranceCertificatePolicy.reinstate().getWorkspace().fill(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.REINSTATEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        termLifeInsuranceCertificatePolicy.reinstate().submit();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
