/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.platform.bpm;

import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.XmlConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.istf.data.DataProviderFactory;
import com.exigen.ren.rest.platform.bpm.model.processinstance.ProcessInstanceInfoReplyModel;
import com.exigen.ren.rest.platform.bpm.model.processinstance.ProcessInstanceInfoRequestModel;

import static com.exigen.ipb.eisa.ws.rest.util.RestUtil.convert;

public class BPMDevRestService {
    private RestClient client = new RestClient("bpmdev-rs", ConfigurationFactory.get(XmlConfiguration.class));

    public ProcessInstanceInfoReplyModel postProcessItem(String processKey, String entityRefNo, String entityType) {
        return client.processRequest("PROCESS.ITEM", RestClient.HttpMethod.POST,
                convert(DataProviderFactory.emptyData(), ProcessInstanceInfoRequestModel.class, ConfigurationFactory.get(XmlConfiguration.class)),
                InlineRequestContext.builder()
                        .pathParam("processKey", processKey)
                        .pathParam("entityRefNo", entityRefNo)
                        .pathParam("entityType", entityType).build())
                .readEntity(ProcessInstanceInfoReplyModel.class);
    }
}
