/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.customer;

import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.ren.EntityLogger;
import com.exigen.ren.common.AutomationContext;
import com.exigen.ren.common.EntityService;
import com.exigen.ren.common.Workspace;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.enums.SearchEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.SearchPage;
import com.exigen.ren.main.modules.customer.actions.*;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.customer.CustomerRestService;
import com.exigen.ren.rest.customer.model.CustomerModel;
import org.slf4j.LoggerFactory;

public interface ICustomer extends EntityService {

    void create(TestData td);

    default void createViaUI(TestData td) {
        MainPage.QuickSearch.buttonSearchPlus.click();
        SearchPage.buttonCreateCustomer.click();
        getDefaultWorkspace().fill(td);
        Customer.CustomerData.setCustomerNumber(CustomerSummaryPage.labelCustomerNumber.getValue());
        LoggerFactory.getLogger(this.getClass()).info("Created {}", EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));
    }

    default void createViaREST(TestData td) {
        try {
            CustomerRestService restCustomer = RESTServiceType.CUSTOMERS.get();
            LoggerFactory.getLogger(this.getClass()).info("Starting customer creation via REST...");
            CustomerModel customer = restCustomer.postCustomers(td.resolveLinks());
            if (customer.getErrorCode() != null || customer.getErrors() != null) {
                throw new IstfException(String.format("Customer creation failed with error codes {%s} and errors {%s}", customer.getErrors(), customer.getErrorCode()));
            }
            Customer.CustomerData.setCustomerNumber(customer.getCustomerNumber());
            LoggerFactory.getLogger(this.getClass()).info("Customer has been created successfully");
            MainPage.QuickSearch.search(customer.getCustomerNumber());
            if (MainPage.dialogConfirmation.isPresent()) {
                MainPage.dialogConfirmation.confirm();
                LoggerFactory.getLogger(this.getClass()).info("Customer wasn't found by Quick Search. Customer number: " + customer.getCustomerNumber());
                SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.CUSTOMER, customer.getCustomerNumber());
                if (SearchPage.buttonSearch.isPresent()) {
                    SearchPage.openFirstResult();
                }
            }
            LoggerFactory.getLogger(this.getClass()).info("Created {}", EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));
        } catch (Exception e) {
            throw new IstfException(e);
        }
    }

    //TODO(arutski): transfer method to util class
    default void createWithExistingAccount(TestData td) {
        NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.ACCOUNT);
        CustomerSummaryPage.buttonAddCustomer.click();
        getDefaultWorkspace().fill(td);
        CustomerSummaryPage.tableCustomerInformation.getRow(2).getCell(1).click();
        Customer.CustomerData.setCustomerNumber(CustomerSummaryPage.labelCustomerNumber.getValue());
        LoggerFactory.getLogger(this.getClass()).info("Created {}", EntityLogger.getEntityHeader(EntityLogger.EntityType.CUSTOMER));
    }

    default Workspace getDefaultWorkspace() {
        return AutomationContext.getAction(DeleteCustomerAction.class).getWorkspace();
    }

    default AddCommunicationAction addCommunication() {
        return AutomationContext.getAction(AddCommunicationAction.class);
    }

    default AddCommunicationThreadAction addCommunicationThread() {
        return AutomationContext.getAction(AddCommunicationThreadAction.class);
    }

    default RemoveCommunicationAction removeCommunication() {
        return AutomationContext.getAction(RemoveCommunicationAction.class);
    }

    default UpdateCommunicationAction updateCommunication() {
        return AutomationContext.getAction(UpdateCommunicationAction.class);
    }

    default AddOpportunityAction addOpportunity() {
        return AutomationContext.getAction(AddOpportunityAction.class);
    }

    default UpdateOpportunityAction updateOpportunity() {
        return AutomationContext.getAction(UpdateOpportunityAction.class);
    }

    default UpdateAction update() {
        return AutomationContext.getAction(UpdateAction.class);
    }

    default RemoveRelationshipContactAction removeRelationshipContact() {
        return AutomationContext.getAction(RemoveRelationshipContactAction.class);
    }

    default AddRelationshipContactAction addRelationshipContact() {
        return AutomationContext.getAction(AddRelationshipContactAction.class);
    }

    default RemoveOpportunityAction removeOpportunity() {
        return AutomationContext.getAction(RemoveOpportunityAction.class);
    }

    default DeleteCustomerAction deleteCustomer() {
        return AutomationContext.getAction(DeleteCustomerAction.class);
    }

    default UpdateRelationshipContactAction updateRelationshipContact() {
        return AutomationContext.getAction(UpdateRelationshipContactAction.class);
    }

    default ScheduledUpdateAction scheduledUpdate() {
        return AutomationContext.getAction(ScheduledUpdateAction.class);
    }

    default InquiryAction inquiry() {
        return AutomationContext.getAction(InquiryAction.class);
    }

    default DeletePendingUpdatesAction deletePendingUpdates() {
        return AutomationContext.getAction(DeletePendingUpdatesAction.class);
    }

    default AddNewContactsDetailsAction addNewContactsDetails() {
        return AutomationContext.getAction(AddNewContactsDetailsAction.class);
    }

    default RemoveNewContactsDetailsAction removeNewContactsDetails() {
        return AutomationContext.getAction(RemoveNewContactsDetailsAction.class);
    }

    default AddNewRelationshipContactAction addNewRelationshipContacts() {
        return AutomationContext.getAction(AddNewRelationshipContactAction.class);
    }

    default RemoveNewRelationshipContactsAction removeNewRelationshipContacts() {
        return AutomationContext.getAction(RemoveNewRelationshipContactsAction.class);
    }

    default RemoveBusinessEntityAction removeBusinessEntity() {
        return AutomationContext.getAction(RemoveBusinessEntityAction.class);
    }

    default AddAgencyAction addAgency() {
        return AutomationContext.getAction(AddAgencyAction.class);
    }

    default RemoveAgencyAction removeAgency() {
        return AutomationContext.getAction(RemoveAgencyAction.class);
    }

    default AddParticipantAction addParticipant() {
        return AutomationContext.getAction(AddParticipantAction.class);
    }

    default UpdateParticipantEmploymentAction updateParticipantEmployment() {
        return AutomationContext.getAction(UpdateParticipantEmploymentAction.class);
    }

    default UpdateParticipantMembershipAction updateParticipantMembership() {
        return AutomationContext.getAction(UpdateParticipantMembershipAction.class);
    }

    default UpdateParticipantStudentAction updateParticipantStudent() {
        return AutomationContext.getAction(UpdateParticipantStudentAction.class);
    }

    default RemoveParticipantEmploymentAction removeParticipantEmployment() {
        return AutomationContext.getAction(RemoveParticipantEmploymentAction.class);
    }

    default RemoveParticipantMembershipAction removeParticipantMembership() {
        return AutomationContext.getAction(RemoveParticipantMembershipAction.class);
    }

    default RemoveParticipantStudentAction removeParticipantStudent() {
        return AutomationContext.getAction(RemoveParticipantStudentAction.class);
    }

    default RemoveProviderAction removeProvider() {
        return AutomationContext.getAction(RemoveProviderAction.class);
    }

    default AssociateExistingCustomerAction associateExistingCustomer() {
        return AutomationContext.getAction(AssociateExistingCustomerAction.class);
    }

    default RemoveNewProductDetailsAction removeNewProductDetails() {
        return AutomationContext.getAction(RemoveNewProductDetailsAction.class);
    }

    default QualifyAction qualify() {
        return AutomationContext.getAction(QualifyAction.class);
    }

    default MakeInvalidAction makeInvalid() {
        return AutomationContext.getAction(MakeInvalidAction.class);
    }

    default UndoInvalidAction undoInvalid() {
        return AutomationContext.getAction(UndoInvalidAction.class);
    }

    default RemoveDivisionsAction removeDivisions() {
        return AutomationContext.getAction(RemoveDivisionsAction.class);
    }

    default ViewHistoryAction viewHistory() {
        return AutomationContext.getAction(ViewHistoryAction.class);
    }

    default UpdateContactsDetailsAction updateContactsDetails() {
        return AutomationContext.getAction(UpdateContactsDetailsAction.class);
    }

    default AddAssociateQuoteOnOpportunityAction addAssociateQuoteOnOpportunity() {
        return AutomationContext.getAction(AddAssociateQuoteOnOpportunityAction.class);
    }

    default RemoveAssociateQuoteOnOpportunityAction removeAssociateQuoteOnOpportunity() {
        return AutomationContext.getAction(RemoveAssociateQuoteOnOpportunityAction.class);
    }

    default AddAssociatePolicyOnOpportunityAction addAssociatePolicyOnOpportunity() {
        return AutomationContext.getAction(AddAssociatePolicyOnOpportunityAction.class);
    }

    default RemoveAssociatePolicyOnOpportunityAction removeAssociatePolicyOnOpportunity() {
        return AutomationContext.getAction(RemoveAssociatePolicyOnOpportunityAction.class);
    }

    default StartNewQuoteInOpportunityAction startNewQuoteInOpportunity() {
        return AutomationContext.getAction(StartNewQuoteInOpportunityAction.class);
    }

    default StartNewQuoteInOpportunityUpdateAction startNewQuoteInOpportunityUpdate() {
        return AutomationContext.getAction(StartNewQuoteInOpportunityUpdateAction.class);
    }

    default StartNewQuoteInOpportunityPreviewAction startNewQuoteInOpportunityPreview() {
        return AutomationContext.getAction(StartNewQuoteInOpportunityPreviewAction.class);
    }

    default AddAssociateCampaignOnOpportunityAction addAssociateCampaignOnOpportunity() {
        return AutomationContext.getAction(AddAssociateCampaignOnOpportunityAction.class);
    }

    default RemoveAssociateCampaignOnOpportunityAction removeAssociateCampaignOnOpportunity() {
        return AutomationContext.getAction(RemoveAssociateCampaignOnOpportunityAction.class);
    }

    default RemoveGroupAction removeGroup() {
        return AutomationContext.getAction(RemoveGroupAction.class);
    }

    default AssociateDivisionsAction associateDivisions() {
        return AutomationContext.getAction(AssociateDivisionsAction.class);
    }

    default MergeCustomerAction mergeCustomer() {
        return AutomationContext.getAction(MergeCustomerAction.class);
    }

    default GenerateOnDemandDocumentAction generateDocument() {
        return AutomationContext.getAction(GenerateOnDemandDocumentAction.class);
    }

}
