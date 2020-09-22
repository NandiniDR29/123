package com.exigen.ren.modules.claim.gb_st.master;

import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsBenefitSummaryTab;
import com.exigen.ren.main.modules.claim.common.tabs.LossContextTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsSTBaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.Tab.buttonSaveAndExit;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimFNOLLeftMenu.*;
import static com.exigen.ren.common.enums.NavigationEnum.ClaimTab.FNOL;
import static com.exigen.ren.common.pages.NavigationPage.isLeftMenuPresent;
import static com.exigen.ren.common.pages.NavigationPage.toSubTab;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.PAID_FAMILY_LEAVE;
import static com.exigen.ren.main.modules.claim.common.metadata.AdditionalPartiesAdditionalPartyTabMetaData.*;
import static com.exigen.ren.main.modules.claim.common.metadata.LossContextTabMetaData.TYPE_OF_LOSS;
import static com.exigen.ren.main.modules.claim.common.tabs.AdditionalPartiesAdditionalPartyTab.addAdditionalPartyAssociation;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimAddPFLBenefitLossEventAndAdditionalParty extends ClaimGroupBenefitsSTBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-18887", component = CLAIMS_GROUPBENEFITS)
    public void testClaimAddPFLBenefitLossEventAndAdditionalParty() {
        mainApp().open();

        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_ST);

        LOGGER.info("TEST: Step #1, 3");
        disabilityClaim.initiate(disabilityClaim.getSTTestData().getTestData(TestDataKey.DATA_GATHER, TestDataKey.DEFAULT_TEST_DATA_KEY)
                .adjust(TestData.makeKeyPath(LossContextTab.class.getSimpleName(), TYPE_OF_LOSS.getLabel()), PAID_FAMILY_LEAVE));

        benefitsBenefitSummaryTab.navigateToTab();
        BenefitsBenefitSummaryTab.comboboxDamage.setValue(PAID_FAMILY_LEAVE);
        BenefitsBenefitSummaryTab.linkAddComponents.click();

        assertSoftly(softly -> {
            softly.assertThat(isLeftMenuPresent(PFL_PARTICIPANT_INFORMATION.get())).isTrue();
            softly.assertThat(isLeftMenuPresent(PFL_QUALIFYING_EVENT.get())).isTrue();
        });

        LOGGER.info("TEST: Step #2, 4");
        buttonSaveAndExit.click();
        disabilityClaim.addBenefit().start(PAID_FAMILY_LEAVE);

        assertSoftly(softly -> {
            softly.assertThat(isLeftMenuPresent(PFL_PARTICIPANT_INFORMATION.get())).isTrue();
            softly.assertThat(isLeftMenuPresent(PFL_QUALIFYING_EVENT.get())).isTrue();
            softly.assertThat(isLeftMenuPresent(COVERAGE_EVALUATION.get())).isTrue();

        });

        LOGGER.info("TEST: Step #7-12");
        toSubTab(FNOL);
        dialogConfirmation.confirm();
        additionalPartiesAdditionalPartyTab.navigateToTab();
        addAdditionalPartyAssociation.click();
        assertSoftly(softly -> {
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(CLAIM_ROLE))
                    .containsAllOptions(ImmutableList.of("Care Recipient", "Health Care Provider", "Military Member"));
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(PREFERRED_PAYMENT_METHOD)).isAbsent();
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(CONTACT_PREFERENCE)).isOptional();
        });

        additionalPartiesAdditionalPartyTab.getAssetList().getAsset(PARTY_NAME).setValue("Other Individual");
        assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(SOCIAL_SECURITY_NUMBER_SSN)).isRequired();

        LOGGER.info("TEST: Step #16");
        additionalPartiesAdditionalPartyTab.getAssetList().getAsset(ADD_ADDITIONAL_PARTY_ASSOCIATION_ADDRESS).click();
        assertSoftly(softly -> {
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(ADDRESS_TYPE)).isPresent().isRequired();
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(COUNTRY)).isPresent().isRequired();
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(ZIP_POSTAL_CODE)).isPresent().isRequired();
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(ADDRESS_LINE_1)).isPresent().isRequired();
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(ADDRESS_LINE_2)).isPresent();
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(ADDRESS_LINE_3)).isPresent();
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(CITY)).isPresent().isRequired();
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(STATE_PROVINCE)).isPresent().isRequired();
            softly.assertThat(additionalPartiesAdditionalPartyTab.getAssetList().getAsset(REMOVE_ADDITIONAL_PARTY_ASSOCIATION_ADDRESS)).isPresent();
        });
    }
}
