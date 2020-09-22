package com.exigen.ren.rest.dxp.model.claim.claimsMemberModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecoveryModel extends RestError {
    private String payee;
    private String recoveryStatus;
    private String payeeType;
    private RecoveryDetailsModel recoveryDetails;
    private String recoveryNumber;
    private MoneyModel recoveryAmt;
    private String description;
    private String issueDate;

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getRecoveryStatus() {
        return recoveryStatus;
    }

    public void setRecoveryStatus(String recoveryStatus) {
        this.recoveryStatus = recoveryStatus;
    }

    public String getPayeeType() {
        return payeeType;
    }

    public void setPayeeType(String payeeType) {
        this.payeeType = payeeType;
    }

    public RecoveryDetailsModel getRecoveryDetails() {
        return recoveryDetails;
    }

    public void setRecoveryDetails(RecoveryDetailsModel recoveryDetails) {
        this.recoveryDetails = recoveryDetails;
    }

    public String getRecoveryNumber() {
        return recoveryNumber;
    }

    public void setRecoveryNumber(String recoveryNumber) {
        this.recoveryNumber = recoveryNumber;
    }

    public MoneyModel getRecoveryAmt() {
        return recoveryAmt;
    }

    public void setRecoveryAmt(MoneyModel recoveryAmt) {
        this.recoveryAmt = recoveryAmt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RecoveryModel that = (RecoveryModel) o;
        return Objects.equals(payee, that.payee) &&
                Objects.equals(recoveryStatus, that.recoveryStatus) &&
                Objects.equals(payeeType, that.payeeType) &&
                Objects.equals(recoveryDetails, that.recoveryDetails) &&
                Objects.equals(recoveryNumber, that.recoveryNumber) &&
                Objects.equals(recoveryAmt, that.recoveryAmt) &&
                Objects.equals(description, that.description) &&
                Objects.equals(issueDate, that.issueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), payee, recoveryStatus, payeeType, recoveryDetails, recoveryNumber, recoveryAmt, description, issueDate);
    }
}
