package com.exigen.ren.rest.integration;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.integration.model.*;
import com.exigen.ren.rest.integration.model.findProvider.IntegrationFindProviderBodyModel;
import com.exigen.ren.rest.integration.model.findProvider.IntegrationFindProviderModel;
import com.exigen.ren.rest.integration.model.IntegrationFindFeeScheduleFeeBodyModel;
import com.exigen.ren.rest.integration.model.IntegrationFindFeeScheduleFeeModel;
import com.exigen.ren.rest.integration.model.IntegrationFindOONFeeBodyModel;
import com.exigen.ren.rest.integration.model.IntegrationFindUcrFeeBodyModel;
import com.fasterxml.jackson.databind.DeserializationFeature;

import javax.ws.rs.core.GenericType;
import java.util.List;

public class IntegrationRestService {

    private RestClient client;

    public IntegrationRestService() {
        JsonConfiguration conf = ConfigurationFactory.get(JsonConfiguration.class);
        conf.getObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        client = new RestClient("integration-provider-rs", conf);
    }

    public ResponseContainer<IntegrationFindFeeScheduleFeeModel> postFindFeeScheduleFee(IntegrationFindFeeScheduleFeeBodyModel model) {
        return new ResponseContainer<>(client.processRequest("FIND.FEE.SCHEDULE.FEE", RestClient.HttpMethod.POST, model),
                new GenericType<IntegrationFindFeeScheduleFeeModel>(){});
    }

    public ResponseContainer<List<IntegrationFindFeeScheduleFeeModel>> postFindFeeScheduleFeeForAllProcedureCode(IntegrationFindFeeScheduleFeeBodyModel model) {
        return new ResponseContainer<>(client.processRequest("FIND.FEE.SCHEDULE.FEE.FOR.ALL.PROCEDURE.CODE", RestClient.HttpMethod.POST, model),
                new GenericType<List<IntegrationFindFeeScheduleFeeModel>>(){});
    }

    public ResponseContainer<List<IntegrationFindFeeScheduleFeeModel>> postFindUCRFeeForAllProcedureCode(IntegrationFindUcrFeeBodyModel model) {
        return new ResponseContainer<>(client.processRequest("FIND.UCR.FEE.FOR.ALL.PROCEDURE.CODE", RestClient.HttpMethod.POST, model),
                new GenericType<List<IntegrationFindFeeScheduleFeeModel>>(){});
    }

    public ResponseContainer<List<IntegrationFindFeeScheduleFeeModel>> postFindFeeByNameForAllProcedureCode(IntegrationFindFeeByNameBodyModel model) {
        return new ResponseContainer<>(client.processRequest("FIND.FEE.BY.NAME.FOR.ALL.PROCEDURE.CODE", RestClient.HttpMethod.POST, model),
                new GenericType<List<IntegrationFindFeeScheduleFeeModel>>(){});
    }

    public ResponseContainer<List<IntegrationFindFeeScheduleFeeModel>> postFindOONFeeForAllProcedureCode(IntegrationFindOONFeeBodyModel model) {
        return new ResponseContainer<>(client.processRequest("FIND.OON.FEE.FOR.ALL.PROCEDURE.CODE", RestClient.HttpMethod.POST, model),
                new GenericType<List<IntegrationFindFeeScheduleFeeModel>>(){});
    }


    public ResponseContainer<IntegrationFindFeeScheduleFeeModel> postFindURCFee(IntegrationFindUcrFeeBodyModel model) {
        return new ResponseContainer<>(client.processRequest("FIND.UCR.FEE", RestClient.HttpMethod.POST, model),
                new GenericType<IntegrationFindFeeScheduleFeeModel>(){});
    }

    public ResponseContainer<IntegrationFindProviderModel> postFindProvider(IntegrationFindProviderBodyModel model) {
        return new ResponseContainer<>(client.processRequest("PROVIDER.FIND.PROVIDER", RestClient.HttpMethod.POST, model),
                new GenericType<IntegrationFindProviderModel>(){});
    }

    public ResponseContainer<IntegrationFindFeeScheduleFeeModel> postFindOONFee(IntegrationFindOONFeeBodyModel model) {
        return new ResponseContainer<>(client.processRequest("FIND.OON.FEE", RestClient.HttpMethod.POST, model),
                new GenericType<IntegrationFindFeeScheduleFeeModel>(){});
    }
}
