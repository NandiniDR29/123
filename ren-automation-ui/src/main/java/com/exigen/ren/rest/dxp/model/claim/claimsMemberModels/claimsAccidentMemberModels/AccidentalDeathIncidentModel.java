package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.claimsAccidentMemberModels;

import com.exigen.ren.rest.dxp.model.claim.claimsMemberModels.ClaimsMemberIncidentAbstractModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccidentalDeathIncidentModel extends ClaimsMemberIncidentAbstractModel {
}
