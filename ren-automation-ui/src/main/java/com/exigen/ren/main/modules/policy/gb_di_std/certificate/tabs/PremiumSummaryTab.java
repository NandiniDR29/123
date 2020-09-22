/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_di_std.certificate.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.policy.gb_di_std.certificate.metadata.PremiumSummaryTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY;

public class PremiumSummaryTab extends DefaultTab {
    public static Button buttonRate = new Button(By.xpath("//input[@value = 'Rate' and not(@class = 'hidden') and not(contains(@style,'none'))]"));
    public static TableExtended<PremiumSummary> tablePremiumSummary = new TableExtended<>("Premium Summary", By.xpath("//div[@id='policyDataGatherForm:certificatePremiumSummaryTable']//table"));

    public static StaticElement labelTotalPremiumCp = new StaticElement(By.xpath("//table[@id='totalPremiumAmount']//tr[td[contains(., 'Total Annual Premium')]]/td[2]"));

    public PremiumSummaryTab() {
        super(PremiumSummaryTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        buttonRate.click();
        buttonNext.click();
        return this;
    }

    public enum PremiumSummary implements Named {
        PLAN("Plan"),
        COVERAGE_NAME("Coverage Name"),
        CONTRIBUTION_TYPE("Contribution Type"),
        CONTRIBUTION("Contribution %"),
        INSUREDS("Insureds"),
        BENEFIT_AMOUNT("Benefit Amount"),
        RATE_BASIS("Rate Basis"),
        RATE("Rate"),
        ANNUAL_PREMIUM("Annual Premium"),
        PAYOR("Payor"),
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

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.expand(PREMIUM_SUMMARY.get(), this.getTabName());
        return this;
    }

    @Override
    public String getTabName() {
        return PREMIUM_SUMMARY.get();
    }
}
