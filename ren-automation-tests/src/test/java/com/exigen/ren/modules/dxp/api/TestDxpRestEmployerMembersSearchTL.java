/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package com.exigen.ren.modules.dxp.api;

import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.policy.common.tabs.common.CancellationActionTab;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.InsuredTabMetaData;
import com.exigen.ren.main.modules.policy.gb_tl.certificate.TermLifeInsuranceCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.certificate.EmployerGroupsGroupMemberModel;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.modules.policy.common.metadata.common.CancellationActionTabMetaData.CANCEL_DATE;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.InsuredTabMetaData.SEARCH_CUSTOMER;
import static com.exigen.ren.main.modules.policy.gb_tl.certificate.metadata.CertificatePolicyTabMetaData.EFFECTIVE_DATE;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpRestEmployerMembersSearchTL extends RestBaseTest implements CaseProfileContext, TermLifeInsuranceMasterPolicyContext, TermLifeInsuranceCertificatePolicyContext {

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36835", component = CUSTOMER_REST)
    public void testDxpRestEmployerMembersSearch() {

        mainApp().open();
        String customerNumberAuthorize = createDefaultNICWithIndRelationshipDefaultRoles();
        String customerNumNIC1 = CustomerSummaryPage.labelCustomerNumber.getValue();

        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        LOGGER.info("Create master policy with status Active");
        createDefaultTermLifeInsuranceMasterPolicy();
        String policyNumberMP2 = PolicySummaryPage.labelPolicyNumber.getValue();

        createDefaultTermLifeInsuranceCertificatePolicy();

        String policyNumberCP2 = PolicySummaryPage.labelPolicyNumber.getValue();
        navigateToCustomer();
        String customerNumIC2 = CustomerSummaryPage.labelCustomerNumber.getValue();
        String lastNameIC2 = CustomerSummaryPage.labelCustomerName.getValue().split(" ")[1];
        String firstNameIC2 = CustomerSummaryPage.labelCustomerName.getValue().split(" ")[0];
        MainPage.QuickSearch.search(policyNumberCP2);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        MainPage.QuickSearch.search(policyNumberMP2);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

        LOGGER.info("Create master policy with status Pending");
        String effectiveDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().plusMonths(2).format(MM_DD_YYYY);

        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicyData()
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE.getLabel()), effectiveDate));
        String policyNumberMP3 = PolicySummaryPage.labelPolicyNumber.getValue();

        termLifeInsuranceCertificatePolicy.createPolicy(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER,  "TestDataWithoutNewCustomer")
                .adjust(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.ISSUE, TestDataKey.DEFAULT_TEST_DATA_KEY))
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), EFFECTIVE_DATE.getLabel()), effectiveDate)
                .adjust(TestData.makeKeyPath(insuredTab.getMetaKey(), SEARCH_CUSTOMER.getLabel(),
                        InsuredTabMetaData.SearchCustomerSingleSelector.FIRST_NAME.getLabel()), firstNameIC2));

        String policyNumberCP3 = PolicySummaryPage.labelPolicyNumber.getValue();
        navigateToCustomer();
        MainPage.QuickSearch.search(policyNumberCP3);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_PENDING);
        MainPage.QuickSearch.search(policyNumberMP3);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_PENDING);

        LOGGER.info("---=={Step 1}==---");
        assertSoftly(softly -> {
            ResponseContainer<List<EmployerGroupsGroupMemberModel>> responseGroups = dxpRestService.getEmployerGroupsGroupMember(customerNumberAuthorize, customerNumNIC1,
                    firstNameIC2, null, null, null, null, null, null, null, null);
            softly.assertThat(responseGroups.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(responseGroups.getModel().size()).isEqualTo(1);
            EmployerGroupsGroupMemberModel profile = responseGroups.getModel().get(0);
            softly.assertThat(profile.getCustomerNumber()).isEqualTo(customerNumIC2);
            softly.assertThat(profile.getCertificatesCount().getTotal()).isEqualTo("2");
            softly.assertThat(profile.getCertificatesCount().getActive()).isEqualTo("1");
            softly.assertThat(profile.getCertificatesCount().getPending()).isEqualTo("1");
        });

        LOGGER.info("---=={Step 2}==---");
        assertSoftly(softly -> {
            ResponseContainer<List<EmployerGroupsGroupMemberModel>> responseGroups = dxpRestService.getEmployerGroupsGroupMember(customerNumberAuthorize, customerNumNIC1,
                    null, lastNameIC2, null, null, null, null, null, null, null);
            softly.assertThat(responseGroups.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(responseGroups.getModel().size()).isEqualTo(1);
            EmployerGroupsGroupMemberModel profile = responseGroups.getModel().get(0);
            softly.assertThat(profile.getCustomerNumber()).isEqualTo(customerNumIC2);
            softly.assertThat(profile.getCertificatesCount().getTotal()).isEqualTo("2");
            softly.assertThat(profile.getCertificatesCount().getActive()).isEqualTo("1");
            softly.assertThat(profile.getCertificatesCount().getPending()).isEqualTo("1");
        });

        LOGGER.info("---=={Step 3}==---");
        assertSoftly(softly -> {
            ResponseContainer<List<EmployerGroupsGroupMemberModel>> responseGroups = dxpRestService.getEmployerGroupsGroupMember(customerNumberAuthorize, customerNumNIC1,
                    null, null, customerNumIC2, null, null, null, null, null, null);
            softly.assertThat(responseGroups.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(responseGroups.getModel().size()).isEqualTo(1);
            EmployerGroupsGroupMemberModel profile = responseGroups.getModel().get(0);
            softly.assertThat(profile.getCustomerNumber()).isEqualTo(customerNumIC2);
            softly.assertThat(profile.getCertificatesCount().getTotal()).isEqualTo("2");
            softly.assertThat(profile.getCertificatesCount().getActive()).isEqualTo("1");
            softly.assertThat(profile.getCertificatesCount().getPending()).isEqualTo("1");
        });

        LOGGER.info("---=={Step 4}==---");
        MainPage.QuickSearch.search(policyNumberCP2);
        termLifeInsuranceCertificatePolicy.cancel().perform(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        MainPage.QuickSearch.search(policyNumberCP3);
        termLifeInsuranceCertificatePolicy.cancel().perform(termLifeInsuranceCertificatePolicy.getDefaultTestData(TestDataKey.CANCELLATION, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(CancellationActionTab.class.getSimpleName(), CANCEL_DATE.getLabel()), effectiveDate));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);

        LOGGER.info("---=={Step 5}==---");
        assertSoftly(softly -> {
            ResponseContainer<List<EmployerGroupsGroupMemberModel>> responseGroups = dxpRestService.getEmployerGroupsGroupMember(customerNumberAuthorize, customerNumNIC1,
                    null, null, null, null, null, null, null, null, null);
            softly.assertThat(responseGroups.getResponse().getStatus()).isEqualTo(200);
            softly.assertThat(responseGroups.getModel().size()).isEqualTo(1);
            EmployerGroupsGroupMemberModel profile = responseGroups.getModel().get(0);
            softly.assertThat(profile.getCustomerNumber()).isEqualTo(customerNumIC2);
            softly.assertThat(profile.getCertificatesCount().getTotal()).isEqualTo("2");
            softly.assertThat(profile.getCertificatesCount().getCancelled()).isEqualTo("1");
            softly.assertThat(profile.getCertificatesCount().getPendingCancellation()).isEqualTo("1");
            softly.assertThat(profile.getCertificatesCount().getActive()).isEqualTo("0");
            softly.assertThat(profile.getCertificatesCount().getPending()).isEqualTo("0");
        });
    }
}