/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ac;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.IClaim;
import com.exigen.ren.main.modules.claim.common.actions.AddBenefitAction;
import com.exigen.ren.main.modules.claim.common.actions.PostRecoveryAction;
import com.exigen.ren.main.modules.claim.common.actions.UpdatePaymentAction;
import com.exigen.ren.main.modules.claim.common.actions.UpdateRecoveryAction;
import com.exigen.ren.main.modules.claim.gb_ac.actions.*;
import com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.ren.main.pages.summary.claim.ClaimAdjudicationSingleBenefitCalculationPage.BenefitPeriod.BENEFIT_PERIOD_START_DATE;

public class AccidentHealthClaim implements IClaim {
    private TestData gbACTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/claim/gb_ac");

    public TestData getGbACTestData() {
        return gbACTestData;
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(GroupBenefitsAccHealthCreateAction.class).getWorkspace();
    }

    @Override
    public Action claimUpdate() {
        return AutomationContext.getAction(GroupBenefitsAccHealthUpdateClaimAction.class);
    }

    @Override
    public AddBenefitAction addBenefit() {
        return AutomationContext.getAction(GroupBenefitsAccHealthAddBenefitAction.class);
    }

    @Override
    public Action addPayment() {
        return AutomationContext.getAction(GroupBenefitsAccHealthAddPaymentAction.class);
    }

    @Override
    public Action inquiryBenefit() {
        return AutomationContext.getAction(GroupBenefitsAccHealthInquiryBenefitAction.class);
    }

    @Override
    public Action viewBenefit() {
        return AutomationContext.getAction(GroupBenefitsAccHealthViewBenefitAction.class);
    }

    @Override
    public UpdatePaymentAction updatePayment() {
        return AutomationContext.getAction(GroupBenefitsAccHealthUpdatePaymentAction.class);
    }

    @Override
    public Action claimNotification() {
        return AutomationContext.getAction(GroupBenefitsAccHealthClaimNotificationAction.class);
    }

    @Override
    public GroupBenefitsAccHealthUpdateBenefitAction updateBenefit() {
        return AutomationContext.getAction(GroupBenefitsAccHealthUpdateBenefitAction.class);
    }

    @Override
    public PostRecoveryAction postRecovery() {
        return AutomationContext.getAction(PostRecoveryAction.class);
    }

    @Override
    public UpdateRecoveryAction updateRecovery() {
        return AutomationContext.getAction(UpdateRecoveryAction.class);
    }

    public void initiatePaymentAndFillUpToTab(TestData testData, Class<? extends Tab> tabClass, boolean isFillTab) {
        addPayment().start();
        addPayment().getWorkspace().fillUpTo(testData, tabClass, isFillTab);
    }

    public LocalDateTime getBenefitPeriodStartDate(int singleBenefitRowNumber) {
        viewSingleBenefitCalculation().perform(singleBenefitRowNumber);
        return LocalDate.parse(ClaimAdjudicationSingleBenefitCalculationPage.tableBenefitPeriod.getRow(1)
                .getCell(BENEFIT_PERIOD_START_DATE.getName()).getValue(), MM_DD_YYYY).atStartOfDay();
    }
}
