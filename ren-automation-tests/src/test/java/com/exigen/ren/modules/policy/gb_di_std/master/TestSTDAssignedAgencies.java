package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.TableConstants.AgenciesProducersTable.AGENCY_TYPE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.AGENCY_PRODUCER_COMBO;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSTDAssignedAgencies extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    private static final String AGENCY_TYPE_CALL_CENTER = "Call Center";
    private static final String AGENCY_TYPE_ERROR_MESSAGE = "Agency Type is required";
    private static final String AGENCY_TYPE_AGENCY = "Agency";
    private static final String AGENCY1 = "QA Agency";
    private static final String AGENCY2 = "CCRA1";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-21504", component = POLICY_GROUPBENEFITS)
    public void testAssignedAgenciesSingle() {
        LOGGER.info("REN-21504 Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), PolicyInformationTab.class);

        assertSoftly(softly -> {
            LOGGER.info("REN-21504 Step 1");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS)).isEnabled().hasValue(VALUE_YES);

            LOGGER.info("REN-21504 Step 2");
            Tab.buttonTopSave.click();
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_CALL_CENTER);

            LOGGER.info("REN-21504 Step 3");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(AGENCY_TYPE_CALL_CENTER);

            LOGGER.info("REN-21504 Step 8");
            policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS).setValue(VALUE_NO);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isAbsent();

            LOGGER.info("REN-21504 Step 9");
            premiumSummaryTab.navigate();
            premiumSummaryTab.rate();

            Row errorRow = ErrorPage.tableError.getRowContains(ErrorPage.TableError.MESSAGE.getName(), AGENCY_TYPE_ERROR_MESSAGE);
            softly.assertThat(errorRow)
                    .as(String.format("Row with error message: '%s'", AGENCY_TYPE_ERROR_MESSAGE))
                    .isAbsent();
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-21649", component = POLICY_GROUPBENEFITS)
    public void testAssignedAgenciesMultiple() {
        LOGGER.info("REN-21649 Preconditions");

        mainApp().open();
        customerNonIndividual.createViaUI(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        caseProfile.create(tdSpecific().getTestData("TestData_CaseProfile"), shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
        shortTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultSTDMasterPolicyData(), PolicyInformationTab.class);

        assertSoftly(softly -> {
            LOGGER.info("REN-21649 Step 1");
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21649 Step 2");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21649 Step 4");
            policyInformationTab.getAssetList().getAsset(AGENCY_PRODUCER_COMBO).setValue(AGENCY1);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_CALL_CENTER);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(AGENCY_TYPE_CALL_CENTER);

            LOGGER.info("REN-21649 Step 5");
            policyInformationTab.getAssetList().getAsset(AGENCY_PRODUCER_COMBO).setValue(AGENCY2);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_AGENCY);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(AGENCY_TYPE_AGENCY);

            LOGGER.info("REN-21649 Step 6");
            policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS).setValue(VALUE_NO);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isAbsent();

            LOGGER.info("REN-21649 Step 7");
            policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS).setValue(VALUE_YES);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isDisabled().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21649 Step 8");
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
