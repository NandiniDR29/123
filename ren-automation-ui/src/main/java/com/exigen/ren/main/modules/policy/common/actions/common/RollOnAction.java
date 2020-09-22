/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common.actions.common;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.Action;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.main.modules.policy.common.tabs.common.RollOnChangesActionTab;
import org.openqa.selenium.By;

//TODO iguliam : check inherited classes and union same to common class
public class RollOnAction implements Action {
    public static Button buttonRollOnChanges = new Button(By.xpath("//input[@value = 'Roll On Changes' and not(@class = 'hidden') and not(contains(@style,'none'))]"));

    private Workspace workspace = new Workspace.Builder()
            .registerTab(RollOnChangesActionTab.class)
            .build();

    @Override
    public String getName() {
        return "Roll On Changes";
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public Action submit() {
        buttonRollOnChanges.click();
        return this;
    }

    //TODO iguliam Move to CL level if not applicable for personal lines
    public Action perform(boolean isAutomatic, boolean setOldValues) {
        start();

        Table tableOosEndorsements = new Table(By.xpath("//table[@id='affectedEndoresmentForm:historyTable']"));
        int rowsCount = tableOosEndorsements.getRowsCount();
        int columnsCount = tableOosEndorsements.getColumnsCount();

        for (int i = 1; i <= rowsCount; i++) {
            tableOosEndorsements.getRow(i).getCell(columnsCount).controls.links.get(
                    isAutomatic ? 1 : 2).click();
        }

        if (!isAutomatic) {
            Table tableDifferences = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table"));
            rowsCount = tableDifferences.getRowsCount();
            columnsCount = tableDifferences.getColumnsCount();

            //expand rows
            for (int i = 0; i < rowsCount; i++) {
                Link expandTringle = new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i
                        + "']/td[1]/span[contains(@class, 'ui-treetable-toggler')]"));
                if (expandTringle.isPresent()) {
                    expandTringle.click();
                }
            }

            //apply values
            Link linkSetValue;
            rowsCount = tableDifferences.getRowsCount();
            for (int i = 1; i <= rowsCount; i++) {
                linkSetValue = tableDifferences.getRow(i).getCell(columnsCount).controls.links.get(
                        setOldValues ? 2 : 1);

                if (linkSetValue.isPresent() && linkSetValue.isVisible()) {
                    linkSetValue.click();
                }
            }
        }
        return submit();
    }
}
