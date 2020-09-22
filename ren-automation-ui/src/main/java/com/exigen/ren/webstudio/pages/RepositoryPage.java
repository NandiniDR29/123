/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.webstudio.pages;

import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.CheckBox;
import org.openqa.selenium.By;

public class RepositoryPage extends WebStudioAbstractPage  {
    private static final String PROJECT_TMPL = "//a[.='%s']";
    private static final Button buttonDeploy = new Button(By.id("deployId1"), wsWaiter);
    private static final CheckBox allCheckBoxes = new CheckBox(By.id("smartRedeployForm:smartRedeployTable:allDeploymentsCheck"), wsWaiter);
    private Button buttonFinallyDeploy = new Button(By.id("smartRedeployForm:smartRedeployBtn"), wsWaiter);

    public RepositoryPage openProject(String projectName) {
        new Button(By.xpath(String.format(PROJECT_TMPL, projectName)), wsWaiter).click();
        return this;
    }

    public void deploy() {
        buttonDeploy.click();
        allCheckBoxes.setValue(true);
        buttonFinallyDeploy.click();
    }
}
