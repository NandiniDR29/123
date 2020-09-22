package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.assets.AssetList;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.enums.BillingConstants;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.PaymentInquiryTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsActionTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.exigen.ren.rest.claim.ClaimRestContext;
import com.exigen.ren.rest.claim.model.payments.PaymentModel;
import com.exigen.ren.rest.claim.model.payments.PaymentNetPaymentAmtModel;
import com.exigen.ren.rest.claim.model.payments.PaymentPayeeModel;
import com.exigen.ren.rest.claim.model.recoveries.*;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.MM_DD_YYYY_H_MM_A;
import static com.exigen.ren.helpers.DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.ISSUED;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.*;
import static com.exigen.ren.main.modules.claim.common.tabs.BalanceActionTab.ClaimUnprocessedBalanceTableExtended.POST_DATE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.PaymentInquiryTabMetaData.PAYMENT_DETAILS;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.PaymentInquiryTabMetaData.PaymentDetailsSection.ORIGINAL_PAYEE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.RecoveryDetailsActionTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsTab.tableRecoveryDetails;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimRENSpecificFieldsAPI extends ClaimGroupBenefitsDNBaseTest implements ClaimRestContext {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-39908", component = CLAIMS_GROUPBENEFITS)
    public void testClaimRENSpecificFieldsAPI_TC01() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("Steps 1,4");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithoutPayment"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED);
        String claim1Number = ClaimSummaryPage.getClaimNumber();
        String todayDate = TimeSetterUtil.getInstance().getCurrentTime().withSecond(0).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);

        LOGGER.info("Step 5");
        TestData tdRecovery1 = dentalClaim.getDefaultTestData("ClaimPayment", "TestData_PostRecovery").resolveLinks();
        dentalClaim.postRecovery().perform(tdRecovery1);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)).hasCellWithValue(STATUS.getName(), ISSUED);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();

        LOGGER.info("Step 10");
        RecoveryModel recoveryModel1REST = claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, null, null, null, null, null, null).getModel().get(0);
        TestData tdRecoveryModel1 = dentalClaim.getDefaultTestData("ClaimPayment", "TestData_PostRecovery", RecoveryDetailsActionTab.class.getSimpleName());
        RecoveryModel recoveryModel1UI = createRecoveryModel(tdRecoveryModel1);
        assertThat(recoveryModel1REST).isEqualToIgnoringNullFields(recoveryModel1UI);

        LOGGER.info("Step 11");
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();
        dentalClaim.postRecovery().perform(tdSpecific().getTestData("TestData_Recovery2"));
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();

        LOGGER.info("Step 12");
        List<RecoveryModel> recoveriesModel = claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, null, null, null, null, null, null).getModel();
        recoveryModel1REST = recoveriesModel.get(0);
        assertThat(recoveryModel1REST).isEqualToIgnoringNullFields(recoveryModel1UI);
        RecoveryModel recoveryModel2REST = recoveriesModel.get(1);
        TestData tdRecoveryModel2 = tdSpecific().getTestData("TestData_Recovery2", RecoveryDetailsActionTab.class.getSimpleName());
        RecoveryModel recoveryModel2UI = createRecoveryModel(tdRecoveryModel2);
        assertThat(recoveryModel2REST).isEqualToIgnoringNullFields(recoveryModel2UI);

        LOGGER.info("Step 13-1");
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();
        dentalClaim.voidRecovery().perform(tdClaim.getTestData("ClaimPayment", "TestData_VoidRecovery"), 1);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        recoveryModel1UI = createRecoveryModel(tdRecoveryModel1);
        recoveriesModel = claimRENCoreRestService.getRENClaimRecoveries(claim1Number, "issued", null, null, null, null, null, null).getModel();
        assertThat(recoveriesModel).hasSize(1);
        assertThat(recoveriesModel.get(0)).isEqualToIgnoringNullFields(recoveryModel2UI);

        LOGGER.info("Step 13-2");
        recoveriesModel = claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, recoveryModel2UI.getRecoveryDetails().getEft().getBankAccountNumber(), null, null, null, null, null).getModel();
        assertThat(recoveriesModel).hasSize(0);

        LOGGER.info("Step 14");
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();
        dentalClaim.postRecovery().perform(tdRecovery1);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(3).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        RecoveryModel recoveryModel3UI = createRecoveryModel(tdRecoveryModel1);

        String checkNumber = tdRecovery1.getTestData(RecoveryDetailsActionTab.class.getSimpleName()).getValue(CHECK.getLabel());
        recoveriesModel = claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, checkNumber, null, null, null, null, null).getModel();
        assertThat(recoveriesModel).hasSize(2);
        assertThat(recoveriesModel.get(0)).isEqualToIgnoringNullFields(recoveryModel1UI);
        assertThat(recoveriesModel.get(1)).isEqualToIgnoringNullFields(recoveryModel3UI);

        LOGGER.info("Step 15");
        assertSoftly(softly -> {
            softly.assertThat(claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, checkNumber + "1", null, null, null, null, null).getModel()).hasSize(0);
            softly.assertThat(claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, checkNumber.substring(1), null, null, null, null, null).getModel()).hasSize(0);
            softly.assertThat(claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, "z" + checkNumber.substring(1), null, null, null, null, null).getModel()).hasSize(0);
        });

        LOGGER.info("Step 16");
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();
        LocalDateTime todayPlus4Days = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().plusDays(4);
        dentalClaim.postRecovery().perform(tdRecovery1.adjust(TestData.makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RECOVERY_POST_DATE.getLabel()),
                todayPlus4Days.format(DateTimeUtilsHelper.MM_DD_YYYY_H_MM_A)));
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(4).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        RecoveryModel recoveryModel4UI = createRecoveryModel(tdRecoveryModel1);
        recoveriesModel = claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, null, todayPlus4Days.format(YYYY_MM_DD_HH_MM_SS_Z), null, null, null, null).getModel();
        assertThat(recoveriesModel).hasSize(1);
        assertThat(recoveriesModel.get(0)).isEqualToIgnoringNullFields(recoveryModel4UI);

        LOGGER.info("Step 17");
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();
        dentalClaim.updateRecovery().perform(tdSpecific().getTestData("TestData_Update1"), 2);
        dentalClaim.updateRecovery().perform(tdSpecific().getTestData("TestData_Update2"), 3);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        recoveryModel2UI = createRecoveryModel(tdRecoveryModel2);
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(3).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        recoveryModel3UI = createRecoveryModel(tdRecoveryModel1);
        Tab.buttonTopCancel.click();
        Page.dialogConfirmation.confirm();

        LOGGER.info("Step 18");
        assertThat(claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, null, todayDate, null, null, null, null).getModel()).hasSize(4);

        LOGGER.info("Step 19");
        String todayPlus5Days = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().plusDays(5).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        assertThat(claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, null, todayPlus5Days, null, null, null, null).getModel()).hasSize(0);

        LOGGER.info("Step 20");
        todayDate = TimeSetterUtil.getInstance().getCurrentTime().withHour(23).withMinute(59).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        recoveriesModel = claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, null, null, todayDate, null, null, null).getModel();
        assertThat(recoveriesModel).hasSize(1);
        assertThat(recoveriesModel.get(0)).isEqualToIgnoringNullFields(recoveryModel1UI);

        LOGGER.info("Step 21");
        assertThat(claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, null, null, todayPlus4Days.format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z), null, null, null).getModel()).hasSize(4);

        LOGGER.info("Step 22");
        String todayMinus1Day = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay().minusDays(1).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        assertThat(claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, null, null, todayMinus1Day, null, null, null).getModel()).hasSize(0);

        LOGGER.info("Step 26");
        recoveriesModel = claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, null, null, null, "-recoveryIssueDate", null, null).getModel();
        assertThat(recoveriesModel).hasSize(4);
        assertThat(recoveriesModel.get(0)).isEqualToIgnoringNullFields(recoveryModel4UI);
        assertThat(recoveriesModel.get(1)).isEqualToIgnoringNullFields(recoveryModel3UI);
        assertThat(recoveriesModel.get(2)).isEqualToIgnoringNullFields(recoveryModel2UI);
        assertThat(recoveriesModel.get(3)).isEqualToIgnoringNullFields(recoveryModel1UI);

        LOGGER.info("Step 27");
        recoveriesModel = claimRENCoreRestService.getRENClaimRecoveries(claim1Number, null, null, null, null, "-recoveryIssueDate", "0", "1").getModel();
        assertThat(recoveriesModel).hasSize(1);
        assertThat(recoveriesModel.get(0)).isEqualToIgnoringNullFields(recoveryModel4UI);

        LOGGER.info("Step 30");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithPayment"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
        String claim2Number = ClaimSummaryPage.getClaimNumber();
        dentalClaim.postRecovery().perform(tdRecovery1);
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(2)).hasCellWithValue(STATUS.getName(), ISSUED);
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(2).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();

        LOGGER.info("Step 31");
        recoveriesModel = claimRENCoreRestService.getRENClaimRecoveries(claim2Number, null, null, null, null, null, null, null).getModel();
        RecoveryModel recoveryModel5UI = createRecoveryModel(tdRecoveryModel1);
        assertThat(recoveriesModel).hasSize(1);
        assertThat(recoveriesModel.get(0)).isEqualToIgnoringNullFields(recoveryModel5UI);
    }

    private RecoveryModel createRecoveryModel(TestData td) {
        RecoveryModel recoveryModel = new RecoveryModel();
        recoveryModel.setRecoveryNumber(tableRecoveryDetails.getRow(1).getCell(RecoveryDetailsTab.RecoveryDetails.RECOVERY_NUMBER.getName()).getValue());
        RecoveryGrossPaymentAmtModel grossPaymentAmtModel = new RecoveryGrossPaymentAmtModel();
        String recoveryAmount = tableRecoveryDetails.getRow(1).getCell(RecoveryDetailsTab.RecoveryDetails.RECOVERY_AMOUNT.getName()).getValue();
        grossPaymentAmtModel.setAmount(new Currency(recoveryAmount).toPlainString());
        grossPaymentAmtModel.setCurrencyCd("USD");
        recoveryModel.setGrossPaymentAmt(grossPaymentAmtModel);
        RecoveryPayeeModel recoveryPayeeModel = new RecoveryPayeeModel();
        recoveryPayeeModel.setDisplayValue(tableRecoveryDetails.getRow(1).getCell(RecoveryDetailsTab.RecoveryDetails.RECOVERED_FROM.getName()).getValue());
        recoveryModel.setPayee(recoveryPayeeModel);
        String recoveryPostDate = tableRecoveryDetails.getRow(1).getCell(RecoveryDetailsTab.RecoveryDetails.RECOVERY_POST_DATE.getName()).getValue();
        recoveryModel.setRecoveryIssueDate(LocalDateTime.parse(recoveryPostDate, MM_DD_YYYY_H_MM_A).format(YYYY_MM_DD_HH_MM_SS_Z));
        recoveryModel.setRecoveryStatus(tableRecoveryDetails.getRow(1).getCell(RecoveryDetailsTab.RecoveryDetails.STATUS.getName()).getValue());
        recoveryModel.setRecoveryMemo(td.getValue(RECOVERY_MEMO.getLabel()));
        RecoveryDetailsModel recoveryDetailsModel = new RecoveryDetailsModel();
        if (td.getValue(RECOVERY_METHOD.getLabel()).equals("Check")) {
            recoveryDetailsModel.setType("cheque");
            RecoveryDetailsChequeModel recoveryDetailsChequeModel = new RecoveryDetailsChequeModel();
            recoveryDetailsChequeModel.setCheckNumber(td.getValue(CHECK.getLabel()));
            recoveryDetailsModel.setCheque(recoveryDetailsChequeModel);
        } else {
            recoveryDetailsModel.setType("eft");
            RecoveryDetailsEFTModel recoveryDetailsEFTModel = new RecoveryDetailsEFTModel();
            recoveryDetailsEFTModel.setBankAccountNumber(td.getValue(BANK_ACCOUNT_NUMBER.getLabel()));
            recoveryDetailsModel.setEft(recoveryDetailsEFTModel);
        }
        recoveryModel.setRecoveryDetails(recoveryDetailsModel);
        return  recoveryModel;
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITH_TS})
    @TestInfo(testCaseId = "REN-39913", component = CLAIMS_GROUPBENEFITS)
    public void testClaimRENSpecificFieldsAPI_TC02() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        createDefaultCaseProfile(groupDentalMasterPolicy.getType());
        createDefaultGroupDentalMasterPolicy();
        createDefaultGroupDentalCertificatePolicy();

        LOGGER.info("Steps 1,4");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithPayment"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
        String claim1Number = ClaimSummaryPage.getClaimNumber();
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.STATUS.getName()))
                .hasValue(BillingConstants.PaymentsAndOtherTransactionStatus.APPROVED);

        LOGGER.info("Step 9");
        PaymentModel paymentModel1UI = createPaymentModel(1);
        List<PaymentModel> paymentsModel = claimRENCoreRestService.getRENClaimPayments(claim1Number, null, null, null, null, null, null, null).getModel();
        assertThat(paymentsModel).hasSize(1);
        assertThat(paymentsModel.get(0).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel1UI);

        LOGGER.info("Step 11");
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.ADJUDICATION.get());
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));
        dentalClaim.claimSubmit().perform();
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        paymentModel1UI = createPaymentModel(1);
        PaymentModel paymentModel2UI = createPaymentModel(2);
        paymentsModel = claimRENCoreRestService.getRENClaimPayments(claim1Number, "approved", null, null, null, null, null, null).getModel();
        assertThat(paymentsModel).hasSize(1);
        assertThat(paymentsModel.get(0).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel2UI);

        LOGGER.info("Steps 16-18");
        LocalDateTime todayDateFrom = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().atStartOfDay();
        LocalDateTime todayDateTill = TimeSetterUtil.getInstance().getCurrentTime().withHour(23).withMinute(59);
        moveDateAndAdjustClaim(claim1Number);
        moveDateAndAdjustClaim(claim1Number);
        moveDateAndAdjustClaim(claim1Number);
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        paymentModel2UI = createPaymentModel(2);
        PaymentModel paymentModel3UI = createPaymentModel(3);
        PaymentModel paymentModel4UI = createPaymentModel(4);
        PaymentModel paymentModel5UI = createPaymentModel(5);

        LOGGER.info("Step 19");
        String todayPlus1Day = todayDateFrom.plusDays(1).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        paymentsModel = claimRENCoreRestService.getRENClaimPayments(claim1Number, null, null, todayPlus1Day, null, null, null, null).getModel();
        assertThat(paymentsModel).hasSize(3);
        assertThat(paymentsModel.get(0).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel3UI);
        assertThat(paymentsModel.get(1).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel4UI);
        assertThat(paymentsModel.get(2).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel5UI);

        LOGGER.info("Step 20");
        String todayPlus3DaysFrom = todayDateFrom.plusDays(3).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        paymentsModel = claimRENCoreRestService.getRENClaimPayments(claim1Number, null, null, todayPlus3DaysFrom, null, null, null, null).getModel();
        assertThat(paymentsModel).hasSize(1);
        assertThat(paymentsModel.get(0).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel5UI);

        LOGGER.info("Step 21");
        String todayPlus4Days = todayDateFrom.plusDays(4).format(DateTimeUtilsHelper.YYYY_MM_DD_HH_MM_SS_Z);
        paymentsModel = claimRENCoreRestService.getRENClaimPayments(claim1Number, null, null, todayPlus4Days, null, null, null, null).getModel();
        assertThat(paymentsModel).hasSize(0);

        LOGGER.info("Step 22");
        paymentsModel = claimRENCoreRestService.getRENClaimPayments(claim1Number, null, null, null, todayDateTill.format(YYYY_MM_DD_HH_MM_SS_Z), null, null, null).getModel();
        assertThat(paymentsModel).hasSize(2);
        assertThat(paymentsModel.get(0).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel1UI);
        assertThat(paymentsModel.get(1).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel2UI);

        LOGGER.info("Step 23");
        String todayPlus3DaysTill = todayDateTill.plusDays(3).format(YYYY_MM_DD_HH_MM_SS_Z);
        assertThat(claimRENCoreRestService.getRENClaimPayments(claim1Number, null, null, null, todayPlus3DaysTill, null, null, null).getModel()).hasSize(5);

        LOGGER.info("Step 28");
        paymentsModel = claimRENCoreRestService.getRENClaimPayments(claim1Number, null, null, null, null, "-paymentIssueDate", null, null).getModel();
        assertThat(paymentsModel).hasSize(5);
        assertThat(paymentsModel.get(0).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel5UI);
        assertThat(paymentsModel.get(1).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel4UI);
        assertThat(paymentsModel.get(2).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel3UI);
        assertThat(paymentsModel.get(3).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel2UI);
        assertThat(paymentsModel.get(4).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel1UI);

        LOGGER.info("Step 29");
        paymentsModel = claimRENCoreRestService.getRENClaimPayments(claim1Number, null, null, null, null, "-paymentIssueDate", "0", "1").getModel();
        assertThat(paymentsModel).hasSize(1);
        assertThat(paymentsModel.get(0).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel5UI);

        LOGGER.info("Steps 32,33");
        dentalClaim.create(dentalClaim.getDefaultTestData(DATA_GATHER_CERTIFICATE, "TestData_WithPayment"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.ADJUDICATED);
        String claim2Number = ClaimSummaryPage.getClaimNumber();
        NavigationPage.toSubTab(NavigationEnum.ClaimTab.FINANCIALS.get());
        PaymentModel paymentModel6UI = createPaymentModel(1);
        paymentsModel = claimRENCoreRestService.getRENClaimPayments(claim2Number, null, null, null, null, null, null, null).getModel();
        assertThat(paymentsModel).hasSize(1);
        assertThat(paymentsModel.get(0).normalizePaymentDate()).isEqualToIgnoringNullFields(paymentModel6UI);
    }

    private PaymentModel createPaymentModel(int row) {
        PaymentModel paymentModel = new PaymentModel();
        paymentModel.setPaymentNumber(tableSummaryOfClaimPaymentsAndRecoveries.getRow(row).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue());
        String paymentIssueDate = tableSummaryOfClaimPaymentsAndRecoveries.getRow(row).getCell(POST_DATE.getName()).getValue();
        paymentModel.setPaymentIssueDate(LocalDateTime.parse(paymentIssueDate, MM_DD_YYYY_H_MM_A).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        paymentModel.setPaymentStatus(tableSummaryOfClaimPaymentsAndRecoveries.getRow(row).getCell(STATUS.getName()).getValue().toLowerCase());
        PaymentNetPaymentAmtModel paymentNetPaymentAmtModel = new PaymentNetPaymentAmtModel();
        String amount = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_NET_AMOUNT.getName()).getValue();
        paymentNetPaymentAmtModel.setAmount(new Currency(amount).toPlainString());
        paymentNetPaymentAmtModel.setCurrencyCd("USD");
        paymentModel.setNetPaymentAmt(paymentNetPaymentAmtModel);
        dentalClaim.paymentInquiry().start(row);
        AssetList paymentDetails = dentalClaim.paymentInquiry().getWorkspace().getTab(PaymentInquiryTab.class).getAssetList().getAsset(PAYMENT_DETAILS);
        PaymentPayeeModel paymentPayeeModel = new PaymentPayeeModel();
        paymentPayeeModel.setDisplayValue(paymentDetails.getAsset(ORIGINAL_PAYEE).getValue());
        paymentModel.setPayee(paymentPayeeModel);
        dentalClaim.paymentInquiry().submit();
        return paymentModel;
    }

    private void moveDateAndAdjustClaim(String claimNumber) {
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
        mainApp().reopen();
        MainPage.QuickSearch.search(claimNumber);
        dentalClaim.claimAdjust().perform(tdClaim.getTestData("ClaimAdjust", TestDataKey.DEFAULT_TEST_DATA_KEY));
        dentalClaim.claimSubmit().perform();
    }

}
