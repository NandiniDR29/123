package com.exigen.ren.modules.claim.gb_ac.scenarios;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.common.metadata.ApprovalPeriodsActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.ApprovalPeriodsCancelActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.BenefitPeriodActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.EliminationPeriodActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.ApprovalPeriodsActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.ApprovalPeriodsCancelActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitPeriodActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.EliminationPeriodActionTab;
import com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitCoverageEvaluationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ac.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.modules.claim.gb_ac.tabs.BenefitPremiumWaiverIncidentTab;
import com.exigen.ren.main.modules.claim.gb_ac.tabs.BenefitPremiumWaiverInjuryPartyInformationTab;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsACBaseTest;
import com.google.common.collect.ImmutableList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimACAvailableBenefits.PREMIUM_WAIVER;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LINK_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.claim.common.tabs.ApprovalPeriodsActionTab.ListOfApprovalPeriodsTable.*;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitCoverageEvaluationTabMetaData.COVERAGE_EVALUATION;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitCoverageEvaluationTabMetaData.LIST_OF_COVERAGE;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverIncidentTabMetaData.PREMIUM_WAIVER_INCIDENT;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverIncidentTabMetaData.PremiumWaiverIncidentMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverInjuryPartyInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverInjuryPartyInformationTabMetaData.DependentAdditionalInformationMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverInjuryPartyInformationTabMetaData.EligibilityVerificationDependentMetaData.VERIFIED_POLICY_DEPENDENT;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverInjuryPartyInformationTabMetaData.EligibilityVerificationParticipantMetaData.DATE_OF_VERIFICATION;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverInjuryPartyInformationTabMetaData.EligibilityVerificationParticipantMetaData.VERIFIED_POLICY_PARTICIPANT;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverInjuryPartyInformationTabMetaData.InjuryPartyAddressMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverInjuryPartyInformationTabMetaData.PremiumWaiverPartyMetaData.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;

public class ScenarioTestClaimEnablePremiumWaiverFunctionality extends ClaimGroupBenefitsACBaseTest {

