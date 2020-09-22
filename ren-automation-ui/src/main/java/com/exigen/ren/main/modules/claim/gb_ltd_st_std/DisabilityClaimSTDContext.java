/*
 * Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.main.modules.claim.gb_ltd_st_std;

import com.exigen.ren.TestDataKey;


public interface DisabilityClaimSTDContext extends DisabilityClaimContext {

    default void createDefaultShortTermDisabilityClaimForMasterPolicy() {
        disabilityClaim.create(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultShortTermDisabilityClaimForCertificatePolicy() {
        disabilityClaim.create(disabilityClaim.getSTDTestData().getTestData("DataGatherCertificate", TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

    default void createDefaultSTDClaimForCertificatePolicyWithoutBenefits() {
        disabilityClaim.create(disabilityClaim.getSTDTestData().getTestData("DataGatherCertificate", "TestData_Without_Benefits"));
    }

    default void createDefaultShortTermDisabilityClaimWithoutPolicy() {
        disabilityClaim.createWithoutPolicy(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER_WITHOUT_POLICY,
                TestDataKey.DEFAULT_TEST_DATA_KEY));
    }

}
