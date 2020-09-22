package com.exigen.ren.modules.dxp.api.claim;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_ac.AccidentHealthClaimACContext;
import com.exigen.ren.main.modules.policy.gb_ac.certificate.GroupAccidentCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsAccidentMemberModels.ClaimsAccidentMemberClaimsModel;
import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsAccidentMemberModels.ClaimsDamageModel;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpMemberViewClaimDetailsAccident extends RestBaseTest implements CaseProfileContext, GroupAccidentMasterPolicyContext, GroupAccidentCertificatePolicyContext, AccidentHealthClaimACContext {
    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38719", component = CUSTOMER_REST)
    public void testDxpMemberViewClaimDetailsAccident() {
        mainApp().open();
        createDefaultNICWithIndRelationshipDefaultRoles();
        String customerNumNIC = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        createDefaultGroupAccidentCertificatePolicy();
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumIC = CustomerSummaryPage.labelCustomerNumber.getValue();
        accHealthClaim.create(accHealthClaim.getGbACTestData().getTestData(DATA_GATHER_CERTIFICATE, "TestData_Without_Benefits"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        accHealthClaim.addBenefit().perform(accHealthClaim.getGbACTestData().getTestData("NewBenefit", "TestData_AccidentalDeath"));
        accHealthClaim.addBenefit().perform(accHealthClaim.getGbACTestData().getTestData("NewBenefit", "TestData_PremiumWaiver"));
        accHealthClaim.addBenefit().perform(accHealthClaim.getGbACTestData().getTestData("NewBenefit", "TestData_AccidentalDismemberment"));
        accHealthClaim.addBenefit().perform(accHealthClaim.getGbACTestData().getTestData("NewBenefit", "TestData_CriticalIllness"));
        accHealthClaim.addBenefit().perform(accHealthClaim.getGbACTestData().getTestData("NewBenefit", "TestData_DiagnosisAndTreatment"));

        accHealthClaim.updateBenefit().perform(accHealthClaim.getGbACTestData().getTestData("UpdateBenefit", "Update_AccidentalDeath"), 1);
        accHealthClaim.updateBenefit().perform(accHealthClaim.getGbACTestData().getTestData("UpdateBenefit", "Update_PremiumWaiver"), 2);
        accHealthClaim.updateBenefit().perform(accHealthClaim.getGbACTestData().getTestData("UpdateBenefit", "Update_AccidentalDismemberment"), 3);
        accHealthClaim.updateBenefit().perform(accHealthClaim.getGbACTestData().getTestData("UpdateBenefit", "Update_CriticalIllness"), 4);
        accHealthClaim.updateBenefit().perform(accHealthClaim.getGbACTestData().getTestData("UpdateBenefit", "Update_DiagnosisAndTreatment"), 5);

        toSubTab(OVERVIEW);
        accHealthClaim.claimSubmit().perform(new SimpleDataProvider());
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();

        assertSoftly(softly -> {
            ResponseContainer<ClaimsAccidentMemberClaimsModel> response = dxpRestService.getMemberClaimsAccident(customerNumIC, claimNumber);

            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            List<ClaimsDamageModel> claimsDamages = dxpRestService.getMemberClaimsAccident(customerNumIC, claimNumber).getModel().getClaimsDamages();
            softly.assertThat(claimsDamages).hasSize(5);

            List<ClaimsDamageModel> accidentDeathDamages = claimsDamages.stream().filter(damage -> damage.getAccidentalDeath() != null).collect(Collectors.toList());
            softly.assertThat(accidentDeathDamages).hasSize(1);
            softly.assertThat(accidentDeathDamages.get(0).getAccidentalDeath().getAccidentalDeathIncident().getDateOfLastWorked()).isEqualTo(currentDate.format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<ClaimsDamageModel> premiumWaiverDamages = claimsDamages.stream().filter(damage -> damage.getPremiumWaiver() != null).collect(Collectors.toList());
            softly.assertThat(premiumWaiverDamages).hasSize(1);
            softly.assertThat(premiumWaiverDamages.get(0).getPremiumWaiver().getPremiumWaiverIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(1).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<ClaimsDamageModel> accidentalDismembermentDamages = claimsDamages.stream().filter(damage -> damage.getAccidentalDismemberment() != null).collect(Collectors.toList());
            softly.assertThat(accidentalDismembermentDamages).hasSize(1);
            softly.assertThat(accidentalDismembermentDamages.get(0).getAccidentalDismemberment().getAccidentalDismembermentIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(2).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<ClaimsDamageModel> criticalIllnessDamages = claimsDamages.stream().filter(damage -> damage.getCriticalIllness() != null).collect(Collectors.toList());
            softly.assertThat(criticalIllnessDamages).hasSize(1);
            softly.assertThat(criticalIllnessDamages.get(0).getCriticalIllness().getCriticalIllnessIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(3).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<ClaimsDamageModel> diagnosisAndTreatmentDamages = claimsDamages.stream().filter(damage -> damage.getAccidentalInjury() != null).collect(Collectors.toList());
            softly.assertThat(diagnosisAndTreatmentDamages).hasSize(1);
            softly.assertThat(diagnosisAndTreatmentDamages.get(0).getAccidentalInjury().getAccidentalInjuryIncident().getDateOfLastWorked()).isEqualTo(currentDate.plusDays(4).format(DateTimeUtilsHelper.YYYY_MM_DD));
        });

        assertSoftly(softly -> {
            ResponseContainer<ClaimsAccidentMemberClaimsModel> response = dxpRestService.getMemberClaimsAccident(customerNumNIC, claimNumber);

            softly.assertThat(response.getResponse().getStatus()).isEqualTo(403);
            softly.assertThat(response.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
            softly.assertThat(response.getModel().getMessage()).isEqualTo("Requested user has no access to this claim");
        });
    }
}
