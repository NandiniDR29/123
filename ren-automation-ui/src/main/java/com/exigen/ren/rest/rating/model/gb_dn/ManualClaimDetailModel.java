package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.exigen.istf.exceptions.IstfException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.exigen.ren.main.enums.RatingReportConstants.FactorsSectionFields.*;
import static com.exigen.ren.main.enums.RatingReportConstants.NetworkSectionFields.*;
import static com.exigen.ren.main.enums.RatingReportConstants.Networks.PPO;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManualClaimDetailModel extends Model {

    private String planName;
    private String area;
    private List<AreaFactorsModel> areaFactors;
    private String totalEnrolled;
    private String tierSize;
    private String requiredParticipationPct;
    private String minNumberOfParticipants;
    private String currentParticipationPct;
    private String occupation;
    private String occupationFactor;
    private String initialLoad;
    private String groupSize;
    private String yearLoad;
    private String voluntaryLoad;
    private String waitingLoad;
    private String waitGroupSize;
    private String rolloverLoad;
    private String steerage;
    private String planUtilization;
    private List<NetworkDetailsModel> networkDetails;
    private String assumedParticipationPct;
    private List<ClaimsModel> claims;
    private List<AdjClaimsModel> adjClaims;
    private String claimsAnnual;
    private String adjClaimsAnnual;
    private String ppoPenetration;
    private String ppoAreaFactor;
    private String indemnityAreaFactor;
    private String epoPenetration;
    private String epoAreaFactor;
    private List<AgeBreakdownModel> ageBreakdown;
    private List<SubGroupEnrollmentBreakdownModel> subGroupEnrollmentBreakdownModel;
    private List<EnrollmentBreakdownModel> enrollmentBreakdown;
    private List<EnrollmentBreakdownReportModel> enrollmentBreakdownReport;
    private List<EnrollmentByStateModel> enrollmentByState;
    private List<EnrollmentByMSAModel> enrollmentByMSAModel;

    public String getPlanName() {
        return planName;
    }

    public ManualClaimDetailModel setPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    public String getTotalEnrolled() {
        return totalEnrolled;
    }

    public ManualClaimDetailModel setTotalEnrolled(String totalEnrolled) {
        this.totalEnrolled = totalEnrolled;
        return this;
    }

    public String getTierSize() {
        return tierSize;
    }

    public ManualClaimDetailModel setTierSize(String tierSize) {
        this.tierSize = tierSize;
        return this;
    }

    public String getRequiredParticipationPct() {
        return requiredParticipationPct;
    }

    public ManualClaimDetailModel setRequiredParticipationPct(String requiredParticipationPct) {
        this.requiredParticipationPct = requiredParticipationPct;
        return this;
    }

    public String getMinNumberOfParticipants() {
        return minNumberOfParticipants;
    }

    public ManualClaimDetailModel setMinNumberOfParticipants(String minNumberOfParticipants) {
        this.minNumberOfParticipants = minNumberOfParticipants;
        return this;
    }

    public String getCurrentParticipationPct() {
        return currentParticipationPct;
    }

    public ManualClaimDetailModel setCurrentParticipationPct(String currentParticipationPct) {
        this.currentParticipationPct = currentParticipationPct;
        return this;
    }

    public String getOccupation() {
        return occupation;
    }

    public ManualClaimDetailModel setOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }

    public String getOccupationFactor() {
        return occupationFactor;
    }

    public ManualClaimDetailModel setOccupationFactor(String occupationFactor) {
        this.occupationFactor = occupationFactor;
        return this;
    }

    public String getInitialLoad() {
        return initialLoad;
    }

    public ManualClaimDetailModel setInitialLoad(String initialLoad) {
        this.initialLoad = initialLoad;
        return this;
    }

    public String getGroupSize() {
        return groupSize;
    }

    public ManualClaimDetailModel setGroupSize(String groupSize) {
        this.groupSize = groupSize;
        return this;
    }

    public String getYearLoad() {
        return yearLoad;
    }

    public ManualClaimDetailModel setYearLoad(String yearLoad) {
        this.yearLoad = yearLoad;
        return this;
    }

    public String getVoluntaryLoad() {
        return voluntaryLoad;
    }

    public ManualClaimDetailModel setVoluntaryLoad(String voluntaryLoad) {
        this.voluntaryLoad = voluntaryLoad;
        return this;
    }

    public String getWaitingLoad() {
        return waitingLoad;
    }

    public ManualClaimDetailModel setWaitingLoad(String waitingLoad) {
        this.waitingLoad = waitingLoad;
        return this;
    }

    public String getWaitGroupSize() {
        return waitGroupSize;
    }

    public ManualClaimDetailModel setWaitGroupSize(String waitGroupSize) {
        this.waitGroupSize = waitGroupSize;
        return this;
    }

    public String getRolloverLoad() {
        return rolloverLoad;
    }

    public ManualClaimDetailModel setRolloverLoad(String rolloverLoad) {
        this.rolloverLoad = rolloverLoad;
        return this;
    }

    public String getSteerage() {
        return steerage;
    }

    public ManualClaimDetailModel setSteerage(String steerage) {
        this.steerage = steerage;
        return this;
    }

    public String getPlanUtilization() {
        return planUtilization;
    }

    public ManualClaimDetailModel setPlanUtilization(String planUtilization) {
        this.planUtilization = planUtilization;
        return this;
    }

    public List<NetworkDetailsModel> getNetworkDetails() {
        return networkDetails;
    }

    public ManualClaimDetailModel setNetworkDetails(List<NetworkDetailsModel> networkDetails) {
        this.networkDetails = networkDetails.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return this;
    }

    public String getAssumedParticipationPct() {
        return assumedParticipationPct;
    }

    public ManualClaimDetailModel setAssumedParticipationPct(String assumedParticipationPct) {
        this.assumedParticipationPct = assumedParticipationPct;
        return this;
    }

    public List<ClaimsModel> getClaims() {
        return claims;
    }

    public ManualClaimDetailModel setClaims(List<ClaimsModel> claims) {
        this.claims = claims;
        return this;
    }

    public List<AdjClaimsModel> getAdjClaims() {
        return adjClaims;
    }

    public ManualClaimDetailModel setAdjClaims(List<AdjClaimsModel> adjClaims) {
        this.adjClaims = adjClaims;
        return this;
    }

    public String getClaimsAnnual() {
        return claimsAnnual;
    }

    public ManualClaimDetailModel setClaimsAnnual(String claimsAnnual) {
        this.claimsAnnual = claimsAnnual;
        return this;
    }

    public String getAdjClaimsAnnual() {
        return adjClaimsAnnual;
    }

    public ManualClaimDetailModel setAdjClaimsAnnual(String adjClaimsAnnual) {
        this.adjClaimsAnnual = adjClaimsAnnual;
        return this;
    }

    public String getPpoPenetration() {
        return ppoPenetration;
    }

    public ManualClaimDetailModel setPpoPenetration(String ppoPenetration) {
        this.ppoPenetration = ppoPenetration;
        return this;
    }

    public String getPpoAreaFactor() {
        return ppoAreaFactor;
    }

    public ManualClaimDetailModel setPpoAreaFactor(String ppoAreaFactor) {
        this.ppoAreaFactor = ppoAreaFactor;
        return this;
    }

    public String getIndemnityAreaFactor() {
        return indemnityAreaFactor;
    }

    public ManualClaimDetailModel setIndemnityAreaFactor(String indemnityAreaFactor) {
        this.indemnityAreaFactor = indemnityAreaFactor;
        return this;
    }

    public List<AgeBreakdownModel> getAgeBreakdown() {
        return ageBreakdown;
    }

    public ManualClaimDetailModel setAgeBreakdown(List<AgeBreakdownModel> ageBreakdown) {
        this.ageBreakdown = ageBreakdown;
        return this;
    }

    public List<SubGroupEnrollmentBreakdownModel> getSubGroupEnrollmentBreakdownModel() {
        return subGroupEnrollmentBreakdownModel;
    }

    public ManualClaimDetailModel setSubGroupEnrollmentBreakdownModel(List<SubGroupEnrollmentBreakdownModel> subGroupEnrollmentBreakdownModel) {
        this.subGroupEnrollmentBreakdownModel = subGroupEnrollmentBreakdownModel;
        return this;
    }

    public List<EnrollmentBreakdownModel> getEnrollmentBreakdown() {
        return enrollmentBreakdown;
    }

    public ManualClaimDetailModel setEnrollmentBreakdown(List<EnrollmentBreakdownModel> enrollmentBreakdown) {
        this.enrollmentBreakdown = enrollmentBreakdown;
        return this;
    }

    public List<EnrollmentBreakdownReportModel> getEnrollmentBreakdownReport() {
        return enrollmentBreakdownReport;
    }

    public ManualClaimDetailModel setEnrollmentBreakdownReport(List<EnrollmentBreakdownReportModel> enrollmentBreakdownReport) {
        this.enrollmentBreakdownReport = enrollmentBreakdownReport;
        return this;
    }

    public List<EnrollmentByStateModel> getEnrollmentByState() {
        return enrollmentByState;
    }

    public ManualClaimDetailModel setEnrollmentByState(List<EnrollmentByStateModel> enrollmentByState) {
        this.enrollmentByState = enrollmentByState;
        return this;
    }

    public List<EnrollmentByMSAModel> getEnrollmentByMSAModel() {
        return enrollmentByMSAModel;
    }

    public ManualClaimDetailModel setEnrollmentByMSAModel(List<EnrollmentByMSAModel> enrollmentByMSAModel) {
        this.enrollmentByMSAModel = enrollmentByMSAModel;
        return this;
    }

    /**
     * @return Sum of all numberOfLives for ageBreakdowns
     */
    public String getAgeBreakDownTotalNumberOfLives() {
        return String.valueOf(ageBreakdown.stream().mapToDouble(ageBreakdownModel -> Double.parseDouble(ageBreakdownModel.getNumberOfLives())).sum()).split("\\.")[0];
    }

    /**
     * @return Sum of all numberOfLives for enrollmentBreakdownReport
     */
    public String getEnrollmentBreakdownReportTotalNumberOfLives() {
        return String.valueOf(enrollmentBreakdownReport.stream().mapToDouble(enrollmentBreakdownModel -> Double.parseDouble(enrollmentBreakdownModel.getNumberOfLives())).sum()).split("\\.")[0];
    }

    /**
     * Adapt values from REST to expected UI format
     * Method can be used to getting values from REST and compare with UI values*
     */
    public Map<String, String> getNetworkDataByNetworkName(String network) {
        NetworkDetailsModel model = getNetworkDetailsModelByNetworkName(network);
        Map<String, String> result = new HashMap<>();
        result.put(AREA, new BigDecimal(model.getArea()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(TREND, new BigDecimal(model.getTrend()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(MAXIMUM_EE_AND_SP, new BigDecimal(model.getMaximumEEandSP()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(MAXIMUM_CH, new BigDecimal(model.getMaximumCH()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(PREVENTIVE, new BigDecimal(model.getEmployeeDetailsModelByBenefitCategory("Preventive").getCopay()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(RADIOGRAPHS, new BigDecimal(model.getEmployeeDetailsModelByBenefitCategory("Radiographs").getCopay()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(MAJOR_RESTORATIVE, new BigDecimal(model.getEmployeeDetailsModelByBenefitCategory("Major Restorative").getCopay()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(OTHER_CLASS_SECOND, new BigDecimal(model.getEmployeeDetailsModelByBenefitCategory("Other Class II").getCopay()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(PROSTHODONTICS, new BigDecimal(model.getEmployeeDetailsModelByBenefitCategory("Prosthodontics").getCopay()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(NETWORK_PENETRATION, new DecimalFormat("#0.00%").format(new BigDecimal(model.getPpoPenetration())));
        if (PPO.equals(network)) { //put STEERAGE data that exist only for PPO
            result.put(STEERAGE, new DecimalFormat("#0.00%").format(new BigDecimal(this.getSteerage())));
        }
        result.put(NET_UTILIZATION, new DecimalFormat("#0.00%").format(new BigDecimal(model.getNetUtilization())));
        return result;
    }

    public NetworkDetailsModel getNetworkDetailsModelByNetworkName(String networkName) {
        NetworkDetailsModel model = null;
        for (NetworkDetailsModel networkDetailsModel : networkDetails) {
            if (networkName.equals(networkDetailsModel.getNetwork())) {
                model = networkDetailsModel;
            }
        }
        if (model == null) {
            throw new IstfException(String.format("Network Details Model with name '%s' doesn't exist", networkName));
        }
        return model;
    }

    public String getArea() {
        return area;
    }

    public ManualClaimDetailModel setArea(String area) {
        this.area = area;
        return this;
    }

    public List<AreaFactorsModel> getAreaFactors() {
        return areaFactors;
    }

    public ManualClaimDetailModel setAreaFactors(List<AreaFactorsModel> areaFactors) {
        this.areaFactors = areaFactors;
        return this;
    }

    public String getEpoPenetration() {
        return epoPenetration;
    }

    public ManualClaimDetailModel setEpoPenetration(String epoPenetration) {
        this.epoPenetration = epoPenetration;
        return this;
    }

    public String getEpoAreaFactor() {
        return epoAreaFactor;
    }

    public ManualClaimDetailModel setEpoAreaFactor(String epoAreaFactor) {
        this.epoAreaFactor = epoAreaFactor;
        return this;
    }

    /**
     * Adapt values from REST to expected UI format
     * Method can be used to getting values from REST and compare with UI values*
     */
    public List<AreaFactorsModel> getAreaFactorsWithRoundedValues(boolean isDefaultAreaNames) {
        List<AreaFactorsModel> result = getAreaFactors();
        result.forEach(
                model -> {
                    if (isDefaultAreaNames) {
                        model.setArea(String.format("AREA %s", model.getArea()));
                    }
                    model.setIndemnityFactor(new BigDecimal(model.getIndemnityFactor()).setScale(4, RoundingMode.HALF_UP).toString());
                    model.setPPOFactor(new BigDecimal(model.getPPOFactor()).setScale(4, RoundingMode.HALF_UP).toString());
                    model.setPPOPenetrationFactor(new DecimalFormat("#0.00%").format(new BigDecimal(model.getPPOPenetrationFactor())));
                    model.setAreaFactorBlend(new BigDecimal(model.getAreaFactorBlend()).setScale(4, RoundingMode.HALF_UP).toString());
                }
        );
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ManualClaimDetailModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ManualClaimDetailModel that = (ManualClaimDetailModel) o;
        return Objects.equals(planName, that.planName) &&
                Objects.equals(area, that.area) &&
                Objects.equals(areaFactors, that.areaFactors) &&
                Objects.equals(totalEnrolled, that.totalEnrolled) &&
                Objects.equals(tierSize, that.tierSize) &&
                Objects.equals(requiredParticipationPct, that.requiredParticipationPct) &&
                Objects.equals(minNumberOfParticipants, that.minNumberOfParticipants) &&
                Objects.equals(currentParticipationPct, that.currentParticipationPct) &&
                Objects.equals(occupation, that.occupation) &&
                Objects.equals(occupationFactor, that.occupationFactor) &&
                Objects.equals(initialLoad, that.initialLoad) &&
                Objects.equals(groupSize, that.groupSize) &&
                Objects.equals(yearLoad, that.yearLoad) &&
                Objects.equals(voluntaryLoad, that.voluntaryLoad) &&
                Objects.equals(waitingLoad, that.waitingLoad) &&
                Objects.equals(waitGroupSize, that.waitGroupSize) &&
                Objects.equals(rolloverLoad, that.rolloverLoad) &&
                Objects.equals(steerage, that.steerage) &&
                Objects.equals(planUtilization, that.planUtilization) &&
                Objects.equals(networkDetails, that.networkDetails) &&
                Objects.equals(assumedParticipationPct, that.assumedParticipationPct) &&
                Objects.equals(claims, that.claims) &&
                Objects.equals(adjClaims, that.adjClaims) &&
                Objects.equals(claimsAnnual, that.claimsAnnual) &&
                Objects.equals(adjClaimsAnnual, that.adjClaimsAnnual) &&
                Objects.equals(ppoPenetration, that.ppoPenetration) &&
                Objects.equals(ppoAreaFactor, that.ppoAreaFactor) &&
                Objects.equals(indemnityAreaFactor, that.indemnityAreaFactor) &&
                Objects.equals(epoPenetration, that.epoPenetration) &&
                Objects.equals(epoAreaFactor, that.epoAreaFactor) &&
                Objects.equals(ageBreakdown, that.ageBreakdown) &&
                Objects.equals(subGroupEnrollmentBreakdownModel, that.subGroupEnrollmentBreakdownModel) &&
                Objects.equals(enrollmentBreakdown, that.enrollmentBreakdown) &&
                Objects.equals(enrollmentBreakdownReport, that.enrollmentBreakdownReport) &&
                Objects.equals(enrollmentByState, that.enrollmentByState) &&
                Objects.equals(enrollmentByMSAModel, that.enrollmentByMSAModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), planName, area, areaFactors, totalEnrolled, tierSize, requiredParticipationPct, minNumberOfParticipants, currentParticipationPct, occupation, occupationFactor, initialLoad, groupSize, yearLoad, voluntaryLoad, waitingLoad, waitGroupSize, rolloverLoad, steerage, planUtilization, networkDetails, assumedParticipationPct, claims, adjClaims, claimsAnnual, adjClaimsAnnual, ppoPenetration, ppoAreaFactor, indemnityAreaFactor, epoPenetration, epoAreaFactor, ageBreakdown, subGroupEnrollmentBreakdownModel, enrollmentBreakdown, enrollmentBreakdownReport, enrollmentByState, enrollmentByMSAModel);
    }

    @Override
    public String toString() {
        return "ManualClaimDetailModel{" +
                "planName='" + planName + '\'' +
                ", area='" + area + '\'' +
                ", areaFactors=" + areaFactors +
                ", totalEnrolled='" + totalEnrolled + '\'' +
                ", tierSize='" + tierSize + '\'' +
                ", requiredParticipationPct='" + requiredParticipationPct + '\'' +
                ", minNumberOfParticipants='" + minNumberOfParticipants + '\'' +
                ", currentParticipationPct='" + currentParticipationPct + '\'' +
                ", occupation='" + occupation + '\'' +
                ", occupationFactor='" + occupationFactor + '\'' +
                ", initialLoad='" + initialLoad + '\'' +
                ", groupSize='" + groupSize + '\'' +
                ", yearLoad='" + yearLoad + '\'' +
                ", voluntaryLoad='" + voluntaryLoad + '\'' +
                ", waitingLoad='" + waitingLoad + '\'' +
                ", waitGroupSize='" + waitGroupSize + '\'' +
                ", rolloverLoad='" + rolloverLoad + '\'' +
                ", steerage='" + steerage + '\'' +
                ", planUtilization='" + planUtilization + '\'' +
                ", networkDetails=" + networkDetails +
                ", assumedParticipationPct='" + assumedParticipationPct + '\'' +
                ", claims=" + claims +
                ", adjClaims=" + adjClaims +
                ", claimsAnnual='" + claimsAnnual + '\'' +
                ", adjClaimsAnnual='" + adjClaimsAnnual + '\'' +
                ", ppoPenetration='" + ppoPenetration + '\'' +
                ", ppoAreaFactor='" + ppoAreaFactor + '\'' +
                ", indemnityAreaFactor='" + indemnityAreaFactor + '\'' +
                ", epoPenetration='" + epoPenetration + '\'' +
                ", epoAreaFactor='" + epoAreaFactor + '\'' +
                ", ageBreakdown=" + ageBreakdown +
                ", subGroupEnrollmentBreakdownModel=" + subGroupEnrollmentBreakdownModel +
                ", enrollmentBreakdown=" + enrollmentBreakdown +
                ", enrollmentBreakdownReport=" + enrollmentBreakdownReport +
                ", enrollmentByState=" + enrollmentByState +
                ", enrollmentByMSAModel=" + enrollmentByMSAModel +
                '}';
    }
}
