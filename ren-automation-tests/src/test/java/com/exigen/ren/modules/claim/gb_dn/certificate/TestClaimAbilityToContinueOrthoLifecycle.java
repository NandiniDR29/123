package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.ipb.eisa.utils.batchjob.JobRunner;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.admin.modules.general.scheduler.pages.GeneralSchedulerPage;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.admin.modules.security.role.metadata.GeneralRoleMetaData;
import com.exigen.ren.admin.modules.security.role.tabs.GeneralRoleTab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.MyWorkConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.claim.common.tabs.ClaimContinueActionTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.admin.modules.security.profile.ProfileContext.profileCorporate;
import static com.exigen.ren.admin.modules.security.role.RoleContext.roleCorporate;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.*;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.main.enums.ActionConstants.ClaimAction.*;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.*;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.ACTIONS;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.PLAN_TO_PAY;
import static com.exigen.ren.main.modules.claim.common.metadata.ClaimContinueActionTabMetaData.DETAILS;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.ORTHO_MONTHS;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SUBMITTED_SERVICES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.buttonCancel;
import static com.exigen.ren.main.pages.summary.MyWorkSummaryPage.tableTasks;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithSpecificRole;
import static com.exigen.ren.utils.AdminActionsHelper.searchOrCreateRole;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAbilityToContinueOrthoLifecycle extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-21792", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAbilityToContinueOrthoLifecycle() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), REVIEW_REQUIRED_1)
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CHARGE.getLabel()), "100")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), TOOTH.getLabel()), "3")
        );
        String claimNumber = getClaimNumber();

        LOGGER.info("Step 1");
        assertSoftly(softly -> {
            softly.assertThat(labelClaimStatus).hasValue(LOGGED_INTAKE);
            softly.assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(CONTINUE_CLAIM);
            softly.assertThat(NavigationPage.comboBoxListAction).containsAllOptions(CLAIM_INQUIRY, CLAIM_SUBMIT, GENERATE_ON_DEMAND, PEND_CLAIM, CANCEL_CLAIM);
        });

        LOGGER.info("Step 2-3");
        dentalClaim.claimSubmit().perform();
        assertSoftly(softly -> {
            softly.assertThat(labelClaimStatus.getValue()).startsWith(PENDED);
            softly.assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(CONTINUE_CLAIM);
            softly.assertThat(tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_VIEW)).isPresent();
            softly.assertThat(tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE)).isPresent();
            softly.assertThat(NavigationPage.comboBoxListAction).containsAllOptions(CLAIM_INQUIRY, GENERATE_ON_DEMAND, READJUDICATE, PEND_CLAIM, FINAL_ADJUDICATION, CANCEL_CLAIM);
        });

        LOGGER.info("Step 4");
        NavigationPage.toSubTab(EDIT_CLAIM);
        IntakeInformationTab.tableSubmittedServices.getRow(ImmutableMap.of(
                CDT_CODE.getLabel(), REVIEW_REQUIRED_1,
                CHARGE.getLabel(), new Currency(100).toString())).getCell(9).controls.links.get(ActionConstants.CHANGE).click();
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D8010");
        intakeInformationTab.getAssetList().getAsset(ORTHO_MONTHS).setValue("6");
        IntakeInformationTab.buttonSaveAndExit.click();
        assertSoftly(softly -> {
            softly.assertThat(labelClaimStatus).hasValue(ADJUDICATED_ORTHO);
            softly.assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(CONTINUE_CLAIM);
            softly.assertThat(NavigationPage.comboBoxListAction).containsAllOptions(CLAIM_INQUIRY, SUSPEND_CLAIM, ADJUST_CLAIM);
        });

        LOGGER.info("Step 6-7");
        dentalClaim.claimSuspend().perform();
        assertSoftly(softly -> {
            softly.assertThat(labelClaimStatus).hasValue(SUSPENDED);
            softly.assertThat(NavigationPage.comboBoxListAction).hasOptions(ValueConstants.EMPTY, CLAIM_INQUIRY, CONTINUE_CLAIM, ADJUST_CLAIM);
            softly.assertThat(tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_VIEW)).isPresent();
            softly.assertThat(tableResultsOfAdjudication.getRow(1).getCell(ACTIONS.getName()).controls.links.get(ActionConstants.LINE_OVERRIDE)).isAbsent();
        });

        LOGGER.info("Step 8");
        String roleName = searchOrCreateRole(roleCorporate.defaultTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(GeneralRoleTab.class.getSimpleName(), GeneralRoleMetaData.PRIVILEGES.getLabel()),
                        ImmutableList.of("ALL", "EXCLUDE " + Privilege.CLAIM_CONTINUE.get())), roleCorporate);
        createUserWithSpecificRole(roleName, profileCorporate);
        search(claimNumber);
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(CONTINUE_CLAIM);

        LOGGER.info("Step 9-10");
        mainApp().reopen();
        search(claimNumber);
        dentalClaim.claimContinue().perform();
        assertSoftly(softly -> {
            softly.assertThat(labelClaimStatus).hasValue(ADJUDICATED_ORTHO);
            softly.assertThat(NavigationPage.comboBoxListAction).doesNotContainOption(CONTINUE_CLAIM);
            softly.assertThat(NavigationPage.comboBoxListAction).containsAllOptions(CLAIM_INQUIRY, SUSPEND_CLAIM, ADJUST_CLAIM);
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-21793", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAbilityToContinueOrthoLifecycle_2() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();

        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CHARGE.getLabel()), "100")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), "D0160"));
        String claimNumber = getClaimNumber();
        dentalClaim.claimSubmit().perform();
        assertThat(labelClaimStatus).hasValue(ADJUDICATED);
        dentalClaim.claimSuspend().perform();
        assertThat(labelClaimStatus).hasValue(SUSPENDED);

        LOGGER.info("Step 1");
        dentalClaim.claimContinue().start();
        ClaimContinueActionTab claimContinueActionTab = dentalClaim.claimContinue().getWorkspace().getTab(ClaimContinueActionTab.class);
        assertSoftly(softly -> {
            softly.assertThat(claimContinueActionTab.getAssetList().getAsset(DETAILS)).isPresent().isOptional().hasValue(ValueConstants.EMPTY);
            softly.assertThat(ClaimContinueActionTab.buttonContinueClaim).isPresent();
            softly.assertThat(ClaimContinueActionTab.buttonCancel).isPresent();
        });

        LOGGER.info("Step 2");
        ClaimContinueActionTab.buttonCancel.click();
        assertSoftly(softly -> {
            softly.assertThat(NavigationPage.isSubTabSelected(ADJUDICATION)).isTrue();
            softly.assertThat(labelClaimStatus).hasValue(SUSPENDED);
        });

        LOGGER.info("Step 7");
        dentalClaim.claimContinue().perform();
        assertThat(labelClaimStatus).hasValue(ADJUDICATED);

        LOGGER.info("Step 8");
        activitiesAndUserNotes.expand();
        assertThat(activitiesAndUserNotes.getRow(DESCRIPTION, String.format("Continue Claim # %s, N/A", claimNumber))).isPresent();

        LOGGER.info("Step 9");
        buttonTasks.click();
        assertThat(tableTasks.getRow(MyWorkConstants.MyWorkTasksTable.TASK_NAME.getName(), "Review Suspended Dental Claim")).isAbsent();
        buttonCancel.click();

        LOGGER.info("Step 14");
        NavigationPage.toSubTab(FINANCIALS);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);

        NavigationPage.toSubTab(ADJUDICATION);
        dentalClaim.claimAdjust().perform();
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(ORTHO_MONTHS).setValue("6");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(DOS).setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D8010");
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CHARGE).setValue("1000");
        IntakeInformationTab.buttonSubmitClaim.click();
        assertSoftly(softly -> {
            softly.assertThat(labelClaimStatus).hasValue(ADJUDICATED_ORTHO);
            softly.assertThat(tableResultsOfAdjudication).hasRows(6);
        });
        NavigationPage.toSubTab(FINANCIALS);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(2);

        LOGGER.info("Step 15");
        NavigationPage.toSubTab(ADJUDICATION);
        dentalClaim.claimSuspend().perform();
        assertSoftly(softly -> {
            softly.assertThat(labelClaimStatus).hasValue(SUSPENDED);
            softly.assertThat(NavigationPage.isSubTabSelected(ADJUDICATION)).isTrue();
        });

        LocalDate planToPayForTHeLastFeature = LocalDate.parse(tableResultsOfAdjudication.getRow(6).getCell(PLAN_TO_PAY.getName()).getValue(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(planToPayForTHeLastFeature.atStartOfDay());
        adminApp().open();

        JobRunner.executeJob(GeneralSchedulerPage.CLAIMS_ORTHO_PROCESSING_JOB);

        mainApp().open();
        search(claimNumber);
        NavigationPage.toSubTab(FINANCIALS);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(2);

        LOGGER.info("Step 16");
        NavigationPage.toSubTab(ADJUDICATION);
        dentalClaim.claimContinue().start();
        assertSoftly(softly -> {
            softly.assertThat(claimContinueActionTab.getAssetList().getAsset(DETAILS)).isPresent().isOptional().hasValue(ValueConstants.EMPTY);
            softly.assertThat(ClaimContinueActionTab.buttonContinueClaim).isPresent();
            softly.assertThat(ClaimContinueActionTab.buttonCancel).isPresent();
        });

        LOGGER.info("Step 17");
        ClaimContinueActionTab.buttonContinueClaim.click();
        assertSoftly(softly -> {
            softly.assertThat(labelClaimStatus).hasValue(ADJUDICATED_ORTHO);
            softly.assertThat(NavigationPage.isSubTabSelected(ADJUDICATION)).isTrue();
        });
    }
}