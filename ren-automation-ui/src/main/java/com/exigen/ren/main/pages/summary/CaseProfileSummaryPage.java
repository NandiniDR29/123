/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.pages.summary;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import org.openqa.selenium.By;

import static com.exigen.ren.common.enums.NavigationEnum.AppMainTabs.CASE;
import static com.exigen.ren.main.enums.ActionConstants.RE_GENERATE;
import static com.exigen.ren.main.enums.ActionConstants.UPDATE;
import static com.exigen.ren.main.enums.CaseProfileConstants.CaseProfileProposalTable.PROPOSAL_NUMBER;

public class CaseProfileSummaryPage extends SummaryPage {

    public static StaticElement labelCaseProfileName = new StaticElement(By.id("caseProfileContextInfoForm:caseProfile_caseProfileName"));
    public static StaticElement labelCaseProfileNumber = new StaticElement(By.id("caseProfileContextInfoForm:caseProfile_caseNum"));
    public static StaticElement labelCaseProfileStatus = new StaticElement(By.id("caseProfileContextInfoForm:caseProfile_timedStatusCdText"));
    public static StaticElement labelCaseProfileEffectiveDate = new StaticElement(By.id("caseProfileContextInfoForm:caseProfile_effectiveDate"));

    public static TableExtended<CaseProfilesTable> tableSelectCaseProfile = new TableExtended<>("Case Profiles", By.id("casesList:body_cases"));
    public static Table tableProposal = new Table(By.xpath("//div[@id='groupProposalConsolidatedForm:consolidatedProposalsTable']//table"));
    public static Table tableCaseProfileVersionHistory = new Table(By.id("caseVersionHistoryView:body_caseVersionHistoryTable"));
    public static TableExtended<SummaryClassificationGroupsTable> tableClassificationGroups = new TableExtended<>("Classification Groups", By.xpath("//table[@id='consolidatedView:body_classificationGroupsTable']"));
    public static TableExtended<TableConstants.FileIntakeManagement> tableFileIntakeManagement = new TableExtended<>("File Intake Management", By.id("consolidatedView:body_intakeProfileConsolidateView"));

    public static Button buttonAddNewProposal = new Button(By.id("groupProposalConsolidatedForm:groupProposalConsolidatedAddProposal"));
    public static Button buttonCaseProfileVersionHistory = new Button(By.id("caseProfileContextInfoForm:lnkCaseProfileHistory"));

    public static String getEffectiveDate() {
        return CaseProfileSummaryPage.labelCaseProfileEffectiveDate.getValue().replace("(", "").replace(")", "").trim();

    }

    public enum SummaryClassificationGroupsTable implements Named {
        CLASS_NUMBER("Class Number"),
        CLASS_NAME("Class Name"),
        CLASS_TYPE("Class Type"),
        RELATIONSHIP_TYPE("Relationship Type"),
        ORIGINAL_EFFECTIVE_DATE("Original Effective Date");

        private String name;

        SummaryClassificationGroupsTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum CaseProfilesTable implements Named {
        CASE_PROFILE_NAME("Case Profile Name");

        private String name;

        CaseProfilesTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static void updateExistingProposalByNumber(String proposalNumber) {
        LOGGER.info(String.format("Update link click with proposal: '%s'", proposalNumber));
        CaseProfileSummaryPage.tableProposal.getRowContains(PROPOSAL_NUMBER, proposalNumber).getCell(6).controls.links.get(UPDATE).click();
        Tab.buttonNext.click();
    }

    public static void reGenerateProposalByNumber(String proposalNumber) {
        LOGGER.info(String.format("RE-Generate link click with proposal: '%s'", proposalNumber));
        CaseProfileSummaryPage.tableProposal.getRowContains(PROPOSAL_NUMBER, proposalNumber).getCell(6).controls.links.get(RE_GENERATE).click();
        Tab.buttonNext.click();
    }
    public static void navigateToProposalsTab(){
        NavigationPage.toMainTab(CASE);
        CaseProfileSummaryPage.tableSelectCaseProfile.getRow(1).getCell(CaseProfileSummaryPage.CaseProfilesTable.CASE_PROFILE_NAME.getName()).controls.links.getFirst().click();
        NavigationPage.toSubTab(NavigationEnum.CaseProfileTab.PROPOSALS.get());
    }

}
