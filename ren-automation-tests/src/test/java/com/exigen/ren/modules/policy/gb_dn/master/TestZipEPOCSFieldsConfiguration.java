package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.BENEFIT_WAITING_PERIODS;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitWaitingPeriodsMetaData.EPCOS_WAITING_PERIOD;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestZipEPOCSFieldsConfiguration extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private ETCSCoreSoftAssertions softly;
    private static String DEFAULT_ZIPCODE_FIVE = "90210";
    private static String DEFAULT_ZIPCODE_NINE = "90210-0806";
    private static String DEFAULT_ZIPCODE_FIVE_NONUSA = "11111";
    private static ImmutableList<String> WAITING_PERIOD_VALUES = ImmutableList.of("None", "6 months", "12 months", "18 months", "24 months");
    private static String COUNTRY_USA = "United States";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-16157", "REN-16220"}, component = POLICY_GROUPBENEFITS)
    public void testZipEPOCSFieldsConfiguration() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        initiateQuoteAndFillUpToTab(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY), PolicyInformationTab.class, false);
        assertSoftly(softly -> {
            this.softly = softly;
            LOGGER.info("---=={REN-16157 TC1 Step 2}==---");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.ZIP_CODE)).hasValue(DEFAULT_ZIPCODE_FIVE);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.COUNTRY)).hasValue(COUNTRY_USA);
            LOGGER.info("---=={REN-16157 TC1 Step 5,7}==---");
            validateEPOCSWaitingPeriod();
            Tab.buttonTopCancel.click();
            Page.dialogConfirmation.buttonYes.click();
            LOGGER.info("---=={REN-16157 TC2 Precondition}==---");
            createCustomerFromTestDataAndInitiateQuote("TestData_AddressPreferredYes");
            LOGGER.info("---=={REN-16157 TC2 Step 2}==---");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.ZIP_CODE)).hasValue(DEFAULT_ZIPCODE_NINE);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.COUNTRY)).hasValue(COUNTRY_USA);
            LOGGER.info("---=={REN-16157 TC2 Step 4,5,6}==---");
            validateEPOCSWaitingPeriod();
            Tab.buttonTopCancel.click();
            Page.dialogConfirmation.buttonYes.click();
            LOGGER.info("---=={REN-16220 TC1 Precondition}==---");
            createCustomerFromTestDataAndInitiateQuote("TestData_AddressNonUSA");
            LOGGER.info("---=={REN-16220 TC1 Step 2}==---");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.ZIP_CODE)).hasValue(DEFAULT_ZIPCODE_FIVE_NONUSA);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.COUNTRY)).hasValue(COUNTRY_USA);
            LOGGER.info("---=={REN-16220 TC2 Precondition}==---");
            Tab.buttonTopCancel.click();
            Page.dialogConfirmation.buttonYes.click();
            createCustomerFromTestDataAndInitiateQuote("TestData_AddressNonUSAPreferredYes");
            LOGGER.info("---=={REN-16220 TC2 Step 2}==---");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.ZIP_CODE)).hasValue(DEFAULT_ZIPCODE_FIVE_NONUSA);
            softly.assertThat(policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.COUNTRY)).hasValue(COUNTRY_USA);
        });
    }

    private void createCustomerFromTestDataAndInitiateQuote(String testDataKey) {
        customerNonIndividual.create(tdSpecific().getTestData(testDataKey));
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        initiateQuoteAndFillUpToTab(getDefaultDNMasterPolicyData(), PolicyInformationTab.class, true);
    }

    private void validateEPOCSWaitingPeriod() {
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        planDefinitionTab.getAssetList().getAsset(PLAN).fill(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY).getTestDataList(planDefinitionTab.getClass().getSimpleName()).get(0));
        ImmutableList.of("ALACARTE-A La Carte", "BASEPOS-Basic EPOS", "MAJEPOS-Major EPOS").forEach(planName -> {
            LOGGER.info("Selected Plan:" + planName);
            PlanDefinitionTab.planTable.getRow(TableConstants.Plans.PLAN.getName(), planName).getCell(6).controls.links.get(ActionConstants.CHANGE).click();
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(BENEFIT_WAITING_PERIODS).getAsset(EPCOS_WAITING_PERIOD)).isPresent().hasOptions(WAITING_PERIOD_VALUES);
        });
    }
}
