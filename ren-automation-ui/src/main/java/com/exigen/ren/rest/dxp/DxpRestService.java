package com.exigen.ren.rest.dxp;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ipb.eisa.ws.rest.entity.MultiPartEntity;
import com.exigen.ren.main.enums.DXPConstants;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.billing.model.*;
import com.exigen.ren.rest.customer.groupSponsors.model.PaperlessModel;
import com.exigen.ren.rest.dxp.model.*;
import com.exigen.ren.rest.dxp.model.billing.*;
import com.exigen.ren.rest.dxp.model.certificate.EmployerCertificatePoliciesDetailedModel;
import com.exigen.ren.rest.dxp.model.certificate.EmployerGroupsGroupMemberModel;
import com.exigen.ren.rest.dxp.model.claim.*;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsAccidentEmployerModels.ClaimsAccidentEmployerClaims;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsDisabilityEmployerModels.ClaimsDisabilityEmployerClaimsModel;
import com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsTermLifeEmployerModels.ClaimsTermLifeEmployerClaimsModel;
import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.RecoveryModel;
import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsAccidentMemberModels.ClaimsAccidentMemberClaimsModel;
import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDentalMemberModels.ClaimsDentalMemberClaimsModel;
import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsDisabilityMemberModels.ClaimsDisabilityMemberClaimsModel;
import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsTermLifeMemberModels.ClaimsTermLifeMemberClaimsModel;
import com.exigen.ren.rest.dxp.model.policy.EmployerMasterPoliciesDetailed;
import com.exigen.ren.rest.dxp.model.policy.policy_details_ac.PolicyViewMasterPolicyDetailsAC;
import com.exigen.ren.rest.dxp.model.policy.policy_details_dn.PolicyViewMasterPolicyDetailsDN;
import com.exigen.ren.rest.dxp.model.policy.policy_details_vs.PolicyViewMasterPolicyDetailsVS;
import com.exigen.ren.rest.model.RestError;
import com.exigen.ren.rest.platform.efolder.model.FoldersDocumentModel;
import com.fasterxml.jackson.databind.DeserializationFeature;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.List;

public class DxpRestService {

    private RestClient client;
    private JsonConfiguration conf;

    public DxpRestService() {
        conf = ConfigurationFactory.get(JsonConfiguration.class);
        conf.getObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        client = new RestClient("dxpapi", conf);
    }

    public ResponseContainer<EmployerProfileModel> getEmployerProfile(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.PROFILE", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                EmployerProfileModel.class);
    }

