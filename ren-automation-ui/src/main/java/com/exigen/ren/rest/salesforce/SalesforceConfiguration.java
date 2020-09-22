package com.exigen.ren.rest.salesforce;

import com.exigen.ipb.eisa.base.config.CustomTestProperties;
import com.exigen.ipb.eisa.ws.rest.conf.client.DefaultConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.client.IClientConfiguration;
import com.exigen.ipb.eisa.ws.rest.features.AdditionalLogging;
import com.exigen.ipb.eisa.ws.rest.features.AuthRemoval;
import com.exigen.ipb.eisa.ws.rest.features.RestLogger;
import com.exigen.ipb.eisa.ws.rest.features.mapper.AbstractContextResolver;
import com.exigen.ipb.eisa.ws.rest.features.mapper.JsonContextResolver;
import com.exigen.istf.config.ClassConfigurator;
import com.exigen.istf.config.ClassConfigurator.Configurable;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import java.util.logging.Level;

public class SalesforceConfiguration implements IClientConfiguration {

    protected ClientConfig clientConfig = new ClientConfig();
    protected Level logLvl = Level.parse("INFO");
    protected AbstractContextResolver contextResolver = initContextResolver();

    public ClientConfig getConfig() {
        return clientConfig;
    }

    public SalesforceConfiguration() {
        clientConfig
                .property(ClientProperties.CONNECT_TIMEOUT, PropertyProvider.getProperty(CustomTestProperties.REST_CLIENT_CONNECTION_TIMEOUT, "0"))
                .property(ClientProperties.READ_TIMEOUT, PropertyProvider.getProperty(CustomTestProperties.REST_CLIENT_READ_TIMEOUT, "0"))

                .register(SalesforceRequestFilter.class)
                .register(new LoggingFeature(new RestLogger(), logLvl, LoggingFeature.Verbosity.PAYLOAD_TEXT, 30000000))
                .register(new AdditionalLogging())
                .register(contextResolver)
                .register(MultiPartWriter.class);
    }

    protected AbstractContextResolver initContextResolver() {
        return new JsonContextResolver();
    }

    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
