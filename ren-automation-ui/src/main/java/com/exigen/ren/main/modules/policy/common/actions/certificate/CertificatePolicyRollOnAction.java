/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common.actions.certificate;

import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.Action;
import com.exigen.ren.main.modules.policy.common.actions.common.RollOnAction;
import org.openqa.selenium.By;

public class CertificatePolicyRollOnAction extends RollOnAction {

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
                if (new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i + "']" +
                        "/td[1]/span[contains(@class, 'ui-treetable-toggler')]")).isPresent()) {
                    new Link(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']//tr[@id='comparisonTreeForm:comparisonTree_node_" + i + "']" +
                            "/td[1]/span[contains(@class, 'ui-treetable-toggler')]")).click();
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
