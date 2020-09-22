package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.RecoveryDetailsActionTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsActionTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimBalancePage;
import com.exigen.ren.main.pages.summary.claim.ClaimFinancialsPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.common.Tab.buttonCancel;
import static com.exigen.ren.common.Tab.buttonSaveAndExit;
import static com.exigen.ren.common.enums.ActivitiesAndUserNotesConstants.ActivitiesAndUserNotesTable.DESCRIPTION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.*;
import static com.exigen.ren.common.pages.NavigationPage.comboBoxListAction;
import static com.exigen.ren.main.enums.ClaimConstants.CDTCodes.REVIEW_REQUIRED_1;
import static com.exigen.ren.main.enums.TableConstants.ClaimDentalClaimBalanceExtendedTable.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.buttonSubmitClaim;
import static com.exigen.ren.main.pages.summary.SummaryPage.activitiesAndUserNotes;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.getClaimNumber;
import static com.exigen.ren.utils.AdminActionsHelper.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimBalancePartOne extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-23733", "REN-23734"}, component = CLAIMS_GROUPBENEFITS)
    public void testPrepareInfrastructure() {
        LOGGER.info("Precondition");
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_PardisRajabi_Provider"));
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.LOGGED_INTAKE);
        String claimNumber = getClaimNumber();

        LOGGER.info("REN-23733 STEP#1  Navigate to 'Edit Claim' and Check 'Payee Type' field");
        NavigationPage.toSubTab(EDIT_CLAIM);
        assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).isEnabled().hasOptions(ImmutableList.of("Service Provider", "Primary Insured", "Other"));
        buttonSaveAndExit.click();

        LOGGER.info("REN-23733 STEP#2  Ensure, that 'Cancel Claim' action is visible");
        assertThat(comboBoxListAction).containsOption("Cancel Claim");

        LOGGER.info("REN-23733 STEP#3-5, REN-23734 STEP#1-2  Navigate And Inspect 'Financials' tab");
        NavigationPage.toSubTab(FINANCIALS);
        ClaimFinancialsPage.buttonBalance.click();
        assertSoftly(softly -> {
            softly.assertThat(ClaimBalancePage.tableClaimBalance.getHeader().getValue()).contains(DATE_OF_TRANSACTION.getName(), CLAIM_ID.getName(), PAYMENT_RECOVERY.getName(), CHECK_EFT.getName(), PAYEE_RECOVERED_FROM.getName(), TRANSACTION_AMOUNT.getName(), TRANSACTION_TYPE.getName(), TRANSACTION_COMMENT.getName());
            softly.assertThat(ClaimBalancePage.totalBalance).isPresent();});

        buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("REN-23733 STEP#6 Navigate to 'Adjudication' tab Expand 'Activities & User Notes' Ensure, that BAM message is added");
        NavigationPage.toSubTab(ADJUDICATION);
        activitiesAndUserNotes.expand();
        assertThat(activitiesAndUserNotes.getRow(DESCRIPTION, String.format("View Balance for Claim # %s", claimNumber))).isPresent();

        LOGGER.info("REN-23733 STEP#7 Navigate to 'Edit Claim' tab Fill all required fields and Ensure, that 'Cancel Claim' action is visible");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.fillTab(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .mask(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SEARCH_PROVIDER.getLabel()))
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), TYPE_OF_TRANSACTION.getLabel()), "Actual Services")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), CDT_CODE.getLabel()), REVIEW_REQUIRED_1)
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), DOS.getLabel()), DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel(), TOOTH.getLabel()), "3"));
        buttonSubmitClaim.click();
        assertThat(comboBoxListAction).containsOption("Cancel Claim");

        LOGGER.info("REN-23733 STEP#9 Navigate to 'Edit Claim' and Check 'Payee Type' field");
        NavigationPage.toSubTab(EDIT_CLAIM);
        assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).isEnabled().hasOptions(ImmutableList.of("Service Provider", "Primary Insured", "Other"));
        buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("REN-23733 STEP#10 Navigate to 'Financials' tab Ensure, that 'Type of Recovery' is NOT visible");
        NavigationPage.toSubTab(FINANCIALS);
        ClaimFinancialsPage.buttonPostRecovery.click();
        CustomAssertions.assertThat(dentalClaim.updateRecovery().getWorkspace().getTab(RecoveryDetailsActionTab.class).getAssetList().getAsset(RecoveryDetailsActionTabMetaData.TYPE_OF_RECOVERY)).isAbsent();
        buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("REN-23733 STEP#12 ");
        NavigationPage.toSubTab(EDIT_CLAIM);
        intakeInformationTab.getAssetList().getAsset(SUBMITTED_SERVICES).getAsset(CDT_CODE).setValue("D1310");
        intakeInformationTab.submitTab();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
        assertThat(comboBoxListAction).doesNotContainOption("Cancel Claim");

        LOGGER.info("REN-23733 STEP#12 Repeat steps 3-6");
        NavigationPage.toSubTab(FINANCIALS);
        ClaimFinancialsPage.buttonBalance.click();
        assertSoftly(softly -> {
            softly.assertThat(ClaimBalancePage.tableClaimBalance.getHeader().getValue()).contains(DATE_OF_TRANSACTION.getName(), CLAIM_ID.getName(), PAYMENT_RECOVERY.getName(), CHECK_EFT.getName(), PAYEE_RECOVERED_FROM.getName(), TRANSACTION_AMOUNT.getName(), TRANSACTION_TYPE.getName(), TRANSACTION_COMMENT.getName());
            softly.assertThat(ClaimBalancePage.totalBalance).isPresent();});
        buttonCancel.click();
        Page.dialogConfirmation.confirm();

        NavigationPage.toSubTab(ADJUDICATION);
        activitiesAndUserNotes.expand();
        assertThat(activitiesAndUserNotes.getRow(DESCRIPTION, String.format("View Balance for Claim # %s", claimNumber))).isPresent();

        LOGGER.info("REN-23733 STEP#17 Perform 'Adjust Claim' action And Ensure,that Cancel Claim action is visible");
        dentalClaim.claimAdjust().perform();
        assertThat(comboBoxListAction).containsOption("Cancel Claim");

        LOGGER.info("REN-23733 STEP#18 Perform 'Adjust Claim' action And Ensure,that Cancel Claim action is visible");
        NavigationPage.toSubTab(EDIT_CLAIM);
        assertThat(intakeInformationTab.getAssetList().getAsset(PAYEE_TYPE)).isDisabled();
        buttonCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("REN-23734 Precondition Setup User without 'Claim Balance' privilege.");
        createUserWithPrivilege(ImmutableList.of("ALL", "EXCLUDE " + Privilege.CLAIM_BALANCE.get()));

        LOGGER.info("REN-23734 STEP#4 Navigate to 'Financials' tab and Ensure, that 'Balance' button is NOT visible");
        MainPage.QuickSearch.search(claimNumber);
        NavigationPage.toSubTab(FINANCIALS);
        assertThat(ClaimFinancialsPage.buttonBalance).isAbsent();
    }
}
