/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.common;

import com.exigen.istf.exceptions.IstfException;

public class ObjectsFactory {

    /**
     * Creates an object of any class if default constructor is present
     *
     * @param clazz class of the object which instance should be created
     * @return created object's instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<?> clazz) {
        T objectInstance;
        try {
            objectInstance = (T) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IstfException(String.format("Unable to instantiate Object for class [%1$s], please ensure that default constructor is present for requested object",
                    clazz.getSimpleName()), e);
        }
        return objectInstance;
    }

}
