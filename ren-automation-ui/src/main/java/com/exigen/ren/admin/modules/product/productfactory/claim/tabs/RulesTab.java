/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory.claim.tabs;

import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFCheckBox;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFStaticElement;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFTextBox;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.ElementWaiter;
import com.exigen.istf.webdriver.controls.waiters.Waiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.product.productfactory.DefaultTab;
import com.exigen.ren.admin.modules.product.productfactory.metadata.ProductMetaData;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.exigen.istf.verification.CustomAssertions.assertThat;

/**
 * @author ikisly
 */
public class RulesTab extends DefaultTab {
    final int WAIT_FOR_ELEMENT_TIMEOUT = 60000;
    public static PFTextBox searchRuleName = new PFTextBox(By.xpath("//input[@name='name']"));
    public static AssetList assetListRuleProperties = new AssetList(By.xpath("//*"), ProductMetaData.RulesTab.RuleProperties.class);

    public static PFStaticElement frame = new PFStaticElement(By.xpath("//iframe[@class='fullframe has-drawer-left-always-open']"));

    public static PFStaticElement sectionAutocomplete = new PFStaticElement(By.xpath("//ul[@class='md-autocomplete-suggestions']"));
    public static Table tableSearchResults = new Table(By.xpath("//table[@st-table='rulesCtrl.rules']")).applyConfiguration("RuleSearchResult");

    public static PFButton buttonAddNewRule = new PFButton(By.id("add-rule"));
    public static PFButton buttonDeleteRule = new PFButton(By.id("delete-rules"));
    public static PFButton buttonDeleteConfirm = new PFButton(By.className("md-confirm-button"));
    public static PFCheckBox chbSelectRule = new PFCheckBox(By.xpath("//table[@st-table='rulesCtrl.rules']//tbody//md-checkbox"));
    public static PFButton buttonEditRule = new PFButton(By.xpath("//button[@title='Open']"));
    public static PFButton buttonSaveChanges = new PFButton(By.id("save"));
    public static PFButton buttonSearchRule = new PFButton(By.xpath("//button[@class='md-raised md-button md-ink-ripple']"));

    public final Verify verify = this.new Verify();

    public RulesTab() {
        searchRuleName.setName("Rule name");
    }

    @Override
    protected String tabName() {
        return "Rules";
    }

    public void switchToFrame() {
        BrowserController.get().driver().switchTo().frame(frame.getWebElement());
    }

    /**
     * Switch to default content
     */
    public void switchToDefaultContent() {
        BrowserController.get().driver().switchTo().defaultContent();
    }

    public RulesTab addRule(TestData testData) {
        ((RulesTab) open()).switchToFrame();
        buttonAddNewRule.waitForAccessible(WAIT_FOR_ELEMENT_TIMEOUT);
        buttonAddNewRule.click(Waiters.DEFAULT.then(new ElementWaiter(buttonSaveChanges)));
        assetListRuleProperties.fill(testData);
        new PFButton(By.id("save")).click(Waiters.DEFAULT.then(new ElementWaiter(buttonAddNewRule)));
        switchToDefaultContent();
        return this;
    }

    public RulesTab editRule(TestData testData) {
        ((RulesTab) open()).switchToFrame();
        searchRule(testData);
        tableSearchResults.getRow(1).getCell(2).click();
        buttonEditRule.click();
        assetListRuleProperties.fill(testData);
        buttonSaveChanges.click();
        switchToDefaultContent();
        return this;
    }

    public RulesTab deleteRule(TestData testData) {
        ((RulesTab) open()).switchToFrame();
        searchRule(testData);
        chbSelectRule.setValue(true);
        buttonDeleteRule.click();
        buttonDeleteConfirm.click();
        switchToDefaultContent();
        return this;
    }

    private RulesTab searchRule(TestData testData) {
        searchRuleName.fill(testData);

        if (sectionAutocomplete.isPresent() && sectionAutocomplete.isVisible()) {
            sectionAutocomplete.getWebElement().sendKeys(Keys.ESCAPE);
        }

        buttonSearchRule.waitFor(BaseElement::isVisible);
        buttonSearchRule.click();
        return this;
    }

    @Override
    protected Waiter tabLoadingWaiter() {
        return Waiters.DEFAULT.then(new ElementWaiter(frame).then(Waiters.SLEEP(2000)));
    }

    public class Verify {
        /**
         * Rule should be uniquely determined by testData.
         */
        public void ruleExists(TestData testData, boolean exist) {
            ((RulesTab) open()).switchToFrame();
            searchRule(testData);
            assertThat(exist).as("Rule is not exist as expected.").isEqualTo(tableSearchResults.getRowsCount() == 1);
            switchToDefaultContent();
        }

        /**
         * Rule should be uniquely determined by testData.
         */
        public void ruleStatus(TestData testData, boolean enabled) {
            ((RulesTab) open()).switchToFrame();
            searchRule(testData);
            assertThat(enabled).as("Rule is not enabled as expected.").isEqualTo(!tableSearchResults.getRow(1).getAttribute("class").contains("rule-disabled"));
            switchToDefaultContent();
        }
    }
}
