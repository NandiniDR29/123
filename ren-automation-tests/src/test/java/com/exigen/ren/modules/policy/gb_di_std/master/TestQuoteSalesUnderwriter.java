package com.exigen.ren.modules.policy.gb_di_std.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.*;
import static com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteSalesUnderwriter extends BaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-14514", component = POLICY_GROUPBENEFITS)
    public void testQuoteSalesUnderwriter() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
        AssetList internalAssetList = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.INTERNAL_TEAM);
        assertSoftly(softly -> {
            ImmutableList.of(SALES_REPRESENTATIVE, SALES_SUPPORT_ASSOCIATE, UNDERWRITER).forEach(asset ->
                    softly.assertThat(internalAssetList.getAsset(asset)).isPresent().isRequired().isEnabled());

            internalAssetList.getAsset(SALES_REPRESENTATIVE).setValue("");
            Tab.buttonNext.click();
            softly.assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).hasWarning();
            internalAssetList.getAsset(SALES_REPRESENTATIVE).setValueByIndex(1);

            internalAssetList.getAsset(SALES_SUPPORT_ASSOCIATE).setValue("");
            Tab.buttonNext.click();
            softly.assertThat(internalAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).hasWarning();
            internalAssetList.getAsset(SALES_SUPPORT_ASSOCIATE).setValueByIndex(1);

            shortTermDisabilityMasterPolicy.getDefaultWorkspace()
                    .fillFromTo(getDefaultSTDMasterPolicyData(), policyInformationTab.getClass(), premiumSummaryTab.getClass(), true);
            premiumSummaryTab.submitTab();

            softly.assertThat(QuoteSummaryPage.tableMasterQuote.getHeader().getValue()).doesNotContain("Sales Support Associate", "Underwriter");
            softly.assertThat(QuoteSummaryPage.tableMasterQuote.getHeader().getValue()).contains(QuoteSummaryPage.MasterQuote.SALES_REPRESENTATIVE.getName());
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-14601", component = POLICY_GROUPBENEFITS)
    public void testQuoteSalesUnderwriterRules() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        customerNonIndividual.setSalesRep(USER_10001_FIRST_NAME, USER_10001_LAST_NAME);
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());
        shortTermDisabilityMasterPolicy.initiate(getDefaultSTDMasterPolicyData());
        AssetList internalAssetList = policyInformationTab.getAssetList().getAsset(PolicyInformationTabMetaData.INTERNAL_TEAM);

        assertSoftly(softly -> {
            softly.assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(USER_10001).containsAllOptions(SALES_REPRESENTATIVE_USERS);
            softly.assertThat(internalAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).hasValue("").containsAllOptions(SALES_SUPPORT_ASSOCIATE_USERS);
            softly.assertThat(internalAssetList.getAsset(UNDERWRITER)).hasValue(USER_ISBA).containsAllOptions(UNDERWRITER_USERS);
            SALES_SUPPORT_ASSOCIATE_USERS.forEach(salesSupportUser -> {
                softly.assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).doesNotContainOption(salesSupportUser);
                softly.assertThat(internalAssetList.getAsset(UNDERWRITER)).doesNotContainOption(salesSupportUser);
            });
            SALES_REPRESENTATIVE_USERS.forEach(salesSupportUser -> {
                softly.assertThat(internalAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).doesNotContainOption(salesSupportUser);
                softly.assertThat(internalAssetList.getAsset(UNDERWRITER)).doesNotContainOption(salesSupportUser);
            });
            UNDERWRITER_USERS.forEach(salesSupportUser -> {
                softly.assertThat(internalAssetList.getAsset(SALES_REPRESENTATIVE)).doesNotContainOption(salesSupportUser);
                softly.assertThat(internalAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).doesNotContainOption(salesSupportUser);
            });

        });
    }

}
