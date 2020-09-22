package com.exigen.ren.modules.docgen.general;

import com.exigen.istf.data.TestData;
import com.exigen.istf.data.impl.SimpleDataProvider;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.module.efolder.Efolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.modules.billing.account.metadata.BillingAccountTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.BillingAccountTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.FileIntakeManagementTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_ac.masterpolicy.GroupAccidentMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_std.masterpolicy.ShortTermDisabilityMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_tl.masterpolicy.TermLifeInsuranceMasterPolicyContext;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.modules.docgen.ValidationXMLBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.INVOICE;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UPLOAD_FILE;
import static com.exigen.ren.main.modules.caseprofile.metadata.FileIntakeManagementTabMetaData.UploadFileDialog.FILE_UPLOAD;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestSupportDxpRequirementsForInvoiceGeneration extends ValidationXMLBaseTest implements CustomerContext, CaseProfileContext, ShortTermDisabilityMasterPolicyContext,
        LongTermDisabilityMasterPolicyContext, TermLifeInsuranceMasterPolicyContext, GroupAccidentMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_STD, GB_TL, GB_AC, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-32283"}, component = POLICY_GROUPBENEFITS)
    public void testSupportDxpRequirementsForInvoiceGenerationSelfBill() {
        LOGGER.info("REN-32283 Precondition");
        String finalBill = "%1$s-Billing Statement-%2$s-%3$s-%4$s-\\d{2}-\\d{2}-\\d{2}-\\d{3}.pdf";
        String draftBill = "%1$s-Billing Statement-%2$s-%3$s-\\d{2}-\\d{2}-\\d{2}-\\d{3}.pdf";
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(shortTermDisabilityMasterPolicy.getType(), longTermDisabilityMasterPolicy.getType(),
                termLifeInsuranceMasterPolicy.getType(), groupAccidentMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(FileIntakeManagementTab.class.getSimpleName() + "[0]", UPLOAD_FILE.getLabel(), FILE_UPLOAD.getLabel()),
                        "$<file:REN_Rating_Census_File_All.xlsx>"));

        shortTermDisabilityMasterPolicy.createPolicy(getDefaultSTDMasterPolicySelfAdminData());

        List<TestData> tdBillingAccount = getDefaultSTDMasterPolicySelfAdminData()
                .mask(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", BillingAccountTabMetaData.CREATE_NEW_BILLING_ACCOUNT.getLabel()))
                .adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", BillingAccountTabMetaData.SELECT_ACTION.getLabel()), "Bill Under Account")
                .adjust(TestData.makeKeyPath(BillingAccountTab.class.getSimpleName() + "[0]", BillingAccountTabMetaData.BILL_UNDER_ACCOUNT.getLabel()), "index=1")
                .getTestDataList(BillingAccountTab.class.getSimpleName());

        longTermDisabilityMasterPolicy.createPolicy(getDefaultLTDMasterPolicySelfAdminData().adjust(BillingAccountTab.class.getSimpleName(), tdBillingAccount));

        termLifeInsuranceMasterPolicy.createPolicy(getDefaultTLMasterPolicySelfAdminData().adjust(BillingAccountTab.class.getSimpleName(), tdBillingAccount));

        groupAccidentMasterPolicy.createPolicy(getDefaultACMasterPolicySelfAdminData().adjust(BillingAccountTab.class.getSimpleName(), tdBillingAccount));

        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateDraftBill().perform(new SimpleDataProvider());
        billingAccount.generateFutureStatement().perform();

        String dueDate = LocalDate.parse(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY).format(DateTimeUtilsHelper.YYYY_MM_DD);
        String issueDate = DateTimeUtils.getCurrentDateTime().format(DateTimeUtilsHelper.YYYY_MM_DD);
        String invoice = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(INVOICE).getValue();

        assertSoftly(softly -> {
            LOGGER.info("REN-32283 STEP#1");
            softly.assertThat(Efolder.getFileName("Invoices and Bills", "Billing Statement", 1))
                    .matches(String.format(finalBill, customerNumber, invoice, dueDate, issueDate));
            LOGGER.info("REN-32283 STEP#2");
            softly.assertThat(Efolder.getFileName("Invoices and Bills", "Billing Statement", 2))
                    .matches(String.format(draftBill, customerNumber, dueDate, issueDate));
        });
    }
}
