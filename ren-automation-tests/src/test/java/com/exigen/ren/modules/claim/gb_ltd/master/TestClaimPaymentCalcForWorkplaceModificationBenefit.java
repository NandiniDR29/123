package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.CoveragesActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentDetailsTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.assertj.core.api.Condition;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimPaymentsTab.PAYMENT_ALLOCATION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimPaymentsTab.PAYMENT_DETAILS;
import static com.exigen.ren.common.pages.NavigationPage.toLeftMenuTab;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.claim.common.metadata.CoveragesActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.ASSOCIATED_INSURABLE_RISK_LABEL;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentDetailsTabMetaData.PAYMENT_TO;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPaymentCalcForWorkplaceModificationBenefit extends ClaimGroupBenefitsLTDBaseTest {

    private final static String WORKPLACE_MODIFICATION_BENEFIT = "Workplace Modification Benefit";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-25770", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalcForWorkplaceModificationBenefitIntegration() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());

        LOGGER.info("Step 1, 2");
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData().adjust(tdSpecific().getTestData("TestData_REN_25770").resolveLinks()));
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), BenefitsLTDInjuryPartyInformationTab.class, true);
        benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).setValueContains(WORKPLACE_MODIFICATION_BENEFIT);

        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ESTIMATED_COST_VALUE)).hasValue(new Currency(2000).toString());

        LOGGER.info("Step 3");
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(REPORTED_EXPENSE_AMOUNT)).isRequired().hasValue(EMPTY);

        LOGGER.info("Step 4");
        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY).
                adjust(tdSpecific().getTestData("TestData_Claim").resolveLinks()), benefitsLTDInjuryPartyInformationTab.getClass());

        disabilityClaim.claimOpen().perform();

        LOGGER.info("Step 6");
        disabilityClaim.updateBenefit().start(1);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(REPORTED_EXPENSE_AMOUNT)).isRequired().hasValue(new Currency(3000).toString());
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step 7");
        disabilityClaim.calculateSingleBenefitAmount().start(1);
        CoveragesActionTab coveragesActionTab = disabilityClaim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class);
        coveragesActionTab.getAssetList().getAsset(ASSOCIATED_INSURABLE_RISK).setValueContains(WORKPLACE_MODIFICATION_BENEFIT);
        coveragesActionTab.getAssetList().getAsset(COVERAGE).setValue("LTD Core - CON");
        coveragesActionTab.getAssetList().getAsset(INDEMNITY_RESERVE).setValue("3000");
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step 8");
        disabilityClaim.addPayment().start();
        disabilityClaim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_TO.getLabel()), "contains=John"), PaymentPaymentPaymentAllocationTab.class);

        assertSoftly(softly -> {
            PaymentPaymentPaymentAllocationTab paymentAllocationTab = disabilityClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
            softly.assertThat(paymentAllocationTab.getAssetList().getAsset(ASSOCIATED_INSURABLE_RISK_LABEL)).valueContains(WORKPLACE_MODIFICATION_BENEFIT);
            softly.assertThat(paymentAllocationTab.getAssetList().getAsset(PaymentPaymentPaymentAllocationTabMetaData.ALLOCATION_AMOUNT)).hasValue(new Currency(2000).toString());
        });

        disabilityClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).fillTab(tdSpecific().getTestData("TestData_PaymentDetails"));
        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();

        assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("The claimant is not eligible for Workplace Modification Benefit."))).isPresent();

        LOGGER.info("Step 9");
        toLeftMenuTab(PAYMENT_DETAILS);

        disabilityClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentDetailsTab.class).fillTab(tdSpecific().getTestData("TestData_PaymentDetails"));
        toLeftMenuTab(PAYMENT_ALLOCATION);
        PaymentPaymentPaymentAllocationTab.buttonPostPayment.click();
        Page.dialogConfirmation.confirm();

        assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-26249", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPaymentCalcForWorkplaceModificationBenefitNotIntegration() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData().adjust(tdSpecific().getTestData("TestData_REN_26249").resolveLinks()));

        LOGGER.info("Step 1, 2");
        initiateClaimWithPolicyAndFillToTab(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), BenefitsLTDInjuryPartyInformationTab.class, true);

        List<String> associatedScheduledItemValues = benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).getAllValues();

        assertThat(associatedScheduledItemValues).doNotHave(new Condition<>(str ->
                str.matches(WORKPLACE_MODIFICATION_BENEFIT + ".*"), WORKPLACE_MODIFICATION_BENEFIT + ".*"));

        benefitsLTDInjuryPartyInformationTab.submitTab();
        disabilityClaim.getDefaultWorkspace().fillFrom(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), benefitsLTDIncidentTab.getClass());
        disabilityClaim.claimOpen().perform();

        LOGGER.info("Step 6");
        disabilityClaim.updateBenefit().start(1);

        associatedScheduledItemValues = benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM).getAllValues();

        assertThat(associatedScheduledItemValues).doNotHave(new Condition<>(str ->
                str.matches(WORKPLACE_MODIFICATION_BENEFIT + ".*"), WORKPLACE_MODIFICATION_BENEFIT + ".*"));

        LOGGER.info("Step 7");
        Tab.buttonSaveAndExit.click();
        disabilityClaim.calculateSingleBenefitAmount().start(1);

        List<String> associatedInsurableRiskValues = disabilityClaim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class).getAssetList()
                .getAsset(ASSOCIATED_INSURABLE_RISK).getAllValues();

        assertThat(associatedInsurableRiskValues).doNotHave(new Condition<>(str ->
                str.matches(WORKPLACE_MODIFICATION_BENEFIT + ".*"), WORKPLACE_MODIFICATION_BENEFIT + ".*"));

        CoveragesActionTab coveragesActionTab = disabilityClaim.calculateSingleBenefitAmount().getWorkspace().getTab(CoveragesActionTab.class);
        coveragesActionTab.getAssetList().getAsset(ASSOCIATED_INSURABLE_RISK).setValueContains("John");
        coveragesActionTab.getAssetList().getAsset(COVERAGE).setValue("LTD Core - CON");
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step 8");
        disabilityClaim.addPayment().start();
        disabilityClaim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(TestData.makeKeyPath(PaymentPaymentPaymentDetailsTab.class.getSimpleName(), PAYMENT_TO.getLabel()), "contains=John"), PaymentPaymentPaymentAllocationTab.class);

        assertThat(disabilityClaim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class).getAssetList().getAsset(PaymentPaymentPaymentAllocationTabMetaData.ASSOCIATED_INSURABLE_RISK_LABEL).getValue())
                .doesNotContainPattern(WORKPLACE_MODIFICATION_BENEFIT + ".*");
    }
}