/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.billing.account.metadata.OtherTransactionsActionTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.main.modules.billing.account.tabs.OtherTransactionsActionTab.Allocations.WRITE_OFF_ALL;


public class OtherTransactionsActionTab extends ActionTab {

    public static final TableExtended<Allocations> tableAllocations = new TableExtended<>("Allocations", By.xpath("//div[@id='fullAdminOtherTransactionsForm:invoiceTable']//table"));

    public OtherTransactionsActionTab() {
        super(OtherTransactionsActionTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        OtherTransactionsActionTab.tableAllocations.getRow(1).getCell(WRITE_OFF_ALL.getName()).controls.checkBoxes.getFirst().setValue(true);
        OtherTransactionsActionTab.buttonOk.click();
        return this;
    }

    public enum Allocations implements Named {
        WRITE_OFF_ALL("Write-Off All"),
        INVOICE("Invoice"),
        AMOUNT_BILLED("Amount Billed"),
        AMOUNT_PAID("Amount Paid"),
        REMAINING_DUE("Remaining Due"),
        WRITE_OFF_AMOUNT("Write-Off Amount");

        private String name;

        Allocations(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
