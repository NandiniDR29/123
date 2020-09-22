package com.exigen.ren.modules.claim.gb_ltd.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.common.DefaultTab;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsLTDInjuryPartyInformationTab.ListOfParticipantIndexedPreDisabilityEarnings;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER_CERTIFICATE;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.LONG_TERM_DISABILITY;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.ADD_PARTICIPANT_INDEXED;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsLTDInjuryPartyInformationTabMetaData.ParticipantIndexedPreDisabilityEarningsMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckIndexedPreDisabilityEarnings extends ClaimGroupBenefitsLTDBaseTest {
    private Currency indexedEarnings = new Currency("1000");

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36758", component = CLAIMS_GROUPBENEFITS)
    public void testCheckIndexedPreDisabilityEarningsCertificate() {
        LocalDate currentDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DI_LTD);
        disabilityClaim.initiate(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY));

        LOGGER.info("TEST REN-36758: Step 1");
        disabilityClaim.getDefaultWorkspace().fillUpTo(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER_CERTIFICATE, DEFAULT_TEST_DATA_KEY), benefitsLTDInjuryPartyInformationTab.getClass(), true);
        assertThat(benefitsLTDInjuryPartyInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS)).isPresent();

        LOGGER.info("TEST REN-36758: Step 27");
        checkParticipantIndexedPreDisabilityEarnings(benefitsLTDInjuryPartyInformationTab, currentDate);

        LOGGER.info("TEST REN-36758: Step 24");
        assertSoftly(softly -> {
            softly.assertThat(BenefitsLTDInjuryPartyInformationTab.listOfParticipantIndexedPreDisabilityEarnings.getRow(1))
                    .hasCellWithValue(ListOfParticipantIndexedPreDisabilityEarnings.YEAR_NO.getName(), "1")
                    .hasCellWithValue(ListOfParticipantIndexedPreDisabilityEarnings.YEAR_START_DATE.getName(), currentDate.format(MM_DD_YYYY))
                    .hasCellWithValue(ListOfParticipantIndexedPreDisabilityEarnings.YEAR_END_DATE.getName(), currentDate.plusMonths(1).format(MM_DD_YYYY))
                    .hasCellWithValue(ListOfParticipantIndexedPreDisabilityEarnings.INDEXED_PRE_DISABILITY_EARNINGS.getName(), indexedEarnings.toString());

            softly.assertThat(BenefitsLTDInjuryPartyInformationTab.listOfParticipantIndexedPreDisabilityEarnings.getRow(2))
                    .hasCellWithValue(ListOfParticipantIndexedPreDisabilityEarnings.YEAR_NO.getName(), "2")
                    .hasCellWithValue(ListOfParticipantIndexedPreDisabilityEarnings.YEAR_START_DATE.getName(), currentDate.plusMonths(1).plusDays(1).format(MM_DD_YYYY))
                    .hasCellWithValue(ListOfParticipantIndexedPreDisabilityEarnings.YEAR_END_DATE.getName(), currentDate.plusMonths(2).format(MM_DD_YYYY))
                    .hasCellWithValue(ListOfParticipantIndexedPreDisabilityEarnings.INDEXED_PRE_DISABILITY_EARNINGS.getName(), indexedEarnings.toString());
        });

        BenefitsLTDInjuryPartyInformationTab.removeParticipantByName("2");
        assertThat(BenefitsLTDInjuryPartyInformationTab.listOfParticipantIndexedPreDisabilityEarnings).isAbsent();
        BenefitsLTDInjuryPartyInformationTab.buttonSaveAndExit.click();

        LOGGER.info("TEST REN-36758: Step 28-29");
        claim.addBenefit().start(LONG_TERM_DISABILITY);
        checkParticipantIndexedPreDisabilityEarnings(benefitsLTDInjuryPartyInformationTab, currentDate);
    }

    private void checkParticipantIndexedPreDisabilityEarnings(DefaultTab tab, LocalDate currentDate) {
        tab.getAssetList().getAsset(ADD_PARTICIPANT_INDEXED).click();
        assertSoftly(softly -> {
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO)).isPresent();
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE)).isPresent();
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE)).isPresent();
            softly.assertThat(tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS)).isPresent();
        });

        LOGGER.info("TEST REN-36758: Step 3-6");
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.format(MM_DD_YYYY));
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(currentDate.format(MM_DD_YYYY));
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS).setValue(indexedEarnings.toString());
        Tab.buttonNext.click();
        assertThat(tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO))
                .hasWarningWithText("'Year No.' is required");

        LOGGER.info("TEST REN-36758: Step 9-12");
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO).setValue("1");
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.format(MM_DD_YYYY));

        LOGGER.info("TEST REN-36758: Step 12-17");
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(currentDate.minusMonths(1).format(MM_DD_YYYY));
        assertThat(tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE))
                .hasWarningWithText("Cannot be earlier than Year Start Date");
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(currentDate.plusMonths(1).format(MM_DD_YYYY));

        LOGGER.info("TEST REN-36758: Step 7-8");
        tab.getAssetList().getAsset(ADD_PARTICIPANT_INDEXED).click();
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO).setValue("1");
        assertThat(tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO))
                .hasWarningWithText("Year No. can't be duplicated.");

        LOGGER.info("TEST REN-36758: Step 18-19");
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.plusDays(10).format(MM_DD_YYYY));
        assertThat(tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE))
                .hasWarningWithText("Year Period can't be overlapped.");

        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.plusDays(20).format(MM_DD_YYYY));
        assertThat(tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE))
                .hasWarningWithText("Year Period can't be overlapped.");

        LOGGER.info("TEST REN-36758: Step 20");
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO).setValue("2");
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.plusMonths(1).plusDays(1).format(MM_DD_YYYY));
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(currentDate.plusMonths(2).format(MM_DD_YYYY));
        tab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS).setValue(indexedEarnings.toString());
    }
}
