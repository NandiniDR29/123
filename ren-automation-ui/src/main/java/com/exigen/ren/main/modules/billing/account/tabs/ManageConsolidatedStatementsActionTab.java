package com.exigen.ren.main.modules.billing.account.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.billing.account.metadata.ConsolidatedStatementDetailsTabMetaData;
import com.exigen.ren.main.modules.billing.account.metadata.ManageConsolidatedStatementsTabMetaData;
import org.openqa.selenium.By;

public class ManageConsolidatedStatementsActionTab extends ActionTab {

    public static TableExtended<TableConstants.IncludeBAInTheConsolidatedStatement> tableManageConsolidatedStatements =
            new TableExtended<>("Manage Consolidated Statements", By.xpath("//div[@id='setUpConsolidatedStatement:consolidatedStatementsTable']//table"));

    public ManageConsolidatedStatementsActionTab() {
        super(ManageConsolidatedStatementsTabMetaData.class);
    }
}