/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.pages.summary;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.istf.webdriver.controls.*;
import com.exigen.ren.common.components.Dialog;
import com.exigen.ren.common.pages.MainPage;
import org.openqa.selenium.By;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuoteSummaryPage extends MainPage {
    public static final String DIALOG_META = "ProductInitiateDialog";

    public static Button buttonAddNewQuote = new Button(By.xpath("//input[@id='quoteForm:actionButton_newQuote' or @id='quoteForm:newQuoteButton']"));
    public static Button buttonCancel = new Button(By.id("quoteForm:cancelButton"));
    public static ComboBox comboBoxBroadLineOfBusiness = new ComboBox(By.id("quoteForm:quoteCreationPopupMultiEdit_blob"));
    public static ComboBox comboBoxProduct = new ComboBox(By.id("quoteForm:quoteCreationPopupMultiEdit_productCd"));
    public static Dialog dialogSelectProduct = new Dialog("//div[@id='quoteForm:quotePreCreationPopup_container']");
    public static Link labelCaseProfileNumber = new Link(By.xpath("//span[@id='productContextInfoForm:caseProfileLabel']"));
    public static StaticElement labelCustomerName = new StaticElement(By.xpath("//span[@id='productContextInfoForm:policyDetail_customerDisplayNameText']"));

    public static TableExtended<CoverageSummary> tableCoverageSummary = new TableExtended<>("Coverage Summary",
            By.xpath("//table[@id='productConsolidatedViewForm:body_scolumn_GroupCoverageSplitComponent']"));
    public static TableExtended<SelectQuote> tableSelectQuote = new TableExtended<>("SelectQuote", By.id("quotePageContents:body_quote_list_table"));
    public static TableExtended<MasterQuote> tableMasterQuote = new TableExtended<>("Master Quote", By.id("productConsolidatedViewForm:body_scolumn_Policy"));
    public static TableExtended<CustomerInformation> tableCustomerInformation = new TableExtended<>("Customer Information",
            By.xpath("//table[@id='productConsolidatedViewForm:body_scolumn_GroupMasterPolicySponsor']"));
    public static TableExtended<PlanSummary> tablePlanSummary = new TableExtended<>("Plan Summary",
            By.xpath("//table[@id='productConsolidatedViewForm:body_scolumn_GroupCoverageSplitComponent']"));

    public static StaticElement labelMoratoriumMessage = new StaticElement(By.id("quotePageContents:moratoriumExistsMessage"));
    public static StaticElement labelMessage = new StaticElement(By.xpath("li[@id, 'policyDataGatherForm:bannerGroup0:2']/span"));
    public static StaticElement labelQuoteNumber = new StaticElement(By.xpath("//li[@id = 'policyDataGatherForm:bannerGroup0:1']/span"));
    public static StaticElement labelQuoteStatus = new StaticElement(By.xpath("//li[@id = 'policyDataGatherForm:bannerGroup2:0']/span"));
    public static StaticElement labelQuoteStatusCp = new StaticElement(By.xpath("//li[@id = 'policyDataGatherForm:bannerGroup4:0']/span"));
    public static StaticElement labelInsuredNameCp = new StaticElement(By.xpath("//li[@id = 'policyDataGatherForm:bannerGroup0:0']/span"));

    public enum SelectQuote implements Named {
        QUOTE("Quote #"),
        TYPE("Type"),
        PREMIUM("Premium"),
        STATUS("Status"),
        EFF_DATE("Eff. Date"),
        LAST_CHG_DATE("Last Chg. Date"),
        BROKER("Broker"),
        LAST_PERFORMER("Last Performer"),
        BUNDLE("Bundle")
        ;
        private String name;

        SelectQuote(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum MasterQuote implements Named {
        QUOTE_NUMBER("Quote Number"),
        GROUP_IS_MEMBER_COMPANY("Group is Member Company"),
        MEMBER_COMPANY_NAME("Member Company Name"),
        NAME_TO_DISPLAY_ON_MP_DOCUMENTS("Name to Display on Master Policy Documents"),
        SMALL_GROUP("Small group?"),
        QUOTE_CREATION_DATE("Quote Creation Date"),
        QUOTE_EXPIRATION_DATE("Quote Expiration Date"),
        ANNIVERSARY_DATE("Anniversary Date"),
        POLICY_EFFECTIVE_DATE("Policy Effective Date"),
        POLICY_EXPIRATION_DATE("Policy Expiration Date"),
        SALES_REPRESENTATIVE("Sales Representative");
        private String name;

        MasterQuote(String name){
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CustomerInformation implements Named {
        NAME_LEGAL("Name - Legal");

        private String name;

        CustomerInformation(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CoverageSummary implements Named {
        PARTICIPANTS("Participants"),
        VOLUME("Volume"),
        MODAL_PREMIUM("Modal Premium");

        private String name;

        CoverageSummary(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum PlanSummary implements Named {
        PLAN("Plan"),
        EFFECTIVE_DATE("Effective Date"),
        CONTRIBUTION_TYPE("Contribution Type"),
        CONTRIBUTION("Contribution %"),
        PARTICIPANTS("Participants"),
        PAYOR("Payor"),
        PAYMENT_MODE("Payment Mode"),
        MODAL_PREMIUM("Modal Premium"),
        ANNUAL_PREMIUM("Annual Premium");

        private String name;

        PlanSummary(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static String getQuoteNumber() {
        Pattern pattern = Pattern.compile("[A-Z]{2}[0-9]*");
        Matcher matcher = pattern.matcher(labelQuoteNumber.getValue());
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            throw new IstfException("Cannot find quote number");
        }
    }
}