package com.exigen.ren.modules.dxp.api.claim;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.claim.gb_dn.DentalClaimContext;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.GroupDentalCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDentalMemberModels.ClaimsDamageModel;
import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDentalMemberModels.ClaimsDentalMemberClaimsModel;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.ipb.eisa.verification.CustomSoftAssertionsExtended.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CUSTOMER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.COVERED_CDT_CODE;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.DECISION;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.SubmittedServicesColumns.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpMemberClaimsDental extends RestBaseTest implements CaseProfileContext, GroupDentalMasterPolicyContext,
        GroupDentalCertificatePolicyContext, DentalClaimContext {
    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39678", component = CUSTOMER_REST)
    public void testDxpMemberClaimsDental() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumNIC = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());

        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData());
        groupDentalCertificatePolicy.createPolicyViaUI(getDefaultGroupDentalCertificatePolicyData());
        NavigationPage.toMainTab(CUSTOMER);
        String customerNumIC = CustomerSummaryPage.labelCustomerNumber.getValue();

        dentalClaim.create(tdSpecific().getTestData("TestData_TwoServices"));
        String claimNumber = getClaimNumber();
        dentalClaim.claimSubmit().perform();

        String providerName = tableProvider.getRow(1).getCell(IntakeInformationTab.ProviderColumns.PROVIDER_NAME.getName()).getValue();
        String practiceName = tableProvider.getRow(1).getCell(IntakeInformationTab.ProviderColumns.PRACTICE_NAME.getName()).getValue();

        Row row_D3410 = tableSubmittedServices.getRow(CDT_CODE.getName(), "D3410");
        String dos_D3410 = row_D3410.getCell(DOS.getName()).getValue();
        String tooth_D3410 = row_D3410.getCell(TOOTH.getName()).getValue();
        Currency charge_D3410 = new Currency(row_D3410.getCell(CHARGE.getName()).getValue());

        Row row_D0160 = tableSubmittedServices.getRow(CDT_CODE.getName(), "D0160");
        String dos_D0160 = row_D0160.getCell(DOS.getName()).getValue();
        String tooth_D0160 = row_D0160.getCell(TOOTH.getName()).getValue();
        Currency charge_D0160 = new Currency(row_D0160.getCell(CHARGE.getName()).getValue());

        String decision_D3410 = tableResultsOfAdjudication.getRow(COVERED_CDT_CODE.getName(), "D3410").getCell(DECISION.getName()).getValue();
        String decision_D0160 = tableResultsOfAdjudication.getRow(COVERED_CDT_CODE.getName(), "D0160").getCell(DECISION.getName()).getValue();

        LOGGER.info("Step 1");
        assertSoftly(softly -> {
            ResponseContainer<ClaimsDentalMemberClaimsModel> response = dxpRestService.getMemberClaimsDental(customerNumIC, claimNumber);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(response.getModel().getClaimsServiceRequest().getClaimsDentalVendorViews().get(0).getProviderName()).isEqualTo(providerName);
            softly.assertThat(response.getModel().getClaimsServiceRequest().getClaimsDentalVendorViews().get(0).getPracticeName()).isEqualTo(practiceName);

            ClaimsDamageModel claimDamage_D3410 = response.getModel().getClaimsDamages().stream().filter(elem -> elem.getLossDescription().equals("D3410")).findFirst()
                    .orElseThrow(() -> new IstfException((String.format("Claim damage with lossDescription=%s is absent", "D3410"))));
            softly.assertThat(claimDamage_D3410.getClaimsProcedure().getProcedureDate()).isEqualTo(LocalDate.parse(dos_D3410, DateTimeUtils.MM_DD_YYYY).format(DateTimeUtilsHelper.YYYY_MM_DD));
            softly.assertThat(claimDamage_D3410.getClaimsProcedure().getToothLetter()).isEqualTo(tooth_D3410);
            softly.assertThat(new Currency(claimDamage_D3410.getClaimsProcedure().getReportedFee())).isEqualTo(charge_D3410);
            softly.assertThat(claimDamage_D3410.getDentalEvaluationFeatures().get(0).getConsideredProcedure()).isEqualTo("D3410");
            softly.assertThat(claimDamage_D3410.getDentalEvaluationFeatures().get(0).getStatusReason()).isEqualTo(decision_D3410);

            ClaimsDamageModel claimDamage_D0160 = response.getModel().getClaimsDamages().stream().filter(elem -> elem.getLossDescription().equals("D0160")).findFirst()
                    .orElseThrow(() -> new IstfException((String.format("Claim damage with lossDescription=%s is absent", "D0160"))));
            softly.assertThat(claimDamage_D0160.getClaimsProcedure().getProcedureDate()).isEqualTo(LocalDate.parse(dos_D0160, DateTimeUtils.MM_DD_YYYY).format(DateTimeUtilsHelper.YYYY_MM_DD));
            softly.assertThat(claimDamage_D0160.getClaimsProcedure().getToothLetter()).isEqualTo(tooth_D0160);
            softly.assertThat(new Currency(claimDamage_D0160.getClaimsProcedure().getReportedFee())).isEqualTo(charge_D0160);
            softly.assertThat(claimDamage_D0160.getDentalEvaluationFeatures().get(0).getConsideredProcedure()).isEqualTo("D0160");
            softly.assertThat(claimDamage_D0160.getDentalEvaluationFeatures().get(0).getStatusReason()).isEqualTo(decision_D0160);
        });

        LOGGER.info("Step 2");
        assertSoftly(softly -> {
            ResponseContainer<ClaimsDentalMemberClaimsModel> response = dxpRestService.getMemberClaimsDental(customerNumNIC, claimNumber);
            softly.assertThat(response.getResponse().getStatus()).isEqualTo(403);
            softly.assertThat(response.getModel().getErrorCode()).isEqualTo("ERROR_SERVICE_AUTHORIZATION");
            softly.assertThat(response.getModel().getMessage()).isEqualTo("Requested user has no access to this claim");
        });
    }
}
