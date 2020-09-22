/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_dn;

import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.claim.IClaim;
import com.exigen.ren.main.modules.claim.common.actions.*;
import com.exigen.ren.main.modules.claim.gb_dn.actions.*;

public class DentalClaim implements IClaim {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/claim/gb_dn");

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(IndBenefitsDentalCreateAction.class).getWorkspace();
    }

    @Override
    public Action claimInquiry() {
        return AutomationContext.getAction(IndBenefitsDentalClaimInquiryAction.class);
    }

    @Override
    public Action claimNotification() {
        return AutomationContext.getAction(IndBenefitsDentalClaimNotificationAction.class);
    }

    @Override
    public Action claimUpdate() {
        return AutomationContext.getAction(IndBenefitsDentalClaimUpdateAction.class);
    }

    public Action claimAdjust() {
        return AutomationContext.getAction(DentalClaimAdjustAction.class);
    }

    public Action claimCancel() {
        return AutomationContext.getAction(DentalClaimCancelAction.class);
    }

    public DentalPendClaimAction pendClaim()  {
        return AutomationContext.getAction(DentalPendClaimAction.class);
    }

    public GenerateOnDemandDocumentAction generateDocument() {
        return AutomationContext.getAction(GenerateOnDemandDocumentAction.class);
    }

    @Override
    public IssuePaymentAction issuePayment() {
        return AutomationContext.getAction(DentalClaimIssuePaymentAction.class);
    }

    @Override
    public ClearPaymentAction clearPayment() {
        return AutomationContext.getAction(DentalClaimClearPaymentAction.class);
    }

    @Override
    public UpdatePaymentAction updatePayment() {
        return AutomationContext.getAction(DentalClaimUpdatePaymentAction.class);
    }

    @Override
    public DisapprovePaymentAction disapprovePayment() {
        return AutomationContext.getAction(DentalClaimDisapprovePaymentAction.class);
    }

    @Override
    public ApprovePaymentAction approvePayment() {
        return AutomationContext.getAction(DentalClaimApprovePaymentAction.class);
    }

    @Override
    public DeclinePaymentAction declinePayment() {
        return AutomationContext.getAction(DentalClaimDeclinePaymentAction.class);
    }

    @Override
    public StopPaymentAction stopPayment() {
        return AutomationContext.getAction(DentalClaimStopPaymentAction.class);
    }

    @Override
    public ConfirmStopPaymentAction confirmStopPayment() {
        return AutomationContext.getAction(DentalClaimConfirmStopPaymentAction.class);
    }

    @Override
    public PostRecoveryAction postRecovery() {
        return AutomationContext.getAction(DentalClaimPostRecoveryAction.class);
    }

    @Override
    public UpdateRecoveryAction updateRecovery() {
        return AutomationContext.getAction(DentalClaimUpdateRecoveryAction.class);
    }

    @Override
    public VoidRecoveryAction voidRecovery() {
        return AutomationContext.getAction(DentalClaimVoidRecoveryAction.class);
    }

    public LineOverrideAction lineOverride() {
        return AutomationContext.getAction(LineOverrideAction.class);
    }


    public PaymentInquiryAction paymentInquiry() {
        return AutomationContext.getAction(DentalPaymentInquiryAction.class);
    }

}
