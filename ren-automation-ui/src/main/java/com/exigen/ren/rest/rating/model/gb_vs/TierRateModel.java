package com.exigen.ren.rest.rating.model.gb_vs;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TierRateModel extends Model {

    private String area;
    private String tier;
    private String adjustedManualRate;
    private String enrollment;
    private String finalRate;
    private String proposedRate;
    private String claimCost;
    private String vspSays;

    public String getArea() {
        return area;
    }

    public TierRateModel setArea(String area) {
        this.area = area;
        return this;
    }

    public String getTier() {
        return tier;
    }

    public TierRateModel setTier(String tier) {
        this.tier = tier;
        return this;
    }

    public String getAdjustedManualRate() {
        return adjustedManualRate;
    }

    public TierRateModel setAdjustedManualRate(String adjustedManualRate) {
        this.adjustedManualRate = adjustedManualRate;
        return this;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public TierRateModel setEnrollment(String enrollment) {
        this.enrollment = enrollment;
        return this;
    }

    public String getFinalRate() {
        return finalRate;
    }

    public TierRateModel setFinalRate(String finalRate) {
        this.finalRate = finalRate;
        return this;
    }

    public String getProposedRate() {
        return proposedRate;
    }

    public TierRateModel setProposedRate(String proposedRate) {
        this.proposedRate = proposedRate;
        return this;
    }

    public String getClaimCost() {
        return claimCost;
    }

    public TierRateModel setClaimCost(String claimCost) {
        this.claimCost = claimCost;
        return this;
    }

    public String getVspSays() {
        return vspSays;
    }

    public TierRateModel setVspSays(String vspSays) {
        this.vspSays = vspSays;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TierRateModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TierRateModel that = (TierRateModel) o;
        return Objects.equals(area, that.area) &&
                Objects.equals(tier, that.tier) &&
                Objects.equals(adjustedManualRate, that.adjustedManualRate) &&
                Objects.equals(enrollment, that.enrollment) &&
                Objects.equals(finalRate, that.finalRate) &&
                Objects.equals(proposedRate, that.proposedRate) &&
                Objects.equals(claimCost, that.claimCost) &&
                Objects.equals(vspSays, that.vspSays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), area, tier, adjustedManualRate, enrollment, finalRate, proposedRate, claimCost, vspSays);
    }

    @Override
    public String toString() {
        return "TierRateModel{" +
                "area='" + area + '\'' +
                ", tier='" + tier + '\'' +
                ", adjustedManualRate='" + adjustedManualRate + '\'' +
                ", enrollment='" + enrollment + '\'' +
                ", finalRate='" + finalRate + '\'' +
                ", proposedRate='" + proposedRate + '\'' +
                ", claimCost='" + claimCost + '\'' +
                ", vspSays='" + vspSays + '\'' +
                '}';
    }
}
