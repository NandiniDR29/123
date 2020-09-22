package com.exigen.ren.modules.billing.admin;

import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.admin.modules.billing.rules.payment_tolerance.PaymentToleranceContext;
import com.exigen.ren.admin.modules.billing.rules.payment_tolerance.tabs.PaymentToleranceTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.admin.modules.billing.rules.payment_tolerance.metadata.PaymentToleranceMetaData.*;
import static com.exigen.ren.admin.modules.billing.rules.payment_tolerance.tabs.PaymentToleranceTab.PaymentTolerance.ACTIONS;
import static com.exigen.ren.admin.modules.billing.rules.payment_tolerance.tabs.PaymentToleranceTab.PaymentTolerance.EVENT;
import static com.exigen.ren.utils.components.Components.Platform_Admin;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestBillingPaymentToleranceConfiguration extends BaseTest implements PaymentToleranceContext {

    @Test(groups = {BILLING_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-20471", component = Platform_Admin)
    public void testRenameLabelAgencyTypeAgency() {
        adminApp().open();
        paymentTolerance.navigate();
        PaymentToleranceTab.tablePaymentTolerance.getRow(ImmutableMap.of(EVENT.getName(), "Invoice Payment Tolerance"))
                .getCell(ACTIONS.getName()).controls.links.get(ActionConstants.EDIT).click();

        assertSoftly(softly -> {
            Tab paymentToleranceTab = paymentTolerance.getDefaultWorkspace().getTab(PaymentToleranceTab.class);
            Row invoicePaymentTolerance = PaymentToleranceTab.tablePaymentTolerance.getRow(ImmutableMap.of(EVENT.getName(), "Invoice Payment Tolerance"));
            softly.assertThat(invoicePaymentTolerance).isPresent();
            softly.assertThat(invoicePaymentTolerance.getCell(PaymentToleranceTab.PaymentTolerance.BROAD_LINE_OF_BUSINESS.getName())).hasValue("Group Insurance");
            softly.assertThat(invoicePaymentTolerance.getCell(PaymentToleranceTab.PaymentTolerance.PRODUCTS.getName())).hasValue("All");
            softly.assertThat(invoicePaymentTolerance.getCell(PaymentToleranceTab.PaymentTolerance.GEOGRAPHY.getName())).hasValue("All");
            softly.assertThat(invoicePaymentTolerance.getCell(PaymentToleranceTab.PaymentTolerance.AMOUNT.getName())).hasValue("$25%");
            softly.assertThat(paymentToleranceTab.getAssetList().getAsset(SELF_ADMINISTERED)).hasValue(true);
            softly.assertThat(paymentToleranceTab.getAssetList().getAsset(LIST_BILL)).hasValue(true);
            softly.assertThat(paymentToleranceTab.getAssetList().getAsset(FLAT)).hasValue(false);
            softly.assertThat(paymentToleranceTab.getAssetList().getAsset(PERCENTAGE)).hasValue(true);
            softly.assertThat(paymentToleranceTab.getAssetList().getAsset(TOLERANCE_BALANCE)).hasValue("Carry Forward Balance");
        });
    }
}