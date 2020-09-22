package com.exigen.ren.modules.dxp.api.claim;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.common.tabs.SingleBenefitCalculationActionTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimSTDContext;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageEvaluationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.ShortTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels.ClaimsDisabilityEmployerClaimsModel;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels.ShortTermDisabilityCoverageEvaluationModel;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.main.modules.claim.common.tabs.SingleBenefitCalculationActionTab.EliminationQualificationPeriod.ELIMINATION_PERIOD;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageEvaluationTabMetaData.APPROVED_THROUGH_DATE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageEvaluationTabMetaData.ELIGIBILITY_VERIFIED;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpEmployerViewClaimDetailsSTD extends RestBaseTest implements CaseProfileContext,
        ShortTermDisabilityMasterPolicyContext, ShortTermDisabilityCertificatePolicyContext, DisabilityClaimSTDContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38343", component = CUSTOMER_REST)
    public void testDxpEmployerViewClaimDetails_STD() {
        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(shortTermDisabilityMasterPolicy.getType());

        createDefaultShortTermDisabilityMasterPolicy();
        createDefaultShortTermDisabilityCertificatePolicy();
        NavigationPage.toMainTab(CUSTOMER);
        String customerNameForCP = CustomerSummaryPage.labelCustomerName.getValue();
        String firstNameIC = customerNameForCP.split(" ")[0];

        createDefaultSTDClaimForCertificatePolicyWithoutBenefits();

        disabilityClaim.claimOpen().perform();
        LocalDate dateOfLoss = LocalDate.parse(ClaimSummaryPage.tableLossEvent.getRow(1).getCell("Reported Date of Loss").controls.links.getFirst().getValue(), DateTimeUtils.MM_DD_YYYY);

        LocalDateTime currDatePlus10Days = TimeSetterUtil.getInstance().getCurrentTime().plusYears(10);
        disabilityClaim.addBenefit().perform(disabilityClaim.getSTDTestData().getTestData("NewBenefit", "TestData_STD_OtherValues")
                .adjust(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), APPROVED_THROUGH_DATE.getLabel()), currDatePlus10Days.format(DateTimeUtils.MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), ELIGIBILITY_VERIFIED.getLabel()), "Yes")
                .mask(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), BenefitCoverageEvaluationTabMetaData.INSURED_PERSON_COVERAGE_EFFECTIVE_DATE.getLabel()))
        );

        String claimNumber = ClaimSummaryPage.getClaimNumber();

        disabilityClaim.calculateSingleBenefitAmount().perform(disabilityClaim.getSTDTestData().getTestData("CalculateASingleBenefitAmount", "TestData_STD"), 1);
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

            softly.assertThat(response.getModel().getClaimsPolicy().getPrecClaimsPolicyRiskParticipant().getPrecClaimsPolicyRiskParticipantParty().getFirstName()).isEqualTo(firstNameIC);

            ShortTermDisabilityCoverageEvaluationModel coverageEvaluationModel_STD = response.getModel().getClaimsDamages().get(0).getShortTermDisability().getShortTermDisabilityCoverageEvaluations().get(0);
            softly.assertThat(coverageEvaluationModel_STD.getEligibilityVerified()).isEqualTo("YES");
            softly.assertThat(coverageEvaluationModel_STD.getApprovedThroughDt()).isEqualTo(currDatePlus10Days.format(DateTimeUtilsHelper.YYYY_MM_DD));

            softly.assertThat(response.getModel().getFeatures().get(0).getBenefitPeriod()).isEqualTo(benefitPeriod);
            softly.assertThat(response.getModel().getFeatures().get(0).getEliminationPeriod()).isEqualTo(eliminationPeriod);
        });
    }
}