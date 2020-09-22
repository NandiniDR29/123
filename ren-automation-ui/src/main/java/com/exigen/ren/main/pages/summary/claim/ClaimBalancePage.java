package com.exigen.ren.main.pages.summary.claim;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.TableConstants;
import org.openqa.selenium.By;

public class ClaimBalancePage extends MainPage {

    public static TableExtended<TableConstants.ClaimDentalClaimBalanceExtendedTable> tableClaimBalance = (TableExtended) new TableExtended<>("Claim Balance",
            By.xpath("//div[@id='policyDataGatherForm:paymentBalanceHistoryTable_ClaimsDentalPaymentBalanceHistory']/div[1]/table")).applyConfiguration("NoRecordsFound");
    public static final StaticElement totalBalance = new StaticElement(By.id("policyDataGatherForm:paymentBalanceHistoryTable_ClaimsDentalPaymentBalanceHistory:paymentBalanceHistory_totalBalance"));
}
