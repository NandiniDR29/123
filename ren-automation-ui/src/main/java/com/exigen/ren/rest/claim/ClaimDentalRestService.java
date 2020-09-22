package com.exigen.ren.rest.claim;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.data.TestData;
import com.exigen.ren.TestDataProvider;
import com.exigen.ren.common.EntityService;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.claim.model.common.claimbody.ClaimBodyModel;
import com.exigen.ren.rest.claim.model.dental.claimImage.ClaimImageModel;
import com.exigen.ren.rest.claim.model.dental.claiminfo.ClaimInfoModel;
import com.exigen.ren.rest.claim.model.dental.claiminterest.ClaimInterestModel;
import com.exigen.ren.rest.claim.model.dental.processediclaim.ProcessEdiClaimResponseModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class ClaimDentalRestService implements EntityService {

    private TestData defaultTestData = TestDataProvider.getDefaultTestDataProvider().get("modules/claim/gb_dn/rest");

    private RestClient client;
    private JsonConfiguration configuration;

    public ClaimDentalRestService() {
        configuration = ConfigurationFactory.get(JsonConfiguration.class);
        configuration.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        configuration.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client = new RestClient("claims-dental", configuration);
    }

    public ResponseContainer<ProcessEdiClaimResponseModel> processEdiClaim(TestData data) {
        return new ResponseContainer<>(client.processRequest("CLAIMS.PROCESS.EDI.CLAIM", RestClient.HttpMethod.POST,
                RestUtil.convert(data, ClaimBodyModel.class, configuration)), ProcessEdiClaimResponseModel.class);
    }

    public ResponseContainer<ClaimInterestModel> getClaimInterest(String claimNumber) {
        return new ResponseContainer<>(client.processRequest("CLAIMS.INTEREST", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("Accept", "application/json").pathParam("claimNumber", claimNumber).build()),
                ClaimInterestModel.class);
    }

    public ResponseContainer<ClaimInfoModel> getClaimInfo(String claimNumber) {
        return new ResponseContainer<>(client.processRequest("CLAIMS.INFO", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("Accept", "application/json").pathParam("claimNumber", claimNumber).build()),
                ClaimInfoModel.class);
    }

    public ResponseContainer<ClaimImageModel> getClaimImage(String claimNumber) {
        return new ResponseContainer<>(client.processRequest("CLAIMS.IMAGE", RestClient.HttpMethod.GET, null,
                InlineRequestContext.builder().header("Accept", "application/json").pathParam("claimNumber", claimNumber).build()),
                ClaimImageModel.class);
    }

    @Override
    public TestData defaultTestData() {
        return defaultTestData;
    }
}
