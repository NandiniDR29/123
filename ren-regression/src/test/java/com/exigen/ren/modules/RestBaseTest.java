/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.data.TestData;
import com.exigen.ren.admin.modules.agencyvendor.agency.AgencyContext;
import com.exigen.ren.admin.modules.agencyvendor.brand.BrandContext;
import com.exigen.ren.admin.modules.security.profile.ProfileContext;
import com.exigen.ren.admin.modules.security.role.RoleContext;
import com.exigen.ren.admin.modules.workflow.processmanagement.TaskContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.mywork.MyWorkContext;
import com.exigen.ren.rest.RESTServiceType;
import com.exigen.ren.rest.billing.BillingRestService;
import com.exigen.ren.rest.customer.CustomerRestService;
import com.exigen.ren.rest.customer.RenCustomerRestService;
import com.exigen.ren.rest.dxp.DxpRestService;
import com.exigen.ren.rest.integration.IntegrationRestService;
import com.exigen.ren.rest.partysearch.PartySearchRestService;
import com.exigen.ren.rest.platform.bpm.AgencyVendorRestService;
import com.exigen.ren.rest.platform.bpm.BPMDevRestService;
import com.exigen.ren.rest.platform.bpm.BPMRestService;
import com.exigen.ren.rest.platform.efolder.EFolderRestService;
import com.exigen.ren.rest.platform.providers.ProviderRestService;
import com.exigen.ren.rest.policyBenefits.enrollmentServices.EnrollmentRestService;
import com.exigen.ren.rest.rating.RatingReportRestService;
import com.exigen.ren.rest.security.SecurityRestService;

public class RestBaseTest extends BaseTest implements CustomerContext, ProfileContext, TaskContext, RoleContext, AgencyContext, BrandContext, MyWorkContext {

    private RESTServiceType notesType = RESTServiceType.NOTES;
    private RESTServiceType bpmType = RESTServiceType.BPM;
    private RESTServiceType partySearchType = RESTServiceType.PARTY_SEARCH;
    private RESTServiceType customersType = RESTServiceType.CUSTOMERS;
    private RESTServiceType billingType = RESTServiceType.BILLING;
    private RESTServiceType providerType = RESTServiceType.PROVIDER;
    private RESTServiceType productDecisionTableType = RESTServiceType.PRODUCT_DECISION_TABLE;

    protected static final String CUSTOMER_RS_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss'Z'";
    private static final String CHECK_TASK_PRESENCE = "select count(ID_) from ACT_RE_PROCDEF where NAME_ like '%1$s'";

    protected static final String SPECIAL_SUMBOLS = "!@#$%^&*()?\"\'/\\:;.|-+=_";

    protected static final String SELECT_ADDRESS_ID = "select Mailing_Address_ID from ThirdParty where code='%s'";
    protected static final String SELECT_ADDRESS = "select addressLine1 from ThirdPartyAddress where id='%s'";
    protected static final String GET_OID_QUERY = "select oid from ServiceFacility where id = '%s'";
    protected static final String GET_FACILITY_QUERY = "select * from ServiceFacility where id = '%s'";
    protected static final String GET_UNIT_NUMBER_BY_ID_QUERY = "SELECT address_unitNumber FROM VendorProfile WHERE id='%s'";
    protected static final String UPDATE_NOTES_DATE = "Update ACTIVITY set ENDED='2018-01-01', STARTED='2018-01-01', updatedOn='2018-01-01' where primaryEntityId = '%s' and title = '%s'";
    protected static final String GET_NOTES_ID = "SELECT id,title FROM ACTIVITY where DTYPE = 'UserNote' and STATUS = 'COMMITTED' and primaryEntityId ='%s'";
    protected static final String UPDATE_NOTES_DATE_MSSQL = "Update ACTIVITY set ENDED='2018-01-01', STARTED='2018-01-01', updatedOn='2018-01-01' where primaryEntityId = '%s' and title = '%s'";
    protected static final String UPDATE_NOTES_DATE_ORCL = "Update ACTIVITY set ENDED=TO_DATE('2018-01-01', 'YYYY-MM-DD'), STARTED=TO_DATE('2018-01-01', 'YYYY-MM-DD'), updatedOn=TO_DATE('2018-01-01', 'YYYY-MM-DD') where primaryEntityId = '%s' and title = '%s'";
    protected static final String UPDATE_NOTES_DATE_POSTGRE = "Update ACTIVITY set ENDED='2018-01-01', STARTED='2018-01-01', updatedOn='2018-01-01' where primaryEntityId = '%s' and title = '%s'";
    protected static final String GET_ROLE_QUERY = "SELECT id  FROM S_AUTHORITY WHERE name = '%s' and DTYPE='ROLE'";
    protected static final String ADD_ROLE = "INSERT INTO S_AUTHORITY(domain,name,DTYPE)VALUES('corporate','%s','ROLE')";
    protected static final String ADD_PRIVILEGES = "INSERT INTO S_ROLE_PRIVILEGES (role_id,priv_id) SELECT (SELECT id as role_id FROM S_AUTHORITY WHERE name = '%s' and DTYPE='ROLE') as role_id, priv_id " +
            "FROM S_ROLE_PRIVILEGES WHERE role_id=(SELECT id FROM S_AUTHORITY WHERE name = 'QA All') and priv_id in(SELECT id FROM S_AUTHORITY where name %s and DTYPE='PRIV')";

