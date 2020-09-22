/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.tabs;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.controls.ratingreport.ApplyTextBox;
import com.exigen.ipb.eisa.controls.ratingreport.RedistributionCalculatorComboBox;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.webdriver.BrowserController;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.components.Dialog;
import com.exigen.ren.common.components.DialogOverrideCommissionPremiumSummary;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.PolicyConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.rest.rating.model.gb_dn.*;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.exigen.ren.common.enums.NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY;
import static com.exigen.ren.main.enums.RatingReportConstants.AsoAdminCostDentalTable.*;
import static com.exigen.ren.main.enums.RatingReportConstants.*;
import static com.exigen.ren.main.enums.RatingReportConstants.DemographicSection.*;
import static com.exigen.ren.main.enums.RatingReportConstants.FactorsSectionFields.*;
import static com.exigen.ren.main.enums.RatingReportConstants.NetworkDetailsRows.TOTAL;
import static com.exigen.ren.main.enums.RatingReportConstants.NetworkSectionFields.*;
import static com.exigen.ren.main.enums.RatingReportConstants.Networks.PPO;
import static com.exigen.ren.main.enums.RatingReportConstants.NetworksTier.*;
import static com.exigen.ren.main.enums.RatingReportConstants.RedistributionCalculatorTierTable.TIER;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryRatingReportTierColumns.*;

public class PremiumSummaryTab extends DefaultTab {
    public static Button buttonRate = new Button(By.xpath("//input[@value = 'Rate' and not(@class = 'hidden') and not(contains(@style,'none'))]"), doubleWaiter);
    public static Button buttonOverrideAndRatePremium = new Button(By.xpath("//input[@value='Override Rate and Premium']"));
    public static Button buttonMaintainInForceRates = new Button(By.id("policyDataGatherForm:processMaintainInForceRateButton_callRateOverridePopup"));
    public static Button buttonViewRateHistory = new Button(By.id("policyDataGatherForm:callViewRateHistoryPopup"));

    public static Dialog dialogPremiumOverride = new Dialog("//div[@id='policyOverrideInfoForm:masterPolicyPremiumOverridePopup']");
    public static StaticElement labelFinalAnnualPremium = new StaticElement(By.xpath("//div[@id='policyOverrideInfoForm:masterPolicyPremiumOverridePopup']//tr[td[contains(., 'Final Annual Premium')]]/td[2]"));
    public static StaticElement labelTotalPremium = new StaticElement(By.xpath("//table[@id='policyDataGatherForm:totalPremiumAmt']//tr[td[contains(., 'Total Annual Premium')]]/td[2]"));
    public static final TableExtended<TableConstants.PremiumSummaryCoveragesTable> premiumSummaryCoveragesTable = new TableExtended<>("Premium Summary Coverages Table", By.xpath("//div[@id='policyDataGatherForm:coverageDefinitionPremiums']/div/table"));
    public static final TableExtended<TableConstants.PremiumSummaryCoveragesTable> premiumSummaryByPayorCoveragesTable = new TableExtended<>("Premium Summary by Payor", By.xpath("//div[@id='policyDataGatherForm:splitCoverageGroupPremiums']/div/table"));
    public static final TableExtended<PremiumSummaryClassNameTable> premiumSummaryClassNameTable = new TableExtended<>("Coverage-Plan Premium Summary", By.xpath("//div[@id='policyDataGatherForm:classificationGroupPremiums']/div/table"));
    public static final TableExtended<PremiumSummarySubGroupTiersTable> premiumSummarySubGroupTiersTable = new TableExtended<>("Premium Summary Sub Group Tiers", By.xpath("//div[@id='policyDataGatherForm:subGroupTierPremiums']/div/table"));
    public static final TableExtended<TableConstants.PremiumSummaryASOFeeTable> premiumSummaryASOFeeTable = new TableExtended<>("Premium Summary ASO Fee Table", By.xpath("//*[@id='policyDataGatherForm:renAsoFeeBasis']/div/table"));
    public static DialogOverrideCommissionPremiumSummary dialogOverrideCommission = new DialogOverrideCommissionPremiumSummary("//div[@id = 'groupOverrideCommisionPopUp']");
    public static TextBox txtRatingFormula = new TextBox(By.xpath("//input[@id='policyDataGatherForm:sedit_RateDateViewOnlyComponent_ratingFormula']"));

    private static final ByT VIEW_RATE_DETAILS = ByT.id("policyDataGatherForm:coverageDefinitionPremiums:%s:viewRateDetailsBtn");
    private static final ByT VIEW_COMMISSION_NAME = ByT.xpath("//*[@id='policyDataGatherForm:componentView_GroupCommissionStrategy']/descendant::a[%s]");
    private static final String AGENCY_SECTION_LINK_LOCATOR = "//a[contains(text(), '%s')]";

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

    public static void expandCommissionAgencySection(String agencyName) {
        if (new StaticElement(ByT.xpath(AGENCY_SECTION_LINK_LOCATOR + "/parent::h3").format(agencyName)).getAttribute("aria-expanded").equals("false")) {
            new Link(ByT.xpath(AGENCY_SECTION_LINK_LOCATOR).format(agencyName)).click();
        }
    }

