package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.helpers.DateTimeUtilsHelper;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.enums.ValueConstants;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.RecoveryDetailsActionTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsActionTab;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsTab.RecoveryDetails;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.data.TestData.makeKeyPath;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.CLAIM_HISTORY;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.ISSUED;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.PAYMENT_RECOVERY_NUMBER;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.STATUS;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.RecoveryDetailsActionTabMetaData.CHECK;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.RecoveryDetailsActionTabMetaData.RECOVERY_AMOUNT;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab.ProviderColumns.PRACTICE_NAME;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsTab.RecoveryDetails.*;
import static com.exigen.ren.main.modules.claim.gb_dn.tabs.RecoveryDetailsTab.tableRecoveryDetails;
import static com.exigen.ren.main.pages.summary.claim.ClaimHistoryPage.ClaimHistory.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimHistoryPage.tableClaimHistory;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimTrackHistoryPaymentRecoveryUpdate extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-38171", component = CLAIMS_GROUPBENEFITS)
    public void testClaimTrackHistoryPaymentRecoveryUpdate() {
        mainApp().open();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        dentalClaim.create(dentalClaim.getDefaultTestData("DataGatherCertificate", "TestData_WithoutPayment"));
        dentalClaim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED_TEAM_LEAD);
        String practiceNameLabel = ClaimSummaryPage.tableProvider.getRow(1).getCell(PRACTICE_NAME.getName()).getValue();

        LOGGER.info("Step 1");
        dentalClaim.postRecovery().perform(tdClaim.getTestData("ClaimPayment", "TestData_PostRecovery"));
        assertThat(tableSummaryOfClaimPaymentsAndRecoveries.getRow(1)).hasCellWithValue(STATUS.getName(), ISSUED);
        String recoveryId = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).getValue();

        LOGGER.info("Step 2");
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        assertSoftly(softly -> {
            softly.assertThat(tableRecoveryDetails.getHeader().getValue()).isEqualTo(ImmutableList.of("Recovery #", "Recovery Amount", "Recovered From", "Recovery Post Date",
                    "Recovery Method", "Check #", "Bank Account Number", "Recovery Memo", "Creator", "Status"));
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RecoveryDetails.RECOVERY_AMOUNT.getName())).hasValue(new Currency(0.01).toString());
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RECOVERY_METHOD.getName())).hasValue("Check");
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RECOVERED_FROM.getName())).hasValue(practiceNameLabel);
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RecoveryDetails.CHECK.getName())).hasValue("12345");
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RECOVERY_MEMO.getName())).hasValue("Simple test memo");
        });

        LOGGER.info("Step 3");
        NavigationPage.toSubTab(CLAIM_HISTORY);
        Page.dialogConfirmation.confirm();
        assertThat(tableClaimHistory.getRow(1)).hasCellWithValue(DATE_AND_TIME.getName(), "No records found.");

        LOGGER.info("Step 6");
        dentalClaim.updateRecovery().perform(tdClaim.getTestData("ClaimPayment", "TestData_PostRecovery")
                .adjust(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RecoveryDetailsActionTabMetaData.RECOVERY_AMOUNT.getLabel()), "10")
                .adjust(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), CHECK.getLabel()), "54321")
                .adjust(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RecoveryDetailsActionTabMetaData.RECOVERY_MEMO.getLabel()), "Memo after update")
                .adjust(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RecoveryDetailsActionTabMetaData.RECOVERY_POST_DATE.getLabel()),
                        TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtilsHelper.MM_DD_YYYY_H_MM_A)), 1);

        LOGGER.info("Step 7");
        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        assertSoftly(softly -> {
            softly.assertThat(tableRecoveryDetails.getHeader().getValue()).isEqualTo(ImmutableList.of("Recovery #", "Recovery Amount", "Recovered From", "Recovery Post Date",
                    "Recovery Method", "Check #", "Bank Account Number", "Recovery Memo", "Creator", "Status"));
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RecoveryDetails.RECOVERY_AMOUNT.getName())).hasValue(new Currency(10).toString());
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RECOVERY_METHOD.getName())).hasValue("Check");
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RECOVERED_FROM.getName())).hasValue(practiceNameLabel);
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RecoveryDetails.CHECK.getName())).hasValue("54321");
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RECOVERY_MEMO.getName())).hasValue("Memo after update");
        });

        LOGGER.info("Step 8");
        NavigationPage.toSubTab(CLAIM_HISTORY);
        Page.dialogConfirmation.confirm();
        assertThat(tableClaimHistory).hasRows(2);

        Row recoveryAmountHistoryRow = tableClaimHistory.getRow(ATTRIBUTE.getName(), RECOVERY_AMOUNT.getLabel());
        Row checkHistoryRow = tableClaimHistory.getRow(ATTRIBUTE.getName(), CHECK.getLabel());
        assertSoftly(softly -> {
            softly.assertThat(tableClaimHistory.getHeader().getValue()).doesNotContain(RecoveryDetailsActionTabMetaData.RECOVERY_POST_DATE.getLabel(), RecoveryDetailsActionTabMetaData.RECOVERY_MEMO.getLabel());

            softly.assertThat(recoveryAmountHistoryRow.getCell(ORIGINAL.getName())).hasValue(new Currency(0.01).toPlainString());
            softly.assertThat(recoveryAmountHistoryRow.getCell(NEW.getName())).hasValue(new Currency(10).toPlainString());
            softly.assertThat(recoveryAmountHistoryRow.getCell(REFERENCE.getName())).hasValue(recoveryId);

            softly.assertThat(checkHistoryRow.getCell(ORIGINAL.getName())).hasValue("12345");
            softly.assertThat(checkHistoryRow.getCell(NEW.getName())).hasValue("54321");
            softly.assertThat(checkHistoryRow.getCell(REFERENCE.getName())).hasValue(recoveryId);
        });

        LOGGER.info("Step 11");
        dentalClaim.updateRecovery().perform(tdClaim.getTestData("ClaimPayment", "TestData_PostRecovery")
                .mask(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RECOVERY_AMOUNT.getLabel()))
                .mask(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RecoveryDetailsActionTabMetaData.RECOVERED_FROM.getLabel()))
                .mask(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), CHECK.getLabel()))
                .adjust(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RecoveryDetailsActionTabMetaData.RECOVERY_METHOD.getLabel()), "EFT")
                .adjust(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RecoveryDetailsActionTabMetaData.BANK_ACCOUNT_NUMBER.getLabel()), "111111111111111111111")
                .adjust(makeKeyPath(RecoveryDetailsActionTab.class.getSimpleName(), RecoveryDetailsActionTabMetaData.RECOVERY_MEMO.getLabel()), ValueConstants.EMPTY), 1);

        tableSummaryOfClaimPaymentsAndRecoveries.getRow(1).getCell(PAYMENT_RECOVERY_NUMBER.getName()).controls.links.getFirst().click();
        assertSoftly(softly -> {
            softly.assertThat(tableRecoveryDetails.getHeader().getValue()).isEqualTo(ImmutableList.of("Recovery #", "Recovery Amount", "Recovered From", "Recovery Post Date",
                    "Recovery Method", "Check #", "Bank Account Number", "Recovery Memo", "Creator", "Status"));
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(BANK_ACCOUNT_NUMBER.getName())).hasValue("*****************1111");
            softly.assertThat(tableRecoveryDetails.getRow(1).getCell(RECOVERY_MEMO.getName())).hasValue(ValueConstants.EMPTY);
        });

        LOGGER.info("Step 13");
        NavigationPage.toSubTab(CLAIM_HISTORY);
        Page.dialogConfirmation.confirm();
        assertThat(tableClaimHistory).hasRows(5);

        Row recoveryMethodHistoryRow = tableClaimHistory.getRow(ATTRIBUTE.getName(), RecoveryDetailsActionTabMetaData.RECOVERY_METHOD.getLabel());
        Row bankAccountNumberHistoryRow = tableClaimHistory.getRow(ATTRIBUTE.getName(), "Bank Account");
        assertSoftly(softly -> {
            softly.assertThat(tableClaimHistory.getHeader().getValue())
                    .doesNotContain(RecoveryDetailsActionTabMetaData.RECOVERY_POST_DATE.getLabel(), RecoveryDetailsActionTabMetaData.RECOVERY_MEMO.getLabel());

            softly.assertThat(recoveryMethodHistoryRow.getCell(ORIGINAL.getName())).hasValue("Check");
            softly.assertThat(recoveryMethodHistoryRow.getCell(NEW.getName())).hasValue("EFT");
            softly.assertThat(recoveryMethodHistoryRow.getCell(REFERENCE.getName())).hasValue(recoveryId);

            softly.assertThat(bankAccountNumberHistoryRow.getCell(ORIGINAL.getName())).hasValue(ValueConstants.EMPTY);
            softly.assertThat(bankAccountNumberHistoryRow.getCell(NEW.getName())).hasValue("*****************1111");
            softly.assertThat(bankAccountNumberHistoryRow.getCell(REFERENCE.getName())).hasValue(recoveryId);
        });
    }
}
