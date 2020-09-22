/* Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AutomationContext {

    private static Map<Class<? extends Action>, Action> actionsContext = new ConcurrentHashMap<>();
    private static Map<Class<? extends EntityService>, EntityService> entityServiceContext = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Action> T getAction(Class<? extends Action> clazz) {
        if (Objects.isNull(actionsContext.get(clazz))) {
            actionsContext.putIfAbsent(clazz, ObjectsFactory.create(clazz));
        }
        return (T) actionsContext.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T extends EntityService> T getService(Class<? extends EntityService> clazz) {
        if (Objects.isNull(entityServiceContext.get(clazz))) {
            entityServiceContext.putIfAbsent(clazz, ObjectsFactory.create(clazz));
        }
        return (T) entityServiceContext.get(clazz);
    }
}
