/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.TestDataRequestContext;
import com.exigen.ipb.eisa.ws.rest.entity.FreemarkerEntity;
import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.rest.ResponseWrapper;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.account.model.AccountModel;
import com.exigen.ren.rest.customer.campaigns.model.CampaignModel;
import com.exigen.ren.rest.customer.communications.model.CommunicationModel;
import com.exigen.ren.rest.customer.communications.model.CommunicationThreadModel;
import com.exigen.ren.rest.customer.communications.model.CommunicationsModel;
import com.exigen.ren.rest.customer.communications.model.CommunicationsModelThread;
import com.exigen.ren.rest.customer.groupSponsors.model.GroupSponsorModel;
import com.exigen.ren.rest.customer.model.*;
import com.exigen.ren.rest.customer.opportunity.model.OpportunitiesModel;
import com.exigen.ren.rest.customer.opportunity.model.OpportunityModel;
import com.exigen.ren.rest.customer.provider.model.CustomerProviderModel;
import com.exigen.ren.rest.customer.provider.model.ProviderModel;
import com.fasterxml.jackson.databind.DeserializationFeature;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;


public class CustomerRestService {

    private RestClient client;
    private static final String CUSTOMER_NUMBER_KEY = "customerNumber";
    private static final String CONTACT_ID_KEY = "contactId";
    private JsonConfiguration conf;

    public CustomerRestService() {
        conf = ConfigurationFactory.get(JsonConfiguration.class);
        conf.getObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        client = new RestClient("customercore-rs", conf);
    }

    public CustomerRestService(User user) {
        conf = ConfigurationFactory.get(JsonConfiguration.class, user);
        conf.getObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        client = new RestClient("customercore-rs", conf);
    }

    public CustomerModel putIndividualCustomers(String customerNumber, CustomerModel model) {
        return client.processRequest("CUSTOMERS.INDIVIDUAL.PUT", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()).readEntity(CustomerModel.class);
    }

