package com.exigen.ren.modules.claim.gb_tl.master;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsTLBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.Users.ISBA;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LINK_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.PAYMENT_METHOD;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithSpecificRole;
import static com.exigen.ren.utils.AdminActionsHelper.searchOrCreateRole;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAbilityReissuePaymentWithWithoutPrivilege extends ClaimGroupBenefitsTLBaseTest {

    private static final String REISSUE_PAYMENT = "Reissue Payment";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33807", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAbilityReissuePaymentWithoutPrivilege() {
        String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.CLAIM_REISSUE_PAYMENT.get())), roleCorporate);
        User userWOClaimReissuePayment = createUserWithSpecificRole(roleName, profileCorporate);

        commonSteps(userWOClaimReissuePayment);

        assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(REISSUE_PAYMENT))).isAbsent();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-33807", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAbilityReissuePaymentWithPrivilege() {

        mainApp().open();

        commonSteps(ISBA);

        assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(REISSUE_PAYMENT))).isPresent();
    }

    private void commonSteps(User user) {
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultTermLifeInsuranceMasterPolicy();

        LOGGER.info("Test: Step 1");
        termLifeClaim.create(termLifeClaim.getDefaultTestData(DATA_GATHER, "TestData_BenefitAcceleratedDeath"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", DEFAULT_TEST_DATA_KEY), 1);
        claim.addPayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_FinalPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_METHOD.getLabel()), "Check").resolveLinks());

        mainApp().reopen(approvalUsername, approvalPassword);
        MainPage.QuickSearch.search(claimNumber);

        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        mainApp().reopen(user);
        MainPage.QuickSearch.search(claimNumber);

        toSubTab(PAYMENTS);
        ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries
                .getRow(1).getCell(ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.PAYMENT_NUMBER).controls.links.getFirst().click();
    }
}