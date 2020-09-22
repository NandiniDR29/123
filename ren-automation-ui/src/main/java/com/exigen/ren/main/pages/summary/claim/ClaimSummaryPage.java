/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.pages.summary.claim;

import com.exigen.ipb.eisa.controls.ActivitiesAndUserNotes;
import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.istf.webdriver.controls.waiters.Waiters;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.pages.summary.SummaryPage;
import org.openqa.selenium.By;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClaimSummaryPage extends SummaryPage {
    private static String baseLabelXpath = "//label[text()='%s:']/..//span[contains(@id, 'producContextInfoForm:headerCell')]";

    public static StaticElement labelClaimNumber = new StaticElement(By.xpath("//div[@id='producContextInfoForm:header_panel' or @id='claimsDatagatherForm:header_panel']/div[@class='header']/div"));
    public static StaticElement labelClaimStatus = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.CLAIM_STATUS)));
    public static StaticElement labelClaimStatusFull = new StaticElement(By.id("producContextInfoForm:j_id_24_3c_p_2_e_1_6:content"));
    public static StaticElement labelClaimPatient = new StaticElement(By.xpath(String.format(baseLabelXpath, "Patient")));
    public static StaticElement labelClaimWithoutPolicy = new StaticElement(By.id("producContextInfoForm:headerCell_2_0"));
    public static StaticElement labelClaimWithPolicy = new StaticElement(By.id("producContextInfoForm:headerLink_2_0"));
    public static StaticElement labelClaimWithCertificatePolicy = new StaticElement(By.id("producContextInfoForm:headerLink_3_0"));
    public static StaticElement labelSpecialHandling = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.SPECIAL_HANDLING)));
    public static StaticElement labelDateOfLoss = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.DATW_OF_LOSS)));
    public static StaticElement labelPolicyStatus = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.POLICY_STATUS)));
    public static StaticElement labelTypeOfClaim = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.TYPE_OF_CLAIM)));
    public static StaticElement labelInsuredName = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.INSURED_NAME)));
    public static StaticElement labelTotalIncurred = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.TOTAL_INCURRED)));
    public static StaticElement labelUnderwritingCompany = new StaticElement(By.xpath(String.format(baseLabelXpath, ClaimConstants.ClaimProductInfoTable.UNDERWRITING_COMPANY)), Waiters.DEFAULT);
    public static StaticElement headerUnderwritingCompany = new StaticElement(By.id("producContextInfoForm:j_id_24_3c_s_a_e_1_6:content"));

    public static ActivitiesAndUserNotes activitiesAndUserNotes = new ActivitiesAndUserNotes();
    public static Table tableLossEvent =
            new Table(By.xpath("//div[@id='productConsolidatedViewForm:scolumn_LossEvent' or @id='productConsolidatedViewForm:scolumn_PrecCLLossEvent']//table"));
    public static Table tableClaimParties = new Table(By.id("productConsolidatedViewForm:body_scolumn_ClaimsConsolidatedParty"));
    public static Table tableClaimSponsor = new Table(By.id("productConsolidatedViewForm:body_scolumn_PrecClaimsPolicySponsor"));
    public static Table tableClaimParticipant = new Table(By.id("productConsolidatedViewForm:body_scolumn_PrecClaimsPolicyRiskParticipantParty"));
    public static TableExtended<TableConstants.LifeAndDisabilityClaims> tableListOfNonDentalClaims =
            new TableExtended<>("Life & Disability Claims", By.xpath("//div[@id='claimList:claimNonDentalListTable']//table"));
    public static TableExtended<TableConstants.DentalClaims> tableListOfDentalClaims =
            new TableExtended<>("Dental Claims", By.xpath("//div[@id='claimList:claimDentalListTable']//table"));
    public static Table tableClaimCreationResult = new Table(By.xpath("//div[@id='policyDataGatherForm:claimsCreationResultsTable_ClaimsCreationResults']//table"));
    public static Table tableSubrogationInformation = new Table(By.id("productConsolidatedViewForm:body_scolumn_SubrogationSummary"));
    public static Table tableResponsibleParties = new Table(By.id("productConsolidatedViewForm:body_scolumn_ClaimsSubrogationResponsiblePartyView"));
    public static TableExtended<TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended> tableSummaryOfClaimPaymentsAndRecoveries =
            new TableExtended<>("Payments And Recoveries", By.xpath("//div[@id='productConsolidatedViewForm:scolumn_ClaimsFinancialRecords']//table"));
    public static TableExtended<TableConstants.ClaimSummaryOfPaymentSeriesTableExtended> tableSummaryOfClaimPaymentSeries =
            new TableExtended<>("Payment Series", By.xpath("//div[@id='productConsolidatedViewForm:scolumn_ClaimsPaymentSeriesRecordsSummary']//table"));

    public static Table tableBeneficiaryPayments = new Table(By.xpath("//div[@id='policyDataGatherForm:featurePaymentsInfoTable_ClaimsFeaturePayments']//table"));
    public static Table tableSingleBenefitCalculationReserveHistory = new Table(By.xpath("//div[@id='policyDataGatherForm:componentInstancesTable_PrecClaimsTransactionHistory']//table"));
    public static TableExtended<TableConstants.ClaimSummaryClaimDataTableExtended> tableClaimData =
            new TableExtended<>("Claim Data",
                    By.xpath("//div[@id='productConsolidatedViewForm:scolumn_ClaimsDentalTreatmentDetails']//table"));
    public static TableExtended<TableConstants.ClaimSummaryDigitalImageTableExtended> tableDigitalImage =
            new TableExtended<>("Digital Image",
                    By.xpath("//div[@id='productConsolidatedViewForm:scolumn_RenClaimsDigitalImage']//table"));
    public static TableExtended<TableConstants.ClaimTablePatientHistory> tablePatientHistory = new TableExtended<>("Claims Dental Consolidated Patient History",
            By.xpath("//div[@id='productConsolidatedViewForm:scolumn_ClaimsDentalConsolidatedPatientHistory']//table"));
    public static TableExtended<TableConstants.MyWorkTasks> tableMyWorkTasks = new TableExtended<>("Tasks", By.xpath("//div[@id='taskListForm:customTaskTable:workTasks']//table"));
    public static TableExtended<TableConstants.ClaimSummaryOfScheduledPaymentsSeriesTableExtended> tableScheduledPaymentsOfSeries =
            new TableExtended<>("Scheduled Payments of Series",
                    By.xpath("//div[@id='policyDataGatherForm:SeriesScheduledPaymentsViewTable_ClaimsPaymentSeriesScheduledPaymentsView']//table"),
                    By.id("policyDataGatherForm:SeriesScheduledPaymentsViewTable_ClaimsPaymentSeriesScheduledPaymentsView_paginator_bottom"));
    public static TableExtended<TableConstants.ClaimSummaryOfPostedPaymentsOfSeriesTableExtended> tablePostedPaymentsOfSeries =
            new TableExtended<>("Posted Payments of Series",
                    By.xpath("//div[@id='policyDataGatherForm:SeriesPostedPaymentsViewTable_ClaimsPaymentSeriesPostedPaymentsView']//table"));
    public static TableExtended<IntakeInformationTab.ProviderColumns> tableProvider = new TableExtended<>("Provider",
            By.id("productConsolidatedViewForm:body_scolumn_ClaimsDentalConsolidatedVendors"));
    public static TableExtended<IntakeInformationTab.SubmittedServicesColumns> tableSubmittedServices =
            new TableExtended<>("Submitted Services", By.xpath("//div[@id='productConsolidatedViewForm:scolumn_ClaimsDentalEvaluationConsolidatedDamage']//table"));
    public static TableExtended<TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended> tableResultsOfAdjudication =
            new TableExtended<>("Results of Adjudication", By.xpath("//div[@id='productConsolidatedViewForm:scolumn_DentalEvaluationConsolidatedFeaturePPO']//table"));
    public static TableExtended<IntakeInformationTab.InternationalProviderColumns> tableInternationalProvider =
            new TableExtended<>("International Provider",
                    By.xpath("//div[@id='scolumn_RenClaimsDentalConsolidatedInternationalProvider']//table"));
    public static Button buttonSubrogationOpen = new Button(By.id("policyDataGatherForm:okProcessClaimActionBtn_SubrogationOpenAction_footer"));
    public static Button buttonSecureClaim = new Button(By.id("policyDataGatherForm:okProcessClaimActionBtn_ClaimsSecureAction_footer"));
    public static Button buttonUnsecureClaim = new Button(By.id("policyDataGatherForm:okProcessClaimActionBtn_ClaimsUnsecureAction_footer"));
    public static Button buttonSIUPotential = new Button(By.id("policyDataGatherForm:okProcessClaimActionBtn_ClaimsFraudPotentialAction_footer"));
    public static Button buttonSIUReview = new Button(By.id("policyDataGatherForm:okProcessClaimActionBtn_ClaimsReviewFraudAction_footer"));
    public static Button buttonSIUClear = new Button(By.id("policyDataGatherForm:okProcessClaimActionBtn_ClaimsClearFraudAction_footer"));
    public static Button buttonCreateNewClaim = new Button(By.id("claimList:newClaim"));
    public static Button buttonCreateTask = new Button(By.xpath("//a[contains(@id,'createTask') and text()='Create Task']"));
    public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));
    public static StaticElement labelTotalBalance = new StaticElement(By.id("policyDataGatherForm:paymentBalanceHistoryTable_PrecClaimsPaymentBalanceHistory:paymentBalanceHistory_totalBalance"));

    public static String getClaimNumber() {
        String valueNumber = "";
        String content = labelClaimNumber.getValue();

        Pattern pattern = Pattern.compile("#TC\\d*|#OC\\d*|#C\\d*");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            valueNumber = matcher.group(0);
        }

        return valueNumber.replace("#", "");
    }

    public static Currency getTotalBalanceCurrency() {
        Pattern pattern = Pattern.compile("([+-]?\\$\\d*\\.?\\d+)");
        Matcher matcher = pattern.matcher(labelTotalBalance.getValue());
        if (matcher.find()) {
            return new Currency(matcher.group(0));
        } else {
            throw new IstfException("Cannot get 'Total Balance'");
        }
    }

    public static String getClaimDBStatus(String claimNumber) {
        String query = "select claimStatusCd from ClaimsSummaryInfo where claimNumber='%s';";
        return DBService.get().getRow(String.format(query, claimNumber)).get("claimStatusCd");
    }
}
