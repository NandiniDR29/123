package com.exigen.ren.rest.claim.model.common.claimbody.claim;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class PaidToDateInfoModel extends Model {

    private Map<String, PaidToDateInfoEntityModel> paidToDateInfoEntity = new HashMap<>();

    // @JsonAnySetter because services keys are generated values in format "yyyy-MM-dd'T'HH:mm:ss'Z'"
    @JsonAnySetter
    public void setPaidToDateInfoEntity(String name, PaidToDateInfoEntityModel value) {
        paidToDateInfoEntity.put(name, value);
    }

    public Map<String, PaidToDateInfoEntityModel> getPaidToDateInfoEntity() {
        return paidToDateInfoEntity;
    }

}
