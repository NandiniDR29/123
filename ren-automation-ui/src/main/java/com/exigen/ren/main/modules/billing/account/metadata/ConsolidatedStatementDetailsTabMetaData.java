package com.exigen.ren.main.modules.billing.account.metadata;

import com.exigen.ipb.eisa.controls.dialog.DialogMultiSelectorExtended;
import com.exigen.ipb.eisa.controls.dialog.type.AbstractDialog;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.ComboBox;
import com.exigen.istf.webdriver.controls.TextBox;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import com.exigen.istf.webdriver.controls.composite.assets.metadata.MetaData;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import org.openqa.selenium.By;

public class ConsolidatedStatementDetailsTabMetaData extends MetaData {

    public static final AssetDescriptor<ComboBox> CONSOLIDATED_STATEMENT_ENABLED_FROM = declare("Consolidated Statement enabled from:", ComboBox.class);
    public static final AssetDescriptor<Button> ADD_BUTTOM = declare("Add", Button.class, By.id("setUpConsolidatedStatement:addButton"));

    public static final AssetDescriptor<DialogMultiSelectorExtended> ADD_BILLING_ACCOUNTS = declare("Add Billing Accounts", DialogMultiSelectorExtended.class, AddBillingAccountsMetaData.class, false, By.xpath("//div[@id='setUpConsolidatedStatement:consolidatedStatementAddBillingAccountsDialog']"));

    public static final class AddBillingAccountsMetaData extends MetaData {
        public static final AssetDescriptor<TextBox> POLICY = declare("Policy #", TextBox.class);
        public static final AssetDescriptor<TextBox> BILLING_ACCOUNT = declare("Billing Account #", TextBox.class, Waiters.DEFAULT, false, By.id("setUpConsolidatedStatement:billingAccountNumber"));
        public static final AssetDescriptor<Button> BUTTON_OPEN_POPUP = declare(AbstractDialog.getDefaultPopupOpenerName(), Button.class, Waiters.DEFAULT, false, By.id("setUpConsolidatedStatement:addButton"));
    }
}