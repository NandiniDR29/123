package com.exigen.ren.modules.policy.gb_pfl.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.*;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestPolicyInformationFieldAttribute extends BaseTest implements CustomerContext, CaseProfileContext, PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_PFL, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-20246", "REN-20304", "REN-20768", "REN-20772", "REN-20773"}, component = POLICY_GROUPBENEFITS)

    public void testPolicyInformationFieldAttribute() {
        mainApp().open(USER_10_LOGIN, "qa");
        createDefaultNonIndividualCustomer();
        customerNonIndividual.setSalesRep(USER_10001_FIRST_NAME, USER_10001_LAST_NAME);
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());
        paidFamilyLeaveMasterPolicy.initiate(getDefaultPFLMasterPolicyData());
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultPFLMasterPolicyData(), policyInformationTab.getClass());

        assertSoftly(softly -> {
            LOGGER.info("REN-20246 Step 1,Step 3,Step 5,Step 7,Step 10");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SHORT_TERM)).isAbsent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(USE_POLICY_TERM)).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Country"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Not Disabled"))).isAbsent();

            LOGGER.info("REN-20304 Step 1-3,Step 1-3,Step7,Step 1-5");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(SITUS_STATE)).isPresent().isEnabled().isRequired();
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NY");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Amount Received with Application"))).isAbsent();

            LOGGER.info("REN-20768 Step 1-7,Step 10,Step12");
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NY");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(UNDEWRITING_COMPANY)).isPresent().isDisabled().hasValue("Renaissance Life & Health Insurance Company of New York");
            policyInformationTab.getAssetList().getAsset(SITUS_STATE).setValue("NJ");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(UNDEWRITING_COMPANY)).isPresent().isDisabled().hasValue("Renaissance Life & Health Insurance Company of America");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Private Plan Number"))).isAbsent();

            LOGGER.info("REN-20772 Step 1-14");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(INTERNAL_TEAM).getAsset(InternalTeamMetaData.SALES_REPRESENTATIVE)).isPresent().isRequired().isEnabled().hasValue("User10001_FirstName User10001_LastName").containsAllOptions(ImmutableList.of("User10001_FirstName User10001_LastName", "User10002_FirstName User10002_LastName", "User10003_FirstName User10003_LastName", "User11003_FirstName User11003_LastName", "User100011_FirstName_123456789 User100011_LastName_123456789", "QA QA user"));
            softly.assertThat(policyInformationTab.getAssetList().getAsset(INTERNAL_TEAM).getAsset(InternalTeamMetaData.SALES_SUPPORT_ASSOCIATE)).isPresent().isEnabled().hasValue(StringUtils.EMPTY).containsAllOptions(ImmutableList.of(StringUtils.EMPTY, "User10004_FirstName User10004_LastName", "User10005_FirstName User10005_LastName", "User10006_FirstName User10006_LastName", "User11002_FirstName User11002_LastName", "User100012_FirstName_123456789 User100012_LastName_123456789", "QA QA user"));
            softly.assertThat(policyInformationTab.getAssetList().getAsset(INTERNAL_TEAM).getAsset(InternalTeamMetaData.UNDERWRITER)).isPresent().isRequired().isEnabled().hasValue(StringUtils.EMPTY).containsAllOptions(ImmutableList.of(StringUtils.EMPTY, "User10007_FirstName User10007_LastName", "User10008_FirstName User10008_LastName", "User10009_FirstName User10009_LastName", "User11002_FirstName User11002_LastName", "User100013_FirstName_123456789 User100013_LastName_123456789", "QA QA user", "User100010_FirstName User100010_LastName"));

            LOGGER.info("REN-20773 Step 1-2");
            softly.assertThat(policyInformationTab.getAssetList().getAsset(EXCLUSIONS).getAsset(ExclusionMetaData.NOT_UNDER_CARE_OF_DOCTOR)).isPresent();
            softly.assertThat(policyInformationTab.getAssetList().getAsset(EXCLUSIONS).getAsset(ExclusionMetaData.PERIOD_OF_WORK_FOR_PAY_BENEFIT_EXCEEDS_PRE_DISABILITY_WAGES)).isPresent();
        });
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultPFLMasterPolicyData(), policyInformationTab.getClass(), premiumSummaryTab.getClass());
        premiumSummaryTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
    }
}