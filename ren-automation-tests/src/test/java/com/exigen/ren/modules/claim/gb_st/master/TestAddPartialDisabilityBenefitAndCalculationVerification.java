package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ValueConstants.EMPTY;
import static com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData.BENEFIT;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantCoverageTabMetaData.BENEFIT_PERCENTAGE;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.COVERED_EARNINGS;
import static com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab.buttonPostPayment;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsStatutorySTDInjuryPartyInformationTabMetaData.WORK_STATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAddPartialDisabilityBenefitAndCalculationVerification extends ClaimGroupBenefitsSTBaseTest {

    private static final String IN_LIEU_BENEFIT_VALUE = "Partial Disability Benefit";
    private static final String WARING_MESSAGE = String.format("%s is less than or equal to 14 calendar days after date of loss. Partial Disability Benefit selection is not allowed", PAYMENT_FROM_DATE.getLabel());

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-46626", component = CLAIMS_GROUPBENEFITS)
    public void testAutoGeneratePaymentCheckNumber() {
        LOGGER.info("General preconditions preparing");
        LocalDateTime currentDate = DateTimeUtils.getCurrentDateTime();

        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(statutoryDisabilityInsuranceMasterPolicy.getType());
        statutoryDisabilityInsuranceMasterPolicy.createPolicy(
                statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(DATA_GATHER, "TestData_NJ")
                        .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.PROPOSE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                        .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ACCEPT_CONTRACT, TestDataKey.DEFAULT_TEST_DATA_KEY))
                        .adjust(statutoryDisabilityInsuranceMasterPolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY)));

        LOGGER.info("Step#2 verification");
        disabilityClaim.create(disabilityClaim.getSTTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY)
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), COVERED_EARNINGS.getLabel()), "1000")
                .adjust(makeKeyPath(policyInformationParticipantParticipantInformationTab.getMetaKey(), WORK_STATE.getLabel()), "NJ")
                .adjust(makeKeyPath(policyInformationParticipantParticipantCoverageTab.getMetaKey(), BENEFIT_PERCENTAGE.getLabel()), "60")
                .adjust(makeKeyPath(additionalPartiesAdditionalPartyTab.getMetaKey(), BENEFIT.getLabel()), "index=1"));

        claim.claimOpen().perform();
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_ST"), 1);

        PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), currentDate.plusDays(15).format(MM_DD_YYYY)), paymentPaymentPaymentAllocationTab.getClass(), true);
        assertSoftly(softly -> {

            softly.assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT)).containsOption(IN_LIEU_BENEFIT_VALUE);

            LOGGER.info("Steps#3.1, 3.2 verification");
            paymentPaymentPaymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT).setValue(IN_LIEU_BENEFIT_VALUE);
            paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(currentDate.plusDays(13).format(MM_DD_YYYY));
            softly.assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE)).hasWarningWithText(WARING_MESSAGE);

            LOGGER.info("Steps#3.3 verification");
            paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(currentDate.plusDays(15).format(MM_DD_YYYY));
            softly.assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE)).hasNoWarning();

            LOGGER.info("Step#3.4 verification");
            paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(currentDate.plusDays(14).format(MM_DD_YYYY));
            softly.assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE)).hasWarningWithText(WARING_MESSAGE);

            LOGGER.info("Step#3.4 verification");
            paymentPaymentPaymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT).setValue(EMPTY);
            softly.assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE)).hasNoWarning();

            LOGGER.info("Steps#4.1-4.3 verification");
            paymentPaymentPaymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT).setValue(IN_LIEU_BENEFIT_VALUE);
            paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(currentDate.plusDays(15).format(MM_DD_YYYY));
            paymentPaymentPaymentAllocationTab.getAssetList().getAsset(CURRENT_EARNINGS).setValue(new Currency().toPlainString());
            softly.assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(CURRENT_EARNINGS))
                    .hasWarningWithText(String.format("%s must be bigger than 0.", CURRENT_EARNINGS.getLabel()));

            LOGGER.info("Step#4.4 verification");
            paymentPaymentPaymentAllocationTab.getAssetList().getAsset(CURRENT_EARNINGS).setValue("100");
            softly.assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(CURRENT_EARNINGS)).hasNoWarning();
        });

        LOGGER.info("Step#5.1 verification");
        buttonPostPayment.click();
        Page.dialogConfirmation.confirm();

        claim.addPayment().start();
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment")
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_FROM_DATE.getLabel()), currentDate.format(MM_DD_YYYY))
                .adjust(makeKeyPath(paymentPaymentPaymentAllocationTab.getMetaKey(), PAYMENT_THROUGH_DATE.getLabel()), currentDate.plusDays(6).format(MM_DD_YYYY)), paymentPaymentPaymentAllocationTab.getClass(), true);
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(IN_LIEU_BENEFIT).setValue(IN_LIEU_BENEFIT_VALUE);
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(CURRENT_EARNINGS).setValue("500");
        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency(100).toString());

        LOGGER.info("Step#5.2 verification");
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(CURRENT_EARNINGS).setValue("100");
        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(ALLOCATION_AMOUNT)).hasValue(new Currency(600).toString());
    }
}
