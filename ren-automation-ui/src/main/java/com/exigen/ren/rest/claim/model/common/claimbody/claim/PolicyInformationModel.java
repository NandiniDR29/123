package com.exigen.ren.rest.claim.model.common.claimbody.claim;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;


public class PolicyInformationModel extends Model {

    private Map<String, ServiceModel> services = new HashMap<>();

    // @JsonAnySetter because services keys are generated values in format "yyyy-MM-dd'T'HH:mm:ss'Z'"
    @JsonAnySetter
    public void setServices(String name, ServiceModel value) {
        services.put(name, value);
    }

    public Map<String, ServiceModel> getServices() {
        return services;
    }

}
