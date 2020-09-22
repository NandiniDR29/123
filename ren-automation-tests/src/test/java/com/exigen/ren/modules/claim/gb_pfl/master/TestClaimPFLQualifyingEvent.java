package com.exigen.ren.modules.claim.gb_pfl.master;

import com.exigen.ipb.eisa.controls.composite.TableExtended;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.utils.datetime.DateTimeUtils;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.EntitiesHolder;
import com.exigen.ren.TestDataKey;
import com.exigen.ren.main.modules.claim.common.tabs.BenefitsBenefitSummaryTab;
import com.exigen.ren.main.modules.policy.common.GroupBenefitsMasterPolicyType;
import com.exigen.ren.modules.claim.ClaimGroupBenefitsPFLBaseTest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.util.Collections;

import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.pages.Page.dialogConfirmation;
import static com.exigen.ren.main.enums.ClaimConstants.ClaimSTATAvailableBenefits.PAID_FAMILY_LEAVE;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.metadata.BenefitsPFLQualifyingEventTabMetaData.*;
import static com.exigen.ren.main.modules.claim.gb_ltd_st_std.tabs.BenefitsPFLQualifyingEventTab.*;
import static com.exigen.ren.utils.components.Components.CLAIMS_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestClaimPFLQualifyingEvent extends ClaimGroupBenefitsPFLBaseTest {

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-21541", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPflQualifyingEvent() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_PFL);
        disabilityClaim.initiate(disabilityClaim.getPFLTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits"));
        benefitsBenefitSummaryTab.navigateToTab();
        BenefitsBenefitSummaryTab.comboboxDamage.setValue(PAID_FAMILY_LEAVE);
        BenefitsBenefitSummaryTab.linkAddComponents.click();
        benefitsPflQualifyingEventTab.navigateToTab();

        LOGGER.info("Step 2");
        assertSoftly(softly -> {
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON)).isRequired()
                    .hasOptions(ImmutableList.of(StringUtils.EMPTY, "Bond with child", "Care for family member",
                            "Military qualifying event"));
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(THE_FAMILY_MEMBER_IS_EMPLOYEES)).isRequired()
                    .hasOptions(ImmutableList.of(StringUtils.EMPTY, "Child", "Spouse", "Domestic partner", "Parent",
                            "Parent-in-law", "Grandparent", "Grandchild"));
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(CONTINUOUS_OF_PERIODIC)).isRequired()
                    .hasOptions(ImmutableList.of(StringUtils.EMPTY, "Continuous", "Periodic"));
        });

        assertSoftly(softly -> {
            LOGGER.info("Step 5");
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(TYPE)).isRequired()
                    .hasOptions(ImmutableList.of(StringUtils.EMPTY, "PFL start date", "PFL end date"));
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(DATE)).isRequired()
                    .hasValue(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));

            LOGGER.info("Step 7");
            benefitsPflQualifyingEventTab.getAssetList().getAsset(ADD_PFL_DATE).click();
            checkTableColumns(tableListOfPFLDate, "Type", "Date");

            LOGGER.info("Step 8");
            benefitsPflQualifyingEventTab.getAssetList().getAsset(REMOVE_PFL_DATE).click();
            dialogConfirmation.confirm();
            softly.assertThat(tableListOfPFLDate).isAbsent();
        });

        assertSoftly(softly -> {
            LOGGER.info("Step 9");
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(ADVANCE_NOTICE)).isRequired();
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(FORM_SUBMITTED_IN_ADVANCE)).isRequired().hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(DATE_OF_HIRE)).isRequired().hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(OCCUPATION_CODE)).isRequired().hasValue(StringUtils.EMPTY);

            LOGGER.info("Step 23");
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(REQUEST_REIMBURSEMENT)).isRequired().hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(FMLA_CONCURRENTLY_WITH_PFL)).isRequired().hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(OTHER_DISABILITY)).isRequired()
                    .hasOptions(ImmutableList.of(StringUtils.EMPTY, "NYS Disability", "PFL", "Both Disability and PFL", "None"));
        });

        LOGGER.info("Step 27, 32");
        benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON).setValue("Bond with child");
        assertSoftly(softly -> {
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(CHILD_DATE_OF_BIRTH)).isRequired().hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(CHILD_GENDER)).isRequired()
                    .hasOptions(ImmutableList.of(StringUtils.EMPTY, "Male", "Female", "Not designated/Other"));
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(LIVE_WITH_PARTICIPANT)).isRequired().hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(RELATIONSHIP_TO_PARTICIPANT)).isRequired()
                    .hasOptions(ImmutableList.of(
                            StringUtils.EMPTY,
                            "Biological child",
                            "Stepchild",
                            "Foster child",
                            "Adopted child",
                            "Legal ward",
                            "Spouse/Domestic partner’s child",
                            "Loco parentis"));
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(FMLA_CONCURRENTLY_WITH_PFL)).isAbsent();
        });
    }

    @Test(groups = {CLAIM_GB, REGRESSION, WITHOUT_TS})
    @TestInfo(testCaseId = "REN-21542", component = CLAIMS_GROUPBENEFITS)
    public void testClaimPflQualifyingEvent_2() {
        mainApp().open();
        EntitiesHolder.openDefaultMasterPolicy(GroupBenefitsMasterPolicyType.GB_PFL);

        disabilityClaim.initiate(disabilityClaim.getPFLTestData().getTestData(TestDataKey.DATA_GATHER, "TestData_Without_Benefits"));
        benefitsBenefitSummaryTab.navigateToTab();
        BenefitsBenefitSummaryTab.comboboxDamage.setValue(PAID_FAMILY_LEAVE);
        BenefitsBenefitSummaryTab.linkAddComponents.click();
        benefitsPflQualifyingEventTab.navigateToTab();

        LOGGER.info("Step 1");
        assertSoftly(softly ->
                ImmutableList.of("PFL Request", "PFL Date", "Employment Information", "Income Information").forEach(sectionName ->
                        softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(sectionName))).isPresent()));

        LOGGER.info("Step 2");
        assertSoftly(softly -> {
            benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON).setValue("Care for family member");
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(FIRST_DATE_CARE_FOR_PATIENT_IS_NEEDED)).isRequired()
                    .hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(EXPECTED_DATE_PATIENT_WILL_NO_LONGER_REQUIRE_CARE)).isRequired()
                    .hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(LICENSE_NUMBER)).isRequired()
                    .hasValue(StringUtils.EMPTY);
        });

        assertSoftly(softly -> {
            LOGGER.info("Step 9");
            benefitsPflQualifyingEventTab.getAssetList().getAsset(ADD_HEALTH_CARE_PROVIDER_CERTIFICATION).click();
            checkTableColumns(tableListOfHealthCareProviderCertification, "First date care for patient is needed", "Expected date patient will no longer require care");

            LOGGER.info("Step 10");
            benefitsPflQualifyingEventTab.getAssetList().getAsset(REMOVE_HEALTH_CARE_PROVIDER_CERTIFICATION).click();
            dialogConfirmation.confirm();
            softly.assertThat(tableListOfHealthCareProviderCertification).isAbsent();
        });

        assertSoftly(softly -> {
            LOGGER.info("Step 11");
            benefitsPflQualifyingEventTab.getAssetList().getAsset(ADD_HEALTH_CARE_PROVIDER_INFORMATION).click();
            softly.assertThat(tableListOfHealthCareProviderInformation).isPresent();

            LOGGER.info("Step 12");
            benefitsPflQualifyingEventTab.getAssetList().getAsset(REMOVE_HEALTH_CARE_PROVIDER_INFORMATION).click();
            dialogConfirmation.confirm();
            softly.assertThat(tableListOfHealthCareProviderInformation).isAbsent();
        });

        LOGGER.info("Step 13");
        assertSoftly(softly -> {
            benefitsPflQualifyingEventTab.getAssetList().getAsset(PFL_REASON).setValue("Military qualifying event");
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(BEGINNING_DATE)).isRequired()
                    .hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(TO_DATE)).isRequired()
                    .hasValue(StringUtils.EMPTY);
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(REASON_TO_REQUEST_FOR_MILITARY).getAllValues())
                    .hasSameElementsAs(ImmutableList.of("Arranging for child care", "Arranging for parental care", "Counseling",
                            "Making financial arrangements", "Making legal arrangements",
                            "Acting as military member’s representative before a federal, state, or local agency for " +
                                    "purpose of obtaining, arranging, or appealing military service benefits",
                            "Attending any event sponsored by the military or military service organizations", "Other"));
        });

        assertSoftly(softly -> {
            LOGGER.info("Step 16");
            benefitsPflQualifyingEventTab.getAssetList().getAsset(ADD_MILITARY_QUALIFYING_EVENT_INFORMATION).click();
            checkTableColumns(tableListOfMilitaryQualifyingEventInformation, "Beginning Date", "To Date");

            LOGGER.info("Step 17");
            benefitsPflQualifyingEventTab.getAssetList().getAsset(REMOVE_MILITARY_QUALIFYING_EVENT_INFORMATION).click();
            dialogConfirmation.confirm();
            softly.assertThat(tableListOfMilitaryQualifyingEventInformation).isAbsent();

            LOGGER.info("Step 18");
            benefitsPflQualifyingEventTab.getAssetList().getAsset(REASON_TO_REQUEST_FOR_MILITARY).setValue(Collections.singletonList("Other"));
            softly.assertThat(benefitsPflQualifyingEventTab.getAssetList().getAsset(OTHER_REASON)).isRequired()
                    .hasValue(StringUtils.EMPTY);
        });

    }

    private void checkTableColumns(TableExtended table, String... columnNames) {
        assertSoftly(softly -> {
            softly.assertThat(table).hasRows(2);
            softly.assertThat(table.getHeader().getValue()).contains(columnNames);
        });
    }


}
