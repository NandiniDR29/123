/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest;

import com.exigen.ipb.eisa.base.application.ApplicationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.DefaultUriResolver;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.util.Map;

/**
 * Uri resolver implementation for etcs-automation project
 *
 * @author ikisly
 */
public class RenUriResolver extends DefaultUriResolver {
    private static Map<Object, Object> predefinedURL;

    static {
        predefinedURL = ImmutableMap.builder()
                .put("bpmdev-rs", "/services/bpm-rs/v1/work/dev")
                .build();
    }

    public String getRestServiceTarget(String name) {
        String propertyTarget = super.getRestServiceTarget(name);

        if (propertyTarget.startsWith(":")) {
            URIBuilder builder = new URIBuilder();
            String appHost = builder.setScheme(ApplicationFactory.getInstance().getMainApplication().getProtocol())
                    .setHost(ApplicationFactory.getInstance().getMainApplication().getHost())
                    .toString();
            return appHost + propertyTarget;
        }

        if (!StringUtils.isEmpty(propertyTarget)) {
            return propertyTarget;
        }

        if (predefinedURL.containsKey(name)) {
            return ApplicationFactory.getInstance().getMainApplication().formatUrl() + predefinedURL.get(name);
        }

        return ApplicationFactory.getInstance().getMainApplication().formatUrl() + String.format("/services/%s/v1", name);
    }
}
