/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.admin.modules.product.productfactory.pages;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.admin.modules.common.pages.AdminPage;
import com.exigen.ren.admin.modules.product.productfactory.metadata.ProductMetaData;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.enums.TableConstants;
import org.openqa.selenium.By;

import static com.exigen.ren.main.enums.ProductConstants.ProductTable.CHANGE_EXPIRATION;
import static com.exigen.ren.main.enums.ProductConstants.ProductTable.PRODUCT_CODE;

public class ProductProductFactoryPage extends AdminPage {
    public static AssetList assetListSearchForm = new AssetList(By.id("searchForm"), ProductMetaData.ProductProductFactorySearch.class);
    public static Button buttonAddNewProduct = new Button(By.id("searchForm:addbtn"));
    public static Button buttonDeleteProduct = new Button(By.id("searchForm:deleteBtn"));
    public static TableExtended<TableConstants.Empty> tableProducts = new TableExtended<>("Products", By.id("searchForm:body_foundProducts"), By.id("searchForm:body_foundProducts:dataScrollerfoundProducts"));
    public static Button buttonSearch = new Button(assetListSearchForm, By.id("searchForm:search"));

    public static void search(TestData td) {
        assetListSearchForm.fill(td);
        buttonSearch.click();
    }

    public static void searchAndOpen(TestData td) {
        search(td);
        selectProduct(td);
    }

    public static void selectProduct(TestData td) {
        tableProducts.getRow(PRODUCT_CODE, td.getValue(ProductMetaData.ProductProductFactorySearch.class.getSimpleName(), "Product Code"))
                .getCell(ProductConstants.ProductTable.PRODUCT_NAME).controls.links.getFirst().click();
    }

    public static void expire(TestData td) {
        tableProducts.getRow(PRODUCT_CODE, td.getValue(ProductMetaData.ProductProductFactorySearch.class.getSimpleName(), PRODUCT_CODE))
                .getCell(CHANGE_EXPIRATION).controls.links.getFirst().click();
    }
}
