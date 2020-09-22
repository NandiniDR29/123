package com.exigen.ren.modules.policy.gb_dn.master;

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
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.INTERNAL_TEAM;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.InternalTeamMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestQuoteInternalTeamSection extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-14057", component = POLICY_GROUPBENEFITS)
    public void testQuoteInternalTeamSection() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        customerNonIndividual.setSalesRep(USER_10001_FIRST_NAME, USER_10001_LAST_NAME);
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        AssetList internalTeamAssetList = policyInformationTab.getAssetList().getAsset(INTERNAL_TEAM);
        assertSoftly(softly -> {
            LOGGER.info("Steps: 1-4");
            ImmutableList.of(SALES_REPRESENTATIVE, SALES_SUPPORT_ASSOCIATE, UNDERWRITER).forEach(control ->
                    softly.assertThat(internalTeamAssetList.getAsset(control)).isPresent().isEnabled().isRequired());

            LOGGER.info("Steps: 5-10");
            checkInternalTeamSection(internalTeamAssetList, softly);
        });
        LOGGER.info("Steps: 11-14");
        policyInformationTab.getAssetList().fill(getDefaultDNMasterPolicyData()
                .mask(TestData.makeKeyPath(policyInformationTab.getMetaKey(), INTERNAL_TEAM.getLabel())));
        Tab.buttonSaveAndExit.click();
        String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
       navigateToCustomer();
        customerNonIndividual.setSalesRep(USER_10004_FIRST_NAME, USER_10004_LAST_NAME);
        MainPage.QuickSearch.search(quoteNumber);
        groupDentalMasterPolicy.dataGather().start();
        assertSoftly(softly ->
                checkInternalTeamSection(internalTeamAssetList, softly));
    }

    private void checkInternalTeamSection(AssetList internalTeamAssetList, ETCSCoreSoftAssertions softly) {
        softly.assertThat(internalTeamAssetList.getAsset(SALES_REPRESENTATIVE)).hasValue(USER_10001).containsAllOptions(SALES_REPRESENTATIVE_USERS);
        softly.assertThat(internalTeamAssetList.getAsset(SALES_SUPPORT_ASSOCIATE)).hasValue("").containsAllOptions(SALES_SUPPORT_ASSOCIATE_USERS);
        softly.assertThat(internalTeamAssetList.getAsset(UNDERWRITER)).hasValue(USER_ISBA).containsAllOptions(UNDERWRITER_USERS);
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
