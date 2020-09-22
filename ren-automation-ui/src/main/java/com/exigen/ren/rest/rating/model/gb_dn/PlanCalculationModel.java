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

import static com.exigen.ren.main.enums.RatingReportConstants.FactorsSectionFields.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlanCalculationModel extends Model {

    private String planName;
    List<TierRateModel> tierRates;
    private String manualCompositeRate;
    private String experienceRate;
    private String formulaCompositeRate;
    private String adjManualCompositeRate;
    private String adjFormulaCompositeRate;
    private String proposedCompositeRate;
    private String currentAnnualPremium;
    private String renewalAnnualPremium;
    private String finalAnnualPremium;
    private String pctRenewalFromCurrent;
    private String pctFinalFromCurrent;
    private String pctFinalFromRenewal;
    private String pctProposedFromCurrent;
    private String pctProposedFromRenewal;
    private String proposedAnnualPremium;
    private String experienceAdjFactor;
    List<SubGroupEnrollmentBreakdownModel> subGroupEnrollmentBreakdown;
    private ManualClaimDetailModel manualClaimDetail;

    public String getPlanName() {
        return planName;
    }

    public PlanCalculationModel setPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    public List<TierRateModel> getTierRates() {
        return tierRates;
    }

    public PlanCalculationModel setTierRates(List<TierRateModel> tierRates) {
        this.tierRates = tierRates;
        return this;
    }

    public String getManualCompositeRate() {
        return manualCompositeRate;
    }

    public PlanCalculationModel setManualCompositeRate(String manualCompositeRate) {
        this.manualCompositeRate = manualCompositeRate;
        return this;
    }

    public String getExperienceRate() {
        return experienceRate;
    }

    public PlanCalculationModel setExperienceRate(String experienceRate) {
        this.experienceRate = experienceRate;
        return this;
    }

    public String getFormulaCompositeRate() {
        return formulaCompositeRate;
    }

    public PlanCalculationModel setFormulaCompositeRate(String formulaCompositeRate) {
        this.formulaCompositeRate = formulaCompositeRate;
        return this;
    }

    public String getAdjManualCompositeRate() {
        return adjManualCompositeRate;
    }

    public PlanCalculationModel setAdjManualCompositeRate(String adjManualCompositeRate) {
        this.adjManualCompositeRate = adjManualCompositeRate;
        return this;
    }

    public String getAdjFormulaCompositeRate() {
        return adjFormulaCompositeRate;
    }

    public PlanCalculationModel setAdjFormulaCompositeRate(String adjFormulaCompositeRate) {
        this.adjFormulaCompositeRate = adjFormulaCompositeRate;
        return this;
    }

    public String getProposedCompositeRate() {
        return proposedCompositeRate;
    }

    public PlanCalculationModel setProposedCompositeRate(String proposedCompositeRate) {
        this.proposedCompositeRate = proposedCompositeRate;
        return this;
    }

    public String getFinalAnnualPremium() {
        return finalAnnualPremium;
    }

    public PlanCalculationModel setFinalAnnualPremium(String finalAnnualPremium) {
        this.finalAnnualPremium = finalAnnualPremium;
        return this;
    }

    public String getProposedAnnualPremium() {
        return proposedAnnualPremium;
    }

    public PlanCalculationModel setProposedAnnualPremium(String proposedAnnualPremium) {
        this.proposedAnnualPremium = proposedAnnualPremium;
        return this;
    }

    public String getExperienceAdjFactor() {
        return experienceAdjFactor;
    }

    public PlanCalculationModel setExperienceAdjFactor(String experienceAdjFactor) {
        this.experienceAdjFactor = experienceAdjFactor;
        return this;
    }

    public List<SubGroupEnrollmentBreakdownModel> getSubGroupEnrollmentBreakdown() {
        return subGroupEnrollmentBreakdown;
    }

    public PlanCalculationModel setSubGroupEnrollmentBreakdown(List<SubGroupEnrollmentBreakdownModel> subGroupEnrollmentBreakdown) {
        this.subGroupEnrollmentBreakdown = subGroupEnrollmentBreakdown;
        return this;
    }

    public ManualClaimDetailModel getManualClaimDetail() {
        return manualClaimDetail;
    }

    public PlanCalculationModel setManualClaimDetail(ManualClaimDetailModel manualClaimDetail) {
        this.manualClaimDetail = manualClaimDetail;
        return this;
    }

    /**
     * Adapt values from REST to expected UI format
     * Method can be used to getting values from REST and compare with UI values*
     */
    public Map<String, String> getFactorsDataWithRoundedValues() {
        Map<String, String> result = new HashMap<>();
        result.put(INITIAL_LOAD, new BigDecimal(manualClaimDetail.getInitialLoad()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(OCCUPATION, new BigDecimal(manualClaimDetail.getOccupationFactor()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(OCCUPATION_CODE, manualClaimDetail.getOccupation());
        result.put(WAIT_GRP_SIZE, new BigDecimal(manualClaimDetail.getWaitGroupSize()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(PLAN_UTIL, new BigDecimal(manualClaimDetail.getPlanUtilization()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(GROUP_SIZE, new BigDecimal(manualClaimDetail.getGroupSize()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(CAL_YR_LOAD, new BigDecimal(manualClaimDetail.getYearLoad()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(VOL_LOAD, new BigDecimal(manualClaimDetail.getVoluntaryLoad()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(WAIT_LOAD, new BigDecimal(manualClaimDetail.getWaitingLoad()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(ROLLOVER_LOAD, new BigDecimal(manualClaimDetail.getRolloverLoad()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(PREVENTIVE, new BigDecimal(manualClaimDetail.getNetworkDetails().get(0).getEmployeeDetailsModelByBenefitCategory("Preventive").getAgeFactor()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(RADIOGRAPHS, new BigDecimal(manualClaimDetail.getNetworkDetails().get(0).getEmployeeDetailsModelByBenefitCategory("Radiographs").getAgeFactor()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(MAJOR_RESTORATIVE, new BigDecimal(manualClaimDetail.getNetworkDetails().get(0).getEmployeeDetailsModelByBenefitCategory("Major Restorative").getAgeFactor()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(OTHER_CLASS_SECOND, new BigDecimal(manualClaimDetail.getNetworkDetails().get(0).getEmployeeDetailsModelByBenefitCategory("Other Class II").getAgeFactor()).setScale(4, RoundingMode.HALF_UP).toString());
        result.put(PROSTHODONTICS, new BigDecimal(manualClaimDetail.getNetworkDetails().get(0).getEmployeeDetailsModelByBenefitCategory("Prosthodontics").getAgeFactor()).setScale(4, RoundingMode.HALF_UP).toString());
        return result;
    }

    public String getCurrentAnnualPremium() {
        return currentAnnualPremium;
    }

    public PlanCalculationModel setCurrentAnnualPremium(String currentAnnualPremium) {
        this.currentAnnualPremium = currentAnnualPremium;
        return this;
    }

    public String getRenewalAnnualPremium() {
        return renewalAnnualPremium;
    }

    public PlanCalculationModel setRenewalAnnualPremium(String renewalAnnualPremium) {
        this.renewalAnnualPremium = renewalAnnualPremium;
        return this;
    }

    public String getPctRenewalFromCurrent() {
        return pctRenewalFromCurrent;
    }

    public PlanCalculationModel setPctRenewalFromCurrent(String pctRenewalFromCurrent) {
        this.pctRenewalFromCurrent = pctRenewalFromCurrent;
        return this;
    }

    public String getPctFinalFromCurrent() {
        return pctFinalFromCurrent;
    }

    public PlanCalculationModel setPctFinalFromCurrent(String pctFinalFromCurrent) {
        this.pctFinalFromCurrent = pctFinalFromCurrent;
        return this;
    }

    public String getPctFinalFromRenewal() {
        return pctFinalFromRenewal;
    }

    public PlanCalculationModel setPctFinalFromRenewal(String pctFinalFromRenewal) {
        this.pctFinalFromRenewal = pctFinalFromRenewal;
        return this;
    }

    public String getPctProposedFromCurrent() {
        return pctProposedFromCurrent;
    }

    public PlanCalculationModel setPctProposedFromCurrent(String pctProposedFromCurrent) {
        this.pctProposedFromCurrent = pctProposedFromCurrent;
        return this;
    }

    public String getPctProposedFromRenewal() {
        return pctProposedFromRenewal;
    }

    public PlanCalculationModel setPctProposedFromRenewal(String pctProposedFromRenewal) {
        this.pctProposedFromRenewal = pctProposedFromRenewal;
        return this;
    }

    /**
     * Adapt values from REST to expected UI format
     * Method can be used to getting values from REST and compare with UI values*
     */
    public List<TierRateModel> getTierRateModelsWithAdaptedToExpectedUIValues() {
        List<TierRateModel> result = getTierRates();
        result.forEach(
                model -> {
                    model.setArea(String.format("AREA %s", model.getArea()));
                    model.setAdjManualRate(new DecimalFormat("#,###,##0.00").format(new BigDecimal(model.getAdjManualRate()).setScale(2, RoundingMode.HALF_UP)));
                    model.setAdjFormulaRate(new DecimalFormat("#,###,##0.00").format(new BigDecimal(model.getAdjFormulaRate()).setScale(2, RoundingMode.HALF_UP)));
                    model.setProposedRate(new DecimalFormat("#,###,##0.00").format(new BigDecimal(model.getProposedRate()).setScale(2, RoundingMode.HALF_UP)));
                    model.setManualRate(null);
                    model.setFormulaRate(null);
                }
        );
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlanCalculationModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PlanCalculationModel that = (PlanCalculationModel) o;
        return Objects.equals(planName, that.planName) &&
                Objects.equals(tierRates, that.tierRates) &&
                Objects.equals(manualCompositeRate, that.manualCompositeRate) &&
                Objects.equals(experienceRate, that.experienceRate) &&
                Objects.equals(formulaCompositeRate, that.formulaCompositeRate) &&
                Objects.equals(adjManualCompositeRate, that.adjManualCompositeRate) &&
                Objects.equals(adjFormulaCompositeRate, that.adjFormulaCompositeRate) &&
                Objects.equals(proposedCompositeRate, that.proposedCompositeRate) &&
                Objects.equals(currentAnnualPremium, that.currentAnnualPremium) &&
                Objects.equals(renewalAnnualPremium, that.renewalAnnualPremium) &&
                Objects.equals(finalAnnualPremium, that.finalAnnualPremium) &&
                Objects.equals(pctRenewalFromCurrent, that.pctRenewalFromCurrent) &&
                Objects.equals(pctFinalFromCurrent, that.pctFinalFromCurrent) &&
                Objects.equals(pctFinalFromRenewal, that.pctFinalFromRenewal) &&
                Objects.equals(pctProposedFromCurrent, that.pctProposedFromCurrent) &&
                Objects.equals(pctProposedFromRenewal, that.pctProposedFromRenewal) &&
                Objects.equals(proposedAnnualPremium, that.proposedAnnualPremium) &&
                Objects.equals(experienceAdjFactor, that.experienceAdjFactor) &&
                Objects.equals(subGroupEnrollmentBreakdown, that.subGroupEnrollmentBreakdown) &&
                Objects.equals(manualClaimDetail, that.manualClaimDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), planName, tierRates, manualCompositeRate, experienceRate, formulaCompositeRate, adjManualCompositeRate, adjFormulaCompositeRate, proposedCompositeRate, currentAnnualPremium, renewalAnnualPremium, finalAnnualPremium, pctRenewalFromCurrent, pctFinalFromCurrent, pctFinalFromRenewal, pctProposedFromCurrent, pctProposedFromRenewal, proposedAnnualPremium, experienceAdjFactor, subGroupEnrollmentBreakdown, manualClaimDetail);
    }

    @Override
    public String toString() {
        return "PlanCalculationModel{" +
                "planName='" + planName + '\'' +
                ", tierRates=" + tierRates +
                ", manualCompositeRate='" + manualCompositeRate + '\'' +
                ", experienceRate='" + experienceRate + '\'' +
                ", formulaCompositeRate='" + formulaCompositeRate + '\'' +
                ", adjManualCompositeRate='" + adjManualCompositeRate + '\'' +
                ", adjFormulaCompositeRate='" + adjFormulaCompositeRate + '\'' +
                ", proposedCompositeRate='" + proposedCompositeRate + '\'' +
                ", currentAnnualPremium='" + currentAnnualPremium + '\'' +
                ", renewalAnnualPremium='" + renewalAnnualPremium + '\'' +
                ", finalAnnualPremium='" + finalAnnualPremium + '\'' +
                ", pctRenewalFromCurrent='" + pctRenewalFromCurrent + '\'' +
                ", pctFinalFromCurrent='" + pctFinalFromCurrent + '\'' +
                ", pctFinalFromRenewal='" + pctFinalFromRenewal + '\'' +
                ", pctProposedFromCurrent='" + pctProposedFromCurrent + '\'' +
                ", pctProposedFromRenewal='" + pctProposedFromRenewal + '\'' +
                ", proposedAnnualPremium='" + proposedAnnualPremium + '\'' +
                ", experienceAdjFactor='" + experienceAdjFactor + '\'' +
                ", subGroupEnrollmentBreakdown=" + subGroupEnrollmentBreakdown +
                ", manualClaimDetail=" + manualClaimDetail +
                '}';
    }
}
