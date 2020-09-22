package com.exigen.ren.admin.modules.agencyvendor.agency.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.admin.modules.agencyvendor.AgencyVendorDefaultTab;
import com.exigen.ren.admin.modules.agencyvendor.agency.metadata.CommissionsInfoMetaData;
import com.exigen.ren.common.Tab;
import org.openqa.selenium.By;

public class CommissionsInfoTab extends AgencyVendorDefaultTab {

    public static TableExtended<CommissionPaymentHistory> tableCommissionPaymentHistory = new TableExtended<>("Commission Payment History", By.id("brokerCommissionsForm:body_brokerCommissionHistTable"));

    public CommissionsInfoTab() {
        super(CommissionsInfoMetaData.class);
    }


    public enum CommissionPaymentHistory implements Named {
        PAYMENT_DATE("Payment Date"),
        REFERENCE_ID("Reference Id"),
        PAYMENT_TYPE("Payment Type"),
        PAYMENT_METHOD("Payment Method"),
        AMOUNT("Amount"),
        STATUS("Status");
        private String name;

        CommissionPaymentHistory(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    public Tab navigateToTab() {
        new Link(By.xpath(".//*[@id='brokerAgencyInfoForm']//*[text()='Commission']")).click();
        return this;
    }
}
