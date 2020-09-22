package com.exigen.ren.modules.claim.gb_pfl.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.Button;
import com.exigen.istf.webdriver.controls.composite.assets.AbstractContainer;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.common.Tab;
import com.exigen.ren.main.modules.claim.common.tabs.AdditionalPartiesAdditionalPartyTab;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsBenefitSummaryTab;
import com.exigen.ren.main.modules.claim.common.tabs.LossContextTab;
import com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsPFLBaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.*;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.ADJUDICATION;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FNOL;
import static com.exigen.ren.common.pages.ErrorPage.TableError.MESSAGE;
import static com.exigen.ren.common.pages.ErrorPage.tableError;
import static com.exigen.ren.common.pages.NavigationPage.isLeftMenuPresent;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.PAID_FAMILY_LEAVE;
import static com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.EventInformationLossEventTabMetaData.DISABILITY_REASON;
import static com.exigen.ren.main.modules.claim.common.metadata.LossContextTabMetaData.TYPE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLParticipantInformationTabMetaData.OTHER_LAST_NAME;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimLossEventAndAdditionalParty extends ClaimGroupBenefitsPFLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = {"REN-21540", "REN-21546"}, component = CLAIMS_GROUPBENEFITS)
    public void testClaimLossEventAndAdditionalParty() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_PFL);

        LOGGER.info("REN-21540: Step 1");
        disabilityClaim.initiateCreation();
        TestData testData = disabilityClaim.getPFLTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY);
        disabilityClaim.getInitializationView().fillUpTo(testData, LossContextTab.class);
        LossContextTab lossContextTab = disabilityClaim.getInitializationView().getTab(LossContextTab.class);
        assertThat(lossContextTab.getAssetList().getAsset(TYPE_OF_LOSS)).containsOption(PAID_FAMILY_LEAVE);
        lossContextTab.fillTab(testData).submitTab();
        benefitsBenefitSummaryTab.navigateToTab();
        assertThat(BenefitsBenefitSummaryTab.comboboxDamage).containsOption(PAID_FAMILY_LEAVE);

        LOGGER.info("REN-21540: Step 2, 3");
        Tab.buttonSaveAndExit.click();
        toSubTab(ADJUDICATION);
        assertThat(new Button(By.xpath("//a[text()='Add Benefit: Paid Family Leave']"))).isPresent();
        toSubTab(FNOL);
        benefitsBenefitSummaryTab.navigateToTab();
        BenefitsBenefitSummaryTab.comboboxDamage.setValue(PAID_FAMILY_LEAVE);
        BenefitsBenefitSummaryTab.linkAddComponents.click();
        assertSoftly(softly -> {
            softly.assertThat(isLeftMenuPresent(PFL_PARTICIPANT_INFORMATION.get())).isTrue();
            softly.assertThat(isLeftMenuPresent(PFL_QUALIFYING_EVENT.get())).isTrue();

            LOGGER.info("REN-21546: Step 2, 3");
            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(OTHER_LAST_NAME)).isOptional().hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflParticipantInformationTab.getAssetList().getAsset(BenefitsPFLParticipantInformationTabMetaData.GENDER))
                    .isRequired().hasOptions("", "Male", "Female", "Not designated/Other");
        });
        Tab.buttonSaveAndExit.click();

        LOGGER.info("REN-21540: Step 4");
        disabilityClaim.inquiryBenefit().start(1);
        assertSoftly(softly -> {
            softly.assertThat(isLeftMenuPresent(PFL_PARTICIPANT_INFORMATION.get())).isTrue();
            softly.assertThat(isLeftMenuPresent(PFL_QUALIFYING_EVENT.get())).isTrue();
            softly.assertThat(isLeftMenuPresent(COVERAGE_EVALUATION.get())).isTrue();
        });

        LOGGER.info("REN-21540: Step 7");
        toSubTab(FNOL);
        additionalPartiesAdditionalPartyTab.navigateToTab();
        AdditionalPartiesAdditionalPartyTab.addAdditionalPartyAssociation.click();
        AbstractContainer<?, ?> additionalPartyTabAssetList = additionalPartiesAdditionalPartyTab.getAssetList();
        assertThat(additionalPartyTabAssetList.getAsset(CLAIM_ROLE))
                .containsAllOptions("Care Recipient", "Health Care Provider", "Military Member");

        LOGGER.info("REN-21540: Step 10");

        additionalPartyTabAssetList.getAsset(ADD_ADDITIONAL_PARTY_ASSOCIATION_ADDRESS).click();
        ImmutableList.of(
                ADDRESS_TYPE,
                COUNTRY,
                ZIP_POSTAL_CODE,
                ADDRESS_LINE_1,
                ADDRESS_LINE_2,
                ADDRESS_LINE_3,
                CITY,
                STATE_PROVINCE,
                REMOVE_ADDITIONAL_PARTY_ASSOCIATION_ADDRESS).forEach(asset ->
                assertSoftly(softly ->
                        softly.assertThat(additionalPartyTabAssetList.getAsset(asset)).isPresent()));

        ImmutableList.of(
                ZIP_POSTAL_CODE,
                ADDRESS_LINE_1,
                CITY,
                STATE_PROVINCE).forEach(asset ->
                additionalPartyTabAssetList.getAsset(asset).setValue(StringUtils.EMPTY));

        LOGGER.info("REN-21540: Step 6, 13, 14, 15, 16");
        Tab.buttonSaveAndExit.click();
        disabilityClaim.claimSubmit().perform();
        assertSoftly(softly -> ImmutableList.of(
                DISABILITY_REASON,
                ZIP_POSTAL_CODE,
                ADDRESS_LINE_1,
                CITY,
                STATE_PROVINCE).forEach(asset ->
                softly.assertThat(tableError).hasMatchingRows(MESSAGE.getName(), String.format("'%s' is required", asset.getLabel()))));
    }
}
