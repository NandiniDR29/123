package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.BLEND_DEMOGRAPHICS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBlendDemorgraphicFieldForRating extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-19190"}, component = POLICY_GROUPBENEFITS)
    public void testBlendDemographicFieldForRating() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), policyInformationTab.getClass());
        assertSoftly(softly -> {
            LOGGER.info("REN 19190 Step 1-3");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(BLEND_DEMOGRAPHICS)).isPresent().isRequired().hasValue(VALUE_YES);
            policyInformationTab.getAssetList().getAsset(BLEND_DEMOGRAPHICS).setValue(VALUE_NO);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(BLEND_DEMOGRAPHICS)).hasValue(VALUE_NO);
        });
    }
}
