/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ltd_st_std;

import com.exigen.ren.TestDataKey;


public interface DisabilityClaimSTContext extends DisabilityClaimContext {

    default void createDefaultStatutoryDisabilityInsuranceClaimForMasterPolicy() {
        disabilityClaim.create(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultStatutoryDisabilityInsuranceClaimForCertificatePolicy() {
        disabilityClaim.create(disabilityClaim.getSTTestData().getTestData("DataGatherCertificate", TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultStatutoryDisabilityInsuranceClaimWithoutPolicy() {
        disabilityClaim.createWithoutPolicy(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER_WITHOUT_POLICY,
                TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultStatutoryDisabilityInsuranceClaimWithoutBenefits() {
        disabilityClaim.create(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits"));
    }
}
