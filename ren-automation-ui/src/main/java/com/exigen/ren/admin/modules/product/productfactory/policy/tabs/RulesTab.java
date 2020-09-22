/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory.policy.tabs;

import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFCheckBox;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFStaticElement;
import com.exigen.ipb.eisa.controls.productfactory.custom.PFTextBox;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.ipb.eisa.waiters.AngularLoadingWaiter;
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
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;

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
    public static PFButton buttonDeleteConfirm = new PFButton(By.className("md-primary md-confirm-button md-button md-ink-ripple md-default-theme"));
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
        BrowserController.get().executeScript("document.getElementById('add-rule').click()");
        AngularLoadingWaiter.get().go();
        new ElementWaiter(buttonSaveChanges).go();
        assetListRuleProperties.fill(testData);
        BrowserController.get().executeScript("document.getElementById('save').click()");
        AngularLoadingWaiter.get().go();
        new ElementWaiter(buttonAddNewRule).go();
        switchToDefaultContent();
        return this;
    }

    public RulesTab editRule(TestData testData) {
        ((RulesTab) open()).switchToFrame();
        searchRule(testData);
        BrowserController.get().executeScript("document.getElementsByClassName('name ng-binding')[0].click()");
        AngularLoadingWaiter.get().go();
        new ElementWaiter(buttonEditRule).go();
        BrowserController.get().executeScript("document.getElementsByClassName('md-icon-button md-button md-ink-ripple')[6].click()");
        AngularLoadingWaiter.get().go();
        new ElementWaiter(buttonSaveChanges).go();
        assetListRuleProperties.fill(testData);
        BrowserController.get().executeScript("document.getElementById('save').click()");
        AngularLoadingWaiter.get().go();
        new ElementWaiter(buttonAddNewRule).go();
        switchToDefaultContent();
        return this;
    }

    public RulesTab deleteRule(TestData testData) {
        ((RulesTab) open()).switchToFrame();
        searchRule(testData);
        Waiters.DEFAULT.then(new ElementWaiter(chbSelectRule)).go();
        BrowserController.get().executeScript("document.getElementsByTagName('md-checkbox')[1].click()");
        buttonDeleteRule.waitFor(BaseElement::isVisible);
        BrowserController.get().executeScript("document.getElementById('delete-rules').click()");
        Waiters.SLEEP(3000).go();
        BrowserController.get().executeScript("document.getElementsByClassName('md-primary md-confirm-button md-button md-ink-ripple md-default-theme')[0].click()");
        AngularLoadingWaiter.get().then(new ElementWaiter(buttonAddNewRule)).go();
        RetryService.run(predicate -> tableSearchResults.getRowsCount() == 0, () -> null,
                StopStrategies.stopAfterAttempt(60), WaitStrategies.fixedWait(1, TimeUnit.SECONDS));
        switchToDefaultContent();
        return this;
    }

    private RulesTab searchRule(TestData testData) {
        searchRuleName.waitForAccessible(WAIT_FOR_ELEMENT_TIMEOUT);
        searchRuleName.fill(testData);
        buttonSearchRule.waitFor(BaseElement::isVisible);
        BrowserController.get().executeScript("document.getElementsByClassName('md-raised md-button md-ink-ripple')[0].click()");
        AngularLoadingWaiter.get().then(new ElementWaiter(tableSearchResults)).go();
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
