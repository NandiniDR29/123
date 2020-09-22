package com.exigen.ren.modules.claim.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.CaseProfileDetailsTab;
import com.exigen.ren.main.modules.claim.common.tabs.CompleteNotificationTab;
import com.exigen.ren.main.modules.claim.common.tabs.LossEventTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageDeterminationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsPFLParticipantInformationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsPFLQualifyingEventTab;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsPFLBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.buttonCancel;
import static com.exigen.ren.common.metadata.SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE;
import static com.exigen.ren.common.pages.ErrorPage.TableError.MESSAGE;
import static com.exigen.ren.common.pages.ErrorPage.tableError;
import static com.exigen.ren.main.modules.claim.common.metadata.LossEventTabMetaData.DATE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData.PARTICIPANT_WORK_DAYS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData.ParticipantWorkDaysMetaData.AVERAGE_HOURS_WORKED_PER_WEEK;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLQualifyingEventTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAverageHoursWorkedPerWeek extends ClaimGroupBenefitsPFLBaseTest {

    private static final String ERROR_MESSAGE = "Employee is not eligible for PFL Benefit";
    private static final String LOSS_DATE = "07/22/2019";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-20874", "REN-21547"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimAverageHoursWorkedPerWeek_1() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());
        paidFamilyLeaveMasterPolicy.createPolicy(getDefaultPFLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY"));
        disabilityClaim.initiate(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit"));
        disabilityClaim.getDefaultWorkspace().fillUpTo(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit"),
                BenefitsPFLParticipantInformationTab.class);
        TextBox averageHours = benefitsPflParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_WORK_DAYS).getAsset(AVERAGE_HOURS_WORKED_PER_WEEK);
        assertSoftly(softly -> {
            softly.assertThat(averageHours).isRequired().hasValue("40");
            ImmutableList.of("-1", "57").forEach(value -> {
                averageHours.setValue(value);
                softly.assertThat(averageHours).hasWarningWithText("Average hours worked per week should between 0 and 56");
            });
            ImmutableList.of("0", "40", "20").forEach(value -> {
                averageHours.setValue(value);
                softly.assertThat(averageHours).hasNoWarning();
            });
        });

        disabilityClaim.getDefaultWorkspace().fillFromTo(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit"),
                BenefitsPFLParticipantInformationTab.class, benefitsPflQualifyingEventTab.getClass());

        LOGGER.info("REN-21547: Steps 3, 4");
        benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON).setValue("Bond with child");
        assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(FOSTER_CARE_OR_ADOPTION_PLACEMENT))
                .isPresent().isRequired().hasValue("No");

        LOGGER.info("REN-21547: Steps 5, 6");
        benefitsPflQualifyingEventTab.getAssetList().getAsset(FOSTER_CARE_OR_ADOPTION_PLACEMENT).setValue("Yes");
        assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(DATE_OF_FOSTER_CARE_OR_ADOPTION_PLACEMENT))
                .isPresent().isRequired().hasValue("");
        benefitsPflQualifyingEventTab.getAssetList().getAsset(FOSTER_CARE_OR_ADOPTION_PLACEMENT).setValue("No");

        disabilityClaim.getDefaultWorkspace().fillFromTo(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit")
                        .mask(TestData.makeKeyPath(benefitsPflQualifyingEventTab.getMetaKey(), PFL_REASON.getLabel())),
                benefitsPflQualifyingEventTab.getClass(), CompleteNotificationTab.class, true);

        claim.claimOpen().submit();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20875", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAverageHoursWorkedPerWeek_2() {
        preconditions();

        LOGGER.info("Step 1");
        disabilityClaim.initiate(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit")
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), LOSS_DATE));
        disabilityClaim.getDefaultWorkspace().fillUpTo(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit"),
                BenefitsPFLQualifyingEventTab.class);
        TextBox hireDate = benefitsPflQualifyingEventTab.getAssetList().getAsset(DATE_OF_HIRE);
        hireDate.setValue("01/27/2019");
        assertThat(hireDate).hasWarningWithText(ERROR_MESSAGE);

        LOGGER.info("Step 2");
        benefitsPflQualifyingEventTab.fillTab(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit")
                .mask(TestData.makeKeyPath(benefitsPflQualifyingEventTab.getMetaKey(), DATE_OF_HIRE.getLabel())));
        Tab.buttonSaveAndExit.click();
        claim.claimSubmit().perform();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_MESSAGE);
        buttonCancel.click();

        LOGGER.info("Step 4");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.OVERVIEW.get());
        Page.dialogConfirmation.confirm();
        claim.claimOpen().perform();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_MESSAGE);
        buttonCancel.click();

        LOGGER.info("Step 6");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FNOL.get());
        Page.dialogConfirmation.confirm();
        benefitsPflQualifyingEventTab.navigateToTab();
        hireDate.setValue("01/26/2019");
        assertThat(hireDate).hasNoWarning();
        Tab.buttonNext.click();
        disabilityClaim.getDefaultWorkspace().fillFromTo(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit"),
                BenefitCoverageDeterminationTab.class, CompleteNotificationTab.class, true);

        claim.claimOpen().submit();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20875", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAverageHoursWorkedPerWeek_3() {
        preconditions();

        LOGGER.info("Step 8");
        disabilityClaim.initiate(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit")
                .adjust(TestData.makeKeyPath(LossEventTab.class.getSimpleName(), DATE_OF_LOSS.getLabel()), LOSS_DATE));
        disabilityClaim.getDefaultWorkspace().fillUpTo(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit")
                        .adjust(TestData.makeKeyPath(
                                benefitsPflParticipantInformationTab.getMetaKey(),
                                PARTICIPANT_WORK_DAYS.getLabel(),
                                AVERAGE_HOURS_WORKED_PER_WEEK.getLabel()), "19"),
                BenefitsPFLQualifyingEventTab.class);
        TextBox hireDate = benefitsPflQualifyingEventTab.getAssetList().getAsset(DATE_OF_HIRE);
        hireDate.setValue("01/29/2019");
        assertThat(hireDate).hasWarningWithText(ERROR_MESSAGE);

        LOGGER.info("Step 9");
        benefitsPflQualifyingEventTab.fillTab(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit")
                .mask(TestData.makeKeyPath(benefitsPflQualifyingEventTab.getMetaKey(), DATE_OF_HIRE.getLabel())));
        Tab.buttonSaveAndExit.click();
        claim.claimSubmit().perform();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_MESSAGE);
        buttonCancel.click();
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.OVERVIEW.get());
        Page.dialogConfirmation.confirm();
        claim.claimOpen().perform();
        assertThat(tableError).hasMatchingRows(MESSAGE.getName(), ERROR_MESSAGE);
        buttonCancel.click();

        LOGGER.info("Step 10");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FNOL.get());
        Page.dialogConfirmation.confirm();
        benefitsPflQualifyingEventTab.navigateToTab();
        hireDate.setValue("01/28/2019");
        assertThat(hireDate).hasNoWarning();
        Tab.buttonNext.click();
        disabilityClaim.getDefaultWorkspace().fillFromTo(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit"),
                BenefitCoverageDeterminationTab.class, CompleteNotificationTab.class, true);

        claim.claimOpen().submit();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);
    }

    private void preconditions() {
        mainApp().open();
        createDefaultNonIndividualCustomer();

        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_ForPFL")
                        .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), CaseProfileDetailsTabMetaData.EFFECTIVE_DATE.getLabel()), LOSS_DATE),
                paidFamilyLeaveMasterPolicy.getType());
        paidFamilyLeaveMasterPolicy.createPolicy(getDefaultPFLMasterPolicyData()
                .adjust(TestData.makeKeyPath("InitiniateDialog", COVERAGE_EFFECTIVE_DATE.getLabel()), LOSS_DATE)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), LOSS_DATE)
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "NY"));
    }
}
