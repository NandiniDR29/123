/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory.policy.tabs;

import com.exigen.ipb.eisa.controls.productfactory.custom.PFButton;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.ElementWaiter;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.admin.modules.product.productfactory.DefaultTab;
import com.exigen.ren.admin.modules.product.productfactory.metadata.ProductMetaData;
import org.openqa.selenium.By;

/**
 * @author ikisly
 */
public class HomeTab extends DefaultTab {

    public static PFButton btnActivate = new PFButton(By.xpath("//button[@title='Activate']"));
    public static PFButton btnDeactivate = new PFButton(By.xpath("//button[@title='Deactivate']"));
    public static PFButton buttonAddRootEntity = new PFButton(By.id("roots:addNewRootEntity"));
    public static PFButton buttonActivateAnyWay = new PFButton(By.id("lookupErrorForm:deploy-anyway"));
    public Table tableRootEntity = new Table(By.id("roots:productRootEntitiesTable"));


    private RootConfiguration rootConfiguration = new RootConfiguration();

    public RootConfiguration rootConfiguration() {
        return rootConfiguration;
    }

    public HomeTab deactivateProduct() {
        open();
        if (btnDeactivate.isPresent() && btnDeactivate.isVisible()) {
            btnDeactivate.click();
        }
        return this;
    }

    public HomeTab activateProduct() {
        open();
        btnActivate.click(Waiters.DEFAULT.then(Waiters.SLEEP(500)));
        if (buttonActivateAnyWay.isPresent() && buttonActivateAnyWay.isVisible()) {
            buttonActivateAnyWay.click();
        }
        return this;
    }

    @Override
    protected String tabName() {
        return "Home";
    }

    public class RootConfiguration {
        public AssetList assetListProductRoot = new AssetList(By.xpath("//*"), ProductMetaData.HomeTab.class);
        public Button btnSave = new Button(By.id("entityConfigurationCreationForm:create"));
        public Button btnRemoveConfirm = new Button(By.id("entityConfigurationRemovelForm:submitEntityConfigurationRemoval"));

        public RootConfiguration configure(int row) {
            tableRootEntity.getRow(row).getCell(5).controls.links.get("Configure").click();
            return this;
        }

        public RootConfiguration eliminate(String columnName, String cellValueInColumn) {
            tableRootEntity.getRowContains(columnName, cellValueInColumn).getCell(5).controls.links.get("Eliminate").click();
            btnRemoveConfirm.click();
            return this;
        }

        public AssetList getAssetListProductRoot() {
            return assetListProductRoot;
        }

        public HomeTab save() {
            btnSave.click();
            return HomeTab.this;
        }

        public RootConfiguration addRootEntry() {
            buttonAddRootEntity.click(new ElementWaiter(btnSave));
            return this;
        }

        public RootConfiguration fillRoot(TestData td) {
            assetListProductRoot.fill(td);
            return this;
        }

        public HomeTab fillRootAndSave(TestData td) {
            assetListProductRoot.fill(td);
            return save();
        }
    }
}