    protected static final String TEST_DATA_KEY = "TestData";

    protected TestData tdNotesRest = testDataManager.rest.get(notesType).getTestData("NotesRestData", TEST_DATA_KEY);
    protected TestData tdNotesBOBRest = testDataManager.rest.get(notesType).getTestData("NotesBOBRestData", TEST_DATA_KEY);
    protected TestData tdTasksRest = testDataManager.rest.get(bpmType).getTestData("TasksRestData", TEST_DATA_KEY);
    protected TestData tdTasksAgencyTransferRest = testDataManager.rest.get(bpmType).getTestData("TasksRestAgencyTransferData", TEST_DATA_KEY);
    protected TestData tdCustomersRest = testDataManager.rest.get(customersType).getTestData("CustomerRestData", TEST_DATA_KEY);
    protected TestData tdMyWorkRest = testDataManager.rest.get(bpmType).getTestData("MyWorkUsersAccessRestData", TEST_DATA_KEY);
    protected TestData tdBillingRest = testDataManager.rest.get(billingType).getTestData("BillingRestData", TEST_DATA_KEY);
    protected TestData tdPartySearchRest = testDataManager.rest.get(partySearchType).getTestData("PartySearchRestData", TEST_DATA_KEY);
    protected TestData tdPartyRest = testDataManager.rest.get(partySearchType);
    protected TestData tdCustomerIndividual = customerIndividual.getDefaultTestData();
    protected TestData tdCustomerNonIndividual = customerNonIndividual.getDefaultTestData();
    protected TestData tdProvider = testDataManager.rest.get(providerType);
    protected TestData tdProductDecisionTable = testDataManager.rest.get(productDecisionTableType);
    protected TestData tdTaskDefinition = task.defaultTestData();
    protected TestData tdRoleCorporate = roleCorporate.defaultTestData();
    protected TestData tdProfileCorporate = profileCorporate.defaultTestData();
    protected TestData tdAgency = agency.defaultTestData();
    protected TestData tdBrand = brand.defaultTestData();
    protected TestData tdMyWork = myWork.defaultTestData();

    protected SecurityRestService securityServiceAPI = new SecurityRestService();
    protected CustomerRestService customerRestClient = customersType.get();
    protected BillingRestService billingRestService = billingType.get();
    protected BPMDevRestService bpmDevRestClient = new BPMDevRestService();
    protected BPMRestService bpmRestClient = new BPMRestService();
    protected AgencyVendorRestService restAgency = new AgencyVendorRestService();
    protected ProviderRestService service = new ProviderRestService();
    protected EFolderRestService eFolderRestService = new EFolderRestService();
    protected PartySearchRestService partySearchRSClient = RESTServiceType.PARTY_SEARCH.get();
    protected DxpRestService dxpRestService = RESTServiceType.DXP.get();
    protected RatingReportRestService ratingReportRestService = RESTServiceType.RATING_REPORT.get();
    protected EnrollmentRestService enrollmentRestService = RESTServiceType.ENROLLMENT_SERVICES_REST_SERVICE.get();
    protected RenCustomerRestService renCustomerRestService = RESTServiceType.REN_CUSTOMERS.get();
    protected IntegrationRestService integrationRestService =  RESTServiceType.INTEGRATION.get();

    protected void addRole(String roleName, String privileges) {
        if (DBService.get().getRows(String.format(GET_ROLE_QUERY, roleName)).size() == 0) {
            DBService.get().executeUpdate(String.format(ADD_ROLE, roleName));
            DBService.get().executeUpdate(String.format(ADD_PRIVILEGES, roleName, privileges));
        }
    }

    protected enum TaskControlsState {
        ABSENT,
        DISABLED,
        PRESENT,
        ENABLED,
        CREATE_TASK_ENABLED_TASKS_DISABLED,
        ONLY_CREATE_TASK
    }

    protected enum PartOfDay {
        MORNING,
        NIGHT,
        EVENING,
        AFTERNOON
    }

    protected enum DayOfWeek {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    protected enum ConsentStatus {
        GRANTED,
        DENIED,
        REQUESTED,
        NOT_REQUESTED
    }

    protected enum ContactMethodType {
        PHONE
    }
}
