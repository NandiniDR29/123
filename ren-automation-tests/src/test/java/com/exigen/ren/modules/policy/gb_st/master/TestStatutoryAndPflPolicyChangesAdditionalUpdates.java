package com.exigen.ren.modules.policy.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomAssertions;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.DialogRateAndPremiumOverride;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.CaseProfileConstants;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.ClassificationManagementTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.CaseProfileSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ActionConstants.CHANGE;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.PFL_NY;
import static com.exigen.ren.main.enums.CoveragesConstants.GroupStatutoryCoverages.STAT_NY;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.CUSTOMER_ACCEPTED;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_ACTIVE;
import static com.exigen.ren.main.enums.ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.VOLUME;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UPLOAD_FILE;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UploadFileDialog.FILE_UPLOAD;
import static com.exigen.ren.main.modules.policy.gb_st.masterpolicy.tabs.ClassificationManagementTab.tableClassificationSubGroupsAndRatingInfo;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class TestStatutoryAndPflPolicyChangesAdditionalUpdates extends BaseTest implements CustomerContext, CaseProfileContext, StatutoryDisabilityInsuranceMasterPolicyContext {

    private final static String MSG_VGBC0026 = "[VGBC0026] If 'Coverage Name' = \"Stat NJ\" AND 'Tier' = \"Employer\", then Rate cannot be negative; else, Rate cannot be negative or equal to zero";
    private final static String PREMIUM_FUND = "Premium Fund";
    private final static String ERR_MSG = "If 'Coverage Name' = \"Stat NJ\" AND 'Tier' = \"Employer\", then Rate cannot be negative; else, Rate cannot be negative or equal to zero";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42794", component = POLICY_GROUPBENEFITS)
    public void testStatutoryAndPflPolicyChangesAdditionalUpdatesRules1() {
        LOGGER.info("REN-42794 Precondition");
        commonPrecondition();

        LOGGER.info("REN-42794 Step 1-3");
        statutoryDisabilityInsuranceMasterPolicy.initiateAndFillUpToTab(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY), ClassificationManagementTab.class, true);
        IntStream.range(0, ClassificationManagementTab.tablePlanTierAndRatingInfo.getRowsCount())
                .forEach(row -> ClassificationManagementTab.tablePlanTierAndRatingInfo.getRow(row + 1).getCell(5).controls.links.getFirst().click());
        classificationManagementMpTab.submitTab();
        premiumSummaryTab.getAssetList().fill(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(premiumSummaryTab.getMetaKey(), PremiumSummaryTabMetaData.CREDIBILITY_FACTOR.getLabel()), "0.00"));
        classificationManagementMpTab.navigate();
        IntStream.range(0, ClassificationManagementTab.tablePlanTierAndRatingInfo.getRowsCount())
                .forEach(row -> {
                    ClassificationManagementTab.tablePlanTierAndRatingInfo.getRow(row + 1).getCell(5).controls.links.getFirst().click();
                    classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.NUMBER_OF_PARTICIPANTS).setValue("1");
                    classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.VOLUME).setValue("0");
                });
        classificationManagementMpTab.submitTab();
        premiumSummaryTab.rate();

        LOGGER.info("REN-42794 Step 4");
        assertThat(PremiumSummaryTab.premiumSummaryCoveragesTable.getRowContains(COVERAGE_NAME.getName(), "NJ").getCell(VOLUME.getName())).hasValue(EMPTY);
        assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);

        LOGGER.info("REN-42794 Step 5");
        Tab.buttonNext.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("REN-42794 Step 6");
        statutoryDisabilityInsuranceMasterPolicy.propose().start();
        statutoryDisabilityInsuranceMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultSTMasterPolicyData(), ProposalActionTab.class, false);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 1, "-0.02", MSG_VGBC0026);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 2, "-0.02", MSG_VGBC0026);

        LOGGER.info("REN-42794 Step 7");
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 1, "0", MSG_VGBC0026);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 2, "0", EMPTY);

        LOGGER.info("REN-42794 Step 8");
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 1, "0.02", EMPTY);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 2, "0.02", EMPTY);

        LOGGER.info("REN-42794 Step 9");
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 1, "0.00260000", EMPTY);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 2, "0.00000000", EMPTY);
        ProposalActionTab.setRateUpdateReasonForQuote(policyNumber, 1, PREMIUM_FUND);
        ProposalActionTab.buttonCalculatePremium.click();
        String proposalNumber = ProposalActionTab.labelProposalNumber.getValue();
        assertThat(ProposalActionTab.labelProposalStatus).hasValue(CaseProfileConstants.ProposalStatus.PROPOSED_PREMIUM_CALCULATED);
        ProposalActionTab.buttonGeneratePreProposal.click();
        Page.dialogConfirmation.confirm();
        CaseProfileSummaryPage.updateExistingProposalByNumber(proposalNumber);
        ProposalActionTab.buttonGenerateProposal.click();
        Page.dialogConfirmation.confirm();
        MainPage.QuickSearch.search(policyNumber);

        LOGGER.info("REN-42794 Step 12-14");
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(getDefaultSTMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(CUSTOMER_ACCEPTED);
        statutoryDisabilityInsuranceMasterPolicy.issue().perform(getDefaultSTMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("REN-42794 Step 15");
        startEndorsementAndOpenClassificationTab();
        assertSoftly(softly -> {
            softly.assertThat(ClassificationManagementTab.tablePlanTierAndRatingInfo.getRow(1).getCell(TableConstants.PlanTierAndRatingSelection.RATE.getName())).hasValue("0.00260000");
            softly.assertThat(ClassificationManagementTab.tablePlanTierAndRatingInfo.getRow(2).getCell(TableConstants.PlanTierAndRatingSelection.RATE.getName())).hasValue("0.00000000");
        });

        LOGGER.info("REN-42794 Step 16");
        IntStream.range(0, ClassificationManagementTab.tablePlanTierAndRatingInfo.getRowsCount())
                .forEach(row -> {
                    ClassificationManagementTab.tablePlanTierAndRatingInfo.getRow(row + 1).getCell(5).controls.links.getFirst().click();
                    classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.VOLUME).setValue("0");
                });
        Tab.buttonNext.click();
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.INITIATED);

        LOGGER.info("REN-42794 Step 17");
        premiumSummaryTab.rate();
        assertSoftly(softly -> {
            softly.assertThat(PremiumSummaryTab.premiumSummaryCoveragesTable.getRowContains(COVERAGE_NAME.getName(), "NJ").getCell(VOLUME.getName())).hasValue(EMPTY);
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
        });

        LOGGER.info("REN-42794 Step 18");
        PremiumSummaryTab.buttonMaintainInForceRates.click();
        PremiumSummaryTab.buttonViewRatingDetails.click();
        assertSoftly(softly -> {
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
            softly.assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getRow(2).getCell(TableConstants.RateDetailsTable.RATE.getName())).hasValue("0.00000");
            softly.assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getRow(2).getCell(TableConstants.RateDetailsTable.IN_FORCE_RATE.getName())).hasValue("0.00000");
        });
        RateDialogs.ViewRateDetailsDialog.close();
        Tab.buttonNext.click();

        LOGGER.info("REN-42794 Step 19");
        PolicySummaryPage.buttonPendedEndorsement.click();
        statutoryDisabilityInsuranceMasterPolicy.issue().perform(getDefaultSTMasterPolicyData()
                .adjust(BillingAccountTab.class.getSimpleName(), new ArrayList<>(Collections.singletonList(new SimpleDataProvider()))));
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42864", component = POLICY_GROUPBENEFITS)
    public void testStatutoryAndPflPolicyChangesAdditionalUpdatesRules2() {
        LOGGER.info("REN-42864 Precondition");
        commonPrecondition();

        LOGGER.info("REN-42864 Step 1-5");
        statutoryDisabilityInsuranceMasterPolicy.initiateAndFillUpToTab(tdSpecific().getTestData("TestData_NY")
                .adjust(TestData.makeKeyPath(premiumSummaryTab.getMetaKey(), PremiumSummaryTabMetaData.CREDIBILITY_FACTOR.getLabel()), "0.00"), PremiumSummaryTab.class, true);

        LOGGER.info("REN-42864 Step 6");
        classificationManagementMpTab.navigate();
        ClassificationManagementTab.tablePlansAndCoverages.getRow(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName(), STAT_NY).getCell(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName()).click();
        assertSoftly(softly -> {
            tableClassificationSubGroupsAndRatingInfo.getRows().forEach(row -> {
                row.getCell(tableClassificationSubGroupsAndRatingInfo.getColumnsCount()).controls.links.get(CHANGE).click();
                softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.NUMBER_OF_PARTICIPANTS).getValue())
                        .isNotEqualTo("1");
                softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.TOTAL_VOLUME).getValue())
                        .isNotEqualTo(new Currency(5).toString());
            });
        });

        ClassificationManagementTab.tablePlansAndCoverages.getRow(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName(), PFL_NY).getCell(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName()).click();
        assertSoftly(softly -> {
            tableClassificationSubGroupsAndRatingInfo.getRows().forEach(row -> {
                row.getCell(tableClassificationSubGroupsAndRatingInfo.getColumnsCount()).controls.links.get(CHANGE).click();
                softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.NUMBER_OF_PARTICIPANTS))
                        .hasValue("1");
                softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.TOTAL_VOLUME))
                        .hasValue(new Currency(5).toString());
            });
        });

        LOGGER.info("REN-42864 Step 7");
        premiumSummaryTab.navigate();
        premiumSummaryTab.rate();
        assertSoftly(softly -> {
            IntStream.range(0, PremiumSummaryTab.premiumSummaryCoveragesTable.getRowsCount())
                    .forEach(row -> {
                        assertThat(PremiumSummaryTab.premiumSummaryCoveragesTable.getRow(row + 1).getCell(VOLUME.getName()).getValue()).isNotEmpty();
                    });
            softly.assertThat(QuoteSummaryPage.labelQuoteStatus).valueContains(PREMIUM_CALCULATED);
        });

        LOGGER.info("REN-42864 Step 8");
        premiumSummaryTab.buttonNext.click();
        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).valueContains(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("REN-42864 Step 9");
        statutoryDisabilityInsuranceMasterPolicy.propose().start();
        statutoryDisabilityInsuranceMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultSTMasterPolicyData(), ProposalActionTab.class, false);
        String msgVGBC0025 = "[VGBC0025] Rate cannot be negative or equal to zero";
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 1, "-0.02", msgVGBC0025);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 2, "-0.02", msgVGBC0025);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 3, "-0.02", msgVGBC0025);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 4, "-0.02", MSG_VGBC0026);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 5, "-0.02", MSG_VGBC0026);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 6, "-0.02", MSG_VGBC0026);

        LOGGER.info("REN-42864 Step 10");
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 1, "0.00", msgVGBC0025);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 2, "0.00", msgVGBC0025);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 3, "0.00", msgVGBC0025);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 4, "0.00", MSG_VGBC0026);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 5, "0.00", MSG_VGBC0026);
        setValueOnMultiQuoteProposalAndCheckErrorByPolicy(policyNumber, 6, "0.00", MSG_VGBC0026);

        LOGGER.info("REN-42864 Step 11");
        setValueOnMultiQuoteProposalAndCheckErrorByCoverage(policyNumber, STAT_NY, 1, "0.02", EMPTY);
        setValueOnMultiQuoteProposalAndCheckErrorByCoverage(policyNumber, STAT_NY, 2, "0.02", EMPTY);
        setValueOnMultiQuoteProposalAndCheckErrorByCoverage(policyNumber, STAT_NY, 3, "0.02", EMPTY);
        setValueOnMultiQuoteProposalAndCheckErrorByCoverage(policyNumber, PFL_NY, 4, "0.02", EMPTY);
        setValueOnMultiQuoteProposalAndCheckErrorByCoverage(policyNumber, PFL_NY, 5, "0.02", EMPTY);
        setValueOnMultiQuoteProposalAndCheckErrorByCoverage(policyNumber, PFL_NY, 6, "0.02", EMPTY);

        IntStream.range(1, 7).forEach(index ->
                ProposalActionTab.setRateUpdateReasonForQuote(policyNumber, index, PREMIUM_FUND));

        LOGGER.info("REN-42864 Step 12");
        statutoryDisabilityInsuranceMasterPolicy.propose().getWorkspace().fillFrom(getDefaultSTMasterPolicyData(), ProposalActionTab.class);

        LOGGER.info("REN-42864 Step 13-14");
        MainPage.QuickSearch.search(policyNumber);
        statutoryDisabilityInsuranceMasterPolicy.acceptContract().perform(getDefaultSTMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(CUSTOMER_ACCEPTED);
        statutoryDisabilityInsuranceMasterPolicy.issue().perform(getDefaultSTMasterPolicyData());
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_ACTIVE);

        LOGGER.info("REN-42864 Step 15");
        startEndorsementAndOpenClassificationTab();
        // TODO (ybandarenka) Need to update after fix REN-45969
        ClassificationManagementTab.tablePlansAndCoverages.getRow(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName(), PFL_NY).getCell(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName()).click();
        classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.NUMBER_OF_PARTICIPANTS)
                .setValue("1");
        classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.TOTAL_VOLUME)
                .setValue("5");

        LOGGER.info("REN-42864 Step 16");
        Tab.buttonNext.click();
        PremiumSummaryTab.buttonRemoveRatingCensus.click();
        premiumSummaryTab.getAssetList().fill(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(premiumSummaryTab.getMetaKey(), PremiumSummaryTabMetaData.CREDIBILITY_FACTOR.getLabel()), "0.00"));

        LOGGER.info("REN-42864 Step 17");
        classificationManagementMpTab.navigate();
        ClassificationManagementTab.tablePlansAndCoverages.getRow(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName(), PFL_NY).getCell(TableConstants.PlansAndCoverages.COVERAGE_NAME.getName()).click();
        assertSoftly(softly -> {
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.NUMBER_OF_PARTICIPANTS))
                    .hasValue("1");
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.TOTAL_VOLUME))
                    .hasValue(new Currency(5).toString());
        });
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_ST, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = "REN-42911", component = POLICY_GROUPBENEFITS)
    public void testStatutoryAndPflPolicyChangesAdditionalUpdatesRulesRateAndPremiumOverride() {
        LOGGER.info("REN-42794 Precondition");
        commonPrecondition();
        LOGGER.info("REN-42794 Step 1-3");
        statutoryDisabilityInsuranceMasterPolicy.initiateAndFillUpToTab(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY), ClassificationManagementTab.class, true);
        IntStream.range(0, ClassificationManagementTab.tablePlanTierAndRatingInfo.getRowsCount())
                .forEach(row -> ClassificationManagementTab.tablePlanTierAndRatingInfo.getRow(row + 1).getCell(5).controls.links.getFirst().click());
        classificationManagementMpTab.submitTab();

        LOGGER.info("REN-42794 Step 4");
        premiumSummaryTab.getAssetList().fill(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(premiumSummaryTab.getMetaKey(), PremiumSummaryTabMetaData.CREDIBILITY_FACTOR.getLabel()), "0.00"));
        premiumSummaryTab.rate();

        LOGGER.info("REN-42794 Step 5");
        PremiumSummaryTab.openOverrideAndRatePremiumPopUp();
        DialogRateAndPremiumOverride.overrideReason.setValue(PREMIUM_FUND);
        setValueRateAndPremiumOverrideAndCheckErrorDialog(1, "-0.020", ERR_MSG);
        setValueRateAndPremiumOverrideAndCheckErrorDialog(2, "-0.020", ERR_MSG);

        LOGGER.info("REN-42794 Step 6");
        setValueRateAndPremiumOverrideAndCheckErrorDialog(1, "0", ERR_MSG);
        setValueRateAndPremiumOverrideAndCheckErrorDialog(2, "0", EMPTY);

        LOGGER.info("REN-42794 Step 7");
        setValueRateAndPremiumOverrideAndCheckErrorDialog(1, "0.020", EMPTY);
        setValueRateAndPremiumOverrideAndCheckErrorDialog(2, "0.020", EMPTY);

        LOGGER.info("REN-42794 Step 8");
        setValueRateAndPremiumOverrideAndCheckErrorDialog(1, "0.020", EMPTY);
        setValueRateAndPremiumOverrideAndCheckErrorDialog(2, "0.000", EMPTY);
        DialogRateAndPremiumOverride.dialogPremiumOverride.confirm();
    }

    private void commonPrecondition() {
        mainApp().open("qa", "qa");
        createDefaultNonIndividualCustomer();
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(statutoryDisabilityInsuranceMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(FileIntakeManagementTab.class.getSimpleName() + "[0]", UPLOAD_FILE.getLabel(), FILE_UPLOAD.getLabel()), "$<file:REN_Rating_Census_File_All.xlsx>"));
    }

    private void startEndorsementAndOpenClassificationTab() {
        statutoryDisabilityInsuranceMasterPolicy.endorse().start().getWorkspace();
        statutoryDisabilityInsuranceMasterPolicy.endorse().getWorkspace().fill(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ENDORSEMENT, TestDataKey.DEFAULT_TEST_DATA_KEY));
        Tab.buttonOk.click();
        Page.dialogConfirmation.confirm();
        classificationManagementMpTab.navigate();
    }

    private void setValueRateAndPremiumOverrideAndCheckErrorDialog(int index, String value, String errorMessage) {
        DialogRateAndPremiumOverride.setOverriddenTermRateValueByIndex(index - 1, value);
        if (!errorMessage.equals(EMPTY)) {
            assertThat(DialogRateAndPremiumOverride.getValidationMessageValueByIndex(index)).hasValue(errorMessage);
        } else {
            assertThat(DialogRateAndPremiumOverride.getValidationMessageValueByIndex(index)).isAbsent();
        }
    }

    private void setValueOnMultiQuoteProposalAndCheckErrorByPolicy(String policyNumber, int rowNumber, String value, String errorMessage) {
        ProposalActionTab.getProposedTermRateForQuote(policyNumber, rowNumber).setValue(value);
        if (!errorMessage.equals(EMPTY)) {
            assertThat(ProposalActionTab.getDiscountReasonErrorMessageForQuoteByPolicy(policyNumber, rowNumber)).hasValue(errorMessage);
        } else {
            assertThat(ProposalActionTab.getDiscountReasonErrorMessageForQuoteByPolicy(policyNumber, rowNumber)).isAbsent();
        }
    }

    private void setValueOnMultiQuoteProposalAndCheckErrorByCoverage(String policyNumber, String coverage, int rowNumber, String value, String errorMessage) {
        ProposalActionTab.getProposedTermRateForQuote(policyNumber, rowNumber).setValue(value);
        if (!errorMessage.equals(EMPTY)) {
            assertThat(ProposalActionTab.getDiscountReasonErrorMessageForQuoteByCoverage(coverage, rowNumber)).hasValue(errorMessage);
        } else {
            assertThat(ProposalActionTab.getDiscountReasonErrorMessageForQuoteByCoverage(coverage, rowNumber)).isAbsent();
        }
    }
}
