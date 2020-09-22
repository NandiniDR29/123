package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.exigen.istf.exceptions.IstfException;
import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkDetailsModel extends Model {

    private String network;
    private List<EmployeeDetailsModel> employeeDetails;
    private List<SpouseDetailsModel> spouseDetails;
    private List<ChildDetailsModel> childDetails;
    private String trend;
    private String area;
    private String maximumEEandSP;
    private String maximumCH;
    private String netUtilization;
    private String ppoPenetration;
    private String rateCalcEmp;
    private String rateCalcWOEmp;
    private String rateCalcSp;
    private String rateCalcWOSp;
    private String rateCalcCh;
    private String rateCalcWOCh;

    public String getNetwork() {
        return network != null && !network.isEmpty() ? network : "foo";
    }

    public NetworkDetailsModel setNetwork(String network) {
        this.network = network;
        return this;
    }

    public List<EmployeeDetailsModel> getEmployeeDetails() {
        return employeeDetails;
    }

    public NetworkDetailsModel setEmployeeDetails(List<EmployeeDetailsModel> employeeDetails) {
        this.employeeDetails = employeeDetails;
        return this;
    }

    public List<SpouseDetailsModel> getSpouseDetails() {
        return spouseDetails;
    }

    public NetworkDetailsModel setSpouseDetails(List<SpouseDetailsModel> spouseDetails) {
        this.spouseDetails = spouseDetails;
        return this;
    }

    public List<ChildDetailsModel> getChildDetails() {
        return childDetails;
    }

    public NetworkDetailsModel setChildDetails(List<ChildDetailsModel> childDetails) {
        this.childDetails = childDetails;
        return this;
    }

    public String getTrend() {
        return trend;
    }

    public NetworkDetailsModel setTrend(String trend) {
        this.trend = trend;
        return this;
    }

    public String getArea() {
        return area;
    }

    public NetworkDetailsModel setArea(String area) {
        this.area = area;
        return this;
    }

    public String getMaximumEEandSP() {
        return maximumEEandSP;
    }

    public NetworkDetailsModel setMaximumEEandSP(String maximumEEandSP) {
        this.maximumEEandSP = maximumEEandSP;
        return this;
    }

    public String getMaximumCH() {
        return maximumCH;
    }

    public NetworkDetailsModel setMaximumCH(String maximumCH) {
        this.maximumCH = maximumCH;
        return this;
    }

    public String getNetUtilization() {
        return netUtilization;
    }

    public NetworkDetailsModel setNetUtilization(String netUtilization) {
        this.netUtilization = netUtilization;
        return this;
    }

    public String getPpoPenetration() {
        return ppoPenetration;
    }

    public NetworkDetailsModel setPpoPenetration(String ppoPenetration) {
        this.ppoPenetration = ppoPenetration;
        return this;
    }

    public String getRateCalcEmp() {
        return rateCalcEmp;
    }

    public NetworkDetailsModel setRateCalcEmp(String rateCalcEmp) {
        this.rateCalcEmp = rateCalcEmp;
        return this;
    }

    public String getRateCalcWOEmp() {
        return rateCalcWOEmp;
    }

    public NetworkDetailsModel setRateCalcWOEmp(String rateCalcWOEmp) {
        this.rateCalcWOEmp = rateCalcWOEmp;
        return this;
    }

    public String getRateCalcSp() {
        return rateCalcSp;
    }

    public NetworkDetailsModel setRateCalcSp(String rateCalcSp) {
        this.rateCalcSp = rateCalcSp;
        return this;
    }

    public String getRateCalcWOSp() {
        return rateCalcWOSp;
    }

    public NetworkDetailsModel setRateCalcWOSp(String rateCalcWOSp) {
        this.rateCalcWOSp = rateCalcWOSp;
        return this;
    }

    public String getRateCalcCh() {
        return rateCalcCh;
    }

    public NetworkDetailsModel setRateCalcCh(String rateCalcCh) {
        this.rateCalcCh = rateCalcCh;
        return this;
    }

    public String getRateCalcWOCh() {
        return rateCalcWOCh;
    }

    public NetworkDetailsModel setRateCalcWOCh(String rateCalcWOCh) {
        this.rateCalcWOCh = rateCalcWOCh;
        return this;
    }

    public EmployeeDetailsModel getEmployeeDetailsModelByBenefitCategory(String benefitCategory) {
        EmployeeDetailsModel model = null;
        for (EmployeeDetailsModel empoloyeeDetails : employeeDetails) {
            if (benefitCategory.equals(empoloyeeDetails.getBenefitCategory())) {
                model = empoloyeeDetails;
            }
        }
        if (model == null) {
            throw new IstfException(String.format("Employee Details Model with benefit Category '%s' doesn't exist", benefitCategory));
        }
        return model;
    }

    public SpouseDetailsModel getSpouseDetailsModelByBenefitCategory(String benefitCategory) {
        SpouseDetailsModel model = null;
        for (SpouseDetailsModel spouseDetails : spouseDetails) {
            if (benefitCategory.equals(spouseDetails.getBenefitCategory())) {
                model = spouseDetails;
            }
        }
        if (model == null) {
            throw new IstfException(String.format("Spouse Details Model with benefit Category '%s' doesn't exist", benefitCategory));
        }
        return model;
    }

    public ChildDetailsModel getChildDetailsModelByBenefitCategory(String benefitCategory) {
        ChildDetailsModel model = null;
        for (ChildDetailsModel childDetails : childDetails) {
            if (benefitCategory.equals(childDetails.getBenefitCategory())) {
                model = childDetails;
            }
        }
        if (model == null) {
            throw new IstfException(String.format("Child Details Model with benefit Category '%s' doesn't exist", benefitCategory));
        }
        return model;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NetworkDetailsModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        NetworkDetailsModel that = (NetworkDetailsModel) o;
        return Objects.equals(network, that.network) &&
                Objects.equals(employeeDetails, that.employeeDetails) &&
                Objects.equals(spouseDetails, that.spouseDetails) &&
                Objects.equals(childDetails, that.childDetails) &&
                Objects.equals(trend, that.trend) &&
                Objects.equals(area, that.area) &&
                Objects.equals(maximumEEandSP, that.maximumEEandSP) &&
                Objects.equals(maximumCH, that.maximumCH) &&
                Objects.equals(netUtilization, that.netUtilization) &&
                Objects.equals(ppoPenetration, that.ppoPenetration) &&
                Objects.equals(rateCalcEmp, that.rateCalcEmp) &&
                Objects.equals(rateCalcWOEmp, that.rateCalcWOEmp) &&
                Objects.equals(rateCalcSp, that.rateCalcSp) &&
                Objects.equals(rateCalcWOSp, that.rateCalcWOSp) &&
                Objects.equals(rateCalcCh, that.rateCalcCh) &&
                Objects.equals(rateCalcWOCh, that.rateCalcWOCh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), network, employeeDetails, spouseDetails, childDetails, trend, area, maximumEEandSP, maximumCH, netUtilization, ppoPenetration, rateCalcEmp, rateCalcWOEmp, rateCalcSp, rateCalcWOSp, rateCalcCh, rateCalcWOCh);
    }

    @Override
    public String toString() {
        return "NetworkDetailsModel{" +
                "network='" + network + '\'' +
                ", employeeDetails=" + employeeDetails +
                ", spouseDetails=" + spouseDetails +
                ", childDetails=" + childDetails +
                ", trend='" + trend + '\'' +
                ", area='" + area + '\'' +
                ", maximumEEandSP='" + maximumEEandSP + '\'' +
                ", maximumCH='" + maximumCH + '\'' +
                ", netUtilization='" + netUtilization + '\'' +
                ", ppoPenetration='" + ppoPenetration + '\'' +
                ", rateCalcEmp='" + rateCalcEmp + '\'' +
                ", rateCalcWOEmp='" + rateCalcWOEmp + '\'' +
                ", rateCalcSp='" + rateCalcSp + '\'' +
                ", rateCalcWOSp='" + rateCalcWOSp + '\'' +
                ", rateCalcCh='" + rateCalcCh + '\'' +
                ", rateCalcWOCh='" + rateCalcWOCh + '\'' +
                '}';
    }
}
