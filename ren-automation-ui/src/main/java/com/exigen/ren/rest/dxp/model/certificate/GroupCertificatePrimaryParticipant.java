package com.exigen.ren.rest.dxp.model.certificate;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupCertificatePrimaryParticipant extends RestError {

    private GroupCertificatePolicyRelationshipEmpInfo groupCertificatePolicyRelationshipEmpInfo;
    private GroupCertificatePrimaryParticipantPersonInfoProxy groupCertificatePrimaryParticipantPersonInfoProxy;
    private GroupCertificateEligibilityInformation groupCertificateEligibilityInformation;

    public GroupCertificatePolicyRelationshipEmpInfo getGroupCertificatePolicyRelationshipEmpInfo() {
        return groupCertificatePolicyRelationshipEmpInfo;
    }

    public void setGroupCertificatePolicyRelationshipEmpInfo(GroupCertificatePolicyRelationshipEmpInfo groupCertificatePolicyRelationshipEmpInfo) {
        this.groupCertificatePolicyRelationshipEmpInfo = groupCertificatePolicyRelationshipEmpInfo;
    }

    public GroupCertificatePrimaryParticipantPersonInfoProxy getGroupCertificatePrimaryParticipantPersonInfoProxy() {
        return groupCertificatePrimaryParticipantPersonInfoProxy;
    }

    public void setGroupCertificatePrimaryParticipantPersonInfoProxy(GroupCertificatePrimaryParticipantPersonInfoProxy groupCertificatePrimaryParticipantPersonInfoProxy) {
        this.groupCertificatePrimaryParticipantPersonInfoProxy = groupCertificatePrimaryParticipantPersonInfoProxy;
    }

    public GroupCertificateEligibilityInformation getGroupCertificateEligibilityInformation() {
        return groupCertificateEligibilityInformation;
    }

    public void setGroupCertificateEligibilityInformation(GroupCertificateEligibilityInformation groupCertificateEligibilityInformation) {
        this.groupCertificateEligibilityInformation = groupCertificateEligibilityInformation;
    }
}