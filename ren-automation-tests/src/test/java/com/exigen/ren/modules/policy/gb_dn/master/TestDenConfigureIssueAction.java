package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingAccountSetupTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.customer.tabs.GeneralTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionIssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData.SELECT_ACTION;
import static com.exigen.ren.main.modules.policy.common.metadata.master.PlanDefinitionIssueActionTabMetaData.INCLUDE_RETIREES;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.EligibilityMetaData.MINIMUM_HOURLY_REQUIREMENT;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDenConfigureIssueAction extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23371"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureIssueActionEIN() {
        LOGGER.info("REN-23371 TC1 Preconditions");

        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), GeneralTabMetaData.EIN.getLabel()), StringUtils.EMPTY));
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        groupDentalMasterPolicy.createQuote(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());

        LOGGER.info("REN-23371 TC1 Step 3");

        NavigationPage.setActionAndGo("Issue");
        assertThat(Page.dialogConfirmation.labelMessage).hasValue("EIN is mandatory for the Group Sponsor customer record. Please complete EIN before continuing with Master Quote issue.");
        Page.dialogConfirmation.reject();
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-23415", "REN-23439"}, component = POLICY_GROUPBENEFITS)
    public void testConfigureIssueActionCountyCode() {
        LOGGER.info("REN-23415 TC1 Preconditions");

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), policyInformationTab.getClass());

        LOGGER.info("REN-23415 Precondition 4");

        policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NY");

        assertSoftly(softly -> {

            LOGGER.info("REN-23415 TC1 Step 1 Step 3");

            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).isPresent().isOptional().hasValue(StringUtils.EMPTY).isEnabled();

            LOGGER.info("REN-23415 TC1 Step 1 Step 4");

            policyInformationTab.getAssetList().getAsset(COUNTY_CODE).setValue("003 - Allegany County");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(COUNTY_CODE)).hasNoWarning();

            LOGGER.info("REN-23415 TC1 Step 6 REN-23439 Precondition 5");

            groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData().adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), COUNTY_CODE.getLabel()), StringUtils.EMPTY).adjust(TestData.makeKeyPath(PolicyInformationTab.class.getSimpleName(), GROUP_IS_AN_ASSOCIATION.getLabel()), VALUE_YES).mask(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", PlanDefinitionTabMetaData.ELIGIBILITY.getLabel(), MINIMUM_HOURLY_REQUIREMENT.getLabel())), policyInformationTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            LOGGER.info("REN-23415 TC1 Step 9");

            groupDentalMasterPolicy.propose().perform(getDefaultDNMasterPolicyData());
            groupDentalMasterPolicy.acceptContract().start();
            softly.assertThat(groupDentalMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isOptional().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-23415 TC1 Step 11");

            groupDentalMasterPolicy.acceptContract().getWorkspace().fill(getDefaultDNMasterPolicyData());
            PremiumSummaryBindActionTab.buttonNext.click();
            groupDentalMasterPolicy.issue().start();
            softly.assertThat(groupDentalMasterPolicy.getDefaultWorkspace().getTab(PolicyInformationTab.class).getAssetList().getAsset(COUNTY_CODE)).isRequired().hasValue(StringUtils.EMPTY);

            LOGGER.info("REN-23439 Step 1");

            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(groupDentalMasterPolicy.issue().getWorkspace().getTab(PlanDefinitionIssueActionTab.class).getAssetList().getAsset(INCLUDE_RETIREES)).isPresent().isDisabled().hasValue(VALUE_NO);

            LOGGER.info("REN-23439 Step 3");

            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(GROUP_IS_AN_ASSOCIATION).setValue(VALUE_NO);
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(groupDentalMasterPolicy.issue().getWorkspace().getTab(PlanDefinitionIssueActionTab.class).getAssetList().getAsset(INCLUDE_RETIREES)).isEnabled().isRequired().hasValue(VALUE_NO);

            LOGGER.info("REN-23415 TC1 Step 13");

            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get());
            policyInformationTab.getAssetList().getAsset(COUNTY_CODE).setValue("005 - Bronx County");
            groupDentalMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultDNMasterPolicyData().adjust(TestData.makeKeyPath(PlanDefinitionIssueActionTab.class.getSimpleName(), PlanDefinitionIssueActionTabMetaData.MINIMUM_HOURLY_REQUIREMENT.getLabel()), "25"), IssueActionTab.class, true);
            IssueActionTab.buttonPurchase.click();
            Page.dialogConfirmation.confirm();
            billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).getAssetList().getAsset(SELECT_ACTION).setValue("Create New Account");
            BillingAccountSetupTab.saveTab();
            BillingAccountSetupTab.buttonFinish.click();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        });
    }
}
