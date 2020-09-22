/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package com.exigen.ren.rest.security;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.ipb.eisa.ws.rest.conf.client.ConfigurationFactory;
import com.exigen.ipb.eisa.ws.rest.conf.client.JsonConfiguration;
import com.exigen.ipb.eisa.ws.rest.conf.metadata.InlineRequestContext;
import com.exigen.ipb.eisa.ws.rest.util.RestUtil;
import com.exigen.istf.data.TestData;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.customer.model.UserModel;

import static com.exigen.ipb.eisa.ws.rest.RestClient.HttpMethod.*;

@SuppressWarnings("unchecked")
public class SecurityRestService {

    private RestClient restClient;

    public SecurityRestService(User user) {
        restClient = new RestClient("security-rs", ConfigurationFactory.get(JsonConfiguration.class, user));
    }

    public SecurityRestService() {
        restClient = new RestClient("security-rs", ConfigurationFactory.get(JsonConfiguration.class));
    }

    public ResponseContainer<UserModel> getUser(String user) {
        return new ResponseContainer(restClient.processRequest("USER.GET", GET, null,
                InlineRequestContext.builder().pathParam("userLogin", user).build()), UserModel.class);
    }

    public ResponseContainer<UserModel> getUser(String login, String password, String user) {
        return new ResponseContainer(restClient.processRequest("USER.GET", GET, null,
                InlineRequestContext.builder().user(new User(login, password)).pathParam("userLogin", user).build()), UserModel.class);
    }

    public ResponseContainer<UserModel> postUser(TestData td) {
        return new ResponseContainer(restClient.processRequest("USER.POST", POST, RestUtil.convert(td, UserModel.class)),
                UserModel.class);
    }

    public ResponseContainer<UserModel> postUser(UserModel userModel) {
        return new ResponseContainer(restClient.processRequest("USER.POST", POST, userModel),
                UserModel.class);
    }

    public ResponseContainer<UserModel> postUser(String login, String password, TestData td) {
        return new ResponseContainer(restClient.processRequest("USER.POST", POST, RestUtil.convert(td, UserModel.class),
                InlineRequestContext.builder().user(new User(login, password)).build()), UserModel.class);
    }

    public ResponseContainer<UserModel> postUser(User user, TestData td) {
        return new ResponseContainer(restClient.processRequest("USER.POST", POST, RestUtil.convert(td, UserModel.class),
                InlineRequestContext.builder().user(user).build()), UserModel.class);
    }

    public ResponseContainer<UserModel> putUser(TestData td, String userName) {
        return new ResponseContainer(restClient.processRequest("USER.PUT", PUT, RestUtil.convert(td, UserModel.class),
                InlineRequestContext.builder().pathParam("userName", userName).build()), UserModel.class);
    }

    public ResponseContainer<UserModel> putUser(UserModel userModel, String userName) {
        return new ResponseContainer(restClient.processRequest("USER.PUT", PUT, userModel,
                InlineRequestContext.builder().pathParam("userName", userName).build()), UserModel.class);
    }


}
