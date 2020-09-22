package com.exigen.ren.helpers.logging;

public class RatingLogHolder {

    private RatingLog requestLog;
    private RatingLog responseLog;

    public RatingLogHolder() {
        this("", "", "");
    }

    public RatingLogHolder(String ratingRequestLogContent, String ratingResponseLogContent, String logId) {
        this.requestLog = new RatingLog(ratingRequestLogContent, logId);
        this.responseLog = new RatingLog(ratingResponseLogContent, logId);
    }

    public RatingLog getRequestLog() {
        return requestLog;
    }

    public void setRequestLog(String requestLogContent) {
        this.requestLog = new RatingLog(requestLogContent, "");
    }

    public void setRequestLog(String requestLogContent, String logSectionId) {
        this.requestLog = new RatingLog(requestLogContent, logSectionId);
    }

    public RatingLog getResponseLog() {
        return responseLog;
    }

    public void setResponseLog(String responseLogContent) {
        this.responseLog = new RatingLog(responseLogContent, "");
    }

    public void setResponseLog(String responseLogContent, String logSectionId) {
        this.responseLog = new RatingLog(responseLogContent, logSectionId);
    }

}
