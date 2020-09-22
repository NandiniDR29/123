package com.exigen.ren.main.pages.summary.claim;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.main.pages.summary.SummaryPage;
import org.openqa.selenium.By;

public class ClaimPaymentsPage extends SummaryPage {

    public static Button buttonInquiryPayment = new Button(By.id("policyDataGatherForm:paymentActionLink_paymentInquiry"));
    public static Button buttonUpdatePayment = new Button(By.id("policyDataGatherForm:paymentActionLink_update"));
    public static Button buttonIssuePayment = new Button(By.id("policyDataGatherForm:paymentActionLink_issue"));
    public static Button buttonClearPayment = new Button(By.id("policyDataGatherForm:paymentActionLink_clear"));
    public static Button buttonConsolidatedPayments = new Button(By.id("policyDataGatherForm:paymentActionLink_consolidatedPayments"));
    public static Table tableClaimPaymentView = new Table(By.id("policyDataGatherForm:body_transactionInstanceTable_ClaimsPaymentView"));
    public static final TableExtended<PaymentDeliveryAddress> tablePaymentDeliveryAddress = new TableExtended<>("Payment Delivery Address",
            By.id("policyDataGatherForm:body_componentInstancesTable_ClaimsPaymentAddressContactView"));
    public static final TableExtended<PaymentStatusHistory> tablePaymentStatusHistory = new TableExtended<>("Payment Status History",
            By.id("policyDataGatherForm:body_componentInstancesTable_ClaimsPaymentTransactionHistory"));
    public static final TableExtended<PaymentDetails> tablePaymentDetails = new TableExtended<>("Payment Details",
            By.id("policyDataGatherForm:body_transactionInstanceTable_ClaimsPaymentView"));


    public enum PaymentDeliveryAddress implements Named {
        ATTENTION("Attention"),
        DELIVERY_METHOD("Delivery Method"),
        COUNTRY("Country"),
        ZIP_POSTAL_CODE("Zip / Postal Code"),
        ADDRESS_LINE_1("Address Line 1"),
        CITY("City"),
        STATE_PROVINCE("State / Province");

        private String name;

        PaymentDeliveryAddress(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PaymentStatusHistory implements Named {
        PAYMENT_NUMBER("Payment Number"),
        NET_AMOUNT("Net Amount"),
        PAYEE("Payee"),
        PAYMENT_STATUS_DATE("Payment Status Date"),
        PERFORMER("Performer"),
        ACTION("Action"),
        STATUS("Status"),
        DETAILS("Details");

        private String name;

        PaymentStatusHistory(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PaymentDetails implements Named {
        SERIES("Series #"),
        PAYMENT("Payment #"),
        BENEFIT_AMOUNT("Benefit Amount"),
        NET_AMOUNT("Net Amount"),
        PAYEE("Payee"),
        PAYMENT_POST_DATE("Payment Post Date"),
        CREATOR("Creator"),
        STATUS("Status");

        private String name;

        PaymentDetails(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

}
