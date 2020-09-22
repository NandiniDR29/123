/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common.tabs.master;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingGroupsTab;
import com.exigen.ren.main.modules.policy.common.metadata.master.BillingGroupsActionTabMetaData;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

import static com.exigen.ren.main.enums.ActionConstants.REMOVE;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class BillingGroupsActionTab extends ActionTab {

    public static final TableExtended<BillingGroupsTab.BillingGroups> tableBillingGroups = new TableExtended<>("Billing Groups", By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListGroupBillingGroup']/div/table"));

    public BillingGroupsActionTab() {
        super(BillingGroupsActionTabMetaData.class);
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
                        tableBillingGroups.getRow(BillingGroupsTab.BillingGroups.BILLING_GROUP_NAME.getName(), plan.getValue("Group")).getCell(12).controls.links.get(ActionConstants.CHANGE).click();
                    }
                    List<TestData> list = new ArrayList<>();
                    list.add(plan);
                    setValue(list);
                    if (plan.containsKey("Remove") && tableBillingGroups.isPresent()) {
                        if(plan.containsKey("Group")) {
                            tableBillingGroups.getRow(BillingGroupsTab.BillingGroups.BILLING_GROUP_NAME.getName(), plan.getValue("Group")).getCell(12).controls.links.get(REMOVE).click();
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
        Tab.buttonTopSave.click();
        buttonNext.click();
        return this;
    }
}
