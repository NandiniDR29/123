/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.pages.summary;

import com.exigen.ipb.eisa.controls.collections.ExpandableSections;
import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.istf.webdriver.ByT;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.Link;
import com.exigen.istf.webdriver.controls.Named;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.istf.webdriver.controls.composite.table.NamedTable;
import com.exigen.istf.webdriver.controls.composite.table.Table;
import com.exigen.ren.main.enums.CustomerConstants;
import org.openqa.selenium.By;

import java.util.List;

public class CustomerSummaryPage extends SummaryPage {

    public static StaticElement labelCustomerNumber = new StaticElement(By.xpath("//span[@id='custInfoForm:customerId' or @id='customerId']"));
    public static StaticElement labelCustomerName =
            new StaticElement(By.xpath("//span[@id='custInfoForm:indvName' or @id='oppInfoForm:name' or @id='custInfoForm:legalName' or @id='custInfoForm:name' or @id='acctInfoForm:name' or @id='acctInfoForm:accountName']"));
    public static StaticElement labelCustomerAddress = new StaticElement(By.xpath("//span[@id='custInfoForm:address' or @id='oppInfoForm:address']"));
    public static StaticElement labelCustomerPhone = new StaticElement(By.xpath("//span[contains(@id,'phone')]"));
    public static StaticElement labelCustomerEmail = new StaticElement(By.id("custInfoForm:email"));
    public static StaticElement labelCustomerLeadSource = new StaticElement(By.id("custInfoForm:source"));
    public static StaticElement labelCustomerDOB = new StaticElement(By.id("custInfoForm:birthdate"));
    public static StaticElement labelCustomerLeadStatus = new StaticElement(By.id("custInfoForm:status"));
    public static StaticElement labelCustomerType = new StaticElement(By.id("custInfoForm:customerType"));
    public static StaticElement labelMembershipInformationExpanded = new StaticElement(By.id("crmForm:participantHeaderContentExpandedMembershipInfo_0"));
    public static StaticElement labelMembershipInformationCollapsed = new StaticElement(By.id("crmForm:participantHeaderContentCollapsedMembershipInfo_0"));
    public static StaticElement labelParticipant = new StaticElement(By.xpath("//span[@id='custInfoForm:participantFor']"));
    public static StaticElement labelStudentInformationExpanded = new StaticElement(By.id("crmForm:participantHeaderContentExpandedStudentInfo_0"));
    public static StaticElement labelStudentInformationCollapsed = new StaticElement(By.id("crmForm:participantHeaderContentCollapsedStudentInfo_0"));
    public static StaticElement labelAccountNumber = new StaticElement(By.id("acctInfoForm:accountNo"));
    public static StaticElement labelConfidential = new StaticElement(By.id("acctInfoForm:confidential"));
    public static StaticElement labelSalesforceID = new StaticElement(By.id("custInfoForm:salesforceID"));
    public static StaticElement labelSalesforceUpdate = new StaticElement(By.id("custInfoForm:salesforceUpdate"));
    public static StaticElement labelMergedFrom = new StaticElement(By.id("custInfoForm:mergedFromNavigationLinks"));
    public static StaticElement labelMergedInto = new StaticElement(By.id("custInfoForm:mergeToNavigationLinks"));
    public static StaticElement labelMergedPolicy = new StaticElement(By.id("act-policies:body_act-policyList:1:act-selectPolicy"));
    public static StaticElement labelMergedQuote = new StaticElement(By.id("quotes:quoteList:1:selectPolicy"));
    public static StaticElement labelPendingUpdatesScheduled = new StaticElement(By.xpath("//div[@class='rf-cp-lbl-colps']/span[contains(text(),'Pending Updates Scheduled')]"));
    public static StaticElement labelDeceased = new StaticElement(By.xpath("//span[@id='custInfoForm:customerName']/following-sibling::span/strong[contains(text(), 'Deceased')]"));
    public static StaticElement labelSpecialHandling = new StaticElement(By.id("acctInfoForm:specialHandlingInd"));
    public static StaticElement labelLeadStatus = new StaticElement(By.id("custInfoForm:status"));

    public static ExpandableSections expandableSectionsRelationships = new ExpandableSections(By.xpath("//div[contains(@id,'crmForm:customerRelationshipsTogglePanel:content')]"));

