package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.admin.modules.billing.billingcycle.cancellations.benefits.CancellationsBenefitsContext;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.metadata.SearchMetaData;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.modules.billing.account.metadata.AcceptPaymentActionTabMetaData;
import com.exigen.ren.main.modules.billing.account.tabs.AcceptPaymentActionTab;
import com.exigen.ren.main.modules.billing.setup_billing_groups.tabs.BillingGroupsEffectiveDateTab;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData;
import com.exigen.ren.main.modules.caseprofile.tabs.CaseProfileDetailsTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.CertificatePolicyTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.certificate.metadata.PlansTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.main.pages.summary.billing.BillingSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.exigen.ren.rest.billing.BillingBenefitsRestService;
import com.exigen.ren.rest.claim.ClaimRestContext;
import com.exigen.ren.rest.claim.model.common.claimbody.claim.*;
import com.exigen.ren.rest.claim.model.dental.claiminfo.ClaimInfoSubmittedProcedureModel;
import com.exigen.ren.rest.claim.model.dental.claiminfo.ClaimInfoSubmittedProcedurePolicyInfoModel;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.*;
import static com.exigen.ren.admin.modules.billing.billingcycle.cancellations.benefits.tabs.CancellationsBenefitsTab.tableBenefits;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.EDIT_CLAIM;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD;
import static com.exigen.ren.main.enums.BillingConstants.BillingBillsAndStatmentsTable.TOTAL_DUE;
import static com.exigen.ren.main.enums.PolicyConstants.PlanDental.BASEPOS;
import static com.exigen.ren.main.modules.billing.account.BillingAccountContext.billingAccount;
import static com.exigen.ren.main.modules.caseprofile.metadata.CaseProfileDetailsTabMetaData.GROUP_DOMICILE_STATE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.*;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.POLICY_EFFECTIVE_DATE;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PolicyInformationTabMetaData.SITUS_STATE;
import static com.exigen.ren.main.modules.billing.setup_billing_groups.metadata.BillingGroupsEffectiveDateTabMetaData.BILLING_GROUPS_EFFECTIVE_DATE;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestClaimGetPTDWithGracePeriodFromBilling extends ClaimGroupBenefitsDNBaseTest implements ClaimRestContext, CancellationsBenefitsContext {

    private BillingBenefitsRestService billingBenefitsRestService = new BillingBenefitsRestService();

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39889", component = CLAIMS_GROUPBENEFITS)
    public void testClaimGetPTDWithGracePeriodFromBillingTC01() {
        adminApp().open();
        cancellationsBenefits.navigate();
        if (!tableBenefits.getRow(ImmutableMap.of("Products", "GB_DN", "Geography", "US(CO)")).isPresent()) {
            cancellationsBenefits.create(tdSpecific().getTestData("TestData_CancellationsDN"));
        }

        mainApp().reopen();
        createDefaultNonIndividualCustomer();
        LocalDateTime todayMinus3Months = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().minusMonths(3);
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupDentalMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.EFFECTIVE_DATE.getLabel()), todayMinus3Months.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), GROUP_DOMICILE_STATE.getLabel()),"CO"));
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData()
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE.getLabel()), todayMinus3Months.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), todayMinus3Months.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), SITUS_STATE.getLabel()), "CO"));
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String todayMinus2Months = TimeSetterUtil.getInstance().getCurrentTime().minusMonths(2).format(MM_DD_YYYY);
        groupDentalCertificatePolicy.createPolicyViaUI(getDefaultGroupDentalCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), todayMinus2Months)
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), SITUS_STATE.getLabel()), "CO"));

        LOGGER.info("Step 1");
        dentalClaim.initiate(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));
        String claimNumber = ClaimSummaryPage.getClaimNumber();

        LOGGER.info("Step 2");
        String gracePeriod = billingBenefitsRestService.getPoliciesGracePeriod(masterPolicyNumber, todayMinus3Months).getModel().getGracePeriodConfigurations().get(0).getGracePeriod();
        String todayDateTime = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        ClaimExtensionModel claimExtensionModel = claimCoreRestService.getClaimImage(claimNumber).getModel().getClaim().getExtension();
        assertSoftly(softly -> {
            softly.assertThat(claimExtensionModel.getPaidToDateInfo().getPaidToDateInfoEntity().get(todayDateTime).getGracePeriod()).isEqualTo(gracePeriod);
            softly.assertThat(claimExtensionModel.getPolicyInformation().getServices().get(todayDateTime).getMasterPolicyDetails().getCoverageDefinitions().get(0).isSelfAdministered()).isEqualTo(false);
            softly.assertThat(claimExtensionModel.getPolicyInformation().getServices().get(todayDateTime).getMasterPolicyDetails().getPolicyEffectiveDt()).isEqualTo(todayMinus3Months.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z));
        });

        LOGGER.info("Step 3");
        LocalDateTime todayMinus4Months = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().minusMonths(4);
        intakeInformationTab.fillTab(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_TwoServices")
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel() + "[0]", DOS.getLabel()), todayMinus4Months.format(MM_DD_YYYY)));
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step 4");
        ClaimExtensionModel claimExtensionModel2 = claimCoreRestService.getClaimImage(claimNumber).getModel().getClaim().getExtension();
        assertSoftly(softly -> {
            softly.assertThat(claimExtensionModel2.getPaidToDateInfo().getPaidToDateInfoEntity()).hasSize(1);
            softly.assertThat(claimExtensionModel2.getPaidToDateInfo().getPaidToDateInfoEntity().get(todayDateTime).getGracePeriod()).isEqualTo(gracePeriod);
            softly.assertThat(claimExtensionModel2.getPolicyInformation().getServices().get(todayDateTime).getMasterPolicyDetails().getCoverageDefinitions().get(0).isSelfAdministered()).isEqualTo(false);
            softly.assertThat(claimExtensionModel2.getPolicyInformation().getServices().get(todayDateTime).getMasterPolicyDetails().getPolicyEffectiveDt()).isEqualTo(todayMinus3Months.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z));
            softly.assertThat(claimExtensionModel2.getPolicyInformation().getServices().get(todayMinus4Months.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z))).isNull();
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39671", component = CLAIMS_GROUPBENEFITS)
    public void testClaimGetPTDWithGracePeriodFromBillingTC02() {
        adminApp().open();
        cancellationsBenefits.navigate();
        if (!tableBenefits.getRow(ImmutableMap.of("Products", "GB_DN", "Geography", "US(CO)")).isPresent()) {
            cancellationsBenefits.create(tdSpecific().getTestData("TestData_CancellationsDN"));
        }

        mainApp().reopen();
        createDefaultNonIndividualCustomer();
        LocalDateTime todayMinus3Months = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().minusMonths(3);
        caseProfile.create(CaseProfileContext.getDefaultCaseProfileTestData(groupDentalMasterPolicy.getType())
                .adjust(TestData.makeKeyPath(caseProfileDetailsTab.getMetaKey(), CaseProfileDetailsTabMetaData.EFFECTIVE_DATE.getLabel()), todayMinus3Months.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(CaseProfileDetailsTab.class.getSimpleName(), GROUP_DOMICILE_STATE.getLabel()),"CO"));
        groupDentalMasterPolicy.createPolicy(getDefaultDNMasterPolicyData().adjust(tdSpecific().getTestData("TestData_TwoPlans").resolveLinks())
                .adjust(TestData.makeKeyPath("InitiniateDialog", SearchMetaData.DialogSearch.COVERAGE_EFFECTIVE_DATE.getLabel()), todayMinus3Months.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), POLICY_EFFECTIVE_DATE.getLabel()), todayMinus3Months.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(policyInformationTab.getMetaKey(), PolicyInformationTabMetaData.SITUS_STATE.getLabel()), "CO"));
        groupDentalMasterPolicy.setupBillingGroups().perform(tdSpecific().getTestData("TestData_BillingGroups")
                .adjust(TestData.makeKeyPath(BillingGroupsEffectiveDateTab.class.getSimpleName(), BILLING_GROUPS_EFFECTIVE_DATE.getLabel()), todayMinus3Months.format(MM_DD_YYYY)));
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        LocalDateTime todayMinus2Months = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().minusMonths(2);
        groupDentalCertificatePolicy.createPolicyViaUI(getDefaultGroupDentalCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), todayMinus2Months.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), SITUS_STATE.getLabel()), "CO"));

        LOGGER.info("Step 1");
        dentalClaim.initiate(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        Tab.buttonTopSave.click();

        LOGGER.info("Step 2");
        String gracePeriod = billingBenefitsRestService.getPoliciesGracePeriod(masterPolicyNumber, todayMinus3Months).getModel().getGracePeriodConfigurations().get(0).getGracePeriod();
        String paidToDate = billingBenefitsRestService.getPoliciesPaidToDate(masterPolicyNumber, todayMinus3Months).getModel().getPaidToDate();
        String todayDateTime = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        PaidToDateInfoEntityModel paidToDateInfoEntityModel = claimCoreRestService.getClaimImage(claimNumber).getModel().getClaim().getExtension().getPaidToDateInfo().getPaidToDateInfoEntity().get(todayDateTime);
        LocalDateTime paidToDateFromClaimImage = LocalDateTime.parse(paidToDateInfoEntityModel.getPaidToDate(), DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        ClaimInfoSubmittedProcedurePolicyInfoModel claimInfoSubmittedProcedurePolicyInfoModel = dentalClaimRest.getClaimInfo(claimNumber).getModel().getSubmittedProcedures().get(0).getPolicyInfo();
        assertSoftly(softly -> {
            softly.assertThat(paidToDateInfoEntityModel.getGracePeriod()).isEqualTo(gracePeriod);
            softly.assertThat(paidToDateFromClaimImage.format(DateTimeUtilsHelper.YYYY_MM_DD)).isEqualTo(paidToDate).isEqualTo(todayMinus3Months.format(YYYY_MM_DD));
            softly.assertThat(paidToDateInfoEntityModel.getPaidToDateWithGracePeriod()).isEqualTo(paidToDateFromClaimImage.plusDays(Integer.parseInt(gracePeriod)).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z));
            softly.assertThat(claimInfoSubmittedProcedurePolicyInfoModel.getPolicyPaidToDate()).isEqualTo(paidToDateInfoEntityModel.getPaidToDate());
            softly.assertThat(claimInfoSubmittedProcedurePolicyInfoModel.getPolicyPaidToDateWithGracePeriod()).isEqualTo(paidToDateInfoEntityModel.getPaidToDateWithGracePeriod());
        });

        LOGGER.info("Step 3");
        Tab.buttonSaveAndExit.click();
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateFutureStatement().perform();
        String billingPeriod = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String billingPeriodEndDate = LocalDate.parse(billingPeriod.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        LOGGER.info("Step 4");
        String paidToDate2 = billingBenefitsRestService.getPoliciesPaidToDate(masterPolicyNumber, todayMinus3Months).getModel().getPaidToDate();
        PaidToDateInfoEntityModel paidToDateInfoEntityModel2 = claimCoreRestService.getClaimImage(claimNumber).getModel().getClaim().getExtension().getPaidToDateInfo().getPaidToDateInfoEntity().get(todayDateTime);
        LocalDateTime paidToDateFromClaimImage2 = LocalDateTime.parse(paidToDateInfoEntityModel2.getPaidToDate(), DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        ClaimInfoSubmittedProcedurePolicyInfoModel claimInfoSubmittedProcedurePolicyInfoModel2 = dentalClaimRest.getClaimInfo(claimNumber).getModel().getSubmittedProcedures().get(0).getPolicyInfo();
        assertSoftly(softly -> {
            softly.assertThat(paidToDateInfoEntityModel2.getGracePeriod()).isEqualTo(gracePeriod);
            softly.assertThat(paidToDateFromClaimImage2.format(DateTimeUtilsHelper.YYYY_MM_DD)).isEqualTo(paidToDate2).isEqualTo(billingPeriodEndDate).isNotEqualTo(paidToDate);
            softly.assertThat(paidToDateInfoEntityModel2.getPaidToDateWithGracePeriod()).isEqualTo(paidToDateFromClaimImage2.plusDays(Integer.parseInt(gracePeriod)).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z));
            softly.assertThat(claimInfoSubmittedProcedurePolicyInfoModel2.getPolicyPaidToDate()).isEqualTo(paidToDateInfoEntityModel2.getPaidToDate());
            softly.assertThat(claimInfoSubmittedProcedurePolicyInfoModel2.getPolicyPaidToDateWithGracePeriod()).isEqualTo(paidToDateInfoEntityModel2.getPaidToDateWithGracePeriod());
        });

        LOGGER.info("Step 5");
        MainPage.QuickSearch.search(claimNumber);
        NavigationPage.toSubTab(EDIT_CLAIM);
        LocalDateTime todayMinus4Months = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().minusMonths(4);
        LocalDateTime todayMinus1Month = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().minusMonths(1);
        intakeInformationTab.fillTab(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_ThreeServices")
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel() + "[0]", DOS.getLabel()), todayMinus1Month.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel() + "[1]", DOS.getLabel()), todayMinus2Months.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(IntakeInformationTab.class.getSimpleName(), SUBMITTED_SERVICES.getLabel() + "[2]", DOS.getLabel()), todayMinus4Months.format(MM_DD_YYYY)));
        Tab.buttonSaveAndExit.click();

        LOGGER.info("Step 6");
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.BILLING);
        billingAccount.generateFutureStatement().perform();
        String paidToDate3 = billingBenefitsRestService.getPoliciesPaidToDate(masterPolicyNumber, todayMinus3Months).getModel().getPaidToDate();
        assertThat(paidToDate3).isEqualTo(paidToDate2);

        LOGGER.info("Step 8");
        Currency policyTotalDue = new Currency(BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(TOTAL_DUE).getValue());
        billingAccount.acceptPayment().perform(billingAccount.getDefaultTestData("AcceptPayment", "TestData_Cash")
                .adjust(TestData.makeKeyPath(AcceptPaymentActionTab.class.getSimpleName(), AcceptPaymentActionTabMetaData.AMOUNT.getLabel()), new Currency(policyTotalDue).toString()));
        String billingPeriod2 = BillingSummaryPage.tableBillsAndStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.BILING_PERIOD).getValue();
        String billingPeriodEndDate2 = LocalDate.parse(billingPeriod2.split(" - ")[1], MM_DD_YYYY).format(YYYY_MM_DD);

        LOGGER.info("Step 9");
        String paidToDate4 = billingBenefitsRestService.getPoliciesPaidToDate(masterPolicyNumber, todayMinus3Months).getModel().getPaidToDate();
        Map<String, PaidToDateInfoEntityModel> paidToDateInfo = claimCoreRestService.getClaimImage(claimNumber).getModel().getClaim().getExtension().getPaidToDateInfo().getPaidToDateInfoEntity();
        assertThat(paidToDateInfo).hasSize(2);
        List<ClaimInfoSubmittedProcedureModel> submittedProcedures = dentalClaimRest.getClaimInfo(claimNumber).getModel().getSubmittedProcedures();
        Stream.of(todayMinus1Month.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z), todayMinus2Months.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z)).forEach(
                dos -> {
                    PaidToDateInfoEntityModel paidToDateInfoEntity = paidToDateInfo.get(dos);
                    LocalDateTime paidToDateFromClaimImage3 = LocalDateTime.parse(paidToDateInfoEntity.getPaidToDate(), DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
                    assertSoftly(softly -> {
                        softly.assertThat(paidToDateInfoEntity.getGracePeriod()).isEqualTo(gracePeriod);
                        softly.assertThat(paidToDateFromClaimImage3.format(DateTimeUtilsHelper.YYYY_MM_DD)).isEqualTo(paidToDate4).isEqualTo(billingPeriodEndDate2).isNotEqualTo(paidToDate2);
                        softly.assertThat(paidToDateInfoEntity.getPaidToDateWithGracePeriod()).isEqualTo(paidToDateFromClaimImage3.plusDays(Integer.parseInt(gracePeriod)).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z));
                        Optional<ClaimInfoSubmittedProcedureModel> claimInfoSubmittedProcedureModel = submittedProcedures.stream().filter(submittedProcedure -> submittedProcedure.getDateOfService().equals(dos)).findFirst();
                        assertThat(claimInfoSubmittedProcedureModel).isPresent();
                        softly.assertThat(claimInfoSubmittedProcedureModel.get().getPolicyInfo().getPolicyPaidToDate()).isEqualTo(paidToDateInfoEntity.getPaidToDate());
                        softly.assertThat(claimInfoSubmittedProcedureModel.get().getPolicyInfo().getPolicyPaidToDateWithGracePeriod()).isEqualTo(paidToDateInfoEntity.getPaidToDateWithGracePeriod());
                    });
                });

        LOGGER.info("Step 10");
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalCertificatePolicy.createPolicyViaUI(getDefaultGroupDentalCertificatePolicyData()
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), CertificatePolicyTabMetaData.EFFECTIVE_DATE.getLabel()), todayMinus3Months.format(MM_DD_YYYY))
                .adjust(TestData.makeKeyPath(coveragesTab.getMetaKey(), PlansTabMetaData.PLAN_NAME.getLabel()), BASEPOS)
                .adjust(TestData.makeKeyPath(certificatePolicyTab.getMetaKey(), SITUS_STATE.getLabel()), "CO"));

        LOGGER.info("Step 11");
        String paidToDate5 = billingBenefitsRestService.getPoliciesPaidToDate(masterPolicyNumber, todayMinus3Months).getModel().getPaidToDate();
        Map<String, PaidToDateInfoEntityModel> paidToDateInfo2 = claimCoreRestService.getClaimImage(claimNumber).getModel().getClaim().getExtension().getPaidToDateInfo().getPaidToDateInfoEntity();
        assertThat(paidToDateInfo2).hasSize(2);
        List<ClaimInfoSubmittedProcedureModel> submittedProcedures2 = dentalClaimRest.getClaimInfo(claimNumber).getModel().getSubmittedProcedures();
        Stream.of(todayMinus1Month.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z), todayMinus2Months.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z)).forEach(
                dos -> {
                    PaidToDateInfoEntityModel paidToDateInfoEntity = paidToDateInfo2.get(dos);
                    LocalDateTime paidToDateFromClaimImage4 = LocalDateTime.parse(paidToDateInfoEntity.getPaidToDate(), DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
                    assertSoftly(softly -> {
                        softly.assertThat(paidToDateInfoEntity.getGracePeriod()).isEqualTo(gracePeriod);
                        softly.assertThat(paidToDateFromClaimImage4.format(DateTimeUtilsHelper.YYYY_MM_DD)).isEqualTo(paidToDate5).isEqualTo(todayMinus3Months.format(YYYY_MM_DD)).isNotEqualTo(paidToDate3);
                        softly.assertThat(paidToDateInfoEntity.getPaidToDateWithGracePeriod()).isEqualTo(paidToDateFromClaimImage4.plusDays(Integer.parseInt(gracePeriod)).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z));
                        Optional<ClaimInfoSubmittedProcedureModel> claimInfoSubmittedProcedureModel = submittedProcedures2.stream().filter(submittedProcedure -> submittedProcedure.getDateOfService().equals(dos)).findFirst();
                        assertThat(claimInfoSubmittedProcedureModel).isPresent();
                        softly.assertThat(claimInfoSubmittedProcedureModel.get().getPolicyInfo().getPolicyPaidToDate()).isEqualTo(paidToDateInfoEntity.getPaidToDate());
                        softly.assertThat(claimInfoSubmittedProcedureModel.get().getPolicyInfo().getPolicyPaidToDateWithGracePeriod()).isEqualTo(paidToDateInfoEntity.getPaidToDateWithGracePeriod());
                    });
                });
    }

}
