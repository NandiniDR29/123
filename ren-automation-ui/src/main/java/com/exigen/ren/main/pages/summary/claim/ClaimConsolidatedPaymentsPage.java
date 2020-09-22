package com.exigen.ren.main.pages.summary.claim;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.MainPage;
import org.openqa.selenium.By;

import static com.exigen.ren.main.enums.CommonLocators.COMMON_LOCATOR_GET_VALUE_BY_LABEL;

public class ClaimConsolidatedPaymentsPage extends MainPage {

    public static StaticElement labelCheckNum = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Check #"));
    public static StaticElement labelConsolidatedPaymentNum = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Consolidated Payment #"));
    public static StaticElement labelConsolidatedPaymentAmount = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Consolidated Payment Amount"));
    public static StaticElement labelProviderTIN = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Provider TIN"));
    public static StaticElement labelPracticeName = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Practice Name"));
    public static StaticElement labelProviderAddress = new StaticElement(COMMON_LOCATOR_GET_VALUE_BY_LABEL.format("Provider Address"));

    public static TableExtended<ListOfConsolidatedPayments> tableListOfConsolidatedPayments = new TableExtended<>("List of Consolidated Payments",
            By.xpath("//div[@id='policyDataGatherForm:componentView_ClaimsDentalConsolidatedPayments']//table"));

    public enum ListOfConsolidatedPayments implements Named {
        PAYMENT_NUM("Payment #"),
        PAYMENT_STATUS("Payment Status"),
        CLAIM_NUM("Claim #"),
        NET_AMOUNT("Net Amount"),
        PROVIDER_NAME("Provider Name");

        private String name;

        ListOfConsolidatedPayments(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

}
