package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.CoveragesConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.BASIC_ACCIDENT;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.BASE_BUY_UP;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.VOLUNTARY_10_UNITS;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SIC_CODE;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.SIC_DESCRIPTION;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGACSICCodeDescription extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private static final String ENHANCED_ACC = "Enhanced Accident";
    private static final String WEB_SEARCH_PORTALS = "Web Search Portals";
    private static final String MUSEUMS = "Museums";
    private static final String SURVEYING_MAPPING = "Surveying and Mapping (except Geophysical) Services";
    private static final String MUSIC_PUBLISHERS = "Music Publishers";
    private static final String VOL_10 = "VOL10-Voluntary 10 Units";

    @Test(groups = {GB, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23455"}, component = POLICY_GROUPBENEFITS)
    public void testSICCode() {
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.SIC_CODE.getLabel()), "8999"));
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData(), planDefinitionTab.getClass());
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(BASE_BUY_UP));
        PlanDefinitionTab.tableCoverageDefinition.findRow(TableConstants.CoverageDefinition.COVERAGE_NAME.getName(), CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT)
                .getCell(7).controls.links.get(ActionConstants.CHANGE).click();

        assertSoftly(softly -> {
            LOGGER.info("Test REN-23455 Step 1");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME)).hasValue(ENHANCED_ACC);

            LOGGER.info("Test REN-23455 Step 2");
            planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION).setValue(WEB_SEARCH_PORTALS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_CODE)).hasValue("8999");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION)).hasValue(WEB_SEARCH_PORTALS);

            LOGGER.info("Test REN-23455 Step 4");
            planDefinitionTab.getAssetList().getAsset(SIC_CODE).setValue("8412");
            planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION).setValue(MUSEUMS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_CODE)).hasNoWarning();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION)).hasNoWarning();

            LOGGER.info("Test REN-23455 Step 5");
            planDefinitionTab.openAddedCoverage(ENHANCED_ACCIDENT);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_CODE)).hasValue("8412");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION)).hasValue(MUSEUMS);

            LOGGER.info("Test REN-23455 Step 6");
            planDefinitionTab.getAssetList().getAsset(SIC_CODE).setValue("8713");
            planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION).setValue(SURVEYING_MAPPING);

            LOGGER.info("Test REN-23455 Step 8");
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(BASE_BUY_UP, VOLUNTARY_10_UNITS));
            planDefinitionTab.tableCoverageDefinition.getRow(ImmutableMap.of(TableConstants.Plans.PLAN.getName(), VOL_10)).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
            planDefinitionTab.getAssetList().getAsset(SIC_CODE).setValue("8999");
            planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION).setValue(MUSIC_PUBLISHERS);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_CODE)).hasNoWarning();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION)).hasNoWarning();

            LOGGER.info("Test REN-23455 Step 9");
            planDefinitionTab.openAddedCoverage(BASIC_ACCIDENT);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_CODE)).hasValue("8713");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION)).hasValue(SURVEYING_MAPPING);
        });
    }
}
