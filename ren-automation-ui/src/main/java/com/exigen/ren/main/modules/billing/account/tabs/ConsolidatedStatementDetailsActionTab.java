package com.exigen.ren.main.modules.billing.account.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData;
import org.openqa.selenium.By;

public class ConsolidatedStatementDetailsActionTab extends ActionTab {

    public static TableExtended<TableConstants.IncludeBAInTheConsolidatedStatement> tableIncludeBAInTheConsolidatedStatement = new TableExtended<>("Include Billing Accounts in the Consolidated Statement", By.xpath("//div[@id='setUpConsolidatedStatement:additionalAccountsTable']//table"));
    public static TableExtended<TableConstants.RelatedBillingAccounts> tableRelatedBillingAccounts = new TableExtended<>("Related Billing Accounts", By.xpath("//*[@id='setUpConsolidatedStatement:relatedBillingAccountsTable']//table"));
    public static TableExtended<TableConstants.RelatedBillingAccounts> tableSearchResults = new TableExtended<>("Search results", By.xpath("//*[@id='setUpConsolidatedStatement:additionalAccountsSearchTable']//table"));
    public static Button buttonSelect = new Button(By.id("setUpConsolidatedStatement:selectBtn"));
    public static Button buttonSearch = new Button(By.id("setUpConsolidatedStatement:searchBtn"));

    public ConsolidatedStatementDetailsActionTab() {
        super(ConsolidatedStatementDetailsTabMetaData.class);
    }
}