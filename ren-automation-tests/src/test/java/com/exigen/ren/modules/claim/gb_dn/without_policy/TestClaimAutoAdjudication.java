package com.exigen.ren.modules.claim.gb_dn.without_policy;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ActionConstants;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.LineOverrideTab;
import com.exigen.ren.main.pages.summary.CustomerSummaryPage;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_WITHOUT_POLICY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.*;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSpecialHandlingStatus.ADJUSTED;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.*;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.*;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.*;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.DECISION;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryResultsOfAdjudicationTableExtended.REMARK_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.CDT_CODE;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.SubmittedServicesSection.DOS;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAutoAdjudication extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15694", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPredetermination() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        LOGGER.info("Step 1");
        TestData testDataWithoutPolicy = dentalClaim.getDefaultTestData(DATA_GATHER_WITHOUT_POLICY, TestDataKey.DEFAULT_TEST_DATA_KEY);
        dentalClaim.initiateWithoutPolicy(testDataWithoutPolicy);

        LOGGER.info("Step 2");
        // TODO (ybandarenka) https://jira.exigeninsurance.com/browse/REN-15694?focusedCommentId=4845427&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-4845427
        assertThat(intakeInformationTab.getAssetList().getAsset(PATIENT).getAsset(PatientMetaData.NAME)).containsAllOptions("", String.format("%s - Customer", customerName), "Other Individual");

        LOGGER.info("Step 3");
        dentalClaim.getDefaultWorkspace().fillUpTo(testDataWithoutPolicy
                        .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), TYPE_OF_TRANSACTION.getLabel()), "Predetermination")
                        .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel() + "[0]", DOS.getLabel()), "")
                        .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel() + "[0]", CDT_CODE.getLabel()), "0471"),
                intakeInformationTab.getClass(), true);
        intakeInformationTab.submitTab();
        assertSoftly(softly -> {
            softly.assertThat(NavigationPage.isSubTabSelected(ADJUDICATION)).isTrue();
            softly.assertThat(ClaimSummaryPage.labelClaimPatient).hasValue(customerName);
        });

        LOGGER.info("Step 10, 11");
        claim.claimSubmit().perform();
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(PREDETERMINED);
            Row row = tableResultsOfAdjudication.getRow(1);
            softly.assertThat(row.getCell(DECISION.getName())).hasValue("Disallowed");
            softly.assertThat(row.getCell(REMARK_CODE.getName())).hasValue("MI07006");

            row.getCell("Actions").controls.links.get(ActionConstants.VIEW).click();
            softly.assertThat(LineOverrideTab.labelDecision).hasValue("Disallowed");
            softly.assertThat(LineOverrideTab.labelRemarkCode).hasValue("MI07006");
        });

        LOGGER.info("Step 12");
        Tab.buttonCancel.click();
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption("Adjust Claim");
    }

    @Test(groups = {CLAIM_GB, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15694", component = CLAIMS_GROUPBENEFITS)
    public void testClaimActualServicesAndPredetermination() {
        mainApp().open();
        createDefaultNonIndividualCustomer();
        String customerName = CustomerSummaryPage.labelCustomerName.getValue();

        LOGGER.info("Step 13");
        TestData testDataWithoutPolicy = dentalClaim.getDefaultTestData(DATA_GATHER_WITHOUT_POLICY, "TestData_With_Two_Services")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), TYPE_OF_TRANSACTION.getLabel()), "Actual Services")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel() + "[0]", CDT_CODE.getLabel()), "D0110")
                .adjust(TestData.makeKeyPath(intakeInformationTab.getMetaKey(), SUBMITTED_SERVICES.getLabel() + "[0]", DOS.getLabel()), TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
        dentalClaim.initiateWithoutPolicy(testDataWithoutPolicy);

        LOGGER.info("Step 14");
        dentalClaim.getDefaultWorkspace().fillUpTo(testDataWithoutPolicy, intakeInformationTab.getClass(), true);
        String practiceName = intakeInformationTab.getAssetList().getAsset(IntakeInformationTabMetaData.SEARCH_PROVIDER).getAsset(IntakeInformationTabMetaData.SearchProviderMetaData.PRACTICE_NAME).getValue();
        intakeInformationTab.submitTab();
        assertSoftly(softly -> {
            softly.assertThat(NavigationPage.isSubTabSelected(ADJUDICATION)).isTrue();
            softly.assertThat(ClaimSummaryPage.labelClaimPatient).hasValue(customerName);
        });

        LOGGER.info("Step 16, 21");
        claim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ADJUDICATED);
        assertSoftly(softly -> {
            tableResultsOfAdjudication.getRows().forEach(row -> {
                row.getCell("Actions").controls.links.get(ActionConstants.VIEW).click();
                softly.assertThat(NavigationPage.isSubTabSelected("Line Override")).isTrue();
                Tab.buttonCancel.click();
            });
        });

        LOGGER.info("Step 22");
        NavigationPage.toSubTab(FINANCIALS);
        Row firstPaymentRow = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1);
        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(firstPaymentRow.getCell(PAYMENT_NET_AMOUNT.getName())).hasValue(new Currency("0").toString());

            // TODO Can't get hint value via getHintValue()
            softly.assertThat(firstPaymentRow.getCell(PAYEE_RECOVERED_FROM.getName()).getWebElement().findElement(By.xpath(".//span[contains(@id, 'partyNameDisplayValue')]")).getAttribute("title")).isEqualTo(practiceName);

            softly.assertThat(firstPaymentRow.getCell(STATUS.getName())).hasValue(APPROVED);
        });

        LOGGER.info("Step 23");
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(PAID);
            softly.assertThat(firstPaymentRow.getCell(STATUS.getName())).hasValue(ISSUED);
        });

        LOGGER.info("Step 24");
        NavigationPage.toSubTab(ADJUDICATION);
        assertThat(NavigationPage.comboBoxListAction).doesNotContainOption("Adjust Claim");

        LOGGER.info("Step 26");
        NavigationPage.toSubTab("Patient History");
        assertSoftly(softly -> {
            tablePatientHistory.getRows().forEach(row ->
                    softly.assertThat(row.getCell("DOS").getValue()).isNotEmpty());
        });

        LOGGER.info("Step 27");
        NavigationPage.toSubTab(FINANCIALS);
        claim.declinePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_DeclinePayment"), 1);
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(LOGGED_INTAKE);
            softly.assertThat(firstPaymentRow.getCell(STATUS.getName())).hasValue(DECLINED);
            softly.assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue(ADJUSTED);
        });

        LOGGER.info("Step 28");
        NavigationPage.toSubTab(ADJUDICATION);
        assertSoftly(softly -> tableResultsOfAdjudication.getRows().forEach(row -> {
            softly.assertThat(row.getCell("Actions").controls.links.get("Override")).isAbsent();
            softly.assertThat(row.getCell("Actions").controls.links.get("View")).isPresent();
        }));
    }
}
