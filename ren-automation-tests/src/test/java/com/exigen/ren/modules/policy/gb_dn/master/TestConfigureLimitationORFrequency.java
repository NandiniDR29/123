package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CO_INSURANCE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CoInsuranceMetaData.IS_IT_GRADED_CO_INSURANCE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.CoInsuranceMetaData.MAJOR_IN_NETWORK;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalBasicMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalMajorMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalPreventAndDiagnosticMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalProsthodonticsMetadata.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalRadiographsMetadata.BITEWINGS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.DentalRadiographsMetadata.FULL_MOUTH_RADIOGRAPHS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.LIMITATION_FREQUENCY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.LimitationFrequencyMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureLimitationORFrequency extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private static final AssetList limitationFrequencyAssetList = planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY);
    private static final String NOT_COVERED = "Not Covered";
    private static final String ONE_PER_YEAR = "1 Per Year";
    private static final String FIFTY_PERCENTAGE = "50%";
    private static final String SIXTY_PERCENTAGE = "60%";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-18942", "REN-19073", "REN-19154"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureLimitationORFrequency() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), planDefinitionTab.getClass());
        selectFirstPlanFromDNMasterPolicyDefaultTestData();

        assertSoftly(softly -> {
            LOGGER.info("REN-18942 TC1 Step 1, 2, 10");
            ImmutableList.of(DENTAL_PREVENT_AND_DIAGNOSTIC, DENTAL_BASIC, DENTAL_MAJOR, DENTAL_PROSTHODONTICS,
                    DENTAL_RADIOGRAPHS).forEach(control -> softly.assertThat(limitationFrequencyAssetList
                    .getAsset(control)).isPresent().isEnabled().isRequired());

            LOGGER.info("REN-18942 TC1 Step 3, 10");
            ImmutableList.of(ORAL_EXAMINATION, PROPHYLAXES,
                    PROPHYLAXES_WITH_HISTORY, FLOURIDE_TREATMENT, FLOURIDE_TREATMENT_AGE_LIMIT,
                    SPACE_MAINTAINERS, SPACE_MAINTAINERS_AGE_LIMIT,
                    BRUSH_BIOPSY, SEALANTS, SEALANTS_AGE_LIMIT,
                    EVIDENCE_BASED_DENTISTRY).forEach(control -> softly.assertThat(planDefinitionTab
                    .getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(control))
                    .isPresent().isEnabled().isRequired());

            LOGGER.info("REN-18942 TC1 Step 4, 10");
            ImmutableList.of(FULL_MOUTH_RADIOGRAPHS, BITEWINGS).forEach(control ->
                    softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                            .getAsset(DENTAL_RADIOGRAPHS).getAsset(control)).isPresent().isEnabled().isRequired());

            LOGGER.info("REN-18942 TC1 Step 5, 10");
            ImmutableList.of(POSTERIOR_COMPOSITES, OCCLUSAL_GUARD, ATHLETIC_MOUTHGUARDS, THIRD_MOLAR_REMOVAL, AMALGAM_AND_COMPOSITE_RESIN_FILLINGS)
                    .forEach(control -> softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                            .getAsset(DENTAL_BASIC).getAsset(control)).isPresent().isEnabled().isRequired());

            LOGGER.info("REN-18942 TC1 Step 6, 10");
            ImmutableList.of(CROWNS, INLAYS, VENEERS, FULL_MOUTH_DEBRIDEMENT, PERIODONTAL_SURGERY,
                    ROOT_CANALS, SCALING_AND_ROOT_PLANNING, LIMITED_OCCLUSAL_ADJUSTMENTS, TMD)
                    .forEach(control -> softly.assertThat(planDefinitionTab.getAssetList()
                            .getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR).getAsset(control))
                            .isPresent().isEnabled().isRequired());

            LOGGER.info("REN-18942 TC1 Step 7, 10");
            ImmutableList.of(BRIDGEWORK, DENTURES, RELINES,
                    IMPLANTS).forEach(control -> softly.assertThat(planDefinitionTab.getAssetList()
                    .getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_PROSTHODONTICS).getAsset(control))
                    .isPresent().isEnabled().isRequired());

            LOGGER.info("REN-19073 TC1 Step 16");
            groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData(),
                    PlanDefinitionTab.class, PremiumSummaryTab.class);
            premiumSummaryTab.submitTab();
            groupDentalMasterPolicy.dataGather().start();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC)
                    .getAsset(FLOURIDE_TREATMENT).setValue(NOT_COVERED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                    .getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(FLOURIDE_TREATMENT_AGE_LIMIT)).isAbsent();

            LOGGER.info("REN-19073 TC1 Step 17");
            planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC)
                    .getAsset(SPACE_MAINTAINERS).setValue(NOT_COVERED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                    .getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(SPACE_MAINTAINERS_AGE_LIMIT)).isAbsent();

            LOGGER.info("REN-19073 TC1 Step 18");
            planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC)
                    .getAsset(SEALANTS).setValue(NOT_COVERED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                    .getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(SEALANTS_AGE_LIMIT)).isAbsent();

            LOGGER.info("REN-19073 TC1 Step 19");
            planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR)
                    .getAsset(TMD).setValue(NOT_COVERED);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                    .getAsset(DENTAL_MAJOR).getAsset(TMD_COINSURANCE)).isAbsent();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR)
                    .getAsset(TMD_MAXIMUM)).isAbsent();

            LOGGER.info("REN-19073 TC1 Step 24");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY.get());
            Tab.buttonSaveAndExit.click();
            groupDentalMasterPolicy.dataGather().start();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC)
                    .getAsset(FLOURIDE_TREATMENT).setValue(ONE_PER_YEAR);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                    .getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(FLOURIDE_TREATMENT_AGE_LIMIT))
                    .isPresent().hasValue("19");

            LOGGER.info("REN-19073 TC1 Step 25");
            planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC)
                    .getAsset(SPACE_MAINTAINERS).setValue(ONE_PER_YEAR);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                    .getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(SPACE_MAINTAINERS_AGE_LIMIT))
                    .isPresent().hasValue("14");

            LOGGER.info("REN-19073 TC1 Step 26");
            planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC)
                    .getAsset(SEALANTS).setValue(ONE_PER_YEAR);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                    .getAsset(DENTAL_PREVENT_AND_DIAGNOSTIC).getAsset(SEALANTS_AGE_LIMIT))
                    .isPresent().hasValue("16");

            LOGGER.info("REN-19073 TC1 Step 27");
            planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR)
                    .getAsset(TMD).setValue("Covered (Non-Surgical)");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                    .getAsset(DENTAL_MAJOR).getAsset(TMD_COINSURANCE)).isPresent().hasValue(FIFTY_PERCENTAGE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                    .getAsset(DENTAL_MAJOR).getAsset(TMD_MAXIMUM)).isPresent().hasValue("$500");
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-19154"}, component = POLICY_GROUPBENEFITS)
    public void testVerifyDefaultValueForTMDCoInsurance() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData(), PolicyInformationTab.class, true);

        assertSoftly(softly -> {
            LOGGER.info("REN-19154 TC1 Step 1");
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("MN");
            groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData(),
                    PolicyInformationTab.class, PlanDefinitionTab.class, true);
            planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(IS_IT_GRADED_CO_INSURANCE)
                    .setValue(ValueConstants.VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY)
                    .getAsset(DENTAL_MAJOR).getAsset(TMD_COINSURANCE)).hasValue(FIFTY_PERCENTAGE);

            LOGGER.info("REN-19154 TC1 Step 2");
            planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(IS_IT_GRADED_CO_INSURANCE)
                    .setValue(ValueConstants.VALUE_NO);
            planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(MAJOR_IN_NETWORK).setValue(FIFTY_PERCENTAGE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR)
                    .getAsset(TMD_COINSURANCE)).hasValue(FIFTY_PERCENTAGE);

            LOGGER.info("REN-19154 TC1 Step 3");
            planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(MAJOR_IN_NETWORK).setValue(SIXTY_PERCENTAGE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(MAJOR_IN_NETWORK))
                    .hasValue((planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR)
                            .getAsset(TMD_COINSURANCE)).getValue());

            LOGGER.info("REN-19154 TC1 Step 4");
            planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(MAJOR_IN_NETWORK).setValue("40%");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR)
                    .getAsset(TMD_COINSURANCE)).hasValue(FIFTY_PERCENTAGE);

            LOGGER.info("REN-19154 TC1 Step 7");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get(), Tab.doubleWaiter);
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("AK");
            groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData(),
                    PolicyInformationTab.class, PlanDefinitionTab.class, true);
            planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR)
                    .getAsset(TMD).setValue("Covered (Non-Surgical)");
            planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(MAJOR_IN_NETWORK).setValue("0%");
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(MAJOR_IN_NETWORK))
                    .hasValue((planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR)
                            .getAsset(TMD_COINSURANCE)).getValue());

            LOGGER.info("REN-19154 TC1 Step 8");
            planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(MAJOR_IN_NETWORK).setValue(SIXTY_PERCENTAGE);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(MAJOR_IN_NETWORK))
                    .hasValue((planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR)
                            .getAsset(TMD_COINSURANCE)).getValue());

            LOGGER.info("REN-19154 TC1 Step 11");
            planDefinitionTab.getAssetList().getAsset(CO_INSURANCE).getAsset(IS_IT_GRADED_CO_INSURANCE)
                    .setValue(ValueConstants.VALUE_YES);
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(LIMITATION_FREQUENCY).getAsset(DENTAL_MAJOR)
                    .getAsset(TMD_COINSURANCE)).hasValue(FIFTY_PERCENTAGE);
        });
    }
}