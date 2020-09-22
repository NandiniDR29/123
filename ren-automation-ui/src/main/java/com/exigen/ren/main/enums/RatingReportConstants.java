package com.exigen.ren.main.enums;

import com.exigen.istf.webdriver.controls.Named;

public final class RatingReportConstants {

    //Tiers constants
    public static final String EMPLOYEE_ONLY = "Employee only";
    public static final String EMPLOYEE_FAMILY = "Employee + Family";
    public static final String EMPLOYEE_CHILD_REN = "Employee + Child(ren)";
    public static final String EMPLOYEE_SPOUSE = "Employee + Spouse";
    public static final String COMPOSITE_RATE = "Composite Rate";
    public static final String ANNUAL_PREMIUM = "Annual Premium";
    public static final String PCT_LOWERCASE_FROM_CURRENT = "% from Current";
    public static final String PCT_LOWERCASE_FROM_RENEWAL = "% from Renewal";
    //UW Adjustments constants
    public static final String DUAL_OPTION = "Dual Option";
    public static final String PARTICIPATION_ADJ = "Participation Adjustment";
    public static final String FLEX_PLAN = "Flex Plan";
    public static final String WAIVE_WAITING_PERIOD = "Waive Waiting Period";
    public static final String LIFETIME_DEDUCTIBLE = "Lifetime Deductible";
    public static final String YEARLY_ORTHO_MAXIMUM = "Yearly Ortho Maximum";
    public static final String INDIVIDUAL_ADJ = "Individual Adjustment";
    public static final String ACTUARIAL_BENEFIT_ADJ = "Actuarial Benefit Adjustment";
    public static final String OTHER = "Other";
    //UW Multi Option constants
    public static final String CURRENT_SPREAD = "Current Spread";
    public static final String MANUAL_SPREAD = "Manual Spread";


    public static final class DemographicSection {
        public static final String INDEMNITY = "Indemnity";
        public static final String PPO = "PPO";
        public static final String PPO_NETWORK_PENETRATION = "PPO Network Penetration";
        public static final String EPO_AREA_FACTOR = "EPO Area Factor";
        public static final String EPO_NETWORK_PENETRATION = "EPO Network Penetration";
        public static final String AREA_FACTOR_BLEND = "Area Factor Blend";
    }

    public static final class FactorsSectionFields {
        public static final String FACTORS = "Factors";
        public static final String INITIAL_LOAD = "INITIAL LOAD";
        public static final String OCCUPATION = "OCCUPATION";
        public static final String OCCUPATION_CODE = "OCCUPATION CODE";
        public static final String WAIT_GRP_SIZE = "WAIT GRP SIZE";
        public static final String PLAN_UTIL = "PLAN UTIL";
        public static final String GROUP_SIZE = "GROUP SIZE";
        public static final String CAL_YR_LOAD = "CAL YR LOAD";
        public static final String VOL_LOAD = "VOL LOAD";
        public static final String WAIT_LOAD = "WAIT LOAD";
        public static final String ROLLOVER_LOAD = "ROLLOVER LOAD";
        public static final String PREVENTIVE = "PREVENTIVE";
        public static final String RADIOGRAPHS = "RADIOGRAPHS";
        public static final String MAJOR_RESTORATIVE = "MAJOR RESTORATIVE";
        public static final String OTHER_CLASS_SECOND = "OTHER CLASS II";
        public static final String PROSTHODONTICS = "PROSTHODONTICS";
    }

    public static final class NetworkSectionFields {
        public static final String AREA = "AREA";
        public static final String TREND = "TREND";
        public static final String MAXIMUM_EE_AND_SP = "MAXIMUM(EE AND SP)";
        public static final String MAXIMUM_CH = "MAXIMUM(CH)";
        public static final String NETWORK_PENETRATION = "Network Penetration";
        public static final String STEERAGE = "Steerage";
        public static final String NET_UTILIZATION = "Net. Utilization";
    }

    public static final class NetworkDetailsRows {
        public static final String PREVENTIVE = "Preventive";
        public static final String RADIOGRAPHS = "Radiographs";
        public static final String MAJOR_RESTORATIVE = "Major Restorative";
        public static final String OTHER_CLASS_SECOND = "Other Class II";
        public static final String PROSTODONTICS = "Prosthodontics";
        public static final String TOTAL = "TOTAL";
    }

    public static final class Networks {
        public static final String PPO = "PPO";
        public static final String NON_PARTICIPATING = "Non-Participating";
        public static final String EPO = "EPO";
    }

    public static final class NetworksTier {
        public static final String EMPLOYEE = "Employee";
        public static final String SPOUSE = "Spouse";
        public static final String CHILD = "Child";
    }

    public enum FullyInsuredTable implements Named {
        DESCRIPTION("DESCRIPTION"),
        ANNUAL_AMOUNT("ANNUAL\nAMOUNT"),
        PCT_OF_PREMIUM("% OF PREMIUM"),
        PER_CLAIM("PER CLAIM"),
        PER_EMPLOYEE("PER\nEMPLOYEE");
        private final String name;

        FullyInsuredTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum AsoAdminCostDentalTable implements Named {
        FULLY_INSURED_PREMIUM_NET_COMMISSIONS("Fully Insured Premium Net Commissions"),
        ADMIN_EXPENSES("Admin Expenses"),
        CLAIMS("Claims"),
        RISK_AND_TAXES("Risk & Taxes"),
        LOST_INTEREST("Lost Interest"),
        ASO_PRE_COMMISSION("ASO Pre-Commission"),
        ASO_PRE_COMMISSION_PERM("ASO Pre-Commission PEPM"),
        EXCEPTION("Exception"),
        ASO_NET_COMMISSIONS("ASO Net Commissions"),
        COMMISSION("Commission"),
        FINAL_ASO_PERM("Final ASO PEPM"),
        FINAL_ASO_TOTAL("Final ASO Total"),
        ASO_EQUVALENCY_TOTAL("ASO Equivalency Total");
        private final String name;

        AsoAdminCostDentalTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum EnrolledByStateTable implements Named {
        STATE("STATE"),
        ELIGIBLE("ELIGIBLE"),
        PERCENT("PERCENT");
        private final String name;

        EnrolledByStateTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum NetworkDetailsTable implements Named {
        BENEFIT("BENEFIT"),
        BASE("BASE"),
        DED_CREDIT("DED CREDIT"),
        DED_UTIL("DED UTIL"),
        ONE_YEAR_CLAIMS_RATE("ONE YEAR\n"+"CLAIMS RATE"),
        ONE_YEAR_WEIGHTED("ONE YEAR\n"+"WEIGHTED");
        private final String name;

        NetworkDetailsTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public enum RedistributionCalculatorTierTable implements Named {
        TIER("TIER"),
        ADJUSTED_MANUAL_RATES("ADJUSTED MANUAL RATES"),
        ADJUSTED_FORMULA_RATES("ADJUSTED FORMULA RATES"),
        REDISTRIBUTED_RATES("REDISTRIBUTED RATES");
        private final String name;

        RedistributionCalculatorTierTable(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

}
