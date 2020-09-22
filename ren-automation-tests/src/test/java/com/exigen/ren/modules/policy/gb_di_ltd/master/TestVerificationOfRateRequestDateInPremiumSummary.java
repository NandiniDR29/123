package com.exigen.ren.modules.policy.gb_di_ltd.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.metadata.master.PremiumSummaryBindActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.metadata.master.PremiumSummaryIssueActionTabMetaData;
import com.exigen.ren.main.modules.policy.common.tabs.master.IssueActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryBindActionTab;
import com.exigen.ren.main.modules.policy.common.tabs.master.PremiumSummaryIssueActionTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.RATE_REQUEST_DATE_ERROR;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.RATE_REQUEST_DATE_IS_REQUIRED;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.PREMIUM_CALCULATED;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PremiumSummaryTabMetaData.RATE_REQUEST_DATE;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithSpecificRole;
import static com.exigen.ren.utils.AdminActionsHelper.searchOrCreateRole;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestVerificationOfRateRequestDateInPremiumSummary extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityMasterPolicyContext {

    private PremiumSummaryIssueActionTab premiumSummaryIssueActionTab = longTermDisabilityMasterPolicy.issue().getWorkspace().getTab(PremiumSummaryIssueActionTab.class);
    private PremiumSummaryBindActionTab premiumSummaryBindActionTab = longTermDisabilityMasterPolicy.acceptContract().getWorkspace().getTab(PremiumSummaryBindActionTab.class);
    private String currentDate;
    private String currentDatePlus1d;
    private TextBox rateRequestDateTextBox = premiumSummaryTab.getAssetList().getAsset(RATE_REQUEST_DATE);
    private TextBox rateRequestDateTextBoxIssueActionTab = premiumSummaryIssueActionTab.getAssetList().getAsset(PremiumSummaryIssueActionTabMetaData.RATE_REQUEST_DATE);
    private TextBox rateRequestDateTextBoxBindActionTab = premiumSummaryBindActionTab.getAssetList().getAsset(PremiumSummaryBindActionTabMetaData.RATE_REQUEST_DATE);


    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25143", component = POLICY_GROUPBENEFITS)
    public void testVerificationOfRateRequestDateInPremiumSummaryTC1() {

        preconditions(true);

        LOGGER.info("Test: Step 1");
        assertThat(rateRequestDateTextBox).isPresent().isOptional().isDisabled().hasValue("");
        premiumSummaryTab.fillTab(getDefaultLTDMasterPolicyData());

        LOGGER.info("Test: Step 3");
        premiumSummaryTab.rate();

        assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
            softly.assertThat(rateRequestDateTextBox).isPresent().isRequired().isEnabled().hasValue(currentDate);
        });

        LOGGER.info("Test: Step 4");
        rateRequestDateTextBox.clear();
        Tab.buttonNext.click();
        assertThat(rateRequestDateTextBox).hasWarningWithText(RATE_REQUEST_DATE_IS_REQUIRED);

        LOGGER.info("Test: Step 6");
        rateRequestDateTextBox.setValue(currentDatePlus1d);
        Tab.buttonNext.click();
        assertThat(rateRequestDateTextBox).hasWarningWithText(RATE_REQUEST_DATE_ERROR);

        LOGGER.info("Test: Step 8");
        rateRequestDateTextBox.setValue(currentDate);
        premiumSummaryTab.submitTab();

        LOGGER.info("Test: Step 11");
        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());

        LOGGER.info("Test: Step 12");
        longTermDisabilityMasterPolicy.acceptContract().start();
        longTermDisabilityMasterPolicy.acceptContract().getWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), PremiumSummaryBindActionTab.class);
        assertThat(rateRequestDateTextBoxBindActionTab).isDisabled().hasValue(currentDate);

        LOGGER.info("Test: Step 13");
        Tab.buttonNext.click();
        longTermDisabilityMasterPolicy.issue().start();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), PremiumSummaryIssueActionTab.class);
        assertThat(rateRequestDateTextBoxIssueActionTab).isDisabled().hasValue(currentDate);

        LOGGER.info("Test: Step 14");
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillFromTo(getDefaultLTDMasterPolicyData(), PremiumSummaryIssueActionTab.class, IssueActionTab.class, true);
        longTermDisabilityMasterPolicy.issue().getWorkspace().getTab(IssueActionTab.class).submitTab();
        billingAccount.getDefaultWorkspace().getTab(BillingAccountTab.class).fillTab(getDefaultLTDMasterPolicyData()).submitTab();

        LOGGER.info("Test: Step 16");
        longTermDisabilityMasterPolicy.endorse().start();
        longTermDisabilityMasterPolicy.endorse().getWorkspace().fill(longTermDisabilityMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        assertThat(NavigationPage.PolicyNavigation.isLeftMenuTabSelected(NavigationEnum.GroupBenefitsTab.POLICY_INFORMATION.get())).isTrue();

        LOGGER.info("Test: Step 17");
        premiumSummaryTab.navigateToTab();
        assertThat(rateRequestDateTextBox).isPresent().isRequired().isEnabled().hasValue(currentDate);
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-25143", component = POLICY_GROUPBENEFITS)
    public void testVerificationOfRateRequestDateInPremiumSummaryTC2() {

        preconditions(false);

        LOGGER.info("Test: Step 1");
        assertThat(rateRequestDateTextBox).isPresent().isOptional().isDisabled().hasValue("");
        premiumSummaryTab.fillTab(getDefaultLTDMasterPolicyData());

        LOGGER.info("Test: Step 2");
        premiumSummaryTab.rate();

        assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
            softly.assertThat(rateRequestDateTextBox).isPresent().isDisabled().hasValue(currentDate);
        });

        Tab.buttonNext.click();
        longTermDisabilityMasterPolicy.propose().perform(getDefaultLTDMasterPolicyData());

        LOGGER.info("Test: Step 6");
        longTermDisabilityMasterPolicy.acceptContract().start();
        longTermDisabilityMasterPolicy.acceptContract().getWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), PremiumSummaryBindActionTab.class);
        assertThat(rateRequestDateTextBoxBindActionTab).isDisabled().hasValue(currentDate);

        LOGGER.info("Test: Step 7");
        Tab.buttonNext.click();
        longTermDisabilityMasterPolicy.issue().start();
        longTermDisabilityMasterPolicy.issue().getWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), PremiumSummaryIssueActionTab.class);
        assertThat(rateRequestDateTextBoxIssueActionTab).isDisabled().hasValue(currentDate);
    }

    private void preconditions(boolean userWithAllPrivileges) {
        currentDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().format(DateTimeUtils.MM_DD_YYYY);
        currentDatePlus1d = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);

        if (!userWithAllPrivileges) {
            String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                    .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                            ImmutableList.of("ALL", "EXCLUDE " + Privilege.OVERRIDE_RATE_REQUEST_DATE.get())), roleCorporate);
            createUserWithSpecificRole(roleName, profileCorporate);
        } else {
            mainApp().open();
        }

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.initiate(getDefaultLTDMasterPolicyData());
        longTermDisabilityMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultLTDMasterPolicyData(), premiumSummaryTab.getClass());
    }
}