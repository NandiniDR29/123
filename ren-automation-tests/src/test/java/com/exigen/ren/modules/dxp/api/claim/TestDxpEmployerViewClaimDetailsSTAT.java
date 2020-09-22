package com.exigen.ren.modules.dxp.api.claim;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.common.tabs.SingleBenefitCalculationActionTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimSTContext;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.modules.policy.gb_st.masterpolicy.StatutoryDisabilityInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels.ClaimsDisabilityEmployerClaimsModel;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels.StatutoryShortTermDisabilityCoverageEvaluation;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.ren.main.modules.claim.common.tabs.SingleBenefitCalculationActionTab.EliminationQualificationPeriod.ELIMINATION_PERIOD;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageEvaluationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageEvaluationTabMetaData.ELIGIBILITY_VERIFIED;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpEmployerViewClaimDetailsSTAT extends RestBaseTest implements CaseProfileContext,
        StatutoryDisabilityInsuranceMasterPolicyContext, DisabilityClaimSTContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40097", component = CUSTOMER_REST)
    public void testDxpEmployerViewClaimDetails_STAT() {
        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());

        createDefaultStatutoryDisabilityInsuranceMasterPolicy();
        createDefaultStatutoryDisabilityInsuranceClaimWithoutBenefits();
        disabilityClaim.claimOpen().perform();
        LocalDate dateOfLoss = LocalDate.parse(ClaimSummaryPage.tableLossEvent.getRow(1).getCell("Reported Date of Loss").controls.links.getFirst().getValue(), DateTimeUtils.MM_DD_YYYY);

        LocalDateTime currDatePlus10Days = TimeSetterUtil.getInstance().getCurrentTime().plusYears(10);
        disabilityClaim.addBenefit().perform(disabilityClaim.getSTTestData().getTestData("NewBenefit", "TestData")
                .adjust(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), APPROVED_THROUGH_DATE.getLabel()), currDatePlus10Days.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), ELIGIBILITY_VERIFIED.getLabel()), "Yes"));

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        disabilityClaim.calculateSingleBenefitAmount().perform(disabilityClaim.getPFLTestData().getTestData("CalculateASingleBenefitAmount", "TestData"), 1);
        disabilityClaim.viewSingleBenefitCalculation().perform(1);

        String benefitPeriod = ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1).getCell(BENEFIT_PERIOD.getName()).getValue();
        String eliminationPeriod = SingleBenefitCalculationActionTab.eliminationQualificationPeriodTable.getRow(1).getCell(ELIMINATION_PERIOD.getName()).getValue();

        LOGGER.info("Step 1");
        assertSoftly(softly -> {
            ResponseContainer<ClaimsDisabilityEmployerClaimsModel> response = dxpRestService.getEmployerClaimsDisability(customerNumberAuthorize, claimNumber);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);

            softly.assertThat(response.getModel().getClaimNumber()).isEqualTo(claimNumber);
            softly.assertThat(response.getModel().getClaimStatusCd()).isEqualTo("OPEN");
            softly.assertThat(response.getModel().getLossDt()).isEqualTo(dateOfLoss.format(DateTimeUtilsHelper.YYYY_MM_DD));

            StatutoryShortTermDisabilityCoverageEvaluation coverageEvaluation_STAT = response.getModel().getClaimsDamages().get(0).getStatutoryShortTermDisability().getStatutoryShortTermDisabilityCoverageEvaluations().get(0);
            softly.assertThat(coverageEvaluation_STAT.getEligibilityVerified()).isEqualTo("YES");
            softly.assertThat(coverageEvaluation_STAT.getApprovedThroughDt()).isEqualTo(currDatePlus10Days.format(DateTimeUtilsHelper.YYYY_MM_DD));

            softly.assertThat(response.getModel().getFeatures().get(0).getBenefitPeriod()).isEqualTo(benefitPeriod);
            softly.assertThat(response.getModel().getFeatures().get(0).getEliminationPeriod()).isEqualTo(eliminationPeriod);
        });
    }
}