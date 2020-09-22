package com.exigen.ren.modules.dxp.api.claim;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimLTDContext;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitCoverageEvaluationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDIncidentTabMetaData;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitCoverageEvaluationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitLTDIncidentTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDisabilityMemberModels.ClaimsDisabilityMemberClaimsModel;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpViewClaimDetailsLTD extends RestBaseTest implements CaseProfileContext, LongTermDisabilityMasterPolicyContext, LongTermDisabilityCertificatePolicyContext, DisabilityClaimLTDContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39182", component = CUSTOMER_REST)
    public void testDxpViewClaimDetailsLTD() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumNIC = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        createDefaultLongTermDisabilityMasterPolicy();

        createDefaultLongTermDisabilityCertificatePolicy();
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumIC = CustomerSummaryPage.labelCustomerNumber.getValue();

        createDefaultLTDClaimForCertificatePolicyWithoutBenefits();
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        LocalDateTime currentDate = TimeSetterUtil.getInstance().getCurrentTime();
        disabilityClaim.addBenefit().perform(disabilityClaim.getLTDTestData().getTestData("NewBenefit", "TestData_LTD")
                .adjust(TestData.makeKeyPath(BenefitLTDIncidentTab.class.getSimpleName(), BenefitLTDIncidentTabMetaData.DATE.getLabel()), currentDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))
                .adjust(TestData.makeKeyPath(BenefitLTDIncidentTab.class.getSimpleName(), BenefitLTDIncidentTabMetaData.TYPE.getLabel()), "Date Last Worked")
                .mask(TestData.makeKeyPath(BenefitCoverageEvaluationTab.class.getSimpleName(), BenefitCoverageEvaluationTabMetaData.INSURED_PERSON_COVERAGE_EFFECTIVE_DATE.getLabel()))
        );

        toSubTab(OVERVIEW);
        disabilityClaim.claimSubmit().perform(new SimpleDataProvider());

        LOGGER.info("Step 1");
        assertSoftly(softly -> {
            ResponseContainer<ClaimsDisabilityMemberClaimsModel> response = dxpRestService.getMemberClaimsDisability(customerNumIC, claimNumber);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);

            softly.assertThat(response.getModel().getClaimNumber()).isEqualTo(claimNumber);
            softly.assertThat(response.getModel().getCustomerNumber()).isEqualTo(customerNumIC);
            softly.assertThat(response.getModel().getProductCd()).isEqualTo("CLAIM_DISABILITY");
            softly.assertThat(response.getModel().getClaimsDamages().get(0).getLongTermDisability().getLongTermDisabilityIncident().getLongTermDisabilityDates().get(0).getDiDisabilityDate())
                    .isEqualTo(currentDate.format(DateTimeUtilsHelper.YYYY_MM_DD));
            softly.assertThat(response.getModel().getClaimsDamages().get(0).getLongTermDisability().getLongTermDisabilityIncident().getLongTermDisabilityDates().get(0).getDiDisabilityType()).isEqualTo("DLW");
        });

        LOGGER.info("Step 2");
        assertSoftly(softly -> {
            ResponseContainer<ClaimsDisabilityMemberClaimsModel> response = dxpRestService.getMemberClaimsDisability(customerNumNIC, claimNumber);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(403);
            softly.assertThat(response.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
            softly.assertThat(response.getModel().getMessage()).isEqualTo("Requested user has no access to this claim");
        });
    }

}
