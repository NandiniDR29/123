package com.exigen.ren.modules.claim.gb_std.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsBenefitSummaryTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsCertificatePolicyType;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTDBaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.util.Collections;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.PAID_FAMILY_LEAVE;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAddPFLBenefitPFLParticipantInformation extends ClaimGroupBenefitsSTDBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-18893", "REN-18894"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimAddPFLBenefitPFLParticipantInformation() {
        mainApp().open();

        EntitiesHolder.openDefaultCertificatePolicy(GroupBenefitsCertificatePolicyType.GB_DI_STD);

        LOGGER.info("TEST: Step #1");
        disabilityClaim.initiate(disabilityClaim.getSTDTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY));

        benefitsBenefitSummaryTab.navigateToTab();
        BenefitsBenefitSummaryTab.comboboxDamage.setValue(PAID_FAMILY_LEAVE);
        BenefitsBenefitSummaryTab.linkAddComponents.click();

        LOGGER.info("Test scenarios REN-18893");
        LOGGER.info("TEST: Step #4");
        assertSoftly(softly -> {
            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(PREFERRED_LANGUAGE)).isOptional();
            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(PREFERRED_LANGUAGE).getAllValues())
                    .hasSameElementsAs(ImmutableList.of("English", "Spanish",
                            "Russian", "Polish", "Mandarin", "Italian", "Creole", "Korean", "Other"));

            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(ORIGIN_ETHNICITY)).isOptional();
            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(ORIGIN_ETHNICITY).getAllValues())
                    .hasSameElementsAs(ImmutableList.of("Mexican", "Mexican American",
                            "Chicano/a", "Puerto Rican", "Dominican", "Cuban", "Another Hispanic, Latino/a, or Spanish origin",
                            "Not of Hispanic, Latino/a, or Spanish origin", "Unknown"));

            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(RACE)).isOptional();
            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(RACE).getAllValues())
                    .hasSameElementsAs(ImmutableList.of("American Indian or Alaska Native", "Black or African American",
                            "Asian Indian", "Chinese", "Filipino", "Japanese", "Korean",
                            "Vietnamese", "Other Asian", "White", "Native Hawaiian", "Guamanian or Chamorro", "Samoan",
                            "Other Pacific Islander", "Other race"));

            LOGGER.info("TEST: Step #9");
            benefitsPflParticipantInformationTab.getAssetList().getAsset(PREFERRED_LANGUAGE).setValue(Collections.singletonList("Other"));

            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(OTHER_LANGUAGE)).isPresent();

            LOGGER.info("Test scenarios REN-18894");
            LOGGER.info("TEST: Step #2");
            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INCOME).getAsset(ParticipantIncomeMetaData.ANNUAL_BASE_SALARY)).isOptional().hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INCOME).getAsset(ParticipantIncomeMetaData.ANNUAL_COMMISSION_AMOUNT)).isOptional().hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INCOME).getAsset(ParticipantIncomeMetaData.ANNUAL_BONUS_AMOUNT)).isOptional().hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INCOME).getAsset(ParticipantIncomeMetaData.TOTAL_ANNUAL_INCOME_AMOUNT)).isOptional().hasValue(StringUtils.EMPTY);

            LOGGER.info("TEST: Step #3");
            benefitsPflParticipantInformationTab.getAssetList().getAsset(ASSOCIATE_POLICY_PARTY).setValueByIndex(1);

            Currency annualBaseSalary = new Currency(benefitsPflParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INCOME).getAsset(ParticipantIncomeMetaData.ANNUAL_BASE_SALARY).getValue());
            Currency annualCommissionAmount = new Currency(100);
            Currency annualBonusAmount = new Currency(100);

            benefitsPflParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INCOME).getAsset(ParticipantIncomeMetaData.ANNUAL_COMMISSION_AMOUNT).setValue(annualCommissionAmount.toString());
            benefitsPflParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INCOME).getAsset(ParticipantIncomeMetaData.ANNUAL_BONUS_AMOUNT).setValue(annualBonusAmount.toString());

            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(PARTICIPANT_INCOME).getAsset(ParticipantIncomeMetaData.TOTAL_ANNUAL_INCOME_AMOUNT))
                    .hasValue(annualBaseSalary.add(annualCommissionAmount).add(annualBonusAmount).toString());
        });
    }
}
