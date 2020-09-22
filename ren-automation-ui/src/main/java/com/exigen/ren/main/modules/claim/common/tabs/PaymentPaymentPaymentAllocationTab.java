/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.common.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.data.TestData;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.composite.assets.MultiAssetList;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;

import java.util.List;

import static com.exigen.ren.common.enums.NavigationEnum.ClaimPaymentsTab.PAYMENT_ALLOCATION;
import static com.exigen.ren.main.modules.claim.common.metadata.PaymentPaymentPaymentAllocationTabMetaData.*;

public class PaymentPaymentPaymentAllocationTab extends DefaultTab {

    public static Button buttonAddCoverage = new Button(By.id("policyDataGatherForm:addPrecClaimsPaymentDistribution"));
    public static Button buttonPostPayment = new Button(By.xpath("//input[@value='Post Payment']"));
    public static Button buttonUpdatePayment = new Button(By.xpath("//input[@value='Update Payment']"));
    public static TableExtended<CoverageLimits> tableCoverageLimits = new TableExtended<>("Coverage Limits",
            By.xpath("//div[@id='policyDataGatherForm:componentView_ClaimsDistributionCoverageLimits']//table"));
    public static TableExtended<TableConstants.Empty> tableListOfPaymentAllocations = new TableExtended<>("List of Payment Allocations",
            By.xpath("//div[@id='policyDataGatherForm:dataGatherView_ListPrecClaimsPaymentDistribution']/div/table"));

    public PaymentPaymentPaymentAllocationTab() {
        super(PaymentPaymentPaymentAllocationTabMetaData.class);

        assetList = new MultiAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), metaDataClass) {

            @Override
            protected void addSection(int i, int i1) {
            }

            @Override
            protected void selectSection(int index) {
            }

            @Override
            public void fill(TestData td) {
                List<TestData> allocationList = td.getTestDataList(name);
                for (int i = 0; i < allocationList.size(); i++) {
                    TestData allocation = allocationList.get(i);
                    if (allocation.containsKey(assetList.getAsset(ASSOCIATED_INSURABLE_RISK.getLabel()).getName())) {
                        if (allocation.containsKey(assetList.getAsset(BENEFIT.getLabel()).getName())) {
                            assetList.getAsset(BENEFIT).setValue(allocation.getValue(assetList.getAsset(BENEFIT.getLabel()).getName()));
                        }
                        setAssociatedInsurableRiskByPartialValue(allocation.getValue(assetList.getAsset(ASSOCIATED_INSURABLE_RISK.getLabel()).getName()));
                        setValue(ImmutableList.of(allocation
                                .mask(TestData.makeKeyPath(assetList.getAsset(BENEFIT.getLabel()).getName()))
                                .mask(TestData.makeKeyPath(assetList.getAsset(ASSOCIATED_INSURABLE_RISK.getLabel()).getName()))
                                .mask(TestData.makeKeyPath(assetList.getAsset(COVERAGE.getLabel()).getName()))));
                        if (i < (allocationList.size() - 1)) {
                            buttonAddCoverage.click();
                        }
                    }
                    else {
                        setValue(ImmutableList.of(allocation));
                        if (i < (allocationList.size() - 1)) {
                            buttonAddCoverage.click();
                        }
                    }
                }
            }
        };

        assetList.setName(this.getClass().getSimpleName());
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return PAYMENT_ALLOCATION.get();
    }

    public enum CoverageLimits implements Named {
        LIMIT_LEVEL("Limit Level"),
        LIMIT_AMOUNT("Limit Amount"),
        LIMIT_INDEMNITY_INCURRED_AMOUNT("Limit Indemnity Incurred Amount"),
        LIMIT_REMAINING_AMOUNT("Limit Remaining Amount"),
        BENEFIT_AMOUNT_LEVEL("Benefit Amount Level"),
        BENEFIT_AMOUNT("Benefit Amount"),
        BENEFIT_INDEMNITY_INCURRED_AMOUNT("Benefit Indemnity Incurred Amount"),
        BENEFIT_LIMIT_REMAINING_AMOUNT("Benefit Limit Remaining Amount"),
        NOTES("Notes");

        private String name;

        CoverageLimits(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public void setAssociatedInsurableRiskByPartialValue(String associatedInsurableRisk) {
        int count = assetList.getAsset(COVERAGE).getAllValues().size();
        while (!(assetList.getAsset(ASSOCIATED_INSURABLE_RISK.getLabel()).getValue().toString().contains(associatedInsurableRisk)) && (count > 0)) {
            assetList.getAsset(COVERAGE).setValueByIndex(count - 1);
            count--;
        }
    }

    public void setSingleBenefitCalculationNumber(String number) {
        int count = assetList.getAsset(COVERAGE).getAllValues().size();
        while (!(assetList.getAsset(SINGLE_BENEFIT_CALCULATION_NUMBER_LABEL.getLabel()).getValue().toString().equals(number)) && (count > 0)) {
            assetList.getAsset(COVERAGE).setValueByIndex(count - 1);
            count--;
        }
    }
}