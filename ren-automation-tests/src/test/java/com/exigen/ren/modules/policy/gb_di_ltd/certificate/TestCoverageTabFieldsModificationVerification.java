package com.exigen.ren.modules.policy.gb_di_ltd.certificate;

import com.exigen.ipb.eisa.utils.Currency;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.verification.CustomSoftAssertions;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.TableConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.LongTermDisabilityCertificatePolicyContext;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CoveragesTabMetaData;
import com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.tabs.CoveragesTab;
import com.exigen.ren.main.modules.policy.gb_di_ltd.masterpolicy.LongTermDisabilityMasterPolicyContext;
import com.exigen.ren.modules.BaseTest;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.math.RoundingMode;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.ren.main.enums.ErrorConstants.ErrorMessages.ERROR_PATTERN;
import static com.exigen.ren.main.enums.PolicyConstants.PolicyCoverageSummaryTable.BENEFIT_AMOUNT;
import static com.exigen.ren.main.enums.TableConstants.PremiumSummaryCoveragesTable.VOLUME;
import static com.exigen.ren.main.enums.ValueConstants.VALUE_YES;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CoveragesTabMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CoveragesTabMetaData.ParticipantAddressInfoMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.CoveragesTabMetaData.ParticipantGeneralInfoMetaData.*;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.ADDRESS_INFORMATION;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.metadata.InsuredTabMetaData.AddressInformationMetaData.STATE_PROVINCE;
import static com.exigen.ren.main.modules.policy.gb_di_ltd.certificate.tabs.PremiumSummaryTab.tablePremiumSummary;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestCoverageTabFieldsModificationVerification extends BaseTest implements CustomerContext, CaseProfileContext, LongTermDisabilityCertificatePolicyContext, LongTermDisabilityMasterPolicyContext {

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DI_LTD, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-28140", "REN-28141", "REN-28144", "REN-28169", "REN-28170"}, component = POLICY_GROUPBENEFITS)
    public void testCoverageTabFieldsModificationVerification() {
        LOGGER.info("General preconditions");
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(longTermDisabilityMasterPolicy.getType());
        TestData certificatePolicyTestData = longTermDisabilityCertificatePolicy.getDefaultTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
        longTermDisabilityCertificatePolicy.initiate(certificatePolicyTestData);
        longTermDisabilityCertificatePolicy.getDefaultWorkspace().fillUpTo(certificatePolicyTestData, CoveragesTab.class, false);

        LOGGER.info("REN-28141 Step#1 verification");
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(coveragesTab.getAssetList().getAsset(PLAN)).isPresent();

            LOGGER.info("REN-28169 Step#1 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(GUARANTEED_ISSUE_AMOUNT)).isPresent().isDisabled();

            coveragesTab.getAssetList().getAsset(CoveragesTabMetaData.PLAN).setValueByIndex(1);

            LOGGER.info("REN-28140 Step#1 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(MONTHLY_BENEFIT_AMOUNT)).isPresent();

            LOGGER.info("REN-28140 Step#3 verification");
            String defaultApprovedPercentValue = coveragesTab.getAssetList().getAsset(APPROVED_PERCENT).getValue().replace("%", "");
            coveragesTab.getAssetList().getAsset(EOI_REQUIRED).setValue(VALUE_YES);
            coveragesTab.getAssetList().getAsset(EOI_STATUS).setValue("Approved");
            coveragesTab.getAssetList().getAsset(APPROVED_PERCENT).setValue(defaultApprovedPercentValue);
            Currency annualEarnings = new Currency(coveragesTab.getAssetList().getAsset(ANNUAL_EARNINGS).getValue());
            Currency monthlyBenefitAmount = new Currency(annualEarnings.divide(12), RoundingMode.HALF_UP).multiply(new Currency(defaultApprovedPercentValue).divide(100));
            softly.assertThat(coveragesTab.getAssetList().getAsset(MONTHLY_BENEFIT_AMOUNT)).hasValue(monthlyBenefitAmount.toString());

            LOGGER.info("REN-28140 Step#4 verification");
            coveragesTab.getAssetList().getAsset(EOI_REQUIRED).setValue(VALUE_YES);
            coveragesTab.getAssetList().getAsset(EOI_STATUS).setValue("Pending");
            softly.assertThat(coveragesTab.getAssetList().getAsset(MONTHLY_BENEFIT_AMOUNT)).hasValue(coveragesTab.getAssetList().getAsset(GUARANTEED_ISSUE_AMOUNT).getValue());

            LOGGER.info("REN-28141 Step#3 verification");
            coveragesTab.getAssetList().getAsset(ADD_PARTICIPANT).click();
            coveragesTab.getAssetList().getAsset(PARTICIPANT_SELECTION).setValueByIndex(0);
            softly.assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(SUFFIX)).isPresent();
            softly.assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(TITLE)).isPresent();

            LOGGER.info("REN-28144 Step#1 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).isPresent();

            LOGGER.info("REN-28144 Step#2 verification");
            coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH).setValue(StringUtils.EMPTY);
            Tab.buttonNext.click();
            softly.assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_GENERAL_INFO).getAsset(DATE_OF_BIRTH)).hasWarningWithText(String.format(ERROR_PATTERN, DATE_OF_BIRTH.getLabel()));

            LOGGER.info("REN-28144 Step#4 verification");
            softly.assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(ADDRESS_LINE_1)).isPresent();
            softly.assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(ADDRESS_LINE_2)).isPresent();
            softly.assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(ADDRESS_LINE_3)).isPresent();
        });

        LOGGER.info("REN-28169 Step#3 verification");
        NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.PREMIUM_SUMMARY.get());
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(tablePremiumSummary.getHeader().getValue()).contains(TableConstants.PremiumSummaryCoveragesTable.PLAN.getName());

            LOGGER.info("REN-28169 Step#5 verification");
            softly.assertThat(tablePremiumSummary.getHeader().getValue()).doesNotContain("Trans. AP/RP");

            LOGGER.info("REN-28170 Step#1 verification");
            softly.assertThat(tablePremiumSummary.getHeader().getValue()).doesNotContain(BENEFIT_AMOUNT);
            softly.assertThat(tablePremiumSummary.getHeader().getValue()).contains(VOLUME.getName());
        });

        LOGGER.info("REN-28170 Step#2 verification");
        NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.INSURED.get());
        assertThat(insuredTab.getAssetList().getAsset(ADDRESS_INFORMATION).getAsset(STATE_PROVINCE)).isPresent();

        LOGGER.info("REN-28170 Step#5 verification");
        NavigationPage.toLeftMenuTab(NavigationEnum.GroupBenefitsTab.COVERAGES.get());
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(ParticipantAddressInfoMetaData.STATE_PROVINCE)).isPresent();

        LOGGER.info("REN-28170 Step#7 verification");
        coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(COUNTRY).setValue("Albania");
        assertThat(coveragesTab.getAssetList().getAsset(PARTICIPANT_ADDRESS_INFO).getAsset(ParticipantAddressInfoMetaData.STATE_PROVINCE)).isAbsent();
    }
}