package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitStatutorySTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationBenefitPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.common.Tab.buttonCancel;
import static com.exigen.ren.common.pages.ErrorPage.TableError.MESSAGE;
import static com.exigen.ren.common.pages.ErrorPage.tableError;
import static com.exigen.ren.main.enums.ValueConstants.*;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitSTDIncidentTabMetaData.DATE_OF_HIRE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitStatutorySTDInjuryPartyInformationTabMetaData.EMPLOYMENT_TYPE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitStatutorySTDInjuryPartyInformationTabMetaData.PART_TIME_WORKED_25_DAYS_OR_MORE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimDBLEligibilityAndBusinessRules extends ClaimGroupBenefitsSTBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28222", component = CLAIMS_GROUPBENEFITS)
    public void testClaimDBLEligibilityPartTimeWorked25DaysOrMore() {
        BenefitStatutorySTDInjuryPartyInformationTab benefitStatutorySTDInjuryPartyInformationTab = claim.updateBenefit().getWorkspace().getTab(BenefitStatutorySTDInjuryPartyInformationTab.class);

        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_ST);

        LOGGER.info("Step 1");
        createDefaultStatutoryDisabilityInsuranceClaimForMasterPolicy();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.updateBenefit().start(1);
        benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(EMPLOYMENT_TYPE).setValue("Part Time");

        LOGGER.info("Step 2");
        assertThat(benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(PART_TIME_WORKED_25_DAYS_OR_MORE)).isRequired().hasValue(EMPTY).hasOptions("Yes", "No");

        LOGGER.info("Step 3");
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        MainPage.QuickSearch.search(claimNumber);
        claim.claimSubmit().perform();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);

        claim.claimOpen().perform();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);

        LOGGER.info("Step 4");
        claim.updateBenefit().start(1);
        benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(EMPLOYMENT_TYPE).setValue("Part Time");

        benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(PART_TIME_WORKED_25_DAYS_OR_MORE).setValue(VALUE_NO);
        Tab.buttonSaveAndExit.click();

        assertThat(tableError).hasMatchingRows(MESSAGE.getName(),
                "Employee is not eligible for NY DBL Benefit. Employee must be employed part time for 25 consecutive days in current employment from the date of hire.");

        LOGGER.info("Step 5");
        buttonCancel.click();
        benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(PART_TIME_WORKED_25_DAYS_OR_MORE).setValue(VALUE_YES);
        Tab.buttonSaveAndExit.click();

        assertThat(ClaimAdjudicationBenefitPage.tableBenefitInfo).isPresent();

        LOGGER.info("Step 6");
        claim.updateBenefit().start(1);
        benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(EMPLOYMENT_TYPE).setValue("Unknown");

        assertThat(benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(PART_TIME_WORKED_25_DAYS_OR_MORE)).isAbsent();
    }


    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-28139", component = CLAIMS_GROUPBENEFITS)
    public void testClaimDBLEligibilityEmploymentType() {
        ImmutableList<String> employmentTypeValuesExpected = ImmutableList.of("Full Time", "Part Time", "Unknown");

        BenefitStatutorySTDInjuryPartyInformationTab benefitStatutorySTDInjuryPartyInformationTab = claim.updateBenefit().getWorkspace().getTab(BenefitStatutorySTDInjuryPartyInformationTab.class);

        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_ST);

        LOGGER.info("Step 1");
        String dateOfHire = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().minusDays(20).format(MM_DD_YYYY);

        disabilityClaim.create(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(benefitsStatutorySTDIncidentTab.getMetaKey(), DATE_OF_HIRE.getLabel()), dateOfHire));
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.updateBenefit().start(1);

        assertThat(benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(EMPLOYMENT_TYPE)).isOptional().hasValue(EMPTY).containsAllOptions(employmentTypeValuesExpected).isEnabled();

        LOGGER.info("Step 2");
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        MainPage.QuickSearch.search(claimNumber);
        claim.claimSubmit().perform();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);

        LOGGER.info("Step 3");
        claim.updateBenefit().start(1);

        assertThat(benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(EMPLOYMENT_TYPE)).isOptional().hasValue(EMPTY).containsAllOptions(employmentTypeValuesExpected).isEnabled();

        LOGGER.info("Step 4");
        Tab.buttonCancel.click();
        Page.dialogConfirmation.confirm();

        MainPage.QuickSearch.search(claimNumber);
        claim.claimOpen().perform();

        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);

        claim.updateBenefit().start(1);

        assertThat(benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(EMPLOYMENT_TYPE)).isRequired().hasValue(EMPTY).containsAllOptions(employmentTypeValuesExpected).isEnabled();

        LOGGER.info("Step 5");

        benefitStatutorySTDInjuryPartyInformationTab.getAssetList().getAsset(EMPLOYMENT_TYPE).setValue("Full Time");
        Tab.buttonSaveAndExit.click();

        assertThat(tableError).hasMatchingRows(MESSAGE.getName(),
                "Employee is not eligible for NY DBL Benefit. Employee must be employed full time four consecutive weeks from the date of hire.");
    }
}