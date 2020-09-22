package com.exigen.ren.rest.policyBenefits.enrollmentServices;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.client.XmlConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.policyBenefits.enrollmentServices.models.EnrollmentCensusResponseModel;
import com.fasterxml.jackson.databind.DeserializationFeature;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static com.exigen.ipb.eisa.ws.rest.util.RestUtil.convert;

public class EnrollmentRestService {
    private RestClient client;
    private XmlConfiguration xmlConf;

    public EnrollmentRestService() {
        xmlConf = ConfigurationFactory.get(XmlConfiguration.class);
        xmlConf.getObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        client = new RestClient("policy-group-enrollment-rs", xmlConf);
    }

    public <T extends Model> ResponseContainer<EnrollmentCensusResponseModel> postEnrollmentPolicyNumberDeploy(String policyNumber, TestData testData, Class<T> model) {
        try {
            Response response = client.processRequest("ENROLLMENT.POLICY_NUMBER.DEPLOY", RestClient.HttpMethod.POST,
                    convert(testData, model, xmlConf),
                    InlineRequestContext.builder()
                            .pathParam("policynumber", policyNumber)
                            .build());

            // Firstly parse response to String and then parse String to Json model.
            String stringEntityResponse = response.readEntity(String.class);
            // We don't use readEntity() directly because our client is configured by XmlConfiguration(readEntity() expect XML response) but we get JSON response.
            EnrollmentCensusResponseModel responseModel = ConfigurationFactory.get(JsonConfiguration.class).getObjectMapper().readValue(stringEntityResponse, EnrollmentCensusResponseModel.class);
            return new ResponseContainer<>(response, responseModel);
        } catch (IOException e) {
            throw new IstfException("Exception with deserialize JSON content from given JSON content String", e);
        }
    }
}
