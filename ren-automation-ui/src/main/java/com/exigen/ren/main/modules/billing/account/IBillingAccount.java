/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.billing.account;

import com.exigen.istf.data.TestData;
import com.exigen.ren.common.EntityService;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.billing.account.actions.*;

public interface IBillingAccount extends EntityService {
    void create(TestData td);

    Workspace getDefaultWorkspace();

    GenerateFutureStatementAction generateFutureStatement();

    AcceptPaymentAction acceptPayment();

    TransferPaymentAction transferPayment();

    TransferPaymentBenefitsAction transferPaymentBenefits();

    OtherTransactionsAction otherTransactions();

    DiscardBillAction discardBill();

    RegenerateBillAction regenerateBill();

    GenerateDraftBillAction generateDraftBill();

    GenerateConsolidatedStatementAction generateConsolidatedStatement();

    DeclinePaymentAction declinePayment();

    MovePoliciesAction movePolicies();

    WaiveFeeAction waiveFee();

    RefundAction refund();

    UpdateAction update();

    UpdateBillingAccountAction updateBillingAccount();

    AddHoldAction addHold();

    RemoveHoldAction removeHold();

    ViewModalPremiumAction viewModalPremium();

    UnallocatePaymentAction unallocatePayment();

    ReverseWriteOffAction allocationReverse();

}
