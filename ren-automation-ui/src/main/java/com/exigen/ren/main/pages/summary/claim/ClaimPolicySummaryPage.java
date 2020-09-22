/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.pages.summary.claim;

import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.pages.summary.SummaryPage;
import org.openqa.selenium.By;

public class ClaimPolicySummaryPage extends SummaryPage {

    private static String baseLabelXpath = "//label[text()='%s:']/..//span[contains(@id, 'claimsDatagatherForm:headerEntry')]";

    public static StaticElement labelClaimStatus = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.CLAIM_STATUS)));
    public static StaticElement labelClaimInsured = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.INSURED)));
    public static StaticElement labelClaimPatient = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.PATIENT)));
    public static StaticElement labelClaimPolicyStatus = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.POLICY_STATUS)));
    public static StaticElement labelClaimPolicyNumber = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.POLICY_NUMBER)));
    public static StaticElement labelClaimPolicyPlan = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.POLICY_PLAN)));
    public static StaticElement labelClaimTypeOfClaim = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.TYPE_OF_CLAIM)));
    public static StaticElement labelClaimPolicyProduct = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.POLICY_PRODUCT)));
    public static StaticElement labelClaimTotalIncurred = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.TOTAL_INCURRED)));
    public static StaticElement labelClaimSpecialHandling = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.SPECIAL_HANDLING)));



    public static Table tableGeneralPolicyInformation = new Table(By.id("policyDataGatherForm:policyInfo"));
    public static Table tableRiskItemTree = new Table(By.xpath("//div[@id='policyDataGatherForm:availableRiskItemTreeTable']//table"));
    public static Table tablePolicyParties = new Table(By.id("policyDataGatherForm:body_availableParties"));
    public static Table tableParticipantTtemTree= new Table(By.xpath("//div[@id='policyDataGatherForm:availableRiskItemTreeTable0']//table"));
    public static Table tableDependentsTtemTree = new Table(By.xpath("//div[@id='policyDataGatherForm:availableRiskItemTreeTable1']//table"));
    public static StaticElement labelPolicyEffectiveDate = new StaticElement(By.id("policyDataGatherForm:policyEffectiveDate"));
    public static StaticElement labelPolicyExpirationDate = new StaticElement(By.id("policyDataGatherForm:policyExpirationDate"));
    public static StaticElement labelInceptionDate = new StaticElement(By.id("policyDataGatherForm:inceptionDate"));
    public static StaticElement policySummaryUnderwritingCompany = new StaticElement(By.id("policyDataGatherForm:underwritingCompany"));

    public static void expandPolicyInsurableRisksTable() {
        Link collapsedPolicyInsurableRisksTable = new Link(By.xpath("//div[@id='policyDataGatherForm:availableRiskItemTogglePanel:header']" +
                "//td[@class='rf-cp-ico']"));
        if (collapsedPolicyInsurableRisksTable.isPresent()) {
            collapsedPolicyInsurableRisksTable.click();
        }
    }

    public static void expandInsurableRiskItem(int row) {
        Link collapsedAvailableRiskItem =
                new Link(By.xpath(String.format("//tr[@id='policyDataGatherForm:availableRiskItemTreeTable_node_%s']" +
                        "//span[contains(@class, 'ui-treetable-toggler')]", row)));
        if (collapsedAvailableRiskItem.isPresent()) {
            collapsedAvailableRiskItem.click();
        }
    }


    public static void expandCoverage(int row) {
        Link collapsedAvailableRiskItem =
                new Link(By.xpath(String.format("//tr[@id='policyDataGatherForm:availableRiskItemTreeTable_node_%s_0']" +
                        "//span[contains(@class, 'ui-treetable-toggler')]", row)));
        if (collapsedAvailableRiskItem.isPresent()) {
            collapsedAvailableRiskItem.click();
        }
    }

    public static void expandCoverage(String coverageName) {
        Link collapsedAvailableRiskItem =
                new Link(By.xpath(String.format("//tr[contains(@id, 'policyDataGatherForm:availableRiskItemTreeTable_node_0') "
                        + "and ./td[contains(., '%s')]]//span[contains(@class, 'ui-treetable-toggler')]", coverageName)));
        if (collapsedAvailableRiskItem.isPresent()) {
            collapsedAvailableRiskItem.click();
        }
    }
}
