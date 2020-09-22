package com.exigen.ren.modules.policy.gb_ac.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.EnhancedBenefitsAtoCTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.CATASTROPHIC_ACCIDENT_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsAtoCTabMetaData.CatastrophicAccidentBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.NUMBER_OF_UNITS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUpdateEnhancedBenefits2 extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {
    private AssetList enhancedBenefits2 = enhancedBenefitsAtoCTab.getAssetList().getAsset(CATASTROPHIC_ACCIDENT_BENEFIT);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-16800", "REN-16802"}, component = POLICY_GROUPBENEFITS)
    public void testUpdateEnhancedBenefits2() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName()+"[1]", NUMBER_OF_UNITS.getLabel()), "1"), enhancedBenefitsAtoCTab.getClass());
        EnhancedBenefitsAtoCTab.planTable.getRow(ImmutableMap.of(TableConstants.Plans.COVERAGE_NAME.getName(), ENHANCED_ACCIDENT)).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        assertSoftly(softly -> {
            LOGGER.info("REN-16800 Step 1-2");
            LOGGER.info("REN-16802 Step 2");
            softly.assertThat(enhancedBenefits2.getAsset(EnhancedBenefitsAtoCTabMetaData.CatastrophicAccidentBenefitMetaData.APPLY_BENEFIT)).hasValue(true);
            ImmutableList.of(CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_EMPLOYEE, CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_SPOUSE).forEach(control -> {
                softly.assertThat(enhancedBenefits2.getAsset(control)).isPresent().isRequired().hasValue(new Currency("10000").toString());
            });
            ImmutableList.of(CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_CHILD, CATASTROPHIC_BENEFIT_AMOUNT_ON_OR_AFTER_AGE_70_EMPLOYEE, CATASTROPHIC_BENEFIT_AMOUNT_ON_OR_AFTER_AGE_70_SPOUSE).forEach(control -> {
                softly.assertThat(enhancedBenefits2.getAsset(control)).isPresent().isRequired().hasValue(new Currency("5000").toString());
            });
            LOGGER.info("REN-16802 Step 1");
            softly.assertThat(enhancedBenefits2.getAsset(ELIMINATION_PERIOD_DAYS)).isPresent().isRequired().hasValue("365");
            LOGGER.info("REN-16802 Step 5");
            enhancedBenefits2.getAsset(APPLY_BENEFIT).setValue(false);
            ImmutableList.of(CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_EMPLOYEE, CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_SPOUSE, CATASTROPHIC_BENEFIT_AMOUNT_PRIOR_TO_AGE_70_CHILD,
                    CATASTROPHIC_BENEFIT_AMOUNT_ON_OR_AFTER_AGE_70_EMPLOYEE, CATASTROPHIC_BENEFIT_AMOUNT_ON_OR_AFTER_AGE_70_SPOUSE).forEach(control -> {
                softly.assertThat(enhancedBenefits2.getAsset(control)).isAbsent();
            });
        });
    }
}
