package com.exigen.ren.modules.rating.gb_pfl;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.helpers.logging.RatingLogGrabber;
import com.exigen.ren.helpers.logging.RatingLogHolder;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.common.tabs.common.RateDialogs;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.PaidFamilyLeaveMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.ClassificationManagementTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PlanDefinitionTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PolicyInformationTab;
import com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PremiumSummaryTab;
import com.exigen.ren.main.pages.summary.QuoteSummaryPage;
import com.exigen.ren.modules.rating.RatingBaseTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.ActionConstants.VIEW_RATE_DETAILS;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.*;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.ClassificationManagementTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.metadata.PlanDefinitionTabMetaData.BenefitScheduleMetaData.MAXIMUM_WEEKLY_BENEFIT_AMOUNT;
import static com.exigen.ren.main.modules.policy.gb_pfl.masterpolicy.tabs.PremiumSummaryTab.premiumSummaryCoveragesTable;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestRatingIntegrationPFL extends RatingBaseTest implements PaidFamilyLeaveMasterPolicyContext {

    @Test(groups = {WITHOUT_TS, REGRESSION, RATING_INTEGRATION})
    @TestInfo(testCaseId = "REN-42138", component = POLICY_GROUPBENEFITS)
    public void testRatingIntegrationPFL() {
        mainApp().open();

        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(paidFamilyLeaveMasterPolicy.getType());

        LOGGER.info("TEST: Steps 1-3 ");
        TestData td = tdSpecific().getTestData("TestData");
        TestData tdExpectedRequest = tdSpecific().getTestData("NonFillableRequestData");
        paidFamilyLeaveMasterPolicy.initiate(td);
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillFromTo(td, PolicyInformationTab.class, PlanDefinitionTab.class, true);
        tdExpectedRequest.adjust("plans[0].benefitMax", new Currency(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(MAXIMUM_WEEKLY_BENEFIT_AMOUNT).getValue()).toPlainString().split(".00")[0])
                .adjust("plans[0].benefitPct", String.valueOf(new Float(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.BENEFIT_SCHEDULE).getAsset(BENEFIT_PERCENTAGE).getValue().split("%")[0]) / 100));
        planDefinitionTab.submitTab();
        paidFamilyLeaveMasterPolicy.getDefaultWorkspace().fillFromTo(td, ClassificationManagementTab.class, PremiumSummaryTab.class);
        premiumSummaryTab.rate();
        assertThat(QuoteSummaryPage.labelQuoteStatus).hasValue(ProductConstants.StatusWhileCreating.PREMIUM_CALCULATED);
        String quoteNumber = QuoteSummaryPage.getQuoteNumber();
        tdExpectedRequest.adjust("policyID", quoteNumber);

        LOGGER.info("TEST: Steps 4");
        RatingLogHolder ratingLogHolder = new RatingLogGrabber().grabRatingLog(quoteNumber);
        LOGGER.info(String.format("Request from rating log:\n %s", ratingLogHolder.getRequestLog().getFormattedLogContent()));
        LOGGER.info(String.format("Response from rating log:\n %s", ratingLogHolder.getResponseLog().getFormattedLogContent()));
        Map<String, String> requestFromLog = ratingLogHolder.getRequestLog().getOpenLFieldsMap();
        Map<String, String> responseFromLog = ratingLogHolder.getResponseLog().getOpenLFieldsMap();
        Map<String, String> expectedRequest = createExpectedRequestData(tdExpectedRequest);
        assertThat(requestFromLog).containsAllEntriesOf(expectedRequest);

        LOGGER.info("TEST: Step 5");
        Collection<String> list = tdSpecific().getTestData("ResponseData").getKeys();
        assertThat(responseFromLog.keySet()).containsAll(list);

        String rateValueFromResponse = new BigDecimal(responseFromLog.get("planCalcs[0].premiumRate")).toString();
        Currency totalVolume = new Currency(td.getTestData(ClassificationManagementTab.class.getSimpleName()).getValue(TOTAL_VOLUME.getLabel()));
        assertSoftly(softly -> {
            LOGGER.info("TEST: Verification expected response premium values that were provided by OpenL Team (hardcoded)");
            softly.assertThat(responseFromLog).containsAllEntriesOf(getCheckRatingPremiumData());

            LOGGER.info("TEST: Step 6");
            softly.assertThat(premiumSummaryCoveragesTable.getRow(1).getCell(MANUAL_RATE.getName()).getValue()).contains(rateValueFromResponse);
            softly.assertThat(premiumSummaryCoveragesTable.getRow(1).getCell(QUOTE_RATE.getName()).getValue()).contains(rateValueFromResponse);
            softly.assertThat(premiumSummaryCoveragesTable.getRow(1).getCell(ANNUAL_PREMIUM.getName())).isPresent();
            softly.assertThat(new Currency(premiumSummaryCoveragesTable.getRow(1).getCell(VOLUME.getName()).getValue())).isEqualTo(totalVolume);

            LOGGER.info("TEST: Step 7");
            softly.assertThat(PremiumSummaryTab.txtRatingFormula).hasValue(responseFromLog.get("nature"));
        });

        LOGGER.info("TEST: Step 8");
        premiumSummaryCoveragesTable.getRow(1).getCell(8).controls.buttons.get(VIEW_RATE_DETAILS).click();
        assertThat(RateDialogs.ViewRateDetailsDialog.tableRateDetails.getRow(1).getCell(TableConstants.RateDetailsTable.RATE.getName()).getValue()).contains(rateValueFromResponse);
        RateDialogs.ViewRateDetailsDialog.close();

        LOGGER.info("TEST: Step 9");
        PaidFamilyLeaveMasterPolicyContext.classificationManagementTab.navigateToTab();
        assertSoftly(softly -> {
            softly.assertThat(PaidFamilyLeaveMasterPolicyContext.classificationManagementTab.getAssetList().getAsset(NUMBER_OF_PARTICIPANTS)).hasValue
                    (td.getValue(ClassificationManagementTab.class.getSimpleName(), NUMBER_OF_PARTICIPANTS.getLabel()));
            softly.assertThat(new Currency(PaidFamilyLeaveMasterPolicyContext.classificationManagementTab.getAssetList().getAsset(TOTAL_VOLUME).getValue())).isEqualTo(totalVolume);
            softly.assertThat(PaidFamilyLeaveMasterPolicyContext.classificationManagementTab.getAssetList().getAsset(RATE).getValue()).contains(rateValueFromResponse);
        });
    }

    private Map<String, String> createExpectedRequestData(TestData td) {
        Map<String, String> result;
        Collection<String> list = td.getKeys();
        result = list.stream().collect(Collectors.toMap(Function.identity(), s -> td.getValue(String.valueOf(s)), (a, b) -> b, LinkedHashMap::new));
        result.put("state", tdSpecific().getTestData("TestData").getValue(PolicyInformationTab.class.getSimpleName(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()));
        result.put("rateGuarantee", tdSpecific().getTestData("TestData").getValue(PolicyInformationTab.class.getSimpleName(), PolicyInformationTabMetaData.RATE_GUARANTEE_MONTHS.getLabel()));
        result.put("requestDate", TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD));
        result.put("effectiveDate", TimeSetterUtil.getInstance().getCurrentTime().format(YYYY_MM_DD));
        return result;
    }

}
