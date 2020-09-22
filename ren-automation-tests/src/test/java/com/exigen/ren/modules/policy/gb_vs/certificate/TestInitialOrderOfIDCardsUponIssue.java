package com.exigen.ren.modules.policy.gb_vs.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.metadata.SearchMetaData;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.tabs.CertificatePolicyTab;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.GroupVisionCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.modules.policy.scenarios.certificate.ScenarioTestInitialOrderOfIDCardsUponIssue;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestInitialOrderOfIDCardsUponIssue extends ScenarioTestInitialOrderOfIDCardsUponIssue implements GroupVisionMasterPolicyContext, GroupVisionCertificatePolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25811", component = POLICY_GROUPBENEFITS)
    public void testInitialOrderOfIDCardsUponIssueEffectiveDateAsCurrentDate() {
        LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
        TestData tdMasterPolicy = getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE.getLabel()), today.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), today.format(DateTimeUtils.MM_DD_YYYY));
        TestData tdCertificatePolicy = getDefaultVSCertificatePolicyData()
                .adjust(TestData.makeKeyPath(CertificatePolicyTab.class.getSimpleName(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), today.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        testInitialOrderOfIDCardsUponIssue(GroupBenefitsMasterPolicyType.GB_VS, tdMasterPolicy, GroupBenefitsCertificatePolicyType.GB_VS, tdCertificatePolicy, today);

    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25811", component = POLICY_GROUPBENEFITS)
    public void testInitialOrderOfIDCardsUponIssueEffectiveDateAsFutureDate() {
        LocalDateTime futureDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(10);
        TestData tdMasterPolicy = getDefaultVSMasterPolicyData()
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE.getLabel()), futureDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), futureDate.format(DateTimeUtils.MM_DD_YYYY));
        TestData tdCertificatePolicy = getDefaultVSCertificatePolicyData()
                .adjust(TestData.makeKeyPath(CertificatePolicyTab.class.getSimpleName(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), futureDate.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(groupVisionCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY));

        testInitialOrderOfIDCardsUponIssue(GroupBenefitsMasterPolicyType.GB_VS, tdMasterPolicy, GroupBenefitsCertificatePolicyType.GB_VS, tdCertificatePolicy, futureDate);
    }
}
