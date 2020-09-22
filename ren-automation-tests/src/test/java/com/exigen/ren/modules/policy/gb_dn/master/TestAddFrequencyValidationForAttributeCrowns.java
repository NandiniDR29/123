package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorTable.CODE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMajorMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.LIMITATION_FREQUENCY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.LimitationFrequencyMetaData.DENTAL_MAJOR;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ASO_PLAN;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab.coveragesTable;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddFrequencyValidationForAttributeCrowns extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final String ERROR_MESSAGE = "State Requirement: Selections of frequency for Crowns, Inlays, and Veneers must match";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-46560"}, component = POLICY_GROUPBENEFITS)
    public void testAddFrequencyValidationForAttributeCrowns() {
        LOGGER.info("General Preconditions");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        LOGGER.info("Steps#1, 2 verification");
        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NV")
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), ASO_PLAN.getLabel()), VALUE_NO)
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks().resolveLinks()), PlanDefinitionTab.class, true);
        Tab.buttonNext.click();
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(CROWNS)).hasWarningWithText(ERROR_MESSAGE);

        LOGGER.info("Step#3 verification");
        classificationManagementMpTab.navigateToTab();
        groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData()
                .adjust(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY).resolveLinks()), ClassificationManagementTab.class, PremiumSummaryTab.class, true);
        premiumSummaryTab.rate();

        Row errorRow = ErrorPage.tableError.getRowContains(ErrorPage.TableError.MESSAGE.getName(), ERROR_MESSAGE);
        assertThat(errorRow).as(String.format("Row with error message: '%s'", ERROR_MESSAGE)).isPresent();

        LOGGER.info("Step#6 verification");
        errorRow.getCell(CODE).controls.links.getFirst().click();

        dentalMajorFieldsVerification("Once Every 3 Years", "Once Every 5 Years");
        assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(CROWNS)).hasWarningWithText(ERROR_MESSAGE);

        LOGGER.info("Step#8 verification");
        dentalMajorFieldsVerification("Once Every 3 Years", "Once Every 3 Years");
        assertThat(coveragesTable).isPresent();

        LOGGER.info("Step#9 verification");
        planDefinitionTab.navigateToTab();
        dentalMajorFieldsVerification("Not Covered", "Once Every 3 Years");
        assertThat(coveragesTable).isPresent();

        LOGGER.info("Step#11 verification");
        classificationManagementMpTab.submitTab();
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }

    private void dentalMajorFieldsVerification(String crown, String inlays) {
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(CROWNS).setValue(crown);
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(INLAYS).setValue(inlays);
        planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(VENEERS).setValue("Once Every 3 Years");
        Tab.buttonNext.click();
    }
}
