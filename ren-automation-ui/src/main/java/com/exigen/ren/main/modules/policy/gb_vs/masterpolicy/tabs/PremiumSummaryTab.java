/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.controls.ratingreport.RedistributionCalculatorComboBox;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.Dialog;
import com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PremiumSummaryTabMetaData;
import org.openqa.selenium.By;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY;

public class PremiumSummaryTab extends DefaultTab {
    public static Button buttonRate = new Button(By.xpath("//input[@value = 'Rate' and not(@class = 'hidden') and not(contains(@style,'none'))]"), doubleWaiter);
    public static Button buttonOverrideAndRatePremium = new Button(By.xpath("//input[@value='Override Rate and Premium']"));
    public static Button buttonViewRatingDetails = new Button(By.id("policyDataGatherForm:coverageDefinitionPremiums:0:viewRateDetailsBtn"));
    public static Button buttonMaintainInForceRates = new Button(By.id("policyDataGatherForm:processMaintainInForceRateButton_callRateOverridePopup"));
    public static Button buttonViewRateHistory = new Button(By.id("policyDataGatherForm:callViewRateHistoryPopup"));

    public static Dialog dialogPremiumOverride = new Dialog("//div[@id='policyOverrideInfoForm:masterPolicyPremiumOverridePopup']");
    public static StaticElement labelFinalAnnualPremium = new StaticElement(By.xpath("//div[@id='policyOverrideInfoForm:masterPolicyPremiumOverridePopup']//tr[td[contains(., 'Final Annual Premium')]]/td[2]"));
    public static StaticElement labelTotalPremium = new StaticElement(By.xpath("//table[@id='policyDataGatherForm:totalPremiumAmt']//tr[td[contains(., 'Total Annual Premium')]]/td[2]"));
    public static final TableExtended<TableConstants.PremiumSummaryCoveragesTable> premiumSummaryCoveragesTable = new TableExtended<TableConstants.PremiumSummaryCoveragesTable>("Premium Summary Coverages Table", By.xpath("//div[@id='policyDataGatherForm:coverageDefinitionPremiums']/div/table"));
    public static final TableExtended<TableConstants.PremiumSummaryCoveragesTable> premiumSummaryByPayorCoveragesTable = new TableExtended<>("Premium Summary by Payor", By.xpath("//div[@id='policyDataGatherForm:splitCoverageGroupPremiums']/div/table"));
    public static final TableExtended<TableConstants.PremiumSummaryASOFeeTable> premiumSummaryASOFeeTable = new TableExtended<>("Premium Summary ASO Fee Table", By.xpath("//*[@id='policyDataGatherForm:renAsoFeeBasis']/div/table"));
    public static DialogOverrideCommissionPremiumSummary dialogOverrideCommission = new DialogOverrideCommissionPremiumSummary("//div[@id = 'groupOverrideCommisionPopUp']");
    public static TextBox txtRatingFormula = new TextBox(By.xpath("//input[@id='policyDataGatherForm:sedit_RateDateViewOnlyComponent_ratingFormula']"));

    private static final String AGENCY_SECTION_LINK_LOCATOR = "//a[contains(text(), '%s')]";
    private static final ByT VIEW_RATE_DETAILS = ByT.id("policyDataGatherForm:coverageDefinitionPremiums:%s:viewRateDetailsBtn");
    private static final ByT VIEW_COMMISSION_NAME = ByT.xpath("//*[@id='policyDataGatherForm:componentView_GroupCommissionStrategy']/descendant::a[%s]");

    private static final ByT PROPOSED_ASO_FEE = ByT.id("policyDataGatherForm:renAsoFeeBasis:%s:proposedAsoFeeMoney");
    private static final ByT ASO_FEE_OVERRIDE_REASON = ByT.id("policyDataGatherForm:renAsoFeeBasis:0:asoFeeOverridenReason");

    public PremiumSummaryTab() {
        super(PremiumSummaryTabMetaData.class);
    }

    @Override
    public Tab submitTab() {
        rate();
        buttonNext.click();
        return this;
    }

    public void rate() {
        buttonRate.waitForAccessible(1000);
        buttonRate.click();
    }

