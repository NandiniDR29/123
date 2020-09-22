/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.utils;

import com.exigen.istf.config.TestProperties;

import java.util.function.Function;

public class DBServicePropertyConfigurator implements Function<String, String> {
    @Override
    public String apply(String s) {
        String result = "";
        switch (s) {
            case TestProperties.DB_URL: {
                result = "url";
                break;
            }
            case TestProperties.DB_USER: {
                result = "username";
                break;
            }
            case TestProperties.DB_PASSWORD: {
                result = "password";
                break;
            }
        }
        return result;
    }
}
