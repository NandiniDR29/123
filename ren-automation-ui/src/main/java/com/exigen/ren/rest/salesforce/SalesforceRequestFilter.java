package com.exigen.ren.rest.salesforce;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.ren.RenTestProperties;
import com.exigen.ren.rest.salesforce.model.SalesforceLoginResponseModel;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import java.net.URI;

public class SalesforceRequestFilter implements ClientRequestFilter {

    protected static String clientId = PropertyProvider.getProperty(RenTestProperties.SALESFORCE_CLIENT_ID);
    protected static String clientSecret = PropertyProvider.getProperty(RenTestProperties.SALESFORCE_CLIENT_SECRET);
    protected static String username = PropertyProvider.getProperty(RenTestProperties.SALESFORCE_USERNAME);
    protected static String password = PropertyProvider.getProperty(RenTestProperties.SALESFORCE_PASSWORD);

    private static String token;

    @Override
    public void filter(ClientRequestContext requestContext) {
        if (token == null) {
            SalesforceLoginResponseModel loginResponse = new RestClient("salesforce").processRequest("LOGIN", RestClient.HttpMethod.POST,
                    null, InlineRequestContext.builder().queryParam("grant_type", "password").queryParam("client_id", clientId)
                            .queryParam("client_secret", clientSecret).queryParam("username", username).queryParam("password", password).build())
                    .readEntity(SalesforceLoginResponseModel.class);
            token = loginResponse.getAccessToken();
        }
        requestContext.getHeaders().putSingle(HttpHeaders.AUTHORIZATION, "Bearer "+token);

        // Encode plus sign as %2B in URL
        requestContext.setUri(URI.create(requestContext.getUri().toString().replace("+", "%2B")));
    }
}