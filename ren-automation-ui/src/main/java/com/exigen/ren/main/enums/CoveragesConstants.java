package com.exigen.ren.main.enums;

public class CoveragesConstants {

    private CoveragesConstants() {
    }

    public static final class TermLifeCoverages {
        public static final String BTL = "Employee Basic Life Insurance";
        public static final String BTL_BL  = "Employee Basic Life Insurance - Basic Life";
        public static final String DEP_BTL = "Dependent Basic Life Insurance (Spouse & Child)";
        public static final String SP_VOL_BTL = "Spouse Voluntary Life Insurance";
        public static final String DEP_VOL_BTL = "Child Voluntary Life Insurance";
        public static final String ADD = "Employee Basic Accidental Death and Dismemberment Insurance";
        public static final String ADD_BL = "Employee Basic Accidental Death and Dismemberment Insurance - Basic Life";
        public static final String VOL_BTL = "Employee Voluntary Life Insurance";
        public static final String VOL_ADD = "Employee Voluntary Accidental Death and Dismemberment Insurance";
        public static final String DEP_ADD = "Dependent Basic Accidental Death and Dismemberment Insurance (Spouse & Child)";
        public static final String DEP_VOL_ADD = "Dependent Voluntary Accidental Death and Dismemberment Insurance (Spouse & Child)";
        public static final String ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY = "Employee Basic Accidental Death and Dismemberment Insurance - Basic Life + Voluntary";
        public static final String VOL_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY = "Employee Voluntary Accidental Death and Dismemberment Insurance - Basic Life + Voluntary";
        public static final String DEP_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY = "Dependent Basic Accidental Death and Dismemberment Insurance (Spouse & Child) - Basic Life + Voluntary";
        public static final String DEP_VOL_ADD_BASIC_LIFE_PLAN_PLUS_VOLUNTARY = "Dependent Voluntary Accidental Death and Dismemberment Insurance (Spouse & Child) - Basic Life + Voluntary";
        public static final String BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY = "Employee Basic Life Insurance - Basic Life + Voluntary";
        public static final String VOL_BTL_BASIC_LIFE_PLAN_PLUS_VOLUNTARY = "Employee Voluntary Life Insurance - Basic Life + Voluntary";
    }

    public static final class TermLifeCoveragesId {
        public static final String BTL = "BTL";
        public static final String DEP_BTL = "DEP_BTL";
        public static final String SP_VOL_BTL = "SP_VOL_BTL";
        public static final String DEP_VOL_BTL = "DEP_VOL_BTL";
        public static final String ADD = "ADD";
        public static final String VOL_BTL = "VOL_BTL";
        public static final String VOL_ADD = "VOL_ADD";
        public static final String DEP_ADD = "DEP_ADD";
        public static final String DEP_VOL_ADD = "DEP_VOL_ADD";
    }

    public static final class GroupAccidentCoverages {
        public static final String BASIC_ACCIDENT = "Basic Accident";
        public static final String ENHANCED_ACCIDENT = "Enhanced Accident";
    }

    public static final class GroupStatutoryCoverages {
        public static final String ENHANCED_NY = "Enhanced NY";
        public static final String PFL_NY = "PFL NY";
        public static final String STAT_NJ = "Stat NJ";
        public static final String STAT_NY = "Stat NY";
        public static final String PFL_NY_STATUTORY_NY = "PFL NY - Statutory New York";
    }

    public static final class GroupPFLCoverages {
        public static final String PFL = "PFL";
        public static final String PFL_FLB_FAMILY_LEAVE_BENEFIT = "PFL - FLB Family Leave Benefit";
    }

    public static final class GroupDentalCoverages {
        public static final String DENTAL = "Dental";
        public static final String EMPLOYEE_ONLY = "Employee Only";
        public static final String EMPLOYEE_SPOUSE = "Employee + Spouse";
        public static final String EMPLOYEE_CHILD_REN = "Employee + Child(ren)";
        public static final String EMPLOYEE_FAMILY = "Employee + Family";
        public static final String EMPLOYEE_ONE = "Employee + 1";
        public static final String COMPOSITE = "Composite tier";
    }

    public static final class GroupVisionCoverages {
        public static final String VISION = "Vision";
    }

    public static final class GroupLTDCoverages {
        public static final String LTD_CORE = "LTD Core";
    }

    public static final class GroupSTDCoverages {
        public static final String STD_CORE = "STD Core";
    }

    public static final class CoverageTiers {
        public static final String EMPLOYEE_ONLY = "Employee Only";
        public static final String EMPLOYEE_PLUS_FAMILY = "Employee + Family";
        public static final String EMPLOYEE_PLUS_SPOUSE = "Employee + Spouse";
        public static final String EMPLOYEE_PLUS_CHILDREN = "Employee + Children";
        public static final String EMPLOYEE_PLUS_CHILD_REN = "Employee + Child(ren)";
        public static final String EMPLOYEE_PLUS_ONE = "Employee + 1";
    }
}