    public static Link linkMergedFrom = new Link(By.xpath("//span[@id='custInfoForm:mergedFromNavigationLinks']/a"));
    public static Link linkMergedInto = new Link(By.xpath("//span[@id='custInfoForm:mergeToNavigationLinks']/a"));
    public static Link linkAccountTab = new Link(By.xpath("//span[.='Account']"));
    public static Link linkAddNewRelationshipContact = new Link(By.xpath("//a[.='Add New Relationship Contact']"));
    public static Link linkCommunicationTab = new Link(By.xpath("//a[span[.='Communication']]"));
    public static Link linkCreateNewClaimWithoutPolicy = new Link(By.id("claims:addNewClaimNoPolicy"));
    public static Link linkCustomerInfoTab = new Link(By.xpath("//span[.='Customer Info']"));
    public static Link linkEditThisRelationship = new Link(By.xpath("//a[.='Edit This Relationship']"));
    public static Link linkOpportunityTab = new Link(By.xpath("//span[.='Opportunity']"));
    public static Link linkPendingUpdatesPanel = new Link(By.xpath("//div[@id='pendingUpdatesForm:pendingUpdatesTogglePanel:header']"));
    public static Link linkPendingUpdatesCompareAll = new Link(By.xpath("//div[@id='pendingUpdatesForm:pendingUpdateDetailsTogglePanel:header']//div[@class='rf-cp-lbl-colps']//a[.='compare all']"));
    public static Link linkPendingUpdatesRemoveAll = new Link(By.xpath("//div[@id='pendingUpdatesForm:pendingUpdateDetailsTogglePanel:header']//div[@class='rf-cp-lbl-colps']//a[.='remove all']"));
    public static Link linkPendingUpdatesReschedule = new Link(By.xpath("//div[@id='pendingUpdatesForm:pendingUpdateDetailsTogglePanel:header']//div[@class='rf-cp-lbl-colps']//a[.='reschedule']"));
    public static Link linkPendingUpdatesExpand = new Link(By.xpath("//div[@id='pendingUpdatesForm:pendingUpdateDetailsTogglePanel:header']//div[@class='rf-cp-lbl-colps']//span[contains(text(),'Pending Updates Scheduled for')]"));
    public static Link linkRelatedCommunications = new Link(By.xpath("//div[@id='communicationsSwitchPanel:header']//td[2]"));
    public static Link linkRemoveThisBusinessEntity = new Link(By.xpath("//a[.='Remove This Business Entity']"));
    public static Link linkRemoveThisDivision = new Link(By.xpath("//a[.='Remove This Division']"));
    public static Link linkRemoveThisRelationship = new Link(By.xpath("//a[.='Remove This Relationship']"));

