package com.exigen.ren.rest.dxp.model.certificate;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupCertificateParticipantGroups extends RestError {

    private PreconfigGroupCertificateCoverage preconfigGroupCertificateCoverage;

    private GroupCertificateCoverage groupCertificateCoverage;

    public PreconfigGroupCertificateCoverage getPreconfigGroupCertificateCoverage() {
        return preconfigGroupCertificateCoverage;
    }

    public void setPreconfigGroupCertificateCoverage(PreconfigGroupCertificateCoverage preconfigGroupCertificateCoverage) {
        this.preconfigGroupCertificateCoverage = preconfigGroupCertificateCoverage;
    }

    public GroupCertificateCoverage getGroupCertificateCoverage() {
        return groupCertificateCoverage;
    }

    public void setGroupCertificateCoverage(GroupCertificateCoverage groupCertificateCoverage) {
        this.groupCertificateCoverage = groupCertificateCoverage;
    }
}