    public static Button getCommissionOverrideButtonForAgencyWithCoverage(String agencyName, String coverageName) {
        expandCommissionAgencySection(agencyName);
        return new Button(ByT.xpath(AGENCY_SECTION_LINK_LOCATOR + "/ancestor::div[1]//label[text() = '%s']/ancestor::div[1]/preceding-sibling::input[contains(@id, 'callCommissionsOverride')]").format(agencyName, coverageName));
    }

    public static TableExtended<TableConstants.CommissionsTable> getCommissionTableForAgencySection(String agencyName, String coverageName) {
        expandCommissionAgencySection(agencyName);
        TableExtended<TableConstants.CommissionsTable> tableCommissions = new TableExtended<>("Commissions",
                ByT.xpath(AGENCY_SECTION_LINK_LOCATOR + "/ancestor::div[1]//span[text() = '%s']//ancestor::table[1]").format(agencyName, coverageName));
        tableCommissions.waitForAccessible(1000);
        return tableCommissions;
    }

    @Override
    public String getTabName() {
        return PREMIUM_SUMMARY.get();
    }

    @Override
    public Tab navigate() {
        NavigationPage.PolicyNavigation.leftMenu(this.getTabName(), doubleWaiter);
        return this;
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

    public static class RatingReportView {

        public static final String RATING_REPORT_SECTION_LOCATOR = "//div[@id='ratingReportContent']";
        public static final String PARENT_LOCATOR_MULTIOPTION = "//h3[text()='%s']//following-sibling::div[%s]";
        public static final String RATING_REPORT_TABLE_LOCATOR = "//div[@id='ratingReportContent']//h3[text()='%s']//following-sibling::div[1]//table";
        //Tiers constants
        public static final String EMPLOYEE_ONLY = "Employee only";
        public static final String EMPLOYEE_FAMILY = "Employee + Family";
        public static final String COMPOSITE_RATE = "Composite Rate";
        public static final String ANNUAL_PREMIUM = "Annual Premium";
        public static final String PCT_LOWERCASE_FROM_CURRENT = "% from Current";
        public static final String PCT_LOWERCASE_FROM_RENEWAL = "% from Renewal";

        public static Table asoAdminExpensesTable = new Table(By.xpath(String.format(RATING_REPORT_TABLE_LOCATOR, "ASO")));
        public static Table fullyInsuredAdminExpensesTable = new Table(By.xpath(String.format(RATING_REPORT_TABLE_LOCATOR, "Fully Insured")));
        public static Table areaFactorsTable = new Table(By.xpath(String.format(RATING_REPORT_TABLE_LOCATOR, "Area Factors")));
        public static Table enrolledByStateTable = new Table(By.xpath(String.format(RATING_REPORT_TABLE_LOCATOR, "Enrolled By State")));
        public static Table visionFactorsTable = new Table(By.xpath(String.format(RATING_REPORT_TABLE_LOCATOR, "Vision Factors")));
        public static Link lnkAdminExpenses = new Link(By.xpath(String.format("%s//h2[text()='Admin Expenses']", RATING_REPORT_SECTION_LOCATOR)));
        public static Link lnkRatingDetails = new Link(By.xpath(String.format("%s//h2[text()='Rating Details - ']", RATING_REPORT_SECTION_LOCATOR)));
        public static Link lnkDemographic = new Link(By.xpath(String.format("%s//a[text()='Demographic']", RATING_REPORT_SECTION_LOCATOR)));
        public static Link lnkFactors = new Link(By.xpath(String.format("%s//a[text()='Factors']", RATING_REPORT_SECTION_LOCATOR)));
        public static Link lnkRedistributionCalculator = new Link(By.xpath(String.format("%s//a[text()='Redistribution Calculator']", RATING_REPORT_SECTION_LOCATOR)));
        public static Button btnApplyRates = new Button(By.xpath("//a[text()='APPLY RATES']"), doubleWaiter);
        public static RedistributionCalculatorComboBox cbxRatingDetailsSelectPlan = new RedistributionCalculatorComboBox
                (By.xpath("//h2[text()='Rating Details - ']//parent::div[1]//following-sibling::div//div[@class=' css-2b097c-container']"), doubleWaiter);


        public static void expandAdminExpenses() {
            if (!isAdminExpensesExpanded()) {
                lnkAdminExpenses.click();
            }
        }

        public static void closeAdminExpenses() {
            if (isAdminExpensesExpanded()) {
                lnkAdminExpenses.click();
            }
        }

        public static void expandRatingDetails() {
            if (!isRatingDetailsExpanded()) {
                lnkRatingDetails.click();
            }
        }

        public static void openDemographicSection() {
            expandRatingDetails();
            lnkDemographic.click();
        }

        public static void openFactorsSection() {
            expandRatingDetails();
            lnkFactors.click();
        }

        public static void openRedistributionCalculatorSection() {
            expandRatingDetails();
            lnkRedistributionCalculator.click();
        }

        public static boolean isAdminExpensesExpanded() {
            return fullyInsuredAdminExpensesTable.isPresent() && fullyInsuredAdminExpensesTable.isVisible();
        }

        public static boolean isRatingDetailsExpanded() {
            return lnkDemographic.isPresent() && lnkDemographic.isVisible();
        }

        public static Map<String, String> getAsoData() {
            Map<String, String> asoData = new LinkedHashMap<>();
            asoAdminExpensesTable.getRows().forEach(
                    row -> asoData.put(row.getCell(1).getValue(), row.getCell(2).getValue())
            );
            return asoData;
        }

        public static Map<String, String> getFullyInsuredData() {
            Map<String, String> fullyInsuredData = new LinkedHashMap<>();
            fullyInsuredAdminExpensesTable.getRows().forEach(
                    row -> fullyInsuredData.put(row.getCell(1).getValue(), row.getCell(2).getValue())
            );
            return fullyInsuredData;
        }

        public static Map<String, String> getAreaFactorsData() {
            Map<String, String> areaFactors = new LinkedHashMap<>();
            areaFactorsTable.getRows().forEach(
                    row -> areaFactors.put(row.getCell(1).getValue(), row.getCell(2).getValue())
            );
            return areaFactors;
        }

        public static Map<String, String> getVisionFactorsData() {
            Map<String, String> factors = new LinkedHashMap<>();
            visionFactorsTable.getRows().forEach(
                    row -> factors.put(row.getCell(1).getValue(), row.getCell(2).getValue())
            );
            return factors;
        }

        public static class CalculateRateView {
            public static final ByT REDISTRIBUTION_CALCULATOR_LOCATOR = ByT.xpath("%s//h4[text()='%s']//following-sibling::div[%s]//p[contains(text(),'%s')]//following-sibling::div[1]//div[@class=' css-2b097c-container']");
            public static final ByT SECTION_LOCATOR = ByT.xpath("%s//h4[text()='%s']//following-sibling::div[%s]%s");
            public static final ByT SELECT_PLAN_LOCATOR = ByT.xpath("//p[contains(text(),'%s')]//following-sibling::div[1]//div[@class=' css-2b097c-container']");
            public static final String OVERRIDE_TXT_PATH = "//p[contains(text(),'OVERRIDE')]//following-sibling::input[1]";
            private static final String UW_ADJUSTMENTS = "UW Adjustments";
            //Multi Option
            public static CheckBox chbxMultiOption = new CheckBox(By.xpath("//label[contains(text(),'MULTI-OPTION')]"));
            public static RedistributionCalculatorComboBox cbxSelectPlanFirst = new RedistributionCalculatorComboBox
                    (SELECT_PLAN_LOCATOR.format("SELECT PLAN 1"), doubleWaiter);
            public static RedistributionCalculatorComboBox cbxSelectPlanSecond = new RedistributionCalculatorComboBox
                    (SELECT_PLAN_LOCATOR.format("SELECT PLAN 2"), doubleWaiter);
            public static RedistributionCalculatorComboBox cbxSelectPlanThird = new RedistributionCalculatorComboBox
                    (SELECT_PLAN_LOCATOR.format("SELECT PLAN 3"), doubleWaiter);
            public static Button btnRedistributeMultiOption = new Button(By.xpath("//label[text()='MULTI-OPTION']//following-sibling::div[1]//a[text()='REDISTRIBUTE']"));
            public static RedistributionCalculatorComboBox cbxDualOptionRedistribution = new RedistributionCalculatorComboBox(By.xpath("//label[text()='MULTI-OPTION']//following-sibling::div[1]//p[text()='DUAL OPTION REDISTRIBUTION']//following-sibling::div[1]//div[@class=' css-2b097c-container']"), doubleWaiter);
            //added @disabled for links to check if link disabled due to not valid attributes for UI elements and all links always enabled on CalculateRateView
            public static Link lnkAddPlan = new Link(ByT.xpath("%s//a[contains(text(),'Add Plan') and not (@disabled)]").format(RATING_REPORT_SECTION_LOCATOR));
            public static Link lnkRemovePlan = new Link(ByT.xpath("%s//a[contains(text(),'Remove Plan') and not (@disabled)]").format(RATING_REPORT_SECTION_LOCATOR));
            //UW Adjustments
            public static TextBox txtOverrideFirstUWAdjustment = new TextBox(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "1", OVERRIDE_TXT_PATH), doubleWaiter);
            public static TextBox txtOverrideSecondUWAdjustment = new TextBox(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "2", OVERRIDE_TXT_PATH), doubleWaiter);
            public static RedistributionCalculatorComboBox cbxReasonFirstUWAdjustment = new RedistributionCalculatorComboBox
                    (REDISTRIBUTION_CALCULATOR_LOCATOR.format("", "UW Adjustments", "1", "Reason"), doubleWaiter);
            public static RedistributionCalculatorComboBox cbxReasonSecondUWAdjustment = new RedistributionCalculatorComboBox
                    (REDISTRIBUTION_CALCULATOR_LOCATOR.format("", "UW Adjustments", "2", "Reason"), doubleWaiter);
            public static Button btnOverrideFirstAddNote = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "1", "//a[text()='Add Note']"));
            public static Button btnOverrideSecondAddNote = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "2", "//a[text()='Add Note']"));
            public static TextBox txtOverrideFirstComment = new TextBox(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "1", "//textarea"));
            public static TextBox txtOverrideSecondComment = new TextBox(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "2", "//textarea"));
            public static Button btnOverrideFirstRemoveNote = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "1", "//a[text()='Remove Note']"));
            public static Button btnOverrideSecondRemoveNote = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "2", "//a[text()='Remove Note']"));
            public static Button btnApplyAdjustments = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "3", "//a[text()='APPLY ADJUSTMENTS']"));
            public static Button btnResetAdjustments = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "3", "//a[text()='RESET']"));
            //Redistribution
            public static RedistributionCalculatorComboBox cbxRedistributeRate = new RedistributionCalculatorComboBox
                    (ByT.xpath("//p[contains(text(),'%s')]//following-sibling::div[1]//div[@class=' css-2b097c-container']").format("REDISTRIBUTE RATE"), doubleWaiter);
            public static Button btnRedistributionRates = new Button(SECTION_LOCATOR.format("", "Redistribution", "2", "//a[text()='REDISTRIBUTE RATES']"));
            public static Button btnResetRedistribution = new Button(SECTION_LOCATOR.format("", "Redistribution", "2", "//a[text()='RESET']"));

            public static final ByT BTN_REDISTRIBUTE = ByT.xpath("//h3[text()='%s']//following-sibling::div[2]//a[text()='REDISTRIBUTE']");
        }

        public static TextBox proposedASOFeeByIndex(int index) {
            return new TextBox(PROPOSED_ASO_FEE.format(index - 1));
        }

        public static ComboBox asoFeeOverrideReasonByIndex(int index) {
            return new ComboBox(ASO_FEE_OVERRIDE_REASON.format(index - 1));
        }
    }

    public static void openPremiumSummaryByPayorTable(String planName) {
        premiumSummaryCoveragesTable.getRow(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(), planName).getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName())
                .controls.links.getFirst().click(Tab.doubleWaiter);
    }
}
