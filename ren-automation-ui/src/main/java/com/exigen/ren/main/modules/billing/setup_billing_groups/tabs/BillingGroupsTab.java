/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.setup_billing_groups.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.billing.setup_billing_groups.metadata.BillingGroupsTabMetaData;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

import static com.exigen.ren.main.enums.ActionConstants.REMOVE;

public class BillingGroupsTab extends ActionTab {

    public static final TableExtended<BillingGroups> tableBillingGroups = new TableExtended<>("Billing Groups", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListGroupBillingGroup']/div/table"));

    public BillingGroupsTab() {
        super(BillingGroupsTabMetaData.class);

        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {

            @Override
            protected void addSection(int index, int size) {}

            @Override
            protected void selectSection(int index) {}

            @Override
            public void fill(TestData td) {
                if (td.containsKey(name)) {
                    fillSection(td);
                }
            }

            private void fillSection(TestData td) {
                for (TestData plan : td.getTestDataList(name)) {
                    if (plan.containsKey("Group") && tableBillingGroups.isPresent()) {
                        tableBillingGroups.getRow(BillingGroups.BILLING_GROUP_NAME.getName(), plan.getValue("Group")).getCell(12).controls.links.get(ActionConstants.CHANGE).click();
                    }
                    List<TestData> list = new ArrayList<>();
                    list.add(plan);
                    setValue(list);
                    if (plan.containsKey("Remove") && tableBillingGroups.isPresent()) {
                        if(plan.containsKey("Group")) {
                            tableBillingGroups.getRow(BillingGroups.BILLING_GROUP_NAME.getName(), plan.getValue("Group")).getCell(12).controls.links.get(REMOVE).click();
                        }
                        else {
                            tableBillingGroups.getRow(1).getCell(12).controls.links.get(REMOVE).click();
                        }

                        Page.dialogConfirmation.confirm();
                    }
                }
            }
        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() {
        buttonTopSave.click();
        Tab.buttonNext.click();
        Page.dialogConfirmation.buttonYes.click();
        return this;
    }

    public enum BillingGroups implements Named {
        COVERAGE("Coverage"),
        BILLING_GROUP_NAME("Billing Group Name");

        private String name;

        BillingGroups(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

}
