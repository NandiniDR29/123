package com.exigen.ren.modules.claim.gb_dn.certificate;

import com.exigen.ipb.eisa.base.application.impl.users.User;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.admin.modules.security.Privilege;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ClaimConstants;
import com.exigen.ren.main.modules.claim.gb_dn.tabs.IntakeInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.pages.summary.claim.ClaimHistoryPage.ClaimHistory;
import com.exigen.ren.main.pages.summary.claim.ClaimSummaryPage;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsDNBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.*;
import static com.exigen.ren.common.pages.MainPage.QuickSearch.search;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_NO;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.claim.gb_dn.metadata.IntakeInformationTabMetaData.*;
import static com.exigen.ren.main.pages.summary.claim.ClaimHistoryPage.tableClaimHistory;
import static com.exigen.ren.utils.AdminActionsHelper.createUserWithPrivilege;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckClaimHistoryTabAndRecords extends ClaimGroupBenefitsDNBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36511", component = CLAIMS_GROUPBENEFITS)
    public void testCheckClaimHistoryTabAndRecords() {
        LocalDate currentDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
        adminApp().open();
        User user = createUserWithPrivilege(ImmutableList.of("ALL", "EXCLUDE " + Privilege.CLAIM_INQUIRY_CLAIM_HISTORY.get()));

        mainApp().reopen();
        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DN);

        LOGGER.info("TEST REN-36511: Step 1-2");
        dentalClaim.create(tdSpecific().getTestData(DEFAULT_TEST_DATA_KEY));
        String claimNumber = ClaimSummaryPage.getClaimNumber();
        assertSoftly(softly -> {
            softly.assertThat(ClaimSummaryPage.labelClaimStatus).hasValue(ClaimConstants.ClaimStatus.LOGGED_INTAKE);
            softly.assertThat(NavigationPage.isSubTabPresent(PATIENT_HISTORY.get())).isTrue();
        });

        toSubTab(CLAIM_HISTORY);
        assertThat(tableClaimHistory.getRow(1)).hasCellWithValue(ClaimHistory.DATE_AND_TIME.getName(), "No records found.");

        LOGGER.info("TEST REN-36511: Step 3-4");
        toSubTab(EDIT_CLAIM);
        intakeInformationTab.fillTab(tdSpecific().getTestData("IntakeInformationTab2"));
        IntakeInformationTab.buttonSaveAndExit.click();

        toSubTab(CLAIM_HISTORY);
        assertSoftly(softly -> {
            softly.assertThat(tableClaimHistory.getRow(1)).hasCellWithValue(ClaimHistory.DATE_AND_TIME.getName(), "No records found.");
            softly.assertThat(tableClaimHistory.getHeader()).hasValue(ImmutableList.of(
                    ClaimHistory.DATE_AND_TIME.getName(),
                    ClaimHistory.REFERENCE.getName(),
                    ClaimHistory.COMPONENT.getName(),
                    ClaimHistory.ATTRIBUTE.getName(),
                    ClaimHistory.ORIGINAL.getName(),
                    ClaimHistory.NEW.getName(),
                    ClaimHistory.ADJUSTED_BY.getName()));
        });

        LOGGER.info("TEST REN-36511: Step 5");
        toSubTab(EDIT_CLAIM);
        IntakeInformationTab.buttonSubmitClaim.click();
        assertThat(ClaimSummaryPage.labelClaimStatus.getValue()).startsWith(ClaimConstants.ClaimStatus.PENDED);

        toSubTab(EDIT_CLAIM);
        intakeInformationTab.fillTab(tdSpecific().getTestData("IntakeInformationTab3"));
        IntakeInformationTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-36511: Step 6");
        toSubTab(EDIT_CLAIM);
        intakeInformationTab.fillTab(tdSpecific().getTestData("IntakeInformationTab1"));
        IntakeInformationTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-36511: Step 7");
        toSubTab(CLAIM_HISTORY);
        assertSoftly(softly -> {
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), CLEAN_CLAIM_DATE.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), "")
                    .hasCellWithValue(ClaimHistory.NEW.getName(), currentDate.format(MM_DD_YYYY));
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), OTHER_COVERAGE.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), VALUE_NO)
                    .hasCellWithValue(ClaimHistory.NEW.getName(), VALUE_YES);
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), ORTHO_MONTHS.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), "6")
                    .hasCellWithValue(ClaimHistory.NEW.getName(), "3");
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), PAYEE_TYPE.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), "Primary Insured")
                    .hasCellWithValue(ClaimHistory.NEW.getName(), "Service Provider");
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), SOURCE.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), "ECS - CHC")
                    .hasCellWithValue(ClaimHistory.NEW.getName(), "Manual Entry");
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), TYPE_OF_TRANSACTION.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), "Actual Services")
                    .hasCellWithValue(ClaimHistory.NEW.getName(), "Predetermination");
        });

        LOGGER.info("TEST REN-36511: Step 8");
        toSubTab(EDIT_CLAIM);
        intakeInformationTab.fillTab(tdSpecific().getTestData("IntakeInformationTab2"));
        IntakeInformationTab.buttonSaveAndExit.click();

        toSubTab(CLAIM_HISTORY);
        assertSoftly(softly -> {
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), OTHER_COVERAGE.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), VALUE_YES)
                    .hasCellWithValue(ClaimHistory.NEW.getName(), VALUE_NO);
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), ORTHO_MONTHS.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), "3")
                    .hasCellWithValue(ClaimHistory.NEW.getName(), "5");
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), PAYEE_TYPE.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), "Service Provider")
                    .hasCellWithValue(ClaimHistory.NEW.getName(), "Primary Insured");
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), SOURCE.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), "Manual Entry")
                    .hasCellWithValue(ClaimHistory.NEW.getName(), "ECS - Tesia");
            softly.assertThat(tableClaimHistory.getRow(ClaimHistory.ATTRIBUTE.getName(), TYPE_OF_TRANSACTION.getLabel()))
                    .hasCellWithValue(ClaimHistory.ORIGINAL.getName(), "Predetermination")
                    .hasCellWithValue(ClaimHistory.NEW.getName(), "Actual Services");
        });

        LOGGER.info("TEST REN-36511: Step 10");
        mainApp().reopen(user.getLogin(), user.getPassword());
        search(claimNumber);
        assertSoftly(softly -> {
            softly.assertThat(NavigationPage.isSubTabPresent(CLAIM_HISTORY.get())).isFalse();
            softly.assertThat(NavigationPage.isSubTabPresent(PATIENT_HISTORY.get())).isFalse();
        });
    }
}
