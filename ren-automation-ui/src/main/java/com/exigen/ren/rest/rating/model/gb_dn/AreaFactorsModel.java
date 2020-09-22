package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaFactorsModel extends Model {

    private String area;
    private String indemnityFactor;
    private String ppoFactor;
    private String ppoPenetrationFactor;
    private String areaFactorBlend;

    public String getArea() {
        return area;
    }

    public AreaFactorsModel setArea(String area) {
        this.area = area;
        return this;
    }

    public String getIndemnityFactor() {
        return indemnityFactor;
    }

    public AreaFactorsModel setIndemnityFactor(String indemnityFactor) {
        this.indemnityFactor = indemnityFactor;
        return this;
    }

    @JsonProperty("PPOFactor")
    public String getPPOFactor() {
        return ppoFactor;
    }

    @JsonProperty("PPOFactor")
    public AreaFactorsModel setPPOFactor(String ppoFactor) {
        this.ppoFactor = ppoFactor;
        return this;
    }

    @JsonProperty("PPOPenetrationFactor")
    public String getPPOPenetrationFactor() {
        return ppoPenetrationFactor;
    }

    @JsonProperty("PPOPenetrationFactor")
    public AreaFactorsModel setPPOPenetrationFactor(String ppoPenetrationFactor) {
        this.ppoPenetrationFactor = ppoPenetrationFactor;
        return this;
    }

    public String getAreaFactorBlend() {
        return areaFactorBlend;
    }

    public AreaFactorsModel setAreaFactorBlend(String areaFactorBlend) {
        this.areaFactorBlend = areaFactorBlend;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AreaFactorsModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AreaFactorsModel that = (AreaFactorsModel) o;
        return Objects.equals(area, that.area) &&
                Objects.equals(indemnityFactor, that.indemnityFactor) &&
                Objects.equals(ppoFactor, that.ppoFactor) &&
                Objects.equals(ppoPenetrationFactor, that.ppoPenetrationFactor) &&
                Objects.equals(areaFactorBlend, that.areaFactorBlend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), area, indemnityFactor, ppoFactor, ppoPenetrationFactor, areaFactorBlend);
    }

    @Override
    public String toString() {
        return "AreaFactorsModel{" +
                "area='" + area + '\'' +
                ", indemnityFactor='" + indemnityFactor + '\'' +
                ", ppoFactor='" + ppoFactor + '\'' +
                ", ppoPenetrationFactor='" + ppoPenetrationFactor + '\'' +
                ", areaFactorBlend='" + areaFactorBlend + '\'' +
                '}';
    }
}
