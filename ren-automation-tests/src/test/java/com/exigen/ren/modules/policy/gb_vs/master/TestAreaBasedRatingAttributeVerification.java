/*
 *  Copyright © 2019 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 *  CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.
 */
package com.exigen.ren.modules.policy.gb_vs.master;

import com.exigen.istf.config.PropertyProvider;
import com.exigen.istf.config.TestProperties;
import com.exigen.istf.utils.TestInfo;
import com.exigen.istf.webdriver.controls.StaticElement;
import com.exigen.ren.common.Tab;
import com.exigen.ren.common.enums.NavigationEnum;
import com.exigen.ren.common.pages.MainPage;
import com.exigen.ren.common.pages.NavigationPage;
import com.exigen.ren.main.enums.ProductConstants;
import com.exigen.ren.main.modules.caseprofile.CaseProfileContext;
import com.exigen.ren.main.modules.customer.CustomerContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.GroupVisionMasterPolicyContext;
import com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData;
import com.exigen.ren.main.pages.summary.PolicySummaryPage;
import com.exigen.ren.modules.BaseTest;
import org.testng.annotations.Test;

import java.io.File;

import static com.exigen.istf.verification.CustomAssertions.assertThat;
import static com.exigen.istf.verification.CustomSoftAssertions.assertSoftly;
import static com.exigen.ren.common.enums.UsersConsts.USER_10_LOGIN;
import static com.exigen.ren.common.enums.UsersConsts.USER_QA_QA_LOGIN;
import static com.exigen.ren.main.enums.CommonLocators.COMMON_LABEL_WITH_TEXT_LOCATOR;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PlanDefinitionTabMetaData.RatingMetadata.RATE_TYPE;
import static com.exigen.ren.main.modules.policy.gb_vs.masterpolicy.metadata.PremiumSummaryTabMetaData.*;
import static com.exigen.ren.utils.components.Components.POLICY_GROUPBENEFITS;
import static com.exigen.ren.utils.groups.Groups.*;

public class TestAreaBasedRatingAttributeVerification extends BaseTest implements CustomerContext, CaseProfileContext, GroupVisionMasterPolicyContext {

    private final static String FILE_NAME_VALUE = "test";
    private final static String FILE_NAME = "Vision - Rate Areas Mapping template.xlsx";

    @Test(groups = {GB, GB_PRECONFIGURED, GB_VS, WITHOUT_TS, REGRESSION})
    @TestInfo(testCaseId = {"REN-21340", "REN-21461", "REN-21463"}, component = POLICY_GROUPBENEFITS)
    public void testAreaBasedRatingAttributeVerification() {
        assertSoftly(softly -> {
            mainApp().open(USER_10_LOGIN, USER_QA_QA_LOGIN);
            createDefaultNonIndividualCustomer();
            caseProfile.create(caseProfile.getDefaultTestData("CaseProfile", "TestData_PolicyCreationWithOnePlanAuto"), groupVisionMasterPolicy.getType());

            LOGGER.info("---=={REN-21340 Step 1}==---");
            groupVisionMasterPolicy.initiate(getDefaultVSMasterPolicyData());
            groupVisionMasterPolicy.getDefaultWorkspace().fillUpTo(getDefaultVSMasterPolicyData(), premiumSummaryTab.getClass());
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            softly.assertThat(planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(RATE_TYPE)).hasValue("Family Tier");
            premiumSummaryTab.navigate();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Rate Areas Mapping"))).isAbsent();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("If Non-Standard Area Mapping is needed, upload modified template below"))).isAbsent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isAbsent();
            premiumSummaryTab.submitTab();
            String masterPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

            LOGGER.info("---=={REN-21340 Step 2, REN-21461 Step 1}==---");
            mainApp().reopen();
            MainPage.QuickSearch.search(masterPolicyNumber);
            groupVisionMasterPolicy.dataGather().start();
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.PLAN_DEFINITION.get());
            planDefinitionTab.getAssetList().getAsset(PlanDefinitionTabMetaData.RATING).getAsset(RATE_TYPE).setValue("Area + Tier");
            premiumSummaryTab.navigate();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Rate Areas Mapping"))).isPresent();
            Tab.buttonSaveAndExit.click();

            mainApp().reopen(USER_10_LOGIN, USER_QA_QA_LOGIN);
            MainPage.QuickSearch.search(masterPolicyNumber);
            groupVisionMasterPolicy.dataGather().start();
            premiumSummaryTab.navigate();

            LOGGER.info("---=={REN-21340 Step 3}==---");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("If Non-Standard Area Mapping is needed, upload modified template below"))).isPresent();

            LOGGER.info("---=={REN-21340 Step 4}==---");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Note. Only xlsx file type is supported. The maximum file size is 1 MB. Only one file may be attached at a time."))).isPresent();

