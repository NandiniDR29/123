/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.rest;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.istf.data.TestData;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.platform.bpm.BPMRestService;
import com.exigen.ren.rest.platform.bpm.model.agencytransfer.AgencyTransferResponseModel;
import com.exigen.ren.rest.platform.bpm.model.tasks.AssignmentModel;
import com.exigen.ren.rest.platform.bpm.model.tasks.TaskAssignmentResponseModel;
import com.exigen.ren.rest.platform.bpm.model.tasks.TasksResponseModel;
import com.exigen.ren.rest.platform.notes.model.NotesResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomAssertions.assertThat;

public class BPMRestHelper extends RestHelper {

    private static BPMRestService bpmRestClient = RESTServiceType.BPM.get();
    public static final String TASK_ID_KEY = "taskId";
    public static final String NOTE_ID_KEY = "noteId";
    protected static final Logger LOGGER = LoggerFactory.getLogger(BPMRestHelper.class);

    public static void assertTasksResponse(TestData testData, User user, Map<String, String> taskIdMapper) {
        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            List<TasksResponseModel> expectedResponse = responses.get(i).getTestDataList(RESPONSES_TD_KEY.toLowerCase())
                    .stream().map(td -> new TasksResponseModel(td, taskIdMapper)).collect(Collectors.toList());
            List<TasksResponseModel> actualResponse = bpmRestClient.getTasks(data, user);
            assertThat(actualResponse)
                    .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                    .containsAll(expectedResponse);
            i++;
        }
    }

    public static TasksResponseModel taskCreate(TestData testData, User user) {
        return bpmRestClient.postTasks(testData, user).get(0);
    }

    public static void assertTasksCreateAssignmentResponse(TestData testData, User user, String referenceId) {

        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            AssignmentModel actualResponse = bpmRestClient.postTasks(data.adjust("referenceId", referenceId), user)
                    .get(0).getAssignment();
            AssignmentModel expectedResponse = new AssignmentModel(responses.get(i));
            assertThat(actualResponse)
                    .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                    .isEqualTo(expectedResponse);
            i++;
        }
    }

    public static void assertTasksCreateErrorResponse(TestData testData, User user, String referenceId) {

        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            List<TasksResponseModel> actualResponse = bpmRestClient.postTasks(data.adjust("referenceId", referenceId),
                    user);
            TasksResponseModel expectedResponse = new TasksResponseModel(responses.get(i));

            assertThat(actualResponse.get(0))
                    .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                    .isEqualTo(expectedResponse);
            i++;
        }
    }

    public static void assertTasksAssigneeSuccessfulPutResponse(TestData testData, User restUser, List<TasksResponseModel> taskIds) {


        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            String taskId = taskIds.get(Integer.parseInt(data.getValue(TASK_ID_KEY)) - 1).getId();
            TaskAssignmentResponseModel response = bpmRestClient.putTaskAssignmentById(taskId, new AssignmentModel(data), restUser);
            assertThat(response.getStatus())
                    .as(String.format("Unsuccessful assignee for [%1$s]", responses.get(i).getValue("logMessage")))
                    .isEqualToIgnoringCase("Success");
            AssignmentModel actualResponse = bpmRestClient.getTaskAssignmentById(taskId, restUser);
            AssignmentModel expectedResponse = new AssignmentModel(responses.get(i));
            assertThat(actualResponse)
                    .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                    .isEqualTo(expectedResponse);
            i++;
        }
    }

    public static void assertTasksAssigneeErrorPutResponse(TestData testData, User user, List<TasksResponseModel> taskIds) {
        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            String taskId = taskIds.get(Integer.parseInt(data.getValue(TASK_ID_KEY)) - 1).getId();
            TaskAssignmentResponseModel actualResponse = bpmRestClient.putTaskAssignmentById(taskId, new AssignmentModel(data),
                    user);
            TestData responseData = populateMsgWithRealTaskId(responses.get(i), taskId);
            TasksResponseModel expectedResponse = new TasksResponseModel(responseData);
            assertThat(actualResponse)
                    .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                    .isEqualTo(expectedResponse);
            i++;
        }
    }

    public static void assertTasksUpdateResponse(TestData testData, User user, Map<String, String> taskIdMapper) {
        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            String taskId = data.getValue(TASK_ID_KEY);
            if (taskId.startsWith("task")) {
                taskId = taskIdMapper.get(taskId);
            }

            List<TasksResponseModel> expectedResponse = new ArrayList<>();
            TestData responseData = populateMsgWithRealTaskId(responses.get(i), taskId);
            if (responseData.getTestDataList(RESPONSES_TD_KEY.toLowerCase()).isEmpty()) {
                expectedResponse = Collections.singletonList(new TasksResponseModel(responseData));
            } else {
                for (TestData td : responseData.getTestDataList(RESPONSES_TD_KEY.toLowerCase())) {
                    expectedResponse.add(new TasksResponseModel(td, taskIdMapper).includeAdditionalFields());
                }
            }
            data.adjust(TASK_ID_KEY, "null");
            List<TasksResponseModel> actualResponse = bpmRestClient.putTask(taskId, data,
                    user);
            actualResponse.forEach(TasksResponseModel::includeAdditionalFields);
            assertThat(actualResponse).as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                    .isEqualTo(expectedResponse);
            i++;
        }
    }

    public static TasksResponseModel taskUpdate(TestData testData, User user, String taskId) {
        return bpmRestClient.putTask(taskId, testData, user).get(0);
    }

    public static void assertTasksAgencyTransfer(TestData testData, User user, String referenceId) {
        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            if (data.containsKey("entityRefNo") && data.getValue("entityRefNo").isEmpty()) {
                data.adjust("entityRefNo", referenceId);
            }
            AgencyTransferResponseModel actualResponse = bpmRestClient.postAgencyTransfer(data,
                    new User(user.getLogin(), user.getPassword()));
            AgencyTransferResponseModel expectedResponse = new AgencyTransferResponseModel(responses.get(i));
            assertThat(actualResponse)
                    .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                    .isEqualTo(expectedResponse);
            i++;
        }
    }

    public static void assertTasksNotesResponse(TestData testData, User user, Map<String, String> mapper) {
        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            List<NotesResponseModel> expectedResponse;
            if (responses.get(i).getTestDataList(RESPONSES_TD_KEY.toLowerCase()).isEmpty()) {
                expectedResponse = Collections.singletonList(new NotesResponseModel(responses.get(i)));
            } else {
                expectedResponse = new ArrayList<>();
                for (TestData td : responses.get(i).getTestDataList(RESPONSES_TD_KEY.toLowerCase())) {
                    expectedResponse.add(new NotesResponseModel(td));
                }
            }
            String taskId = data.getValue(TASK_ID_KEY).startsWith("task") ? mapper.get(data.getValue(TASK_ID_KEY)) : data.getValue(TASK_ID_KEY);
            List<NotesResponseModel> actualResponse = bpmRestClient.getTaskNotesByTaskId(taskId,
                    user);
            assertThat(actualResponse)
                    .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                    .isEqualTo(expectedResponse);
            i++;
        }
    }

    public static void assertCreateTasksNoteResponse(TestData testData, User user, String taskId) {
        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            NotesResponseModel expectedResponse = new NotesResponseModel(responses.get(i));
            NotesResponseModel actualResponse = bpmRestClient.postTaskNotesByTaskId(taskId, data, user).getModel();
            assertThat(actualResponse)
                    .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                    .isEqualTo(expectedResponse);
            i++;
        }
    }

    public static void assertUpdateTasksNoteResponse(TestData testData, User user, String taskId, String noteId) {
        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            NotesResponseModel expectedResponse = new NotesResponseModel(responses.get(i));
            NotesResponseModel actualResponse = bpmRestClient.putTaskNotesByTaskId(
                    data.adjust(TASK_ID_KEY, taskId).adjust(NOTE_ID_KEY, noteId), user).getModel();
            assertThat(actualResponse)
                    .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                    .isEqualTo(expectedResponse);
            i++;
        }
    }

    public static BPMRestService getBpmRestClient() {
        return bpmRestClient;
    }

    private static TestData populateMsgWithRealTaskId(TestData testData, String taskId) {
        String matcher = "TASK_ID_PUT_HERE";
        String message;
        if (testData.containsKey("message") && testData.getValue("message").contains(matcher)) {
            message = testData.getValue("message").replace(matcher, taskId);
            testData.adjust("message", message);
        }
        if (!testData.getTestDataList("errors").isEmpty()) {
            List<TestData> updatedData = new ArrayList<>();
            for (TestData data : testData.getTestDataList("errors")) {
                if (data.containsKey("message") && data.getValue("message").contains(matcher)) {
                    updatedData.add(data.adjust("message", data.getValue("message").replace(matcher, taskId)).resolveLinks());
                } else {
                    updatedData.add(data);
                }
            }
            testData.adjust("errors", updatedData);
        }
        return testData.resolveLinks();
    }
}
