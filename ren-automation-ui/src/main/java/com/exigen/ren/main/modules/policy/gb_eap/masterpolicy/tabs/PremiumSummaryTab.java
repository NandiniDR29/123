/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.Dialog;
import com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_eap.masterpolicy.metadata.PremiumSummaryTabMetaData;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY;

public class PremiumSummaryTab extends DefaultTab {
    public static Button buttonRate = new Button(By.xpath("//input[@value = 'Rate' and not(@class = 'hidden') and not(contains(@style,'none'))]"), doubleWaiter);
    public static Button buttonOverrideAndRatePremium = new Button(By.xpath("//input[@value='Override Rate and Premium']"));
    public static Button buttonMaintainInForceRates = new Button(By.id("policyDataGatherForm:processMaintainInForceRateButton_callRateOverridePopup"));

    public static Dialog dialogPremiumOverride = new Dialog("//div[@id='policyOverrideInfoForm:masterPolicyPremiumOverridePopup']");
    public static DialogOverrideCommissionPremiumSummary dialogOverrideCommision = new DialogOverrideCommissionPremiumSummary("//div[@id = 'groupOverrideCommisionPopUp']");

    public static StaticElement labelFinalAnnualPremium = new StaticElement(By.xpath("//div[@id='policyOverrideInfoForm:masterPolicyPremiumOverridePopup']//tr[td[contains(., 'Final Annual Premium')]]/td[2]"));
    public static StaticElement labelTotalPremium = new StaticElement(By.xpath("//table[@id='policyDataGatherForm:totalPremiumAmt']//tr[td[contains(., 'Total Annual Premium')]]/td[2]"));
    public static TextBox txtRatingFormula = new TextBox(By.xpath("//input[@id='policyDataGatherForm:sedit_RateDateViewOnlyComponent_ratingFormula']"));

    private static final ByT VIEW_RATE_DETAILS = ByT.id("policyDataGatherForm:coverageDefinitionPremiums:%s:viewRateDetailsBtn");
    private static final ByT VIEW_COMMISSION_NAME = ByT.xpath("//*[@id='policyDataGatherForm:componentView_GroupCommissionStrategy']/descendant::a[%s]");
    private static final String AGENCY_SECTION_LINK_LOCATOR = "//a[contains(text(), '%s')]";

    public static final TableExtended<TableConstants.PremiumSummaryCoveragesTable> premiumSummaryCoveragesTable =
            new TableExtended<>("Premium Summary Coverages Table",
                    By.xpath("//div[@id='policyDataGatherForm:coverageDefinitionPremiums']/div/table"));
    public static final TableExtended<TableConstants.PremiumSummaryCoveragesTable> premiumSummaryByPayorCoveragesTable =
            new TableExtended<>("Premium Summary by Payor",
                    By.xpath("//div[@id='policyDataGatherForm:splitCoverageGroupPremiums']/div/table"));
    public static final TableExtended<PremiumSummaryClassNameTable> premiumSummaryClassNameTable =
            new TableExtended<>("Coverage-Plan Premium Summary",
                    By.xpath("//div[@id='policyDataGatherForm:classificationGroupPremiums']/div/table"));
    public static final TableExtended<PremiumSummarySubGroupTable> premiumSummarySubGroupTable =
            new TableExtended<>("Premium Summary Sub Group",
                    By.xpath("//div[@id='policyDataGatherForm:subGroupPremiums']/div/table"));

    public PremiumSummaryTab() {
        super(PremiumSummaryTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        rate();
        buttonNext.click();
        return this;
    }

    public Tab rate() {
        buttonRate.waitForAccessible(2000);
        buttonRate.click();
        return this;
    }

    public enum PremiumSummaryClassNameTable implements Named {
        CLASS_NAME("Class Name");

        private String name;

        PremiumSummaryClassNameTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PremiumSummarySubGroupTable implements Named {
        SUB_GROUP_NAME("Sub-Group Name"),
        MODAL_PREMIUM("Modal Premium");

        private String name;

        PremiumSummarySubGroupTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName(), doubleWaiter);
        return this;
    }

    @Override
    public String getTabName() {
        return PREMIUM_SUMMARY.get();
    }

    public static void openOverrideAndRatePremiumPopUp() {
        Waiters.AJAX.go();
        buttonOverrideAndRatePremium.click();
    }

    public void openViewRateDetailsByPlanName(String planName) {
        int index = premiumSummaryCoveragesTable.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), planName).getIndex();
        new Button(VIEW_RATE_DETAILS.format(index - 1)).click();
    }

    public static Button rateDetailsButton(String planName) {
        int index = premiumSummaryCoveragesTable.getRowContains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), planName).getIndex();
        return new Button(VIEW_RATE_DETAILS.format(index - 1));
    }

    public static Button getCommissionOverrideButtonForAgencyWithCoverage(String agencyName, String coverageName) {
        expandCommissionAgencySection(agencyName);
        return new Button(ByT.xpath(AGENCY_SECTION_LINK_LOCATOR + "/ancestor::div[1]//label[text() = '%s']/ancestor::div[1]/preceding-sibling::input[contains(@id, 'callCommissionsOverride')]").format(agencyName, coverageName));
    }

    public static void expandCommissionAgencySection(String agencyName) {
        if (new StaticElement(ByT.xpath(AGENCY_SECTION_LINK_LOCATOR + "/parent::h3").format(agencyName)).getAttribute("aria-expanded").equals("false")) {
            new Link(ByT.xpath(AGENCY_SECTION_LINK_LOCATOR).format(agencyName)).click();
        }
    }

    public static String getCommissionName(int index) {
        return new StaticElement(VIEW_COMMISSION_NAME.format(index)).getValue();
    }

    public static String getCommissionTypeByAgency(String agencyName, String coverage) {
        PremiumSummaryTab.expandCommissionAgencySection(agencyName);
        StaticElement commissionType = new StaticElement(ByT.xpath(AGENCY_SECTION_LINK_LOCATOR + "/ancestor::div[1]//label[text() = '%s']//preceding-sibling::label[contains(@id, 'commissionRateInfoType')]").format(agencyName, coverage));
        return commissionType.getValue();
    }

    public static TableExtended<TableConstants.AgencyCommission> getCommissionTable(String agencyName, String coverage) {
        PremiumSummaryTab.expandCommissionAgencySection(agencyName);
        TableExtended<TableConstants.AgencyCommission> agencyCommissionsTable = new TableExtended<>("Agency Commissions Table",
                ByT.xpath(AGENCY_SECTION_LINK_LOCATOR + "/ancestor::div[1]//label[text() = '%s']/following-sibling::div[1]//table").format(agencyName, coverage));
        agencyCommissionsTable.waitForAccessible(1000);
        return agencyCommissionsTable;
    }

    public static TableExtended<TableConstants.CommissionsTable> getCommissionTableForAgencySection(String agencyName, String coverageNameOrPremiumReceivedPerPolicyYear) {
        expandCommissionAgencySection(agencyName);
        TableExtended<TableConstants.CommissionsTable> tableCommissions = new TableExtended<>("Commissions",
                ByT.xpath(AGENCY_SECTION_LINK_LOCATOR + "/ancestor::div[1]//span[text() = '%s']//ancestor::table[1]").format(agencyName, coverageNameOrPremiumReceivedPerPolicyYear));
        tableCommissions.waitForAccessible(1000);
        return tableCommissions;
    }
}
