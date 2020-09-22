package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.PolicyConstants.PlanAccident.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestGACSICPolicyInformation extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20699"}, component = POLICY_GROUPBENEFITS)
    public void testGACSICPolicyInformation() {
        mainApp().open();

        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.SIC_CODE.getLabel()), "1521"));
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData(), planDefinitionTab.getClass());

        assertSoftly(softly -> {
            LOGGER.info("REN-20699 Step 1");
            planDefinitionTab.getAssetList().getAsset(PLAN).setValue(ImmutableList.of(BASE_BUY_UP, ENHANCED_10_UNITS, VOLUNTARY_10_UNITS));
            PlanDefinitionTab.tableCoverageDefinition.getRows().forEach(tablerow -> {
                tablerow.getCell(7).controls.links.get(ActionConstants.CHANGE).click();
                LOGGER.info("REN-20699 Step 2, step 3, step 4, step 6 and step 7");
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_CODE)).isPresent().isRequired().isEnabled().hasValue("1521");
                softly.assertThat(planDefinitionTab.getAssetList().getAsset(SIC_DESCRIPTION)).isPresent().isRequired().isEnabled().hasValue(StringUtils.EMPTY);
            });

        });
    }
}
