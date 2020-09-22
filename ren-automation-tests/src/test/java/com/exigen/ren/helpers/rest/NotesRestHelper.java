/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.helpers.rest;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.ipb.eisa.ws.rest.RestClient;
import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.data.TestData;
import com.exigen.istf.exceptions.IstfException;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.enums.AdminConstants;
import com.exigen.ren.main.pages.summary.NotesAndAlertsSummaryPage;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.platform.notes.NotesRestService;
import com.exigen.ren.rest.platform.notes.RESTNoteType;
import com.exigen.ren.rest.platform.notes.model.NotesResponseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.exigen.istf.verification.CustomAssertions.assertThat;

public class NotesRestHelper extends RestHelper {

    private static NotesRestService notesRestClient = RESTServiceType.NOTES.get();

    private static final String NOTE_ID_KEY = "noteId";
    private static final String ENTITY_REF_KEY = "entityRefNo";

    public static void assertNotesResponse(TestData testData, User user, RestClient.HttpMethod method, String entityRef) {

        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY)
                .stream().map(td -> (td.containsKey("performerId") && td.getValue("performerId").equals("qa")) ? td.adjust("performerId", PropertyProvider.getProperty(TestProperties.APP_USER)) : td)
                .collect(Collectors.toList());
        int i = 0;
        for (TestData data : requests) {
            if (entityRef != null && data.getValue(ENTITY_REF_KEY) != null && data.getValue(ENTITY_REF_KEY).isEmpty()) {
                data = data.adjust(ENTITY_REF_KEY, entityRef).resolveLinks();
            }
            switch (method) {
                case GET: {
                    List<NotesResponseModel> actualResponse = notesRestClient.getNotesByTypeAndRefItem(
                            data.getValue("entityType"), data.getValue(ENTITY_REF_KEY),
                            new User(user.getLogin(), user.getPassword()));
                    List<NotesResponseModel> expectedResponse = new ArrayList<>();

                    if (responses.get(i).containsKey(RESPONSES_TD_KEY.toLowerCase())) {
                        if (!responses.get(i).getTestDataList(RESPONSES_TD_KEY.toLowerCase()).isEmpty()) {
                            responses.get(i).getTestDataList(RESPONSES_TD_KEY.toLowerCase()).forEach(td ->
                                    expectedResponse.add(new NotesResponseModel(td)));
                        }
                    } else {
                        expectedResponse.add(new NotesResponseModel(responses.get(i)));
                    }
                    assertThat(actualResponse)
                            .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                            .containsExactlyInAnyOrder(expectedResponse.toArray(new NotesResponseModel[expectedResponse.size()]));

                    break;
                }
                case POST: {
                    NotesResponseModel actualResponse = notesRestClient.postNotes(data,
                            new User(user.getLogin(), user.getPassword())).getModel();
                    NotesResponseModel expectedResponse = new NotesResponseModel(responses.get(i));

                    assertThat(actualResponse)
                            .as(String.format("Scenario [%1$s] failed", responses.get(i).getValue("logMessage")))
                            .isEqualTo(expectedResponse);
                    assertNotePresence(data, responses.get(i));
                    break;
                }
                case PUT: {
                    String noteId = data.getValue(ENTITY_REF_KEY);
                    NotesResponseModel actualResponse = notesRestClient.putNotesItem(noteId,
                            data.mask(ENTITY_REF_KEY).mask(NOTE_ID_KEY),
                            new User(user.getLogin(), user.getPassword())).getModel();
                    NotesResponseModel expectedResponse = new NotesResponseModel(responses.get(i));

                    assertThat(actualResponse)
                            .as("TestScenario [%s] FAILED. Please see log below.", responses.get(i).getValue("logMessage"))
                            .isEqualTo(expectedResponse);
                    break;
                }
                default:
                    break;
            }
            i++;
        }
    }

    public static NotesResponseModel notesPost(TestData data, User user) {
        return notesRestClient.postNotes(data, new User(user.getLogin(), user.getPassword())).getModel();
    }

    public static NotesResponseModel notesDelete(String noteId, User user){
        return  notesRestClient.deleteNotesItem(noteId, new User(user.getLogin(), user.getPassword()));
    }

    public static NotesResponseModel notesPut(TestData data, User user, String noteId) {
        return notesRestClient.putNotesItem(noteId,
                data.mask(ENTITY_REF_KEY).mask(NOTE_ID_KEY),
                new User(user.getLogin(), user.getPassword())).getModel();
    }

    public static void assertNotesGetResponseByNoteId(TestData testData, User user, String noteId) {
        List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
        List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
        int i = 0;
        for (TestData data : requests) {
            if (noteId != null && data.getValue(NOTE_ID_KEY) != null && data.getValue(NOTE_ID_KEY).isEmpty()) {
                data = data.adjust(NOTE_ID_KEY, noteId).resolveLinks();
            }
            NotesResponseModel actualResponse = notesRestClient.getNotesItem(data.getValue(NOTE_ID_KEY), user);
            NotesResponseModel expectedResponse = new NotesResponseModel(responses.get(i));

            assertThat(actualResponse)
                    .as("TestScenario [%s] FAILED. Please see log below.", responses.get(i).getValue("logMessage"))
                    .isEqualTo(expectedResponse);
            i++;
        }
    }

    public static String getNoteIdByUser(User user, RESTNoteType noteType, String entityRefNo) {
        List<NotesResponseModel> responseList =
                notesRestClient.getNotesByTypeAndRefItem(noteType.get(), entityRefNo);
        for (NotesResponseModel response : responseList) {
            if (response.getPerformerId().equalsIgnoreCase(user.getLogin())) {
                return response.getId(); // currently works only with on note per user. enhance to support multiple notes
            }
        }
        throw new IstfException(String.format("There are no Notes created by User[%1$s] for Quote[%2$s]", user.getLogin(), entityRefNo));
    }

    public static NotesRestService getNotesRestClient() {
        return notesRestClient;
    }

    private static void assertNotePresence(TestData requestTestData, TestData expectedTestData) {
        if (null == expectedTestData.getValue("presentOnUi")) {
            return;
        }
        NotesAndAlertsSummaryPage.open();
        assertThat(NotesAndAlertsSummaryPage.tableFilterResults.getRow(AdminConstants.AdminFilterResultsTable.TITLE, requestTestData.getValue("title")))
                .isPresent(Boolean.parseBoolean(expectedTestData.getValue("presentOnUi")));
        Tab.buttonBack.click();
    }

    public static void checkFilterNotes(TestData queryData, List<TestData> notesData) {
        List<NotesResponseModel> actualResponse = NotesRestHelper.getNotesRestClient().getNotesByTypeAndRefItem(queryData);
        if (notesData!= null) {
            assertThat(actualResponse.size()).isEqualTo(notesData.size());
            actualResponse.forEach(notesResponse -> {
                assertThat(notesData.stream().anyMatch(e->e.getValue("title").equals(notesResponse.getTitle()))).isTrue();
                notesData.stream().filter(td->td.getValue("title").equals(notesResponse.getTitle())).forEach(e->
                {
                    NotesResponseModel expectedResponse = new NotesResponseModel(e);
                    assertThat(notesResponse).isEqualTo(expectedResponse);
                });
            });
        } else {
            assertThat(actualResponse.size()).isEqualTo(0);
        }
    }
}
