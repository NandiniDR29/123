package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.admin.modules.agencyvendor.agency.AgencyContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.ErrorPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.TableConstants.AgenciesProducersTable.AGENCY_TYPE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.AGENCY_PRODUCER_COMBO;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAssignedAgencies extends BaseTest implements AgencyContext, CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final String AGENCY_TYPE_CALL_CENTER = "Call Center";
    private static final String AGENCY_TYPE_ERROR_MESSAGE = "Agency Type is required";
    private static final String AGENCY_TYPE_AGENCY = "Agency";
    private static final String AGENCY1= "QA Agency";
    private static final String AGENCY2 = "CCRA1";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21664"}, component = POLICY_GROUPBENEFITS)
    public void testAssignedAgencies() {

        LOGGER.info("REN-21664 Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), PolicyInformationTab.class);

        assertSoftly(softly -> {
            LOGGER.info("REN-21664 Step 1");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS)).isEnabled().hasValue(VALUE_YES);

            LOGGER.info("REN-21664 Step 2");
            Tab.buttonTopSave.click();
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_CALL_CENTER);

            LOGGER.info("REN-21664 Step 3");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(AGENCY_TYPE_CALL_CENTER);

            LOGGER.info("REN-21664 Step 9");
            policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS).setValue(VALUE_NO);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isAbsent();

            LOGGER.info("REN-21664 Step 10");
            groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData(), PolicyInformationTab.class, PolicyInformationTab.class, true);
            Tab.buttonNext.click();
            softly.assertThat(NavigationPage.PolicyNavigation.isLeftMenuTabSelected(NavigationEnum.GroupBenefitsTab.ENROLLMENT.get())).isTrue();

            LOGGER.info("REN-21664 Step 11");
            premiumSummaryTab.navigate();
            premiumSummaryTab.rate();

            Row errorRow = ErrorPage.tableError.getRowContains(ErrorPage.TableError.MESSAGE.getName(), AGENCY_TYPE_ERROR_MESSAGE);
            assertThat(errorRow)
                    .as(String.format("Row with error message: '%s'", AGENCY_TYPE_ERROR_MESSAGE))
                    .isAbsent();
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21665"}, component = POLICY_GROUPBENEFITS)
    public void testMultipleAssignedAgencies() {

        LOGGER.info("REN-21665 Preconditions");

        mainApp().open();
        customerNonIndividual.createViaUI(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        caseProfile.create(tdSpecific().getTestData("TestData_CaseProfile"), groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), PolicyInformationTab.class);

        assertSoftly(softly -> {
            LOGGER.info("REN-21665 Step 1");
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21665 Step 2");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21665 Step 3");
            policyInformationTab.getAssetList().getAsset(AGENCY_PRODUCER_COMBO).setValue(AGENCY1);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_CALL_CENTER);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(AGENCY_TYPE_CALL_CENTER);

            LOGGER.info("REN-21665 Step 5");
            policyInformationTab.getAssetList().getAsset(AGENCY_PRODUCER_COMBO).setValue(AGENCY2);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable.getRow(1).getCell(AGENCY_TYPE.getName())).hasValue(AGENCY_TYPE_AGENCY);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isPresent().isDisabled().hasValue(AGENCY_TYPE_AGENCY);

            LOGGER.info("REN-21665 Step 6");
            policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS).setValue(VALUE_NO);
            softly.assertThat(PolicyInformationTab.agenciesProducersTable).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isAbsent();

            LOGGER.info("REN-21665 Step 7");
            policyInformationTab.getAssetList().getAsset(ALLOW_INDEPENDENT_COMMISSIONABLE_PRODUCERS).setValue(VALUE_YES);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.AGENCY_TYPE)).isDisabled().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-21665 Step 8");
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
