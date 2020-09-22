/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common;

import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;

import java.util.Objects;

/**
 * Common Interface for all entities used within project. I.e. IndividualBenefitsPolicy, IBilling, IClaim etc. *
 */
public interface EntityService {

    /**
     * Provides ability to get default test data relevant to the entity.
     *
     * @param testDataKey - define keys if some particular test data is needed
     * @return - {@link TestData} all test data objects fetched from files stored in entity's test data path
     */
    default TestData getDefaultTestData(String... testDataKey) {
        if (Objects.nonNull(defaultTestData())) {
            return Objects.nonNull(testDataKey) && testDataKey.length != 0 ? defaultTestData().getTestData(testDataKey).resolveLinks() : defaultTestData().resolveLinks();
        }
        throw new IstfException(String.format("Not supported or absent for current type of entity[%1$s]. Please implement method or add test data reference to Entity Service implementation if needed.",
                this.getClass()));
    }

    TestData defaultTestData();

}
