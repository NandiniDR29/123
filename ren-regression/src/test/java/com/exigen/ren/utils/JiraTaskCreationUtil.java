package com.exigen.ren.utils;

import com.atlassian.httpclient.api.ResponseTransformationException;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.*;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class JiraTaskCreationUtil extends BaseTest {
    private final JiraRestClient client = JiraUtils.getClient();

    @Test
    public void fillBacklogWithQAATasksFromNF() {
        String sprintNumber = PropertyProvider.getProperty("test.current.sprint.number");
        boolean linkCases = PropertyProvider.getProperty("test.task.link.cases", false);
        if (sprintNumber.isEmpty()) {
            throw new IstfException("Please set test.current.sprint.number prop");
        }
        createQAATasksInBacklogFromNFView(sprintNumber, linkCases);
    }


    private void createQAATasksInBacklogFromNFView(String sprintNumber, boolean linkCases) {
        String jiraSourceQuery = "project = REN AND issuetype = \"New Feature\" AND status in (\"Ready For Delivery\", \"In Customer Validation\", \"Passed Customer Validation\", \"Failed Customer Validation\", \"Closed\") AND \"DEV Sprint\" = \"%s\" ORDER BY status, component ASC";
        List<String> withoutDevTc = new ArrayList<>();
        List<String> createdIssues = new ArrayList<>();
        try {
            Iterable<Issue> issues = client.getSearchClient().searchJql(String.format(jiraSourceQuery, sprintNumber),1000, 0, Collections.emptySet()).get().
                    getIssues();
            Iterator<Issue> iter = issues.iterator();
            IssueType qaaTaskType = ((ArrayList<IssueType>) client.getMetadataClient().getIssueTypes().get()).stream().filter(issueType -> issueType.getName().equals("QAA Task"))
                    .collect(Collectors.toList()).get(0);
            while (iter.hasNext()) {
                Issue issue = iter.next();
                if (isQaaTaskExistsByName(issue) && isCoveredByAutomationTask(issue)) {
                    LOGGER.info("{} was skipped as already added to automation", issue.getKey());
                    continue;//skip already created tasks which contain NF issueKey in their summary
                }

                if (hasLinkedTcWithDevName(issue)) {
                    LOGGER.info("Need to create qaa task for {}", issue.getKey());
                    createQaaTask(sprintNumber, createdIssues, qaaTaskType, issue, linkCases);
                } else {
                    LOGGER.info("{} : {} -> doesn't have Dev TC", issue.getKey(), issue.getSummary());
                    withoutDevTc.add(issue.getKey());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Exception " , e);
        }
        LOGGER.info("without dev TC {}", withoutDevTc);
        LOGGER.info("Full list of created issues: " + createdIssues);
    }

    private void createQaaTask(String sprintNumber, List<String> createdIssues, IssueType qaaTaskType, Issue issue, boolean linkCases) {
        String components = ((ArrayList<BasicComponent>) issue.getComponents()).stream()
                .map(component -> "[" + component.getName() + "]").collect(Collectors.joining());
        IssueInput newIssue = new IssueInputBuilder()
                .setSummary("[" + sprintNumber + "]" + components + "[" + issue.getKey() + "]" + issue.getSummary())
                //.setFieldValue(sprintField.getId(), sprintField.getValue())      - no rights for this field
                .setPriority(issue.getPriority())
                .setProject(issue.getProject())
                .setComponents(issue.getComponents())
                .setIssueType(qaaTaskType).build();
        try {
            BasicIssue newlyCreatedIssue = client.getIssueClient().createIssue(newIssue).claim();
            client.getIssueClient().linkIssue(new LinkIssuesInput(newlyCreatedIssue.getKey(), issue.getKey(), "Cover"));
            LOGGER.info("Successfully created issue: " + newlyCreatedIssue.getKey());
            createdIssues.add(newlyCreatedIssue.getKey());
            if (linkCases) {
                List<Issue> testCases = getDevTestCasesFromNF(issue);
                for (Issue testCase : testCases) {
                    client.getIssueClient().linkIssue(new LinkIssuesInput(newlyCreatedIssue.getKey(), testCase.getKey(), "Test Execution"));
                }
            }
        } catch(ResponseTransformationException e) {
            throw new IstfException("User does not have rights to create new tickets", e);
        } catch(Exception e) {
            throw new IstfException("Exception during creation of QAA Task for " + issue.getKey(), e);
        }
    }

    private boolean isQaaTaskExistsByName(Issue issue) throws InterruptedException, ExecutionException {
        String jiraDestinationCheckQuery = "project = REN AND issuetype = 'QAA Task' and summary ~ '%1$s'";
        return client.getSearchClient().searchJql(String.format(jiraDestinationCheckQuery, issue.getKey()))
                .get().getIssues().iterator().hasNext();
    }

    private boolean isCoveredByAutomationTask(Issue issue) throws ExecutionException, InterruptedException {
        return hasLinkedQaaByLinkedType(issue, "is covered by");
    }


    private boolean hasLinkedQaaByLinkedType(Issue issue, String linkedType) throws ExecutionException, InterruptedException {
        List<IssueLink> coversIssues = getLinkedTaskByLinkedType(issue, linkedType);
        for (IssueLink link : coversIssues) {
            if ("QAA Task".equals(client.getIssueClient().getIssue(link.getTargetIssueKey()).get().getIssueType().getName())) {
                return true;
            }
        }
        return false;
    }

    private List<IssueLink> getLinkedTaskByLinkedType(Issue issue, String linkedType) {
        return Lists.newArrayList(Objects.requireNonNull(issue.getIssueLinks())).
                stream().filter(issueLink -> linkedType.equals(issueLink.getIssueLinkType().getDescription())).collect(Collectors.toList());
    }

    private boolean hasLinkedAutomationTask(Issue issue) throws ExecutionException, InterruptedException {
        return hasLinkedQaaByLinkedType(issue, "related to");
    }

    private boolean hasLinkedTcWithDevName(Issue issue) throws ExecutionException, InterruptedException {
        List<IssueLink> coversIssues = getLinkedTaskByLinkedType(issue, "is executed by");
        List<IssueLink> coversIssuesExecutes = getLinkedTaskByLinkedType(issue, "executes");
        List<IssueLink> coversIssuesRelated = getLinkedTaskByLinkedType(issue, "related to");
        coversIssues.addAll(coversIssuesExecutes);
        coversIssues.addAll(coversIssuesRelated);
        for (IssueLink link : coversIssues) {
            if (StringUtils.containsIgnoreCase(client.getIssueClient().getIssue(link.getTargetIssueKey()).get().getSummary(),"DEV") &&
                    client.getIssueClient().getIssue(link.getTargetIssueKey()).get().getIssueType().getName().contains("Test Case")) {
                return true;
            }
        }
        return false;
    }

    private List<Issue> getDevTestCasesFromNF(Issue issue) throws ExecutionException, InterruptedException {
        List<Issue> testCases = new ArrayList<>();
        List<IssueLink> coversIssues = getLinkedTaskByLinkedType(issue, "is executed by");
        List<IssueLink> coversIssuesExecutes = getLinkedTaskByLinkedType(issue, "executes");
        List<IssueLink> coversIssuesRelated = getLinkedTaskByLinkedType(issue, "related to");
        coversIssues.addAll(coversIssuesExecutes);
        coversIssues.addAll(coversIssuesRelated);
        for (IssueLink link : coversIssues) {
            if (StringUtils.containsIgnoreCase(client.getIssueClient().getIssue(link.getTargetIssueKey()).get().getSummary(),"DEV") &&
                    client.getIssueClient().getIssue(link.getTargetIssueKey()).get().getIssueType().getName().contains("Test Case")) {
                testCases.add(client.getIssueClient().getIssue(link.getTargetIssueKey()).get());
            }
        }
        return testCases;
    }
}
