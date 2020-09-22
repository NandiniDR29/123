package com.exigen.ren.utils;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.utils.logging.CustomLogger;
import org.apache.commons.io.FileUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;


public class JiraUtils {

    public static boolean jiraEnabled = Boolean.valueOf(PropertyProvider.getProperty("jira.enabled", "false"));
    private static String url = PropertyProvider.getProperty("jira.url", "https://jira.exigeninsurance.com");
    private static String login = PropertyProvider.getProperty("jira.login");
    private static String password = PropertyProvider.getProperty("jira.password");

    private static int connectionTimeout = Integer.valueOf(PropertyProvider.getProperty("http.connection.timeout", "30000"));
    private static int readTimeout = Integer.valueOf(PropertyProvider.getProperty("http.read.timeout", "120000"));
    private static Logger log = CustomLogger.getInstance();
    private static JiraRestClient jiraClient;

    public static JiraRestClient getClient() {
        if (jiraClient == null) {
            try {
                AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
                jiraClient = factory.createWithBasicHttpAuthentication(new URI(url), login, password);
                return jiraClient;
            } catch (Exception e) {
                log.error("Unable to connect to JIRA: ", e);
                return null;
            }

        } else {
            return jiraClient;
        }
    }


    public static void addCommentToIssue(String issueKey, String comment) {
        try {
            getClient().getIssueClient().addComment(getClient().getIssueClient().getIssue(issueKey).get().getCommentsUri(),
                    Comment.valueOf(comment));
            //            log.info("Jira " + issueKey + " commented: " + comment);
        } catch (Exception e) {
            log.error("Can't add the comment to issue: " + issueKey);
        }
    }

    public static String getIssueSummary(String issueKey) {
        try {
            return getClient().getIssueClient().getIssue(issueKey).get().getSummary();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getIssueStatus(String issueKey) {
        try {
            return getClient().getIssueClient().getIssue(issueKey).get().getStatus().getName();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getIssuePriority(String issueKey) {
        try {
            return getClient().getIssueClient().getIssue(issueKey).get().getPriority().getName();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getAutomatedValue(String issueKey) {
        try {
            JSONObject jsonObject = (JSONObject) getClient().getIssueClient().getIssue(issueKey).get().getFieldByName("Automated").getValue();
            return jsonObject.getString("value");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getToBeUpdateValue(String issueKey) {
        try {
            JSONObject jsonObject = (JSONObject) getClient().getIssueClient().getIssue(issueKey).get().getFieldByName("To Be Updated").getValue();
            return jsonObject.getString("value");
        } catch (Exception e) {
            return "";
        }
    }

    public static ArrayList<String> searchKeysOfIssues(String jqlQuery) {
        try {
            ArrayList<String> issuesKeys = new ArrayList<String>();
            SearchResult result = getClient().getSearchClient().searchJql(jqlQuery, 10000, 0, null).get();

            Iterator<Issue> it = result.getIssues().iterator();
            while (it.hasNext()) {
                issuesKeys.add(it.next().getKey());
            }
            return issuesKeys;
        } catch (Exception e) {
            log.error("Unable to get issues from JIRA: ", e);
            return null;
        }
    }

    public static void downloadAttachments(String issueKey, String destinationFolder) {
        downloadAttachments(issueKey, new File(destinationFolder));
    }

    /**
     * Download all file attachments of issue to specified folder
     */
    public static void downloadAttachments(String issueKey, File destinationFolder) {
        try {
            Issue issue = getClient().getIssueClient().getIssue(issueKey).get();
            if (issue != null) {
                Iterator<Attachment> attachments = issue.getAttachments().iterator();
                while (attachments.hasNext()) {
                    downloadAttachment(attachments.next(), destinationFolder);
                }
                log.info("Attachments for issue " + issueKey + " was successfully downloaded to "
                        + destinationFolder.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Attachments for issue " + issueKey + " wasn't downloaded");
        }
    }

    public static void downloadAttachment(String issueKey, String attachmentName, String destinationFolder) {
        downloadAttachment(issueKey, attachmentName, new File(destinationFolder));
    }

    public static void downloadAttachment(String issueKey, String attachmentName, File destinationFolder) {
        try {
            Issue issue = getClient().getIssueClient().getIssue(issueKey).get();
            boolean downloaded = false;
            if (issue != null) {
                Iterator<Attachment> attachments = issue.getAttachments().iterator();
                while (attachments.hasNext()) {
                    Attachment attachment = attachments.next();
                    if (attachment.getFilename().equalsIgnoreCase(attachmentName)) {
                        downloadAttachment(attachment, destinationFolder);
                        downloaded = true;
                    }
                }
                if (downloaded) {
                    log.info("Attachment " + attachmentName + " for issue " + issueKey + " was successfully downloaded");
                } else {
                    log.error("Attachment " + attachmentName + " for issue " + issueKey + " wasn't found");
                }
            }
        } catch (Exception e) {
            log.error("Attachment " + attachmentName + " for issue " + issueKey + " wasn't downloaded");
        }
    }

    private static void downloadAttachment(Attachment attachment, File destinationFolder) {
        try {
            URL u = new URL(attachment.getContentUri().toString());
            URLConnection connection = u.openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);
            String userData = login + ":" + password;
            String encodedUserPass = Base64.encode(userData.getBytes());
            connection.addRequestProperty("Authorization", "Basic " + encodedUserPass);
            InputStream input = connection.getInputStream();
            FileUtils.copyInputStreamToFile(input, new File(destinationFolder.getAbsolutePath() + File.separator
                    + attachment.getFilename()));
        } catch (Exception e) {
            log.error("Unable to download attachments: ", e);
        }
    }

}