    public void testClaimEnablePremiumWaiverFunctionalityTC01(String testDataKey, boolean isForMasterPolicy) {
        BenefitPremiumWaiverInjuryPartyInformationTab benefitPremiumWaiverInjuryPartyInformationTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitPremiumWaiverInjuryPartyInformationTab.class);
        BenefitPremiumWaiverIncidentTab benefitPremiumWaiverIncidentTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitPremiumWaiverIncidentTab.class);
        BenefitCoverageEvaluationTab benefitCoverageEvaluationTab = accHealthClaim.getDefaultWorkspace().getTab(BenefitCoverageEvaluationTab.class);
        AssetList premiumWaiverPartyAssetList = benefitPremiumWaiverInjuryPartyInformationTab.getAssetList().getAsset(PREMIUM_WAIVER_PARTY);
        AssetList injuryPartyAddressAssetList = benefitPremiumWaiverInjuryPartyInformationTab.getAssetList().getAsset(INJURY_PARTY_ADDRESS);
        AssetList eligibilityVerificationParticipantAssetList = benefitPremiumWaiverInjuryPartyInformationTab.getAssetList().getAsset(ELIGIBILITY_VERIFICATION_PARTICIPANT);
        AssetList eligibilityVerificationDependentAssetList = benefitPremiumWaiverInjuryPartyInformationTab.getAssetList().getAsset(ELIGIBILITY_VERIFICATION_DEPENDENT);
        AssetList dependentAdditionalInformationAssetList = benefitPremiumWaiverInjuryPartyInformationTab.getAssetList().getAsset(DEPENDENT_ADDITIONAL_INFORMATION);
        AssetList listOfCoverageAssetList = benefitCoverageEvaluationTab.getAssetList().getAsset(LIST_OF_COVERAGE);
        AssetList coverageEvaluationAssetList = benefitCoverageEvaluationTab.getAssetList().getAsset(COVERAGE_EVALUATION);

        LOGGER.info("Step 1");
        accHealthClaim.getDefaultWorkspace().fillUpTo(accHealthClaim.getGbACTestData().getTestData(testDataKey, "TestData_With_PremiumWaiverBenefit"), BenefitPremiumWaiverInjuryPartyInformationTab.class);

        assertSoftly(softly -> {
            softly.assertThat(premiumWaiverPartyAssetList).isPresent();
            softly.assertThat(injuryPartyAddressAssetList).isPresent();

            LOGGER.info("Step 2");
            ImmutableList.of(ASSOCIATE_POLICY_PARTY, RELATIONSHIP_TO_PARTICIPANT, PREFIX, FIRST_NAME, MIDDLE_NAME, LAST_NAME, SUFFIX, SOCIAL_SECURITY_NUMBER_SSN, DATE_OF_BIRTH, CURRENT_AGE, GENDER, MARITAL_STATUS, PHONE_TYPE, PHONE, EMAIL, FAX, CONTACT_PREFERENCE, PREFERRED_PAYMENT_METHOD).forEach(control -> {
                softly.assertThat(premiumWaiverPartyAssetList.getAsset(control)).isPresent();
            });

            LOGGER.info("Step 8");
            ImmutableList.of(ADDRESS_TYPE, COUNTRY, ZIP_POSTAL_CODE, ADDRESS_LINE_1, ADDRESS_LINE_2, ADDRESS_LINE_3, CITY, STATE_PROVINCE, COUNTY).forEach(control ->
                    softly.assertThat(injuryPartyAddressAssetList.getAsset(control)).isPresent());
        });

        LOGGER.info("Step 13");
        premiumWaiverPartyAssetList.getAsset(PREFERRED_PAYMENT_METHOD).setValue("Check");

        assertSoftly(softly -> ImmutableList.of(BANK_NAME, BANK_ACCOUNT_NUMBER, BANK_TRANSIT_ROUTING_NUMBER, BANK_ACCOUNT_TYPE, SUBJECT_TO_INTERNATIONAL_ACH_FORMATTING).forEach(control ->
                softly.assertThat(premiumWaiverPartyAssetList.getAsset(control)).isAbsent()));

        LOGGER.info("Step 14");
        premiumWaiverPartyAssetList.getAsset(PREFERRED_PAYMENT_METHOD).setValue("EFT");

        assertSoftly(softly -> ImmutableList.of(BANK_NAME, BANK_ACCOUNT_NUMBER, BANK_TRANSIT_ROUTING_NUMBER, BANK_ACCOUNT_TYPE, SUBJECT_TO_INTERNATIONAL_ACH_FORMATTING).forEach(control ->
                softly.assertThat(premiumWaiverPartyAssetList.getAsset(control)).isPresent()));

        LOGGER.info("Step 17");
        injuryPartyAddressAssetList.getAsset(ZIP_POSTAL_CODE).setValue("10001");

        assertThat(injuryPartyAddressAssetList.getAsset(ZIP_POSTAL_CODE)).hasNoWarning();

        LOGGER.info("Step 18");
        accHealthClaim.getDefaultWorkspace().fillFrom(accHealthClaim.getGbACTestData().getTestData(testDataKey, "TestData_With_PremiumWaiverBenefit")
                .adjust(TestData.makeKeyPath(benefitPremiumWaiverInjuryPartyInformationTab.getMetaKey(), PREMIUM_WAIVER_PARTY.getLabel(), PREFERRED_PAYMENT_METHOD.getLabel()), "EFT")
                .adjust(TestData.makeKeyPath(benefitPremiumWaiverInjuryPartyInformationTab.getMetaKey(), PREMIUM_WAIVER_PARTY.getLabel(), BANK_NAME.getLabel()), "index=1")
                .adjust(TestData.makeKeyPath(benefitPremiumWaiverInjuryPartyInformationTab.getMetaKey(), PREMIUM_WAIVER_PARTY.getLabel(), BANK_ACCOUNT_NUMBER.getLabel()), "12345")
                .adjust(TestData.makeKeyPath(benefitPremiumWaiverInjuryPartyInformationTab.getMetaKey(), PREMIUM_WAIVER_PARTY.getLabel(), BANK_TRANSIT_ROUTING_NUMBER.getLabel()), "111111111")
                .adjust(TestData.makeKeyPath(benefitPremiumWaiverInjuryPartyInformationTab.getMetaKey(), PREMIUM_WAIVER_PARTY.getLabel(), BANK_ACCOUNT_TYPE.getLabel()), "index=1"), BenefitPremiumWaiverInjuryPartyInformationTab.class);

        accHealthClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);

        accHealthClaim.claimOpen().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);

        LOGGER.info("Step 19");
        claim.addBenefit().start(PREMIUM_WAIVER);
        premiumWaiverPartyAssetList.getAsset(ASSOCIATE_POLICY_PARTY).setValueContains("John");

        assertSoftly(softly -> {
            softly.assertThat(premiumWaiverPartyAssetList).isPresent();
            softly.assertThat(injuryPartyAddressAssetList).isPresent();
            softly.assertThat(eligibilityVerificationParticipantAssetList).isPresent();

            LOGGER.info("Step 20");
            softly.assertThat(eligibilityVerificationParticipantAssetList.getAsset(VERIFIED_POLICY_PARTICIPANT)).isPresent();
            softly.assertThat(eligibilityVerificationParticipantAssetList.getAsset(DATE_OF_VERIFICATION)).isPresent();
        });

        LOGGER.info("Step 21");
        benefitPremiumWaiverIncidentTab.navigate();

        assertSoftly(softly -> ImmutableList.of(REPORTED_DATE_OF_DIAGNOSIS, WORK_EDUCATIONAL_HISTORY, RTW_DATE, WORK_RELATED, DATE_OF_HIRE, TERMINATION_DATE, FIRST_DATE_OF_TREATMENT, ACCIDENT, HOURS_NORMALLY_WORKED_PER_WEEK, DATE_LAST_WORKED).forEach(control ->
                softly.assertThat(benefitPremiumWaiverIncidentTab.getAssetList().getAsset(PREMIUM_WAIVER_INCIDENT).getAsset(control)).isPresent()));

        LOGGER.info("Step 22");
        benefitCoverageEvaluationTab.navigate();

        assertSoftly(softly -> {
            softly.assertThat(listOfCoverageAssetList).isPresent();
            softly.assertThat(coverageEvaluationAssetList).isPresent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Benefit Amount"))).isPresent();

            LOGGER.info("Step 23");
            softly.assertThat(listOfCoverageAssetList.getAsset(BenefitCoverageEvaluationTabMetaData.PremiumWaiverIncidentMetaData.COVERAGE)).isPresent();
            softly.assertThat(listOfCoverageAssetList.getAsset(BenefitCoverageEvaluationTabMetaData.PremiumWaiverIncidentMetaData.EFFECTIVE_DATE)).isPresent();

            LOGGER.info("Step 24");
            softly.assertThat(coverageEvaluationAssetList.getAsset(BenefitCoverageEvaluationTabMetaData.CoverageEvaluationMetaData.ELIGIBILITY_VERIFIED)).isPresent();
            softly.assertThat(coverageEvaluationAssetList.getAsset(BenefitCoverageEvaluationTabMetaData.CoverageEvaluationMetaData.LIABILITY_DECISION)).isPresent();
            softly.assertThat(coverageEvaluationAssetList.getAsset(BenefitCoverageEvaluationTabMetaData.CoverageEvaluationMetaData.DATE_OF_VERIFICATION)).isPresent();
            softly.assertThat(coverageEvaluationAssetList.getAsset(BenefitCoverageEvaluationTabMetaData.CoverageEvaluationMetaData.SUPPORTING_DOCUMENTTATION_RECEIVED)).isPresent();
        });

        LOGGER.info("Step 29");
        benefitPremiumWaiverInjuryPartyInformationTab.navigate();
        premiumWaiverPartyAssetList.getAsset(PREFERRED_PAYMENT_METHOD).setValue("Check");

        assertSoftly(softly -> {
            ImmutableList.of(BANK_NAME, BANK_ACCOUNT_NUMBER, BANK_TRANSIT_ROUTING_NUMBER, BANK_ACCOUNT_TYPE).forEach(control ->
                    softly.assertThat(premiumWaiverPartyAssetList.getAsset(control)).isAbsent());
        });

        LOGGER.info("Step 30");
        premiumWaiverPartyAssetList.getAsset(PREFERRED_PAYMENT_METHOD).setValue("EFT");

        assertSoftly(softly -> ImmutableList.of(BANK_NAME, BANK_ACCOUNT_NUMBER, VIEW_BUTTON, BANK_TRANSIT_ROUTING_NUMBER, BANK_ACCOUNT_TYPE).forEach(control ->
                softly.assertThat(premiumWaiverPartyAssetList.getAsset(control)).isPresent()));

        if (isForMasterPolicy) {
            Tab.buttonCancel.click();
            dialogConfirmation.confirm();

            LOGGER.info("Step 35");
            accHealthClaim.create(accHealthClaim.getGbACTestData().getTestData(testDataKey, "TestData_Without_Benefits"));
            String claimNumber = ClaimSummaryPage.getClaimNumber();

            claim.addBenefit().start(PREMIUM_WAIVER);
            premiumWaiverPartyAssetList.getAsset(ASSOCIATE_POLICY_PARTY).setValueContains("Martha");

            assertSoftly(softly -> {
                softly.assertThat(premiumWaiverPartyAssetList).isPresent();
                softly.assertThat(injuryPartyAddressAssetList).isPresent();
                softly.assertThat(dependentAdditionalInformationAssetList).isPresent();
                softly.assertThat(eligibilityVerificationDependentAssetList).isPresent();

                LOGGER.info("Step 36");
                ImmutableList.of(FULL_TIME_DEPENDENT_STUDENT, EMPLOYED_DEPENDENT, DISABLED_DEPENDENT).forEach(control ->
                        softly.assertThat(dependentAdditionalInformationAssetList.getAsset(control)).isPresent());

                ImmutableList.of(VERIFIED_POLICY_DEPENDENT, BenefitPremiumWaiverInjuryPartyInformationTabMetaData.EligibilityVerificationDependentMetaData.DATE_OF_VERIFICATION).forEach(control ->
                        softly.assertThat(eligibilityVerificationDependentAssetList.getAsset(control)).isPresent());
            });

            LOGGER.info("Step 37");
            claim.addBenefit().getWorkspace().fillFrom(tdClaim.getTestData("NewBenefit", "TestData_PremiumWaiver")
                    .mask(TestData.makeKeyPath(benefitPremiumWaiverInjuryPartyInformationTab.getMetaKey(), ADD_NEW_BENEFIT_PREMIIUM_WAIVER.getLabel()))
                    .adjust(TestData.makeKeyPath(benefitPremiumWaiverInjuryPartyInformationTab.getMetaKey(), PREMIUM_WAIVER_PARTY.getLabel(), DATE_OF_BIRTH.getLabel()), "12/01/1985"), benefitPremiumWaiverInjuryPartyInformationTab.getClass());
            Tab.buttonSaveAndExit.click();

            search(claimNumber);

            accHealthClaim.claimSubmit().perform();
            assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.NOTIFICATION);

            accHealthClaim.claimOpen().perform();
            assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.OPEN);
        }
    }

    public void testClaimEnablePremiumWaiverFunctionalityTC02() {
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        ApprovalPeriodsActionTab approvalPeriodsActionTab = claim.premiumWaiverApprovalPeriodsAction().getWorkspace().getTab(ApprovalPeriodsActionTab.class);
        EliminationPeriodActionTab eliminationPeriodActionTab = claim.updateEliminationPeriodAction().getWorkspace().getTab(EliminationPeriodActionTab.class);
        BenefitPeriodActionTab benefitPeriodActionTab = claim.updateBenefitPeriodAction().getWorkspace().getTab(BenefitPeriodActionTab.class);
        ApprovalPeriodsCancelActionTab approvalPeriodsCancelActionTab = claim.cancelPremiumWaiverApprovalPeriodsAction().getWorkspace().getTab(ApprovalPeriodsCancelActionTab.class);

        LOGGER.info("Step 1");
        claim.claimOpen().perform();

        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_PremiumWaiver"), 1);

        claim.viewSingleBenefitCalculation().perform(1);
        LocalDateTime bpStartDate = LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();

        assertSoftly(softly -> {
            softly.assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(claim.updateEliminationPeriodAction().getName()))).isPresent();
            softly.assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(claim.updateBenefitPeriodAction().getName()))).isPresent();
            softly.assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(claim.premiumWaiverApprovalPeriodsAction().getName()))).isPresent();
        });

        LOGGER.info("Steps 2, 5");
        claim.updateEliminationPeriodAction().start(1);

        assertSoftly(softly -> {
            softly.assertThat(eliminationPeriodActionTab.getAssetList().getAsset(EliminationPeriodActionTabMetaData.ELIMINATION_PERIOD_START_DATE)).isPresent()
                    .isDisabled().hasValue(currentDate.format(MM_DD_YYYY));
            softly.assertThat(eliminationPeriodActionTab.getAssetList().getAsset(EliminationPeriodActionTabMetaData.ELIMINATION_PERIOD_END_DATE)).isPresent();
            softly.assertThat(eliminationPeriodActionTab.getAssetList().getAsset(EliminationPeriodActionTabMetaData.OVERRIDE_REASON)).isPresent();
        });
        LOGGER.info("Step 6");
        eliminationPeriodActionTab.getAssetList().getAsset(EliminationPeriodActionTabMetaData.ELIMINATION_PERIOD_END_DATE).setValue(bpStartDate.plusDays(10).format(MM_DD_YYYY));
        assertThat(eliminationPeriodActionTab.getAssetList().getAsset(EliminationPeriodActionTabMetaData.ELIMINATION_PERIOD_END_DATE))
                .hasWarningWithText("Benefit Period Start Date is before than Elimination Period End Date.");

        LOGGER.info("Step 13");
        eliminationPeriodActionTab.getAssetList().getAsset(EliminationPeriodActionTabMetaData.ELIMINATION_PERIOD_END_DATE).setValue(bpStartDate.minusDays(1).format(MM_DD_YYYY));
        eliminationPeriodActionTab.getAssetList().getAsset(EliminationPeriodActionTabMetaData.OVERRIDE_REASON).setValue("reason");
        eliminationPeriodActionTab.submitTab();

        assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(claim.updateEliminationPeriodAction().getName()))).isPresent();

        LOGGER.info("Steps 14, 17");
        claim.updateBenefitPeriodAction().start(1);

        assertSoftly(softly -> {
            softly.assertThat(benefitPeriodActionTab.getAssetList().getAsset(BenefitPeriodActionTabMetaData.BENEFIT_PERIOD_START_DATE)).isPresent()
                    .isDisabled().hasValue(bpStartDate.format(MM_DD_YYYY));
            softly.assertThat(benefitPeriodActionTab.getAssetList().getAsset(BenefitPeriodActionTabMetaData.BENEFIT_PERIOD_END_DATE)).isPresent();
            softly.assertThat(benefitPeriodActionTab.getAssetList().getAsset(BenefitPeriodActionTabMetaData.OVERRIDE_REASON)).isPresent();
        });

        LOGGER.info("Step 18");
        benefitPeriodActionTab.getAssetList().getAsset(BenefitPeriodActionTabMetaData.BENEFIT_PERIOD_END_DATE).setValue(bpStartDate.minusDays(5).format(MM_DD_YYYY));
        assertThat(benefitPeriodActionTab.getAssetList().getAsset(BenefitPeriodActionTabMetaData.BENEFIT_PERIOD_END_DATE))
                .hasWarningWithText("Benefit Period End Date is before than Benefit Period Start Date.");

        LOGGER.info("Step 25");
        benefitPeriodActionTab.getAssetList().getAsset(BenefitPeriodActionTabMetaData.BENEFIT_PERIOD_END_DATE).setValue(bpStartDate.plusMonths(6).format(MM_DD_YYYY));
        benefitPeriodActionTab.getAssetList().getAsset(BenefitPeriodActionTabMetaData.OVERRIDE_REASON).setValue("reason");
        benefitPeriodActionTab.submitTab();

        assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(claim.updateBenefitPeriodAction().getName()))).isPresent();

        LOGGER.info("Steps 30, 34");
        claim.premiumWaiverApprovalPeriodsAction().start(1);
        ApprovalPeriodsActionTab.buttonAdd.click();

        assertSoftly(softly -> {
            softly.assertThat(ApprovalPeriodsActionTab.buttonAdd).isPresent();
            softly.assertThat(ApprovalPeriodsActionTab.buttonRemove).isPresent();
            softly.assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_FREQUENCY)).isPresent().hasValue("Manually Specify");
            softly.assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE)).isPresent();
            softly.assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE)).isPresent();
            softly.assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_STATUS)).isPresent();
        });


        LOGGER.info("Step 35");
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE).setValue(bpStartDate.minusDays(5).format(MM_DD_YYYY));
        assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE))
                .hasWarningWithText("Approval Period Start date should not be before Premium Waiver Benefit Period Start Date");

        LOGGER.info("Step 36");
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE).setValue(bpStartDate.plusDays(10).format(MM_DD_YYYY));
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE).setValue(bpStartDate.plusDays(20).format(MM_DD_YYYY));
        ApprovalPeriodsActionTab.buttonAdd.click();

        assertSoftly(softly -> {
            approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE).setValue(bpStartDate.plusDays(10).format(MM_DD_YYYY));
            softly.assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE))
                    .hasWarningWithText("Approval Period Start Date conflicts with other Approval Periods.");

            LOGGER.info("Step 40");
            softly.assertThat(ApprovalPeriodsActionTab.tableListOfApprovalPeriods.getHeader().getValue())
                    .contains(APPROVAL_START_DATE.getName(), APPROVAL_END_DATE.getName(), DATE_OF_STATUS_CHANGE.getName(), APPROVER.getName(), STATUS.getName());
        });

        ApprovalPeriodsActionTab.tableListOfApprovalPeriods.getRow(1).getCell(7).controls.links.get(ActionConstants.REMOVE).click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 39");
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_STATUS).setValue("Approved");
        assertSoftly(softly -> {
            softly.assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE)).isDisabled();

            approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_STATUS).setValue("Disapproved");
            softly.assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE)).isDisabled();
        });

        LOGGER.info("Step 39.1");
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_STATUS).setValue("To Be Determined");
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE).setValue(currentDate.plusDays(1).format(MM_DD_YYYY));
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE).setValue(currentDate.format(MM_DD_YYYY));
        assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE).getWarning().orElse(EMPTY))
                .contains("Approval Period End Date should not be Before Approval Period Start Date");

        LOGGER.info("Step 39.2");
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE).setValue(currentDate.format(MM_DD_YYYY));
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE).setValue(currentDate.minusDays(1).format(MM_DD_YYYY));
        assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE).getWarning().orElse(EMPTY))
                .contains("Approval Period End date should be after today");

        LOGGER.info("Step 39.3");
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE).setValue(bpStartDate.minusDays(1).format(MM_DD_YYYY));
        assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE))
                .hasWarningWithText("Approval Period End Date should not be Before Premium Waiver Benefit Period Start Date");

        LOGGER.info("Step 39.4");
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE).setValue(bpStartDate.plusMonths(6).plusDays(1).format(MM_DD_YYYY));
        assertThat(approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE))
                .hasWarningWithText("Approval Period End date should be before Benefit Period End Date");

        LOGGER.info("Step 41");
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_START_DATE).setValue(bpStartDate.plusDays(1).format(MM_DD_YYYY));
        approvalPeriodsActionTab.getAssetList().getAsset(ApprovalPeriodsActionTabMetaData.APPROVAL_PERIOD_END_DATE).setValue(bpStartDate.plusDays(15).format(MM_DD_YYYY));
        approvalPeriodsActionTab.submitTab();

        assertThat(new Link(COMMON_LINK_WITH_TEXT_LOCATOR.format(claim.cancelPremiumWaiverApprovalPeriodsAction().getName()))).isPresent();

        LOGGER.info("Step 42");
        claim.cancelPremiumWaiverApprovalPeriodsAction().start(1);

        assertSoftly(softly -> {
            softly.assertThat(ApprovalPeriodsCancelActionTab.tableListOfUnProcessedApprovalPeriods.getHeader().getValue())
                    .contains(APPROVAL_START_DATE.getName(), APPROVAL_END_DATE.getName(), DATE_OF_STATUS_CHANGE.getName(), APPROVER.getName(), STATUS.getName());
            softly.assertThat(approvalPeriodsCancelActionTab.getAssetList().getAsset(ApprovalPeriodsCancelActionTabMetaData.CANCEL_REASON)).isPresent();
        });
    }
}