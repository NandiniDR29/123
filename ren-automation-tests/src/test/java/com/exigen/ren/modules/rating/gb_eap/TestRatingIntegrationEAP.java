package com.exigen.ren.modules.rating.gb_eap;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.helpers.logging.RatingLogHolder;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.EmployeeAssistanceProgramMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.ClassificationManagementTabMetaData;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.rating.RatingBaseTest;
import org.testng.annotations.Test;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.ActionConstants.VIEW_RATE_DETAILS;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.MANUAL_RATE;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PlanDefinitionTabMetaData.PLAN;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.ClassificationManagementTab.tableCoverageRelationships;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.PremiumSummaryTab.premiumSummaryCoveragesTable;
import static com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs.PremiumSummaryTab.txtRatingFormula;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.RATING_INTEGRATION;
import static com.exigen.ren.utils.groups.Groups.REGRESSION;
import static com.exigen.ren.utils.groups.Groups.WITHOUT_TS;

public class TestRatingIntegrationEAP extends RatingBaseTest implements EmployeeAssistanceProgramMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = {"REN-43667"}, component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationEAP() {
        TestData tdQuote = tdSpecific().getTestData("TestData");
        TestData tdExpectedResponse = tdSpecific().getTestData("ResponseData");

        LOGGER.info("TEST: Steps 1-3");
        mainApp().open();
        customerNonIndividual.create(getDefaultCustomerNonIndividualTestData());
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(employeeAssistanceProgramMasterPolicy.getType()));
        initiateEAPQuoteAndFillToTab(tdSpecific().getTestData("TestData"), premiumSummaryTab.getClass(), true);
        premiumSummaryTab.rate();
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
        String quoteNumber = QuoteSummaryPage.getQuoteNumber();
        tdQuote.adjust("policyID", quoteNumber);

        LOGGER.info("TEST: Step 4");
        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(quoteNumber);
        LOGGER.info(String.format("Request from rating log:\n %s", ratingLogHolder.getRequestLog().getFormattedLogContent()));
        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));

        Map<String, String> requestFromLog = ratingLogHolder.getRequestLog().getOpenLFieldsMap();
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();
        Map<String, String> expectedRequest = createExpectedRequestData(tdQuote);

        assertThat(requestFromLog).containsAllEntriesOf(expectedRequest);

        LOGGER.info("TEST: Step 5");
        Collection<String> list = tdExpectedResponse.getKeys();
        assertThat(responseFromLog.keySet()).containsAll(list);

        assertSoftly(softly -> {
            LOGGER.info("TEST: Verification expected response premium values that were provided by OpenL Team (hardcoded)");
            softly.assertThat(responseFromLog).containsAllEntriesOf(getCheckRatingPremiumData());

            LOGGER.info("TEST: Step 6");
            String rateFromResponse = new Currency(responseFromLog.get("planDetails[0].planRate"), RoundingMode.HALF_UP).toString();
            softly.assertThat(premiumSummaryCoveragesTable.getRow(1).getCell(MANUAL_RATE.getName()))
                    .hasValue(rateFromResponse);

            LOGGER.info("TEST: Step 7");
            softly.assertThat(txtRatingFormula).hasValue(responseFromLog.get("nature"));

            LOGGER.info("TEST: Step 8");
            premiumSummaryCoveragesTable.getRow(1).getCell(7).controls.buttons.get(VIEW_RATE_DETAILS).click();
            softly.assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getRow(1).getCell(TableConstants.RateDetailsTable.RATE.getName()))
                    .hasValue(rateFromResponse);
            RateDialogs.ViewRateDetailsDialog.close();

            LOGGER.info("TEST: Step 9");
            classificationManagementMpTab.navigateToTab();
            softly.assertThat(tableCoverageRelationships.getRow(1).getCell(TableConstants.CoverageRelationships.RATE.getName()))
                    .hasValue(rateFromResponse);
            softly.assertThat(classificationManagementMpTab.getAssetList().getAsset(ClassificationManagementTabMetaData.RATE))
                    .hasValue(rateFromResponse);
        });

    }

    private Map<String, String> createExpectedRequestData(TestData tdQuote) {
        Map<String, String> result = new HashMap<>();
        String currentDate = TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD);
        result.put("effectiveDate", currentDate);
        result.put("requestDate", currentDate);
        result.put("quoteType", "New Business");
        result.put("state", tdQuote.getTestData(PolicyInformationTab.class.getSimpleName()).getValue(PolicyInformationTabMetaData.SITUS_STATE.getLabel()));
        result.put("zip", tdQuote.getTestData(PolicyInformationTab.class.getSimpleName()).getValue(PolicyInformationTabMetaData.ZIP_CODE.getLabel()));
        result.put("plans[0].planName", tdQuote.getTestDataList(planDefinitionTab.getMetaKey()).get(0).getValue(PLAN.getLabel()));
        result.put("plans[0].ratingBasis", "PMNTH");
        result.put("plans[0].planType", "FACETOFACE");
        return result;
    }
}
