package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.main.modules.claim.common.metadata.RecoveryRecoveredFromActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.RecoveryRecoveredFromActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.RecoveryRecoveryAllocationActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.RecoveryRecoveryDetailsActionTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.DisabilityClaimLTDContext;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.gb_dn.ClaimSubledgerBaseTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.OVERVIEW;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_NUMBER;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.common.metadata.RecoveryRecoveryAllocationActionTabMetaData.ALLOCATION_AMOUNT;
import static com.exigen.ren.main.modules.claim.common.metadata.RecoveryRecoveryAllocationActionTabMetaData.RECOVERY_TAX;
import static com.exigen.ren.main.modules.claim.common.metadata.RecoveryRecoveryAllocationActionTabMetaData.RecoveryTaxMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.RecoveryRecoveryDetailsActionTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.FICA_MATCH;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddFICATaxInformationToClaimRecovery extends ClaimSubledgerBaseTest implements LongTermDisabilityMasterPolicyContext, DisabilityClaimLTDContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-42783", component = CLAIMS_GROUPBENEFITS)
    public void testAddFICATaxInformationToClaimRecovery() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(makeKeyPath(LongTermDisabilityMasterPolicyContext.planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), FICA_MATCH.getLabel()), "Reimbursement"));

        LOGGER.info("TEST REN-42783: Step 1");
        disabilityClaim.create(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, "TestData_WithOneBenefit")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "3000"));
        disabilityClaim.claimOpen().perform();
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        claim.calculateSingleBenefitAmount().perform(disabilityClaim.getLTDTestData().getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);

        RecoveryRecoveryDetailsActionTab recoveryDetailsActionTab = disabilityClaim.postRecovery().getWorkspace().getTab(RecoveryRecoveryDetailsActionTab.class);
        RecoveryRecoveryAllocationActionTab recoveryAllocationActionTab = disabilityClaim.postRecovery().getWorkspace().getTab(RecoveryRecoveryAllocationActionTab.class);
        disabilityClaim.postRecovery().start();
        assertThat(recoveryDetailsActionTab.getAssetList().getAsset(OVERPAYMENT_TAX_RECORDING)).hasValue("No");

        LOGGER.info("TEST REN-42783: Step 2");
        RecoveryRecoveryDetailsActionTab.cancelClickAndCloseDialog();
        disabilityClaim.addPayment().perform(disabilityClaim.getLTDTestData().getTestData("ClaimPayment", "TestData_IndemnityPayment"));
        disabilityClaim.issuePayment().perform(disabilityClaim.getLTDTestData().getTestData("ClaimPayment", "TestData_IssuePayment"), 1);

        toSubTab(OVERVIEW);
        disabilityClaim.claimUpdate().perform(disabilityClaim.getLTDTestData().getTestData("TestClaimUpdate", "TestData_Update")
                .adjust(makeKeyPath(PolicyInformationParticipantParticipantInformationActionTab.class.getSimpleName(), BenefitLTDInjuryPartyInformationTabMetaData.COVERED_EARNINGS.getLabel()), "2000"));

        disabilityClaim.postRecovery().start();
        assertThat(recoveryDetailsActionTab.getAssetList().getAsset(TAXATION_YEAR)).hasValue(String.valueOf(DateTimeUtils.getCurrentDateTime().getYear()));

        LOGGER.info("TEST REN-42783: Step 3");
        disabilityClaim.postRecovery().getWorkspace().fillUpTo(disabilityClaim.getLTDTestData().getTestData("ClaimPayment", "TestData_PostRecovery")
                .adjust(makeKeyPath(recoveryDetailsActionTab.getMetaKey(), OVERPAYMENT_TAX_RECORDING.getLabel()), "Yes")
                .adjust(makeKeyPath(recoveryDetailsActionTab.getMetaKey(), AMOUNT.getLabel()), "600")
                .adjust(makeKeyPath(recoveryAllocationActionTab.getMetaKey(), ALLOCATION_AMOUNT.getLabel()), "600"), RecoveryRecoveryAllocationActionTab.class, true);

        RecoveryRecoveryAllocationActionTab.addRecoveryTax.click();
        assertSoftly(softly -> {
            softly.assertThat(recoveryAllocationActionTab.getAssetList().getAsset(RECOVERY_TAX).getAsset(TAX_TYPE)).isPresent().isRequired();
            softly.assertThat(recoveryAllocationActionTab.getAssetList().getAsset(RECOVERY_TAX).getAsset(TAX_COMMENTS)).isPresent().isOptional();
        });

        LOGGER.info("TEST REN-42783: Step 4");
        RecoveryRecoveryAllocationActionTab.buttonNext.click();
        assertThat(recoveryAllocationActionTab.getAssetList().getAsset(RECOVERY_TAX).getAsset(TAX_TYPE)).hasWarningWithText("'Tax Type' is required");

        recoveryAllocationActionTab.getAssetList().getAsset(RECOVERY_TAX).getAsset(TAX_TYPE).setValue("FICA Social Security Tax");
        recoveryAllocationActionTab.getAssetList().getAsset(RECOVERY_TAX).getAsset(FICA_SOCIAL_SECURITY_TAX_AMOUNT).setValue("10");
        RecoveryRecoveryAllocationActionTab.buttonNext.click();
        disabilityClaim.postRecovery().getWorkspace().getTab(RecoveryRecoveredFromActionTab.class).getAssetList().getAsset(RecoveryRecoveredFromActionTabMetaData.PARTY_NAME).setValueByIndex(1);

        LOGGER.info("TEST REN-42783: Step 5");
        recoveryAllocationActionTab.navigate();
        RecoveryRecoveryAllocationActionTab.addRecoveryTax.click();
        recoveryAllocationActionTab.getAssetList().getAsset(RECOVERY_TAX).getAsset(TAX_TYPE).setValue("FICA Medicare Tax");
        recoveryAllocationActionTab.getAssetList().getAsset(RECOVERY_TAX).getAsset(FICA_MEDICARE_TAX_AMOUNT).setValue("2");
        assertThat(RecoveryRecoveryAllocationActionTab.tableListOfRecoveryTax).isPresent();

        LOGGER.info("TEST REN-42783: Step 7");
        disabilityClaim.postRecovery().submit();
        String paymentNumber1 = tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(PAYMENT_NUMBER.getName()).getValue();

        List<Map<String, String>> reportsREST = parseResponse(claimCoreRestService.getFinanceSubledgerByClaimNumber(claimNumber));
        validatePayment("REC_POST_POST_TRX_D",
                "REC_POST",
                "recovery payment",
                "DEBIT",
                "2003",
                "Post recovery",
                paymentNumber1,
                reportsREST);
        validatePayment("REC_POST_POST_TRX_C",
                "REC_POST",
                "recovery payment",
                "CREDIT",
                "2027",
                "Post recovery",
                paymentNumber1,
                reportsREST);
        validatePayment("REC_POST_FICA_SS_OASDI_EE_TAX_TRX_D_RenClaimsRecoveryCalculatorTax",
                "REC_POST",
                "recovery payment",
                "DEBIT",
                "2019",
                "Post recovery tax",
                paymentNumber1,
                reportsREST);
        validatePayment("REC_POST_FICA_SS_OASDI_EE_TAX_TRX_C_RenClaimsRecoveryCalculatorTax",
                "REC_POST",
                "recovery payment",
                "CREDIT",
                "2027",
                "Post recovery tax",
                paymentNumber1,
                reportsREST);
        validatePayment("REC_POST_FICA_MEDICARE_EE_TAX_TRX_D_RenClaimsRecoveryCalculatorTax",
                "REC_POST",
                "recovery payment",
                "DEBIT",
                "2019",
                "Post recovery tax",
                paymentNumber1,
                reportsREST);
        validatePayment("REC_POST_FICA_MEDICARE_EE_TAX_TRX_C_RenClaimsRecoveryCalculatorTax",
                "REC_POST",
                "recovery payment",
                "CREDIT",
                "2027",
                "Post recovery tax",
                paymentNumber1,
                reportsREST);
    }
}
