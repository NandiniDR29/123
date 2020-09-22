package com.exigen.ren.modules.claim.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.tabs.PaymentPaymentPaymentAllocationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsPFLBaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData.PARTICIPANT_WORK_DAYS;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimVerifyWorkedPayableDaysAndPayableDays extends ClaimGroupBenefitsPFLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20869", component = CLAIMS_GROUPBENEFITS)
    public void testClaimVerifyWorkedPayableDaysAndPayableDays() {
        TestData addPaymentTestData = tdClaim.getTestData("ClaimPayment", "TestData_IndemnityPayment");
        LocalDate paymentFromDate = LocalDate.parse(addPaymentTestData.getValue(PaymentPaymentPaymentAllocationTab.class.getSimpleName(),
                PAYMENT_FROM_DATE.getLabel()), DateTimeUtils.MM_DD_YYYY).with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_PFL);
        disabilityClaim.create(tdClaim.getTestData(TestDataKey.DATA_GATHER, "TestData_With_PFL_Benefit")
                .adjust(tdSpecific().getTestData(TestDataKey.DEFAULT_TEST_DATA_KEY).resolveLinks())
                .mask(TestData.makeKeyPath(benefitsPflParticipantInformationTab.getMetaKey(), PARTICIPANT_WORK_DAYS.getLabel())));
        disabilityClaim.claimOpen().perform();
        LOGGER.info("TEST: Step #1");
        claim.calculateSingleBenefitAmount().perform(tdClaim.getTestData("CalculateASingleBenefitAmount", TestDataKey.DEFAULT_TEST_DATA_KEY), 1);

        LOGGER.info("TEST: Step #2");
        PaymentPaymentPaymentAllocationTab paymentPaymentPaymentAllocationTab = claim.addPayment().getWorkspace().getTab(PaymentPaymentPaymentAllocationTab.class);

        disabilityClaim.addPayment().start();
        disabilityClaim.addPayment().getWorkspace().fillUpTo(addPaymentTestData, paymentPaymentPaymentAllocationTab.getClass());
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(RESERVE_TYPE).setValue("Indemnity");

        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYABLE_DAYS)).isOptional().isDisabled().hasValue(StringUtils.EMPTY);

        LOGGER.info("TEST: Step #3");
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(paymentFromDate.format(DateTimeUtils.MM_DD_YYYY));

        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(paymentFromDate.plusDays(6).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYABLE_DAYS)).isDisabled().isOptional().hasValue("7");

        LOGGER.info("TEST: Step #5");
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_FROM_DATE).setValue(paymentFromDate.format(DateTimeUtils.MM_DD_YYYY));
        paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYMENT_THROUGH_DATE).setValue(paymentFromDate.plusDays(13).format(DateTimeUtils.MM_DD_YYYY));
        assertThat(paymentPaymentPaymentAllocationTab.getAssetList().getAsset(PAYABLE_DAYS)).hasValue("14");
    }
}