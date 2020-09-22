/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package com.exigen.ren.admin.modules.billing.billingcycle.cancellations.benefits.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.admin.modules.billing.billingcycle.cancellations.benefits.metadata.CancellationsBenefitsGeneralMetaData;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import org.openqa.selenium.By;

public class CancellationsBenefitsTab extends DefaultTab {

    public static final Button buttonAdd = new Button(By.id("CancellationsListForm:addButton_footer"));
    public static final TableExtended tableBenefits = (TableExtended) new TableExtended("Benefits table", By.xpath("//*[@id='CancellationsListForm:allCancellations']//table")).applyConfiguration("NoRecordsFound");
    public static final Button buttonAddCommunicationAndCancelNotices = new Button(By.id("cancelForm:addButton"));

    public CancellationsBenefitsTab() {
        super(CancellationsBenefitsGeneralMetaData.class);
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {
            @Override
            protected void addSection(int index, int size) {
                buttonAdd.click();
            }

            @Override
            protected void selectSection(int index) {
            }


            @Override
            protected void setSectionValue(int index, TestData value) {
                super.setSectionValue(index, value);
                buttonSave.click();
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() { return this;}
}