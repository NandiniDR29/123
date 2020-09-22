/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.main.modules.policy.common;

import com.exigen.istf.utils.meters.WaitMeters;

import java.util.Objects;

public abstract class PolicyType {
    public static final String CREATION_METER = "%s_%s_%s_CREATION";

    protected IPolicy policy;
    protected String shortName;
    protected String fullName;

    public PolicyType(String shortName, String fullName, IPolicy policy) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.policy = policy;
        registerMeters();
    }

    private void registerMeters() {
        WaitMeters.register(String.format(CREATION_METER, shortName, "QUOTE", "REST"));
        WaitMeters.register(String.format(CREATION_METER, shortName, "POLICY", "REST"));
    }

    public String getName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getKey() {
        return policy.getClass().getSimpleName();
    }

    public IPolicy get() {
        return policy;
    }


    @Override
    public boolean equals(Object anObject) {
        if (anObject == null) {
            return false;
        }
        if (!PolicyType.class.isAssignableFrom(anObject.getClass())) {
            return false;
        }
        if (!(anObject instanceof PolicyType)) {
            return false;
        }
        PolicyType policyType = (PolicyType) anObject;
        if ((this.shortName == null) ? (policyType.getShortName() != null) : !this.shortName.equals(policyType.getShortName())) {
            return false;
        }
        return (this.fullName == null) ? (policyType.getName() == null) : this.fullName.equals(policyType.getName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(policy, shortName, fullName);
    }
}
