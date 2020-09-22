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
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.tabs.EnhancedBenefitsHtoLTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupAccidentCoverages.ENHANCED_ACCIDENT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.LOSS_OF_FINGER_TOE_HAND_FOOT_OR_SIGHT_OF_AN_EYE_BENEFIT;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.EnhancedBenefitsHtoLTabMetaData.LossOfFingerToeHandFootOrSightOfAnEyeBenefitMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.metadata.PlanDefinitionTabMetaData.NUMBER_OF_UNITS;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestUpdateEnhancedBenefitsPart1 extends BaseTest implements CustomerContext, CaseProfileContext, GroupAccidentMasterPolicyContext {

    private AssetList enhancedHtoL = enhancedBenefitsHtoLTab.getAssetList().getAsset(LOSS_OF_FINGER_TOE_HAND_FOOT_OR_SIGHT_OF_AN_EYE_BENEFIT);

    @Test(groups = {GB, GB_PRECONFIGURED, GB_AC, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-16196", "REN-16210", "REN-16211", "REN-16212", "REN-16213", "REN-16216", "REN-16217", "REN-16219", "REN-16226", "REN-16289", "REN-16291", "REN-16292", "REN-16293"}, component = POLICY_GROUPBENEFITS)
    public void testUpdateEnhancedBenefits1() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        groupAccidentMasterPolicy.initiate(getDefaultACMasterPolicyData());
        groupAccidentMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultACMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getClass().getSimpleName()+"[1]", NUMBER_OF_UNITS.getLabel()), "1"), enhancedBenefitsHtoLTab.getClass());
        EnhancedBenefitsHtoLTab.planTable.getRow(ImmutableMap.of(TableConstants.Plans.COVERAGE_NAME.getName(), ENHANCED_ACCIDENT)).getCell(7).controls.links.get(ActionConstants.CHANGE).click();
        LOGGER.info("REN-16196 TC 1 Step 2-5");
        assertSoftly(softly -> {
            softly.assertThat(enhancedBenefitsHtoLTab.getAssetList().getAsset(LOSS_OF_FINGER_TOE_HAND_FOOT_OR_SIGHT_OF_AN_EYE_BENEFIT)).isPresent();
            LOGGER.info("Step 2 - REN-16196, REN-16210, REN-16211, REN-16212, REN-16213, REN-16216, REN-16217, REN-16219, REN-16226, REN-16289, REN-16291, REN-16292, REN-16293");
            softly.assertThat(enhancedHtoL.getAsset(APPLY_BENEFIT)).hasValue(true);
            enhancedHtoL.getAsset(APPLY_BENEFIT).setValue(false);
            ImmutableList.of(LOSS_OF_ONE_FINGER_OR_ONE_TOE_EMPLOYEE, LOSS_OF_ONE_FINGER_OR_ONE_TOE_SPOUSE, LOSS_OF_ONE_FINGER_OR_ONE_TOE_CHILD,
                    LOSS_OF_ONE_HAND_OR_ONE_FOOT_OR_SIGHT_OF_ONE_EYE_EMPLOYEE, LOSS_OF_ONE_HAND_OR_ONE_FOOT_OR_SIGHT_OF_ONE_EYE_SPOUSE,
                    LOSS_OF_ONE_HAND_OR_ONE_FOOT_OR_SIGHT_OF_ONE_EYE_CHILD, LOSS_OF_TWO_OR_MORE_FINGERS_TWO_OR_MORE_TOES_OR_ANY_COMBINATION_OF_TWO_OR_MORE_FINGERS_TO_TOES_EMPLOYEE,
                    LOSS_OF_TWO_OR_MORE_FINGERS_TWO_OR_MORE_TOES_OR_ANY_COMBINATION_OF_TWO_OR_MORE_FINGERS_TO_TOES_SPOUSE,
                    LOSS_OF_TWO_OR_MORE_FINGERS_TWO_OR_MORE_TOES_OR_ANY_COMBINATION_OF_TWO_OR_MORE_FINGERS_TO_TOES_CHILD,
                    LOSS_OF_BOTH_HANDS_OR_BOTH_FEET_OR_SIGHT_IN_BOTH_EYES_OR_ANY_COMBINATION_OF_TWO_EMPLOYEE, LOSS_OF_BOTH_HANDS_OR_BOTH_FEET_OR_SIGHT_IN_BOTH_EYES_OR_ANY_COMBINATION_OF_TWO_SPOUSE,
                    LOSS_OF_BOTH_HANDS_OR_BOTH_FEET_OR_SIGHT_IN_BOTH_EYES_OR_ANY_COMBINATION_OF_TWO_CHILD, INCURRAL_PERIOD_DAYS).forEach(control -> {
                softly.assertThat(enhancedHtoL.getAsset(control)).isAbsent();
            });
            LOGGER.info("Rest steps - REN-16196, REN-16210, REN-16211, REN-16212, REN-16213, REN-16216, REN-16217, REN-16219, REN-16226, REN-16289, REN-16291, REN-16292, REN-16293");
            enhancedHtoL.getAsset(EnhancedBenefitsHtoLTabMetaData.LossOfFingerToeHandFootOrSightOfAnEyeBenefitMetaData.APPLY_BENEFIT).setValue(true);
            ImmutableList.of(LOSS_OF_ONE_FINGER_OR_ONE_TOE_EMPLOYEE, LOSS_OF_ONE_FINGER_OR_ONE_TOE_SPOUSE).forEach(control ->
                    softly.assertThat(enhancedHtoL.getAsset(control)).isPresent().isRequired().hasValue(new Currency(150).toString()));
            softly.assertThat(enhancedHtoL.getAsset(LOSS_OF_ONE_FINGER_OR_ONE_TOE_CHILD)).isPresent().isRequired().hasValue((new Currency(50).toString()));
            ImmutableList.of(LOSS_OF_ONE_HAND_OR_ONE_FOOT_OR_SIGHT_OF_ONE_EYE_EMPLOYEE, LOSS_OF_ONE_HAND_OR_ONE_FOOT_OR_SIGHT_OF_ONE_EYE_SPOUSE).forEach(control ->
                    softly.assertThat(enhancedHtoL.getAsset(control)).isPresent().isRequired().hasValue(new Currency(1500).toString()));
            softly.assertThat(enhancedHtoL.getAsset(LOSS_OF_ONE_HAND_OR_ONE_FOOT_OR_SIGHT_OF_ONE_EYE_CHILD)).isPresent().isRequired().hasValue(new Currency(500).toString());
            ImmutableList.of(LOSS_OF_TWO_OR_MORE_FINGERS_TWO_OR_MORE_TOES_OR_ANY_COMBINATION_OF_TWO_OR_MORE_FINGERS_TO_TOES_EMPLOYEE, LOSS_OF_TWO_OR_MORE_FINGERS_TWO_OR_MORE_TOES_OR_ANY_COMBINATION_OF_TWO_OR_MORE_FINGERS_TO_TOES_SPOUSE)
                    .forEach(control -> softly.assertThat(enhancedHtoL.getAsset(control)).isRequired().hasValue(new Currency(300).toString()));
            softly.assertThat(enhancedHtoL.getAsset(LOSS_OF_TWO_OR_MORE_FINGERS_TWO_OR_MORE_TOES_OR_ANY_COMBINATION_OF_TWO_OR_MORE_FINGERS_TO_TOES_CHILD)).isPresent().isRequired().hasValue(new Currency(100).toString());
            softly.assertThat(enhancedHtoL.getAsset(LOSS_OF_BOTH_HANDS_OR_BOTH_FEET_OR_SIGHT_IN_BOTH_EYES_OR_ANY_COMBINATION_OF_TWO_CHILD)).isPresent().isRequired().hasValue(new Currency(1000).toString());
            ImmutableList.of(LOSS_OF_BOTH_HANDS_OR_BOTH_FEET_OR_SIGHT_IN_BOTH_EYES_OR_ANY_COMBINATION_OF_TWO_EMPLOYEE, LOSS_OF_BOTH_HANDS_OR_BOTH_FEET_OR_SIGHT_IN_BOTH_EYES_OR_ANY_COMBINATION_OF_TWO_SPOUSE)
                    .forEach(control -> softly.assertThat(enhancedHtoL.getAsset(control)).isPresent().isRequired().hasValue(new Currency(3000).toString()));
            softly.assertThat(enhancedHtoL.getAsset(INCURRAL_PERIOD_DAYS)).isPresent().isRequired().hasValue("365");
        });
    }
}