    public ResponseContainer<CustomerModel> putNonIndividualCustomer(String customerNumber, CustomerModel model) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.NONINDIVIDUAL.PUT", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<CustomerModel>() {
                });
    }

    public ResponseContainer<CustomerModel> postCustomer(CustomerModel customer) {
        return new ResponseContainer<>(client.processRequest("POSTCUSTOMERENTITY", RestClient.HttpMethod.POST, customer),
                new GenericType<CustomerModel>() {
                });
    }

    public ResponseContainer<CustomerModel> getCustomersItem(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.ITEM", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<CustomerModel>() {
                });
    }

    public ResponseContainer<List<CustomerModel>> getCustomerDetails(List<String> customerNumbers) {
        MultivaluedMap<String, String> params = new MultivaluedHashMap<>();
        params.put(CUSTOMER_NUMBER_KEY, customerNumbers);
        return new ResponseContainer<>(client.processRequest("CUSTOMER.DETAILS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().queryParams(params).build()),
                new GenericType<List<CustomerModel>>() {
                });
    }

    public ResponseContainer<List<CustomerModel>> getCustomerWithActiveCampaign() {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.WITHACTIVECAMPAIGNS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().build()),
                new GenericType<List<CustomerModel>>() {
                });
    }

    public CustomerModel putCustomer(CustomerModel customer, String customerNumber) {
        return client.processRequest("PUTCUSTOMERENTITY", RestClient.HttpMethod.PUT, customer,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build())
                .readEntity(CustomerModel.class);
    }

    public CustomerModel postCustomers(TestData data) {
        return client.processRequest("CUSTOMERS.CREATE", RestClient.HttpMethod.POST,
                new CustomerModel(data)).readEntity(CustomerModel.class);
    }

    public ContactMethodAddressModel putCustomersAddressesItem(ContactMethodAddressModel address, String customerNumber, int contactId) {
        return client.processRequest("CUSTOMERS.PUTADDRESSESENTITY.ITEM", RestClient.HttpMethod.PUT, address,
                InlineRequestContext.builder()
                        .pathParam(CUSTOMER_NUMBER_KEY, customerNumber).pathParam(CONTACT_ID_KEY, Integer.toString(contactId)).build())
                .readEntity(ContactMethodAddressModel.class);
    }

    public ContactMethodAddressModel postCustomersAddressesItem(ContactMethodAddressModel address, String customerNumber) {
        return postContactMethod("CUSTOMERS.POSTADDRESSESENTITY.ITEM", customerNumber, address).readEntity(ContactMethodAddressModel.class);
    }

    public ContactMethodEmailModel putCustomersEmailsItem(ContactMethodEmailModel email, String customerNumber, int contactId) {
        return putContactMethod("CUSTOMERS.EMAILSENTITY.ITEM", customerNumber, email, contactId).readEntity(ContactMethodEmailModel.class);
    }

    public Response putCustomerWebAddressItem(ContactMethodWebAddressModel webAddress, String customerNumber, int contactId) {
        return putContactMethod("CUSTOMERS.WEBADDRESSES.ITEM", customerNumber, webAddress, contactId);
    }

    public Response putCustomerSocialNetItem(ContactMethodSocialNetModel webAddress, String customerNumber, int contactId) {
        return putContactMethod("CUSTOMERS.SOCIALNETS.ITEM", customerNumber, webAddress, contactId);
    }

    private Response putContactMethod(String requestAlias, String customerNumber, ContactMethodModel contactMethodModel, int contactId) {
        return client.processRequest(requestAlias, RestClient.HttpMethod.PUT, contactMethodModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).pathParam(CONTACT_ID_KEY, Integer.toString(contactId)).build());
    }

    public ContactMethodEmailModel postCustomersEmailsItem(ContactMethodEmailModel email, String customerNumber) {
        return postContactMethod("CUSTOMERS.POSTEMAILSENTITY.ITEM", customerNumber, email).readEntity(ContactMethodEmailModel.class);
    }

    public ContactMethodPhoneModel postCustomersPhonesItem(ContactMethodPhoneModel phone, String customerNumber) {
        return postContactMethod("CUSTOMERS.PHONESENTITY", customerNumber, phone).readEntity(ContactMethodPhoneModel.class);
    }

    public ContactMethodWebAddressModel postCustomersWebAddressItemAndReturnEntity(ContactMethodWebAddressModel webAddress, String customerNumber) {
        return postContactMethod("CUSTOMERS.WEBADDRESSES", customerNumber, webAddress).readEntity(ContactMethodWebAddressModel.class);
    }

    public Response postCustomersWebAddressItem(ContactMethodWebAddressModel webAddress, String customerNumber) {
        return postContactMethod("CUSTOMERS.WEBADDRESSES", customerNumber, webAddress);
    }

    public Response postCustomersSocialNetItem(ContactMethodSocialNetModel socialNet, String customerNumber) {
        return postContactMethod("CUSTOMERS.SOCIALNETS", customerNumber, socialNet);
    }

    private Response postContactMethod(String requestAlias, String customerNumber, ContactMethodModel contactMethodModel) {
        return client.processRequest(requestAlias, RestClient.HttpMethod.POST, contactMethodModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build());
    }

    public ResponseContainer<List<ContactMethodPhoneModel>> getCustomersPhonesItem(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.PHONESENTITY", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), new GenericType<List<ContactMethodPhoneModel>>() {
        });
    }

    public ResponseContainer<List<ContactMethodAddressModel>> getCustomersAddressItem(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.POSTADDRESSESENTITY.ITEM", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), new GenericType<List<ContactMethodAddressModel>>() {
        });
    }

    public ResponseWrapper deleteCustomersAddressItem(String customerNumber, int contactId) {
        return new ResponseWrapper(client.processRequest("CUSTOMERS.DELETEADDRESSESENTITY.ITEM", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).pathParam(CONTACT_ID_KEY, Integer.toString(contactId))
                        .build()));
    }

    public ContactMethodPhoneModel putCustomersPhonesItem(ContactMethodPhoneModel phone, String customerNumber, int contactId) {
        return client.processRequest("CUSTOMERS.PHONESENTITY.ITEM", RestClient.HttpMethod.PUT, phone,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber)
                        .pathParam(CONTACT_ID_KEY, Integer.toString(contactId)).build()).readEntity(ContactMethodPhoneModel.class);
    }

    public List<NavigationLinkModel> getCustomersNavigationLinks(String customerNumber) {
        return client.processRequest("NAVIGATIONLINKS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("customerNumber", customerNumber).build())
                .readEntity(new GenericType<List<NavigationLinkModel>>() {
                });
    }

    public ResponseContainer<NavigationLinkModel> postCustomersNavigationLinks(String customerNumber, TestData data) {
        return new ResponseContainer<>(client.processRequest("NAVIGATIONLINKS", RestClient.HttpMethod.POST,
                RestUtil.convert(data, NavigationLinkModel.class, conf),
                InlineRequestContext.builder().pathParam("customerNumber", customerNumber).build()),
                NavigationLinkModel.class);
    }

    public ResponseContainer<NavigationLinkModel> deleteNavigationLinks(String customerNumber, String navigationLink) {
        return new ResponseContainer<>(client.processRequest("NAVIGATIONLINKS_DELETE", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().pathParam("customerNumber", customerNumber).pathParam("linkId", navigationLink)
                        .build()), NavigationLinkModel.class);
    }

    public ResponseContainer<List<ContactMethodSocialNetModel>> getCustomersSocialNets(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.SOCIALNETS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<List<ContactMethodSocialNetModel>>() {
                });
    }

    public ResponseContainer<List<ContactMethodWebAddressModel>> getCustomersWebAddresses(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.WEBADDRESSES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<List<ContactMethodWebAddressModel>>() {
                });
    }

    public ResponseContainer<List<ContactMethodEmailModel>> getCustomersEmails(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.EMAILS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<List<ContactMethodEmailModel>>() {
                });
    }

    public ResponseContainer<List<ContactMethodChatModel>> getCustomersChats(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.CHATS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<List<ContactMethodChatModel>>() {
                });
    }

    public ResponseContainer<ContactMethodChatModel> postCustomersChat(String customerNumber, ContactMethodChatModel contactMethodChatModel) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.CHATS", RestClient.HttpMethod.POST, contactMethodChatModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<ContactMethodChatModel>() {
                });
    }

    public ResponseContainer<ContactMethodChatModel> putCustomersChat(String customerNumber, String contactId, ContactMethodChatModel contactMethodChatModel) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.CHATENTITY.ITEM", RestClient.HttpMethod.PUT, contactMethodChatModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).pathParam("contactId", contactId).build()),
                new GenericType<ContactMethodChatModel>() {
                });
    }

    public ResponseContainer<List<RelationshipModel>> getCustomersRelationships(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.RELATIONSHIPS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<List<RelationshipModel>>() {
                });
    }

    public ResponseContainer<List<AdditionalNameModel>> getCustomersAdditionalNames(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.ADDITIONAL.NAMES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<List<AdditionalNameModel>>() {
                });
    }

    public ResponseContainer<AdditionalNameModel> postCustomersAdditionalNames(String customerNumber, AdditionalNameModel additionalNameModel) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.ADDITIONAL.NAMES", RestClient.HttpMethod.POST, additionalNameModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<AdditionalNameModel>() {
                });
    }

    public ResponseContainer<AdditionalNameModel> putCustomersAdditionalNames(String customerNumber, String additionalNameId, AdditionalNameModel additionalNameModel) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.ADDITIONAL.ITEM", RestClient.HttpMethod.PUT, additionalNameModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber)
                        .pathParam("additionalNameId", additionalNameId).build()),
                new GenericType<AdditionalNameModel>() {
                });
    }

    public ResponseContainer<List<AdditionalNameModel>> getCustomersBusinessAdditionalNames(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.BUSINESS.ADDITIONAL.NAMES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<List<AdditionalNameModel>>() {
                });
    }

    public ResponseContainer<AdditionalNameModel> postCustomersBusinessAdditionalNames(String customerNumber, AdditionalNameModel additionalNameModel) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.BUSINESS.ADDITIONAL.NAMES", RestClient.HttpMethod.POST, additionalNameModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<AdditionalNameModel>() {
                });
    }

    public ResponseContainer<AdditionalNameModel> putCustomersBusinessAdditionalNames(String customerNumber, String additionalNameId, AdditionalNameModel additionalNameModel) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.BUSINESS.ADDITIONAL.ITEM", RestClient.HttpMethod.PUT, additionalNameModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber)
                        .pathParam("additionalNameId", additionalNameId).build()),
                new GenericType<AdditionalNameModel>() {
                });
    }

    public ResponseContainer<List<OpportunitiesModel>> getOpportunities(String customerNumber) {
        Response response = client.processRequest("OPPORTUNITIES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().queryParam(CUSTOMER_NUMBER_KEY, customerNumber).build());
        return new ResponseContainer<>(response, new GenericType<List<OpportunitiesModel>>() {
        });
    }

    public ResponseContainer<List<OpportunitiesModel>> getOpportunity(String customerNumber) {
        Response response = client.processRequest("OPPORTUNITY", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build());
        return new ResponseContainer<>(response, new GenericType<List<OpportunitiesModel>>() {
        });
    }

    public ResponseContainer<OpportunityModel> getOpportunityByID(String customerNumber, String opportunityId) {
        return new ResponseContainer<>(client.processRequest("OPPORTUNITY.ITEM", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber)
                        .pathParam("opportunityId", opportunityId).build()), OpportunityModel.class);
    }

    public ResponseContainer<OpportunityModel> postOpportunity(String customerNumber, TestData data) {
        return new ResponseContainer<>(client.processRequest("OPPORTUNITY", RestClient.HttpMethod.POST,
                FreemarkerEntity.from("customercore-rs/OPPORTUNITY.POST", data),
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), OpportunityModel.class);
    }

    public ResponseContainer<OpportunityModel> putOpportunity(TestData td, String customerNumber, String opportunityId) {
        return new ResponseContainer<>(client.processRequest("OPPORTUNITY.ITEM", RestClient.HttpMethod.PUT,
                td.adjust("opportunityId", opportunityId).adjust(CUSTOMER_NUMBER_KEY, customerNumber).resolveLinks(),
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber)
                        .pathParam("opportunityId", opportunityId).build()), OpportunityModel.class);
    }

    public ResponseContainer<CampaignModel> getCampaignByID(String campaignId) {
        return new ResponseContainer<>(client.processRequest("CAMPAIGNS.ID", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("campaignId", campaignId).build()), CampaignModel.class);
    }

    public ResponseContainer<List<CampaignModel>> getCampaigns(TestData queryData) {
        return new ResponseContainer<>(client.processRequest("CAMPAIGNS", RestClient.HttpMethod.GET, null,
                TestDataRequestContext.of(queryData)), new GenericType<List<CampaignModel>>() {
        });
    }

    public Response putCampaignByID(TestData data, String campaignID) {
        return client.processRequest("CAMPAIGNS.ID", RestClient.HttpMethod.PUT, data,
                InlineRequestContext.builder().pathParam("campaignId", campaignID).build());
    }

    public ResponseContainer<CampaignModel> postCampaigns(TestData data) {
        return new ResponseContainer<>(client.processRequest("CAMPAIGNS", RestClient.HttpMethod.POST, data,
                InlineRequestContext.builder().build()), CampaignModel.class);
    }

    public ResponseContainer<List<AccountModel>> getAccounts(String accountNumber, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("ACCOUNT", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().queryParam("accountNumber", accountNumber).queryParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<List<AccountModel>>() {
                });
    }

    public ResponseContainer<List<AccountModel>> getAccount(String accountNumber) {
        return new ResponseContainer<>(client.processRequest("ACCOUNT.ITEM", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("accountNumber", accountNumber).build()),
                new GenericType<List<AccountModel>>() {
                });
    }

    public ResponseContainer<List<CustomerModel>> getAccountCustomer(String accountNumber) {
        return new ResponseContainer<>(client.processRequest("ACCOUNT.CUSTOMER", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("accountNumber", accountNumber).build()),
                new GenericType<List<CustomerModel>>() {
                });
    }

    public ResponseContainer<CommunicationsModel> postCommunications(String customerNumber, TestData data) {
        return new ResponseContainer<>(client.processRequest("COMMUNICATIONS", RestClient.HttpMethod.POST,
                RestUtil.convert(data, CommunicationsModel.class),
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), CommunicationsModel.class);
    }

    public ResponseContainer<List<CommunicationModel>> getCommunications(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("COMMUNICATIONS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), new GenericType<List<CommunicationModel>>() {
        });
    }

    public ResponseContainer<CommunicationsModelThread> getCommunicationsThreads(String customerNumber, String communicationId, String threadId) {
        return new ResponseContainer<>(client.processRequest("THREADS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).pathParam("communicationId", communicationId)
                        .pathParam("threadId", threadId).build()), CommunicationsModelThread.class);
    }

    public Response putCommunicationsThreads(String customerNumber, String communicationId, String threadId, TestData data) {
        return client.processRequest("THREADS", RestClient.HttpMethod.PUT, RestUtil.convert(data, CommunicationThreadModel.class),
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).pathParam("communicationId", communicationId)
                        .pathParam("threadId", threadId).build());
    }

    public ResponseContainer<CommunicationsModelThread> postCommunicationsThreads(String customerNumber, String communicationId, TestData data) {
        return new ResponseContainer<>(client.processRequest("POST.THREADS", RestClient.HttpMethod.POST,
                RestUtil.convert(data, CommunicationThreadModel.class), InlineRequestContext.builder().
                        pathParam(CUSTOMER_NUMBER_KEY, customerNumber).pathParam("communicationId", communicationId).build()), CommunicationsModelThread.class);
    }

    public ResponseContainer<CommunicationModel> putCommunication(String customerNumber, String communicationId, TestData data) {
        return new ResponseContainer<>(client.processRequest("COMMUNICATION.ID", RestClient.HttpMethod.PUT,
                RestUtil.convert(data, CommunicationModel.class), InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber)
                        .pathParam("communicationId", communicationId).build()), CommunicationModel.class);
    }

    public ResponseContainer<CommunicationModel> getCommunication(String customerNumber, String communicationId) {
        return new ResponseContainer<>(client.processRequest("COMMUNICATION.ID", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber)
                        .pathParam("communicationId", communicationId).build()), CommunicationModel.class);
    }

    public ResponseContainer<ProviderModel> postProvider(String customerNumber, TestData data) {
        return new ResponseContainer<>(client.processRequest("PROVIDERS", RestClient.HttpMethod.POST,
                RestUtil.convert(data, ProviderModel.class), InlineRequestContext.builder()
                        .pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), ProviderModel.class);
    }

    public ResponseContainer<RelationshipModel> postRelationshipAndReturnEntity(String customerNumber, RelationshipModel relationshipModel) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.RELATIONSHIPS", RestClient.HttpMethod.POST, relationshipModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()),
                new GenericType<RelationshipModel>() {
                });
    }

    public Response postRelationship(String customerNumber, RelationshipModel relationshipModel) {
        return client.processRequest("CUSTOMERS.RELATIONSHIPS", RestClient.HttpMethod.POST, relationshipModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build());
    }

    public ResponseContainer<ProviderModel> putProviders(String customerNumber, String providerId, ProviderModel providerModel) {
        return new ResponseContainer<>(client.processRequest("PROVIDERS.ITEM", RestClient.HttpMethod.PUT, providerModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber)
                        .pathParam("providerId", providerId).build()), new GenericType<ProviderModel>() {
        });
    }

    public ResponseContainer<List<GroupSponsorModel>> getMembershipParticipants(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("GROUPSPONSORS.MEMBERSHIP", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("sponsorNumber", customerNumber).build()),
                new GenericType<List<GroupSponsorModel>>() {
                });
    }

    public ResponseContainer<List<GroupSponsorModel>> getEmploymentParticipants(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("GROUPSPONSORS.EMPLOYMENT", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("sponsorNumber", customerNumber).build()),
                new GenericType<List<GroupSponsorModel>>() {
                });
    }

    public ResponseContainer<List<GroupSponsorModel>> getStudentParticipants(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("GROUPSPONSORS.STUDENT", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam("sponsorNumber", customerNumber).build()),
                new GenericType<List<GroupSponsorModel>>() {
                });
    }

    public ResponseContainer<GroupSponsorModel> postParticipants(String customerNumber, GroupSponsorModel groupSponsorModel) {
        return new ResponseContainer<>(client.processRequest("GROUPSPONSORS.POST", RestClient.HttpMethod.POST, groupSponsorModel,
                InlineRequestContext.builder().pathParam("sponsorNumber", customerNumber).build()),
                new GenericType<GroupSponsorModel>() {
                });
    }

    public ResponseContainer<GroupSponsorModel> putParticipants(String customerNumber, String participantNumber, GroupSponsorModel groupSponsorModel) {
        return new ResponseContainer<>(client.processRequest("GROUPSPONSORS.PUT", RestClient.HttpMethod.PUT, groupSponsorModel,
                InlineRequestContext.builder().pathParam("sponsorNumber", customerNumber).pathParam("participantNumber", participantNumber).build()),
                new GenericType<GroupSponsorModel>() {
                });
    }

    public ResponseContainer<List<ProviderModel>> getProviders(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("PROVIDERS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), new GenericType<List<ProviderModel>>() {
        });
    }

    public ResponseContainer<List<RelationshipModel>> getRelationships(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("CUSTOMERS.RELATIONSHIPS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), new GenericType<List<RelationshipModel>>() {
        });
    }

    public ResponseContainer<RelationshipModel> putRelationships(String customerNumber, String relationshipId, RelationshipModel relationshipModel) {
        return new ResponseContainer<>(client.processRequest("RELATIONSHIPS.ITEM", RestClient.HttpMethod.PUT, relationshipModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).pathParam("relationshipId", relationshipId)
                        .build()), new GenericType<RelationshipModel>() {
        });
    }

    public ResponseContainer<List<EmploymentModel>> getEmployments(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYMENTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), new GenericType<List<EmploymentModel>>() {
        });
    }

    public ResponseContainer<EmploymentModel> postEmployments(String customerNumber, EmploymentModel employmentModel) {
        return new ResponseContainer<>(client.processRequest("EMPLOYMENTS", RestClient.HttpMethod.POST, employmentModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), new GenericType<EmploymentModel>() {
        });
    }

    public ResponseContainer<List<EmploymentModel>> putEmployment(String customerNumber, String employmentId, EmploymentModel employmentModel) {
        return new ResponseContainer<>(client.processRequest("EMPLOYMENTS.ITEM", RestClient.HttpMethod.PUT, employmentModel,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber)
                        .pathParam("employmentId", employmentId).build()), new GenericType<List<EmploymentModel>>() {
        });
    }

    public ResponseContainer<List<CustomerProviderModel>> getProviderLatest(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("GET.LATEST.PROVIDERS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().queryParam(CUSTOMER_NUMBER_KEY, customerNumber).build()), new GenericType<List<CustomerProviderModel>>() {
        });
    }

    public Response deleteEmploymentParticipant(String customerNumber, String participantNumber) {
        return client.processRequest("GROUPSPONSORS.EMPLOYMENT.ITEM", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().pathParam("sponsorNumber", customerNumber).pathParam("participantNumber", participantNumber)
                        .build());
    }

    public Response deleteMembershipParticipant(String customerNumber, String participantNumber) {
        return client.processRequest("GROUPSPONSORS.MEMBERSHIP.ITEM", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().pathParam("sponsorNumber", customerNumber).pathParam("participantNumber", participantNumber)
                        .build());
    }

    public Response deleteStudentParticipant(String customerNumber, String participantNumber) {
        return client.processRequest("GROUPSPONSORS.STUDENT.ITEM", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().pathParam("sponsorNumber", customerNumber).pathParam("participantNumber", participantNumber)
                        .build());
    }

    public Response deleteCustomersChatItem(String customerNumber, int contactId) {
        return deleteItem("CUSTOMERS.CHATENTITY.ITEM", customerNumber, CONTACT_ID_KEY, contactId);
    }

    public Response deleteAdditionalName(String customerNumber, int additionalNameId, boolean individual) {
        return deleteItem(individual ? "DELETE.INDADDITIONALNAME" : "DELETE.BUSINESSADDITIONALNAME", customerNumber, "additionalNameId", additionalNameId);
    }

    public Response deleteEmployment(String customerNumber, int employmentId) {
        return deleteItem("EMPLOYMENTS.ITEM", customerNumber, "employmentId", employmentId);
    }

    public Response deleteCustomersPhoneItem(String customerNumber, int contactId) {
        return deleteItem("CUSTOMERS.DELETEPHONEENTITY.ITEM", customerNumber, CONTACT_ID_KEY, contactId);
    }

    public Response deleteCustomersEmailItem(String customerNumber, int contactId) {
        return deleteItem("CUSTOMERS.DELETEEMAILENTITY.ITEM", customerNumber, CONTACT_ID_KEY, contactId);
    }

    public Response deleteCustomersWebaddressItem(String customerNumber, int contactId) {
        return deleteItem("CUSTOMERS.DELETEWEBADDRESSENTITY.ITEM", customerNumber, CONTACT_ID_KEY, contactId);
    }

    public Response deleteCustomersSocialNetItem(String customerNumber, int contactId) {
        return deleteItem("CUSTOMERS.SOCIALNETS.ITEM", customerNumber, CONTACT_ID_KEY, contactId);
    }

    public Response deleteProvider(String customerNumber, int providerId) {
        return deleteItem("PROVIDERS.ITEM", customerNumber, "providerId", providerId);
    }

    public Response deleteRelationship(String customerNumber, int relationshipId) {
        return deleteItem("RELATIONSHIPS.ITEM", customerNumber, "relationshipId", relationshipId);
    }

    private Response deleteItem(String requestAlias, String customerNumber, String valueParameterName, int itemId) {
        return client.processRequest(requestAlias, RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().pathParam(CUSTOMER_NUMBER_KEY, customerNumber).pathParam(valueParameterName, Integer.toString(itemId)).build());
    }

}
