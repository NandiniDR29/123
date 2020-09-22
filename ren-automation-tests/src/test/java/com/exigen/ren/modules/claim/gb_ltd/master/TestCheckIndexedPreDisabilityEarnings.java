package com.exigen.ren.modules.claim.gb_ltd.master;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.main.modules.claim.common.tabs.PolicyInformationParticipantParticipantInformationTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsLTDBaseTest;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static com.exigen.istf.utils.datetime.DateTimeUtils.MM_DD_YYYY;
import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.TestDataKey.DATA_GATHER;
import static com.exigen.ren.TestDataKey.DEFAULT_TEST_DATA_KEY;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.ADD_PARTICIPANT_INDEXED;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS;
import static com.exigen.ren.main.modules.claim.common.metadata.PolicyInformationParticipantParticipantInformationTabMetaData.ParticipantIndexedPreDisabilityEarningsMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCheckIndexedPreDisabilityEarnings extends ClaimGroupBenefitsLTDBaseTest {
    private Currency indexedEarnings = new Currency("1000");

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-36758", component = CLAIMS_GROUPBENEFITS)
    public void testCheckIndexedPreDisabilityEarningsMaster() {
        LocalDate currentDate = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_DI_LTD);
        disabilityClaim.initiate(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY));

        LOGGER.info("TEST REN-36758: Step 1");
        disabilityClaim.getDefaultWorkspace().fillUpTo(disabilityClaim.getLTDTestData().getTestData(DATA_GATHER, DEFAULT_TEST_DATA_KEY), policyInformationParticipantParticipantInformationTab.getClass(), true);
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS)).isPresent();

        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ADD_PARTICIPANT_INDEXED).click();
        assertSoftly(softly -> {
            softly.assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO)).isPresent();
            softly.assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE)).isPresent();
            softly.assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE)).isPresent();
            softly.assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS)).isPresent();
        });

        LOGGER.info("TEST REN-36758: Step 3-6");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.format(MM_DD_YYYY));
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(currentDate.format(MM_DD_YYYY));
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS).setValue(indexedEarnings.toString());
        PolicyInformationParticipantParticipantInformationTab.buttonNext.click();
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO))
                .hasWarningWithText("'Year No.' is required");

        LOGGER.info("TEST REN-36758: Step 9-12");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO).setValue("1");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.format(MM_DD_YYYY));

        LOGGER.info("TEST REN-36758: Step 12-17");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(currentDate.minusMonths(1).format(MM_DD_YYYY));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE))
                .hasWarningWithText("Cannot be earlier than Year Start Date");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(currentDate.plusMonths(1).format(MM_DD_YYYY));

        LOGGER.info("TEST REN-36758: Step 7-8");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(ADD_PARTICIPANT_INDEXED).click();
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO).setValue("1");
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO))
                .hasWarningWithText("Year No. can't be duplicated.");

        LOGGER.info("TEST REN-36758: Step 18-19");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.plusDays(10).format(MM_DD_YYYY));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE))
                .hasWarningWithText("Year Period can't be overlapped.");

        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.plusDays(20).format(MM_DD_YYYY));
        assertThat(policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE))
                .hasWarningWithText("Year Period can't be overlapped.");

        LOGGER.info("TEST REN-36758: Step 20");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_NO).setValue("2");
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_START_DATE).setValue(currentDate.plusMonths(1).plusDays(1).format(MM_DD_YYYY));
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(YEAR_END_DATE).setValue(currentDate.plusMonths(2).format(MM_DD_YYYY));
        policyInformationParticipantParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INDEXED_PRE_DISABILITY_EARNINGS).getAsset(INDEXED_PRE_DISABILITY_EARNINGS).setValue(indexedEarnings.toString());

        LOGGER.info("TEST REN-36758: Step 24");
        assertSoftly(softly -> {
            softly.assertThat(PolicyInformationParticipantParticipantInformationTab.listOfParticipantIndexedPreDisabilityEarnings.getRow(1))
                    .hasCellWithValue(PolicyInformationParticipantParticipantInformationTab.ListOfParticipantIndexedPreDisabilityEarnings.YEAR_NO.getName(), "1")
                    .hasCellWithValue(PolicyInformationParticipantParticipantInformationTab.ListOfParticipantIndexedPreDisabilityEarnings.YEAR_START_DATE.getName(), currentDate.format(MM_DD_YYYY))
                    .hasCellWithValue(PolicyInformationParticipantParticipantInformationTab.ListOfParticipantIndexedPreDisabilityEarnings.YEAR_END_DATE.getName(), currentDate.plusMonths(1).format(MM_DD_YYYY))
                    .hasCellWithValue(PolicyInformationParticipantParticipantInformationTab.ListOfParticipantIndexedPreDisabilityEarnings.INDEXED_PRE_DISABILITY_EARNINGS.getName(), indexedEarnings.toString());

            softly.assertThat(PolicyInformationParticipantParticipantInformationTab.listOfParticipantIndexedPreDisabilityEarnings.getRow(2))
                    .hasCellWithValue(PolicyInformationParticipantParticipantInformationTab.ListOfParticipantIndexedPreDisabilityEarnings.YEAR_NO.getName(), "2")
                    .hasCellWithValue(PolicyInformationParticipantParticipantInformationTab.ListOfParticipantIndexedPreDisabilityEarnings.YEAR_START_DATE.getName(), currentDate.plusMonths(1).plusDays(1).format(MM_DD_YYYY))
                    .hasCellWithValue(PolicyInformationParticipantParticipantInformationTab.ListOfParticipantIndexedPreDisabilityEarnings.YEAR_END_DATE.getName(), currentDate.plusMonths(2).format(MM_DD_YYYY))
                    .hasCellWithValue(PolicyInformationParticipantParticipantInformationTab.ListOfParticipantIndexedPreDisabilityEarnings.INDEXED_PRE_DISABILITY_EARNINGS.getName(), indexedEarnings.toString());
        });

        PolicyInformationParticipantParticipantInformationTab.removeParticipantByName("2");
        assertThat(PolicyInformationParticipantParticipantInformationTab.listOfParticipantIndexedPreDisabilityEarnings).isAbsent();
    }
}