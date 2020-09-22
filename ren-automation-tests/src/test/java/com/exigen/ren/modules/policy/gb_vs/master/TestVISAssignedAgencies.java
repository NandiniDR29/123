package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.TableConstants.AgenciesProducersTable.AGENCY_TYPE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.AGENCY_PRODUCER_COMBO;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestVISAssignedAgencies extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    private static final String AGENCY_TYPE_CALL_CENTER = "Call Center";
    private static final String AGENCY_TYPE_ERROR_MESSAGE = "Agency Type is required";
    private static final String AGENCY_TYPE_AGENCY = "Agency";
    private static final String AGENCY1 = "QA Agency";
    private static final String AGENCY2 = "CCRA1";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-21751", component = POLICY_GROUPBENEFITS)
    public void testAssignedAgenciesSingle() {
        LOGGER.info("REN-21751 Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), PolicyInformationTab.class);

        assertSoftly(softly -> {
            LOGGER.info("REN-21751 Step 1");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS)).isEnabled().hasValue(VALUE_YES);

            LOGGER.info("REN-21751 Step 2");
            Tab.buttonTopSave.click();
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_CALL_CENTER);

            LOGGER.info("REN-21751 Step 3");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(AGENCY_TYPE_CALL_CENTER);

            LOGGER.info("REN-21751 Step 9");
            policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS).setValue(VALUE_NO);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isAbsent();

            LOGGER.info("REN-21751 Step 10");
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), PolicyInformationTab.class, PolicyInformationTab.class, true);
            Tab.buttonNext.click();
            softly.assertThat(NavigationPage.PolicyNavigation.isLeftMenuTabSelected(NavigationEnum.GroupBenefitsTab.ENROLLMENT.get())).isTrue();

            LOGGER.info("REN-21751 Step 11");
            premiumSummaryTab.navigate();
            premiumSummaryTab.rate();

            Row errorRow = ErrorPage.tableError.getRowContains(ErrorPage.TableError.MESSAGE.getName(), AGENCY_TYPE_ERROR_MESSAGE);
            assertThat(errorRow)
                    .as(String.format("Row with error message: '%s'", AGENCY_TYPE_ERROR_MESSAGE))
                    .isAbsent();
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-21752", component = POLICY_GROUPBENEFITS)
    public void testAssignedAgenciesMultiple() {
        LOGGER.info("REN-21752 Preconditions");

        mainApp().open();
        customerNonIndividual.createViaUI(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        caseProfile.create(tdSpecific().getTestData("TestData_CaseProfile"), groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), PolicyInformationTab.class);

        assertSoftly(softly -> {
            LOGGER.info("REN-21752 Step 1");
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21752 Step 2");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21752 Step 4");
            policyInformationTab.getAssetList().getAsset(AGENCY_PRODUCER_COMBO).setValue(AGENCY1);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_CALL_CENTER);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(AGENCY_TYPE_CALL_CENTER);

            LOGGER.info("REN-21752 Step 5");
            policyInformationTab.getAssetList().getAsset(AGENCY_PRODUCER_COMBO).setValue(AGENCY2);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_AGENCY);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(AGENCY_TYPE_AGENCY);

            LOGGER.info("REN-21752 Step 6");
            policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS).setValue(VALUE_NO);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isAbsent();

            LOGGER.info("REN-21752 Step 7");
            policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS).setValue(VALUE_YES);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isDisabled().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21752 Step 8");
            policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.ADD_AGENCY).click();
            PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            policyInformationTab.getAssetList().getAsset(AGENCY_PRODUCER_COMBO).setValue(AGENCY1);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_CALL_CENTER);
            PolicyInformationTab.agenciesProducersTable.getRow(2).getCell(8).controls.links.get(ActionConstants.CHANGE).click();
            policyInformationTab.getAssetList().getAsset(AGENCY_PRODUCER_COMBO).setValue(AGENCY2);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(2).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_AGENCY);
        });
    }
}