    public ResponseContainer<List<ProfileAddressesModel>> getEmployerMemberAddresses(String customerNumberAuthorize, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.MEMBERS.ADDRESSES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("customerNumber", customerNumber).build()),
                new GenericType<List<ProfileAddressesModel>>() {
                });
    }

    public ResponseContainer<ProfileAddressesModel> postEmployerMemberAddresses(String customerNumberAuthorize, ProfileAddressesModel model, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.MEMBERS.ADDRESSES", RestClient.HttpMethod.POST, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("customerNumber", customerNumber).build()),
                new GenericType<ProfileAddressesModel>() {
                });
    }

    public ResponseContainer<ProfileAddressesModel> putEmployerMemberAddresses(String customerNumberAuthorize, ProfileAddressesModel model, String customerNumber, String addressId) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.MEMBERS.ADDRESSES.ADDRESS", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("customerNumber", customerNumber).pathParam("addressId", addressId).build()),
                new GenericType<ProfileAddressesModel>() {
                });
    }

    public ResponseContainer<ProfileAddressesModel> deleteEmployerMemberAddresses(String customerNumberAuthorize, String customerNumber, String addressId) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.MEMBERS.ADDRESSES.ADDRESS", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("customerNumber", customerNumber).pathParam("addressId", addressId).build()),
                new GenericType<ProfileAddressesModel>() {
                });
    }

    public ResponseContainer<List<ProfileEmailModel>> getMemberProfileEmails(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.EMAILS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                new GenericType<List<ProfileEmailModel>>() {
                });
    }

    public ResponseContainer<List<String>> putMemberProfilePaperless(String customerNumber, PaperlessModel model) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.PAPERLESS", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                new GenericType<List<String>>() {
                });
    }

    public ResponseContainer<List<String>> putMemberProfileEmailsCommunicationPreferencess(String customerNumber, String emailId, List<String> model) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.EMAILS.ID.COMMUNICATION.PREFERENCES", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("emailId", emailId).build()),
                new GenericType<List<String>>() {
                });
    }

    public ResponseContainer<List<String>> putMemberProfileAddressesCommunicationPreferencess(String customerNumber, String addressId, List<String> model) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.ADDRESSES.ID.COMMUNICATION.PREFERENCES", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("addressId", addressId).build()),
                new GenericType<List<String>>() {
                });
    }

    public ResponseContainer<List<String>> getMemberProfileAcknowledgments(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.ACKNOWLEDGMENTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                new GenericType<List<String>>() {
                });
    }

    public ResponseContainer<List<String>> getEmployerProfileAcknowledgments(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.PROFILE.ACKNOWLEDGMENTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                new GenericType<List<String>>() {
                });
    }

    public ResponseContainer<List<String>> putMemberProfileAcknowledgments(String customerNumberAuthorize, List<String> list) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.ACKNOWLEDGMENTS", RestClient.HttpMethod.PUT, list,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).build()),
                new GenericType<List<String>>() {
                });
    }

    public ResponseContainer<List<String>> putEmployerProfileAcknowledgments(String customerNumberAuthorize, List<String> list) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.PROFILE.ACKNOWLEDGMENTS", RestClient.HttpMethod.PUT, list,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).build()),
                new GenericType<List<String>>() {
                });
    }

    public ResponseContainer<List<String>> deleteMemberProfileAcknowledgments(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.ACKNOWLEDGMENTS", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                new GenericType<List<String>>() {
                });
    }

    public ResponseContainer<List<String>> deleteEmployerProfileAcknowledgments(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.PROFILE.ACKNOWLEDGMENTS", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                new GenericType<List<String>>() {
                });
    }

    public ResponseContainer<ProfileEmailModel> postMemberProfileEmail(ProfileEmailModel email, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.EMAILS", RestClient.HttpMethod.POST, email,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                ProfileEmailModel.class);
    }

    public ResponseContainer<List<ProfileEmailModel>> getMemberProfileEmail(String authorizeCustomerNumber, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.CUSTOMER.NUMBER.EMAILS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", authorizeCustomerNumber).pathParam("customerNumber", customerNumber).build()),
                new GenericType<List<ProfileEmailModel>>() {
                });
    }

    public ResponseContainer<List<ProfilePhoneModel>> postMemberProfilePhones(ProfilePhoneModel model, String authorizeCustomerNumber, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.MEMBER.CUSTOMER.NUMBER.PHONES", RestClient.HttpMethod.POST, model,
                InlineRequestContext.builder().header("X-Customer-Number", authorizeCustomerNumber).pathParam("customerNumber", customerNumber).build()),
                new GenericType<List<ProfilePhoneModel>>() {
                });
    }

    public ResponseContainer<List<ClaimEmployerModel>> getEmployerMembersCustomerNumberClaims(
            String authorizeCustomerNumber, String customerNumber, String limit, String offset, String insuredFirstName, String sort,
            String lossDateFrom, String claimStatusCd, String reportedDateTill, String insuredLastName) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.MEMBER.CUSTOMER.NUMBER.CLAIMS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", authorizeCustomerNumber)
                        .pathParam("customerNumber", customerNumber)
                        .queryParam("limit", limit)
                        .queryParam("offset", offset)
                        .queryParam("insuredFirstName", insuredFirstName)
                        .queryParam("sort", sort)
                        .queryParam("lossDateFrom", lossDateFrom)
                        .queryParam("claimStatusCd", claimStatusCd)
                        .queryParam("reportedDateTill", reportedDateTill)
                        .queryParam("insuredLastName", insuredLastName)
                        .build()),
                new GenericType<List<ClaimEmployerModel>>() {
                });
    }

    public ResponseContainer<ProfileEmailModel> deleteMemberProfileEmailByCustomerNumber(String customerNumberAuthorize, String customerNumber, String emailId) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.CUSTOMER.NUMBER.EMAILS.EMAIL", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("customerNumber", customerNumber)
                        .pathParam("emailId", emailId).build()),
                ProfileEmailModel.class);
    }

    public ResponseContainer<ProfileEmailModel> putMemberProfileEmailByCustomerNumber(String customerNumberAuthorize, ProfileEmailModel email, String customerNumber, String emailId) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.CUSTOMER.NUMBER.EMAILS.EMAIL", RestClient.HttpMethod.PUT, email,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("customerNumber", customerNumber)
                        .pathParam("emailId", emailId).build()),
                ProfileEmailModel.class);
    }

    public ResponseContainer<ProfileEmailModel> putMemberProfileEmail(ProfileEmailModel email, String customerNumber, String emailId) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.EMAILS.ID", RestClient.HttpMethod.PUT, email,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("emailId", emailId).build()),
                ProfileEmailModel.class);
    }

    public ResponseContainer<ProfileEmailModel> deleteMemberProfileEmail(String customerNumber, String emailId) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.EMAILS.ID", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("emailId", emailId).build()),
                ProfileEmailModel.class);
    }

    public ResponseContainer<List<ProfilePhoneModel>> getMemberProfilePhones(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.PHONES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                new GenericType<List<ProfilePhoneModel>>() {
                });
    }

    public ResponseContainer<ProfilePhoneModel> postMemberProfilePhone(ProfilePhoneModel email, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.PHONES", RestClient.HttpMethod.POST, email,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                ProfilePhoneModel.class);
    }

    public ResponseContainer<ProfilePhoneModel> putMemberProfilePhone(ProfilePhoneModel email, String customerNumber, String phoneId) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.PHONES.ID", RestClient.HttpMethod.PUT, email,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("phoneId", phoneId).build()),
                ProfilePhoneModel.class);
    }

    public ResponseContainer<ProfilePhoneModel> deleteMemberProfilePhone(String customerNumber, String phoneId) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.PHONES.ID", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("phoneId", phoneId).build()),
                ProfilePhoneModel.class);
    }

    public ResponseContainer<CustomerRenContacts> putMemberProfileCommunicationPreferences(String customerNumber, CustomerRenContacts model) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE.COMMUNICATION-PREFERENCES", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                CustomerRenContacts.class);
    }

    public ResponseContainer<MemberProfileModel> getMemberProfile(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.PROFILE", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                MemberProfileModel.class);
    }

    public ResponseContainer<List<EmployerGroupsModel>> getEmployerGroups(String customerNumber, String groupCustomerNumber, String legalName, String legalId, String billingAccountNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .queryParam("groupCustomerNumber", groupCustomerNumber)
                        .queryParam("legalName", legalName)
                        .queryParam("legalId", legalId)
                        .queryParam("billingAccountNumber", billingAccountNumber)
                        .build()),
                new GenericType<List<EmployerGroupsModel>>() {
                });
    }

    public ResponseContainer<List<EmployerGroupsGroupMemberModel>> getEmployerGroupsGroupMember(String customerNumberAuthorize, String groupNumber, String firstName, String lastName,
                                                                                                String customerNumber, String activeCoverages, String stateProvCd,
                                                                                                String dateOfHireFrom, String dateOfHireTo, String taxId, String sort) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.NUMBER.MEMBER", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("groupNumber", groupNumber)
                        .queryParam("firstName", firstName)
                        .queryParam("lastName", lastName)
                        .queryParam("customerNumber", customerNumber)
                        .queryParam("activeCoverages", activeCoverages)
                        .queryParam("stateProvCd", stateProvCd)
                        .queryParam("dateOfHireFrom", dateOfHireFrom)
                        .queryParam("dateOfHireTo", dateOfHireTo)
                        .queryParam("taxId", taxId)
                        .queryParam("sort", sort)
                        .build()),
                new GenericType<List<EmployerGroupsGroupMemberModel>>() {
                });
    }

    public ResponseContainer<ProfilePhoneModel> postEmployerGroupsContactPersonsPhones(String customerNumberAuthorize, String groupNumber, String customerNumber, ProfilePhoneModel model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.CONTACT.PERSONS.PHONES", RestClient.HttpMethod.POST, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("groupNumber", groupNumber)
                        .pathParam("customerNumber", customerNumber).build()), new GenericType<ProfilePhoneModel>() {
        });
    }

    public ResponseContainer<List<ProfilePhoneModel>> getEmployerGroupsContactPersonsPhones(String customerNumberAuthorize, String groupNumber, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.CONTACT.PERSONS.PHONES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("groupNumber", groupNumber)
                        .pathParam("customerNumber", customerNumber).build()), new GenericType<List<ProfilePhoneModel>>() {
        });
    }

    public ResponseContainer<ProfilePhoneModel> putEmployerGroupsContactPersonsPhones(String customerNumberAuthorize, String groupNumber, String customerNumber, String phoneId, ProfilePhoneModel model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.CONTACT.PERSONS.PHONES.ID", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("groupNumber", groupNumber).pathParam("phoneId", phoneId)
                        .pathParam("customerNumber", customerNumber).build()), new GenericType<ProfilePhoneModel>() {
        });
    }

    public ResponseContainer<ProfilePhoneModel> deleteEmployerGroupsContactPersonsPhones(String customerNumberAuthorize, String groupNumber, String customerNumber, String phoneId) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.CONTACT.PERSONS.PHONES.ID", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("groupNumber", groupNumber).pathParam("phoneId", phoneId)
                        .pathParam("customerNumber", customerNumber).build()), new GenericType<ProfilePhoneModel>() {
        });
    }

    public ResponseContainer<ProfileEmployerEmailModel> postEmployerGroupsContactPersonsEmails(String customerNumberAuthorize, String groupNumber, String customerNumber, ProfileEmployerEmailModel model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.CONTACT.PERSONS.EMAILS", RestClient.HttpMethod.POST, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("groupNumber", groupNumber)
                        .pathParam("customerNumber", customerNumber).build()), new GenericType<ProfileEmployerEmailModel>() {
        });
    }

    public ResponseContainer<List<ProfileEmployerEmailModel>> getEmployerGroupsContactPersonsEmails(String customerNumberAuthorize, String groupNumber, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.CONTACT.PERSONS.EMAILS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("groupNumber", groupNumber)
                        .pathParam("customerNumber", customerNumber).build()), new GenericType<List<ProfileEmployerEmailModel>>() {
        });
    }

    public ResponseContainer<ProfileEmployerEmailModel> putEmployerGroupsContactPersonsEmails(String customerNumberAuthorize, String groupNumber, String customerNumber, String emailId, ProfileEmployerEmailModel model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.CONTACT.PERSONS.EMAILS.ID", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("groupNumber", groupNumber).pathParam("emailId", emailId)
                        .pathParam("customerNumber", customerNumber).build()), new GenericType<ProfileEmployerEmailModel>() {
        });
    }

    public ResponseContainer<ProfileEmployerEmailModel> deleteEmployerGroupsContactPersonsEmails(String customerNumberAuthorize, String groupNumber, String customerNumber, String emailId) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.CONTACT.PERSONS.EMAILS.ID", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("groupNumber", groupNumber).pathParam("emailId", emailId)
                        .pathParam("customerNumber", customerNumber).build()), new GenericType<ProfileEmployerEmailModel>() {
        });
    }

    public ResponseContainer<RestError> putEmployerGroupsGroupContactPersonsPaperless(String customerNumberAuthorize, String groupNumber, String customerNumber, PaperlessModel model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.CONTACT.PERSONS.PAPERLESS", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("groupNumber", groupNumber)
                        .pathParam("customerNumber", customerNumber)
                        .build()), new GenericType<RestError>() {
        });
    }

    public ResponseContainer<CustomerRenContacts> putEmployerGroupsGroupContactPersonsCommunicationPreferences(String customerNumberAuthorize, String groupNumber, String customerNumber, CustomerRenContacts model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP.CONTACT.PERSONS.COMMUNICATION-PREFERENCES", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("groupNumber", groupNumber)
                        .pathParam("customerNumber", customerNumber)
                        .build()), new GenericType<CustomerRenContacts>() {
        });
    }

    public ResponseContainer<List<CustomerGroupContactModel>> getEmployerGroupsGroupNumberContacts(String customerNumberAuthorize, String groupNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP_NUMBER.CONTACTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("groupNumber", groupNumber)
                        .build()), new GenericType<List<CustomerGroupContactModel>>() {
        });
    }

    public ResponseContainer<CustomerGroupContactRelationshipModel> postEmployerGroupsGroupNumberContactRelationships(String customerNumberAuthorize, String groupNumber, CustomerGroupContactRelationshipModel model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP_NUMBER.CONTACT-RELATIONSHIPS", RestClient.HttpMethod.POST, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("groupNumber", groupNumber)
                        .build()), new GenericType<CustomerGroupContactRelationshipModel>() {
        });
    }

    public ResponseContainer<RestError> deleteEmployerContactRelationship(String customerNumberAuthorize, String groupNumber, String contactRelationshipId) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP_NUMBER.CONTACT-RELATIONSHIPS.CONTACT_RELATIONSHIP_ID", RestClient.HttpMethod.DELETE, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("groupNumber", groupNumber)
                        .pathParam("contactRelationshipId", contactRelationshipId)
                        .build()), new GenericType<RestError>() {
        });
    }

    public ResponseContainer<CustomerGroupContactRelationshipModel> putEmployerContactRelationship(String customerNumberAuthorize, String groupNumber, String contactRelationshipId, CustomerGroupContactRelationshipModel model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP_NUMBER.CONTACT-RELATIONSHIPS.CONTACT_RELATIONSHIP_ID", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("groupNumber", groupNumber)
                        .pathParam("contactRelationshipId", contactRelationshipId)
                        .build()), new GenericType<CustomerGroupContactRelationshipModel>() {
        });
    }

    public ResponseContainer<EmployerGroupModel> getEmployerGroupsNumber(String customerNumberAuthorize, String groupNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUPS.GROUP_NUMBER", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("groupNumber", groupNumber)
                        .build()), new GenericType<EmployerGroupModel>() {
        });
    }

    public ResponseContainer<List<CertificatesModel>> getMemberCertificates(String customerNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.CERTIFICATES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                new GenericType<List<CertificatesModel>>() {
                });
    }

    public ResponseContainer<CertificatesModel> getMemberCertificatesByProduct(DXPConstants.MemberCertificates config, String customerNumber, String certificateNumber, String effectiveDate) {
        return new ResponseContainer<>(client.processRequest(config.getName(), RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("certificateNumber", certificateNumber)
                        .queryParam("transactionEffectiveDate", effectiveDate).build()),
                new GenericType<CertificatesModel>() {
                });
    }

    public ResponseContainer<MemberProfileModel> getEmployerMemberProfile(String customerNumberAuthorize, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.MEMBER.PROFILE", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("customerNumber", customerNumber).build()),
                MemberProfileModel.class);
    }

    public ResponseContainer<List<EmployerGoupsMasterPoliciesModel>> getEmployerGroupMasterPolicies(String customerNumberAuthorize, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.GROUP.MASTER.POLICIES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("customerNumber", customerNumber).build()),
                new GenericType<List<EmployerGoupsMasterPoliciesModel>>() {
                });
    }

    public ResponseContainer<List<RegistrationMembers>> getEmployerRegistrationMembers(String customerNumber, String firstName, String lastName, String email, String phone, String ssn, String policyNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.REGISTRATION.MEMBERS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("customerNumber", customerNumber)
                        .queryParam("firstName", firstName)
                        .queryParam("lastName", lastName)
                        .queryParam("email", email)
                        .queryParam("phone", phone)
                        .queryParam("ssn", ssn)
                        .queryParam("policyNumber", policyNumber)
                        .build()),
                new GenericType<List<RegistrationMembers>>() {
                });
    }

    public ResponseContainer<List<RegistrationMembers>> getMemberRegistrationMembers(String customerNumber, String firstName, String lastName, String email, String phone, String ssn) {
        return new ResponseContainer<>(client.processRequest("MEMBER.REGISTRATION.MEMBERS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("customerNumber", customerNumber).queryParam("firstName", firstName)
                        .queryParam("lastName", lastName).queryParam("email", email).queryParam("phone", phone).queryParam("ssn", ssn)
                        .build()),
                new GenericType<List<RegistrationMembers>>() {
                });
    }

    public ResponseContainer<List<FoldersDocumentModel>> getEmployerFolders(String customerNumber, String entityType) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.FOLDERS.ENTITY.TYPE", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("entityType", entityType).build()),
                new GenericType<List<FoldersDocumentModel>>() {
                });
    }

    public ResponseContainer<List<FoldersDocumentModel>> getMemberFolders(String customerNumber, String entityType) {
        return new ResponseContainer<>(client.processRequest("MEMBER.FOLDERS.ENTITY.TYPE", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("entityType", entityType).build()),
                new GenericType<List<FoldersDocumentModel>>() {
                });
    }

    public ResponseContainer<List<FoldersDocumentModel>> getEmployerFoldersRefNo(String customerNumber, String entityType, String entityRefNo) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.FOLDERS.ENTITY.TYPE.ENTITYREFNO", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("entityType", entityType)
                        .pathParam("entityRefNo", entityRefNo).build()),
                new GenericType<List<FoldersDocumentModel>>() {
                });
    }

    public ResponseContainer<List<FoldersDocumentModel>> getMemberFoldersRefNo(String customerNumber, String entityType, String entityRefNo) {
        return new ResponseContainer<>(client.processRequest("MEMBER.FOLDERS.ENTITY.TYPE.ENTITYREFNO", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("entityType", entityType)
                        .pathParam("entityRefNo", entityRefNo).build()),
                new GenericType<List<FoldersDocumentModel>>() {
                });
    }

    public ResponseContainer<UUIDModel> postEmployerFolders(String customerNumberAuthorize, String entityRefNo, String entityType, String folderUuid, String uploadedFileName, String documentType, String fileName,
                                                            String comment) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.FOLDERS", RestClient.HttpMethod.POST, MultiPartEntity.from(uploadedFileName),
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("entityRefNo", entityRefNo)
                        .pathParam("entityType", entityType)
                        .pathParam("folderUuid", folderUuid)
                        .queryParam("documentType", documentType)
                        .queryParam("fileName", fileName)
                        .queryParam("comment", comment)
                        .build()), UUIDModel.class);
    }

    public ResponseContainer<UUIDModel> postMemberFolders(String customerNumberAuthorize, String entityRefNo, String entityType, String folderUuid, String uploadedFileName, String documentType, String fileName,
                                                          String comment) {
        return new ResponseContainer<>(client.processRequest("MEMBER.FOLDERS", RestClient.HttpMethod.POST, MultiPartEntity.from(uploadedFileName),
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("entityRefNo", entityRefNo)
                        .pathParam("entityType", entityType)
                        .pathParam("folderUuid", folderUuid)
                        .queryParam("documentType", documentType)
                        .queryParam("fileName", fileName)
                        .queryParam("comment", comment)
                        .build()),
                UUIDModel.class);
    }

    public ResponseContainer<EmployerMasterPoliciesDetailed> getEmployerMasterPoliciesDetailed(DXPConstants.EmployerMasterPolicies config, String customerNumberAuthorize, String customerNumber, String masterPolicyNumber, String effectiveDate) {
        return new ResponseContainer<>(client.processRequest(config.getName(), RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize)
                        .pathParam("customerNumber", customerNumber).pathParam("masterPolicyNumber", masterPolicyNumber)
                        .queryParam("transactionEffectiveDate", effectiveDate)
                        .build()),
                new GenericType<EmployerMasterPoliciesDetailed>() {
                });
    }

    public Response getEmployerDocumentByUUID(String customerNumber, String uuid) {
        return client.processRequest("EMPLOYER.FOLDERS.DOCUMENTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .header("accept", "application/octet-stream").pathParam("uuid", uuid).build());
    }

    public Response getMemberDocumentByUUID(String customerNumber, String uuid) {
        return client.processRequest("MEMBER.FOLDERS.DOCUMENTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .header("accept", "application/octet-stream").pathParam("uuid", uuid).build());
    }

    public ResponseContainer<RestError> postCertificateCancellationReason(DXPConstants.CertificateCancellationReason config, String customerNumber, String certificatePolicyNumber, String cancelDate, String cancellationReason, String supportingData) {
        return new ResponseContainer<>(client.processRequest(config.getName(), RestClient.HttpMethod.POST, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("certificatePolicyNumber", certificatePolicyNumber)
                        .queryParam("cancelDate", cancelDate).queryParam("cancellationReason", cancellationReason).queryParam("supportingData", supportingData).build()),
                new GenericType<RestError>() {
                });
    }

    public ResponseContainer<RestError> postCertificateRescindCancellationReason(DXPConstants.CertificateRescindCancellationReason config, String customerNumber, String certificatePolicyNumber, String rescindCancellationReason) {
        return new ResponseContainer<>(client.processRequest(config.getName(), RestClient.HttpMethod.POST, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("certificatePolicyNumber", certificatePolicyNumber)
                        .queryParam("rescindCancellationReason", rescindCancellationReason).build()),
                new GenericType<RestError>() {
                });
    }

    public ResponseContainer<BillingGenerateDraftBillModel> postEmployerBillingGenerateDraft(String customerNumber, String accountNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.ACCOUNTS.STATEMENTS.GENERATE.DRAFT", RestClient.HttpMethod.POST, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("accountNumber", accountNumber).build()),
                new GenericType<BillingGenerateDraftBillModel>() {
                });
    }

    public ResponseContainer<List<BillingInvoiceCoveragePeriodModel>> getEmployerBillingAccountsSelfBillingInvoiceCoveragePeriods(String customerNumber, String accountNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.ACCOUNTS.SELF.BILLING.INVOICE.COVERAGE.PERIODS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("accountNumber", accountNumber).build()),
                new GenericType<List<BillingInvoiceCoveragePeriodModel>>() {
                });
    }

    public ResponseContainer<InvoiceDetailsModel> getEmployerBillingAccountsInvoices(String customerNumber, String accountNumber, String invoiceNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.ACCOUNTS.INVOICES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("accountNumber", accountNumber)
                        .pathParam("invoiceNumber", invoiceNumber)
                        .build()), InvoiceDetailsModel.class);
    }

    public ResponseContainer<BillingInvoiceCoveragePeriodDateModel> getEmployerBillingAccountsSelfBillingInvoiceCoveragePeriodsWithDate(String customerNumber, String accountNumber,
                                                                                                                                        String startDate, String endDate) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.ACCOUNTS.SELF.BILLING.INVOICE.COVERAGE.PERIODS.WITH.DATE", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("accountNumber", accountNumber)
                        .pathParam("startDate", startDate).pathParam("endDate", endDate).build()),
                new GenericType<BillingInvoiceCoveragePeriodDateModel>() {
                });
    }

    public ResponseContainer<BillingInvoiceCoveragePeriodDateModel> getEmployerBillingAccountsSelfBillingInvoiceCoveragePeriodsWithDateSubmit(
            String customerNumber, String accountNumber, String startDate, String endDate, SelfBillingInvoiceCoveragePeriodDate model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.ACCOUNTS.SELF.BILLING.INVOICE.COVERAGE.PERIODS.WITH.DATE.SUBMIT", RestClient.HttpMethod.POST, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("accountNumber", accountNumber)
                        .pathParam("startDate", startDate).pathParam("endDate", endDate).build()),
                new GenericType<BillingInvoiceCoveragePeriodDateModel>() {
                });
    }

    public ResponseContainer<List<BillingAccountsModel>> getEmployerBillingAccounts(String customerNumber, String groupCustomerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.ACCOUNTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("groupCustomerNumber", groupCustomerNumber).build()),
                new GenericType<List<BillingAccountsModel>>() {
                });
    }

    public ResponseContainer<List<GeneralPaymentMethodModel>> getEmployerBillingPaymentMethods(String customerNumber, String groupCustomerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.PAYMENT.METHODS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("groupCustomerNumber", groupCustomerNumber).build()),
                new GenericType<List<GeneralPaymentMethodModel>>() {
                });
    }

    public ResponseContainer<List<BillingBenefitsCustomerInvoiceModel>> getEmployerBillingGroupsStatements(
            String customerNumber, String groupCustomerNumber, String accountNumber, String invoiceNumber, String dueDateStart, String dueDateEnd,
            String offset, String limit) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.GROUPS.STATEMENTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("groupCustomerNumber", groupCustomerNumber)
                        .queryParam("accountNumber", accountNumber)
                        .queryParam("invoiceNumber", invoiceNumber)
                        .queryParam("dueDateStart", dueDateStart)
                        .queryParam("dueDateEnd", dueDateEnd)
                        .queryParam("offset", offset)
                        .queryParam("limit", limit)
                        .build()),
                new GenericType<List<BillingBenefitsCustomerInvoiceModel>>() {
                });
    }

    public ResponseContainer<List<BillingHistoryInvoiceDetailsModel>> getEmployerBillingGroupsBillingHistory(String customerNumber, String groupCustomerNumber, String offset, String limit, String accountNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.GROUPS.BILLING-HISTORY", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("groupCustomerNumber", groupCustomerNumber)
                        .queryParam("accountNumber", accountNumber)
                        .queryParam("offset", offset)
                        .queryParam("limit", limit)
                        .build()),
                new GenericType<List<BillingHistoryInvoiceDetailsModel>>() {
                });
    }

    public ResponseContainer<BillingAccountRecurringPaymentMethodModel> getEmployerBillingRecurringPaymentsSettings(String customerNumber, String accountNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.RECURRING.PAYMENTS.SETTINGS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("accountNumber", accountNumber).build()),
                new GenericType<BillingAccountRecurringPaymentMethodModel>() {
                });
    }

    public ResponseContainer<RestError> putEmployerBillingRecurringPaymentsSettings(String customerNumber, String accountNumber, UpdatePaymentMethodModel model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.RECURRING.PAYMENTS.SETTINGS", RestClient.HttpMethod.PUT, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("accountNumber", accountNumber).build()),
                new GenericType<RestError>() {
                });
    }

    public ResponseContainer<RestError> postEmployerBillingMakePayment(String customerNumber, String referenceNumber, String accountNumber,
                                                                       String invoiceNumber, BenefitsPaymentInputDataModel model) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.MAKE.PAYMENT", RestClient.HttpMethod.POST, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("accountNumber", accountNumber)
                        .queryParam("referenceNumber", referenceNumber)
                        .queryParam("invoiceNumber", invoiceNumber)
                        .build()),
                new GenericType<RestError>() {
                });
    }

    public ResponseContainer<BillingGenerateBillModel> postEmployerBillingGenerate(String customerNumber, String accountNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.ACCOUNTS.STATEMENTS.GENERATE", RestClient.HttpMethod.POST, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("accountNumber", accountNumber).build()),
                new GenericType<BillingGenerateBillModel>() {
                });
    }

    public ResponseContainer<DXPBillingAccountsDocumentGenerationStatus> getEmployerBillingGenerationStatus(String customerNumber, String docgenTicket) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.BILLING.ACCOUNTS.STATEMENTS.GENERATION.STATUS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("docgenTicket", docgenTicket).build()),
                new GenericType<DXPBillingAccountsDocumentGenerationStatus>() {
                });
    }

    public ResponseContainer<ClaimsAccidentEmployerClaims> getEmployerClaimsAccident(String customerNumber, String claimNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.CLAIMS.ACCIDENT", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("claimNumber", claimNumber)
                        .build()), ClaimsAccidentEmployerClaims.class);
    }

    public ResponseContainer<ClaimsAccidentMemberClaimsModel> getMemberClaimsAccident(String customerNumber, String claimNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.CLAIMS.ACCIDENT", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("claimNumber", claimNumber)
                        .build()), ClaimsAccidentMemberClaimsModel.class);
    }

    public ResponseContainer<ClaimsDentalMemberClaimsModel> getMemberClaimsDental(String customerNumberToAuthorize, String claimNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.CLAIMS.DENTAL", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberToAuthorize)
                        .pathParam("claimNumber", claimNumber)
                        .build()), ClaimsDentalMemberClaimsModel.class);
    }

    public ResponseContainer<ClaimsTermLifeEmployerClaimsModel> getEmployerClaimsTermLife(String customerNumber, String claimNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.CLAIMS.TERM.LIFE", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("claimNumber", claimNumber)
                        .build()), ClaimsTermLifeEmployerClaimsModel.class);
    }

    public ResponseContainer<ClaimsTermLifeMemberClaimsModel> getMemberClaimsTermLife(String customerNumber, String claimNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.CLAIMS.TERM.LIFE", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("claimNumber", claimNumber)
                        .build()), ClaimsTermLifeMemberClaimsModel.class);
    }

    public ResponseContainer<ClaimsDisabilityEmployerClaimsModel> getEmployerClaimsDisability(String customerNumber, String claimNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.CLAIMS.DISABILITY", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("claimNumber", claimNumber)
                        .build()), ClaimsDisabilityEmployerClaimsModel.class);
    }

    public ResponseContainer<ClaimsDisabilityMemberClaimsModel> getMemberClaimsDisability(String customerNumber, String claimNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.CLAIMS.DISABILITY", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber)
                        .pathParam("claimNumber", claimNumber)
                        .build()), ClaimsDisabilityMemberClaimsModel.class);
    }

    public ResponseContainer<ClaimFNOLModel> postMemberClaimFNOL(DXPConstants.MemberClaimFNOL config, String customerNumber, DXPClaimsFNOLClaimBodyModel model) {
        return new ResponseContainer<>(client.processRequest(config.getName(), RestClient.HttpMethod.POST, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                ClaimFNOLModel.class);
    }

    public ResponseContainer<ClaimFNOLModel> postEmployerClaimFNOL(DXPConstants.EmployerClaimFNOL config, String customerNumber, DXPClaimsFNOLClaimBodyModel model) {
        return new ResponseContainer<>(client.processRequest(config.getName(), RestClient.HttpMethod.POST, model,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).build()),
                ClaimFNOLModel.class);
    }

    public ResponseContainer<List<CertificatesModel>> getEmployerMembersCertificates(String customerNumberAuthorize, String customerNumber) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.MEMBERS.CERTIFICATES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumberAuthorize).pathParam("customerNumber", customerNumber).build()),
                new GenericType<List<CertificatesModel>>() {
                });
    }

    public ResponseContainer<CertificatesPremiumSummariesModel> getEmployerCertificatesPremiumByProduct(DXPConstants.EmployerCertificatesPremium config, String customerNumber, String certificateNumber, String effectiveDate) {
        return new ResponseContainer<>(client.processRequest(config.getName(), RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("certificateNumber", certificateNumber)
                        .queryParam("transactionEffectiveDate", effectiveDate).build()),
                new GenericType<CertificatesPremiumSummariesModel>() {
                });
    }

    public ResponseContainer<EmployerCertificatePoliciesDetailedModel> getCertificatesPremiumByProduct(DXPConstants.EmployerCertificates config, String customerNumber, String certificateNumber, String effectiveDate) {
        return new ResponseContainer<>(client.processRequest(config.getName(), RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("certificateNumber", certificateNumber)
                        .queryParam("transactionEffectiveDate", effectiveDate).build()),
                new GenericType<EmployerCertificatePoliciesDetailedModel>() {
                });
    }

    public ResponseContainer<CertificatesPremiumSummariesModel> getMemberCertificatesPremiumByProduct(DXPConstants.MemberCertificatesPremium config, String customerNumber, String certificateNumber, String effectiveDate) {
        return new ResponseContainer<>(client.processRequest(config.getName(), RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("certificateNumber", certificateNumber)
                        .queryParam("transactionEffectiveDate", effectiveDate).build()),
                new GenericType<CertificatesPremiumSummariesModel>() {
                });
    }

    public ResponseContainer<PolicyViewMasterPolicyDetailsVS> getMemberMasterPolicyDetailsVS(String customerNumber, String certificateNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.CERTIFICATES.VISION.PLAN.DETAILS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("certificateNumber", certificateNumber).build()),
                new GenericType<PolicyViewMasterPolicyDetailsVS>() {
                });
    }

    public ResponseContainer<PolicyViewMasterPolicyDetailsDN> getMemberMasterPolicyDetailsDN(String customerNumber, String certificateNumber) {
        return new ResponseContainer<>(client.processRequest("MEMBER.CERTIFICATES.DENTAL.PLAN.DETAILS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("certificateNumber", certificateNumber).build()),
                new GenericType<PolicyViewMasterPolicyDetailsDN>() {
                });
    }

    public ResponseContainer<PolicyViewMasterPolicyDetailsAC> getMemberMasterPolicyDetailsAC(String customerNumber, String certificateNumber, String transactionEffectiveDate) {
        return new ResponseContainer<>(client.processRequest("MEMBER.CERTIFICATES.ACCIDENT.PLAN.DETAILS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("X-Customer-Number", customerNumber).pathParam("certificateNumber", certificateNumber)
                        .queryParam("transactionEffectiveDate", transactionEffectiveDate).build()),
                new GenericType<PolicyViewMasterPolicyDetailsAC>() {
                });
    }

    public ResponseContainer<List<ClaimEmployerModel>> getMemberClaims(
            String customerNumber, String limit, String offset, String insuredFirstName, String sort,
            String lossDateFrom, String claimStatusCd, String reportedDateTill, String insuredLastName) {
        return new ResponseContainer<>(client.processRequest("MEMBER.CLAIMS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder()
                        .header("X-Customer-Number", customerNumber)
                        .queryParam("limit", limit)
                        .queryParam("offset", offset)
                        .queryParam("insuredFirstName", insuredFirstName)
                        .queryParam("sort", sort)
                        .queryParam("lossDateFrom", lossDateFrom)
                        .queryParam("claimStatusCd", claimStatusCd)
                        .queryParam("reportedDateTill", reportedDateTill)
                        .queryParam("insuredLastName", insuredLastName)
                        .build()), new GenericType<List<ClaimEmployerModel>>() {
        });
    }

    public ResponseContainer<List<RenClaimNoteModel>> getClaimsNotes(DXPConstants.ClaimsNotes claimsNotesConfig, String customerNumber, String claimNumber, String limit, String offset) {
        return new ResponseContainer<>(client.processRequest(claimsNotesConfig.getName(), RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder()
                        .header("X-Customer-Number", customerNumber)
                        .pathParam("claimNumber", claimNumber)
                        .queryParam("limit", limit)
                        .queryParam("offset", offset)
                        .build()), new GenericType<List<RenClaimNoteModel>>() {
        });
    }

    public ResponseContainer<List<RecoveryModel>> getMemberClaimsRecovery(
            String customerNumber, String claimNumber, String checkNumber, String recoveryStatus, String issueDateFrom, String issueDateTill) {
        return new ResponseContainer<>(client.processRequest("MEMBER.CLAIMS.RECOVERIES", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder()
                        .header("X-Customer-Number", customerNumber)
                        .pathParam("claimNumber", claimNumber)
                        .queryParam("checkNumber", checkNumber)
                        .queryParam("recoveryStatus", recoveryStatus)
                        .queryParam("issueDateFrom", issueDateFrom)
                        .queryParam("issueDateTill", issueDateTill)
                        .build()), new GenericType<List<RecoveryModel>>() {
        });
    }

    public ResponseContainer<List<ClaimsPaymentModel>> getEmployerClaimsPayments(String customerNumber, String claimNumber, String paymentStatus,
                                                                                 String issueDateFrom, String issueDateTill,
                                                                                 String fromDtFrom, String fromDtTill,
                                                                                 String throughDtFrom, String throughDtTill) {
        return new ResponseContainer<>(client.processRequest("EMPLOYER.CLAIMS.PAYMENTS", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder()
                        .header("X-Customer-Number", customerNumber)
                        .pathParam("claimNumber", claimNumber)
                        .queryParam("paymentStatus", paymentStatus)
                        .queryParam("issueDateFrom", issueDateFrom)
                        .queryParam("issueDateTill", issueDateTill)
                        .queryParam("fromDtFrom", fromDtFrom)
                        .queryParam("fromDtTill", fromDtTill)
                        .queryParam("throughDtFrom", throughDtFrom)
                        .queryParam("throughDtTill", throughDtTill)
                        .build()), new GenericType<List<ClaimsPaymentModel>>() {
        });
    }

}
