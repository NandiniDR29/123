/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.billing.setup_billing_groups.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.utils.RetryService;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.controls.BaseElement;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.ActionTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.modules.billing.setup_billing_groups.metadata.BillingAccountSetupTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.PAYMENTS;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCertificatePoliciesTable.POLICY_NUMBER;
import static com.exigen.ren.main.pages.summary.PolicySummaryPage.tableCertificatePolicies;

public class BillingAccountSetupTab extends ActionTab {
    public static TableExtended<BillingGroups> tableBillingGroups = new TableExtended<>("Billing Groups", By.xpath("//div[@id = 'purchaseForm:newBillingGroupsListTable']//table"));
    public static Button buttonSave = new Button(By.id("purchaseForm:saveAccountTemplateBtn_footer"));

    public BillingAccountSetupTab() {

        super(BillingAccountSetupTabMetaData.class);

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
                for (TestData billingGroup : td.getTestDataList(name)) {
                    if (billingGroup.containsKey("Billing Group Name")) {
                        tableBillingGroups.getRow(BillingGroups.BILLING_GROUP_NAME.getName(), billingGroup.getValue("Billing Group Name")).getCell(BillingGroups.BILLING_GROUP_NAME.getName()).click();
                    }
                    List<TestData> list = new ArrayList<>();
                    list.add(billingGroup);
                    setValue(list);
                    saveTab();
                }
            }

        };
        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab submitTab() {
        Tab.buttonFinish.waitFor(BaseElement::isPresent);
        Tab.buttonFinish.click();
        return this;
    }

    public static void saveTab() {
        try {
            RetryService.run(predicate -> buttonSave.isPresent(),
                    () -> {
                        BrowserController.get().driver().navigate().refresh();
                        return null;
                    }, StopStrategies.stopAfterAttempt(30), WaitStrategies.fixedWait(10, TimeUnit.SECONDS));
        } catch (RuntimeException e) {
            throw new IstfException("Save button is not displayed", e);
        }
        buttonSave.click();
    }

    public enum BillingGroups implements Named {
        BILLING_GROUP("Billing Group #"),
        ASSOCIATED_COVERAGES("Associated Coverages"),
        BILLING_ACCOUNT("Billing Account #"),
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