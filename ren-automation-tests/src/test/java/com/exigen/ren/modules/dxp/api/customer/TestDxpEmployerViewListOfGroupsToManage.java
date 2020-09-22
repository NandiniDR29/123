package com.exigen.ren.modules.dxp.api.customer;

import com.exigen.ipb.eisa.utils.db.DBService;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.metadata.GeneralTabMetaData;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.RestBaseTest;
import com.exigen.ren.rest.ResponseContainer;
import com.exigen.ren.rest.dxp.model.EmployerGroupsModel;
import com.exigen.ren.utils.DbQueryConstants;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.CustomerConstants.CustomerRelationshipServiceRole.*;
import static com.exigen.ren.utils.components.Components.CUSTOMER_REST;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestDxpEmployerViewListOfGroupsToManage extends RestBaseTest implements CaseProfileContext, GroupAccidentMasterPolicyContext, GroupDentalMasterPolicyContext, TermLifeInsuranceMasterPolicyContext {
    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40878", component = CUSTOMER_REST)
    public void testDxpEmployerViewListOfGroupsToManage_1() {
        mainApp().open();
        createDefaultIndividualCustomer();
        String number_IC = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultNICWithIndividualRelationshipWithRoles(number_IC, ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName(), PORTAL_BENEFITS_ADMINISTRATOR.getUIName()));
        String NIC_1 = CustomerSummaryPage.labelCustomerNumber.getValue();
        String legalNameNIC_1 = CustomerSummaryPage.labelCustomerName.getValue();
        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        String masterPolicy_AC = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccountNumber_AC = DBService.get().getValue(String.format(DbQueryConstants.FIND_BILLING_ACCOUNT_NUMBER_BY_MASTER_POLICY_NUMBER, masterPolicy_AC)).get();

        createDefaultNICWithIndividualRelationshipWithRoles(number_IC, ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName(), PORTAL_BROKER_ADMINISTRATOR.getUIName()));
        String legalNameNIC_2 = CustomerSummaryPage.labelCustomerName.getValue();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();

        createDefaultNICWithIndividualRelationshipWithRoles(number_IC, ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName()));
        customerNonIndividual.update().start();
        String legalId = generalTab.getAssetList().getAsset(GeneralTabMetaData.EIN).getValue();
        Tab.cancelClickAndCloseDialog();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultTermLifeInsuranceMasterPolicy();

        LOGGER.info("Step 1");
        ResponseContainer<List<EmployerGroupsModel>> responseGroups = dxpRestService.getEmployerGroups(number_IC, NIC_1, null, null, null);
        assertThat(responseGroups.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> {
            softly.assertThat(responseGroups.getModel()).hasSize(1);
            EmployerGroupsModel employerGroupsModel = responseGroups.getModel().get(0);
            softly.assertThat(employerGroupsModel.getGroupCustomerNumber()).isEqualTo(NIC_1);
            softly.assertThat(employerGroupsModel.getLegalName()).isEqualTo(legalNameNIC_1);
            softly.assertThat(employerGroupsModel.getBillingAccountNumber()).isEqualTo(billingAccountNumber_AC);
            softly.assertThat(employerGroupsModel.getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicy_AC);
        });

        LOGGER.info("Step 2");
        ResponseContainer<List<EmployerGroupsModel>> responseGroups_2 = dxpRestService.getEmployerGroups(number_IC, null, legalNameNIC_2, null, null);
        assertThat(responseGroups_2.getResponse().getStatus()).isEqualTo(200);
        assertThat(responseGroups_2.getModel()).isEmpty();

        LOGGER.info("Step 3");
        ResponseContainer<List<EmployerGroupsModel>> responseGroups_3 = dxpRestService.getEmployerGroups(number_IC, null, null, legalId, null);
        assertThat(responseGroups_3.getResponse().getStatus()).isEqualTo(200);
        assertThat(responseGroups_3.getModel()).isEmpty();
    }

    @Test(groups = {DXP, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-40690", component = CUSTOMER_REST)
    public void testDxpEmployerViewListOfGroupsToManage_2() {
        mainApp().open();

        createDefaultIndividualCustomer();
        String number_IC = CustomerSummaryPage.labelCustomerNumber.getValue();
        createDefaultNICWithIndividualRelationshipWithRoles(number_IC, ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName(), PORTAL_BENEFITS_ADMINISTRATOR.getUIName()));
        String NIC_1 = CustomerSummaryPage.labelCustomerNumber.getValue();
        String legalNameNIC_1 = CustomerSummaryPage.labelCustomerName.getValue();
        customerNonIndividual.update().start();
        String legalId_1 = generalTab.getAssetList().getAsset(GeneralTabMetaData.EIN).getValue();
        Tab.cancelClickAndCloseDialog();

        createDefaultCaseProfile(groupAccidentMasterPolicy.getType());
        createDefaultGroupAccidentMasterPolicy();
        String masterPolicy_AC = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccountNumber_AC = DBService.get().getValue(String.format(DbQueryConstants.FIND_BILLING_ACCOUNT_NUMBER_BY_MASTER_POLICY_NUMBER, masterPolicy_AC)).get();

        createDefaultNICWithIndividualRelationshipWithRoles(number_IC, ImmutableList.of(ADMINISTRATIVE.getUIName(), BILLING.getUIName(), CLAIMS.getUIName(), PORTAL_BENEFITS_ADMINISTRATOR.getUIName()));
        String NIC_2 = CustomerSummaryPage.labelCustomerNumber.getValue();
        String legalNameNIC_2 = CustomerSummaryPage.labelCustomerName.getValue();
        createDefaultCaseProfile(termLifeInsuranceMasterPolicy.getType());
        createDefaultTermLifeInsuranceMasterPolicy();
        String masterPolicy_TL = PolicySummaryPage.labelPolicyNumber.getValue();
        String billingAccountNumber_TL = DBService.get().getValue(String.format(DbQueryConstants.FIND_BILLING_ACCOUNT_NUMBER_BY_MASTER_POLICY_NUMBER, masterPolicy_TL)).get();

        LOGGER.info("Step 1");
        ResponseContainer<List<EmployerGroupsModel>> responseGroups = dxpRestService.getEmployerGroups(number_IC, NIC_1, null, null, null);
        assertThat(responseGroups.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> {
            softly.assertThat(responseGroups.getModel()).hasSize(1);
            EmployerGroupsModel employerGroupsModel = responseGroups.getModel().get(0);
            softly.assertThat(employerGroupsModel.getGroupCustomerNumber()).isEqualTo(NIC_1);
            softly.assertThat(employerGroupsModel.getLegalName()).isEqualTo(legalNameNIC_1);
            softly.assertThat(employerGroupsModel.getBillingAccountNumber()).isEqualTo(billingAccountNumber_AC);
            softly.assertThat(employerGroupsModel.getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicy_AC);
        });

        LOGGER.info("Step 2");
        ResponseContainer<List<EmployerGroupsModel>> responseGroups_2 = dxpRestService.getEmployerGroups(number_IC, null, legalNameNIC_2, null, null);
        assertThat(responseGroups_2.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> {
            softly.assertThat(responseGroups_2.getModel()).hasSize(1);
            EmployerGroupsModel employerGroupsModel = responseGroups_2.getModel().get(0);
            softly.assertThat(employerGroupsModel.getGroupCustomerNumber()).isEqualTo(NIC_2);
            softly.assertThat(employerGroupsModel.getLegalName()).isEqualTo(legalNameNIC_2);
            softly.assertThat(employerGroupsModel.getBillingAccountNumber()).isEqualTo(billingAccountNumber_TL);
            softly.assertThat(employerGroupsModel.getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicy_TL);
        });

        LOGGER.info("Step 3");
        ResponseContainer<List<EmployerGroupsModel>> responseGroups_3 = dxpRestService.getEmployerGroups(number_IC, null, null, legalId_1, null);
        assertThat(responseGroups_3.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> {
            softly.assertThat(responseGroups_3.getModel()).hasSize(1);
            EmployerGroupsModel employerGroupsModel = responseGroups_3.getModel().get(0);
            softly.assertThat(employerGroupsModel.getGroupCustomerNumber()).isEqualTo(NIC_1);
            softly.assertThat(employerGroupsModel.getLegalName()).isEqualTo(legalNameNIC_1);
            softly.assertThat(employerGroupsModel.getBillingAccountNumber()).isEqualTo(billingAccountNumber_AC);
            softly.assertThat(employerGroupsModel.getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicy_AC);
        });

        LOGGER.info("Step 4");
        ResponseContainer<List<EmployerGroupsModel>> responseGroups_4 = dxpRestService.getEmployerGroups(number_IC, null, null, null, billingAccountNumber_TL);
        assertThat(responseGroups_4.getResponse().getStatus()).isEqualTo(200);
        assertSoftly(softly -> {
            softly.assertThat(responseGroups_4.getModel()).hasSize(1);
            EmployerGroupsModel employerGroupsModel = responseGroups_4.getModel().get(0);
            softly.assertThat(employerGroupsModel.getGroupCustomerNumber()).isEqualTo(NIC_2);
            softly.assertThat(employerGroupsModel.getLegalName()).isEqualTo(legalNameNIC_2);
            softly.assertThat(employerGroupsModel.getBillingAccountNumber()).isEqualTo(billingAccountNumber_TL);
            softly.assertThat(employerGroupsModel.getMasterPolicies().get(0).getPolicyNumber()).isEqualTo(masterPolicy_TL);
        });
    }
}
