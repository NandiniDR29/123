package com.exigen.ren.modules.claim.gb_dn.without_policy;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.composite.table.Row;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_WITHOUT_POLICY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FINANCIALS;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSpecialHandlingStatus.ADJUSTED;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimStatus.*;
import static com.exigen.ren.main.enums.ClaimConstants.PaymentsAndRecoveriesTransactionStatus.*;
import static com.exigen.ren.main.enums.TableConstants.ClaimSummaryOfPaymentsAndRecoveriesTableExtended.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage.tableSummaryOfClaimPaymentsAndRecoveries;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPayment extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-15695", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPayment() {
        mainApp().open();
        createDefaultNonIndividualCustomer();

        LOGGER.info("Step 4, 5");
        TestData testDataWithoutPolicy = dentalClaim.getDefaultTestData(DATA_GATHER_WITHOUT_POLICY, TestDataKey.DEFAULT_TEST_DATA_KEY);
        dentalClaim.initiateWithoutPolicy(testDataWithoutPolicy);
        dentalClaim.getDefaultWorkspace().fillUpTo(testDataWithoutPolicy, intakeInformationTab.getClass(), true);
        String practiceName = intakeInformationTab.getAssetList().getAsset(IntakeInformationTabMetaData.SEARCH_PROVIDER).getAsset(IntakeInformationTabMetaData.SearchProviderMetaData.PRACTICE_NAME).getValue();
        intakeInformationTab.submitTab();
        claim.claimSubmit().perform();
        assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ADJUDICATED);

        LOGGER.info("Step 6");
        NavigationPage.toSubTab(FINANCIALS);
        Row firstPaymentRow = tableSummaryOfClaimPaymentsAndRecoveries.getRow(1);
        assertSoftly(softly -> {
            softly.assertThat(tableSummaryOfClaimPaymentsAndRecoveries).hasRows(1);
            softly.assertThat(firstPaymentRow.getCell(PAYMENT_NET_AMOUNT.getName())).hasValue(new Currency("0").toString());

            // TODO Can't get hint value via getHintValue()
            softly.assertThat(firstPaymentRow.getCell(PAYEE_RECOVERED_FROM.getName()).getWebElement().findElement(By.xpath(".//span[contains(@id, 'partyNameDisplayValue')]")).getAttribute("title")).isEqualTo(practiceName);

            softly.assertThat(firstPaymentRow.getCell(STATUS.getName())).hasValue(APPROVED);
        });

        LOGGER.info("Step 8");
        claim.issuePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_IssuePayment"), 1);
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(PAID);
            softly.assertThat(firstPaymentRow.getCell(STATUS.getName())).hasValue(ISSUED);
        });

        LOGGER.info("Step 9");
        claim.declinePayment().perform(tdClaim.getTestData("ClaimPayment", "TestData_DeclinePayment"), 1);
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(LOGGED_INTAKE);
            softly.assertThat(firstPaymentRow.getCell(STATUS.getName())).hasValue(DECLINED);
            softly.assertThat(ClaimSummaryPage.labelSpecialHandling).hasValue(ADJUSTED);
        });
    }
}
