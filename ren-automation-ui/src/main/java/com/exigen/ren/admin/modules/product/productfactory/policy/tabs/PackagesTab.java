/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory.policy.tabs;

import com.exigen.ipb.eisa.controls.productfactory.custom.PFDialog;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFJSButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFStaticElement;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.ElementWaiter;
import com.exigen.istf.webdriver.controls.waiters.Waiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.product.productfactory.DefaultTab;
import com.exigen.ren.admin.modules.product.productfactory.metadata.ProductMetaData;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class PackagesTab extends DefaultTab {
    public static AssetList assetListPackages = new AssetList(By.xpath("//*"), ProductMetaData.PackagesTab.class);
    private static PFStaticElement frame = new PFStaticElement(By.xpath("//iframe[@class='fullframe has-drawer-left-always-open']"));
    private static final String OPEN_TABLE_ACTIONS_TEMPLATE = "//span[text()='%s']/parent::*/following-sibling::*/button";
    private static final String SELECT_TABLE_ACTIONS_TEMPLATE = "//div[contains(@class, 'md-active')]//button[text()[contains(.,'%s')]]";
    public static Table tablePackages = new Table(By.xpath("//table[@ng-model='$ctrl.selectedPackages']"));
    public static Table tableDimensions = new Table(By.xpath("//table[@ng-model='$ctrl.selectedDimensions']"));
    public static PFDialog dialogDeleteFromDimension = new PFDialog("//*[@class='_md md-default-theme md-transition-in']");
    public static PFJSButton buttonCreateNewTable = new PFJSButton(By.xpath("//*[contains(@ng-click,'$ctrl.createTable')]"));
    public static PFJSButton buttonSave = new PFJSButton(By.xpath("//button[@ng-click='$ctrl.save()']"));
    public static PFStaticElement labelCreateTableHeader = new PFStaticElement(By.xpath("//h2[@class='md-title']"));


    @Override
    protected String tabName() {
        return "Packages";
    }

    @Override
    protected Waiter tabLoadingWaiter() {
        return Waiters.DEFAULT.then(new ElementWaiter(frame).then(Waiters.SLEEP(7000)));
    }

    /**
     * Switch to iFrame
     */
    public void switchToFrame() {
        BrowserController.get().driver().switchTo().frame(frame.getWebElement());
    }

    /**
     * Switch to default content
     */
    public void switchToDefaultContent() {
        BrowserController.get().driver().switchTo().defaultContent();
    }

    public void openPackage(String packageName) {
        BrowserController.get().executeScript("arguments[0].click()",
                PackagesTab.tablePackages.getRow("Label", packageName).getCell("Label").controls.links.getFirst().getWebElement());
        new ElementWaiter(PackagesTab.tableDimensions).go();
    }

    public void openDimension(String dimension) {
        BrowserController.get().executeScript("arguments[0].click()",
                PackagesTab.tableDimensions.getRow("countryCd", dimension).getWebElement());
        Waiters.SLEEP(1500).go();
    }

    public void clickPackageTableActions(String packageTableName) {
        new PFJSButton(By.xpath(String.format(OPEN_TABLE_ACTIONS_TEMPLATE, packageTableName))).click();
    }

    public void selectPackageTableAction(String action) {
        new PFJSButton(By.xpath(String.format(SELECT_TABLE_ACTIONS_TEMPLATE, action))).click();
        Waiters.SLEEP(2000).go();
    }

    public void performActionForSelectedPackageTable(String packageName, String action) {
        clickPackageTableActions(packageName);
        selectPackageTableAction(action);
    }

    public List<String> getAllPackageTables() {
        return BrowserController.get().driver().findElements(By.xpath("//*[@class='md-title']")).stream().
                map(WebElement::getText).collect(Collectors.toList());
    }

    public List<String> getAllAvailableActions() {
        return BrowserController.get().driver().findElements(By.xpath("//div[contains(@class, 'md-active')]//button")).stream().
                map(element -> StringUtils.substringAfter(element.getText().trim(), " ")).collect(Collectors.toList());
    }

    public void addTable(TestData td) {
        buttonCreateNewTable.click();
        assetListPackages.fill(td);
        buttonSave.click();
    }

}
