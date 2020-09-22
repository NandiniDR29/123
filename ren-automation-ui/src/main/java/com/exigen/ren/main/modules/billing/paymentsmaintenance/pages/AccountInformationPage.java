/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.paymentsmaintenance.pages;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.pages.summary.SummaryPage;
import org.openqa.selenium.By;

public class AccountInformationPage extends SummaryPage {
    public static TableExtended<AccountInformation> tableAccountInformation = new TableExtended<>("Account Information", By.xpath("//div[@id='suspenseForm:accountAssociationResults']//table"));

    public static void removeAmount(int rowNum) {
        tableAccountInformation.getRow(rowNum).getCell(AccountInformation.ACTIONS.getName()).controls.links.get(ActionConstants.REMOVE).click();
        dialogConfirmation.confirm();
    }

    public enum AccountInformation implements Named {
        ASSOCIATED_AMOUNT("Associated Amount"),
        BILLING_ACCOUNT("Billing Account #"),
        ALLOCATED_AMOUNT("Allocated Amount"),
        ACTIONS("Action");

        private String name;

        AccountInformation(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
