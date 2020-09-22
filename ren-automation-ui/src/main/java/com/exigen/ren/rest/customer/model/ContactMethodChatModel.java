/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.customer.model;

import com.exigen.istf.data.TestData;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactMethodChatModel extends ContactMethodModel {

    private String chatId;

    public ContactMethodChatModel() {
    }

    public ContactMethodChatModel(ContactMethodChatModel contactMethod) {
        super(contactMethod);
        this.chatId = contactMethod.getChatId();
    }

    public ContactMethodChatModel(TestData data) {
        setContactMethod(Type.CHAT);
        setContactType(data.getValue("Chat Type").toUpperCase());
        setChatId(data.getValue(GeneralTabMetaData.ChatDetailsMetaData.CHAT_ID.getLabel()));
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatID) {
        this.chatId = chatID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RestError)) {
            return false;
        }

        if (getErrorCode() != null) {
            return super.equals(o);
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        ContactMethodChatModel that = (ContactMethodChatModel) o;

        return chatId != null ? chatId.equals(that.chatId) : that.chatId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (chatId != null ? chatId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String parentToString = super.toString();
        return parentToString + "\nChildClass\nContactMethodChat{" +
                "chatId='" + chatId + '\'' +
                '}';
    }

}
