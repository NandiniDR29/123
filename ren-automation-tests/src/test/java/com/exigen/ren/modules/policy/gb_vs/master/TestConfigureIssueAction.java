package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.PlanDefinitionIssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION;
import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.PolicyConstants.PlanVision.A_LA_CARTE;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionIssueActionTabMetaData.INCLUDE_RETIREES;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.FREQUENCY;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.FrequencyMetadata.FREQUENCY_DEFINITION;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestConfigureIssueAction extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23369"}, component = POLICY_GROUPBENEFITS)
    public void configureIssueAction() {
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.EIN.getLabel()), StringUtils.EMPTY));
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.createQuote(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.acceptContract().perform(getDefaultVSMasterPolicyData());

        LOGGER.info("Test REN-23369 Step 3");
        NavigationPage.setActionAndGo("Issue");
        assertThat(Page.dialogConfirmation.labelMessage).hasValue("EIN is mandatory for the Group Sponsor customer record. Please complete EIN before continuing with Master Quote issue.");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23375", "REN-23443", "REN-23453", "REN-23462", "REN-22117"}, component = POLICY_GROUPBENEFITS)
    public void testVerifyCountyCodeDropDown() {
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), policyInformationTab.getClass());

        assertSoftly(softly -> {
            LOGGER.info("Test REN-23375 TC1 Step 1 and TC1 Step 3");
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NY");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).isPresent().isEnabled().isOptional().hasValue(StringUtils.EMPTY);

            LOGGER.info("Test REN-23443 TC1 Step 1 and TC1 Step 2");
            assertThat(policyInformationTab.getAssetList().getAsset(ZIP_CODE)).isRequired().isEnabled().isPresent().hasValue("90210");

            LOGGER.info("Test REN-23375 TC1 Step 4");
            policyInformationTab.getAssetList().getAsset(COUNTY_CODE).setValue("005 - Bronx County");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).hasNoWarning();

            LOGGER.info("Test REN-23375 TC1 Step 6 and REN-23453 TC1 Step 1");
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData().adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), COUNTY_CODE.getLabel()), StringUtils.EMPTY), policyInformationTab.getClass(), planDefinitionTab.getClass());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.PLAN).setValue(ImmutableList.of(A_LA_CARTE));
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(FREQUENCY).getAsset(FREQUENCY_DEFINITION)).hasOptions(StringUtils.EMPTY, "Calendar Year", "Service Year");
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), planDefinitionTab.getClass(), premiumSummaryTab.getClass(), true);
            LOGGER.info("Test REN-22117");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Area Factor Version"))).isAbsent();

            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("Test REN-23375 TC1 Step 9");
            groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
            groupVisionMasterPolicy.acceptContract().start();
            softly.assertThat(groupVisionMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isOptional().hasValue(StringUtils.EMPTY);

            LOGGER.info("Test REN-23375 TC1 Step 11");
            groupVisionMasterPolicy.acceptContract().getWorkspace().fill(getDefaultVSMasterPolicyData());
            PremiumSummaryBindActionTab.buttonNext.click();
            groupVisionMasterPolicy.issue().start();
            softly.assertThat(groupVisionMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isRequired().hasValue(StringUtils.EMPTY);

            LOGGER.info("Test REN-23462 TC1 Step 1");
            NavigationPage.toLeftMenuTab(PLAN_DEFINITION);
            assertThat(groupVisionMasterPolicy.issue().getWorkspace().getTab(PlanDefinitionIssueActionTab.class).getAssetList().getAsset(INCLUDE_RETIREES)).isDisabled().isPresent().hasValue(VALUE_NO);

            LOGGER.info("Test REN-23462 TC1 Step 3");
            NavigationPage.toLeftMenuTab(POLICY_INFORMATION);
            policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_NO);
            NavigationPage.PolicyNavigation.leftMenu(PLAN_DEFINITION.get());
            assertThat(groupVisionMasterPolicy.issue().getWorkspace().getTab(PlanDefinitionIssueActionTab.class).getAssetList().getAsset(INCLUDE_RETIREES)).isEnabled().isRequired().hasValue(VALUE_NO);

            LOGGER.info("Test REN-23462 TC1 Step 4");
            groupVisionMasterPolicy.issue().getWorkspace().getTab(PlanDefinitionIssueActionTab.class).getAssetList().getAsset(INCLUDE_RETIREES).setValue(VALUE_YES);
            assertThat(groupVisionMasterPolicy.issue().getWorkspace().getTab(PlanDefinitionIssueActionTab.class).getAssetList().getAsset(INCLUDE_RETIREES)).hasNoWarning();

            LOGGER.info("Test REN-23375 Step TC1 14");
            NavigationPage.toLeftMenuTab(POLICY_INFORMATION);
            policyInformationTab.getAssetList().getAsset(COUNTY_CODE).setValue("005 - Bronx County");
            groupVisionMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultVSMasterPolicyData()
                    .adjust(TestData.makeKeyPath(PlanDefinitionIssueActionTab.class.getSimpleName(), PlanDefinitionIssueActionTabMetaData.MINIMUM_HOURLY_REQUIREMENT.getLabel()), "25"), BillingAccountTab.class, true);
            groupVisionMasterPolicy.issue().getWorkspace().getTab(BillingAccountTab.class).submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23443"}, component = POLICY_GROUPBENEFITS)
    public void testValidateZIPCodeTwoAddress() {
        mainApp().open();
        customerNonIndividual.create(tdSpecific().getTestData("TestData_TwoAddress"));
        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), policyInformationTab.getClass());

        LOGGER.info("Test REN-23443 TC2 Step 1 and TC2 Step 2");
        assertThat(policyInformationTab.getAssetList().getAsset(ZIP_CODE)).isRequired().isEnabled().isPresent().hasValue("63745");
    }
}
