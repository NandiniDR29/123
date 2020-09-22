package com.exigen.ren.rest.claim.model.common.claimbody.claim;

import com.exigen.ipb.eisa.ws.rest.model.Model;

import java.util.List;

public class PartyExtensionModel extends Model {
    private List<PatientHistoryModel> patientHistory;

    public List<PatientHistoryModel> getPatientHistory() {
        return patientHistory;
    }

    public void setPatientHistory(List<PatientHistoryModel> patientHistory) {
        this.patientHistory = patientHistory;
    }

}
