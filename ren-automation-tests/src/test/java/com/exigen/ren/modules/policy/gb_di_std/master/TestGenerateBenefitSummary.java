package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGenerateBenefitSummary extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {
    private static final String DEDUCTION_BY_PAY_PERIOD_DEFAULT_BI_WEEKLY = "Bi-Weekly";
    private static final String DEDUCTION_BY_PAY_PERIOD_NO_RATES = "No Rates";
    private static final ImmutableList<String> DEDUCTION_BY_PAY_PERIOD_VALUE_LIST = ImmutableList.of(StringUtils.EMPTY, "Monthly", "Weekly", "Semi-Monthly", "Bi-Weekly", "No Rates");

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23226"}, component = POLICY_GROUPBENEFITS)
    public void testGenerateBenefitSummary() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        assertSoftly(softly -> {
            LOGGER.info("REN-23226 Step 1");
            shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
            shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), ShortTermDisabilityMasterPolicyContext.policyInformationTab.getClass());
            softly.assertThat(ShortTermDisabilityMasterPolicyContext.policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.DEDUCTION_BY_PAY_PERIOD)).isPresent().isOptional().hasValue(DEDUCTION_BY_PAY_PERIOD_DEFAULT_BI_WEEKLY).hasOptions(DEDUCTION_BY_PAY_PERIOD_VALUE_LIST);

            LOGGER.info("REN-23226 Step 3");
            ShortTermDisabilityMasterPolicyContext.policyInformationTab.fillTab(getDefaultSTDMasterPolicyData());
            ShortTermDisabilityMasterPolicyContext.policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.DEDUCTION_BY_PAY_PERIOD).setValue(DEDUCTION_BY_PAY_PERIOD_NO_RATES);
            Tab.buttonNext.click();
            softly.assertThat(NavigationPage.PolicyNavigation.isLeftMenuTabSelected(NavigationEnum.GroupBenefitsTab.ENROLLMENT.get())).isTrue();
        });
    }
}