            LOGGER.info("---=={REN-21340 Step 5}==---");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Attached Rate Areas Mapping"))).isAbsent();

            LOGGER.info("---=={REN-21340 Step 6}==---");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE)).isAbsent();

            LOGGER.info("---=={REN-21340 Step 7}==---");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isPresent().isDisabled();
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP).click();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.FILE_UPLOAD)).isAbsent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.FILE_NAME)).isAbsent();

            LOGGER.info("---=={REN-21340 Step 8}==---");
            Tab.buttonSaveAndExit.click();

            LOGGER.info("---=={REN-21340 Step 9}==---");
            mainApp().reopen();
            MainPage.QuickSearch.search(masterPolicyNumber);
            groupVisionMasterPolicy.dataGather().start();
            premiumSummaryTab.navigate();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isPresent().isEnabled();

            LOGGER.info("---=={REN-21340 Step 10}==---");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Attached Rate Areas Mapping"))).isAbsent();

            LOGGER.info("---=={REN-21340 Step 11}==---");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE)).isAbsent();

            LOGGER.info("---=={REN-21340 Step 12}==---");
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP).click();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.FILE_UPLOAD)).isPresent().isEnabled();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.FILE_NAME)).isPresent().isEnabled();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_ClOSE_POPUP)).isPresent().isEnabled();

            LOGGER.info("---=={REN-21340 Step 13}==---");
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.FILE_UPLOAD)
                    .setValue(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION) + FILE_NAME));
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.FILE_NAME).setValue(FILE_NAME_VALUE);
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.DEFAULT_POPUP_SUBMITTER_NAME).click();

            LOGGER.info("---=={REN-21340 Step 14}==---");
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Attached Rate Areas Mapping"))).isPresent().isEnabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(FILE_NAME_VALUE))).isPresent().isEnabled();

            LOGGER.info("---=={REN-21340 Step 15}==---");
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE)).isPresent().isEnabled();

            LOGGER.info("---=={REN-21340 Step 16}==---");
            NavigationPage.PolicyNavigation.leftMenu(NavigationEnum.GroupBenefitsTab.CLASSIFICATION_MANAGEMENT.get());
            groupVisionMasterPolicy.getDefaultWorkspace().fillFromTo(getDefaultVSMasterPolicyData(), GroupVisionMasterPolicyContext.classificationManagementMpTab.getClass(), premiumSummaryTab.getClass());
            premiumSummaryTab.submitTab();

            LOGGER.info("---=={REN-21340 Step 17}==---");
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
            groupVisionMasterPolicy.dataGather().start();
            premiumSummaryTab.navigate();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP)).isPresent().isDisabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format("Attached Rate Areas Mapping"))).isPresent().isEnabled();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(FILE_NAME_VALUE))).isPresent().isEnabled();
            Tab.buttonSaveAndExit.click();

            LOGGER.info("---=={REN-21340 Step 19}==---");
            mainApp().reopen(USER_10_LOGIN, USER_QA_QA_LOGIN);
            MainPage.QuickSearch.search(masterPolicyNumber);
            groupVisionMasterPolicy.dataGather().start();
            premiumSummaryTab.navigate();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE)).isPresent().isDisabled();
            Tab.buttonSaveAndExit.click();

            LOGGER.info("---=={REN-21463 Step 3}==---");
            mainApp().reopen();
            MainPage.QuickSearch.search(masterPolicyNumber);
            groupVisionMasterPolicy.dataGather().start();
            premiumSummaryTab.navigate();
            premiumSummaryTab.getAssetList().getAsset(REMOVE).click();
            softly.assertThat(new StaticElement(COMMON_LABEL_WITH_TEXT_LOCATOR.format(FILE_NAME_VALUE))).isAbsent();
            softly.assertThat(premiumSummaryTab.getAssetList().getAsset(REMOVE)).isAbsent();

            LOGGER.info("---=={REN-21463 Step 4}==---");
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.BUTTON_OPEN_POPUP).click();
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.FILE_UPLOAD)
                    .setValue(new File(PropertyProvider.getProperty(TestProperties.UPLOAD_FILES_LOCATION) + FILE_NAME));
            premiumSummaryTab.getAssetList().getAsset(UPLOAD_FILE).getAsset(UploadFileDialog.FILE_NAME).setValue(FILE_NAME_VALUE);
            Tab.buttonSave.click();

            LOGGER.info("---=={REN-21463 Step 6}==---");
            premiumSummaryTab.submitTab();
            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
            groupVisionMasterPolicy.propose().perform(getDefaultVSMasterPolicyData());
            assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PROPOSED);
        });
    }
}
