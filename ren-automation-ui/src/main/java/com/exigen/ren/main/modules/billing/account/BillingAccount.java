/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.billing.account;

import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.actions.*;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.actions.SetupBillingGroupsAction;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;

public class BillingAccount implements IBillingAccount {
    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/billing/billingaccount");
    private TestData defaultAPITestData = TestDataProvider.getDefaultTestDataProvider().get("modules/billing/api");

    @Override
    public Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(CreateAction.class).getWorkspace();
    }


    public void navigateToBillingAccount() {
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING);
    }

    public void navigateToBillingAccountList() {
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING);
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING);
    }

    public Workspace getPaymentsAdjustmentsDefaultWorkspace() {
        return AutomationContext.getAction(ReverseWriteOffAction.class).getWorkspace();
    }

    public Workspace getOtherTransactionsWorkspace() {
        return AutomationContext.getAction(OtherTransactionsAction.class).getWorkspace();
    }

    @Override
    public void create(TestData td) {
        getDefaultWorkspace().fill(td);
    }

    @Override
    public GenerateFutureStatementAction generateFutureStatement() {
        return AutomationContext.getAction(GenerateFutureStatementAction.class);
    }

    @Override
    public AcceptPaymentAction acceptPayment() {
        return AutomationContext.getAction(AcceptPaymentAction.class);
    }

    @Override
    public UnallocatePaymentAction unallocatePayment() {
        return AutomationContext.getAction(UnallocatePaymentAction.class);
    }

    @Override
    public TransferPaymentAction transferPayment() {
        return AutomationContext.getAction(TransferPaymentAction.class);
    }

    @Override
    public TransferPaymentBenefitsAction transferPaymentBenefits() {
        return AutomationContext.getAction(TransferPaymentBenefitsAction.class);
    }

    @Override
    public OtherTransactionsAction otherTransactions() {
        return AutomationContext.getAction(OtherTransactionsAction.class);
    }

    @Override
    public DiscardBillAction discardBill() {
        return AutomationContext.getAction(DiscardBillAction.class);
    }

    @Override
    public RegenerateBillAction regenerateBill() {
        return AutomationContext.getAction(RegenerateBillAction.class);
    }

    @Override
    public GenerateDraftBillAction generateDraftBill() {
        return AutomationContext.getAction(GenerateDraftBillAction.class);
    }

    @Override
    public GenerateConsolidatedStatementAction generateConsolidatedStatement() {
        return AutomationContext.getAction(GenerateConsolidatedStatementAction.class);
    }

    @Override
    public DeclinePaymentAction declinePayment() {
        return AutomationContext.getAction(DeclinePaymentAction.class);
    }

    @Override
    public MovePoliciesAction movePolicies() {
        return AutomationContext.getAction(MovePoliciesAction.class);
    }

    @Override
    public WaiveFeeAction waiveFee() {
        return AutomationContext.getAction(WaiveFeeAction.class);
    }

    @Override
    public RefundAction refund() {
        return AutomationContext.getAction(RefundAction.class);
    }

    @Override
    public UpdateAction update() {
        return AutomationContext.getAction(UpdateAction.class);
    }

    @Override
    public UpdateBillingAccountAction updateBillingAccount() {
        return AutomationContext.getAction(UpdateBillingAccountAction.class);
    }

    @Override
    public AddHoldAction addHold() {
        return AutomationContext.getAction(AddHoldAction.class);
    }

    @Override
    public RemoveHoldAction removeHold() {
        return AutomationContext.getAction(RemoveHoldAction.class);
    }

    @Override
    public ViewModalPremiumAction viewModalPremium() {
        return AutomationContext.getAction(ViewModalPremiumAction.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }

    public void addSuspenseAmount(String amount) {
        acceptPayment().start();
        AbstractContainer<?, ?> acceptPaymentAL = acceptPayment().getWorkspace().getTab(AcceptPaymentActionTab.class).getAssetList();
        acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.PAYMENT_METHOD).setValue("Cash");
        acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.AMOUNT).setValue(amount);
        acceptPaymentAL.getAsset(AcceptPaymentActionTabMetaData.SUSPEND_REMAINING).setValue(VALUE_YES);
        AcceptPaymentActionTab.buttonOk.click();
    }

    @Override
    public ReverseWriteOffAction allocationReverse() {
        return AutomationContext.getAction(ReverseWriteOffAction.class);
    }

    public void paymentsAdjustmentsAction(String action){
        BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.ACTION.getName())
                .controls.links.get(action).click();
        Page.dialogConfirmation.confirm();
    }

    public static String getRefundNumber() {
        return BillingSummaryPage.tablePaymentsAndAdjustmentsGB.getRow(1).getCell(TableConstants.BillingPaymentsAndAdjustmentsGB.TYPE.getName()).getValue().split("\\D+")[1];
    }

    public SetupBillingGroupsAction setupBillingGroups() {
        return AutomationContext.getAction(SetupBillingGroupsAction.class);
    }

    public ManageInvoicingCalendarsAction addManageInvoicingCalendars() {
        return AutomationContext.getAction(ManageInvoicingCalendarsAction.class);
    }

    public SetUpConsolidatedStatementAction setUpConsolidatedStatement() {
        return AutomationContext.getAction(SetUpConsolidatedStatementAction.class);
    }

    public ManageConsolidatedStatementsAction manageConsolidatedStatementsAction() {
        return AutomationContext.getAction(ManageConsolidatedStatementsAction.class);
    }

    public ManagePaymentMethodsAction managePaymentMethods() {
        return AutomationContext.getAction(ManagePaymentMethodsAction.class);
    }

    public InquiryAction inquiry() {
        return AutomationContext.getAction(InquiryAction.class);
    }

    public TestData defaultAPITestData() {
        return defaultAPITestData;
    }
}
