/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.account.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.admin.modules.cms.printmonitor.pages.PrintMonitorPage;
import com.exigen.ren.admin.modules.cms.printmonitor.printdocs.tabs.PrintDocsTab;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingAccountSetupTab;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;

public class BillingAccountTab extends DefaultTab {

    public static Link linkManageInvocingCalendars = new Link(By.linkText("Manage Invoicing Calendars"));
    public static Button billingAccountGeneralOptions = new Button(By.xpath("//*[@id='purchaseForm:billingAccountDetailsCollapsablePanel']/legend/span"));
    public static Button buttonSave = new Button(By.id("purchaseForm:saveAccountTemplateBtn_footer"));
    public static Button addToAll =  new Button(By.id("purchaseForm:addToAll"));
    public static TableExtended<BillingAccountSetupTab.BillingGroups> tableBillingGroups = new TableExtended<>("Billing Groups", By.xpath("//div[@id = 'purchaseForm:newBillingGroupsListTable']//table"));


    public BillingAccountTab() {
        super(BillingAccountTabMetaData.class);
    }


    public static void expandBillingAccountGeneralOptions(){
        billingAccountGeneralOptions.waitForPageUpdate();
        billingAccountGeneralOptions.waitForAccessible(60000);
        if (new StaticElement(By.xpath("//*[@id='purchaseForm:billingAccountDetailsCollapsablePanel_collapsed']")).getAttribute("value").equals("true")) {
            billingAccountGeneralOptions.click();
        }
    }

    @Override
    public Tab fillTab(TestData td) {
        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {

            @Override
            protected void selectSection(int index) {}

            @Override
            protected void setSectionValue(int index, TestData value) {

                if (td.getTestDataList(name).stream().anyMatch(testData -> testData.containsKey("Billing Group Name Key"))) {
                    for (TestData plan : td.getTestDataList(name)) {
                        if (plan.containsKey("Billing Group Name Key") && tableBillingGroups.isPresent()) {
                            tableBillingGroups.getRow(BillingAccountSetupTab.BillingGroups.BILLING_GROUP_NAME.getName(), plan.getValue("Billing Group Name Key")).getCell(1).click();
                        }
                        super.setSectionValue(index, value);
                        saveTab();
                    }
                }
                else {
                    BillingAccountSetupTab.tableBillingGroups.getRow(index + 1).getCell(BillingAccountSetupTab.BillingGroups.BILLING_GROUP.getName()).click();
                    super.setSectionValue(index, value);
                    saveTab();
                }
            }


            @Override
            protected void addSection(int index, int size) {

            }
        };

        assetList.setName(this.getClass().getSimpleName());
        assetList.fill(td);
        return this;
    }

    public static void saveTab() {
        if (!buttonSave.isPresent()) {
            try {
                RetryService.run(predicate -> buttonSave.isPresent(),
                        () -> {
                            BrowserController.get().driver().navigate().refresh();
                            return null;
                        }, StopStrategies.stopAfterAttempt(30), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
            } catch (RuntimeException e) {
                throw new IstfException("Save button is not displayed", e);
            }
        }
        buttonSave.click();
    }



    @Override
    public Tab submitTab() {
        Tab.buttonFinish.waitFor(20000, BaseElement::isPresent);
        if(!Tab.buttonFinish.isEnabled() && BillingAccountSetupTab.tableBillingGroups.getRowsCount() > 1) {
            addToAll.click();
        }
        Tab.buttonFinish.waitFor(BaseElement::isEnabled);
        Tab.buttonFinish.click();
        return this;
    }
}
