package com.exigen.ren.modules.dxp.api.claim;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_tl.TermLifeClaimContext;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsTermLifeEmployerModels.ClaimsTermLifeEmployerClaimsModel;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsTermLifeEmployerModels.DamageModel;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpEmployerViewClaimDetailsLife extends RestBaseTest implements CaseProfileContext,
        TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext, TermLifeClaimContext {
    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38641", component = CUSTOMER_REST)
    public void testDxpEmployerViewClaimDetailsLife() {
        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());

        createDefaultTermLifeInsuranceMasterPolicy();
        createDefaultTermLifeInsuranceCertificatePolicy();
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumIC = CustomerSummaryPage.labelCustomerNumber.getValue();
        termLifeClaim.create(termLifeClaim.getDefaultTestData("DataGatherCertificate", "TestData_Without_Benefits"));
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        termLifeClaim.addBenefit().perform(termLifeClaim.getDefaultTestData("NewBenefit", "TestData_Death"));
        termLifeClaim.addBenefit().perform(termLifeClaim.getDefaultTestData("NewBenefit", "TestData_AcceleratedDeath"));
        termLifeClaim.addBenefit().perform(termLifeClaim.getDefaultTestData("NewBenefit", "TestData_PremiumWaiver_Other"));
        termLifeClaim.addBenefit().perform(termLifeClaim.getDefaultTestData("NewBenefit", "TestData_AccidentalDismemberment"));
        termLifeClaim.addBenefit().perform(termLifeClaim.getDefaultTestData("NewBenefit", "TestData_AccidentalDeath"));

        toSubTab(OVERVIEW);
        termLifeClaim.claimSubmit().perform(new SimpleDataProvider());

        LOGGER.info("Step 1");
        assertSoftly(softly -> {
            ResponseContainer<ClaimsTermLifeEmployerClaimsModel> response = dxpRestService.getEmployerClaimsTermLife(customerNumberAuthorize, claimNumber);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);

            softly.assertThat(response.getModel().getClaimNumber()).isEqualTo(claimNumber);
            softly.assertThat(response.getModel().getCustomerNumber()).isEqualTo(customerNumIC);
            softly.assertThat(response.getModel().getProductCd()).isEqualTo("CLAIM_TERM_LIFE");

            LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
            List<DamageModel> claimsDamages = response.getModel().getClaimsDamages();

            List<DamageModel> deathDamages = claimsDamages.stream().filter(damage -> damage.getDeath() != null).collect(Collectors.toList());
            softly.assertThat(deathDamages).hasSize(1);
            softly.assertThat(deathDamages.get(0).getDeath().getDeathIncident().getDateOfLastWorked()).isEqualTo(currentDate.format(DateTimeUtilsHelper.YYYY_MM_DD));


            List<DamageModel> acceleratedDeathDamages = claimsDamages.stream().filter(damage -> damage.getAcceleratedDeath() != null).collect(Collectors.toList());
            softly.assertThat(acceleratedDeathDamages).hasSize(1);
            softly.assertThat(acceleratedDeathDamages.get(0).getAcceleratedDeath().getAcceleratedDeathIncident().getDateOfLastWorked()).isEqualTo(currentDate.minusDays(1).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> premiumWaiverDamages = claimsDamages.stream().filter(damage -> damage.getPremiumWaiver() != null).collect(Collectors.toList());
            softly.assertThat(premiumWaiverDamages).hasSize(1);
            softly.assertThat(premiumWaiverDamages.get(0).getPremiumWaiver().getPremiumWaiverIncident().getDateOfLastWorked()).isEqualTo(currentDate.minusDays(2).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> accidentalDismemberment = claimsDamages.stream().filter(damage -> damage.getAccidentalDismemberment() != null).collect(Collectors.toList());
            softly.assertThat(accidentalDismemberment).hasSize(1);
            softly.assertThat(accidentalDismemberment.get(0).getAccidentalDismemberment().getAccidentalDismembermentIncident().getDateOfLastWorked()).isEqualTo(currentDate.minusDays(3).format(DateTimeUtilsHelper.YYYY_MM_DD));

            List<DamageModel> accidentalDeath = claimsDamages.stream().filter(damage -> damage.getAccidentalDeath() != null).collect(Collectors.toList());
            softly.assertThat(accidentalDeath).hasSize(1);
            softly.assertThat(accidentalDeath.get(0).getAccidentalDeath().getAccidentalDeathIncident().getDateOfLastWorked()).isEqualTo(currentDate.minusDays(4).format(DateTimeUtilsHelper.YYYY_MM_DD));
        });

        LOGGER.info("Step 2");
        assertSoftly(softly -> {
            ResponseContainer<ClaimsTermLifeEmployerClaimsModel> response = dxpRestService.getEmployerClaimsTermLife(customerNumIC, claimNumber);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(403);
            softly.assertThat(response.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
            softly.assertThat(response.getModel().getMessage()).isEqualTo("User has no access to claim");
        });
    }
}
