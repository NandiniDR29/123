package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.modules.policy.common.actions.common.EndorseAction;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyChangesToSupportNonStandardDentalPlans extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private AssetList exclusionAsset = planDefinitionTab.getAssetList()
            .getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY)
            .getAsset(PlanDefinitionTabMetaData.LimitationFrequencyMetaData.EXCLUSIONS);

    private AssetList dentalMajorAsset = planDefinitionTab.getAssetList()
            .getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY)
            .getAsset(PlanDefinitionTabMetaData.LimitationFrequencyMetaData.DENTAL_MAJOR);

    private AssetList preventAndDiagnosticAsset = planDefinitionTab.getAssetList()
            .getAsset(PlanDefinitionTabMetaData.LIMITATION_FREQUENCY)
            .getAsset(PlanDefinitionTabMetaData.LimitationFrequencyMetaData.DENTAL_PREVENT_AND_DIAGNOSTIC);

    private String NOT_COVERED = "Not Covered";
    private String COVERED_NON_SURGICAL = "Covered (Non-Surgical)";
    private String COVERED_SURGICAL = "Covered (Surgical)";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-42424", "REN-42452", "REN-42455"}, component = POLICY_GROUPBENEFITS)
    public void testAttributesVerification() {
        LOGGER.info("REN-42424, REN-42452, REN-42455  Precondition");
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiateAndFillUpToTab(getDefaultDNMasterPolicyData(), PlanDefinitionTab.class, true);
        String quoteNumber = QuoteSummaryPage.getQuoteNumber();
        planDefinitionTab.selectDefaultPlan();

        assertSoftly(softly -> {
            LOGGER.info("REN-42424 TC#1 Step 1");
            softly.assertThat(dentalMajorAsset
                    .getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.LIMITED_OCCLUSAL_ADJUSTMENTS).getName()).isEqualTo("Limited Occlusal Adjustments");

            LOGGER.info("REN-42424 TC#2 Step 1");
            softly.assertThat(exclusionAsset
                    .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.COMPLETE_OCCLUSAL_ADJUSTMENT)).isPresent().isOptional().isDisabled().hasValue(true);

            LOGGER.info("REN-42424 TC#2 Step 2");
            softly.assertThat(exclusionAsset
                    .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.PULP_CAPS)).isPresent().isOptional().isDisabled().hasValue(true);

            LOGGER.info("REN-42424 TC#2 Step 3");
            softly.assertThat(exclusionAsset
                    .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.PULPAL_THERAPY)).isPresent().isOptional().isDisabled().hasValue(true);

            LOGGER.info("REN-42424 TC#2 Step 4");
            softly.assertThat(exclusionAsset
                    .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.EXCISION_OF_BONE_TISSUE)).isPresent().isOptional().isDisabled().hasValue(true);

            LOGGER.info("REN-42424 TC#2 Step 5");
            softly.assertThat(exclusionAsset
                    .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.BIOPSY_LESION_VESTIBULOPLASTY)).isPresent().isOptional().isDisabled().hasValue(true);

            LOGGER.info("REN-42424 TC#2 Step 6");
            softly.assertThat(exclusionAsset
                    .getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.ARESTIN)).isPresent().isOptional().isDisabled().hasValue(true);

            LOGGER.info("REN-42424 TC#2 Step 7, REN-42455 Step 1");
            softly.assertThat(preventAndDiagnosticAsset
                    .getAsset(PlanDefinitionTabMetaData.DentalPreventAndDiagnosticMetaData.INCLUDE_BICUSPID_SEALANT)).isPresent().isRequired().isDisabled().hasValue("No");

            LOGGER.info("REN-42424 TC#2 Step 8");
            checkAttributesAreDisabled(softly);
            dentalMajorAsset.getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMD).setValue(NOT_COVERED);
            softly.assertThat(dentalMajorAsset
                    .getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMJ_DIAGNOSTICS_XRAY)).isAbsent();

            CustomAssertions.assertThat(DBService.get()
                    .getValue("SELECT tmjDiagnosticsXray FROM PreconfigGroupDentalMajor pgdm WHERE pgdm.connectedToInstanceName = ( SELECT tmjDiagnosticsXray FROM PreconfigGroupDentalMajor pgdm WHERE pgdm.connectedToInstanceName = (SELECT top 1 gcd.instanceName FROM GroupCoverageDefinition gcd LEFT JOIN PolicySummary ps ON gcd.connectedToInstanceName = ps.instanceName WHERE ps.policyNumber = ? order by ps.createdOn desc))", quoteNumber)).isEmpty();
        });

        LOGGER.info("REN-42452 Step 2");
        Tab.buttonNext.click();
        groupDentalMasterPolicy.getDefaultWorkspace().fillFrom(getDefaultDNMasterPolicyData(), ClassificationManagementTab.class);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        LOGGER.info("REN-42452 Step 4");
        groupDentalMasterPolicy.dataGather().start();
        planDefinitionTab.navigateToTab();
        dentalMajorAsset.getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMD).setValue(NOT_COVERED);
        CustomAssertions.assertThat(dentalMajorAsset.getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMJ_DIAGNOSTICS_XRAY)).isPresent(false);

        LOGGER.info("REN-42452 Step 5");
        assertSoftly(this::checkAttributesAreDisabled);
        premiumSummaryTab.navigate();
        groupDentalMasterPolicy.dataGather().getWorkspace().fillFrom(getDefaultDNMasterPolicyData(), PremiumSummaryTab.class);

        LOGGER.info("REN-42452 Step 6, REN-42455 Step 2,3");
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.issue().perform(getDefaultDNMasterPolicyData());
        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("REN-42452 Step 8");
        EndorseAction.startEndorsementForPolicy(GroupBenefitsMasterPolicyType.GB_DN, groupDentalMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, DEFAULT_TEST_DATA_KEY));
        planDefinitionTab.navigateToTab();
        assertSoftly(softly -> {
            LOGGER.info("REN-42455 Step 4 'TMJ Diagnostics - Xray' and 'Include Bicuspid Sealant' are Required");
            ImmutableList.of(COVERED_NON_SURGICAL, COVERED_SURGICAL)
                    .forEach(value -> {
                        dentalMajorAsset.getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMD).setValue(value);
                        softly.assertThat(dentalMajorAsset
                                .getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMJ_DIAGNOSTICS_XRAY))
                                .isPresent().isRequired().isEnabled().hasValue("Once Every 3 Years")
                                .hasOptions("Once Every 12 Months", "Once Every 2 Years", "Once Every 3 Years", "Six Every 5 Years");

                        LOGGER.info("REN-42452 Step 1");
                        softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.COMPLETE_OCCLUSAL_ADJUSTMENT)).isEnabled();
                        softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.PULP_CAPS)).isEnabled();
                        softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.PULPAL_THERAPY)).isEnabled();
                        softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.EXCISION_OF_BONE_TISSUE)).isEnabled();
                        softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.BIOPSY_LESION_VESTIBULOPLASTY)).isEnabled();
                        softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.ARESTIN)).isEnabled();
                        softly.assertThat(preventAndDiagnosticAsset.getAsset(PlanDefinitionTabMetaData.DentalPreventAndDiagnosticMetaData.INCLUDE_BICUSPID_SEALANT)).isRequired().isEnabled();
                        softly.assertThat(dentalMajorAsset.getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMJ_DIAGNOSTICS_XRAY)).isEnabled();
                    });
        });
        Tab.buttonSaveAndExit.click();

        LOGGER.info("REN-42452 Step 10");
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalMasterPolicy.dataGather().start();
        planDefinitionTab.navigateToTab();
        dentalMajorAsset.getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMD).setValue(NOT_COVERED);
        CustomAssertions.assertThat(dentalMajorAsset.getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMJ_DIAGNOSTICS_XRAY)).isAbsent();
        premiumSummaryTab.navigate();
        groupDentalMasterPolicy.dataGather().getWorkspace().fillFrom(getDefaultDNMasterPolicyData(), PremiumSummaryTab.class);

        LOGGER.info("REN-42452 Step 11");
        PolicySummaryPage.buttonPendedEndorsement.click();
        groupDentalMasterPolicy.issue().perform(getDefaultDNMasterPolicyData()
                .adjust(BillingAccountTab.class.getSimpleName(), new ArrayList<>(Collections.singletonList(new SimpleDataProvider()))));
        //TODO(dkliuchenia): after the implementation of the functionality 'Rate Guarantee Renewal' REN-394 should be done REN-42452 Step 11 and REN-42455 Step 5

    }

    private void checkAttributesAreDisabled(ETCSCoreSoftAssertions softly) {
        ImmutableList.of(COVERED_NON_SURGICAL, COVERED_SURGICAL)
                .forEach(value -> {
                    LOGGER.info("REN-42455 Step 1 'TMJ Diagnostics - Xray' is mandatory field");
                    dentalMajorAsset.getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMD).setValue(value);
                    softly.assertThat(dentalMajorAsset
                            .getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMJ_DIAGNOSTICS_XRAY))
                            .isPresent().isRequired(true).isDisabled().hasValue("Once Every 3 Years")
                            .hasOptions("Once Every 12 Months", "Once Every 2 Years", "Once Every 3 Years", "Six Every 5 Years");

                    LOGGER.info("REN-42452 Step 1");
                    softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.COMPLETE_OCCLUSAL_ADJUSTMENT)).isDisabled();
                    softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.PULP_CAPS)).isDisabled();
                    softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.PULPAL_THERAPY)).isDisabled();
                    softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.EXCISION_OF_BONE_TISSUE)).isDisabled();
                    softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.BIOPSY_LESION_VESTIBULOPLASTY)).isDisabled();
                    softly.assertThat(exclusionAsset.getAsset(PlanDefinitionTabMetaData.ExclusionsMetaData.ARESTIN)).isDisabled();
                    softly.assertThat(preventAndDiagnosticAsset
                            .getAsset(PlanDefinitionTabMetaData.DentalPreventAndDiagnosticMetaData.INCLUDE_BICUSPID_SEALANT)).isDisabled();
                    softly.assertThat(dentalMajorAsset.getAsset(PlanDefinitionTabMetaData.DentalMajorMetaData.TMJ_DIAGNOSTICS_XRAY)).isDisabled();
                });
    }
}
