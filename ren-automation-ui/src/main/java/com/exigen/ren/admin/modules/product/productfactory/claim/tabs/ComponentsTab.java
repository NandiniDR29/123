/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory.claim.tabs;

import com.exigen.ipb.eisa.controls.productfactory.ProductFactoryCheckBox;
import com.exigen.ipb.eisa.controls.productfactory.custom.*;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.waiters.ElementWaiter;
import com.exigen.istf.webdriver.controls.waiters.LocatorWaiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.product.productfactory.DefaultTab;
import com.exigen.ren.admin.modules.product.productfactory.metadata.ProductMetaData;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.UnhandledAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.exigen.istf.verification.CustomAssertions.assertThat;

public class ComponentsTab extends DefaultTab {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentsTab.class);
    public static PFButton btnSave = new PFButton(By.id("persistence:save"));
    public static String connectLinkTemplate = "//*[(self::span or self::h4) and contains(@id,'componentName') and .='%s']/ancestor::div[@class='pf-list-item']//a[.='Connect']";
    public AssetList assetListProperties = new AssetList(By.xpath("//*"), ProductMetaData.ComponentsTab.class);


    public ComponentsTab root(TestData testData) {
        open();
        PFLink addComponent = new PFLink(By.xpath(String.format(connectLinkTemplate, testData.getValue("Name"))), Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
        addComponent.waitForAccessible(Integer.parseInt(PropertyProvider.getProperty(TestProperties.WEBDRIVER_AJAX_TIMEOUT, "5000")));
        addComponent.click();
        submit();
        return this;
    }

    public Relation relation() {
        return ((ComponentsTab) open()).new Relation();
    }

    public Repository repository() {
        return ((ComponentsTab) open()).new Repository();
    }

    public Port port() {
        return ((ComponentsTab) open()).new Port();
    }

    @Override
    protected String tabName() {
        return "Components";
    }


    public class Port {
        public Port() {
            this(true);
        }

        public Port(boolean needOpening) {
            if (needOpening) {
                setTreeType(this.getClass().getSimpleName());
            }
        }

        public Port selectNode(List<String> path) {
            ComponentsTab.this.selectNode(path);
            return this;
        }

        public Port selectNode(TestData testData) {
            return selectNode(testData.getList("Path"));
        }

        public Port connect(TestData testData) {
            selectNode(testData.getList("Path"));

            PFButton buttonSearch = new PFButton(By.xpath("//form[@id='port-context']//button[contains(@id,'port-context')]"));
            buttonSearch.click();

            PFTextBox textBoxSearch = new PFTextBox(By.id("port-context:query")) {
                @Override
                protected void setRawValue(String value) {
                    Action.before();
                    CharSequence seq = value.isEmpty() ? Keys.DELETE : value;
                    getWebElement().sendKeys(Keys.chord(Keys.CONTROL, "a") + seq + Keys.ENTER);
                    Action.after();
                }
            };
            textBoxSearch.setValue(testData.getValue("Description"));
            PFLink addComponent = new PFLink(By.xpath(String.format(connectLinkTemplate, testData.getValue("Name"))), Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
            addComponent.waitForAccessible(Integer.parseInt(PropertyProvider.getProperty(TestProperties.WEBDRIVER_AJAX_TIMEOUT, "5000")));
            addComponent.click();
            submit();
            return this;
        }

        public Port manageAttributes(TestData testData) {
            return this;
        }

        public Port updateComponent(TestData testData) {
            selectNode(testData.getList("Path"));
            ComponentsTab.this.updateComponent(testData);
            submit();
            return this;
        }
    }

    public class Relation {

        public Relation() {
            setTreeType(this.getClass().getSimpleName());
        }

        public Relation selectNode(TestData testData) {
            ComponentsTab.this.selectNode(testData.getList("Path"));
            return this;
        }

        public ManageAttribute manageAttributes() {
            return new ManageAttribute();
        }

        public ManageAttribute manageAttributes(TestData testData) {
            return new ManageAttribute(testData);
        }

        /**
         * Update control values of selected component described in {@code ProductMetaData.ComponentsTab.class}
         *
         * @param testData
         * @return
         */
        public Relation updateComponent(TestData testData) {
            selectNode(testData);
            ComponentsTab.this.updateComponent(testData);
            submit();
            return this;
        }
    }

    public class Repository {

        public Repository() {
            setTreeType(this.getClass().getSimpleName());
        }

        public Port connect(TestData td) {
            new PFLink(By.xpath("//h3[.='" + td.getValue("Name") + "']")).click();
            new PFButton(By.id("repository-component-actions:connectComponent")).click();
            submit();
            return new Port(false);
        }
    }

    /**
     * Set tree type according to name
     *
     * @param type
     */
    private void setTreeType(String type) {
        new PFLink(By.xpath("//button[span[@id='tree-context:active-type']]")).click(Waiters.SLEEP(1000));
        new PFLink(By.xpath("//ul[@id='tree-context:types']//a[.='" + type + "']")).click(
                Waiters.DEFAULT.then(new LocatorWaiter(By.xpath("//span[@id='tree-context:active-type' and .='" + type + "']"))));
    }

    /**
     * Update component of tree node
     *
     * @param testData
     */
    private void updateComponent(TestData testData) {
        assetListProperties.setName("Properties");
        assetListProperties.fill(testData);
    }

    /**
     * Select tree node by path
     *
     * @param path
     */
    private void selectNode(List<String> path) {
        path.forEach(this::expandComponent);
        new PFLink(By.xpath("//h3[.='" + path.get(path.size() - 1) + "']")).click(Waiters.DEFAULT.then(Waiters.SLEEP(1000)));
    }

    /**
     * Expand component of tree
     *
     * @param component
     */
    private void expandComponent(String component) {
        if (isComponentCanBeExpanded(component)) {
            BrowserController.get().executeScript("arguments[0].scrollIntoView(); RichFaces.$(arguments[0]).expand();",
                    new PFLink(By.xpath("//h3[.='" + component + "']/ancestor::div[contains(@class, 'rf-tr-nd rf-tr-nd-colps')]")).getWebElement());
        }
    }

    /**
     * Show if tree node is expandable
     *
     * @param component
     * @return
     */
    private boolean isComponentCanBeExpanded(String component) {
        try {
            return new PFLink(By.xpath("//h3[.='" + component + "']/ancestor::div/span")).getAttribute("class").contains("colps");
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Save changes on the tab
     */
    private void submit() {
        if (btnSave.isPresent()) {
            btnSave.click();
        }
    }

    /**
     * Class to manage attributes of node
     */
    public class ManageAttribute {
        PFButton btnManageAttributes = new PFButton(By.id("manage-attributes:manage"));

        StaticElement formComponentAttributes = new StaticElement(By.xpath("//form[@id='component-attributes']"));
        PFButton btnAddNewAttribute = new PFButton(formComponentAttributes, By.id("component-attributes:create"));
        PFButton btnDone = new PFButton(formComponentAttributes, By.xpath("//button[.='Done']"));
        PFButton btnAddComponentAttribute = new PFButton(formComponentAttributes, By.id("component-attributes:add"));

        StaticElement formAddAttributeForm = new StaticElement(By.xpath("//form[@id='attribute-add']"));
        PFButton btnAddAttribute = new PFButton(formAddAttributeForm, By.id("attribute-add:add"));

        StaticElement formAttributeCreate = new StaticElement(By.xpath("//form[@id='attribute-create']"));
        PFTextBox txAttributeName = new PFTextBox(formAttributeCreate, By.id("attribute-create:name"));
        PFTextBox txAttributeLabel = new PFTextBox(formAttributeCreate, By.id("attribute-create:label"));
        PFComboBox cmbDataType = new PFComboBox(formAttributeCreate, By.id("attribute-create:type"));
        PFButton btnCreateNewAttribute = new PFButton(formAttributeCreate, By.id("attribute-create:create"));

        TestData commonTestDataForActions;
        public final Verify verify = this.new Verify();

        public ManageAttribute() {
            this(null);
        }

        public ManageAttribute(TestData testData) {
            txAttributeName.setName("Attribute name");
            txAttributeLabel.setName("Attribute label");
            cmbDataType.setName("Data type");
            btnManageAttributes.click(new ElementWaiter(btnDone));
            commonTestDataForActions = testData;
        }

        /**
         * Create new attribute based on specific testData
         *
         * @param testData specific testData
         * @return
         */
        public ManageAttribute create(TestData testData) {
            testData.getTestDataList("Properties", "Manage Attributes", "Create").forEach(this::addAttribute);
            return this;
        }

        /**
         * Create new attribute based on common testData is set by constructor
         *
         * @return
         */
        public ManageAttribute create() {
            return create(commonTestDataForActions);
        }

        private void addAttribute(TestData testData) {
            btnAddNewAttribute.waitForAccessible(5000);
            btnAddNewAttribute.click();

            txAttributeName.fill(testData);
            txAttributeLabel.fill(testData);
            cmbDataType.fill(testData);

            btnCreateNewAttribute.click();
        }

        /**
         * Delete attributes based on specific testData
         *
         * @param testData specific testData
         * @return
         */
        public ManageAttribute delete(TestData testData) {
            testData.getList("Properties", "Manage Attributes", "Delete").forEach(this::deleteSingleAttribute);
            return this;
        }

        public ManageAttribute delete() {
            return delete(commonTestDataForActions);
        }

        /**
         * Delete attributes based on common testData is set by constructor
         *
         * @param attributeName
         */
        private void deleteSingleAttribute(String attributeName) {
            try {
                new PFButton(formComponentAttributes, By.xpath(".//h4[.='" + attributeName + "']/ancestor::div[@class='pf-list-item attribute-item']//div[@class='pf-list-item-action']//button"))
                        .click();
            } catch (UnhandledAlertException e) {
                LOGGER.debug("Cannot click on button... Accept alert...", e);
                BrowserController.get().driver().switchTo().alert().accept();
                //fake control for page loading wait after alert accepting.
                new StaticElement(By.xpath("//*")).waitForPageUpdate();
            }
        }

        /**
         * Add attributes based on specific testData
         *
         * @param testData specific testData
         * @return
         */
        public ManageAttribute add(TestData testData) {
            btnAddComponentAttribute.click();
            testData.getList("Properties", "Manage Attributes", "Add").forEach(this::addSingleAttribute);
            btnAddAttribute.click();
            return this;
        }

        /**
         * Add attributes based on common testData is set by constructor
         *
         * @return
         */
        public ManageAttribute add() {
            return add(commonTestDataForActions);
        }

        private void addSingleAttribute(String attributeName) {
            new ProductFactoryCheckBox(formAddAttributeForm, By.xpath(".//h4[.='" + attributeName + "']/ancestor::div[@class='pf-list-item']//input[@type='checkbox']")).setValue(true);
        }

        /**
         * Set attributes as visible based on specific testData
         *
         * @param testData specific testData
         * @return
         */
        public ManageAttribute visible(TestData testData) {
            TestData td = testData.getTestData("Properties", "Manage Attributes", "Visible");
            td.getKeys().forEach(k -> setSingleAttributeVisible(k, td.getValue(k)));
            return this;
        }

        /**
         * Set attributes as visible based on common testData is set by constructor
         *
         * @return
         */
        public ManageAttribute visible() {
            return visible(commonTestDataForActions);
        }

        public void setSingleAttributeVisible(String key, String value) {
            new ProductFactoryCheckBox(formComponentAttributes, By.xpath(".//h4[.='" + key + "']/ancestor::div[@class='pf-list-item attribute-item']//input[@type='checkbox']"))
                    .setValue(Boolean.parseBoolean(value));
        }

        public ComponentsTab submit() {
            btnDone.click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
            return ComponentsTab.this;
        }

        public class Verify {
            public void visible(TestData testData) {
                Map<String, Boolean> actual = new HashMap<>();
                BrowserController.get().driver().findElements(By.xpath("//form[@id='component-attributes']//div[@class='pf-list-item attribute-item']"))
                        .forEach(we -> actual.put(we.findElement(By.tagName("h4")).getText(), we.findElement(By.xpath(".//input[@type='checkbox']")).isSelected()));

                TestData td = testData.getTestData("Properties", "Manage Attributes", "Visible");
                td.getKeys().forEach(
                        key -> assertThat(actual.get(key)).isEqualTo(Boolean.valueOf(td.getValue(key)))
                );
                btnDone.click();
            }
        }
    }
}
