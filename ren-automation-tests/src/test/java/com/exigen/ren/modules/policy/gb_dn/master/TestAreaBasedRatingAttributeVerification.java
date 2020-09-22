/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_dn.master;

import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.data.TestData;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.common.pages.Page;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.GroupDentalMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.ClassificationManagementTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PremiumSummaryTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.io.File;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.USER_10_LOGIN;
import static com.exigen.ren.common.enums.UsersConsts.USER_QA_QA_LOGIN;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.caseprofile.tabs.ProposalActionTab.OVERRIDE_RULES_LIST_KEY;
import static com.exigen.ren.main.modules.policy.gb_dn.masterpolicy.metadata.PremiumSummaryTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAreaBasedRatingAttributeVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupDentalMasterPolicyContext {

    private final static String FILE_NAME_VALUE = "test";
    private final static String FILE_NAME = "Dental - Rate Areas Mapping template.xlsx";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_DN, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21374", "REN-21398", "REN-21372"}, component = POLICY_GROUPBENEFITS)
    public void testAreaBasedRatingAttributeVerification() {

        mainApp().open();
        createDefaultNonIndividualCustomer();
        caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_PolicyCreationWithOnePlanAuto"), groupDentalMasterPolicy.getType());
        groupDentalMasterPolicy.initiate(getDefaultDNMasterPolicyData());
        groupDentalMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), premiumSummaryTab.getClass());
        Tab.buttonSaveAndExit.click();
        String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        LOGGER.info("---=={REN-21374 Step 1}==---");
        assertSoftly(softly -> {
            mainApp().reopen(USER_10_LOGIN, USER_QA_QA_LOGIN);
            MainPage.QuickSearch.search(masterPolicyNumber);
            groupDentalMasterPolicy.dataGather().start();
            premiumSummaryTab.navigate();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Rate Areas Mapping"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("If Non-Standard Area Mapping is needed, upload modified template below"))).isAbsent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isAbsent();
            premiumSummaryTab.submitTab();
        });

        LOGGER.info("---=={REN-21374 Step 2, REN-21372 Step 1}==---");
        mainApp().reopen();
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalMasterPolicy.dataGather().start();
        NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
        planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(PlanDefinitionTabMetaData.RatingMetaData.RATE_TYPE).setValue("Area + Tier");
        premiumSummaryTab.navigate();

        assertSoftly(softly -> {
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Rate Areas Mapping"))).isPresent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("If Non-Standard Area Mapping is needed, upload modified template below"))).isPresent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Note. Only xlsx file type is supported. The maximum file size is 1 MB. Only one file may be attached at a time."))).isPresent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isPresent().isEnabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Attached Rate Areas Mapping"))).isAbsent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE_UPLOAD_FILE)).isAbsent();
            Tab.buttonSaveAndExit.click();
        });
        LOGGER.info("---=={REN-21374 Step 3}==---");
        groupDentalMasterPolicy.dataGather().start();
        premiumSummaryTab.navigate();

        assertSoftly(softly -> {
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isPresent().isEnabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Attached Rate Areas Mapping"))).isAbsent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE_UPLOAD_FILE)).isAbsent();

            LOGGER.info("---=={REN-21374 Step 4, REN-21372 Step 2}==---");
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.BUTTON_OPEN_POPUP).click();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.FILE_UPLOAD)).isPresent().isEnabled();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.FILE_NAME)).isPresent().isEnabled();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.BUTTON_ClOSE_POPUP)).isPresent().isEnabled();

            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.FILE_UPLOAD)
                    .setValue(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION) + FILE_NAME));
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.FILE_NAME).setValue(FILE_NAME_VALUE);
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.DEFAULT_POPUP_SUBMITTER_NAME).click();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isPresent().isDisabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Attached Rate Areas Mapping"))).isPresent().isEnabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(FILE_NAME_VALUE))).isPresent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE_UPLOAD_FILE)).isPresent().isEnabled();
            Tab.buttonSaveAndExit.click();
        });

        LOGGER.info("---=={REN-21374 Step 5, REN-21372 Step 3}==---");
        mainApp().reopen(USER_10_LOGIN, USER_QA_QA_LOGIN);
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalMasterPolicy.dataGather().start();
        premiumSummaryTab.navigate();

        assertSoftly(softly -> {
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isPresent().isDisabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Attached Rate Areas Mapping"))).isPresent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(FILE_NAME_VALUE))).isPresent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE_UPLOAD_FILE)).isPresent().isDisabled();
        });
        LOGGER.info("---=={REN-21374 Step 6}==---");
        mainApp().reopen();
        MainPage.QuickSearch.search(masterPolicyNumber);
        groupDentalMasterPolicy.dataGather().start();
        premiumSummaryTab.navigate();
        premiumSummaryTab.getAssetList().getAsset(REMOVE_UPLOAD_FILE).click();

        assertSoftly(softly -> {
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isPresent().isEnabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Attached Rate Areas Mapping"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(FILE_NAME_VALUE))).isAbsent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE_UPLOAD_FILE)).isAbsent();
            Tab.buttonSaveAndExit.click();

            LOGGER.info("---=={REN-21374 Step 7}==---");
            groupDentalMasterPolicy.dataGather().start();
            premiumSummaryTab.navigate();
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.BUTTON_OPEN_POPUP).click();
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.FILE_UPLOAD)
                    .setValue(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION) + FILE_NAME));
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.FILE_NAME).setValue(FILE_NAME_VALUE);
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(PremiumSummaryTabMetaData.UploadFileDialog.DEFAULT_POPUP_SUBMITTER_NAME).click();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get());
            groupDentalMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultDNMasterPolicyData()
                    .mask(TestData.makeKeyPath(classificationManagementMpTab.getClass().getSimpleName(), ClassificationManagementTabMetaData.PlanTierAndRatingInfoMetaData.RATE.getLabel())), classificationManagementMpTab.getClass(), premiumSummaryTab.getClass());
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);

            groupDentalMasterPolicy.dataGather().start();
            premiumSummaryTab.navigate();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isPresent().isDisabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Attached Rate Areas Mapping"))).isPresent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(FILE_NAME_VALUE))).isPresent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE_UPLOAD_FILE)).isPresent().isEnabled();
            premiumSummaryTab.submitTab();

            LOGGER.info("---=={REN-21374 Step 11}==---");
            groupDentalMasterPolicy.propose().start();
            groupDentalMasterPolicy.propose().getWorkspace().fillUpTo(getDefaultDNMasterPolicyData(), ProposalActionTab.class, false);
            groupDentalMasterPolicy.propose().getWorkspace().getTab(ProposalActionTab.class).fillTab(getDefaultDNMasterPolicyData()
                    .adjust(TestData.makeKeyPath(ProposalActionTab.class.getSimpleName(), OVERRIDE_RULES_LIST_KEY),
                            ImmutableList.of("Proposal requires Underwriter approval because Major Waiting Period is less t...", "Proposal with an A La Carte Plan requires Underwriter approval")));
            ProposalActionTab.buttonCalculatePremium.click();
            ProposalActionTab.buttonGenerateProposal.click();
            Page.dialogConfirmation.confirm();

            MainPage.QuickSearch.search(masterPolicyNumber);
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);
            groupDentalMasterPolicy.acceptContract().perform(getDefaultDNMasterPolicyData());
            groupDentalMasterPolicy.dataGather().start();
            premiumSummaryTab.navigate();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isAbsent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE_UPLOAD_FILE)).isAbsent();
        });
    }
}