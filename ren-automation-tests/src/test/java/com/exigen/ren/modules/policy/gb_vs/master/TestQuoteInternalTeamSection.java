package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.ETCSCoreSoftAssertions;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.*;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.INTERNAL_TEAM;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteInternalTeamSection extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-15229", component = POLICY_GROUPBENEFITS)
    public void testQuoteInternalTeamSection() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        customerNonIndividual.setSalesRep(USER_10001_FIRST_NAME, USER_10001_LAST_NAME);

        createDefaultCaseProfile(groupVisionMasterPolicy.getType());
        groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
        AssetList internalTeamAssetList = policyInformationTab.getAssetList().getAsset(INTERNAL_TEAM);
        assertSoftly(softly -> {
            ImmutableList.of(SALES_REPRESENTATIVE, SALES_SUPPORT_ASSOCIATE, UNDERWRITER).forEach(control ->
                    softly.assertThat(internalTeamAssetList.getAsset(control)).isPresent().isEnabled().isRequired());

            softly.assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(USER_10001).containsAllOptions(SALES_REPRESENTATIVE_USERS);
            softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).hasValue("").containsAllOptions(SALES_SUPPORT_ASSOCIATE_USERS);
            softly.assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).hasValue(USER_ISBA).containsAllOptions(UNDERWRITER_USERS);
            checkInternalSectionDropdownValues(internalTeamAssetList, softly);
        });
        policyInformationTab.getAssetList().fill(getDefaultVSMasterPolicyData()
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), INTERNAL_TEAM.getLabel())));
        Tab.buttonSaveAndExit.click();
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
       navigateToCustomer();
        customerNonIndividual.setSalesRep(USER_10004_FIRST_NAME, USER_10004_LAST_NAME);
        MainPage.QuickSearch.search(quoteNumber);
        groupVisionMasterPolicy.dataGather().start();
        assertSoftly(softly -> {
            softly.assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(USER_10001).containsAllOptions(SALES_REPRESENTATIVE_USERS);
            softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).hasValue("").containsAllOptions(SALES_SUPPORT_ASSOCIATE_USERS);
            softly.assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).hasValue(USER_ISBA).containsAllOptions(UNDERWRITER_USERS);
            checkInternalSectionDropdownValues(internalTeamAssetList, softly);
        });
    }

    private void checkInternalSectionDropdownValues(AssetList internalTeamAssetList, ETCSCoreSoftAssertions softly) {
        SALES_SUPPORT_ASSOCIATE_USERS.forEach(salesSupportUser -> {
            softly.assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).doesNotContainOption(salesSupportUser);
            softly.assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).doesNotContainOption(salesSupportUser);
        });
        SALES_REPRESENTATIVE_USERS.forEach(salesRepUser -> {
            softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).doesNotContainOption(salesRepUser);
            softly.assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).doesNotContainOption(salesRepUser);
        });
        UNDERWRITER_USERS.forEach(underwriterUser -> {
            softly.assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).doesNotContainOption(underwriterUser);
            softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).doesNotContainOption(underwriterUser);
        });
    }
}
