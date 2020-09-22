package com.exigen.ren.modules.policy.gb_tl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CoveragesConstants.TermLifeCoverages.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PlanDefinitionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData.UNDEWRITING_COMPANY;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteMainSection extends BaseTest implements CustomerContext, CaseProfileContext, TermLifeInsuranceMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-11351", component = POLICY_GROUPBENEFITS)
    public void testQuoteMainSection() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        termLifeInsuranceMasterPolicy.initiate(getDefaultTLMasterPolicyData());
        AssetList policyInfAssetList = (AssetList) policyInformationTab.getAssetList();
        assertSoftly(softly -> {
            softly.assertThat(policyInfAssetList.getAsset(UNDEWRITING_COMPANY)).hasOptions(
                    "Renaissance Life & Health Insurance Company of America", "Renaissance Life & Health Insurance Company of New York");
            policyInfAssetList.getAsset(SITUS_STATE).setValue("NY");
            softly.assertThat(policyInfAssetList.getAsset(UNDEWRITING_COMPANY)).hasValue("Renaissance Life & Health Insurance Company of New York");
            policyInfAssetList.getAsset(SITUS_STATE).setValue("PA");
            softly.assertThat(policyInfAssetList.getAsset(UNDEWRITING_COMPANY)).hasValue("Renaissance Life & Health Insurance Company of America");
        });

        LOGGER.info("REN-11351 TC_3. Step 1-3");
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        planDefinitionTab.selectDefaultPlan();
        AbstractContainer<?, ?> planDefAssetList = planDefinitionTab.getAssetList();
        assertThat(planDefAssetList.getAsset(COVERAGE_NAME)).hasValue(BTL);

        assertSoftly(softly -> {
            LOGGER.info("REN-11351 TC_3. Step 4-5");
            softly.assertThat(planDefAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isPresent().isRequired();
            softly.assertThat(planDefAssetList.getAsset(CENSUS_TYPE)).hasValue("Enrolled");

            LOGGER.info("REN-11351 TC_3. Step 8-10");
            planDefAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("123");
            planDefAssetList.getAsset(CENSUS_TYPE).setValue("Eligible");
            softly.assertThat(planDefAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasValue("").isOptional();

            LOGGER.info("REN-11351 TC_3. Step 12-13");
            PlanDefinitionTab.changeCoverageTo(DEP_BTL);
            planDefAssetList.getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(VOL_BTL);

            LOGGER.info("REN-11351 TC_3. Step 14");
            planDefAssetList.getAsset(CENSUS_TYPE).setValue("Enrolled");
            softly.assertThat(planDefAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isPresent().isRequired();
            planDefAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES).setValue("123");
            planDefAssetList.getAsset(CENSUS_TYPE).setValue("Eligible");
            softly.assertThat(planDefAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).hasValue("").isOptional();
            planDefAssetList.getAsset(PlanDefinitionTabMetaData.COVERAGE_NAME).setValue(DEP_BTL);
            softly.assertThat(planDefAssetList.getAsset(TOTAL_NUMBER_OF_ELIGIBLE_LIVES)).isAbsent();
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_TL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-11354", component = POLICY_GROUPBENEFITS)
    public void testQuoteMainSectionUnderwritingCompany() {
        mainApp().open();
        EntitiesHolder.openCopiedMasterQuote(termLifeInsuranceMasterPolicy.getType());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

        termLifeInsuranceMasterPolicy.dataGather().start();
        AssetList policyInfAssetList = (AssetList) policyInformationTab.getAssetList();
        assertThat(policyInfAssetList.getAsset(UNDEWRITING_COMPANY)).isPresent();
        termLifeInsuranceMasterPolicy.dataGather().submit();
        termLifeInsuranceMasterPolicy.propose().perform();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);

        termLifeInsuranceMasterPolicy.acceptContract().start();
        assertThat(policyInfAssetList.getAsset(UNDEWRITING_COMPANY)).isPresent();
        termLifeInsuranceMasterPolicy.acceptContract().getWorkspace()
                .fill(termLifeInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        termLifeInsuranceMasterPolicy.acceptContract().submit();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED);

        termLifeInsuranceMasterPolicy.issue().start();
        assertThat(policyInfAssetList.getAsset(UNDEWRITING_COMPANY)).isPresent();
    }
}