    public static TableExtended<TableConstants.CommissionsTable> getCommissionTableForAgencySection(String agencyName, String coverageNameOrPremiumReceivedPerPolicyYear) {
        expandCommissionAgencySection(agencyName);
        TableExtended<TableConstants.CommissionsTable> tableCommissions = new TableExtended<>("Commissions",
                ByT.xpath(AGENCY_SECTION_LINK_LOCATOR + "/ancestor::div[1]//span[text() = '%s']//ancestor::table[1]").format(agencyName, coverageNameOrPremiumReceivedPerPolicyYear));
        tableCommissions.waitForAccessible(1000);
        return tableCommissions;
    }

    public static Button getCommissionOverrideButtonForAgencyWithCoverage(String agencyName, String coverageName) {
        expandCommissionAgencySection(agencyName);
        return new Button(ByT.xpath(AGENCY_SECTION_LINK_LOCATOR + "/ancestor::div[1]//label[text() = '%s']/ancestor::div[1]/preceding-sibling::input[contains(@id, 'callCommissionsOverride')]").format(agencyName, coverageName));
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

    public static Currency getPremium() {
        return new Currency(PremiumSummaryTab.labelTotalPremium.getValue());
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

    public static TextBox proposedASOFeeByIndex(int index) {
        return new TextBox(PROPOSED_ASO_FEE.format(index - 1));
    }

    public static ComboBox asoFeeOverrideReasonByIndex(int index) {
        return new ComboBox(ASO_FEE_OVERRIDE_REASON.format(index - 1));
    }

    public static class RatingReportView {

        public static final String RATING_REPORT_SECTION_LOCATOR = "//div[@id='ratingReportContent']";

        public static Link lnkAdminExpenses = new Link(By.xpath(String.format("%s//h2[text()='Admin Expenses']", RATING_REPORT_SECTION_LOCATOR)));
        public static Link lnkRatingDetails = new Link(By.xpath(String.format("%s//h2[text()='Rating Details - ']", RATING_REPORT_SECTION_LOCATOR)));
        public static Link lnkDemographic = new Link(By.xpath(String.format("%s//a[text()='Demographic']", RATING_REPORT_SECTION_LOCATOR)));
        public static Link lnkFactors = new Link(By.xpath(String.format("%s//a[text()='Factors']", RATING_REPORT_SECTION_LOCATOR)));
        public static Link lnkRedistributionCalculator = new Link(By.xpath(String.format("%s//a[text()='Redistribution Calculator']", RATING_REPORT_SECTION_LOCATOR)));
        //Admin Expenses Section
        public static Table tblASO = new Table(By.xpath("//h3[text()='ASO']/following-sibling::div[1]//table")).applyConfiguration("TableWithoutHeader");
        public static final String APPLY_TEXTBOX_LOCATOR = "//p[text() ='%s']//following-sibling::div[1]//input";
        public static Table tblFullyInsuredAdminExpenses = new Table(By.xpath("//h3[text()='Fully Insured']//following-sibling::div[2]//table")).applyConfiguration("TableWithoutHeader");
        public static ApplyTextBox txtNumberOfClaimsPerEmployee = new ApplyTextBox(ByT.xpath(APPLY_TEXTBOX_LOCATOR).format("NUMBER OF CLAIMS PER EMPLOYEE"));
        public static ApplyTextBox txtUwExceptionDollarAmount = new ApplyTextBox(ByT.xpath(APPLY_TEXTBOX_LOCATOR).format("UW EXCEPTION DOLLAR AMOUNT"));
        public static ApplyTextBox txtRiskAdjustmentPercent = new ApplyTextBox(ByT.xpath(APPLY_TEXTBOX_LOCATOR).format("RISK ADJUSTMENT PERCENT"));
        public static ApplyTextBox txtUwExceptionPercent = new ApplyTextBox(ByT.xpath(APPLY_TEXTBOX_LOCATOR).format("UW EXCEPTION PERCENT"));
        //Demographic Tab
        //Summary section
        private static final ByT SUMMARY_PIE_TOTAL = ByT.xpath("//h4[text()='%s']//following-sibling::div[1]//div[@class='pie-stat']//span");
        private static final ByT SUMMARY_LIST = ByT.xpath("//h4[text()='%s']//following-sibling::ul//li");
        //Area Factors section
        private static final ByT AREA_FACTORS_LOCATOR = ByT.xpath("//h3[text()='Area Factors']//parent::div//following-sibling::div//p[text()='%s']//preceding-sibling::%s");
        public static StaticElement lblIndemnity = new StaticElement(AREA_FACTORS_LOCATOR.format(INDEMNITY, "p"));
        public static StaticElement lblPPO = new StaticElement(AREA_FACTORS_LOCATOR.format(DemographicSection.PPO, "p"));
        public static StaticElement lblPPONetworkPenetration = new StaticElement(AREA_FACTORS_LOCATOR.format(PPO_NETWORK_PENETRATION, "p"));
        public static StaticElement lblEPOAreaFactor = new StaticElement(AREA_FACTORS_LOCATOR.format(EPO_AREA_FACTOR, "p"));
        public static StaticElement lblEPONetworkPenetration = new StaticElement(AREA_FACTORS_LOCATOR.format(EPO_NETWORK_PENETRATION, "p"));
        public static TextBox txtIndemnity = new TextBox(AREA_FACTORS_LOCATOR.format(INDEMNITY, "input"));
        public static TextBox txtPPO = new TextBox(AREA_FACTORS_LOCATOR.format(DemographicSection.PPO, "input"));
        public static TextBox txtPPONetworkPenetration = new TextBox(AREA_FACTORS_LOCATOR.format(PPO_NETWORK_PENETRATION, "div//input"));
        public static TextBox txtEPOAreaFactor = new TextBox(AREA_FACTORS_LOCATOR.format(EPO_AREA_FACTOR, "input"));
        public static TextBox txtEPONetworkPenetration = new TextBox(AREA_FACTORS_LOCATOR.format(EPO_NETWORK_PENETRATION, "div//input"));
        public static Link lnkEditAreaFactors = new Link(By.xpath("//h3[text()='Area Factors']//following-sibling::a[text()='EDIT']"));
        public static Link lnkCancelAreaFactors = new Link(By.xpath("//h3[text()='Area Factors']//parent::div//following-sibling::div//a[text()='CANCEL']"));
        public static Link lnkSaveAreaFactors = new Link(By.xpath("//h3[text()='Area Factors']//parent::div//following-sibling::div//a[text()='SAVE']"));
        //Enrolled By State Section
        private static final ByT TBL_ENROLLED_BY_STATE_LOCATOR = ByT.xpath("//h3[text()='Enrolled By State']//following-sibling::div//table%s");
        public static final ByT TBL_NESTED_ENROLLED_BY_STATE_LOCATOR = ByT.xpath("//h3[text()='Enrolled By State']//following-sibling::div//table//span[text()='%s']//parent::td//parent::tr//following-sibling::tr[1]//table");
        public static Table tblEnrolledByState = new Table(TBL_ENROLLED_BY_STATE_LOCATOR.format("")).applyConfiguration("TableWithoutHeader");
        public static Link lnkExpandAllEnrolledByState = new Link(TBL_ENROLLED_BY_STATE_LOCATOR.format("//label[text()='Expand All']"));
        //Factors Tab
        //Factors
        public static final ByT FACTORS_FIELD_LOCATOR = ByT.xpath("//h3[text()='%s']//following-sibling::div[1]//p[text()='%s']/following-sibling::div[1]%s");
        public static TextBox txtVolLoad = new TextBox(FACTORS_FIELD_LOCATOR.format("Factors", "VOL LOAD", "//input"));
        public static Link lnkApplyFactors = new Link(By.xpath("//h3[text()='Factors']//following-sibling::div[1]//a[text()='Apply']"));
        public static Link lnkResetFactors = new Link(By.xpath("//h3[text()='Factors']//following-sibling::div[1]//a[text()='Reset']"));
        //Network section
        public static final ByT FACTORS_NETWORK_LOCATOR = ByT.xpath("//h3[text()='%s']//following-sibling::div[1]//p[text()='%s']//preceding-sibling::p");
        public static final ByT FACTORS_DETAILS_TABLE_LOCATOR = ByT.xpath("//h3[text()='%s']//following-sibling::div[2]//*[text()='%s']//following-sibling::table[1]");

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
                lnkRatingDetails.click(Waiters.AJAX.then(Waiters.SLEEP(2000)));
            }
        }

        public static void openDemographicSection() {
            expandRatingDetails();
            lnkDemographic.click(Waiters.AJAX.then(Waiters.SLEEP(2000)));
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
            return tblFullyInsuredAdminExpenses.isPresent() && tblFullyInsuredAdminExpenses.isVisible();
        }

        public static boolean isRatingDetailsExpanded() {
            return lnkDemographic.isPresent() && lnkDemographic.isVisible();
        }

        public static AsoAdminCostModel getAsoAdminCostModelFromUI() {
            AsoAdminCostModel asoAdminCostModel = new AsoAdminCostModel();
            asoAdminCostModel.setFullyInsuredPremiumNetComAmt(tblASO.getRow(1, FULLY_INSURED_PREMIUM_NET_COMMISSIONS.getName()).getCell(2).getValue());
            asoAdminCostModel.setFullyInsuredPremiumNetComPct(tblASO.getRow(1, FULLY_INSURED_PREMIUM_NET_COMMISSIONS.getName()).getCell(3).getValue());
            asoAdminCostModel.setAdmExpencesAmt(tblASO.getRow(1, ADMIN_EXPENSES.getName()).getCell(2).getValue());
            asoAdminCostModel.setAdmExpencesPct(tblASO.getRow(1, ADMIN_EXPENSES.getName()).getCell(3).getValue());
            asoAdminCostModel.setClaimsAmt(tblASO.getRow(1, CLAIMS.getName()).getCell(2).getValue());
            asoAdminCostModel.setClaimsPct(tblASO.getRow(1, CLAIMS.getName()).getCell(3).getValue());
            asoAdminCostModel.setRiskAndTaxesAmt(tblASO.getRow(1, RISK_AND_TAXES.getName()).getCell(2).getValue());
            asoAdminCostModel.setRiskAndTaxesPct(tblASO.getRow(1, RISK_AND_TAXES.getName()).getCell(3).getValue());
            asoAdminCostModel.setLostInterestAmt(tblASO.getRow(1, LOST_INTEREST.getName()).getCell(2).getValue());
            asoAdminCostModel.setLostInterestPct(tblASO.getRow(1, LOST_INTEREST.getName()).getCell(3).getValue());
            asoAdminCostModel.setAsoPreCommision(tblASO.getRow(1, ASO_PRE_COMMISSION.getName()).getCell(2).getValue());
            asoAdminCostModel.setAsoPreCommisionPEPM(tblASO.getRow(1, ASO_PRE_COMMISSION_PERM.getName()).getCell(2).getValue());
            asoAdminCostModel.setExeptionPct(tblASO.getRow(1, EXCEPTION.getName()).getCell(2).getValue());
            asoAdminCostModel.setAsoNetCommisions(tblASO.getRow(1, ASO_NET_COMMISSIONS.getName()).getCell(2).getValue());
            asoAdminCostModel.setAsoCommissionPEPMOverride(tblASO.getRow(1, COMMISSION.getName()).getCell(2).getValue());
            asoAdminCostModel.setAsoFinalPEPM(tblASO.getRow(1, FINAL_ASO_PERM.getName()).getCell(2).getValue());
            asoAdminCostModel.setAsoFinal(tblASO.getRow(1, FINAL_ASO_TOTAL.getName()).getCell(2).getValue());
            asoAdminCostModel.setAsoEquivalency(tblASO.getRow(1, ASO_EQUVALENCY_TOTAL.getName()).getCell(2).getValue());
            return asoAdminCostModel;
        }


        public static List<AdminCostsModel> getFullyInsuredDataFromUI() {
            List<AdminCostsModel> adminCostsModelsFromUI = new ArrayList<>();
            tblFullyInsuredAdminExpenses.getColumn(FullyInsuredTable.DESCRIPTION.getName()).getValue().forEach(
                    description -> {
                        AdminCostsModel model = new AdminCostsModel();
                        model.setAdminCategory(tblFullyInsuredAdminExpenses.getRow(FullyInsuredTable.DESCRIPTION.getName(), description).getCell(FullyInsuredTable.DESCRIPTION.getName()).getValue());
                        model.setAnnualAmt(tblFullyInsuredAdminExpenses.getRow(FullyInsuredTable.DESCRIPTION.getName(), description).getCell(FullyInsuredTable.ANNUAL_AMOUNT.getName()).getValue());
                        model.setPremiumPct(tblFullyInsuredAdminExpenses.getRow(FullyInsuredTable.DESCRIPTION.getName(), description).getCell(FullyInsuredTable.PCT_OF_PREMIUM.getName()).getValue());
                        model.setPerClaimAmt(tblFullyInsuredAdminExpenses.getRow(FullyInsuredTable.DESCRIPTION.getName(), description).getCell(FullyInsuredTable.PER_CLAIM.getName()).getValue());
                        model.setPerEmployeeAmt(tblFullyInsuredAdminExpenses.getRow(FullyInsuredTable.DESCRIPTION.getName(), description).getCell(FullyInsuredTable.PER_EMPLOYEE.getName()).getValue());
                        adminCostsModelsFromUI.add(model);
                    });
            if (adminCostsModelsFromUI.isEmpty()) {
                throw new IstfException("Fully Insured table is empty");
            }
            return adminCostsModelsFromUI;
        }

        public static String getTotalByAgeFromSummarySection() {
            return new StaticElement(SUMMARY_PIE_TOTAL.format("BY AGE")).getValue();
        }

        public static String getTotalByEnrollmentFromSummarySection() {
            return new StaticElement(SUMMARY_PIE_TOTAL.format("BY ENROLLMENT")).getValue();
        }

        public static Map<String, String> getByAgeDataFromSummarySection() {
            Map<String, String> result = new HashMap<>();
            List<WebElement> webElementList = BrowserController.get().driver().findElements(SUMMARY_LIST.format("BY AGE"));
            webElementList.forEach(
                    webElement -> result.put(webElement.findElement(By.xpath(".//span[1]")).getText(), webElement.findElement(By.xpath(".//span[2]")).getText()));
            return result;
        }

        public static Map<String, String> getByEnrollmentDataFromSummarySection() {
            Map<String, String> result = new HashMap<>();
            BrowserController.get().driver().findElements(SUMMARY_LIST.format("BY ENROLLMENT")).forEach(
                    webElement -> result.put(webElement.findElement(By.xpath(".//span[1]")).getText(), webElement.findElement(By.xpath(".//span[2]")).getText()));
            return result;
        }

        public static List<AreaFactorsModel> getAreaFactorsDataAreaTierFromUI() {
            List<AreaFactorsModel> areaFactorsModels = new ArrayList<>();
            List<WebElement> areaFactorsNamesElements = BrowserController.get().driver().findElements(
                    By.xpath("//h3[text()='Area Factors']/parent::div//following-sibling::div[1]//ul/li"));
            //create AreaFactorsModel list with correct size and set 'Area' for each model
            areaFactorsNamesElements.stream().skip(1).forEach(
                    areaFactorNameElement -> {
                        AreaFactorsModel areaFactorsModel = new AreaFactorsModel();
                        areaFactorsModel.setArea(areaFactorNameElement.getText());
                        areaFactorsModels.add(areaFactorsModel);
                    }
            );
            //set other attributes for created 'Area Factors' models
            ByT areaFactorsAttribute = ByT.xpath("//p[text()='%s']/parent::div/parent::div//following-sibling::div/div");
            ImmutableList.of(INDEMNITY, DemographicSection.PPO, PPO_NETWORK_PENETRATION, AREA_FACTOR_BLEND).forEach(
                    areaFactorAttribute -> {
                        List<WebElement> areaFactorAttributeElements = BrowserController.get().driver().findElements(areaFactorsAttribute.format(areaFactorAttribute));
                        IntStream.range(0, areaFactorAttributeElements.size()).forEach(i -> {
                            if (INDEMNITY.equals(areaFactorAttribute)) {
                                areaFactorsModels.get(i).setIndemnityFactor(areaFactorAttributeElements.get(i).getText());
                            }
                            if (DemographicSection.PPO.equals(areaFactorAttribute)) {
                                areaFactorsModels.get(i).setPPOFactor(areaFactorAttributeElements.get(i).getText());
                            }
                            if (PPO_NETWORK_PENETRATION.equals(areaFactorAttribute)) {
                                areaFactorsModels.get(i).setPPOPenetrationFactor(areaFactorAttributeElements.get(i).getText());
                            }
                            if (AREA_FACTOR_BLEND.equals(areaFactorAttribute)) {
                                areaFactorsModels.get(i).setAreaFactorBlend(areaFactorAttributeElements.get(i).getText());
                            }
                        });
                    }
            );
            return areaFactorsModels;
        }

        public static Map<String, String> getFactorsDataFromUI() {
            Map<String, String> result = new HashMap<>();
            ImmutableList.of(INITIAL_LOAD, OCCUPATION, OCCUPATION_CODE, WAIT_GRP_SIZE, PLAN_UTIL,
                    GROUP_SIZE, CAL_YR_LOAD, VOL_LOAD, WAIT_LOAD, ROLLOVER_LOAD,
                    PREVENTIVE, RADIOGRAPHS, MAJOR_RESTORATIVE, OTHER_CLASS_SECOND, PROSTHODONTICS).forEach(
                    fieldName -> result.put(fieldName, getValueFromFactorsSectionField(FACTORS, fieldName)));
            return result;
        }

        public static String getValueFromFactorsSectionField(String section, String fieldName) {
            if (!fieldName.equals(VOL_LOAD)) {
                return new StaticElement(FACTORS_FIELD_LOCATOR.format(section, fieldName, "")).getValue();
            }
            return txtVolLoad.getValue();
        }

        public static Map<String, String> getNetworkCommonDataFromUI(String networkSection) {
            Map<String, String> result = new HashMap<>();
            ImmutableList.of(AREA, TREND, MAXIMUM_EE_AND_SP, MAXIMUM_CH, PREVENTIVE,
                    RADIOGRAPHS, MAJOR_RESTORATIVE, OTHER_CLASS_SECOND, PROSTHODONTICS).forEach(
                    fieldName -> result.put(fieldName, getValueFromFactorsSectionField(networkSection, fieldName)));
            if (PPO.equals(networkSection)) { //put STEERAGE data that exist only for PPO
                result.put(STEERAGE, getValueFromFactorsNetworkSectionField(networkSection, STEERAGE));
            }
            ImmutableList.of(NETWORK_PENETRATION, NET_UTILIZATION).forEach(
                    fieldName -> result.put(fieldName, getValueFromFactorsNetworkSectionField(networkSection, fieldName)));
            return result;
        }

        public static String getValueFromFactorsNetworkSectionField(String section, String fieldName) {
            return new StaticElement(FACTORS_NETWORK_LOCATOR.format(section, fieldName)).getValue();
        }

        public static List<EmployeeDetailsModel> getNetworkEmloyeeDetailsDataFromUI(String networkSection) {
            List<EmployeeDetailsModel> employeeDetailsModelsFromUI = new ArrayList<>();
            Table tblDetails = new TableExtended<NetworkDetailsTable>("Emloyee Details", FACTORS_DETAILS_TABLE_LOCATOR.format(networkSection, EMPLOYEE)).applyConfiguration("TableWithoutHeaderAndBody");
            ImmutableList.of(NetworkDetailsRows.PREVENTIVE, NetworkDetailsRows.RADIOGRAPHS,
                    NetworkDetailsRows.MAJOR_RESTORATIVE, NetworkDetailsRows.OTHER_CLASS_SECOND, NetworkDetailsRows.PROSTODONTICS).forEach(
                    rowName -> {
                        EmployeeDetailsModel model = new EmployeeDetailsModel();
                        Row row = tblDetails.getRow(NetworkDetailsTable.BENEFIT.getName(), rowName);
                        model.setBenefitCategory(row.getCell(NetworkDetailsTable.BENEFIT.getName()).getValue());
                        model.setBase(row.getCell(NetworkDetailsTable.BASE.getName()).getValue());
                        model.setDeductibleCredit(row.getCell(NetworkDetailsTable.DED_CREDIT.getName()).getValue());
                        model.setDeductibleUtil(row.getCell(NetworkDetailsTable.DED_UTIL.getName()).getValue());
                        model.setClaimsRate(row.getCell(NetworkDetailsTable.ONE_YEAR_CLAIMS_RATE.getName()).getValue());
                        model.setRateCalc(row.getCell(NetworkDetailsTable.ONE_YEAR_WEIGHTED.getName()).getValue());
                        employeeDetailsModelsFromUI.add(model);
                    });
            return employeeDetailsModelsFromUI;
        }

        public static String getNetworkDetailsOneYearClaimsRateTotalValue(String networkSection, String subSection) {
            Table tbl = new Table(FACTORS_DETAILS_TABLE_LOCATOR.format(networkSection, subSection)).applyConfiguration("TableWithoutHeaderAndBody");
            if (EMPLOYEE.equals(subSection)) {
                return tbl.getRow(NetworkDetailsTable.BENEFIT.getName(), TOTAL).getCell(NetworkDetailsTable.ONE_YEAR_CLAIMS_RATE.getName()).getValue();
            }
            return tbl.getRow(6).getCell(5).getValue();
        }

        public static String getNetworkDetailsOneYearWeightedTotalValue(String networkSection, String subSection) {
            Table tbl = new Table(FACTORS_DETAILS_TABLE_LOCATOR.format(networkSection, subSection)).applyConfiguration("TableWithoutHeaderAndBody");
            if (EMPLOYEE.equals(subSection)) {
                return tbl.getRow(NetworkDetailsTable.BENEFIT.getName(), TOTAL).getCell(NetworkDetailsTable.ONE_YEAR_WEIGHTED.getName()).getValue();
            }
            return tbl.getRow(6).getCell(6).getValue();
        }

        public static List<SpouseDetailsModel> getNetworkSpouseDetailsDataFromUI(String networkSection) {
            List<SpouseDetailsModel> spouseDetailsModelsFromUI = new ArrayList<>();
            Table tblDetails = new TableExtended<NetworkDetailsTable>("Spouse Details", FACTORS_DETAILS_TABLE_LOCATOR.format(networkSection, SPOUSE)).applyConfiguration("TableWithoutHeaderAndBody");
            IntStream.range(1, tblDetails.getRowsCount()).forEach(i -> {
                SpouseDetailsModel model = new SpouseDetailsModel();
                model.setBenefitCategory(tblDetails.getRow(i).getCell(1).getValue());
                model.setBase(tblDetails.getRow(i).getCell(2).getValue());
                model.setDeductibleCredit(tblDetails.getRow(i).getCell(3).getValue());
                model.setDeductibleUtil(tblDetails.getRow(i).getCell(4).getValue());
                model.setClaimsRate(tblDetails.getRow(i).getCell(5).getValue());
                model.setRateCalc(tblDetails.getRow(i).getCell(6).getValue());
                spouseDetailsModelsFromUI.add(model);
            });
            if (spouseDetailsModelsFromUI.isEmpty()) {
                throw new IstfException("Spouse Details table is empty");
            }
            return spouseDetailsModelsFromUI;
        }

        public static List<ChildDetailsModel> getNetworkChildDetailsDataFromUI(String networkSection) {
            List<ChildDetailsModel> childDetailsModelsFromUI = new ArrayList<>();
            Table tblDetails = new TableExtended<NetworkDetailsTable>("Child Details", FACTORS_DETAILS_TABLE_LOCATOR.format(networkSection, CHILD)).applyConfiguration("TableWithoutHeaderAndBody");
            IntStream.range(1, tblDetails.getRowsCount()).forEach(i -> {
                ChildDetailsModel model = new ChildDetailsModel();
                model.setBenefitCategory(tblDetails.getRow(i).getCell(1).getValue());
                model.setBase(tblDetails.getRow(i).getCell(2).getValue());
                model.setDeductibleCredit(tblDetails.getRow(i).getCell(3).getValue());
                model.setDeductibleUtil(tblDetails.getRow(i).getCell(4).getValue());
                model.setClaimsRate(tblDetails.getRow(i).getCell(5).getValue());
                model.setRateCalc(tblDetails.getRow(i).getCell(6).getValue());
                childDetailsModelsFromUI.add(model);
            });
            if (childDetailsModelsFromUI.isEmpty()) {
                throw new IstfException("Child Details table is empty");
            }
            return childDetailsModelsFromUI;
        }

        public static class CalculateRateView {
            public static final ByT REDISTRIBUTION_CALCULATOR_LOCATOR = ByT.xpath("%s//h4[text()='%s']//following-sibling::div[%s]//p[contains(text(),'%s')]//following-sibling::div[1]//div[@class=' css-2b097c-container']");
            public static final ByT SECTION_LOCATOR = ByT.xpath("%s//h4[text()='%s']//following-sibling::div[%s]%s");
            public static final ByT MULTI_OPTION_COMBOBOX_LOCATOR = ByT.xpath("//p[contains(text(),'%s')]//following-sibling::div[1]//div[@class=' css-2b097c-container']");
            public static final String OVERRIDE_TXT_PATH = "//p[contains(text(),'OVERRIDE')]//following-sibling::input[1]";
            public static final String ADD_NOTE_PATH = "//a[text()='Add Note']";
            public static final String UW_ADJUSTMENTS = "UW Adjustments";
            //Multi Option
            public static CheckBox chbxMultiOption = new CheckBox(By.xpath("//label[contains(text(),'MULTI-OPTION')]"));
            public static RedistributionCalculatorComboBox cbxSelectPlanFirst = new RedistributionCalculatorComboBox
                    (MULTI_OPTION_COMBOBOX_LOCATOR.format("SELECT PLAN 1"), doubleWaiter);
            public static RedistributionCalculatorComboBox cbxSelectPlanSecond = new RedistributionCalculatorComboBox
                    (MULTI_OPTION_COMBOBOX_LOCATOR.format("SELECT PLAN 2"), doubleWaiter);
            public static RedistributionCalculatorComboBox cbxSelectPlanThird = new RedistributionCalculatorComboBox
                    (MULTI_OPTION_COMBOBOX_LOCATOR.format("SELECT PLAN 3"), doubleWaiter);
            public static RedistributionCalculatorComboBox cbxDualOptionRedistribution = new RedistributionCalculatorComboBox
                    (MULTI_OPTION_COMBOBOX_LOCATOR.format("DUAL OPTION REDISTRIBUTION"), doubleWaiter);
            public static Button btnRedistributeMultiOption = new Button(By.xpath("//label[text()='MULTI-OPTION']//following-sibling::div[1]//a[text()='REDISTRIBUTE']"));
            //added @disabled for links to check if link disabled due to not valid attributes for UI elements and all links always enabled on CalculateRateView
            public static Link lnkAddPlan = new Link(ByT.xpath("%s//a[contains(text(),'Add Plan') and not (@disabled)]").format(RATING_REPORT_SECTION_LOCATOR));
            public static Link lnkRemovePlan = new Link(ByT.xpath("%s//a[contains(text(),'Remove Plan') and not (@disabled)]").format(RATING_REPORT_SECTION_LOCATOR));
            //UW Adjustments
            public static TextBox txtOverrideFirstUWAdjustment = new TextBox(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "1", OVERRIDE_TXT_PATH), doubleWaiter);
            public static TextBox txtOverrideSecondUWAdjustment = new TextBox(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "2", OVERRIDE_TXT_PATH), doubleWaiter);
            public static TextBox txtOverrideThirdUWAdjustment = new TextBox(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "3", OVERRIDE_TXT_PATH), doubleWaiter);
            public static RedistributionCalculatorComboBox cbxReasonFirstUWAdjustment = new RedistributionCalculatorComboBox
                    (REDISTRIBUTION_CALCULATOR_LOCATOR.format("", "UW Adjustments", "1", "Reason"), doubleWaiter);
            public static RedistributionCalculatorComboBox cbxReasonSecondUWAdjustment = new RedistributionCalculatorComboBox
                    (REDISTRIBUTION_CALCULATOR_LOCATOR.format("", "UW Adjustments", "2", "Reason"), doubleWaiter);
            public static RedistributionCalculatorComboBox cbxReasonThirdUWAdjustment = new RedistributionCalculatorComboBox
                    (REDISTRIBUTION_CALCULATOR_LOCATOR.format("", "UW Adjustments", "3", "Reason"), doubleWaiter);
            public static Button btnOverrideFirstAddNote = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "1", ADD_NOTE_PATH));
            public static Button btnOverrideSecondAddNote = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "2", ADD_NOTE_PATH));
            public static Button btnOverrideThirdAddNote = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "3", ADD_NOTE_PATH));
            public static TextBox txtOverrideFirstComment = new TextBox(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "1", "//textarea"));
            public static TextBox txtOverrideSecondComment = new TextBox(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "2", "//textarea"));
            public static TextBox txtOverrideThirdComment = new TextBox(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "3", "//textarea"));
            public static Button btnOverrideFirstRemoveNote = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "1", "//a[text()='Remove Note']"));
            public static Button btnOverrideSecondRemoveNote = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "2", "//a[text()='Remove Note']"));
            public static Button btnApplyAdjustments = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "4", "//a[text()='APPLY ADJUSTMENTS']"));
            public static Button btnResetAdjustments = new Button(SECTION_LOCATOR.format("", UW_ADJUSTMENTS, "4", "//a[text()='RESET']"));
            //Redistribution
            public static final ByT REDISTRIBUTION_RATE_LOCATOR = ByT.xpath("//p[contains(text(),'%s')]//following-sibling::div[1]//div[@class=' css-2b097c-container']");
            public static RedistributionCalculatorComboBox cbxRedistributeRate = new RedistributionCalculatorComboBox
                    (REDISTRIBUTION_RATE_LOCATOR.format("REDISTRIBUTE RATE"), doubleWaiter);
            public static Button btnRedistributionRates = new Button(SECTION_LOCATOR.format("", "Redistribution", "2", "//a[text()='REDISTRIBUTE RATES']"));
            public static Button btnResetRedistribution = new Button(SECTION_LOCATOR.format("", "Redistribution", "2", "//a[text()='RESET']"));
            //Tier section
            public static final ByT AREA_TIER_TABLE_LOCATOR = ByT.xpath("//h3[text()='%s']//following-sibling::table");
            public static Table tblTierCompositeRateAndAnnualPremium = new Table(By.xpath("//td[text()='Composite Rate']/ancestor::table[1]")).applyConfiguration("TableWithoutHeaderAndBody");
            public static final ByT BTN_REDISTRIBUTE = ByT.xpath("//h3[text()='%s']//following-sibling::div[2]//a[text()='REDISTRIBUTE']");

            public static List<TierRateModel> getTierRateModelsFromRedistributionCalculatorTab() {
                List<TierRateModel> tierRateModelsList = new ArrayList<>();
                //create tables and set data from them to models
                BrowserController.get().driver().findElements(By.xpath("//div[@class='plan-table']//h3[@class='reporting-h3']")).forEach(we -> {
                    String area = we.getText();
                    Table tbl = new Table(AREA_TIER_TABLE_LOCATOR.format(area)).applyConfiguration("TableWithoutHeaderAndBody");
                    tbl.getColumn(TIER.getName()).getValue().forEach(
                            tierName -> {
                                TierRateModel tierRateModel = new TierRateModel();
                                tierRateModel.setArea(area);
                                tierRateModel.setTier(tierName);
                                tierRateModel.setAdjManualRate(tbl.getRow(TIER.getName(), tierName).getCell(ADJUSTED_MANUAL_RATES.getName()).getValue());
                                tierRateModel.setAdjFormulaRate(tbl.getRow(TIER.getName(), tierName).getCell(ADJUSTED_FORMULA_RATES.getName()).getValue());
                                tierRateModel.setProposedRate(tbl.getRow(TIER.getName(), tierName).getCell(REDISTRIBUTED_RATES.getName()).getValue());
                                tierRateModelsList.add(tierRateModel);
                            });
                });
                if (tierRateModelsList.isEmpty()) {
                    throw new IstfException("'Tier' table on 'Redistribution Calculator' tab is empty");
                }
                return tierRateModelsList;
            }

            public static List<Table> getListOfAreaTierTables() {
                List<Table> result = new ArrayList<>();
                BrowserController.get().driver().findElements(By.xpath("//h3[@class='reporting-h3']"))
                        .forEach(we -> result.add(new Table(AREA_TIER_TABLE_LOCATOR.format(we.getText())).applyConfiguration("TableWithoutHeaderAndBody")));
                return result;
            }
        }

    }

    public enum PremiumSummaryClassNameTable implements Named {
        CLASS_NAME("Class Name"),
        MODAL_PREMIUM("Modal Premium");

        private String name;

        PremiumSummaryClassNameTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PremiumSummarySubGroupTiersTable implements Named {
        COVERAGE_TIER_NAME("Coverage Tier Name"),
        MODAL_PREMIUM("Modal Premium");

        private String name;

        PremiumSummarySubGroupTiersTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static void openPremiumSummaryByPayorTable(String planName) {
        premiumSummaryCoveragesTable.getRow(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName(),planName).getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName())
                .controls.links.getFirst().click(Tab.doubleWaiter);
    }

    public static void openPremiumSummaryClassNameTable(String payor) {
        premiumSummaryByPayorCoveragesTable.getRow(PolicyConstants.PolicyCoverageSummaryTable.PAYOR, payor).getCell(TableConstants.PremiumSummaryCoveragesTable.COVERAGE_NAME.getName())
                .controls.links.getFirst().click(Tab.doubleWaiter);
    }

    public static void openPremiumSummarySubGroupTable(String className) {
        premiumSummaryClassNameTable.getRow(PremiumSummaryClassNameTable.CLASS_NAME.getName(), className).getCell(PremiumSummaryClassNameTable.CLASS_NAME.getName())
                .controls.links.getFirst().click(Tab.doubleWaiter);
    }


}
