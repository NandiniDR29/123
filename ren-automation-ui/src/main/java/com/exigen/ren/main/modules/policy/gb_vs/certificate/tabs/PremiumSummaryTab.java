/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.policy.gb_vs.certificate.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_vs.certificate.metadata.PremiumSummaryTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY;

public class PremiumSummaryTab extends DefaultTab {
    public static Button buttonRate = new Button(By.xpath("//input[@value = 'Rate' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
    public static TableExtended<PremiumSummary> tablePremiumSummary = new TableExtended<>("Premium Summary", By.xpath("//div[@id='policyDataGatherForm:certificatePremiumSummaryTable']//table"));
    public static TableExtended<TableConstants.AsoFeeTable> tableASOFee = new TableExtended<>("ASO Fee Table", (By.xpath("//div[@id='policyDataGatherForm:renAsoFeeBasis']//table")));

    public PremiumSummaryTab() {
        super(PremiumSummaryTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonRate.click();
        buttonNext.click();
        return this;
    }

    @Override
    public String getTabName() {
        return PREMIUM_SUMMARY.get();
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName());
        return this;
    }

    public enum PremiumSummary implements Named {
        PLAN_NAME("Plan Name"),
        COVERAGE_NAME("Coverage Name"),
        CONTRIBUTION_TYPE("Contribution Type"),
        CONTRIBUTION("Contribution %"),
        INSUREDS("Insureds"),
        BENEFIT_AMOUNT("Benefit Amount"),
        RATE_BASIS("Rate Basis"),
        RATE("Rate"),
        ANNUAL_PREMIUM("Annual Premium"),
        PAYMENT_MODE("Payment Mode"),
        MODAL_PREMIUM("Modal Premium");

        private String name;

        PremiumSummary(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
