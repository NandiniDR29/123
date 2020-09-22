package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.MyWorkConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimSuspendActionTab;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.ADJUDICATION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.common.pages.NavigationPage.comboBoxListAction;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.PENDING;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LINK_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_RECOVERY_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.STATUS;
import static com.exigen.ren.main.modules.claim.common.metadata.ClaimSuspendActionTabMetaData.DETAILS;
import static com.exigen.ren.main.modules.claim.common.tabs.ClaimSuspendActionTab.buttonSuspendClaim;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.ORTHO_MONTHS;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.CHARGE;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData.GENERAL_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.InsuredTabMetaData.GeneralInformationMetaData.DATE_OF_BIRTH;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.buttonCancel;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.tableTasks;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithSpecificRole;
import static com.exigen.ren.utils.AdminActionsHelper.searchOrCreateRole;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimProcessOrthoAbilitySuspendLifecycle extends ClaimGroupBenefitsDNBaseTest {

    private static final String REVIEW_SUSPENDED_DENTAL_CLAIM = "Review Suspended Dental Claim";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23506", component = CLAIMS_GROUPBENEFITS)
    public void testClaimProcessOrthoAbilityToSuspendLifecycle() {
        mainApp().open();
        commonPreconditions();
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), ORTHO_MONTHS.getLabel()), "")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(),
                        CDT_CODE.getLabel()), REVIEW_REQUIRED_1)
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(),
                        CHARGE.getLabel()), "1"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.LOGGED_INTAKE);

        LOGGER.info("TEST: Step# 1");
        assertThat(comboBoxListAction).doesNotContainOption(dentalClaim.claimSuspend().getName());

        LOGGER.info("TEST: Step# 2");
        dentalClaim.claimSubmit().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED);
            softly.assertThat(comboBoxListAction).doesNotContainOption(dentalClaim.claimSuspend().getName());
        });

        LOGGER.info("TEST: Step# 3");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D0160");
        Tab.buttonSaveAndExit.click();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);

        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format("Issue Payment"))).isPresent().isEnabled();

        LOGGER.info("TEST: Step# 4-5");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        dialogConfirmation.confirm();
        dentalClaim.claimSuspend().start();
        assertSoftly(softly -> {
            softly.assertThat(new ClaimSuspendActionTab().getAssetList().getAsset(DETAILS)).isPresent();
            softly.assertThat(buttonSuspendClaim).isPresent().isEnabled();
        });

        LOGGER.info("TEST: Step# 7");
        Tab.buttonCancel.click();
        assertSoftly(softly -> {
            softly.assertThat(NavigationPage.isSubTabSelected(ADJUDICATION)).isTrue();
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
        });

        LOGGER.info("TEST: Step# 8");
        dentalClaim.claimSuspend().perform();
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.SUSPENDED);
            softly.assertThat(NavigationPage.isSubTabSelected(ADJUDICATION)).isTrue();
        });

        LOGGER.info("TEST: Step# 10");
        assertThat(activitiesAndUserNotes).hasMatchingRows(ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION,
                String.format("Suspend Claim # %s, N/A", claimNumber));

        LOGGER.info("TEST: Step# 11");
        buttonTasks.click();
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REVIEW_SUSPENDED_DENTAL_CLAIM)).isPresent();
        buttonCancel.click();

        LOGGER.info("TEST: Step# 13");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format("Issue Payment"))).isAbsent();

        LOGGER.info("TEST: Step# 14");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        dialogConfirmation.confirm();
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.LOGGED_INTAKE);

        buttonTasks.click();
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REVIEW_SUSPENDED_DENTAL_CLAIM)).isAbsent();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23506", component = CLAIMS_GROUPBENEFITS)
    public void testClaimProcessOrthoAbilityToSuspendWithClaimSuspendPrivilege() {
        mainApp().open();
        commonPreconditions();
        dentalClaim.create(tdSpecific().getTestData("TestData_TwoServices"));
        dentalClaim.claimSubmit().perform();

        LOGGER.info("TEST: Step #16");
        dentalClaim.lineOverride().perform(tdSpecific().getTestData("TestData_LineOverride"), 6);
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED_ORTHO);
        assertThat(comboBoxListAction).containsOption(dentalClaim.claimSuspend().getName());

        LOGGER.info("TEST: Step #17");
        dentalClaim.claimSuspend().perform();
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.SUSPENDED);
            softly.assertThat(NavigationPage.isSubTabSelected(ADJUDICATION)).isTrue();
        });

        LOGGER.info("TEST: Step #21");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(STATUS.getName())).hasValue(PENDING);

        dentalClaim.disapprovePayment().perform(dentalClaim.getDefaultTestData("ClaimPayment", "TestData_DisapprovePayment"), 1);
        buttonTasks.click();
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REVIEW_SUSPENDED_DENTAL_CLAIM)).isAbsent();
        buttonCancel.click();

        LOGGER.info("TEST: Step #22");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED_ORTHO);

        dentalClaim.claimSuspend().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.SUSPENDED);

        buttonTasks.click();
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), REVIEW_SUSPENDED_DENTAL_CLAIM)).isPresent();
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23506", component = CLAIMS_GROUPBENEFITS)
    public void testClaimProcessOrthoAbilityToSuspendWithoutClaimSuspendPrivilege() {
        String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.CLAIM_SUSPEND.get())), roleCorporate);
        createUserWithSpecificRole(roleName, profileCorporate);
        commonPreconditions();
        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), ORTHO_MONTHS.getLabel()), "3")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(),
                        CDT_CODE.getLabel()), "D8010")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(),
                        CHARGE.getLabel()), "100"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED_ORTHO);

        LOGGER.info("TEST: Step #23");
        assertThat(comboBoxListAction).doesNotContainOption(dentalClaim.claimSuspend().getName());
    }

    private void commonPreconditions() {
        createDefaultNonIndividualCustomer();
        String todayMinus3Months = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().minusMonths(3).format(MM_DD_YYYY);
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupDentalMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.EFFECTIVE_DATE.getLabel()), todayMinus3Months));
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_MasterPolicy").resolveLinks()));
        groupDentalCertificatePolicy.createPolicyViaUI(getDefaultGroupDentalCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), todayMinus3Months)
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), GENERAL_INFORMATION.getLabel(), DATE_OF_BIRTH.getLabel()),
                        TimeSetterUtil.getInstance().getCurrentTime().minusYears(17).format(DateTimeUtilsHelper.MM_DD_YYYY)));
    }

}
