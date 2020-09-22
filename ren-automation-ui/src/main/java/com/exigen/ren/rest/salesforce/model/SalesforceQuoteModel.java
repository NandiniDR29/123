package com.exigen.ren.rest.salesforce.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesforceQuoteModel extends RestError {

    @JsonProperty("EIS_Quote_ID__c")
    private String quoteId;
    @JsonProperty("Quote_Vision_Plan__c")
    private String plan;
    @JsonProperty("Underwriting_Notes__c")
    private String underwritingNotes;
    @JsonProperty("Elimination_Period__c")
    private String eliminationPeriod;
    @JsonProperty("Benefit_Maximum__c")
    private String benefitMaximum;
    @JsonProperty("Benefit_Type__c")
    private String benefitType;
    @JsonProperty("Employer_Contribution__c")
    private String employerContribution;
    @JsonProperty("Situs_State_Rated__c")
    private String situsStateRelated;
    @JsonProperty("Quote_Name__c")
    private String quoteName;
    @JsonProperty("SIC_Code__c")
    private String sicCode;
    @JsonProperty("Percent_of_Salary__c")
    private String percentOfSalary;
    @JsonProperty("Own_occ_Period__c")
    private String ownOccPeriod;
    @JsonProperty("Policy_Type__c")
    private String policyType;
    @JsonProperty("Class__c")
    private String classificationGroup;
    @JsonProperty("Experience_Credibility__c")
    private String credibilityFactor;
    @JsonProperty("Stage__c")
    private String stage;
    @JsonProperty("Current_Carrier__c")
    private String priorCarrierName;
    @JsonProperty("Flat_Benefit_Amount__c")
    private String flatBenefitAmount;
    @JsonProperty("LTD_Employees_Eligible__c")
    private String totalNumberOfEligibleLivesLtd;
    @JsonProperty("STD_Employees_Eligible__c")
    private String totalNumberOfEligibleLivesStd;
    @JsonProperty("Duration__c")
    private String maximumPaymentDuration;
    @JsonProperty("Accident_Elimination_Period__c")
    private String eliminationPeriodInjury;
    @JsonProperty("Sickness_Elimination_Period__c")
    private String eliminationPeriodSickness;
    @JsonProperty("Accident_Elimination_Period_Other__c")
    private String eliminationPeriodInjuryOther;
    @JsonProperty("Sickness_Elimination_Period_Other__c")
    private String eliminationPeriodSicknessOther;
    @JsonProperty("Other_Percent_Description__c")
    private String percentDescriptionOther;
    @JsonProperty("Duration_Other__c")
    private String maximumPaymentDurationOther;
    @JsonProperty("Guarantee_Issue_Amount__c")
    private String guaranteeIssueAmount;
    @JsonProperty("Age_Reductions__c")
    private String ageReduction;
    @JsonProperty("Quote_Record_Type_Holder__c")
    private String quoteRecordTypeHolder;
    @JsonProperty("Product__c")
    private String product;
    @JsonProperty("Waiver_of_Premium__c")
    private String waiverOfPremium;
    @JsonProperty("BL_Employees_Eligible__c")
    private String employeesEligible;
    @JsonProperty("BL_Enrolled__c")
    private String enrolled;
    @JsonProperty("BL_Volume__c")
    private String volume;
    @JsonProperty("Other_Reduction__c")
    private String otherAgeReduction;
    @JsonProperty("Incremental_Multiple_of_Salary__c")
    private String incrementalMultiSalary;
    @JsonProperty("CV_SUM__c")
    private String premiumSummary;
    @JsonProperty("Total_Enrollment__c")
    private String totalEnrollment;
    @JsonProperty("Maximum__c")
    private String maximumBenefitAmount;
    @JsonProperty("X1_Party_Quoted_Enrollment__c")
    private String firstPartyEnrollment;
    @JsonProperty("X1st_Party_Formula_Rate__c")
    private String firstPartyFormulaRate;
    @JsonProperty("X1st_Party_Quoted_Rate__c")
    private String firstPartyQuotedRate;
    @JsonProperty("X2_Party_Quoted_Enrollment__c")
    private String secondPartyEnrollment;
    @JsonProperty("X2nd_Party_Formula_Rate__c")
    private String secondPartyFormulaRate;
    @JsonProperty("X2nd_Party_Quoted_Rate__c")
    private String secondPartyQuotedRate;
    @JsonProperty("X3_Party_Quoted_Enrollment__c")
    private String thirdPartyEnrollment;
    @JsonProperty("X3rd_Party_Formula_Rate__c")
    private String thirdPartyFormulaRate;
    @JsonProperty("X3rd_Party_Quoted_Rate__c")
    private String thirdPartyQuotedRate;
    @JsonProperty("X4_Party_Quoted_Enrollment__c")
    private String fourthPartyEnrollment;
    @JsonProperty("X4th_Party_Formula_Rate__c")
    private String fourthPartyFormulaRate;
    @JsonProperty("X4th_Party_Quoted_Rate__c")
    private String fourthPartyQuotedRate;
    @JsonProperty("BL_Formula_Rate__c")
    private String formulaRate;
    @JsonProperty("BL_Quoted_Rate__c")
    private String quotedRate;

    @JsonProperty("BL_AD_D_Formula_Rate__c")
    private String addFormulaRate;
    @JsonProperty("BL_AD_D_Quoted_Rate__c")
    private String addQuotedRate;

    @JsonProperty("DBL_Formula_Rate__c")
    private String formulaRateDBL;
    @JsonProperty("DBL_Quoted_Rate__c")
    private String quotedRateDBL;

    @JsonProperty("TDB_Formula_Rate__c")
    private String formulaRateTDB;
    @JsonProperty("TDB_Quoted_Rate__c")
    private String quotedRateTDB;

    @JsonProperty("DL_Formula_Rate__c")
    private String formulaRateDL;
    @JsonProperty("DL_Quoted_Rate__c")
    private String quotedRateDL;

    @JsonProperty("FSR_Under_20__c")
    private String rateUnder20;
    @JsonProperty("FSR_20_24__c")
    private String rate20_24;
    @JsonProperty("FSR_25_29__c")
    private String rate25_29;
    @JsonProperty("FSR_30_34__c")
    private String rate30_34;
    @JsonProperty("FSR_35_39__c")
    private String rate35_39;
    @JsonProperty("FSR_40_44__c")
    private String rate40_44;
    @JsonProperty("FSR_45_49__c")
    private String rate45_49;
    @JsonProperty("FSR_50_54__c")
    private String rate50_54;
    @JsonProperty("FSR_55_59__c")
    private String rate55_59;
    @JsonProperty("FSR_60_64__c")
    private String rate60_64;
    @JsonProperty("FSR_65_69__c")
    private String rate65_69;
    @JsonProperty("FSR_70_74__c")
    private String rate70_74;
    @JsonProperty("FSR_75_79__c")
    private String rate75_79;
    @JsonProperty("FSR_85__c")
    private String rate85;

    @JsonProperty("QSR_Under_20__c")
    private String proposedRateUnder20;
    @JsonProperty("QSR_20_24__c")
    private String proposedRate20_24;
    @JsonProperty("QSR_25_29__c")
    private String proposedRate25_29;
    @JsonProperty("QSR_30_34__c")
    private String proposedRate30_34;
    @JsonProperty("QSR_35_39__c")
    private String proposedRate35_39;
    @JsonProperty("QSR_40_44__c")
    private String proposedRate40_44;
    @JsonProperty("QSR_45_49__c")
    private String proposedRate45_49;
    @JsonProperty("QSR_50_54__c")
    private String proposedRate50_54;
    @JsonProperty("QSR_55_59__c")
    private String proposedRate55_59;
    @JsonProperty("QSR_60_64__c")
    private String proposedRate60_64;
    @JsonProperty("QSR_65_69__c")
    private String proposedRate65_69;
    @JsonProperty("QSR_70_74__c")
    private String proposedRate70_74;
    @JsonProperty("QSR_75_79__c")
    private String proposedRate75_79;
    @JsonProperty("QSR_85__c")
    private String proposedRate85;

    @JsonProperty("CV_20_24__c")
    private String totalVolume20_24;
    @JsonProperty("CEL_20_24__c")
    private String participants20_24;

    public String getFormulaRateTDB() {
        return formulaRateTDB;
    }

    public void setFormulaRateTDB(String formulaRateTDB) {
        this.formulaRateTDB = formulaRateTDB;
    }

    public String getQuotedRateTDB() {
        return quotedRateTDB;
    }

    public void setQuotedRateTDB(String quotedRateTDB) {
        this.quotedRateTDB = quotedRateTDB;
    }

    public String getFormulaRateDBL() {
        return formulaRateDBL;
    }

    public void setFormulaRateDBL(String formulaRateDBL) {
        this.formulaRateDBL = formulaRateDBL;
    }

    public String getQuotedRateDBL() {
        return quotedRateDBL;
    }

    public void setQuotedRateDBL(String quotedRateDBL) {
        this.quotedRateDBL = quotedRateDBL;
    }

    public String getFormulaRateDL() {
        return formulaRateDL;
    }

    public void setFormulaRateDL(String formulaRateDL) {
        this.formulaRateDL = formulaRateDL;
    }

    public String getQuotedRateDL() {
        return quotedRateDL;
    }

    public void setQuotedRateDL(String quotedRateDL) {
        this.quotedRateDL = quotedRateDL;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getUnderwritingNotes() {
        return underwritingNotes;
    }

    public void setUnderwritingNotes(String underwritingNotes) {
        this.underwritingNotes = underwritingNotes;
    }

    public String getEliminationPeriod() {
        return eliminationPeriod;
    }

    public void setEliminationPeriod(String eliminationPeriod) {
        this.eliminationPeriod = eliminationPeriod;
    }

    public String getBenefitMaximum() {
        return benefitMaximum;
    }

    public void setBenefitMaximum(String benefitMaximum) {
        this.benefitMaximum = benefitMaximum;
    }

    public String getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(String benefitType) {
        this.benefitType = benefitType;
    }

    public String getEmployerContribution() {
        return employerContribution;
    }

    public void setEmployerContribution(String employerContribution) {
        this.employerContribution = employerContribution;
    }

    public String getSitusStateRelated() {
        return situsStateRelated;
    }

    public void setSitusStateRelated(String situsStateRelated) {
        this.situsStateRelated = situsStateRelated;
    }

    public String getQuoteName() {
        return quoteName;
    }

    public void setQuoteName(String quoteName) {
        this.quoteName = quoteName;
    }

    public String getSicCode() {
        return sicCode;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    public String getPercentOfSalary() {
        return percentOfSalary;
    }

    public void setPercentOfSalary(String percentOfSalary) {
        this.percentOfSalary = percentOfSalary;
    }

    public String getOwnOccPeriod() {
        return ownOccPeriod;
    }

    public void setOwnOccPeriod(String ownOccPeriod) {
        this.ownOccPeriod = ownOccPeriod;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getClassificationGroup() {
        return classificationGroup;
    }

    public void setClassificationGroup(String classificationGroup) {
        this.classificationGroup = classificationGroup;
    }

    public String getCredibilityFactor() {
        return credibilityFactor;
    }

    public void setCredibilityFactor(String credibilityFactor) {
        this.credibilityFactor = credibilityFactor;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getPriorCarrierName() {
        return priorCarrierName;
    }

    public void setPriorCarrierName(String priorCarrierName) {
        this.priorCarrierName = priorCarrierName;
    }

    public String getFlatBenefitAmount() {
        return flatBenefitAmount;
    }

    public void setFlatBenefitAmount(String flatBenefitAmount) {
        this.flatBenefitAmount = flatBenefitAmount;
    }

    public String getTotalNumberOfEligibleLivesLtd() {
        return totalNumberOfEligibleLivesLtd;
    }

    public void setTotalNumberOfEligibleLivesLtd(String totalNumberOfEligibleLivesLtd) {
        this.totalNumberOfEligibleLivesLtd = totalNumberOfEligibleLivesLtd;
    }

    public String getTotalNumberOfEligibleLivesStd() {
        return totalNumberOfEligibleLivesStd;
    }

    public void setTotalNumberOfEligibleLivesStd(String totalNumberOfEligibleLivesStd) {
        this.totalNumberOfEligibleLivesStd = totalNumberOfEligibleLivesStd;
    }

    public String getMaximumPaymentDuration() {
        return maximumPaymentDuration;
    }

    public void setMaximumPaymentDuration(String maximumPaymentDuration) {
        this.maximumPaymentDuration = maximumPaymentDuration;
    }

    public String getEliminationPeriodInjury() {
        return eliminationPeriodInjury;
    }

    public void setEliminationPeriodInjury(String eliminationPeriodInjury) {
        this.eliminationPeriodInjury = eliminationPeriodInjury;
    }

    public String getEliminationPeriodSickness() {
        return eliminationPeriodSickness;
    }

    public void setEliminationPeriodSickness(String eliminationPeriodSickness) {
        this.eliminationPeriodSickness = eliminationPeriodSickness;
    }

    public String getEliminationPeriodInjuryOther() {
        return eliminationPeriodInjuryOther;
    }

    public void setEliminationPeriodInjuryOther(String eliminationPeriodInjuryOther) {
        this.eliminationPeriodInjuryOther = eliminationPeriodInjuryOther;
    }

    public String getEliminationPeriodSicknessOther() {
        return eliminationPeriodSicknessOther;
    }

    public void setEliminationPeriodSicknessOther(String eliminationPeriodSicknessOther) {
        this.eliminationPeriodSicknessOther = eliminationPeriodSicknessOther;
    }

    public String getPercentDescriptionOther() {
        return percentDescriptionOther;
    }

    public void setPercentDescriptionOther(String percentDescriptionOther) {
        this.percentDescriptionOther = percentDescriptionOther;
    }

    public String getMaximumPaymentDurationOther() {
        return maximumPaymentDurationOther;
    }

    public void setMaximumPaymentDurationOther(String maximumPaymentDurationOther) {
        this.maximumPaymentDurationOther = maximumPaymentDurationOther;
    }

    public String getGuaranteeIssueAmount() {
        return guaranteeIssueAmount;
    }

    public void setGuaranteeIssueAmount(String guaranteeIssueAmount) {
        this.guaranteeIssueAmount = guaranteeIssueAmount;
    }

    public String getAgeReduction() {
        return ageReduction;
    }

    public void setAgeReduction(String ageReduction) {
        this.ageReduction = ageReduction;
    }

    public String getQuoteRecordTypeHolder() {
        return quoteRecordTypeHolder;
    }

    public void setQuoteRecordTypeHolder(String quoteRecordTypeHolder) {
        this.quoteRecordTypeHolder = quoteRecordTypeHolder;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getWaiverOfPremium() {
        return waiverOfPremium;
    }

    public void setWaiverOfPremium(String waiverOfPremium) {
        this.waiverOfPremium = waiverOfPremium;
    }

    public String getEmployeesEligible() {
        return employeesEligible;
    }

    public void setEmployeesEligible(String employeesEligible) {
        this.employeesEligible = employeesEligible;
    }

    public String getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(String enrolled) {
        this.enrolled = enrolled;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getOtherAgeReduction() {
        return otherAgeReduction;
    }

    public void setOtherAgeReduction(String otherAgeReduction) {
        this.otherAgeReduction = otherAgeReduction;
    }

    public String getIncrementalMultiSalary() {
        return incrementalMultiSalary;
    }

    public void setIncrementalMultiSalary(String incrementalMultiSalary) {
        this.incrementalMultiSalary = incrementalMultiSalary;
    }

    public String getPremiumSummary() {
        return premiumSummary;
    }

    public void setPremiumSummary(String premiumSummary) {
        this.premiumSummary = premiumSummary;
    }

    public String getTotalEnrollment() {
        return totalEnrollment;
    }

    public void setTotalEnrollment(String totalEnrollment) {
        this.totalEnrollment = totalEnrollment;
    }

    public String getMaximumBenefitAmount() {
        return maximumBenefitAmount;
    }

    public void setMaximumBenefitAmount(String maximumBenefitAmount) {
        this.maximumBenefitAmount = maximumBenefitAmount;
    }

    public String getFirstPartyEnrollment() {
        return firstPartyEnrollment;
    }

    public void setFirstPartyEnrollment(String firstPartyEnrollment) {
        this.firstPartyEnrollment = firstPartyEnrollment;
    }

    public String getFirstPartyFormulaRate() {
        return firstPartyFormulaRate;
    }

    public void setFirstPartyFormulaRate(String firstPartyFormulaRate) {
        this.firstPartyFormulaRate = firstPartyFormulaRate;
    }

    public String getFirstPartyQuotedRate() {
        return firstPartyQuotedRate;
    }

    public void setFirstPartyQuotedRate(String firstPartyQuotedRate) {
        this.firstPartyQuotedRate = firstPartyQuotedRate;
    }

    public String getSecondPartyEnrollment() {
        return secondPartyEnrollment;
    }

    public void setSecondPartyEnrollment(String secondPartyEnrollment) {
        this.secondPartyEnrollment = secondPartyEnrollment;
    }

    public String getSecondPartyFormulaRate() {
        return secondPartyFormulaRate;
    }

    public void setSecondPartyFormulaRate(String secondPartyFormulaRate) {
        this.secondPartyFormulaRate = secondPartyFormulaRate;
    }

    public String getSecondPartyQuotedRate() {
        return secondPartyQuotedRate;
    }

    public void setSecondPartyQuotedRate(String secondPartyQuotedRate) {
        this.secondPartyQuotedRate = secondPartyQuotedRate;
    }

    public String getThirdPartyEnrollment() {
        return thirdPartyEnrollment;
    }

    public void setThirdPartyEnrollment(String thirdPartyEnrollment) {
        this.thirdPartyEnrollment = thirdPartyEnrollment;
    }

    public String getThirdPartyFormulaRate() {
        return thirdPartyFormulaRate;
    }

    public void setThirdPartyFormulaRate(String thirdPartyFormulaRate) {
        this.thirdPartyFormulaRate = thirdPartyFormulaRate;
    }

    public String getThirdPartyQuotedRate() {
        return thirdPartyQuotedRate;
    }

    public void setThirdPartyQuotedRate(String thirdPartyQuotedRate) {
        this.thirdPartyQuotedRate = thirdPartyQuotedRate;
    }

    public String getFourthPartyEnrollment() {
        return fourthPartyEnrollment;
    }

    public void setFourthPartyEnrollment(String fourthPartyEnrollment) {
        this.fourthPartyEnrollment = fourthPartyEnrollment;
    }

    public String getFourthPartyFormulaRate() {
        return fourthPartyFormulaRate;
    }

    public void setFourthPartyFormulaRate(String fourthPartyFormulaRate) {
        this.fourthPartyFormulaRate = fourthPartyFormulaRate;
    }

    public String getFourthPartyQuotedRate() {
        return fourthPartyQuotedRate;
    }

    public void setFourthPartyQuotedRate(String fourthPartyQuotedRate) {
        this.fourthPartyQuotedRate = fourthPartyQuotedRate;
    }

    public String getFormulaRate() {
        return formulaRate;
    }

    public void setFormulaRate(String formulaRate) {
        this.formulaRate = formulaRate;
    }

    public String getQuotedRate() {
        return quotedRate;
    }

    public void setQuotedRate(String quotedRate) {
        this.quotedRate = quotedRate;
    }

    public String getAddFormulaRate() {
        return addFormulaRate;
    }

    public void setAddFormulaRate(String addFormulaRate) {
        this.addFormulaRate = addFormulaRate;
    }

    public String getAddQuotedRate() {
        return addQuotedRate;
    }

    public void setAddQuotedRate(String addQuotedRate) {
        this.addQuotedRate = addQuotedRate;
    }

    public String getRateUnder20() {
        return rateUnder20;
    }

    public void setRateUnder20(String rateUnder20) {
        this.rateUnder20 = rateUnder20;
    }

    public String getRate20_24() {
        return rate20_24;
    }

    public void setRate20_24(String rate20_24) {
        this.rate20_24 = rate20_24;
    }

    public String getRate25_29() {
        return rate25_29;
    }

    public void setRate25_29(String rate25_29) {
        this.rate25_29 = rate25_29;
    }

    public String getRate30_34() {
        return rate30_34;
    }

    public void setRate30_34(String rate30_34) {
        this.rate30_34 = rate30_34;
    }

    public String getRate35_39() {
        return rate35_39;
    }

    public void setRate35_39(String rate35_39) {
        this.rate35_39 = rate35_39;
    }

    public String getRate40_44() {
        return rate40_44;
    }

    public void setRate40_44(String rate40_44) {
        this.rate40_44 = rate40_44;
    }

    public String getRate45_49() {
        return rate45_49;
    }

    public void setRate45_49(String rate45_49) {
        this.rate45_49 = rate45_49;
    }

    public String getRate50_54() {
        return rate50_54;
    }

    public void setRate50_54(String rate50_54) {
        this.rate50_54 = rate50_54;
    }

    public String getRate55_59() {
        return rate55_59;
    }

    public void setRate55_59(String rate55_59) {
        this.rate55_59 = rate55_59;
    }

    public String getRate60_64() {
        return rate60_64;
    }

    public void setRate60_64(String rate60_64) {
        this.rate60_64 = rate60_64;
    }

    public String getRate65_69() {
        return rate65_69;
    }

    public void setRate65_69(String rate65_69) {
        this.rate65_69 = rate65_69;
    }

    public String getRate70_74() {
        return rate70_74;
    }

    public void setRate70_74(String rate70_74) {
        this.rate70_74 = rate70_74;
    }

    public String getRate75_79() {
        return rate75_79;
    }

    public void setRate75_79(String rate75_79) {
        this.rate75_79 = rate75_79;
    }

    public String getRate85() {
        return rate85;
    }

    public void setRate85(String rate85) {
        this.rate85 = rate85;
    }

    public String getProposedRateUnder20() {
        return proposedRateUnder20;
    }

    public void setProposedRateUnder20(String proposedRateUnder20) {
        this.proposedRateUnder20 = proposedRateUnder20;
    }

    public String getProposedRate20_24() {
        return proposedRate20_24;
    }

    public void setProposedRate20_24(String proposedRate20_24) {
        this.proposedRate20_24 = proposedRate20_24;
    }

    public String getProposedRate25_29() {
        return proposedRate25_29;
    }

    public void setProposedRate25_29(String proposedRate25_29) {
        this.proposedRate25_29 = proposedRate25_29;
    }

    public String getProposedRate30_34() {
        return proposedRate30_34;
    }

    public void setProposedRate30_34(String proposedRate30_34) {
        this.proposedRate30_34 = proposedRate30_34;
    }

    public String getProposedRate35_39() {
        return proposedRate35_39;
    }

    public void setProposedRate35_39(String proposedRate35_39) {
        this.proposedRate35_39 = proposedRate35_39;
    }

    public String getProposedRate40_44() {
        return proposedRate40_44;
    }

    public void setProposedRate40_44(String proposedRate40_44) {
        this.proposedRate40_44 = proposedRate40_44;
    }

    public String getProposedRate45_49() {
        return proposedRate45_49;
    }

    public void setProposedRate45_49(String proposedRate45_49) {
        this.proposedRate45_49 = proposedRate45_49;
    }

    public String getProposedRate50_54() {
        return proposedRate50_54;
    }

    public void setProposedRate50_54(String proposedRate50_54) {
        this.proposedRate50_54 = proposedRate50_54;
    }

    public String getProposedRate55_59() {
        return proposedRate55_59;
    }

    public void setProposedRate55_59(String proposedRate55_59) {
        this.proposedRate55_59 = proposedRate55_59;
    }

    public String getProposedRate60_64() {
        return proposedRate60_64;
    }

    public void setProposedRate60_64(String proposedRate60_64) {
        this.proposedRate60_64 = proposedRate60_64;
    }

    public String getProposedRate65_69() {
        return proposedRate65_69;
    }

    public void setProposedRate65_69(String proposedRate65_69) {
        this.proposedRate65_69 = proposedRate65_69;
    }

    public String getProposedRate70_74() {
        return proposedRate70_74;
    }

    public void setProposedRate70_74(String proposedRate70_74) {
        this.proposedRate70_74 = proposedRate70_74;
    }

    public String getProposedRate75_79() {
        return proposedRate75_79;
    }

    public void setProposedRate75_79(String proposedRate75_79) {
        this.proposedRate75_79 = proposedRate75_79;
    }

    public String getProposedRate85() {
        return proposedRate85;
    }

    public void setProposedRate85(String proposedRate85) {
        this.proposedRate85 = proposedRate85;
    }

    public String getTotalVolume20_24() {
        return totalVolume20_24;
    }

    public void setTotalVolume20_24(String totalVolume20_24) {
        this.totalVolume20_24 = totalVolume20_24;
    }

    public String getParticipants20_24() {
        return participants20_24;
    }

    public void setParticipants20_24(String participants20_24) {
        this.participants20_24 = participants20_24;
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
        SalesforceQuoteModel that = (SalesforceQuoteModel) o;
        return Objects.equals(quoteId, that.quoteId) &&
                Objects.equals(plan, that.plan) &&
                Objects.equals(underwritingNotes, that.underwritingNotes) &&
                Objects.equals(eliminationPeriod, that.eliminationPeriod) &&
                Objects.equals(benefitMaximum, that.benefitMaximum) &&
                Objects.equals(benefitType, that.benefitType) &&
                Objects.equals(employerContribution, that.employerContribution) &&
                Objects.equals(situsStateRelated, that.situsStateRelated) &&
                Objects.equals(quoteName, that.quoteName) &&
                Objects.equals(sicCode, that.sicCode) &&
                Objects.equals(percentOfSalary, that.percentOfSalary) &&
                Objects.equals(ownOccPeriod, that.ownOccPeriod) &&
                Objects.equals(policyType, that.policyType) &&
                Objects.equals(classificationGroup, that.classificationGroup) &&
                Objects.equals(credibilityFactor, that.credibilityFactor) &&
                Objects.equals(stage, that.stage) &&
                Objects.equals(priorCarrierName, that.priorCarrierName) &&
                Objects.equals(flatBenefitAmount, that.flatBenefitAmount) &&
                Objects.equals(totalNumberOfEligibleLivesLtd, that.totalNumberOfEligibleLivesLtd) &&
                Objects.equals(totalNumberOfEligibleLivesStd, that.totalNumberOfEligibleLivesStd) &&
                Objects.equals(maximumPaymentDuration, that.maximumPaymentDuration) &&
                Objects.equals(eliminationPeriodInjury, that.eliminationPeriodInjury) &&
                Objects.equals(eliminationPeriodSickness, that.eliminationPeriodSickness) &&
                Objects.equals(eliminationPeriodInjuryOther, that.eliminationPeriodInjuryOther) &&
                Objects.equals(eliminationPeriodSicknessOther, that.eliminationPeriodSicknessOther) &&
                Objects.equals(percentDescriptionOther, that.percentDescriptionOther) &&
                Objects.equals(maximumPaymentDurationOther, that.maximumPaymentDurationOther) &&
                Objects.equals(guaranteeIssueAmount, that.guaranteeIssueAmount) &&
                Objects.equals(ageReduction, that.ageReduction) &&
                Objects.equals(quoteRecordTypeHolder, that.quoteRecordTypeHolder) &&
                Objects.equals(product, that.product) &&
                Objects.equals(waiverOfPremium, that.waiverOfPremium) &&
                Objects.equals(employeesEligible, that.employeesEligible) &&
                Objects.equals(enrolled, that.enrolled) &&
                Objects.equals(volume, that.volume) &&
                Objects.equals(otherAgeReduction, that.otherAgeReduction) &&
                Objects.equals(incrementalMultiSalary, that.incrementalMultiSalary) &&
                Objects.equals(premiumSummary, that.premiumSummary) &&
                Objects.equals(totalEnrollment, that.totalEnrollment) &&
                Objects.equals(maximumBenefitAmount, that.maximumBenefitAmount) &&
                Objects.equals(firstPartyEnrollment, that.firstPartyEnrollment) &&
                Objects.equals(firstPartyFormulaRate, that.firstPartyFormulaRate) &&
                Objects.equals(firstPartyQuotedRate, that.firstPartyQuotedRate) &&
                Objects.equals(secondPartyEnrollment, that.secondPartyEnrollment) &&
                Objects.equals(secondPartyFormulaRate, that.secondPartyFormulaRate) &&
                Objects.equals(secondPartyQuotedRate, that.secondPartyQuotedRate) &&
                Objects.equals(thirdPartyEnrollment, that.thirdPartyEnrollment) &&
                Objects.equals(thirdPartyFormulaRate, that.thirdPartyFormulaRate) &&
                Objects.equals(thirdPartyQuotedRate, that.thirdPartyQuotedRate) &&
                Objects.equals(fourthPartyEnrollment, that.fourthPartyEnrollment) &&
                Objects.equals(fourthPartyFormulaRate, that.fourthPartyFormulaRate) &&
                Objects.equals(fourthPartyQuotedRate, that.fourthPartyQuotedRate) &&
                Objects.equals(formulaRate, that.formulaRate) &&
                Objects.equals(quotedRate, that.quotedRate) &&
                Objects.equals(addFormulaRate, that.addFormulaRate) &&
                Objects.equals(addQuotedRate, that.addQuotedRate) &&
                Objects.equals(rateUnder20, that.rateUnder20) &&
                Objects.equals(rate20_24, that.rate20_24) &&
                Objects.equals(rate25_29, that.rate25_29) &&
                Objects.equals(rate30_34, that.rate30_34) &&
                Objects.equals(rate35_39, that.rate35_39) &&
                Objects.equals(rate40_44, that.rate40_44) &&
                Objects.equals(rate45_49, that.rate45_49) &&
                Objects.equals(rate50_54, that.rate50_54) &&
                Objects.equals(rate55_59, that.rate55_59) &&
                Objects.equals(rate60_64, that.rate60_64) &&
                Objects.equals(rate65_69, that.rate65_69) &&
                Objects.equals(rate70_74, that.rate70_74) &&
                Objects.equals(rate75_79, that.rate75_79) &&
                Objects.equals(rate85, that.rate85) &&
                Objects.equals(proposedRateUnder20, that.proposedRateUnder20) &&
                Objects.equals(proposedRate20_24, that.proposedRate20_24) &&
                Objects.equals(proposedRate25_29, that.proposedRate25_29) &&
                Objects.equals(proposedRate30_34, that.proposedRate30_34) &&
                Objects.equals(proposedRate35_39, that.proposedRate35_39) &&
                Objects.equals(proposedRate40_44, that.proposedRate40_44) &&
                Objects.equals(proposedRate45_49, that.proposedRate45_49) &&
                Objects.equals(proposedRate50_54, that.proposedRate50_54) &&
                Objects.equals(proposedRate55_59, that.proposedRate55_59) &&
                Objects.equals(proposedRate60_64, that.proposedRate60_64) &&
                Objects.equals(proposedRate65_69, that.proposedRate65_69) &&
                Objects.equals(proposedRate70_74, that.proposedRate70_74) &&
                Objects.equals(proposedRate75_79, that.proposedRate75_79) &&
                Objects.equals(proposedRate85, that.proposedRate85) &&
                Objects.equals(totalVolume20_24, that.totalVolume20_24) &&
                Objects.equals(formulaRateDL, that.formulaRateDL) &&
                Objects.equals(quotedRateDL, that.quotedRateDL) &&
                Objects.equals(formulaRateDBL, that.formulaRateDBL) &&
                Objects.equals(quotedRateDBL, that.quotedRateDBL) &&
                Objects.equals(formulaRateTDB, that.formulaRateTDB) &&
                Objects.equals(quotedRateTDB, that.quotedRateTDB) &&
                Objects.equals(participants20_24, that.participants20_24);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), quoteId, formulaRateTDB, quotedRateTDB, formulaRateDBL, quotedRateDBL,formulaRateDL, quotedRateDL, plan, underwritingNotes, eliminationPeriod, benefitMaximum, benefitType, employerContribution, situsStateRelated, quoteName, sicCode, percentOfSalary, ownOccPeriod, policyType, classificationGroup, credibilityFactor, stage, priorCarrierName, flatBenefitAmount, totalNumberOfEligibleLivesLtd, totalNumberOfEligibleLivesStd, maximumPaymentDuration, eliminationPeriodInjury, eliminationPeriodSickness, eliminationPeriodInjuryOther, eliminationPeriodSicknessOther, percentDescriptionOther, maximumPaymentDurationOther, guaranteeIssueAmount, ageReduction, quoteRecordTypeHolder, product, waiverOfPremium, employeesEligible, enrolled, volume, otherAgeReduction, incrementalMultiSalary, premiumSummary, totalEnrollment, maximumBenefitAmount, firstPartyEnrollment, firstPartyFormulaRate, firstPartyQuotedRate, secondPartyEnrollment, secondPartyFormulaRate, secondPartyQuotedRate, thirdPartyEnrollment, thirdPartyFormulaRate, thirdPartyQuotedRate, fourthPartyEnrollment, fourthPartyFormulaRate, fourthPartyQuotedRate, formulaRate, quotedRate, addFormulaRate, addQuotedRate, rateUnder20, rate20_24, rate25_29, rate30_34, rate35_39, rate40_44, rate45_49, rate50_54, rate55_59, rate60_64, rate65_69, rate70_74, rate75_79, rate85, proposedRateUnder20, proposedRate20_24, proposedRate25_29, proposedRate30_34, proposedRate35_39, proposedRate40_44, proposedRate45_49, proposedRate50_54, proposedRate55_59, proposedRate60_64, proposedRate65_69, proposedRate70_74, proposedRate75_79, proposedRate85, totalVolume20_24, participants20_24);
    }
}
