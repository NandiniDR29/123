package com.exigen.ren.rest.salesforce.model;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseFromItems extends RestError {

    private List<RecentItem> recentItems;

    public void setRecentItems(List<RecentItem> recentItems) {
        this.recentItems = recentItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ResponseFromItems that = (ResponseFromItems) o;
        return recentItems.equals(that.recentItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), recentItems);
    }

    public List<RecentItem> getRecentItems() {
        return recentItems;
    }
}
