package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMajorMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.LIMITATION_FREQUENCY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.LimitationFrequencyMetaData.DENTAL_MAJOR;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.coveragesTable;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckFrequencyValidationForAttributeCrowns extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-45172"}, component = POLICY_GROUPBENEFITS)
    public void testCheckFrequencyValidationForAttributeCrowns() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        LOGGER.info("Steps#1, 2, 3 verification");
        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_NO)
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("Step#4 verification");
        groupDentalMasterPolicy.dataGather().start();
        dentalMajorFieldsVerification("Once Every 4 Years");

        LOGGER.info("Step#5 verification");
        dentalMajorFieldsVerification("Not Covered");

        LOGGER.info("Step#6 verification");
        classificationManagementMpTab.submitTab();
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }

    private void dentalMajorFieldsVerification(String valueInlays){
        planDefinitionTab.navigateToTab();
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(CROWNS).setValue("Once Every 4 Years");
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS).setValue(valueInlays);
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(VENEERS).setValue("Once Every 4 Years");
        Tab.buttonNext.click();
        assertThat(coveragesTable).isPresent();
    }
}
