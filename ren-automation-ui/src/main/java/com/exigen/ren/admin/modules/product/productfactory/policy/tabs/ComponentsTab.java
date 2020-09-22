/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory.policy.tabs;

import com.exigen.ipb.eisa.controls.productfactory.ProductFactoryCheckBox;
import com.exigen.ipb.eisa.controls.productfactory.custom.*;
import com.exigen.ipb.eisa.waiters.AngularLoadingWaiter;
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
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomAssertions.assertThat;

public class ComponentsTab extends DefaultTab {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentsTab.class);
    public static PFButton btnSave = new PFButton(By.id("persistence:save"));
    public static String connectLinkTemplate = "//*[(self::span or self::h4) and contains(@id,'componentName') and .='%s']/ancestor::div[@class='pf-list-item']//a[.='Connect']";
    public AssetList assetListProperties = new AssetList(By.xpath("//*"), ProductMetaData.ComponentsTab.class);
    private PFButton btnDone = new PFButton(By.xpath("//button[.='Done']"));

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

    public AssetList getAssetListProperties() {
        return assetListProperties;
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

        public CustomizeColumns customizeColumns() {
            return new CustomizeColumns();
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
            return updateComponent(testData, true);
        }

        public Relation updateComponent(TestData testData, boolean submit) {
            selectNode(testData);
            ComponentsTab.this.updateComponent(testData);
            if (submit) {
                submit();
            }
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
        Waiters.SLEEP(500).go();
        PFLink link = new PFLink(By.xpath("//h3[.='" + path.get(path.size() - 1) + "']"));
        new LocatorWaiter(By.className("pf-ui-loading-mask pf-ui-loading-mask-visible"), 240000, false).go();
        new AngularLoadingWaiter().go();
        link.waitForAccessible(3000);
        LOGGER.info(String.format("Loading mask is present? %s", new StaticElement(By.xpath("//div[@class='pf-ui-loading-mask pf-ui-loading-mask-visible']")).isPresent()));
        try {
            link.click(Waiters.DEFAULT.then(Waiters.SLEEP(1000)));
        } catch (WebDriverException ignore) {
        }
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
            Waiters.SLEEP(1000).go();
            btnSave.click();
        }
    }

    /**
     * Class for managing column customization
     */
    public class CustomizeColumns {
        PFLink linkCustomize = new PFLink(By.id("instance-table:manage-columns"));
        StaticElement formInstanceListColumns = new StaticElement(By.xpath("//form[@id='component-columns']"));
        PFButton buttonAddColumn = new PFButton(formInstanceListColumns, By.xpath(".//button[@id='component-columns:add']"));
        PFButton buttonDone = new PFButton(formInstanceListColumns, By.xpath(".//button[.='Done']"));

        StaticElement formAddColumn = new StaticElement(By.xpath("//form[@id='component-column']"));
        PFButton buttonAdd = new PFButton(formAddColumn, By.id("component-column:save"));
        PFButton buttonCancel = new PFButton(formAddColumn, By.xpath(".//button[.='Cancel']"));
        AssetList assetListColumn = new AssetList(By.xpath("//*"), ProductMetaData.ComponentsTab.CustomizeColumns.class);


        public CustomizeColumns() {
            linkCustomize.click();
        }

        public CustomizeColumns addColumns(List<TestData> testDataList) {
            testDataList.forEach(this::addColumn);
            return this;
        }

        public CustomizeColumns addColumn(TestData td) {
            buttonAddColumn.click(new ElementWaiter(buttonAdd).then(Waiters.SLEEP(1000)));
            assetListColumn.fill(td);
            buttonAdd.click();
            return this;
        }

        public CustomizeColumns cancel() {
            buttonCancel.click();
            return this;
        }

        public void submit() {
            this.buttonDone.click();
            ComponentsTab.this.submit();
        }

        public CustomizeColumns deleteColumn(String label) {
            new PFButton(By.xpath("//div[@class='pf-list-item' and contains(., '" + label + "')]//button")).click();
            return this;
        }

        public List<String> getColumnList() {
            return formInstanceListColumns.getWebElement().findElements(By.tagName("a")).stream()
                    .map(WebElement::getText).collect(Collectors.toList());
        }

        public CustomizeColumns updateColumn(TestData td) {
            String label = td.getValue("CustomizeColumns", "Label");
            new PFButton(By.xpath("//div[@class='pf-list-item' and contains(., '" + label + "')]//a"))
                    .click(new ElementWaiter(buttonAdd).then(Waiters.SLEEP(1000)));
            assetListColumn.fill(td);
            buttonAdd.click();
            return this;
        }
    }

    /**
     * Class for managing attributes of node
     */
    public class ManageAttribute {
        PFButton btnManageAttributes = new PFButton(By.id("manage-attributes:manage"));

        StaticElement formComponentAttributes = new StaticElement(By.xpath("//form[@id='component-attributes']"));
        PFButton btnAddNewAttribute = new PFButton(formComponentAttributes, By.id("component-attributes:create"));
        PFButton btnDone = new PFButton(formComponentAttributes, By.xpath("//button[.='Done']"));
        PFButton btnAddComponentAttribute = new PFButton(formComponentAttributes, By.id("component-attributes:add"));

        StaticElement formAddAttributeForm = new StaticElement(By.xpath("//form[@id='attribute-add']"));
        PFButton btnCancel = new PFButton(formAddAttributeForm, By.xpath(".//button[.='Cancel']"));
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
            AngularLoadingWaiter.get().go();
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
            btnAddNewAttribute.click(new ElementWaiter(txAttributeName).then(Waiters.SLEEP(1000)));

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
                new PFButton(formComponentAttributes, By.xpath(".//h4[.='" + attributeName + "']/ancestor::div[@class='pf-list-item attribute-item']//div[@class='pf-list-item-action']//button"), Waiters.SLEEP(3000))
                        .click();
                BrowserController.get().driver().switchTo().alert().accept();
            } catch (UnhandledAlertException e) {
                BrowserController.get().driver().switchTo().alert().accept();
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

        public Map<String, String> getAllAttributes() {
            Map<String, String> map = BrowserController.get().driver().findElements(By.xpath("//div[@class='pf-list-item-content has-action']")).stream()
                    .collect(Collectors.toMap(x -> x.findElement(By.xpath(".//a")).getText(), o -> o.findElement(By.xpath(".//h4")).getText()));
            btnDone.click();
            return map;
        }

        public ManageAttribute update(TestData td) {
            td.getTestDataList("Properties", "Manage Attributes", "Update")
                    .forEach(this::updateAttribute);
            return this;
        }

        public void updateAttribute(TestData testData) {
            new PFLink(By.xpath(String.format("//div[@class='pf-list-item-content has-action' and h4[.='%s']]//a",
                    testData.getValue("Attribute name")))).click();
            assetListProperties.fill(testData.mask("Attribute name"));
            btnSave.click();
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
            ((JavascriptExecutor) BrowserController.get().driver()).executeScript("arguments[0].click()",
                    new ProductFactoryCheckBox(formAddAttributeForm, By.xpath(".//h4[.='" + attributeName + "']/ancestor::div[@class='pf-list-item']//input[@type='checkbox']")).getWebElement());
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

        public PFButton getBtnAddComponentAttribute() {
            return btnAddComponentAttribute;
        }


        public PFButton getBtnCancel() {
            return btnCancel;
        }

        /**
         * @return true when there is at least one attribute to add
         */
        public boolean isAttributeToAddExist() {
            return btnAddAttribute.isPresent();
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
