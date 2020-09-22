package com.exigen.ren.modules.dxp.api.claim;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_ac.AccidentHealthClaimACContext;
import com.exigen.ren.main.modules.claim.gb_ac.tabs.BenefitPremiumWaiverIncidentTab;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsAccidentEmployerModels.ClaimsAccidentEmployerClaims;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsAccidentEmployerModels.DamageModel;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverIncidentTabMetaData.PREMIUM_WAIVER_INCIDENT;
import static com.exigen.ren.main.modules.claim.gb_ac.metadata.BenefitPremiumWaiverIncidentTabMetaData.PremiumWaiverIncidentMetaData.RTW_DATE;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpEmployerViewClaimDetailsAccident extends RestBaseTest implements CaseProfileContext, GroupAccidentMasterPolicyContext, GroupAccidentCertificatePolicyContext, AccidentHealthClaimACContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38329", component = CUSTOMER_REST)
    public void testDxpEmployerViewClaimDetailsAccident() {
        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentCertificatePolicy();
        accHealthClaim.create(accHealthClaim.getGbACTestData().getTestData(DATA_GATHER_CERTIFICATE, "TestData_Without_Benefits"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();

        accHealthClaim.addBenefit().perform(accHealthClaim.getGbACTestData().getTestData("NewBenefit", "TestData_AccidentalDeath"));
        accHealthClaim.addBenefit().perform(accHealthClaim.getGbACTestData().getTestData("NewBenefit", "TestData_PremiumWaiver"));
        accHealthClaim.addBenefit().perform(accHealthClaim.getGbACTestData().getTestData("NewBenefit", "TestData_AccidentalDismemberment"));
        accHealthClaim.addBenefit().perform(accHealthClaim.getGbACTestData().getTestData("NewBenefit", "TestData_CriticalIllness"));
        accHealthClaim.addBenefit().perform(accHealthClaim.getGbACTestData().getTestData("NewBenefit", "TestData_DiagnosisAndTreatment"));

        accHealthClaim.updateBenefit().perform(accHealthClaim.getGbACTestData().getTestData("UpdateBenefit", "Update_AccidentalDeath"), 1);
        accHealthClaim.updateBenefit().perform(accHealthClaim.getGbACTestData().getTestData("UpdateBenefit", "Update_PremiumWaiver")
                        .adjust(TestData.makeKeyPath(
                                BenefitPremiumWaiverIncidentTab.class.getSimpleName(),
                                PREMIUM_WAIVER_INCIDENT.getLabel(),
                                RTW_DATE.getLabel()),
                                currentDate.format(DateTimeUtils.MM_DD_YYYY)),
                2);
        accHealthClaim.updateBenefit().perform(accHealthClaim.getGbACTestData().getTestData("UpdateBenefit", "Update_AccidentalDismemberment"), 3);
        accHealthClaim.updateBenefit().perform(accHealthClaim.getGbACTestData().getTestData("UpdateBenefit", "Update_CriticalIllness"), 4);
        accHealthClaim.updateBenefit().perform(accHealthClaim.getGbACTestData().getTestData("UpdateBenefit", "Update_DiagnosisAndTreatment"), 5);

        LOGGER.info("Step 1");
        assertSoftly(softly -> {
            ResponseContainer<ClaimsAccidentEmployerClaims> response = dxpRestService.getEmployerClaimsAccident(customerNumberAuthorize, claimNumber);

            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            List<DamageModel> claimsDamages = response.getModel().getClaimsDamages();
            softly.assertThat(claimsDamages).hasSize(5);

            List<DamageModel> accidentDeathDamages = claimsDamages.stream().filter(damage -> damage.getAccidentalDeath() != null).collect(Collectors.toList());
            softly.assertThat(accidentDeathDamages).hasSize(1);
            softly.assertThat(accidentDeathDamages.get(0).getAccidentalDeath().getAccidentalDeathIncident().getDateOfLastWorked()).isEqualTo(currentDate.format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> premiumWaiverDamages = claimsDamages.stream().filter(damage -> damage.getPremiumWaiver() != null).collect(Collectors.toList());
            softly.assertThat(premiumWaiverDamages).hasSize(1);
            softly.assertThat(premiumWaiverDamages.get(0).getPremiumWaiver().getPremiumWaiverIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(1).format(DateTimeUtilsHelper.YYYY_MM_DD));
            softly.assertThat(premiumWaiverDamages.get(0).getPremiumWaiver().getPremiumWaiverIncident().getRtwDate()).isEqualTo(currentDate.format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> accidentalDismembermentDamages = claimsDamages.stream().filter(damage -> damage.getAccidentalDismemberment() != null).collect(Collectors.toList());
            softly.assertThat(accidentalDismembermentDamages).hasSize(1);
            softly.assertThat(accidentalDismembermentDamages.get(0).getAccidentalDismemberment().getAccidentalDismembermentIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(2).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> criticalIllnessDamages = claimsDamages.stream().filter(damage -> damage.getCriticalIllness() != null).collect(Collectors.toList());
            softly.assertThat(criticalIllnessDamages).hasSize(1);
            softly.assertThat(criticalIllnessDamages.get(0).getCriticalIllness().getCriticalIllnessIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(3).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> diagnosisAndTreatmentDamages = claimsDamages.stream().filter(damage -> damage.getAccidentalInjury() != null).collect(Collectors.toList());
            softly.assertThat(diagnosisAndTreatmentDamages).hasSize(1);
            softly.assertThat(diagnosisAndTreatmentDamages.get(0).getAccidentalInjury().getAccidentalInjuryIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(4).format(DateTimeUtilsHelper.YYYY_MM_DD));
        });

        LOGGER.info("Step 2");
        accHealthClaim.updateBenefit().perform(tdSpecific().getTestData("Update_AccidentalDeath"), 1);
        accHealthClaim.updateBenefit().perform(tdSpecific().getTestData("Update_PremiumWaiver"), 2);
        accHealthClaim.updateBenefit().perform(tdSpecific().getTestData("Update_AccidentalDismemberment"), 3);
        accHealthClaim.updateBenefit().perform(tdSpecific().getTestData("Update_CriticalIllness"), 4);
        accHealthClaim.updateBenefit().perform(tdSpecific().getTestData("Update_DiagnosisAndTreatment"), 5);

        LOGGER.info("Step 3");
        assertSoftly(softly -> {
            ResponseContainer<ClaimsAccidentEmployerClaims> response = dxpRestService.getEmployerClaimsAccident(customerNumberAuthorize, claimNumber);

            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            List<DamageModel> claimsDamages = response.getModel().getClaimsDamages();
            softly.assertThat(claimsDamages).hasSize(5);

            List<DamageModel> accidentDeathDamages = claimsDamages.stream().filter(damage -> damage.getAccidentalDeath() != null).collect(Collectors.toList());
            softly.assertThat(accidentDeathDamages).hasSize(1);
            softly.assertThat(accidentDeathDamages.get(0).getAccidentalDeath().getAccidentalDeathIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(10).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> premiumWaiverDamages = claimsDamages.stream().filter(damage -> damage.getPremiumWaiver() != null).collect(Collectors.toList());
            softly.assertThat(premiumWaiverDamages).hasSize(1);
            softly.assertThat(premiumWaiverDamages.get(0).getPremiumWaiver().getPremiumWaiverIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(11).format(DateTimeUtilsHelper.YYYY_MM_DD));
            softly.assertThat(premiumWaiverDamages.get(0).getPremiumWaiver().getPremiumWaiverIncident().getRtwDate()).isEqualTo(currentDate.plusDays(11).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> accidentalDismembermentDamages = claimsDamages.stream().filter(damage -> damage.getAccidentalDismemberment() != null).collect(Collectors.toList());
            softly.assertThat(accidentalDismembermentDamages).hasSize(1);
            softly.assertThat(accidentalDismembermentDamages.get(0).getAccidentalDismemberment().getAccidentalDismembermentIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(12).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> criticalIllnessDamages = claimsDamages.stream().filter(damage -> damage.getCriticalIllness() != null).collect(Collectors.toList());
            softly.assertThat(criticalIllnessDamages).hasSize(1);
            softly.assertThat(criticalIllnessDamages.get(0).getCriticalIllness().getCriticalIllnessIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(13).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> diagnosisAndTreatmentDamages = claimsDamages.stream().filter(damage -> damage.getAccidentalInjury() != null).collect(Collectors.toList());
            softly.assertThat(diagnosisAndTreatmentDamages).hasSize(1);
            softly.assertThat(diagnosisAndTreatmentDamages.get(0).getAccidentalInjury().getAccidentalInjuryIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(14).format(DateTimeUtilsHelper.YYYY_MM_DD));
        });
    }
}