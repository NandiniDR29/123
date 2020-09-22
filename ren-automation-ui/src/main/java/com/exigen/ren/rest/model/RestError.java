/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.rest.model;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.exigen.istf.data.TestData;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestError extends Model {

    protected String errorCode;
    protected String message;
    protected String field;
    private boolean fieldEqualsContainsEnabled;
    protected List<RestError> errors;

    public RestError() {
    }

    public RestError(String errorCode, String message, String field) {
        this.errorCode = errorCode;
        this.message = message;
        this.field = field;
    }

    public RestError(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public RestError(TestData testData) {
        this.errorCode = testData.getValue("errorCode");
        this.message = testData.getValue("message");
        this.field = testData.getValue("field");
        if (!testData.getTestDataList("errors").isEmpty()) {
            errors = new ArrayList<>();
            for (TestData data : testData.getTestDataList("errors")) {
                this.errors.add(new RestError(data));
            }
        }
    }

    public List<RestError> getErrors() {
        return errors;
    }

    public void setErrors(List<RestError> errors) {
        this.errors = errors;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    /**
     * If used then {@link RestError} field attribute will skip each pair of characters having *
     *
     * @return
     */
    public RestError setFieldEqualsContainsEnabled() {
        fieldEqualsContainsEnabled = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestError)) {
            return false;
        }
        RestError error = (RestError) o;

        if (errorCode != null ? !errorCode.equals(error.errorCode) : error.errorCode != null) {
            return false;
        }
        if (message != null ? !message.equals(error.message) : error.message != null) {
            return false;
        }
        return field != null ? equalsContains(field, error.field) : error.field == null;
    }

    @Override
    public int hashCode() {
        int result = errorCode != null ? errorCode.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (field != null ? field.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Error{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", field='" + field + '\'' +
                '}';
    }

    private boolean equalsContains(String s1, String s2) {
        return fieldEqualsContainsEnabled ? s1.equals(s2) || s1.contains(s2) || s2.contains(s1) : s1.equals(s2);
    }

    public enum Key {
        ERROR_CODE("errorCode"), FIELD("field"), MESSAGE("message");
        private String key;

        Key(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    public enum ErrorCode {
        ERROR_403("403"), ERROR_404("404"), ERROR_422("422");

        private String code;

        ErrorCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

}
