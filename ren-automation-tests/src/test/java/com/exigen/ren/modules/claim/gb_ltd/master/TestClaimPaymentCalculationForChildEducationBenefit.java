package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentCalculatorTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesCalculatorActionTab;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentSeriesPaymentAllocationActionTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitLTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.LONG_TERM_DISABILITY;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSummaryOfPaymentsAndRecoveriesTable.TRANSACTION_STATUS;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.APPROVED;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentCalculatorActionTabMetaData.PaymentAdditionMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.BENEFIT;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitLTDInjuryPartyInformationTabMetaData.ASSOCIATED_SCHEDULED_ITEM;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OPTIONS;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.BENEFIT_AMOUNT_FOR_ALL_CHILDREN;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.metadata.PlanDefinitionTabMetaData.OptionsMetaData.CHILD_EDUCATION_BENEFIT;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPaymentCalculationForChildEducationBenefit extends ClaimGroupBenefitsLTDBaseTest {

    private static final String CHILD_EDUCATION_BENEFIT_VALUE = "Child Education Benefit";
    private static final String BENEFIT_AMOUNT_PER_CHILD_VALUE = "200";
    private static final String BENEFIT_AMOUNT_FOR_ALL_CHILDREN_VALUE = "600";

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23488", component = CLAIMS_GROUPBENEFITS)
    public void testCWMPPaymentCalculationForChildEducationBenefitNotIncluded() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(),
                        CHILD_EDUCATION_BENEFIT.getLabel()), "Not Included"));
        claim.create(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits"));

        LOGGER.info("Step 1");
        claim.claimOpen().perform();
        claim.addBenefit().start(LONG_TERM_DISABILITY);
        BenefitLTDInjuryPartyInformationTab benefitLTDInjuryPartyInformationTab = claim.addBenefit().getWorkspace().getTab(BenefitLTDInjuryPartyInformationTab.class);
        assertThat(benefitLTDInjuryPartyInformationTab.getAssetList().getAsset(ASSOCIATED_SCHEDULED_ITEM)).doesNotContainOption(CHILD_EDUCATION_BENEFIT_VALUE);

        disabilityClaim.addBenefit().getWorkspace().fill(tdClaim.getTestData("NewBenefit", "TestData_LTD_OtherValues"));
        Tab.buttonSaveAndExit.click();
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.updateMaximumBenefitPeriodAction()
                .perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_UpdateMaximumBenefitPeriod"), 1);

        LOGGER.info("Step 2");
        claim.addPayment().start();
        PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), paymentPaymentPaymentAllocationTab.getClass());
        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(BENEFIT)).doesNotContainOption(CHILD_EDUCATION_BENEFIT_VALUE);

        claim.addPayment().getWorkspace().fillFromTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), paymentPaymentPaymentAllocationTab.getClass(), paymentCalculatorTab.getClass());

        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        assertThat(paymentCalculatorTab.getAssetList().getAsset(PaymentCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(ADDITION_TYPE)).doesNotContainOption(CHILD_EDUCATION_BENEFIT_VALUE);
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-23488", component = CLAIMS_GROUPBENEFITS)
    public void testCWMPPaymentCalculationForChildEducationBenefitIncluded() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(longTermDisabilityMasterPolicy.getType());
        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicyData()
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), CHILD_EDUCATION_BENEFIT.getLabel()), "Included")
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), PlanDefinitionTabMetaData.OptionsMetaData.BENEFIT_AMOUNT_PER_CHILD.getLabel()), BENEFIT_AMOUNT_PER_CHILD_VALUE)
                .adjust(TestData.makeKeyPath(planDefinitionTab.getMetaKey() + "[1]", OPTIONS.getLabel(), BENEFIT_AMOUNT_FOR_ALL_CHILDREN.getLabel()), BENEFIT_AMOUNT_FOR_ALL_CHILDREN_VALUE)
        );
        claim.create(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits_Without_AdditionalParties")
                .adjust(additionalPartiesAdditionalPartyTab.getMetaKey(), new SimpleDataProvider()));

        LOGGER.info("Step 3");
        claim.claimOpen().perform();
        claim.addBenefit().perform(tdClaim.getTestData("NewBenefit", "TestData_LTD"));
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_LTD"), 1);
        claim.updateMaximumBenefitPeriodAction()
                .perform(tdClaim.getTestData("CalculateASingleBenefitAmount", "TestData_UpdateMaximumBenefitPeriod"), 1);
        claim.addPayment().start();
        PaymentCalculatorTab paymentCalculatorTab = claim.addPayment().getWorkspace().getTab(PaymentCalculatorTab.class);
        claim.addPayment().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment"), paymentCalculatorTab.getClass());

        LOGGER.info("Step 4");
        paymentCalculatorTab.getAssetList().getAsset(BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentCalculatorTab.getAssetList().getAsset(PaymentCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(ADDITION_TYPE).setValue(CHILD_EDUCATION_BENEFIT_VALUE);

        assertSoftly(softly -> {
            LOGGER.info("Steps 5-8");
            TextBox numberOfChildren = paymentCalculatorTab.getAssetList().getAsset(PaymentCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(NUMBER_OF_CHILDREN);
            softly.assertThat(numberOfChildren).isPresent().isRequired();
            numberOfChildren.setValue("2");
            softly.assertThat(numberOfChildren).hasNoWarning();

            LOGGER.info("Steps 9-13");
            TextBox beginningDate = paymentCalculatorTab.getAssetList().getAsset(PaymentCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(BEGINNING_DATE);
            softly.assertThat(beginningDate).isPresent().isRequired();
            beginningDate.setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(beginningDate).hasNoWarning();

            LOGGER.info("Steps 14");
            TextBox benefitAmountPerChild = paymentCalculatorTab.getAssetList().getAsset(BENEFIT_AMOUNT_PER_CHILD);
            softly.assertThat(benefitAmountPerChild).isDisabled().hasValue(new Currency(BENEFIT_AMOUNT_PER_CHILD_VALUE).toString());

            LOGGER.info("Steps 16-20");
            TextBox throughDate = paymentCalculatorTab.getAssetList().getAsset(PaymentCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(THROUGH_DATE);
            softly.assertThat(throughDate).isPresent().isRequired();
            throughDate.setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(throughDate).hasNoWarning();

            LOGGER.info("Steps 21");
            TextBox totalChildEducationBenefitAmount = paymentCalculatorTab.getAssetList().getAsset(TOTAL_CHILD_EDUCATION_BENEFIT_AMOUNT);
            softly.assertThat(totalChildEducationBenefitAmount).isPresent().isDisabled()
                    .hasValue(new Currency(BENEFIT_AMOUNT_PER_CHILD_VALUE).multiply(new Currency(numberOfChildren.getValue())).toString());
            softly.assertThat(new Currency(totalChildEducationBenefitAmount.getValue()).lessThan(new Currency(BENEFIT_AMOUNT_FOR_ALL_CHILDREN_VALUE))).isTrue();

            numberOfChildren.setValue("10");
            softly.assertThat(new Currency(totalChildEducationBenefitAmount.getValue()).equals(new Currency(BENEFIT_AMOUNT_FOR_ALL_CHILDREN_VALUE))).isTrue();

            LOGGER.info("Steps 22");
            softly.assertThat(paymentCalculatorTab.getAssetList().getAsset(PaymentCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(ADDITION_COMMENTS)).isPresent();
        });
        PaymentPaymentPaymentAllocationTab paymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);
        paymentPaymentAllocationTab.navigateToTab();
        claim.addPayment().submit();
        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)
                    .getCell(TRANSACTION_STATUS)).hasValue(APPROVED);
        });

        PaymentSeriesCalculatorActionTab paymentSeriesCalculatorActionTab =
                claim.createPaymentSeries().getWorkspace().getTab(PaymentSeriesCalculatorActionTab.class);
        claim.createPaymentSeries().start();
        claim.createPaymentSeries().getWorkspace().fillUpTo(tdClaim.getTestData("ClaimPayment", "TestData_CreatePaymentSeries"),
                PaymentSeriesPaymentAllocationActionTab.class);
        paymentSeriesCalculatorActionTab.navigateToTab();
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.BUTTON_ADD_PAYMENT_ADDITION).click();
        paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.ADDITION_TYPE).setValue(CHILD_EDUCATION_BENEFIT_VALUE);
        assertSoftly(softly -> {
            LOGGER.info("Steps 5-8");
            TextBox numberOfChildren = paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.PAYMENT_ADDITION).getAsset(PaymentAdditionMetaData.NUMBER_OF_CHILDREN);
            softly.assertThat(numberOfChildren).isPresent().isRequired();
            numberOfChildren.setValue("2");
            softly.assertThat(numberOfChildren).hasNoWarning();

            LOGGER.info("Steps 9-13");
            TextBox beginningDate = paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.BEGINNING_DATE);
            softly.assertThat(beginningDate).isPresent().isRequired();
            beginningDate.setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(beginningDate).hasNoWarning();

            LOGGER.info("Steps 14");
            TextBox benefitAmountPerChild = paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.BENEFIT_AMOUNT_PER_CHILD);
            softly.assertThat(benefitAmountPerChild).isDisabled().hasValue(new Currency(BENEFIT_AMOUNT_PER_CHILD_VALUE).toString());

            LOGGER.info("Steps 16-20");
            TextBox throughDate = paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.THROUGH_DATE);
            softly.assertThat(throughDate).isPresent().isRequired();
            throughDate.setValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
            softly.assertThat(throughDate).hasNoWarning();

            LOGGER.info("Steps 21");
            TextBox totalChildEducationBenefitAmount = paymentSeriesCalculatorActionTab.getAssetList().getAsset(PaymentSeriesCalculatorActionTabMetaData.TOTAL_CHILD_EDUCATION_BENEFIT_AMOUNT);
            softly.assertThat(totalChildEducationBenefitAmount).isPresent().isDisabled()
                    .hasValue(new Currency(BENEFIT_AMOUNT_PER_CHILD_VALUE).multiply(new Currency(numberOfChildren.getValue())).toString());
            softly.assertThat(new Currency(totalChildEducationBenefitAmount.getValue()).lessThan(new Currency(BENEFIT_AMOUNT_FOR_ALL_CHILDREN_VALUE))).isTrue();

            numberOfChildren.setValue("10");
            softly.assertThat(new Currency(totalChildEducationBenefitAmount.getValue()).equals(new Currency(BENEFIT_AMOUNT_FOR_ALL_CHILDREN_VALUE))).isTrue();

            LOGGER.info("Steps 22");
            softly.assertThat(paymentSeriesCalculatorActionTab.getAssetList().getAsset(PAYMENT_ADDITION).getAsset(PaymentSeriesCalculatorActionTabMetaData.PaymentAdditionMetaData.ADDITION_COMMENTS)).isPresent();
        });
    }
}
