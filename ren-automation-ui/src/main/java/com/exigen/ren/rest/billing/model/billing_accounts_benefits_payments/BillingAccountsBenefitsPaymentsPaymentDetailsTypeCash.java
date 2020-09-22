package com.exigen.ren.rest.billing.model.billing_accounts_benefits_payments;

import java.util.Objects;

public class BillingAccountsBenefitsPaymentsPaymentDetailsTypeCash {

    private String profileId;
    private String extensionFields;
    private String referenceNumber;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getExtensionFields() {
        return extensionFields;
    }

    public void setExtensionFields(String extensionFields) {
        this.extensionFields = extensionFields;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        BillingAccountsBenefitsPaymentsPaymentDetailsTypeCash that = (BillingAccountsBenefitsPaymentsPaymentDetailsTypeCash) o;
        return Objects.equals(profileId, that.profileId) &&
                Objects.equals(extensionFields, that.extensionFields) &&
                Objects.equals(referenceNumber, that.referenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileId, extensionFields, referenceNumber);
    }
}
