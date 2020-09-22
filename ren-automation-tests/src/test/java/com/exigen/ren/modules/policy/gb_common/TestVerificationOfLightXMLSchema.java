package com.exigen.ren.modules.policy.gb_common;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.policyBenefits.enrollmentServices.models.EnrollmentCensusResponseModel;
import com.exigen.ren.rest.policyBenefits.enrollmentServices.models.enrollmentCensusRecordsModels.EnrollmentCensusIncorrectRequest;
import com.exigen.ren.rest.policyBenefits.enrollmentServices.models.enrollmentCensusRecordsModels.EnrollmentCensusRequest;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ProductConstants.PolicyStatus.POLICY_CANCELLED;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestVerificationOfLightXMLSchema extends RestBaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41316"}, component = POLICY_GROUPBENEFITS)
    public void testVerificationOfLightXmlschemaForActivePolicy() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC1: Step 1");
        ResponseContainer<EnrollmentCensusResponseModel> enrollmentCensusResponse = enrollmentRestService.postEnrollmentPolicyNumberDeploy(policyNumber,
                tdSpecific().getTestData("TestData_Incorrect_Model").adjust(TestData.makeKeyPath("fileMetaData", "masterPolicyNumber"), policyNumber),
                EnrollmentCensusIncorrectRequest.class);
        checkStatusErrorCodeAndMessage(enrollmentCensusResponse, "422",
                "cvc-complex-type.2.4.a: Invalid content was found starting with element 'attributeTest'. One of '{participantCount}' is expected.");

        LOGGER.info("TC2: Step 1");
        ResponseContainer<EnrollmentCensusResponseModel> enrollmentCensusResponse_2 = enrollmentRestService.postEnrollmentPolicyNumberDeploy(policyNumber,
                tdSpecific().getTestData("TestData_Correct")
                        .adjust(TestData.makeKeyPath("fileMetaData", "masterPolicyNumber"), policyNumber)
                        .adjust(TestData.makeKeyPath("fileMetaData", "timestamp"), "stringFormatForTimeStamp"),
                EnrollmentCensusRequest.class);
        checkStatusErrorCodeAndMessage(enrollmentCensusResponse_2, "422",
                "cvc-datatype-valid.1.2.1: 'stringFormatForTimeStamp' is not a valid value for 'dateTime'.");

        LOGGER.info("TC2: Step 3");
        ResponseContainer<EnrollmentCensusResponseModel> enrollmentCensusResponse_3 = enrollmentRestService.postEnrollmentPolicyNumberDeploy(policyNumber,
                tdSpecific().getTestData("TestData_Correct")
                        .adjust(TestData.makeKeyPath("fileMetaData", "masterPolicyNumber"), policyNumber)
                        .adjust(TestData.makeKeyPath("fileMetaData", "enrollmentSource"), "enrollmentSource_INCORRECT_value"),
                EnrollmentCensusRequest.class);
        checkStatusErrorCodeAndMessage(enrollmentCensusResponse_3, "422",
                "cvc-enumeration-valid: Value 'enrollmentSource_INCORRECT_value' is not facet-valid with respect to enumeration '[DXP, GROUP]'. It must be a value from the enumeration.");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41316"}, component = POLICY_GROUPBENEFITS)
    public void testVerificationOfLightXmlschemaForCancelledPolicy() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        groupDentalMasterPolicy.cancel().perform(groupDentalMasterPolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(POLICY_CANCELLED);
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("TC4: Step 1");
        ResponseContainer<EnrollmentCensusResponseModel> enrollmentCensusResponse = enrollmentRestService.postEnrollmentPolicyNumberDeploy(policyNumber,
                tdSpecific().getTestData("TestData_Correct").adjust(TestData.makeKeyPath("fileMetaData", "masterPolicyNumber"), policyNumber),
                EnrollmentCensusRequest.class);
        checkStatusErrorCodeAndMessage(enrollmentCensusResponse, "ENR001",
                "Enrollment can be initiated only on Master Policy in status Active or Policy Pending.");
    }

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-41316"}, component = POLICY_GROUPBENEFITS)
    public void testVerificationOfLightXmlschemaForNonExistedPolicy() {
        String nonExistedPolicyNumber = "MP9999999999";

        LOGGER.info("TC5: Step 1");
        ResponseContainer<EnrollmentCensusResponseModel> enrollmentCensusResponse = enrollmentRestService.postEnrollmentPolicyNumberDeploy(nonExistedPolicyNumber,
                tdSpecific().getTestData("TestData_Correct").adjust(TestData.makeKeyPath("fileMetaData", "masterPolicyNumber"), nonExistedPolicyNumber),
                EnrollmentCensusRequest.class);
        checkStatusErrorCodeAndMessage(enrollmentCensusResponse, "ENR002", "Master Policy not found");
    }

    public void checkStatusErrorCodeAndMessage(ResponseContainer<EnrollmentCensusResponseModel> enrollmentCensusResponse, String errorCode, String errorMessage) {
        assertThat(enrollmentCensusResponse.getResponse().getStatus()).isEqualTo(422);
        assertThat(enrollmentCensusResponse.getError().getErrorCode()).isEqualTo(errorCode);
        assertThat(enrollmentCensusResponse.getError().getMessage()).isEqualTo(errorMessage);
    }
}