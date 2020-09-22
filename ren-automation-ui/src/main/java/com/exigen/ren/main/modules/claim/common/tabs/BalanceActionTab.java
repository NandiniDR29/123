package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.modules.claim.common.metadata.BalanceActionMetaData;
import org.openqa.selenium.By;

public class BalanceActionTab extends ActionTab {
    public static TableExtended<ClaimUnprocessedBalanceTableExtended> tableSummaryOfClaimPaymentsAndRecoveries =
            new TableExtended<>("Claim Unprocessed Balance", By.xpath("//div[@id='policyDataGatherForm:componentView_ClaimsUnprocessedBalance']//table"));

    public BalanceActionTab() {
        super(BalanceActionMetaData.class);
    }

    public enum ClaimUnprocessedBalanceTableExtended implements Named {
        PARTY_NAME("Party Name"),
        PAYMENT_NUMBER("Payment Number"),
        POST_DATE("Post Date"),
        FROM_DATE("From Date"),
        PAYMENT_STATUS("Payment Status"),
        NET_AMOUNT("Net Amount"),
        BALANCE_AMOUNT("Balance Amount");

        private String name;

        ClaimUnprocessedBalanceTableExtended(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}