    public static Button buttonAddAffinityGroup = new Button(By.xpath("//a[.='Add Affinity Group']"));
    public static Button buttonAddAgency = new Button(By.xpath("//a[.='Add Agency']"));
    public static Button buttonAddContact = new Button(By.xpath("//a[.='Add Contact']"));
    public static Button buttonAddCustomer = new Button(By.xpath("//a[.='Add Customer']"));
    public static Button buttonAddNewContactsDetails = new Button(By.xpath("//a[.='Add New Contacts Details']"));
    public static Button buttonAddQuote = new Button(By.id("quotes:addPolicyBtn"));
    public static Button buttonAgencies = new Button(By.xpath("//div[.='Agencies'][2]"));
    public static Button buttonContactRelationship = new Button(By.xpath("//a[.='Contacts & Relationships']"));
    public static Button buttonEditRelationship = new Button(By.xpath("//a[.='Edit This Relationship']"));
    public static Button buttonOkKeepHistory = new Button(By.xpath("//input[contains(@id, 'crmForm:relationshipAssociationRemovalConfirmationPopup_btnOK_keepHistory')]"));
    public static Button buttonRemoveThisAccount = new Button(By.xpath("//a[.='Remove This Account']"));
    public static Button buttonStartNewCommunication = new Button(By.xpath("//a[.='Start New Communication']"));
    public static Button buttonTimeLine = new Button(By.xpath("//a[@id='crmForm:customerOverview_CUSTOMER' or @id='crmForm:customerContactsTable:0:history' "
            + "or @id ='crmForm:customerOverview_CUSTOMER_' or @id='crmForm:additionalNamesTable:0:historyLink']"));
    public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));
    public static Button buttonCompareAll = new Button(By.xpath("//div[@class='rf-cp-lbl-colps']/a[contains(text(),'compare all')]"));
    public static Button buttonCustomerOverview = new Button(By.id("goBackForm:customerOverview"));
    public static Button buttonCreateNewCustomer = new Button(By.id("crmPartyForm:addDuplicateBtn_footer"));
    public static Button buttonReorderCards = new Button(By.xpath("//*[@id='crmForm:contactMethod:reorderCardsIndBtn']"));
    public static Button buttonAddBundle = new Button(By.id("policyBundles:addBundleBtn"));

    public static TableExtended<ActiveAndRecentlyExpiredPolicies> tableActiveAndRecentlyExpiredPolicies = new TableExtended<>("Active and Recently Expired Policies", By.xpath("//*[@id='act-policies:act-policyList']//table"));
    public static Table tableAffinityGroups = new Table(By.id("associationForm:body_associationTbl"));
    public static TableExtended<AgenciesTable> tableAgencies = new TableExtended<>("Agencies", By.id("assignments:body_assignmentsTbl"));
    public static Table tableBilling = new Table(By.id("customerBilling:customerBillingAccountTbl"));
    public static NamedTable<CustomerConstants.CustomerProvidersTable> tableProviders = new NamedTable<>("Customer Providers", By.xpath("//*[@id='providers:providersList']//*[@class='ui-datatable-tablewrapper']/table"));
    public static Table tableBundles = new Table(By.id("policyBundles:body_policyBundlesTable"));
    public static Table tableBusinessEntities = new Table(By.xpath("//*[@id='businessEntityList']//table"));
    public static Table tableClaims = new Table(By.id("claims:customerClaimTbl"));
    public static NamedTable<CustomerConstants.CustomerContactsTable> tableCustomerContacts = new NamedTable<>("Customer Contacts", By.id("crmForm:customerContactsTable"));
    public static Table tableCustomerInformation = new Table(By.xpath("//div[@id='custAssociations:customersToDisplay']//table"));
    public static Table tableEmploymentsInformation = new Table(By.xpath("//*[@id='employmentsList']//table"));
    public static Table tableCustomerGeneralInformation = new Table(By.id("custInfoForm:grid"));
    public static TableExtended<CustomerInformation> tableCustomers = new TableExtended<>("Customer Information", By.xpath("//*[@id='custAssociations:customersToDisplay']//table"));
    public static Table tableDesignatedContacts = new Table(By.id("contactForm:body_contactsTbl"));
    public static Table tableDivisions = new Table(By.xpath("//*[@id='divisionTable']//table"));
    public static Table tableEmploymentCensus = new Table(By.id("EMP_crmForm:censusDataTable"));
    public static Table tableGroup = new Table(By.id("crmForm:custGroupInfoListGroup"));
    public static Table tableGroups = new Table(By.id("body_groupInfosList"));
    public static Table tableInactivePolicies = new Table(By.id("inact-policies:body_inact-policyList"));
    public static Table tableMajorLargeAccount = new Table(By.id("crmForm:majorAccountPanel"));
    public static Table tableMembershipCensus = new Table(By.id("MEM_crmForm:censusDataTable"));
    public static Table tableNewProductDetails = new Table(By.xpath("//*[@id='crmForm:otherProductsTable']//table"));
    public static Table tableQuotes = new Table(By.xpath("//div[@id='quotes:quoteList']//table"));
    public static Table tableRelationshipPartySearchResult = new Table(By.id("crmPartyForm:partyList"));
    public static Table tableRelationshipResult = new Table(By.xpath("//div[@id='crmForm:newRelationshipsTogglePanel_0:header']//table"));
    public static Table tableStudentCensus = new Table(By.id("STU_crmForm:censusDataTable"));
    public static Table tableAddresslInfo = new Table(By.id("addressCompareTable"));
    public static Table tableGeneralInfo = new Table(By.id("indGeneralInfoCompareTable"));
    public static Table tableAdditionalNamesInfo = new Table(By.id("nonIndvAdditionalNameCompareTable"));
    public static Table tableVersionCompare = new Table(By.xpath("//table[contains(@id,'ParticipationDetailsCompareTable')]"));
    public static Table tableProviderVersionCompare = new Table(By.xpath("//table[contains(@id,'serviceProviderCompareTable')]"));
    public static Table tableTimlineSection = new Table(By.xpath("//div[@id='historyForm:historyTable']//table[tbody]"));
    public static Table tableRelatedCommunications = new Table(By.id("communicationsSearchForm:searchTable"))
            .applyConfiguration("RelatedCommunications");
    public static Table tableCustomerListAccount = new Table(By.id("searchCustomerResult:searchCustomerTable"));
    public static Table tableProductsOwned = new Table(By.xpath("//*[@id='productsOwned:productsOwnedList']//table"));
    public static Table tablePendingUpdates = new Table(By.xpath("//div[@id='pendingUpdatesForm:pendingUpdateDetailsTogglePanel:content']//table"));

    private static final ByT LINK_REFERENCE_RELATIONSHIP = ByT.xpath(".//a[contains(@id,'crmForm:referenceLink')]");
    private static final ByT LINK_SECTION_ESTABLISHED = ByT.xpath("//div[contains(div,'%s') and contains(@id,'crmForm:newRelationshipsTogglePanel') and not(contains(@id,':header'))]");
    private static final ByT LINK_REMOVE_PROVIDER = ByT.id("crmForm:removeProvider_%s");
    private static final ByT LINK_TIMELINE_PROVIDER = ByT.id("crmForm:customerOverview_SERVICE_PROVIDER_%s");
    private static final ByT LINK_EXPAND_COLLAPSE_PROVIDER = ByT.xpath("//div[@id='crmForm:providerTogglePanel_%s']//td[@class='rf-cp-ico']");
    private static final ByT LINK_MERGED_FROM = ByT.xpath("//span[@id='custInfoForm:mergedFromNavigationLinks']/a[contains(text(),%s)]");
    private static final ByT LINK_MERGED_INTO = ByT.xpath("//span[@id='custInfoForm:mergeToNavigationLinks']/a[contains(text(),%s)]");

    public enum AgenciesTable implements Named {
        AGENCY_NAME("Agency Name"),
        AGENCY_CHANNEL("Agency Channel");

        private String name;

        AgenciesTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static void expandRelatedCommunications() {
        Link collapsedRelatedCommunications = new Link(By.xpath("//div[text()='Related Communications' and contains(@class, 'colps')]"));
        if (collapsedRelatedCommunications.isVisible()) {
            collapsedRelatedCommunications.click();
        }
    }

    public static String getCustomerLastName(){
        return  CustomerSummaryPage.labelCustomerName.getValue().split("\\s+")[1];
    }

    public static String getCustomerFirstName(){
        return  CustomerSummaryPage.labelCustomerName.getValue().split("\\s+")[0];
    }

    public static String getEstablishRelationshipFirstName(String expandSection) {
        Link expandEstablishRelationshipSection = new Link(LINK_SECTION_ESTABLISHED.format(expandSection));
        expandEstablishRelationshipSection.click();
        return expandEstablishRelationshipSection.getValue().split(" ")[1];
    }

    public static void goToRelationshipPage(String expandSection) {
        Link linkExpandSection = new Link(LINK_SECTION_ESTABLISHED.format(expandSection));
        Link linkReference = new Link(linkExpandSection, LINK_REFERENCE_RELATIONSHIP.format(expandSection));
        if (!linkReference.isPresent()) {
            linkExpandSection.click();
        }
        linkReference.click();
    }

    public static Link getRelationLink(String section) {
        return new Link(LINK_SECTION_ESTABLISHED.format(section));
    }

    public static List<String> getRelationshipsSectionsNames() {
        return expandableSectionsRelationships.getTexts();
    }

    public static String getRelationshipReferenceByName(String expandSection) {
        Link linkExpandSection = new Link(LINK_SECTION_ESTABLISHED.format(expandSection));
        linkExpandSection.click();
        return new Link(linkExpandSection, LINK_REFERENCE_RELATIONSHIP.format(expandSection)).getValue();
    }

    public static Link getRemoveProviderLink(int i) {
        return new Link(LINK_REMOVE_PROVIDER.format(i));
    }

    public static Link getProviderTimelineLink(int i) {
        return new Link(LINK_TIMELINE_PROVIDER.format(i));
    }

    public static Link getProviderExpandCollapseLink(int i) {
        return new Link(LINK_EXPAND_COLLAPSE_PROVIDER.format(i));
    }

    public static Link getMergedFromLink(String i) {
        return new Link(LINK_MERGED_FROM.format(i));
    }

    public static Link getMergedIntoLink(String i) {
        return new Link(LINK_MERGED_INTO.format(i));
    }

    public enum CustomerInformation implements Named {
        CUSTOMER("Customer #"),
        EIN("EIN");
        private String name;

        CustomerInformation(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum ActiveAndRecentlyExpiredPolicies implements Named {
        POLICY("Policy"),
        MEMBER_COMPANY_NAME("Member Company Name"),
        PRODUCT("Product"),
        STATUS("Status"),
        ADDITIONAL_INFO("Additional Info"),
        EFF_DATE("Eff. Date"),
        EXP_DATE("Exp. Date"),
        AGENCY("Agency"),
        BRAND_INFO("Brand Info"),
        GROUP_ID("Group ID"),
        AGENT_PHONE("Agent Phone"),
        MERGED_FROM("Merged From");
        private String name;

        ActiveAndRecentlyExpiredPolicies(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
